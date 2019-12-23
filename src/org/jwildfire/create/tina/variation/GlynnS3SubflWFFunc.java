/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

public class GlynnS3SubflWFFunc extends SubFlameWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PROBABILITY = "weight";
  private static final String PARAM_HIDE = "show/hide(1/0)";
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_CONTRAST = "contrast";
  private static final String PARAM_POW = "pow";
  private static final String PARAM_TYPE = "type";
  private static final String PARAM_PHI1 = "phi1";
  private static final String PARAM_PHI2 = "phi2";

  private static final String[] additionalParamNames = {PARAM_PROBABILITY, PARAM_HIDE, PARAM_RADIUS, PARAM_THICKNESS,
          PARAM_CONTRAST, PARAM_POW, PARAM_TYPE, PARAM_PHI1, PARAM_PHI2};

  private double weight = 0.50;
  private int show = 1;
  private double radius = 1.0;
  private double thickness = 0.42;
  private double contrast = 1.0;
  private double pow = 1.5;
  private int type = 0;
  private double phi1 = 0.0;
  private double phi2 = 360.0;

  private static class Point implements Serializable {
    private static final long serialVersionUID = 1L;

    private double x, y;
  }


  private void circle(FlameTransformationContext pContext, Point p) {
    double r = this.radius + this.thickness - this._gamma * pContext.random();
    double Phi = this._phi10 + this._delta * pContext.random();
    double sinPhi = sin(Phi);
    double cosPhi = cos(Phi);
    p.x = r * cosPhi;
    p.y = r * sinPhi;
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
    XYZPoint pnew = new XYZPoint();
    double r, Alpha;

    r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    Alpha = this.radius / r;

    if (type != 0) {
      if (r < this.radius) {
        if (show == 1) {
          circle(pContext, toolPoint);
          pnew.x += pAmount * toolPoint.x;
          pnew.y += pAmount * toolPoint.y;
        } else { // agregado
          pnew.x += pAmount * pAffineTP.x; //parece funcionar
          pnew.y += pAmount * pAffineTP.y; //parece funcionar
          //    			            pnew.x += 0.0;  // center dot
          //    			            pnew.y += 0.0;
          //                    	 return;   //ccenter dot
          //                   	 pnew.x=oldx; //center dot
          //                    	 pnew.y=oldy;
        }
      } else {
        if (pContext.random() > this.contrast * pow(Alpha, this._absPow)) {
          pnew.x += pAmount * pAffineTP.x;
          pnew.y += pAmount * pAffineTP.y;
        } else {
          pnew.x += pAmount * Alpha * Alpha * pAffineTP.x;
          pnew.y += pAmount * Alpha * Alpha * pAffineTP.y;
        }
      }
    }

    if (type == 0) {
      if (r < this._radius1) {
        if (show == 1) {
          circle2(pContext, toolPoint);
          pnew.x += pAmount * toolPoint.x;
          pnew.y += pAmount * toolPoint.y;
        } else { // agregado
          pnew.x += pAmount * pAffineTP.x; //parece funcionar
          pnew.y += pAmount * pAffineTP.y; //parece funcionar

//	   			            pnew.x += 0.0;  // center dot
//	   			            pnew.y += 0.0;

//			        		 return; center dot

//	                    	 pnew.x=oldx; center dot
//	                    	 pnew.y=oldy;
        }
      } else {
        if (pContext.random() > this.contrast * pow(Alpha, this._absPow)) {
          pnew.x += pAmount * pAffineTP.x;
          pnew.y += pAmount * pAffineTP.y;
        } else {
          pnew.x += pAmount * Alpha * Alpha * pAffineTP.x;
          pnew.y += pAmount * Alpha * Alpha * pAffineTP.y;
        }
      }
    }

    //		    oldx=pnew.x;
    //		    oldy=pnew.y;

    double prob = pContext.random();

    if (prob < weight) {
      pVarTP.x = pnew.x;
      pVarTP.y = pnew.y;
      if (pContext.isPreserveZCoordinate()) {
        pVarTP.z += pAmount * pAffineTP.z;
      }
    } else {
      subflameIter(pContext);
      pVarTP.x += q.x * pAmount;
      pVarTP.y += q.y * pAmount;
      pVarTP.z += q.z * pAmount;

      setColor(pVarTP);

    }
  }

  @Override
  public String[] getParameterNames() {
    return joinArrays(additionalParamNames, paramNames);
  }

  @Override
  public Object[] getParameterValues() {
    return joinArrays(new Object[]{weight, show, radius, thickness, contrast, pow, type, phi1, phi2}, super.getParameterValues());
  }

  @Override
  public void setParameter(String pName, double pValue) {

    if (PARAM_PROBABILITY.equalsIgnoreCase(pName))
      weight = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_HIDE.equalsIgnoreCase(pName))
      show = (int) limitVal(pValue, 0, 1);
    else if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_CONTRAST.equalsIgnoreCase(pName))
      contrast = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_POW.equalsIgnoreCase(pName))
      pow = pValue;
    else if (PARAM_TYPE.equalsIgnoreCase(pName))
      type = (int) pValue;
    else if (PARAM_PHI1.equalsIgnoreCase(pName))
      phi1 = pValue;
    else if (PARAM_PHI2.equalsIgnoreCase(pName))
      phi2 = pValue;
    else
      super.setParameter(pName, pValue);
  }

  @Override
  public String getName() {
    return "glynns3subfl";
  }


  private Point toolPoint = new Point();

  private double _radius1, _radius2, _gamma, _absPow;
  private double _phi10, _phi20, _delta;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    if (type == 0) {
      this._radius1 = this.radius + this.thickness;
      this._radius2 = sqr(this.radius) / this._radius1;
      this._gamma = this._radius1 / (this._radius1 + this._radius2);
      this._absPow = fabs(this.pow);
    } else {
      this._phi10 = M_PI * this.phi1 / 180.0;
      this._phi20 = M_PI * this.phi2 / 180.0;
      this._gamma = this.thickness * (2.0 * this.radius + this.thickness) / (this.radius + this.thickness);
      this._delta = this._phi20 - this._phi10;
      this._absPow = fabs(this.pow);
    }
  }

  public GlynnS3SubflWFFunc() {
    color_mode = CM_DIRECT;
  }
}
