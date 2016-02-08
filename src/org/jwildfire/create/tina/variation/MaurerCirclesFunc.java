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
import java.util.Arrays;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.floor;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
* Maurer Rose variation by CozyG
* 
*/
public class MaurerCirclesFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private boolean DEBUG_RELATIVE_ANGLE = false;
  private boolean DEBUG_META_MODE = false;
  private boolean DEBUG_SAMPLING = false;

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
  
  private static final String PARAM_DIRECT_COLOR_MEASURE = "direct_color_measure";
  private static final String PARAM_DIRECT_COLOR_GRADIENT = "direct_color_gradient";
//  private static final String PARAM_COLOR_SCALING = "color_scaling";
  private static final String PARAM_DIRECT_COLOR_THRESHOLDING = "direct_color_thresholding";
  private static final String PARAM_COLOR_LOW_THRESH = "color_low_threshold";
  private static final String PARAM_COLOR_HIGH_THRESH = "color_high_threshold";
  
  private static final String PARAM_LINE_LENGTH_FILTER = "line_length_filter_mode";
  private static final String PARAM_LINE_LENTH_LOW_THRESH = "line_length_low_threshold";
  private static final String PARAM_LINE_LENGTH_HIGH_THRESH = "line_length_high_threshold";
  
  private static final String PARAM_LINE_ANGLE_FILTER = "line_angle_filter_mode";
  private static final String PARAM_LINE_ANGLE_LOW_THRESH = "line_angle_low_threshold";
  private static final String PARAM_LINE_ANGLE_HIGH_THRESH = "line_angle_high_threshold";
  
  private static final String PARAM_POINT_ANGLE_FILTER = "point_angle_filter_mode";
  private static final String PARAM_POINT_ANGLE_LOW_THRESH = "point_angle_low_threshold";
  private static final String PARAM_POINT_ANGLE_HIGH_THRESH = "point_angle_high_threshold";
  
  private static final String PARAM_META_MODE = "meta_mode";
  private static final String PARAM_META_MIN_STEP = "meta_min_step";
  private static final String PARAM_META_MAX_STEP = "meta_max_step";
  private static final String PARAM_META_STEP_DIFF = "meta_step_diff";
  
  private static final String PARAM_RANDOMIZE = "randomize";


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
  
  // color mode
  // 0 NORMAL --> normal (no direct coloring)
  // 1 LINE_LENGTH --> color by line length
  // 2 LINE_ANGLE --> color by absolute angle
  // 3 LINE_RELATIVE_ANGLE --> color by relative angle
  
  // where does percentage vs absolute numbers come in?
  
  // color gradient
  // 0 NORMAL --> normal (uses flame/layer colormap, but in standard way)
  // 1 COLORMAP --> flame/layer colormap used for direct coloring
  // 2 ALTERNATIVE_COLORMAP --> reserved for specifying colormap for direct coloring that is different from flame
  // 3 --> RED_GREEN
  // 4 --> RED_BLUE
  // 5 --> BLUE_GREEN
  
  // so for flames using earlier versions, need to split 
  // PARAM_COLOR_MODE into PARAM_DIRECT_COLOR_GRADIENT and PARAM_DIRECT_COLOR_MODE
  // NORMAL ==> NORMAL, NORMAL
  // RED ==> no equivalent (other than normal red coloring based on colormap)
  // GREEN ==> no equivalent
  // BLUE ==> no equivalent
  // LINE_LENGTH_RG ==> LINE_LENGTH, RED_GREEN
  // LINE_LENGTH_RB ==> LINE_LENGTH, RED_BLUE
  // LINE_LENGTH_BG ==> LINE_LENGTH, BLUE_GREEN
  // LINE_LENGTH_COLORMAP => LINE_LENGTH, STANDARD_COLORMAP
  // and etc for LINE_ANGLE_* and LINE_ANGLE_RELATIVE_*
  
  private static final int NORMAL = 0;
  private static final int NONE = 0;
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
  private static final int LINE_LENGTH_COLORMAP = 13;
  private static final int LINE_ANGLE_COLORMAP = 14;
  private static final int LINE_ANGLE_RELATIVE_COLORMAP = 15;
  
  // direct color gradient -- should be near bottom of parameter list, 
  //   since (now that colormap options is working) will be almost always COLORMAP
  private static final int COLORMAP_CLAMP = 1;
  private static final int COLORMAP_CYCLE = 2;
  private static final int RED_GREEN = 3;
  private static final int RED_BLUE = 4;
  private static final int BLUE_GREEN = 5; 
  
  // direct color measures (and filters)
  private static final int LINE_LENGTH = 1;
  private static final int LINE_ANGLE = 2;
  private static final int POINT_ANGLE = 3;
  private static final int META_INDEX = 4;
  private static final int Z_MINMAX = 5;
  private static final int Z_ABSOLUTE_MINMAX = 6;
  // private static final int WEIGHTED_LINE_LENGTH = 5;

  // measure thresholding
  private static final int PERCENT = 0;
  private static final int VALUE = 1;
  // other possibilties -- distance or deviation from mean?
  
  // measure filtering
  private static final int OFF = 0;
  private static final int BAND_PASS_PERCENT = 1;
  private static final int BAND_STOP_PERCENT = 2;
  private static final int BAND_PASS_VALUE = 3;
  private static final int BAND_STOP_VALUE = 4;
  
  // meta modes
  private static final int LINE_OFFSET_INCREMENT = 1;
  private static final int A_LINEAR_INCREMENT = 2;
  private static final int B_LINEAR_INCREMENT = 3;
  private static final int C_LINEAR_INCREMENT = 4;
  private static final int D_LINEAR_INCREMENT = 4;
  private static final int E_LINEAR_INCREMENT = 5;
  private static final int F_LINEAR_INCREMENT = 6;
  private static final int A_B_LINEAR_INCREMENT = 5;
  
  private static final String[] paramNames = { 
    PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_LINE_OFFSET_DEGREES, PARAM_LINE_COUNT, PARAM_CURVE_MODE, 
    PARAM_SHOW_LINES, PARAM_SHOW_CIRCLES, PARAM_SHOW_POINTS, PARAM_SHOW_CURVE, 
    PARAM_LINE_THICKNESS, PARAM_CIRCLE_THICKNESS, PARAM_POINT_THICKNESS, PARAM_CURVE_THICKNESS, 
    PARAM_DIRECT_COLOR_MEASURE, PARAM_DIRECT_COLOR_GRADIENT, PARAM_DIRECT_COLOR_THRESHOLDING, 
    PARAM_COLOR_LOW_THRESH, PARAM_COLOR_HIGH_THRESH, 
    PARAM_LINE_LENGTH_FILTER, PARAM_LINE_LENTH_LOW_THRESH, PARAM_LINE_LENGTH_HIGH_THRESH, 
    PARAM_LINE_ANGLE_FILTER, PARAM_LINE_ANGLE_LOW_THRESH, PARAM_LINE_ANGLE_HIGH_THRESH, 
    PARAM_POINT_ANGLE_FILTER, PARAM_POINT_ANGLE_LOW_THRESH, PARAM_POINT_ANGLE_HIGH_THRESH, 
    PARAM_RANDOMIZE, PARAM_DIFF_MODE, 
    PARAM_META_MODE, PARAM_META_MIN_STEP, PARAM_META_MAX_STEP, PARAM_META_STEP_DIFF
  };

  private double a_param = 2; // numerator of k in rose curve equations,   k = kn/kd  (n1 in supershape equation)
  private double b_param = 1; // denominator of k in rose curve equations, k = kn/kd  (n2 in supershape equation)
  private double c_param = 0; // often called "c" in rose curve modifier equations    (n3 in supershape equation)
  private double d_param = 0; // used for n3 in supershape equation
  
  private double a, b, c, d;
  private double line_offset;
  // rhodonea vars
  // private double kn, kd, k, radial_offset; // k = kn/kd
  // epitrochoid / hypotrochoid vars
  private double a_radius, b_radius, c_radius;
  
  private double cycles; // 1 cycle = 2*PI
  private double line_count = 360;
  private double line_offset_param = 71;  // specified in degrees
  private double step_size_radians;

  private double show_lines_param = 1;
  private double show_circles_param = 0;
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
  private int color_measure = NONE;
  private int color_gradient = NORMAL;
  private int color_thresholding = PERCENT;
  
