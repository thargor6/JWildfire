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
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.pow;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqr;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;


import js.glsl.G;
import js.glsl.vec2;

public class GlynnLissaFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;



  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_RADIUS1 = "radius1";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_PHI1 = "phi1";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_PHASE = "phase";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_POW = "pow";
  private static final String PARAM_CONTRAST = "contrast";


  private static final String[] paramNames = {  PARAM_RADIUS,PARAM_RADIUS1,PARAM_THICKNESS,PARAM_PHI1,PARAM_A, PARAM_B, PARAM_WIDTH,PARAM_PHASE,PARAM_SCALE,PARAM_POW,PARAM_CONTRAST };


  private double radius = 1.0;
  private double radius1 = 0.5;
  private double thickness = 1.0;
  private double phi1 = 0.0;
  
  private double a = 3.0;
  private double b = 2.0;
  private double width = 0.0;
  private double phase = 0.0;
  private double scale = .71;
  private double pow = 1.5;
  private double contrast = 0.5;

  private double _x1, _y1, _absPow;
  
  
   void circle(FlameTransformationContext pContext, vec2 p) {
	    double r = this.radius1 * (this.thickness + (1.0 - this.thickness) * pContext.random());
	    double Phi = 2.0 * M_PI * pContext.random();
	    double sinPhi = sin(Phi);
	    double cosPhi = cos(Phi);
	    p.x = r * cosPhi + this._x1;
	    p.y = r * sinPhi + this._y1;
	  }
  
 
  
	vec2 lissajous(double t, double a, double b, double d)
	{
		return new vec2(Math.sin(a*t+d), Math.sin(b*t));
	}
	
	public boolean isInside(double px,double py,double rx,double ry,double rw,double rh)
	{
	if (px >= rx &&         // right of the left edge AND
		    px <= rx + rw &&    // left of the right edge AND
		    py >= ry &&         // below the top AND
		    py <= ry + rh) {    // above the bottom
		        return true;
		}
		return false;
	}
	  @Override
	 public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

		  double period = Math.PI*2.0;

		  double d = phase;// phase

		  double t = pContext.random()*period;
		  vec2 p = lissajous(t, a, b, d);

		  double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);


//		  boolean test=isInside(pAffineTP.x,pAffineTP.y,-1.,-1.,2.,2.);
		  double y = pContext.random() - 0.5;
		  double xi,yi;
		  if(r<Math.abs(radius))
		  {			  
			  if(radius1>=0)
			  {
		       p = lissajous(t, a, b, d);
			   pVarTP.x += pAmount * ((p.x*scale+width*y)*radius1+_x1);
			   pVarTP.y += pAmount * ((p.y*scale+width*y)*radius1+_y1);
			  }
			  else
			  {
				  circle(pContext,p);
				  pVarTP.x += pAmount * (p.x);
				  pVarTP.y += pAmount * (p.y);
			  }
		  }
		  else 
		  {

			  double Alpha = Math.abs(this.radius) / r;
			  if (pContext.random() > this.contrast * pow(Alpha, this._absPow)) {
				  xi = pAffineTP.x;
				  yi = pAffineTP.y;
			  }
			  else
			  {
				  xi = Alpha * Alpha * pAffineTP.x;
				  yi = Alpha * Alpha * pAffineTP.y;
			  }
			  double Z = sqr(xi - this._x1) + sqr(yi - this._y1);
			  if (Z < this.radius1 * this.radius1) { 
				  vec2 p1 = lissajous(t, a, b, d);
				  pVarTP.x += pAmount * ((p.x*scale+width*y)*radius1+_x1);
				  pVarTP.y += pAmount * ((p.y*scale+width*y)*radius1+_y1);
			  }
			  else {
				  pVarTP.x += pAmount * xi;
				  pVarTP.y += pAmount * yi;
			  }

		  }

		  if   (pContext.isPreserveZCoordinate()) {
			  pVarTP.z += pAmount * pAffineTP.z;
		  }

	  }
	  
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		    double a = M_PI * phi1 / 180.0;
		    double sinPhi1 = sin(a);
		    double cosPhi1 = cos(a);
		    this._x1 = this.radius * cosPhi1;
		    this._y1 = this.radius * sinPhi1;
		    this._absPow = fabs(this.pow);
		  }


  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] {  radius,radius1,thickness,phi1,a, b,width, phase,scale,pow,contrast };
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
	      thickness = pValue;
  else if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName))
        width = pValue;
    else if (PARAM_PHASE.equalsIgnoreCase(pName))
      phase = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
        scale = pValue;
    else if (PARAM_POW.equalsIgnoreCase(pName))
        pow = pValue;
      else if (PARAM_CONTRAST.equalsIgnoreCase(pName))
        contrast = limitVal(pValue, 0.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "glynnlissa";
  }

  @Override
  public void randomize() {
  	radius = Math.random() * 1.5 + 0.25;
  	radius1 = Math.random() * radius + 0.02;
  	phi1 = Math.random() * 360.0 - 180.0;
  	thickness = Math.random();
  	pow = Math.random() * 2.0 + 0.5;
  	contrast = Math.random();
    a = (int) (Math.random() * 10 + 1);
    if (Math.random() < 0.5) a -= Math.random();
    b = (int) (Math.random() * 10 + 1);
    if (Math.random() < 0.5) b -= Math.random();
    width = Math.random() * 0.1;
    phase = Math.random() * 2.0 * Math.PI - Math.PI;
    scale = Math.random() * 0.75 + 0.25;
  }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION};
	}

}
