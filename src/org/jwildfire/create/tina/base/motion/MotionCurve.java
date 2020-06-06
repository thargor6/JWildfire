/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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
package org.jwildfire.create.tina.base.motion;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.Envelope.Interpolation;

public class MotionCurve implements Serializable, Assignable<MotionCurve> {
  private static final long serialVersionUID = 1L;

  private boolean enabled;
  private int viewXMin = -10;
  private int viewXMax = 310;
  private double viewYMin = -120.0;
  private double viewYMax = 120.0;
  private Interpolation interpolation = Interpolation.SPLINE;
  private int selectedIdx = 0;
  private boolean locked;
  private int[] x = new int[] {};
  private double[] y = new double[] {};
  private MotionCurve parent;
  private final MotionValueChangeHandler changeHandler;

  public MotionCurve() {
    changeHandler = DefaultMotionValueChangeHandler.INSTANCE;
  }

  public MotionCurve(MotionValueChangeHandler pChangeHandler) {
    changeHandler = pChangeHandler;
  }

  public void assignFromEnvelope(Envelope pEnvelope) {
    viewXMin = pEnvelope.getViewXMin();
    viewXMax = pEnvelope.getViewXMax();
    viewYMin = pEnvelope.getViewYMin();
    viewYMax = pEnvelope.getViewYMax();
    interpolation = pEnvelope.getInterpolation();
    selectedIdx = pEnvelope.getSelectedIdx();
    locked = pEnvelope.isLocked();
    x = new int[pEnvelope.getX().length];
    System.arraycopy(pEnvelope.getX(), 0, x, 0, x.length);

    y = new double[pEnvelope.getY().length];
    System.arraycopy(pEnvelope.getY(), 0, y, 0, y.length);
  }

