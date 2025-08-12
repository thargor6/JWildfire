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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class HypercropFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_N = "n";
  private static final String PARAM_RAD = "rad";
  private static final String PARAM_ZERO = "zero";

  private static final String[] paramNames = {PARAM_N, PARAM_RAD, PARAM_ZERO};

  private int n = 4;
  private double rad = 1.0;
  private double zero = 0.0;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {

    // "Hypercrop" variation created by tatasz implemented into JWildfire by Brad
    // Stefanov https://www.deviantart.com/tatasz/art/Hyperstuff-721510796
    double coef = n * 0.5 / M_PI;
    double FX = pAffineTP.x;
    double FY = pAffineTP.y;
    double FZ = pAffineTP.z;
    double a0 = M_PI / n;
    double len = 1 / cos(a0);
    double d = rad * sin(a0) * len;
    double angle = atan2(pAffineTP.y, pAffineTP.x);
    angle = floor(angle * coef) / coef + M_PI / n;
    double x0 = cos(angle) * len;
    double y0 = sin(angle) * len;
    if (sqrt(sqr(pAffineTP.x - x0) + sqr(pAffineTP.y - y0)) < d) {
      if (zero > 1.5) {
        FX = x0;
        FY = y0;
        FZ = 0.0;
      } else {
        if (zero > 0.5) {
          FX = 0.0;
          FY = 0.0;
          FZ = 0.0;
        } else {
          double rangle = atan2(pAffineTP.y - y0, pAffineTP.x - x0);
          FX = x0 + cos(rangle) * d;
          FY = y0 + sin(rangle) * d;
          FZ = 0.0;
        }
      }
    }

    pVarTP.x += FX * pAmount;
    pVarTP.y += FY * pAmount;
    pVarTP.z += FZ * pAmount;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{n, rad, zero};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName))
      n = Tools.FTOI(pValue);
    else if (PARAM_RAD.equalsIgnoreCase(pName))
      rad = pValue;
    else if (PARAM_ZERO.equalsIgnoreCase(pName))
      zero = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hypercrop";
  }
  
  @Override
  public void randomize() {
    n = (int) (Math.random() * 18 + 3);
    rad = Math.random();
    zero = (int) (Math.random() * 3);
  }
  
  @Override
  public void mutate(double pAmount) {
    switch ((int) (Math.random() * 3)) {
    case 0:
      n += pAmount;
      if (n < 3) n = 3;
      break;
    case 1:
      rad += mutateStep(rad, pAmount);
      break;
    case 2:
      zero += 1;
      if (zero > 2) zero = 0;
      break;
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_CROP,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float coef = __hypercrop_n * 0.5 / PI;"
    		+"    float FX = __x;"
    		+"    float FY = __y;"
    		+"    float FZ = __z;"
    		+"    float a0 = PI / __hypercrop_n;"
    		+"    float len = 1.0 / cosf(a0);"
    		+"    float d =  __hypercrop_rad  * sinf(a0) * len;"
    		+"    float angle = atan2f(__y, __x);"
    		+"    angle = floorf(angle * coef) / coef + PI / __hypercrop_n;"
    		+"    float x0 = cosf(angle) * len;"
    		+"    float y0 = sinf(angle) * len;"
    		+"    if (sqrtf((__x - x0)*(__x - x0) + (__y - y0)*(__y - y0)) < d) {"
    		+"      if ( __hypercrop_zero  > 1.5) {"
    		+"        FX = x0;"
    		+"        FY = y0;"
    		+"        FZ = 0.0;"
    		+"      } else {"
    		+"        if ( __hypercrop_zero  > 0.5) {"
    		+"          FX = 0.0;"
    		+"          FY = 0.0;"
    		+"          FZ = 0.0;"
    		+"        } else {"
    		+"          float rangle = atan2f(__y - y0, __x - x0);"
    		+"          FX = x0 + cosf(rangle) * d;"
    		+"          FY = y0 + sinf(rangle) * d;"
    		+"          FZ = 0.0;"
    		+"        }"
    		+"      }"
    		+"    }"
    		+"    __px += FX * __hypercrop;"
    		+"    __py += FY * __hypercrop;"
    		+"    __pz += FZ * __hypercrop;";
  }
}
