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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class OakLeafFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_FILLED = "filled";
  private static final String[] paramNames = {PARAM_FILLED};

  private double filled = 0.25;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double t = -pAffineTP.getPrecalcAtan()*0.5-M_PI_2;
    double r = (filled > 0 && filled > pContext.random()) ? pAmount * pContext.random() * 2.0 : pAmount * 2.0;

    // oak leaf formula by Hamid Naderi Yeganeh: https://blogs.scientificamerican.com/guest-blog/how-to-draw-with-math/
    double lx = (pow(cos(t), 9.0))/100.0*pow(cos(5.0*t), 10.0) +sin(2.0*t)/4.0*(1.0-pow(sin(10.0*t), 2.0)/2.0)*(1.0-(pow(cos(t)*cos(3.0*t), 8.0)));
    double ly = sin(t)*(1.0-pow(sin(10.0*t), 2.0)/5.0*(0.5+pow(sin(2.0*t), 2.0)));

    pVarTP.x += r * lx;
    pVarTP.y += r * ly;

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
    return new Object[]{filled};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FILLED.equalsIgnoreCase(pName))
      filled = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "oak_leaf";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float t = -__phi*0.5-(PI*0.5f);\n"
        + "    float r = (__oak_leaf_filled > 0 && __oak_leaf_filled > RANDFLOAT()) ? __oak_leaf * RANDFLOAT() * 2.0 : __oak_leaf * 2.0;\n"
        + "\n"
        + "    \n"
//        + "    float lx = (powf(cosf(t), 9.0))/100.0*powf(cosf(5.0*t), 10.0) +sinf(2.0*t)/4.0*(1.0-powf(sinf(10.0*t), 2.0)/2.0)*(1.0-(powf(cosf(t)*cosf(3.0*t), 8.0)));\n"
        + "    float lx = (oak_leaf_ipow9f(cosf(t)))/100.0*oak_leaf_ipow10f(cosf(5.0*t)) +sinf(2.0*t)/4.0*(1.0-oak_leaf_ipow2f(sinf(10.0*t))/2.0)*(1.0-(oak_leaf_ipow8f(cosf(t)*cosf(3.0*t))));\n"
//        + "    float ly = sinf(t)*(1.0-powf(sinf(10.0*t), 2.0)/5.0*(0.5+powf(sinf(2.0*t), 2.0)));\n"
        + "    float ly = sinf(t)*(1.0-oak_leaf_ipow2f(sinf(10.0*t))/5.0*(0.5+oak_leaf_ipow2f(sinf(2.0*t))));\n"
        + "\n"
        + "    __px += r * lx;\n"
        + "    __py += r * ly;\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __oak_leaf * __z;\n" : "");
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float oak_leaf_ipow2f(float a) {\n"
            + "return a*a;\n"
            +"}\n\n"
            +"__device__ float oak_leaf_ipow8f(float a) {\n"
            + "  float b=a*a*a*a;\n"
            + "  return b*b;\n"
            +"}\n\n"
            +"__device__ float oak_leaf_ipow9f(float a) {\n"
            + "  float b=a*a*a;\n"
            + "  return b*b*b;\n"
            +"}\n\n"
            +"__device__ float oak_leaf_ipow10f(float a) {\n"
            + "  float b=a*a*a;\n"
            + "  return b*b*b*a;\n"
            +"}\n\n"
            ;
  }
}
