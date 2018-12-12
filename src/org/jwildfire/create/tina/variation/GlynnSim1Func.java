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

public class GlynnSim1Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_RADIUS1 = "radius1";
  private static final String PARAM_PHI1 = "phi1";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_POW = "pow";
  private static final String PARAM_CONTRAST = "contrast";

  private static final String[] paramNames = {PARAM_RADIUS, PARAM_RADIUS1, PARAM_PHI1, PARAM_THICKNESS, PARAM_POW, PARAM_CONTRAST};

  private double radius = 1.0;
  private double radius1 = 0.1;
  private double phi1 = 110.0;
  private double thickness = 0.1;
  private double pow = 1.5;
  private double contrast = 0.5;

  private static class Point implements Serializable {
    private static final long serialVersionUID = 1L;
    private double x, y;
  }

  private void circle(FlameTransformationContext pContext, Point p) {
    double r = this.radius1 * (this.thickness + (1.0 - this.thickness) * pContext.random());
    double Phi = 2.0 * M_PI * pContext.random();
    double sinPhi = sin(Phi);
    double cosPhi = cos(Phi);
    p.x = r * cosPhi + this._x1;
    p.y = r * sinPhi + this._y1;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* GlynnSim1 by eralex61, http://eralex61.deviantart.com/art/GlynnSim-plugin-112621621 */
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double Alpha = this.radius / r;
    if (r < this.radius) { //object generation
      circle(pContext, toolPoint);
      pVarTP.x += pAmount * toolPoint.x;
      pVarTP.y += pAmount * toolPoint.y;
    } else {
      if (pContext.random() > this.contrast * pow(Alpha, this._absPow)) {
        toolPoint.x = pAffineTP.x;
        toolPoint.y = pAffineTP.y;
      } else {
        toolPoint.x = Alpha * Alpha * pAffineTP.x;
        toolPoint.y = Alpha * Alpha * pAffineTP.y;
      }
      double Z = sqr(toolPoint.x - this._x1) + sqr(toolPoint.y - this._y1);
      if (Z < this.radius1 * this.radius1) { //object generation
        circle(pContext, toolPoint);
        pVarTP.x += pAmount * toolPoint.x;
        pVarTP.y += pAmount * toolPoint.y;
      } else {
        pVarTP.x += pAmount * toolPoint.x;
        pVarTP.y += pAmount * toolPoint.y;
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
    return new Object[]{radius, radius1, phi1, thickness, pow, contrast};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_RADIUS1.equalsIgnoreCase(pName))
      radius1 = pValue;
    else if (PARAM_PHI1.equalsIgnoreCase(pName))
      phi1 = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_POW.equalsIgnoreCase(pName))
      pow = pValue;
    else if (PARAM_CONTRAST.equalsIgnoreCase(pName))
      contrast = limitVal(pValue, 0.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "glynnSim1";
  }

  private double _x1, _y1, _absPow;
  private Point toolPoint = new Point();

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double a = M_PI * phi1 / 180.0;
    double sinPhi1 = sin(a);
    double cosPhi1 = cos(a);
    this._x1 = this.radius * cosPhi1;
    this._y1 = this.radius * sinPhi1;
    this._absPow = fabs(this.pow);
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"GlynnSim1_radius", "GlynnSim1_radius1", "GlynnSim1_Phi1", "GlynnSim1_thickness", "GlynnSim1_pow", "GlynnSim1_contrast"};
  }

}
