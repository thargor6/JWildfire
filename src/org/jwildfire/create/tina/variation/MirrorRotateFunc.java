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

public class MirrorRotateFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ANGLE = "angle";
  private static final String[] paramNames = {PARAM_ANGLE};

  private double angle = 45.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double mr_angle = this.angle * 0.017453292519943295;
    double c2 = cos(2.0 * mr_angle);
    double s2 = sin(2.0 * mr_angle);
    double nx = pAffineTP.x * c2 + pAffineTP.y * s2;
    double ny = pAffineTP.x * s2 - pAffineTP.y * c2;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{angle}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANGLE.equalsIgnoreCase(pName)) angle = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "mirror_rotate"; }
  
  @Override
  public void randomize() {
  	angle = Math.random() * 180.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float mr_angle = __mirror_rotate_angle * 0.01745329f;\n"
         + "  float c2 = cosf(2.0f * mr_angle);\n"
         + "  float s2 = sinf(2.0f * mr_angle);\n"
         + "  float nx = __x * c2 + __y * s2;\n"
         + "  float ny = __x * s2 - __y * c2;\n"
         + "  __px += __mirror_rotate * nx;\n"
         + "  __py += __mirror_rotate * ny;\n";
  }
}