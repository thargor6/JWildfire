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

public class FaultFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_DISPLACEMENT = "displacement";
  private static final String[] paramNames = {PARAM_ANGLE, PARAM_DISPLACEMENT};

  private double angle = 30.0;
  private double displacement = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ft_angle = this.angle * Math.PI / 180.0;
    double ft_nx = cos(ft_angle + Math.PI * 0.5);
    double ft_ny = sin(ft_angle + Math.PI * 0.5);
    
    double side = pAffineTP.x * ft_nx + pAffineTP.y * ft_ny;
    // Replaced sgn() with standard inline check to fix the compiler error
    double side_sign = (side == 0.0) ? 0.0 : ((side < 0.0) ? -1.0 : 1.0);

    double ft_cos = cos(ft_angle);
    double ft_sin = sin(ft_angle);

    pVarTP.x += pAmount * (pAffineTP.x + side_sign * this.displacement * ft_cos);
    pVarTP.y += pAmount * (pAffineTP.y + side_sign * this.displacement * ft_sin);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{angle, displacement}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANGLE.equalsIgnoreCase(pName)) angle = pValue;
    else if (PARAM_DISPLACEMENT.equalsIgnoreCase(pName)) displacement = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "fault"; }
  
  @Override
  public void randomize() {
  	angle = Math.random() * 360.0 - 180.0;
  	displacement = Math.random() * 6.0 - 3.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

@Override
  public String getGPUCode(FlameTransformationContext context) {
    // Replaced M_PI with float literal 3.14159265f to prevent strict OpenCL header errors
    return "  float ft_angle = __fault_angle * 3.14159265f / 180.0f;\n"
         + "  float ft_nx = cosf(ft_angle + 1.57079632f);\n"
         + "  float ft_ny = sinf(ft_angle + 1.57079632f);\n"
         + "  float fault_line_dist = __x * ft_nx + __y * ft_ny;\n"
         + "  float side = 1.0f;\n"
         + "  if (fault_line_dist < 0.0f) { side = -1.0f; }\n"
         + "  else if (fault_line_dist == 0.0f) { side = 0.0f; }\n"
         + "  float ft_cos = cosf(ft_angle);\n"
         + "  float ft_sin = sinf(ft_angle);\n"
         + "  __px += __fault * (__x + side * __fault_displacement * ft_cos);\n"
         + "  __py += __fault * (__y + side * __fault_displacement * ft_sin);\n";
  }
}