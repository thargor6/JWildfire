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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PrimitivesWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHAPE = "shape";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_FILLED = "filled";
  private static final String[] paramNames = {PARAM_SHAPE, PARAM_A, PARAM_B, PARAM_C, PARAM_FILLED};

  private static final int SHAPE_DISC = 0;
  private static final int SHAPE_SPHERE = 1;
  private static final int SHAPE_CUBE = 2;
  private static final int SHAPE_SQUARE = 3;
  private static final int SHAPE_TRIANGLE = 4;
  private static final int SHAPE_PYRAMID = 5;
  private static final int SHAPE_TORUS = 6;

  private int shape = SHAPE_SPHERE;
  private double a = 1.0;
  private double b = 1.0;
  private double c = 1.0;
  private int filled = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    switch (shape) {
      case SHAPE_DISC:
        createDisc(pContext, pXForm, pAffineTP, pVarTP, pAmount);
        break;
      case SHAPE_CUBE:
        createCube(pContext, pXForm, pAffineTP, pVarTP, pAmount);
        break;
      case SHAPE_SQUARE:
        createSquare(pContext, pXForm, pAffineTP, pVarTP, pAmount);
        break;
      case SHAPE_TRIANGLE:
        createTriangle(pContext, pXForm, pAffineTP, pVarTP, pAmount);
        break;
      case SHAPE_PYRAMID:
        createPyramid(pContext, pXForm, pAffineTP, pVarTP, pAmount);
        break;
      case SHAPE_TORUS:
        createTorus(pContext, pXForm, pAffineTP, pVarTP, pAmount);
        break;
      case SHAPE_SPHERE:
      default:
        createSphere(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    }
  }

  private void createSquare(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double size = pAmount;
    double dx = 0.0, dy = 0.0;

    if (filled == 1 && pContext.random() < 0.5) {
      dx = size * (pContext.random() - 0.5);
      dy = size * (pContext.random() - 0.5);
    } else {
      switch (pContext.random(2)) {
        case 0:
          boolean left = pContext.random() < 0.5;
          if (filled == 0 || pContext.random() < 0.05) {
            dx = size * (left ? -0.5 : 0.5);
          } else {
            dx = size * (pContext.random() - 0.5);
          }
          dy = size * (pContext.random() - 0.5);
          break;
        case 1:
          boolean top = pContext.random() < 0.5;
          dx = size * (pContext.random() - 0.5);
          if (filled == 0 || pContext.random() < 0.05) {
            dy = size * (top ? -0.5 : 0.5);
          } else {
            dy = size * (pContext.random() - 0.5);
          }
          break;
        default: // nothing to do
          break;
      }
    }
    pVarTP.x += dx;
    pVarTP.y += dy;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  private boolean insideUnitTriangle(double pX, double pY) {
    // Compute vectors
    //    v0 = C - A
    //    v1 = B - A
    //    v2 = P - A
    double v0x = 0;
    double v0y = 1;
    double v1x = 1;
    double v1y = 0;
    double v2x = pX;
    double v2y = pY;
    // Compute dot products
    double dot00 = v0x * v0x + v0y * v0y;
    double dot01 = v0x * v1x + v0y * v1y;
    double dot02 = v0x * v2x + v0y * v2y;
    double dot11 = v1x * v1x + v1y * v1y;
    double dot12 = v1x * v2x + v1y * v2y;
    // Compute barycentric coordinates
    double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
    // Check if point is in triangle
    return (u >= 0) && (v >= 0) && (u + v < 1);
  }

  private void createTriangle(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double dx = 0.0, dy = 0.0;
    while (true) {
      dx = pContext.random();
      dy = pContext.random();
      if (insideUnitTriangle(dx, dy)) {
        break;
      }
    }
    pVarTP.x += pContext.random() < 0.5 ? dx : -dx;
    pVarTP.y += -dy;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  private void createCube(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double size = pAmount;
    double dx = 0.0, dy = 0.0, dz = 0.0;

    switch (pContext.random(3)) {
      case 0:
        boolean left = pContext.random() < 0.5;
        if (filled == 0 || pContext.random() < 0.05) {
          dx = size * (left ? -0.5 : 0.5);
        } else {
          dx = size * (pContext.random() - 0.5);
        }
        dy = size * (pContext.random() - 0.5);
        dz = size * (pContext.random() - 0.5);
        break;
      case 1:
        boolean top = pContext.random() < 0.5;
        dx = size * (pContext.random() - 0.5);
        if (filled == 0 || pContext.random() < 0.05) {
          dy = size * (top ? -0.5 : 0.5);
        } else {
          dy = size * (pContext.random() - 0.5);
        }
        dz = size * (pContext.random() - 0.5);
        break;
      case 2:
        boolean front = pContext.random() < 0.5;
        dx = size * (pContext.random() - 0.5);
        dy = size * (pContext.random() - 0.5);
        if (filled == 0 || pContext.random() < 0.05) {
          dz = size * (front ? -0.5 : 0.5);
        } else {
          dz = size * (pContext.random() - 0.5);
        }
        break;
      default: // nothing to do
        break;
    }
    pVarTP.x += dx;
    pVarTP.y += dy;
    pVarTP.z += dz;
  }

  private static final double P_SINA = sqrt(3.0) / 2.0;
  private static final double P_COSA = 0.5;

  private void createPyramid(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double size = pAmount;
    double dx = 0.0, dy = 0.0, dz = 0.0;
    int side = pContext.random(5);
    switch (side) {
      case 0:
        dx = size * (pContext.random() - 0.5);
        dy = size * 0.5;
        dz = size * (pContext.random() - 0.5);
        break;
      default:
        double tx = 0.0,
                ty = 0.0;
        while (true) {
          tx = pContext.random();
          ty = pContext.random();
          if (insideUnitTriangle(tx, ty)) {
            break;
          }
        }
        tx *= size;
        ty *= size;

        switch (side) {
          case 1:
            dx = -size * 0.5 + P_COSA * ty;
            dy = size * 0.5 - P_SINA * ty;
            dz = (pContext.random() < 0.5 ? tx : -tx) * 0.5;
            break;
          case 2:
            dx = size * 0.5 - P_COSA * ty;
            dy = 0.5 * size - P_SINA * ty;
            dz = (pContext.random() < 0.5 ? tx : -tx) * 0.5;
            break;
          case 3:
            dx = (pContext.random() < 0.5 ? tx : -tx) * 0.5;
            dy = 0.5 * size - P_SINA * ty;
            dz = -size * 0.5 + P_COSA * ty;
            break;
          case 4:
            dx = (pContext.random() < 0.5 ? tx : -tx) * 0.5;
            dy = 0.5 * size - P_SINA * ty;
            dz = size * 0.5 - P_COSA * ty;
            break;
          default: // nothing to do
            break;
        }
    }
    pVarTP.x += dx;
    pVarTP.y += dy;
    pVarTP.z += dz;
  }

  private void createSphere(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double radius = filled == 0 ? pAmount : pContext.random() < 0.05 ? pAmount : pAmount * pContext.random();

    double a = pContext.random() * 2 * M_PI;
    double sina = sin(a);
    double cosa = cos(a);

    double b = pContext.random() * 2 * M_PI;
    double sinb = sin(b);
    double cosb = cos(b);

    pVarTP.x += radius * sina * cosb;
    pVarTP.y += radius * sina * sinb;
    pVarTP.z += radius * cosa;
  }

  private void createDisc(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double radius = filled == 0 ? pAmount : pContext.random() < 0.05 ? pAmount : pAmount * pContext.random();
    double a = pContext.random() * 2 * M_PI;
    double sina = sin(a);
    double cosa = cos(a);

    pVarTP.x += radius * cosa;
    pVarTP.y += radius * sina;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  private void createTorus(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double radius = filled == 0 ? a : pContext.random() < 0.05 ? a : a * pContext.random();
    double alpha = pContext.random() * 2 * M_PI;

    double x = radius * cos(alpha);
    double y = radius * sin(alpha);

    double beta = pContext.random() * 2 * M_PI;

    pVarTP.y += x * pAmount;
    pVarTP.x += (y + b + b) * sin(beta) * pAmount;
    pVarTP.z += (y + b + b) * cos(beta) * pAmount;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{shape, a, b, c, filled};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHAPE.equalsIgnoreCase(pName))
      shape = Tools.FTOI(pValue);
    else if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_FILLED.equalsIgnoreCase(pName))
      filled = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "primitives_wf";
  }

}
