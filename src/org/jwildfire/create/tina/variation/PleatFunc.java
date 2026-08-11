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

public class PleatFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_DEPTH = "depth";
  private static final String[] paramNames = {PARAM_WIDTH, PARAM_DEPTH};

  private double width = 0.5;
  private double depth = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double pl_width = Math.max(fabs(this.width), 0.01);
    double pl_cell = Math.floor(pAffineTP.x / pl_width);
    double pl_lx = pAffineTP.x / pl_width - pl_cell;
    double pl_crease = this.depth * (1.0 - fabs(2.0 * pl_lx - 1.0));
    
    double row_mod = pl_cell % 2.0;
    if (row_mod < 0) row_mod += 2.0;
    double pl_dir = row_mod > 0.5 ? 1.0 : -1.0;

    pVarTP.x += pAmount * pAffineTP.x;
    pVarTP.y += pAmount * (pAffineTP.y + pl_crease * pl_dir);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{width, depth}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_WIDTH.equalsIgnoreCase(pName)) width = pValue;
    else if (PARAM_DEPTH.equalsIgnoreCase(pName)) depth = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "pleat"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float pl_width = fmaxf(fabsf(__pleat_width), 0.01f);\n"
         + "  float pl_cell = floorf(__x / pl_width);\n"
         + "  float pl_lx = __x / pl_width - pl_cell;\n"
         + "  float pl_crease = __pleat_depth * (1.0f - fabsf(2.0f * pl_lx - 1.0f));\n"
         + "  float raw_mod = fmod(pl_cell, 2.0f);\n"
         + "  if (raw_mod < 0.0f) raw_mod += 2.0f;\n"
         + "  float pl_dir = raw_mod > 0.5f ? 1.0f : -1.0f;\n"
         + "  __px += __pleat * __x;\n"
         + "  __py += __pleat * (__y + pl_crease * pl_dir);\n";
  }
}