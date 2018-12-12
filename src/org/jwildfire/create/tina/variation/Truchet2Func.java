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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Truchet2Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_EXPONENT1 = "exponent1";
  private static final String PARAM_EXPONENT2 = "exponent2";
  private static final String PARAM_WIDTH1 = "width1";
  private static final String PARAM_WIDTH2 = "width2";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_INVERSE = "inverse";

  private static final String[] paramNames = {PARAM_EXPONENT1, PARAM_EXPONENT2, PARAM_WIDTH1, PARAM_WIDTH2,
          PARAM_SCALE, PARAM_SEED, PARAM_INVERSE};
  // truchet2 by tatasz
  // https://www.deviantart.com/tatasz/art/Truchet2-plugin-730170455 converted to JWF by Brad Stefanov and Jesus Sosa

  private double exponent1 = 1.0;  // 2
  private double exponent2 = 2.0;  // 2
  private double width1 = 0.5;
  private double width2 = 0.5;
  private double scale = 10.0;
  private double seed = 50.0;
  private int inverse = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    // APO VARIABLES
    double xp = fabs((pAffineTP.x / scale - floor(pAffineTP.x / scale)) - 0.5) * 2.0;
    double width = width1 * (1 - xp) + xp * width2;
    width = (width < 1.0) ? width : 1.0;

    if (width <= 0.0) {
//			if (inverse >= 0.5) {  //JSI
      pVarTP.x += (pAffineTP.x) * pAmount;
      pVarTP.y += (pAffineTP.y) * pAmount;
//			}   //JSI
    } else {
      double xp2 = exponent1 * (1 - xp) + xp * exponent2;
      double n = xp2;
      n = (n < 2.0) ? n : 2.0;

      if (n <= 0) {
//				if (inverse >= 0.5) {
        pVarTP.x += (pAffineTP.x) * pAmount;
        pVarTP.y += (pAffineTP.y) * pAmount;
//				}
      } else {
        double onen = 1.0 / xp2;
        seed = fabs(seed);
        double seed2 = sqrt(seed + (seed / 2) + SMALL_EPSILON) / ((seed * 0.5) + SMALL_EPSILON) * 0.25;

        // VARIABLES
        double r0 = 0.0;
        double r1 = 0.0;

        // INITIALISATION
        double x = pAffineTP.x;
        double y = pAffineTP.y;
        double intx = round(x);
        double inty = round(y);

        double r = x - intx;
        if (r < 0.0)
          x = 1.0 + r;
        else
          x = r;

        r = y - inty;
        if (r < 0.0)
          y = 1.0 + r;
        else
          y = r;

        // CALCULATE THE TILE TYPE
        double tiletype = 0.0;
        if (seed == 0.0)
          tiletype = 0.0;
        else if (seed == 1.0)
          tiletype = 1.0;
        else {
          double xrand = round(pAffineTP.x);
          double yrand = round(pAffineTP.y);
          xrand = xrand * seed2;
          yrand = yrand * seed2;
          double niter = xrand + yrand + xrand * yrand;
          double randint = (niter + seed) * seed2 / 2.0;
          randint = fmod((randint * 32747.0 + 12345.0), 65535.0);
          tiletype = fmod(randint, 2.0);// randint%2;
        }

        // DRAWING THE POINTS
        if (tiletype < 1.0) {
          r0 = pow((pow(fabs(x), n) + pow(fabs(y), n)), onen);
          r1 = pow((pow(fabs(x - 1.0), n) + pow(fabs(y - 1.0), n)), onen);
        } else {
          r0 = pow((pow(fabs(x - 1.0), n) + pow(fabs(y), n)), onen);
          r1 = pow((pow(fabs(x), n) + pow(fabs(y - 1.0), n)), onen);
        }

        double rmax = 0.5 * (pow(2.0, onen) - 1.0) * width;
        double r00 = fabs(r0 - 0.5) / rmax;
        double r11 = fabs(r1 - 0.5) / rmax;

        //			if (inverse < 0.5) {  //JSI
        if (inverse == 0) {
          if (r00 < 1.0 || r11 < 1.0) {
//					if (r00 < 1.0) {
            pVarTP.x += (x + floor(pAffineTP.x)) * pAmount;
            pVarTP.y += (y + floor(pAffineTP.y)) * pAmount;
          } else // JSI
          {
//				pVarTP.x += pAffineTP.x * pAmount; //fill
//				pVarTP.y += pAffineTP.y * pAmount;  // fill
            pVarTP.x += 100.0;
            pVarTP.y += 100.0;
          }
//					if (r11 < 1.0) {
//						pVarTP.x += (x + floor(pAffineTP.x)) * pAmount;
//						pVarTP.y += (y + floor(pAffineTP.y)) * pAmount;
//					} 
//					else //JSI
//					{
//						pVarTP.x += 0.0;
//						pVarTP.y += 0.0;
//					}
        } else {
          if (r00 > 1.0 && r11 > 1.0) {
            pVarTP.x += (x + floor(pAffineTP.x)) * pAmount;
            pVarTP.y += (y + floor(pAffineTP.y)) * pAmount;
          } else //JSI
          {
            pVarTP.x += 10000.0;
            pVarTP.y += 10000.0;
          }
        }
      }
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{exponent1, exponent2, width1, width2, scale, seed, inverse};
  }

  @Override
  public void setParameter(String pName, double pValue) {

    if (PARAM_EXPONENT1.equalsIgnoreCase(pName))
      exponent1 = limitVal(pValue, -1.0, 3.0);
    else if (PARAM_EXPONENT2.equalsIgnoreCase(pName))
      exponent2 = limitVal(pValue, -1.0, 3.0);
    else if (PARAM_WIDTH1.equalsIgnoreCase(pName))
      width1 = limitVal(pValue, -1.0, 2.0);
    else if (PARAM_WIDTH2.equalsIgnoreCase(pName))
      width2 = limitVal(pValue, -1.0, 2.0);
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = pValue;
    else if (PARAM_INVERSE.equalsIgnoreCase(pName))
      inverse = (int) limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "truchet2";
  }

}
