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

public class PowBlockFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_NUMERATOR = "numerator";
  private static final String PARAM_DENOMINATOR = "denominator";
  private static final String PARAM_ROOT = "root";
  private static final String PARAM_CORRECTN = "correctn";
  private static final String PARAM_CORRECTD = "correctd";

  private static final String[] paramNames = {PARAM_NUMERATOR, PARAM_DENOMINATOR, PARAM_ROOT, PARAM_CORRECTN, PARAM_CORRECTD};

  private double numerator = 2.0 + Math.random() * 5.0;
  private double denominator = 1.0 + Math.random() * 3.0;
  private double correctn = 1.0;
  private double correctd = 1.0;
  private double root = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pow_block optimized version, original by cothe coded by DarkBeam 2014
    double theta = pAffineTP.getPrecalcAtanYX();
    double r2 = pow(pAffineTP.getPrecalcSumsq(), _power) * pAmount;

    double ran = ((theta) * _deneps + (root
            * M_2PI * floor(pContext.random() * denominator) * _deneps)) * numerator;

    pVarTP.x += r2 * cos(ran);
    pVarTP.y += r2 * sin(ran);
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
    return new Object[]{numerator, denominator, root, correctn, correctd};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_NUMERATOR.equalsIgnoreCase(pName))
      numerator = pValue;
    else if (PARAM_DENOMINATOR.equalsIgnoreCase(pName))
      denominator = pValue;
    else if (PARAM_ROOT.equalsIgnoreCase(pName))
      root = pValue;
    else if (PARAM_CORRECTN.equalsIgnoreCase(pName))
      correctn = pValue;
    else if (PARAM_CORRECTD.equalsIgnoreCase(pName))
      correctd = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pow_block";
  }

  private double _power;
  private double _deneps;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _power = denominator * correctn * 1.0 / (fabs(correctd) + SMALL_EPSILON);
    if (fabs(_power) <= SMALL_EPSILON)
      _power = SMALL_EPSILON;
    _power = (numerator * 0.5) / _power;
    _deneps = denominator;
    if (fabs(_deneps) <= SMALL_EPSILON)
      _deneps = SMALL_EPSILON;
    _deneps = 1.0 / _deneps;
  }

}