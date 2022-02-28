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

public class ShredlinFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X_DISTANCE = "xdistance";
  private static final String PARAM_X_WIDTH = "xwidth";
  private static final String PARAM_Y_DISTANCE = "ydistance";
  private static final String PARAM_Y_WIDTH = "ywidth";

  private static final String[] paramNames = {PARAM_X_DISTANCE, PARAM_X_WIDTH, PARAM_Y_DISTANCE, PARAM_Y_WIDTH};

  private double xdistance = 1.0;
  private double xwidth = 0.5;
  private double ydistance = 1.0;
  private double ywidth = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Shredlin by Zy0rg
    double sxd = xdistance;
    double sxw = xwidth;
    double syd = ydistance;
    double syw = ywidth;
    double vv = pAmount;

    int xpos = pAffineTP.x < 0 ? 1 : 0;
    int ypos = pAffineTP.y < 0 ? 1 : 0;
    double xrng = pAffineTP.x / sxd;
    double yrng = pAffineTP.y / syd;

    pVarTP.x = vv * sxd * ((xrng - (int) xrng) * sxw + (int) xrng + (0.5 - xpos) * (1 - sxw));
    pVarTP.y = vv * syd * ((yrng - (int) yrng) * syw + (int) yrng + (0.5 - ypos) * (1 - syw));

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
    return new Object[]{xdistance, xwidth, ydistance, ywidth};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X_DISTANCE.equalsIgnoreCase(pName))
      xdistance = pValue;
    else if (PARAM_X_WIDTH.equalsIgnoreCase(pName))
      xwidth = limitVal(pValue, -1.0, 1.0);
    else if (PARAM_Y_DISTANCE.equalsIgnoreCase(pName))
      ydistance = pValue;
    else if (PARAM_Y_WIDTH.equalsIgnoreCase(pName))
      ywidth = limitVal(pValue, -1.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "shredlin";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float sxd = __shredlin_xdistance;\n"
        + "float sxw = __shredlin_xwidth;\n"
        + "float syd = __shredlin_ydistance;\n"
        + "float syw = __shredlin_ywidth;\n"
        + "float vv  = __shredlin;\n"
        + "\n"
        + "float xpos = __x < 0 ? 1.f : 0.f;\n"
        + "float ypos = __y < 0 ? 1.f : 0.f;\n"
        + "float xrng = __x / sxd;\n"
        + "float yrng = __y / syd;\n"
        + "\n"
        + "__px += vv * sxd * ((xrng - (int)xrng) * sxw + (int)xrng + (0.5f - xpos) * (1.f - sxw));\n"
        + "__py += vv * syd * ((yrng - (int)yrng) * syw + (int)yrng + (0.5f - ypos) * (1.f - syw));\n"
        + (context.isPreserveZCoordinate() ? "__pz += __shredlin * __z;\n" : "");
  }
}
