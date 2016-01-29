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
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.sin;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
* Maurer Rose variation by CozyG
* 
*/
public class MaurerCirclesFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private boolean DEBUG = false;

  // PARAM_KNUMER ==> PARAM_A
  // PARAM_KDENOM ==> PARAM_B
  // PARAM_RADIAL_OFFSET ==> PARAM_C
  // PARAM_THICKNESS ==> split into PARAM_CURVE_THICKNESS, PARAM_LINE_THICKNESS, PARAM_POINT_THICKNESS
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
    private static final String PARAM_D = "d";
  private static final String PARAM_LINE_OFFSET_DEGREES = "line_offset_degrees";
  private static final String PARAM_LINE_COUNT = "line_count";
  private static final String PARAM_SHOW_LINES = "show_lines";
  private static final String PARAM_SHOW_CIRCLES = "show_circles";
  private static final String PARAM_SHOW_POINTS = "show_points";
  private static final String PARAM_SHOW_CURVE = "show_curve";
  
  private static final String PARAM_LINE_THICKNESS = "line_thickness";
  private static final String PARAM_CIRCLE_THICKNESS = "circle_thickness";
  private static final String PARAM_POINT_THICKNESS = "point_thickness";
  private static final String PARAM_CURVE_THICKNESS = "curve_thickness";
  
  private static final String PARAM_DIFF_MODE = "diff_mode";
  private static final String PARAM_CURVE_MODE = "curve_mode";
  private static final String PARAM_COLOR_MODE = "color_mode";
//  private static final String PARAM_COLOR_SCALING = "color_scaling";
  private static final String PARAM_COLOR_LOW_THRESH = "color_low_threshold";
  private static final String PARAM_COLOR_HIGH_THRESH = "color_high_threshold";
  private static final String PARAM_LINE_LOW_THRESH = "line_low_threshold";
  private static final String PARAM_LINE_HIGH_THRESH = "line_high_threshold";
  private static final String PARAM_ANGLE_LOW_THRESH = "angle_low_threshold";
  /* need to fix "angel" mis-spelling in previously generated flames */
  private static final String PARAM_ANGLE_HIGH_THRESH = "angel_high_threshold";
  private static final String PARAM_RELATIVE_ANGLE_LOW_THRESH = "relative_angle_low_threshold";
  private static final String PARAM_RELATIVE_ANGLE_HIGH_THRESH = "relative_angle_high_threshold";
  
  private static final String PARAM_LINE_VARIATION_FREQ = "line_variation_freq";
  private static final String PARAM_LINE_VARIATION_AMP = "line_variation_amp";

  private static final int CIRCLE = 0;
  private static final int RECTANGLE = 1;
  private static final int ELLIPSE = 2;
  private static final int RHODONEA = 3;
  private static final int EPITROCHOID = 4;
  private static final int HYPOTROCHOID = 5;
  private static final int LISSAJOUS = 6;
  private static final int EPISPIRAL = 7;
  private static final int SUPERSHAPE = 8;
  private static final int STARR_CURVE = 9;
  private static final int FARRIS_MYSTERY_CURVE = 10;
  private static final int WAGON_FANCIFUL_CURVE = 11;
  private static final int FAY_BUTTERFLY = 12;
  private static final int RIGGE1 = 13;
  private static final int RIGGE2 = 14;
  
  private static final int NORMAL = 0;
  private static final int RED = 1;
  private static final int GREEN = 2;
  private static final int BLUE = 3;
  private static final int LINE_LENGTH_RG = 4;
  private static final int LINE_LENGTH_RB = 5;
  private static final int LINE_LENGTH_BG = 6;
  private static final int LINE_ANGLE_RG = 7;
  private static final int LINE_ANGLE_RB = 8;
  private static final int LINE_ANGLE_BG = 9;
  private static final int LINE_ANGLE_RELATIVE_RG = 10;
  private static final int LINE_ANGLE_RELATIVE_RB = 11;
  private static final int LINE_ANGLE_RELATIVE_BG = 12;
  
  private static final String[] paramNames = { 
    PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_LINE_OFFSET_DEGREES, PARAM_LINE_COUNT, PARAM_CURVE_MODE, 
    PARAM_SHOW_LINES, PARAM_SHOW_CIRCLES, PARAM_SHOW_POINTS, PARAM_SHOW_CURVE, 
    PARAM_LINE_THICKNESS, PARAM_CIRCLE_THICKNESS, PARAM_POINT_THICKNESS, PARAM_CURVE_THICKNESS, 
    PARAM_DIFF_MODE, 
    PARAM_COLOR_MODE, PARAM_COLOR_LOW_THRESH, PARAM_COLOR_HIGH_THRESH, 
    PARAM_LINE_LOW_THRESH, PARAM_LINE_HIGH_THRESH, PARAM_ANGLE_LOW_THRESH, PARAM_ANGLE_HIGH_THRESH, 
    PARAM_RELATIVE_ANGLE_LOW_THRESH, PARAM_RELATIVE_ANGLE_HIGH_THRESH, 
    PARAM_LINE_VARIATION_FREQ, PARAM_LINE_VARIATION_AMP
  };

  private double a = 2; // numerator of k in rose curve equations,   k = kn/kd
  private double b = 1; // denominator of k in rose curve equations, k = kn/kd
  private double c = 0; // often called "c" in rose curve modifier equations
  private double d = 0; // used for n3 in supershape equation

  // rhodonea vars
  private double kn, kd, k, radial_offset; // k = kn/kd
  // epitrochoid / hypotrochoid vars
  private double a_radius, b_radius, c_radius;
  
  private double cycles; // 1 cycle = 2*PI
  private double line_count = 360;
  private double line_offset_degrees = 71;
  private double step_size_radians;

  private double show_lines_param = 0;
  private double show_circles_param = 1;
  private double show_points_param = 0;
  private double show_curve_param = 0;
  private double line_fraction, circle_fraction, point_fraction, curve_fraction;
  private double line_threshold, circle_threshold, point_threshold, point_half_threshold;
  
  private double line_thickness_param = 0.3;
  private double circle_thickness_param = 0.3;
  private double point_thickness_param = 1;
  private double curve_thickness_param = 0.1;
  private double line_thickness, circle_thickness, point_thickness, curve_thickness;
  
  // private double show_curve = 0.01;
  // private double show_points = 0.5;
  private boolean diff_mode = false;
  private int curve_mode = RHODONEA;
  // private boolean direct_color = true;
  private int color_mode = NORMAL;
