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

import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.sinAndCos;

public class DLAWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_BUFFER_SIZE = "buffer_size";
  private static final String PARAM_MAX_ITER = "max_iter";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_JITTER = "jitter";
  private static final String[] paramNames = {PARAM_BUFFER_SIZE, PARAM_MAX_ITER, PARAM_SEED, PARAM_SCALE, PARAM_JITTER};

  private int buffer_size = 800;
  private int max_iter = 6000;
  private int seed = 666;
  private double scale = 10.0;
  private double jitter = 0.01;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    Point point = getRandomPoint();
    pVarTP.x += pAmount * point.x;
    pVarTP.y += pAmount * point.y;
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
    return new Object[]{buffer_size, max_iter, seed, scale, jitter};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_BUFFER_SIZE.equalsIgnoreCase(pName))
      buffer_size = Tools.FTOI(pValue);
    else if (PARAM_MAX_ITER.equalsIgnoreCase(pName))
      max_iter = Tools.FTOI(pValue);
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = Tools.FTOI(pValue);
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_JITTER.equalsIgnoreCase(pName))
      jitter = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dla_wf";
  }

  private String makeKey() {
    return String.valueOf(buffer_size) + "#" + String.valueOf(_max_iter) + "#" + String.valueOf(seed) + "#" + String.valueOf(scale) + "#" + String.valueOf(jitter);
  }

  private static class Point {
    double x, y;
  }

  private static final Point ZERO = new Point();

  private static Map<String, List<Point>> cache = new HashMap<String, List<Point>>();

  private DoubleWrapperWF sina = new DoubleWrapperWF();
  private DoubleWrapperWF cosa = new DoubleWrapperWF();
  private List<Point> _points;

  private List<Point> getPoints() {
    double jitterRadius = Math.max(Math.min(1.0, jitter), 0.0);

    String key = makeKey();
    List<Point> res = cache.get(key);
    if (res == null) {
      AbstractRandomGenerator randGen = new MarsagliaRandomGenerator();
      randGen.randomize(seed);
      short[][] points = calculate();
      res = new ArrayList<Point>();
      for (int i = 0; i < points.length; i++) {
        for (int j = 0; j < points[i].length; j++) {
          double aRnd;
          // Always calculate random jitter at each cell to ensure to have always the same result, even if more cells get populated.
          // This way the growth may be animated by just increasig the max_iter param. 
          if (jitterRadius > MathLib.EPSILON) {
            aRnd = randGen.random();
          } else {
            aRnd = 0.0;
          }
          if (points[i][j] != 0) {
            Point point = new Point();
            point.x = (double) (i - buffer_size / 2) / (double) buffer_size * scale;
            point.y = (double) (j - buffer_size / 2) / (double) buffer_size * scale;
            if (jitterRadius > MathLib.EPSILON) {
              double alpha = aRnd * 2 * M_PI;
              sinAndCos(alpha, sina, cosa);
              point.x += jitterRadius * cosa.value;
              point.y += jitterRadius * sina.value;
            }
            res.add(point);
          }

        }
      }
      cache.put(key, res);
    }
    return res;
  }

  private Point getRandomPoint() {
    if (_points.size() > 0) {
      return _points.get((int) (Math.random() * _points.size()));
    } else
      return ZERO;
  }

  private short[][] calculate() {
    AbstractRandomGenerator randGen = new MarsagliaRandomGenerator();
    int centre = buffer_size / 2;
    double pi2 = 2.0 * Math.PI;
    int size2 = buffer_size - 2;
    short q[][] = new short[buffer_size][buffer_size];
    randGen.randomize(seed);
    q[centre][centre] = 1;
    double r1 = 3.0;
    double r2 = 3.0 * r1;
    for (int i = 0; i < _max_iter; i++) {
      double phi = pi2 * randGen.random();
      double ri = r1 * Math.cos(phi);
      double rj = r1 * Math.sin(phi);
      int ci = centre + (int) (ri + 0.5);
      int cj = centre + (int) (rj + 0.5);
      short qt = 0;
      while (qt == 0) {
        double rr = randGen.random();
        rr += rr;
        rr += rr;
        int rd = (int) rr;
        switch (rd) {
          case 0:
            ci++;
            break;
          case 1:
            cj--;
            break;
          case 2:
            ci--;
            break;
          default:
            cj++;
        }
        if ((ci < 1) || (ci > size2) || (cj < 1) || (cj > size2)) {
          qt = 1;
          i--;
        } else {
          int sum = q[ci - 1][cj] + q[ci + 1][cj] + q[ci][cj - 1] + q[ci][cj + 1];
          if (sum != 0) {
            q[ci][cj] = qt = 1;
            double r3 = (double) (ci - centre);
            double r4 = (double) (cj - centre);
            r3 *= r3;
            r4 *= r4;
            r3 += r4;
            r3 = Math.sqrt(r3);
            if (r3 > r1) {
              r1 = r3;
              r2 = 2.1 * r1;
            }
          } else {
            double r3 = (double) (ci - centre);
            double r4 = (double) (cj - centre);
            r3 *= r3;
            r4 *= r4;
            r3 += r4;
            r3 = Math.sqrt(r3);
            if (r3 > r2) {
              qt = 1;
              i--;
            }
          }
        }
      }
    }
    return q;
  }

  private int _max_iter;


  @Override
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _max_iter = pContext.isPreview() ? (max_iter < 6000) ? max_iter : 6000 : max_iter;
    // causes the points to be written to cache, happens only once
    getPoints();
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _max_iter = pContext.isPreview() ? (max_iter < 6000) ? max_iter : 6000 : max_iter;
    // points are read from cache
    _points = getPoints();
  }
}
