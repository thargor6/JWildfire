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

import static java.lang.Math.abs;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
  Butterfly2, a variation based on the Butterfly curve, discovered ~1988 by Temple H. Fay
  Implemented by CozyG, March 2015
  For references, see http://en.wikipedia.org/wiki/Butterfly_curve_%28transcendental%29
*/
public class ButterflyFayFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  /* removing extra parameters to simplify (at least for now)
  private static final String PARAM_M1 = "m1";
  private static final String PARAM_M2 = "m2";
  private static final String PARAM_M3 = "m3";
  private static final String PARAM_M4 = "m4";
  private static final String PARAM_M5 = "m5";
  private static final String PARAM_M6 = "m6";
  private static final String PARAM_M7 = "m7";
  private static final String PARAM_M8 = "m8";
  private static final String PARAM_B1 = "b1";
  private static final String PARAM_B2 = "b2";
  private static final String PARAM_B3 = "b3";
  private static final String PARAM_B4 = "b4";
  private static final String PARAM_B5 = "b5";
  private static final String PARAM_B6 = "b6";
  private static final String PARAM_B7 = "b7";
  */


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


  private static final String[] paramNames = { PARAM_OFFSET, PARAM_UNIFIED_INNER_OUTER, PARAM_OUTER_MODE, PARAM_INNER_MODE,
                                               PARAM_OUTER_SPREAD, PARAM_INNER_SPREAD,
                                               PARAM_OUTER_SPREAD_RATIO, PARAM_INNER_SPREAD_RATIO, PARAM_SPREAD_SPLIT,
                                               PARAM_CYCLES, PARAM_FILL }; 

  // PARAM_M1, PARAM_M2, PARAM_M3, PARAM_M4, PARAM_M5, PARAM_M6, PARAM_M7, PARAM_M8, 
  // PARAM_B1, PARAM_B2, PARAM_B3, PARAM_B4, PARAM_B5, PARAM_B6, PARAM_B7 };
  /* removing extra parameters to simplify (at least for now) 
  private double m1 = 1;
  private double m2 = 1;
  private double m3 = 1;
  private double m4 = 1;
  private double m5 = 1;
  private double m6 = 1;
  private double m7 = 1;
  private double m8 = 1;
  private double b1 = 0;
  private double b2 = 0;
  private double b3 = 0;
  private double b4 = 0;
  private double b5 = 0;
  private double b6 = 0;
  private double b7 = 0;
  */

  private double cyclesParam = 0;  // number of cycles (2*PI radians, circle circumference), if set to 0 then number of cycles is calculated automatically
  private double offset = 0;  // offset c from equations
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
  private double cycles;  // 1 cycle = 2*PI

  private double cycle_length = 2 * M_PI; // 2(PI)
  private double radians_to_close = 2 * M_PI * M_PI * M_PI;  // 2(PI)^3
  private double cycles_to_close = radians_to_close / cycle_length;  // = PI^2

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // setting cyclesParam to 0 is meant to indicate the function should determine how many cycles 
    //     (actually setting cycles = 0 will just yield a single point)
    //  for the butterfly curve I am taking that to mean closing the curve, which as noted above 
    //      I have observationally determined is at exactly 2(PI)^3 radians, which is same as (PI)^2 cycles
    if (cyclesParam == 0) {  
      cycles = cycles_to_close;
    }
    else {
      cycles = cyclesParam;
    }
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double theta = atan2(pAffineTP.y, pAffineTP.x);  // atan2 range is [-PI, PI], so covers 2PI, or 1 cycle
    double t = cycles * theta;
    // r = e^cos(t) - 2cos(4t) - sin^5(t/12)
    // y = sin(t)*r
    // x = cos(t)*r
    double rin = spread_split * sqrt((pAffineTP.x  * pAffineTP.x) + (pAffineTP.y * pAffineTP.y));
    double r = 0.5 * (exp(cos(t)) - (2 * cos(4*t)) - pow(sin(t/12), 5) + offset);
    
    // removing extra parameters to simplify (at least for now) 
    // "fully" parameterized version:
    // double r = 0.5 * ( (m1 * exp(m4 * cos(t + b1) + b4)) - (m2 * 2 * (cos(m5*4*t + b2) + b5)) - (m3 * pow(m6 * (sin(m7 * t/12 + b3) + b6), (m8 * 5 + b7))) + offset);

    if (fill != 0) { 
      r = r + (fill * (pContext.random() - 0.5));
    }

    double x = r * sin(t);
    double y = -1 * r * cos(t);   // adding -1 to flip butterfly to point up
    double xin, yin;
    double rinx, riny;

    if ((abs(rin) > abs(r)) || (unified_inner_outer == 1))  { // incoming point lies "outside" of curve OR ignoring inner_outer distinction
      switch(outer_mode) {
        case 0: // no spread
          pVarTP.x += pAmount * x;
          pVarTP.y += pAmount * y;
          break;
        case 1:
          rinx = (rin * outer_spread * outer_spread_ratio) - (outer_spread * outer_spread_ratio) + 1;
          riny = (rin * outer_spread) - outer_spread + 1;
          // rin = (rin * outer_spread) - outer_spread + 1;
          pVarTP.x += pAmount * rinx * x;
          pVarTP.y += pAmount * riny * y;
          // if (pVarTP.y == 0)  { pVarTP.x = 0; }
          break;
        case 2:
          xin = Math.abs(pAffineTP.x);
          yin = Math.abs(pAffineTP.y);
          if (x<0) { xin = xin * -1; }
          if (y<0) { yin = yin * -1; }
          //pVarTP.x += adjustedAmount * x + ((rin - r) * outer_spread * outer_spread_ratio);
          pVarTP.x += pAmount * (x + (outer_spread * outer_spread_ratio * (xin-x)));
          pVarTP.y += pAmount * (y + (outer_spread * (yin-y)));
          break;
        case 3:
          xin = Math.abs(pAffineTP.x);
          yin = Math.abs(pAffineTP.y);
          if (x<0) { xin = xin * -1; }
          if (y<0) { yin = yin * -1; }
          pVarTP.x += pAmount * (x + (outer_spread * outer_spread_ratio * xin));
          pVarTP.y += pAmount * (y + (outer_spread * yin));
          break;
        case 4:
          rinx = (0.5 * rin) + (outer_spread * outer_spread_ratio);
          riny = (0.5 * rin) + outer_spread;
          pVarTP.x += pAmount * rinx * x;
          pVarTP.y += pAmount * riny * y;
          break;
        case 7: // original butterfly2 implementation (same as outer_mode 3, but without the sign modifications)
          pVarTP.x += pAmount * (x + (outer_spread * outer_spread_ratio * pAffineTP.x));
          pVarTP.y += pAmount * (y + (outer_spread * pAffineTP.y));
          break;
        default:
          pVarTP.x += pAmount * x;
          pVarTP.y += pAmount * y;
          break;
      }
    }
    else  { // incoming point lies "inside" or "on" curve
      switch(inner_mode) {
        case 0: // no spread
          pVarTP.x += pAmount * x;
          pVarTP.y += pAmount * y;
          break;
        case 1:
          rinx = (rin * inner_spread * inner_spread_ratio) - (inner_spread * inner_spread_ratio) + 1;
          riny = (rin * inner_spread) - inner_spread + 1;
          // rin = (rin * inner_spread) - inner_spread + 1;
          pVarTP.x += pAmount * rinx * x;
          pVarTP.y += pAmount * riny * y;
          // if (pVarTP.y == 0)  { pVarTP.x = 0; }
          break;
        case 2:
          xin = Math.abs(pAffineTP.x);
          yin = Math.abs(pAffineTP.y);
          if (x<0) { xin = xin * -1; }
          if (y<0) { yin = yin * -1; }
          //pVarTP.x += adjustedAmount * x + ((rin - r) * inner_spread * inner_spread_ratio);
          pVarTP.x += pAmount * (x - (inner_spread * inner_spread_ratio * (x-xin)));
          pVarTP.y += pAmount * (y - (inner_spread * (y-yin)));
          break;
        case 3:
          xin = Math.abs(pAffineTP.x);
          yin = Math.abs(pAffineTP.y);
          if (x<0) { xin = xin * -1; }
          if (y<0) { yin = yin * -1; }
          pVarTP.x += pAmount * (x - (inner_spread * inner_spread_ratio * xin));
          pVarTP.y += pAmount * (y - (inner_spread * yin));
          break;
        case 4:
          rinx = (0.5 * rin) + (inner_spread * inner_spread_ratio);
          riny = (0.5 * rin) + inner_spread;
          pVarTP.x += pAmount * rinx * x;
          pVarTP.y += pAmount * riny * y;
          break;
        case 7: // original butterfly2 implementation (same as inner_mode 3, but without the sign modifications)
          pVarTP.x += pAmount * (x + (inner_spread * inner_spread_ratio * pAffineTP.x));
          pVarTP.y += pAmount * (y + (inner_spread * pAffineTP.y));
          break;
        default:
          pVarTP.x += pAmount * x;
          pVarTP.y += pAmount * y;
          break;
      }
    }
    //    pVarTP.x += pAmount * (x + (spread * spread_ratio * pAffineTP.x));
    //    pVarTP.y += pAmount * (y + (spread * pAffineTP.y));

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
    return new Object[] { offset, unified_inner_outer, outer_mode, inner_mode, outer_spread, inner_spread, outer_spread_ratio, inner_spread_ratio, spread_split, cyclesParam, fill };
                          //                     m1, m2, m3, m4, m5, m6, m7, m8, 
                          // b1, b2, b3, b4, b5, b6, b7 };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_OFFSET.equalsIgnoreCase(pName))
      offset = pValue;
    else if (PARAM_UNIFIED_INNER_OUTER.equalsIgnoreCase(pName)) {
      unified_inner_outer = (pValue == 0 ? 0 : 1);
    }
    else if (PARAM_OUTER_MODE.equalsIgnoreCase(pName)) {
      outer_mode = (int)floor(pValue);
      if (outer_mode > 7 || outer_mode < 0) { outer_mode = 0; }
    }
    else if (PARAM_INNER_MODE.equalsIgnoreCase(pName)) {
      inner_mode = (int)floor(pValue);
      if (inner_mode > 7 || inner_mode < 0) { inner_mode = 0; }
    }
    else if (PARAM_OUTER_SPREAD.equalsIgnoreCase(pName))
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
      cyclesParam = pValue;
    else if (PARAM_FILL.equalsIgnoreCase(pName))
      fill = pValue;
    
    /* removing extra parameters to simplify (at least for now) 
    else if (PARAM_M1.equalsIgnoreCase(pName))
      m1 = pValue;
    else if (PARAM_M2.equalsIgnoreCase(pName))
      m2 = pValue;
    else if (PARAM_M3.equalsIgnoreCase(pName))
      m3 = pValue;
    else if (PARAM_M4.equalsIgnoreCase(pName))
      m4 = pValue;
    else if (PARAM_M5.equalsIgnoreCase(pName))
      m5 = pValue;
    else if (PARAM_M6.equalsIgnoreCase(pName))
      m6 = pValue;
    else if (PARAM_M7.equalsIgnoreCase(pName))
      m7 = pValue;
    else if (PARAM_M8.equalsIgnoreCase(pName))
      m8 = pValue;

    else if (PARAM_B1.equalsIgnoreCase(pName))
      b1 = pValue;
    else if (PARAM_B2.equalsIgnoreCase(pName))
      b2 = pValue;
    else if (PARAM_B3.equalsIgnoreCase(pName))
      b3 = pValue;
    else if (PARAM_B4.equalsIgnoreCase(pName))
      b4 = pValue;
    else if (PARAM_B5.equalsIgnoreCase(pName))
      b5 = pValue;
    else if (PARAM_B6.equalsIgnoreCase(pName))
      b6 = pValue;
    else if (PARAM_B7.equalsIgnoreCase(pName))
      b7 = pValue;
    */
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "butterfly_fay";
  }

}
