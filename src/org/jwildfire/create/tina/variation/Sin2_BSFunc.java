/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

public class Sin2_BSFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X1 = "x1";
  private static final String PARAM_X2 = "x2";
  private static final String PARAM_Y1 = "y1";
  private static final String PARAM_Y2 = "y2";
  private static final String[] paramNames = {PARAM_X1, PARAM_X2, PARAM_Y1, PARAM_Y2};
  private double x1 = 1.0;
  private double x2 = 1.0;
  private double y1 = 1.0;
  private double y2 = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* complex vars by cothe */
    /* exp log sin cos tan sec csc cot sinh cosh tanh sech csch coth */
    /* Variables added by Brad Stefanov */
    //Sine SIN
    double sinsin = sin(pAffineTP.x * x1);
    double sincos = cos(pAffineTP.x * x2);
    double sinsinh = sinh(pAffineTP.y * y1);
    double sincosh = cosh(pAffineTP.y * y2);
    pVarTP.x += pAmount * sinsin * sincosh;
    pVarTP.y += pAmount * sincos * sinsinh;
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
    return new Object[]{x1, x2, y1, y2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X1.equalsIgnoreCase(pName))
      x1 = pValue;
    else if (PARAM_X2.equalsIgnoreCase(pName))
      x2 = pValue;
    else if (PARAM_Y1.equalsIgnoreCase(pName))
      y1 = pValue;
    else if (PARAM_Y2.equalsIgnoreCase(pName))
      y2 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "sin2_bs";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float sinsin = sinf(__x *  __sin2_bs_x1 );"
    		+"    float sincos = cosf(__x *  __sin2_bs_x2 );"
    		+"    float sinsinh = sinhf(__y *  __sin2_bs_y1 );"
    		+"    float sincosh = coshf(__y *  __sin2_bs_y2 );"
    		+"    __px += __sin2_bs * sinsin * sincosh;"
    		+"    __py += __sin2_bs * sincos * sinsinh;"
            + (context.isPreserveZCoordinate() ? "__pz += __sin2_bs *__z;" : "");
  }
}
