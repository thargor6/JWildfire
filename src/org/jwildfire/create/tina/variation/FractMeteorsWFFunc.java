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

public class FractMeteorsWFFunc extends AbstractFractWFFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public String getName() {
    return "fract_meteors_wf";
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
      double x2 = (pX * x1 - pY * y1) * (1 - yseed) - (pX * x1 + pY * y1) / (xs + ys) * (1 + xseed);
      y1 = (pX * y1 + pY * x1) * (1 - xseed) + (pX * y1 - pY * x1) / (xs + ys) * (1 + yseed);

      x1 = x2;
      xs = x1 * x1;
      ys = y1 * y1;
    }
    return currIter;
  }

  @Override
  protected void initParams() {
    xmin = -1.7;
    xmax = 1.7;
    ymin = -1.1;
    ymax = 1.1;
    xseed = 0;
    yseed = 0;
    clip_iter_min = 3;
    scale = 5.7;
  }
}
