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

public class GlynnSuperShapeFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;



  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_RADIUS1 = "radius1";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_PHI1 = "phi1";

  private static final String PARAM_M = "m";
  private static final String PARAM_N1 = "n1";
  private static final String PARAM_N2 = "n2";
  private static final String PARAM_N3 = "n3";
  
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_POW = "pow";
  private static final String PARAM_CONTRAST = "contrast";


  private static final String[] paramNames = {  PARAM_RADIUS,PARAM_RADIUS1,PARAM_THICKNESS,PARAM_PHI1,PARAM_M,PARAM_N1, PARAM_N2, PARAM_N3,PARAM_SCALE,PARAM_POW,PARAM_CONTRAST };


  private double radius = 1.0;
  private double radius1 = 0.5;
  private double thickness = 1.0;
  private double phi1 = 0.0;
  
  private double m = 5;  
  private double n1 = 1.7;
  private double n2 = 1.7;
  private double n3 = 1.7;

  private double scale = .71;
  private double pow = 1.5;
  private double contrast = 0.5;

  private double _x1, _y1, _absPow;
  
  
  
  public vec2 supershape(double m,double n1,double n2,double n3,double phi)
  {
     double r;
     double t1,t2;
     double a=1,b=1;
     vec2 p=new vec2(0.0);

     t1 = Math.cos(m * phi / 4) / a;
     t1 = Math.abs(t1);
     t1 = Math.pow(t1,n2);

     t2 = Math.sin(m * phi / 4) / b;
     t2 = Math.abs(t2);
     t2 = Math.pow(t2,n3);

     r = Math.pow(t1+t2,1/n1);
     if (Math.abs(r) == 0) {
        p.x = 0;
        p.y = 0;
     } else {
        r = 1 / r;
        p.x = r * Math.cos(phi);
        p.y = r * Math.sin(phi);
     }
     return p;
  }
  
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


		  double phi = pContext.random()*period;
		  vec2 p = supershape(m, n1, n2, n3,phi);

		  double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);

//		  double y = pContext.random() - 0.5;
		  double xi,yi;
		  
		  if(r<Math.abs(radius))
		  {			  
			  if(radius1>=0)
			  {
		       p =  supershape(m, n1, n2, n3,phi);
			   pVarTP.x += pAmount * ((p.x*scale)*radius1+_x1);
			   pVarTP.y += pAmount * ((p.y*scale)*radius1+_y1);
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
				  vec2 p1 = supershape(m, n1, n2, n3,phi);
				  pVarTP.x += pAmount * ((p.x*scale)*radius1+_x1);
				  pVarTP.y += pAmount * ((p.y*scale)*radius1+_y1);
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
    return new Object[] {  radius,radius1,thickness,phi1,m, n1,n2,n3,scale,pow,contrast };
  }

  @Override
  public void setParameter(String pName, double pValue) {
	  if (PARAM_RADIUS.equalsIgnoreCase(pName))
	      radius = pValue;
	  else if (PARAM_RADIUS1.equalsIgnoreCase(pName))
	      radius1 = pValue;
	  else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
	      thickness = pValue;
	  else if (PARAM_PHI1.equalsIgnoreCase(pName))
	      phi1 = pValue;
  else if (PARAM_M.equalsIgnoreCase(pName))
      m = pValue;
    else if (PARAM_N1.equalsIgnoreCase(pName))
      n1 = pValue;
    else if (PARAM_N2.equalsIgnoreCase(pName))
        n2 = pValue;
    else if (PARAM_N3.equalsIgnoreCase(pName))
      n3 = pValue;
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
    return "glynnSShape";
  }

  @Override
  public void randomize() {
  	radius = Math.random() * 1.5 + 0.25;
  	radius1 = Math.random() * radius + 0.02;
  	phi1 = Math.random() * 360.0 - 180.0;
  	thickness = Math.random();
  	pow = Math.random() * 2.0 + 0.5;
  	contrast = Math.random();
    m = Math.random() * 9.0 + 3.0;
    if (Math.random() < 0.5) m = (int) m;
    n1 = Math.random() * 5.0 - 2.5;
    n2 = Math.random() * 5.0 - 2.5;
    n3 = Math.random() * 5.0 - 2.5;
    scale = Math.random() * 0.75 + 0.25;
  }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_2D};
	}

}
