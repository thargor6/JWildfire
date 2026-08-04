/*

  JWildfire - an image and animation processor written in Java

  Copyright (C) 1995-2026 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.exp;

public class JetStreamFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPEED = "speed";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_CENTER = "center";
  private static final String[] paramNames = {PARAM_SPEED, PARAM_WIDTH, PARAM_CENTER};

  private double speed = 1.0;
  private double width = 0.3;
  private double center = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double js_width = Math.max(fabs(this.width), 0.01);
    double js_dy = pAffineTP.y - this.center;
    double js_profile = exp(-(js_dy * js_dy) / (js_width * js_width));

    pVarTP.x += pAmount * (pAffineTP.x + this.speed * js_profile);
    pVarTP.y += pAmount * pAffineTP.y;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{speed, width, center}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPEED.equalsIgnoreCase(pName)) speed = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName)) width = pValue;
    else if (PARAM_CENTER.equalsIgnoreCase(pName)) center = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "jet_stream"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float js_width = fmaxf(fabsf(__jet_stream_width), 0.01f);\n"
         + "  float js_dy = __y - __jet_stream_center;\n"
         + "  float js_profile = expf(-(js_dy * js_dy) / (js_width * js_width));\n"
         + "  __px += __jet_stream * (__x + __jet_stream_speed * js_profile);\n"
         + "  __py += __jet_stream * __y;\n";
  }
}