package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Arrays;
import java.util.Random;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * @author Jesus Sosa
 * @date October 17, 2017
 * https://introcs.cs.princeton.edu/java/23recursion/BrownianIsland.java.html
 * brownian_js
 * <p>
 * Variation params:
 * Level:  1,2,3,4,5
 * variation: length of path
 * Line_thickness : 1..100
 * show lines  0=hide lines 1=show lines
 * show_points 0: hiden points 1=show points
 * point_thickness
 */

public class BrownianFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_LEVEL = "level";
  private static final String PARAM_VARIATION = "variation";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_LINE_THICKNESS = "line_thickness";
  private static final String PARAM_SHOW_LINES = "show_lines";
  private static final String PARAM_SHOW_POINTS = "show_points";
  private static final String PARAM_POINT_THICKNESS = "point_thickness";

  private static final String[] paramNames = {PARAM_LEVEL, PARAM_VARIATION, PARAM_SEED,
          PARAM_LINE_THICKNESS, PARAM_SHOW_LINES, PARAM_SHOW_POINTS,
          PARAM_POINT_THICKNESS};

  //  private  DynamicArray2D xyPoints = new DynamicArray2D(1000); 

  private int level = 10;
  private double variation = 3;
  private int seed_param = (int) (1000 * Math.random());
  private double line_thickness_param = 0.5;
  private double line_thickness;

  private double show_lines_param = 1;
  private double show_points_param = 0;
  private double line_fraction, point_fraction;
  private double line_threshold, point_threshold;

  private double point_thickness_param = 3.0;
  private double point_thickness;

  Draw2D canvas;

  static class Point2D {
    public int code;
    public double x;
    public double y;

    public Point2D(int code, double x, double y) {
      this.code = code;
      this.x = x;
      this.y = y;
    }
  }

  static class DoublePoint2D {
    public double x;
    public double y;
  }

  static class DynamicArray2D {
    // The storage for the elements. 
    // The capacity is the length of this array.
    private Point2D[] data;

    // The number of elements (logical size).
    // It must be smaller than the capacity.
    private int size;

    // Constructs an empty DynamicArray
    public DynamicArray2D() {
      data = new Point2D[16];
      size = 0;
    }

    // Constructs an empty DynamicArray with the
    // specified initial capacity.
    public DynamicArray2D(int capacity) {
      if (capacity < 16)
        capacity = 16;
      data = new Point2D[capacity];
      size = 0;
    }

    // Increases the capacity, if necessary, to ensure that
    // it can hold at least the number of elements
    // specified by the minimum capacity argument.
    public void ensureCapacity(int minCapacity) {
      int oldCapacity = data.length;
      if (minCapacity > oldCapacity) {
        int newCapacity = (oldCapacity * 2);
        if (newCapacity < minCapacity)
          newCapacity = minCapacity;
        data = (Point2D[]) Arrays.copyOf(data, newCapacity);
      }
    }

    // Returns the logical size
    public int size() {
      return size;
    }

    public boolean isEmpty() {
      return size == 0;
    }

    // Checks if the given index is in range.
    private void rangeCheck(int index) {
      if (index >= size || index < 0)
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    // Returns the element at the specified position.
    public Point2D get(int index) {
      rangeCheck(index);
      return data[index];
    }

    // Appends the specified element to the end.
    public boolean add(Point2D element) {
      ensureCapacity(size + 1);
      data[size++] = element;
      return true;
    }

    // Removes all of the elements.
    public void clear() {
      size = 0;
    }

    // Replaces the element at the specified position
    public Point2D set(int index, Point2D element) {
      rangeCheck(index);
      Point2D oldValue = data[index];
      data[index] = element;
      return oldValue;
    }

    public int capacity() {
      return data.length;
    }
  }

  static class Draw2D {

    private DynamicArray2D xyPoints = new DynamicArray2D(1000);
    private int idx;
    private DoublePoint2D old_point = new DoublePoint2D();
    private double seed;
    private int level;
    private double variation;

    Random random;

    public void setLevel(int level) {
      this.level = level;
    }

    public void setVariation(double variation) {
      this.variation = variation;
    }

    public void setSeed(double seed_param) {
      this.seed = seed_param;
      if (seed == 0)
        random = new Random(System.currentTimeMillis());
      else
        random = new Random((long) seed_param);
    }

    public Draw2D() {
      idx = 0;

    }

    public void moveTo(double x, double y) {
      Point2D p = new Point2D(0, x, y);

      p.code = 0;
      p.x = x;
      p.y = y;
      xyPoints.add(p);
      //	 System.out.println("<c,x,y : > < " + p.code +" , " + p.x +" , " + p.y +">");
    }

    public void lineTo(double x, double y) {
      Point2D p = new Point2D(0, x, y);

      p.code = 1;
      p.x = x;
      p.y = y;
      xyPoints.add(p);
      //	 System.out.println("<c,x,y : > < " + p.code +" , " + p.x +" , " + p.y +">");
    }

    public void getPoint(Point2D result) {
      result.code = xyPoints.get(idx).code;
      result.x = xyPoints.get(idx).x;
      result.y = xyPoints.get(idx).y;
      idx++;
      if (idx >= xyPoints.size())
        idx = 0;
    }

    public boolean getLine(DoublePoint2D p1, DoublePoint2D p2) {
      Point2D p = new Point2D(0, 0.0, 0.0);

      getPoint(p);
      old_point.x = p.x;
      old_point.y = p.y;

      p1.x = old_point.x;
      p1.y = old_point.y;
      getPoint(p);
      if (p.code == 1) {
        old_point.x = p.x;
        old_point.y = p.y;
        p2.x = p.x;
        p2.y = p.y;
        return true;
      } else
        return false;
    }

    public void clearPoints() {
      xyPoints.clear();
      idx = 0;
    }

    // Random random=new Random(System.currentTimeMillis());
    // Random random=new Random((long) seed_param);

    /**
     * Returns a random real number uniformly in [0, 1).
     *
     * @return a random real number uniformly in [0, 1)
     */
    public double uniform() {

      return random.nextDouble();
    }

    /**
     * Returns a random real number uniformly in [a, b).
     *
     * @param a the left endpoint
     * @param b the right endpoint
     * @return a random real number uniformly in [a, b)
     * @throws IllegalArgumentException unless {@code a < b}
     */
    public double uniform(double a, double b) {
      if (!(a < b)) {
        throw new IllegalArgumentException("invalid range: [" + a + ", " + b + ")");
      }
      return a + uniform() * (b - a);
    }

    public double gaussian() {
      // use the polar form of the Box-Muller transform
      double r, x, y;
      do {
        x = uniform(-1.0, 1.0);
        y = uniform(-1.0, 1.0);
        r = x * x + y * y;
      }
      while (r >= 1 || r == 0);
      return x * MathLib.sqrt(-2 * MathLib.log(r) / r);

      // Remark:  y * Math.sqrt(-2 * Math.log(r) / r)
      // is an independent random gaussian
    }

    /**
     * Returns a random real number from a Gaussian distribution with mean &mu;
     * and standard deviation &sigma;.
     *
     * @param mu    the mean
     * @param sigma the standard deviation
     * @return a real number distributed according to the Gaussian distribution
     * with mean {@code mu} and standard deviation {@code sigma}
     */
    public double gaussian(double mu, double sigma) {
      return mu + sigma * gaussian();
    }

    public void midpoint(double x0, double y0, double x1, double y1, double var, int n) {
      if (n == 0) {
        moveTo(x0, y0);
        lineTo(x1, y1);
        return;
      }
      double xmid = 0.5 * (x0 + x1) + gaussian(0, MathLib.sqrt(var));
      double ymid = 0.5 * (y0 + y1) + gaussian(0, MathLib.sqrt(var));

      midpoint(x0, y0, xmid, ymid, var / 2.7, n - 1); // 3 seems to be a good value
      midpoint(xmid, ymid, x1, y1, var / 2.7, n - 1);
    }

    public void draw() {
      midpoint(0, 0, 0, 0, variation / MathLib.sqrt(2), level);
    }

  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    line_thickness = line_thickness_param / 100;

    double show_sum = show_lines_param + show_points_param;

    line_fraction = show_lines_param / show_sum;
    point_fraction = show_points_param / show_sum;

    line_threshold = line_fraction;
    point_threshold = line_fraction + point_fraction;
    //   point_half_threshold = line_fraction + (point_fraction/2);

    line_thickness = line_thickness_param / 100;
    point_thickness = point_thickness_param / 100;

    //	  canvas=new Draw2D();
    //      canvas.draw();
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
    if (line_thickness != 0) {
      xoffset += ((pContext.random() - 0.5) * line_thickness);
      yoffset += ((pContext.random() - 0.5) * line_thickness);
    }
    xout = x1 + xoffset;
    yout = y1 + yoffset;
    DoublePoint2D value = new DoublePoint2D();
    value.x = xout;
    value.y = yout;
    return value;
  }

  public DoublePoint2D pDot(FlameTransformationContext pContext, double x1, double y1) {
    double xout = 0, yout = 0;
    double xoffset = 0, yoffset = 0;
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
    // if (rnd <= point_half_threshold) {
    xout = x1 + xoffset;
    yout = y1 + yoffset;
    // }
    DoublePoint2D value = new DoublePoint2D();
    value.x = xout;
    value.y = yout;
    return value;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    DoublePoint2D p1 = new DoublePoint2D();
    DoublePoint2D p2 = new DoublePoint2D();

    DoublePoint2D out = new DoublePoint2D();

    if (canvas.getLine(p1, p2)) {
      double rnd = pContext.random();

      if (rnd < line_threshold)
        out = pLine(pContext, p1.x, p1.y, p2.x, p2.y);
      else if (rnd <= point_threshold)
        out = pDot(pContext, p1.x, p1.y);
    }
    pVarTP.x += pAmount * out.x;
    pVarTP.y += pAmount * out.y;
    //    pVarTP.color=Math.random();
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
    return new Object[]{level, variation, seed_param, line_thickness_param, show_lines_param, show_points_param, point_thickness_param};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LEVEL.equalsIgnoreCase(pName)) {
      level = limitIntVal(Tools.FTOI(pValue), 1, 15);
      canvas = new Draw2D();
      canvas.setSeed(seed_param);
      canvas.setLevel(level);
      canvas.setVariation(variation);
      canvas.draw();
    } else if (PARAM_VARIATION.equalsIgnoreCase(pName))
      variation = pValue;
    else if (PARAM_LINE_THICKNESS.equalsIgnoreCase(pName))
      line_thickness_param = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName)) {
      seed_param = (int) pValue;
      canvas = new Draw2D();
      canvas.setSeed(seed_param);
      canvas.setLevel(level);
      canvas.setVariation(variation);
      canvas.draw();
    } else if (PARAM_SHOW_LINES.equalsIgnoreCase(pName))
      show_lines_param = pValue;
    else if (PARAM_SHOW_POINTS.equalsIgnoreCase(pName))
      show_points_param = pValue;
    else if (PARAM_POINT_THICKNESS.equalsIgnoreCase(pName))
      point_thickness_param = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "brownian_js";
  }

}
