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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.sqr;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class Glynnia3Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RSCALE = "rscale";
  private static final String PARAM_DSCALE = "dscale";
  private static final String PARAM_RTHRESH = "rthresh";
  private static final String PARAM_YTHRESH = "ythresh";

  private static final String[] paramNames = {PARAM_RSCALE, PARAM_DSCALE, PARAM_RTHRESH, PARAM_YTHRESH};

  private double rscale = 1.0;
  private double dscale = 1.0;
  private double rthresh = 0.0;
  private double ythresh = 0.0;

  /**
   * glynnia Apophysis plugin by Michael Faber: http://michaelfaber.deviantart.com/art/The-Lost-Variations-258913970
   * glynnia2 Apophysis plugin by Maulana Randa: http://www.deviantart.com/art/Glynnia2-190716081
   * glynnia3 JWildfire variation by CozyG: ported from glynnia2 plugin and adding extra parameters
   */
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double r = rscale * (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)));
    double d;

    if (r > rthresh && pAffineTP.y > ythresh) {
      if (pContext.random() > 0.5) {
        d = dscale * sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        pVarTP.x += _vvar2 * d;
        pVarTP.y -= _vvar2 / d * pAffineTP.y;
      } else {
        d = dscale * (r + pAffineTP.x);
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount / dx;
        pVarTP.x += r * d;
        pVarTP.y += r * pAffineTP.y;
      }
    } else {
      if (pContext.random() > 0.5) {
        d = dscale * sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        pVarTP.x -= _vvar2 * d;
        pVarTP.y -= _vvar2 / d * pAffineTP.y;
      } else {
        d = dscale * (r + pAffineTP.x);
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount / dx;
        pVarTP.x -= r * d;
        pVarTP.y += r * pAffineTP.y;
      }
    }
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
    return new Object[]{rscale, dscale, rthresh, ythresh};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RSCALE.equalsIgnoreCase(pName)) {
      rscale = pValue;
    } else if (PARAM_DSCALE.equalsIgnoreCase(pName)) {
      dscale = pValue;
    } else if (PARAM_RTHRESH.equalsIgnoreCase(pName)) {
      rthresh = pValue;
    } else if (PARAM_YTHRESH.equalsIgnoreCase(pName)) {
      ythresh = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "glynnia3";
  }

  private double _vvar2;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _vvar2 = pAmount * sqrt(2.0) / 2.0;
  }

  @Override
  public void randomize() {
  	rscale = Math.random() * 4.0 + 1.0;
  	if (Math.random() < 0.25) rscale = 1 / rscale;
  	dscale = Math.random() * 9.0 + 1.0;
  	if (Math.random() < 0.5) dscale = 1 / dscale;
  	if (Math.random() < 0.1) dscale = -dscale;
  	rthresh = Math.random() * 2.5;
  	ythresh = Math.random() * 4.0 - 2.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float _vvar2 = __glynnia3 * sqrtf(2.0) / 2.0;\n"
        + " float r = __glynnia3_rscale * (sqrtf(__x*__x + __y*__y));\n"
        + "    float d;\n"
        + "\n"
        + "    if (r > __glynnia3_rthresh && __y > __glynnia3_ythresh) {\n"
        + "      if (RANDFLOAT() > 0.5) {\n"
        + "        d = __glynnia3_dscale * sqrtf(r + __x);\n"
        + "        if (d != 0) {\n"
        + "        __px += _vvar2 * d;\n"
        + "        __py -= _vvar2 / d * __y;\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __glynnia3 * __z;\n" : "")
        + "        }\n"
        + "      } else {\n"
        + "        d = __glynnia3_dscale * (r + __x);\n"
        + "        float dx = sqrtf(r * (__y*__y + d*d));\n"
        + "        if (dx != 0) {\n"
        + "          r = __glynnia3 / dx;\n"
        + "          __px += r * d;\n"
        + "          __py += r * __y;\n"
            + (context.isPreserveZCoordinate() ? "      __pz += __glynnia3 * __z;\n" : "")
        + "        }\n"
        + "      }\n"
        + "    } else {\n"
        + "      if (RANDFLOAT() > 0.5) {\n"
        + "        d = __glynnia3_dscale * sqrtf(r + __x);\n"
        + "        if (d != 0) {\n"
        + "          __px -= _vvar2 * d;\n"
        + "          __py -= _vvar2 / d * __y;\n"
            + (context.isPreserveZCoordinate() ? "      __pz += __glynnia3 * __z;\n" : "")
        + "        }\n"
        + "      } else {\n"
        + "        d = __glynnia3_dscale * (r + __x);\n"
        + "        float dx = sqrtf(r * (__y*__y + d*d));\n"
        + "        if (dx != 0) {\n"
        + "          r = __glynnia3 / dx;\n"
        + "          __px -= r * d;\n"
        + "          __py += r * __y;\n"
            + (context.isPreserveZCoordinate() ? "      __pz += __glynnia3 * __z;\n" : "")
        + "        }\n"
        + "      }\n"
        + "    }\n";
  }
}
