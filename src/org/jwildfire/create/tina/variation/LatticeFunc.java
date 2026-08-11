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

public class LatticeFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQ = "freq";
  private static final String PARAM_PULL = "pull";
  private static final String PARAM_SHEAR = "shear";
  private static final String[] paramNames = {PARAM_FREQ, PARAM_PULL, PARAM_SHEAR};

  private double freq = 3.0;
  private double pull = 0.5;
  private double shear = 0.0;

  private double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double lt_freq = Math.max(fabs(this.freq), 0.01);
    double lt_pull = clamp(this.pull, 0.0, 1.0);
    double lt_cx = Math.round(pAffineTP.x * lt_freq) / lt_freq;
    double lt_cy = Math.round(pAffineTP.y * lt_freq) / lt_freq;
    double lt_dx = lt_cx - pAffineTP.x;
    double lt_dy = lt_cy - pAffineTP.y;

    pVarTP.x += pAmount * (pAffineTP.x + lt_pull * lt_dx + this.shear * lt_dy);
    pVarTP.y += pAmount * (pAffineTP.y + lt_pull * lt_dy + this.shear * lt_dx);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{freq, pull, shear}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQ.equalsIgnoreCase(pName)) freq = pValue;
    else if (PARAM_PULL.equalsIgnoreCase(pName)) pull = pValue;
    else if (PARAM_SHEAR.equalsIgnoreCase(pName)) shear = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "lattice"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float lt_freq = fmaxf(fabsf(__lattice_freq), 0.01f);\n"
         + "  float lt_pull = clamp(__lattice_pull, 0.0f, 1.0f);\n"
         + "  float lt_cx = roundf(__x * lt_freq) / lt_freq;\n"
         + "  float lt_cy = roundf(__y * lt_freq) / lt_freq;\n"
         + "  float lt_dx = lt_cx - __x;\n"
         + "  float lt_dy = lt_cy - __y;\n"
         + "  __px += __lattice * (__x + lt_pull * lt_dx + __lattice_shear * lt_dy);\n"
         + "  __py += __lattice * (__y + lt_pull * lt_dy + __lattice_shear * lt_dx);\n";
  }
}