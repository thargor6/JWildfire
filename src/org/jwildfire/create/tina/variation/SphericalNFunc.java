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

public class SphericalNFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_POWER = "power";
  private static final String PARAM_DIST = "dist";

  private static final String[] paramNames = {PARAM_POWER, PARAM_DIST};
  private static final String[] paramAltNames = {"SphericalN_Power", "SphericalN_Dist"};

  private double power = 3.0;
  private double dist = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // SphericalN by eralex61, http://eralex61.deviantart.com/art/SphericalN-plugin-166218657?q=gallery%3Aapophysis-plugins%2F24607713&qo=36
    //R=sqrt(sqr(pAffineTP.x)+sqr(pAffineTP.y));
    double R = pow(sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)), this.dist);
    int N = (int) floor(this.power * pContext.random());
    double alpha = atan2(pAffineTP.y, pAffineTP.x) + N * M_2PI / floor(this.power);
    double sina = sin(alpha);
    double cosa = cos(alpha);

    if (R > SMALL_EPSILON) {
      pVarTP.x += pAmount * cosa / R;
      pVarTP.y += pAmount * sina / R;
    }
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "sphericalN";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{power, dist};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return paramAltNames;
  }

}
