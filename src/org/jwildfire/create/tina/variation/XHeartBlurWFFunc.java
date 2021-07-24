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

public class XHeartBlurWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_ANGLE = "angle";
  public static final String PARAM_RATIO = "ratio";
  private static final String[] paramNames = {PARAM_ANGLE, PARAM_RATIO};

  private double angle = 0.0;
  private double ratio = 0.0;
  // derived variables
  private double rat, cosa, sina;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // based on xheart by xyrus02, http://xyrus02.deviantart.com/art/XHeart-Plugin-139866412
    double dx = 2.0 - pContext.random() * 4.0;
    double dy = 2.0 - pContext.random() * 4.0;

    double r2_4 = dx * dx + dy * dy + 4;
    if (r2_4 == 0)
      r2_4 = 1;
    double bx = 4 / r2_4, by = rat / r2_4;
    double x = cosa * (bx * dx) - sina * (by * dy);
    double y = sina * (bx * dx) + cosa * (by * dy);

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
    return "xheart_blur_wf";
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
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float rat, cosa, sina;\n"
        + "float ang = 0.25f*PI + (0.5f * 0.25f*PI * __xheart_blur_wf_angle);\n"
        + "sina = sinf(ang);\n"
        + "cosa = cosf(ang);\n"
        + "rat = 6.f + 2.f * __xheart_blur_wf_ratio;\n"
        + "float dx = 2.0f - RANDFLOAT() * 4.0f;\n"
        + "float dy = 2.0f - RANDFLOAT() * 4.0f;\n"
        + "float r2_4 = dx * dx + dy * dy + 4.f;\n"
        + "if (fabsf(r2_4) <= 1.e-6f)\n"
        + "  r2_4 = 1.f;\n"
        + "float bx = 4.f / r2_4, by = rat / r2_4;\n"
        + "float x = cosa * (bx * dx) - sina * (by * dy);\n"
        + "float y = sina * (bx * dx) + cosa * (by * dy);\n"
        + "if (x > 0) {\n"
        + "  __px += __xheart_blur_wf * x;\n"
        + "  __py += __xheart_blur_wf * y;\n"
        + "} else {\n"
        + "  __px += __xheart_blur_wf * x;\n"
        + "  __py += -__xheart_blur_wf * y;\n"
        + "}\n"
        + (context.isPreserveZCoordinate() ? "__pz += __xheart_blur_wf * __z;\n" : "");
  }
}


