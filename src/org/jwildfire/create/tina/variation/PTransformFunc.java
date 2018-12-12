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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PTransformFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ROTATE = "rotate";
  private static final String PARAM_POWER = "power";
  private static final String PARAM_MOVE = "move";
  private static final String PARAM_SPLIT = "split";
  private static final String PARAM_LOG = "use_log";

  private static final String[] paramNames = {PARAM_ROTATE, PARAM_POWER, PARAM_MOVE, PARAM_SPLIT, PARAM_LOG};

  private double rotate = 0.0;
  private int power = 1;
  private double move = 0.0;
  private double split = 0.0;
  private int log = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double rho, theta;

    rho = (log != 0) ? log(pAffineTP.getPrecalcSqrt()) / power + move : pAffineTP.getPrecalcSqrt() / power + move;
    theta = pAffineTP.getPrecalcAtanYX() + rotate;

    if (pAffineTP.x >= 0.0)
      rho += split;
    else
      rho -= split;

    if (log != 0) rho = exp(rho);

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
    return new Object[]{rotate, power, move, split, log};
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
      log = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pTransform";
  }

}
