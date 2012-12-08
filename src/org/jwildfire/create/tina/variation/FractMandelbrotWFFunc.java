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

import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_CUDA;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_JWILDFIRE;

import java.util.List;

import org.jwildfire.base.Tools;

public class FractMandelbrotWFFunc extends AbstractFractWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";

  private int power;

  @Override
  public String getName() {
    return "fract_mandelbrot_wf";
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE | AVAILABILITY_CUDA;
  }

  @Override
  protected int iterate(double pX, double pY) {
    switch (power) {
      case 2:
        return iterate2(pX, pY);
      case 3:
        return iterate3(pX, pY);
      case 4:
        return iterate4(pX, pY);
      default:
        return iterateN(pX, pY);
    }
  }

  protected int iterate2(double pX, double pY) {
    int currIter = 0;
    double x1 = pX;
    double y1 = pY;
    double xs = x1 * x1;
    double ys = y1 * y1;
    while ((currIter++ < max_iter) && (xs + ys < 4.0)) {
      y1 = 2.0 * x1 * y1 + pY;
      x1 = (xs - ys) + pX;
      xs = x1 * x1;
      ys = y1 * y1;
    }
    return currIter;
  }

  protected int iterate3(double pX, double pY) {
    int currIter = 0;
    double x1 = pX;
    double y1 = pY;
    double xs = x1 * x1;
    double ys = y1 * y1;
    while ((currIter++ < max_iter) && (xs + ys < 4.0)) {
      double x2 = xs * x1 - 3.0 * x1 * ys + pX;
      y1 = 3.0 * xs * y1 - ys * y1 + pY;
      x1 = x2;
      xs = x1 * x1;
      ys = y1 * y1;
    }
    return currIter;
  }

  protected int iterate4(double pX, double pY) {
    int currIter = 0;
    double x1 = pX;
    double y1 = pY;
    double xs = x1 * x1;
    double ys = y1 * y1;
    while ((currIter++ < max_iter) && (xs + ys < 4.0)) {
      double x2 = xs * xs + ys * ys - 6.0 * xs * ys + pX;
      y1 = 4.0 * x1 * y1 * (xs - ys) + pY;
      x1 = x2;
      xs = x1 * x1;
      ys = y1 * y1;
    }
    return currIter;
  }

  protected int iterateN(double pX, double pY) {
    int currIter = 0;
    double x1 = pX;
    double y1 = pY;
    double xs = x1 * x1;
    double ys = y1 * y1;
    while ((currIter++ < max_iter) && (xs + ys < 4.0)) {
      double x2 = x1 * (xs * xs - 10.0 * xs * ys + 5.0 * ys * ys);
      double y2 = y1 * (ys * ys - 10.0 * xs * ys + 5.0 * xs * xs);
      for (int k = 5; k < power; k++) {
        double xa = x1 * x2 - y1 * y2;
        double ya = x1 * y2 + x2 * y1;
        x2 = xa;
        y2 = ya;
      }
      x1 = x2 + pX;
      y1 = y2 + pY;

      xs = x1 * x1;
      ys = y1 * y1;
    }
    return currIter;
  }

  @Override
  protected void initParams() {
    xmin = -2.35;
    xmax = 0.75;
    ymin = -1.2;
    ymax = 1.2;
    clip_iter_min = 3;
    power = 2;
    offsetx = 0.55;
    scale = 4.0;
  }

  @Override
  protected void addCustomParameterNames(List<String> pList) {
    pList.add(PARAM_POWER);
  }

  @Override
  protected void addCustomParameterValues(List<Object> pList) {
    pList.add(power);
  }

  @Override
  protected boolean setCustomParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName)) {
      power = Tools.FTOI(pValue);
      if (power < 2)
        power = 2;
      return true;
    }
    return false;
  }
}
