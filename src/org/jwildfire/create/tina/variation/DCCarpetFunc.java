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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.fmod;

public class DCCarpetFunc extends VariationFunc implements SupportsGPU {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_ORIGIN = "origin";

  private static final String[] paramNames = {PARAM_ORIGIN};

  private double origin = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* dc_carpet by Xyrus02, http://apophysis-7x.org/extensions */
    int x0 = (pContext.random() < 0.5) ? -1 : 1;
    int y0 = (pContext.random() > 0.5) ? -1 : 1;

    double x = pAffineTP.x + x0, y = pAffineTP.y + y0;
    double x0_xor_y0 = (double) (x0 ^ y0);
    double h = -H + (1 - x0_xor_y0) * H;

    pVarTP.x += pAmount * (pXForm.getXYCoeff00() * x + pXForm.getXYCoeff10() * y + pXForm.getXYCoeff20());
    pVarTP.y += pAmount * (pXForm.getXYCoeff01() * x + pXForm.getXYCoeff11() * y + pXForm.getXYCoeff21());
    pVarTP.color = fmod(fabs(pVarTP.color * 0.5 * (1 + h) + x0_xor_y0 * (1 - h) * 0.5), 1.0);
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
    return new Object[]{origin};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ORIGIN.equalsIgnoreCase(pName))
      origin = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_carpet";
  }

  private double H;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    H = 0.1 * origin;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float H = 0.1f * __dc_carpet_origin;\n"
        + "int x0 = (RANDFLOAT() < 0.5f) ? -1 : 1;\n"
        + "int y0 = (RANDFLOAT() > 0.5f) ? -1 : 1;\n"
        + "float x = __x + x0, y = __y + y0;\n"
        + "float x0_xor_y0 = (float) (x0 ^ y0);\n"
        + "float h = -H + (1.f - x0_xor_y0) * H;\n"
        + "__px += __dc_carpet * (xform->a * x + xform->b * y + xform->c);\n"
        + "__py += __dc_carpet * (xform->d * x + xform->e * y + xform->f);\n"
        + "__pal = fmodf(fabsf(__pal * 0.5f * (1.f + h) + x0_xor_y0 * (1.f - h) * 0.5f), 1.0f);"
        + (context.isPreserveZCoordinate() ? "__pz += __dc_carpet * __z;\n" : "");
  }
}
