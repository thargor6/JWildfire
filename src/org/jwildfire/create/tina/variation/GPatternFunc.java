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


import csk.taprats.geometry.*;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.plot.DrawFunc;

import java.util.Random;

import static org.jwildfire.base.mathlib.MathLib.fmod;

/**
 * @author Jesus Sosa
 * @date May 15, 2018
 */


public class GPatternFunc extends DrawFunc {

  private static final long serialVersionUID = 1L;

  private double line_thickness;

  private static final String PARAM_STRING = "string";

  private static final String PARAM_SEED = "seed";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_HEIGHT = "height";

  private static final String PARAM_LINEHEIGHT = "lineheight";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_RANDPOS = "randPos";
  private static final String PARAM_RANDSIZE = "randSize";
  private static final String PARAM_FILL = "fill";
  private static final String PARAM_OUTLINE = "ouline";

  private static final String PARAM_COLOR = "fill color";
  private static final String PARAM_SPEEDCOLOR = "fill color speed";
  private static final String PARAM_OUTLINECOLOR = "outline color";

  private static final String RESSOURCE_STRING = "string";


  private static final String[] paramNames = {PARAM_SEED, PARAM_WIDTH, PARAM_HEIGHT,
          PARAM_LINEHEIGHT, PARAM_ANGLE, PARAM_RANDPOS, PARAM_RANDSIZE, PARAM_FILL, PARAM_OUTLINE, PARAM_COLOR, PARAM_SPEEDCOLOR, PARAM_OUTLINECOLOR};

  private static final String[] ressourceNames = {RESSOURCE_STRING};

  private int seed = 10000;
  private double width = 4.0;
  private double height = 2.0;
  private double lineheight = 0.15;
  private String string = new String("3,4,5,6,5,4,3");
  private int fill = 1;
  private double angle = 0.0;
  private double randPos = 0.0;
  private double randSize = 0.0;
  private int outline = 0;
  private double color = 0.0;
  private double speedcolor = 1.0;
  private double outlinecolor = 0.0;

  Random randomize;

  public double random(double r1, double r2) {
    return r1 + (r2 - r1) * randomize.nextDouble();
  }

  public void build_pattern() {

    int polys[];
    String p[] = null;

    p = string.split(",");
    polys = new int[p.length];
    double polyWidth = lineheight;

    randomize = new Random(seed);

    if (p.length != 0) {
      for (int k = 0; k < p.length; k++) {
        try {
          polys[k] = Integer.parseInt(p[k]);
        } catch (NumberFormatException numberFormatException) {

        }
        if (polys[k] > 20 || polys[k] < 3) {
          polys[k] = 3;
        }
      }

      for (double posj = 0; posj <= width; posj += polyWidth) {
        double y = 0;
        for (int polyCntr = 0; y <= height; polyCntr++) {
          double x = posj + random(-randPos, randPos);
          double r = (float) (polyWidth / 2) + random(-randSize, randSize);

          if (outline == 1)
            primitives.add(new Ngon(polys[polyCntr], r, angle, new Point(x, y), outlinecolor, outline));
          if (fill == 1) {
            double calccolor = fmod(color + speedcolor * (double) randomize.nextDouble(), 1.0);
            primitives.add(new Ngon(polys[polyCntr], r, angle, new Point(x, y), calccolor, 0.0));
          }
          if (polyCntr > polys.length - 2)
            polyCntr = -1;
          y += lineheight;
        }
      }
    }
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    line_thickness = 0.5 / 100;
    build_pattern();
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
    } else if (primitive.gettype() == 3) {
      Triangle triangle = (Triangle) primitive;
      out = plotTriangle(pContext, triangle);
      color = triangle.getColor();
      pVarTP.x += pAmount * out.getX();
      pVarTP.y += pAmount * out.getY();
      pVarTP.color = color;
    } else if (primitive.gettype() == 4) {
      Ngon polygon = (Ngon) primitive;
      out = plotPolygon(pContext, polygon);
      color = polygon.getColor();

      pVarTP.x += out.getX() * polygon.getScale() * polygon.getCosa() + out.getY() * polygon.getScale() * polygon.getSina() + polygon.getPos().getX();
      pVarTP.y += -out.getX() * polygon.getScale() * polygon.getSina() + out.getY() * polygon.getScale() * polygon.getCosa() + polygon.getPos().getY();
      pVarTP.color = color;
    }


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
    return new Object[]{seed, width, height, lineheight, angle, randPos, randSize, fill, outline, color, speedcolor, outlinecolor};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = (int) pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName))
      width = Tools.limitValue(pValue, 0, 10);
    else if (PARAM_HEIGHT.equalsIgnoreCase(pName))
      height = Tools.limitValue(pValue, 0, 10);
    else if (PARAM_LINEHEIGHT.equalsIgnoreCase(pName))
      lineheight = Tools.limitValue(pValue, 0.01, 1.5);
    else if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_RANDPOS.equalsIgnoreCase(pName))
      randPos = pValue;
    else if (PARAM_RANDSIZE.equalsIgnoreCase(pName))
      randSize = Tools.limitValue(pValue, -1, 1);
    else if (PARAM_FILL.equalsIgnoreCase(pName))
      fill = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_OUTLINE.equalsIgnoreCase(pName))
      outline = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_COLOR.equalsIgnoreCase(pName))
      color = Tools.limitValue(pValue, 0, 1);
    else if (PARAM_SPEEDCOLOR.equalsIgnoreCase(pName))
      speedcolor = Tools.limitValue(pValue, 0, 1);
    else if (PARAM_OUTLINECOLOR.equalsIgnoreCase(pName))
      outlinecolor = Tools.limitValue(pValue, 0.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(string != null ? string.getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_STRING.equalsIgnoreCase(pName)) {
      string = pValue != null ? new String(pValue) : "";
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_STRING.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else
      throw new IllegalArgumentException(pName);
  }

  public String getName() {
    return "gpattern";
  }

}
