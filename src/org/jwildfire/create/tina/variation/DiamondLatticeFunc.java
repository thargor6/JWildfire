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

public class DiamondLatticeFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_PULL = "pull";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_PULL};

  private double scale = 2.0;
  private double pull = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double dl_ax = pAffineTP.x * this.scale;
    double dl_ay = pAffineTP.y * this.scale;
    double dl_u = (dl_ax + dl_ay) * 0.7071067811865475;
    double dl_v = (dl_ax - dl_ay) * 0.7071067811865475;
    double dl_nu = Math.round(dl_u);
    double dl_nv = Math.round(dl_v);
    double dl_du = dl_u - dl_nu;
    double dl_dv = dl_v - dl_nv;
    double dl_bx = (dl_nu + dl_nv) * 0.7071067811865475;
    double dl_by = (dl_nu - dl_nv) * 0.7071067811865475;

    pVarTP.x += pAmount * (dl_bx + dl_du * (1.0 - this.pull)) / this.scale;
    pVarTP.y += pAmount * (dl_by + dl_dv * (1.0 - this.pull)) / this.scale;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, pull}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_PULL.equalsIgnoreCase(pName)) pull = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "diamond_lattice"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float dl_ax = __x * __diamond_lattice_scale;\n"
         + "  float dl_ay = __y * __diamond_lattice_scale;\n"
         + "  float dl_u = (dl_ax + dl_ay) * 0.70710678f;\n"
         + "  float dl_v = (dl_ax - dl_ay) * 0.70710678f;\n"
         + "  float dl_nu = roundf(dl_u);\n"
         + "  float dl_nv = roundf(dl_v);\n"
         + "  float dl_du = dl_u - dl_nu;\n"
         + "  float dl_dv = dl_v - dl_nv;\n"
         + "  float dl_bx = (dl_nu + dl_nv) * 0.70710678f;\n"
         + "  float dl_by = (dl_nu - dl_nv) * 0.70710678f;\n"
         + "  __px += __diamond_lattice * (dl_bx + dl_du * (1.0f - __diamond_lattice_pull)) / __diamond_lattice_scale;\n"
         + "  __py += __diamond_lattice * (dl_by + dl_dv * (1.0f - __diamond_lattice_pull)) / __diamond_lattice_scale;\n";
  }
}