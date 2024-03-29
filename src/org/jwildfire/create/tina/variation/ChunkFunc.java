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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.sqr;


public class ChunkFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";
  private static final String PARAM_MODE = "mode";

  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_MODE};
  private double a = 1.0;
  private double b = 0.0;
  private double c = 1.0;
  private double d = 0.0;
  private double e = 0.0;
  private double f = -1.0;
  private int mode = 0;


  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* chunk by zephyrtronium https://zephyrtronium.deviantart.com/art/Chunk-Apophysis-Plugin-Pack-182375397 converted by Brad Stefanov */
    double aa = pAmount * a;
    double bb = pAmount * b;
    double cc = pAmount * c;
    double dd = pAmount * d;
    double ee = pAmount * e;
    double ff = pAmount * f;


    double r = aa * sqr(pAffineTP.x) + bb * pAffineTP.x * pAffineTP.y + cc * sqr(pAffineTP.y) + dd * pAffineTP.x + ee * pAffineTP.y + ff;
    switch (mode) {
      case 0:
        if (r <= 0) {
          pVarTP.x += pAffineTP.x;
          pVarTP.y += pAffineTP.y;
          if (pContext.isPreserveZCoordinate()) {
            pVarTP.z += pAmount * pAffineTP.z;
          }
        }
        break;
      case 1:
        if (r > 0) {
          pVarTP.x += pAffineTP.x;
          pVarTP.y += pAffineTP.y;
          if (pContext.isPreserveZCoordinate()) {
            pVarTP.z += pAmount * pAffineTP.z;
          }
          break;


        }
    }


  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{a, b, c, d, e, f, mode};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName)) {
      a = pValue;
    } else if (PARAM_B.equalsIgnoreCase(pName)) {
      b = pValue;
    } else if (PARAM_C.equalsIgnoreCase(pName)) {
      c = pValue;
    } else if (PARAM_D.equalsIgnoreCase(pName)) {
      d = pValue;
    } else if (PARAM_E.equalsIgnoreCase(pName)) {
      e = pValue;
    } else if (PARAM_F.equalsIgnoreCase(pName)) {
      f = pValue;
    } else if (PARAM_MODE.equalsIgnoreCase(pName))
      mode = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else {
      System.out.println("pName not recognized: " + pName);
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "chunk";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float aa = __chunk *  __chunk_a ;"
    		+"    float bb = __chunk *  __chunk_b ;"
    		+"    float cc = __chunk *  __chunk_c ;"
    		+"    float dd = __chunk *  __chunk_d ;"
    		+"    float ee = __chunk *  __chunk_e ;"
    		+"    float ff = __chunk *  __chunk_f ;"
    		+"    float r = aa * __x*__x + bb * __x * __y + cc * __y*__y + dd * __x + ee * __y + ff;"
    		+"    switch ( (int) __chunk_mode ) {"
    		+"      case 0:"
    		+"        if (r <= 0) {"
    		+"          __px += __x;"
    		+"          __py += __y;"
    		+"        }"
    		+"        break;"
    		+"      case 1:"
    		+"        if (r > 0) {"
    		+"          __px += __x;"
    		+"          __py += __y;"
    		+"          break;"
    		+"        }"
    		+"    }"
            + (context.isPreserveZCoordinate() ? "__pz += __chunk *__z;" : "");
  }
}