  public Envelope toEnvelope() {
    Envelope res = new Envelope();
    res.setViewXMin(viewXMin);
    res.setViewXMax(viewXMax);
    res.setViewYMin(viewYMin);
    res.setViewYMax(viewYMax);
    res.setInterpolation(interpolation);
    res.setSelectedIdx(selectedIdx);
    res.setLocked(locked);
    int[] newX = new int[x.length];
    System.arraycopy(x, 0, newX, 0, x.length);

    double[] newY = new double[y.length];
    System.arraycopy(y, 0, newY, 0, y.length);

    res.setValues(newX, newY);
    return res;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public void assign(MotionCurve pSrc) {
    enabled = pSrc.enabled;
    viewXMin = pSrc.viewXMin;
    viewXMax = pSrc.viewXMax;
    viewYMin = pSrc.viewYMin;
    viewYMax = pSrc.viewYMax;
    interpolation = pSrc.interpolation;
    selectedIdx = pSrc.selectedIdx;
    locked = pSrc.locked;
    x = new int[pSrc.x.length];
    System.arraycopy(pSrc.x, 0, x, 0, x.length);
    y = new double[pSrc.y.length];
    System.arraycopy(pSrc.y, 0, y, 0, y.length);
    parent = pSrc.parent;
  }

  @Override
  public MotionCurve makeCopy() {
    MotionCurve res = new MotionCurve();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(MotionCurve pSrc) {
    if ((enabled != pSrc.enabled) || (fabs(viewXMin - pSrc.viewXMin) > EPSILON) ||
        (fabs(viewXMax - pSrc.viewXMax) > EPSILON) || (fabs(viewYMin - pSrc.viewYMin) > EPSILON) ||
        (fabs(viewYMax - pSrc.viewYMax) > EPSILON) || (interpolation != pSrc.interpolation) ||
        (selectedIdx != pSrc.selectedIdx) || (locked != pSrc.locked) ||
        (x.length != pSrc.x.length) || (y.length != pSrc.y.length) ||
        (parent == null && pSrc.parent != null) || (parent != null && pSrc.parent == null) ||
        (parent != null && pSrc.parent != null && !parent.isEqual(pSrc.parent))) {
      return false;
    }
    for (int i = 0; i < x.length; i++) {
      if (x[i] != pSrc.x[i]) {
        return false;
      }
    }
    for (int i = 0; i < y.length; i++) {
      if (fabs(y[i] - pSrc.y[i]) > EPSILON) {
        return false;
      }
    }
    return true;
  }

  public boolean isEmpty() {
    return x.length == 0;
  }

  public MotionCurve getParent() {
    return parent;
  }

  public void setParent(MotionCurve parent) {
    this.parent = parent;
  }

  public MotionValueChangeHandler getChangeHandler() {
    return changeHandler;
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

  public Interpolation getInterpolation() {
    return interpolation;
  }

  public void setInterpolation(Interpolation interpolation) {
    this.interpolation = interpolation;
  }

  public int getSelectedIdx() {
    return selectedIdx;
  }

  public void setSelectedIdx(int selectedIdx) {
    this.selectedIdx = selectedIdx;
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public int[] getX() {
    return x;
  }

  public void setPoints(int[] x, double[] y) {
    if (x == null || y == null || x.length != y.length) {
      throw new IllegalArgumentException();
    }
    this.x = x;
    this.y = y;
  }

  public double[] getY() {
    return y;
  }

  public void appendKeyFrame(Integer pKeyFrame, double pValue) {
    int newX[] = new int[x.length + 1];
    System.arraycopy(x, 0, newX, 0, x.length);
    newX[x.length] = pKeyFrame;
    x = newX;
    double newY[] = new double[y.length + 1];
    System.arraycopy(y, 0, newY, 0, y.length);
    newY[y.length] = pValue;
    y = newY;
  }

  public MotionCurve multiplyValue(double pScale) {
    MotionCurve res = this.makeCopy();
    for (int i = 0; i < y.length; i++) {
      y[i] *= pScale;
    }
    return res;
  }

  public void clear() {
    x = new int[] {};
    y = new double[] {};
    viewXMin = -10;
    viewXMax = 310;
    viewYMin = -120.0;
    viewYMax = 120.0;
    interpolation = Interpolation.SPLINE;
    selectedIdx = 0;
  }

  public int indexOfKeyFrame(int frame) {
    if(x!=null) {
      for(int i=0;i<x.length;i++) {
        if(frame==x[i]) {
          return i;
        }
      }
    }
    return -1;
  }

  public boolean hasKeyFrame(int frame) {
    return indexOfKeyFrame(frame) >= 0;
  }

  public void updateKeyFrame(int frame, double value) {
    int idx = indexOfKeyFrame(frame);
    if(idx<0) {
      throw new RuntimeException("Invalid frame <" + frame + ">");
    }
    y[idx] = value;
  }

  public void addKeyFrame(int frame, double value) {
    if(indexOfKeyFrame(frame)>=0) {
      throw new RuntimeException("Key-frame <" + frame + "> already exists");
    }
    int newx[] = new int[x.length+1];
    double newy[] = new double[x.length+1];
    int idx=0;
    for(int i=0;i<x.length;i++) {
      if(x[i]<frame) {
        newx[idx] = x[i];
        newy[idx++] = y[i];
      }
      else {
        break;
      }
    }
    newx[idx] = frame;
    newy[idx++] = value;
    for(int i=0;i<x.length;i++) {
      if(x[i]>frame) {
        newx[idx] = x[i];
        newy[idx++] = y[i];
      }
    }
    x = newx;
    y = newy;
  }

  public void autoFitRange() {
    int xmin, xmax;
    double ymin, ymax;
    if (x.length > 0) {
      xmin = xmax = x[0];
      ymin = ymax = y[0];
      for (int i = 1; i < x.length; i++) {
        if (x[i] < xmin)
          xmin = x[i];
        else if (x[i] > xmax)
          xmax = x[i];
        if (y[i] < ymin)
          ymin = y[i];
        else if (y[i] > ymax)
          ymax = y[i];
      }
    }
    else {
      xmin = 1;
      xmax = 9;
      ymin = -0.5;
      ymax = 0.5;
    }
    viewXMin = xmin - Math.max(1, Tools.FTOI((xmax - xmin) * 0.1));
    viewXMax = xmax + Math.max(1, Tools.FTOI((xmax - xmin) * 0.1));
    viewYMin = ymin - (ymax - ymin) * 0.1;
    viewYMax = ymax + (ymax - ymin) * 0.1;
  }

  public void deleteKeyFrameIfExists(int pKeyFrame) {
    int idx = indexOfKeyFrame(pKeyFrame);
    if(idx>=0) {
      // last keyframe -> reset the curve
      if(x.length==1) {
        assign(new MotionCurve());
        setEnabled(false);
      }
      // remove the keyframe
      else {
        deleteKeyFrame(pKeyFrame);
      }
    }
  }

  private void deleteKeyFrame(int pKeyFrame) {
    if(x.length<2) {
      throw new RuntimeException("There must be at least one curve point");
    }
    int idx = indexOfKeyFrame(pKeyFrame);
    if(idx<0) {
      throw new RuntimeException("Keyframe <"+pKeyFrame+"> does not exists");
    }
    int newX[] = new int[x.length-1];
    double newY[] = new double[x.length-1];
    int dstIdx=0;
    for(int i=0;i<x.length;i++) {
      if(i!=idx) {
        newX[dstIdx] = x[i];
        newY[dstIdx++] = y[i];
      }
    }
    x = newX;
    y = newY;
  }

  public int getNextKeyFrame(int pStartKeyFrame, boolean pForward) {
    if(pForward) {
      for(int i=0;i<x.length;i++) {
        if(x[i]>pStartKeyFrame) {
          return x[i];
        }
      }
    }
    else {
      for(int i=x.length-1;i>=0;i--) {
        if(x[i]<pStartKeyFrame) {
          return x[i];
        }
      }
    }
    return -1;
  }

  public void duplicateKeyFrameIfExists(int pKeyFrame, int pDestKeyFrame) {
    int idx = indexOfKeyFrame(pKeyFrame);
    if(idx>=0) {
      double valueAtKeyFrame = y[idx];
      if(hasKeyFrame(pDestKeyFrame)) {
        updateKeyFrame(pDestKeyFrame, valueAtKeyFrame);
      }
      else {
        addKeyFrame(pDestKeyFrame, valueAtKeyFrame);
      }
    }
  }
}
