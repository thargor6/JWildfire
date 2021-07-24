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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.exp;

public class CurveFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XAMP = "xamp";
  private static final String PARAM_YAMP = "yamp";
  private static final String PARAM_XLENGTH = "xlength";
  private static final String PARAM_YLENGTH = "ylength";

  private static final String[] paramNames = {PARAM_XAMP, PARAM_YAMP, PARAM_XLENGTH, PARAM_YLENGTH};

  private double xAmp = 0.25;
  private double yAmp = 0.5;
  private double xLength = 1.0;
  private double yLength = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Curve in the Apophysis Plugin Pack */
    pVarTP.x += pAmount * (pAffineTP.x + xAmp * exp(-pAffineTP.y * pAffineTP.y / _pc_xlen));
    pVarTP.y += pAmount * (pAffineTP.y + yAmp * exp(-pAffineTP.x * pAffineTP.x / _pc_ylen));
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
    return new Object[]{xAmp, yAmp, xLength, yLength};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XAMP.equalsIgnoreCase(pName))
      xAmp = pValue;
    else if (PARAM_YAMP.equalsIgnoreCase(pName))
      yAmp = pValue;
    else if (PARAM_XLENGTH.equalsIgnoreCase(pName))
      xLength = pValue;
    else if (PARAM_YLENGTH.equalsIgnoreCase(pName))
      yLength = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "curve";
  }

  private double _pc_xlen, _pc_ylen;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _pc_xlen = xLength * xLength;
    _pc_ylen = yLength * yLength;
    if (_pc_xlen < 1E-20)
      _pc_xlen = 1E-20;
    if (_pc_ylen < 1E-20)
      _pc_ylen = 1E-20;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float pc_xlen = __curve_xlength*__curve_xlength;\n"
        + "float pc_ylen = __curve_ylength*__curve_ylength;\n"
        + "__px += __curve*(__x+__curve_xamp*expf(-__y*__y/pc_xlen));\n"
        + "__py += __curve*(__y+__curve_yamp*expf(-__x*__x/pc_ylen));\n"
        + (context.isPreserveZCoordinate() ? "__pz += __curve*__z;\n" : "");
  }
}
