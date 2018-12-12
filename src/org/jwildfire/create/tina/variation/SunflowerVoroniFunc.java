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
import csk.taprats.geometry.Point;
import csk.taprats.geometry.Triangle;
import csk.taprats.geometry.Triangulate;
import megamu.mesh.MPolygon;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.plot.DrawFunc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.jwildfire.base.mathlib.MathLib.fmod;


/**
 * @author Jesus Sosa
 * @date jul 06, 2018
 * inspired from a program in R language from this site
 * https://fronkonstin.com/2018/03/11/mandalas-colored/
 */

public class SunflowerVoroniFunc extends DrawFunc {

  private static final long serialVersionUID = 1L;

  private double line_thickness;


  private static final String PARAM_NPOINTS = "nPoints";
  private static final String PARAM_ITERS = "Iters";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_COLORMODE = "color mode";
  private static final String PARAM_OUTLINE = "outline";
  private static final String PARAM_FILL = "fill";
  private static final String PARAM_OUTLINECOLOR = "outline color";

  private static final String[] paramNames = {PARAM_NPOINTS, PARAM_ITERS, PARAM_ANGLE, PARAM_COLORMODE, PARAM_OUTLINE, PARAM_FILL, PARAM_OUTLINECOLOR};


  private int nPoints = 50;
  private int Iters = 3;
  private double angle = 180.0;
  private int colormode = 0;
  private int outline = 0;
  private int fill = 1;
  private double outlinecolor = 0.5;

  private List vertices = null;

  float[][] points = null;


  megamu.mesh.Voronoi voroni = null;

  public void build_with_mesh() {

    double ang = angle * (3.0 - Math.sqrt(5.0));
    double rmax = Math.sqrt((double) (nPoints + 1)) / 30.0;
    points = new float[nPoints][2];
    for (int i = 0; i < nPoints; i++) {
      double r = Math.sqrt((double) (i + 1)) / 30.0;
      double t = (double) (i + 1) * ang * Math.PI / 180.0;
      double x = r * Math.cos(t) / rmax;
      double y = r * Math.sin(t) / rmax;
      points[i][0] = (float) x;
      points[i][1] = (float) y;
      double sc = (1.0 - r / rmax);
    }


    voroni = new megamu.mesh.Voronoi(points);
    float[][] myEdges = voroni.getEdges();

    if (outline == 1) {
      for (int i = 0; i < myEdges.length; i++) {
        float startX = myEdges[i][0];
        float startY = myEdges[i][1];
        float endX = myEdges[i][2];
        float endY = myEdges[i][3];

        primitives.add(new Line(startX, startY, endX, endY, outlinecolor));
      }
    }

    if (fill == 1) {
      MPolygon[] myRegions = voroni.getRegions();
      double color = 0;
      ;


      for (int i = 0; i < myRegions.length; i++) {
        // an array of points
        float[][] regionCoordinates = myRegions[i].getCoords();

        ArrayList<Point> a = new ArrayList<Point>();
        for (int j = 0; j < regionCoordinates.length; j++) {
          a.add(new Point(regionCoordinates[j][0], regionCoordinates[j][1]));
        }
        double area = Triangulate.Area(a);

        Random randomize = new Random((long) area);

        if (colormode == 0)
          color = fmod(1.0 + area, 1.0);
        else if (colormode == 1)
          color = (double) i / (double) myRegions.length;


        ArrayList<Point> result = new ArrayList<Point>();

        //  Invoke the triangulator to triangulate this polygon.
        Triangulate.Process(a, result);
        int tcount = result.size() / 3;

        for (int j = 0; j < tcount; j++) {
          final Point p1 = result.get(j * 3 + 0);
          final Point p2 = result.get(j * 3 + 1);
          final Point p3 = result.get(j * 3 + 2);
          if (colormode == 2)
            color = randomize.nextDouble();
          primitives.add(new Triangle(p1, p2, p3, color));
        }
      }
    }
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    line_thickness = 0.5 / 100;
    if (outline == 0 && fill == 0)
      outline = 1;
//   build_pattern();
    build_with_mesh();
  }


  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{nPoints, Iters, angle, colormode, outline, fill, outlinecolor};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_NPOINTS.equalsIgnoreCase(pName))
      nPoints = (int) Tools.limitValue(pValue, 10, 1000);
    else if (PARAM_ITERS.equalsIgnoreCase(pName))
      Iters = (int) Tools.limitValue(pValue, 2, 4);
    else if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = Tools.limitValue(pValue, 0.0, 360.0);
    else if (PARAM_COLORMODE.equalsIgnoreCase(pName))
      colormode = (int) Tools.limitValue(pValue, 0, 2);
    else if (PARAM_OUTLINE.equalsIgnoreCase(pName))
      outline = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_FILL.equalsIgnoreCase(pName))
      fill = (int) Tools.limitValue(pValue, 0, 1);
    else if (PARAM_OUTLINECOLOR.equalsIgnoreCase(pName))
      outlinecolor = Tools.limitValue(pValue, 0.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  public String getName() {
    return "sunvoroni";
  }

}