//  private double color_scaling = 100;
  private double color_low_thresh = 0.3;
  private double color_high_thresh = 2.0;
  private double line_low_thresh = 0;    // if != 0, hide lines with lengh < line_low_thresh
  private double line_high_thresh = 0;   // if != 0, hide lines with lenght > line_high_thresh
  private double angle_low_thresh = 0;   // if != 0, hide lines with angle < angle_low_thresh
  private double angle_high_thresh = 0;  // if != 0, hide lines with angle > angle_high_thresh
  private double rangle_low_thresh = 0;  // if != 0, hide lines with relative angle < rangle_low_thresh
  private double rangle_high_thresh = 0; // if != 0, hide lines with relative angle > rangle_high_thresh
  
  private double line_variation_freq = 0; // if != 0, determines frequency of variation sine wave (as 
  private double line_variation_amp = 0;
  
  private long count = 0;
  
  class DoublePoint2D {
    public double x;
    public double y;
  }
  private DoublePoint2D curve_point = new DoublePoint2D(); 
  
  // for rhodonea:
  //     a = knumer
  //     b = kdenom
  //     c = radial_offset
  // for epitrochoid / hypotrochoid
  //     option 1: 
  //       a = radius
  //       b = cusps
  //       c = cusp_size
  //       cusp_divisor = 1
  //     option 2: 
  //       a = a_radius
  //       b = b_radius
  //       c = c_radius        
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    
    // rhodonea init
    kn = a;
    kd = b;
    k = kn / kd;
    radial_offset = c;
    
    // epitrochoid/hypotrochoid init
    double radius = a;
    double cusps = b;
    double cusp_size = c;
    double cusp_divisor = 1;
    double cusps_ratio = cusps/cusp_divisor;
    double c_scale = cusp_size + 1;
    
    if (curve_mode == EPITROCHOID) {
      b_radius = -1 * radius / (1 - cusps_ratio - c_scale);
      a_radius = b_radius * cusps_ratio;
      c_radius = b_radius * c_scale;
    }
    else if (curve_mode == HYPOTROCHOID) {
      b_radius = radius/(cusps_ratio + c_scale + 1)  ;
      a_radius = b_radius * cusps_ratio;
      c_radius = b_radius * c_scale;
    }
    
    double show_sum = show_lines_param + show_circles_param + show_points_param + show_curve_param;
    line_fraction = show_lines_param / show_sum;
    circle_fraction = show_circles_param / show_sum;
    point_fraction = show_points_param / show_sum;
    curve_fraction = show_curve_param / show_sum;
    line_threshold = line_fraction;
    circle_threshold = line_threshold + circle_fraction;
    point_threshold = circle_threshold + point_fraction;
    point_half_threshold = circle_threshold + (point_fraction/2);
    
    line_thickness = line_thickness_param / 100;
    circle_thickness = circle_thickness_param / 100;
    point_thickness = point_thickness_param / 100;
    curve_thickness = curve_thickness_param / 100;
    
    step_size_radians = M_2PI * (line_offset_degrees / 360);
    cycles = (line_count * step_size_radians) / M_2PI;

  }
  
  /* 
  *  reuses object variable curve_point
  */
  public DoublePoint2D getCurveCoords(double theta) {
    if (curve_mode == CIRCLE) {
      double r = a;
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
    }
    else if (curve_mode == RECTANGLE) {
      double t = theta % M_2PI;
      if (t < 0) { t = (0.5 - t); }

      if (t <= 0.25) {
        curve_point.x = a/2;
        // curve_point.y = (s * 4 * b) - b/2;  
        curve_point.y = (t * 4 * b) - b/2;  
      }
      else if (t <= 0.5) {
        curve_point.x = ((t-0.25) * 4 * a) - a/2;
        curve_point.y = b/2;
      }
      else if (t <= 0.75) {
        curve_point.x = -a/2;
        curve_point.y = ((t-0.5) * 4 * b) - b/2;
      }
      else {
        curve_point.x = ((t-0.75) * 4 * a) -a/2;
        curve_point.y = -b/2;
      }
    }
    else if (curve_mode == ELLIPSE) {
    }
    else if (curve_mode == RHODONEA) {
      double r = cos(k * theta) + c;
      if (DEBUG && count % 100000 == 0) { System.out.println("radius = " + r); }
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
    }
    else if (curve_mode == EPISPIRAL) {
      double r = (1/cos(k * theta)) + c;
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
    }
    else if (curve_mode == EPITROCHOID) {
      // option 1:
      // double x = ((a_radius + b_radius) * cos(theta)) - (c_radius * cos(((a_radius + b_radius)/b_radius) * theta));
      // double y = ((a_radius + b_radius) * sin(theta)) - (c_radius * sin(((a_radius + b_radius)/b_radius) * theta));
      // option 2:
      double x = ((a + b) * cos(theta)) - (c * cos(((a + b)/b) * theta));
      double y = ((a + b) * sin(theta)) - (c * sin(((a + b)/b) * theta));
      curve_point.x = x;
      curve_point.y = y;
    }
    else if (curve_mode == HYPOTROCHOID) {
      // option 1:
      // double x = ((a_radius - b_radius) * cos(theta)) + (c_radius * cos(((a_radius - b_radius)/b_radius) * theta));
      // double y = ((a_radius - b_radius) * sin(theta)) - (c_radius * sin(((a_radius - b_radius)/b_radius) * theta));
      // option 2: 
      double x = ((a - b) * cos(theta)) + (c * cos(((a - b)/b) * theta));
      double y = ((a - b) * sin(theta)) - (c * sin(((a - b)/b) * theta));
      curve_point.x = x;
      curve_point.y = y;
    }
    else if (curve_mode == LISSAJOUS) {
      // x = A * sin(a*t + d)
      // y = B * sin(b*t);
      // for now keep A = B = 1
      double x = sin((a*theta) + c);
      double y = sin(b*theta);
      curve_point.x = x;
      curve_point.y = y;
    }
    else if (curve_mode == SUPERSHAPE) {
      // original supershape variables: a, b, m, n1, n2, n3
      // a = m
      // b = n1
      // c = n2
      // d = n3
      double a1 = 1;
      double b1 = 1;
      double m = a;
      double n1 = b;
      double n2 = c;
      double n3 = d;

      double r = pow( 
                    (pow( fabs( (cos(m * theta / 4))/a1), n2) + 
                     pow( fabs( (sin(m * theta / 4))/b1), n3)), 
                    (-1/n1));
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
    }
    else if (curve_mode == STARR_CURVE) {
      double r = 2 + (sin(a * theta)/2);
      theta = theta + (sin(b * theta)/c); 
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
    }
    else if (curve_mode == FARRIS_MYSTERY_CURVE) {
      double t = theta;
      curve_point.x = cos(t)/a + cos(6*t)/b + sin(14*t)/c;
      curve_point.y = sin(t)/a + sin(6*t)/b + cos(14*t)/c;
      // can also be represented more concisely with complex numbers: 
      //   c(t) = (e^(i*t))/a + (e^(6*i*t))/b + (e^(-14*i*t))/c
      //   should break out into a fully parameterized version with exponent parameters as well 
    }
    else if (curve_mode == WAGON_FANCIFUL_CURVE) {
      curve_point.x = sin(a * theta) * cos(c * theta);
      curve_point.y = sin(b * theta) * sin(c * theta);
    }
    else if (curve_mode == FAY_BUTTERFLY) {
      // r = e^cos(t) - 2cos(4t) - sin^5(t/12)
      // y = sin(t)*r
      // x = cos(t)*r
      double t = theta;
      // double r = 0.5 * (exp(cos(t)) - (2 * cos(4 * t)) - pow(sin(t / 12), 5) + offset);
      double r = 0.5 * (exp(cos(t)) - (2 * cos(4 * t)) - pow(sin(t / 12), 5));
      curve_point.x = r * sin(t);
      curve_point.y = r * cos(t);
    }
    else if (curve_mode == RIGGE1) {
      double r = (1 - cos(a * theta)) + (1 - cos(a * b * theta));
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
    }
    else if (curve_mode == RIGGE2) {
      double r = (1 - cos(a * theta)) - (1 - cos(a * b * theta));
      curve_point.x = r * cos(theta);
      curve_point.y = r * sin(theta);
    }
    else {  // default to circle
      double r = a;
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
    count++;
    double xin = pAffineTP.x;
    double yin = pAffineTP.y;
    // atan2 range is [-PI, PI], so tin covers 2PI, or 1 cycle (from -0.5 to 0.5 cycle)
    double tin = atan2(yin, xin); // polar coordinate angle (theta in radians) of incoming point
    // double tin = (Math.random() * M_2PI) - M_PI;
    double t = cycles * tin; // angle of rose curve
    
    // double r = cos(k * t) + radial_offset;  
    DoublePoint2D p = getCurveCoords(t);

    double x = p.x;
    double y = p.y;
    double r = sqrt(x*x + y*y);
    double rinx, riny;
    double xout, yout, rout;
    
      // map to a Maurer Rose line
      // find nearest step
      double step_number = floor(t/step_size_radians);
      
      // find polar and cartesian coordinates for endpoints of Maure Rose line
      double theta1 = step_number * step_size_radians;
      double theta2;
      if (line_variation_freq == 0 || line_variation_amp == 0) {
        theta2 = theta1 + step_size_radians;
      }
      else {
        // theta2 = theta1 + step_size_radians + (line_variation_amp * (sin(M_2PI/line_variation_freq)));
        double varangle = line_variation_amp * sin((theta1 % M_2PI)/line_variation_freq);
        theta2 = theta1 + step_size_radians + varangle;
      }

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
      double a1 = atan2(y1, x1); 
      double a2 = atan2(y2, x2);
      if (a1 < 0) { a1 += M_2PI; }  // map to [0..2Pi]
      if (a2 < 0) { a2 += M_2PI; }  // map to [0..2Pi]
      // double adiff = abs(a2-a1);
      double adiff = abs(a2 - a1);
      if (adiff > M_PI) { adiff = M_2PI - adiff; }
      double line_angle = atan2(ydiff, xdiff);  // atan2 range is [-Pi..+Pi]
      // if (line_angle < 0) { line_angle += M_2PI; }   // map to range [0..+2Pi]
      // delta_from_yaxis should be 0 if parallel to y-axis, and M_PI/2 if parallel to x-axis
      double delta_from_yaxis = abs(abs(line_angle) - (M_PI/2.0));
      // scale to range [0..1]; (0 parallel to y-axis, 1 parallel to x-axis)
      delta_from_yaxis = delta_from_yaxis / (M_PI/2.0);
      double line_length = Math.sqrt( (xdiff * xdiff) + (ydiff * ydiff));
      
      if (line_low_thresh != 0 && line_length < line_low_thresh) {
        pVarTP.doHide = true;
      }
      if (line_high_thresh != 0 && line_length > line_high_thresh) {
        pVarTP.doHide = true;
      }
      
      if (angle_low_thresh != 0 && delta_from_yaxis < angle_low_thresh) {
        pVarTP.doHide = true;
      }
      if (angle_high_thresh != 0 && delta_from_yaxis > angle_high_thresh) {
        pVarTP.doHide = true;
      }
      
      if (rangle_low_thresh != 0 && adiff < rangle_low_thresh) {
        pVarTP.doHide = true;
      }
      if (rangle_high_thresh != 0 && adiff > rangle_high_thresh) {
        pVarTP.doHide = true;
      }

      // yoffset = [+-] m * d / (sqrt(1 + m^2))
      double xoffset=0, yoffset=0;

      double rnd = pContext.random();
      if (rnd < line_threshold) {
        // draw lines
        double d = Math.random() * line_length;
        xoffset = d / Math.sqrt(1 + m*m);
        if (x2 < x1) { xoffset = -1 * xoffset; }  // determine sign based on p2
        yoffset = Math.abs(m * xoffset);
        if (y2 < y1) { yoffset = -1 * yoffset; }
                if (line_thickness != 0) {
          xoffset += ((pContext.random() - 0.5) * line_thickness);
          yoffset += ((pContext.random() - 0.5) * line_thickness);
        }
        xout = x1 + xoffset;
        yout = y1 + yoffset;
      }
      else if (rnd <= circle_threshold) {
        // draw circles
        double midlength = line_length/2;  // use midlength of Maurer line as radius
        double ang = Math.random() * M_2PI;
        
        // original version -- 
        //   circles centered on midpoint of "Maurer line"
        //   and use midlength of Maurer line as radius
        double xmid = midlength / Math.sqrt(1 + m*m);
        if (x2 < x1) { xmid = -1 * xmid; }  
        double ymid = Math.abs(m * xmid);
        if (y2 < y1) { ymid = -1 * ymid; }
        xoffset = xmid + (midlength * sin(ang));
        yoffset = ymid + (midlength * cos(ang));
        
        // circles centered on points
        // and use midlenth of Maurer line as radius
        /*
        xoffset = (midlength * sin(ang));
        yoffset = (midlength * cos(ang));
        */
        
        // circles centered on points
        // and use length of Maurer line as radius
        /*
        xoffset = (line_length * sin(ang));
        yoffset = (line_length * cos(ang));
        */
        
        // add thickness
        if (circle_thickness != 0) {
          xoffset += ((pContext.random() - 0.5) * circle_thickness);
          yoffset += ((pContext.random() - 0.5) * circle_thickness);
        }
        xout = x1 + xoffset;
        yout = y1 + yoffset;
      }
      else if (rnd <= point_threshold) {
        // draw point
        if (point_thickness != 0) {
          double roffset = pContext.random() * point_thickness;
          double rangle = (pContext.random() * M_2PI);
          xoffset = roffset * cos(rangle);
          yoffset = roffset * sin(rangle);
        }
        else {
          xoffset = 0;
          yoffset = 0;
        }
        if (rnd <= point_half_threshold) {
          xout = x1 + xoffset;
          yout = y1 + yoffset;
        }
        else {
          xout = x2 + xoffset;
          yout = y2 + yoffset;
        }
      }
      else {
        // draw curve
        if (curve_thickness != 0) {
          xout = x + ((pContext.random() - 0.5) * curve_thickness);
          yout = y + ((pContext.random() - 0.5) * curve_thickness);
        }
        else {
          xout = x;
          yout = y;
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

    if (color_mode != NORMAL) {
      pVarTP.rgbColor = true;
      if (color_mode == RED) {
        pVarTP.redColor = 255;
        pVarTP.greenColor = 0;
        pVarTP.blueColor = 0;
      }
      else if (color_mode == GREEN) {
        pVarTP.redColor = 0;
        pVarTP.greenColor = 255;
        pVarTP.blueColor = 0;
      }
      else if (color_mode == BLUE) {
        pVarTP.redColor = 0;
        pVarTP.greenColor = 0;
        pVarTP.blueColor = 255;
      }
      else if (color_mode == LINE_LENGTH_RG || color_mode == LINE_LENGTH_RB || color_mode == LINE_LENGTH_BG) {
        double baseColor = 0;
        if (line_length < color_low_thresh) { baseColor = 0; }
        else if (line_length > color_high_thresh) { baseColor = 255; }
        else { baseColor = ((line_length - color_low_thresh)/(color_high_thresh - color_low_thresh)) * 255; }
        
        if (color_mode == LINE_LENGTH_RG) {
          pVarTP.redColor = baseColor;
          pVarTP.greenColor = 255 - baseColor;
          pVarTP.blueColor = 0;
        }
        else if (color_mode == LINE_LENGTH_RB) {
          pVarTP.redColor = baseColor;
          pVarTP.greenColor = 0;
          pVarTP.blueColor = 255 - baseColor;
        }
        else if (color_mode == LINE_LENGTH_BG) {
          pVarTP.redColor = 0;
          pVarTP.greenColor = 255 - baseColor;
          pVarTP.blueColor = baseColor;
        }
      }
      else if (color_mode == LINE_ANGLE_RG || color_mode == LINE_ANGLE_RB || color_mode == LINE_ANGLE_BG) {
        double baseColor = 0;

        // if color_low_thresh and color_high_thresh are same, then ignore thresholds and do linear gradient across entire range
        if (color_low_thresh == color_high_thresh) {
          baseColor = delta_from_yaxis * 255;
        }
        else if (delta_from_yaxis < color_low_thresh) { baseColor = 0; }
        else if (delta_from_yaxis > color_high_thresh) { baseColor = 255; }
        else {
          baseColor = ((delta_from_yaxis - color_low_thresh)/(color_high_thresh - color_low_thresh)) * 255; 
        }
        if (color_mode == LINE_ANGLE_RG) {
          pVarTP.redColor = baseColor;
          pVarTP.greenColor = 255 - baseColor;
          pVarTP.blueColor = 0;
        }
        else if (color_mode == LINE_ANGLE_RB) {
          pVarTP.redColor = baseColor;
          pVarTP.greenColor = 0;
          pVarTP.blueColor = 255 - baseColor;
        }
        else if (color_mode == LINE_ANGLE_BG) {
          pVarTP.redColor = 0;
          pVarTP.greenColor = 255 - baseColor;
          pVarTP.blueColor = baseColor;
        }
      }
      else if (color_mode == LINE_ANGLE_RELATIVE_RG || color_mode == LINE_ANGLE_RELATIVE_RB || color_mode == LINE_ANGLE_RELATIVE_BG) {
        double baseColor = 0;
        if (DEBUG && count % 100000 == 0) { System.out.println(adiff/M_PI + ", radians: " + adiff + ", step_size: " + step_size_radians); }

        // if color_low_thresh and color_high_thresh are same, then ignore thresholds and do linear gradient across entire range
        if (color_low_thresh == color_high_thresh) {
          baseColor = (adiff/M_PI) * 255;
        }
        else if (adiff < color_low_thresh) { baseColor = 0; }
        else if (adiff > color_high_thresh) { baseColor = 255; }
        else {
          baseColor = ((adiff - color_low_thresh)/(color_high_thresh - color_low_thresh)) * 255; 
        }
        if (color_mode == LINE_ANGLE_RELATIVE_RG) {
          pVarTP.redColor = baseColor;
          pVarTP.greenColor = 255 - baseColor;
          pVarTP.blueColor = 0;
        }
        else if (color_mode == LINE_ANGLE_RELATIVE_RB) {
          pVarTP.redColor = baseColor;
          pVarTP.greenColor = 0;
          pVarTP.blueColor = 255 - baseColor;
        }
        else if (color_mode == LINE_ANGLE_RELATIVE_BG) {
          pVarTP.redColor = 0;
          pVarTP.greenColor = 255 - baseColor;
          pVarTP.blueColor = baseColor;
        }
      }
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { 
      a, b, c, d, line_offset_degrees, line_count, curve_mode, 
      show_lines_param, show_circles_param, show_points_param, show_curve_param, 
      line_thickness_param, circle_thickness_param, point_thickness_param, curve_thickness_param, 
      (diff_mode ? 1 : 0), color_mode, color_low_thresh, color_high_thresh, 
      line_low_thresh, line_high_thresh, angle_low_thresh, angle_high_thresh, 
      rangle_low_thresh, rangle_high_thresh, 
      line_variation_freq, line_variation_amp };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
    else if (PARAM_LINE_OFFSET_DEGREES.equalsIgnoreCase(pName))
      line_offset_degrees = pValue;
    else if (PARAM_LINE_COUNT.equalsIgnoreCase(pName))
      line_count = pValue;
    else if (PARAM_CURVE_MODE.equalsIgnoreCase(pName))
      curve_mode = (int)pValue;
    else if (PARAM_SHOW_LINES.equalsIgnoreCase(pName))
      show_lines_param = pValue;
    else if (PARAM_SHOW_CIRCLES.equalsIgnoreCase(pName))
      show_circles_param = pValue;
    else if (PARAM_SHOW_POINTS.equalsIgnoreCase(pName))
      show_points_param = pValue;    
    else if (PARAM_SHOW_CURVE.equalsIgnoreCase(pName))
      show_curve_param = pValue;
    else if (PARAM_LINE_THICKNESS.equalsIgnoreCase(pName))
      line_thickness_param = pValue;
    else if (PARAM_CIRCLE_THICKNESS.equalsIgnoreCase(pName))
      circle_thickness_param = pValue;
    else if (PARAM_POINT_THICKNESS.equalsIgnoreCase(pName))
      point_thickness_param = pValue;
    else if (PARAM_CURVE_THICKNESS.equalsIgnoreCase(pName))
      curve_thickness_param = pValue;
    else if (PARAM_DIFF_MODE.equalsIgnoreCase(pName)) {
      diff_mode = (pValue >= 1);
    }
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) {
      color_mode = (int)pValue;
    }
    else if (PARAM_COLOR_LOW_THRESH.equalsIgnoreCase(pName)) {
      color_low_thresh = pValue;
    }
    else if (PARAM_COLOR_HIGH_THRESH.equalsIgnoreCase(pName)) {
      color_high_thresh = pValue;
    }
    else if (PARAM_LINE_LOW_THRESH.equalsIgnoreCase(pName)) {
      line_low_thresh = pValue;
    }
    else if (PARAM_LINE_HIGH_THRESH.equalsIgnoreCase(pName)) {
      line_high_thresh = pValue;
    }
    else if (PARAM_ANGLE_LOW_THRESH.equalsIgnoreCase(pName)) {
      angle_low_thresh = pValue;
    }
    else if (PARAM_ANGLE_HIGH_THRESH.equalsIgnoreCase(pName)) {
      angle_high_thresh = pValue;
    }
    else if (PARAM_RELATIVE_ANGLE_LOW_THRESH.equalsIgnoreCase(pName)) {
      rangle_low_thresh = pValue;
    }
    else if (PARAM_RELATIVE_ANGLE_HIGH_THRESH.equalsIgnoreCase(pName)) {
      rangle_high_thresh = pValue;
    }
    else if (PARAM_LINE_VARIATION_FREQ.equalsIgnoreCase(pName)) {
      line_variation_freq = pValue;
    }
    else if (PARAM_LINE_VARIATION_AMP.equalsIgnoreCase(pName)) {
      line_variation_amp = pValue;
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "maurer_circles";
  }

}
