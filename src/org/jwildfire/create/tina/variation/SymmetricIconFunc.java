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

public class SymmetricIconFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_LAMBDA = "lambda";
  private static final String PARAM_ALPHA = "alpha";
  private static final String PARAM_BETA = "beta";
  private static final String PARAM_OMEGA = "omega";
  private static final String[] paramNames = {PARAM_LAMBDA, PARAM_ALPHA, PARAM_BETA, PARAM_OMEGA};

  private double lambda = 1.56;
  private double alpha = -1.0;
  private double beta = 0.1;
  private double omega = -0.82;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r2 = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;
    double nx = this.lambda * pAffineTP.x + this.alpha * (pAffineTP.x * pAffineTP.x - pAffineTP.y * pAffineTP.y) + this.beta * r2 * pAffineTP.x + this.omega * pAffineTP.x * pAffineTP.y;
    double ny = this.lambda * pAffineTP.y + 2.0 * this.alpha * pAffineTP.x * pAffineTP.y + this.beta * r2 * pAffineTP.y + this.omega * pAffineTP.x * pAffineTP.x;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{lambda, alpha, beta, omega}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LAMBDA.equalsIgnoreCase(pName)) lambda = pValue;
    else if (PARAM_ALPHA.equalsIgnoreCase(pName)) alpha = pValue;
    else if (PARAM_BETA.equalsIgnoreCase(pName)) beta = pValue;
    else if (PARAM_OMEGA.equalsIgnoreCase(pName)) omega = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "symmetric_icon"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float r2 = __x * __x + __y * __y;\n"
         + "  float nx = __symmetric_icon_lambda * __x + __symmetric_icon_alpha * (__x * __x - __y * __y) + __symmetric_icon_beta * r2 * __x + __symmetric_icon_omega * __x * __y;\n"
         + "  float ny = __symmetric_icon_lambda * __y + 2.0f * __symmetric_icon_alpha * __x * __y + __symmetric_icon_beta * r2 * __y + __symmetric_icon_omega * __x * __x;\n"
         + "  __px += __symmetric_icon * nx;\n"
         + "  __py += __symmetric_icon * ny;\n";
  }
}