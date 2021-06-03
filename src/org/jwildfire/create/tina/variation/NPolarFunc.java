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

public class NPolarFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PARITY = "parity";
  private static final String PARAM_N = "n";

  private static final String[] paramNames = {PARAM_PARITY, PARAM_N};

  private int parity = 0;
  private int n = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = (this._isodd != 0) ? pAffineTP.x : this._vvar * atan2(pAffineTP.x, pAffineTP.y);
    double y = (this._isodd != 0) ? pAffineTP.y : this._vvar_2 * log(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double angle = (atan2(y, x) + M_2PI * (pContext.random(Integer.MAX_VALUE) % (int) this._absn)) / this._nnz;
    double r = pAmount * pow(sqr(x) + sqr(y), this._cn) * ((this._isodd == 0) ? 1.0 : this.parity);
    double sina = sin(angle);
    double cosa = cos(angle);
    cosa *= r;
    sina *= r;
    x = (this._isodd != 0) ? cosa : (this._vvar_2 * log(cosa * cosa + sina * sina));
    y = (this._isodd != 0) ? sina : (this._vvar * atan2(cosa, sina));
    pVarTP.x += x;
    pVarTP.y += y;
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
    return new Object[]{parity, n};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PARITY.equalsIgnoreCase(pName))
      parity = Tools.FTOI(pValue);
    else if (PARAM_N.equalsIgnoreCase(pName))
      n = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "npolar";
  }

  private double _vvar;
  private double _vvar_2;

  private int _nnz;
  private int _isodd;

  private double _cn;
  private double _absn;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    this._nnz = (this.n == 0) ? 1 : this.n;
    this._vvar = pAmount / M_PI;
    this._vvar_2 = this._vvar * 0.5;
    this._absn = fabs(this._nnz);
    this._cn = 1.0 / this._nnz / 2.0;
    this._isodd = iabs(parity) % 2;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "int n = lroundf(varpar->npolar_n);\n"
        + "int parity  = lroundf(varpar->npolar_parity);\n"
        + "int _nnz = (n == 0) ? 1 : n;\n"
        + "float _vvar = varpar->npolar / PI;\n"
        + "float _vvar_2 = _vvar * 0.5;\n"
        + "float _absn = fabsf(_nnz);\n"
        + "float _cn = 1.0 / _nnz / 2.0;\n"
        + "float _isodd = (parity < 0 ? -parity : parity) % 2;\n"
        + "float x = (_isodd != 0) ? __x : _vvar * atan2f(__x, __y);\n"
        + "    float y = (_isodd != 0) ? __y : _vvar_2 * logf(__x * __x + __y * __y);\n"
        + "    float angle = (atan2f(y, x) + (2.0f*PI) * (lroundf(RANDFLOAT()*0x0000ffff) % (int) _absn)) / _nnz;\n"
        + "    float r = varpar->npolar * powf(x*x + y*y, _cn) * ((_isodd == 0) ? 1.0 : parity);\n"
        + "    float sina = sinf(angle);\n"
        + "    float cosa = cosf(angle);\n"
        + "    cosa *= r;\n"
        + "    sina *= r;\n"
        + "    x = (_isodd != 0) ? cosa : (_vvar_2 * logf(cosa * cosa + sina * sina));\n"
        + "    y = (_isodd != 0) ? sina : (_vvar * atan2f(cosa, sina));\n"
        + "    __px += x;\n"
        + "    __py += y;\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->npolar*__z;\n" : "");
  }
}
