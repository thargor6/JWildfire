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

import org.jwildfire.base.Tools;

public class Envelope implements Serializable {
  private static final long serialVersionUID = 1L;

  public enum Interpolation {
    SPLINE, BEZIER, LINEAR
  }

  private int viewXMin = -10;
  private int viewXMax = 70;
  private double viewYMin = -120.0;
  private double viewYMax = 120.0;
  private Interpolation interpolation = Interpolation.SPLINE;
  private int selectedIdx = 0;
  private int x[];
  private double y[];
  private boolean locked;

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
    return res;
  }

  public Envelope() {
    x = new int[0];
    y = new double[0];
  }

  public Envelope(double value) {
    x = new int[1];
    x[0] = 1;
    y = new double[1];
    y[0] = value;
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
  }

  public double evaluate(int pFrame) {
    /* check if x-value is inside the supported range */
    if (size() == 1)
      return y[0];
    int min, max;
    min = max = x[0];
    for (int i = 1; i < size(); i++) {
      if (x[i] < min)
        min = x[i];
      else if (x[i] > max)
        max = x[i];
    }
    if (pFrame <= min)
      return y[0];
    else if (pFrame >= max)
      return y[size() - 1];

    int subdiv = org.jwildfire.envelope.Interpolation.calcSubDivPRV(x, size());
    org.jwildfire.envelope.Interpolation interpolationX, interpolationY;
    if (size() > 2) {
      switch (getInterpolation()) {
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
    interpolationX.setSrc(x);
    interpolationX.setSnum(size());
    interpolationX.setSubdiv(subdiv);
    interpolationX.interpolate();
    interpolationY.setSrc(y);
    interpolationY.setSnum(size());
    interpolationY.setSubdiv(subdiv);
    interpolationY.interpolate();
    if (interpolationX.getDnum() != interpolationY.getDnum())
      throw new IllegalStateException();

    int indl = -1, indr = -1;
    int vSNum = interpolationX.getDnum();
    double vSX[] = interpolationX.getDest();
    double vSY[] = interpolationY.getDest();
    for (int i = 0; i < vSNum; i++) {
      if (Tools.FTOI(vSX[i]) <= pFrame)
        indl = i;
      else {
        indr = i;
        break;
      }
    }
    if ((indl >= 0) && (indr >= 0)) {
      double xdist = vSX[indr] - vSX[indl];
      if (xdist < 0.001)
        return vSX[indl];
      else
        return vSY[indl] + (pFrame - vSX[indl]) / xdist * (vSY[indr] - vSY[indl]);
    }
    else if (indl >= 0) {
      return vSY[indl];
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
}
