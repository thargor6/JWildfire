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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_CUDA;
import static org.jwildfire.create.tina.base.Constants.AVAILABILITY_JWILDFIRE;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class PrimitivesWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHAPE = "shape";
  private static final String PARAM_SCALEY = "scaley";
  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";
  private static final String[] paramNames = { PARAM_SHAPE, PARAM_SCALEY, PARAM_FREQX, PARAM_FREQY };

  private static final int SHAPE_DISC = 0;
  private static final int SHAPE_SPHERE = 1;
  private static final int SHAPE_CUBE = 2;
  private static final int SHAPE_SQUARE = 3;

  private int shape = SHAPE_SPHERE;
  private double scaley = 0.5;
  private double freqx = M_PI / 2;
  private double freqy = M_PI / 4;

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
      case SHAPE_SPHERE:
      default:
        createSphere(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    }
  }

  private void createSquare(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double size = pAmount;
    double dx = 0.0, dy = 0.0;

    if (pContext.random() < 0.5) {
      dx = size * (pContext.random() - 0.5);
      dy = size * (pContext.random() - 0.5);
    }
    else {
      switch (pContext.random(2)) {
        case 0:
          boolean left = pContext.random() < 0.5;
          dx = size * (left ? -0.5 : 0.5);
          dy = size * (pContext.random() - 0.5);
          break;
        case 1:
          boolean top = pContext.random() < 0.5;
          dx = size * (pContext.random() - 0.5);
          dy = size * (top ? -0.5 : 0.5);
          break;
      }
    }
    pVarTP.x += dx;
    pVarTP.y += dy;
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
        dx = size * (left ? -0.5 : 0.5);
        dy = size * (pContext.random() - 0.5);
        dz = size * (pContext.random() - 0.5);
        break;
      case 1:
        boolean top = pContext.random() < 0.5;
        dx = size * (pContext.random() - 0.5);
        dy = size * (top ? -0.5 : 0.5);
        dz = size * (pContext.random() - 0.5);
        break;
      case 2:
        boolean front = pContext.random() < 0.5;
        dx = size * (pContext.random() - 0.5);
        dy = size * (pContext.random() - 0.5);
        dz = size * (front ? -0.5 : 0.5);
        break;
    }
    pVarTP.x += dx;
    pVarTP.y += dy;
    pVarTP.z += dz;
  }

  private void createSphere(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double radius = pAmount;

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
    double radius = pContext.random() < 0.05 ? pAmount : pAmount * pContext.random();
    double a = pContext.random() * 2 * M_PI;
    double sina = sin(a);
    double cosa = cos(a);

    pVarTP.x += radius * cosa;
    pVarTP.y += radius * sina;
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
    return new Object[] { shape, scaley, freqx, freqy };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHAPE.equalsIgnoreCase(pName))
      shape = Tools.FTOI(pValue);
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaley = pValue;
    else if (PARAM_FREQX.equalsIgnoreCase(pName))
      freqx = pValue;
    else if (PARAM_FREQY.equalsIgnoreCase(pName))
      freqy = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "primitives_wf";
  }

  @Override
  public int getAvailability() {
    return AVAILABILITY_JWILDFIRE | AVAILABILITY_CUDA;
  }

}
