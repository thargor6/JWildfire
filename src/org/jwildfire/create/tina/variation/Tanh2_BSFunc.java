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

public class Tanh2_BSFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X1 = "x1";
  private static final String PARAM_X2 = "x2";
  private static final String PARAM_Y1 = "y1";
  private static final String PARAM_Y2 = "y2";
  private static final String[] paramNames = {PARAM_X1, PARAM_X2, PARAM_Y1, PARAM_Y2};
  private double x1 = 2.0;
  private double x2 = 2.0;
  private double y1 = 2.0;
  private double y2 = 2.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* complex vars by cothe */
    /* exp log sin cos tan sec csc cot sinh cosh tanh sech csch coth */
    /* Variables added by Brad Stefanov */
    //Hyperbolic Tangent TANH
    double tanhsin = sin(y1 * pAffineTP.y);
    double tanhcos = cos(y2 * pAffineTP.y);
    double tanhsinh = sinh(x1 * pAffineTP.x);
    double tanhcosh = cosh(x2 * pAffineTP.x);
    double d = (tanhcos + tanhcosh);
    if (d == 0) {
      return;
    }
    double tanhden = 1.0 / d;
    pVarTP.x += pAmount * tanhden * tanhsinh;
    pVarTP.y += pAmount * tanhden * tanhsin;
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
    return "tanh2_bs";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float tanhsin = sinf( __tanh2_bs_y1  * __y);"
    		+"    float tanhcos = cosf( __tanh2_bs_y2  * __y);"
    		+"    float tanhsinh = sinhf( __tanh2_bs_x1  * __x);"
    		+"    float tanhcosh = coshf( __tanh2_bs_x2  * __x);"
    		+"    float d = (tanhcos + tanhcosh);"
    		+"    if (d != 0) {"
    		+"      float tanhden = 1.0 / d;"
    		+"      __px += __tanh2_bs * tanhden * tanhsinh;"
    		+"      __py += __tanh2_bs * tanhden * tanhsin;"
    		+"    }"
            + (context.isPreserveZCoordinate() ? "__pz += __tanh2_bs *__z;" : "");
  }
}
