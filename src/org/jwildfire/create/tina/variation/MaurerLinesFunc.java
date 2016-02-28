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
import java.math.BigInteger;
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
import static org.jwildfire.base.mathlib.MathLib.sqr;

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
  private boolean DEBUG_COSETS = false;
  
  // PARAM_KNUMER ==> PARAM_A
  // PARAM_KDENOM ==> PARAM_B
  // PARAM_RADIAL_OFFSET ==> PARAM_C
  // PARAM_THICKNESS ==> split into PARAM_CURVE_THICKNESS, PARAM_LINE_THICKNESS, PARAM_POINT_THICKNESS
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_THETA_STEP_SIZE_DEGREES = "theta_step_size";
  private static final String PARAM_INITIAL_THETA_DEGREES = "initial_theta";
  private static final String PARAM_LINE_COUNT = "line_count";
  private static final String PARAM_RENDER_MODE = "render_mode";
  private static final String PARAM_CURVE_MODE = "curve_mode";
  private static final String PARAM_USE_COSETS = "use_cosets";
  
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
  
  private static final String PARAM_RENDER_MODIFIER1 = "render_modifier1";
  private static final String PARAM_RENDER_MODIFIER2 = "render_modifier2";
  private static final String PARAM_RENDER_MODIFIER3 = "render_modifier3";
  
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
  private static final int POLYGON = 1;
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
  private static final int QUADRATIC_BEZIER = 7;  // render a Bezier curve with origin as control point
  private static final int QUADRATIC_BEZIER_REFLECTED = 8;  // render a Bezier curve with origin as control point, then reflect across the Maurer line
  private static final int QUADRATIC_BEZIER_BOTH = 9;  // render a Bezier curve with origin as control point, then reflect across the Maurer line
  private static final int QUADRATIC_BEZIER_ALTERNATE = 10;  // render a Bezier curve with origin as control point, then reflect across the Maurer line
  private static final int ZBEZIER = 11;
  private static final int SINE_WAVE = 12; 
  private static final int SINE_WAVE_REFLECTED = 13;
  private static final int SINE_WAVE_BOTH = 14;
  private static final int SINE_WAVE_ALTERNATE = 15;
  private static final int ZSINE = 16;
  private static final int ZSINE_BOTH = 17;
  private static final int ZSINE_REFLECTED = 18;
  private static final int ZSINE_ALTERNATE = 19;
  private static final int ZBEZIER2 = 21;
  private static final int ELLIPSES = 22;
  private static final int ZELLIPSES = 23;
  private static final int KOCHANEK_BARTELS_SPLINE = 25;
  private static final int CUBIC_HERMITE_SPLINE = 26;
  
  private static final int NORMAL = 0;
  private static final int NONE = 0;
  private static final int OFF = 0;
  
  // direct color gradient -- should be near bottom of parameter list,
  //   since (now that colormap options is working) will be almost always COLORMAP
  private static final int COLORMAP_CLAMP = 1;
  private static final int COLORMAP_WRAP = 2;
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
  private static final int DISTANCE_FROM_NEAREST_END_POINTS = 9;
  // private static final int WEIGHTED_LINE_LENGTH = 5;
  
  // measure thresholding
  private static final int PERCENT = 0;
  private static final int VALUE = 1;
  // other possibilties -- distance or deviation from mean?
  
  // measure filtering
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
  private static final int D_LINEAR_INCREMENT = 5;
  private static final int E_LINEAR_INCREMENT = 6;
  private static final int F_LINEAR_INCREMENT = 7;
  private static final int INITIAL_OFFSET = 8;
  private static final int A_B_LINEAR_INCREMENT = 9;
  
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
  private double theta_step_size_param = 71;  // specified in degrees
  private double initial_theta_param = 0; // specified in degrees
  private double render_mode = STANDARD_LINES;
  private double render_modifier1 = 1.0;
  private double render_modifier2 = 1.0;
  private double render_modifier3 = 1.0;
  
  // private double coset_line_offset;
  private double theta_step_radians;
  private double initial_theta_radians;
  private double cycles; // 1 cycle = 2*PI
  
  private double show_points_param = 0;
  private double show_curve_param = 0;
  
  private double line_thickness_param = 0.1;
  private double point_thickness_param = 1;
  private double curve_thickness_param = 0.5;
  private double line_thickness, point_thickness, curve_thickness;
  
  private boolean diff_mode = false;
  private int curve_mode = RHODONEA;
  private int direct_color_gradient = OFF;
  private int direct_color_measure = LINE_LENGTH_LINES;
  private int direct_color_thesholding = PERCENT;
  
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

  /* 
  *  vars for using cosets method from original Maurer Rose paper:
  *    line_count becomes the (integer) number of "degrees" circle is divided into 
  *    coset_line_offset is rounded to nearest integer
  *    initial offset is ignored (considered 0)
  *    WARNING: certain meta_modes override use_cosets setting:
  *        LINE_OFFSET_INCREMENT
  *        INITIAL_OFFSET
  */
  private boolean use_cosets = false;  
  int coset_line_offset;
  int coset_circle_div;
  int coset_gcd;
  int coset_lcm;
  double coset_cycles;
  double coset_step_size;
  
  private boolean randomize = false;
  private int sample_size = 1000;
  private double[] sampled_line_lengths = new double[sample_size];
  private double[] sampled_line_angles = new double[sample_size];
  private double[] sampled_point_angles = new double[sample_size];
  
  private long count = 0;
  
  ParametricCurve curve;
  
  class ParametricCurve {
    public void getCurvePoint(double t, DoublePoint2D point) { }

    public DoublePoint2D getCurvePoint(double t) {
      DoublePoint2D outpoint = new DoublePoint2D();
      getCurvePoint(t, outpoint);
      return outpoint;
    }
  }
  
  class RhodoneaCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double k = a/b;
      double r = cos(k * t) + c;
      // if (DEBUG_RELATIVE_ANGLE && count % 100000 == 0) { System.out.println("radius = " + r); }
      point.x = r * cos(t);
      point.y = r * sin(t);
    }
            
  }
  
  class CircleCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double r = a;
      point.x = r * cos(t);
      point.y = r * sin(t);
    }
  }
  
  class PolygonCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
    }
  }
  
  class EllipseCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      point.x = a * cos(t);
      point.y = b * sin(t);
    }
  }
  
  class EpitrochoidCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      // option 1:
      // double x = ((a_radius + b_radius) * cos(theta)) - (c_radius * cos(((a_radius + b_radius)/b_radius) * theta));
      // double y = ((a_radius + b_radius) * sin(theta)) - (c_radius * sin(((a_radius + b_radius)/b_radius) * theta));
      // option 2:
      double x = ((a + b) * cos(t)) - (c * cos(((a + b)/b) * t));
      double y = ((a + b) * sin(t)) - (c * sin(((a + b)/b) * t));
      point.x = x;
      point.y = y;
    }
  }
  
  class HypotrochoidCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      // option 1:
      // double x = ((a_radius - b_radius) * cos(theta)) + (c_radius * cos(((a_radius - b_radius)/b_radius) * theta));
      // double y = ((a_radius - b_radius) * sin(theta)) - (c_radius * sin(((a_radius - b_radius)/b_radius) * theta));
      // option 2:
      double x = ((a - b) * cos(t)) + (c * cos(((a - b)/b) * t));
      double y = ((a - b) * sin(t)) - (c * sin(((a - b)/b) * t));
      point.x = x;
      point.y = y;
    }
  }
  
  class EpispiralCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double k = a/b;
      double r = (1/cos(k * t)) + c;
      point.x = r * cos(t);
      point.y = r * sin(t);
    }
  }
  
  class LissajousCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      // x = A * sin(a*t + d)
      // y = B * sin(b*t);
      // for now keep A = B = 1
      double x = sin((a*t) + c);
      double y = sin(b*t);
      point.x = x;
      point.y = y;
    }
  }
  
  class SupershapeCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      // original supershape variables: a, b, line_slope, n1, n2, n3
      // a = line_slope
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
              (pow( fabs( (cos(m * t / 4))/a1), n2) +
                      pow( fabs( (sin(m * t / 4))/b1), n3)),
              (-1/n1));
      point.x = r * cos(t);
      point.y = r * sin(t);
    }
  }
  
  class StarrCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double r = 2 + (sin(a * t)/2);
      double t2 = t + (sin(b * t)/c);
      point.x = r * cos(t2);
      point.y = r * sin(t2);
    }
  }
  
  class FarrisMysteryCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      point.x = cos(t)/a + cos(6*t)/b + sin(14*t)/c;
      point.y = sin(t)/a + sin(6*t)/b + cos(14*t)/c;
      // can also be represented more concisely with complex numbers:
      //   c(t) = (e^(i*t))/a + (e^(6*i*t))/b + (e^(-14*i*t))/c
      //   should break out into a fully parameterized version with exponent parameters as well
    }
  }
  
  class WagonFancifulCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      point.x = sin(a * t) * cos(c * t);
      point.y = sin(b * t) * sin(c * t);
    }
  }
  
  class FayButterflyCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      // r = e^cos(t) - 2cos(4t) - sin^5(t/12)
      // y = sin(t)*r
      // x = cos(t)*r
      double r = 0.5 * (exp(cos(t)) - (2 * cos(4 * t)) - pow(sin(t / 12), 5));
      point.x = r * sin(t);
      point.y = r * cos(t);
    }
  }
  
  class Rigge1Curve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double r = (1 - cos(a * t)) + (1 - cos(a * b * t));
      point.x = r * cos(t);
      point.y = r * sin(t);
    }
  }
  
  class Rigge2Curve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double r = (1 - cos(a * t)) - (1 - cos(a * b * t));
      point.x = r * cos(t);
      point.y = r * sin(t);
    }
  }
  
  
  /**
   *  only using z coordinate for specific modes
   */
  
  class DoublePoint2D {
    public double x;
    public double y;
  }

  class DoublePoint3D extends DoublePoint2D {
    public double z;
  }

    private DoublePoint3D curve_point = new DoublePoint3D();
  private DoublePoint3D end_point1 = new DoublePoint3D();
  private DoublePoint3D end_point2 = new DoublePoint3D();
  // end_point0 (point on curve one step before current Maurer line endpoints)
  //     used for Kochanek-Bartels spline interpolation, possibly other uses in the future
  private DoublePoint3D end_point0 = new DoublePoint3D();
  // end_point1 (point on curve one step after current Maurer line endpoints);
  //     used for Kochanek-Bartels spline interpolation, possibly other uses in the future
  private DoublePoint3D end_point3 = new DoublePoint3D();

  
  class MaurerFilter {
    public int mode = BAND_PASS_VALUE; // off, percentile/value/?, bandpass/bandstop
    public int measure = LINE_LENGTH_LINES;// line length, angle, etc.
    public int operator = AND;  // AND, OR, XOR, ANOTB, BNOTA
    public double low_thresh = 0;
    public double high_thresh = 1;
  }
  
  // need this method since can't do a class.newInstance() call on inner classes
  ///    otherwise would keep array of curve classes and instantiate via curve_classes[curve_mode].newInstance()
  public ParametricCurve createCurve(int curve_index) {
    ParametricCurve new_curve;
    if (curve_index == CIRCLE) { new_curve = new CircleCurve(); }
    else if (curve_index == POLYGON) { new_curve = new PolygonCurve(); }
    else if (curve_index == ELLIPSE) { new_curve = new PolygonCurve(); }
    else if (curve_index == RHODONEA) { new_curve = new RhodoneaCurve(); }
    else if (curve_index == EPITROCHOID) { new_curve = new EpitrochoidCurve(); }
    else if (curve_index == HYPOTROCHOID) { new_curve = new HypotrochoidCurve(); }
    else if (curve_index == LISSAJOUS) { new_curve = new LissajousCurve(); }
    else if (curve_index == EPISPIRAL) { new_curve = new EpispiralCurve(); }
    else if (curve_index == SUPERSHAPE) { new_curve = new SupershapeCurve(); }
    else if (curve_index == STARR_CURVE) { new_curve = new StarrCurve(); }
    else if (curve_index == FARRIS_MYSTERY_CURVE) { new_curve = new FarrisMysteryCurve(); }
    else if (curve_index == WAGON_FANCIFUL_CURVE) { new_curve = new WagonFancifulCurve(); }
    else if (curve_index == FAY_BUTTERFLY) { new_curve = new FayButterflyCurve(); }
    else if (curve_index == RIGGE1) { new_curve = new Rigge1Curve(); }
    else if (curve_index == RIGGE2) { new_curve = new Rigge2Curve(); }
    else { new_curve = new CircleCurve(); } // default to circle curve
    return new_curve;
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
    curve = createCurve(curve_mode);
    
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
    
    line_thickness = line_thickness_param / 100;
    point_thickness = point_thickness_param / 100;
    curve_thickness = curve_thickness_param / 100;

    theta_step_radians = M_2PI * (theta_step_size_param / 360);
    initial_theta_radians = M_2PI * (initial_theta_param / 360);
    cycles = (line_count * theta_step_radians) / M_2PI;

    coset_line_offset = (int)Math.floor(theta_step_size_param);
    coset_circle_div = (int)Math.floor(line_count);
    coset_gcd = gcd(coset_line_offset, coset_circle_div);
    coset_lcm = lcm(coset_line_offset, coset_circle_div);
    coset_cycles = coset_lcm/coset_circle_div; 
    coset_step_size = M_2PI * ((double)coset_line_offset / (double)coset_circle_div);
    
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
      // theta1 = theta_step_radians * (int)(Math.random()*line_count);
      theta1 = Math.random() * cycles * M_2PI;
      // reusing end_point1
//      end_point1 = getCurveCoords(theta1, end_point1);
      curve.getCurvePoint(theta1, end_point1);
      x1 = end_point1.x;
      y1 = end_point1.y;
      // double sampled_step_size = getStepSize();
      theta2 = theta1 + theta_step_radians;
      // reusing end_point2
      // end_point2 = getCurveCoords(theta2, end_point2);
      curve.getCurvePoint(theta2, end_point2);
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
  
  /*
  *   get GCD (greatest common denominator) of two integers 
  *   guaranteed to always return positive (or zero)
  */
  public int gcd(int a, int b) {
    BigInteger aBig = BigInteger.valueOf(a);
    BigInteger bBig = BigInteger.valueOf(b);
    BigInteger result = aBig.gcd(bBig);  // BigInteger.coset_gcd always returns postivie (or zero)
    return result.intValue();
  }
  
  /*
   *   get LCM (least common multiple) of two integers
   *   guaranteed to always return positive (or zero);
   */
  public int lcm(int a, int b) {
    return abs(a) * (abs(b) / gcd(a, b));
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
  
  public void setValues() {
    a = a_param;
    b = b_param;
    c = c_param;
    d = d_param;
    
    if (use_cosets) {
      theta_step_radians = coset_step_size;
      cycles =  coset_cycles;
      // if relatively prime, then don't use cosets
      if (coset_gcd == 1) {  // relatively prime if don't share a common denominator other than 1
        initial_theta_radians = 0;
        if (DEBUG_COSETS && count % 50000 == 0) {
          System.out.println("gcd is 1, lcm: " + coset_lcm + ", cycles: " + cycles + ", zero initial_offset");
        }
      }
      else {
        // pick one of the cosets [0, 1, ... coset_gcd-1 ], use this to decide the initial_offset
        double initial_offset = (int)(Math.round(Math.random() * (coset_gcd-1)));
        initial_theta_radians = M_2PI * (initial_offset / (double)coset_circle_div);
        if (DEBUG_COSETS && count % 50000 == 0) {
          System.out.println("gcd: " + coset_gcd + ", lcm: " + coset_lcm + ", cycles: " + cycles + ", initial_offset: " + initial_offset);
        }
      }
    }
    
    if (meta_mode != OFF) {
      // determine which meta-step to use
      //    should round instead?
      current_meta_step = (int)(Math.random() * meta_steps);
      double meta_value = meta_min_value + (current_meta_step * meta_step_value);
      if (meta_mode == LINE_OFFSET_INCREMENT) {
        // coset_line_offset = meta_min_value + (current_meta_step * meta_step_value);'
        double line_offset = meta_value;
        theta_step_radians = M_2PI * (line_offset / 360);
        cycles = (line_count * theta_step_radians) / M_2PI;
        // actual_step_size = meta_min_step_radians + (meta_step_diff_radians * current_meta_step);
        //      actual_step_size = M_2PI * (((int)(Math.random()* 60)+10)/360.0);
      }
      else if (meta_mode == INITIAL_OFFSET) {
        double initial_offset = meta_value;
        initial_theta_radians = M_2PI * (initial_offset / 360);
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
    // curve_point = getCurveCoords(t, curve_point);
    curve.getCurvePoint(t, curve_point);
    
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
    step_number = floor(t/theta_step_radians);
    
    // find polar and cartesian coordinates for endpoints of Maure Rose line
    theta1 = (step_number * theta_step_radians) + initial_theta_radians;
    theta2 = theta1 + theta_step_radians;
    
    // reusing end_point1
    // end_point1 = getCurveCoords(theta1, end_point1);
    curve.getCurvePoint(theta1, end_point1);
    x1 = end_point1.x;
    y1 = end_point1.y;
    // reusing end_point2
    // end_point2 = getCurveCoords(theta2, end_point2);
    curve.getCurvePoint(theta2, end_point2);
    x2 = end_point2.x;
    y2 = end_point2.y;
   /* if (DEBUG_COSETS && count % 50000 == 0) {
      System.out.println("step_number: " + step_number + ", theta1: " + theta1 + ", theta2: " + theta2);
      System.out.println("point1: " + x1 + ", " + y1 + "  point2: " + x2 + ", " + y2 );
    }
    */
    
    // find the slope and length of the line
    double ydiff = y2 - y1;
    double xdiff = x2 - x1;
   /* if (xdiff == 0) {
      xdiff =
    }
    */

    // slope of line (m in y=mx+b line equation)
    double line_slope = ydiff / xdiff;  
    // y-intercept of line (b in y=mx+b line equation)
    double line_intercept; 
    // special-casing for slope being NaN (xdiff = 0) ==> line_intercept set to NaN as well 
    if (Double.isNaN(line_slope)) {
      line_intercept = Double.NaN;
    }     
    else { line_intercept = y1 - (line_slope * x1); }
    
    double point_angle = getPointAngle(end_point1, end_point2);
    
    double raw_line_angle = atan2(ydiff, xdiff);  // atan2 range is [-Pi..+Pi]
    // if (raw_line_angle < 0) { raw_line_angle += M_2PI; }   // map to range [0..+2Pi]
    // delta_from_yaxis should be 0 if parallel to y-axis, and M_PI/2 if parallel to x-axis
    double unscaled_line_angle = abs(abs(raw_line_angle) - (M_PI/2.0));
    // scale to range [0..1]; (0 parallel to y-axis, 1 parallel to x-axis)
    double line_angle = unscaled_line_angle / (M_PI/2.0);
    double line_length = Math.sqrt( (xdiff * xdiff) + (ydiff * ydiff));
    
    // yoffset = [+-] line_slope * d / (sqrt(1 + line_slope^2))
    // double xoffset=0, yoffset=0, zoffset = 0;
    
    double line_delta = 0;
    double midlength = line_length/2.0;  // use midlength of Maurer line as radius
    double rnd = pContext.random();
    double xmid = (x1 + x2)/2.0;
    double ymid = (y1 + y2)/2.0;

    boolean use_render_mode = true;
    if (show_points_param > 0 || show_curve_param > 0) {
      /**
       *  overrides of rendering mode
       */
      double xoffset=0, yoffset=0, zoffset = 0;
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
      double xoffset=0, yoffset=0, zoffset = 0;
      /**
       *   RENDER MODES
       */
      if (render_mode == STANDARD_LINES) {
        // draw lines
        line_delta = Math.random() * line_length;
        xoffset = line_delta / Math.sqrt(1 + line_slope*line_slope);
        if (x2 < x1) { xoffset = -1 * xoffset; }  // determine sign based on p2
        yoffset = Math.abs(line_slope * xoffset);
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
        // line_delta = ang;
        line_delta = sqrt(((x1-xout) * (x1-xout)) + ((y1-yout) * (y1-yout)));
      }
      else if (render_mode == ZCIRCLES) {
        double ang = Math.random() * M_2PI;
        double rad = midlength;
        double mid_delta = rad * cos(ang);
        zout = rad * sin(ang);
        // move along line, out from midpoint by mid_delta
        xout = xmid + (mid_delta * cos(raw_line_angle));
        yout = ymid + (mid_delta * sin(raw_line_angle));
        line_delta = sqrt(((x1-xout) * (x1-xout)) + ((y1-yout) * (y1-yout)));
      }

      else if (render_mode == QUADRATIC_BEZIER || render_mode == QUADRATIC_BEZIER_REFLECTED || 
              render_mode == QUADRATIC_BEZIER_BOTH || render_mode == QUADRATIC_BEZIER_ALTERNATE || 
              render_mode == ZBEZIER || render_mode == ZBEZIER2) {
        // use origin (0,0) as control point, and endpoints of Maurer line as Bezier curve endpoints
        //    (since using origin as control point, can drop middle term of standard Bezier curve calc
        double bt = Math.random();  // want bt => [0:1]
        double ax, ay;
        // use full formula with control point
        if (render_modifier1 != 1 || render_modifier2 != 1) {
          // render_modifier1 controls relative radius of control point
          //   (fraction of distance from midpoint of line to origin,
          //    so as render_modifier range: [0=>1] then control point radius range: [midpoint_radius=>0]
          // render_modifier2 controls relative angle of control point
          //    (angle from midpoint-origin line to control point)
          //    so if render_modifier = 1 then control point angle delta = 0
          double midpoint_radius = sqrt(xmid*xmid + ymid*ymid);
          double midpoint_angle = atan2(ymid, xmid);
          double cradius = (1 - render_modifier1) * midpoint_radius;
          double cangle = midpoint_angle + (M_2PI - (render_modifier2*M_2PI));
          // double cangle = midpoint_angle;
          double cx = cradius * cos(cangle);
          double cy = cradius * sin(cangle);
          ax = ((1-bt) * (1-bt) * x1) + (2*(1-bt)*bt*cx) + (bt * bt * x2);
          ay = ((1-bt) * (1-bt) * y1) + (2*(1-bt)*bt*cy) + (bt * bt * y2);
        }
        else {
          // control point is origin (0,0), so can drop control point term of quadratic Bezier
          //    and therefore skip midradius, midangle etc. control point calcs
          ax = ((1-bt) * (1-bt) * x1) + (bt * bt * x2);
          ay = ((1-bt) * (1-bt) * y1) + (bt * bt * y2);
        }
        
        line_delta = bt * line_length;
        if (render_mode == QUADRATIC_BEZIER) {
          xout = ax;
          yout = ay;
        }
        else if (render_mode == QUADRATIC_BEZIER_REFLECTED) {
        // double ax = ((1-bt) * (1-bt) * x1) + (bt * bt * x2);
        // double ay = ((1-bt) * (1-bt) * y1) + (bt * bt * y2);
        // now reflect point A across the Maurer line to get output point
          double bc = (((x2-x1)*(ax-x1)) + ((y2-y1)*(ay-y1))) / (((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
          xout = (2 * (x1 + ((x2-x1)*bc))) - ax;
          yout = (2 * (y1 + ((y2-y1)*bc))) - ay;
        }
        else if (render_mode == QUADRATIC_BEZIER_BOTH) {
          if (Math.random() < 0.5) {
            xout = ax;
            yout = ay;
          }
          else {
            double bc = (((x2-x1)*(ax-x1)) + ((y2-y1)*(ay-y1))) / (((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
            xout = (2 * (x1 + ((x2-x1)*bc))) - ax;
            yout = (2 * (y1 + ((y2-y1)*bc))) - ay;
          }
        }
        else if (render_mode == QUADRATIC_BEZIER_ALTERNATE) {
          // even steps use Bezier, odd steps use reflected Bezier
          if (step_number % 2 == 0) {
            xout = ax;
            yout = ay;
          }
          else {
            double bc = (((x2-x1)*(ax-x1)) + ((y2-y1)*(ay-y1))) / (((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
            xout = (2 * (x1 + ((x2-x1)*bc))) - ax;
            yout = (2 * (y1 + ((y2-y1)*bc))) - ay;
          }
        }
        else if (render_mode == ZBEZIER) {
          zout = ay;
          xoffset = line_delta / Math.sqrt(1 + line_slope*line_slope);
          if (x2 < x1) { xoffset = -1 * xoffset; }  // determine sign based on p2
          yoffset = Math.abs(line_slope * xoffset);
          if (y2 < y1) { yoffset = -1 * yoffset; }
          xout = x1 + xoffset;
          yout = y1 + yoffset;
        }
        else if (render_mode == ZBEZIER2) {
          // xout = x1 + (line_delta * cos(raw_line_angle));
          // yout = y1 + (line_delta * sin(raw_line_angle));
          // unrotate: 
          // oR.x = oP.x + (o.x - oP.x) * cos(theta) - (o.y - oP.y) * sin(theta)
        // oR.y = oP.y + (o.x - oP.x) * sin(theta) + (o.y - oP.y) * cos(theta)
          /* double newx = x1 + ((ax - x1) * cos(-raw_line_angle)) - ((ay - y1) * sin(-raw_line_angle));
          double newy = y1 + ((ax - x1) * sin(-raw_line_angle)) + ((ay - y1) * cos(-raw_line_angle));
          xout = ax;
          yout = (line_slope * xout) + line_intercept;
          zout = newy - y1;
          */
          //xoffset = line_delta / Math.sqrt(1 + line_slope*line_slope);
          //if (x2 < x1) { xoffset = -1 * xoffset; }  // determine sign based on p2
          //yoffset = Math.abs(line_slope * xoffset);
          //if (y2 < y1) { yoffset = -1 * yoffset; }
          xout = ax;
          // yout = (line_slope * xout) + y1;
          yout = (line_slope * xout) + line_intercept;
         //  yout = y1 + ()
          zout = ((ax - x1) * sin(-raw_line_angle)) + ((ay - y1) * cos(-raw_line_angle));
        }
      }
      else if (render_mode == ELLIPSES) {
        double ang = Math.random() * M_2PI;
        // double ang = (Math.random() * M_2PI) - M_PI;
        // offset along line (relative to start of line)
        double relative_line_offset = (midlength * render_modifier1) * cos(ang);
        line_delta = midlength * cos(ang);
        
        // offset perpendicular to line:
        // shift angle by -pi/2 get range=>[-1:1] as line_ofset=>[0=>line_length], 
        //   then adding 1 to gets range=>[0:2], 
        //   then scaling by line_length/2 * amplitude gets perp_offset: [0:(line_length*amplitude)]
        double relative_perp_offset = (midlength * (render_modifier2/2)) * sin(ang);
        // double relative_perp_offset = midlength * sin(ang);
                
        // shift to make offsets relative to start point (x1, y1)
        double line_offset = relative_line_offset + x1 - midlength;
        double perp_offset = relative_perp_offset + y1;
        // then consider (line_offset, perp_offset) as point and rotate around start point (x1, y1) ?
        // should already have angle (raw_line_angle)
        // 2D rotation transformation of point B about a given fixed point A to give point C
        // C.x = A.x + (B.x - A.x) * cos(theta) - (B.y - A.y) * sin(theta)
        // C.y = A.y + (B.x - A.x) * sin(theta) + (B.y - A.y) * cos(theta)

        // double newx = x1 + ((line_offset - x1) * cos(raw_line_angle)) - ((perp_offset - y1) * sin(raw_line_angle));
        // double newy = y1 + ((line_offset - x1) * sin(raw_line_angle)) + ((perp_offset - y1) * cos(raw_line_angle));
        double newx = x1 + ((line_offset - x1) * cos(raw_line_angle + M_PI)) - ((perp_offset - y1) * sin(raw_line_angle + M_PI));
        double newy = y1 + ((line_offset - x1) * sin(raw_line_angle + M_PI)) + ((perp_offset - y1) * cos(raw_line_angle + M_PI));
        xout = newx;
        yout = newy;
        // xout = line_offset;
        // yout = perp_offset;
      }
      else if (render_mode == ZELLIPSES) {
        // ang ==> [-Pi : +Pi]  or [ 0 : 2Pi ] ?
        // double  ang = (Math.random() * M_2PI) - M_PI;  // ==> [ -Pi : +Pi ]
        double ang = (Math.random() * M_2PI);   // ==> [ 0 : 2Pi ]

        // offset along line (relative to start of line)
        // if render_modifier1 == 1, then ranges are
        //    delta_from_midlength ==> [-midlength : +midlength]
        //    delta_from_start ==> [0 : line_length]
        double delta_from_midlength = (midlength * render_modifier1) * cos(ang);
        double delta_from_start = midlength - delta_from_midlength;
        line_delta = delta_from_start;
        
        // offset perpendicular to line:
        // shift angle by -pi/2 get range=>[-1:1] as line_ofset=>[0=>line_length], 
        //   then adding 1 to gets range=>[0:2], 
        //   then scaling by line_length/2 * amplitude gets perp_offset: [0:(line_length*amplitude)]
        double relative_perp_offset = (midlength * (render_modifier2/2))* sin(ang);
                
        // shift to make offsets relative to start point (x1, y1)
        double line_offset = delta_from_midlength + x1 - midlength;
        double perp_offset = relative_perp_offset + y1;
        // then consider (line_offset, perp_offset) as point and rotate around start point (x1, y1) ?
        // should already have angle (raw_line_angle)
        // 2D rotation transformation of point B about a given fixed point A to give point C
        // C.x = A.x + (B.x - A.x) * cos(theta) - (B.y - A.y) * sin(theta)
        // C.y = A.y + (B.x - A.x) * sin(theta) + (B.y - A.y) * cos(theta)

        // drop last term for xout and yout since y offset (perp_offset) = y1  [[ or B.y = A.y in above equation ]
        // double newx = x1 + ((line_offset - x1) * cos(raw_line_angle)) - ((perp_offset - y1) * sin(raw_line_angle));
        // double newy = y1 + ((line_offset - x1) * sin(raw_line_angle)) + ((perp_offset - y1) * cos(raw_line_angle));
        xout = x1 + ((line_offset - x1) * cos(raw_line_angle + M_PI));  
        yout = y1 + ((line_offset - x1) * sin(raw_line_angle + M_PI));  // drop last term since y offset (perp_offset) = 0
        zout = relative_perp_offset;

      }
      else if (render_mode == SINE_WAVE || render_mode == SINE_WAVE_REFLECTED || 
               render_mode == SINE_WAVE_BOTH || render_mode == SINE_WAVE_ALTERNATE ||
               render_mode == ZSINE || render_mode == ZSINE_REFLECTED || render_mode == ZSINE_BOTH || render_mode == ZSINE_ALTERNATE) {
        // amplitude calculated such that when render_modifier = 1, relative_perp_offset range: [0 ==> line_length/2]
        double amplitude = (line_length/4) * render_modifier1; 
        double frequency = render_modifier2;
        // range of [0 -> 2Pi ]
        double ang = Math.random() * M_2PI;
        
        // offset along line (relative to start of line) ==> [0=>line_length]
        double relative_line_offset = ang * (line_length/M_2PI);
        line_delta = relative_line_offset;
        // offset perpendicular to line:
        // shift angle by -pi to get cos range=>[-1:1] as ang => [0:2Pi], 
        // find perp offset for endpoints:
        //     since midpoint is always at middle of range (0), and using cos, 
        //     endpoints should have same offset, so use either one
        //     endpoints are at [+/-](Pi*frequency)
        double endpoint_perp_offset = amplitude * cos(M_PI * frequency);  // so at amp=1, freq=1, ==> -1
        double relative_perp_offset = amplitude * ( cos((ang - M_PI) * frequency) );
        // adjust relative_per_offset so endpoints are at 0
        relative_perp_offset = relative_perp_offset - endpoint_perp_offset;

        // shift to make offsets relative to start point (x1, y1)
        double line_offset = relative_line_offset + x1;
        double perp_offset = relative_perp_offset + y1;
        // then consider (line_offset, perp_offset) as point and rotate around start point (x1, y1) ?
        // should already have angle (raw_line_angle)
        // 2D rotation about a given point  
        // oR.x = oP.x + (o.x - oP.x) * cos(theta) - (o.y - oP.y) * sin(theta)
        // oR.y = oP.y + (o.x - oP.x) * sin(theta) + (o.y - oP.y) * cos(theta)

        double newx = x1 + ((line_offset - x1) * cos(raw_line_angle)) - ((perp_offset - y1) * sin(raw_line_angle));
        double newy = y1 + ((line_offset - x1) * sin(raw_line_angle)) + ((perp_offset - y1) * cos(raw_line_angle));
        
        if (render_mode == SINE_WAVE) {
          xout = newx;
          yout = newy;
        }
        else if (render_mode == SINE_WAVE_REFLECTED) {
          double bc = (((x2-x1)*(newx-x1)) + ((y2-y1)*(newy-y1))) / (((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
          xout = (2 * (x1 + ((x2-x1)*bc))) - newx;
          yout = (2 * (y1 + ((y2-y1)*bc))) - newy;
        }
        else if (render_mode == SINE_WAVE_BOTH) {
          if (Math.random() < 0.5) {
            xout = newx;
            yout = newy;
          }
          else {
            double bc = (((x2-x1)*(newx-x1)) + ((y2-y1)*(newy-y1))) / (((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
            xout = (2 * (x1 + ((x2-x1)*bc))) - newx;
            yout = (2 * (y1 + ((y2-y1)*bc))) - newy;
          }
        }
        else if (render_mode == SINE_WAVE_ALTERNATE) {
          if (step_number % 2 == 0) {
            xout = newx;
            yout = newy;
          }
          else {
            double bc = (((x2-x1)*(newx-x1)) + ((y2-y1)*(newy-y1))) / (((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
            xout = (2 * (x1 + ((x2-x1)*bc))) - newx;
            yout = (2 * (y1 + ((y2-y1)*bc))) - newy;
          }
        }
        else if (render_mode == ZSINE) {
          xout = x1 + (relative_line_offset * cos(raw_line_angle));
          // can save a sin() call by using slope-intercept once have xout
          // yout = y1 + (relative_line_offset * sin(raw_line_angle));
          yout = (line_slope * xout) + line_intercept;
          zout = relative_perp_offset; // plus z1?
        }
        else if (render_mode == ZSINE_BOTH) {
          xout = x1 + (relative_line_offset * cos(raw_line_angle));
          // yout = y1 + (relative_line_offset * sin(raw_line_angle));
          yout = (line_slope * xout) + line_intercept;
          if (Math.random() < 0.5) {
            zout = relative_perp_offset; // plus z1?
          }
          else {
            zout = -relative_perp_offset;
          }
        }
      }
      else if (render_mode == KOCHANEK_BARTELS_SPLINE) {
        // cobbled together from:
        // Online:
        //    https://en.wikipedia.org/wiki/Kochanek%E2%80%93Bartels_spline
        //    https://en.wikipedia.org/wiki/Cubic_Hermite_spline#Catmull.E2.80.93Rom_spline
        //    http://paulbourke.net/miscellaneous/interpolation/
        //    http://cubic.org/docs/hermite.htm
        // Original Paper: "Interpolating splines with local tension, continuity, and bias control", Kochanek & Bartels
        // Reference Book: "Curves and Surfaces for Computer Graphics", David Saloman
        // GDC2012 Presentation: "Interpolation and Splines", Squirrel Eiserloh
        //  
        // Hermite Basis Functions: 
        // range from [0:1] along [0..line_length]
        double t1 = Math.random();
        double t2 = t1*t1;
        double t3 = t1*t1*t1;
        double h00 = 2*t3 - 3*t2 + 1;
        double h10 = t3 - 2*t2+ t1;
        double h01 = -2*t3 + 3*t2;
        double h11 = t3 - t2;
        
        // have points p1 and p2, get p0 and p3 (previous point and next point)
        double theta0 = theta1 - theta_step_radians;
        double theta3 = theta2 + theta_step_radians;
        curve.getCurvePoint(theta0, end_point0);
        curve.getCurvePoint(theta3, end_point3);
        double x0 = end_point0.x;
        double y0 = end_point0.y;
        double x3 = end_point0.x;
        double y3 = end_point0.y;

        // but want defaults for tension, bias, continuity to be 0?
        double tension = render_modifier1;
        double continuity = render_modifier2;
        double bias = render_modifier3;

        // m2 ==> KB-calculated tangent for end_point2
        double c110 = (1-tension)*(1+bias)*(1+continuity)/2;
        double c121 = (1-tension)*(1-bias)*(1-continuity)/2;
        double c221 = (1-tension)*(1+bias)*(1-continuity)/2;
        double c232 = (1-tension)*(1-bias)*(1+continuity)/2;

        // m1 ==> KB-calculated tangent for end_point1        
        // m2 ==> KB-calculated tangent for end_point1        
        double m1x = (c110 * (x1-x0)) + (c121 * (x2-x1));
        double m2x = (c221 * (x2-x1)) + (c232 * (x3-x2));
        
        double m1y = (c110 * (y1-y0)) + (c121 * (y2-y1));
        double m2y = (c221 * (y2-y1)) + (c232 * (y3-y2));
        
        double xnew = (h00 * x1) + (h10 * m1x) + (h01 * x2) + (h11 * m2x);
        double ynew = (h00 * y1) + (h10 * m1y) + (h01 * y2) + (h11 * m2y);
        line_delta = t1 * line_length;
        xout = xnew;
        yout = ynew;
        
      }
      
      else if (render_mode == CUBIC_HERMITE_SPLINE) {
        // using general cubic hermite spline, with tangent vectors determined by derivative of underlying curve

        // Hermite Basis Functions: 
        // range from [0:1] along [0..line_length]
        double t1 = Math.random();
        double t2 = t1*t1;
        double t3 = t1*t1*t1;
        double h00 = 2*t3 - 3*t2 + 1;
        double h10 = t3 - 2*t2+ t1;
        double h01 = -2*t3 + 3*t2;
        double h11 = t3 - t2;
        
        // have points p1 and p2, get p0 and p3 (previous point and next point)
        /*
        double theta0 = theta1 - theta_step_radians;
        double theta3 = theta2 + theta_step_radians;
        curve.getCurvePoint(theta0, end_point0);
        curve.getCurvePoint(theta3, end_point3);
        double x0 = end_point0.x;
        double y0 = end_point0.y;
        double x3 = end_point0.x;
        double y3 = end_point0.y;
        */

        /*
        // but want defaults for tension, bias, continuity to be 0?
        double tension = render_modifier1;
        double continuity = render_modifier2;
        double bias = render_modifier3;

        // m2 ==> KB-calculated tangent for end_point2
        double c110 = (1-tension)*(1+bias)*(1+continuity)/2;
        double c121 = (1-tension)*(1-bias)*(1-continuity)/2;
        double c221 = (1-tension)*(1+bias)*(1-continuity)/2;
        double c232 = (1-tension)*(1-bias)*(1+continuity)/2;

        // m1 ==> KB-calculated tangent for end_point1        
        // m2 ==> KB-calculated tangent for end_point1        

        double m1x = (c110 * (x1-x0)) + (c121 * (x2-x1));
        double m2x = (c221 * (x2-x1)) + (c232 * (x3-x2));
        
        double m1y = (c110 * (y1-y0)) + (c121 * (y2-y1));
        double m2y = (c221 * (y2-y1)) + (c232 * (y3-y2));
        */

        // use first derivative of curve (currently using rhodonea) at each endpoint for tangent vectors
        //     tangent = f'(t)
        double m1x = -(cos((a/b)*theta1) + c) * sin(theta1);
        double m2x = -(cos((a/b)*theta2) + c) * sin(theta2);
        double m1y =  (cos((a/b)*theta1) + c) * cos(theta1);
        double m2y =  (cos((a/b)*theta2) + c) * cos(theta2);
        //m1x = -1 * m1x;
        //m2x = -1 * m2x;
        // or is it:
        //                f'(t)
        //    tangent  = ------- 
        //               |f'(t)|  ==> speed ==> sqrt(x'(t)^2 + y'(t)^2)
        double speed1 = sqrt(m1x*m1x + m1y*m1y);
        double speed2 = sqrt(m2x*m2x + m2y*m2y);
        double tan1x = m1x/speed1;
        double tan2x = m2x/speed2;
        double tan1y = m1y/speed1;
        double tan2y = m2y/speed2;
        
        double xnew = (h00 * x1) + (h10 * tan1x) + (h01 * x2) + (h11 * tan2x);
        double ynew = (h00 * y1) + (h10 * tan1y) + (h01 * y2) + (h11 * tan2y);
        // double xnew = (h00 * x1) + (h10 * m1x) + (h01 * x2) + (h11 * m2x);
        // double ynew = (h00 * y1) + (h10 * m1y) + (h01 * y2) + (h11 * m2y);
        // double xnew = (h00 * x1) + ((h10 * m1x)*(x2-x1)) + (h01 * x2) + ((h11 * m2x)*(x2-x1));
        // double ynew = (h00 * y1) + ((h10 * m1y)*(y2-y1)) + (h01 * y2) + ((h11 * m2y)*(y2-y1));
        line_delta = t1 * line_length;
        xout = xnew;
        yout = ynew;
        
      }
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
          sampled_vals = null; // percentiles calculated directly for DISTANCE_ALONG_LINE_POINTS
        }
        else if (measure == DISTANCE_FROM_MIDLINE_POINTS) {
          val = abs(line_delta - midlength);
          sampled_vals = null; // percentiles calculated directly for DISTANCE_FROM_MIDLINE_POINTS
        }
        else if (measure == DISTANCE_FROM_NEAREST_END_POINTS) {
          val = Math.min(line_delta, line_length - line_delta);
          sampled_vals = null;  // percentiles calculated directly for DISTANCE_FROM_NEAREST_END_POINTS
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
          if (measure == DISTANCE_ALONG_LINE_POINTS) {
            low_thresh = mfilter.low_thresh * line_length;
            high_thresh = mfilter.high_thresh * line_length;
          }
          else if (measure == DISTANCE_FROM_MIDLINE_POINTS || measure == DISTANCE_FROM_NEAREST_END_POINTS) {
            // low_thresh and high_thresh for DISTANCE_FROM_MIDLINE_POINTS and DISTANCE_FROM_NEAREST_END_POINTS 
            //      can behave differently when thresholding by value, but act the same when thresholding by percentile
            low_thresh = mfilter.low_thresh * midlength;
            high_thresh = mfilter.high_thresh * midlength;
          }
          else {
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
    if (direct_color_measure != NONE && direct_color_gradient != OFF) {
      double val;
      double[] sampled_vals;
      if (direct_color_measure == LINE_LENGTH_LINES) {
        val = line_length;
        sampled_vals = sampled_line_lengths;
      }
      else if (direct_color_measure == LINE_ANGLE_LINES) {
        val = line_angle;
        sampled_vals = sampled_line_angles;
      }
      else if (direct_color_measure == POINT_ANGLE_LINES) {
        val = point_angle;
        sampled_vals = sampled_point_angles;
      }
      else if (direct_color_measure == DISTANCE_ALONG_LINE_POINTS) {
        val = line_delta;
        sampled_vals = null;
      }
      else if (direct_color_measure == DISTANCE_FROM_MIDLINE_POINTS) {
        val = abs(line_delta - midlength);
        sampled_vals = null;
      }
      else if (direct_color_measure == META_INDEX && meta_mode != OFF) {
        val = current_meta_step;
        sampled_vals = null;
      }
      else if (direct_color_measure == Z_MINMAX_POINTS) {
        // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
        // val = abs(zout*2); // min/max zout should be
        val = zout + (line_length/2); //  range of val should be 0 to line_length;
        sampled_vals = sampled_line_lengths;
      }
      else if (direct_color_measure == Z_ABSOLUTE_MINMAX_POINTS) {
        // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
        val = abs(zout) * 2; // range of val should be 0 to line_length
        sampled_vals = sampled_line_lengths;
      }
      else if (direct_color_measure == DISTANCE_FROM_NEAREST_END_POINTS) {
        val = Math.min(line_delta, line_length - line_delta);
        sampled_vals = null;  // percentiles calculated directly for DISTANCE_FROM_NEAREST_END_POINTS
      } 
      else { // default to LINE_LENGTH_LINES
        val = line_length;
        sampled_vals = sampled_line_lengths;
      }
      
      double baseColor = 0;
      
      double low_value, high_value;
      // ignore percentile option and direct_color_thesholding if using META_INDEX mode??
      if (direct_color_measure == META_INDEX && meta_mode != OFF) {
        low_value = 0;
        high_value = meta_steps;
      }
      else {
        if (direct_color_thesholding == PERCENT) {
          if (direct_color_measure == DISTANCE_ALONG_LINE_POINTS) {
            low_value = color_low_thresh * line_length;
            high_value = color_high_thresh * line_length;
          }
          else if (direct_color_measure == DISTANCE_FROM_MIDLINE_POINTS) {
            low_value = color_low_thresh * midlength;
            high_value = color_high_thresh * midlength;
          }
          else if (direct_color_measure == DISTANCE_FROM_MIDLINE_POINTS || direct_color_measure == DISTANCE_FROM_NEAREST_END_POINTS) {
            // low_thresh and high_thresh for DISTANCE_FROM_MIDLINE_POINTS and DISTANCE_FROM_NEAREST_END_POINTS 
            //      can behave differently when thresholding by value, but act the same when thresholding by percentile
            low_value = color_low_thresh * midlength;
            high_value = color_high_thresh * midlength;
          }
          else {
            int low_index, high_index;
            if (color_low_thresh < 0 || color_low_thresh >= 1) { low_index = 0; }
            else { low_index = (int)(color_low_thresh * sample_size); }
            if (color_high_thresh >= 1 || color_high_thresh < 0) { high_index = sample_size - 1; }
            else { high_index = (int)(color_high_thresh * (sample_size-1)); }
            low_value = sampled_vals[low_index];
            high_value = sampled_vals[high_index];
          }
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
      if (direct_color_gradient == COLORMAP_CLAMP) {
        pVarTP.rgbColor = false;
        pVarTP.color = baseColor / 255.0;
        if (pVarTP.color < 0) { pVarTP.color = 0; }
        if (pVarTP.color > 1.0) { pVarTP.color = 1.0; }
      }
      else if (direct_color_gradient == COLORMAP_WRAP) {
        // if val is outside range, wrap it around (cylce) to keep within range
        if (val < low_value) {
          val = high_value - ((low_value - val) % (high_value - low_value));
        }
        else if (val > high_value) {
          val = low_value + ((val - low_value) % (high_value - low_value));
        }
        baseColor = ((val - low_value)/(high_value - low_value)) * 255; 
        pVarTP.color = baseColor / 255.0;
        if (pVarTP.color < 0) { pVarTP.color = 0; }
        if (pVarTP.color > 1.0) { pVarTP.color = 1.0; }
      }
      else if (direct_color_gradient == RED_GREEN) {  //
        pVarTP.rgbColor = true;
        pVarTP.redColor = baseColor;
        pVarTP.greenColor = 255 - baseColor;
        pVarTP.blueColor = 0;
      }
      else if (direct_color_gradient == RED_BLUE) {
        pVarTP.rgbColor = true;
        pVarTP.redColor = baseColor;
        pVarTP.greenColor = 0;
        pVarTP.blueColor = 255 - baseColor;
      }
      else if (direct_color_gradient == BLUE_GREEN) {
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
    plist.put(PARAM_CURVE_MODE, curve_mode);    
    plist.put(PARAM_A, a_param);
    plist.put(PARAM_B, b_param);
    plist.put(PARAM_C, c_param);
    plist.put(PARAM_D, d_param);
    plist.put(PARAM_THETA_STEP_SIZE_DEGREES, theta_step_size_param);
    plist.put(PARAM_INITIAL_THETA_DEGREES, initial_theta_param);
    plist.put(PARAM_LINE_COUNT, line_count);
    plist.put(PARAM_RENDER_MODE, render_mode);
    plist.put(PARAM_RENDER_MODIFIER1, render_modifier1);
    plist.put(PARAM_RENDER_MODIFIER2, render_modifier2);
    plist.put(PARAM_RENDER_MODIFIER3, render_modifier3);
    plist.put(PARAM_USE_COSETS, (use_cosets ? 1 : 0));
    
    plist.put(PARAM_DIRECT_COLOR_MEASURE, direct_color_measure);
    plist.put(PARAM_DIRECT_COLOR_GRADIENT, direct_color_gradient);
    plist.put(PARAM_DIRECT_COLOR_THRESHOLDING, direct_color_thesholding);
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
    else if (PARAM_RENDER_MODIFIER1.equalsIgnoreCase(pName))
      render_modifier1 = pValue;
    else if (PARAM_RENDER_MODIFIER2.equalsIgnoreCase(pName))
      render_modifier2 = pValue;
    else if (PARAM_RENDER_MODIFIER3.equalsIgnoreCase(pName))
      render_modifier3 = pValue;
    else if (PARAM_THETA_STEP_SIZE_DEGREES.equalsIgnoreCase(pName))
      theta_step_size_param = pValue;
    else if (PARAM_INITIAL_THETA_DEGREES.equalsIgnoreCase(pName))
      initial_theta_param = pValue;
    else if (PARAM_LINE_COUNT.equalsIgnoreCase(pName))
      line_count = pValue;
    else if (PARAM_CURVE_MODE.equalsIgnoreCase(pName))
      curve_mode = (int)pValue;
    else if (PARAM_USE_COSETS.equalsIgnoreCase(pName)) {
      use_cosets = (pValue >= 1);
    }
    
    else if (PARAM_DIRECT_COLOR_MEASURE.equalsIgnoreCase(pName)) {
      direct_color_measure = (int)pValue;
    }
    else if (PARAM_DIRECT_COLOR_GRADIENT.equalsIgnoreCase(pName)) {
      direct_color_gradient = (int)pValue;
    }
    else if (PARAM_DIRECT_COLOR_THRESHOLDING.equalsIgnoreCase(pName)) {
      direct_color_thesholding = (int)pValue;
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
