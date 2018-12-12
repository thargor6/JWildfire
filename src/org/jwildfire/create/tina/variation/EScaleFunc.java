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

public class EScaleFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_ANGLE = "angle";

  private static final String[] paramNames = {PARAM_SCALE, PARAM_ANGLE};

  private double scale = 1.0;
  private double angle = 0.0;

  //Taking the square root of numbers close to zero is dangerous.  If x is negative
  //due to floating point errors we get NaN results.
  private double sqrt_safe(double x) {
    if (x <= 0.0)
      return 0.0;
    return sqrt(x);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // eScale by Michael Faber, http://michaelfaber.deviantart.com/art/eSeries-306044892
    double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
    double tmp2 = 2.0 * pAffineTP.x;
    double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
    if (xmax < 1.0)
      xmax = 1.0;
    double sinhmu, coshmu;

    double mu = acosh(xmax); //  mu > 0
    double t = pAffineTP.x / xmax;

    if (t > 1.0)
      t = 1.0;
    else if (t < -1.0)
      t = -1.0;

    double nu = acos(t); // -Pi < nu < Pi
    if (pAffineTP.y < 0.0)
      nu *= -1.0;

    mu *= scale;

    nu = fmod(fmod(scale * (nu + M_PI + angle), M_2PI * scale) - angle - scale * M_PI, M_2PI);

    if (nu > M_PI)
      nu -= M_2PI;
    if (nu < -M_PI)
      nu += M_2PI;

    sinhmu = sinh(mu);
    coshmu = cosh(mu);
    pVarTP.x += pAmount * coshmu * cos(nu);
    pVarTP.y += pAmount * sinhmu * sin(nu);
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
    return new Object[]{scale, angle};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "eScale";
  }

}
