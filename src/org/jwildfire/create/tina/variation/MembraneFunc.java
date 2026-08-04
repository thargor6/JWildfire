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
import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.exp;

public class MembraneFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_HEIGHT = "height";
  private static final String PARAM_STIFF = "stiff";
  private static final String[] paramNames = {PARAM_RADIUS, PARAM_HEIGHT, PARAM_STIFF};

  private double radius = 0.5;
  private double height = 0.3;
  private double stiff = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double mb_radius = Math.max(fabs(this.radius), 0.01);
    double mb_stiff = Math.max(fabs(this.stiff), 0.01);
    
    double mb_r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double mb_ring = mb_r - mb_radius;
    
    double mb_env = exp(-mb_ring * mb_ring / (mb_radius * mb_radius * mb_stiff));
    
    double mb_safe = Math.max(mb_r, 0.001);
    double il_scale = (mb_r + this.height * mb_env) / mb_safe;

    // Direct assignment operator to properly map the warped space as its own structural layer
    pVarTP.x = pAmount * pAffineTP.x * il_scale;
    pVarTP.y = pAmount * pAffineTP.y * il_scale;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{radius, height, stiff}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName)) radius = pValue;
    else if (PARAM_HEIGHT.equalsIgnoreCase(pName)) height = pValue;
    else if (PARAM_STIFF.equalsIgnoreCase(pName)) stiff = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "membrane"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // Uses absolute assignment (=) rather than addition (+=) to allow full coordinate space expansion
    return "  float mb_radius = fmaxf(fabsf(__membrane_radius), 0.01f);\n"
         + "  float mb_stiff = fmaxf(fabsf(__membrane_stiff), 0.01f);\n"
         + "  float mb_r = sqrtf(__x * __x + __y * __y);\n"
         + "  float mb_ring = mb_r - mb_radius;\n"
         + "  float mb_env = expf(-(mb_ring * mb_ring) / (mb_radius * mb_radius * mb_stiff));\n"
         + "  float mb_safe = fmaxf(mb_r, 0.001f);\n"
         + "  float il_scale = (mb_r + __membrane_height * mb_env) / mb_safe;\n"
         + "  __px = __membrane * __x * il_scale;\n"
         + "  __py = __membrane * __y * il_scale;\n";
  }
}