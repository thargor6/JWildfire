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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class RippleFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_FREQUENCY = "frequency";
  private static final String PARAM_VELOCITY = "velocity";
  private static final String PARAM_AMPLITUDE = "amplitude";
  private static final String PARAM_CENTERX = "centerx";
  private static final String PARAM_CENTERY = "centery";
  private static final String PARAM_PHASE = "phase";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_FIXED_DIST_CALC = "fixed_dist_calc";

  private static final String[] paramNames = {PARAM_FREQUENCY, PARAM_VELOCITY, PARAM_AMPLITUDE, PARAM_CENTERX, PARAM_CENTERY, PARAM_PHASE, PARAM_SCALE, PARAM_FIXED_DIST_CALC};

  private double frequency = 2.0;
  private double velocity = 1.0;
  private double amplitude = 0.5;
  private double centerx = 0.0;
  private double centery = 0.0;
  private double phase = 0.0;
  private double scale = 1.0;
  private int fixed_dist_calc = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Ripple by Xyrus02, http://xyrus02.deviantart.com/art/Ripple-Plugin-for-Apophysis-154713493   
    //align input x, y to given center and multiply with scale
    double x = (pAffineTP.x * _s) - centerx, y = (pAffineTP.y * _s) + centery;

    // calculate distance from center but constrain it to EPS
    double d = _fixed_dist_calc ? sqrt(x * x + y * y) : sqrt(x * x * y * y);
    if (d < SMALL_EPSILON)
      d = SMALL_EPSILON;

    // normalize (x,y)
    double nx = x / d, ny = y / d;

    // calculate cosine wave with given frequency, velocity 
    // and phase based on the distance to center
    double wave = cos(_f * d - _vxp);

    // calculate the wave offsets
    double d1 = wave * _pxa + d, d2 = wave * _pixa + d;

    // we got two offsets, so we also got two new positions (u,v)
    double u1 = (centerx + nx * d1), v1 = (-centery + ny * d1);
    double u2 = (centerx + nx * d2), v2 = (-centery + ny * d2);

    // interpolate the two positions by the given phase and
    // invert the multiplication with scale from before
    pVarTP.x = pAmount * (lerp(u1, u2, _p)) * _is;
    pVarTP.y = pAmount * (lerp(v1, v2, _p)) * _is;

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
    return new Object[]{frequency, velocity, amplitude, centerx, centery, phase, scale, fixed_dist_calc};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FREQUENCY.equalsIgnoreCase(pName))
      frequency = pValue;
    else if (PARAM_VELOCITY.equalsIgnoreCase(pName))
      velocity = pValue;
    else if (PARAM_AMPLITUDE.equalsIgnoreCase(pName))
      amplitude = pValue;
    else if (PARAM_CENTERX.equalsIgnoreCase(pName))
      centerx = pValue;
    else if (PARAM_CENTERY.equalsIgnoreCase(pName))
      centery = pValue;
    else if (PARAM_PHASE.equalsIgnoreCase(pName))
      phase = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_FIXED_DIST_CALC.equalsIgnoreCase(pName))
      fixed_dist_calc = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "ripple";
  }

  private double _f, _s, _p, _is, _pxa, _pixa, _vxp;
  private boolean _fixed_dist_calc;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // some variables are settled in another range for edit comfort
    // - transform them
    _f = frequency * 5;
    double a = amplitude * 0.01;
    _p = phase * M_2PI - M_PI;

    // scale must not be zero
    _s = scale == 0 ? SMALL_EPSILON : scale;

    // we will need the inverse scale
    _is = 1.0 / _s;

    // pre-multiply velocity+phase, phase+amplitude and (PI-phase)+amplitude
    _vxp = velocity * _p;
    _pxa = _p * a;
    _pixa = (M_PI - _p) * a;
    _fixed_dist_calc = fixed_dist_calc != 0;
  }

  private double lerp(double a, double b, double p) {
    return a + (b - a) * p;
  }

}
