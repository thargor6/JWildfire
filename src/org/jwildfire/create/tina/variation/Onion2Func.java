/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

  Variation created by Nicolaus Anderson, 2013
  Under the same license.
  
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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * @author Nicolaus Anderson
 */
public class Onion2Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PN_MEETING_PT = "meeting_pt";
  private static final String PN_CIRCLE_A = "circle_a";
  private static final String PN_CIRCLE_B = "circle_b";
  private static final String PN_SHIFT_X = "shift_x";
  private static final String PN_SHIFT_Y = "shift_y";
  private static final String PN_SHIFT_Z = "shift_z";
  private static final String PN_TOP_CROP = "top_crop";
  private static final String PN_STRETCH = "stretch";

  private static final String[] paramNames = {PN_MEETING_PT, PN_CIRCLE_A, PN_CIRCLE_B, PN_SHIFT_X, PN_SHIFT_Y, PN_SHIFT_Z, PN_TOP_CROP, PN_STRETCH};

  private double meeting_pt = 0.5;
  private double circle_a = 1.0;
  private double circle_b = 1.0;
  private double shift_x = 0.0;
  private double shift_y = 0.0;
  private double shift_z = 0.0;
  private double top_crop = 0.0;
  private double stretch = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Description:
     The transform creates a shape similar to an onion by starting with a circle
     and smoothly transitioning to a (negative) exponential function. In this case,
     they meet where exp(x) has a slope of 1.
     To make the function spherical, the point's x and y values are treated as a
     radius and become "t". The circle and exponential function are then formed
     from parametric equations, where "t" is the same "t" from the original point's
     x and y values.
     These equations are as follows:
     circle: xp=cos(t), yp=sin(t) (or r=cos(t), z=sin(t))
     exponential: xp=cos(t), yp=exp(s) (or r=cos(t), z=exp(s))
     where "s" represents a shifted "t" value so as to aline the exponential
     function with the circle.
     
     The slope of the circle:
     dyp/dxp = cos(t)dt/-sin(t)dt = -cot(t)
     Thus, cot(t) becomes the coefficient on exp(x) so as to make the slopes the
     same. However, this requires a y-axis realignment of cot(t) instead of 1
     in addition to a shift in the y-axis to bring exp(t) to meet with the circle.
     y-axis-shift = sin(t) - cot(t)
     
     This value of "t" - where the circle and exponential function meet - is
     called "m", and it is a user-set variable.
     Thus, the shifts for exp(s) are:
     x-axis shift = cos(m)
     y-axis shift = sin(m) - cot(m)
     
     Final exponential equation:
     xp = cos(t)
     yp = cot(t)*exp( -xp + cos(m) ) + sin(m) - cot(m)

     NOTE: xp is made negative instead of cot(t) so as to perform the desired
     mirror in parametric space.
    */

    // Initial coordinates
    XYZPoint ini = new XYZPoint();
    ini.x = pAffineTP.x;
    ini.y = pAffineTP.y;
    ini.z = pAffineTP.z;
    ini.x -= shift_x;
    ini.y -= shift_y;
    ini.z -= shift_z;
    ini.invalidate();

    // Final coordinates in parametric space
    double r;
    double z;

    /* Convert x and y of point to parametric space,
     noting they are the radius outward in real space. */
    double t = ini.getPrecalcSqrt() / stretch - M_PI / 2;

    /* NOTE: "t" and "meetingPt" are in radians */

    if (t > meeting_pt) {
      // exponential curve
      r = cos(t);
      if (tan(meeting_pt) != 0.0) {
        z = exp(cos(meeting_pt) - r) / tan(meeting_pt)
                + sin(meeting_pt)
                - 1 / tan(meeting_pt);

        if (z > top_crop && top_crop > 0) { /* FIX ADDED. top_crop could start at -1 for cropping below middle. */
          z = top_crop;
          r = 0;
        }
      } else {
        z = top_crop;
      }
    } else {
      // circular curve
      r = cos(t);
      z = sin(t);
    }
    // Expand radius of onion
    r *= circle_a * pAmount;
    z *= circle_b * pAmount;

    // Convert parametric space (2D) back to real space (3D)
    /* A new coordinate equals the new factor times a unit vector in the
     direction of the coordinate of interest. */
    pVarTP.x += r * (ini.x / ini.getPrecalcSqrt());
    pVarTP.y += r * (ini.y / ini.getPrecalcSqrt());
    pVarTP.z += z;

    {
      // (this should cause positive z values to curl inside the onion)

      /* Treating z as a vector that is split into two components, one being
       an x-component of parametric space which becomes a new radius for
       real space, taking into account conversion through unit vectors */
      pVarTP.x += r * ini.z * (ini.x / ini.getPrecalcSqrt());
      pVarTP.y += r * ini.z * (ini.y / ini.getPrecalcSqrt());

      /* The z-component is a normalized value from the parametric space y
       (which is real space z), taking into account the amount that went into
       the x component. */
      pVarTP.z += ini.z * z / (r * r + z * z);
    }

    pVarTP.x += shift_x;
    pVarTP.y += shift_y;
    pVarTP.z += shift_z;
  }

  @Override
  public String getName() {
    return "onion2";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{
            meeting_pt,
            circle_a,
            circle_b,
            shift_x,
            shift_y,
            shift_z,
            top_crop,
            stretch
    };
  }

  @Override
  public void setParameter(String pName, double pValue) {

    if (PN_MEETING_PT.equalsIgnoreCase(pName)) {
      meeting_pt = pValue;
    } else if (PN_CIRCLE_A.equalsIgnoreCase(pName)) {
      circle_a = pValue;
    } else if (PN_CIRCLE_B.equalsIgnoreCase(pName)) {
      circle_b = pValue;
    } else if (PN_SHIFT_X.equalsIgnoreCase(pName)) {
      shift_x = pValue;
    } else if (PN_SHIFT_Y.equalsIgnoreCase(pName)) {
      shift_y = pValue;
    } else if (PN_SHIFT_Z.equalsIgnoreCase(pName)) {
      shift_z = pValue;
    } else if (PN_TOP_CROP.equalsIgnoreCase(pName)) {
      top_crop = pValue;
    } else if (PN_STRETCH.equalsIgnoreCase(pName)) {
      if (pValue > 0.0)
        stretch = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }
}
