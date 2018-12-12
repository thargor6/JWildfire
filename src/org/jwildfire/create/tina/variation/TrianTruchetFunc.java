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


import csk.taprats.geometry.Point;
import csk.taprats.geometry.Triangle;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.plot.DrawFunc;

import java.util.Random;

/**
 * variation "triantruchet"
 *
 * @author Jesus Sosa
 * @date August 30, 2018
 * <p>
 * Parameters
 * <p>
 * seed:       random control
 * size:  width length from 0.0 to 10.0
 * TilesPerRow: Number of tiles per row (from 2 to 600)
 * TilesPerColumn: Number of tiles per column (from 2 to 600)
 */


public class TrianTruchetFunc extends DrawFunc {

  private static final long serialVersionUID = 1L;


  private static final String PARAM_SEED = "seed";
  private static final String PARAM_SIZE = "Size";
  private static final String PARAM_TILESPERROW = "TilesPerRow";
  private static final String PARAM_TILESPERCOLUMN = "TilesPerColumn";


  private static final String RESSOURCE_STRING = "string";


  private static final String[] paramNames = {PARAM_SEED, PARAM_SIZE,
          PARAM_TILESPERROW, PARAM_TILESPERCOLUMN};

  private static final String[] ressourceNames = {RESSOURCE_STRING};

  private int seed = 10000;
  private double size = 2.0;
  private int numberTilesPerRow = 10;
  private int numberTilesPerColumn = 10;
  private String string = new String("0132");


  private int n1, n2, n3, n4;

  Random randomize;

  float tileSize = (float) size / numberTilesPerRow;
  int numberTiles = numberTilesPerRow * numberTilesPerColumn;

  static public class Tile {

    int Xindex;
    int Yindex;

    float x, y; // X-coordinate, y-coordinate center of tile
    float tilt;
    float angle;
    float tileSize;

    // Constructor
    Tile(int Xindex, int Yindex, float tileSize) {
      x = Xindex * tileSize + tileSize / 2;
      y = Yindex * tileSize + tileSize / 2;
      this.tilt = (float) 0.0;
      this.tileSize = tileSize;
    }

    public void rotate(int nveces) {
      tilt += nveces * Math.PI / 2.0;
    }

    public void change() {
      tilt += Math.PI / 2.0;
    }

    public Triangle display() {
      float x1, y1, x2, y2, x3, y3;
      float x1t, y1t, x2t, y2t, x3t, y3t;

      x1 = -tileSize / 2;
      y1 = -tileSize / 2;

      x2 = -tileSize / 2;
      y2 = tileSize / 2;

      x3 = tileSize / 2;
      y3 = tileSize / 2;

      x1t = (float) (x1 * Math.cos(tilt) + y1 * Math.sin(tilt) + x);
      y1t = (float) (-x1 * Math.sin(tilt) + y1 * Math.cos(tilt) + y);

      x2t = (float) (x2 * Math.cos(tilt) + y2 * Math.sin(tilt) + x);
      y2t = (float) (-x2 * Math.sin(tilt) + y2 * Math.cos(tilt) + y);

      x3t = (float) (x3 * Math.cos(tilt) + y3 * Math.sin(tilt) + x);
      y3t = (float) (-x3 * Math.sin(tilt) + y3 * Math.cos(tilt) + y);

      Triangle t = new Triangle(new Point(x1t, y1t), new Point(x2t, y2t), new Point(x3t, y3t));
      return t;
    }
  }

  public Point plotTriangle(FlameTransformationContext pContext, Triangle triangle) {
    Point p1 = triangle.getP1();
    Point p2 = triangle.getP2();
    Point p3 = triangle.getP3();

    // uniform sampling:  http://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
    double sqrt_r1 = MathLib.sqrt(pContext.random());
    double r2 = pContext.random();
    double a = 1.0 - sqrt_r1;
    double b = sqrt_r1 * (1.0 - r2);
    double c = r2 * sqrt_r1;
    double dx = a * p1.getX() + b * p2.getX() + c * p3.getX();
    double dy = a * p1.getY() + b * p2.getY() + c * p3.getY();

    Point out = new Point(dx, dy);
    return out;

  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

    tileSize = (float) size / numberTilesPerRow;
    numberTiles = numberTilesPerRow * numberTilesPerColumn;
    randomize = new Random(seed);
    randomize.nextDouble();
    n1 = (int) (randomize.nextDouble() * 4);
    n2 = (int) (randomize.nextDouble() * 4);
    n3 = (int) (randomize.nextDouble() * 4);
    n4 = (int) (randomize.nextDouble() * 4);
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    int i = (int) (randomize.nextDouble() * numberTilesPerRow);
    int j = (int) (randomize.nextDouble() * numberTilesPerColumn);
    Tile tile = new Tile(i, j, tileSize);
    if (seed == 0) {
      try {
        n1 = Integer.parseInt(string.substring(0, 1));
        n2 = Integer.parseInt(string.substring(1, 2));
        n3 = Integer.parseInt(string.substring(2, 3));
        n4 = Integer.parseInt(string.substring(3, 4));
      } catch (Exception e) {
      }
    }
    if (i % 2 == 0) // i odd (par)
    {
      if (j % 2 == 0)  // j par
        tile.rotate(n1);
      else  // j even
        tile.rotate(n3);
    } else // i even (impar)
    {
      if (j % 2 == 0) // j odd
        tile.rotate(n2);
      else    // j even
        tile.rotate(n4);
    }

    Triangle t = tile.display();
    double color = (numberTilesPerRow * j + i) / (double) numberTiles;

    Point dot = plotTriangle(pContext, t);

    pVarTP.x += pAmount * (dot.getX() - tileSize * numberTilesPerRow / 2.0);
    pVarTP.y += pAmount * (dot.getY() - tileSize * numberTilesPerColumn / 2.0);
    pVarTP.color = color;

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
    return new Object[]{seed, size, numberTilesPerRow, numberTilesPerColumn};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEED.equalsIgnoreCase(pName)) {
      seed = (int) pValue;
    } else if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = Tools.limitValue(pValue, 0, 10);
    else if (PARAM_TILESPERROW.equalsIgnoreCase(pName))
      numberTilesPerRow = (int) Tools.limitValue(pValue, 2, 600);
    else if (PARAM_TILESPERCOLUMN.equalsIgnoreCase(pName))
      numberTilesPerColumn = (int) Tools.limitValue(pValue, 2, 600);
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
    return "triantruchet";
  }

}
