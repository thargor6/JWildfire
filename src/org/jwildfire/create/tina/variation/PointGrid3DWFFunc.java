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

public class PointGrid3DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XMIN = "xmin";
  private static final String PARAM_XMAX = "xmax";
  private static final String PARAM_XCOUNT = "xcount";
  private static final String PARAM_YMIN = "ymin";
  private static final String PARAM_YMAX = "ymax";
  private static final String PARAM_YCOUNT = "ycount";
  private static final String PARAM_ZMIN = "zmin";
  private static final String PARAM_ZMAX = "zmax";
  private static final String PARAM_ZCOUNT = "zcount";
  private static final String PARAM_DISTORTION = "distortion";
  private static final String PARAM_SEED = "seed";

  private static final String[] paramNames = {PARAM_XMIN, PARAM_XMAX, PARAM_XCOUNT, PARAM_YMIN, PARAM_YMAX, PARAM_YCOUNT, PARAM_ZMIN, PARAM_ZMAX, PARAM_ZCOUNT, PARAM_DISTORTION, PARAM_SEED};

  private double xmin = -3.0;
  private double xmax = 3.0;
  private int xcount = 10;
  private double ymin = -3.0;
  private double ymax = 3.0;
  private int ycount = 10;
  private double zmin = -1.0;
  private double zmax = 1.0;
  private int zcount = 10;
  private double distortion = 2.3;
  private int seed = 1234;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pointgrid3d_wf by Andreas Maschke
    int xIdx = pContext.random(xcount);
    int yIdx = pContext.random(ycount);
    int zIdx = pContext.random(zcount);
    double x = xmin + _dx * xIdx;
    double y = ymin + _dy * yIdx;
    double z = zmin + _dz * zIdx;
    if (distortion > 0) {
      long xseed = (seed + 1563) * xIdx + zIdx;
      _shapeRandGen.randomize(xseed);
      double distx = (0.5 - _shapeRandGen.random()) * distortion;
      long yseed = (seed + 6715) * yIdx + xIdx;
      _shapeRandGen.randomize(yseed);
      double disty = (0.5 - _shapeRandGen.random()) * distortion;
      long zseed = (seed + 4761) * zIdx + yIdx;
      _shapeRandGen.randomize(zseed);
      double distz = (0.5 - _shapeRandGen.random()) * distortion;
      x += distx;
      y += disty;
      z += distz;
    }
    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;
    pVarTP.z += z * pAmount;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{xmin, xmax, xcount, ymin, ymax, ycount, zmin, zmax, zcount, distortion, seed};
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
    else if (PARAM_ZMIN.equalsIgnoreCase(pName))
      zmin = pValue;
    else if (PARAM_ZMAX.equalsIgnoreCase(pName))
      zmax = pValue;
    else if (PARAM_ZCOUNT.equalsIgnoreCase(pName))
      zcount = Tools.FTOI(pValue);
    else if (PARAM_DISTORTION.equalsIgnoreCase(pName))
      distortion = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pointgrid3d_wf";
  }

  private AbstractRandomGenerator _shapeRandGen;
  private double _dx, _dy, _dz;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _shapeRandGen = new MarsagliaRandomGenerator();
    _dx = (xmax - xmin) / (double) xcount;
    _dy = (ymax - ymin) / (double) ycount;
    _dz = (zmax - zmin) / (double) zcount;
  }

}
