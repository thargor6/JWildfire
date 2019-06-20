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


import static java.lang.Math.abs;

import static org.jwildfire.base.mathlib.MathLib.*;

public class HexModulusFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;


  private static final String PARAM_SIZE = "size";

	
  private static final String[] paramNames = {PARAM_SIZE};

  private double size = 1;


  private final static double M_SQRT3_2 = 0.86602540378443864676372317075249;
  private final static double M_SQRT3 = 1.7320508075688772935274463415059;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* hex_modulus from Tatyana Zabanova converted by Brad Stefanov https://www.deviantart.com/tatasz/art/hex-modulus-plugin-801956762 */

    double hsize= M_SQRT3_2 / size;
	double weight = pAmount / M_SQRT3_2;
    //get hex
    double X = pAffineTP.x * hsize;
    double Y = pAffineTP.y * hsize;
    double x = (0.57735026918962576450914878050196 * X - Y / 3.0); // sqrt(3) / 3
    double z = (2.0 * Y / 3.0);
    double y = -x - z;
    //round
    double rx = round(x);
    double ry = round(y);
    double rz = round(z);

    double x_diff = abs(rx - x);
    double y_diff = abs(ry - y);
    double z_diff = abs(rz - z);

    if ((x_diff > y_diff) & (x_diff > z_diff)){
        rx = -ry-rz;
    } else if (y_diff > z_diff){
        ry = -rx-rz;
    } else {
        rz = -rx-ry;
    }

    double FX_h = M_SQRT3 * rx + M_SQRT3_2 * rz;
    double FY_h = 1.5 * rz;

    double FX = X - FX_h;
    double FY = Y - FY_h;


    pVarTP.x += FX * weight;
    pVarTP.y += FY * weight;
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
    return new Object[]{size};
  }

  @Override
  public void setParameter(String pName, double pValue ) {
    if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hex_modulus";
  }

}
