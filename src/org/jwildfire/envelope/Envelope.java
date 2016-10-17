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
package org.jwildfire.envelope;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

import org.jwildfire.base.Tools;

public class Envelope implements Serializable, Cloneable {
  private static final long serialVersionUID = 1L;

  public enum Interpolation {
    SPLINE, BEZIER, LINEAR
  }

  public enum EditMode {
    DRAG_POINTS, DRAG_CURVE_HORIZ, DRAG_CURVE_VERT, SCALE_CURVE_HORIZ, SCALE_CURVE_VERT
  }

  private int viewXMin = -10;
  private int viewXMax = 70;
  private double viewYMin = -120.0;
  private double viewYMax = 120.0;
  private Interpolation interpolation = Interpolation.SPLINE;
  private int selectedIdx = 0;
  private int x[];
  private int xmin, xmax;
  private double y[];
  private boolean locked;
  private boolean useBisection = false;

  public Envelope clone() {
    Envelope res = new Envelope();
    res.viewXMin = viewXMin;
    res.viewXMax = viewXMax;
    res.viewYMin = viewYMin;
    res.viewYMax = viewYMax;
    res.interpolation = interpolation;
    res.selectedIdx = selectedIdx;
    res.x = new int[x.length];
    res.y = new double[x.length];
    for (int i = 0; i < x.length; i++) {
      res.x[i] = x[i];
      res.y[i] = y[i];
    }
    res.xmin = xmin;
    res.xmax = xmax;
    return res;
  }

  public Envelope() {
    x = new int[0];
    y = new double[0];
    updateMinMax();
  }

  public Envelope(double value) {
    x = new int[1];
    x[0] = 1;
    y = new double[1];
    y[0] = value;
    updateMinMax();
  }

  public Envelope(int[] pX, double pY[]) {
    if (pX == null || pY == null || pX.length != pY.length) {
      throw new IllegalArgumentException();
    }
    x = pX;
    y = pY;
    updateMinMax();
  }

  public Envelope(double pValue, int pViewXMin, int pViewXMax, double pViewYMin, double pViewYMax) {
    x = new int[1];
    x[0] = 1;
    y = new double[1];
    y[0] = pValue;
    viewXMin = pViewXMin;
    viewXMax = pViewXMax;
    viewYMin = pViewYMin;
    viewYMax = pViewYMax;
    updateMinMax();
  }

  public int getViewXMin() {
    return viewXMin;
  }

  public void setViewXMin(int viewXMin) {
    this.viewXMin = viewXMin;
  }

  public int getViewXMax() {
    return viewXMax;
  }

  public void setViewXMax(int viewXMax) {
    this.viewXMax = viewXMax;
  }

  public double getViewYMin() {
    return viewYMin;
  }

  public void setViewYMin(double viewYMin) {
    this.viewYMin = viewYMin;
  }

  public double getViewYMax() {
    return viewYMax;
  }

  public void setViewYMax(double viewYMax) {
    this.viewYMax = viewYMax;
  }

  public void setInterpolation(Interpolation interpolation) {
    this.interpolation = interpolation;
  }

  public Interpolation getInterpolation() {
    return interpolation;
  }

  public int size() {
    return x.length;
  }

  public void setSelectedX(int pX) {
    x[selectedIdx] = pX;
    updateMinMax();
  }

  public void setSelectedY(double pY) {
    y[selectedIdx] = pY;
  }

  public void select(int pIdx) {
    if ((pIdx < 0) || (pIdx >= x.length))
      throw new IllegalArgumentException(String.valueOf(pIdx));
    selectedIdx = pIdx;
  }

  public int getSelectedX() {
    return x[selectedIdx];
  }

  public double getSelectedY() {
    return y[selectedIdx];
  }

  public void clear() {
    viewXMin = -10;
    viewXMax = 70;
    viewYMin = -120.0;
    viewYMax = 120.0;
    interpolation = Interpolation.SPLINE;
    selectedIdx = 0;
    x = new int[1];
    y = new double[1];
    updateMinMax();
  }

  public int[] getX() {
    return x;
  }

  public double[] getY() {
    return y;
  }

  public int getSelectedIdx() {
    return selectedIdx;
  }

  public void setValues(int[] pX, double[] pY) {
    if (pX.length != pY.length)
      throw new IllegalArgumentException();
    x = pX;
    y = pY;
    if (selectedIdx >= x.length)
      selectedIdx--;
    if (selectedIdx < 0 && x.length > 0)
      selectedIdx = 0;
    updateMinMax();
  }

  private static final Map<InterpolatedPointsKey, InterpolatedPoints> interpolatedPointCache = new WeakHashMap<InterpolatedPointsKey, InterpolatedPoints>();

  private static InterpolatedPoints getInterpolatedPoints(int pX[], double pY[], Interpolation pInterpolation) {
    InterpolatedPointsKey key = new InterpolatedPointsKey(pX, pY, pInterpolation);
    InterpolatedPoints res = interpolatedPointCache.get(key);
    if (res == null) {
      res = new InterpolatedPoints(pX, pY, pInterpolation);
      interpolatedPointCache.put(key, res);
    }
    return res;
  }

