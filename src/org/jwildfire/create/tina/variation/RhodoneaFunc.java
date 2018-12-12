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

import java.math.BigInteger;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * Rhodonea curves (also known as rose curves)
 * Implemented by CozyG, March 2015
 * For references, see http://en.wikipedia.org/wiki/Rose_%28mathematics%29
 * There are other JWildfire variations (RoseWF, PRose3D), and an Apophysis rose plugin (http://fardareismai.deviantart.com/art/Apophysis-Plugin-Rose-246324281)
 * that implement the class of rose curves where k is an integer and cycles = 1,
 * but Rhodonea implements a fuller range of possibilities (at least within the 2D plane)
 * <p>
 * From reference literature:
 * Rhodonea curves were studied and named by the Italian mathematician Guido Grandi between the year 1723 and 1728
 * These curves can all be expressed by a polar equation of the form
 * r = cos(k * theta)
 * <p>
 * or, alternatively, as a pair of Cartesian parametric equations of the form
 * x = cos(kt)cos(t)
 * y = cos(kt)sin(t)
 * <p>
 * If k is an integer, the curve will be rose-shaped with:
 * 2k petals if k is even, and
 * k petals if k is odd.
 * <p>
 * When k is even, the entire graph of the rose will be traced out exactly once when the value of θ changes from 0 to 2π.
 * When k is odd, this will happen on the interval between 0 and π.
 * (More generally, this will happen on any interval of length 2π for k even, and π for k odd.)
 * <p>
 * If k is a half-integer (e.g. 1/2, 3/2, 5/2), the curve will be rose-shaped with 4k petals.
 * <p>
 * If k can be expressed as n±1/6, where n is a nonzero integer, the curve will be rose-shaped with 12k petals.
 * <p>
 * If k can be expressed as n/3, where n is an integer not divisible by 3,
 * the curve will be rose-shaped with n petals if n is odd and 2n petals if n is even.
 * <p>
 * If k is rational, then the curve is closed and has finite length.
 * If k is irrational, then it is not closed and has infinite length.
 * Furthermore, the graph of the rose in this case forms a dense set
 * (i.e., it comes arbitrarily close to every point in the unit disk).
 * <p>
 * Adding an offset parameter c, so the polar equation becomes
 * r = cos(k * theta) + c
 * In the case where the parameter k is an odd integer, the two overlapping halves of the curve separate as the offset changes from zero.
 */
public class RhodoneaFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_KNUMER = "knumer";
  private static final String PARAM_KDENOM = "kdenom";
  private static final String PARAM_INNER_MODE = "inner_mode";
  private static final String PARAM_OUTER_MODE = "outer_mode";
  private static final String PARAM_INNER_SPREAD = "inner_spread";
  private static final String PARAM_OUTER_SPREAD = "outer_spread";
  private static final String PARAM_INNER_SPREAD_RATIO = "inner_spread_ratio";
  private static final String PARAM_OUTER_SPREAD_RATIO = "outer_spread_ratio";
  private static final String PARAM_SPREAD_SPLIT = "spread_split";
  private static final String PARAM_FILL = "fill";
  private static final String PARAM_RADIAL_OFFSET = "radial_offset";
  // if cycles is set to 0, function will make best effort to calculate minimum number of cycles needed 
  //     to close curve, or a somewhat arbitrary number if cannot 
  private static final String PARAM_CYCLES = "cycles";
  private static final String PARAM_CYCLE_OFFSET = "cycle_offset";
  private static final String PARAM_METACYCLES = "metacycles"; // only in effect when cycles = 0 (automatic cycle calculations in effect)
  private static final String PARAM_METACYCLE_EXPANSION = "metacycle_expansion";

  private static final String[] paramNames = {PARAM_KNUMER, PARAM_KDENOM, PARAM_INNER_MODE, PARAM_OUTER_MODE, PARAM_INNER_SPREAD, PARAM_OUTER_SPREAD, PARAM_INNER_SPREAD_RATIO, PARAM_OUTER_SPREAD_RATIO, PARAM_SPREAD_SPLIT, PARAM_FILL, PARAM_RADIAL_OFFSET, PARAM_CYCLES, PARAM_CYCLE_OFFSET, PARAM_METACYCLES, PARAM_METACYCLE_EXPANSION};

  private double knumer = 3; // numerator of k in rose curve equations,   k = kn/kd
  private double kdenom = 4; // denominator of k in rose curve equations, k = kn/kd
  private double radial_offset = 0; // often called "c" in rose curve modifier equations
  private int inner_mode = 1; // transform mode to use for incoming points within or on the curve
  private int outer_mode = 1; // transform mode to use for incoming points outside the curve
  private double inner_spread = 0; // amount to spread away from rose curve within or on the curve, based on incoming point x/y coords
  private double outer_spread = 0; // amount to spread away from rose curve outside the curve, based on incoming point x/y coords
  private double inner_spread_ratio = 1; // how much inner spread applies to x relative to y
  private double outer_spread_ratio = 1; // how much outer spread applies to x relative to y
  private double spread_split = 1; // scaling of input point radius to determine inner/outer threshold
  private double cycles_param = 0; // number of cycles (roughly circle loops?), if set to 0 then number of cycles is calculated automatically to close curve (if possible)
  private double cycle_offset = 0; // radians to offset cycle for incoming points
  private double metacycle_expansion = 0; // expansion factor for each metacycle
  private double metacycles = 1; // if cycles is calculated automatically to close the curve, metacycles is number of times to loop over closed curve
  private double fill = 0; // amount to thicken curve by randomizing input 

  private double kn, kd;
  private double k; // k = kn/kd
  private double cycles; // 1 cycle = 2*PI
  private double cycles_to_close;

  // want to figure out (when possible):
  //     number of cycles(radians) to close the curve (radians = 2*PI*cycles)
  //     number of petals in the curve
  //     given those, can also calculate: for a given input x and y, which petal(s) the point will map to.
  //         
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    kn = knumer;
    kd = kdenom;
    k = kn / kd;

    // attempt to calculate minimum cycles manually, or reasonable upper bound if unsure
    if ((k % 1) == 0) { // k is integer 
      if ((k % 2) == 0) { // k is even integer, will have 2k petals
        cycles_to_close = 1; // (2PI)
      } else { // k is odd integer, will have k petals (or sometimes 2k with offset)
        if (radial_offset != 0 || inner_spread != 0 || outer_spread != 0 || fill != 0) {
          cycles_to_close = 1;
        } // if adding an offset or spread, need a full cycle
        else {
          cycles_to_close = 0.5;
        } // (1PI)
      }
    } else if ((kn % 1 == 0) && (kd % 1 == 0)) {
      // if kn and kd are integers,
      //   determine if kn and kd are relatively prime (their greatest common denominator is 1)
      //   using builtin gcd() function for BigIntegers in Java
      // and if they're not, make them
      BigInteger bigkn = BigInteger.valueOf((long) kn);
      BigInteger bigkd = BigInteger.valueOf((int) kd);
      int gcd = bigkn.gcd(bigkd).intValue();
      if (gcd != 1) {
        kn = kn / gcd;
        kd = kd / gcd;
      }

      // paraphrased from http://www.encyclopediaofmath.org/index.php/Roses_%28curves%29:
      //    If kn and kd are relatively prime, then the rose consists of 2*kn petals if either kn or kd are even, and kn petals if both kn and kd are odd
      //
      // paraphrased from http://mathworld.wolfram.com/Rose.html:
      //    If k=kn/kd is a rational number, then the curve closes at a polar angle of theta = PI * kd if (kn * kd) is odd, and 2 * PI * kd if (kn * kd) is even
      if ((kn % 2 == 0) || (kd % 2 == 0)) {
        cycles_to_close = kd; // 2 * PI * kd
      } else {
        cycles_to_close = kd / 2; // PI * kd
      }
    }

    // additional special cases:
    /*  
    // superceded by relative prime conditional?
    else if (((k * 2) % 1) == 0) { // k is a half-integer (1/2, 3/2, 5/2, etc.), will have 4k petals
      cycles_to_close = 2;  // (4PI)
      petal_count = 4*k;
    }
    */
    /* 
    // superceded by relative prime conditional?
    // If k can be expressed as kn/3, where n is an integer not divisible by 3, the curve will be rose-shaped with n petals if n is odd and 2n petals if n is even.
    //    (case where kn is integer divisible by three is already handled above, where k is integer)
    else if (((k * 3) % 1) == 0) {
      double basekn = k * 3;
      if ((basekn % 2) == 0) {
        cycles_to_close = 3;
        petal_count = 2 * basekn;
      }
      else  {
        cycles_to_close = 1.5;
          petal_count = basekn;
      }
    }
    */
    /*
    // superceded by relative prime conditional????
      If k can be expressed as n±1/6, where n is a nonzero integer, the curve will be rose-shaped with 12k petals.
      else if {
    
      }
    */

    else {
      //     if one or both of kn and kd are non-integers, then the above may still be true (k may still be [effectively] rational) but haven't 
      //          figured out a way to determine this.
      //    could restrict kn and kd to integers to simplify, but that would exclude a huge space of interesting patterns
      //    could set cycles extremely high, but if k is truly irrational this will just approarch a uniform distribution across a circle, 
      //                and also exclude a large space of interesting patterns with non-closed curves
      //    so for now keep kn and kd as continuous doubles, and just pick a large but not huge number for cycles
      // 
      //    realistically in this case it is better for user to fiddle with manual cycles setting to get a pattern they like
      //        
      cycles_to_close = 2 * kn * kd;
      if (cycles < 16) {
        cycles_to_close = 16;
      } // ???  just want to keep number higher if kn and kd are small but potentially irrational
    }
    if (cycles_param == 0) {
      // use auto-calculation of cycles (2*PI*radians) to close the curve, 
      //     and metacycles for how many closed curves to cycle through
      cycles = cycles_to_close * metacycles;
    } else {
      // manually set number of cycles (cycles are specified in 2*PI*radians)
      cycles = cycles_param;
    }
    // System.out.println("cycles to close: " + cycles_to_close);
    // System.out.println("metacycles: " + metacycles);
    // System.out.println("total cycles: " + cycles);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /*
        k = kn/kd
        r = cos(k * theta)   
     or, alternatively, as a pair of Cartesian parametric equations of the form
        t = atan2(y,x)
        x = cos(kt)cos(t)
        y = cos(kt)sin(t)
    */
    double rin = spread_split * sqrt((pAffineTP.x * pAffineTP.x) + (pAffineTP.y * pAffineTP.y));
    // atan2 range is [-PI, PI], so tin covers 2PI, or 1 cycle (from -0.5 to 0.5 cycle)
    double tin = atan2(pAffineTP.y, pAffineTP.x); // polar coordinate angle (theta in radians) of incoming point
    double t = cycles * (tin + (cycle_offset * 2 * M_PI)); // angle of rose curve
    double r = cos(k * t) + radial_offset; // radius of rose curve 
    // double r = sin(k * t) + radial_offset;  // can use sin instead of cos to rotate by PI/(2*k) radians
    if (fill != 0) {
      r = r + (fill * (pContext.random() - 0.5));
    }
    double x = r * cos(t);
    double y = r * sin(t);
    double expansion = floor((cycles * (tin + M_PI)) / (cycles_to_close * 2 * M_PI));
    double adjustedAmount = pAmount + (expansion * metacycle_expansion);
    double xin, yin;
    double rinx, riny;

    if (abs(rin) > abs(r)) { // incoming point lies outside of current petal of rose curve
      switch (outer_mode) {
        case 0: // no spread
          pVarTP.x += adjustedAmount * x;
          pVarTP.y += adjustedAmount * y;
          break;
        case 1:
          rinx = (rin * outer_spread * outer_spread_ratio) - (outer_spread * outer_spread_ratio) + 1;
          riny = (rin * outer_spread) - outer_spread + 1;
          pVarTP.x += adjustedAmount * rinx * x;
          pVarTP.y += adjustedAmount * riny * y;
          if (pVarTP.y == 0) {
            pVarTP.x = 0;
          }
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
          //pVarTP.x += adjustedAmount * x + ((rin - r) * outer_spread * outer_spread_ratio);
          pVarTP.x += adjustedAmount * (x + (outer_spread * outer_spread_ratio * (xin - x)));
          pVarTP.y += adjustedAmount * (y + (outer_spread * (yin - y)));
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
          pVarTP.x += adjustedAmount * (x + (outer_spread * outer_spread_ratio * xin));
          pVarTP.y += adjustedAmount * (y + (outer_spread * yin));
          break;
        case 4:
          rinx = (0.5 * rin) + (outer_spread * outer_spread_ratio);
          riny = (0.5 * rin) + outer_spread;
          pVarTP.x += adjustedAmount * rinx * x;
          pVarTP.y += adjustedAmount * riny * y;
          break;
        case 5: // mask "inside" the curve
          pVarTP.x += pAffineTP.x;
          pVarTP.y += pAffineTP.y;
          break;
        case 6: // mask "outside" the curve
          pVarTP.doHide = true;
          break;
        default:
          pVarTP.x += adjustedAmount * x;
          pVarTP.y += adjustedAmount * y;
          break;
      }
    } else { // incoming point lies on or inside current petal of rose curve
      switch (inner_mode) {
        case 0: // no spread, all points map to rose curve
          pVarTP.x += adjustedAmount * x;
          pVarTP.y += adjustedAmount * y;
          break;
        case 1:
          // rin = (rin * inner_spread) - inner_spread + 1;
          rinx = (rin * inner_spread * inner_spread_ratio) - (inner_spread * inner_spread_ratio) + 1;
          riny = (rin * inner_spread) - inner_spread + 1;
          pVarTP.x += adjustedAmount * rinx * x;
          pVarTP.y += adjustedAmount * riny * y;
          if (pVarTP.y == 0) {
            pVarTP.x = 0;
          }
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
          pVarTP.x += adjustedAmount * (x - (inner_spread * inner_spread_ratio * (x - xin)));
          pVarTP.y += adjustedAmount * (y - (inner_spread * (y - yin)));
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
          pVarTP.x += adjustedAmount * (x - (inner_spread * inner_spread_ratio * xin));
          pVarTP.y += adjustedAmount * (y - (inner_spread * yin));
          break;
        case 4:
          // rin = (0.5 * rin) + inner_spread;
          rinx = (0.5 * rin) + (inner_spread * inner_spread_ratio);
          riny = (0.5 * rin) + inner_spread;
          pVarTP.x += adjustedAmount * rinx * x;
          pVarTP.y += adjustedAmount * riny * y;
          break;
        case 5: // mask "inside" the curve
          pVarTP.doHide = true;
          break;
        case 6: // mask "outside" the curve
          pVarTP.x += pAffineTP.x;
          pVarTP.y += pAffineTP.y;
          break;
        default: // shouldn't reach here (inner_mode is constrained to be 0-6), but just in case...
          pVarTP.x += adjustedAmount * x;
          pVarTP.y += adjustedAmount * y;
          break;
      }
    }

    boolean draw_diagnostics = false;
    if (draw_diagnostics) {
      double diagnostic = pContext.random() * 100;
      // draw diagnostic unit circles
      if (diagnostic == 0) {
        // ignore zero
      }
      if (diagnostic <= 3) { // diagnostic = (0-3]
        double radius = ceil(diagnostic); // radius = 1, 2, 3
        double angle = diagnostic * 2 * M_PI; // in radians, ensures coverage of unit circles
        pVarTP.x = radius * cos(angle);
        pVarTP.y = radius * sin(angle);
      }
      // draw diagnostic unit squares
      else if (diagnostic <= 6) { // diagnostic = (3-6]
        double unit = ceil(diagnostic) - 3; // unit = 1, 2, 3
        int side = (int) ceil(4 * (ceil(diagnostic) - diagnostic)); // side = 1, 2, 3, 4
        double varpos = (pContext.random() * unit * 2) - unit;
        double sx = 0, sy = 0;
        if (side == 1) {
          sx = unit;
          sy = varpos;
        } else if (side == 2) {
          sx = varpos;
          sy = unit;
        } else if (side == 3) {
          sx = -1 * unit;
          sy = varpos;
        } else if (side == 4) {
          sx = varpos;
          sy = -1 * unit;
        }
        pVarTP.x = sx;
        pVarTP.y = sy;
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
    return new Object[]{knumer, kdenom,
            inner_mode, outer_mode, inner_spread, outer_spread, inner_spread_ratio, outer_spread_ratio, spread_split,
            fill, radial_offset,
            cycles_param, cycle_offset, metacycles, metacycle_expansion};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_KNUMER.equalsIgnoreCase(pName))
      knumer = pValue;
    else if (PARAM_KDENOM.equalsIgnoreCase(pName))
      kdenom = pValue;
    else if (PARAM_RADIAL_OFFSET.equalsIgnoreCase(pName))
      radial_offset = pValue;
    else if (PARAM_INNER_MODE.equalsIgnoreCase(pName)) {
      inner_mode = (int) floor(pValue);
      if (inner_mode > 6 || inner_mode < 0) {
        inner_mode = 0;
      }
    } else if (PARAM_OUTER_MODE.equalsIgnoreCase(pName)) {
      outer_mode = (int) floor(pValue);
      if (outer_mode > 6 || outer_mode < 0) {
        outer_mode = 0;
      }
    } else if (PARAM_INNER_SPREAD.equalsIgnoreCase(pName))
      inner_spread = pValue;
    else if (PARAM_OUTER_SPREAD.equalsIgnoreCase(pName))
      outer_spread = pValue;
    else if (PARAM_INNER_SPREAD_RATIO.equalsIgnoreCase(pName))
      inner_spread_ratio = pValue;
    else if (PARAM_OUTER_SPREAD_RATIO.equalsIgnoreCase(pName))
      outer_spread_ratio = pValue;
    else if (PARAM_SPREAD_SPLIT.equalsIgnoreCase(pName))
      spread_split = pValue;
    else if (PARAM_CYCLES.equalsIgnoreCase(pName))
      cycles_param = pValue;
    else if (PARAM_CYCLE_OFFSET.equalsIgnoreCase(pName))
      cycle_offset = pValue;
    else if (PARAM_METACYCLES.equalsIgnoreCase(pName))
      metacycles = pValue;
    else if (PARAM_METACYCLE_EXPANSION.equalsIgnoreCase(pName))
      metacycle_expansion = pValue;
    else if (PARAM_FILL.equalsIgnoreCase(pName))
      fill = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "rhodonea";
  }

}
