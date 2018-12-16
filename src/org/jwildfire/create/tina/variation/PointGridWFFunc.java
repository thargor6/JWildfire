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
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;

public class PointGridWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_XCOUNT = "xcount";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_YCOUNT = "ycount";
  private static final String PARAM_DISTORTION = "distortion";
  private static final String PARAM_SEED = "seed";

  private static final String[] paramNames = {PARAM_XMIN, PARAM_XMAX, PARAM_XCOUNT, PARAM_YMIN, PARAM_YMAX, PARAM_YCOUNT, PARAM_DISTORTION, PARAM_SEED};

  private double xmin = -3.0;
  private double xmax = 3.0;
  private int xcount = 32;
  private double ymin = -3.0;
  private double ymax = 3.0;
  private int ycount = 32;
  private double distortion = 2.3;
  private int seed = 1234;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pointgrid_wf by Andreas Maschke
    int xIdx = pContext.random(xcount);
    int yIdx = pContext.random(ycount);
    double x = xmin + _dx * xIdx;
    double y = ymin + _dy * yIdx;
    if (distortion > 0) {
      long xseed = (seed + 1563) * xIdx + yIdx;
      _shapeRandGen.randomize(xseed);
      double distx = (0.5 - _shapeRandGen.random()) * distortion;
      long yseed = (seed + 6715) * yIdx + xIdx;
      _shapeRandGen.randomize(yseed);
      double disty = (0.5 - _shapeRandGen.random()) * distortion;
      x += distx;
      y += disty;
    }
    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{xmin, xmax, xcount, ymin, ymax, ycount, distortion, seed};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XMIN.equalsIgnoreCase(pName))
      xmin = pValue;
    else if (PARAM_XMAX.equalsIgnoreCase(pName))
      xmax = pValue;
    else if (PARAM_XCOUNT.equalsIgnoreCase(pName))
      xcount = Tools.FTOI(pValue);
    else if (PARAM_YMIN.equalsIgnoreCase(pName))
      ymin = pValue;
    else if (PARAM_YMAX.equalsIgnoreCase(pName))
      ymax = pValue;
    else if (PARAM_YCOUNT.equalsIgnoreCase(pName))
      ycount = Tools.FTOI(pValue);
    else if (PARAM_DISTORTION.equalsIgnoreCase(pName))
      distortion = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pointgrid_wf";
  }

  private AbstractRandomGenerator _shapeRandGen;
  private double _dx, _dy;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _shapeRandGen = new MarsagliaRandomGenerator();
    _dx = (xmax - xmin) / (double) xcount;
    _dy = (ymax - ymin) / (double) ycount;
  }

}
