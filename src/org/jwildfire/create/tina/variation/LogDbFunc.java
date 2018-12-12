/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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

public class LogDbFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_BASE = "base";
  private static final String PARAM_FIX_PERIOD = "fix_period";

  private static final String[] paramNames = {PARAM_BASE, PARAM_FIX_PERIOD};

  private double base = 1.0;
  private double fix_period = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // logdb by DarkBeam 2014, http://jwildfire.org/forum/viewtopic.php?f=23&t=1450&p=2692#p2692
    double fix_atan_period = 0;
    int adp;
    int i;
    for (i = 0; i < 7; i++) {
      adp = pContext.random(10) - 5; // approximated binomial distr
      if (iabs(adp) >= 3)
        adp = 0; // 0 needs more chances
      fix_atan_period += (double) adp;
    }
    fix_atan_period *= _fixpe;
    pVarTP.x += _denom * log(pAffineTP.getPrecalcSumsq());
    pVarTP.y += pAmount * (pAffineTP.getPrecalcAtanYX() + fix_atan_period);
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
    return new Object[]{base, fix_period};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_BASE.equalsIgnoreCase(pName))
      base = pValue;
    else if (PARAM_FIX_PERIOD.equalsIgnoreCase(pName))
      fix_period = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "log_db";
  }

  private double _denom;
  private double _fixpe;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _denom = 0.5;
    if (base > 1e-20)
      _denom = _denom / log(2.71828182845905 * base);
    _denom *= pAmount;
    _fixpe = M_PI;
    if (fix_period > 1e-20)
      _fixpe *= fix_period;
  }

}