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


import csk.taprats.geometry.Ngon;
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Primitive;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.plot.DrawFunc;


/**
 * @author Jesus Sosa
 * @date jul 08, 2018
 * inspired from a program in R language from this site
 * https://fronkonstin.com/2017/05/22/sunflowers-for-colourlovers/
 */

public class SunFlowersFunc extends DrawFunc {

  private static final long serialVersionUID = 1L;

  private double line_thickness;

  private static final String PARAM_NPOINTS = "nPoints";
  private static final String PARAM_SHAPE = "shape";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_COLOR = "color";
  private static final String PARAM_FILL = "F. filling";
  private static final String PARAM_INVERT = "invert";

  private static final String[] paramNames = {PARAM_NPOINTS, PARAM_SHAPE, PARAM_SCALE, PARAM_ANGLE, PARAM_COLOR, PARAM_FILL, PARAM_INVERT};


  private int nPoints = 500;
  private int shape = 10;
  private double scale = 0.02;
  private double angle = 180.0;
  private double color = 0;
  private double fill = 0.0;
  private int invert = 0;


  public void build_sunflower() {

    double ang = angle * (3.0 - Math.sqrt(5.0));
    double rmax = Math.sqrt((double) (nPoints + 1)) / 30.0;
    for (int i = 0; i < nPoints; i++) {
      double r = Math.sqrt((double) (i + 1)) / 30.0;
      double t = (double) (i + 1) * ang * Math.PI / 180.0;
      double x = r * Math.cos(t) / rmax;
      double y = r * Math.sin(t) / rmax;
      double sc = (1.0 - r / rmax);
      if (invert == 1)
        sc = r / rmax;
      //primitives.add(new Ngon(shape,scale*sc,0.0,new Point(x,y),(double)i/nPoints,fill));
      primitives.add(new Ngon(shape, scale * sc, 0.0, new Point(x, y), sc, fill));
    }
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    line_thickness = 0.5 / 100;
    build_sunflower();
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    Point out = null;

    Primitive primitive = null;
    double color = 0.0;

    primitive = getPrimitive(pContext);

    if (primitive.gettype() == 4) {
      Ngon polygon = (Ngon) primitive;
      out = plotPolygon(pContext, polygon);
      color = polygon.getColor();

      pVarTP.x += pAmount * (out.getX() * polygon.getScale() * polygon.getCosa() + out.getY() * polygon.getScale() * polygon.getSina() + polygon.getPos().getX());
      pVarTP.y += pAmount * (-out.getX() * polygon.getScale() * polygon.getSina() + out.getY() * polygon.getScale() * polygon.getCosa() + polygon.getPos().getY());
      pVarTP.color = color;

      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    }
  }


  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{nPoints, shape, scale, angle, color, fill, invert};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_NPOINTS.equalsIgnoreCase(pName))
      nPoints = (int) Tools.limitValue(pValue, 10, 1000);
    else if (PARAM_SHAPE.equalsIgnoreCase(pName))
      shape = (int) Tools.limitValue(pValue, 3, 20);
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = Tools.limitValue(pValue, 0.0, 100.0);
    else if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = Tools.limitValue(pValue, 0.0, 360.0);
    else if (PARAM_COLOR.equalsIgnoreCase(pName))
      color = Tools.limitValue(pValue, 0.0, 1.0);
    else if (PARAM_FILL.equalsIgnoreCase(pName))
      fill = Tools.limitValue(pValue, 0.0, 1.0);
    else if (PARAM_INVERT.equalsIgnoreCase(pName))
      invert = (int) Tools.limitValue(pValue, 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  public String getName() {
    return "sunflower";
  }

}
