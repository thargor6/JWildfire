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

/*************************************************************************************************
 * @author Jesus Sosa
 * @date November 27, 2017
 *
 * Variation: hamid_js
 * Parameters:
 *
 * presetid : select one from 20 patterns
 * number_lines/circles : number of Lines or Circles to draw
 * filled circles:   0= not filled , 1= filled
 *
 * Based on work "Math Art" By Hamid Naderi Yeganeh 
 *
 * Check The work of Hamid Naderi Yeganeh 
 * https://mathematics.culturalspot.org/browse
 *
 *************************************************************************************************/

package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.lang.reflect.Method;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * @date October 17, 2017
 * draw_js Variation
 * draw vectors in format 0,x,y,1,x,y,1,x,y..... (0=moveTo, 1=lineTo)
 * Variation params:
 * Level:  1,2,3,4,5
 * Line_thickness : 1..100
 */

public class HamidFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_PRESETID = "presetId";
  private static final String PARAM_NPOINTS = "number_lines/circles";
  private static final String PARAM_FILLED = "filled circles";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String PARAM_G = "g";
  private static final String PARAM_H = "h";
  private static final String PARAM_I = "i";
  private static final String PARAM_J = "j";
  private static final String PARAM_K = "k";
  private static final String PARAM_L = "l";
  private static final String PARAM_M = "m";
  private static final String PARAM_N = "n";
  private static final String PARAM_O = "o";
  private static final String PARAM_P = "p";

  private static final String[] paramNames = {PARAM_PRESETID, PARAM_NPOINTS,
          PARAM_FILLED, PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F,
          PARAM_G, PARAM_H, PARAM_I, PARAM_J, PARAM_K, PARAM_L, PARAM_M,
          PARAM_N, PARAM_O, PARAM_P};

  private static int nMax = 20;

  private int presetId = 0;
  private int npoints = 2000;
  private int filled = 1;
  private double a = 1.0;
  private double b = 1.0;
  private double c = 1.0;
  private double d = 1.0;
  private double e = 1.0;
  private double f = 1.0;
  private double g = 1.0;
  private double h = 1.0;
  private double i = 1.0;
  private double j = 1.0;
  private double k = 1.0;
  private double l = 1.0;
  private double m = 1.0;
  private double n = 1.0;
  private double o = 1.0;
  private double p = 1.0;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer,
                   XForm pXForm, double pAmount) {
    Flame flame = pContext.getFlameRenderer().getFlame();
    flame.setAntialiasAmount(0.0);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm,
                        XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (presetId >= nMax)
      presetId = nMax - 1;

    int I = (int) (pContext.random() * npoints + 1);
    double xout = 0, yout = 0;


    if(presetId < 10) {
      double x1 = 0, y1 = 0;
      double x2 = 0, y2 = 0;
      switch(presetId) {
        case 0: {
          x1 = -MathLib.sin(4. * a * MathLib.M_PI * Integer.valueOf(I)
                  / npoints);
          y1 = -MathLib.cos(2. * b * MathLib.M_PI * I / npoints);
          x2 = -0.5 * e * MathLib.sin(8. * c * MathLib.M_PI * I / npoints);
          y2 = -0.5 * f * MathLib.cos(4. * d * MathLib.M_PI * I / npoints);
          break;
        }
        case 1:
        {
          x1 = -MathLib.sin(2. * a * MathLib.M_PI * Integer.valueOf(I)
                  / npoints);
          y1 = -MathLib.cos(2. * b * MathLib.M_PI * I / npoints);
          x2 = -0.5 * e * MathLib.sin(8.0 * c * MathLib.M_PI * I / npoints);
          y2 = -0.5 * f * MathLib.cos(12. * d * MathLib.M_PI * I / npoints);
          break;
        }
        case 2: {
          x1 = -MathLib.sin(8. * a * MathLib.M_PI * Integer.valueOf(I)
                  / npoints);
          y1 = -MathLib.cos(2. * b * MathLib.M_PI * I / npoints);
          x2 = -0.5 * e * MathLib.sin(6. * c * MathLib.M_PI * I / npoints);
          y2 = -0.5 * f * MathLib.cos(2. * d * MathLib.M_PI * I / npoints);
          break;
        }
        case 3: {
          x1 = -MathLib.sin(10. * a * MathLib.M_PI * Integer.valueOf(I)
                  / npoints);
          y1 = -MathLib.cos(2. * b * MathLib.M_PI * I / npoints);
          x2 = -0.5 * e * MathLib.sin(12. * c * MathLib.M_PI * I / npoints);
          y2 = -0.5 * f * MathLib.cos(2. * d * MathLib.M_PI * I / npoints);
          break;
        }
        case 4: {
          x1 = MathLib.sin(10. * a * MathLib.M_PI
                  * (Integer.valueOf(I) + 699) / npoints);
          y1 = MathLib.cos(8. * b * MathLib.M_PI * (I + 699) / npoints);
          x2 = MathLib.sin(12. * c * MathLib.M_PI * (I + 699) / npoints);
          y2 = MathLib.cos(10. * d * MathLib.M_PI * (I + 699) / npoints);
          break;
        }
        case 5: {
          x1 = -2 * a * MathLib.cos(4. * b * MathLib.M_PI * I / 1000);
          y1 = 0.5
                  * c
                  * MathLib.pow(MathLib.cos(6.0 * d * MathLib.M_PI * I / npoints),
                  3 * i);

          x2 = -2 * e * MathLib.sin(6. * f * MathLib.M_PI * I / npoints)
                  / 15.0 * j;
          y2 = (4.0 * g * MathLib.sin(2.0 * h * MathLib.M_PI * I / npoints)
                  / 15.0 * k);
          break;
        }
        case 6: {
          x1 = 3.0
                  * a
                  * MathLib.pow(
                  MathLib.sin(2. * b * MathLib.M_PI * Integer.valueOf(I)
                          / npoints), 3 * h);
          y1 = -MathLib.cos(8. * c * MathLib.M_PI * I / npoints);
          x2 = 1.5
                  * d
                  * MathLib.pow(MathLib.sin(2. * e * MathLib.M_PI * I / npoints), 3 * i);
          y2 = -0.5 * f * MathLib.cos(6. * g * MathLib.M_PI * I / npoints);
          break;
        }
        case 7: {
          x1 = MathLib.cos(6. * a * MathLib.M_PI * Integer.valueOf(I)
                  / npoints);
          y1 = -MathLib.cos(8. * b * MathLib.M_PI * I / npoints);
          x2 = 1.5 * c
                  * MathLib.pow(MathLib.sin(2. * MathLib.M_PI * I / npoints), 3 * f);
          y2 = -0.5 * d * MathLib.cos(6. * e * MathLib.M_PI * I / npoints);
          break;
        }
        case 8: {
          x1 = Math
                  .pow(MathLib.sin(20. * a * MathLib.M_PI * Integer.valueOf(I)
                          / npoints), 3);
          y1 = MathLib.pow(MathLib.cos(18. * b * MathLib.M_PI * I / npoints),
                  3 * e);

          x2 = Math
                  .pow(MathLib.sin(16. * c * MathLib.M_PI * Integer.valueOf(I)
                          / npoints), 3);
          y2 = MathLib.pow(MathLib.cos(14. * d * MathLib.M_PI * I / npoints),
                  3 * f);
          break;
        }
        case 9: {
          {
            int n = I;
            do {
              if (n < 100) {
                x1 = n / 100.0;
                y1 = 0.0;
                break;
              } else if (n < 200) {
                x1 = 1.0;
                y1 = (n - 100.) / 100.0;
                break;
              } else if (n < 300) {
                x1 = (300 - n) / 100.0;
                y1 = 1.0;
                break;
              } else if (n < 400) {
                x1 = 0.0;
                y1 = (400 - n) / 100.0;
                break;
              } else
                n = n - 400;
            } while (true);
          }
          {
            int n = I + 199;
            do {
              if (n < 100) {
                x2 = n / 100.0;
                y2 = 0.0;
                break;
              } else if (n < 200) {
                x2 = 1.0;
                y2 = (n - 100.) / 100.0;
                break;
              } else if (n < 300) {
                x2 = (300 - n) / 100.0;
                y2 = 1.0;
                break;
              } else if (n < 400) {
                x2 = 0.0;
                y2 = (400 - n) / 100.0;
                break;
              } else
                n = n - 400;
            } while (true);
          }
          break;
        }
      }

      double ydiff = y2 - y1;
      double xdiff = x2 - x1;
      double m;
      if (xdiff == 0)
        m = 10000;
      else
        m = ydiff / xdiff; // slope
      double line_length = Math.sqrt((xdiff * xdiff) + (ydiff * ydiff));
      double xoffset = 0, yoffset = 0;

      // draw point at a random distance along line
      // (along straight line connecting endpoint1 and endpoint2)
      double d = pContext.random() * line_length;
      // x = x1 [+-] (d / (sqrt(1 + m^2)))
      // y = y1 [+-] (m * d / (sqrt(1 + m^2)))
      // determine sign based on orientation of endpoint2 relative to
      // endpoint1
      xoffset = d / Math.sqrt(1 + m * m);
      if (x2 < x1) {
        xoffset = -1 * xoffset;
      }
      yoffset = Math.abs(m * xoffset);
      if (y2 < y1) {
        yoffset = -1 * yoffset;
      }

      xout = x1 + xoffset;
      yout = y1 + yoffset;

    }
    else {
      double x = 0;
      double y = 0;
      double radius = 0;
      switch(presetId) {
        case 10: {
          x = 2.0 * a * MathLib.sin(2.0 * b * MathLib.M_PI * I / npoints)
                  / 3.0 + MathLib.sin(36.0 * c * MathLib.M_PI * I / npoints)
                  * MathLib.cos(30.0 * d * MathLib.M_PI * I / npoints) / 3.0 * k
                  + MathLib.pow(MathLib.sin(14.0 * MathLib.M_PI * I / npoints), 3 * l)
                  / 3.0 * m;
          y = 2.0 * e * MathLib.cos(2.0 * f * MathLib.M_PI * I / npoints)
                  / 3.0 * n + MathLib.cos(36.0 * g * MathLib.M_PI * I / npoints)
                  * MathLib.cos(30.0 * h * MathLib.M_PI * I / npoints) / 3.0 * o
                  + MathLib.pow(MathLib.cos(14.0 * i * MathLib.M_PI * I / npoints), 3)
                  / 5.0 * p;
          radius = 1.0 / 150.0 + MathLib.pow(
                  MathLib.sin(36. * j * MathLib.M_PI * I / npoints), 4) / 9.0;
          break;
        }
        case 11: {
          x = MathLib.sin(14.0 * a * MathLib.M_PI * I / npoints);
          y = MathLib.cos(32.0 * b * MathLib.M_PI * I / npoints);
          radius = MathLib.pow(
                  MathLib.sin(20. * c * MathLib.M_PI * I / npoints), 2) / 3.0 * d;
          break;
        }
        case 12: {
          x = 2.0 * a * MathLib.sin(6.0 * b * MathLib.M_PI * I / npoints)
                  / 3.0 + MathLib.sin(36.0 * c * MathLib.M_PI * I / npoints)
                  * MathLib.cos(30.0 * d * MathLib.M_PI * I / npoints) / 4.0
                  + MathLib.pow(MathLib.sin(14.0 * e * MathLib.M_PI * I / npoints), 3 * k)
                  / 4.0 * l;
          y = 2.0 * f * MathLib.cos(6.0 * g * MathLib.M_PI * I / npoints)
                  / 3.0 + MathLib.cos(28.0 * h * MathLib.M_PI * I / npoints)
                  * MathLib.cos(30.0 * i * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.cos(14.0 * MathLib.M_PI * I / npoints), 3 * m)
                  / 5.0 * n;
          radius = 1.0 / 150.0 + MathLib.pow(
                  MathLib.sin(36. * j * MathLib.M_PI * I / npoints), 4 * o) / 20.0 * p;
          break;
        }
        case 13: {
          x = MathLib.sin(10.0 * a * MathLib.M_PI * I / npoints);
          y = MathLib.pow(MathLib.cos(28.0 * b * MathLib.M_PI * I / npoints), 3 * e);
          radius = MathLib.pow(
                  MathLib.sin(18. * c * MathLib.M_PI * I / npoints), 2) / 3.0 * d;
          break;
        }
        case 14: {
          x = 2.0 * a * MathLib.sin(18.0 * b * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.sin(40.0 * c * MathLib.M_PI * I / npoints), 3)
                  * MathLib.cos(30.0 * d * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.sin(34.0 * e * MathLib.M_PI * I / npoints), 3 * k)
                  / 4.0 * l;
          y = 2.0 * f * MathLib.cos(18.0 * g * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.cos(28.0 * h * MathLib.M_PI * I / npoints), 3)
                  * MathLib.cos(40.0 * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.cos(34.0 * i * MathLib.M_PI * I / npoints), 3 * m)
                  / 3.0 * n;
          radius = 1.0 / 150.0 + MathLib.pow(
                  MathLib.sin(56. * j * MathLib.M_PI * I / npoints), 4 * o) / 8.0 * p;
          break;
        }
        case 15: {
          x = 2.0 * a * MathLib.sin(14.0 * b * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.sin(10.0 * c * MathLib.M_PI * I / npoints), 3)
                  * MathLib.cos(28.0 * d * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.sin(46.0 * e * MathLib.M_PI * I / npoints), 3 * m)
                  / 3.0 * n;
          y = 2.0 * MathLib.cos(14.0 * f * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.cos(10.0 * g * MathLib.M_PI * I / npoints), 3)
                  * MathLib.cos(28.0 * h * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.cos(34.0 * j * MathLib.M_PI * I / npoints), 3 * o)
                  / 3.0;
          radius = 1.0 / 150.0
                  + MathLib.pow(MathLib.sin(72. * k * MathLib.M_PI * I / npoints), 6) / 9.0
                  + MathLib.pow(MathLib.sin(12.0 * l * MathLib.M_PI * I / npoints), 4)
                  / 20.0 * p;
          break;
        }
        case 16: {
          x = MathLib.sin(38.0 * a * MathLib.M_PI * I / npoints)
                  * MathLib.cos(26.0 * b * MathLib.M_PI * I / npoints) / 2.0
                  + MathLib.pow(MathLib.sin(18.0 * c * MathLib.M_PI * I / npoints), 3 * l)
                  / 2.0 * m;
          y = 2.0 * MathLib.cos(18.0 * d * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.cos(38.0 * e * MathLib.M_PI * I / npoints)
                  * MathLib.cos(36.0 * f * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.cos(18.0 * g * MathLib.M_PI * I / npoints), 3 * n)
                  / 4.0 * o;
          radius = 1.0 / 150.0
                  + MathLib.pow(MathLib.sin(18. * h * MathLib.M_PI * I / npoints), 10)
                  * MathLib.pow(MathLib.sin(100.0 * i * MathLib.M_PI * I / npoints), 6)
                  / 100.0
                  + MathLib.pow(MathLib.sin(72.0 * j * MathLib.M_PI * I / npoints), 8)
                  * MathLib.pow(MathLib.cos(18.0 * k * MathLib.M_PI * I / npoints), 6)
                  / 20.0 * p;
          break;
        }
        case 17: {
          x = 2.0 * a * MathLib.sin(10.0 * b * MathLib.M_PI * I / npoints)
                  + MathLib.pow(MathLib.sin(6.0 * c * MathLib.M_PI * I / npoints), 3)
                  * MathLib.cos(28.0 * d * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.sin(126.0 * e * MathLib.M_PI * I / npoints) / 10.0 * l;
          y = 2.0 * MathLib.cos(6.0 * f * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.cos(18.0 * g * MathLib.M_PI * I / npoints), 3)
                  * MathLib.cos(36.0 * h * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.cos(126.0 * i * MathLib.M_PI * I / npoints) / 10.0 * m;
          radius = 1.0
                  / 150.0
                  + MathLib.pow(MathLib.sin(48. * j * MathLib.M_PI * I / npoints), 4)
                  * (1.0 + MathLib.pow(MathLib.sin(12.0 * k * MathLib.M_PI * I / npoints),
                  2 * n)) / 18.0 * o;

          break;
        }
        case 18: {
          x = 2.0 * a * MathLib.sin(18.0 * b * MathLib.M_PI * I / npoints) / 5.0
                  + MathLib.pow(MathLib.sin(34.0 * c * MathLib.M_PI * I / npoints), 3 * j)
                  / 3.0 * k
                  + MathLib.pow(MathLib.sin(50.0 * d * MathLib.M_PI * I / npoints), 3 * l)
                  / 3.0 * m;
          y = 2.0 * MathLib.cos(18.0 * e * MathLib.M_PI * I / npoints) / 3.0
                  + MathLib.pow(MathLib.cos(34.0 * f * MathLib.M_PI * I / npoints), 3)
                  / 3.0
                  + MathLib.pow(MathLib.cos(50.0 * g * MathLib.M_PI * I / npoints), 3 * n)
                  / 10.0 * o;
          radius = 1.0 / 150.0 + MathLib.pow(
                  MathLib.sin(58. * i * MathLib.M_PI * I / npoints), 4) / 10.0 * p;

          break;
        }
        case 19: {
          x = MathLib.cos(10.0 * a * MathLib.M_PI * I / npoints)
                  * (1.0 * c - MathLib.pow(MathLib.sin(16.0 * b * MathLib.M_PI * I / npoints),
                  2 * h) / 2.0 * i);
          y = MathLib.sin(10.0 * d * MathLib.M_PI * I / npoints)
                  * (1.0 * f - MathLib.pow(MathLib.cos(34.0 * e * MathLib.M_PI * I / npoints),
                  2 * j) / 2.0 * k);
          radius = 1.0 / 200.0 + MathLib.pow(
                  MathLib.sin(52. * g * MathLib.M_PI * I / npoints), 4 * l) / 10.0 * m;

          break;
        }
      }

      if (filled == 1) {
        if (pContext.random() > 0.05)
          radius = radius * pContext.random();
      }
      double a = pContext.random() * 2 * M_PI;
      double sina = sin(a);
      double cosa = cos(a);

      xout = x + radius * cosa;
      yout = y + radius * sina;
    }

    pVarTP.x += pAmount * xout;
    pVarTP.y += pAmount * yout;

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
    return new Object[]{presetId, npoints, filled, a, b, c, d, e, f, g,
            h, i, j, k, l, m, n, o, p};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESETID.equalsIgnoreCase(pName)) {
      presetId = limitIntVal(Tools.FTOI(pValue), 0, nMax - 1);
    } else if (PARAM_NPOINTS.equalsIgnoreCase(pName))
      npoints = (int) pValue;
    else if (PARAM_FILLED.equalsIgnoreCase(pName))
      filled = (int) pValue;
    else if (PARAM_A.equalsIgnoreCase(pName)) {
      a = pValue;
    } else if (PARAM_B.equalsIgnoreCase(pName)) {
      b = pValue;
    } else if (PARAM_C.equalsIgnoreCase(pName)) {
      c = pValue;
    } else if (PARAM_D.equalsIgnoreCase(pName)) {
      d = pValue;
    } else if (PARAM_E.equalsIgnoreCase(pName)) {
      e = pValue;
    } else if (PARAM_F.equalsIgnoreCase(pName)) {
      f = pValue;
    } else if (PARAM_G.equalsIgnoreCase(pName)) {
      g = pValue;
    } else if (PARAM_H.equalsIgnoreCase(pName)) {
      h = pValue;
    } else if (PARAM_I.equalsIgnoreCase(pName)) {
      i = pValue;
    } else if (PARAM_J.equalsIgnoreCase(pName)) {
      j = pValue;
    } else if (PARAM_K.equalsIgnoreCase(pName)) {
      k = pValue;
    } else if (PARAM_L.equalsIgnoreCase(pName)) {
      l = pValue;
    } else if (PARAM_M.equalsIgnoreCase(pName)) {
      m = pValue;
    } else if (PARAM_N.equalsIgnoreCase(pName)) {
      n = pValue;
    } else if (PARAM_O.equalsIgnoreCase(pName)) {
      o = pValue;
    } else if (PARAM_P.equalsIgnoreCase(pName)) {
      p = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hamid_js";
  }
  
  @Override
  public void randomize() {
    super.randomize();
    presetId = (int) (Math.random() * nMax);
    npoints = (int) (Math.random() * 5000 + 500);
    filled = (int) (Math.random() * 2);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE};
  }

}
