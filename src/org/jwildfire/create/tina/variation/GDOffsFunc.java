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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class GDOffsFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_DELTA_X = "delta_x";
  private static final String PARAM_DELTA_Y = "delta_y";
  private static final String PARAM_AREA_X = "area_x";
  private static final String PARAM_AREA_Y = "area_y";
  private static final String PARAM_CENTER_X = "center_x";
  private static final String PARAM_CENTER_Y = "center_y";
  private static final String PARAM_GAMMA = "gamma";
  private static final String PARAM_SQUARE = "square";

  private static final String[] paramNames = {PARAM_DELTA_X, PARAM_DELTA_Y, PARAM_AREA_X, PARAM_AREA_Y, PARAM_CENTER_X, PARAM_CENTER_Y, PARAM_GAMMA, PARAM_SQUARE};

  private double delta_x = 0.0;
  private double delta_y = 0.0;
  private double area_x = 2.0;
  private double area_y = 2.0;
  private double center_x = 0;
  private double center_y = 0;
  private int gamma = 1;
  private int square = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // gdoffs by Xyrus02, http://xyrusworx.deviantart.com/art/GdOffs-Plugin-for-Apophysis-208416713?offset=0     
    double osc_x = fosc(gdodx, 1), osc_y = fosc(gdody, 1);
    double in_x = (pAffineTP.x + gdocx), in_y = (pAffineTP.y + gdocy);
    double out_x = 0, out_y = 0;

    if (gdos != 0) {
      out_x = flip(flip(in_x, fosc(in_x, 4), osc_x), fosc(fclp(gdob * in_x), 4), osc_x);
      out_y = flip(flip(in_y, fosc(in_y, 4), osc_x), fosc(fclp(gdob * in_y), 4), osc_x);
    } else {
      out_x = flip(flip(in_x, fosc(in_x, 4), osc_x), fosc(fclp(gdob * in_x), 4), osc_x);
      out_y = flip(flip(in_y, fosc(in_y, 4), osc_y), fosc(fclp(gdob * in_y), 4), osc_y);
    }

    pVarTP.x += pAmount * out_x;
    pVarTP.y += pAmount * out_y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{delta_x, delta_y, area_x, area_y, center_x, center_y, gamma, square};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_DELTA_X.equalsIgnoreCase(pName))
      delta_x = pValue;
    else if (PARAM_DELTA_Y.equalsIgnoreCase(pName))
      delta_y = pValue;
    else if (PARAM_AREA_X.equalsIgnoreCase(pName))
      area_x = pValue;
    else if (PARAM_AREA_Y.equalsIgnoreCase(pName))
      area_y = pValue;
    else if (PARAM_CENTER_X.equalsIgnoreCase(pName))
      center_x = pValue;
    else if (PARAM_CENTER_Y.equalsIgnoreCase(pName))
      center_y = pValue;
    else if (PARAM_GAMMA.equalsIgnoreCase(pName))
      gamma = Tools.FTOI(pValue);
    else if (PARAM_SQUARE.equalsIgnoreCase(pName))
      square = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "gdoffs";
  }

  //Fine tune stuff, try not to touch :)
  private static final double __agdod = 0.1;
  private static final double __agdoa = 2.0;
  private static final double __agdoc = 1.0;

  private double gdodx, gdody, gdoax, gdoay, gdocx, gdocy, gdob;
  private int gdog, gdos;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    gdodx = delta_x * __agdod;
    gdody = delta_y * __agdod;

    gdoax = ((fabs(area_x) < 0.1) ? 0.1 : fabs(area_x)) * __agdoa;
    gdoay = ((fabs(area_y) < 0.1) ? 0.1 : fabs(area_y)) * __agdoa;

    gdocx = (center_x) * __agdoc;
    gdocy = (center_y) * __agdoc;

    gdog = gamma;
    gdos = square;

    gdob = (double) (gdog) * __agdoa / (max(gdoax, gdoay));
  }

  private double fclp(double a) {
    return ((a < 0) ? -(fmod(fabs(a), 1)) : fmod(fabs(a), 1));
  }

  private double fscl(double a) {
    return fclp((a + 1) / 2);
  }

  private double fosc(double p, double a) {
    return fscl(-1 * cos(p * a * M_2PI));
  }

  private double flip(double a, double b, double c) {
    return (c * (b - a) + a);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float gdodx = __gdoffs_delta_x * 0.1f;\n"
        + "float gdody = __gdoffs_delta_y * 0.1f;\n"
        + "float gdoax = ((fabsf(__gdoffs_area_x) < 1.f) ? 0.1f : fabsf(__gdoffs_area_x))*2.f;\n"
        + "float gdoay = ((fabsf(__gdoffs_area_y) < 1.f) ? 0.1f : fabsf(__gdoffs_area_y))*2.f;\n"
        + "float gdocx = __gdoffs_center_x;\n"
        + "float gdocy = __gdoffs_center_y;\n"
        + "int   gdos  = __gdoffs_square;\n"
        + "float gdob = __gdoffs_gamma * 2.f /fmaxf(gdoax, gdoay);\n"
        + "\n"
        + "float osc_x=gdoffs_fosc(gdodx,1.f),\n"
        + "  osc_y=gdoffs_fosc(gdody,1.f);\n"
        + "float in_x=(__x+gdocx),\n"
        + "  in_y=(__y+gdocy);\n"
        + "float out_x=0.f,out_y=0.f;\n"
        + "\n"
        + "if ((gdos) != 0) {\n"
        + "    out_x = gdoffs_flip(gdoffs_flip(in_x,gdoffs_fosc(in_x,4.f),osc_x),gdoffs_fosc(gdoffs_fclp(gdob*in_x),4.f),osc_x);\n"
        + "    out_y = gdoffs_flip(gdoffs_flip(in_y,gdoffs_fosc(in_y,4.f),osc_x),gdoffs_fosc(gdoffs_fclp(gdob*in_y),4.f),osc_x);\n"
        + "} else {\n"
        + "    out_x = gdoffs_flip(gdoffs_flip(in_x,gdoffs_fosc(in_x,4.f),osc_x),gdoffs_fosc(gdoffs_fclp(gdob *in_x),4.f),osc_x);\n"
        + "    out_y = gdoffs_flip(gdoffs_flip(in_y,gdoffs_fosc(in_y,4.f),osc_y),gdoffs_fosc(gdoffs_fclp(gdob *in_y),4.f),osc_y);\n"
        + "}\n"
        + "__px += __gdoffs*out_x;\n"
        + "__py += __gdoffs*out_y;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __gdoffs*__z;\n" : "");
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float gdoffs_fclp(float a){return ((a<0.f)?-(fmodf(fabsf(a),1.f)):fmodf(fabsf(a),1.f));}\n"
        + "__device__ float gdoffs_fscl(float a){return gdoffs_fclp((a+1.f)*0.5f); }\n"
        + "__device__ float gdoffs_fosc(float p, float a){return gdoffs_fscl(-cosf(p*a*2.f*PI));}\n"
        + "__device__ float gdoffs_flip(float a, float b, float c){return (c*(b-a)+a);}";
  }
}
