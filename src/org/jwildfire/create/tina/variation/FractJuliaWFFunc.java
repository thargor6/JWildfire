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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;

import java.util.List;

public class FractJuliaWFFunc extends AbstractFractWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_XSEED = "xseed";
  private static final String PARAM_YSEED = "yseed";

  private int power;
  private double xseed;
  private double yseed;

  @Override
  public String getName() {
    return "fract_julia_wf";
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
    power = 2;
  }

  @Override
  protected boolean setCustomParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName)) {
      power = Tools.FTOI(pValue);
      if (power < 2)
        power = 2;
      return true;
    } else if (PARAM_XSEED.equalsIgnoreCase(pName)) {
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
    pList.add(PARAM_POWER);
    pList.add(PARAM_XSEED);
    pList.add(PARAM_YSEED);
  }

  @Override
  protected void addCustomParameterValues(List<Object> pList) {
    pList.add(power);
    pList.add(xseed);
    pList.add(yseed);
  }

  public class Julia2Iterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      y1 = 2.0 * x1 * y1 + yseed;
      x1 = this.xs - this.ys + xseed;
      setCurrPoint(x1, y1);
    }

  }

  public class Julia3Iterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      double x2 = this.xs * x1 - 3.0 * x1 * this.ys + xseed;
      y1 = 3.0 * this.xs * y1 - this.ys * y1 + yseed;
      x1 = x2;
      setCurrPoint(x1, y1);
    }

  }

  public class Julia4Iterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      double x2 = this.xs * this.xs + this.ys * this.ys - 6.0 * this.xs * this.ys + xseed;
      y1 = 4.0 * x1 * y1 * (this.xs - this.ys) + yseed;
      x1 = x2;
      setCurrPoint(x1, y1);
    }

  }

  public class JuliaNIterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      double x2 = x1 * (this.xs * this.xs - 10.0 * this.xs * this.ys + 5.0 * this.ys * this.ys);
      double y2 = y1 * (this.ys * this.ys - 10.0 * this.xs * this.ys + 5.0 * this.xs * this.xs);
      for (int k = 5; k < power; k++) {
        double xa = x1 * x2 - y1 * y2;
        double ya = x1 * y2 + x2 * y1;
        x2 = xa;
        y2 = ya;
      }
      x1 = x2 + xseed;
      y1 = y2 + yseed;
      setCurrPoint(x1, y1);
    }

  }

  private Julia2Iterator iterator2 = new Julia2Iterator();
  private Julia3Iterator iterator3 = new Julia3Iterator();
  private Julia4Iterator iterator4 = new Julia4Iterator();
  private JuliaNIterator iteratorN = new JuliaNIterator();
  private Iterator iterator;

  @Override
  protected Iterator getIterator() {
    return iterator;
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    switch (power) {
      case 2:
        iterator = iterator2;
        break;
      case 3:
        iterator = iterator3;
        break;
      case 4:
        iterator = iterator4;
        break;
      default:
        iterator = iteratorN;
        break;
    }
  }
}
