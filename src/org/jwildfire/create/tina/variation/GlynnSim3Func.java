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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.io.Serializable;

import static org.jwildfire.base.mathlib.MathLib.*;

public class GlynnSim3Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_THICKNESS2 = "thickness2";
  private static final String PARAM_CONTRAST = "contrast";
  private static final String PARAM_POW = "pow";

  private static final String[] paramNames = {PARAM_RADIUS, PARAM_THICKNESS, PARAM_CONTRAST, PARAM_POW};

  private double radius = 1.0;
  private double thickness = 0.1;
  private double contrast = 0.5;
  private double pow = 1.5;

  private static class Point implements Serializable {
    private static final long serialVersionUID = 1L;

    private double x, y;
  }

  private void circle2(FlameTransformationContext pContext, Point p) {
    //    double r = this.radius + this.thickness - this.Gamma * pContext.random();
    double phi = 2.0 * M_PI * pContext.random();
    double sinPhi = sin(phi);
    double cosPhi = cos(phi);
    double r;
    if (pContext.random() < this._gamma) {
      r = this._radius1;
    } else {
      r = this._radius2;
    }
    p.x = r * cosPhi;
    p.y = r * sinPhi;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* GlynnSim3 by eralex61, http://eralex61.deviantart.com/art/GlynnSim-plugin-112621621 */
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double alpha = this.radius / r;
    if (r < this._radius1) {
      circle2(pContext, toolPoint);
      pVarTP.x += pAmount * toolPoint.x;
      pVarTP.y += pAmount * toolPoint.y;
    } else {
      if (pContext.random() > this.contrast * pow(alpha, this._absPow)) {

        pVarTP.x += pAmount * pAffineTP.x;
        pVarTP.y += pAmount * pAffineTP.y;
      } else {
        pVarTP.x += pAmount * alpha * alpha * pAffineTP.x;
        pVarTP.y += pAmount * alpha * alpha * pAffineTP.y;
      }
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
    return new Object[]{radius, thickness, contrast, pow};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = pValue;
    else if (PARAM_THICKNESS2.equalsIgnoreCase(pName)) {
    }  // ignore deprecated parameter
    else if (PARAM_CONTRAST.equalsIgnoreCase(pName))
      contrast = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_POW.equalsIgnoreCase(pName))
      pow = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "glynnSim3";
  }

  private Point toolPoint = new Point();
  private double _radius1, _radius2, _gamma, _absPow;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    this._radius1 = this.radius + this.thickness;
    this._radius2 = sqr(this.radius) / this._radius1;
    this._gamma = this._radius1 / (this._radius1 + this._radius2);
    this._absPow = fabs(this.pow);
  }
}
