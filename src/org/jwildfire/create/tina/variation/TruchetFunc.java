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

/*
 * Truchet Plugin by TyrantWave, see http://tyrantwave.deviantart.com/art/Truchet-Plugin-107982844
 */
package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.MathLib.SMALL_EPSILON;
import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.fabs;
import static org.jwildfire.base.MathLib.floor;
import static org.jwildfire.base.MathLib.fmod;
import static org.jwildfire.base.MathLib.pow;
import static org.jwildfire.base.MathLib.round;
import static org.jwildfire.base.MathLib.sin;
import static org.jwildfire.base.MathLib.sqrt;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class TruchetFunc extends VariationFunc {

  private static final String PARAM_EXTENDED = "extended";
  private static final String PARAM_EXPONENT = "exponent";
  private static final String PARAM_ARC_WIDTH = "arc_width";
  private static final String PARAM_ROTATION = "rotation";
  private static final String PARAM_SIZE = "size";
  private static final String PARAM_SEED = "seed";

  private static final String[] paramNames = { PARAM_EXTENDED, PARAM_EXPONENT, PARAM_ARC_WIDTH, PARAM_ROTATION, PARAM_SIZE, PARAM_SEED };

  private int extended = 0;
  private double exponent = 2.0;
  private double arc_width = 0.5;
  private double rotation = 0.0;
  private double size = 1.0;
  private double seed = 50.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //APO VARIABLES
    int extended = this.extended;
    double n = this.exponent;
    double onen = 1.0 / this.exponent;
    double tdeg = this.rotation;
    double width = this.arc_width;
    double size = this.size;
    double seed = fabs(this.seed);
    double seed2 = sqrt(seed + (seed / 2) + SMALL_EPSILON) / ((seed * 0.5) + SMALL_EPSILON) * 0.25;
    //VARIABLES   
    double x, y;
    int intx = 0;
    int inty = 0;
    double r = -tdeg;
    double r0 = 0.0;
    double r1 = 0.0;
    double rmax = 0.5 * (pow(2.0, 1.0 / n) - 1.0) * width;
    double scale = (cos(r) - sin(r)) / pAmount;
    double tiletype = 0.0;
    double randint = 0.0;
    double modbase = 65535.0;
    double multiplier = 32747.0;//1103515245;
    double offset = 12345.0;
    double niter = 0.0;
    int randiter = 0;
    //INITIALISATION   
    x = pAffineTP.x * scale;
    y = pAffineTP.y * scale;
    intx = (int) round(x);
    inty = (int) round(y);

    r = x - intx;
    if (r < 0.0) {
      x = 1.0 + r;
    }
    else {
      x = r;
    }

    r = y - inty;
    if (r < 0.0) {
      y = 1.0 + r;
    }
    else {
      y = r;
    }
    //CALCULATE THE TILE TYPE
    if (seed == 0.0) {
      tiletype = 0.0;
    }
    else if (seed == 1.0) {
      tiletype = 1.0;
    }
    else {
      if (extended == 0) {
        double xrand = round(pAffineTP.x);
        double yrand = round(pAffineTP.y);
        xrand = xrand * seed2;
        yrand = yrand * seed2;
        niter = xrand + yrand + xrand * yrand;
        randint = (niter + seed) * seed2 / 2.0;
        randint = fmod((randint * multiplier + offset), modbase);
      }
      else {
        seed = floor(seed);
        int xrand = (int) round(pAffineTP.x);
        int yrand = (int) round(pAffineTP.y);
        niter = fabs(xrand + yrand + xrand * yrand);
        randint = seed + niter;
        randiter = 0;
        while (randiter < niter) {
          randiter += 1;
          randint = fmod((randint * multiplier + offset), modbase);
        }
      }
      tiletype = fmod(randint, 2.0);//randint%2;
    }
    //DRAWING THE POINTS
    if (extended == 0) { //Fast drawmode
      if (tiletype < 1.0) {
        r0 = pow((pow(fabs(x), n) + pow(fabs(y), n)), onen);
        r1 = pow((pow(fabs(x - 1.0), n) + pow(fabs(y - 1.0), n)), onen);
      }
      else {
        r0 = pow((pow(fabs(x - 1.0), n) + pow(fabs(y), n)), onen);
        r1 = pow((pow(fabs(x), n) + pow(fabs(y - 1.0), n)), onen);
      }
    }
    else {
      if (tiletype == 1.0) { //Slow drawmode 
        r0 = pow((pow(fabs(x), n) + pow(fabs(y), n)), onen);
        r1 = pow((pow(fabs(x - 1.0), n) + pow(fabs(y - 1.0), n)), onen);
      }
      else {
        r0 = pow((pow(fabs(x - 1.0), n) + pow(fabs(y), n)), onen);
        r1 = pow((pow(fabs(x), n) + pow(fabs(y - 1.0), n)), onen);
      }
    }

    r = fabs(r0 - 0.5) / rmax;
    if (r < 1.0) {
      pVarTP.x += size * (x + floor(pAffineTP.x));
      pVarTP.y += size * (y + floor(pAffineTP.y));
    }

    r = fabs(r1 - 0.5) / rmax;
    if (r < 1.0) {
      pVarTP.x += size * (x + floor(pAffineTP.x));
      pVarTP.y += size * (y + floor(pAffineTP.y));
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { extended, exponent, arc_width, rotation, size, seed };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_EXTENDED.equalsIgnoreCase(pName))
      extended = Tools.FTOI(pValue);
    else if (PARAM_EXPONENT.equalsIgnoreCase(pName))
      exponent = pValue;
    else if (PARAM_ARC_WIDTH.equalsIgnoreCase(pName))
      arc_width = pValue;
    else if (PARAM_ROTATION.equalsIgnoreCase(pName))
      rotation = pValue;
    else if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "truchet";
  }

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm) {
    if (extended < 0) {
      extended = 0;
    }
    else if (extended > 1) {
      extended = 1;
    }
    if (exponent < 0.001) {
      exponent = 0.001;
    }
    else if (exponent > 2.0) {
      exponent = 2.0;
    }
    if (arc_width < 0.001) {
      arc_width = 0.001;
    }
    else if (arc_width > 1.0) {
      arc_width = 1.0;
    }
    if (size < 0.001) {
      size = 0.001;
    }
    else if (size > 10.0) {
      size = 10.0;
    }
  }
}
