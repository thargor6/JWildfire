/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.*;

public class LayeredSpiralFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";

  private static final String[] paramNames = {PARAM_RADIUS};

  private double radius = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* layered_spiral by Will Evans, http://eevans1.deviantart.com/art/kaleidoscope-plugin-122185469  */
    double a = pAffineTP.x * this.radius; // adjusts the layered_spiral radius.
    double t = sqr(pAffineTP.x) + sqr(pAffineTP.y) + EPSILON;
    pVarTP.x += pAmount * a * cos(t);
    pVarTP.y += pAmount * a * sin(t);

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{radius};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "layered_spiral";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float a = __x * __layered_spiral_radius;\n"
        + "float t = __x*__x + __y*__y ADD_EPSILON;\n"
        + "float c;\n"
        + "float s;\n"
        + "sincosf(t, &s, &c);\n"
        + "\n"
        + "__px += __layered_spiral * a * c;\n"
        + "__py += __layered_spiral * a * s;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __layered_spiral * __z;\n" : "");
  }
}

