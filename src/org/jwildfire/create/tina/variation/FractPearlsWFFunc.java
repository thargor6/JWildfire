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

public class FractPearlsWFFunc extends AbstractFractWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XSEED = "xseed";
  private static final String PARAM_YSEED = "yseed";

  private double xseed;
  private double yseed;

  @Override
  public String getName() {
    return "fract_pearls_wf";
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE | AVAILABILITY_CUDA;
  }

  @Override
  protected int iterate(double pX, double pY) {
    int currIter = 0;
    double x1 = pX;
    double y1 = pY;

    double jx = xseed;
    double jy = yseed;

    double xs = x1 * x1;
    double ys = y1 * y1;
    while ((currIter++ < max_iter) && (xs + ys < 4.0)) {
      double x2 = jx * x1 - jy * y1 - ((jx * x1 + jy * y1) / (xs + ys));
      y1 = jx * y1 + jy * x1 + ((jx * y1 - jy * x1) / (xs + ys));

      x1 = x2;
      xs = x1 * x1;
      ys = y1 * y1;
    }
    return currIter;
  }

  @Override
  protected void initParams() {
    xmin = -2.0;
    xmax = 2.0;
    ymin = -2.0;
    ymax = 2.0;
    clip_iter_min = 1;
    scale = 2.45;
    xseed = 0.0;
    yseed = -0.31;
  }

  @Override
  protected boolean setCustomParameter(String pName, double pValue) {
    if (PARAM_XSEED.equalsIgnoreCase(pName)) {
      xseed = pValue;
      return true;
    }
    else if (PARAM_YSEED.equalsIgnoreCase(pName)) {
      yseed = pValue;
      return true;
    }
    return false;
  }

  @Override
  protected void addCustomParameterNames(List<String> pList) {
    pList.add(PARAM_XSEED);
    pList.add(PARAM_YSEED);
  }

  @Override
  protected void addCustomParameterValues(List<Object> pList) {
    pList.add(xseed);
    pList.add(yseed);
  }
}
