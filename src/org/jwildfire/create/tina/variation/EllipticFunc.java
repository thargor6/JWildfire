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

import org.jwildfire.base.Tools;

public class EllipticFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_MODE = "mode";
  private static final String[] paramNames = {PARAM_MODE};
  
  private int mode = MODE_MIRRORY;
  
  // Modes
  private static final int MODE_ORIGINAL = 0; // Original Apophysis plugin
  private static final int MODE_MIRRORY = 1; // Mirror y result; legacy JWildfire behavior
  private static final int MODE_PRECISION = 2; // Alternate calculation to avoid precision loss by Claude Heiland-Allen; see https://mathr.co.uk/blog/2017-11-01_a_more_accurate_elliptic_variation.html

  private double sqrt_safe(double x) {
    return (x < SMALL_EPSILON) ? 0.0 : sqrt(x);
  }

  private double sqrt1pm1(double x) {
    if (-0.0625 < x && x < 0.0625)
    {
      // [4,4] Pade approximant to degree 8 truncated Taylor series of sqrt(x+1)-1 about 0
      // computed with a Wolfram Alpha Open Code notebook
      // accurate to machine precision within this range?
      double num = 0;
      double den = 0;
      num += 1.0 / 32.0;
      den += 1.0 / 256.0;
      num *= x;
      den *= x;
      num += 5.0 / 16.0;
      den += 5.0 / 32.0;
      num *= x;
      den *= x;
      num += 3.0 / 4.0;
      den += 15.0 / 16.0;
      num *= x;
      den *= x;
      num += 1.0 / 2.0;
      den += 7.0 / 4.0;
      num *= x;
      den *= x;
      den += 1.0;
      return num / den;
    }
    return sqrt(1 + x) - 1;
  }

 @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (mode == MODE_PRECISION) {
      double sq = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x;
      double x2 = 2.0 * pAffineTP.x;
      double xmaxm1 = 0.5 * (sqrt1pm1(sq + x2) + sqrt1pm1(sq - x2));
      double ssx = (xmaxm1 < 0) ? 0 : sqrt(xmaxm1);
      double a = pAffineTP.x / (1 + xmaxm1);
      
      int sign = (pAffineTP.y > 0) ? 1 : -1;
      pVarTP.x += _v * asin(max(-1, min(1, a)));  //asin(clamp(a, -1, 1))
      pVarTP.y += sign * _v * Math.log1p(xmaxm1 + ssx);      
    } else {  // MODE_ORIGINAL or MODE_MIRRORY
      double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
      double x2 = 2.0 * pAffineTP.x;
      double xmax = 0.5 * (sqrt(tmp + x2) + sqrt(tmp - x2));
  
      double a = pAffineTP.x / xmax;
      double b = sqrt_safe(1.0 - a * a);
      
      int sign = (pAffineTP.y > 0) ? 1 : -1;
      if (mode == MODE_MIRRORY)
        sign = (pContext.random() < 0.5) ? 1 : -1;
  
      pVarTP.x += _v * atan2(a, b);
      pVarTP.y += sign * _v * log(xmax + sqrt_safe(xmax - 1.0));
    }
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "elliptic";
  }

  private double _v;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _v = pAmount * M_2_PI;
  }
  
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{mode};
  }

  @Override
  public void setParameter(String pName, double pValue) {

    if (PARAM_MODE.equalsIgnoreCase(pName)) {
       mode = Tools.limitValue(Tools.FTOI(pValue), 0, 2);
    } else
      throw new IllegalArgumentException(pName);
  }

}
