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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PostAxisSymmetryWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final int AXIS_X = 0;
  private static final int AXIS_Y = 1;
  private static final int AXIS_Z = 2;

  private static final String PARAM_AXIS = "axis";
  private static final String PARAM_CENTRE_X = "centre_x";
  private static final String PARAM_CENTRE_Y = "centre_y";
  private static final String PARAM_CENTRE_Z = "centre_z";
  private static final String PARAM_ROTATION = "rotation";
  private static final String PARAM_X1colorshift = "x1colorshift";
  private static final String PARAM_Y1colorshift = "y1colorshift";
  private static final String PARAM_Z1colorshift = "z1colorshift";
  private static final String PARAM_X2colorshift = "x2colorshift";
  private static final String PARAM_Y2colorshift = "y2colorshift";
  private static final String PARAM_Z2colorshift = "z2colorshift";
  private static final String[] paramNames = {PARAM_AXIS, PARAM_CENTRE_X, PARAM_CENTRE_Y, PARAM_CENTRE_Z, PARAM_ROTATION, PARAM_X1colorshift, PARAM_Y1colorshift, PARAM_Z1colorshift, PARAM_X2colorshift, PARAM_Y2colorshift, PARAM_Z2colorshift};

  private int axis = AXIS_X;
  private double centre_x = 0.25;
  private double centre_y = 0.5;
  private double centre_z = 0.5;
  private double rotation = 30.0;
  private double x1colorshift = 0.0;
  private double y1colorshift = 0.0;
  private double z1colorshift = 0.0;
  private double x2colorshift = 0.0;
  private double y2colorshift = 0.0;
  private double z2colorshift = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    switch (axis) {
      case AXIS_X: {
        double dx, dy;
        dx = pVarTP.x - centre_x;
        if (Math.random() < 0.5) {
          double ax = centre_x + dx + _halve_dist;
          double ay = pVarTP.y;
          if (_doRotate) {
            dx = ax - centre_x;
            dy = ay - centre_y;
            ax = centre_x + dx * _cosa + dy * _sina;
            ay = centre_y + dy * _cosa - dx * _sina;
          }
          pVarTP.x = ax;
          pVarTP.y = ay;
          pVarTP.color = fmod(pVarTP.color + x1colorshift, 1.0);
        } else {
          double bx = centre_x - dx - _halve_dist;
          double by = pVarTP.y;
          if (_doRotate) {
            dx = bx - centre_x;
            dy = by - centre_y;
            bx = centre_x + dx * _cosa - dy * _sina;
            by = centre_y + dy * _cosa + dx * _sina;
          }
          pVarTP.x = bx;
          pVarTP.y = by;
          pVarTP.color = fmod(pVarTP.color + x2colorshift, 1.0);
        }
      }
      break;
      case AXIS_Y: {
        double dx, dy;
        dy = pVarTP.y - centre_y;
        if (Math.random() < 0.5) {
          double ax = pVarTP.x;
          double ay = centre_y + dy + _halve_dist;
          if (_doRotate) {
            dx = ax - centre_x;
            dy = ay - centre_y;
            ax = centre_x + dx * _cosa + dy * _sina;
            ay = centre_y + dy * _cosa - dx * _sina;
          }
          pVarTP.x = ax;
          pVarTP.y = ay;
          pVarTP.color = fmod(pVarTP.color + y1colorshift, 1.0);
        } else {
          double bx = pVarTP.x;
          double by = centre_y - dy - _halve_dist;
          if (_doRotate) {
            dx = bx - centre_x;
            dy = by - centre_y;
            bx = centre_x + dx * _cosa - dy * _sina;
            by = centre_y + dy * _cosa + dx * _sina;
            pVarTP.color = fmod(pVarTP.color + y2colorshift, 1.0);
          }
          pVarTP.x = bx;
          pVarTP.y = by;
        }
      }
      break;
      case AXIS_Z:
      default: {
        double dx, dz;
        dz = pVarTP.z - centre_z;
        if (Math.random() < 0.5) {
          double ax = pVarTP.x;
          double az = centre_z + dz + _halve_dist;
          if (_doRotate) {
            dx = ax - centre_x;
            dz = az - centre_z;
            ax = centre_x + dx * _cosa + dz * _sina;
            az = centre_y + dz * _cosa - dx * _sina;
          }
          pVarTP.x = ax;
          pVarTP.z = az;
          pVarTP.color = fmod(pVarTP.color + z1colorshift, 1.0);
        } else {
          double bx = pVarTP.x;
          double bz = centre_z - dz - _halve_dist;
          if (_doRotate) {
            dx = bx - centre_x;
            dz = bz - centre_z;
            bx = centre_x + dx * _cosa - dz * _sina;
            bz = centre_y + dz * _cosa + dx * _sina;
          }
          pVarTP.x = bx;
          pVarTP.z = bz;
          pVarTP.color = fmod(pVarTP.color + z2colorshift, 1.0);
        }
      }
      break;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{axis, centre_x, centre_y, centre_z, rotation, x1colorshift, y1colorshift, z1colorshift, x2colorshift, y2colorshift, z2colorshift};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CENTRE_X.equalsIgnoreCase(pName))
      centre_x = pValue;
    else if (PARAM_CENTRE_Y.equalsIgnoreCase(pName))
      centre_y = pValue;
    else if (PARAM_CENTRE_Z.equalsIgnoreCase(pName))
      centre_z = pValue;
    else if (PARAM_ROTATION.equalsIgnoreCase(pName))
      rotation = pValue;
    else if (PARAM_AXIS.equalsIgnoreCase(pName))
      axis = limitIntVal(Tools.FTOI(pValue), AXIS_X, AXIS_Z);
    else if (PARAM_X1colorshift.equalsIgnoreCase(pName))
      x1colorshift = pValue;
    else if (PARAM_Y1colorshift.equalsIgnoreCase(pName))
      y1colorshift = pValue;
    else if (PARAM_Z1colorshift.equalsIgnoreCase(pName))
      z1colorshift = pValue;
    else if (PARAM_X2colorshift.equalsIgnoreCase(pName))
      x2colorshift = pValue;
    else if (PARAM_Y2colorshift.equalsIgnoreCase(pName))
      y2colorshift = pValue;
    else if (PARAM_Z2colorshift.equalsIgnoreCase(pName))
      z2colorshift = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "post_axis_symmetry_wf";
  }

  @Override
  public int getPriority() {
    return 1;
  }

  private double _sina, _cosa, _halve_dist;
  private boolean _doRotate;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double a = rotation * M_2PI / 180.0 / 2.0;
    _doRotate = fabs(a) > EPSILON;

    _sina = sin(a);
    _cosa = cos(a);
    _halve_dist = pAmount / 2.0;
  }

}
