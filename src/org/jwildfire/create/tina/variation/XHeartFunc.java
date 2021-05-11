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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class XHeartFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_RATIO = "ratio";
  private static final String[] paramNames = {PARAM_ANGLE, PARAM_RATIO};

  private double angle = 0.0;
  private double ratio = 0.0;
  // derived variables
  private double rat, cosa, sina;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // xheart by xyrus02, http://xyrus02.deviantart.com/art/XHeart-Plugin-139866412
    double r2_4 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + 4;
    if (r2_4 == 0)
      r2_4 = 1;
    double bx = 4 / r2_4, by = rat / r2_4;
    double x = cosa * (bx * pAffineTP.x) - sina * (by * pAffineTP.y);
    double y = sina * (bx * pAffineTP.x) + cosa * (by * pAffineTP.y);

    if (x > 0) {
      pVarTP.x += pAmount * x;
      pVarTP.y += pAmount * y;
    } else {
      pVarTP.x += pAmount * x;
      pVarTP.y += -pAmount * y;
    }
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
    return new Object[]{angle, ratio};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_RATIO.equalsIgnoreCase(pName))
      ratio = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "xheart";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double ang = M_PI_4 + (0.5 * M_PI_4 * angle);
    sina = sin(ang);
    cosa = cos(ang);
    rat = 6 + 2 * ratio;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float ang = M_PI_4_F + (0.5f * M_PI_4_F * varpar->xheart_angle);\n"
        + "float cosa;\n"
        + "float sina;\n"
        + "sincosf(ang, &sina, &cosa);\n"
        + "float r   = 6.f + 2.f * varpar->xheart_ratio;\n"
        + "float r2_4 = __x*__x + __y*__y + 4.f;\n"
        + "r2_4 = r2_4 == 0.f ? 1.f : r2_4;\n"
        + "float bx = 4.f/r2_4; float by = r/r2_4;\n"
        + "float x = cosa * bx*__x - sina * by*__y;\n"
        + "float y = sina * bx*__x + cosa * by*__y;\n"
        + "\n"
        + "if (x > 0.f) {\n"
        + "    __px +=  varpar->xheart * x;\n"
        + "    __py +=  varpar->xheart * y;\n"
        + "} else {\n"
        + "    __px +=  varpar->xheart * x;\n"
        + "    __py += -varpar->xheart * y;\n"
        + "}\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->xheart*__z;\n" : "");
  }
}
