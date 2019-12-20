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

import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.pow;

public class CSinFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_STRETCH = "stretch";

  private static final String[] paramNames = {PARAM_STRETCH};

  private double stretch= 1.0;

  private final Complex _c = new Complex();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // csin by 2010 Branden Brown, a.k.a. zephyrtronium

    _c.re = pAffineTP.x * stretch;
    _c.im = pAffineTP.y * stretch;

    _c.Sin();

    pVarTP.x += pAmount * _c.re;
    pVarTP.y += pAmount * _c.im;

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
    return new Object[]{stretch};
  }


  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_STRETCH.equalsIgnoreCase(pName))
      stretch = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "csin";
  }

}
