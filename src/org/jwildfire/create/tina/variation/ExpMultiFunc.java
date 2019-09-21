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
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.Complex;
import static org.jwildfire.base.mathlib.MathLib.M_2_PI;


public class ExpMultiFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  
  private static final String PARAM_RE = "re";
  private static final String PARAM_IM = "im";
  private static final String PARAM_RE_ADD = "re_add";
  private static final String PARAM_IM_ADD = "im_add";
  private static final String PARAM_SQR = "sqr";
  private static final String PARAM_ASECH = "asech";
  private static final String PARAM_ACOSECH = "acosech";
  private static final String PARAM_ACOTH = "acoth";
  
  private static final String[] paramNames = { PARAM_RE, PARAM_IM, PARAM_RE_ADD, PARAM_IM_ADD, PARAM_SQR, PARAM_ASECH, PARAM_ACOSECH, PARAM_ACOTH};
  private double re = 1.0;
  private double im = 0.0;
  private double re_add = 0.0;
  private double im_add = 0.0;
  private double sqr = 0;
  private double asech = 0;
  private double acosech = 0;
  private double acoth = 1;
  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
  //exp_multi by Whittaker Courtney
 
       Complex z = new Complex(pAffineTP.x, pAffineTP.y);
       Complex z2 = new Complex(re, im);
       Complex z3 = new Complex(re_add, im_add);
       Complex Acoth = new Complex(acoth, 0);    
       Complex Acosech = new Complex(acosech, 0);  
       Complex Asech = new Complex(asech, 0);  
       Complex Sqr = new Complex(sqr, 0);                     
     z.Exp();       
     z.Div(z2);
     if (sqr != 0){
     z.Sqr();
     z.Mul(Sqr);
     }
     z.Add(z3);
     z.Sqrt();
     if (asech != 0){
       z.AsecH();  
       z.Mul(Asech); 
     }
     if (acosech != 0){
       z.AcosecH();
       z.Mul(Acosech); 
         }
     if (acoth != 0){
       z.AcotH();  
       z.Mul(Acoth);
     }

       
       z.Scale(pAmount * M_2_PI);
                                                                   
   if (pContext.random() < 0.5){                              
       pVarTP.x += z.re;  
       pVarTP.y += z.im;    
    }
    else{     
       pVarTP.x += -z.re; 
       pVarTP.y += -z.im;
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
    return new Object[] {re, im, re_add, im_add, sqr, asech, acosech, acoth};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RE.equalsIgnoreCase(pName))
      re = pValue;
    else if (PARAM_IM.equalsIgnoreCase(pName))
      im = pValue;
    else if (PARAM_RE_ADD.equalsIgnoreCase(pName))
      re_add = pValue;      
    else if (PARAM_IM_ADD.equalsIgnoreCase(pName))
      im_add = pValue;  
    else if (PARAM_SQR.equalsIgnoreCase(pName))
      sqr = pValue;       
    else if (PARAM_ASECH.equalsIgnoreCase(pName))
      asech = pValue; 
    else if (PARAM_ACOSECH.equalsIgnoreCase(pName))
      acosech = pValue;  
    else if (PARAM_ACOTH.equalsIgnoreCase(pName))
      acoth = pValue;      
    else
      throw new IllegalArgumentException(pName);
  }
 

  @Override
  public String getName() {
    return "exp_multi";
  }

}
