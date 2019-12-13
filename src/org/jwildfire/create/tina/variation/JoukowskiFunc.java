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

import js.glsl.vec2;
import js.glsl.G;

import static org.jwildfire.base.mathlib.MathLib.*;

import org.jwildfire.base.Tools;

public class JoukowskiFunc extends VariationFunc {
	/*
	 * Variation :  joukowski
	 * Date: august 29, 2019
	 * Author: Jesus Sosa
	 * Reference & Credits:  https://www.shadertoy.com/view/wdtGD7
	 */


	private static final long serialVersionUID = 1L;



	private static final String PARAM_P1 = "P1";
	private static final String PARAM_P2 = "P2";
	private static final String PARAM_INVERT = "inverse";
	
	double p1=2.5;
	double p2=0.0;
    int invert=0;

 	
    vec2 z0=new vec2(-p2,0.2);
    
	private static final String[] additionalParamNames = { PARAM_P1,PARAM_P2,PARAM_INVERT};

	vec2 cexp( double b )         { return new vec2(Math.cos(b), Math.sin(b)); }
	vec2 cmul( vec2 a, vec2 b ) 
	{
		return new vec2( a.x*b.x - a.y*b.y, a.x*b.y + a.y*b.x ); 
	}
	vec2 cdiv( vec2 a, vec2 b )  
	{  double d = G.dot(b,b); 
	      return new vec2( G.dot(a,b), a.y*b.x - a.x*b.y ).division(d);
	}
	vec2 csqrt( vec2 z )
	{ double m = G.length(z);
	  return G.sqrt( new vec2(m+z.x, m-z.x).multiply(0.5) ).multiply( new vec2( 1.0, G.sign(z.y) ));
	}
	double cnorm( vec2 z )
	{ 
		return G.length(z); 
	}
	
	vec2 Jouk(vec2 z)
	{
	    return cmul(cexp(-0.0), (z.plus(z0)).plus(cdiv(new vec2(p1*p1, 0.), (z.plus(z0)))));
	}

	vec2 invJouk(vec2 z)
	{
	    z = cmul(cexp(0.0), z);
	    
	    vec2 r1 = z.division(2.).plus(csqrt(cmul(z,z).division(4.).minus( new vec2(p1*p1, 0.)))).minus(z0);
	    vec2 r2 = z.division(2.).minus(csqrt(cmul(z,z).division(4.).minus( new vec2(p1*p1, 0.)))).minus( z0);

	    if (cnorm(r1) > cnorm(r2))
	        return r1;
	    else
	        return r2;
	}
	 	
	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
	    
		    double x = pAffineTP.x;
		    double y = pAffineTP.y;

		    vec2 uv=new vec2(x,y);
		    vec2 Z;
		    if(invert==0)
		       Z = Jouk(uv);
		    else
		       Z = invJouk(uv);	

		    pVarTP.x += pAmount * (Z.x);
		    pVarTP.y += pAmount * (Z.y);
		    
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }
	  
	  
	  @Override
	  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		  z0=new vec2(-p2,0.2);
	   }
	  
	public String getName() {
		return "joukowski";
	}

	public String[] getParameterNames() {
		return (additionalParamNames);
	}

	public Object[] getParameterValues() { //
		return (new Object[] {  p1,p2,invert});
	}

	public void setParameter(String pName, double pValue) {

		if(pName.equalsIgnoreCase(PARAM_P1))
		{
			   p1=pValue;
		}
		else if(pName.equalsIgnoreCase(PARAM_P2))
		{
			   p2=pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			invert =(int)Tools.limitValue(pValue, 0 , 1);
		}
		else
		      throw new IllegalArgumentException(pName);
	}
	
	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will make them refresh
		return true;
	}	

}
