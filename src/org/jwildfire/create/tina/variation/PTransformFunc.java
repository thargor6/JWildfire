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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PTransformFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ROTATE = "rotate";
  private static final String PARAM_POWER = "power";
  private static final String PARAM_MOVE = "move";
  private static final String PARAM_SPLIT = "split";
  private static final String PARAM_LOG = "use_log";

  private static final String[] paramNames = {PARAM_ROTATE, PARAM_POWER, PARAM_MOVE, PARAM_SPLIT, PARAM_LOG};

  private double rotate = 0.30;
  private int power = 2;
  private double move = 0.40;
  private double split = 0.0;
  private int use_log = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double rho, theta;

    rho = (use_log != 0) ? log(pAffineTP.getPrecalcSqrt()) / power + move : pAffineTP.getPrecalcSqrt() / power + move;
    theta = pAffineTP.getPrecalcAtanYX() + rotate;

    if (pAffineTP.x >= 0.0)
      rho += split;
    else
      rho -= split;

    if (use_log != 0) rho = exp(rho);

    pVarTP.x += pAmount * rho * cos(theta);
    pVarTP.y += pAmount * rho * sin(theta);

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
    return new Object[]{rotate, power, move, split, use_log};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ROTATE.equalsIgnoreCase(pName))
      rotate = pValue;
    else if (PARAM_POWER.equalsIgnoreCase(pName))
      power = limitIntVal(Tools.FTOI(pValue), 1, Integer.MAX_VALUE);
    else if (PARAM_MOVE.equalsIgnoreCase(pName))
      move = pValue;
    else if (PARAM_SPLIT.equalsIgnoreCase(pName))
      split = pValue;
    else if (PARAM_LOG.equalsIgnoreCase(pName))
      use_log = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pTransform";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return  "int use_log = roundf(__pTransform_use_log);\n"
        + "int power = lroundf(__pTransform_power);\n"
        + "float rho = (use_log != 0) ? logf(__r) / power + __pTransform_move : __r / power + __pTransform_move;\n"
        + "float theta = __theta + __pTransform_rotate;\n"
        + "    if (__x >= 0.0)\n"
        + "      rho += __pTransform_split;\n"
        + "    else\n"
        + "      rho -= __pTransform_split;\n"
        + "    if (use_log != 0) rho = expf(rho);\n"
        + "    __px += __pTransform * rho * cosf(theta);\n"
        + "    __py += __pTransform * rho * sinf(theta);\n"
        + (context.isPreserveZCoordinate() ? "__pz += __pTransform * __z;\n" : "");
  }
}
