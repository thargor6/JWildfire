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

public class LazyJessFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_N = "n";
  private static final String PARAM_SPIN = "spin";
  private static final String PARAM_SPACE = "space";
  private static final String PARAM_CORNER = "corner";
  private static final String[] paramNames = {PARAM_N, PARAM_SPIN, PARAM_SPACE, PARAM_CORNER};

  private int n = 4;
  private double spin = M_PI;
  private double space = 0.0;
  private int corner = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* lazyjess by FarDareisMai, http://fardareismai.deviantart.com/art/Apophysis-Plugin-Lazyjess-268929293 */
    double theta, sina, cosa;
    double x = pAffineTP.x;
    double y = pAffineTP.y;
    double modulus = sqrt(x * x + y * y);

    // n==2 requires a special case
    if (n == 2) {
      if (fabs(x) < pAmount) {// If the input point falls inside the designated area...
        // ...then rotate it.
        theta = atan2(y, x) + spin;
        sina = sin(theta);
        cosa = cos(theta);
        x = pAmount * modulus * cosa;
        y = pAmount * modulus * sina;

        if (fabs(x) < pAmount) {
          pVarTP.x += x;
          pVarTP.y += y;
        } else {// If it is now part of a corner that falls outside the designated area...
          // ...then flip and rotate into place.
          theta = atan2(y, x) - spin + corner_rotation;
          sina = sin(theta);
          cosa = cos(theta);
          pVarTP.x += pAmount * modulus * cosa;
          pVarTP.y -= pAmount * modulus * sina;
        }
      } else {
        modulus = 1.0 + space / modulus;
        pVarTP.x += pAmount * modulus * x;
        pVarTP.y += pAmount * modulus * y;
      }
    } else {
      // Calculate the distance r from origin to the edge of the polygon at the angle of the input point.
      theta = atan2(y, x) + M_2PI;
      double theta_diff = fmod(theta + half_slice, pie_slice);
      double r = pAmount * M_SQRT2 * sin_vertex / sin(M_PI - theta_diff - vertex);

      if (modulus < r) {
        // Again, rotating points within designated area.
        theta = atan2(y, x) + spin + M_2PI;
        sina = sin(theta);
        cosa = cos(theta);
        x = pAmount * modulus * cosa;
        y = pAmount * modulus * sina;

        // Calculating r and modulus for our rotated point.
        theta_diff = fmod(theta + half_slice, pie_slice);
        r = pAmount * M_SQRT2 * sin_vertex / sin(M_PI - theta_diff - vertex);
        modulus = sqrt(x * x + y * y);

        if (modulus < r) {
          pVarTP.x += x;
          pVarTP.y += y;
        } else {
          // Again, flipping and rotating corners that fall outside the designated area.
          theta = atan2(y, x) - spin + corner_rotation + M_2PI;
          sina = sin(theta);
          cosa = cos(theta);
          pVarTP.x += pAmount * modulus * cosa;
          pVarTP.y -= pAmount * modulus * sina;
        }
      } else {
        modulus = 1.0 + space / modulus;
        pVarTP.x += pAmount * modulus * x;
        pVarTP.y += pAmount * modulus * y;
      }
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
    return new Object[]{n, spin, space, corner};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName))
      n = Tools.FTOI(pValue);
    else if (PARAM_SPIN.equalsIgnoreCase(pName))
      spin = pValue;
    else if (PARAM_SPACE.equalsIgnoreCase(pName))
      space = pValue;
    else if (PARAM_CORNER.equalsIgnoreCase(pName))
      corner = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "lazyjess";
  }

  private double vertex, sin_vertex, pie_slice, half_slice, corner_rotation;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    if (n < 2) {
      n = 2;
    }

    vertex = M_PI * (n - 2) / (2 * n);
    sin_vertex = sin(vertex);
    pie_slice = M_2PI / n;
    half_slice = pie_slice / 2;
    corner_rotation = (corner - 1) * pie_slice;
  }

}
