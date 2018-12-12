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

public class PrePostAffineFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE_X = "scale_x";
  private static final String PARAM_SCALE_Y = "scale_y";
  private static final String PARAM_SCALE_Z = "scale_z";
  private static final String PARAM_YAW = "yaw";
  private static final String PARAM_PITCH = "pitch";
  private static final String PARAM_ROLL = "roll";
  private static final String PARAM_MOVE_X = "move_x";
  private static final String PARAM_MOVE_Y = "move_y";
  private static final String PARAM_MOVE_Z = "move_z";

  private static final String[] paramNames = {PARAM_SCALE_X, PARAM_SCALE_Y, PARAM_SCALE_Z, PARAM_YAW, PARAM_PITCH, PARAM_ROLL, PARAM_MOVE_X, PARAM_MOVE_Y, PARAM_MOVE_Z};

  private double scale_x = 1;
  private double scale_y = 1;
  private double scale_z = 1;
  private double yaw = 0;
  private double pitch = 0;
  private double roll = 0;
  private double move_x = 0;
  private double move_y = 0;
  private double move_z = 0;

  private double coefxx, coefxy, coefxz, coefyx, coefyy, coefyz, coefzx, coefzy, coefzz;
  private double icoefxx, icoefxy, icoefxz, icoefyx, icoefyy, icoefyz, icoefzx, icoefzy, icoefzz;


  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // pre-calculate coefficients for transform
    double d2r = M_PI / 180;
    double sinyaw = sin(yaw * d2r), cosyaw = cos(yaw * d2r);
    double sinpitch = sin(pitch * d2r), cospitch = cos(pitch * d2r);
    double sinroll = sin(roll * d2r), cosroll = cos(roll * d2r);

    coefxx = cosyaw * cosroll - sinyaw * sinpitch * sinroll;
    coefxy = -sinyaw * cosroll - cosyaw * sinpitch * sinroll;
    coefxz = -cospitch * sinroll;

    coefyx = sinyaw * cospitch;
    coefyy = cosyaw * cospitch;
    coefyz = -sinpitch;

    coefzx = cosyaw * sinroll + sinyaw * sinpitch * cosroll;
    coefzy = cosyaw * sinpitch * cosroll - sinyaw * sinroll;
    coefzz = cospitch * cosroll;

    icoefxx = cosyaw * cosroll - sinyaw * sinpitch * sinroll;
    icoefxy = sinyaw * cospitch;
    icoefxz = cosyaw * sinroll + sinyaw * sinpitch * cosroll;

    icoefyx = -sinyaw * cosroll - cosyaw * sinpitch * sinroll;
    icoefyy = cosyaw * cospitch;
    icoefyz = cosyaw * sinpitch * cosroll - sinyaw * sinroll;

    icoefzx = -cospitch * sinroll;
    icoefzy = -sinpitch;
    icoefzz = cospitch * cosroll;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // post affine, pVarTP -> pVarTP
    if (pAmount == 0) return;

    double x = pAmount * scale_x * pVarTP.x;
    double y = pAmount * scale_y * pVarTP.y;
    double z = pAmount * scale_z * pVarTP.z;

    pVarTP.x = coefxx * x + coefxy * y + coefxz * z + move_x;
    pVarTP.y = coefyx * x + coefyy * y + coefyz * z + move_y;
    pVarTP.z = coefzx * x + coefzy * y + coefzz * z + move_z;

  }

  @Override
  public void invtransform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pre affine, inverted, pAffineTP -> pAffineTP
    if (pAmount == 0) return;

    double x = pAffineTP.x - move_x;
    double y = pAffineTP.y - move_y;
    double z = pAffineTP.z - move_z;

    pAffineTP.x = (icoefxx * x + icoefxy * y + icoefxz * z) / (pAmount * scale_x);
    pAffineTP.y = (icoefyx * x + icoefyy * y + icoefyz * z) / (pAmount * scale_y);
    pAffineTP.z = (icoefzx * x + icoefzy * y + icoefzz * z) / (pAmount * scale_z);

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{scale_x, scale_y, scale_z, yaw, pitch, roll, move_x, move_y, move_z};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE_X.equalsIgnoreCase(pName))
      scale_x = (pValue == 0) ? EPSILON : pValue;
    else if (PARAM_SCALE_Y.equalsIgnoreCase(pName))
      scale_y = (pValue == 0) ? EPSILON : pValue;
    else if (PARAM_SCALE_Z.equalsIgnoreCase(pName))
      scale_z = (pValue == 0) ? EPSILON : pValue;
    else if (PARAM_YAW.equalsIgnoreCase(pName))
      yaw = pValue;
    else if (PARAM_PITCH.equalsIgnoreCase(pName))
      pitch = pValue;
    else if (PARAM_ROLL.equalsIgnoreCase(pName))
      roll = pValue;
    else if (PARAM_MOVE_X.equalsIgnoreCase(pName))
      move_x = pValue;
    else if (PARAM_MOVE_Y.equalsIgnoreCase(pName))
      move_y = pValue;
    else if (PARAM_MOVE_Z.equalsIgnoreCase(pName))
      move_z = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "prepost_affine";
  }

  @Override
  public int getPriority() {
    return 2;
  }

}
