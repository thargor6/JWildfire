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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

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
 * Maurer Lines variation by CozyG
 * Initially based on the "Maurer Rose", as described by Peter Maurer,
 *    for more information on Maurer Roses see https://en.wikipedia.org/wiki/Maurer_rose
 *
 */
public class MaurerLinesFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private boolean DEBUG_RELATIVE_ANGLE = false;
  private boolean DEBUG_META_MODE = false;
  private boolean DEBUG_SAMPLING = false;
  private boolean DEBUG_DYNAMIC_PARAMETERS = false;
  
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
  private static final String PARAM_RENDER_MODE = "render_mode";
  private static final String PARAM_CURVE_MODE = "curve_mode";
  
  private static final String PARAM_DIRECT_COLOR_MEASURE = "direct_color_measure";
  private static final String PARAM_DIRECT_COLOR_GRADIENT = "direct_color_gradient";
//  private static final String PARAM_COLOR_SCALING = "color_scaling";
  private static final String PARAM_DIRECT_COLOR_THRESHOLDING = "direct_color_thresholding";
  private static final String PARAM_COLOR_LOW_THRESH = "color_low_threshold";
  private static final String PARAM_COLOR_HIGH_THRESH = "color_high_threshold";
  
  private static final String PARAM_FILTER_COUNT = "number_of_filters";
  private static final String PARAM_FILTER_PREFIX = "filter";
  private static final String PARAM_FILTER_MODE_SUFFIX = "mode";
  private static final String PARAM_FILTER_MEASURE_SUFFIX = "measure";
  private static final String PARAM_FILTER_OPERATOR_SUFFIX = "operator";
  private static final String PARAM_FILTER_LOW_SUFFIX = "low_threshold";
  private static final String PARAM_FILTER_HIGH_SUFFIX = "high_threshold";
  
  private static final String PARAM_META_MODE = "meta_mode";
  private static final String PARAM_META_MIN_VALUE = "meta_min_step";
  private static final String PARAM_META_MAX_VALUE = "meta_max_step";
  private static final String PARAM_META_STEP_VALUE = "meta_step_diff";
  
  private static final String PARAM_SHOW_POINTS = "show_points";
  private static final String PARAM_SHOW_CURVE = "show_curve";
  private static final String PARAM_LINE_THICKNESS = "line_thickness";
  private static final String PARAM_POINT_THICKNESS = "point_thickness";
  private static final String PARAM_CURVE_THICKNESS = "curve_thickness";
  private static final String PARAM_DIFF_MODE = "diff_mode";
  
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
  // 1 LINE_LENGTH_LINES --> color by line length
  // 2 LINE_ANGLE_LINES --> color by absolute angle
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
  // LINE_LENGTH_RG ==> LINE_LENGTH_LINES, RED_GREEN
  // LINE_LENGTH_RB ==> LINE_LENGTH_LINES, RED_BLUE
  // LINE_LENGTH_BG ==> LINE_LENGTH_LINES, BLUE_GREEN
  // LINE_LENGTH_COLORMAP => LINE_LENGTH_LINES, STANDARD_COLORMAP
  // and etc for LINE_ANGLE_* and LINE_ANGLE_RELATIVE_*
  
  private static final int STANDARD_LINES = 1;
  private static final int CIRCLES1 = 2;
  private static final int CIRCLES2 = 3;
  private static final int CIRCLES3 = 4;
  private static final int ZCIRCLES = 5;
  private static final int ZCIRCLES2 = 6;
  
  private static final int NORMAL = 0;
  private static final int NONE = 0;
  
  // direct color gradient -- should be near bottom of parameter list,
  //   since (now that colormap options is working) will be almost always COLORMAP
  private static final int COLORMAP_CLAMP = 1;
  private static final int COLORMAP_CYCLE = 2;
  private static final int RED_GREEN = 3;
  private static final int RED_BLUE = 4;
  private static final int BLUE_GREEN = 5;
  
  // direct color measures (and filters)
  private static final int LINE_LENGTH_LINES = 1;
  private static final int LINE_ANGLE_LINES = 2;
  private static final int POINT_ANGLE_LINES = 3;
  private static final int META_INDEX = 4;
  private static final int Z_MINMAX_POINTS = 5;
  private static final int Z_ABSOLUTE_MINMAX_POINTS = 6;
  private static final int DISTANCE_ALONG_LINE_POINTS = 7;
  private static final int DISTANCE_FROM_MIDLINE_POINTS = 8;
  // private static final int WEIGHTED_LINE_LENGTH = 5;
  
  // measure thresholding
  private static final int PERCENT = 0;
  private static final int VALUE = 1;
  // other possibilties -- distance or deviation from mean?
  
  // measure filtering
  private static final int OFF = 0;
  private static final int BAND_PASS_VALUE = 1;
  private static final int BAND_STOP_VALUE = 2;
  private static final int BAND_PASS_PERCENT = 3;
  private static final int BAND_STOP_PERCENT = 4;
  
  // boolean operations to combine filters
  private static final int AND = 0;
  private static final int OR = 1;
  private static final int XOR = 2;
  // not sure if should include ANOTB and BNOTA
  // these can be done just as easily by using  BAND_PASS and BAND_STOP
  // so to simplify may want to either eliminate ANOTB and BNOTA options for filter.operator,
  //    or eliminate BAND_STOP_VALUE and BAND_STOP_PERCENT options for filter.measure
  private static final int ANOTB = 3;
  private static final int BNOTA = 4;
  
  // meta modes
  private static final int LINE_OFFSET_INCREMENT = 1;
  private static final int A_LINEAR_INCREMENT = 2;
  private static final int B_LINEAR_INCREMENT = 3;
  private static final int C_LINEAR_INCREMENT = 4;
  private static final int D_LINEAR_INCREMENT = 4;
  private static final int E_LINEAR_INCREMENT = 5;
  private static final int F_LINEAR_INCREMENT = 6;
  private static final int A_B_LINEAR_INCREMENT = 5;
  
  private double a_param = 2; // numerator of k in rose curve equations,   k = kn/kd  (n1 in supershape equation)
  private double b_param = 1; // denominator of k in rose curve equations, k = kn/kd  (n2 in supershape equation)
  private double c_param = 0; // often called "c" in rose curve modifier equations    (n3 in supershape equation)
  private double d_param = 0; // used for n3 in supershape equation
  
  private double a, b, c, d;
  
  // rhodonea vars
  // private double kn, kd, k, radial_offset; // k = kn/kd
  // epitrochoid / hypotrochoid vars
  private double a_radius, b_radius, c_radius;
  
  private double line_count = 360;
  private double line_offset_param = 71;  // specified in degrees
  private double render_mode = STANDARD_LINES;
  
  private double line_offset;
  private double step_size_radians;
  private double cycles; // 1 cycle = 2*PI
  
  private double show_points_param = 0;
  private double show_curve_param = 0;
  
  private double line_thickness_param = 0.1;
  private double point_thickness_param = 1;
  private double curve_thickness_param = 0.5;
  private double line_thickness, point_thickness, curve_thickness;
  
  private boolean diff_mode = false;
  private int curve_mode = RHODONEA;
  // private boolean direct_color = true;
  private int color_measure = NONE;
  private int color_gradient = NORMAL;
  private int color_thresholding = PERCENT;
  
