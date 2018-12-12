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

public class FractMandelbrotWFFunc extends AbstractFractWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";

  private int power;

  @Override
  public String getName() {
    return "fract_mandelbrot_wf";
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

  public class Mandelbrot2Iterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;

      y1 = 2.0 * x1 * y1 + this.startY;
      x1 = (this.xs - this.ys) + this.startX;

      setCurrPoint(x1, y1);
    }

  }

  public class Mandelbrot3Iterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;

      double x2 = this.xs * x1 - 3.0 * x1 * this.ys + this.startX;
      y1 = 3.0 * this.xs * y1 - this.ys * y1 + this.startY;
      x1 = x2;
      setCurrPoint(x1, y1);
    }
  }

  public class Mandelbrot4Iterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      double x2 = this.xs * this.xs + this.ys * this.ys - 6.0 * this.xs * this.ys + this.startX;
      y1 = 4.0 * x1 * y1 * (this.xs - this.ys) + this.startY;
      x1 = x2;
      setCurrPoint(x1, y1);
    }
  }

  public class MandelbrotNIterator extends Iterator {
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
      x1 = x2 + this.startX;
      y1 = y2 + this.startY;
      setCurrPoint(x1, y1);
    }

  }

  private Mandelbrot2Iterator iterator2 = new Mandelbrot2Iterator();
  private Mandelbrot3Iterator iterator3 = new Mandelbrot3Iterator();
  private Mandelbrot4Iterator iterator4 = new Mandelbrot4Iterator();
  private MandelbrotNIterator iteratorN = new MandelbrotNIterator();
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
