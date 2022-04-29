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

public class PreRectWFFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X0 = "x0";
  private static final String PARAM_X1 = "x1";
  private static final String PARAM_Y0 = "y0";
  private static final String PARAM_Y1 = "y1";

  private static final String[] paramNames = {PARAM_X0, PARAM_X1, PARAM_Y0, PARAM_Y1};

  private double x0 = -0.5;
  private double x1 = 0.5;
  private double y0 = -0.5;
  private double y1 = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    pAffineTP.x = pAmount * (x0 + dx * pContext.random());
    pAffineTP.y = pAmount * (y0 + dy * pContext.random());
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{x0, x1, y0, y1};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X0.equalsIgnoreCase(pName))
      x0 = pValue;
    else if (PARAM_X1.equalsIgnoreCase(pName))
      x1 = pValue;
    else if (PARAM_Y0.equalsIgnoreCase(pName))
      y0 = pValue;
    else if (PARAM_Y1.equalsIgnoreCase(pName))
      y1 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pre_rect_wf";
  }

  @Override
  public int getPriority() {
    return -1;
  }

  double dx, dy;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    dx = x1 - x0;
    dy = y1 - y0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_PRE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float dx = __pre_rect_wf_x1 - __pre_rect_wf_x0;\n"
        + "float dy = __pre_rect_wf_y1 - __pre_rect_wf_y0;\n"
        + "__x = __pre_rect_wf * (__pre_rect_wf_x0 + dx * RANDFLOAT());\n"
        + "__y = __pre_rect_wf * (__pre_rect_wf_y0 + dy * RANDFLOAT());"
            + "__r2 = __x*__x+__y*__y;\n"
            + "__r = sqrtf(__r2);\n"
            + "__rinv = 1.f/__r;\n"
            + "__phi = atan2f(__x,__y);\n"
            + "__theta = .5f*PI-__phi;\n"
            + "if (__theta > PI)\n"
            + "    __theta -= 2.f*PI;";
  }
}