//  private double color_scaling = 100;
  private double color_low_thresh = 0.3;
  private double color_high_thresh = 2.0;
  
  private int filter_count = 0;
  private List filters = new ArrayList();
  
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
  
  /**
   *  only using z coordinate for specific modes
   */
  class DoublePoint3D {
    public double x;
    public double y;
    public double z;
  }
  private DoublePoint3D end_point1 = new DoublePoint3D();
  private DoublePoint3D end_point2 = new DoublePoint3D();
  private DoublePoint3D curve_point = new DoublePoint3D();
  
  class MaurerFilter {
    public int mode = BAND_PASS_VALUE; // off, percentile/value/?, bandpass/bandstop
    public int measure = LINE_LENGTH_LINES;// line length, angle, etc.
    public int operator = AND;  // AND, OR, XOR, ANOTB, BNOTA
    public double low_thresh = 0;
    public double high_thresh = 1;
  }
  
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
    
    // double show_sum = abs(show_lines_param) + abs(show_circles_param) + abs(show_points_param) + abs(show_curve_param);
    // line_fraction = abs(show_lines_param) / show_sum;
    // circle_fraction = abs(show_circles_param) / show_sum;
    // point_fraction = abs(show_points_param) / show_sum;
    // curve_fraction = abs(show_curve_param) / show_sum;
    // line_threshold = line_fraction;
    // circle_threshold = line_threshold + circle_fraction;
    // point_threshold = circle_threshold + point_fraction;
    // point_half_threshold = circle_threshold + (point_fraction/2);
    
    line_thickness = line_thickness_param / 100;
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
  public double getPointAngle(DoublePoint3D point1, DoublePoint3D point2) {
    double a1 = atan2(point1.y, point1.x);
    double a2 = atan2(point2.y, point2.x);
    
    if (a1 < 0) { a1 += M_2PI; }  // map to [0..2Pi]
    if (a2 < 0) { a2 += M_2PI; }  // map to [0..2Pi]
    double point_angle = abs(a2 - a1);
    if (point_angle > M_PI) { point_angle = M_2PI - point_angle; }
    return point_angle;
  }
  
  public DoublePoint3D getCurveCoords(double theta, DoublePoint3D point) {
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
    double rout;
    double xout = 0, yout = 0, zout = 0;
    
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
    
    // yoffset = [+-] m * d / (sqrt(1 + m^2))
    double xoffset=0, yoffset=0, zoffset = 0;
    
    double line_delta = 0;
    double midlength = line_length/2;  // use midlength of Maurer line as radius
    double rnd = pContext.random();
    double xmid = (x1 + x2)/2.0;
    double ymid = (y1 + y2)/2.0;

    boolean use_render_mode = true;
    if (show_points_param > 0 || show_curve_param > 0) {
      /**
       *  overrides of rendering mode
       */
      double rand2 = Math.random();
      if (rand2 <= show_points_param) {
        // drawing Maurer anchor points instead of specified render_mode
        use_render_mode = false;
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
        if (rnd <= (show_points_param/2)) {
          xout = x1 + xoffset;
          yout = y1 + yoffset;
        }
        else {
          xout = x2 + xoffset;
          yout = y2 + yoffset;
        }
        zout = 0;
      }
      else if (rand2 <= (show_points_param + show_curve_param)) {
        // drawing base curve instead of specified render_mode
        use_render_mode = false;
        if (curve_thickness != 0) {
          xout = x + ((pContext.random() - 0.5) * curve_thickness);
          yout = y + ((pContext.random() - 0.5) * curve_thickness);
        }
        else {
          xout = x;
          yout = y;
        }
        zout = 0;
      }
      else { use_render_mode = true;
         xout = 0;
         yout = 0;
      }
    }
    if (use_render_mode) {
      /**
       *   RENDER MODES
       */
      if (render_mode == STANDARD_LINES) {
        // draw lines
        line_delta = Math.random() * line_length;
        xoffset = line_delta / Math.sqrt(1 + m*m);
        if (x2 < x1) { xoffset = -1 * xoffset; }  // determine sign based on p2
        yoffset = Math.abs(m * xoffset);
        if (y2 < y1) { yoffset = -1 * yoffset; }
        xout = x1 + xoffset;
        yout = y1 + yoffset;
      }
      else if (render_mode == CIRCLES1) {
        // draw circles
        //   circles centered on midpoint of "Maurer line"
        //   and use midlength of Maurer line as radius
        double ang = Math.random() * M_2PI;
        double rad = midlength;
        xout = xmid + (rad * sin(ang));
        yout = ymid + (rad * cos(ang));
        line_delta = ang;
      }
      else if (render_mode == ZCIRCLES) {
        double ang = Math.random() * M_2PI;
        double rad = midlength;
        line_delta = rad * cos(ang);
        zout = rad * sin(ang);
        // move along line, out from midpoint by line_delta
        xout = xmid + (line_delta * cos(raw_line_angle));
        yout = ymid + (line_delta * sin(raw_line_angle));
      }
      
      /*      else if (render_mode == CIRCLES2) {
      
      }
      */
      else {
        xout = 0;
        yout = 0;
        zout = 0;
      }
      // handling thickness
      if (line_thickness != 0) {
        xout += ((pContext.random() - 0.5) * line_thickness);
        yout += ((pContext.random() - 0.5) * line_thickness);
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
    
    
    
    
    //
    // FILTERING
    //
    
    //     if (rnd < line_threshold) {
    pVarTP.doHide = false;
    boolean cumulative_pass = true;
    for (int findex=0; findex < filter_count; findex++)  {
      MaurerFilter mfilter = (MaurerFilter)filters.get(findex);
      int fmode = mfilter.mode;
      // if filter mode is OFF, then skip this filter
      if (fmode != OFF) {
        int measure = mfilter.measure;
        int op = mfilter.operator;
        double low_thresh, high_thresh;
        double val;
        double[] sampled_vals;
        if (measure == LINE_LENGTH_LINES) {
          val = line_length;
          sampled_vals = sampled_line_lengths;
        }
        else if (measure == LINE_ANGLE_LINES) {
          val = line_angle;
          sampled_vals = sampled_line_angles;
        }
        else if (measure == POINT_ANGLE_LINES) {
          val = point_angle;
          sampled_vals = sampled_point_angles;
        }
        else if (measure == DISTANCE_ALONG_LINE_POINTS) {
          val = line_delta;
          sampled_vals = null;
        }
        else if (measure == DISTANCE_FROM_MIDLINE_POINTS) {
          val = abs(line_delta - midlength);
          sampled_vals = null;
        }
        else if (measure == Z_MINMAX_POINTS) {
          // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
          // val = abs(zout*2); // min/max zout should be
          val = zout + (line_length/2); //  range of val should be 0 to line_length;
          sampled_vals = sampled_line_lengths;
        }
        else if (measure == Z_ABSOLUTE_MINMAX_POINTS) {
          // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
          val = abs(zout) * 2; // range of val should be 0 to line_length
          sampled_vals = sampled_line_lengths;
        }
        else {
          // default to line length
          val = line_length;
          sampled_vals = sampled_line_lengths;
        }
        if (fmode == BAND_PASS_VALUE|| fmode == BAND_STOP_VALUE) {
          low_thresh = mfilter.low_thresh;
          high_thresh = mfilter.high_thresh;
        }
        else if (fmode == BAND_PASS_PERCENT || fmode == BAND_STOP_PERCENT) {
          int low_index, high_index;
          // should probably round here instead of flooring with (int) cast?
          if (mfilter.low_thresh <= 0 || mfilter.low_thresh >= 1) { low_index = 0; }
          else { low_index = (int)(mfilter.low_thresh * sampled_vals.length); }
          // if high_thresh not in (0 -> 1.0) exclusive range, clamp  at 100%
          if (mfilter.high_thresh >= 1 || mfilter.high_thresh <= 0) { high_index = (sampled_vals.length - 1); }
          else { high_index = (int)(mfilter.high_thresh * sampled_vals.length); }
          low_thresh = sampled_vals[low_index];
          high_thresh = sampled_vals[high_index];
        }
        else { // default to values
          low_thresh = mfilter.low_thresh;
          high_thresh = mfilter.high_thresh;
        }
        
        boolean inband =  (val >= low_thresh && val <= high_thresh);
        boolean current_pass;
        if (fmode == BAND_PASS_VALUE || fmode == BAND_PASS_PERCENT) {
          current_pass = inband;
        }
        else if (fmode == BAND_STOP_VALUE || fmode == BAND_STOP_PERCENT) {
          // effectively the same as additional NOT operation on this filter
          current_pass = !inband;
        }
        else {
          // default to bandpass: passing values that are within filter band
          current_pass = inband;
        }
        if (findex == 0) { // no combo operator for first filter
          cumulative_pass = current_pass;
        }
        else {
          if (op == AND) {
            cumulative_pass = cumulative_pass && current_pass;
          }
          else if (op == OR) {
            cumulative_pass = cumulative_pass || current_pass;
          }
          else if (op == XOR) {
            cumulative_pass = (cumulative_pass && !current_pass) || (!cumulative_pass && current_pass);
          }
          else if (op == ANOTB) {
            cumulative_pass = cumulative_pass && !current_pass;
          }
          else if (op == BNOTA) {
            cumulative_pass = !cumulative_pass && current_pass;
          }
          else { // default to AND
            cumulative_pass = cumulative_pass && current_pass;
          }
        }
      }
    }
    pVarTP.doHide = !cumulative_pass;
    //
    // END FILTERING
    //
    //  }
    
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
      double val;
      double[] sampled_vals;
      if (color_measure == LINE_LENGTH_LINES) {
        val = line_length;
        sampled_vals = sampled_line_lengths;
      }
      else if (color_measure == LINE_ANGLE_LINES) {
        val = line_angle;
        sampled_vals = sampled_line_angles;
      }
      else if (color_measure == POINT_ANGLE_LINES) {
        val = point_angle;
        sampled_vals = sampled_point_angles;
      }
      else if (color_measure == DISTANCE_ALONG_LINE_POINTS) {
        val = line_delta;
        sampled_vals = null;
      }
      else if (color_measure == DISTANCE_FROM_MIDLINE_POINTS) {
        val = abs(line_delta - midlength);
        sampled_vals = null;
      }
      else if (color_measure == META_INDEX && meta_mode != OFF) {
        val = current_meta_step;
        sampled_vals = null;
      }
      else if (color_measure == Z_MINMAX_POINTS) {
        // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
        // val = abs(zout*2); // min/max zout should be
        val = zout + (line_length/2); //  range of val should be 0 to line_length;
        sampled_vals = sampled_line_lengths;
      }
      else if (color_measure == Z_ABSOLUTE_MINMAX_POINTS) {
        // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
        val = abs(zout) * 2; // range of val should be 0 to line_length
        sampled_vals = sampled_line_lengths;
      }
      else { // default to LINE_LENGTH_LINES
        val = line_length;
        sampled_vals = sampled_line_lengths;
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
          low_value = sampled_vals[low_index];
          high_value = sampled_vals[high_index];
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
      
      if (val < low_value) { baseColor = 0; }
      else if (val >= high_value) { baseColor = 255; }
      else { baseColor = ((val - low_value)/(high_value - low_value)) * 255; }
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
    LinkedHashMap plist = getParameters();
    return (String[])plist.keySet().toArray(new String[0]);
//    return paramNames;
  }
  
  @Override
  public Object[] getParameterValues() {
    LinkedHashMap plist = getParameters();
    return plist.values().toArray();
  }
  
  public LinkedHashMap getParameters() {
    LinkedHashMap plist = new LinkedHashMap(100);
    
    plist.put(PARAM_A, a_param);
    plist.put(PARAM_B, b_param);
    plist.put(PARAM_C, c_param);
    plist.put(PARAM_D, d_param);
    plist.put(PARAM_RENDER_MODE, render_mode);
    plist.put(PARAM_LINE_OFFSET_DEGREES, line_offset_param);
    plist.put(PARAM_LINE_COUNT, line_count);
    plist.put(PARAM_CURVE_MODE, curve_mode);
    
    plist.put(PARAM_DIRECT_COLOR_MEASURE, color_measure);
    plist.put(PARAM_DIRECT_COLOR_GRADIENT, color_gradient);
    plist.put(PARAM_DIRECT_COLOR_THRESHOLDING, color_thresholding);
    plist.put(PARAM_COLOR_LOW_THRESH, color_low_thresh);
    plist.put(PARAM_COLOR_HIGH_THRESH, color_high_thresh);
    plist.put(PARAM_FILTER_COUNT, filter_count);
    while (filter_count > filters.size()) {
      MaurerFilter mfil = new MaurerFilter();
      System.out.println("get, adding filter index: " + filters.size());
      filters.add(mfil);
    }
    for (int i=0; i<filter_count; i++) {
      MaurerFilter mfil = (MaurerFilter)filters.get(i);
      String filter_base = PARAM_FILTER_PREFIX + (i+1) + "_";
      // operator is about how to combine with previous filter, so first filter has no operator
      if (i != 0) {
        plist.put(filter_base + PARAM_FILTER_OPERATOR_SUFFIX, mfil.operator); // AND, OR, XOR, ANOTB, BNOTA }
      }
      plist.put(filter_base + PARAM_FILTER_MODE_SUFFIX, mfil.mode); // off, percentile/value/?, bandpass/bandstop
      plist.put(filter_base + PARAM_FILTER_MEASURE_SUFFIX, mfil.measure); // line length, angle, etc.
      plist.put(filter_base + PARAM_FILTER_LOW_SUFFIX, mfil.low_thresh);
      plist.put(filter_base + PARAM_FILTER_HIGH_SUFFIX, mfil.high_thresh);
      if (DEBUG_DYNAMIC_PARAMETERS) {
        System.out.println("get, adding param: " + filter_base + PARAM_FILTER_LOW_SUFFIX + ", " + mfil.low_thresh);
      }
    }
    plist.put(PARAM_RANDOMIZE, (randomize ? 1 : 0));
    plist.put(PARAM_DIFF_MODE, (diff_mode ? 1 : 0));
    plist.put(PARAM_META_MODE, meta_mode);
    plist.put(PARAM_META_MIN_VALUE, meta_min_value);
    plist.put(PARAM_META_MAX_VALUE, meta_max_value);
    plist.put(PARAM_META_STEP_VALUE, meta_step_value);
    plist.put(PARAM_SHOW_POINTS, show_points_param);
    plist.put(PARAM_SHOW_CURVE, show_curve_param);
    plist.put(PARAM_LINE_THICKNESS, line_thickness_param);
    plist.put(PARAM_POINT_THICKNESS, point_thickness_param);
    plist.put(PARAM_CURVE_THICKNESS, curve_thickness_param);
    return plist;
  };
  
  @Override
  public void setParameter(String pName, double pValue) {
    if (DEBUG_DYNAMIC_PARAMETERS) { System.out.println("setParameter called: " + pName + ", " + pValue); }
    if (PARAM_A.equalsIgnoreCase(pName))
      a_param = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b_param = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c_param = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d_param = pValue;
    else if (PARAM_RENDER_MODE.equalsIgnoreCase(pName))
      render_mode = (int)pValue;
    else if (PARAM_LINE_OFFSET_DEGREES.equalsIgnoreCase(pName))
      line_offset_param = pValue;
    else if (PARAM_LINE_COUNT.equalsIgnoreCase(pName))
      line_count = pValue;
    else if (PARAM_CURVE_MODE.equalsIgnoreCase(pName))
      curve_mode = (int)pValue;
    
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
    else if (PARAM_FILTER_COUNT.equalsIgnoreCase(pName)) {
      filter_count = (int)pValue;
      if (filter_count > filters.size()) {
        while (filter_count > filters.size()) {
          MaurerFilter mfil = new MaurerFilter();
          filters.add(mfil);
          if (DEBUG_DYNAMIC_PARAMETERS) { System.out.println("set, count changed: " + filters.size() + ", low: " + mfil.low_thresh); }
        }
      }
    }
    
    else if (pName.startsWith(PARAM_FILTER_PREFIX)) {
      String temp1 = pName.replaceFirst(PARAM_FILTER_PREFIX, "");
      String fnum = temp1.substring(0, temp1.indexOf("_"));
      String suffix = pName.substring(pName.indexOf('_') + 1);
      int findex = Integer.parseInt(fnum) - 1;
      MaurerFilter mfil = (MaurerFilter)filters.get(findex);
      if (PARAM_FILTER_OPERATOR_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.operator = (int)pValue;
      }
      else if (PARAM_FILTER_MODE_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.mode = (int)pValue;
      }
      else if (PARAM_FILTER_MEASURE_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.measure = (int)pValue;
      }
      else if (PARAM_FILTER_LOW_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.low_thresh= pValue;
        if (findex == 1) {
          if (DEBUG_DYNAMIC_PARAMETERS) { System.out.println("set, low changed: " + mfil.low_thresh); }
        }
      }
      else if (PARAM_FILTER_HIGH_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.high_thresh = pValue;
      }
      else {
        throw new IllegalArgumentException(pName);
      }
      
    }
    else if (PARAM_RANDOMIZE.equalsIgnoreCase(pName)) {
      randomize = (pValue >= 1);
    }
    else if (PARAM_META_MODE.equalsIgnoreCase(pName)) {
      meta_mode = (int)pValue;
    }
    else if (PARAM_META_MIN_VALUE.equalsIgnoreCase(pName)) {
      meta_min_value = pValue;
    }
    else if (PARAM_META_MAX_VALUE.equalsIgnoreCase(pName)) {
      meta_max_value = pValue;
    }
    else if (PARAM_META_STEP_VALUE.equalsIgnoreCase(pName)) {
      this.meta_step_value = pValue;
    }
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
    else if (PARAM_DIFF_MODE.equalsIgnoreCase(pName)) {
      diff_mode = (pValue >= 1);
    }
    else
      throw new IllegalArgumentException(pName);
  }
  
  @Override
  public String getName() {
    return "maurer_lines";
  }
  
  @Override
  public VariationFunc makeCopy() {
    return super.makeCopy();
  }
  
  @Override
  public boolean dynamicParameterExpansion() { return true; }
  
  @Override
  public boolean dynamicParameterExpansion(String paramName) {
    if (paramName == PARAM_FILTER_COUNT) { return true; }
    else { return false; }
  }
}
