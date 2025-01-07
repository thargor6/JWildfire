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

public class JuliaOutsideFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;
  
  private static final String PARAM_RE_DIV = "re_div";
  private static final String PARAM_IM_DIV = "im_div";
  private static final String PARAM_MODE = "mode";
    
  private static final String[] paramNames = { PARAM_RE_DIV, PARAM_IM_DIV, PARAM_MODE};
  private double re_div = 1.0;
  private double im_div = 0.0;
  private int mode = 0;  
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
  //julia_outside by Whittaker Courtney 
 
       Complex z = new Complex(pAffineTP.x, pAffineTP.y);
       Complex z2 = new Complex(pAffineTP.x, pAffineTP.y);
       Complex z3 = new Complex(re_div, im_div);

if (mode == 0 || mode == 2){
z.Sqrt();
}
z.Inc();
if (mode == 0 || mode == 2){
z.Sqr();
}
if (mode == 0 || mode == 2){
z2.Sqrt();
}
z2.Dec();
if (mode == 0 || mode == 2){
z2.Sqr();
}
z.Div(z2);
if (mode == 0 || mode == 1){
z.Sqrt();
}
z.Div(z3);

if (mode == 0 || mode == 1){                                                                   
   if (pContext.random() < 0.5){                              
       pVarTP.x += pAmount * z.re;  
       pVarTP.y += pAmount * z.im;    
    }
    else{     
       pVarTP.x += pAmount * -z.re; 
       pVarTP.y += pAmount * -z.im;
    }                   
}
else{
       pVarTP.x += pAmount * z.re;
       pVarTP.y += pAmount * z.im;
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
    return new Object[] {re_div, im_div, mode};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RE_DIV.equalsIgnoreCase(pName))
      re_div = pValue;
    else if (PARAM_IM_DIV.equalsIgnoreCase(pName))
      im_div = pValue;
    else if (pName.equalsIgnoreCase(PARAM_MODE)) {
      mode =(int)Tools.limitValue(pValue, 0 , 2);
        }
    else
      throw new IllegalArgumentException(pName);
  }
 

  @Override
  public String getName() {
    return "julia_outside";
  }
  
  @Override
  public void randomize() {
    re_div = Math.random() * 5.0 - 2.5;
    im_div = Math.random() * 5.0 - 2.5;
    mode = (int) (Math.random() * 3.0);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return    "Complex z,z2,z3;"
    		+ "Complex_Init(&z ,__x, __y);"
    		+" Complex_Init(&z2,__x, __y);"
    		+" Complex_Init(&z3, __julia_outside_re_div ,  __julia_outside_im_div );"
    		+" if ( __julia_outside_mode  == 0.0 ||  __julia_outside_mode  == 2.0){"
    		+"     Complex_Sqrt(&z);"
    		+" }"
    		+"Complex_Inc(&z);"
    		+" if ( __julia_outside_mode  == 0.0 ||  __julia_outside_mode  == 2.0){"
    		+"   Complex_Sqr(&z);"
    		+" }"
    		+" if ( __julia_outside_mode  == 0.0 ||  __julia_outside_mode  == 2.0){"
    		+"   Complex_Sqrt(&z2);"
    		+" }"
    		+"Complex_Dec(&z2);"
    		+"if ( __julia_outside_mode  == 0.0 ||  __julia_outside_mode  == 2.0){"
    		+"   Complex_Sqr(&z2);"
    		+"}"
    		+"Complex_Div(&z,&z2);"
    		+"if ( __julia_outside_mode  == 0.0 ||  __julia_outside_mode  == 1.0){"
    		+"   Complex_Sqrt(&z);"
    		+"}"
    		+"Complex_Div(&z,&z3);"
    		+"if ( __julia_outside_mode  == 0.0 ||  __julia_outside_mode  == 1.0){                                                                   "
    		+"   if (RANDFLOAT() < 0.5){"
    		+"       __px += __julia_outside * z.re;"
    		+"       __py += __julia_outside * z.im;"
    		+"    }"
    		+"    else{"
    		+"       __px += __julia_outside * (-z.re);"
    		+"       __py += __julia_outside * (-z.im);"
    		+"    }"
    		+"}"
    		+"else{"
    		+"       __px += __julia_outside * z.re;"
    		+"       __py += __julia_outside * z.im;"
    		+"    }"
            + (context.isPreserveZCoordinate() ? "__pz += __julia_outside * __z;" : "");
  }
}
