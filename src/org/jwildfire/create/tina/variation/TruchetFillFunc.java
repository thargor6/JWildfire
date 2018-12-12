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

public class TruchetFillFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_EXPONENT = "exponent";
  private static final String PARAM_ARC_WIDTH = "arc_width";
  private static final String PARAM_SEED = "seed";

  private static final String[] paramNames = {PARAM_EXPONENT, PARAM_ARC_WIDTH, PARAM_SEED};

  private double pexponent, exponent = 2.0;
  private double arc_width, width = 0.5;
  private double seed, seed2 = 0.0;
  private double onen = 0.5;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    exponent = pexponent > 2.0 ? 2.0 : pexponent < 0.001 ? 0.001 : pexponent;
    onen = 1 / exponent;
    width = arc_width > 1.0 ? 1.0 : arc_width < 0.001 ? 0.001 : arc_width;
    seed2 = sqrt(seed * 1.5) / (seed * 0.5) * 0.25;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* by Tatyana Zabanova, https://tatasz.deviantart.com/art/Apophysis-Plugin-Pack-1-647321921 transcribed be Rick Sidwell*/
    double rmax = 0.5 * (pow(2.0, onen) - 1.0) * width;
    double scale = 1.0 / pAmount;
    double modbase = 65535.0;
    double multiplier = 32747.0;//1103515245;
    double offset = 12345.0;

    double x = pAffineTP.x * scale;
    double y = pAffineTP.y * scale;
    double intx = round(x);
    double inty = round(y);

    double r = x - intx;
    x = r < 0.0 ? 1.0 + r : r;
    r = y - inty;
    y = r < 0.0 ? 1.0 + r : r;

    double tiletype;
    if (seed == 0)
      tiletype = 0.0;
    else if (seed == 1.0)
      tiletype = 1.0;
    else {
      double xrand = pAffineTP.x;
      double yrand = pAffineTP.y;
      xrand = round(fabs(xrand)) * seed2;
      yrand = round(fabs(yrand)) * seed2;
      double niter = xrand + yrand + xrand * yrand;
      double randint = (seed + niter) * seed2 * 0.5;
      randint = fmod((randint * multiplier + offset), modbase);
      tiletype = fmod(randint, 2.0);
    }

    double r0, r1;
    if (tiletype < 1.0) {  //Slow drawmode
      r0 = pow((pow(fabs(x), exponent) + pow(fabs(y), exponent)), onen);
      r1 = pow((pow(fabs(x - 1.0), exponent) + pow(fabs(y - 1.0), exponent)), onen);
    } else {
      r0 = pow((pow(fabs(x - 1.0), exponent) + pow(fabs(y), exponent)), onen);
      r1 = pow((pow(fabs(x), exponent) + pow(fabs(y - 1.0), exponent)), onen);
    }

    double x1, y1;
    double r00 = fabs(r0 - 0.5) / rmax;
    if (r00 < 1.0) {
      x1 = 2.0 * (x + floor(pAffineTP.x));
      y1 = 2.0 * (y + floor(pAffineTP.y));
    } else {
      x1 = 0.0;
      y1 = 0.0;
    }

    double r11 = fabs(r1 - 0.5) / rmax;
    if (r11 < 1.0) {
      pVarTP.x += x1 + 2.0 * (x + floor(pAffineTP.x)) - pAffineTP.x;
      pVarTP.y += y1 + 2.0 * (y + floor(pAffineTP.y)) - pAffineTP.y;
    } else {
      pVarTP.x += x1 - pAffineTP.x;
      pVarTP.y += y1 - pAffineTP.y;
    }

    if (pContext.isPreserveZCoordinate()) pVarTP.z += pAmount * pAffineTP.z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{pexponent, arc_width, seed};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_EXPONENT.equalsIgnoreCase(pName))
      pexponent = pValue;
    else if (PARAM_ARC_WIDTH.equalsIgnoreCase(pName))
      arc_width = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "truchet_fill";
  }

}
