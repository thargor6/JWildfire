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
 * http://introcs.cs.princeton.edu/java/23recursion/RecursiveSquares.java.html
 * rsquares_js Variation
 * Level: 1,2,3,4,5
 * Show_lines: 0=hide,1=show
 * Line_thickness : 1..200
 * show_points: 0=hide,1=show
 * point_thickness_param: 1..200
 */

public class RsquaresFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_LEVEL = "level";
  private static final String PARAM_SHOW_LINES = "show_lines";
  private static final String PARAM_LINE_THICKNESS = "line_thickness";

  private static final String PARAM_SHOW_POINTS = "show_points";
  private static final String PARAM_POINT_THICKNESS = "point_thickness";

  private static final String[] paramNames = {
          PARAM_LEVEL, PARAM_SHOW_LINES, PARAM_LINE_THICKNESS, PARAM_SHOW_POINTS,
          PARAM_POINT_THICKNESS};

  private int level = 2;

  private int show_lines_param = 1;
  private double line_thickness_param = 0.5;
  private double line_thickness;

  private int show_points_param = 0;
  private double point_thickness_param = 3.0;
  private double point_thickness;

  private double line_fraction, point_fraction;
  private double line_threshold, point_threshold;

  private DoublePoint2D endpoint1 = new DoublePoint2D();
  private DoublePoint2D endpoint2 = new DoublePoint2D();

  private rSquares rsquares = new rSquares();

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

  static class rSquares {
    private DynamicArray xpoints = new DynamicArray(1000);
    private DynamicArray ypoints = new DynamicArray(1000);
    private int idx = 0;

    public rSquares() {
      idx = 0;
      clearPoints();
    }

    private void drawSquare(double x, double y, double size) {
      //    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
      //    StdDraw.filledSquare(x, y, size/2);
      //    StdDraw.setPenColor(StdDraw.BLACK);
      //    StdDraw.square(x, y, size/2);
      xpoints.add(x - size / 2);
      ypoints.add(y - size / 2);

      xpoints.add(x - size / 2);
      ypoints.add(y + size / 2);

      xpoints.add(x - size / 2);
      ypoints.add(y + size / 2);

      xpoints.add(x + size / 2);
      ypoints.add(y + size / 2);

      xpoints.add(x + size / 2);
      ypoints.add(y + size / 2);

      xpoints.add(x + size / 2);
      ypoints.add(y - size / 2);

      xpoints.add(x + size / 2);
      ypoints.add(y - size / 2);

      xpoints.add(x - size / 2);
      ypoints.add(y - size / 2);

    }

    // plot an order n recursive squares pattern
    // centered on (x, y) of the given side length
    public void draw(int n, double x, double y, double size) {
      if (n == 0)
        return;

      drawSquare(x, y, size);

      // 2.2 ratio looks good
      double ratio = 2.2;

      // recursively draw 4 smaller trees of order n-1
      draw(n - 1, x - size / 2, y - size / 2, size / ratio); // lower left  
      draw(n - 1, x - size / 2, y + size / 2, size / ratio); // upper left  
      draw(n - 1, x + size / 2, y - size / 2, size / ratio); // lower right 
      draw(n - 1, x + size / 2, y + size / 2, size / ratio); // upper right
    }

    public void getPoint(DoublePoint2D result) {
      result.x = xpoints.get(idx);
      result.y = ypoints.get(idx);
      idx++;
      if (idx >= xpoints.size())
        idx = 0;
    }

    public void clearPoints() {
      xpoints.clear();
      ypoints.clear();
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
    // point_half_threshold = line_fraction + (point_fraction/2);

    line_thickness = line_thickness_param / 100;
    point_thickness = point_thickness_param / 100;

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

  public DoublePoint2D plotPoint(FlameTransformationContext pContext, double x1, double y1, double x2, double y2) {
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

    rsquares.getPoint(endpoint1);
    x1 = endpoint1.x;
    y1 = endpoint1.y;

    rsquares.getPoint(endpoint2);
    x2 = endpoint2.x;
    y2 = endpoint2.y;

    double rnd = pContext.random();

    if (rnd < line_threshold)
      out = plotLine(pContext, x1, y1, x2, y2);
    else if (rnd <= point_threshold)
      out = plotPoint(pContext, x1, y1, x2, y2);

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
    return new Object[]{level, show_lines_param, line_thickness_param, show_points_param, point_thickness_param};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LEVEL.equalsIgnoreCase(pName)) {
      level = limitIntVal(Tools.FTOI(pValue), 1, 9);
      rsquares.draw(level, 0.5, 0.5, 1.0);
    } else if (PARAM_SHOW_LINES.equalsIgnoreCase(pName))
      show_lines_param = (int) pValue;
    else if (PARAM_LINE_THICKNESS.equalsIgnoreCase(pName))
      line_thickness_param = pValue;
    else if (PARAM_SHOW_POINTS.equalsIgnoreCase(pName))
      show_points_param = (int) pValue;
    else if (PARAM_POINT_THICKNESS.equalsIgnoreCase(pName))
      point_thickness_param = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "rsquares_js";
  }

}
