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

public class BlobFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_LOW = "low";
  private static final String PARAM_HIGH = "high";
  private static final String PARAM_WAVES = "waves";
  private static final String[] paramNames = {PARAM_LOW, PARAM_HIGH, PARAM_WAVES};

  private double low = 0.3;
  private double high = 1.2;
  private double waves = 6;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double a = atan2(pAffineTP.x, pAffineTP.y);
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    r = r * (low + (high - low) * (0.5 + 0.5 * sin(waves * a)));
    double nx = sin(a) * r;
    double ny = cos(a) * r;

    pVarTP.x += pAmount * nx;
    pVarTP.y += pAmount * ny;
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
    return new Object[]{low, high, waves};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LOW.equalsIgnoreCase(pName))
      low = pValue;
    else if (PARAM_HIGH.equalsIgnoreCase(pName))
      high = pValue;
    else if (PARAM_WAVES.equalsIgnoreCase(pName))
      waves = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "blob";
  }
  
  @Override
  public void randomize() {
  	if (Math.random() < 0.75) {
  		low = Math.random() * Math.random();
  		high = low + Math.random();
  	} else {
  		low = Math.random() * 2.0 - 1.0;
  		high = low + Math.random() * 2.0;
  	}
  	if (Math.random() < 0.5) {
  		waves = Math.random() * 6.0 + 3.0;
  	} else {
  		waves = Math.random() * 30.0 + 1.0;
  	}
  	if (Math.random() < 0.75) {
  		waves = Math.round(waves);
  	}
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "__px += __blob*(__blob_low+(__blob_high-__blob_low)*.5f*(sinf(__blob_waves*__phi)+1.f))*__x;\n"
        + "__py += __blob*(__blob_low+(__blob_high-__blob_low)*.5f*(sinf(__blob_waves*__phi)+1.f))*__y;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __blob*__z;\n" : "");
  }
}
