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

public class Pie3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SLICES = "slices";
  private static final String PARAM_ROTATION = "rotation";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String[] paramNames = {PARAM_SLICES, PARAM_ROTATION, PARAM_THICKNESS};

  private double slices = 7;
  private double rotation = 0.0;
  private double thickness = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    int sl = (int) (pContext.random() * slices + 0.5);
    double a = rotation + 2.0 * M_PI * (sl + pContext.random() * thickness) / slices;
    double r = pAmount * pContext.random();
    double sina = sin(a);
    double cosa = cos(a);
    pVarTP.x += r * cosa;
    pVarTP.y += r * sina;
    pVarTP.z += r * sin(r);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{slices, rotation, thickness};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SLICES.equalsIgnoreCase(pName))
      slices = pValue;
    else if (PARAM_ROTATION.equalsIgnoreCase(pName))
      rotation = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pie3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float slices = __pie3D_slices;\n"
        + "int sl  = (int) (RANDFLOAT() * slices + 0.5f);\n"
        + "float a = __pie3D_rotation + 2.f * M_PI_F * (sl + RANDFLOAT() * __pie3D_thickness) / slices;\n"
        + "float r = __pie3D * RANDFLOAT();\n"
        + "float sina, cosa;\n"
        + "sincosf(a,  &sina, &cosa);\n"
        + "__px += r * cosa;\n"
        + "__py += r * sina;\n"
        + "__pz += r * sinf(r);\n";
  }
}
