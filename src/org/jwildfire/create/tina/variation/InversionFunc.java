package org.jwildfire.create.tina.variation;

import static java.lang.Math.abs;
import java.math.BigInteger;
import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.atan2;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.sqr;
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
 * InversionFunc, a variation for inversion geometry transformations
 * Supports standard circle inversion
 * Includes x0, y0, z0 params for specifying origin translationa specific radius parameter
 *     (efffectively replaces pre- and post- transform coefficients 
 *      so don't need to keep them as mirrors of each other)
 *  also includes "draw_circles" param
 *      if 0 < draw_circles < 1, then that fraction of incoming points is used to 
 *      draw circle of inversion rather than doing the actual inversion
 *  In addition to standard circle inversion, can also be used for p-circle inversion as 
 *      described by Ramirez et al in "Generating Fractal Patterns by Using P-Circle Inversion" (2015)
 */
public class InversionFunc extends VariationFunc {
  // public class InversionFunc extends VariationFunc implements Guides {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_XORIGIN = "xorigin";
  public static final String PARAM_YORIGIN = "yorigin";
  public static final String PARAM_ZORIGIN = "zorigin";
  public static final String PARAM_ROTATION = "rotation (pi * n radians)";
  public static final String PARAM_SCALE= "scale";
  public static final String PARAM_SHAPE = "shape";
  public static final String PARAM_A = "a";
  public static final String PARAM_B = "b";
  public static final String PARAM_C = "c";
  public static final String PARAM_D = "d";
  public static final String PARAM_E = "e";
  public static final String PARAM_F = "f";
  
  public static final String PARAM_ZMODE = "zmode";
  public static final String PARAM_INVERSION_MODE = "imode";
  public static final String PARAM_HIDE_UNINVERTED = "hide_uninverted";
  // public static final String PARAM_RING_MIN = "ring_min";
  // public static final String PARAM_RING_MAX = "ring_max";
  public static final String PARAM_P= "p";
  public static final String PARAM_P2 = "p2";
  public static final String PARAM_DRAW_CIRCLE = "draw_circle";
  public static final String PARAM_GUIDES_ENABLED = "guides_enabled";
  public static final String PARAM_PASSTHROUGH = "passthrough";
  
  private static final String[] paramNames = { 
    PARAM_SCALE, PARAM_ROTATION, 
    PARAM_SHAPE, 
    PARAM_ZMODE, 
    PARAM_INVERSION_MODE, PARAM_HIDE_UNINVERTED, 
    // PARAM_RING_MIN, PARAM_RING_MAX, 
    PARAM_P, PARAM_P2, PARAM_DRAW_CIRCLE, PARAM_PASSTHROUGH, PARAM_GUIDES_ENABLED, 
    PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, 
    PARAM_XORIGIN, PARAM_YORIGIN, PARAM_ZORIGIN, 
  };
  
  public static int STANDARD = 0;
  public static int EXTERNAL_INVERSION_ONLY = 1;
  public static int INTERNAL_INVERSION_ONLY = 2;
  // outer inversion is like external inversion, except 
  //    leaves unchanged any  "external" regions that are distance < radius (inner hole)
  public static int EXTERNAL_RING_INVERSION_ONLY = 3;
  public static int INTERNAL_RING_INVERSION_ONLY = 4;
  
  public static int CIRCLE = 0;
  public static int ELLIPSE = 1;
  public static int HYPERBOLA = 2;
  public static int REGULAR_POLYGON = 3;
  public static int RHODONEA = 4;
  public static int SUPERSHAPE = 5;
//  public static int WELDED_CIRCLES = 6;
  
  ParametricShape shape;
  boolean draw_guides = false;
  double rotation_pi_fraction = 0;
  double shape_rotation_radians;
  
  public void setDrawGuides(boolean draw) {
    draw_guides = draw;
  }
  
  public boolean getDrawGuides() { return draw_guides; }
  
  /**
   *  only using z coordinate for specific modes
   */
  
  class PolarPoint2D {
    public double x;
    public double y;
    public double r;
    // public double t;
  }

 /* class DoublePoint3D extends DoublePoint2D {
    public double z;
  }
  */
  
  // abstract class ParametricShape {
  abstract class ParametricShape {
    public PolarPoint2D ptemp = new PolarPoint2D();
    public abstract void getCurvePoint(double t, PolarPoint2D point);
    public abstract double getPeriod();
    
    /** simple shapes will not have more than one intersection along a given ray out from shape center */
    public boolean simpleShape() { return true; }

    public PolarPoint2D getCurvePoint(double t) {
      PolarPoint2D outpoint = new PolarPoint2D();
      getCurvePoint(t, outpoint);
      return outpoint;
    }
    
    /*
    public void getFirstDerivative(double t, PolarPoint2D result) {
      result.x = Double.NaN;
      result.y = Double.NaN;
    }
    
    public double getSpeed(double t, PolarPoint2D result) {
      this.getFirstDerivative(t, result);
      if (Double.isNaN(result.x) || Double.isNaN(result.y)) {
        return 0;
      }
      double speed = sqrt(result.x*result.x + result.y*result.y);
      return speed;
    }
    */
    
    // find intersection nearest to point pIn1 of line (defined by point pIn1 and pIn2) and shape 
    //  
    //    shapepoint p to "center" of shape
    //    (where "centroid" is defined by the shape object, 
    //       and is meant to usually be the centroid of the shape)
    /*
    public double getClosestIntersect(PolarPoint2D pin1, PolarPoint2D pin2, PolarPoint2D pout) {
      return 0;
    }
    */
    
    // int callCount = 0;
    // find intersection nearest to point pIn of line from point pIn to "center" of shape
    //    (where "centroid" is defined by the shape object, 
    //       and is meant to usually be the centroid of the object)
    // public void getClosestRadialIntersect(PolarPoint2D pin, PolarPoint2D pout) {    
    public void getMaxCurvePoint(double tin, PolarPoint2D pout) {
      // if doesn't intersect self, will only be one point of intersection
      if (simpleShape()) {
        getCurvePoint(tin, pout);
        return;
      }
      // callCount++;
      // if (callCount % 200000 == 0) {
      //  callCount = 0;
      // }
      ptemp.r = 0;
      ptemp.x = 0;
      ptemp.y = 0;
      pout.r = -0.0001;
      pout.x = 0;
      pout.y = 0;
      // double xin = pin.x;
      // double yin = pin.y;
      // double tin =  atan2(yin, xin);  // atan2 range is [-Pi..+Pi]
      
      // don't need this -- shape rotation is handled in getCurvePoint() call
      // double t = tin - shape_rotation_radians;
      int theta_count = (int)(getPeriod() / M_2PI);
      //int theta_count = (int)((getPeriod() / M_PI) + EPSILON);  // adding epsilon to avoid any division accuracy issues
      // int theta_count = 20;
      
      for (int i=0; i<theta_count; i++) {
        double theta = tin + (i * M_PI);
        getCurvePoint(theta, ptemp);
        if (abs(ptemp.r) > pout.r) {
          pout.x = ptemp.x;
          pout.y = ptemp.y;
          pout.r = abs(ptemp.r);
        }
        // double theta = tin + (i * M_PI);
        //if (i%2 == 0) { // even ==> multiples of 2pi of tin ==> only consider those with positive radius
          //getCurvePoint(theta, ptemp);
          // if ()
       //        }
       //        else { // odd ==> 180 degree rotation ==> only consider those with negative radius
        //}
      }
      
      // determine all theta to test based on period and tin
      //   (and for each theta also want to check t-PI since can have negative radius)
      
      // for all t, find rc (curve radius), and determine max rc
      // getCurvePoint(tin, pout);
      
      // convert back from polar (t,rc) to output point of curve intersection (x', y')

    }
  }
  
  // still need a parameter for shape rotation about (x0, y0) ?
  
  class Circle extends ParametricShape {

    @Override
    public double getPeriod() { return M_2PI; }
    
    @Override
    public void getCurvePoint(double tin, PolarPoint2D point) {
      // for consistency with other shapes, though rotation shouldn't matter for the circle...
      double t = tin - shape_rotation_radians;  
      double r = scale;
      point.r = r;
      point.x = r * cos(tin);
      point.y = r * sin(tin);
    }
  }
  
  /**
   *  uses parameter a as number of sides for polygon
   * 
   */
  class RegularPolygon extends ParametricShape {
    
    @Override 
    public double getPeriod() { return M_2PI; }
    
    @Override
    // parametric polygon equation derived from: 
    //    http://math.stackexchange.com/questions/41940/is-there-an-equation-to-describe-regular-polygons
    //    http://www.geogebra.org/m/157867
    public void getCurvePoint(double tin, PolarPoint2D point) {
      double theta = abs((tin - shape_rotation_radians) % M_2PI);
      // double t = theta - shape_rotation_radians;
      double n = Math.floor(a);
      // double r = cos(M_PI/n) / cos(t%(M_2PI/n) - M_PI/n);
      double r = cos(M_PI/n) / cos(theta%(M_2PI/n) - M_PI/n);
      r *= scale;
      point.r = r;
      point.x = r * cos(tin);
      point.y = r * sin(tin);
    }
  }
  
  /**
   * uses param a, b as standard a, b params for an ellipse
   */
  class Ellipse extends ParametricShape {
    
    @Override 
    public double getPeriod() { return M_2PI; }
    
    @Override
    public void getCurvePoint(double tin, PolarPoint2D point) {
      // t is input angle modified by shape rotation
      double t = tin - shape_rotation_radians;
      double r = (a * b) / sqrt(sqr(b*cos(t)) + sqr(a*sin(t)));
      r *= scale;
      point.r = r;
      point.x = r * cos(tin);
      point.y = r * sin(tin);
    }
  }
  
  /*
   * uses param a, b as standard a, b params for a hyperbola
   */
  class Hyperbola extends ParametricShape {
    
    @Override 
    public double getPeriod() { return M_2PI; }
    
    @Override
    public void getCurvePoint(double tin, PolarPoint2D point) {
      double t = tin - shape_rotation_radians;
      double r2 = (a * a * b * b) / ((b * b * cos(t) * cos(t)) - (a * a * sin(t) * sin(t)));
      double r = sqrt(r2);
      r *= scale;
      point.r = r;
      point.x = r * cos(tin);
      point.y = r * sin(tin);
    }
  }
  
  /**
   * 
   */
  class SuperShape extends ParametricShape {
    @Override
    public double getPeriod() {
      return M_2PI;
    }

    @Override
    public void getCurvePoint(double tin, PolarPoint2D point) {
      double t = tin - shape_rotation_radians;

      // mapping params (a,b,c,d,e,f) to 
      //  naming convention of supershape (a,b,c,n1,n2,n3)
      // a = a param
      // b = b param
      double m = c;
      double n1 = d;
      double n2 = e;
      double n3 = f;
      
      double r = pow(
              (pow( fabs( (cos(m * t / 4))/a), n2) +
                      pow( fabs( (sin(m * t / 4))/b), n3)),
              (-1/n1));
      r *= scale;
      point.r = r;
      point.x = r * cos(tin);
      point.y = r * sin(tin);
    }
  }
  
  class Rhodonea extends ParametricShape {
    double period;
    // double kn, kd;
    public Rhodonea() {
      // kn = a;
      // kd = b;
      calcPeriod();
    }
    
    @Override
    public boolean simpleShape() { return false; }
    
    @Override 
    public double getPeriod() {
      return period;
    }
    @Override
    public void getCurvePoint(double tin, PolarPoint2D point) {
      double t = tin - shape_rotation_radians;
      double k = a/b;
      double r = cos(k * t) + c;
      r *= scale;
      point.r = r;
      point.x = r * cos(tin);
      point.y = r * sin(tin);
    }
    
    // sets Rhodonea.period
    public void calcPeriod() {
      double k = a/b;
      if ((k % 1) == 0) { // k is integer
        if ((k % 2) == 0) { // k is even integer, will have 2k petals and close in 2*Pi
          period = M_2PI; // (2PI)
        }
        else { // k is odd integer, will have k petals (or 2k if c!= 0)
          if (c != 0) {
            period = M_2PI;  // 2Pi
          } 
          else {
            period = M_PI;  // 1Pi
          } 
        }
      }
      else if ((a % 1 == 0) && (b % 1 == 0)) {
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
          period = kd * M_2PI; //
        }
        else {
          period = kd * M_PI;
        }
      }
      else {
        // a/b is irrational, just set to 2*pi for now
        period = M_2PI;
      }
    }
    
  }
  
  // public boolean DESIGN_GUIDE_MODE = false;
  double scale = 1;
  double x0 = 0;
  double y0 = 0;
  double z0 = 0;
  double a = 1;
  double b = 1;
  double c = 0;
  double d = 0;
  double e = 0; 
  double f = 0;
  int shape_mode = CIRCLE;
  // if pass_through != 0, then allow (pass_through) fraction of points to pass through unaltered
  double passthrough = 0;
  
  double p = 2;
  double p2 = 2;
  // double ring_min_ratio = 0;
  // double ring_max_ratio = 1;
  // double ring_min;
  // double ring_max;
  double draw_shape = 0;
  boolean guides_enabled = true;
  int inversion_mode = STANDARD;
  boolean hide_uninverted = false;
  boolean zmode = false;

  PolarPoint2D curve_point = new PolarPoint2D();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double xin = pAffineTP.x;
    double yin = pAffineTP.y;
    double zin = pAffineTP.z;


    double iscale;
    if (draw_guides && guides_enabled) { 
      double rnd = pContext.random();
        double theta = rnd * shape.getPeriod();
        if (zmode) {
          double split = pContext.random() * 3.1;
          if (split < 1) {
            pVarTP.x += scale * cos(theta);
            pVarTP.y += scale * sin(theta);
           // pVarTP.z = z0;
           // pVarTP.z += zin;
          }
          else if (split < 2) {
            pVarTP.x += scale * cos(theta);
            // pVarTP.y = y0;
            // pVarTP.y += yin;
            pVarTP.z += scale * sin(theta);
          }
          else if (split < 3) {
            // pVarTP.x = x0;
            // pVarTP.x += xin;
            pVarTP.y += scale * sin(theta);
            pVarTP.z += scale * cos(theta);
          }
          else {
            // pVarTP.x += x0;
            // pVarTP.x += xin;
            // pVarTP.y += yin;
            // pVarTP.z += zin;
          }
        }
        else {
          double split = pContext.random() * 1.1;
          if (split < 1) {
            shape.getCurvePoint(theta, curve_point);
            pVarTP.x += curve_point.x;
            pVarTP.y += curve_point.y;
            //pVarTP.z += curve_point.z;
            // pVarTP.z += z0;
          }
          else {  // draw point at center of shape
            // pVarTP.x += x0;
            // pVarTP.y += y0;
            // pVarTP.z += z0;
          }
        }
      return;
    }
    
    if (draw_shape > 0) {
      double rnd = pContext.random();
      if (rnd < draw_shape) {
        double theta = pContext.random() * shape.getPeriod();
        shape.getCurvePoint(theta, curve_point);
        pVarTP.x += curve_point.x;
        pVarTP.y += curve_point.y;
        return;
      }
    }
    if (passthrough > 0) {
      double rnd = pContext.random(); 
      if (rnd < passthrough) {
        pVarTP.x += xin;
        pVarTP.y += yin;
        pVarTP.z += zin;
        return;
      }
    }
    // to do generalized inversion of input point P, 
    // need two other points:
    // O, the origin of inversion
    // S, the intersection of the line OP and the surface of the shape
    // then output point P' = O + (d1(O,B)^2/d2(O,P)^2) * (P - O)
    // where d1 and d2 are distance metric functions 
    double tin = atan2(yin, xin);
    double rin = sqrt(xin*xin + yin*yin + zin*zin);
    boolean do_inversion;
    // shape.getCurvePoint(tin, curve_point);
    shape.getMaxCurvePoint(tin, curve_point);
    // double rcurve = sqrt(curve_point.x * curve_point.x + curve_point.y * curve_point.y);
    double rcurve = curve_point.r;
    double xcurve = curve_point.x;
    double ycurve = curve_point.y;
    if (inversion_mode == EXTERNAL_INVERSION_ONLY) {
      // only do inversion if input point is outside of circle
      do_inversion = rin > rcurve;
    }
    else if (inversion_mode == INTERNAL_INVERSION_ONLY) {
      // only do inversion if input point is inside of circle
      do_inversion = rin < rcurve;
    }
    else { // default to STANDARD mode ==> always do inversion
      do_inversion = true;
    }
    
    if (do_inversion) {
      double num_scale = rcurve * rcurve;
      double denom_scale;
      if (p2 == 2) {
        num_scale = sqr(rcurve);
      }
      else {
        // num_scale = pow(rcurve, p2);
        num_scale = pow( (pow(abs(xcurve),p2) + pow(abs(ycurve),p2)), p2);
      }
      if (p == 2) {
        // iscale = (rcurve * rcurve) / (sqr(xin) + sqr(yin));
        denom_scale = sqr(xin) + sqr(yin);
      }
      else {
        // iscale = (rcurve * rcurve)/ pow( (pow(abs(xin),p) + pow(abs(yin),p)), 2.0/p);
        denom_scale = pow( (pow(abs(xin),p) + pow(abs(yin),p)), 2.0/p);
      }
      iscale = num_scale/denom_scale;
      
      double xout = iscale * xin;
      double yout = iscale * yin;
      double zout = iscale * zin;
      pVarTP.x += pAmount * xout;
      pVarTP.y += pAmount * yout;
      pVarTP.z += pAmount * zout;
      pVarTP.doHide = false;
    }
    else { // if didn't do inversion, check to see if should hide
      pVarTP.x += xin;
      pVarTP.y += yin;
      pVarTP.z += zin;
      pVarTP.doHide = hide_uninverted;
    }
  }
  
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    shape_rotation_radians = M_PI * rotation_pi_fraction;
    // ring_min = ring_min_ratio * r;
    // ring_max = ring_max_ratio * r;
    if (shape_mode == CIRCLE) {
      shape = new Circle();
    }
    else if (shape_mode == ELLIPSE) {
      shape = new Ellipse();
    }
    else if (shape_mode == HYPERBOLA) {
      shape = new Hyperbola();
    }
    else if (shape_mode == REGULAR_POLYGON) {
      shape = new RegularPolygon();
    }
    else if (shape_mode == RHODONEA) {
      shape = new Rhodonea();
    }
    else if (shape_mode == SUPERSHAPE) {
      shape = new SuperShape();
    }
  }

  @Override
  public String getName() {
    return "inversion";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { 
      scale, 
      rotation_pi_fraction, 
      shape_mode, 
      zmode ? 1 : 0, 
      inversion_mode, hide_uninverted ? 1 : 0, 
      // ring_min_ratio, ring_max_ratio, 
      p, p2, draw_shape, passthrough, guides_enabled ? 1 : 0, 
      a, b, c, d, e, f, 
      x0, y0, z0, 
    };
    
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) {
      scale = pValue;
    }
    else if (PARAM_ROTATION.equalsIgnoreCase(pName)) {
      rotation_pi_fraction = pValue;
    }
    else if (PARAM_SHAPE.equalsIgnoreCase(pName)) {
      shape_mode = (int)floor(pValue);
    }
        else if (PARAM_ZMODE.equalsIgnoreCase(pName)) {
      zmode = (pValue == 1) ? true : false;
    }
   /*
    else if (PARAM_RING_MIN.equalsIgnoreCase(pName)) {
      ring_min_ratio = pValue;
    }
    else if (PARAM_RING_MAX.equalsIgnoreCase(pName)) {
      ring_max_ratio = pValue;
    }
    */
    else if (PARAM_P.equalsIgnoreCase(pName)) {
      p = pValue;
    }
    else if (PARAM_P2.equalsIgnoreCase(pName)) {
      p2 = pValue;
    }
    else if (PARAM_INVERSION_MODE.equalsIgnoreCase(pName)) {
      inversion_mode = (int)pValue;
    }
    else if (PARAM_HIDE_UNINVERTED.equalsIgnoreCase(pName)) {
      hide_uninverted = (pValue == 1) ? true : false;
    }
    else if (PARAM_DRAW_CIRCLE.equalsIgnoreCase(pName)) {
      draw_shape = pValue;
    }
    else if (PARAM_PASSTHROUGH.equalsIgnoreCase(pName)) {
      passthrough = pValue;
    }
    else if (PARAM_GUIDES_ENABLED.equalsIgnoreCase(pName)) {
      guides_enabled = (pValue == 1) ? true : false;
    }
    else if (PARAM_A.equalsIgnoreCase(pName)) {
      a = pValue;
    }
    else if (PARAM_B.equalsIgnoreCase(pName)) {
      b = pValue;
    }
    else if (PARAM_C.equalsIgnoreCase(pName)) {
      c = pValue;
    }
    else if (PARAM_D.equalsIgnoreCase(pName)) {
      d = pValue;
    }
    else if (PARAM_E.equalsIgnoreCase(pName)) {
      e = pValue;
    }
    else if (PARAM_F.equalsIgnoreCase(pName)) {
      f = pValue;
    }
    else if (PARAM_XORIGIN.equalsIgnoreCase(pName)) {
      x0 = pValue;
    }
    else if (PARAM_YORIGIN.equalsIgnoreCase(pName)) {
      y0 = pValue;
    }
    else if (PARAM_ZORIGIN.equalsIgnoreCase(pName)) {
      z0 = pValue;
    }
    else
      throw new IllegalArgumentException(pName);
  }

}
