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

import java.awt.geom.Point2D;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import java.math.BigInteger;
import static org.jwildfire.base.mathlib.MathLib.M_2PI;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
* Maurer Rose variation by CozyG
* 
*/
public class MaurerRoseFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_KNUMER = "knumer";
  private static final String PARAM_KDENOM = "kdenom";
  private static final String PARAM_LINE_OFFSET_DEGREES = "line_offset_degrees";
  private static final String PARAM_LINE_COUNT = "line_count";
  private static final String PARAM_SHOW_CURVE = "show_curve";
  private static final String PARAM_SHOW_POINTS = "show_points";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_RADIAL_OFFSET = "radial_offset";
  private static final String PARAM_DIFF_MODE = "diff_mode";
  private static final String PARAM_RENDER_MODE = "render_mode";
  private static final String PARAM_CURVE_MODE = "curve_mode";
  
  private static final int CONNECTING_LINES = 0;
  private static final int RIGHT_LINES_CURRENT = 1;  
  private static final int RIGHT_LINES_NEXT = 2;  
  private static final int RIGHT_LINES_CENTERED = 3;
  private static final int CIRCLES = 4;
  
  
  private static final int CIRCLE = 0;
  private static final int RECTANGLE = 1;
  private static final int ELLIPSE = 2;
  private static final int RHODONEA = 3;
  private static final int EPITROCHOID = 4;
  private static final int HYPOTROCHOID = 5;
  private static final int LISSAJOUS = 6;
  
  private static final String[] paramNames = { 
    PARAM_KNUMER, PARAM_KDENOM, PARAM_LINE_OFFSET_DEGREES, PARAM_LINE_COUNT, 
    PARAM_CURVE_MODE, PARAM_RENDER_MODE, PARAM_SHOW_CURVE, PARAM_SHOW_POINTS, 
    PARAM_THICKNESS, PARAM_RADIAL_OFFSET, PARAM_DIFF_MODE };

  private double knumer = 2; // numerator of k in rose curve equations,   k = kn/kd
  private double kdenom = 1; // denominator of k in rose curve equations, k = kn/kd
  private double radial_offset = 0; // often called "c" in rose curve modifier equations
  private double cycle_offset = 0; // radians to offset cycle for incoming points
  private double thickness = 0; // amount to thicken curve by randomizing input

  private double kn, kd;
  private double k; // k = kn/kd
  private double cycles; // 1 cycle = 2*PI
  private double line_count = 360;
  private double line_offset_degrees = 71;
  private double step_size_radians;
  private double show_curve = 0.01;
  private double show_points = 0.5;
  private boolean diff_mode = false;
  private int render_mode = CONNECTING_LINES;
  private int curve_mode = RHODONEA;
  
  class DoublePoint2D {
    public double x;
    public double y;
  }
  private DoublePoint2D curve_point = new DoublePoint2D(); 

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
    step_size_radians = M_2PI * (line_offset_degrees / 360);
    cycles = (line_count * step_size_radians) / M_2PI;
  }
  
  /* 
  *  reuses object variable curve_point
  */
  public DoublePoint2D getCurveCoords(double theta) {
    if (curve_mode == CIRCLE) {
      double r = kn;
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
    }
    else if (curve_mode == RECTANGLE) {
    }
    else if (curve_mode == ELLIPSE) {
    }
    else if (curve_mode == RHODONEA) {
      double r = cos(k * theta) + radial_offset;
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
      
    }
    else if (curve_mode == EPITROCHOID) {
      //  double x = ((a_radius + b_radius) * cos(theta)) - (c_radius * cos(((a_radius + b_radius)/b_radius) * theta));
      //  double y = ((a_radius + b_radius) * sin(theta)) - (c_radius * sin(((a_radius + b_radius)/b_radius) * theta));
      //  double r = sqrt(x*x + y*y);
      double x = ((kn + kd) * cos(theta)) - (radial_offset * cos(((kn + kd)/kd) * theta));
      double y = ((kn + kd) * sin(theta)) - (radial_offset * sin(((kn + kd)/kd) * theta));
      curve_point.x = x;
      curve_point.y = y;
    }
    else if (curve_mode == HYPOTROCHOID) {
      // double x = ((a_radius - b_radius) * cos(theta)) + (c_radius * cos(((a_radius - b_radius)/b_radius) * theta));
      // double y = ((a_radius - b_radius) * sin(theta)) - (c_radius * sin(((a_radius - b_radius)/b_radius) * theta));
      double x = ((kn - kd) * cos(theta)) + (radial_offset * cos(((kn - kd)/kd) * theta));
      double y = ((kn - kd) * sin(theta)) - (radial_offset * sin(((kn - kd)/kd) * theta));
      curve_point.x = x;
      curve_point.y = y;
    }
    else if (curve_mode == LISSAJOUS) {
      // x = A * sin(a*t + d)
      // y = B * sin(b*t);
      // for now keep A = B = 1
      double x = sin(kn*theta + radial_offset);
      double y = sin(kd*theta);
      curve_point.x = x;
      curve_point.y = y;
    }
    else {  // default to circle
      double r = kn;
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
    }
    return curve_point;
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
    double xin = pAffineTP.x;
    double yin = pAffineTP.y;
    
    // atan2 range is [-PI, PI], so tin covers 2PI, or 1 cycle (from -0.5 to 0.5 cycle)
    double tin = atan2(yin, xin); // polar coordinate angle (theta in radians) of incoming point
    double t = cycles * (tin + (cycle_offset * 2 * M_PI)); // angle of rose curve
    
    // double r = cos(k * t) + radial_offset;  
    DoublePoint2D p = getCurveCoords(t);

    double x = p.x;
    double y = p.y;
    double r = sqrt(x*x + y*y);
    double rinx, riny;
    double xout, yout, rout;
    
    if (pContext.random() < show_curve) {
      if (thickness != 0) {
        xout = x + ((pContext.random() - 0.5) * thickness);
        yout = y + ((pContext.random() - 0.5) * thickness);
      }
      else {
        xout = x;
        yout = y;
      }
    }
    
    else {
      // map to a Maurer Rose line
      // find nearest step
      double step_number = floor(t/step_size_radians);
      
      // find polar and cartesian coordinates for endpoints of Maure Rose line
      double theta1 = step_number * step_size_radians;
      double theta2 = theta1 + step_size_radians;
      // double radius1 = cos(k * theta1) + radial_offset;
      // double radius2 = cos(k * theta2) + radial_offset;
      DoublePoint2D p1 = getCurveCoords(theta1);
      double x1 = p1.x;
      double y1 = p1.y;
      DoublePoint2D p2 = getCurveCoords(theta2);
      double x2 = p2.x;     
      double y2 = p2.y;
      
      // find the slope and length of the line
      double ydiff = y2 - y1;
      double xdiff = x2 - x1;
      double m = ydiff / xdiff;  // slope
      double line_length = Math.sqrt( (xdiff * xdiff) + (ydiff * ydiff));

      // yoffset = [+-] m * d / (sqrt(1 + m^2))
      double xoffset=0, yoffset=0;
      if (render_mode == CONNECTING_LINES) {
        // xoffset = [+-] d / (sqrt(1 + m^2)) 
        // yoffset = [+-] m * d / (sqrt(1 + m^2))
        // distance along the line from p1
        // double d = ((t - theta1) / step_size_radians) * line_length;
        double d = Math.random() * line_length;
        // x = x1 [+-] (d / (sqrt(1 + m^2))) 
        // y = y1 [+-] (m * d / (sqrt(1 + m^2)))
        xoffset = d / Math.sqrt(1 + m*m);
        if (x2 < x1) { xoffset = -1 * xoffset; }  // determine sign based on p2
        yoffset = Math.abs(m * xoffset);
        if (y2 < y1) { yoffset = -1 * yoffset; }
      }
      else if (render_mode == RIGHT_LINES_CENTERED) {
        double midlength = line_length/2;  // use midlength as midpoint of right line
        double xmid = midlength / Math.sqrt(1 + m*m);
        if (x2 < x1) { xmid = -1 * xmid; }  // determine sign based on p2
        double ymid = Math.abs(m * xmid);
        if (y2 < y1) { ymid = -1 * ymid; }
        // double pslope = -1 / m;
        m = -1/m;
        double d = Math.random() * line_length;
        xoffset = d / Math.sqrt(1 + m*m);
        if (x2 < x1) { xoffset = -1 * xoffset; }  // determine sign based on p2
        yoffset = Math.abs(m * xoffset);
        if (y2 < y1) { yoffset = -1 * yoffset; }
      }
      else if (render_mode == CIRCLES) {
        double midlength = line_length/2;  // use midlength of Maurer line as radius
        double xmid = midlength / Math.sqrt(1 + m*m);
        if (x2 < x1) { xmid = -1 * xmid; }  
        double ymid = Math.abs(m * xmid);
        if (y2 < y1) { ymid = -1 * ymid; }
        double ang = Math.random() * M_2PI;
        xoffset = xmid + (midlength * sin(ang));
        yoffset = ymid + (midlength * cos(ang));
      }

      double rnd1 = pContext.random();
      if (show_points > 0 && rnd1 < show_points) {
        double roffset = pContext.random() * thickness * 2;
        double rangle = (pContext.random() * M_2PI);
        xoffset = roffset * cos(rangle);
        yoffset = roffset * sin(rangle);
        if (rnd1 < (show_points/2)) {
          xout = x1 + xoffset;
          yout = y1 + yoffset;
        }
        else { // if (rnd1 < show_points) {
          xout = x2 + xoffset;
          yout = y2 + yoffset;
        }
      }
      else {
        if (thickness > 0) {
          xoffset += ((pContext.random() - 0.5) * thickness);
          yoffset += ((pContext.random() - 0.5) * thickness);
        }
        xout = x1 + xoffset;
        yout = y1 + yoffset;
      }

    }

    // Add final values in to variations totals
    if (diff_mode) {
      pVarTP.x = pAffineTP.x + (pAmount * (xout - pAffineTP.x));
      pVarTP.y = pAffineTP.y + (pAmount * (yout - pAffineTP.y));
    }
    else {
      pVarTP.x += pAmount * xout;
      pVarTP.y += pAmount * yout;
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
    return new Object[] { knumer, kdenom, line_offset_degrees, line_count, curve_mode, render_mode, show_curve, show_points, 
                          thickness, radial_offset, (diff_mode ? 1 : 0)  };

  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_KNUMER.equalsIgnoreCase(pName))
      knumer = pValue;
    else if (PARAM_KDENOM.equalsIgnoreCase(pName))
      kdenom = pValue;
    else if (PARAM_LINE_OFFSET_DEGREES.equalsIgnoreCase(pName))
      line_offset_degrees = pValue;
    else if (PARAM_LINE_COUNT.equalsIgnoreCase(pName))
      line_count = pValue;
    else if (PARAM_CURVE_MODE.equalsIgnoreCase(pName))
      curve_mode = (int)pValue;
    else if (PARAM_RENDER_MODE.equalsIgnoreCase(pName))
      render_mode = (int)pValue;
    else if (PARAM_SHOW_CURVE.equalsIgnoreCase(pName))
      show_curve = pValue;
    else if (PARAM_SHOW_POINTS.equalsIgnoreCase(pName))
      show_points = pValue;    
    else if (PARAM_RADIAL_OFFSET.equalsIgnoreCase(pName))
      radial_offset = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName) || pName.equalsIgnoreCase("fill"))
      thickness = pValue;
    else if (PARAM_DIFF_MODE.equalsIgnoreCase(pName) || pName.equalsIgnoreCase("diff mode")) {
      diff_mode = (pValue >= 1);
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "maurer_rose";
  }

}
