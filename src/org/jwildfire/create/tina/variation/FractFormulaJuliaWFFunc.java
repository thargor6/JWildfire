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

public class FractFormulaJuliaWFFunc extends AbstractFractFormulaWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_XSEED = "xseed";
  private static final String PARAM_YSEED = "yseed";
  private static final String RESSOURCE_FORMULA = "formula";

  private static final String[] ressourceNames = {RESSOURCE_FORMULA};

  private int power;
  private double xseed;
  private double yseed;
  private String formula = "((z^n)+c)";

  @Override
  public String getName() {
    return "fract_formula_julia_wf";
  }

  @Override
  protected void initParams() {
    xmin = -1.4;
    xmax = 1.4;
    ymin = -1.4;
    ymax = 1.4;
    clip_iter_min = 1;
    max_iter = 30;
    scale = 3.8;
    power = 3;
    xseed = 0.1;
    yseed = 0.7;
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

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    prepare_formula(formula);
  }

  public class FormulaJuliaIterator extends Iterator {
    private static final long serialVersionUID = 1L;

    @Override
    protected void nextIteration() {
      double x1 = this.currX;
      double y1 = this.currY;
      this.xs = x1 * x1;
      this.ys = y1 * y1;
      Complex z = perform_formula(power, x1, y1, xseed, yseed);
      x1 = z.re;
      y1 = z.im;
      setCurrPoint(x1, y1);
    }

  }

  private FormulaJuliaIterator iterator = new FormulaJuliaIterator();

  @Override
  protected Iterator getIterator() {
    return iterator;
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(formula != null ? formula.getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      formula = pValue != null ? new String(pValue) : "";
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_FORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else
      throw new IllegalArgumentException(pName);
  }

}
