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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class ToroidalFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_MAJOR = "major";
  private static final String PARAM_MINOR = "minor";
  private static final String[] paramNames = {PARAM_MAJOR, PARAM_MINOR};

  private double major = 2.0;
  private double minor = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double tor_x = (this.major + this.minor * cos(pAffineTP.y)) * cos(pAffineTP.x);
    double tor_y = (this.major + this.minor * cos(pAffineTP.y)) * sin(pAffineTP.x);

    pVarTP.x += pAmount * tor_x;
    pVarTP.y += pAmount * tor_y;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{major, minor}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MAJOR.equalsIgnoreCase(pName)) major = pValue;
    else if (PARAM_MINOR.equalsIgnoreCase(pName)) minor = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "toroidal"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float tor_x = (__toroidal_major + __toroidal_minor * cosf(__y)) * cosf(__x);\n"
         + "  float tor_y = (__toroidal_major + __toroidal_minor * cosf(__y)) * sinf(__x);\n"
         + "  __px += __toroidal * tor_x;\n"
         + "  __py += __toroidal * tor_y;\n";
  }
}