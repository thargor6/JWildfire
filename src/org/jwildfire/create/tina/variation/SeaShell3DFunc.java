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


import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class SeaShell3DFunc extends VariationFunc implements SupportsGPU {
  /**
   * Sea Shell
   *
   * @author Jesus Sosa
   * @date February 1, 2018
   * based on a work of:
   * http://paulbourke.net/geometry/shell/
   * parameters
   * n: number of Spirals
   * a: final Shell Radius
   * b: height
   * c: inner shell radius
   */

  private static final long serialVersionUID = 1L;

  private static final String PARAM_FINAL_RADIUS = "final_radius";
  private static final String PARAM_HEIGHT = "height";
  private static final String PARAM_INNER = "inner_radius";
  private static final String PARAM_N_SPIRALS = "nSpirals";

  private static final String[] paramNames = {PARAM_FINAL_RADIUS, PARAM_HEIGHT, PARAM_INNER, PARAM_N_SPIRALS};

  double final_radius = 0.25;
  double height = 3.5;
  double inner_radius = 0.4;
  int nSpirals = 3;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /**
     * Sea Shell
     * Reference
     * http://paulbourke.net/geometry/shell/
     * parameters
     * n: number of Spirals
     * a: final Shell Radius
     * b: height
     * c: inner shell radius
     */
    double t;
    double s;
    t = MathLib.M_2PI * pContext.random();
    s = MathLib.M_2PI * pContext.random();

    double x = final_radius * (1 - t / MathLib.M_2PI) * cos(nSpirals * t) * (1.0 + cos(s)) + inner_radius * cos(nSpirals * t);
    double y = final_radius * (1 - t / MathLib.M_2PI) * sin(nSpirals * t) * (1.0 + cos(s)) + inner_radius * sin(nSpirals * t);
    double z = height * t / MathLib.M_2PI + final_radius * (1 - t / MathLib.M_2PI) * sin(s);

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;
    pVarTP.z += z * pAmount;
  }

  public String getName() {
    return "seashell3D";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{final_radius, height, inner_radius, nSpirals};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_FINAL_RADIUS)) {
      final_radius = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_HEIGHT)) {
      height = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_INNER)) {
      inner_radius = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_N_SPIRALS)) {
      nSpirals = (int) pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "   int nSpirals = lroundf(__seashell3D_nSpirals);\n"
        + "    float s, t;\n"
        + "    t = 2.0f*PI * RANDFLOAT();\n"
        + "    s = 2.0f*PI * RANDFLOAT();\n"
        + "\n"
        + "    float x = __seashell3D_final_radius * (1 - t / (2.0f*PI)) * cosf(nSpirals * t) * (1.0 + cosf(s)) + __seashell3D_inner_radius * cosf(nSpirals * t);\n"
        + "    float y = __seashell3D_final_radius * (1 - t / (2.0f*PI)) * sinf(nSpirals * t) * (1.0 + cosf(s)) + __seashell3D_inner_radius * sinf(nSpirals * t);\n"
        + "    float z = __seashell3D_height * t / (2.0f*PI) + __seashell3D_final_radius * (1 - t / (2.0f*PI)) * sinf(s);\n"
        + "\n"
        + "    __px += x * __seashell3D;\n"
        + "    __py += y * __seashell3D;\n"
        + "    __pz += z * __seashell3D;\n";
  }
}
