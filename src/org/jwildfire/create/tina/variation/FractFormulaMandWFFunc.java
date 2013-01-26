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

import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;

public class FractFormulaMandWFFunc extends AbstractFractFormulaWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";

  private int power;

  @Override
  public String getName() {
    return "fract_formula_mand_wf";
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE;
  }

  @Override
  protected void initParams() {
    xmin = -2.35;
    xmax = 0.75;
    ymin = -1.2;
    ymax = 1.2;
    max_iter = 30;
    clip_iter_min = 1;
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
    }
    return false;
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
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
    super.init(pContext, pXForm, pAmount);
    prepare_formula("((z^n)+c)");
  }

  public class FormulaMandIterator extends Iterator {

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      Complex z = perform_formula(power, x1, y1, this.startX, this.startY);
      x1 = z.re;
      y1 = z.im;
      setCurrPoint(x1, y1);
    }

  }

  private FormulaMandIterator iterator = new FormulaMandIterator();

  @Override
  protected Iterator getIterator() {
    return iterator;
  }

}
