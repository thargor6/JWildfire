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

public class LazyTravisFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SPIN_IN = "spin_in";
  private static final String PARAM_SPIN_OUT = "spin_out";
  private static final String PARAM_SPACE = "space";
  private static final String[] paramNames = {PARAM_SPIN_IN, PARAM_SPIN_OUT, PARAM_SPACE};

  private double spin_in = 1.0;
  private double spin_out = 0.5;
  private double space = M_PI / 2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* lazyTravis by Michael Faber, http://michaelfaber.deviantart.com/art/LazyTravis-270731558 */
    double x = fabs(pAffineTP.x);
    double y = fabs(pAffineTP.y);
    double s;
    double p;
    double x2, y2;

    if (x > pAmount || y > pAmount) {
      if (x > y) {
        s = x;
        if (pAffineTP.x > 0.0) {
          p = s + pAffineTP.y + s * _spin_out;
        } else {
          p = 5.0 * s - pAffineTP.y + s * _spin_out;
        }
      } else {
        s = y;
        if (pAffineTP.y > 0.0) {
          p = 3.0 * s - pAffineTP.x + s * _spin_out;
        } else {
          p = 7.0 * s + pAffineTP.x + s * _spin_out;
        }
      }

      p = fmod(p, s * 8.0);

      if (p <= 2.0 * s) {
        x2 = s + space;
        y2 = -(1.0 * s - p);
        y2 = y2 + y2 / s * space;
      } else if (p <= 4.0 * s) {

        y2 = s + space;
        x2 = (3.0 * s - p);
        x2 = x2 + x2 / s * space;
      } else if (p <= 6.0 * s) {
        x2 = -(s + space);
        y2 = (5.0 * s - p);
        y2 = y2 + y2 / s * space;
      } else {
        y2 = -(s + space);
        x2 = -(7.0 * s - p);
        x2 = x2 + x2 / s * space;
      }

      pVarTP.x += pAmount * x2;
      pVarTP.y += pAmount * y2;
    } else {
      if (x > y) {
        s = x;
        if (pAffineTP.x > 0.0) {
          p = s + pAffineTP.y + s * _spin_in;
        } else {
          p = 5.0 * s - pAffineTP.y + s * _spin_in;
        }
      } else {
        s = y;
        if (pAffineTP.y > 0.0) {
          p = 3.0 * s - pAffineTP.x + s * _spin_in;
        } else {
          p = 7.0 * s + pAffineTP.x + s * _spin_in;
        }
      }

      p = fmod(p, s * 8.0);

      if (p <= 2.0 * s) {
        pVarTP.x += pAmount * s;
        pVarTP.y -= pAmount * (s - p);
      } else if (p <= 4.0 * s) {
        pVarTP.x += pAmount * (3.0 * s - p);
        pVarTP.y += pAmount * s;
      } else if (p <= 6.0 * s) {
        pVarTP.x -= pAmount * s;
        pVarTP.y += pAmount * (5.0 * s - p);
      } else {
        pVarTP.x -= pAmount * (7.0 * s - p);
        pVarTP.y -= pAmount * s;
      }
    }
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
    return new Object[]{spin_in, spin_out, space};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SPIN_IN.equalsIgnoreCase(pName))
      spin_in = pValue;
    else if (PARAM_SPIN_OUT.equalsIgnoreCase(pName))
      spin_out = pValue;
    else if (PARAM_SPACE.equalsIgnoreCase(pName))
      space = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "lazyTravis";
  }

  private double _spin_in, _spin_out;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _spin_in = 4.0 * spin_in;
    _spin_out = 4.0 * spin_out;
  }

}
