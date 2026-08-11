/*

  JWildfire - an image and animation processor written in Java

  Copyright (C) 1995-2026 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.exp;

public class MitosisFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SEP = "sep";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_PHASE = "phase";
  private static final String[] paramNames = {PARAM_SEP, PARAM_WIDTH, PARAM_PHASE};

  private double sep = 1.0;
  private double width = 0.5;
  private double phase = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double mi_w = Math.max(fabs(this.width), 0.01);
    double mi_ca = cos(this.phase);
    double mi_sa = sin(this.phase);
    double mi_u = pAffineTP.x * mi_ca + pAffineTP.y * mi_sa;
    double mi_v = -pAffineTP.x * mi_sa + pAffineTP.y * mi_ca;
    
    // Fixed: Replaced sw_tanh with native Math.tanh for the Java CPU layer
    double mi_pull = Math.tanh(mi_u / mi_w);
    double mi_new_u = mi_pull * this.sep * 0.5;
    double mi_neck = 1.0 - 0.5 * exp(-(mi_u * mi_u) / (mi_w * mi_w));
    double mi_new_v = mi_v * mi_neck;

    pVarTP.x += pAmount * (mi_new_u * mi_ca - mi_new_v * mi_sa);
    pVarTP.y += pAmount * (mi_new_u * mi_sa + mi_new_v * mi_ca);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{sep, width, phase}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEP.equalsIgnoreCase(pName)) sep = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName)) width = pValue;
    else if (PARAM_PHASE.equalsIgnoreCase(pName)) phase = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "mitosis"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float mi_sep = __mitosis_sep;\n"
         + "  float mi_w = fmaxf(fabsf(__mitosis_width), 0.01f);\n"
         + "  float mi_phase = __mitosis_phase;\n"
         + "  float mi_ca = cosf(mi_phase);\n"
         + "  float mi_sa = sinf(mi_phase);\n"
         + "  float mi_u = __x * mi_ca + __y * mi_sa;\n"
         + "  float mi_v = -__x * mi_sa + __y * mi_ca;\n"
         + "  float mi_pull = tanhf(mi_u / mi_w);\n"
         + "  float mi_new_u = mi_pull * mi_sep * 0.5f;\n"
         + "  float mi_neck = 1.0f - 0.5f * expf(-(mi_u * mi_u) / (mi_w * mi_w));\n"
         + "  float mi_new_v = mi_v * mi_neck;\n"
         + "  __px += __mitosis * (mi_new_u * mi_ca - mi_new_v * mi_sa);\n"
         + "  __py += __mitosis * (mi_new_u * mi_sa + mi_new_v * mi_ca);\n";
  }
}