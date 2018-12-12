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

import static java.lang.Math.abs;
import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * Butterfly2, a variation based on the Butterfly curve, discovered ~1988 by Temple H. Fay
 * Implemented by CozyG, March 2015
 * For references, see http://en.wikipedia.org/wiki/Butterfly_curve_%28transcendental%29
 */
public class ButterflyFayFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_OFFSET = "offset";
  private static final String PARAM_UNIFIED_INNER_OUTER = "unified_inner_outer";
  private static final String PARAM_OUTER_MODE = "outer_mode";
  private static final String PARAM_INNER_MODE = "inner_mode";

  private static final String PARAM_OUTER_SPREAD = "outer_spread";
  private static final String PARAM_INNER_SPREAD = "inner_spread";
  private static final String PARAM_OUTER_SPREAD_RATIO = "outer_spread_ratio";
  private static final String PARAM_INNER_SPREAD_RATIO = "inner_spread_ratio";
  private static final String PARAM_SPREAD_SPLIT = "spread_split";
  private static final String PARAM_FILL = "fill";

  // my standard approach if there is a cycles variable is that if cycles is set to 0, 
  //     that means function should decide cycle value automatically
  //     for curves, function will make best effort to calculate minimum number of cycles needed 
  //     to close curve, or a somewhat arbitrary number if cannot 
  private static final String PARAM_CYCLES = "cycles";

  private static final String[] paramNames = {PARAM_OFFSET, PARAM_UNIFIED_INNER_OUTER, PARAM_OUTER_MODE, PARAM_INNER_MODE, PARAM_OUTER_SPREAD, PARAM_INNER_SPREAD, PARAM_OUTER_SPREAD_RATIO, PARAM_INNER_SPREAD_RATIO, PARAM_SPREAD_SPLIT, PARAM_CYCLES, PARAM_FILL};

  private double cycles = 0; // number of cycles (2*PI radians, circle circumference), if set to 0 then number of cycles is calculated automatically
  private double offset = 0; // offset c from equations
  private int unified_inner_outer = 1;
  private int outer_mode = 1;
  private int inner_mode = 1;
  private double outer_spread = 0; // deform based on original x/y
  private double inner_spread = 0; // deform based on original x/y
  private double outer_spread_ratio = 1; // how much outer_spread applies to x relative to y
  private double inner_spread_ratio = 1; // how much outer_spread applies to x relative to y
  private double spread_split = 1;
  private double fill = 0;

  // GAH 3/13/2015:
  // I don't have a mathematical proof yet, 
  // but observationally the butterfly curve is closed at 2*(PI)^3 radians (or cycles * (PI)^2 )
  // can't find this reported anywhere -- did I just make a discovery??
  // side note: 
  //      at 2*PI (1 cycle) appears to be closed, but really isn't, this is just a matter of resolution
  //      2nd cycle almost exactly follows path of 1rst cycle, then with start of 3rd cycle starts to diverge more obviously
  private double number_of_cycles; // 1 cycle = 2*PI

  private double cycle_length = 2 * M_PI; // 2(PI)
  private double radians_to_close = 2 * M_PI * M_PI * M_PI; // 2(PI)^3
  private double cycles_to_close = radians_to_close / cycle_length; // = PI^2

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // setting cyclesParam to 0 is meant to indicate the function should determine how many cycles 
    //     (actually setting cycles = 0 will just yield a single point)
    //  for the butterfly curve I am taking that to mean closing the curve, which as noted above 
    //      I have observationally determined is at exactly 2(PI)^3 radians, which is same as (PI)^2 cycles
    if (cycles == 0) {
      number_of_cycles = cycles_to_close;
    } else {
      number_of_cycles = cycles;
    }
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double theta = atan2(pAffineTP.y, pAffineTP.x); // atan2 range is [-PI, PI], so covers 2PI, or 1 cycle
    double t = number_of_cycles * theta;
    // r = e^cos(t) - 2cos(4t) - sin^5(t/12)
    // y = sin(t)*r
    // x = cos(t)*r
    double rin = spread_split * sqrt((pAffineTP.x * pAffineTP.x) + (pAffineTP.y * pAffineTP.y));
    double r = 0.5 * (exp(cos(t)) - (2 * cos(4 * t)) - pow(sin(t / 12), 5) + offset);
    // a "fully" parameterized version:
    // double r = 0.5 * ( (m1 * exp(m4 * cos(t + b1) + b4)) - (m2 * 2 * (cos(m5*4*t + b2) + b5)) - (m3 * pow(m6 * (sin(m7 * t/12 + b3) + b6), (m8 * 5 + b7))) + offset);

    if (fill != 0) {
      r = r + (fill * (pContext.random() - 0.5));
    }

    double x = r * sin(t);
    double y = -1 * r * cos(t); // adding -1 to flip butterfly to point up
    double xin, yin;
    double rinx, riny;

    if ((abs(rin) > abs(r)) || (unified_inner_outer == 1)) { // incoming point lies "outside" of curve OR ignoring inner_outer distinction
      switch (outer_mode) {
        case 0: // no spread
          pVarTP.x += pAmount * x;
          pVarTP.y += pAmount * y;
          break;
        case 1:
          rinx = (rin * outer_spread * outer_spread_ratio) - (outer_spread * outer_spread_ratio) + 1;
          riny = (rin * outer_spread) - outer_spread + 1;
          pVarTP.x += pAmount * rinx * x;
          pVarTP.y += pAmount * riny * y;
          break;
        case 2:
          xin = Math.abs(pAffineTP.x);
          yin = Math.abs(pAffineTP.y);
          if (x < 0) {
            xin = xin * -1;
          }
          if (y < 0) {
            yin = yin * -1;
          }
          pVarTP.x += pAmount * (x + (outer_spread * outer_spread_ratio * (xin - x)));
          pVarTP.y += pAmount * (y + (outer_spread * (yin - y)));
          break;
        case 3:
          xin = Math.abs(pAffineTP.x);
          yin = Math.abs(pAffineTP.y);
          if (x < 0) {
            xin = xin * -1;
          }
          if (y < 0) {
            yin = yin * -1;
          }
          pVarTP.x += pAmount * (x + (outer_spread * outer_spread_ratio * xin));
          pVarTP.y += pAmount * (y + (outer_spread * yin));
          break;
        case 4:
          rinx = (0.5 * rin) + (outer_spread * outer_spread_ratio);
          riny = (0.5 * rin) + outer_spread;
          pVarTP.x += pAmount * rinx * x;
          pVarTP.y += pAmount * riny * y;
          break;
        case 5: // original butterfly2 implementation (same as outer_mode 3, but without the sign modifications)
          pVarTP.x += pAmount * (x + (outer_spread * outer_spread_ratio * pAffineTP.x));
          pVarTP.y += pAmount * (y + (outer_spread * pAffineTP.y));
          break;
        default:
          pVarTP.x += pAmount * x;
          pVarTP.y += pAmount * y;
          break;
      }
    } else { // incoming point lies "inside" or "on" curve
      switch (inner_mode) {
        case 0: // no spread
          pVarTP.x += pAmount * x;
          pVarTP.y += pAmount * y;
          break;
        case 1:
          rinx = (rin * inner_spread * inner_spread_ratio) - (inner_spread * inner_spread_ratio) + 1;
          riny = (rin * inner_spread) - inner_spread + 1;
          pVarTP.x += pAmount * rinx * x;
          pVarTP.y += pAmount * riny * y;
          break;
        case 2:
          xin = Math.abs(pAffineTP.x);
          yin = Math.abs(pAffineTP.y);
          if (x < 0) {
            xin = xin * -1;
          }
          if (y < 0) {
            yin = yin * -1;
          }
          pVarTP.x += pAmount * (x - (inner_spread * inner_spread_ratio * (x - xin)));
          pVarTP.y += pAmount * (y - (inner_spread * (y - yin)));
          break;
        case 3:
          xin = Math.abs(pAffineTP.x);
          yin = Math.abs(pAffineTP.y);
          if (x < 0) {
            xin = xin * -1;
          }
          if (y < 0) {
            yin = yin * -1;
          }
          pVarTP.x += pAmount * (x - (inner_spread * inner_spread_ratio * xin));
          pVarTP.y += pAmount * (y - (inner_spread * yin));
          break;
        case 4:
          rinx = (0.5 * rin) + (inner_spread * inner_spread_ratio);
          riny = (0.5 * rin) + inner_spread;
          pVarTP.x += pAmount * rinx * x;
          pVarTP.y += pAmount * riny * y;
          break;
        case 5: // original butterfly2 implementation (same as inner_mode 3, but without the sign modifications)
          pVarTP.x += pAmount * (x + (inner_spread * inner_spread_ratio * pAffineTP.x));
          pVarTP.y += pAmount * (y + (inner_spread * pAffineTP.y));
          break;
        default:
          pVarTP.x += pAmount * x;
          pVarTP.y += pAmount * y;
          break;
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
    return new Object[]{offset, unified_inner_outer, outer_mode, inner_mode, outer_spread, inner_spread, outer_spread_ratio, inner_spread_ratio, spread_split, cycles, fill};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_OFFSET.equalsIgnoreCase(pName))
      offset = pValue;
    else if (PARAM_UNIFIED_INNER_OUTER.equalsIgnoreCase(pName)) {
      unified_inner_outer = (pValue == 0 ? 0 : 1);
    } else if (PARAM_OUTER_MODE.equalsIgnoreCase(pName)) {
      outer_mode = (int) floor(pValue);
      if (outer_mode > 5 || outer_mode < 0) {
        outer_mode = 0;
      }
    } else if (PARAM_INNER_MODE.equalsIgnoreCase(pName)) {
      inner_mode = (int) floor(pValue);
      if (inner_mode > 5 || inner_mode < 0) {
        inner_mode = 0;
      }
    } else if (PARAM_OUTER_SPREAD.equalsIgnoreCase(pName))
      outer_spread = pValue;
    else if (PARAM_INNER_SPREAD.equalsIgnoreCase(pName))
      inner_spread = pValue;
    else if (PARAM_OUTER_SPREAD_RATIO.equalsIgnoreCase(pName))
      outer_spread_ratio = pValue;
    else if (PARAM_INNER_SPREAD_RATIO.equalsIgnoreCase(pName))
      inner_spread_ratio = pValue;
    else if (PARAM_SPREAD_SPLIT.equalsIgnoreCase(pName))
      spread_split = pValue;
    else if (PARAM_CYCLES.equalsIgnoreCase(pName))
      cycles = pValue;
    else if (PARAM_FILL.equalsIgnoreCase(pName))
      fill = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "butterfly_fay";
  }

}
