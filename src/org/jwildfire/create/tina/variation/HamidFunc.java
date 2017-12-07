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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

import java.lang.reflect.Method;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
/**

 * @date October 17, 2017
 * draw_js Variation
 * draw vectors in format 0,x,y,1,x,y,1,x,y..... (0=moveTo, 1=lineTo)
 * Variation params:
 * Level:  1,2,3,4,5
 * Line_thickness : 1..100
 */
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class HamidFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_PRESETID = "presetId";
  private static final String PARAM_NPOINTS = "number_lines/circles";
  private static final String PARAM_FILLED = "filled circles";

  private static final String[] paramNames = { PARAM_PRESETID,
      PARAM_NPOINTS, PARAM_FILLED };

  private static String[] methods = { "hamid_line_1", "hamid_line_2", "hamid_line_3", "hamid_line_4", "hamid_line_5", "hamid_line_6",
      "hamid_line_7", "hamid_line_8", "hamid_line_9", "hamid_line_10", "hamid_circle_10",
      "hamid_circle_11", "hamid_circle_12", "hamid_circle_13", "hamid_circle_14", "hamid_circle_15", "hamid_circle_16",
      "hamid_circle_17", "hamid_circle_18", "hamid_circle_19" };

  private static int nMax = methods.length;

  //  private  DynamicArray2D xyPoints = new DynamicArray2D(1000); 

  private int presetId = 0;
  private int npoints = 2000;
  private int filled = 1;

  static class DoublePoint2D {
    public double x;
    public double y;

    public DoublePoint2D(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  static class DoubleLine2D {
    double x1;
    double y1;
    double x2;
    double y2;

    public DoubleLine2D(double x1, double y1, double x2, double y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }
  }

  static class DoubleCircle2D {
    double x;
    double y;
    double r;

    public DoubleCircle2D(double x, double y, double r) {
      this.x = x;
      this.y = y;
      this.r = r;
    }

  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    Flame flame = pContext.getFlameRenderer().getFlame();
    flame.setAntialiasAmount(0.0);
    //  flame.setPixelsPerUnit(200);

  }

  private DoublePoint2D pCircle(FlameTransformationContext pContext, double x, double y, double radius) {
    if (filled == 1) {
      if (pContext.random() > 0.05)
        radius = radius * pContext.random();
    }
    double xout = 0, yout = 0;
    double a = pContext.random() * 2 * M_PI;
    double sina = sin(a);
    double cosa = cos(a);

    xout = x + radius * cosa;
    yout = y + radius * sina;

    DoublePoint2D value = new DoublePoint2D(xout, yout);

    return value;
  }

  public DoublePoint2D pLine(FlameTransformationContext pContext, double x1, double y1, double x2, double y2) {
    double ydiff = y2 - y1;
    double xdiff = x2 - x1;
    double m;
    if (xdiff == 0)
      m = 10000;
    else
      m = ydiff / xdiff; // slope
    double line_length = MathLib.sqrt((xdiff * xdiff) + (ydiff * ydiff));
    double xout = 0, yout = 0;
    double xoffset = 0, yoffset = 0;

    // draw point at a random distance along line
    //    (along straight line connecting endpoint1 and endpoint2)
    double d = pContext.random() * line_length;
    // x = x1 [+-] (d / (sqrt(1 + m^2)))
    // y = y1 [+-] (m * d / (sqrt(1 + m^2)))
    // determine sign based on orientation of endpoint2 relative to endpoint1
    xoffset = d / MathLib.sqrt(1 + m * m);
    if (x2 < x1) {
      xoffset = -1 * xoffset;
    }
    yoffset = Math.abs(m * xoffset);
    if (y2 < y1) {
      yoffset = -1 * yoffset;
    }

    xout = x1 + xoffset;
    yout = y1 + yoffset;

    DoublePoint2D value = new DoublePoint2D(xout, yout);

    return value;
  }

  public DoubleLine2D hamid_line_1(Integer I) {
    double x1 = -MathLib.sin(4. * MathLib.M_PI * Integer.valueOf(I) / npoints);
    double y1 = -MathLib.cos(2. * MathLib.M_PI * I / npoints);
    double x2 = -0.5 * MathLib.sin(8. * MathLib.M_PI * I / npoints);
    double y2 = -0.5 * MathLib.cos(4. * MathLib.M_PI * I / npoints);
    return new DoubleLine2D(x1, y1, x2, y2);
  }

  public DoubleLine2D hamid_line_2(Integer I) {
    double x1 = -MathLib.sin(2. * MathLib.M_PI * Integer.valueOf(I) / npoints);
    double y1 = -MathLib.cos(2. * MathLib.M_PI * I / npoints);
    double x2 = -0.5 * MathLib.sin(8. * MathLib.M_PI * I / npoints);
    double y2 = -0.5 * MathLib.cos(12. * MathLib.M_PI * I / npoints);
    return new DoubleLine2D(x1, y1, x2, y2);
  }

  public DoubleLine2D hamid_line_3(Integer I) {
    double x1 = -MathLib.sin(8. * MathLib.M_PI * Integer.valueOf(I) / npoints);
    double y1 = -MathLib.cos(2. * MathLib.M_PI * I / npoints);
    double x2 = -0.5 * MathLib.sin(6. * MathLib.M_PI * I / npoints);
    double y2 = -0.5 * MathLib.cos(2. * MathLib.M_PI * I / npoints);
    return new DoubleLine2D(x1, y1, x2, y2);
  }

  public DoubleLine2D hamid_line_4(Integer I) {
    double x1 = -MathLib.sin(10. * MathLib.M_PI * Integer.valueOf(I) / npoints);
    double y1 = -MathLib.cos(2. * MathLib.M_PI * I / npoints);
    double x2 = -0.5 * MathLib.sin(12. * MathLib.M_PI * I / npoints);
    double y2 = -0.5 * MathLib.cos(2. * MathLib.M_PI * I / npoints);
    return new DoubleLine2D(x1, y1, x2, y2);
  }

  public DoubleLine2D hamid_line_5(Integer I) {
    double x1 = MathLib.sin(10. * MathLib.M_PI * (Integer.valueOf(I) + 699) / npoints);
    double y1 = MathLib.cos(8. * MathLib.M_PI * (I + 699) / npoints);
    double x2 = MathLib.sin(12. * MathLib.M_PI * (I + 699) / npoints);
    double y2 = MathLib.cos(10. * MathLib.M_PI * (I + 699) / npoints);
    return new DoubleLine2D(x1, y1, x2, y2);
  }

  public DoubleLine2D hamid_line_6(Integer I) {
    double x1 = -2 * MathLib.cos(4. * MathLib.M_PI * I / 1000);
    double y1 = 0.5 * MathLib.pow(MathLib.cos(6.0 * MathLib.M_PI * I / npoints), 3);

    double x2 = -2 * MathLib.sin(6. * MathLib.M_PI * I / npoints) / 15.0;
    double y2 = (4.0 * MathLib.sin(2.0 * MathLib.M_PI * I / npoints) / 15.0);
    return new DoubleLine2D(x1, y1, x2, y2);
  }

  public DoubleLine2D hamid_line_7(Integer I) {
    double x1 = 3.0 * MathLib.pow(MathLib.sin(2. * MathLib.M_PI * Integer.valueOf(I) / npoints), 3);
    double y1 = -MathLib.cos(8. * MathLib.M_PI * I / npoints);
    double x2 = 1.5 * MathLib.pow(MathLib.sin(2. * MathLib.M_PI * I / npoints), 3);
    double y2 = -0.5 * MathLib.cos(6. * MathLib.M_PI * I / npoints);
    return new DoubleLine2D(x1, y1, x2, y2);
  }

  public DoubleLine2D hamid_line_8(Integer I) {
    double x1 = MathLib.cos(6. * MathLib.M_PI * Integer.valueOf(I) / npoints);
    double y1 = -MathLib.cos(8. * MathLib.M_PI * I / npoints);
    double x2 = 1.5 * MathLib.pow(MathLib.sin(2. * MathLib.M_PI * I / npoints), 3);
    double y2 = -0.5 * MathLib.cos(6. * MathLib.M_PI * I / npoints);
    return new DoubleLine2D(x1, y1, x2, y2);
  }

  public DoubleLine2D hamid_line_9(Integer I) {
    double x1 = MathLib.pow(MathLib.sin(20. * MathLib.M_PI * Integer.valueOf(I) / npoints), 3);
    double y1 = MathLib.pow(MathLib.cos(18. * MathLib.M_PI * I / npoints), 3);

    double x2 = MathLib.pow(MathLib.sin(16. * MathLib.M_PI * Integer.valueOf(I) / npoints), 3);
    double y2 = MathLib.pow(MathLib.cos(14. * MathLib.M_PI * I / npoints), 3);
    return new DoubleLine2D(x1, y1, x2, y2);
  }

  public DoublePoint2D A(int n) {
    double x1 = 0, y1 = 0;

    do {
      if (n < 100) {
        x1 = n / 100.0;
        y1 = 0.0;
        break;
      }
      else if (n < 200) {
        x1 = 1.0;
        y1 = (n - 100.) / 100.0;
        break;
      }
      else if (n < 300) {
        x1 = (300 - n) / 100.0;
        y1 = 1.0;
        break;
      }
      else if (n < 400) {
        x1 = 0.0;
        y1 = (400 - n) / 100.0;
        break;
      }
      else
        n = n - 400;
    }
    while (true);

    return new DoublePoint2D(x1, y1);

  }

  public DoubleLine2D hamid_line_10(Integer I) {

    DoublePoint2D out1 = A(I);
    DoublePoint2D out2 = A(I + 199);
    return new DoubleLine2D(out1.x, out1.y, out2.x, out2.y);
  }

  public DoubleCircle2D hamid_circle_10(Integer I) {
    double x = 2.0 * MathLib.sin(2.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.sin(36.0 * MathLib.M_PI * I / npoints) * MathLib.cos(30.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.pow(MathLib.sin(14.0 * MathLib.M_PI * I / npoints), 3) / 3.0;
    double y = 2.0 * MathLib.cos(2.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.cos(36.0 * MathLib.M_PI * I / npoints) * MathLib.cos(30.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.pow(MathLib.cos(14.0 * MathLib.M_PI * I / npoints), 3) / 5.0;
    double radius = 1.0 / 150.0 + MathLib.pow(MathLib.sin(36. * MathLib.M_PI * I / npoints), 4) / 9.0;
    return new DoubleCircle2D(x, y, radius);
  }

  public DoubleCircle2D hamid_circle_11(Integer I) {
    double x = MathLib.sin(14.0 * MathLib.M_PI * I / npoints);
    double y = MathLib.cos(32.0 * MathLib.M_PI * I / npoints);
    double radius = MathLib.pow(MathLib.sin(20. * MathLib.M_PI * I / npoints), 2) / 3.0;
    return new DoubleCircle2D(x, y, radius);
  }

  public DoubleCircle2D hamid_circle_12(Integer I) {
    double x = 2.0 * MathLib.sin(6.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.sin(36.0 * MathLib.M_PI * I / npoints) * MathLib.cos(30.0 * MathLib.M_PI * I / npoints) / 4.0 +
        MathLib.pow(MathLib.sin(14.0 * MathLib.M_PI * I / npoints), 3) / 4.0;
    double y = 2.0 * MathLib.cos(6.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.cos(28.0 * MathLib.M_PI * I / npoints) * MathLib.cos(30.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.pow(MathLib.cos(14.0 * MathLib.M_PI * I / npoints), 3) / 5.0;
    double radius = 1.0 / 150.0 + MathLib.pow(MathLib.sin(36. * MathLib.M_PI * I / npoints), 4) / 20.0;
    return new DoubleCircle2D(x, y, radius);
  }

  public DoubleCircle2D hamid_circle_13(Integer I) {
    double x = MathLib.sin(10.0 * MathLib.M_PI * I / npoints);
    double y = MathLib.pow(MathLib.cos(28.0 * MathLib.M_PI * I / npoints), 3);
    double radius = MathLib.pow(MathLib.sin(18. * MathLib.M_PI * I / npoints), 2) / 3.0;
    return new DoubleCircle2D(x, y, radius);
  }

  public DoubleCircle2D hamid_circle_14(Integer I) {
    double x = 2.0 * MathLib.sin(18.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.pow(MathLib.sin(40.0 * MathLib.M_PI * I / npoints), 3) * MathLib.cos(30.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.pow(MathLib.sin(34.0 * MathLib.M_PI * I / npoints), 3) / 4.0;
    double y = 2.0 * MathLib.cos(18.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.pow(MathLib.cos(28.0 * MathLib.M_PI * I / npoints), 3) * MathLib.cos(40.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.pow(MathLib.cos(34.0 * MathLib.M_PI * I / npoints), 3) / 3.0;
    double radius = 1.0 / 150.0 + MathLib.pow(MathLib.sin(56. * MathLib.M_PI * I / npoints), 4) / 8.0;
    return new DoubleCircle2D(x, y, radius);
  }

  public DoubleCircle2D hamid_circle_15(Integer I) {
    double x = 2.0 * MathLib.sin(14.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.pow(MathLib.sin(10.0 * MathLib.M_PI * I / npoints), 3) * MathLib.cos(28.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.pow(MathLib.sin(46.0 * MathLib.M_PI * I / npoints), 3) / 3.0;
    double y = 2.0 * MathLib.cos(14.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.pow(MathLib.cos(10.0 * MathLib.M_PI * I / npoints), 3) * MathLib.cos(28.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.pow(MathLib.cos(34.0 * MathLib.M_PI * I / npoints), 3) / 3.0;
    double radius = 1.0 / 150.0 + MathLib.pow(MathLib.sin(72. * MathLib.M_PI * I / npoints), 6) / 9.0 + MathLib.pow(MathLib.sin(12.0 * MathLib.M_PI * I / npoints), 4) / 20.0;
    return new DoubleCircle2D(x, y, radius);
  }

  public DoubleCircle2D hamid_circle_16(Integer I) {
    double x = MathLib.sin(38.0 * MathLib.M_PI * I / npoints) * MathLib.cos(26.0 * MathLib.M_PI * I / npoints) / 2.0 + MathLib.pow(MathLib.sin(18.0 * MathLib.M_PI * I / npoints), 3) / 2.0;
    double y = 2.0 * MathLib.cos(18.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.cos(38.0 * MathLib.M_PI * I / npoints) * MathLib.cos(36.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.pow(MathLib.cos(18.0 * MathLib.M_PI * I / npoints), 3) / 4.0;
    double radius = 1.0 / 150.0 + MathLib.pow(MathLib.sin(18. * MathLib.M_PI * I / npoints), 10) * MathLib.pow(MathLib.sin(100.0 * MathLib.M_PI * I / npoints), 6) / 100.0 +
        MathLib.pow(MathLib.sin(72.0 * MathLib.M_PI * I / npoints), 8) * MathLib.pow(MathLib.cos(18.0 * MathLib.M_PI * I / npoints), 6) / 20.0;
    return new DoubleCircle2D(x, y, radius);
  }

  public DoubleCircle2D hamid_circle_17(Integer I) {
    double x = 2.0 * MathLib.sin(10.0 * MathLib.M_PI * I / npoints) + MathLib.pow(MathLib.sin(6.0 * MathLib.M_PI * I / npoints), 3) * MathLib.cos(28.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.sin(126.0 * MathLib.M_PI * I / npoints) / 10.0;
    double y = 2.0 * MathLib.cos(6.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.pow(MathLib.cos(18.0 * MathLib.M_PI * I / npoints), 3) * MathLib.cos(36.0 * MathLib.M_PI * I / npoints) / 3.0 +
        MathLib.cos(126.0 * MathLib.M_PI * I / npoints) / 10.0;
    double radius = 1.0 / 150.0 + MathLib.pow(MathLib.sin(48. * MathLib.M_PI * I / npoints), 4) * (1.0 + MathLib.pow(MathLib.sin(12.0 * MathLib.M_PI * I / npoints), 2)) / 18.0;

    return new DoubleCircle2D(x, y, radius);
  }

  public DoubleCircle2D hamid_circle_18(Integer I) {
    double x = 2.0 * MathLib.sin(18.0 * MathLib.M_PI * I / npoints) / 5.0 + MathLib.pow(MathLib.sin(34.0 * MathLib.M_PI * I / npoints), 3) / 3.0 +
        MathLib.pow(MathLib.sin(50.0 * MathLib.M_PI * I / npoints), 3) / 3.0;
    double y = 2.0 * MathLib.cos(18.0 * MathLib.M_PI * I / npoints) / 3.0 + MathLib.pow(MathLib.cos(34.0 * MathLib.M_PI * I / npoints), 3) / 3.0 +
        MathLib.pow(MathLib.cos(50.0 * MathLib.M_PI * I / npoints), 3) / 10.0;
    double radius = 1.0 / 150.0 + MathLib.pow(MathLib.sin(58. * MathLib.M_PI * I / npoints), 4) / 10.0;

    return new DoubleCircle2D(x, y, radius);
  }

  public DoubleCircle2D hamid_circle_19(Integer I) {
    double x = MathLib.cos(10.0 * MathLib.M_PI * I / npoints) * (1.0 - MathLib.pow(MathLib.sin(16.0 * MathLib.M_PI * I / npoints), 2) / 2.0);
    double y = MathLib.sin(10.0 * MathLib.M_PI * I / npoints) * (1.0 - MathLib.pow(MathLib.cos(34.0 * MathLib.M_PI * I / npoints), 2) / 2.0);
    double radius = 1.0 / 200.0 + MathLib.pow(MathLib.sin(52. * MathLib.M_PI * I / npoints), 4) / 10.0;

    return new DoubleCircle2D(x, y, radius);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    DoublePoint2D out = null;
    DoubleLine2D line = null;

    if (presetId > nMax)
      presetId = nMax;

    String peekmethod = methods[presetId];

    try {
      Method m = this.getClass().getDeclaredMethod(peekmethod, new Class[] { Integer.class });
      if (presetId < 10) {
        line = (DoubleLine2D) m.invoke(this, new Object[] { (int) (pContext.random() * npoints + 1) });
        out = pLine(pContext, line.x1, line.y1, line.x2, line.y2);
      }
      else {
        DoubleCircle2D circle = (DoubleCircle2D) m.invoke(this, new Object[] { (int) (pContext.random() * npoints + 1) });
        out = pCircle(pContext, circle.x, circle.y, circle.r);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    pVarTP.x += pAmount * out.x;
    pVarTP.y += pAmount * out.y;

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
    return new Object[] { presetId, npoints, filled };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESETID.equalsIgnoreCase(pName)) {
      presetId = limitIntVal(Tools.FTOI(pValue), 0, nMax);
    }
    else if (PARAM_NPOINTS.equalsIgnoreCase(pName))
      npoints = (int) pValue;
    else if (PARAM_FILLED.equalsIgnoreCase(pName))
      filled = (int) pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hamid_js";
  }

}
