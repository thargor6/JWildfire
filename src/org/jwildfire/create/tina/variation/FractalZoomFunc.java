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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class FractalZoomFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ZOOM = "zoom";
  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_ITERS = "iters";
  private static final String[] paramNames = {PARAM_ZOOM, PARAM_ANGLE, PARAM_ITERS};

  private double zoom = 2.0;
  private double angle = 0.5;
  private int iters = 3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double fz_x = pAffineTP.x;
    double fz_y = pAffineTP.y;
    double fz_cs = cos(this.angle);
    double fz_sn = sin(this.angle);

    for (int i = 0; i < this.iters; i++) {
      double fz_nx = fz_x * fz_cs - fz_y * fz_sn;
      double fz_ny = fz_x * fz_sn + fz_y * fz_cs;
      fz_x = fz_nx * this.zoom;
      fz_y = fz_ny * this.zoom;
    }

    pVarTP.x += pAmount * fz_x;
    pVarTP.y += pAmount * fz_y;
    
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() { return new Object[]{zoom, angle, iters}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ZOOM.equalsIgnoreCase(pName)) zoom = pValue;
    else if (PARAM_ANGLE.equalsIgnoreCase(pName)) angle = pValue;
    else if (PARAM_ITERS.equalsIgnoreCase(pName)) iters = (int) pValue;
    else throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "fractal_zoom"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "  float fz_x = __x;\n"
         + "  float fz_y = __y;\n"
         + "  float fz_cs = cosf(__fractal_zoom_angle);\n"
         + "  float fz_sn = sinf(__fractal_zoom_angle);\n"
         + "  for (int i = 0; i < __fractal_zoom_iters; i++) {\n"
         + "    float fz_nx = fz_x * fz_cs - fz_y * fz_sn;\n"
         + "    float fz_ny = fz_x * fz_sn + fz_y * fz_cs;\n"
         + "    fz_x = fz_nx * __fractal_zoom_zoom;\n"
         + "    fz_y = fz_ny * __fractal_zoom_zoom;\n"
         + "  }\n"
         + "  __px += __fractal_zoom * fz_x;\n"
         + "  __py += __fractal_zoom * fz_y;\n";
  }
}