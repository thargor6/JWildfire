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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.fmod;

public class DCTriangleFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCATTER_AREA = "scatter_area";
  private static final String PARAM_ZERO_EDGES = "zero_edges";

  private static final String[] paramNames = {PARAM_SCATTER_AREA, PARAM_ZERO_EDGES};

  private double scatter_area = 0.0;
  private int zero_edges = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* dc_triangle by Xyrus02, http://apophysis-7x.org/extensions */
    // set up triangle
    double xx = pXForm.getXYCoeff00(), xy = pXForm.getXYCoeff10(); // X
    double yx = pXForm.getXYCoeff01() * -1, yy = pXForm.getXYCoeff11() * -1; // Y
    double ox = pXForm.getXYCoeff20(), oy = pXForm.getXYCoeff21(); // O
    double px = pAffineTP.x - ox, py = pAffineTP.y - oy; // P

    // calculate dot products
    double dot00 = xx * xx + xy * xy; // X * X
    double dot01 = xx * yx + xy * yy; // X * Y
    double dot02 = xx * px + xy * py; // X * P
    double dot11 = yx * yx + yy * yy; // Y * Y
    double dot12 = yx * px + yy * py; // Y * P

    // calculate barycentric coordinates
    double denom = (dot00 * dot11 - dot01 * dot01);
    double num_u = (dot11 * dot02 - dot01 * dot12);
    double num_v = (dot00 * dot12 - dot01 * dot02);

    // u, v must not be constant
    double u = num_u / denom;
    double v = num_v / denom;
    int inside = 0, f = 1;

    // case A - point escapes edge XY
    if (u + v > 1) {
      f = -1;
      if (u > v) {
        u = u > 1 ? 1 : u;
        v = 1 - u;
      } else {
        v = v > 1 ? 1 : v;
        u = 1 - v;
      }
    }

    // case B - point escapes either edge OX or OY
    else if ((u < 0) || (v < 0)) {
      u = u < 0 ? 0 : u > 1 ? 1 : u;
      v = v < 0 ? 0 : v > 1 ? 1 : v;
    }

    // case C - point is in triangle
    else
      inside = 1;

    // handle outside points
    if (zero_edges == 1 && inside == 0)
      u = v = 0;
    else if (inside != 0) {
      u = (u + pContext.random() * A * f);
      v = (v + pContext.random() * A * f);
      u = u < -1 ? -1 : u > 1 ? 1 : u;
      v = v < -1 ? -1 : v > 1 ? 1 : v;

      if ((u + v > 1) && (A > 0))
        if (u > v) {
          u = u > 1 ? 1 : u;
          v = 1 - u;
        } else {
          v = v > 1 ? 1 : v;
          u = 1 - v;
        }
    }

    // set output
    pVarTP.x += pAmount * (ox + u * xx + v * yx);
    pVarTP.y += pAmount * (oy + u * xy + v * yy);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
    pVarTP.color = fmod(fabs(u + v), 1.0);
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{scatter_area, zero_edges};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCATTER_AREA.equalsIgnoreCase(pName))
      scatter_area = pValue;
    else if (PARAM_ZERO_EDGES.equalsIgnoreCase(pName))
      zero_edges = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_triangle";
  }

  private double A;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    A = scatter_area < -1 ? -1 : scatter_area > 1 ? 1 : scatter_area;
  }

}
