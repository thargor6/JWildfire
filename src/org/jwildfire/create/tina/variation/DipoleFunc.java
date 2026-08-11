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

public class DipoleFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SEPARATION = "separation";
  private static final String PARAM_STRENGTH = "strength";
  private static final String[] paramNames = {PARAM_SEPARATION, PARAM_STRENGTH};

  private double separation = 0.5;
  private double strength = 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double dx1 = pAffineTP.x + this.separation;
    double dx2 = pAffineTP.x - this.separation;
    double r1 = sqrt(dx1 * dx1 + pAffineTP.y * pAffineTP.y);
    double r2 = sqrt(dx2 * dx2 + pAffineTP.y * pAffineTP.y);
    
    r1 = Math.max(r1, 1e-4);
    r2 = Math.max(r2, 1e-4);
    double r13 = r1 * r1 * r1;
    double r23 = r2 * r2 * r2;
    
    double ex = dx1 / r13 - dx2 / r23;
    double ey = pAffineTP.y / r13 - pAffineTP.y / r23;

    pVarTP.x += pAmount * (pAffineTP.x + this.strength * ex);
    pVarTP.y += pAmount * (pAffineTP.y + this.strength * ey);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{separation, strength}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEPARATION.equalsIgnoreCase(pName)) separation = pValue;
    else if (PARAM_STRENGTH.equalsIgnoreCase(pName)) strength = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "dipole"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // Swapped out length() vector math for native scalar square-roots to ensure universal GPU compilation
    return "  float dx1 = __x + __dipole_separation;\n"
         + "  float dx2 = __x - __dipole_separation;\n"
         + "  float r1 = sqrtf(dx1 * dx1 + __y * __y);\n"
         + "  float r2 = sqrtf(dx2 * dx2 + __y * __y);\n"
         + "  r1 = fmaxf(r1, 1e-4f);\n"
         + "  r2 = fmaxf(r2, 1e-4f);\n"
         + "  float r13 = r1 * r1 * r1;\n"
         + "  float r23 = r2 * r2 * r2;\n"
         + "  float ex = dx1 / r13 - dx2 / r23;\n"
         + "  float ey = __y / r13 - __y / r23;\n"
         + "  __px += __dipole * (__x + __dipole_strength * ex);\n"
         + "  __py += __dipole * (__y + __dipole_strength * ey);\n";
  }
}