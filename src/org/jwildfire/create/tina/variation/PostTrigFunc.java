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
import org.jwildfire.base.mathlib.Complex;
import static org.jwildfire.base.mathlib.MathLib.M_2_PI;
import static org.jwildfire.base.mathlib.MathLib.*;

public class PostTrigFunc extends VariationFunc {
	private static final long serialVersionUID = 1L;

    private static final String PARAM_RECIPROCALPOW = "reciprocalpow";
	private static final String PARAM_DIVIDEPOW = "log_dividepow";
    private static final String PARAM_SQRTPOW = "sqrtpow";
    
    private static final String PARAM_EXPPOW = "exppow";
    private static final String PARAM_LOGPOW = "logpow";
    
	private static final String PARAM_ASINHPOW = "asinhpow";
    private static final String PARAM_ACOSHPOW = "acoshpow";
    private static final String PARAM_ATANHPOW = "atanhpow";
    
    private static final String PARAM_ASINPOW = "asinpow";
    private static final String PARAM_ACOSPOW = "acospow";
    private static final String PARAM_ATANPOW = "atanpow";
    
    private static final String PARAM_SINPOW = "sinpow";
    private static final String PARAM_COSPOW = "cospow";
    private static final String PARAM_TANPOW = "tanpow";  
      
    private static final String PARAM_SINHPOW = "sinhpow";
    private static final String PARAM_COSHPOW = "coshpow";
    private static final String PARAM_TANHPOW = "tanhpow";
   
	private static final String[] paramNames = { PARAM_RECIPROCALPOW, PARAM_DIVIDEPOW, PARAM_SQRTPOW, PARAM_EXPPOW, PARAM_LOGPOW, PARAM_ASINHPOW, PARAM_ACOSHPOW, PARAM_ATANHPOW, PARAM_ASINPOW, PARAM_ACOSPOW, PARAM_ATANPOW, PARAM_SINPOW, PARAM_COSPOW, PARAM_TANPOW, PARAM_SINHPOW, PARAM_COSHPOW, PARAM_TANHPOW};

    private double reciprocalpow = 1.0;
	private double dividepow = 0.0;
    private double sqrtpow = 0.0;
    
    private double exppow = 0.0;    
	private double logpow = 0.0;
        
    private double asinhpow = 0.0;
    private double acoshpow = 0.0;
    private double atanhpow = 0.0;
    
    private double asinpow = 0.0;
    private double acospow = 0.0;
    private double atanpow = 0.0;
        
    private double sinpow = 0.0;
    private double cospow = 0.0;    
    private double tanpow = 0.0;
        
    private double sinhpow = 0.0;
    private double coshpow = 0.0;    
    private double tanhpow = 0.0;    
    
	@Override
	public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		// post_trig by Whittaker Courtney
		double xx = 0.0, yy = 0.0;
        double xr = pVarTP.x, yr = pVarTP.y;

// reciprocal, log_divide, sqrt, exp, log
// these work either as stand alone or as pre functions if other functions are enabled.              
	if (reciprocalpow != 0) {
			Complex z = new Complex(xr, yr);

			z.Recip();

			z.Scale(pAmount);

				xr = reciprocalpow * z.re;
				yr = reciprocalpow * z.im;
		}

		if (dividepow != 0) {
			Complex z = new Complex(xr, yr);
            Complex z2 = new Complex(xr, yr);
           
            z2.Dec();            
            z.Inc();
			z.Div(z2);
            z.Log();
			z.Scale(pAmount * M_2_PI);

				xr = dividepow * z.re;
				yr = dividepow * z.im;	
		}

		if (sqrtpow != 0) {
			Complex z = new Complex(xr, yr);

			z.Sqrt();

			z.Scale(pAmount);

			if (pContext.random() < 0.5) {
				xr = sqrtpow * z.re;
				yr = sqrtpow * z.im;
			} else {
				xr = sqrtpow * -z.re;
				yr = sqrtpow * -z.im;
			}
		}

		if (exppow != 0) {
			Complex z = new Complex(xr, yr);

			z.Exp();

			z.Scale(pAmount);

			xr = exppow * z.re;
			yr = exppow * z.im;

		}

		if (logpow != 0) {
			Complex z = new Complex(xr, yr);

			z.Log();

			z.Scale(pAmount);

			xr = logpow * z.re;
			yr = logpow * z.im;
		}

//asinh, acosh, atanh
		if (asinhpow != 0) {
			Complex z = new Complex(xr, yr);
			
            z.AsinH();

			z.Scale(pAmount * M_2_PI);

			xx += asinhpow * z.re;
			yy += asinhpow * z.im;

		}

		if (acoshpow != 0) {
			Complex z = new Complex(xr, yr);

			z.AcosH();

			z.Scale(pAmount * M_2_PI);

			if (pContext.random() < 0.5) {
				xx += acoshpow * z.re;
				yy += acoshpow * z.im;
			} else {
				xx += acoshpow * -z.re;
				yy += acoshpow * -z.im;
			}
		}

