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

public class GearTeethFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_TEETH = "teeth";
  private static final String PARAM_DEPTH = "depth";
  private static final String[] paramNames = {PARAM_TEETH, PARAM_DEPTH};

  private double teeth = 12.0;
  private double depth = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double gt_a = atan2(pAffineTP.y, pAffineTP.x);
    double gt_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double gt_tooth = 0.5 * (1.0 + cos(this.teeth * gt_a));
    double gt_rmod = gt_r + this.depth * gt_tooth;

    pVarTP.x += pAmount * gt_rmod * cos(gt_a);
    pVarTP.y += pAmount * gt_rmod * sin(gt_a);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{teeth, depth}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_TEETH.equalsIgnoreCase(pName)) teeth = pValue;
    else if (PARAM_DEPTH.equalsIgnoreCase(pName)) depth = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "gear_teeth"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float gt_a = atan2f(__y, __x);\n"
         + "  float gt_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float gt_tooth = 0.5f * (1.0f + cosf(__gear_teeth_teeth * gt_a));\n"
         + "  float gt_rmod = gt_r + __gear_teeth_depth * gt_tooth;\n"
         + "  __px += __gear_teeth * gt_rmod * cosf(gt_a);\n"
         + "  __py += __gear_teeth * gt_rmod * sinf(gt_a);\n";
  }
}