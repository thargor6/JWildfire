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

public class Waves4Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scalex";
  private static final String PARAM_SCALEY = "scaley";
  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";
  private static final String PARAM_CONT = "cont";
  private static final String PARAM_YFACT = "yfact";

  
  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALEY, PARAM_FREQX, PARAM_FREQY, PARAM_CONT, PARAM_YFACT};

  private double scalex = 0.05;
  private double scaley = 0.05;
  private double freqx = 7.0;
  private double freqy = 13.0;
  private int cont = 0;
  private double yfact = 0.1;


  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* waves4 from Tatyana Zabanova converted by Brad Stefanov https://www.deviantart.com/tatasz/art/Weird-Waves-Plugin-Pack-1-783560564*/
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	
	double ax = floor(y0 * freqx / M_2PI);

    ax = sin(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * yfact) * 43758.5453;
    ax = ax - (int) ax;
    if (cont == 1) ax = (ax > 0.5) ? 1.0 : 0.0;

    
    pVarTP.x += pAmount * (x0 + sin(y0 * freqx) * ax * ax * scalex);
    pVarTP.y += pAmount * (y0 + sin(x0 * freqy) * scaley);
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
    return new Object[]{scalex, scaley, freqx, freqy, cont, yfact};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scalex = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaley = pValue;
    else if (PARAM_FREQX.equalsIgnoreCase(pName))
      freqx = pValue;
    else if (PARAM_FREQY.equalsIgnoreCase(pName))
      freqy = pValue;
    else if (PARAM_CONT.equalsIgnoreCase(pName))
      cont = (int) limitVal(pValue, 0, 1);
    else if (PARAM_YFACT.equalsIgnoreCase(pName))
      yfact = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "waves4";
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

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "\tfloat x0 = __x;\n"
        + "\tfloat y0 = __y;\n"
        + "\t\n"
        + "\tfloat ax = floorf(y0 * __waves4_freqx / (2.0f*PI));\n"
        + "\n"
        + "    ax = sinf(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * __waves4_yfact) * 43758.5453;\n"
        + "    ax = ax - (int) ax;\n"
        + "    if (lroundf(__waves4_cont) == 1) ax = (ax > 0.5) ? 1.0 : 0.0;\n"
        + "\n"
        + "    \n"
        + "    __px += __waves4 * (x0 + sinf(y0 * __waves4_freqx) * ax * ax * __waves4_scalex);\n"
        + "    __py += __waves4 * (y0 + sinf(x0 * __waves4_freqy) * __waves4_scaley);\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __waves4 * __z;\n" : "");
  }
}
