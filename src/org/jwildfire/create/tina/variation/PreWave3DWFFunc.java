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
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PreWave3DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_WAVELEN = "wavelen";
  private static final String PARAM_PHASE = "phase";
  private static final String PARAM_DAMPING = "damping";
  private static final String PARAM_AXIS = "axis";
  private static final String PARAM_CENTRE_X = "centre_x";
  private static final String PARAM_CENTRE_Y = "centre_y";
  private static final String PARAM_CENTRE_Z = "centre_z";

  private static final String[] paramNames = {PARAM_AXIS, PARAM_WAVELEN, PARAM_PHASE, PARAM_DAMPING, PARAM_CENTRE_X, PARAM_CENTRE_Y, PARAM_CENTRE_Z};

  private static final int AXIS_XY = 0;
  private static final int AXIS_YZ = 1;
  private static final int AXIS_ZX = 2;
  private static final int AXIS_RADIAL = 3;

  private int axis = AXIS_XY;
  private double wavelen = 0.5;
  private double phase = 0.0;
  private double damping = 0.01;
  private double centre_x = 0.0;
  private double centre_y = 0.0;
  private double centre_z = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r;
    switch (axis) {
      case AXIS_RADIAL:
        r = sqrt(sqr(pAffineTP.x - centre_x) + sqr(pAffineTP.y - centre_y) + sqr(pAffineTP.z - centre_z));
        break;
      case AXIS_YZ:
        r = sqrt(sqr(pAffineTP.y - centre_y) + sqr(pAffineTP.z - centre_z));
        break;
      case AXIS_ZX:
        r = sqrt(sqr(pAffineTP.z - centre_z) + sqr(pAffineTP.x - centre_x));
        break;
      case AXIS_XY:
      default:
        r = sqrt(sqr(pAffineTP.x - centre_x) + sqr(pAffineTP.y - centre_y));
        break;
    }
    double dl = r / wavelen;
    double amplitude = pAmount;
    if (fabs(damping) > SMALL_EPSILON) {
      double dmp = -dl * damping;
      amplitude *= exp(dmp);
    }
    double amp = amplitude * (double) sin(2.0 * M_PI * dl + phase);

    switch (axis) {
      case AXIS_RADIAL:
        VectorD d = new VectorD(pAffineTP.x - centre_x, pAffineTP.y - centre_y, pAffineTP.z - centre_z);
        d.normalize();
        pAffineTP.x += d.x * amp;
        pAffineTP.y += d.y * amp;
        pAffineTP.z += d.z * amp;
        break;
      case AXIS_YZ:
        pAffineTP.x += amp;
        break;
      case AXIS_ZX:
        pAffineTP.y += amp;
        break;
      case AXIS_XY:
      default:
        pAffineTP.z += amp;
        break;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{axis, wavelen, phase, damping, centre_x, centre_y, centre_z};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_AXIS.equalsIgnoreCase(pName))
      axis = limitIntVal(Tools.FTOI(pValue), AXIS_XY, AXIS_RADIAL);
    else if (PARAM_WAVELEN.equalsIgnoreCase(pName))
      wavelen = pValue;
    else if (PARAM_PHASE.equalsIgnoreCase(pName))
      phase = pValue;
    else if (PARAM_DAMPING.equalsIgnoreCase(pName))
      damping = pValue;
    else if (PARAM_CENTRE_X.equalsIgnoreCase(pName))
      centre_x = pValue;
    else if (PARAM_CENTRE_Y.equalsIgnoreCase(pName))
      centre_y = pValue;
    else if (PARAM_CENTRE_Z.equalsIgnoreCase(pName))
      centre_z = pValue;
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
