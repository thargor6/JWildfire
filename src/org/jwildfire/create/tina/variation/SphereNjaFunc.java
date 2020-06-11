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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * @author Nicolaus Anderson
 */
public class SphereNjaFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_CIRCLE_A = "circle_a";
  private static final String PARAM_CIRCLE_B = "circle_b";
  private static final String PARAM_SHIFT_X = "shift_x";
  private static final String PARAM_SHIFT_Y = "shift_y";
  private static final String PARAM_SHIFT_Z = "shift_z";
  private static final String PARAM_STRETCH = "stretch";

  private static final String[] paramNames = {PARAM_CIRCLE_A, PARAM_CIRCLE_B, PARAM_SHIFT_X, PARAM_SHIFT_Y, PARAM_SHIFT_Z, PARAM_STRETCH};

  private double circle_a = 1.0;
  private double circle_b = 1.0;
  private double shift_x = 0.0;
  private double shift_y = 0.0;
  private double shift_z = 0.0;
  private double stretch = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Description:
    The transform creates a sphere by converting the x and y values to a radius,
    which becomes "t" for a parametric equation.
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

    /* Convert x and y of point to parametric space,
     noting they are the radius outward in real space. */
    double t = ini.getPrecalcSqrt() / stretch - M_PI / 2;

    // circular curve, expanded by pAmount
    double r = cos(t) * pAmount;// * ini.getPrecalcSqrt();
    double z = sin(t) * pAmount;// * ini.getPrecalcSqrt();

    // Convert parametric space (2D) back to real space (3D)
    /* A new coordinate equals the new factor times a unit vector in the
     direction of the coordinate of interest. */
    pVarTP.x += r * (ini.x / ini.getPrecalcSqrt());
    pVarTP.y += r * (ini.y / ini.getPrecalcSqrt());
    pVarTP.z += z;

    {
      // (this should cause positive z values to curl inside the sphere)

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
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{circle_a, circle_b, shift_x, shift_y, shift_z, stretch};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_CIRCLE_A.equalsIgnoreCase(pName))
      circle_a = pValue;
    else if (PARAM_CIRCLE_B.equalsIgnoreCase(pName))
      circle_b = pValue;
    else if (PARAM_SHIFT_X.equalsIgnoreCase(pName))
      shift_x = pValue;
    else if (PARAM_SHIFT_Y.equalsIgnoreCase(pName))
      shift_y = pValue;
    else if (PARAM_SHIFT_Z.equalsIgnoreCase(pName))
      shift_z = pValue;
    else if (PARAM_STRETCH.equalsIgnoreCase(pName))
      stretch = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "sphere_nja";
  }

}
