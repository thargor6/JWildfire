/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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

import java.util.List;

public class FractDragonWFFunc extends AbstractFractWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XSEED = "xseed";
  private static final String PARAM_YSEED = "yseed";

  private double xseed;
  private double yseed;

  @Override
  public String getName() {
    return "fract_dragon_wf";
  }

  @Override
  protected void initParams() {
    xmin = -0.25;
    xmax = 1.2;
    ymin = -0.75;
    ymax = 0.75;
    xseed = 1.4;
    yseed = 0.85;
    clip_iter_min = 4;
    offsetx = -0.5;
    scale = 5.0;
  }

  @Override
  protected boolean setCustomParameter(String pName, double pValue) {
    if (PARAM_XSEED.equalsIgnoreCase(pName)) {
      xseed = pValue;
      return true;
    } else if (PARAM_YSEED.equalsIgnoreCase(pName)) {
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

  public class DragonIterator extends Iterator {

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      double x2 = (x1 - this.xs + this.ys) * xseed - (y1 - 2.0 * x1 * y1) * yseed;
      y1 = (x1 - this.xs + this.ys) * yseed + (y1 - 2.0 * x1 * y1) * xseed;
      x1 = x2;
      setCurrPoint(x1, y1);
    }

  }

  private DragonIterator iterator = new DragonIterator();

  @Override
  protected Iterator getIterator() {
    return iterator;
  }

}
