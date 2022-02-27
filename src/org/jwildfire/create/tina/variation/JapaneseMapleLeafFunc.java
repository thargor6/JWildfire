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

public class JapaneseMapleLeafFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_FILLED = "filled";
  private static final String[] paramNames = {PARAM_FILLED};

  private double filled = 0.15;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double t = pAffineTP.getPrecalcAtan()*0.5+M_PI_2;
    double scale = 0.667;
    double r = (filled > 0 && filled > pContext.random()) ? pAmount * pContext.random() * scale : pAmount * scale;

    // japanese maple leaf formula by Hamid Naderi Yeganeh: https://blogs.scientificamerican.com/guest-blog/how-to-draw-with-math/
    double lx = (4.0/15.0+pow(cos(8.0*t),2.0) + 1.0/5.0 * pow(cos(8.0*t)*cos(24.0*t),10.0) +
            (1.0/6.0 * pow(cos(80.0*t),2.0) + 1.0/15.0 * pow(cos(80.0*t),10.0)))*
                    (1.0-pow(sin(8.0*t),10.0)) *
                    sin(2.0*t)*(1.0+sin(t))*(1-pow(cos(t),10.0) * pow(cos(3.0*t),2.0)+1.0/30.0*pow(cos(t),9.0)*pow(cos(5.0*t),10.0));

    double ly = (8.0/30.0 + pow(cos(8.0*t),2.0) + 1.0/5.0*pow(cos(8.0*t)*cos(24.0*t),10.0)
    +(1.0/6.0 * pow(cos(80.0*t),2.0) +1.0/15.0*pow(cos(80.0*t),10.0))
    *(1.0-pow(cos(t),10.0) *pow(cos(3.0*t),2.0)) * (1.0-pow(sin(8.0*t),10.0)))
    *cos(2.0*t)*(1.0+sin(t)+pow(cos(t)*cos(3.0*t),10.0));

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
    return "japanese_maple_leaf";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return " float t = __phi*0.5+(PI*0.5f);\n"
        + "    float scale = 0.667f;\n"
        + "    float r = (__japanese_maple_leaf_filled > 0 && __japanese_maple_leaf_filled > RANDFLOAT()) ? __japanese_maple_leaf * RANDFLOAT() * scale : __japanese_maple_leaf * scale;\n"
        + "\n"
        + "    \n"

//        + "    float lx = (4.0f/15.0f+powf(cosf(8.0f*t),2.0f) + 1.0f/5.0f * powf(cosf(8.0f*t)*cosf(24.0f*t),10.0f) +\n"
//        + "            (1.0f/6.0f * powf(cosf(80.0f*t),2.0f) + 1.0f/15.0f * powf(cosf(80.0f*t),10.0f)))*\n"
//        + "                    (1.0f-powf(sinf(8.0f*t),10.0f)) *\n"
//        + "                    sinf(2.0f*t)*(1.0f+sinf(t))*(1.0f-powf(cosf(t),10.0f) * powf(cosf(3.0f*t),2.0f)+1.0f/30.0f*powf(cosf(t),9.0f)*powf(cosf(5.0f*t),10.0f));\n"

        + "    float lx = (4.0f/15.0f+jmaple_leaf_ipow2f(cosf(8.0f*t)) + 1.0f/5.0f * jmaple_leaf_ipow10f(cosf(8.0f*t)*cosf(24.0f*t)) +\n"
        + "            (1.0f/6.0f * jmaple_leaf_ipow2f(cosf(80.0f*t)) + 1.0f/15.0f * jmaple_leaf_ipow10f(cosf(80.0f*t))))*\n"
        + "                    (1.0f-jmaple_leaf_ipow10f(sinf(8.0f*t))) *\n"
        + "                    sinf(2.0f*t)*(1.0f+sinf(t))*(1.0f-jmaple_leaf_ipow10f(cosf(t)) * jmaple_leaf_ipow2f(cosf(3.0f*t))+1.0f/30.0f*jmaple_leaf_ipow9f(cosf(t))*jmaple_leaf_ipow10f(cosf(5.0f*t)));\n"

            + "\n"
//        + "    float ly = (8.0f/30.0f + powf(cosf(8.0f*t),2.0f) + 1.0f/5.0f*powf(cosf(8.0f*t)*cosf(24.0f*t),10.0f)\n"
//        + "    +(1.0f/6.0f * powf(cosf(80.0f*t),2.0f) +1.0f/15.0f*powf(cosf(80.0f*t),10.0f))\n"
//        + "    *(1.0f-powf(cosf(t),10.0f) *powf(cosf(3.0f*t),2.0f)) * (1.0f-powf(sinf(8.0f*t),10.0f)))\n"
//        + "    *cosf(2.0f*t)*(1.0f+sinf(t)+powf(cosf(t)*cosf(3.0f*t),10.0f));\n"
          + "    float ly = (8.0f/30.0f + jmaple_leaf_ipow2f(cosf(8.0f*t)) + 1.0f/5.0f*jmaple_leaf_ipow10f(cosf(8.0f*t)*cosf(24.0f*t))\n"
          + "    +(1.0f/6.0f * jmaple_leaf_ipow2f(cosf(80.0f*t)) +1.0f/15.0f*jmaple_leaf_ipow10f(cosf(80.0f*t)))\n"
          + "    *(1.0f-jmaple_leaf_ipow10f(cosf(t)) *jmaple_leaf_ipow2f(cosf(3.0f*t))) * (1.0f-jmaple_leaf_ipow10f(sinf(8.0f*t))))\n"
          + "    *cosf(2.0f*t)*(1.0f+sinf(t)+jmaple_leaf_ipow10f(cosf(t)*cosf(3.0f*t)));\n"
        + "\n"
        + "    __px += r * lx;\n"
        + "    __py += r * ly;\n"
        + "\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __japanese_maple_leaf * __z;\n" : "");
  }

  @Override
  public String getGPUFunctions(FlameTransformationContext context) {
    return "__device__ float jmaple_leaf_ipow2f(float a) {\n"
            + "return a*a;\n"
            +"}\n\n"
            +"__device__ float jmaple_leaf_ipow9f(float a) {\n"
            + "  float b=a*a*a;\n"
            + "  return b*b*b;\n"
            +"}\n\n"
            +"__device__ float jmaple_leaf_ipow10f(float a) {\n"
            + "  float b=a*a*a;\n"
            + "  return b*b*b*a;\n"
            +"}\n\n";
  }
}

