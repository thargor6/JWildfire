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

public class HypertileFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_P = "p";
  private static final String PARAM_Q = "q";
  private static final String PARAM_N = "n";

  private static final String[] paramNames = {PARAM_P, PARAM_Q, PARAM_N};

  private int p = 3;
  private int q = 7;
  private int n = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* hypertile by Zueuk, http://zueuk.deviantart.com/art/Hyperbolic-tiling-plugins-165829025?q=sort%3Atime+gallery%3AZueuk&qo=0 */
    double a = pAffineTP.x + this.re;
    double b = pAffineTP.y - this.im;

    double c = this.re * pAffineTP.x - this.im * pAffineTP.y + 1.0;
    double d = this.re * pAffineTP.y + this.im * pAffineTP.x;

    double vr = pAmount / (sqr(c) + sqr(d));

    pVarTP.x += vr * (a * c + b * d);
    pVarTP.y += vr * (b * c - a * d);
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
    return new Object[]{p, q, n};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_P.equalsIgnoreCase(pName))
      p = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else if (PARAM_Q.equalsIgnoreCase(pName))
      q = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else if (PARAM_N.equalsIgnoreCase(pName))
      n = limitIntVal(Tools.FTOI(pValue), 0, Integer.MAX_VALUE);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hypertile";
  }

  private double re, im;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double pa = 2.0 * M_PI / (double) this.p;
    double qa = 2.0 * M_PI / (double) this.q;

    double r = (1.0 - cos(pa)) / (cos(pa) + cos(qa)) + 1.0;

    if (r > 0)
      r = 1.0 / sqrt(r);
    else
      r = 1.0;

    double a = this.n * pa;

    re = r * cos(a);
    im = r * sin(a);
  }

}
