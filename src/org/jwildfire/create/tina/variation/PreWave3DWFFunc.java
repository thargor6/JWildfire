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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.SMALL_EPSILON;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class PreWave3DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_WAVELEN = "wavelen";
  private static final String PARAM_PHASE = "phase";
  private static final String PARAM_DAMPING = "damping";
  private static final String[] paramNames = { PARAM_WAVELEN, PARAM_PHASE, PARAM_DAMPING };

  private double wavelen = 0.5;
  private double phase = 0.0;
  private double damping = 0.01;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double dl = r / wavelen;
    double amplitude = pAmount;
    if (fabs(damping) > SMALL_EPSILON) {
      double dmp = -dl * damping;
      amplitude *= exp(dmp);
    }
    pAffineTP.z += amplitude * (double) sin(2.0 * M_PI * dl + phase);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { wavelen, phase, damping };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_WAVELEN.equalsIgnoreCase(pName))
      wavelen = pValue;
    else if (PARAM_PHASE.equalsIgnoreCase(pName))
      phase = pValue;
    else if (PARAM_DAMPING.equalsIgnoreCase(pName))
      damping = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pre_wave3D_wf";
  }

  @Override
  public int getPriority() {
    return -1;
  }

}
