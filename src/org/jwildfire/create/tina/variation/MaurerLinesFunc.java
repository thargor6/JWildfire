/*
JWildfire - an image and animation processor written in Java
Copyright (C) 1995-2016 Andreas Maschke

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
/**
 * Maurer Lines variation by CozyG
 * Copyright 2016- Gregg Helt
 * (released under same GNU Lesser General Public License as above)
 * Initially based on the "Maurer Rose", as described by Peter Maurer
 * for more information on Maurer Roses see https://en.wikipedia.org/wiki/Maurer_rose
 * But Maurer Lines greatly expands on this and explores other ideas as well
 * For more information on some of the ideas explored in this variation, see the paper
 * "A Rose by Any Other Name...", Gregg Helt, Bridges Conference, 2016:
 * http://archive.bridgesmathart.org/2016/bridges2016-445.html
 * For a simpler subset of this functionality that is much closer to the original Maurer Rose,
 * see the "maurer_rose" variation
 */
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static java.lang.Math.abs;
import static org.jwildfire.base.mathlib.MathLib.*;

public class MaurerLinesFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private boolean DEBUG_META_MODE = false;
  private boolean DEBUG_SAMPLING = false;
  private boolean DEBUG_DYNAMIC_PARAMETERS = false;
  private boolean DEBUG_COSETS = false;
  private boolean DEBUG_COSETS_INIT = false;
  private boolean DEBUG_TANGENTS = false;

  /*
   *   curve params
   *   usage depends on curve_mode
   *   For example, if curve_mode = RHODONEA:  r = cos((kn/kd) * t) + c
   *      param_a => kn
   *      param_b => kd
   *      param_c => c
   *      other param_[defgh] are unused for RHODONEA
   */
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String PARAM_G = "g";
  private static final String PARAM_H = "h";

  private static final String PARAM_THETA_STEP_SIZE_DEGREES = "theta_step_size";
  private static final String PARAM_INITIAL_THETA_DEGREES = "initial_theta";
  private static final String PARAM_LINE_COUNT = "line_count";
  private static final String PARAM_IRRATIONALIZE = "irrationalize";
  private static final String PARAM_RENDER_MODE = "render_mode";
  private static final String PARAM_RENDER_SUBMODE = "render_submode";
  private static final String PARAM_TANGENT_SUBMODE = "tangent_submode";
  private static final String PARAM_CURVE_MODE = "curve_mode";
  private static final String PARAM_COSETS_MODE = "cosets_mode";

  private static final String PARAM_DIRECT_COLOR_MEASURE = "direct_color_measure";
  private static final String PARAM_DIRECT_COLOR_GRADIENT = "direct_color_gradient";
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

  /**
   * CURVE MODES
   */
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
  private static final int RIGGED_RHODONEA_PLUS = 15;
  private static final int RIGGED_RHODONEA_MINUS = 16;
  private static final int SUPER_ELLIPSE = 17;
  private static final int SUPER_RHODONEA = 18;

  private static final int DEFAULT = 0;  // used for render_mode, render_submode, tangent_mode (for cubic interpolations)
  private static final int NORMAL = 0;
  private static final int NONE = 0;
  private static final int OFF = 0;

  /**
   * RENDER MODES
   */
  private static final int LINES = 1;
  private static final int CIRCLES = 2;
  private static final int ELLIPSES = 3;
  private static final int SINE_WAVES = 4;
  private static final int QUADRATIC_BEZIER = 5;

  private static final int SEQUIN_CIRCLE_SPLINE = 9;
  // CUBIC_HERMITE_SPLINE and CUBIC_HERMITE_SPLINE_FORM2 require first derivative of curve, 
  //     which is currently only calculated for RHODONEA curve_mode
  //     so for now if curve_mode != RHODONEA, these modes leave point unchanged
  private static final int CUBIC_HERMITE_SPLINE = 10;

  // private static final int FINITE_DIFFERENCE_SPLINE = 12;
  // finite difference spline in this context becomes equivalent to Catmull-Rom, so removing it
  // m1 ==> finite difference calculated tangent for end_point1
  // m2 ==> finite difference calculated tangent for end_point2
  // M1 = ((P2-P1)/(t2-t1) + (P1-P0)/(t1-t0))/2
  // M2 = ((P3-P2)/(t3-t2) + (P2-P1)/(t2-t1))/2
  // for now assuming t(i+1) = t(i) + 1, or in other words t(i+t)-t(i) = 1
  // M1 = (P2 - P0)/2
  // M2 = (P3 - P1)/2
  // so is equivalent to Catmull-Rom
  private static final int CARDINAL_SPLINE = 11;
  // UNIFORM_CATMULL_ROM_SPLINE is same as CARDINAL_SPLINE with tightness param is set to 0
  private static final int UNIFORM_CATMULL_ROM_SPLINE = 12;
  private static final int NONUNIFORM_CATMULL_ROM_SPLINE = 13;
  private static final int CHORDAL_CATMULL_ROM_SPLINE = 14;  // special case of nonuniform Catmull-Rom spline
  private static final int CENTRIPETAL_CATMULL_ROM_SPLINE = 15;  // special case of nonuniform Catmull-Rom spline
  private static final int KOCHANEK_BARTELS_SPLINE = 16;
  // HAPPY_ACCIDENTS: accidentally used incorrect tangent calculations, but ended up with interesting curves...
  private static final int CUBIC_HERMITE_HAPPY_ACCIDENT1 = 17;  // incorrect tangent calculations
  private static final int CUBIC_HERMITE_HAPPY_ACCIDENT2 = 18;  // same as ACCIDENT1, but flip/reflect signs for x in tangents
  private static final int CUBIC_HERMITE_HAPPY_ACCIDENT3 = 19;  // same as ACCIDENT1, but flip/reflect signs for y in tangents
  private static final int CUBIC_HERMITE_HAPPY_ACCIDENT4 = 20;  // same as ACCIDENT1, but flip/reflect signs for both x and y in tangents
  private static final int CUBIC_HERMITE_HAPPY_ACCIDENT5 = 21;  // same as ACCIDENT1, but switch tangent points

  // *_SWIZZLE1
  //    same as *, but switch point0 x<->y, and point3 x<->y
  // *_SWIZZLE2
  //    same as *, but switch point0 <-> point3
  // *_SWIZZLE3
  //    same as *, but switch point0 <-> point3, point0 x<->y, point 3 x<->y
  private static final int CARDINAL_SPLINE_SWIZZLE1 = 6;
  private static final int CARDINAL_SPLINE_SWIZZLE2 = 7;
  private static final int CARDINAL_SPLINE_SWIZZLE3 = 8;
  private static final int KOCHANEK_BARTELS_SPLINE_SWIZZLE1 = 22;
  private static final int KOCHANEK_BARTELS_SPLINE_SWIZZLE2 = 23;
  private static final int KOCHANEK_BARTELS_SPLINE_SWIZZLE3 = 24;

  private static final int CUBIC_HERMITE_TANGENT_FORM2 = 25;

  /**
   * RENDER SUBMODES
   */
  private static final int STROKE = 1;
  private static final int REFLECTED_STROKE = 2;
  private static final int BOTH_STROKE = 3;
  private static final int ALTERNATING_STROKE = 4;
  private static final int FILL_TO_LINE = 5;   // fill from calculated point to Maurer line (reduces to Maurer line for line render mode)
  private static final int REFLECTED_FILL_TO_LINE = 6;
  private static final int BOTH_FILL_TO_LINE = 7;
  private static final int ALTERNATING_FILL_TO_LINE = 8;
  private static final int FILL_TO_CURVE = 9;
  private static final int REFLECTED_FILL_TO_CURVE = 10;
  private static final int BOTH_FILL_TO_CURVE = 11;
  private static final int ALTERNATING_FILL_TO_CURVE = 12;
  private static final int FILL_BETWEEN_LINE_CURVE = 13;   // ignores render mode
  private static final int FILL_BETWEEN_LINE_CURVE_SWIZZLE = 14;  // ignores render mode

  private static final int Z_STROKE = 15;
  private static final int Z_REFLECTED_STROKE = 16;
  private static final int Z_BOTH_STROKE = 17;
  private static final int Z_ALTERNATING_STROKE = 18;

  /**
   * TANGENT SUBMODES
   * currently only applies to CUBIC_HERMITE_SPLINE and CUBIC_HERMITE_HAPPY_ACCIDENT* modes
   */
  private static final int TANGENT = 1;      // velocity ==> f'(t)
  private static final int UNIT_TANGENT = 2; // normalized velocity ==> f'(t) / |f'(t)|   
  private static final int UNSCALED_TANGENT = 3;
  private static final int UNSCALED_UNIT_TANGENT = 4;
  private static final int NORMAL_VECTOR = 5;  // tangent vector rotated -90% (also is acceleration ==> f''(t) ??)
  private static final int UNIT_NORMAL = 6;  // normalized acceleration ?? ==> f''(t) / |f''(t)| ==> T'(t)/|T'(t)| where T -> unit tangent vector 
  private static final int UNSCALED_NORMAL = 7;
  private static final int UNSCALED_UNIT_NORMAL = 8;
  private static final int ROTATION_TANGENT = 9;   // specify how much to rotate vector relative to tangent
  private static final int THETA_TANGENT = 10;     // specify theta +/- offset from P1/P2 Maurer endpoints to use for determining tangent
  private static final int INDEX_TANGENT = 11;     // specify endpoint index +/- offset from P1/P2 Maurer endpoint to use for determining tangent
  private static final int XY_SCALE_TANGENT = 12;

  /**
   * COSET MODES
   */
  private static final int NO_COSETS = 0;
  private static final int CLASSIC_COSETS = 1;  // cosets as implemented in original Maurer rose paper -- 
  private static final int CLOSURE_COSETS = 2;  // improved cosets as implemented in Helt rose paper -- line_count is for full curve closure 
  private static final int HYBRID_COSETS = 3;   // hybrid of CLASSIC and CLOSURE -- line_count is for full_curve_closure/2Pi 

  // direct color gradient handling
  private static final int COLORMAP_CLAMP = 1;
  private static final int COLORMAP_WRAP = 2;
  private static final int RED_GREEN = 3;
  private static final int RED_BLUE = 4;
  private static final int BLUE_GREEN = 5;

  // direct color measures (and filters)
  /**
   * DIRECT COLOR MEASURE
   * (also FILTER MEASURE)
   */
  private static final int LINE_LENGTH_LINES = 1;
  private static final int LINE_ANGLE_LINES = 2;
  private static final int POINT_ANGLE_LINES = 3;
  private static final int META_INDEX = 4;
  private static final int Z_MINMAX_POINTS = 5;
  private static final int Z_ABSOLUTE_MINMAX_POINTS = 6;
  private static final int DISTANCE_ALONG_LINE_POINTS = 7;
  private static final int DISTANCE_FROM_MIDLINE_POINTS = 8;
  private static final int DISTANCE_FROM_NEAREST_END_POINTS = 9;
  private static final int SPEED_AT_ENDPOINT1 = 10;
  // private static final int WEIGHTED_LINE_LENGTH = 5;
  private static final int CURRENT_THETA = 11;
  // private static final int CURRENT_COSET = 12;  
  // private static final int CURRENT_POLYLINE_SEGMENT = 13;

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
  private static final int RENDER_MODIFIER1 = 10;
  private static final int RENDER_MODIFIER2 = 11;
  private static final int RENDER_MODIFIER3 = 12;

  // line thickness strategy (so far random is the only one working well)
  private static final int RANDOM = 0;
  private static final int PERPENDICULAR = 1;
  private static final int ROUNDED_CAPS = 2;

  private double a_param = 2;
  private double b_param = 1;
  private double c_param = 0;
  private double d_param = 1;
  private double e_param = 1;
  private double f_param = 1;
  private double g_param = 1;
  private double h_param = 1;

  private double a, b, c, d, e, f, g, h;
  private double line_count = 360;
  private double theta_step_size_param = 71;  // specified in degrees
  private double initial_theta_param = 0; // specified in degrees
  private double render_mode = DEFAULT;
  private double render_submode = DEFAULT;
  private double tangent_submode = DEFAULT;
  private double render_modifier1 = 1.0;
  private double render_modifier2 = 1.0;
  private double render_modifier3 = 1.0;

  private double theta_step_radians;
  private double initial_theta_radians;
  private double cycles; // 1 cycle = 2*PI

  private double show_points_param = 0;
  private double show_curve_param = 0;

  private double line_thickness_strategy = RANDOM;
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

  private int meta_mode = OFF;
  private double meta_min_value = 30;
  private double meta_max_value = 45;
  private double meta_step_value = 1;
  private double current_meta_step;
  private double meta_steps;

  /*
   *  vars for using cosets method from original Maurer Rose paper:
   *    line_count becomes the (integer) number of "degrees" circle is divided into
   *    theta_step_size is rounded to nearest integer
   *    initial offset is ignored (considered 0)
   *    WARNING: certain meta_modes override cosets_mode setting:
   *        LINE_OFFSET_INCREMENT
   *        INITIAL_OFFSET
   */
  private int cosets_mode = 0;
  int coset_d;
  int coset_z;
  int coset_gcd;
  int coset_lcm;

  double lines_per_coset;
  double coset_step_size;
  double coset_count;

  double cycles_to_close_curve;
  double cycles_to_close_polyline;
  double radians_to_close_curve;
  double radians_to_close_polyline;

  private double irrationalize = 0;
  private boolean randomize = false;
  private int sample_size = 1000;
  private double[] sampled_line_lengths = new double[sample_size];
  private double[] sampled_line_angles = new double[sample_size];
  private double[] sampled_point_angles = new double[sample_size];
  private double[] sampled_speeds = new double[sample_size];
  private double[] sampled_thetas = new double[sample_size];
  private long count = 0;

  ParametricCurve curve;

  class ParametricCurve {
    public void getCurvePoint(double t, DoublePoint2D point) {
    }

    public DoublePoint2D getCurvePoint(double t) {
      DoublePoint2D outpoint = new DoublePoint2D();
      getCurvePoint(t, outpoint);
      return outpoint;
    }

    public void getFirstDerivative(double t, DoublePoint2D result) {
      result.x = Double.NaN;
      result.y = Double.NaN;
    }

    public double getSpeed(double t, DoublePoint2D result) {
      this.getFirstDerivative(t, result);
      if (Double.isNaN(result.x) || Double.isNaN(result.y)) {
        return 0;
      }
      double speed = sqrt(result.x * result.x + result.y * result.y);
      return speed;
    }

    public double getCyclesToClose() {
      return Double.NaN;
    }
  }

  class RhodoneaCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double k = a / b;
      double r = cos(k * t) + c;
      point.x = r * cos(t);
      point.y = r * sin(t);
    }

    @Override
    public void getFirstDerivative(double t, DoublePoint2D result) {
      // trying different  way of getting derivatives
      //      previous attempts only calculating dr/dt, but really want dy/dx
      // see http://tutorial.math.lamar.edu/Classes/CalcII/PolarTangents.aspx
      //
      //    dy/dt = f'(t)sin(t) + f(t)cos(t) ==> (dr/dt)*sin(t) + r*cos(t)
      //    dx/dt = f'(t)cos(t) - f(t)sin(t) ==> (dr/dt)*cos(t) - r*sin(t)
      //    dy/dx = (dy/dt) / (dx/dt)
      //
      //  for rhodonea (ignoring c param for now):
      //     r = cos(k * t)
      //           so using standard trig derivative for cosine, for dr/dt
      //     dr/dt =  -k * sin(k * t)
      //         then pluging into above to get dy/dt and dx/dt:
      //     dy/dt = (-k * sin(k*t) * sin(t)) + (cos(k*t) * cos(t))
      //     dx/dt = (-k * sin(k*t) * cos(t)) - (cos(k*t) * sin(t))
      //
      //  which agrees with symbolic first derivative result from Mathematica:
      //      y[t_] = Cos[k * t] * Sin[t]
      //      x[t_] = Cos[k * t] * Cos[t]
      //      y'[t] = (-k * sin(k*t) * sin(t)) + (cos(k*t) * cos(t))
      //      x'[t] = (-k * sin(k*t) * cos(t)) - (cos(k*t) * sin(t))
      double k = a / b;
      double f1x = (-k * sin(k * t) * cos(t)) - ((cos(k * t) + c) * sin(t));  // first x derivative at P0 (of rhodonea)
      double f1y = (-k * sin(k * t) * sin(t)) + ((cos(k * t) + c) * cos(t));  // first y derivative at P0 (of rhodonea)
      result.x = f1x;
      result.y = f1y;
    }

    @Override
    public double getCyclesToClose() {
      double cycles_to_close = Double.NaN;
      double k = a / b;
      // attempt to calculate minimum cycles manually, or reasonable upper bound if unsure
      if ((k % 1) == 0) { // k is integer
        if ((k % 2) == 0) { // k is even integer, will have 2k petals
          cycles_to_close = 1; // (2PI)
        } else { // k is odd integer, will have k petals (or 2k if c!= 0)
          if (c != 0) {
            cycles_to_close = 1;  // 2Pi
          } else {
            cycles_to_close = 0.5;  // 1Pi
          }
        }
      } else if ((a % 1 == 0) && (b % 1 == 0)) {
        double kn = a;
        double kd = b;
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
      return cycles_to_close;
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
    // parametric polygon equation derived from: 
    //    http://math.stackexchange.com/questions/41940/is-there-an-equation-to-describe-regular-polygons
    //    http://www.geogebra.org/m/157867
    public void getCurvePoint(double t, DoublePoint2D point) {
      double n = Math.floor(a);
      double theta = abs(t % M_2PI);
      double r = cos(M_PI / n) / cos(theta % (M_2PI / n) - M_PI / n);
      point.x = r * cos(theta);
      point.y = r * sin(theta);
    }
  }

  class EllipseCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      point.x = a * cos(t);
      point.y = b * sin(t);
    }
  }

  class SuperEllipseCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      point.x = pow(abs(cos(t)), 2.0 / c) * a * Math.signum(cos(t));
      point.y = pow(abs(sin(t)), 2.0 / d) * b * Math.signum(sin(t));
    }
  }

  class EpitrochoidCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      // option 1:
      // double x = ((a_radius + b_radius) * cos(theta)) - (c_radius * cos(((a_radius + b_radius)/b_radius) * theta));
      // double y = ((a_radius + b_radius) * sin(theta)) - (c_radius * sin(((a_radius + b_radius)/b_radius) * theta));
      // option 2:
      double x = ((a + b) * cos(t)) - (c * cos(((a + b) / b) * t));
      double y = ((a + b) * sin(t)) - (c * sin(((a + b) / b) * t));
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
      double x = ((a - b) * cos(t)) + (c * cos(((a - b) / b) * t));
      double y = ((a - b) * sin(t)) - (c * sin(((a - b) / b) * t));
      point.x = x;
      point.y = y;
    }
  }

  class EpispiralCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double k = a / b;
      double r = (1 / cos(k * t)) + c;
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
      double x = sin((a * t) + c);
      double y = sin(b * t);
      point.x = x;
      point.y = y;
    }
  }

  class SuperRhodoneaCurve extends ParametricCurve {
    public void getCurvePoint(double t, DoublePoint2D point) {
      // a = kn
      // b = kd
      // c = m
      // 1 = a1, b1, n2, n3
      // 4 = m
      double k = a / b;
      double m = c;
      double n1 = d;
      double n2 = e;
      double n3 = f;
      double a1 = g;
      double b1 = h;

      double rose = cos(k * t);
      double supershape = pow(
              (pow(fabs((cos(m * t / 4)) / a1), n2) +
                      pow(fabs((sin(m * t / 4)) / b1), n3)),
              (-1 / n1));
      double r = rose * supershape;
      point.x = r * cos(t);
      point.y = r * sin(t);
    }
  }

  class SupershapeCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      // original supershape variables: a, b, line_slope, n1, n2, n3
      // a = m  (line_slope)
      // b = n1
      // c = n2
      // d = n3
      // e = a1
      // f = b1

      // old supershape params
      double m = a;
      double n1 = b;
      double n2 = c;
      double n3 = d;
      double a1 = 1;
      double b1 = 1;

      double r = pow(
              (pow(fabs((cos(m * t / 4)) / a1), n2) +
                      pow(fabs((sin(m * t / 4)) / b1), n3)),
              (-1 / n1));
      point.x = r * cos(t);
      point.y = r * sin(t);
    }
  }

  class StarrCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double r = 2 + (sin(a * t) / 2);
      double t2 = t + (sin(b * t) / c);
      point.x = r * cos(t2);
      point.y = r * sin(t2);
    }
  }

  class FarrisMysteryCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      point.x = cos(t) / a + cos(6 * t) / b + sin(14 * t) / c;
      point.y = sin(t) / a + sin(6 * t) / b + cos(14 * t) / c;
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

    @Override
    public double getCyclesToClose() {
      double radians_to_close = 2 * M_PI * M_PI * M_PI;   // radians to close is 2 * (PI^3)
      double cycles_to_close = radians_to_close / (2 * M_PI);
      return cycles_to_close;
    }
  }

  class RiggedRhodoneaPlusCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double k = a / b;
      // double r = cos(k * t) + c;
      double r = cos(k * t) + cos(c * k * t);
      r = r / 2;  // should get scale similar to base rhodonea?
      point.x = r * cos(t);
      point.y = r * sin(t);
    }
  }

  class RiggedRhodoneaMinusCurve extends ParametricCurve {
    @Override
    public void getCurvePoint(double t, DoublePoint2D point) {
      double k = a / b;
      // double r = cos(k * t) + c;
      double r = cos(k * t) - cos(c * k * t);
      r = r / 2; // should get scale similar to base rhodonea?
      point.x = r * cos(t);
      point.y = r * sin(t);
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
   * only using z coordinate for specific modes
   */

  class DoublePoint2D implements Serializable {
    public double x;
    public double y;
  }

  class DoublePoint3D extends DoublePoint2D {
    public double z;
  }

  private DoublePoint3D curve_point = new DoublePoint3D();
  private DoublePoint3D end_point1 = new DoublePoint3D();
  private DoublePoint3D end_point2 = new DoublePoint3D();


  private DoublePoint3D mpoint = new DoublePoint3D();

  // end_point0 (point on curve one step before current Maurer line endpoints)
  //     used for Kochanek-Bartels spline interpolation, possibly other uses in the future
  private DoublePoint3D end_point0 = new DoublePoint3D();
  // end_point1 (point on curve one step after current Maurer line endpoints);
  //     used for Kochanek-Bartels spline interpolation, possibly other uses in the future
  private DoublePoint3D end_point3 = new DoublePoint3D();

  // extra points to hold derivative/tangent vectors for various cubic interpolations
  private DoublePoint2D first_derivative_point1 = new DoublePoint2D();
  private DoublePoint2D first_derivative_point2 = new DoublePoint2D();

  // extra points to hold vectors for circle spline interpolation
  private DoublePoint2D vecA = new DoublePoint2D();
  private DoublePoint2D vecB = new DoublePoint2D();
  private DoublePoint2D vecC = new DoublePoint2D();
  private DoublePoint2D vecD = new DoublePoint2D();
  private DoublePoint2D vecE = new DoublePoint2D();

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
    if (curve_index == CIRCLE) {
      new_curve = new CircleCurve();
    } else if (curve_index == POLYGON) {
      new_curve = new PolygonCurve();
    } else if (curve_index == ELLIPSE) {
      new_curve = new EllipseCurve();
    } else if (curve_index == RHODONEA) {
      new_curve = new RhodoneaCurve();
    } else if (curve_index == EPITROCHOID) {
      new_curve = new EpitrochoidCurve();
    } else if (curve_index == HYPOTROCHOID) {
      new_curve = new HypotrochoidCurve();
    } else if (curve_index == LISSAJOUS) {
      new_curve = new LissajousCurve();
    } else if (curve_index == EPISPIRAL) {
      new_curve = new EpispiralCurve();
    } else if (curve_index == SUPERSHAPE) {
      new_curve = new SupershapeCurve();
    } else if (curve_index == SUPER_RHODONEA) {
      new_curve = new SuperRhodoneaCurve();
    } else if (curve_index == STARR_CURVE) {
      new_curve = new StarrCurve();
    } else if (curve_index == FARRIS_MYSTERY_CURVE) {
      new_curve = new FarrisMysteryCurve();
    } else if (curve_index == WAGON_FANCIFUL_CURVE) {
      new_curve = new WagonFancifulCurve();
    } else if (curve_index == FAY_BUTTERFLY) {
      new_curve = new FayButterflyCurve();
    } else if (curve_index == RIGGE1) {
      new_curve = new Rigge1Curve();
    } else if (curve_index == RIGGE2) {
      new_curve = new Rigge2Curve();
    } else if (curve_index == RIGGED_RHODONEA_PLUS) {
      new_curve = new RiggedRhodoneaPlusCurve();
    } else if (curve_index == RIGGED_RHODONEA_MINUS) {
      new_curve = new RiggedRhodoneaMinusCurve();
    } else if (curve_index == SUPER_ELLIPSE) {
      new_curve = new SuperEllipseCurve();
    } else {
      new_curve = new CircleCurve();
    } // default to circle curve
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
    setCurveParams();

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
    if (irrationalize != 0) {
      theta_step_radians += (irrationalize * 0.01 * Math.E) / 360;
    }
    initial_theta_radians = M_2PI * (initial_theta_param / 360);
    cycles = (line_count * theta_step_radians) / M_2PI;

    coset_d = (int) Math.floor(theta_step_size_param);
    coset_z = (int) Math.floor(line_count);
    // coset_gcd = gcd(coset_d, coset_z);
    // coset_lcm = lcm(coset_d, coset_z);  
    cycles_to_close_curve = curve.getCyclesToClose();
    if (Double.isNaN(cycles_to_close_curve)) {
      // if cycles to close is unknown, default to 1 cycle (M_2PI radians) 
      cycles_to_close_curve = 1;
    }

    if (cosets_mode == HYBRID_COSETS) {
      coset_z = (int) (coset_z * cycles_to_close_curve);   // WARNING: this may break for small line_count and cycles_to_close_curve non-integer!
    }
    coset_gcd = gcd(coset_d, coset_z);
    coset_lcm = lcm(coset_d, coset_z);
    lines_per_coset = coset_z / coset_gcd;   // lines per polyline (one polyline per coset)
    coset_count = coset_gcd;
    double total_line_segments = lines_per_coset * coset_count;

    if (cosets_mode == CLASSIC_COSETS) {
      cycles_to_close_polyline = coset_lcm / coset_z;
      coset_step_size = M_2PI * ((double) coset_d / (double) coset_z);
    } else if (cosets_mode == CLOSURE_COSETS || cosets_mode == HYBRID_COSETS) {
      cycles_to_close_polyline = cycles_to_close_curve * (coset_lcm / coset_z);
      // double z = cycles_to_close_curve * coset_z;  // or radians_to_close_curve * coset_z / M_2PI
      // lines_per_coset = z / gcd(coset_d, (int)z);
      double radians_to_close = cycles_to_close_curve * M_2PI;
      coset_step_size = radians_to_close * ((double) coset_d / (double) coset_z);
    }
    if (DEBUG_COSETS_INIT && (cosets_mode != 0)) {
      System.out.println("cosets: => count|gcd: " + coset_gcd + ", lcm: " + coset_lcm +
              ", curve close: " + cycles_to_close_curve + ", line close: " + cycles_to_close_polyline + ", coset_z: " + coset_z +
              ", lines_per_coset: " + lines_per_coset + ", total lines: " + total_line_segments);
    }

    double raw_meta_steps = (meta_max_value - meta_min_value) / meta_step_value;
    meta_steps = (int) raw_meta_steps;
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
    if (DEBUG_SAMPLING) {
      System.out.println("sampling");
    }
    for (int i = 0; i < sample_size; i++) {
      count++;
      setValues();
      double theta1, theta2, x1, y1, x2, y2;

      // should be able to improve this, only sample from possible theta
      //    based on step size and lince count:
      // theta1 = theta_step_radians * (int)(Math.random()*line_count);
      theta1 = Math.random() * cycles * M_2PI;   // if using cosets, cycles gets reset based on coset calcs in setValues()
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

      double s1 = curve.getSpeed(theta1, end_point1);

      // find the slope and length of the line
      double ydiff = y2 - y1;
      double xdiff = x2 - x1;
      double m = ydiff / xdiff;  // slope

      double point_angle = getPointAngle(end_point1, end_point2);

      double raw_line_angle = atan2(ydiff, xdiff);  // atan2 range is [-Pi..+Pi]
      // if (raw_line_angle < 0) { line_angle += M_2PI; }   // map to range [0..+2Pi]
      // delta_from_yaxis should be 0 if parallel to y-axis, and M_PI/2 if parallel to x-axis
      double unscaled_line_angle = abs(abs(raw_line_angle) - (M_PI / 2.0));
      // scale to range [0..1]; (0 parallel to y-axis, 1 parallel to x-axis)
      double line_angle = unscaled_line_angle / (M_PI / 2.0);
      double line_length = Math.sqrt((xdiff * xdiff) + (ydiff * ydiff));
      sampled_line_lengths[i] = line_length;
      sampled_line_angles[i] = line_angle;
      sampled_point_angles[i] = point_angle;
      sampled_speeds[i] = s1;
      sampled_thetas[i] = theta1 / M_2PI;
    }
    if (DEBUG_SAMPLING) {
      System.out.println("sorting");
    }
    Arrays.sort(sampled_line_lengths);
    Arrays.sort(sampled_line_angles);
    Arrays.sort(sampled_point_angles);
    Arrays.sort(sampled_speeds);
    Arrays.sort(sampled_thetas);
    if (DEBUG_SAMPLING) {
      System.out.println("low line length: " + sampled_line_lengths[0]);
      System.out.println("high line length: " + sampled_line_lengths[sample_size - 1]);
      System.out.println("low line angle: " + sampled_line_angles[0]);
      System.out.println("high line angle: " + sampled_line_angles[sample_size - 1]);
      System.out.println("low point angle: " + sampled_point_angles[0]);
      System.out.println("high point angle: " + sampled_point_angles[sample_size - 1]);
      System.out.println("low speed: " + sampled_speeds[0]);
      System.out.println("high speed: " + sampled_speeds[sample_size - 1]);
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

    if (a1 < 0) {
      a1 += M_2PI;
    }  // map to [0..2Pi]
    if (a2 < 0) {
      a2 += M_2PI;
    }  // map to [0..2Pi]
    double point_angle = abs(a2 - a1);
    if (point_angle > M_PI) {
      point_angle = M_2PI - point_angle;
    }
    return point_angle;
  }

  public void setCurveParams() {
    a = a_param;
    b = b_param;
    c = c_param;
    d = d_param;
    e = e_param;
    f = f_param;
    g = g_param;
    h = h_param;

  }

  public void setValues() {
    setCurveParams();

    if (cosets_mode != NO_COSETS) {
      theta_step_radians = coset_step_size;
      cycles = cycles_to_close_polyline;
      // if relatively prime, then don't use cosets
      if (coset_gcd == 1) {  // relatively prime if don't share a common denominator other than 1
        initial_theta_radians = 0;
        if (DEBUG_COSETS && count % 50000 == 0) {
          System.out.println("gcd is 1, lcm: " + coset_lcm + ", cycles: " + cycles + ", zero initial_offset");
        }
      } else {
        if (cosets_mode == CLASSIC_COSETS) {
          int coset_id = (int) (Math.floor(Math.random() * coset_gcd));
          initial_theta_radians = M_2PI * ((double) coset_id / (double) coset_z);
        } else if (cosets_mode == CLOSURE_COSETS || cosets_mode == HYBRID_COSETS) {
          // since random gives [0..1), and want [0..{coset_gcd-1}]:
          int coset_id = (int) (Math.floor(Math.random() * coset_gcd));
          double radians_to_close = cycles_to_close_curve * M_2PI;
          initial_theta_radians = radians_to_close * ((double) coset_id / (double) coset_z);
          if (DEBUG_COSETS && count % 50000 == 0) {
            System.out.println("gcd: " + coset_gcd + ", lcm: " + coset_lcm + ", cycles: " + cycles + ", coset: " + coset_id);
          }
        }
      }
    }

    if (meta_mode != OFF) {
      // determine which meta-step to use
      //    should round instead?
      current_meta_step = (int) (Math.random() * meta_steps);
      double meta_value = meta_min_value + (current_meta_step * meta_step_value);
      if (meta_mode == LINE_OFFSET_INCREMENT) {
        // coset_d = meta_min_value + (current_meta_step * meta_step_value);'
        double line_offset = meta_value;
        if (irrationalize != 0) {
          line_offset += (irrationalize * 0.01 * Math.E);
        }
        theta_step_radians = M_2PI * (line_offset / 360);
        cycles = (line_count * theta_step_radians) / M_2PI;
        // actual_step_size = meta_min_step_radians + (meta_step_diff_radians * current_meta_step);
        //      actual_step_size = M_2PI * (((int)(Math.random()* 60)+10)/360.0);
      } else if (meta_mode == INITIAL_OFFSET) {
        double initial_offset = meta_value;
        initial_theta_radians = M_2PI * (initial_offset / 360);
      } else if (meta_mode == A_LINEAR_INCREMENT) {
        a = meta_value;
      } else if (meta_mode == B_LINEAR_INCREMENT) {
        b = meta_value;
      } else if (meta_mode == C_LINEAR_INCREMENT) {
        c = meta_value;
      } else if (meta_mode == D_LINEAR_INCREMENT) {
        d = meta_value;
      } else if (meta_mode == E_LINEAR_INCREMENT) {
        e = meta_value;
      } else if (meta_mode == F_LINEAR_INCREMENT) {
        f = meta_value;
      } else if (meta_mode == A_B_LINEAR_INCREMENT) {
        // treat a param as meta param -- meta_min_value determines min
        // treat b similar, but b_param determines min b
        a = meta_value;
        b = b_param + (current_meta_step * meta_step_value);
      } else if (meta_mode == RENDER_MODIFIER1) {
        render_modifier1 = meta_value;
      } else if (meta_mode == RENDER_MODIFIER2) {
        render_modifier2 = meta_value;
      } else if (meta_mode == RENDER_MODIFIER3) {
        render_modifier3 = meta_value;
      }
      if (DEBUG_META_MODE && count % 50000 == 0) {
        System.out.println("count: " + count + ", metacount = " + (int) (count / 50000));
        System.out.println("meta_step: " + current_meta_step);
        System.out.println("meta_value: " + meta_value);
        System.out.println("a: " + a);
        System.out.println("b: " + b);
      }
    }
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    count++;
    setValues();

    double xin = pAffineTP.x;
    double yin = pAffineTP.y;
    // atan2 range is [-PI, PI], so tin covers 2PI, or 1 cycle (from -0.5 to 0.5 cycle)
    double tin;
    if (randomize) {
      // tin = (Math.random() * M_2PI) - M_PI; // random angle, range [-Pi .. +Pi]
      tin = (Math.random() * M_2PI); // random angle, range [0.. +2Pi]
    } else {
      tin = atan2(yin, xin); // polar coordinate angle (theta in radians) of incoming point [-Pi .. +Pi]
    }
    double t = cycles * tin; // theta parameter for curve calculation

    // reusing curve_point
    // curve_point = getCurveCoords(t, curve_point);
    curve.getCurvePoint(t, curve_point);

    double x = curve_point.x;
    double y = curve_point.y;
    double r = sqrt(x * x + y * y);
    double rinx, riny;
    double rout;
    double xout = 0, yout = 0, zout = 0;

    double step_number, theta1, theta2;
    double x1, y1, x2, y2;

    // map to a Maurer Rose line
    // find nearest step
    step_number = floor(t / theta_step_radians);
    // find distance along line??
    // find radians per full line
    // double line_fraction = (t % theta_step_radians)/radians_per_full_line

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

    // slope of line (m in y=mx+b line equation)
    double line_slope = ydiff / xdiff;
    // y-intercept of line (b in y=mx+b line equation)
    double line_intercept;
    // special-casing for slope being NaN (xdiff = 0) ==> line_intercept set to NaN as well 
    if (Double.isNaN(line_slope)) {
      line_intercept = Double.NaN;
    } else {
      line_intercept = y1 - (line_slope * x1);
    }

    double point_angle = getPointAngle(end_point1, end_point2);

    double raw_line_angle = atan2(ydiff, xdiff);  // atan2 range is [-Pi..+Pi]
    // if (raw_line_angle < 0) { raw_line_angle += M_2PI; }   // map to range [0..+2Pi]
    // delta_from_yaxis should be 0 if parallel to y-axis, and M_PI/2 if parallel to x-axis
    double unscaled_line_angle = abs(abs(raw_line_angle) - (M_PI / 2.0));
    // scale to range [0..1]; (0 parallel to y-axis, 1 parallel to x-axis)
    double line_angle = unscaled_line_angle / (M_PI / 2.0);
    double line_length = Math.sqrt((xdiff * xdiff) + (ydiff * ydiff));

    // yoffset = [+-] line_slope * d / (sqrt(1 + line_slope^2))
    // double xoffset=0, yoffset=0, zoffset = 0;
    double line_delta = 0;
    double midlength = line_length / 2.0;  // use midlength of Maurer line as radius
    double xmid = (x1 + x2) / 2.0;
    double ymid = (y1 + y2) / 2.0;

    // double speed1 = curve.getSpeed(theta1, end_point1);

    boolean use_render_mode = true;
    if (show_points_param > 0 || show_curve_param > 0) {
      double rnd = pContext.random();
      /**
       *  overrides of rendering mode
       */
      double xoffset = 0, yoffset = 0, zoffset = 0;
      double rand2 = Math.random();
      if (rand2 <= show_points_param) {
        // drawing Maurer anchor points instead of specified render_mode
        use_render_mode = false;
        if (point_thickness != 0) {
          double roffset = pContext.random() * point_thickness;
          double rangle = (pContext.random() * M_2PI);
          xoffset = roffset * cos(rangle);
          yoffset = roffset * sin(rangle);
        } else {
          xoffset = 0;
          yoffset = 0;
        }
        if (rnd <= (show_points_param / 2)) {
          xout = x1 + xoffset;
          yout = y1 + yoffset;
        } else {
          xout = x2 + xoffset;
          yout = y2 + yoffset;
        }
        zout = 0;
      } else if (rand2 <= (show_points_param + show_curve_param)) {
        // drawing base curve instead of specified render_mode
        use_render_mode = false;
        if (curve_thickness != 0) {
          xout = x + ((pContext.random() - 0.5) * curve_thickness);
          yout = y + ((pContext.random() - 0.5) * curve_thickness);
        } else {
          xout = x;
          yout = y;
        }
        zout = 0;
      } else {
        use_render_mode = true;
        xout = 0;
        yout = 0;
      }
    }
    if (use_render_mode) {
      double xoffset = 0, yoffset = 0, zoffset = 0;
      // pick a random point along the Maurer line specified by P1 and P2 endpoints
      // t1 ranges from [0..1] as line ranges from P1..P2
      // calculate the corresponding point PML along the Maurer line
      double t1 = Math.random();
      mpoint.x = (x1 * (1 - t1)) + (x2 * t1);
      // same as mpoint.x = x1 + (t1 * (x2-x1));
      mpoint.y = (y1 * (1 - t1)) + (y2 * t1);
      // same as mpoint.y = y1 + (t1 * (y2-y1));
      line_delta = t1 * line_length;

      /**
       *   RENDER MODES
       */
      if (render_mode == LINES || render_mode == DEFAULT) {
        // draw lines
        xout = mpoint.x;
        yout = mpoint.y;
      } else if (render_mode == CIRCLES) {
        // draw circles
        //   circles centered on midpoint of "Maurer line"
        //   and use midlength of Maurer line as radius
        if (render_submode == Z_STROKE) {
          double ang = t1 * M_2PI;
          double rad = midlength;
          double mid_delta = rad * cos(ang);
          zout = rad * sin(ang);
          // move along line, out from midpoint by mid_delta
          xout = xmid + (mid_delta * cos(raw_line_angle));
          yout = ymid + (mid_delta * sin(raw_line_angle));
          line_delta = sqrt(((x1 - xout) * (x1 - xout)) + ((y1 - yout) * (y1 - yout)));
        } else {
          double ang = t1 * M_2PI;
          double rad = midlength;
          xout = xmid + (rad * sin(ang));
          yout = ymid + (rad * cos(ang));
          // line_delta = ang;
          line_delta = sqrt(((x1 - xout) * (x1 - xout)) + ((y1 - yout) * (y1 - yout)));
        }
      } else if (render_mode == QUADRATIC_BEZIER) {
        // use origin (0,0) as control point, and endpoints of Maurer line as Bezier curve endpoints
        //    (since using origin as control point, can drop middle term of standard Bezier curve calc
        // double bt = Math.random();  // want bt => [0:1]
        double bt = t1;
        double ax, ay;
        // use full formula with control point
        if (render_modifier1 != 1 || render_modifier2 != 1) {
          // render_modifier1 controls relative radius of control point
          //   (fraction of distance from midpoint of line to origin,
          //    so as render_modifier range: [0=>1] then control point radius range: [midpoint_radius=>0]
          // render_modifier2 controls relative angle of control point
          //    (angle from midpoint-origin line to control point)
          //    so if render_modifier = 1 then control point angle delta = 0
          double midpoint_radius = sqrt(xmid * xmid + ymid * ymid);
          double midpoint_angle = atan2(ymid, xmid);
          double cradius = (1 - render_modifier1) * midpoint_radius;
          double cangle = midpoint_angle + (M_2PI - (render_modifier2 * M_2PI));
          // double cangle = midpoint_angle;
          double cx = cradius * cos(cangle);
          double cy = cradius * sin(cangle);
          ax = ((1 - bt) * (1 - bt) * x1) + (2 * (1 - bt) * bt * cx) + (bt * bt * x2);
          ay = ((1 - bt) * (1 - bt) * y1) + (2 * (1 - bt) * bt * cy) + (bt * bt * y2);
        } else {
          // control point is origin (0,0), so can drop control point term of quadratic Bezier
          //    and therefore skip midradius, midangle etc. control point calcs
          ax = ((1 - bt) * (1 - bt) * x1) + (bt * bt * x2);
          ay = ((1 - bt) * (1 - bt) * y1) + (bt * bt * y2);
        }

        // line_delta = bt * line_length;
        // for now working on Z submodes here, but handling most submodes at end of transform() method...
        if (render_submode == Z_STROKE) {  // not really working
          zout = ay;
          xoffset = line_delta / Math.sqrt(1 + line_slope * line_slope);
          if (x2 < x1) {
            xoffset = -1 * xoffset;
          }  // determine sign based on p2
          yoffset = Math.abs(line_slope * xoffset);
          if (y2 < y1) {
            yoffset = -1 * yoffset;
          }
          xout = x1 + xoffset;
          yout = y1 + yoffset;
        }
        // hijacking Z_STROKE_BOTH while trying different approaches for Z-Beziers
        else if (render_submode == Z_BOTH_STROKE) {
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
        } else {
          xout = ax;
          yout = ay;
        }
      } else if (render_mode == ELLIPSES) {
        if (render_submode == Z_STROKE) {
          // ang ==> [-Pi : +Pi]  or [ 0 : 2Pi ] ?
          // double  ang = (Math.random() * M_2PI) - M_PI;  // ==> [ -Pi : +Pi ]
          // double ang = (Math.random() * M_2PI);   // ==> [ 0 : 2Pi ]
          double ang = t1 * M_2PI;

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
          double relative_perp_offset = (midlength * (render_modifier2 / 2)) * sin(ang);

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
        } else {
          // double ang = Math.random() * M_2PI;
          double ang = t1 * M_2PI;
          // double ang = (Math.random() * M_2PI) - M_PI;
          // offset along line (relative to start of line)
          double relative_line_offset = (midlength * render_modifier1) * cos(ang);
          line_delta = midlength * cos(ang);

          // offset perpendicular to line:
          // shift angle by -pi/2 get range=>[-1:1] as line_ofset=>[0=>line_length],
          //   then adding 1 to gets range=>[0:2],
          //   then scaling by line_length/2 * amplitude gets perp_offset: [0:(line_length*amplitude)]
          double relative_perp_offset = (midlength * (render_modifier2 / 2)) * sin(ang);
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
      } else if (render_mode == SINE_WAVES) {
        // amplitude calculated such that when render_modifier = 1, relative_perp_offset range: [0 ==> line_length/2]
        double amplitude = (line_length / 4) * render_modifier1;
        double frequency = render_modifier2;
        // range of [0 -> 2Pi ]
        // double ang = Math.random() * M_2PI;
        double ang = t1 * M_2PI;

        // offset along line (relative to start of line) ==> [0=>line_length]
        double relative_line_offset = ang * (line_length / M_2PI);
        line_delta = relative_line_offset;
        // offset perpendicular to line:
        // shift angle by -pi to get cos range=>[-1:1] as ang => [0:2Pi], 
        // find perp offset for endpoints:
        //     since midpoint is always at middle of range (0), and using cos, 
        //     endpoints should have same offset, so use either one
        //     endpoints are at [+/-](Pi*frequency)
        double endpoint_perp_offset = amplitude * cos(M_PI * frequency);  // so at amp=1, freq=1, ==> -1
        double relative_perp_offset = amplitude * (cos((ang - M_PI) * frequency));
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

        // for now working on Z submodes here, but handling most submodes at end of transform() method...
        if (render_submode == Z_STROKE) {
          xout = x1 + (relative_line_offset * cos(raw_line_angle));
          // can save a sin() call by using slope-intercept once have xout
          // yout = y1 + (relative_line_offset * sin(raw_line_angle));
          yout = (line_slope * xout) + line_intercept;
          zout = relative_perp_offset; // plus z1?
        } else if (render_submode == Z_BOTH_STROKE) {
          xout = x1 + (relative_line_offset * cos(raw_line_angle));
          // yout = y1 + (relative_line_offset * sin(raw_line_angle));
          yout = (line_slope * xout) + line_intercept;
          if (Math.random() < 0.5) {
            zout = relative_perp_offset; // plus z1?
          } else {
            zout = -relative_perp_offset;
          }
        } else {
          xout = newx;
          yout = newy;
        }
      } else if (render_mode == SEQUIN_CIRCLE_SPLINE) {
        // Circle Splines with angle-based trionometric interpolation
        // attempting to implement strategy described by Sequin, Lee, Yen in paper
        // "Fair, G2- and C2-continuous circle splines for the interpolation of sparse data points", 2005

        // Want to calculate interpolated point P(u) between P1 and P2, where u:[0=>1]
        // Need P0, P1, P2, P3
        // already have P1, P2, endpoints of current Maurer line
        // calculate P0 and P3, from t=(theta1-theta_offset) and t=(theta2+that_offset) respectively
        double theta0 = theta1 - this.theta_step_radians;
        double theta3 = theta2 + this.theta_step_radians;
        curve.getCurvePoint(theta0, end_point0);
        curve.getCurvePoint(theta3, end_point3);
        double x0 = end_point0.x;
        double y0 = end_point0.y;
        double x3 = end_point3.x;
        double y3 = end_point3.y;

        // then calculate unit direction vectors:
        // A = (P1-P0)/|P1-P0|
        // B = (P2-P1)/|P2-P1|
        // C = (P2-P0)/|P2-P0|
        // D = (P3-P2)/|P3-P2|
        // E = (P3-P1)/|P3-P1|
        // where |Pk - Pj| = norm(Pk-Pi) = sqrt((xk-xi)^2 + (yk-yi)^2)
        double norm10 = sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
        double norm21 = sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        double norm20 = sqrt((x2 - x0) * (x2 - x0) + (y2 - y0) * (y2 - y0));
        double norm32 = sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
        double norm31 = sqrt((x3 - x1) * (x3 - x1) + (y3 - y1) * (y3 - y1));
        vecA.x = (x1 - x0) / norm10;
        vecA.y = (y1 - y0) / norm10;
        vecB.x = (x2 - x1) / norm21;
        vecB.y = (y2 - y1) / norm21;
        vecC.x = (x2 - x0) / norm20;
        vecC.y = (y2 - y0) / norm20;
        vecD.x = (x3 - x2) / norm32;
        vecD.y = (y3 - y2) / norm32;
        vecE.x = (x3 - x1) / norm31;
        vecE.y = (y3 - y1) / norm31;

        // calculate tangent angles at P1 and P2:
        //  (hmm, wonder if could substitute exact tangent calculation for base curve here?? not sure if it's the same angle though...)
        // a1 = arccos(A @ C) ==>  @ is vector dot product
        // a2 = arccos(E @ D)
        double a1 = Math.acos((vecA.x * vecC.x) + (vecA.y * vecC.y));
        double a2 = Math.acos((vecE.x * vecD.x) + (vecE.y * vecD.y));

        // trigonometric angle blending function between P0 and P1:
        // a(u) = (a1 * cos^2(u*Pi/2)) + (a2 * sin^2(u*Pi/2))
        double u = t1;  // already have t1:[0=>1]
        double cu = cos(u * M_PI / 2);
        double su = sin(u * M_PI / 2);
        double au = (a1 * cu * cu) + (a2 * su * su);
        // now can calculate distance of P(u), d(P(u)) from P1:
        //  b = |P2-P1|  ==> should already have this as line length for current Maurer line
        //  d(P(u)) = b * sin(u * a(u)) / sin(a(u))
        double bp = norm21;  // same as line_length?
        double pd = bp * sin(u * au) / sin(au);

        // and deviation angle from Maurer line is 
        //  dangle(u) = (1-u) * a(u)
        double pa = (1 - u) * au;
        pa = -pa; // ???

        // then place point along Maurer line P1P2 at distance d(P(u)) from endpoint P1, 
        // http://math.stackexchange.com/questions/409689/how-do-i-find-a-point-a-given-distance-from-another-point-along-a-line
        double linex = x1 + ((x2 - x1) * pd / norm21);
        double liney = y1 + ((y2 - y1) * pd / norm21);

        //    and rotate by deviation angle dangle(u) around P1
        //    to get final position position of P(u)
        // 2D rotation transformation of point B about a given fixed point A to give point C
        // C.x = A.x + (B.x - A.x) * cos(theta) - (B.y - A.y) * sin(theta)
        // C.y = A.y + (B.x - A.x) * sin(theta) + (B.y - A.y) * cos(theta)
        xout = x1 + ((linex - x1) * cos(pa)) - ((liney - y1) * sin(pa));
        yout = y1 + ((linex - x1) * sin(pa)) + ((liney - y1) * cos(pa));
      } else if (render_mode == CARDINAL_SPLINE ||
              render_mode == UNIFORM_CATMULL_ROM_SPLINE ||
              render_mode == CARDINAL_SPLINE_SWIZZLE1 ||
              render_mode == CARDINAL_SPLINE_SWIZZLE2 ||
              render_mode == CARDINAL_SPLINE_SWIZZLE3) {
        // cobbled together from:
        //    https://en.wikipedia.org/wiki/Cubic_Hermite_spline#Catmull.E2.80.93Rom_spline
        //    http://paulbourke.net/miscellaneous/interpolation/
        //    http://cubic.org/docs/hermite.htm
        //  
        // Hermite Basis Functions: 
        // range from [0:1] along [0..line_length]
        // already have t1, randome [0:1]
        double t2 = t1 * t1;
        double t3 = t1 * t1 * t1;
        double h00 = 2 * t3 - 3 * t2 + 1;
        double h10 = t3 - 2 * t2 + t1;
        double h01 = -2 * t3 + 3 * t2;
        double h11 = t3 - t2;

        // have points p1 and p2, get p0 and p3 (previous point and next point)
        double theta0 = theta1 - theta_step_radians;
        double theta3 = theta2 + theta_step_radians;
        curve.getCurvePoint(theta0, end_point0);
        curve.getCurvePoint(theta3, end_point3);
        double x0, y0, x3, y3;
        if (render_mode == CARDINAL_SPLINE_SWIZZLE1) {
          x0 = end_point0.y;
          y0 = end_point0.x;
          x3 = end_point3.y;
          y3 = end_point3.x;
        } else if (render_mode == CARDINAL_SPLINE_SWIZZLE2) {
          x0 = end_point3.x;
          y0 = end_point3.y;
          x3 = end_point0.x;
          y3 = end_point0.y;
        } else if (render_mode == CARDINAL_SPLINE_SWIZZLE3) {
          x0 = end_point3.y;
          y0 = end_point3.x;
          x3 = end_point0.y;
          y3 = end_point0.x;
        } else {  // render_mode == CARDINAL_SPLINE or UNIFORM_CATMULL_ROM_SPLINE
          x0 = end_point0.x;
          y0 = end_point0.y;
          x3 = end_point3.x;
          y3 = end_point3.y;
        }

        // m1 ==> cardinal spline calculated tangent for end_point1        
        // m2 ==> cardinal spline calculated tangent for end_point1        
        // M1 = (P2 - P0) / (t2 - t0)
        // M2 = (P3 - P1) / (t3 - t1)
        // for now assuming t(i+1) = t(i) + 1, or in other words (t2-t0) = 2 and (t3-t1) = 2
        // (I think this is a standard assumption for baseline Catmull-Rom, see for example http://cubic.org/docs/hermite.htm)
        // so 
        // M1 = (P2 - PO)/2
        // M2 = (P3 - P1)/2
        double m1x = 0.5 * (x2 - x0);
        double m2x = 0.5 * (x3 - x1);
        double m1y = 0.5 * (y2 - y0);
        double m2y = 0.5 * (y3 - y1);

        // also trying addition of a tightness parameter
        // for the case where tanscale = 1 (render_modifier1 = 0), 
        //      get the uniform Catmull-Rom spline as a special case of cardinal splines
        // for the case where transcale = 0 (render_modifier1 = 1), tangents have no effect
        double tanscale;
        if (render_mode == UNIFORM_CATMULL_ROM_SPLINE) {
          tanscale = 1;
        } else {
          tanscale = (1 - render_modifier1);
        }

        m1x = m1x * tanscale;
        m1y = m1y * tanscale;
        m2x = m2x * tanscale;
        m2y = m2y * tanscale;

        double xnew = (h00 * x1) + (h10 * m1x) + (h01 * x2) + (h11 * m2x);
        double ynew = (h00 * y1) + (h10 * m1y) + (h01 * y2) + (h11 * m2y);
        // line_delta = t1 * line_length;
        xout = xnew;
        yout = ynew;
      } else if (render_mode == NONUNIFORM_CATMULL_ROM_SPLINE ||
              render_mode == CENTRIPETAL_CATMULL_ROM_SPLINE ||
              render_mode == CHORDAL_CATMULL_ROM_SPLINE) {
        // cobbled together from:
        //    https://en.wikipedia.org/wiki/Centripetal_Catmull%E2%80%93Rom_spline
        //    https://en.wikipedia.org/wiki/Cubic_Hermite_spline#Catmull.E2.80.93Rom_spline
        // most helpful for actual implementation: 
        //      http://stackoverflow.com/questions/9489736/catmull-rom-curve-with-no-cusps-and-no-self-intersections/19283471#19283471
        // 
        // Original Paper: "A Recursive Evaluation Algorithm for a Class of Catmull-Rom Splines", Barry and Goldman

        // Hermite Basis Functions: 
        // range from [0:1] along [0..line_length]
        // already have t1, random [0:1]
        double t2 = t1 * t1;
        double t3 = t1 * t1 * t1;
        double h00 = 2 * t3 - 3 * t2 + 1;
        double h10 = t3 - 2 * t2 + t1;
        double h01 = -2 * t3 + 3 * t2;
        double h11 = t3 - t2;

        // have points p1 and p2, get p0 and p3 (previous point and next point)
        double theta0 = theta1 - theta_step_radians;
        double theta3 = theta2 + theta_step_radians;
        curve.getCurvePoint(theta0, end_point0);
        curve.getCurvePoint(theta3, end_point3);
        double x0 = end_point0.x;
        double y0 = end_point0.y;
        double x3 = end_point3.x;
        double y3 = end_point3.y;

        double alpha = 0;
        if (render_mode == CENTRIPETAL_CATMULL_ROM_SPLINE) {
          alpha = 0.5;
        } else if (render_mode == CHORDAL_CATMULL_ROM_SPLINE) {
          alpha = 1.0;
        } else {  // render_mode == NONUNIFORM_CATMULL_ROM_SPLINE
          // nonuniform Catmull-Rom spline, with special cases:
          // rm = 0 ==> uniform Catmull-Rom spline (essentially same as cardinal spline)
          // rm = 0.5 ==> centripetal Catmull-Rom spline
          // rm = 1.0 ==> chordal Catmull-Rom spline
          alpha = render_modifier2;
        }

        // centripetal/chordal Catmull-Rom tangent caclulations
        // M1 = (P1 - P0) / (t1 - t0) - (P2 - P0) / (t2 - t0) + (P2 - P1) / (t2 - t1)
        // M2 = (P2 - P1) / (t2 - t1) - (P3 - P1) / (t3 - t1) + (P3 - P2) / (t3 - t2)
        // and t[i+1] = (((x[i+1]-x[i])^2 + (y[i+1]-y[i])^2)^0.5)^a + t[i]
        //     t[i+1] = (((x[i+1]-x[i])^2 + (y[i+1]-y[i])^2))^(a/2) + t[i]
        // rewrite in terms of deltas, then can drop the last term
        // dt0 = t1-t0 = (((x1-x0)^2 + (y1-y0)^2)) ^(a/2)
        // dt1 = t2-t1
        // dt2 = t3-t2
        // M1 = ((P1-P0)/dt0) - ((P2-P0)/(dt0+dt1)) + ((P2-P1)/dt1)
        // M2 = ((P2-P1)/dt1) - ((P3-P1)/(dt1+dt2)) + ((P3-P2)/dt2)
        // also need to rescale by dt1 (so can parameterize t from [0:1]) ??
        // do scaling when combining terms?
        double dt0 = Math.pow(((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)), (alpha / 2));
        double dt1 = Math.pow(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)), (alpha / 2));
        double dt2 = Math.pow(((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2)), (alpha / 2));

        double m1x = ((x1 - x0) / dt0) - ((x2 - x0) / (dt0 + dt1)) + ((x2 - x1) / dt1);
        double m1y = ((y1 - y0) / dt0) - ((y2 - y0) / (dt0 + dt1)) + ((y2 - y1) / dt1);
        double m2x = ((x2 - x1) / dt1) - ((x3 - x1) / (dt1 + dt2)) + ((x3 - x2) / dt2);
        double m2y = ((y2 - y1) / dt1) - ((y3 - y1) / (dt1 + dt2)) + ((y3 - y2) / dt2);

        // scaling tangents (analagous to normalizing to the unit tangent?)
        // double tanscale = dt1;
        // also trying addition of a tightness parameter
        double tanscale = dt1 * (1 - render_modifier1);
        m1x = m1x * tanscale;
        m1y = m1y * tanscale;
        m2x = m2x * tanscale;
        m2y = m2y * tanscale;

        double xnew = (h00 * x1) + (h10 * m1x) + (h01 * x2) + (h11 * m2x);
        double ynew = (h00 * y1) + (h10 * m1y) + (h01 * y2) + (h11 * m2y);
        // line_delta = t1 * line_length;
        xout = xnew;
        yout = ynew;

      } else if (render_mode == KOCHANEK_BARTELS_SPLINE ||
              render_mode == KOCHANEK_BARTELS_SPLINE_SWIZZLE1 ||
              render_mode == KOCHANEK_BARTELS_SPLINE_SWIZZLE2 ||
              render_mode == KOCHANEK_BARTELS_SPLINE_SWIZZLE3) {
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
        // double t1 = Math.random();
        double t2 = t1 * t1;
        double t3 = t1 * t1 * t1;
        double h00 = 2 * t3 - 3 * t2 + 1;
        double h10 = t3 - 2 * t2 + t1;
        double h01 = -2 * t3 + 3 * t2;
        double h11 = t3 - t2;

        // have points p1 and p2, get p0 and p3 (previous point and next point)
        double theta0 = theta1 - theta_step_radians;
        double theta3 = theta2 + theta_step_radians;
        curve.getCurvePoint(theta0, end_point0);
        curve.getCurvePoint(theta3, end_point3);

        double x0, y0, x3, y3;
        if (render_mode == KOCHANEK_BARTELS_SPLINE_SWIZZLE1) {
          x0 = end_point0.y;
          y0 = end_point0.x;
          x3 = end_point3.y;
          y3 = end_point3.x;
        } else if (render_mode == KOCHANEK_BARTELS_SPLINE_SWIZZLE2) {
          x0 = end_point3.x;
          y0 = end_point3.y;
          x3 = end_point0.x;
          y3 = end_point0.y;
        } else if (render_mode == KOCHANEK_BARTELS_SPLINE_SWIZZLE3) {
          x0 = end_point3.y;
          y0 = end_point3.x;
          x3 = end_point0.y;
          y3 = end_point0.x;
        } else {  // KOCHANEK_BARTELS_SPLINE
          x0 = end_point0.x;
          y0 = end_point0.y;
          x3 = end_point3.x;
          y3 = end_point3.y;
        }

        // but want defaults for tension, bias, continuity to be 0?
        double tension = render_modifier1;
        double continuity = render_modifier2;
        double bias = render_modifier3;

        // m2 ==> KB-calculated tangent for end_point2
        double c110 = (1 - tension) * (1 + bias) * (1 + continuity) / 2;
        double c121 = (1 - tension) * (1 - bias) * (1 - continuity) / 2;
        double c221 = (1 - tension) * (1 + bias) * (1 - continuity) / 2;
        double c232 = (1 - tension) * (1 - bias) * (1 + continuity) / 2;

        // m1 ==> KB-calculated tangent for end_point1        
        // m2 ==> KB-calculated tangent for end_point1    
        // note that when tension = bias = continuity = 0, all coefficients become 0.5, 
        // and thus this reduces to Catmull-Rom spline, for example:
        //     m1x = (c110 * (x1-x0)) + (c121 * (x2-x1)) 
        //         = (0.5 * (x1-x0)) + (0.5 *(x2-x1))
        //         =  0.5 * (x1 - x0 + x2 - x1)
        //         =  0.5 * (x2 - x1)
        //      
        double m1x = (c110 * (x1 - x0)) + (c121 * (x2 - x1));
        double m2x = (c221 * (x2 - x1)) + (c232 * (x3 - x2));

        double m1y = (c110 * (y1 - y0)) + (c121 * (y2 - y1));
        double m2y = (c221 * (y2 - y1)) + (c232 * (y3 - y2));

        double xnew = (h00 * x1) + (h10 * m1x) + (h01 * x2) + (h11 * m2x);
        double ynew = (h00 * y1) + (h10 * m1y) + (h01 * y2) + (h11 * m2y);
        // line_delta = t1 * line_length;   // already calculated at start of use_render_mode loop
        xout = xnew;
        yout = ynew;
      } else if (render_mode == CUBIC_HERMITE_SPLINE ||
              render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT1 ||
              render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT2 ||
              render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT3 ||
              render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT4 ||
              render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT5) {
        // using general cubic hermite spline, with tangent vectors determined by derivative of underlying curve
        // using formulation for interpolation on an arbitrary interval:
        //     https://en.wikipedia.org/wiki/Cubic_Hermite_spline

        // w is random theta between theta1 and theta2
        // double w = theta1 + (Math.random() * (theta2 - theta1));
        // double t1 = (w-theta1)/(theta2-theta1);
        // but the above w & t1 calcs are equivalent to t1 randomly ranging from [0:1], so just use that instead...

        // t1 ranges from [0:1]
        // double t1 = Math.random();
        double t2 = t1 * t1;
        double t3 = t1 * t1 * t1;

        // Hermite Basis Functions:
        double h00 = 2 * t3 - 3 * t2 + 1;
        double h10 = t3 - 2 * t2 + t1;
        double h01 = -2 * t3 + 3 * t2;
        double h11 = t3 - t2;

        double dt1, dt2;
        // use render_modifier2 as theta offset (- for dt1, + for dt2) to find points to determine tangent vectors
        if (tangent_submode == THETA_TANGENT) {
          dt1 = theta1 - ((render_modifier2 / 360) * M_2PI);
          dt2 = theta2 + ((render_modifier2 / 360) * M_2PI);
        }
        // use render_modifier2 to determine Maurer anchor points to use for tangent vectors
        //    (-rm2*theta_step_radians for dt1, +rm2*theta_line_offset for dt2) 
        else if (tangent_submode == INDEX_TANGENT) {
          dt1 = theta1 - (render_modifier2 * this.theta_step_radians);
          dt2 = theta2 + (render_modifier2 * this.theta_step_radians);
        } else {
          dt1 = theta1;
          dt2 = theta2;
        }

        double tan1x, tan1y, tan2x, tan2y;
        if (render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT1 ||
                render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT2 ||
                render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT3 ||
                render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT4 ||
                render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT5) {
          // using wrong (but interesting) calculations for first derivative
          double k = a / b;
          first_derivative_point1.x = -(cos(k * dt1) + c) * sin(dt1);
          first_derivative_point1.y = (cos(k * dt1) + c) * cos(dt1);
          first_derivative_point2.x = -(cos(k * dt2) + c) * sin(dt2);
          first_derivative_point2.y = (cos(k * dt2) + c) * cos(dt2);
          if (render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT2) {  // flip signs for x of tangents
            first_derivative_point1.x = -1 * first_derivative_point1.x;
            first_derivative_point2.x = -1 * first_derivative_point2.x;
          } else if (render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT3) { // flip signs for y of tangents
            first_derivative_point1.y = -1 * first_derivative_point1.y;
            first_derivative_point2.y = -1 * first_derivative_point2.y;
          } else if (render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT4) {  // flip signs for both x and y of tangents
            first_derivative_point1.x = -1 * first_derivative_point1.x;
            first_derivative_point2.x = -1 * first_derivative_point2.x;
            first_derivative_point1.y = -1 * first_derivative_point1.y;
            first_derivative_point2.y = -1 * first_derivative_point2.y;
          } else if (render_mode == CUBIC_HERMITE_HAPPY_ACCIDENT5) { // flip tangent points
            DoublePoint2D ptmp = first_derivative_point1;
            first_derivative_point1 = first_derivative_point2;
            first_derivative_point2 = ptmp;
          }

        } else { // CUBIC_HERMITE
          // calculating tangent vectors
          // use first derivative of curve (currently using rhodonea) at each endpoint for tangent vectors
          //     tangent = f'(t)   
          curve.getFirstDerivative(dt1, first_derivative_point1);
          curve.getFirstDerivative(dt2, first_derivative_point2);
        }

        tan1x = first_derivative_point1.x;
        tan1y = first_derivative_point1.y;
        tan2x = first_derivative_point2.x;
        tan2y = first_derivative_point2.y;
        if (Double.isNaN(tan1x) || Double.isNaN(tan1y) ||
                Double.isNaN(tan2x) || Double.isNaN(tan2y)) {
          // if can't find first derivative, bailout with point unchanged
          xout = xin;
          yout = yin;
        } else {
          double tanscale;
          // if *_UNSCALED_* then don't apply scaling adjustment to vectors
          if (tangent_submode == UNSCALED_TANGENT ||
                  tangent_submode == UNSCALED_UNIT_TANGENT ||
                  tangent_submode == UNSCALED_NORMAL ||
                  tangent_submode == UNSCALED_UNIT_NORMAL) {
            tanscale = 1;
          } else {  // apply standard Hermite non-unit-interval scaling to vectors
            // TANGENT (or NORMAL?)
            tanscale = theta2 - theta1;
          }

          double dx1, dx2, dy1, dy2;
          // if *_NORMAL_* then set (dx1, dy1) and (dx2, dy2) to normal vectors instead of tangent vectors
          if (tangent_submode == NORMAL_VECTOR ||
                  tangent_submode == UNIT_NORMAL ||
                  tangent_submode == UNSCALED_NORMAL ||
                  tangent_submode == UNSCALED_UNIT_NORMAL) {
            // rotate tangent vector around point to get normal vector?
            // 2D rotation transformation of point B about a given fixed point A to give point C
            // C.x = A.x + (B.x - A.x) * cos(theta) - (B.y - A.y) * sin(theta)
            // C.y = A.y + (B.x - A.x) * sin(theta) + (B.y - A.y) * cos(theta)
            double rota = -M_PI / 2.0;
            dx1 = x1 + ((tan1x - x1) * cos(rota)) - ((tan1y - y1) * sin(rota));
            dy1 = y1 + ((tan1x - x1) * sin(rota)) + ((tan1y - y1) * cos(rota));
            dx2 = x2 + ((tan2x - x2) * cos(rota)) - ((tan2y - y2) * sin(rota));
            dy2 = y2 + ((tan2x - x2) * sin(rota)) + ((tan2y - y2) * cos(rota));
          } else if (tangent_submode == ROTATION_TANGENT) {
            // user render_modifier2 to determine angle (in degrees) to rotate tangent1
            // user render_modifier3 to determine angle (in degrees) to rotate tangent2
            //   (or maybe should only use one modifier, and rotates both tangents?)
            //   (or one to modify x of both tangents, one to modify y of both tangents? ==> like happy_accident modes?)
            double rota1 = (render_modifier2 / 360) * M_2PI;
            dx1 = x1 + ((tan1x - x1) * cos(rota1)) - ((tan1y - y1) * sin(rota1));
            dy1 = y1 + ((tan1x - x1) * sin(rota1)) + ((tan1y - y1) * cos(rota1));
            double rota2 = (render_modifier3 / 360) * M_2PI;
            dx2 = x2 + ((tan2x - x2) * cos(rota2)) - ((tan2y - y2) * sin(rota2));
            dy2 = y2 + ((tan2x - x2) * sin(rota2)) + ((tan2y - y2) * cos(rota2));
          }
          // attempting to apply reflection/scaling used in happy-accident modes to other modes
          // scale x of tangent by render_modifier2 param
          // scale y of tangent by render_modifier3 param
          else if (tangent_submode == XY_SCALE_TANGENT) {
            dx1 = tan1x * render_modifier2;
            dx2 = tan2x * render_modifier2;
            dy1 = tan1y * render_modifier3;
            dy2 = tan2y * render_modifier3;
          } else { // otherwise set (dx1,dy1) and (dx2,dy2) to tangent vectors
            dx1 = tan1x;
            dy1 = tan1y;
            dx2 = tan2x;
            dy2 = tan2y;
          }

          // if *_UNIT_* then normalize to unit vectors
          if (tangent_submode == UNIT_TANGENT ||
                  tangent_submode == UNIT_NORMAL ||
                  tangent_submode == UNSCALED_UNIT_TANGENT ||
                  tangent_submode == UNSCALED_UNIT_NORMAL) {
            // normalizing the vectors
            double sr1 = sqrt(dx1 * dx1 + dy1 * dy1);
            double sr2 = sqrt(dx2 * dx2 + dy2 * dy2);
            dx1 = dx1 / sr1;
            dy1 = dy1 / sr1;
            dx2 = dx2 / sr2;
            dy2 = dy2 / sr2;
          } else {
            // otherwise leave unnormalized
          }
          // apply tension -- use render_modifier1 as tension parameter for cubic Hermite interpolation
          //    by applying a (1 - tension) scaling factor to the tangent vector Hermite terms
          //    thus if tension = 1, tangent terms will drop out and cubic Hermite interpolation will
          //    reduce to linear interoplation ==> Maurer lines
          tanscale = (1 - render_modifier1) * tanscale;
          // trying to add parameter similar to Kochanek-Bartels "bias"
          // at tension = -1, first tangent term (h10) will be factored out
          // at tension = +1, second tangnt term (h11) will be factored out
          // double tanscale10 = (1 + render_modifier2) * tanscale;
          // double tanscale11 = (1 - render_modifier2) * tanscale;

          //double xnew = (h00 * x1) + (h10 * dx1 * tanscale10 ) + (h01 * x2) + (h11 * dx2 * tanscale11);
          //double ynew = (h00 * y1) + (h10 * dy1 * tanscale10 ) + (h01 * y2) + (h11 * dy2 * tanscale11);
          double xnew = (h00 * x1) + (h10 * dx1 * tanscale) + (h01 * x2) + (h11 * dx2 * tanscale);
          double ynew = (h00 * y1) + (h10 * dy1 * tanscale) + (h01 * y2) + (h11 * dy2 * tanscale);
          xout = xnew;
          yout = ynew;
        }
      } // end CUBIC_HERMITE_TANGENTS


      else if (render_mode == CUBIC_HERMITE_TANGENT_FORM2) {
        // using general cubic hermite spline, with tangent vectors determined by derivative of underlying curve
        // trying long-form 3rd degree hermite polynomial equation from http://www3.nd.edu/~zxu2/acms40390F12/Lec-3.4-5.pdf
        // for SPLINE6, attempting to introduce render modifier parameters (somewhat analogous to KB tension, continuity, bias?) 

        double w0 = theta1;
        curve.getFirstDerivative(w0, first_derivative_point1);
        double ffw0 = first_derivative_point1.x;
        double ggw0 = first_derivative_point1.y;
        // double ffw0 = (-k * sin(k*w0) * cos(w0)) - ((cos(k*w0)+c) * sin(w0));  // first x derivative at P0 (of rhodonea)
        // double ggw0 = (-k * sin(k*w0) * sin(w0)) + ((cos(k*w0)+c) * cos(w0));  // first y derivative at P0 (of rhodonea)

        double w1 = theta2;
        curve.getFirstDerivative(w1, first_derivative_point2);
        double ffw1 = first_derivative_point2.x;
        double ggw1 = first_derivative_point2.y;
        // double ffw1 = (-k * sin(k*w1) * cos(w1)) - ((cos(k*w1)+c) * sin(w1));  // first x derivative at P1 (of rhodonea)       
        // double ggw1 = (-k * sin(k*w1) * sin(w1)) + ((cos(k*w1)+c) * cos(w1));  // first y derivative at P1 (of rhodonea)

        double fw0 = x1;
        double gw0 = y1;
        double fw1 = x2;
        double gw1 = y2;

        if (Double.isNaN(ffw0) || Double.isNaN(ggw0) || Double.isNaN(ffw1) || Double.isNaN(ggw1)) {
          // if can't find first derivatives, bailout with point unchanged
          xout = xin;
          yout = yin;
        } else {
          // w (standing in for x from above hermite equation) ranges from [theta1:theta2]
          double w = theta1 + (t1 * (theta2 - theta1));

          if (DEBUG_TANGENTS && Math.random() < 0.01) {
            double point_threshold = Math.random();
            if (point_threshold < 0.5) {
              // linear interp between point and tangent(point)
              double fraction_of_line = 2 * point_threshold;
              xout = (fw0 * (1 - fraction_of_line)) + (ffw0 * fraction_of_line);
              yout = (gw0 * (1 - fraction_of_line)) + (ggw0 * fraction_of_line);
            } else {
              double fraction_of_line = 2 * (point_threshold - 0.5);
              xout = (fw1 * (1 - fraction_of_line)) + (ffw1 * fraction_of_line);
              yout = (gw1 * (1 - fraction_of_line)) + (ggw1 * fraction_of_line);
            }
          } else {
            double tw = (w - w0) / (w1 - w0);
            double tw1 = ((w1 - w) / (w1 - w0));
            double tw0 = ((w0 - w) / (w0 - w1));

            double hf0 = (1 + (2 * tw)) * tw1 * tw1 * fw0;
            double hff0 = (w - w0) * tw1 * tw1 * ffw0;
            double hf1 = (1 + (2 * tw1)) * tw0 * tw0 * fw1;
            double hff1 = (w - w1) * tw0 * tw0 * ffw1;
            double fw = hf0 + hff0 + hf1 + hff1;

            double hg0 = (1 + (2 * tw)) * tw1 * tw1 * gw0;
            double hgg0 = (w - w0) * tw1 * tw1 * ggw0;
            double hg1 = (1 + (2 * tw1)) * tw0 * tw0 * gw1;
            double hgg1 = (w - w1) * tw0 * tw0 * ggw1;
            double gw = hg0 + hgg0 + hg1 + hgg1;

            xout = fw;
            yout = gw;
          }
        }
      } // end CUBIC_HERMITE_TANGENT_FORM2
      else {
        // default to Maurer lines
        xout = mpoint.x;
        yout = mpoint.y;
      }
      //
      //  END RENDER_MODES
      // 

      // 
      // RENDER_SUBMODES
      //    most handled here, still have some Z-modes and others special-cased above
      // 
      if (render_submode == STROKE || render_submode == DEFAULT) {
        // do nothing
      } else if (render_submode == REFLECTED_STROKE) {
        double bc = (((x2 - x1) * (xout - x1)) + ((y2 - y1) * (yout - y1))) / (((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        xout = (2 * (x1 + ((x2 - x1) * bc))) - xout;
        yout = (2 * (y1 + ((y2 - y1) * bc))) - yout;
      } else if (render_submode == BOTH_STROKE) {
        if (Math.random() < 0.5) {  // reflect half the points, leave other half unaltered
          double bc = (((x2 - x1) * (xout - x1)) + ((y2 - y1) * (yout - y1))) / (((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
          xout = (2 * (x1 + ((x2 - x1) * bc))) - xout;
          yout = (2 * (y1 + ((y2 - y1) * bc))) - yout;
        }
      } else if (render_submode == ALTERNATING_STROKE) {
        if (step_number % 2 != 0) { // reflect odd steps, leave even  steps unaltered
          double bc = (((x2 - x1) * (xout - x1)) + ((y2 - y1) * (yout - y1))) / (((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
          xout = (2 * (x1 + ((x2 - x1) * bc))) - xout;
          yout = (2 * (y1 + ((y2 - y1) * bc))) - yout;
        }
      } else if (render_submode == FILL_TO_LINE || render_submode == FILL_TO_CURVE) {
        DoublePoint2D opoint;
        if (render_submode == FILL_TO_CURVE) {
          opoint = curve_point;
        } else {
          opoint = mpoint;
        }
        // randomly place point on line from outpoint to mpoint
        double rfill = Math.random();
        xout = (xout * (1 - rfill)) + (opoint.x * rfill);
        yout = (yout * (1 - rfill)) + (opoint.y * rfill);
      } else if (render_submode == FILL_BETWEEN_LINE_CURVE) { // ignores render mode
        double rfill = Math.random();
        xout = (mpoint.x * (1 - rfill)) + (curve_point.x * rfill);
        yout = (mpoint.y * (1 - rfill)) + (curve_point.y * rfill);
      } else if (render_submode == FILL_BETWEEN_LINE_CURVE_SWIZZLE) {  // ignores render mode
        double rfill = Math.random();
        xout = (mpoint.x * (1 - rfill)) + (curve_point.y * rfill);
        yout = (mpoint.y * (1 - rfill)) + (curve_point.x * rfill);
      } else if (render_submode == REFLECTED_FILL_TO_LINE || render_submode == REFLECTED_FILL_TO_CURVE) {
        DoublePoint2D opoint;
        if (render_submode == REFLECTED_FILL_TO_CURVE) {
          opoint = curve_point;
        } else {
          opoint = mpoint;
        }
        double bc = (((x2 - x1) * (xout - x1)) + ((y2 - y1) * (yout - y1))) / (((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        xout = (2 * (x1 + ((x2 - x1) * bc))) - xout;
        yout = (2 * (y1 + ((y2 - y1) * bc))) - yout;
        double rfill = Math.random();
        xout = (xout * (1 - rfill)) + (opoint.x * rfill);
        yout = (yout * (1 - rfill)) + (opoint.y * rfill);
      } else if (render_submode == BOTH_FILL_TO_LINE || render_submode == BOTH_FILL_TO_CURVE) {
        DoublePoint2D opoint;
        if (render_submode == BOTH_FILL_TO_CURVE) {
          opoint = curve_point;
        } else {
          opoint = mpoint;
        }
        if (Math.random() < 0.5) {  // reflect half the points, leave other half unaltered
          double bc = (((x2 - x1) * (xout - x1)) + ((y2 - y1) * (yout - y1))) / (((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
          xout = (2 * (x1 + ((x2 - x1) * bc))) - xout;
          yout = (2 * (y1 + ((y2 - y1) * bc))) - yout;
        }
        double rfill = Math.random();
        xout = (xout * (1 - rfill)) + (opoint.x * rfill);
        yout = (yout * (1 - rfill)) + (opoint.y * rfill);
      } else if (render_submode == ALTERNATING_FILL_TO_LINE || render_submode == ALTERNATING_FILL_TO_CURVE) {
        DoublePoint2D opoint;
        if (render_submode == ALTERNATING_FILL_TO_CURVE) {
          opoint = curve_point;
        } else {
          opoint = mpoint;
        }
        if (step_number % 2 != 0) { // reflect odd steps, leave even steps unaltered
          double bc = (((x2 - x1) * (xout - x1)) + ((y2 - y1) * (yout - y1))) / (((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
          xout = (2 * (x1 + ((x2 - x1) * bc))) - xout;
          yout = (2 * (y1 + ((y2 - y1) * bc))) - yout;
        }
        double rfill = Math.random();
        xout = (xout * (1 - rfill)) + (opoint.x * rfill);
        yout = (yout * (1 - rfill)) + (opoint.y * rfill);
      }
//      else if (render_submode == CIRCLE_EXTRUDE) {
//        
//      }

      // handling thickness
      if (line_thickness != 0) {
        if (line_thickness_strategy == RANDOM) {  //  previous simple random offset from center of line 
          xout += ((pContext.random() - 0.5) * line_thickness);
          yout += ((pContext.random() - 0.5) * line_thickness);
        } else if (line_thickness_strategy == PERPENDICULAR) { // random distance _perpendicular_ to unmodified Maurer line;
          double xnorm = xdiff / line_length;
          double ynorm = ydiff / line_length;
          double xperpnorm = -ynorm;
          double yperpnorm = xnorm;
          double perp_offset = (pContext.random() - 0.5) * line_thickness;
          double xperp = perp_offset * xperpnorm;
          double yperp = perp_offset * yperpnorm;
          xout += xperp;
          yout += yperp;
        } else if (line_thickness_strategy == ROUNDED_CAPS) { // random distance _perpendicular_ to unmodified Maurer line, but with rounded caps
          if (line_delta < line_thickness || ((line_length - line_delta) < line_thickness)) {
            xout += ((pContext.random() - 0.5) * line_thickness);
            yout += ((pContext.random() - 0.5) * line_thickness);
          } else {
            double xnorm = xdiff / line_length;
            double ynorm = ydiff / line_length;
            double xperpnorm = -ynorm;
            double yperpnorm = xnorm;
            double perp_offset = (pContext.random() - 0.5) * line_thickness;
            double xperp = perp_offset * xperpnorm;
            double yperp = perp_offset * yperpnorm;
            xout += xperp;
            yout += yperp;
          }
        }
      }
    }

    //
    // FILTERING
    //
    pVarTP.doHide = false;
    boolean cumulative_pass = true;
    for (int findex = 0; findex < filter_count; findex++) {
      MaurerFilter mfilter = (MaurerFilter) filters.get(findex);
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
        } else if (measure == LINE_ANGLE_LINES) {
          val = line_angle;
          sampled_vals = sampled_line_angles;
        } else if (measure == POINT_ANGLE_LINES) {
          val = point_angle;
          sampled_vals = sampled_point_angles;
        } else if (measure == DISTANCE_ALONG_LINE_POINTS) {
          val = line_delta;
          sampled_vals = null; // percentiles calculated directly for DISTANCE_ALONG_LINE_POINTS
        } else if (measure == DISTANCE_FROM_MIDLINE_POINTS) {
          val = abs(line_delta - midlength);
          sampled_vals = null; // percentiles calculated directly for DISTANCE_FROM_MIDLINE_POINTS
        } else if (measure == DISTANCE_FROM_NEAREST_END_POINTS) {
          val = Math.min(line_delta, line_length - line_delta);
          sampled_vals = null;  // percentiles calculated directly for DISTANCE_FROM_NEAREST_END_POINTS
        } else if (measure == Z_MINMAX_POINTS) {
          // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
          // val = abs(zout*2); // min/max zout should be
          val = zout + (line_length / 2); //  range of val should be 0 to line_length;
          sampled_vals = sampled_line_lengths;
        } else if (measure == Z_ABSOLUTE_MINMAX_POINTS) {
          // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
          val = abs(zout) * 2; // range of val should be 0 to line_length
          sampled_vals = sampled_line_lengths;
        } else {
          // default to line length
          val = line_length;
          sampled_vals = sampled_line_lengths;
        }


        if (fmode == BAND_PASS_VALUE || fmode == BAND_STOP_VALUE) {
          low_thresh = mfilter.low_thresh;
          high_thresh = mfilter.high_thresh;
        } else if (fmode == BAND_PASS_PERCENT || fmode == BAND_STOP_PERCENT) {
          if (measure == DISTANCE_ALONG_LINE_POINTS) {
            low_thresh = mfilter.low_thresh * line_length;
            high_thresh = mfilter.high_thresh * line_length;
          } else if (measure == DISTANCE_FROM_MIDLINE_POINTS || measure == DISTANCE_FROM_NEAREST_END_POINTS) {
            // low_thresh and high_thresh for DISTANCE_FROM_MIDLINE_POINTS and DISTANCE_FROM_NEAREST_END_POINTS 
            //      can behave differently when thresholding by value, but act the same when thresholding by percentile
            low_thresh = mfilter.low_thresh * midlength;
            high_thresh = mfilter.high_thresh * midlength;
          } else {
            int low_index, high_index;
            // should probably round here instead of flooring with (int) cast?
            if (mfilter.low_thresh <= 0 || mfilter.low_thresh >= 1) {
              low_index = 0;
            } else {
              low_index = (int) (mfilter.low_thresh * sampled_vals.length);
            }
            // if high_thresh not in (0 -> 1.0) exclusive range, clamp  at 100%
            if (mfilter.high_thresh >= 1 || mfilter.high_thresh <= 0) {
              high_index = (sampled_vals.length - 1);
            } else {
              high_index = (int) (mfilter.high_thresh * sampled_vals.length);
            }
            low_thresh = sampled_vals[low_index];
            high_thresh = sampled_vals[high_index];
          }
        } else { // default to values
          low_thresh = mfilter.low_thresh;
          high_thresh = mfilter.high_thresh;
        }

        boolean inband = (val >= low_thresh && val <= high_thresh);
        boolean current_pass;
        if (fmode == BAND_PASS_VALUE || fmode == BAND_PASS_PERCENT) {
          current_pass = inband;
        } else if (fmode == BAND_STOP_VALUE || fmode == BAND_STOP_PERCENT) {
          // effectively the same as additional NOT operation on this filter
          current_pass = !inband;
        } else {
          // default to bandpass: passing values that are within filter band
          current_pass = inband;
        }
        if (findex == 0) { // no combo operator for first filter
          cumulative_pass = current_pass;
        } else {
          if (op == AND) {
            cumulative_pass = cumulative_pass && current_pass;
          } else if (op == OR) {
            cumulative_pass = cumulative_pass || current_pass;
          } else if (op == XOR) {
            cumulative_pass = (cumulative_pass && !current_pass) || (!cumulative_pass && current_pass);
          } else if (op == ANOTB) {
            cumulative_pass = cumulative_pass && !current_pass;
          } else if (op == BNOTA) {
            cumulative_pass = !cumulative_pass && current_pass;
          } else { // default to AND
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
    } else {
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
      } else if (direct_color_measure == SPEED_AT_ENDPOINT1) {
        // val = speed1;
        val = curve.getSpeed(theta1, end_point1);
        sampled_vals = sampled_speeds;
      } else if (direct_color_measure == CURRENT_THETA) {
        val = theta1 / M_2PI;
        sampled_vals = sampled_thetas;
      } else if (direct_color_measure == LINE_ANGLE_LINES) {
        val = line_angle;
        sampled_vals = sampled_line_angles;
      } else if (direct_color_measure == POINT_ANGLE_LINES) {
        val = point_angle;
        sampled_vals = sampled_point_angles;
      } else if (direct_color_measure == DISTANCE_ALONG_LINE_POINTS) {
        val = line_delta;
        sampled_vals = null;
      } else if (direct_color_measure == DISTANCE_FROM_MIDLINE_POINTS) {
        val = abs(line_delta - midlength);
        sampled_vals = null;
      } else if (direct_color_measure == META_INDEX && meta_mode != OFF) {
        val = current_meta_step;
        sampled_vals = null;
      } else if (direct_color_measure == Z_MINMAX_POINTS) {
        // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
        // val = abs(zout*2); // min/max zout should be
        val = zout + (line_length / 2); //  range of val should be 0 to line_length;
        sampled_vals = sampled_line_lengths;
      } else if (direct_color_measure == Z_ABSOLUTE_MINMAX_POINTS) {
        // min/max zout should be +/- radius of circle ==> +/-(line_length/2)
        val = abs(zout) * 2; // range of val should be 0 to line_length
        sampled_vals = sampled_line_lengths;
      } else if (direct_color_measure == DISTANCE_FROM_NEAREST_END_POINTS) {
        val = Math.min(line_delta, line_length - line_delta);
        sampled_vals = null;  // percentiles calculated directly for DISTANCE_FROM_NEAREST_END_POINTS
      } else { // default to LINE_LENGTH_LINES
        val = line_length;
        sampled_vals = sampled_line_lengths;
      }

      double baseColor = 0;

      double low_value, high_value;
      // ignore percentile option and direct_color_thesholding if using META_INDEX mode??
      if (direct_color_measure == META_INDEX && meta_mode != OFF) {
        low_value = 0;
        high_value = meta_steps;
      } else {
        if (direct_color_thesholding == PERCENT) {
          if (direct_color_measure == DISTANCE_ALONG_LINE_POINTS) {
            low_value = color_low_thresh * line_length;
            high_value = color_high_thresh * line_length;
          } else if (direct_color_measure == DISTANCE_FROM_MIDLINE_POINTS) {
            low_value = color_low_thresh * midlength;
            high_value = color_high_thresh * midlength;
          } else if (direct_color_measure == DISTANCE_FROM_MIDLINE_POINTS || direct_color_measure == DISTANCE_FROM_NEAREST_END_POINTS) {
            // low_thresh and high_thresh for DISTANCE_FROM_MIDLINE_POINTS and DISTANCE_FROM_NEAREST_END_POINTS 
            //      can behave differently when thresholding by value, but act the same when thresholding by percentile
            low_value = color_low_thresh * midlength;
            high_value = color_high_thresh * midlength;
          } else {
            int low_index, high_index;
            if (color_low_thresh < 0 || color_low_thresh >= 1) {
              low_index = 0;
            } else {
              low_index = (int) (color_low_thresh * sample_size);
            }
            if (color_high_thresh >= 1 || color_high_thresh < 0) {
              high_index = sample_size - 1;
            } else {
              high_index = (int) (color_high_thresh * (sample_size - 1));
            }
            low_value = sampled_vals[low_index];
            high_value = sampled_vals[high_index];
          }
        } else {  // default is by value
          low_value = color_low_thresh;
          high_value = color_high_thresh;
        }
        if (low_value > high_value) {
          double temp = low_value;
          low_value = high_value;
          high_value = temp;
        }
      }

      if (val < low_value) {
        baseColor = 0;
      } else if (val >= high_value) {
        baseColor = 255;
      } else {
        baseColor = ((val - low_value) / (high_value - low_value)) * 255;
      }
      if (direct_color_gradient == COLORMAP_CLAMP) {
        pVarTP.rgbColor = false;
        pVarTP.color = baseColor / 255.0;
        if (pVarTP.color < 0) {
          pVarTP.color = 0;
        }
        if (pVarTP.color > 1.0) {
          pVarTP.color = 1.0;
        }
      } else if (direct_color_gradient == COLORMAP_WRAP) {
        pVarTP.rgbColor = false;
        // if val is outside range, wrap it around (cylce) to keep within range
        if (val < low_value) {
          val = high_value - ((low_value - val) % (high_value - low_value));
        } else if (val > high_value) {
          val = low_value + ((val - low_value) % (high_value - low_value));
        }
        baseColor = ((val - low_value) / (high_value - low_value)) * 255;
        pVarTP.color = baseColor / 255.0;
        if (pVarTP.color < 0) {
          pVarTP.color = 0;
        }
        if (pVarTP.color > 1.0) {
          pVarTP.color = 1.0;
        }
      } else if (direct_color_gradient == RED_GREEN) {  //
        pVarTP.rgbColor = true;
        pVarTP.redColor = baseColor;
        pVarTP.greenColor = 255 - baseColor;
        pVarTP.blueColor = 0;
      } else if (direct_color_gradient == RED_BLUE) {
        pVarTP.rgbColor = true;
        pVarTP.redColor = baseColor;
        pVarTP.greenColor = 0;
        pVarTP.blueColor = 255 - baseColor;
      } else if (direct_color_gradient == BLUE_GREEN) {
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
    return (String[]) plist.keySet().toArray(new String[0]);
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
    plist.put(PARAM_IRRATIONALIZE, irrationalize);
    plist.put(PARAM_RENDER_MODE, render_mode);
    plist.put(PARAM_RENDER_SUBMODE, render_submode);
    plist.put(PARAM_TANGENT_SUBMODE, tangent_submode);
    plist.put(PARAM_RENDER_MODIFIER1, render_modifier1);
    plist.put(PARAM_RENDER_MODIFIER2, render_modifier2);
    plist.put(PARAM_RENDER_MODIFIER3, render_modifier3);
    plist.put(PARAM_COSETS_MODE, cosets_mode);

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
    for (int i = 0; i < filter_count; i++) {
      MaurerFilter mfil = (MaurerFilter) filters.get(i);
      String filter_base = PARAM_FILTER_PREFIX + (i + 1) + "_";
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

    plist.put(PARAM_E, e_param);
    plist.put(PARAM_F, f_param);
    plist.put(PARAM_G, g_param);
    plist.put(PARAM_H, h_param);

    return plist;
  }

  ;

  @Override
  public void setParameter(String pName, double pValue) {
    if (DEBUG_DYNAMIC_PARAMETERS) {
      System.out.println("setParameter called: " + pName + ", " + pValue);
    }

    if (PARAM_A.equalsIgnoreCase(pName))
      a_param = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b_param = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c_param = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d_param = pValue;
    else if (PARAM_E.equalsIgnoreCase(pName))
      e_param = pValue;
    else if (PARAM_F.equalsIgnoreCase(pName))
      f_param = pValue;
    else if (PARAM_G.equalsIgnoreCase(pName))
      g_param = pValue;
    else if (PARAM_H.equalsIgnoreCase(pName))
      h_param = pValue;
    else if (PARAM_RENDER_MODE.equalsIgnoreCase(pName))
      render_mode = (int) pValue;
    else if (PARAM_RENDER_SUBMODE.equalsIgnoreCase(pName))
      render_submode = (int) pValue;
    else if (PARAM_TANGENT_SUBMODE.equalsIgnoreCase(pName))
      tangent_submode = (int) pValue;
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
      curve_mode = (int) pValue;

    else if (PARAM_COSETS_MODE.equalsIgnoreCase(pName)) {
      cosets_mode = (int) pValue;
    } else if (PARAM_IRRATIONALIZE.equalsIgnoreCase(pName)) {
      irrationalize = pValue;
    } else if (PARAM_DIRECT_COLOR_MEASURE.equalsIgnoreCase(pName)) {
      direct_color_measure = (int) pValue;
    } else if (PARAM_DIRECT_COLOR_GRADIENT.equalsIgnoreCase(pName)) {
      direct_color_gradient = (int) pValue;
    } else if (PARAM_DIRECT_COLOR_THRESHOLDING.equalsIgnoreCase(pName)) {
      direct_color_thesholding = (int) pValue;
    } else if (PARAM_COLOR_LOW_THRESH.equalsIgnoreCase(pName)) {
      color_low_thresh = pValue;
    } else if (PARAM_COLOR_HIGH_THRESH.equalsIgnoreCase(pName)) {
      color_high_thresh = pValue;
    } else if (PARAM_FILTER_COUNT.equalsIgnoreCase(pName)) {
      filter_count = (int) pValue;
      if (filter_count > filters.size()) {
        while (filter_count > filters.size()) {
          MaurerFilter mfil = new MaurerFilter();
          filters.add(mfil);
          if (DEBUG_DYNAMIC_PARAMETERS) {
            System.out.println("set, count changed: " + filters.size() + ", low: " + mfil.low_thresh);
          }
        }
      }
    } else if (pName.startsWith(PARAM_FILTER_PREFIX)) {
      String temp1 = pName.replaceFirst(PARAM_FILTER_PREFIX, "");
      String fnum = temp1.substring(0, temp1.indexOf("_"));
      String suffix = pName.substring(pName.indexOf('_') + 1);
      int findex = Integer.parseInt(fnum) - 1;
      MaurerFilter mfil = (MaurerFilter) filters.get(findex);
      if (PARAM_FILTER_OPERATOR_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.operator = (int) pValue;
      } else if (PARAM_FILTER_MODE_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.mode = (int) pValue;
      } else if (PARAM_FILTER_MEASURE_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.measure = (int) pValue;
      } else if (PARAM_FILTER_LOW_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.low_thresh = pValue;
        if (findex == 1) {
          if (DEBUG_DYNAMIC_PARAMETERS) {
            System.out.println("set, low changed: " + mfil.low_thresh);
          }
        }
      } else if (PARAM_FILTER_HIGH_SUFFIX.equalsIgnoreCase(suffix)) {
        mfil.high_thresh = pValue;
      } else {
        throw new IllegalArgumentException(pName);
      }

    } else if (PARAM_RANDOMIZE.equalsIgnoreCase(pName)) {
      randomize = (pValue >= 1);
    } else if (PARAM_META_MODE.equalsIgnoreCase(pName)) {
      meta_mode = (int) pValue;
    } else if (PARAM_META_MIN_VALUE.equalsIgnoreCase(pName)) {
      meta_min_value = pValue;
    } else if (PARAM_META_MAX_VALUE.equalsIgnoreCase(pName)) {
      meta_max_value = pValue;
    } else if (PARAM_META_STEP_VALUE.equalsIgnoreCase(pName)) {
      this.meta_step_value = pValue;
    } else if (PARAM_SHOW_POINTS.equalsIgnoreCase(pName))
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
    } else {
      System.out.println("can't find param: name = " + pName + ", value = " + pValue);
      throw new IllegalArgumentException(pName);
    }
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
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String paramName) {
    if (paramName == PARAM_FILTER_COUNT) {
      return true;
    } else {
      return false;
    }
  }
}
