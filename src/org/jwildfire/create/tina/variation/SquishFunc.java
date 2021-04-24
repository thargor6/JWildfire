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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.floor;

public class SquishFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";

  private static final String[] paramNames = {PARAM_POWER};

  private int power = 2;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // squish by MichaelFaber - The angle pack: http://michaelfaber.deviantart.com/art/The-Angle-Pack-277718538
    double x = fabs(pAffineTP.x);
    double y = fabs(pAffineTP.y);
    double s;
    double p;

    if (x > y) {
      s = x;

      if (pAffineTP.x > 0.0) {
        p = pAffineTP.y;
      } else {
        p = 4.0 * s - pAffineTP.y;
      }
    } else {
      s = y;
      if (pAffineTP.y > 0.0) {
        p = 2.0 * s - pAffineTP.x;
      } else {
        p = 6.0 * s + pAffineTP.x;
      }
    }

    p = _inv_power * (p + 8.0 * s * floor(power * pContext.random()));

    if (p <= 1.0 * s) {
      pVarTP.x += pAmount * s;
      pVarTP.y += pAmount * p;
    } else if (p <= 3.0 * s) {
      pVarTP.x += pAmount * (2.0 * s - p);
      pVarTP.y += pAmount * (s);
    } else if (p <= 5.0 * s) {
      pVarTP.x -= pAmount * (s);
      pVarTP.y += pAmount * (4.0 * s - p);
    } else if (p <= 7.0 * s) {
      pVarTP.x -= pAmount * (6.0 * s - p);
      pVarTP.y -= pAmount * (s);
    } else {
      pVarTP.x += pAmount * (s);
      pVarTP.y -= pAmount * (8.0 * s - p);
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
    return new Object[]{power};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = limitIntVal(Tools.FTOI(pValue), 2, Integer.MAX_VALUE);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "squish";
  }

  private double _inv_power;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _inv_power = 1.0 / (double) power;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "    float x = fabsf(__x);\n"
        + "    float y = fabsf(__y);\n"
        + "    float s;\n"
        + "    float p;\n"
        + "    float x2, y2;\n"
        + "    float inv_power = 1.0 / (float) varpar->squish_power;\n"
        + "\n"
        + "    if ( x > y)\n"
        + "    {\n"
        + "        s = x;\n"
        + "\n"
        + "        if( __x > 0.0f)\n"
        + "        {\n"
        + "            p = __y;\n"
        + "        }\n"
        + "        else\n"
        + "        {\n"
        + "            p = 4.0f * s - __y;\n"
        + "        }\n"
        + "    }\n"
        + "    else\n"
        + "    {\n"
        + "        s = y;\n"
        + "        if( __y > 0.0f)\n"
        + "        {\n"
        + "            p = 2.0f * s - __x;\n"
        + "        }\n"
        + "        else\n"
        + "        {\n"
        + "            p = 6.0f * s + __x;\n"
        + "        }\n"
        + "    }\n"
        + "\n"
        + "    p = inv_power * (p + 8.0f * s * floorf(varpar->squish_power * RANDFLOAT()));\n"
        + "\n"
        + "    if( p <= 1.0f* s )\n"
        + "    {\n"
        + "        __px += varpar->squish * s;\n"
        + "        __py += varpar->squish * p;\n"
        + "    }\n"
        + "    else if( p <= 3.0f * s)\n"
        + "    {\n"
        + "        __px += varpar->squish * ( 2.0f * s - p);\n"
        + "        __py += varpar->squish * (s);\n"
        + "    }\n"
        + "    else if( p <= 5.0f * s)\n"
        + "    {\n"
        + "        __px -= varpar->squish * (s);\n"
        + "        __py += varpar->squish * ( 4.0f * s - p);\n"
        + "    }\n"
        + "    else if( p <= 7.0f * s)\n"
        + "    {\n"
        + "        __px -= varpar->squish * (6.0f * s - p);\n"
        + "        __py -= varpar->squish * (s);\n"
        + "    }\n"
        + "    else\n"
        + "    {\n"
        + "        __px += varpar->squish * (s);\n"
        + "        __py -= varpar->squish * ( 8.0f * s - p);\n"
        + "    }\n"
        + (context.isPreserveZCoordinate() ? "    __pz += varpar->squish*__z;\n": "");
  }
}
