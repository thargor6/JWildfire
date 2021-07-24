/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2011 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.*;

public class TargetSpFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_EVEN = "twist";
  private static final String PARAM_NUMSP = "n_of_sp";
  private static final String PARAM_SIZE = "size";
  private static final String PARAM_TIGHTNESS = "tightness";
  private static final String[] paramNames = {PARAM_EVEN, PARAM_NUMSP, PARAM_SIZE, PARAM_TIGHTNESS};

  private double twist = 0.0;
  private int n_of_sp = 1;
  private double size = 0.5 + Math.random() * 3.0;
  private double tightness = 0.5 + Math.random() * 0.25;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* target by Michael Faber,log spiral tweak by Dark-Beam */
    double a = atan2(pAffineTP.y, pAffineTP.x);
    double r = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    //double t = log(r);
    double t = tightness * log(r) + n_of_sp * M_1_PI * (a + M_PI);
    if (t < 0.0)
      t -= t_size_2;

    t = fmod(fabs(t), size);

    if (t < t_size_2)
      a += _rota;
    else
      a += _rotb;

    double s = sin(a);
    double c = cos(a);

    pVarTP.x += r * c;
    pVarTP.y += r * s;
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
    return new Object[]{twist, n_of_sp, size, tightness};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_EVEN.equalsIgnoreCase(pName))
      twist = pValue;
    else if (PARAM_NUMSP.equalsIgnoreCase(pName))
      n_of_sp = (int) pValue;
    else if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = pValue;
    else if (PARAM_TIGHTNESS.equalsIgnoreCase(pName))
      tightness = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "target_sp";
  }

  private double t_size_2;
  private double _rota;
  private double _rotb;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    t_size_2 = 0.5 * size;
    _rota = M_PI * twist;
    _rotb = -M_PI + _rota;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float t_size_2 = 0.5f * __target_sp_size;\n"
        + "float _rota = PI * __target_sp_twist;\n"
        + "float _rotb = -PI + _rota;\n"
        + "float a = atan2f(__y, __x);\n"
        + "    float r = sqrtf(__x*__x + __y*__y);\n"
        + "    float t = __target_sp_tightness * logf(r) + __target_sp_n_of_sp * (a + PI) / PI;\n"
        + "    if (t < 0.0)\n"
        + "      t -= t_size_2;\n"
        + "\n"
        + "    t = fmodf(fabsf(t), __target_sp_size);\n"
        + "\n"
        + "    if (t < t_size_2)\n"
        + "      a += _rota;\n"
        + "    else\n"
        + "      a += _rotb;\n"
        + "\n"
        + "    float s = sinf(a);\n"
        + "    float c = cosf(a);\n"
        + "\n"
        + "    __px += r * c;\n"
        + "    __py += r * s;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __target_sp*__z;\n" : "");
  }
}