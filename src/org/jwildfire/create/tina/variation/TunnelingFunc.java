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

import static org.jwildfire.base.mathlib.MathLib.exp;

public class TunnelingFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_BARRIER = "barrier";
  private static final String PARAM_DECAY = "decay";
  private static final String[] paramNames = {PARAM_BARRIER, PARAM_DECAY};

  private double barrier = 0.5;
  private double decay = 3.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double qt_x = pAffineTP.x - this.barrier;
    double qt_prob = qt_x > 0.0 ? exp(-this.decay * qt_x) : 1.0;

    pVarTP.x += pAmount * pAffineTP.x * qt_prob;
    pVarTP.y += pAmount * pAffineTP.y * qt_prob;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{barrier, decay}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_BARRIER.equalsIgnoreCase(pName)) barrier = pValue;
    else if (PARAM_DECAY.equalsIgnoreCase(pName)) decay = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "tunneling"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float qt_x = __x - __tunneling_barrier;\n"
         + "  float qt_prob = (qt_x > 0.0f) ? expf(-__tunneling_decay * qt_x) : 1.0f;\n"
         + "  __px += __tunneling * __x * qt_prob;\n"
         + "  __py += __tunneling * __y * qt_prob;\n";
  }
}