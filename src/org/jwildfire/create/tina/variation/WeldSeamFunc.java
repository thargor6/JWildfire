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

import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.exp;

public class WeldSeamFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PITCH = "pitch";
  private static final String PARAM_HEIGHT = "height";
  private static final String PARAM_WIDTH = "width";
  private static final String[] paramNames = {PARAM_PITCH, PARAM_HEIGHT, PARAM_WIDTH};

  private double pitch = 0.5;
  private double height = 0.2;
  private double width = 0.2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ws_bx = Math.floor(pAffineTP.x / this.pitch + 0.5) * this.pitch;
    double ws_dx = pAffineTP.x - ws_bx;
    double ws_dist2 = ws_dx * ws_dx + pAffineTP.y * pAffineTP.y;
    double ws_push = this.height * exp(-ws_dist2 / (this.width * this.width + 0.001));
    double ws_len = sqrt(ws_dist2) + 0.001;

    pVarTP.x += pAmount * (pAffineTP.x + ws_push * ws_dx / ws_len);
    pVarTP.y += pAmount * (pAffineTP.y + ws_push * pAffineTP.y / ws_len);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{pitch, height, width}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PITCH.equalsIgnoreCase(pName)) pitch = pValue;
    else if (PARAM_HEIGHT.equalsIgnoreCase(pName)) height = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName)) width = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "weld_seam"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float ws_bx = floorf(__x / __weld_seam_pitch + 0.5f) * __weld_seam_pitch;\n"
         + "  float ws_dx = __x - ws_bx;\n"
         + "  float ws_dist2 = ws_dx * ws_dx + __y * __y;\n"
         + "  float ws_push = __weld_seam_height * expf(-ws_dist2 / (__weld_seam_width * __weld_seam_width + 0.001f));\n"
         + "  float ws_len = sqrtf(ws_dist2) + 0.001f;\n"
         + "  __px += __weld_seam * (__x + ws_push * ws_dx / ws_len);\n"
         + "  __py += __weld_seam * (__y + ws_push * __y / ws_len);\n";
  }
}