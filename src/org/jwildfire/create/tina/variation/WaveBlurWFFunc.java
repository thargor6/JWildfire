/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

public class WaveBlurWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_COUNT = "count";
  public static final String PARAM_AMPLITUDE_Z = "amplitude_z";
  public static final String PARAM_PHASE = "phase";
  public static final String PARAM_DAMPING_Z = "damping_z";
  public static final String PARAM_DIRECT_COLOR = "direct_color";
  public static final String PARAM_COLOR_SCALE = "color_scale";
  public static final String PARAM_COLOR_OFFSET = "color_offset";
  private static final String[] paramNames = {PARAM_COUNT, PARAM_PHASE, PARAM_AMPLITUDE_Z, PARAM_DAMPING_Z, PARAM_DIRECT_COLOR, PARAM_COLOR_SCALE, PARAM_COLOR_OFFSET};

  private int count = 5;
  private double amplitude_z = 0.5;
  private double phase = 0.0;
  private double damping_z = 0.0;
  private double color_scale = 0.5;
  private double color_offset = 0.0;
  private int direct_color = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ang = pContext.random() * M_2PI;
    double rnd = 1.0 - 2.0 * pContext.random();
    double r;
    if (pContext.random() < 0.5) {
      r = acos(rnd) - (pContext.random(count) + 1) * M_PI + phase;
    } else {
      r = acos(-rnd) - (pContext.random(count) + 1) * M_PI + phase;
    }
    r *= pAmount / (count * M_PI);

    double s = sin(ang);
    double c = cos(ang);
    pVarTP.x += r * c;
    pVarTP.y += r * s;
    if (direct_color == 1) {
      pVarTP.color = limitVal(acos(rnd) * color_scale + color_offset, 0.0, 1.0);
    }
    if (amplitude_z != 0.0) {
      double zamp = (cos(rnd) - 0.5) * amplitude_z;
      if (fabs(damping_z) > SMALL_EPSILON) {
        double dmp = r * damping_z;
        zamp *= exp(dmp);
      }
      pVarTP.z += pAmount * zamp;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{count, phase, amplitude_z, damping_z, direct_color, color_scale, color_offset};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_COUNT.equalsIgnoreCase(pName)) {
      count = Tools.FTOI(pValue);
      if (count < 1) {
        count = 1;
      }
    } else if (PARAM_DIRECT_COLOR.equalsIgnoreCase(pName)) {
      direct_color = limitIntVal(Tools.FTOI(pValue), 0, 1);
    } else if (PARAM_AMPLITUDE_Z.equalsIgnoreCase(pName)) {
      amplitude_z = pValue;
    } else if (PARAM_PHASE.equalsIgnoreCase(pName)) {
      phase = pValue;
    } else if (PARAM_DAMPING_Z.equalsIgnoreCase(pName)) {
      damping_z = pValue;
      if (damping_z < 0.0) {
        damping_z = 0.0;
      }
    } else if (PARAM_COLOR_SCALE.equalsIgnoreCase(pName)) {
      color_scale = pValue;
    } else if (PARAM_COLOR_OFFSET.equalsIgnoreCase(pName)) {
      color_offset = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "waveblur_wf";
  }

}
