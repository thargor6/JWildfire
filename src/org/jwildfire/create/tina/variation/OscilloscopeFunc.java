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

public class OscilloscopeFunc extends VariationFunc {

  private static final String PARAM_SEPARATION = "separation";
  private static final String PARAM_FREQUENCY = "frequency";
  private static final String PARAM_AMPLITUDE = "amplitude";
  private static final String PARAM_DAMPING = "damping";

  private static final String[] paramNames = { PARAM_SEPARATION, PARAM_FREQUENCY, PARAM_AMPLITUDE, PARAM_DAMPING };

  private double separation = 1.00;
  private double frequency = Math.PI;
  private double amplitude = 1.0;
  private double damping = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* oscilloscope from the apophysis plugin pack */
    double tpf = 2 * Math.PI * frequency;
    double t;
    if (Math.abs(damping) <= Tools.EPSILON) {
      t = amplitude * pContext.cos(tpf * pAffineTP.x) + separation;
    }
    else {
      t = amplitude * pContext.exp(-Math.abs(pAffineTP.x) * damping) * pContext.cos(tpf * pAffineTP.x) + separation;
    }

    if (Math.abs(pAffineTP.y) <= t) {
      pVarTP.x += pAmount * pAffineTP.x;
      pVarTP.y -= pAmount * pAffineTP.y;
    }
    else {
      pVarTP.x += pAmount * pAffineTP.x;
      pVarTP.y += pAmount * pAffineTP.y;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { separation, frequency, amplitude, damping };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEPARATION.equalsIgnoreCase(pName))
      separation = pValue;
    else if (PARAM_FREQUENCY.equalsIgnoreCase(pName))
      frequency = pValue;
    else if (PARAM_AMPLITUDE.equalsIgnoreCase(pName))
      amplitude = pValue;
    else if (PARAM_DAMPING.equalsIgnoreCase(pName))
      damping = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "oscilloscope";
  }

}
