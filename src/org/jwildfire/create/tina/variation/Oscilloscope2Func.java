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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Oscilloscope2Func extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_SEPARATION = "separation";
  private static final String PARAM_FREQUENCYX = "frequencyx";
  private static final String PARAM_FREQUENCYY = "frequencyy";
  private static final String PARAM_AMPLITUDE = "amplitude";
  private static final String PARAM_PTB = "perturbation";
  private static final String PARAM_DAMPING = "damping";

  private static final String[] paramNames = {PARAM_SEPARATION, PARAM_FREQUENCYX, PARAM_FREQUENCYY, PARAM_AMPLITUDE, PARAM_PTB, PARAM_DAMPING};

  private double separation = 1.00;
  private double frequencyx = M_PI;
  private double frequencyy = M_PI;
  private double amplitude = 1.0;
  private double perturbation = 1.0;
  private double damping = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* oscilloscope from the apophysis plugin pack tweak darkbeam */
    double t;
    double pt = perturbation * sin(_tpf2 * pAffineTP.y);
    if (_noDamping) {
      t = amplitude * (cos(_tpf * pAffineTP.x + pt)) + separation;
    } else {
      t = amplitude * exp(-fabs(pAffineTP.x) * damping) * (cos(_tpf * pAffineTP.x + pt)) + separation;
    }

    if (fabs(pAffineTP.y) <= t) {
      pVarTP.x -= pAmount * pAffineTP.x;
      pVarTP.y -= pAmount * pAffineTP.y;
    } else {
      pVarTP.x += pAmount * pAffineTP.x;
      pVarTP.y += pAmount * pAffineTP.y;
    }
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
    return new Object[]{separation, frequencyx, frequencyy, amplitude, perturbation, damping};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEPARATION.equalsIgnoreCase(pName))
      separation = pValue;
    else if (PARAM_FREQUENCYX.equalsIgnoreCase(pName))
      frequencyx = pValue;
    else if (PARAM_FREQUENCYY.equalsIgnoreCase(pName))
      frequencyy = pValue;
    else if (PARAM_AMPLITUDE.equalsIgnoreCase(pName))
      amplitude = pValue;
    else if (PARAM_PTB.equalsIgnoreCase(pName))
      perturbation = pValue;
    else if (PARAM_DAMPING.equalsIgnoreCase(pName))
      damping = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"osco2_separation", "osco2_frequencyx", "osco2_frequencyy", "osco2_amplitude", "osco2_perturbation", "osco2_damping"};
  }

  @Override
  public String getName() {
    return "oscilloscope2";
  }

  private double _tpf;
  private double _tpf2;
  private boolean _noDamping;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _tpf = 2.0f * M_PI * frequencyx;
    _tpf2 = 2.0f * M_PI * frequencyy;
    _noDamping = fabs(damping) <= EPSILON;
  }
}
