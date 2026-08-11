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

import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class TidalLockFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RATIO = "ratio";
  private static final String PARAM_ECC = "ecc";
  private static final String[] paramNames = {PARAM_RATIO, PARAM_ECC};

  private double ratio = 1.0;
  private double ecc = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double tl_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double tl_a = atan2(pAffineTP.y, pAffineTP.x);
    double tl_new_a = tl_a * this.ratio + this.ecc * sin(2.0 * tl_a);

    pVarTP.x += pAmount * tl_r * cos(tl_new_a);
    pVarTP.y += pAmount * tl_r * sin(tl_new_a);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{ratio, ecc}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RATIO.equalsIgnoreCase(pName)) ratio = pValue;
    else if (PARAM_ECC.equalsIgnoreCase(pName)) ecc = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "tidal_lock"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float tl_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float tl_a = atan2f(__y, __x);\n"
         + "  float tl_new_a = tl_a * __tidal_lock_ratio + __tidal_lock_ecc * sinf(2.0f * tl_a);\n"
         + "  __px += __tidal_lock * tl_r * cosf(tl_new_a);\n"
         + "  __py += __tidal_lock * tl_r * sinf(tl_new_a);\n";
  }
}