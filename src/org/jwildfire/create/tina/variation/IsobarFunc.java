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

public class IsobarFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_AMP = "amp";
  private static final String PARAM_SPIN = "spin";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_AMP, PARAM_SPIN};

  private double freq = 3.0;
  private double amp = 0.3;
  private double spin = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r2 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double iso_r = sqrt(r2 + 1.0e-10);
    double iso_theta = atan2(pAffineTP.y, pAffineTP.x);
    double iso_band = sin(this.freq * iso_r) * this.amp;
    double iso_nr = iso_r + iso_band;
    double iso_nt = iso_theta + this.spin * iso_band;

    pVarTP.x += pAmount * iso_nr * cos(iso_nt);
    pVarTP.y += pAmount * iso_nr * sin(iso_nt);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, amp, spin}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_AMP.equalsIgnoreCase(pName)) amp = pValue;
    else if (PARAM_SPIN.equalsIgnoreCase(pName)) spin = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "isobar"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float iso_r = sqrtf(__x * __x + __y * __y + 1.0e-10f);\n"
         + "  float iso_theta = atan2f(__y, __x);\n"
         + "  float iso_band = sinf(__isobar_freq * iso_r) * __isobar_amp;\n"
         + "  float iso_nr = iso_r + iso_band;\n"
         + "  float iso_nt = iso_theta + __isobar_spin * iso_band;\n"
         + "  __px += __isobar * iso_nr * cosf(iso_nt);\n"
         + "  __py += __isobar * iso_nr * sinf(iso_nt);\n";
  }
}