//  private double color_scaling = 100;
  private double color_low_thresh = 0.3;
  private double color_high_thresh = 2.0;

  private int line_length_filter = OFF;
  private double line_length_low_thresh = 0;    
  private double line_length_high_thresh = 1;  
  private int line_angle_filter = OFF;
  private double line_angle_low_thresh = 0;   
  private double line_angle_high_thresh = 1; 
  private int point_angle_filter = OFF;
  private double point_angle_low_thresh = 0;  
  private double point_angle_high_thresh = 1; 
  
  private  int meta_mode = OFF;
  private double meta_min_value = 30; 
  private double meta_max_value = 45; 
  private double meta_step_value = 1;
  private double current_meta_step;
  private double meta_steps;
  
  private boolean randomize = false;
  private int sample_size = 1000;
  private double[] sampled_line_lengths = new double[sample_size];
  private double[] sampled_line_angles = new double[sample_size];
  private double[] sampled_point_angles = new double[sample_size];
  
  private long count = 0;
  
  class DoublePoint2D {
    public double x;
    public double y;
  }
  private DoublePoint2D end_point1 = new DoublePoint2D(); 
  private DoublePoint2D end_point2 = new DoublePoint2D(); 
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
 /*
    kn = a;
    kd = b;
    k = kn / kd;
    radial_offset = c;
    */
    
    // epitrochoid/hypotrochoid init
  /*  double radius = a;
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
    */
    
    double show_sum = abs(show_lines_param) + abs(show_circles_param) + abs(show_points_param) + abs(show_curve_param);
    line_fraction = abs(show_lines_param) / show_sum;
    circle_fraction = abs(show_circles_param) / show_sum;
    point_fraction = abs(show_points_param) / show_sum;
    curve_fraction = abs(show_curve_param) / show_sum;
    line_threshold = line_fraction;
    circle_threshold = line_threshold + circle_fraction;
    point_threshold = circle_threshold + point_fraction;
    point_half_threshold = circle_threshold + (point_fraction/2);
    
    line_thickness = line_thickness_param / 100;
    circle_thickness = circle_thickness_param / 100;
    point_thickness = point_thickness_param / 100;
    curve_thickness = curve_thickness_param / 100;
    
    step_size_radians = M_2PI * (line_offset_param / 360);
    cycles = (line_count * step_size_radians) / M_2PI;
    
    double raw_meta_steps = (meta_max_value - meta_min_value)/meta_step_value;
    meta_steps = (int)raw_meta_steps;
    if (DEBUG_META_MODE) {
      System.out.println("min: " + meta_min_value);
      System.out.println("max: " + meta_max_value);
      System.out.println("diff: " + meta_step_value);
      System.out.println("meta steps raw: " + raw_meta_steps);
      System.out.println("meta steps: " + meta_steps);
    }
    
    // sampling across theta range to get approximate distribution of: 
    //    line lengths, angles, relative angles
    //    (possibly other measures used for color selection, etc.)
    if (DEBUG_SAMPLING) { System.out.println("sampling"); }
    for (int i=0; i<sample_size; i++) {
      count++;
      setValues();
      double theta1, theta2, x1, y1, x2, y2;
      
      // should be able to improve this, only sample from possible theta
      //    based on step size and lince count: 
      // theta1 = step_size_radians * (int)(Math.random()*line_count);
      theta1 = Math.random() * cycles * M_2PI;
      // reusing end_point1
      end_point1 = getCurveCoords(theta1, end_point1);
      x1 = end_point1.x;
      y1 = end_point1.y;
      // double sampled_step_size = getStepSize();
      theta2 = theta1 + step_size_radians;
      // reusing end_point2
      end_point2 = getCurveCoords(theta2, end_point2);
      x2 = end_point2.x;
      y2 = end_point2.y;
      
      // find the slope and length of the line
      double ydiff = y2 - y1;
      double xdiff = x2 - x1;
      double m = ydiff / xdiff;  // slope 

      double point_angle = getPointAngle(end_point1, end_point2);

      double raw_line_angle = atan2(ydiff, xdiff);  // atan2 range is [-Pi..+Pi]
      // if (raw_line_angle < 0) { line_angle += M_2PI; }   // map to range [0..+2Pi]
      // delta_from_yaxis should be 0 if parallel to y-axis, and M_PI/2 if parallel to x-axis
      double unscaled_line_angle = abs(abs(raw_line_angle) - (M_PI/2.0));
      // scale to range [0..1]; (0 parallel to y-axis, 1 parallel to x-axis)
      double line_angle = unscaled_line_angle / (M_PI/2.0);
      double line_length = Math.sqrt( (xdiff * xdiff) + (ydiff * ydiff));
      sampled_line_lengths[i] = line_length;
      sampled_line_angles[i] = line_angle;
      sampled_point_angles[i] = point_angle;
    }
    if (DEBUG_SAMPLING) { System.out.println("sorting"); }
    Arrays.sort(sampled_line_lengths);
    Arrays.sort(sampled_line_angles);
    Arrays.sort(sampled_point_angles);
    if (DEBUG_SAMPLING) { 
      System.out.println("low line length: " + sampled_line_lengths[0]);
      System.out.println("high line length: " + sampled_line_lengths[sample_size-1]);
      System.out.println("low line angle: " + sampled_line_angles[0]);
      System.out.println("high line angle: " + sampled_line_angles[sample_size-1]);
      System.out.println("low point angle: " + sampled_point_angles[0]);
      System.out.println("high point angle: " + sampled_point_angles[sample_size-1]);
    }

  }
  
  /* get the angle between two points (angle between two lines L1 and L2 to origin, one to P1 and one to P2); */
  public double getPointAngle(DoublePoint2D point1, DoublePoint2D point2) {
     double a1 = atan2(point1.y, point1.x); 
     double a2 = atan2(point2.y, point2.x);

     if (a1 < 0) { a1 += M_2PI; }  // map to [0..2Pi]
     if (a2 < 0) { a2 += M_2PI; }  // map to [0..2Pi]
     double point_angle = abs(a2 - a1);
     if (point_angle > M_PI) { point_angle = M_2PI - point_angle; }
     return point_angle;
  }
  
  public DoublePoint2D getCurveCoords(double theta, DoublePoint2D point) {
    if (curve_mode == CIRCLE) {
      double r = a;
      point.x = r * cos(theta);
      point.y = r * sin(theta);
    }
    else if (curve_mode == RECTANGLE) {
      double t = theta % M_2PI;
      if (t < 0) { t = (0.5 - t); }

      if (t <= 0.25) {
        point.x = a/2;
        // curve_point.y = (s * 4 * b) - b/2;  
        point.y = (t * 4 * b) - b/2;  
      }
      else if (t <= 0.5) {
        point.x = ((t-0.25) * 4 * a) - a/2;
        point.y = b/2;
      }
      else if (t <= 0.75) {
        point.x = -a/2;
        point.y = ((t-0.5) * 4 * b) - b/2;
      }
      else {
        point.x = ((t-0.75) * 4 * a) -a/2;
        point.y = -b/2;
      }
    }
    else if (curve_mode == ELLIPSE) {
    }
    else if (curve_mode == RHODONEA) {
      double k = a/b;
      double r = cos(k * theta) + c;
      if (DEBUG_RELATIVE_ANGLE && count % 100000 == 0) { System.out.println("radius = " + r); }
      point.x = r * cos(theta);
      point.y = r * sin(theta);
    }
    else if (curve_mode == EPISPIRAL) {
      double k = a/b;
      double r = (1/cos(k * theta)) + c;
      point.x = r * cos(theta);
      point.y = r * sin(theta);
    }
    else if (curve_mode == EPITROCHOID) {
      // option 1:
      // double x = ((a_radius + b_radius) * cos(theta)) - (c_radius * cos(((a_radius + b_radius)/b_radius) * theta));
      // double y = ((a_radius + b_radius) * sin(theta)) - (c_radius * sin(((a_radius + b_radius)/b_radius) * theta));
      // option 2:
      double x = ((a + b) * cos(theta)) - (c * cos(((a + b)/b) * theta));
      double y = ((a + b) * sin(theta)) - (c * sin(((a + b)/b) * theta));
      point.x = x;
      point.y = y;
    }
    else if (curve_mode == HYPOTROCHOID) {
      // option 1:
      // double x = ((a_radius - b_radius) * cos(theta)) + (c_radius * cos(((a_radius - b_radius)/b_radius) * theta));
      // double y = ((a_radius - b_radius) * sin(theta)) - (c_radius * sin(((a_radius - b_radius)/b_radius) * theta));
      // option 2: 
      double x = ((a - b) * cos(theta)) + (c * cos(((a - b)/b) * theta));
      double y = ((a - b) * sin(theta)) - (c * sin(((a - b)/b) * theta));
      point.x = x;
      point.y = y;
    }
    else if (curve_mode == LISSAJOUS) {
      // x = A * sin(a*t + d)
      // y = B * sin(b*t);
      // for now keep A = B = 1
      double x = sin((a*theta) + c);
      double y = sin(b*theta);
      point.x = x;
      point.y = y;
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
      point.x = r * cos(theta);
      point.y = r * sin(theta);
    }
    else if (curve_mode == STARR_CURVE) {
      double r = 2 + (sin(a * theta)/2);
      theta = theta + (sin(b * theta)/c); 
      point.x = r * cos(theta);
      point.y = r * sin(theta);
    }
    else if (curve_mode == FARRIS_MYSTERY_CURVE) {
      double t = theta;
      point.x = cos(t)/a + cos(6*t)/b + sin(14*t)/c;
      point.y = sin(t)/a + sin(6*t)/b + cos(14*t)/c;
      // can also be represented more concisely with complex numbers: 
      //   c(t) = (e^(i*t))/a + (e^(6*i*t))/b + (e^(-14*i*t))/c
      //   should break out into a fully parameterized version with exponent parameters as well 
    }
    else if (curve_mode == WAGON_FANCIFUL_CURVE) {
      point.x = sin(a * theta) * cos(c * theta);
      point.y = sin(b * theta) * sin(c * theta);
    }
    else if (curve_mode == FAY_BUTTERFLY) {
      // r = e^cos(t) - 2cos(4t) - sin^5(t/12)
      // y = sin(t)*r
      // x = cos(t)*r
      double t = theta;
      // double r = 0.5 * (exp(cos(t)) - (2 * cos(4 * t)) - pow(sin(t / 12), 5) + offset);
      double r = 0.5 * (exp(cos(t)) - (2 * cos(4 * t)) - pow(sin(t / 12), 5));
      point.x = r * sin(t);
      point.y = r * cos(t);
    }
    else if (curve_mode == RIGGE1) {
      double r = (1 - cos(a * theta)) + (1 - cos(a * b * theta));
      point.x = r * cos(theta);
      point.y = r * sin(theta);
    }
    else if (curve_mode == RIGGE2) {
      double r = (1 - cos(a * theta)) - (1 - cos(a * b * theta));
      point.x = r * cos(theta);
      point.y = r * sin(theta);
    }
    else {  // default to circle
      double r = a;
      point.x = r * cos(theta);
      point.y = r * sin(theta);
    }
    return point;
  }
  
  public void setValues() {
    a = a_param;
    b = b_param;
    c = c_param;
    d = d_param;
    line_offset = line_offset_param;
    
    if (meta_mode != OFF) {
      // which meta-step
      // should round instead?

      current_meta_step = (int)(Math.random() * meta_steps);
      double meta_value = meta_min_value + (current_meta_step * meta_step_value);
      if (meta_mode == LINE_OFFSET_INCREMENT) {
        // line_offset = meta_min_value + (current_meta_step * meta_step_value);'
        line_offset = meta_value;
        step_size_radians = M_2PI * (line_offset / 360);
        cycles = (line_count * step_size_radians) / M_2PI;
        // actual_step_size = meta_min_step_radians + (meta_step_diff_radians * current_meta_step);
        //      actual_step_size = M_2PI * (((int)(Math.random()* 60)+10)/360.0);
      }
      else if (meta_mode == A_LINEAR_INCREMENT) {
        a = meta_value;
      }
      else if (meta_mode == B_LINEAR_INCREMENT) {
        b = meta_value;
      }
      else if (meta_mode == C_LINEAR_INCREMENT) {
        c = meta_value;
      }
      else if (meta_mode == D_LINEAR_INCREMENT) {
        d = meta_value;
      }
      else if (meta_mode == A_B_LINEAR_INCREMENT) {
        // treat a param as meta param -- meta_min_value determines min
        // treat b similar, but b_param determines min b
        a = meta_value;
        b = b_param + (current_meta_step * meta_step_value);
      }
      if (DEBUG_META_MODE && count % 50000 == 0) {
        System.out.println("count: " + count + ", metacount = " + (int)(count / 50000));
        System.out.println("meta_step: " + current_meta_step);
        System.out.println("meta_value: " + meta_value);
        System.out.println("a: " + a);
        System.out.println("b: " + b);
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
    count++;
    setValues();
    // double actual_step_size = getStepSize();

    double xin = pAffineTP.x;
    double yin = pAffineTP.y;
    // atan2 range is [-PI, PI], so tin covers 2PI, or 1 cycle (from -0.5 to 0.5 cycle)
    double tin;
    if (randomize) { 
      tin = (Math.random() * M_2PI) - M_PI; // random angle, range [-Pi .. +Pi]
    }
    else {
      tin = atan2(yin, xin); // polar coordinate angle (theta in radians) of incoming point [-Pi .. +Pi]
    }
    double t = cycles * tin; // theta parameter for curve calculation

    // reusing curve_point
    curve_point = getCurveCoords(t, curve_point);

    double x = curve_point.x;
    double y = curve_point.y;
    double r = sqrt(x*x + y*y);
    double rinx, riny;
    double xout, yout, zout, rout;
    zout = 0;
    
    double step_number, theta1, theta2;
    double x1, y1, x2, y2;

      // map to a Maurer Rose line
      // find nearest step
      step_number = floor(t/step_size_radians);
      
      // find polar and cartesian coordinates for endpoints of Maure Rose line
      theta1 = step_number * step_size_radians;
      theta2 = theta1 + step_size_radians;

      // reusing end_point1
      end_point1 = getCurveCoords(theta1, end_point1);
      x1 = end_point1.x;
      y1 = end_point1.y;
      // reusing end_point2
      end_point2 = getCurveCoords(theta2, end_point2);
      x2 = end_point2.x;     
      y2 = end_point2.y;
      
      // find the slope and length of the line
      double ydiff = y2 - y1;
      double xdiff = x2 - x1;
      double m = ydiff / xdiff;  // slope 

      double point_angle = getPointAngle(end_point1, end_point2);
      
      double raw_line_angle = atan2(ydiff, xdiff);  // atan2 range is [-Pi..+Pi]
      // if (raw_line_angle < 0) { raw_line_angle += M_2PI; }   // map to range [0..+2Pi]
      // delta_from_yaxis should be 0 if parallel to y-axis, and M_PI/2 if parallel to x-axis
      double unscaled_line_angle = abs(abs(raw_line_angle) - (M_PI/2.0));
      // scale to range [0..1]; (0 parallel to y-axis, 1 parallel to x-axis)
      double line_angle = unscaled_line_angle / (M_PI/2.0);
      double line_length = Math.sqrt( (xdiff * xdiff) + (ydiff * ydiff));

      // 
      // FILTERING
      // 
      pVarTP.doHide = false;
      if (line_length_filter != OFF) {
        boolean inband = true; // default is to include all values in band
        double low_value, high_value;
        if (line_length_filter == BAND_PASS_VALUE || line_length_filter == BAND_STOP_VALUE) {
          low_value = line_length_low_thresh;
          high_value = line_length_high_thresh;
        }
        else if (line_length_filter == BAND_PASS_PERCENT || line_length_filter == BAND_STOP_PERCENT) {
          int high_index, low_index;
          if (line_length_high_thresh >= 1.0) { high_index = sample_size-1; }
          else if (line_length_high_thresh < 0) { high_index = 0; }
          else { high_index = (int)(line_length_high_thresh * sample_size); }
          high_value = sampled_line_lengths[high_index];

          if (line_length_low_thresh >= 1.0) { low_index = sample_size-1; }
          else if (line_length_low_thresh < 0) { low_index = 0; }
          else { low_index = (int)(line_length_low_thresh * sample_size); }
          low_value = sampled_line_lengths[low_index];
        }
        else { // default to  values;
          low_value = line_length_low_thresh;
          high_value = line_length_high_thresh;
        }
        // to avoid everything disappearing, if low_val > high_val then flip
        if (low_value > high_value) {
          double temp = low_value;
          low_value = high_value;
          high_value = temp;
        }
        inband = (line_length >= low_value && line_length <= high_value);
        if (line_length_filter == BAND_PASS_PERCENT || line_length_filter == BAND_PASS_VALUE) {
          pVarTP.doHide = !inband;
        }
        else if (line_length_filter == BAND_STOP_PERCENT || line_length_filter == BAND_STOP_VALUE) {
          pVarTP.doHide = inband;
        }
      }

      if (this.line_angle_filter != OFF) {
        boolean inband = true; // default is to include all values in band
        double low_value, high_value;
        if (line_angle_filter == BAND_PASS_VALUE || line_angle_filter == BAND_STOP_VALUE) {
          low_value = line_angle_low_thresh;
          high_value = line_angle_high_thresh;
        }
        else if (line_angle_filter == BAND_PASS_PERCENT || line_angle_filter == BAND_STOP_PERCENT) {
          int high_index, low_index;
          if (line_angle_high_thresh >= 1.0) { high_index = sample_size-1; }
          else if (line_angle_high_thresh < 0) { high_index = 0; }
          else { high_index = (int)(line_angle_high_thresh * sample_size); }
          high_value = sampled_line_angles[high_index];

          if (line_angle_low_thresh >= 1.0) { low_index = sample_size-1; }
          else if (line_angle_low_thresh < 0) { low_index = 0; }
          else { low_index = (int)(line_angle_low_thresh * sample_size); }
          low_value = sampled_line_angles[low_index];
        }
        else { // default to  values;
          low_value = line_angle_low_thresh;
          high_value = line_angle_high_thresh;
        }
        // to avoid everything disappearing, if low_val > high_val then flip
        if (low_value > high_value) {
          double temp = low_value;
          low_value = high_value;
          high_value = temp;
        }
        inband = (line_angle >= low_value && line_angle <= high_value);
        if (line_angle_filter == BAND_PASS_PERCENT || line_angle_filter == BAND_PASS_VALUE) {
          pVarTP.doHide = !inband;
        }
        else if (line_angle_filter == BAND_STOP_PERCENT || line_angle_filter == BAND_STOP_VALUE) {
          pVarTP.doHide = inband;
        }
      }

      if (this.point_angle_filter != OFF) {
        boolean inband = true; // default is to include all values in band
        double low_value, high_value;
        if (point_angle_filter == BAND_PASS_VALUE || point_angle_filter == BAND_STOP_VALUE) {
          low_value = point_angle_low_thresh;
          high_value = point_angle_high_thresh;
        }
        else if (point_angle_filter == BAND_PASS_PERCENT || point_angle_filter == BAND_STOP_PERCENT) {
          int high_index, low_index;
          if (point_angle_high_thresh >= 1.0) { high_index = sample_size-1; }
          else if (point_angle_high_thresh < 0) { high_index = 0; }
          else { high_index = (int)(point_angle_high_thresh * sample_size); }
          high_value = sampled_point_angles[high_index];

          if (point_angle_low_thresh >= 1.0) { low_index = sample_size-1; }
          else if (point_angle_low_thresh < 0) { low_index = 0; }
          else { low_index = (int)(point_angle_low_thresh * sample_size); }
          low_value = sampled_point_angles[low_index];
        }
        else { // default to  values;
          low_value = point_angle_low_thresh;
          high_value = point_angle_high_thresh;
        }
        // to avoid everything disappearing, if low_val > high_val then flip
        if (low_value > high_value) {
          double temp = low_value;
          low_value = high_value;
          high_value = temp;
        }
        inband = (point_angle >= low_value && point_angle <= high_value);
        if (point_angle_filter == BAND_PASS_PERCENT || point_angle_filter == BAND_PASS_VALUE) {
          pVarTP.doHide = !inband;
        }
        else if (point_angle_filter == BAND_STOP_PERCENT || point_angle_filter == BAND_STOP_VALUE) {
          pVarTP.doHide = inband;
        }
      }

      // yoffset = [+-] m * d / (sqrt(1 + m^2))
      double xoffset=0, yoffset=0, zoffset = 0;

      double rnd = pContext.random();
      if (rnd < line_threshold) {
        // draw lines
        double line_delta = Math.random() * line_length;
        xoffset = line_delta / Math.sqrt(1 + m*m);
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
        if (show_circles_param >= 0) {
          xoffset = xmid + (midlength * sin(ang));
          yoffset = ymid + (midlength * cos(ang));
        }
        // using negative show_circles_param to indicate circles should be 3D (perpendicular to xy plane)
        else {  // show_circles_param < 0) {
          boolean semi_circle = false;
          double line_delta = Math.random() * line_length;
          xoffset = line_delta / Math.sqrt(1 + m*m);
          if (x2 < x1) { xoffset = -1 * xoffset; }  // determine sign based on p2
          yoffset = Math.abs(m * xoffset);
          if (y2 < y1) { yoffset = -1 * yoffset; }
          double rad = midlength;
          double xx = (line_delta - rad);
          zout = Math.sqrt((rad * rad) - (xx * xx));
          if (! semi_circle) {
            if (Math.random() < 0.5) { zout = -zout; }
          }

        }

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
      pVarTP.z = pAffineTP.z + (pAmount * (zout - pAffineTP.z));
    }
    else {
      pVarTP.x += pAmount * xout;
      pVarTP.y += pAmount * yout;
      pVarTP.z += pAmount * zout;
    }
    /*
            if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
    */

    //
    //  COLORING
    // 
    if (color_measure != NORMAL) {

      double colvar;
      double[] sample_array;
      if (color_measure == LINE_LENGTH) {
        colvar = line_length;
        sample_array = sampled_line_lengths;
      }
      else if (color_measure == LINE_ANGLE) {
        colvar = line_angle;
        sample_array = sampled_line_angles;
      }
      else if (color_measure == POINT_ANGLE) {
        colvar = point_angle;
        sample_array = sampled_point_angles;
      }
      else if (color_measure == META_INDEX && meta_mode != OFF) {
        colvar = current_meta_step;
        sample_array = null;
      }
      else if (color_measure == Z_MINMAX) {
        // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
        // colvar = abs(zout*2); // min/max zout should be 
        colvar = zout + (line_length/2); //  range of colvar should be 0 to line_length;
        sample_array = sampled_line_lengths;
      }
      else if (color_measure == Z_ABSOLUTE_MINMAX) {
        // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
        colvar = abs(zout) * 2; // range of colvar should be 0 to line_length
        sample_array = sampled_line_lengths;
      }
      else { // default to LINE_LENGTH
        colvar = line_length;
        sample_array = sampled_line_lengths;
      }
      
      double baseColor = 0;      

      double low_value, high_value;
      // ignore percentile option and color_thresholding if using META_INDEX mode??
      if (color_measure == META_INDEX && meta_mode != OFF) {
        low_value = 0; 
        high_value = meta_steps;
      }
      else {
        if (color_thresholding == PERCENT) {
          int low_index, high_index;
          if (color_low_thresh < 0 || color_low_thresh >= 1) { low_index = 0; }
          else { low_index = (int)(color_low_thresh * sample_size); }
          if (color_high_thresh >= 1 || color_high_thresh < 0) { high_index = sample_size - 1; }
          else { high_index = (int)(color_high_thresh * (sample_size-1)); }
          low_value = sample_array[low_index];
          high_value = sample_array[high_index];
        }
        else {  // default is by value
          low_value = color_low_thresh;
          high_value = color_high_thresh;
        }
        if (low_value > high_value) {
          double temp = low_value;
          low_value = high_value;
          high_value = temp;
        }
      }

      if (colvar < low_value) { baseColor = 0; }
      else if (colvar >= high_value) { baseColor = 255; }
      else { baseColor = ((colvar - low_value)/(high_value - low_value)) * 255; }
      if (color_gradient == COLORMAP_CLAMP) {
        pVarTP.rgbColor = false;
        pVarTP.color = baseColor / 255.0;
        if (pVarTP.color < 0) { pVarTP.color = 0; }
        if (pVarTP.color > 1.0) { pVarTP.color = 1.0; }
      }
      else if (color_gradient == RED_GREEN) {  // 
        pVarTP.rgbColor = true;
        pVarTP.redColor = baseColor;
        pVarTP.greenColor = 255 - baseColor;
        pVarTP.blueColor = 0;
      }
      else if (color_gradient == RED_BLUE) {
        pVarTP.rgbColor = true;
        pVarTP.redColor = baseColor;
        pVarTP.greenColor = 0;
        pVarTP.blueColor = 255 - baseColor;
      }
      else if (color_gradient == BLUE_GREEN) {
        pVarTP.rgbColor = true;
        pVarTP.redColor = 0;
        pVarTP.greenColor = 255 - baseColor;
        pVarTP.blueColor = baseColor;
      }

    } // END color_mode != normal

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { 
      a_param, b_param, c_param, d_param, line_offset_param, line_count, curve_mode, 
      show_lines_param, show_circles_param, show_points_param, show_curve_param, 
      line_thickness_param, circle_thickness_param, point_thickness_param, curve_thickness_param, 
      color_measure, color_gradient, color_thresholding, color_low_thresh, color_high_thresh, 
      line_length_filter, line_length_low_thresh, line_length_high_thresh, 
      line_angle_filter, line_angle_low_thresh, line_angle_high_thresh, 
      point_angle_filter, point_angle_low_thresh, point_angle_high_thresh,  
      (randomize ? 1 : 0), (diff_mode ? 1 : 0), 
      meta_mode, meta_min_value, meta_max_value, meta_step_value };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a_param = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b_param = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c_param = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d_param = pValue;
    else if (PARAM_LINE_OFFSET_DEGREES.equalsIgnoreCase(pName))
      line_offset_param = pValue;
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
    else if (PARAM_DIRECT_COLOR_MEASURE.equalsIgnoreCase(pName)) {
      color_measure = (int)pValue;
    }
    else if (PARAM_DIRECT_COLOR_GRADIENT.equalsIgnoreCase(pName)) {
      color_gradient = (int)pValue;
    }
    else if (PARAM_DIRECT_COLOR_THRESHOLDING.equalsIgnoreCase(pName)) {
      color_thresholding = (int)pValue;
    }
    else if (PARAM_COLOR_LOW_THRESH.equalsIgnoreCase(pName)) {
      color_low_thresh = pValue;
    }
    else if (PARAM_COLOR_HIGH_THRESH.equalsIgnoreCase(pName)) {
      color_high_thresh = pValue;
    }
    else if (PARAM_LINE_LENGTH_FILTER.equalsIgnoreCase(pName)) {
      line_length_filter = (int)pValue;
    }
    else if (PARAM_LINE_LENTH_LOW_THRESH.equalsIgnoreCase(pName)) {
      line_length_low_thresh = pValue;
    }
    else if (PARAM_LINE_LENGTH_HIGH_THRESH.equalsIgnoreCase(pName)) {
      line_length_high_thresh = pValue;
    }
    else if (PARAM_LINE_ANGLE_FILTER.equalsIgnoreCase(pName)) {
      line_angle_filter = (int)pValue;
    }
    else if (PARAM_LINE_ANGLE_LOW_THRESH.equalsIgnoreCase(pName)) {
      line_angle_low_thresh = pValue;
    }
    else if (PARAM_LINE_ANGLE_HIGH_THRESH.equalsIgnoreCase(pName)) {
      line_angle_high_thresh = pValue;
    }
    else if (PARAM_POINT_ANGLE_FILTER.equalsIgnoreCase(pName)) {
      point_angle_filter = (int)pValue;
    }
    else if (PARAM_POINT_ANGLE_LOW_THRESH.equalsIgnoreCase(pName)) {
      point_angle_low_thresh = pValue;
    }
    else if (PARAM_POINT_ANGLE_HIGH_THRESH.equalsIgnoreCase(pName)) {
      point_angle_high_thresh = pValue;
    }
    else if (PARAM_RANDOMIZE.equalsIgnoreCase(pName)) {
      randomize = (pValue >= 1);
    }
    else if (PARAM_META_MODE.equalsIgnoreCase(pName)) {
      meta_mode = (int)pValue;
    }
    else if (PARAM_META_MIN_STEP.equalsIgnoreCase(pName)) { 
      meta_min_value = pValue;
    }
    else if (PARAM_META_MAX_STEP.equalsIgnoreCase(pName)) { 
      meta_max_value = pValue;
    }
    else if (PARAM_META_STEP_DIFF.equalsIgnoreCase(pName)) { 
      this.meta_step_value = pValue;
    }
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "maurer_circles";
  }

}
