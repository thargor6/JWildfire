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

public class GlynniaFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // glynnia my Michael Faber, http://michaelfaber.deviantart.com/art/The-Lost-Variations-258913970
    double r = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double d;

    if (r >= 1.0) {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        pVarTP.x += _vvar2 * d;
        pVarTP.y -= _vvar2 / d * pAffineTP.y; //+= _vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount / dx;
        pVarTP.x += r * d;
        pVarTP.y += r * pAffineTP.y; //-= r * pAffineTP.y; 
      }
    } else {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        pVarTP.x -= _vvar2 * d;
        pVarTP.y -= _vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
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
  public String getName() {
    return "glynnia";
  }

  private double _vvar2;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _vvar2 = pAmount * sqrt(2.0) / 2.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float vvar2 = varpar->glynnia*rsqrtf(2.f);\n"
        + "float r = __r;\n"
        + "if( r > 1.f)\n"
        + "{\n"
        + "    if( RANDFLOAT() > 0.5f)\n"
        + "    {\n"
        + "        float d = sqrtf(r + __x);\n"
        + "        __px += vvar2 * d;\n"
        + "        __py += -vvar2 / d * __y;\n"
        + "    }\n"
        + "    else\n"
        + "    {\n"
        + "        float d = r + __x;\n"
        + "        r = varpar->glynnia / sqrtf(r * (__y*__y + d*d));\n"
        + "        __px += r * d;\n"
        + "        __py += r * __y;\n"
        + "    }\n"
        + "    }\n"
        + "    else\n"
        + "    {\n"
        + "    if( RANDFLOAT() > 0.5f)\n"
        + "    {\n"
        + "        float d = sqrtf(r + __x);\n"
        + "        __px -= vvar2 * d;\n"
        + "        __py -= vvar2 / d * __y;\n"
        + "    }\n"
        + "    else\n"
        + "    {\n"
        + "        float d = r + __x;\n"
        + "        r = varpar->glynnia / sqrtf(r * (__y*__y + d*d));\n"
        + "        __px -= r * d;\n"
        + "        __py += r * __y;\n"
        + "    }\n"
        + "}\n"
        + (context.isPreserveZCoordinate() ? "__pz += varpar->glynnia*__z;\n" : "");
  }
}