  private static class InterpolatedPointsKey {
    private final int x[];
    private final double y[];
    private final Interpolation interpolation;
    private final int hashCode;

    public InterpolatedPointsKey(int pX[], double pY[], Interpolation pInterpolation) {
      x = pX;
      y = pY;
      interpolation = pInterpolation;
      hashCode = calcHashCode();
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    private int calcHashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((interpolation == null) ? 0 : interpolation.hashCode());
      result = prime * result + Arrays.hashCode(x);
      result = prime * result + Arrays.hashCode(y);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      InterpolatedPointsKey other = (InterpolatedPointsKey) obj;
      if (interpolation != other.interpolation)
        return false;
      if (!Arrays.equals(x, other.x))
        return false;
      if (!Arrays.equals(y, other.y))
        return false;
      return true;
    }

  }

  private static class InterpolatedPoints {

    private final double vSX[];
    private final double vSY[];
    private final int vSNum;

    public InterpolatedPoints(int pX[], double pY[], Interpolation pInterpolation) {
      int size = pX.length;
      int subdiv = org.jwildfire.envelope.Interpolation.calcSubDivPRV(pX, size);
      org.jwildfire.envelope.Interpolation interpolationX, interpolationY;
      if (size > 2) {
        switch (pInterpolation) {
          case SPLINE:
            interpolationX = new SplineInterpolation();
            interpolationY = new SplineInterpolation();
            break;
          case BEZIER:
            interpolationX = new BezierInterpolation();
            interpolationY = new BezierInterpolation();
            break;
          default:
            interpolationX = new LinearInterpolation();
            interpolationY = new LinearInterpolation();
            break;
        }
      }
      else {
        interpolationX = new LinearInterpolation();
        interpolationY = new LinearInterpolation();
      }
      interpolationX.setSrc(pX);
      interpolationX.setSnum(size);
      interpolationX.setSubdiv(subdiv);
      interpolationX.interpolate();
      interpolationY.setSrc(pY);
      interpolationY.setSnum(size);
      interpolationY.setSubdiv(subdiv);
      interpolationY.interpolate();
      if (interpolationX.getDnum() != interpolationY.getDnum())
        throw new IllegalStateException();

      vSNum = interpolationX.getDnum();

      vSX = interpolationX.getDest();
      vSY = interpolationY.getDest();
    }

    public double[] getvSX() {
      return vSX;
    }

    public double[] getvSY() {
      return vSY;
    }

    public int getvSNum() {
      return vSNum;
    }
  }

  private void updateMinMax() {
    if (x.length > 0) {
      xmin = xmax = x[0];
      for (int i = 1; i < size(); i++) {
        if (x[i] < xmin)
          xmin = x[i];
        else if (x[i] > xmax)
          xmax = x[i];
      }
    }
    else {
      xmin = xmax = 0;
    }
  }

  public double evaluate(double pTime) {
    if (size() == 0)
      return 0.0;
    else if (size() == 1)
      return y[0];
    else if (pTime <= xmin)
      return y[0];
    else if (pTime >= xmax)
      return y[size() - 1];

    int indl = -1, indr = -1;
    InterpolatedPoints iPoints = getInterpolatedPoints(x, y, interpolation);
    double vSX[] = iPoints.getvSX();
    double vSY[] = iPoints.getvSY();
    int vSNum = vSX.length;

    if (useBisection) {
      int low = 0;
      int high = vSNum - 1;
      while (low <= high) {
        int mid = (low + high) >>> 1;
        double midVal = vSX[mid];
        if (midVal < pTime) {
          low = mid + 1;
          indl = mid;
        }
        else if (midVal > pTime) {
          indr = mid;
          high = mid - 1;
        }
        else {
          return vSY[mid];
        }
      }
    }
    else {
      for (int i = 0; i < vSNum; i++) {
        if (Tools.FTOI(vSX[i]) <= pTime) {
          indl = i;
        }
        else {
          indr = i;
          break;
        }
      }
    }

    if ((indl >= 0) && (indr >= 0)) {
      double xdist = vSX[indr] - vSX[indl];
      if (xdist < 0.00000001)
        return vSX[indl];
      else
        return vSY[indl] + (pTime - vSX[indl]) / xdist * (vSY[indr] - vSY[indl]);
    }
    else if (indl >= 0) {
      return vSY[indl];
    }
    else if (indr >= 0) {
      return vSY[indr];
    }
    else {
      return 0.0;
    }

  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public void setSelectedIdx(int pSelectedIdx) {
    selectedIdx = pSelectedIdx;
  }

  @Override
  public String toString() {
    return "Envelope [" + x.length + "]";
  }

  public void setUseBisection(boolean pUseBisection) {
    useBisection = pUseBisection;
  }

  public void smooth(int pSize) {
    if (pSize >= 3 && y != null && y.length > pSize) {
      double newY[] = new double[y.length];
      int s = pSize / 2;
      for (int i = 0; i < y.length; i++) {
        double v = 0.0;
        int w = 0;
        for (int j = i - s; j <= i + s; j++) {
          if (j >= 0 && j < y.length) {
            v += y[j];
            w++;
          }
        }
        newY[i] = v / (double) w;
      }
      y = newY;
    }
  }
}
