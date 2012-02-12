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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class GlynnSim1Func extends VariationFunc {

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_RADIUS1 = "radius1";
  private static final String PARAM_PHI1 = "Phi1";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_POW = "pow";
  private static final String PARAM_CONTRAST = "contrast";

  private static final String[] paramNames = { PARAM_RADIUS, PARAM_RADIUS1, PARAM_PHI1, PARAM_THICKNESS, PARAM_POW, PARAM_CONTRAST };

  private double radius = 1.0;
  private double radius1 = 0.1;
  private double phi1 = 110.0;
  private double thickness = 0.1;
  private double pow = 1.5;
  private double contrast = 0.5;

  private class Point {
    private double x, y;
  }

  private void circle(FlameTransformationContext pContext, Point p) {
    double r = this.radius1 * (this.thickness + (1.0 - this.thickness) * pContext.random());
    double Phi = 2.0 * Math.PI * pContext.random();
    double sinPhi = pContext.sin(Phi);
    double cosPhi = pContext.cos(Phi);
    p.x = r * cosPhi + this.x1;
    p.y = r * sinPhi + this.y1;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* GlynnSim1 by eralex61, http://eralex61.deviantart.com/art/GlynnSim-plugin-112621621 */
    double r = Math.sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double Alpha = this.radius / r;
    if (r < this.radius) { //object generation
      circle(pContext, toolPoint);
      pVarTP.x += pAmount * toolPoint.x;
      pVarTP.y += pAmount * toolPoint.y;
    }
    else {
      if (pContext.random() > this.contrast * Math.pow(Alpha, this.absPow)) {
        toolPoint.x = pAffineTP.x;
        toolPoint.y = pAffineTP.y;
      }
      else {
        toolPoint.x = Alpha * Alpha * pAffineTP.x;
        toolPoint.y = Alpha * Alpha * pAffineTP.y;
      }
      double Z = pContext.sqr(toolPoint.x - this.x1) + pContext.sqr(toolPoint.y - this.y1);
      if (Z < this.radius1 * this.radius1) { //object generation
        circle(pContext, toolPoint);
        pVarTP.x += pAmount * toolPoint.x;
        pVarTP.y += pAmount * toolPoint.y;
      }
      else {
        pVarTP.x += pAmount * toolPoint.x;
        pVarTP.y += pAmount * toolPoint.y;
      }
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { radius, radius1, phi1, thickness, pow, contrast };
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

  private double x1, y1, absPow;
  private Point toolPoint = new Point();

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm) {
    double a = Math.PI * phi1 / 180.0;
    double sinPhi1 = pContext.sin(a);
    double cosPhi1 = pContext.cos(a);
    this.x1 = this.radius * cosPhi1;
    this.y1 = this.radius * sinPhi1;
    this.absPow = Math.abs(this.pow);
  }
}
