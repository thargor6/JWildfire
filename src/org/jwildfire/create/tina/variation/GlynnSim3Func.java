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

import static org.jwildfire.base.MathLib.M_PI;
import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.fabs;
import static org.jwildfire.base.MathLib.pow;
import static org.jwildfire.base.MathLib.sin;
import static org.jwildfire.base.MathLib.sqr;
import static org.jwildfire.base.MathLib.sqrt;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class GlynnSim3Func extends VariationFunc {

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_THICKNESS2 = "thickness2";
  private static final String PARAM_POW = "pow";
  private static final String PARAM_CONTRAST = "contrast";

  private static final String[] paramNames = { PARAM_RADIUS, PARAM_THICKNESS, PARAM_THICKNESS2, PARAM_CONTRAST, PARAM_POW };

  private double radius = 1.0;
  private double thickness = 0.1;
  private double thickness2 = 0.1;
  private double contrast = 0.5;
  private double pow = 1.5;

  private class Point {
    private double x, y;
  }

  private void circle2(FlameTransformationContext pContext, Point p) {
    //    double r = this.radius + this.thickness - this.Gamma * pContext.random();
    double Phi = 2.0 * M_PI * pContext.random();
    double sinPhi = sin(Phi);
    double cosPhi = cos(Phi);
    double r;
    if (pContext.random() < this.Gamma) {
      r = this.radius1;
    }
    else {
      r = this.radius2;
    }
    p.x = r * cosPhi;
    p.y = r * sinPhi;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* GlynnSim3 by eralex61, http://eralex61.deviantart.com/art/GlynnSim-plugin-112621621 */
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double Alpha = this.radius / r;
    if (r < this.radius1) {
      circle2(pContext, toolPoint);
      pVarTP.x += pAmount * toolPoint.x;
      pVarTP.y += pAmount * toolPoint.y;
    }
    else {
      if (pContext.random() > this.contrast * pow(Alpha, this.absPow)) {

        pVarTP.x += pAmount * pAffineTP.x;
        pVarTP.y += pAmount * pAffineTP.y;
      }
      else {
        pVarTP.x += pAmount * Alpha * Alpha * pAffineTP.x;
        pVarTP.y += pAmount * Alpha * Alpha * pAffineTP.y;
      }
    }
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pVarTP.z + pAmount * pAffineTP.z;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { radius, thickness, thickness2, contrast, pow };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = pValue;
    else if (PARAM_THICKNESS2.equalsIgnoreCase(pName))
      thickness2 = pValue;
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
  private double radius1, radius2, Gamma, absPow;

  @Override
  public void init(FlameTransformationContext pContext, XForm pXForm, double pAmount) {
    this.radius1 = this.radius + this.thickness;
    this.radius2 = sqr(this.radius) / this.radius1;
    this.Gamma = this.radius1 / (this.radius1 + this.radius2);
    this.absPow = fabs(this.pow);
  }
}
