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

public class FractJuliaWFFunc extends AbstractFractWFFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public String getName() {
    return "fract_julia_wf";
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
    while ((currIter++ < max_iter) && ((xs + ys) < 4.0)) {
      y1 = 2.0 * x1 * y1 + yseed;
      x1 = xs - ys + xseed;
      xs = x1 * x1;
      ys = y1 * y1;
    }
    return currIter;
  }

  @Override
  protected void initParams() {
    xmin = -1.0;
    xmax = 1.0;
    ymin = -1.2;
    ymax = 1.2;
    xseed = 0.35;
    yseed = 0.09;
    clip_iter_min = 4;
    scale = 3.8;
  }
}
