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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.M_PI;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
  Rhodonea curves (also known as rose curves)
  Implemented by CozyG, March 2015
  For references, see http://en.wikipedia.org/wiki/Rose_%28mathematics%29
  There are other JWildfire variations (RoseWF, PRose3D) that implement the class of rose curves 
  where k is an integer and cycles = 1, but Rhodonea implements a fuller range of possibilities (within the 2D plane)

   From reference literature:
   Up to similarity(?), these curves can all be expressed by a polar equation of the form
        r = cos(k * theta)   

   or, alternatively, as a pair of Cartesian parametric equations of the form
        x = cos(kt)cos(t)
        y = cos(kt)sin(t)

    If k is an integer, the curve will be rose-shaped with:
         2k petals if k is even, and
         k petals if k is odd.

    When k is even, the entire graph of the rose will be traced out exactly once when the value of θ changes from 0 to 2π. 
    When k is odd, this will happen on the interval between 0 and π. 
    (More generally, this will happen on any interval of length 2π for k even, and π for k odd.)

    If k is a half-integer (e.g. 1/2, 3/2, 5/2), the curve will be rose-shaped with 4k petals.

    If k can be expressed as n±1/6, where n is a nonzero integer, the curve will be rose-shaped with 12k petals.

    If k can be expressed as n/3, where n is an integer not divisible by 3, 
       the curve will be rose-shaped with n petals if n is odd and 2n petals if n is even.

    If k is rational, then the curve is closed and has finite length. 
    If k is irrational, then it is not closed and has infinite length. 
    Furthermore, the graph of the rose in this case forms a dense set 
    (i.e., it comes arbitrarily close to every point in the unit disk).

    Adding an offset parameter c, so the polar equation becomes
       r = cos(k * theta) + c
    In the case where the parameter k is an odd integer, the two overlapping halves of the curve separate as the offset changes from zero.

    Rhodonea curves were named by the Italian mathematician Guido Grandi between the year 1723 and 1728.[2]
*/
public class RhodoneaFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_KN = "kn";
  private static final String PARAM_KD = "kd";
  // if cycles is set to 0, function will make best effort to calculate minimum number of cycles needed 
  //     to close curve, or a somewhat arbitrary number if cannot 
  private static final String PARAM_CYCLES = "cycles";
  private static final String PARAM_OFFSET = "offset";
  
  private static final String[] paramNames = { PARAM_KN, PARAM_KD, PARAM_CYCLES, PARAM_OFFSET };

  //private double amp = 0.5;
  //  private int waves = 4;
  private double kn = 3;    // numerator of k
  private double kd = 4;    // denominator of k
  private double cyclesParam = 0;  // number of cycles (roughly circle loops?), if set to 0 then number of cycles is calculated automatically
  private double offset = 0;  // offset c from equations

  private double k;  // k = kn/kd
  private double cycles;  // 1 cycle = 2*PI

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    k = kn/kd;
    if (cyclesParam != 0) { cycles = cyclesParam; } // cycles manually set
    else { // attempt to calculate minimum cycles manually, or reasonable upper bound if unsure
      if ((k % 1) == 0) {  // k is integer 
        if ((k % 2) == 0) { // k is even integer, will have 2k petals
          cycles = 1;  // (2PI)
        }
        else  { // k is odd integer, will have k petals (or sometimes 2k with offset)
          if (offset != 0) { cycles = 1; }  // if adding an offset, need a full cycle
          else { cycles = 0.5; }  // (1PI)
        }
      }
      else if (((k * 2) % 1) == 0) { // k is a half-integer (1/2, 3/2, 5/2, etc.), will have 4k petals
        cycles = 2;  // (4PI)
      }
      else {
        // if kn and kd are both integers, just multiply kn * kd, 
        // I don't have a mathematical proof, but observationally:
        //      if kn and kd are both integers (therefore k is rational, therefore curve is closed)
        //          then kn * kd establishes an upper bound on number of cycles needed to close the curve
        if (((kn % 1) == 0) && ((kd % 1) == 0))  {
          cycles = kn * kd;
        }
        //     if one or both of kn and kd are non-integers, then the above may still be true (k may still be rational) but haven't 
        //          figured out a way to determine this.
        //    could restrict kn and kd to integers to simplify, but that would exclude a huge space of interesting patterns
        //    could set cycles extremely high, but if k is truly irrational this will just approarch a uniform distribution across a circle, 
        //                and also exclude a large space of interesting patterns with non-closed curves
        //    so for now keep kn and kd as continuous doubles, and just pick a large but not huge number for cycles

        // 
        //    realistically in this case it is better for user to fiddle with manual cycles setting to get a pattern they like
        //        
        else  {
          cycles = 2 * kn * kd;
          if (cycles < 32) { cycles = 32; } // ???  just want to keep number higher if kn and kd are small but potentially irrational
        }
      }
    }
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
    // double theta = (pContext.random() * cycles * 2 * M_PI);
    double t = atan2(pAffineTP.y, pAffineTP.x);  // atan2 range is [-PI, PI], so covers 2PI, or 1 cycle
    double theta = cycles * t;
    double r = cos(k * theta) + offset;
    double x = r * cos(theta);
    double y = r * sin(theta);
    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;

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
    return new Object[] { kn, kd, cyclesParam, offset };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_KN.equalsIgnoreCase(pName))
      kn = pValue;
    else if (PARAM_KD.equalsIgnoreCase(pName))
      kd = pValue;
    else if (PARAM_CYCLES.equalsIgnoreCase(pName))
      cyclesParam = pValue;
    else if (PARAM_OFFSET.equalsIgnoreCase(pName))
      offset = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "rhodonea";
  }

}
