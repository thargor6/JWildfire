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

/**
 * Maurer Rose variation by CozyG
 * Copyright 2016- Gregg Helt
 * (released under same GNU Lesser General Public License as above)
 * Based on the "Maurer Rose", as described by Peter Maurer,
 * for more information on Maurer Roses see https://en.wikipedia.org/wiki/Maurer_rose
 * For a superset of this with many more features (and much more complexity), see the "maurer_lines" variation
 */
public class MaurerRoseFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_KN = "kn";
  private static final String PARAM_KD = "kd";
  private static final String PARAM_C = "c";
  private static final String PARAM_LINE_OFFSET_DEGREES = "line_offset_degrees";
  private static final String PARAM_LINE_COUNT = "line_count";
  private static final String PARAM_SHOW_LINES = "show_lines";
  private static final String PARAM_SHOW_POINTS = "show_points";
  private static final String PARAM_SHOW_CURVE = "show_curve";
  private static final String PARAM_LINE_THICKNESS = "line_thickness";
  private static final String PARAM_POINT_THICKNESS = "point_thickness";
  private static final String PARAM_CURVE_THICKNESS = "curve_thickness";

  private static final String[] paramNames = {
          PARAM_KN, PARAM_KD, PARAM_C,
          PARAM_LINE_OFFSET_DEGREES, PARAM_LINE_COUNT,
          PARAM_SHOW_LINES, PARAM_SHOW_POINTS, PARAM_SHOW_CURVE,
          PARAM_LINE_THICKNESS, PARAM_POINT_THICKNESS, PARAM_CURVE_THICKNESS};

  private double kn = 2; // numerator of k in rhodonea curve equation,   k = kn/kd
  private double kd = 1; // denominator of k in rhodonea curve equation, k = kn/kd
  private double k;     // for rhodonea equation, r = cos(k * theta)
  private double c = 0; // offset for extended rhodonea curve equation, r = cos(k * theta) + c


  private double cycles; // 1 cycle = 2*PI
  private double line_count = 360;
  private double line_offset_degrees = 71;
  private double step_size_radians;

  private double show_lines_param = 1;
  private double show_points_param = 0;
  private double show_curve_param = 0.05;
  private double line_fraction, point_fraction, curve_fraction;
  private double line_threshold, point_threshold, point_half_threshold;

  private double line_thickness_param = 0.5;
  private double point_thickness_param = 3.0;
  private double curve_thickness_param = 1.0;
  private double line_thickness, point_thickness, curve_thickness;

  class DoublePoint2D {
    public double x;
    public double y;
  }

  private DoublePoint2D curve_point = new DoublePoint2D();
  private DoublePoint2D endpoint1 = new DoublePoint2D();
  private DoublePoint2D endpoint2 = new DoublePoint2D();

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // rhodonea init   
    k = kn / kd;
    step_size_radians = M_2PI * (line_offset_degrees / 360);
    cycles = (line_count * step_size_radians) / M_2PI;

    // line/curve/point rendering init
    double show_sum = show_lines_param + show_points_param + show_curve_param;
    line_fraction = show_lines_param / show_sum;
    point_fraction = show_points_param / show_sum;
    curve_fraction = show_curve_param / show_sum;
    line_threshold = line_fraction;
    point_threshold = line_fraction + point_fraction;
    point_half_threshold = line_fraction + (point_fraction / 2);
    line_thickness = line_thickness_param / 100;
    point_thickness = point_thickness_param / 100;
    curve_thickness = curve_thickness_param / 100;

  }

  /*
   * Rose/Rhodonea curve equation (in polar coordinates):
   *   k = kn/kd
   *   r = cos(k * theta) + c
   * returned as a Cartesian X/Y point in result param
   */
  public void getCurveCoords(double theta, DoublePoint2D result) {
    double r = cos(k * theta) + c;
    result.x = r * cos(theta);
    result.y = r * sin(theta);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double tin = atan2(pAffineTP.y, pAffineTP.x); // polar coordinate angle (theta in radians) of incoming point
    double t = cycles * tin; // angle of rose curve

    // map to a Maurer Rose line
    // find nearest step
    double step_number = floor(t / step_size_radians);

    // find coordinates for endpoints (on rhodonea curve) for nearest Maurer Rose line
    double theta1 = step_number * step_size_radians;
    double theta2 = theta1 + step_size_radians;
    getCurveCoords(theta1, endpoint1);
    double x1 = endpoint1.x;
    double y1 = endpoint1.y;
    getCurveCoords(theta2, endpoint2);
    double x2 = endpoint2.x;
    double y2 = endpoint2.y;

    // find the slope and length of the line
    double ydiff = y2 - y1;
    double xdiff = x2 - x1;
    double m = ydiff / xdiff;  // slope
    double line_length = Math.sqrt((xdiff * xdiff) + (ydiff * ydiff));

    double xout, yout;
    double xoffset = 0, yoffset = 0;
    double rnd = pContext.random();
    // rnd determines whether to draw line, point, or curve
    if (rnd < line_threshold) {
      // draw point at a random distance along nearest line of Maurer Rose
      //    (along straight line connecting endpoint1 and endpoint2)
      double d = Math.random() * line_length;
      // x = x1 [+-] (d / (sqrt(1 + m^2)))
      // y = y1 [+-] (m * d / (sqrt(1 + m^2)))
      // determine sign based on orientation of endpoint2 relative to endpoint1
      xoffset = d / Math.sqrt(1 + m * m);
      if (x2 < x1) {
        xoffset = -1 * xoffset;
      }
      yoffset = Math.abs(m * xoffset);
      if (y2 < y1) {
        yoffset = -1 * yoffset;
      }
      if (line_thickness != 0) {
        xoffset += ((pContext.random() - 0.5) * line_thickness);
        yoffset += ((pContext.random() - 0.5) * line_thickness);
      }
      xout = x1 + xoffset;
      yout = y1 + yoffset;
    } else if (rnd <= point_threshold) {
      // draw endpoints
      if (point_thickness != 0) {
        double roffset = pContext.random() * point_thickness;
        double rangle = (pContext.random() * M_2PI);
        xoffset = roffset * cos(rangle);
        yoffset = roffset * sin(rangle);
      } else {
        xoffset = 0;
        yoffset = 0;
      }
      if (rnd <= point_half_threshold) {
        xout = x1 + xoffset;
        yout = y1 + yoffset;
      } else {
        xout = x2 + xoffset;
        yout = y2 + yoffset;
      }
    } else {
      // draw rhodonea curve
      getCurveCoords(t, curve_point);
      double curvex = curve_point.x;
      double curvey = curve_point.y;
      if (curve_thickness != 0) {
        xout = curvex + ((pContext.random() - 0.5) * curve_thickness);
        yout = curvey + ((pContext.random() - 0.5) * curve_thickness);
      } else {
        xout = curvex;
        yout = curvey;
      }
    }

    // Add final values and scaling
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
    return new Object[]{kn, kd, c, line_offset_degrees, line_count, show_lines_param, show_points_param, show_curve_param,
            line_thickness_param, point_thickness_param, curve_thickness_param};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_KN.equalsIgnoreCase(pName))
      kn = pValue;
    else if (PARAM_KD.equalsIgnoreCase(pName))
      kd = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_LINE_OFFSET_DEGREES.equalsIgnoreCase(pName))
      line_offset_degrees = pValue;
    else if (PARAM_LINE_COUNT.equalsIgnoreCase(pName))
      line_count = pValue;
    else if (PARAM_SHOW_LINES.equalsIgnoreCase(pName))
      show_lines_param = pValue;
    else if (PARAM_SHOW_POINTS.equalsIgnoreCase(pName))
      show_points_param = pValue;
    else if (PARAM_SHOW_CURVE.equalsIgnoreCase(pName))
      show_curve_param = pValue;
    else if (PARAM_LINE_THICKNESS.equalsIgnoreCase(pName))
      line_thickness_param = pValue;
    else if (PARAM_POINT_THICKNESS.equalsIgnoreCase(pName))
      point_thickness_param = pValue;
    else if (PARAM_CURVE_THICKNESS.equalsIgnoreCase(pName))
      curve_thickness_param = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "maurer_rose";
  }

}
