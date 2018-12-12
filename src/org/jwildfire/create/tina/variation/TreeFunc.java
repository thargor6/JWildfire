package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Arrays;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * @author Jesus Sosa
 * @date October 17, 2017
 * Adapted from the algoritm from:
 * http://introcs.cs.princeton.edu/java/23recursion/Tree.java.html
 * Tree_JS Variation
 * Level: 1,2,3,4,5
 * Line_thickness : 1..200
 * bend_angle: 0..180
 * branch_angle: 0..90
 * branch_ratio: 0..1
 */

public class TreeFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_LEVEL = "level";

  private static final String PARAM_SHOW_LINES = "show_lines";
  private static final String PARAM_LINE_THICKNESS = "line_thickness";

  private static final String PARAM_SHOW_POINTS = "show_points";
  private static final String PARAM_POINT_THICKNESS = "point_thickness";

  private static final String PARAM_BEND_ANGLE = "bend_angle";
  private static final String PARAM_BRANCH_ANGLE = "branch_angle";
  private static final String PARAM_BRANCH_RATIO = "branch_ratio";
  private static final String[] paramNames = {
          PARAM_LEVEL, PARAM_SHOW_LINES, PARAM_LINE_THICKNESS,
          PARAM_BEND_ANGLE, PARAM_BRANCH_ANGLE, PARAM_BRANCH_RATIO, PARAM_SHOW_POINTS, PARAM_POINT_THICKNESS};

  private int nl = 0;
  private int level = 2;

  private int show_lines_param = 1;
  private double line_thickness_param = 0.5;
  private double line_thickness;

  private int show_points_param = 0;
  private double point_thickness_param = 3.0;
  private double point_thickness;

  private double line_fraction, point_fraction;
  private double line_threshold, point_threshold;

  private double bend_angle = 15.0;
  private double branch_angle = 37.0;
  private double branch_ratio = 0.65;

  static class DynamicArray {
    // The storage for the elements. 
    // The capacity is the length of this array.
    private double[] data;

    // The number of elements (logical size).
    // It must be smaller than the capacity.
    private int size;

    // Constructs an empty DynamicArray
    public DynamicArray() {
      data = new double[16];
      size = 0;
    }

    // Constructs an empty DynamicArray with the
    // specified initial capacity.
    public DynamicArray(int capacity) {
      if (capacity < 16)
        capacity = 16;
      data = new double[capacity];
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
        data = Arrays.copyOf(data, newCapacity);
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
    public double get(int index) {
      rangeCheck(index);
      return data[index];
    }

    // Appends the specified element to the end.
    public boolean add(double element) {
      ensureCapacity(size + 1);
      data[size++] = element;
      return true;
    }

    // Removes all of the elements.
    public void clear() {
      size = 0;
    }

    // Replaces the element at the specified position
    public double set(int index, double element) {
      rangeCheck(index);
      double oldValue = data[index];
      data[index] = element;
      return oldValue;
    }

    public int capacity() {
      return data.length;
    }
  }

  static class DoublePoint2D {
    public double x;
    public double y;
  }

  private DynamicArray xpoints = new DynamicArray(1000);
  private DynamicArray ypoints = new DynamicArray(1000);

  private DoublePoint2D endpoint1 = new DoublePoint2D();
  private DoublePoint2D endpoint2 = new DoublePoint2D();

  public void tree(int n, double x, double y, double a, double branchRadius) {
    double bendAngle = Math.toRadians(bend_angle);
    double branchAngle = Math.toRadians(branch_angle);
    double branchRatio = branch_ratio;

    double cx = x + MathLib.cos(a) * branchRadius;
    double cy = y + MathLib.sin(a) * branchRadius;

    // line_thickness=10*Math.pow(n, 1.2);

    xpoints.add(x);
    ypoints.add(y);
    xpoints.add(cx);
    ypoints.add(cy);

    // System.out.println("x1 , y1 - x2,y2 : < " + x + "," + y +" >-< " + cx +" , " +cy + " > <" + thickness + ">");
    if (n == 0)
      return;

    tree(n - 1, cx, cy, a + bendAngle - branchAngle, branchRadius * branchRatio);
    tree(n - 1, cx, cy, a + bendAngle + branchAngle, branchRadius * branchRatio);
    tree(n - 1, cx, cy, a + bendAngle, branchRadius * (1 - branchRatio));
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    line_thickness = line_thickness_param / 100;
    nl = 0;

    double show_sum = show_lines_param + show_points_param;

    line_fraction = show_lines_param / show_sum;
    point_fraction = show_points_param / show_sum;

    line_threshold = line_fraction;
    point_threshold = line_fraction + point_fraction;
    // point_half_threshold = line_fraction + (point_fraction/2);

    line_thickness = line_thickness_param / 100;
    point_thickness = point_thickness_param / 100;

  }

  public void getPointT(DoublePoint2D result) {
    result.x = xpoints.get(nl);
    result.y = ypoints.get(nl);
    nl++;
    if (nl >= xpoints.size())
      nl = 0;
  }

  public DoublePoint2D plotLine(FlameTransformationContext pContext, double x1, double y1, double x2, double y2) {
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

  public DoublePoint2D plotPoint(FlameTransformationContext pContext, double x1, double y1) {
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

    double x1 = 0.0;
    double y1 = 0.0;
    double x2 = 0.0;
    double y2 = 0.0;
    DoublePoint2D out = new DoublePoint2D();

    getPointT(endpoint1);
    x1 = endpoint1.x;
    y1 = endpoint1.y;
    getPointT(endpoint2);
    x2 = endpoint2.x;
    y2 = endpoint2.y;

    double rnd = pContext.random();

    if (rnd < line_threshold) {
      out = plotLine(pContext, x1, y1, x2, y2);
    } else if (rnd <= point_threshold) {
      out = plotPoint(pContext, x1, y1);
    }
    pVarTP.x += pAmount * out.x;
    pVarTP.y += pAmount * out.y;

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
    return new Object[]{level, show_lines_param, line_thickness_param, bend_angle, branch_angle, branch_ratio, show_points_param, point_thickness_param};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LEVEL.equalsIgnoreCase(pName)) {
      level = limitIntVal(Tools.FTOI(pValue), 1, 10);
      xpoints.clear();
      ypoints.clear();
      tree(level, 0.0, 0.0, Math.PI / 2.0, 2);
    } else if (PARAM_SHOW_LINES.equalsIgnoreCase(pName))
      show_lines_param = (int) pValue;
    else if (PARAM_LINE_THICKNESS.equalsIgnoreCase(pName))
      line_thickness_param = pValue;
    else if (PARAM_BEND_ANGLE.equalsIgnoreCase(pName)) {
      bend_angle = pValue;
      xpoints.clear();
      ypoints.clear();
      tree(level, 0.0, 0.0, Math.PI / 2.0, 2);
    } else if (PARAM_BRANCH_ANGLE.equalsIgnoreCase(pName)) {
      branch_angle = pValue;
      xpoints.clear();
      ypoints.clear();
      tree(level, 0.0, 0.0, Math.PI / 2.0, 2);
    } else if (PARAM_BRANCH_RATIO.equalsIgnoreCase(pName)) {
      branch_ratio = pValue;
      xpoints.clear();
      ypoints.clear();
      tree(level, 0.0, 0.0, Math.PI / 2.0, 2);
    } else if (PARAM_SHOW_POINTS.equalsIgnoreCase(pName))
      show_points_param = (int) pValue;
    else if (PARAM_POINT_THICKNESS.equalsIgnoreCase(pName))
      point_thickness_param = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "tree_js";
  }

}
