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

import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_JWILDFIRE;

public class FractMandForthWFFunc extends AbstractFractWFFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public String getName() {
    return "fract_mandforth_wf";
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE;
  }

  @Override
  protected int iterate(double pX, double pY) {
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

  @Override
  protected void initParams() {
    xmin = -1.35;
    xmax = 1.2;
    ymin = -1.25;
    ymax = 1.25;
    xseed = 0;
    yseed = 0;
    clip_iter_min = 6;
    max_clip_iter = 7;
    scale = 4.0;
  }
}
