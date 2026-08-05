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

public class LeatherFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_ROUGH = "rough";
  private static final String[] paramNames = {PARAM_SCALE, PARAM_ROUGH};

  private double scale = 2.0;
  private double rough = 0.3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double lt_scale = Math.max(fabs(this.scale), 0.01);
    double lt_coarse = this.rough * sin(lt_scale * pAffineTP.x) * cos(lt_scale * pAffineTP.y);
    double lt_fine = this.rough * 0.3 * sin(lt_scale * 3.0 * pAffineTP.y) * cos(lt_scale * 3.0 * pAffineTP.x);

    pVarTP.x += pAmount * (pAffineTP.x + lt_coarse);
    pVarTP.y += pAmount * (pAffineTP.y + lt_fine);
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{scale, rough}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_ROUGH.equalsIgnoreCase(pName)) rough = pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "leather"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float lt_scale = fmaxf(fabsf(__leather_scale), 0.01f);\n"
         + "  float lt_coarse = __leather_rough * sinf(lt_scale * __x) * cosf(lt_scale * __y);\n"
         + "  float lt_fine = __leather_rough * 0.3f * sinf(lt_scale * 3.0f * __y) * cosf(lt_scale * 3.0f * __x);\n"
         + "  __px += __leather * (__x + lt_coarse);\n"
         + "  __py += __leather * (__y + lt_fine);\n";
  }
}