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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class SinusGridFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_AMPX = "ampx";
  private static final String PARAM_AMPY = "ampy";
  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";

  private static final String[] paramNames = {PARAM_AMPX, PARAM_AMPY, PARAM_FREQX, PARAM_FREQY};

  private double ampx = 0.5;
  private double ampy = 0.5;
  private double freqx = 1.0;
  private double freqy = 1.0;

  double lerp(double a, double b, double p) {
    return a + p * (b - a);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* SinusGrid, originally written by Georg K. (http://xyrus02.deviantart.com) */
    double x = pAffineTP.x, y = pAffineTP.y;
    double sx = -1.0 * MathLib.cos(x * _fx);
    double sy = -1.0 * MathLib.cos(y * _fy);
    double tx = lerp(pAffineTP.x, sx, ampx), ty = lerp(pAffineTP.y, sy, ampy), tz = pAffineTP.z;
    pVarTP.x += pAmount * tx;
    pVarTP.y += pAmount * ty;
    pVarTP.z += pAmount * tz;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{ampx, ampy, freqx, freqy};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_AMPX.equalsIgnoreCase(pName))
      ampx = pValue;
    else if (PARAM_AMPY.equalsIgnoreCase(pName))
      ampy = pValue;
    else if (PARAM_FREQX.equalsIgnoreCase(pName))
      freqx = pValue;
    else if (PARAM_FREQY.equalsIgnoreCase(pName))
      freqy = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "sinusgrid";
  }

  private double _fx, _fy;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _fx = freqx * MathLib.M_2PI;
    _fy = freqy * MathLib.M_2PI;
    if (_fx == 0.0) _fx = MathLib.EPSILON;
    if (_fy == 0.0) _fy = MathLib.EPSILON;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float ax = varpar->sinusgrid_ampx;\n"
        + "float ay = varpar->sinusgrid_ampy;\n"
        + "float fx = varpar->sinusgrid_freqx * 2.f*M_PI_F;\n"
        + "float fy = varpar->sinusgrid_freqy * 2.f*M_PI_F;\n"
        + "fx = fx == 0.f ? 1.e-10f : fx;\n"
        + "fy = fy == 0.f ? 1.e-10f : fy;\n"
        + "\n"
        + "float sx = -1.f * cosf(__x * fx);\n"
        + "float sy = -1.f * cosf(__y * fy);\n"
        + "float tx = __x + ax * (sx-__x);\n"
        + "float ty = __y + ay * (sy-__y);\n"
        + "\n"
        + "__px += varpar->sinusgrid*tx;\n"
        + "__py += varpar->sinusgrid*ty;\n"
        + "__pz += varpar->sinusgrid*__z;\n";
  }
}
