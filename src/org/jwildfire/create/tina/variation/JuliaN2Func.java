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

public class JuliaN2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_DIST = "dist";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String[] paramNames = {PARAM_POWER, PARAM_DIST, PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F};

  private int power = genRandomPower();
  private double dist = 1.0;
  private double a = 1.0;
  private double b = 0.0;
  private double c = 0.0;
  private double d = 1.0;
  private double e = 0.0;
  private double f = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // julian2 by Xyrus02, http://xyrus02.deviantart.com/art/JuliaN2-Plugin-for-Apophysis-136717838
    if (power == 0)
      return;
    double x = a * pAffineTP.x + b * pAffineTP.y + e;
    double y = c * pAffineTP.x + d * pAffineTP.y + f;
    double sina = 0.0, cosa = 0.0;
    double angle = (atan2(y, x) + M_2PI * (pContext.random(Integer.MAX_VALUE) % _absN)) / power;
    double r = pAmount * pow(sqr(x) + sqr(y), _cN);

    sina = sin(angle);
    cosa = cos(angle);
    pVarTP.x += r * cosa;
    pVarTP.y += r * sina;
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
    return new Object[]{power, dist, a, b, c, d, e, f};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = Tools.FTOI(pValue);
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
    else if (PARAM_E.equalsIgnoreCase(pName))
      e = pValue;
    else if (PARAM_F.equalsIgnoreCase(pName))
      f = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "julian2";
  }

  private int genRandomPower() {
    int res = (int) (Math.random() * 5.0 + 2.5);
    return Math.random() < 0.5 ? res : -res;
  }

  private int _absN;
  private double _cN;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _absN = iabs(Tools.FTOI(power));
    _cN = dist / power * 0.5;
  }

  @Override
  public void randomize() {
    power = (int) (Math.random() * 10 + 2);
    if (Math.random() < 0.5)
      power *= -1;
    double r = Math.random();
    if (r < 0.4)
      dist = Math.random() * 0.5 + 0.75;
    else if (r < 0.8)
      dist = Math.random() * 3.3 + 0.2;
    else 
      dist = 1.0;
    if (Math.random() < 0.4) 
      dist *= -1;
    a = Math.random() * 3.0 - 1.5;
    b = Math.random() * 3.0 - 1.5;
    c = Math.random() * 3.0 - 1.5;
    d = Math.random() * 3.0 - 1.5;
    e = Math.random() * 3.0 - 1.5;
    f = Math.random() * 3.0 - 1.5;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "int power = lroundf(__julian2_power);\n" +
            "int _absN = power < 0 ? -power : power;\n"
        + "float _cN = __julian2_dist / power * 0.5f;\n"
        + "if (power != 0) {\n"
        + "    float x = __julian2_a * __x + __julian2_b * __y + __julian2_e;\n"
        + "    float y = __julian2_c * __x + __julian2_d * __y + __julian2_f;\n"
        + "    float sina = 0.0, cosa = 0.0;\n"
        + "    float angle = (atan2f(y, x) + (2.0f*PI) * (lroundf(RANDFLOAT()*0x0000ffff) % _absN)) / power;\n"
        + "    float r = __julian2 * powf(x*x + y*y, _cN);\n"
        + "\n"
        + "    sina = sinf(angle);\n"
        + "    cosa = cosf(angle);\n"
        + "    __px += r * cosa;\n"
        + "    __py += r * sina;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __julian2 * __z;\n" : "")
        + "}\n";
  }
}
