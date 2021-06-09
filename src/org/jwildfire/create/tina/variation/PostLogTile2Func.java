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

import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.round;

public class PostLogTile2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "spreadx";
  private static final String PARAM_Y = "spready";
  private static final String PARAM_Z = "spreadz";

  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_Z};

  private double spreadx = 2.0;
  private double spready = 2.0;
  private double spreadz = 0.0;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Log_tile by Zy0rg implemented into JWildfire by Brad Stefanov
    //converted to a post variation by Whittaker Courtney

    double spreadx = -this.spreadx;
    if (pContext.random() < 0.5)
      spreadx = this.spreadx;
    double spready = -this.spready;
    if (pContext.random() < 0.5)
      spready = this.spready;
    double spreadz = -this.spreadz;
    if (pContext.random() < 0.5)
      spreadz = this.spreadz;
    pVarTP.x = pAmount * (pVarTP.x + spreadx * round(log(pContext.random())));
    pVarTP.y = pAmount * (pVarTP.y + spready * round(log(pContext.random())));
    pVarTP.z = pAmount * (pVarTP.z + spreadz * round(log(pContext.random())));
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{spreadx, spready, spreadz};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      spreadx = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      spready = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      spreadz = pValue;

    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "post_log_tile2";
  }

  @Override
  public int getPriority() {
    return 1;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float spreadx = -varpar->post_log_tile2_spreadx;\n"
        + "    if (RANDFLOAT() < 0.5)\n"
        + "      spreadx = varpar->post_log_tile2_spreadx;\n"
        + "    float spready = -varpar->post_log_tile2_spready;\n"
        + "    if (RANDFLOAT() < 0.5)\n"
        + "      spready = varpar->post_log_tile2_spready;\n"
        + "    float spreadz = -varpar->post_log_tile2_spreadz;\n"
        + "    if (RANDFLOAT() < 0.5)\n"
        + "      spreadz = varpar->post_log_tile2_spreadz;\n"
        + "    __px = varpar->post_log_tile2 * (__px + spreadx * round(logf(RANDFLOAT())));\n"
        + "    __py = varpar->post_log_tile2 * (__py + spready * round(logf(RANDFLOAT())));\n"
        + "    __pz = varpar->post_log_tile2 * (__pz + spreadz * round(logf(RANDFLOAT())));\n";
  }
}
