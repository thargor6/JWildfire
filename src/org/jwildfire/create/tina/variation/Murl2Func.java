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

import static org.jwildfire.base.mathlib.MathLib.*;

public class Murl2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  /*
   Original function written in C by Peter Sdobnov (Zueuk).
   Transcribed into Java by Nic Anderson (chronologicaldot)
   */

  private double _p2, _invp, _vp;

  // temp variables (instantiated here to speed up processing)
  private double _sina, _cosa, _a, _r, _re, _im, _rl;

  private static final String PARAM_C = "c";
  public static final String PARAM_POWER = "power";

  private static final String[] paramNames = {PARAM_C, PARAM_POWER};

  private double c = 0.1;
  private int power = 3;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    _p2 = (double) power / 2.0;

    if (power != 0) {
      _invp = 1.0 / (double) power;
      if (c == -1) {
        _vp = 0;
      } else {
        _vp = pAmount * pow(c + 1, 2.0 / ((double) power));
      }
    } else {
      _invp = 100000000000.0;
      _vp = pAmount * pow(c + 1, 4 /*Normally infinity, but we let this be a special case*/);
    }

    _a = atan2(pAffineTP.y, pAffineTP.x) * (double) power;
    _sina = sin(_a);
    _cosa = cos(_a);

    _r = c * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), _p2);

    _re = _r * _cosa + 1;
    _im = _r * _sina;

    _r = pow(sqr(_re) + sqr(_im), _invp);
    _a = atan2(_im, _re) * 2.0 * _invp;
    _re = _r * cos(_a);
    _im = _r * sin(_a);

    _rl = _vp / sqr(_r);

    pVarTP.x += _rl * (pAffineTP.x * _re + pAffineTP.y * _im);
    pVarTP.y += _rl * (pAffineTP.y * _re - pAffineTP.x * _im);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "murl2";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{c, power};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = (int) pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _invp, _vp, _a, _sina, _cosa, _r, _re, _im, _rl;" +
            "float _p2 = (float) varpar->murl2_power / 2.0f;\n" +
            "\n" +
            "    if (varpar->murl2_power != 0.f) {\n" +
            "      _invp = 1.0f / (float) varpar->murl2_power;\n" +
            "      if (varpar->murl2_c == -1.f) {\n" +
            "        _vp = 0.f;\n" +
            "      } else {\n" +
            "        _vp = varpar->murl2 * powf(varpar->murl2_c + 1.f, 2.0f / ((float) varpar->murl2_power));\n" +
            "      }\n" +
            "    } else {\n" +
            "      _invp = 100000000000.0f;\n" +
            "      _vp = varpar->murl2 * powf(varpar->murl2_c + 1.f, 4.f /*Normally infinity, but we let this be a special case*/);\n" +
            "    }\n" +
            "\n" +
            "    _a = atan2f(__y, __x) * (float) varpar->murl2_power;\n" +
            "    _sina = sinf(_a);\n" +
            "    _cosa = cosf(_a);\n" +
            "\n" +
            "    _r = varpar->murl2_c * powf(__x * __x + __y * __y, _p2);\n" +
            "\n" +
            "    _re = _r * _cosa + 1.f;\n" +
            "    _im = _r * _sina;\n" +
            "\n" +
            "    _r = powf(_re*_re + _im*_im, _invp);\n" +
            "    _a = atan2f(_im, _re) * 2.0f * _invp;\n" +
            "    _re = _r * cosf(_a);\n" +
            "    _im = _r * sinf(_a);\n" +
            "\n" +
            "    _rl = _vp / (_r*_r);\n" +
            "\n" +
            "    __px += _rl * (__x * _re + __y * _im);\n" +
            "    __py += _rl * (__y * _re - __x * _im);\n" +
            (context.isPreserveZCoordinate() ? "__pz += varpar->murl2 * __z;\n" : "");
  }
}
