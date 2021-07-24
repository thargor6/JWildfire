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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class CollideoscopeFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_NUM = "num";

  private static final String[] paramNames = {PARAM_A, PARAM_NUM};

  private double a = 0.20;
  private int num = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* collideoscope by Michael Faber, http://michaelfaber.deviantart.com/art/Collideoscope-251624597 */
    double a = atan2(pAffineTP.y, pAffineTP.x);
    double r = pAmount * sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    int alt;

    if (a >= 0.0) {
      alt = (int) (a * kn_pi);
      if (alt % 2 == 0) {
        a = alt * pi_kn + fmod(ka_kn + a, pi_kn);
      } else {
        a = alt * pi_kn + fmod(-ka_kn + a, pi_kn);
      }
    } else {
      alt = (int) (-a * kn_pi);
      if (alt % 2 != 0) {
        a = -(alt * pi_kn + fmod(-ka_kn - a, pi_kn));
      } else {
        a = -(alt * pi_kn + fmod(ka_kn - a, pi_kn));
      }
    }

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
    return new Object[]{a, num};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_NUM.equalsIgnoreCase(pName))
      num = limitIntVal(Tools.FTOI(pValue), 1, Integer.MAX_VALUE);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "collideoscope";
  }

  private double kn_pi, pi_kn, ka, ka_kn;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    kn_pi = (double) num * M_1_PI;
    pi_kn = M_PI / (double) num;
    ka = M_PI * a;
    ka_kn = ka / (double) num;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float _kn_pi = (float) __collideoscope_num * M_1_PI_F;\n"
        + "float _pi_kn = M_PI_F / (float) __collideoscope_num;\n"
        + "float _ka    = M_PI_F * __collideoscope_a;\n"
        + "float _ka_kn = _ka / (float) __collideoscope_num;\n"
        + "float a = atan2f(__y, __x);\n"
        + "float r = __collideoscope * __r;\n"
        + "int alt;\n"
        + "\n"
        + "if (a >= 0.f) {\n"
        + "    alt = (int) (a * _kn_pi);\n"
        + "    if (alt % 2 == 0)\n"
        + "        a = alt * _pi_kn + fmodf(_ka_kn + a, _pi_kn);\n"
        + "    else\n"
        + "        a = alt * _pi_kn + fmodf(-_ka_kn + a, _pi_kn);\n"
        + "}\n"
        + "else {\n"
        + "    alt = (int) (-a * _kn_pi);\n"
        + "    if (alt % 2 == 1)\n"
        + "        a = -(alt * _pi_kn + fmodf(-_ka_kn - a, _pi_kn));\n"
        + "    else\n"
        + "        a = -(alt * _pi_kn + fmodf(_ka_kn - a, _pi_kn));\n"
        + "}\n"
        + "\n"
        + "float s, c;\n"
        + "sincosf(a,  &s, &c);\n"
        + "\n"
        + "__px += r * c;\n"
        + "__py += r * s;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __collideoscope*__z;\n" : "");
  }
}
