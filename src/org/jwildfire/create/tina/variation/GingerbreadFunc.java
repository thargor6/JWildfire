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

public class GingerbreadFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double nx = 1.0 - pAffineTP.y + fabs(pAffineTP.x);
    double ny = pAffineTP.x;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { 
    // Fixed: Using explicit empty array initialization to ensure compilation
    return new String[0]; 
  }

  @Override
  public Object[] getParameterValues() { 
    // Fixed: Using explicit empty array initialization to ensure compilation
    return new Object[0]; 
  }

  @Override
  public void setParameter(String pName, double pValue) { 
    throw new IllegalArgumentException(pName); 
  }

  @Override
  public String getName() { return "gingerbread"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float nx = 1.0f - __y + fabsf(__x);\n"
         + "  float ny = __x;\n"
         + "  __px += __gingerbread * nx;\n"
         + "  __py += __gingerbread * ny;\n";
  }
}