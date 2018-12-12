package org.jwildfire.create.tina.variation;

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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * @author Branden Brown, a.k.a. zephyrtronium, transcribed by Nic Anderson, chronologicaldot
 * @date July 19, 2014 (transcribe)
 */
public class PostHeatFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_THETA_PERIOD = "theta_period";
  private static final String PARAM_THETA_PHASE = "theta_phase";
  private static final String PARAM_THETA_AMPLITUDE = "theta_amp";
  private static final String PARAM_PHI_PERIOD = "phi_period";
  private static final String PARAM_PHI_PHASE = "phi_phase";
  private static final String PARAM_PHI_AMPLITUDE = "phi_amp";
  private static final String PARAM_R_PERIOD = "r_period";
  private static final String PARAM_R_PHASE = "r_phase";
  private static final String PARAM_R_AMPLITUDE = "r_amp";

  private static final String[] paramNames = {PARAM_THETA_PERIOD, PARAM_THETA_PHASE, PARAM_THETA_AMPLITUDE, PARAM_PHI_PERIOD, PARAM_PHI_PHASE, PARAM_PHI_AMPLITUDE, PARAM_R_PERIOD, PARAM_R_PHASE, PARAM_R_AMPLITUDE};

  double theta_period = 0.0;
  double theta_phase = 0.0;
  double theta_amp = 0.0;
  double phi_period = 0.0;
  double phi_phase = 0.0;
  double phi_amp = 0.0;
  double r_period = 0.0;
  double r_phase = 0.0;
  double r_amp = 0.0;

  double at, bt, ct, ap, bp, cp, ar, br, cr;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double tx = theta_period == 0 ? 0.0 : 1.0 / theta_period;
    double px = phi_period == 0 ? 0.0 : 1.0 / phi_period;
    double rx = r_period == 0 ? 0.0 : 1.0 / r_period;

    at = pAmount * theta_amp;
    bt = M_2PI * tx;
    ct = theta_phase * tx;
    ap = pAmount * phi_amp;
    bp = M_2PI * px;
    cp = phi_phase * px;
    ar = pAmount * r_amp;
    br = M_2PI * rx;
    cr = r_phase * rx;

    double r = sqrt(sqr(pVarTP.x) + sqr(pVarTP.y) + sqr(pVarTP.z));

    double sint, cost, sinp, cosp, atant, acosp;

    atant = atan2(pVarTP.y, pVarTP.x);

    r += ar * sin(br * r + cr);

    sint = at * sin(bt * r + ct) + atant;
    cost = cos(sint);
    sint = sin(sint);

    if (r != 0) {
      acosp = pVarTP.z / r;
    } else {
      acosp = 0; //pVarTP.z >= 0? 10000 : -10000;
    }

    sinp = ap * sin(bp * r + cp) + acos(acosp);
    cosp = cos(sinp);
    sinp = sin(sinp);

    pVarTP.x = r * cost * sinp;
    pVarTP.y = r * sint * sinp;
    pVarTP.z = r * cosp;
  }

  @Override
  public String getName() {
    return "post_heat";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{
            theta_period, theta_phase, theta_amp,
            phi_period, phi_phase, phi_amp,
            r_period, r_phase, r_amp};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_THETA_PERIOD)) {
      theta_period = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_THETA_PHASE)) {
      theta_phase = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_THETA_AMPLITUDE)) {
      theta_amp = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_PHI_PERIOD)) {
      phi_period = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_PHI_PHASE)) {
      phi_phase = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_PHI_AMPLITUDE)) {
      phi_amp = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_R_PERIOD)) {
      r_period = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_R_PHASE)) {
      r_phase = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_R_AMPLITUDE)) {
      r_amp = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public int getPriority() {
    return 1;
  }
}