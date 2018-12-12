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


import csk.taprats.geometry.Line;
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

public class SZubietaFunc extends DrawFunc {

  private static final long serialVersionUID = 1L;

  private double line_thickness;

  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_HEIGHT = "height";
  private static final String PARAM_TYPE = "type";
  private static final String PARAM_SCALE = "scale";


  private static final String[] paramNames = {PARAM_WIDTH, PARAM_HEIGHT, PARAM_TYPE, PARAM_SCALE};


  private int width = 128;
  private int height = 128;
  private int type = 0;
  private double scale = 0.5;


  public void build_pattern2() {  // Square Tile Fractal pattern
    // http://paulbourke.net/fractals/squaretile/index.html
    for (int i = 0; i < width; ++i) {
      for (int j = 0; j < height; ++j) {
        int x = i & (j - 2 * (i ^ j) + j) & i;
        x %= 256;
        x = Math.abs(x);
        double color = (double) x / 255.0;
        double x1 = (double) i - width / 2.0;
        double y1 = (double) j - height / 2.0;
        primitives.add(new Ngon(20, scale, 0.0, new Point(x1, y1), color, 0.0));
      }
    }
  }

  public void build_pattern1() { // CircleSquares pattern
    // http://paulbourke.net/fractals/circlesquares/index.html
    for (int i = 0; i < width; ++i) {
      for (int j = 0; j < height; ++j) {
        int x = i * i - 2 * (i | j) + j * j;
        x %= 255;
        x = Math.abs(x);
        // g.setColor(new Color(x,x,x));
        double color = (double) x / 255.0;
        double x1 = (double) i - width / 2.0;
        double y1 = (double) j - height / 2.0;
        primitives.add(new Ngon(20, scale, 0.0, new Point(x1, y1), color, 0.0));
      }
    }
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    line_thickness = 0.5 / 100;
    if (type == 0)
      build_pattern1();
    else
      build_pattern2();
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    Point out = null;

    Primitive primitive = null;
    double color = 0.0;

    primitive = getPrimitive(pContext);

    if (primitive.gettype() == 2) {
      Line line = (Line) primitive;
      out = plotLine(pContext, line.getX1(), line.getY1(), line.getX2(), line.getY2());
      color = line.getColor();

      pVarTP.x += pAmount * out.getX();
      pVarTP.y += pAmount * out.getY();
      pVarTP.color = color;
      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    }
    if (primitive.gettype() == 4) {
      Ngon polygon = (Ngon) primitive;
      out = plotPolygon(pContext, polygon);
      color = polygon.getColor();

      pVarTP.x += pAmount * 0.1 * (out.getX() * polygon.getScale() * polygon.getCosa() + out.getY() * polygon.getScale() * polygon.getSina() + polygon.getPos().getX());
      pVarTP.y += pAmount * 0.1 * (-out.getX() * polygon.getScale() * polygon.getSina() + out.getY() * polygon.getScale() * polygon.getCosa() + polygon.getPos().getY());
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
    return new Object[]{width, height, type, scale};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_WIDTH.equalsIgnoreCase(pName))
      width = (int) Tools.limitValue(pValue, 32, 256);
    else if (PARAM_HEIGHT.equalsIgnoreCase(pName))
      height = (int) Tools.limitValue(pValue, 32, 256);
    else if (PARAM_TYPE.equalsIgnoreCase(pName))
      type = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = Tools.limitValue(pValue, 0.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  public String getName() {
    return "szubieta";
  }

}
