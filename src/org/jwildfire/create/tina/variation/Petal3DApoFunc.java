/*
JWildfire - an image and animation processor written in Java 
Copyright (C) 1995-2021 Andreas Maschke
This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
General Public License as published by the Free Software Foundation; either version 2.1 of the 
License, or (at your option) any later version.

This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License along with this software; 
if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqr;
import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.M_PI;

public class Petal3DApoFunc extends VariationFunc implements SupportsGPU {
    private static final long serialVersionUID = 1L;

    private static final String PARAM_WIDTH = "width";
    private static final String PARAM_ZSHAPE = "Zshape";
    private static final String PARAM_SCALE1 = "scale1";
    private static final String PARAM_SCALE2 = "scale2";
    private static final String PARAM_STYLE = "style";

    private double width = 1.0;
    private double zshape = 0.25;
    private double scale1 = 0.25;
    private double scale2 = 0.25;
    private double smoothStyle = 0.0;
    private static final double M_SQRT1_2 = sqrt(0.5);
    private static final String[] paramNames = { PARAM_WIDTH, PARAM_ZSHAPE, PARAM_SCALE1, PARAM_SCALE2, PARAM_STYLE };

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
            double pAmount) {
      // petal3D by Larry Berlin (aporev)
      // https://www.deviantart.com/aporev/art/petal3D-plugin-139564066
      
      double shaper, shaper2, tmpPY, tmpPZ, tmpSmth;
      double squeeze;
      int posNeg = 1;
      if(pContext.random() < 0.5)
      {
          posNeg = -1;
      }
      double styleSign = smoothStyle >= 0 ? 1.0 : -1.0; //copysign(1.0,VAR(petal3D_style));
      double a = cos(pAffineTP.x);
      double bx = (cos(pAffineTP.x)*cos(pAffineTP.y))*(cos(pAffineTP.x)*cos(pAffineTP.y))*(cos(pAffineTP.x)*cos(pAffineTP.y)); 
      double by = (sin(pAffineTP.x)*cos(pAffineTP.y))*(sin(pAffineTP.x)*cos(pAffineTP.y))*(sin(pAffineTP.x)*cos(pAffineTP.y)); 
      
      //  draws petal shape
      pVarTP.x += pAmount * a * bx;
      tmpPY = pAmount * a * by * width;
      pVarTP.y += tmpPY;
      
      //  Shapes the Z dimension
      if(fabs(scale1)>1.0)
      {
          squeeze = fabs(scale1)-1.0;  //  Diminishes the Z effect in harmonics as scale1>1.0 and as pAffineTP.x increases
          shaper  = (-1*sin(pAffineTP.x*scale1*M_PI))*0.5+(pAffineTP.x*squeeze*0.5)+sin(fabs(pAffineTP.y*scale2*M_PI)); 
          shaper2 = (-1*sin(pAffineTP.x*scale1*M_PI))*0.5+(pAffineTP.x*squeeze*0.5)+tmpPY*scale2*0.5;
      }
      else
      {
          squeeze = 0.0;
          shaper  = (-1*sin(pAffineTP.x*scale1*M_PI))*0.5+sin(fabs(pAffineTP.y*scale2*M_PI));
          shaper2 = (-1*sin(pAffineTP.x*scale1*M_PI))*0.5+tmpPY*scale2*0.5;
      }
      
      //  smoothStyle uses shaper at 0.0 and blends to shaper2 when fabs(smoothStyle>=sqrt(0.5))
      //  use of negative values will reverse the sign of Z for shaper2
      
      tmpSmth=0.5-sqr(smoothStyle);
          tmpPZ = shaper;
      if(fabs(smoothStyle)>M_SQRT1_2)
          {
              tmpPZ = shaper2*styleSign;
          }
          else if(smoothStyle<0.0)
              {
                  tmpPZ = tmpSmth*2.0*shaper - (1.0-tmpSmth*2.0)*shaper2;
              }
              else if(smoothStyle>0.0)
                  {
                      tmpPZ = tmpSmth*2.0*shaper + (1.0-tmpSmth*2.0)*shaper2;
                  }
      
      pVarTP.z += pAmount*tmpPZ*zshape;
      
    }

    @Override
    public String[] getParameterNames() {
        return paramNames;
    }

    @Override
    public Object[] getParameterValues() {
        return new Object[] { width, zshape, scale1, scale2, smoothStyle };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_WIDTH.equalsIgnoreCase(pName)) {
            width = pValue;
        } else if (PARAM_ZSHAPE.equalsIgnoreCase(pName)) {
            zshape = pValue;
        } else if (PARAM_SCALE1.equalsIgnoreCase(pName)) {
            scale1 = pValue;
        } else if (PARAM_SCALE2.equalsIgnoreCase(pName)) {
            scale2 = pValue;
        } else if (PARAM_STYLE.equalsIgnoreCase(pName)) {
            smoothStyle = pValue;
        } else {
            System.out.println("pName not recognized: " + pName);
            throw new IllegalArgumentException(pName);
        }
    }

    @Override
    public String getName() {
        return "petal3D_apo";
    }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "     float shaper, shaper2, tmpPY, tmpPZ, tmpSmth;\n"
        + "      float squeeze;\n"
        + "      int posNeg = 1;\n"
        + "      if(RANDFLOAT() < 0.5f)\n"
        + "      {\n"
        + "          posNeg = -1;\n"
        + "      }\n"
        + "      float styleSign = varpar->petal3D_apo_style >= 0 ? 1.0f : -1.0f;\n"
        + "      float a = cosf(__x);\n"
        + "      float bx = (cosf(__x)*cosf(__y))*(cosf(__x)*cosf(__y))*(cosf(__x)*cosf(__y)); \n"
        + "      float by = (sinf(__x)*cosf(__y))*(sinf(__x)*cosf(__y))*(sinf(__x)*cosf(__y)); \n"
        + "      __px += varpar->petal3D_apo * a * bx;\n"
        + "      tmpPY = varpar->petal3D_apo * a * by * varpar->petal3D_apo_width;\n"
        + "      __py += tmpPY;\n"
        + "      if(fabsf(varpar->petal3D_apo_scale1)>1.0f)\n"
        + "      {\n"
        + "          squeeze = fabsf(varpar->petal3D_apo_scale1)-1.0f;\n"
        + "          shaper  = (-1*sinf(__x*varpar->petal3D_apo_scale1*PI))*0.5f+(__x*squeeze*0.5f)+sinf(fabsf(__y*varpar->petal3D_apo_scale2*PI)); \n"
        + "          shaper2 = (-1*sinf(__x*varpar->petal3D_apo_scale1*PI))*0.5f+(__x*squeeze*0.5f)+tmpPY*varpar->petal3D_apo_scale2*0.5f;\n"
        + "      }\n"
        + "      else\n"
        + "      {\n"
        + "          squeeze = 0.0f;\n"
        + "          shaper  = (-1*sinf(__x*varpar->petal3D_apo_scale1*PI))*0.5f+sinf(fabsf(__y*varpar->petal3D_apo_scale2*PI));\n"
        + "          shaper2 = (-1*sinf(__x*varpar->petal3D_apo_scale1*PI))*0.5f+tmpPY*varpar->petal3D_apo_scale2*0.5;\n"
        + "      }\n"
        + "      tmpSmth=0.5f-sqrf(varpar->petal3D_apo_style);\n"
        + "          tmpPZ = shaper;\n"
        + "      if(fabsf(varpar->petal3D_apo_style)>0.70710678f)\n"
        + "          {\n"
        + "              tmpPZ = shaper2*styleSign;\n"
        + "          }\n"
        + "          else if(varpar->petal3D_apo_style<0.0)\n"
        + "              {\n"
        + "                  tmpPZ = tmpSmth*2.0f*shaper - (1.0f-tmpSmth*2.0f)*shaper2;\n"
        + "              }\n"
        + "              else if(varpar->petal3D_apo_style>0.0)\n"
        + "                  {\n"
        + "                      tmpPZ = tmpSmth*2.0f*shaper + (1.0f-tmpSmth*2.0f)*shaper2;\n"
        + "                  }\n"
        + "      \n"
        + "      __pz += varpar->petal3D_apo*tmpPZ*varpar->petal3D_apo_Zshape;\n";
  }
}
