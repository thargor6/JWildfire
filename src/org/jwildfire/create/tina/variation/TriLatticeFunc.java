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

public class TriLatticeFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_MORPH = "morph";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_MORPH};

  private double scale = 2.0;
  private double morph = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double tl_ax = pAffineTP.x * this.scale;
    double tl_ay = pAffineTP.y * this.scale;
    double tl_u = tl_ax - tl_ay * 0.5773502691896257;
    double tl_v = tl_ay * 1.1547005383792515;
    double tl_fu = tl_u - Math.round(tl_u);
    double tl_fv = tl_v - Math.round(tl_v);
    double tl_bx = tl_fu + tl_fv * 0.5;
    double tl_by = tl_fv * 0.8660254037844386;

    pVarTP.x += pAmount * (pAffineTP.x + this.morph * (tl_bx / this.scale - pAffineTP.x));
    pVarTP.y += pAmount * (pAffineTP.y + this.morph * (tl_by / this.scale - pAffineTP.y));
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, morph}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_MORPH.equalsIgnoreCase(pName)) morph = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "tri_lattice"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float tl_ax = __x * __tri_lattice_scale;\n"
         + "  float tl_ay = __y * __tri_lattice_scale;\n"
         + "  float tl_u = tl_ax - tl_ay * 0.57735027f;\n"
         + "  float tl_v = tl_ay * 1.15470054f;\n"
         + "  float tl_fu = tl_u - roundf(tl_u);\n"
         + "  float tl_fv = tl_v - roundf(tl_v);\n"
         + "  float tl_bx = tl_fu + tl_fv * 0.5f;\n"
         + "  float tl_by = tl_fv * 0.86602540f;\n"
         + "  __px += __tri_lattice * mix(__x, tl_bx / __tri_lattice_scale, __tri_lattice_morph);\n"
         + "  __py += __tri_lattice * mix(__y, tl_by / __tri_lattice_scale, __tri_lattice_morph);\n";
  }
}