		if (atanhpow != 0) {
			Complex z = new Complex(xr, yr);

			z.AtanH();

			z.Scale(pAmount * M_2_PI);

			xx += atanhpow * z.re;
			yy += atanhpow * z.im;

		}

// asin, acos, atan
		if (asinpow != 0) {
			Complex z = new Complex(xr, yr);
            
            z.Asin();

			z.Scale(pAmount * M_2_PI);

			xx += asinpow * z.re;
			yy += asinpow * z.im;
		}
		if (acospow != 0) {
			Complex z = new Complex(xr, yr);
            
            z.Acos();

			z.Scale(pAmount * M_2_PI);

			xx += acospow * z.re;
			yy += acospow * z.im;
		}
		if (atanpow != 0) {
			Complex z = new Complex(xr, yr);
            
            z.Atan();

			z.Scale(pAmount * M_2_PI);

			xx += atanpow * z.re;
			yy += atanpow * z.im;
		}

// sin, cos, tan
		if (sinpow != 0) {
			Complex z = new Complex(xr, yr);

			z.Sin();

			z.Scale(pAmount);

			xx += sinpow * z.re;
			yy += sinpow * z.im;
		}

		if (cospow != 0) {
			Complex z = new Complex(xr, yr);

			z.Cos();

			z.Scale(pAmount);

			xx += cospow * z.re;
			yy += cospow * z.im;
		}
		if (tanpow != 0) {
			Complex z = new Complex(xr, yr);
			Complex z2 = new Complex(xr, yr);

			z.Sin();
            z2.Cos();
            z.Div(z2);

			z.Scale(pAmount);

			xx += tanpow * z.re;
			yy += tanpow * z.im;
		}

// sinh, cosh, tanh
		if (sinhpow != 0) {
			Complex z = new Complex(xr, yr);

			z.SinH();

			z.Scale(pAmount);

			xx += sinhpow * z.re;
			yy += sinhpow * z.im;
		}

		if (coshpow != 0) {
			Complex z = new Complex(xr, yr);

			z.CosH();

			z.Scale(pAmount);

			xx += coshpow * z.re;
			yy += coshpow * z.im;
		}
		if (tanhpow != 0) {
			Complex z = new Complex(xr, yr);
			Complex z2 = new Complex(xr, yr);

			z.SinH();
            z2.CosH();
            z.Div(z2);

			z.Scale(pAmount);

			xx += tanhpow * z.re;
			yy += tanhpow * z.im;
		}

// determine if reciprocal, log_divide, sqrt, exp, log are being used alongside 
// another variation.
// if they are being used with other variations, use the combined function values with
// reciprocal, log_divide, sqrt, exp, log used as pre functions to the rest.                                        
       if (asinhpow + acoshpow + atanhpow + sinpow + cospow + tanpow + sinhpow + coshpow + tanhpow + asinpow + acospow + atanpow == 0){
       pVarTP.x = xr;
       pVarTP.y = yr;
       }
       
       else{
        pVarTP.x = xx;
        pVarTP.y = yy;
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
		return new Object[] { reciprocalpow, dividepow, sqrtpow, exppow, logpow, asinhpow, acoshpow, atanhpow, asinpow, acospow, atanpow, sinpow, cospow, tanpow, sinhpow, coshpow, tanhpow};
	}

	@Override
	public void setParameter(String pName, double pValue) {
		
		if (PARAM_RECIPROCALPOW.equalsIgnoreCase(pName))
			reciprocalpow = pValue;
        else if (PARAM_DIVIDEPOW.equalsIgnoreCase(pName))
			dividepow = pValue;
        else if (PARAM_SQRTPOW.equalsIgnoreCase(pName))
			sqrtpow = pValue;

	    else if (PARAM_EXPPOW.equalsIgnoreCase(pName))
			exppow = pValue;
		else if (PARAM_LOGPOW.equalsIgnoreCase(pName))
			logpow = pValue;

        else if (PARAM_ASINHPOW.equalsIgnoreCase(pName))
			asinhpow = pValue;
		else if (PARAM_ACOSHPOW.equalsIgnoreCase(pName))
			acoshpow = pValue;
		else if (PARAM_ATANHPOW.equalsIgnoreCase(pName))
			atanhpow = pValue;

	    else if (PARAM_ASINPOW.equalsIgnoreCase(pName))
			asinpow = pValue;
	    else if (PARAM_ACOSPOW.equalsIgnoreCase(pName))
			acospow = pValue;
	    else if (PARAM_ATANPOW.equalsIgnoreCase(pName))
			atanpow = pValue;

	    else if (PARAM_SINPOW.equalsIgnoreCase(pName))
			sinpow = pValue;
	    else if (PARAM_COSPOW.equalsIgnoreCase(pName))
			cospow = pValue;
	    else if (PARAM_TANPOW.equalsIgnoreCase(pName))
			tanpow = pValue;

	    else if (PARAM_SINHPOW.equalsIgnoreCase(pName))
			sinhpow = pValue;
	    else if (PARAM_COSHPOW.equalsIgnoreCase(pName))
			coshpow = pValue;
	    else if (PARAM_TANHPOW.equalsIgnoreCase(pName))
			tanhpow = pValue;

		else
			throw new IllegalArgumentException(pName);
	}

	@Override
	public String getName() {
		return "post_trig";
	}

@Override
  public int getPriority() {
    return 1;
  }

}
