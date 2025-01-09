/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

public class RingerFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_HIDE = "hide_inner";
  private static final String PARAM_RADIUS = "innerradius";
  private static final String PARAM_CROPRADIUS = "outerradius";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_CONTRAST = "contrast";
  private static final String PARAM_POW = "pow";
  private static final String PARAM_SCATTER_AREA = "scatter_area";
  private static final String PARAM_ZERO = "hide_outer";

  private static final String[] paramNames = {PARAM_HIDE, PARAM_RADIUS, PARAM_CROPRADIUS, PARAM_THICKNESS,
          PARAM_CONTRAST, PARAM_POW, PARAM_SCATTER_AREA, PARAM_ZERO};


  private int hide = 0;
  private double oldx = 0, oldy = 0;

  private double radius = 0.5;
  private double thickness = 0.0;
  private double contrast = 0;
  private double pow = 1.0;

  private double cropradius = 1.0;
  private double scatter_area = 0.0;
  private int zero = 1;
  private double cA;


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

    double r, Alpha;
    r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    Alpha = this.radius / r;

    if (r < this._radius1) {
      if (hide == 0) {
        circle2(pContext, toolPoint); // Draw/Hide the circle
        pVarTP.x += pAmount * toolPoint.x;
        pVarTP.y += pAmount * toolPoint.y;
      } else {
        pVarTP.x = oldx;
        pVarTP.y = oldy;
      }
    } else {
      if (pContext.random() > this.contrast * pow(Alpha, this._absPow)) {
        pVarTP.x += pAmount * pAffineTP.x;
        pVarTP.y += pAmount * pAffineTP.y;
      } else {
        pVarTP.x += pAmount * Alpha * Alpha * pAffineTP.x;
        pVarTP.y += pAmount * Alpha * Alpha * pAffineTP.y;
      }
    }
    oldx = pVarTP.x;
    oldy = pVarTP.y;

    double x0 = 0.0;
    double y0 = 0.0;
    double cr = cropradius;
    double ca = cA;
    double vv = pAmount;

    pVarTP.x -= x0;
    pVarTP.y -= y0;

    double rad = sqrt(pVarTP.x * pVarTP.x + pVarTP.y * pVarTP.y);
    double ang = atan2(pVarTP.y, pVarTP.x);
    double rdc = cr + (pContext.random() * 0.5 * ca);

    boolean esc = rad > cr;
    boolean cr0 = zero == 1;

    double s = sin(ang);
    double c = cos(ang);

    pVarTP.doHide = false;
    if (cr0 && esc) {
      pVarTP.x = pVarTP.y = 0;
      pVarTP.doHide = true;
    } else if (cr0 && !esc) {
      pVarTP.x = vv * pVarTP.x + x0;
      pVarTP.y = vv * pVarTP.y + y0;
    } else if (!cr0 && esc) {
      pVarTP.x = vv * rdc * c + x0;
      pVarTP.y = vv * rdc * s + y0;
    } else if (!cr0 && !esc) {
      pVarTP.x = vv * pVarTP.x + x0;
      pVarTP.y = vv * pVarTP.y + y0;
    }

    // setColor(pVarTP);
  }


  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{hide, radius, cropradius, thickness, contrast, pow, scatter_area, zero};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_HIDE.equalsIgnoreCase(pName))
      hide = (int) limitVal(pValue, 0, 1);
    else if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_CROPRADIUS.equalsIgnoreCase(pName))
      cropradius = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_CONTRAST.equalsIgnoreCase(pName))
      contrast = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_POW.equalsIgnoreCase(pName))
      pow = pValue;
    else if (PARAM_SCATTER_AREA.equalsIgnoreCase(pName))
      scatter_area = pValue;
    else if (PARAM_ZERO.equalsIgnoreCase(pName))
      zero = (int) limitVal(pValue, 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "ringer";
  }

  private Point toolPoint = new Point();
  private double _radius1, _radius2, _gamma, _absPow;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    this._radius1 = this.radius + this.thickness;
    this._radius2 = sqr(this.radius) / this._radius1;
    this._gamma = this._radius1 / (this._radius1 + this._radius2);
    this._absPow = fabs(this.pow);

    cA = max(-1.0, min(scatter_area, 1.0));
  }

  @Override
  public void randomize() {
    hide = (int) (Math.random() * 2);
    radius = Math.random() * 0.5 + 0.1;
    cropradius = radius + Math.random() * 0.75 + 0.1;
    thickness = Math.random();
    contrast = Math.random();
    pow = Math.random() + 0.5;
    scatter_area = Math.random() * 2.5;
    zero = (int) (Math.random() * 2);
  }
  
  @Override
  public void mutate(double pAmount) {
    switch ((int) (Math.random() * 8)) {
    case 0:
      hide = (hide + 1) % 2;
      break;
    case 1:
      radius += mutateStep(radius, pAmount);
      break;
    case 2:
      cropradius += mutateStep(cropradius, pAmount);
      break;
    case 3:
      thickness += mutateStep(thickness, pAmount);
      if (thickness > 1.0) thickness = 0.0;
      break;
    case 4:
      contrast += mutateStep(contrast, pAmount);
      if (contrast > 1.0) contrast = 0.0;
      break;
    case 5:
      pow += mutateStep(pow, pAmount);
      break;
    case 6:
      scatter_area += mutateStep(scatter_area, pAmount);
      break;
    case 7:
      zero = (zero + 1) % 2;
      break;
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D};
  }

}