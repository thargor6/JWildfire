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

/**
 * @author Jesus Sosa
 * @date October 17, 2017
 * Adapted from the algoritm from:
 * http://introcs.cs.princeton.edu/java/32class/Dragon.java.html
 * dragon_JS Variation
 * Level: 1,2,3,4,5
 * Line_thickness : 1..200
 */

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Arrays;

public class DragonFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_LEVEL = "level";
  private static final String PARAM_LINE_THICKNESS = "line_thickness";

  private static final String[] paramNames = {
          PARAM_LEVEL, PARAM_LINE_THICKNESS};

  private int level = 2;
  private double line_thickness_param = 0.5;
  private double line_thickness;
  private Turtle turtle;

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

  static class Turtle {
    private double tx, ty; // turtle is at (x, y)
    private double tangle; // facing this many degrees counterclockwise from the x-axis

    private int idx = 0;
    private DynamicArray xpoints = new DynamicArray(1000);
    private DynamicArray ypoints = new DynamicArray(1000);

    // start at (x0, y0), facing a0 degrees counterclockwise from the x-axis
    public Turtle(double x0, double y0, double a0) {
      tx = x0;
      ty = y0;
      tangle = a0;
    }

    // rotate orientation delta degrees counterclockwise
    public void turnLeft(double delta) {
      tangle += delta;
    }

    // move forward the given amount, with the pen down

    public void goForward(double step) {
      double oldx = tx;
      double oldy = ty;
      tx += step * MathLib.cos(Math.toRadians(tangle));
      ty += step * MathLib.sin(Math.toRadians(tangle));

      xpoints.add(oldx);
      ypoints.add(oldy);

      xpoints.add(tx);
      ypoints.add(ty);

    }

    public void getPoint(DoublePoint2D result) {
      result.x = xpoints.get(idx);
      result.y = ypoints.get(idx);
      idx++;
      if (idx >= xpoints.size())
        idx = 0;
    }
  }

  static class DoublePoint2D {
    public double x;
    public double y;
  }

  private DoublePoint2D endpoint1 = new DoublePoint2D();
  private DoublePoint2D endpoint2 = new DoublePoint2D();

  public void nogard(int n) {
    if (n == 0) {
      turtle.goForward(1.0);
    } else {
      dragon(n - 1);
      turtle.turnLeft(-90);
      nogard(n - 1);
    }
  }

  // dragon curve of order n
  public void dragon(int n) {
    if (n == 0) {
      turtle.goForward(1.0);
    } else {
      dragon(n - 1);
      turtle.turnLeft(90);
      nogard(n - 1);
    }
  }

  public void Dragon(int n) {
    /***********************************************************************
     *  The following constants are used to figure out where to start         
     *  drawing the dragon curve.  left[i] = maximum number of steps taken
     *  to the left in dragon(i).  right[i], up[i], down[i] are similar.
     ***********************************************************************/
    //     int[] left  = { 0, 0, 0, 2, 4, 5, 5,  5,  5,  5, 10, 42, 74, 81,  85,  85 };
    //     int[] right = { 1, 1, 1, 1, 1, 1, 2, 10, 18, 21, 21, 21, 21, 21,  57, 170 };
    //     int[] up    = { 0, 1, 2, 2, 2, 2, 2,  2,  5, 21, 37, 42, 42, 42,  42,  42 };
    //     int[] down  = { 0, 0, 0, 0, 1, 5, 9, 10, 10, 10, 10, 10, 23, 85, 149, 165 };

    //     double size = Math.max(left[n] + right[n], up[n] + down[n]);
    //     double x = (right[n] - left[n]) / 2.0;
    //     double y = (up[n]    - down[n]) / 2.0;

    turtle = new Turtle(0.0, 0.0, 0.0);
    //    turtle = new Turtle(x - size/2, y - size/2, 0.0);
    //     turtle.setXscale(x - size/2, x + size/2);
    //     turtle.setYscale(y - size/2, y + size/2);
    dragon(n);
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    line_thickness = line_thickness_param / 100;

  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double x1 = 0.0;
    double y1 = 0.0;
    double x2 = 0.0;
    double y2 = 0.0;

    turtle.getPoint(endpoint1);
    x1 = endpoint1.x;
    y1 = endpoint1.y;

    turtle.getPoint(endpoint2);
    x2 = endpoint2.x;
    y2 = endpoint2.y;

    double ydiff = y2 - y1;
    double xdiff = x2 - x1;
    double m;
    if (xdiff == 0)
      m = 10000;
    else
      m = ydiff / xdiff; // slope
    double line_length = MathLib.sqrt((xdiff * xdiff) + (ydiff * ydiff));

    double xout, yout;
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

    pVarTP.x += pAmount * xout;
    pVarTP.y += pAmount * yout;
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
    return new Object[]{level, line_thickness_param};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LEVEL.equalsIgnoreCase(pName)) {
      level = limitIntVal(Tools.FTOI(pValue), 1, 21);
      turtle = new Turtle(0.0, 0.0, 0.0);
      Dragon(level);
    } else if (PARAM_LINE_THICKNESS.equalsIgnoreCase(pName))
      line_thickness_param = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dragon_js";
  }

}
