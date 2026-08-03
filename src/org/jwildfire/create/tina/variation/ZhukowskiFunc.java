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

public class ZhukowskiFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_C = "c";
  private static final String[] paramNames = {PARAM_C};

  private double c = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double zk_r2 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + 1e-6;

    pVarTP.x += pAmount * (pAffineTP.x + this.c * pAffineTP.x / zk_r2);
    pVarTP.y += pAmount * (pAffineTP.y - this.c * pAffineTP.y / zk_r2);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{c}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_C.equalsIgnoreCase(pName)) c = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "zhukowski"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float zk_r2 = __x * __x + __y * __y + 1e-6f;\n"
         + "  __px += __zhukowski * (__x + __zhukowski_c * __x / zk_r2);\n"
         + "  __py += __zhukowski * (__y - __zhukowski_c * __y / zk_r2);\n";
  }
}