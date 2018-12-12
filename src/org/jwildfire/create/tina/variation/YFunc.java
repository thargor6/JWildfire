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

public class YFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_HYPERGON = "hypergon";
  private static final String PARAM_HYPERGON_N = "hypergon_n";
  private static final String PARAM_HYPERGON_R = "hypergon_r";
  private static final String PARAM_STAR = "star";
  private static final String PARAM_STAR_N = "star_n";
  private static final String PARAM_STAR_SLOPE = "star_slope";
  private static final String PARAM_LITUUS = "lituus";
  private static final String PARAM_LITUUS_A = "lituus_a";
  private static final String PARAM_SUPER = "super";
  private static final String PARAM_SUPER_M = "super_m";
  private static final String PARAM_SUPER_N1 = "super_n1";
  private static final String PARAM_SUPER_N2 = "super_n2";
  private static final String PARAM_SUPER_N3 = "super_n3";

  private static final String[] paramNames = {PARAM_HYPERGON, PARAM_HYPERGON_N, PARAM_HYPERGON_R, PARAM_STAR, PARAM_STAR_N, PARAM_STAR_SLOPE, PARAM_LITUUS, PARAM_LITUUS_A, PARAM_SUPER, PARAM_SUPER_M, PARAM_SUPER_N1, PARAM_SUPER_N2, PARAM_SUPER_N3};

  private double hypergon = Math.random();
  private int hypergon_n = 4;
  private double hypergon_r = 1.0;
  private double star = Math.random();
  private int star_n = 5;
  private double star_slope = 2.0;
  private double lituus = Math.random() * 0.25;
  private double lituus_a = 1.0;
  private double _super = 0.0;
  private double super_m = 1.0;
  private double super_n1 = 1.0;
  private double super_n2 = 1.0;
  private double super_n3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* y by Michael Faber,  http://michaelfaber.deviantart.com/art/The-Lost-Variations-258913970 */
    double a = atan2(pAffineTP.y, pAffineTP.x);
    double r;
    double s, c;
    double total = 0.0;
    double temp1, temp2;

    if (this.hypergon != 0.0) {
      temp1 = fmod(fabs(a), M_2PI / this._hypergon_n) - M_PI / this._hypergon_n;
      temp2 = sqr(tan(temp1)) + 1.0;
      if (temp2 >= sqr(this._hypergon_d)) {
        total += this.hypergon;
      } else {
        total += this.hypergon * (this._hypergon_d - sqrt(sqr(this._hypergon_d) - temp2)) / sqrt(temp2);
      }
    }
    if (this.star != 0.0) {
      temp1 = tan(fabs(fmod(fabs(a), M_2PI / this._star_n) - M_PI / this._star_n));
      total += this.star * sqrt(sqr(this._star_slope) * (1.0 + sqr(temp1)) / sqr(temp1 + this._star_slope));
    }
    if (this.lituus != 0.0) {
      total += this.lituus * pow(a / M_PI + 1.0, this._lituus_a);
    }
    if (this._super != 0.0) {
      double ang = a * this._super_m;
      s = sin(ang);
      c = cos(ang);
      total += this._super * pow(pow(fabs(c), this.super_n2) + pow(fabs(s), this.super_n3), this._super_n1);
    }

    r = pAmount * sqr(total) / sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));

    s = sin(a);
    c = cos(a);

    pVarTP.x += r * c;
    pVarTP.y += r * s;
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
    return new Object[]{hypergon, hypergon_n, hypergon_r, star, star_n, star_slope, lituus, lituus_a, _super, super_m, super_n1, super_n2, super_n3};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_HYPERGON.equalsIgnoreCase(pName))
      hypergon = pValue;
    else if (PARAM_HYPERGON_N.equalsIgnoreCase(pName))
      hypergon_n = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else if (PARAM_HYPERGON_R.equalsIgnoreCase(pName))
      hypergon_r = pValue;
    else if (PARAM_STAR.equalsIgnoreCase(pName))
      star = pValue;
    else if (PARAM_STAR_N.equalsIgnoreCase(pName))
      star_n = limitIntVal(Tools.FTOI(pValue), 3, Integer.MAX_VALUE);
    else if (PARAM_STAR_SLOPE.equalsIgnoreCase(pName))
      star_slope = pValue;
    else if (PARAM_LITUUS.equalsIgnoreCase(pName))
      lituus = pValue;
    else if (PARAM_LITUUS_A.equalsIgnoreCase(pName))
      lituus_a = pValue;
    else if (PARAM_SUPER.equalsIgnoreCase(pName))
      _super = pValue;
    else if (PARAM_SUPER_M.equalsIgnoreCase(pName))
      super_m = pValue;
    else if (PARAM_SUPER_N1.equalsIgnoreCase(pName))
      super_n1 = pValue;
    else if (PARAM_SUPER_N2.equalsIgnoreCase(pName))
      super_n2 = pValue;
    else if (PARAM_SUPER_N3.equalsIgnoreCase(pName))
      super_n3 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "y";
  }

  private double _hypergon_d, _hypergon_n, _lituus_a, _star_n, _star_slope, _super_m, _super_n1;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _hypergon_d = sqrt(1.0 + sqr(hypergon_r));
    _hypergon_n = (double) hypergon_n;

    _lituus_a = -lituus_a;

    _star_n = (double) star_n;
    _star_slope = tan(star_slope);

    _super_m = super_m / 4.0;
    _super_n1 = -1.0 / (super_n1 + SMALL_EPSILON);

  }

}
