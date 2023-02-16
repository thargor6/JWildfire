/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2023 Andreas Maschke

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
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Hourglass3DFunc extends VariationFunc {

  private static final String PARAM_KX = "kx";
  private static final String PARAM_KY = "ky";
  private static final String PARAM_KZ = "kz";
  private static final String PARAM_MAXHEIGHT = "maxheight";
  private static final String PARAM_LIMIT = "limit";
  private static final String PARAM_ZERO = "zero";
  private static final String[] paramNames = {PARAM_KX, PARAM_KY, PARAM_KZ, PARAM_MAXHEIGHT, PARAM_LIMIT, PARAM_ZERO};

  private double kx = 1.0;
  private double ky = 1.0;
  private double kz = 1.0;
  private double maxheight = 3.0 + 3.0 * Math.random();
  private int limit = 1;
  private int zero = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* hourglass3D, taken from the repository of "Apophysis AV 'Phoenix Edition'": https://sourceforge.net/p/apophysis-av/code/ci/master/tree/Variations/varHyperboloid.pas */
    if (limit == 0)
      calc(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    else if (zero == 0)
      calcCutBoarder(pContext, pXForm, pAffineTP, pVarTP, pAmount);
    else
      calcCut(pContext, pXForm, pAffineTP, pVarTP, pAmount);
  }

  private void calc(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sn = sin(pAffineTP.x);
    double cn = cos(pAffineTP.x);
    double sh = sinh(pAffineTP.y);
    double ch = cosh(pAffineTP.y);
    pVarTP.x += _vkx * ch * sn;
    pVarTP.y += _vky * sh;
    pVarTP.z += _vkz * ch * cn;
  }

  private void calcCut(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double ch = cosh(pAffineTP.y);
    if (ch <= _h) {   // cut the surface
      double sh = sinh(pAffineTP.y);
      double sn = sin(pAffineTP.x);
      double cn = cos(pAffineTP.x);
      pVarTP.x += _vkx * ch * sn;
      pVarTP.y += _vky * sh;
      pVarTP.z += _vkz * ch * cn;
    }
  }

  private void calcCutBoarder(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double sn = sin(pAffineTP.x);
    double cn = cos(pAffineTP.x);
    double sh = sinh(pAffineTP.y);
    double ch = cosh(pAffineTP.y);
    if (ch <= _h) {   // cut the surface
      pVarTP.x += _vkx * ch * sn;
      pVarTP.y += _vky * sh;
      pVarTP.z += _vkz * ch * cn;
     }
     else {   // place the point on it's boarder
      pVarTP.x += _vkx * _h * sn;
      pVarTP.y += _vky * sign(sh) * _hb;
      pVarTP.z += _vkz * _h * cn;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{kx, ky, kz, maxheight, limit, zero};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_KX.equalsIgnoreCase(pName)) {
      kx = pValue;
      if(kx<EPSILON) kx = EPSILON;
    } else if (PARAM_KY.equalsIgnoreCase(pName)) {
      ky = pValue;
      if(ky<EPSILON) ky = EPSILON;
    } else if (PARAM_KZ.equalsIgnoreCase(pName)) {
      kz = pValue;
      if(kz<EPSILON) kz = EPSILON;
    } else if (PARAM_MAXHEIGHT.equalsIgnoreCase(pName)) {
      maxheight = pValue;
      if(maxheight<0.1) maxheight = 0.1;
    }
    else if (PARAM_LIMIT.equalsIgnoreCase(pName))
      limit = pValue <= EPSILON ? 0 :  1;
    else if (PARAM_ZERO.equalsIgnoreCase(pName))
      zero = pValue <= EPSILON ? 0 :  1;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hourglass3D";
  }


  private double _h, _hb, _vkx, _vky, _vkz;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _vkx = pAmount * kx;
    _vky = pAmount * ky;
    _vkz = pAmount * kz;
    _h = MathLib.sqrt(1 + MathLib.sqr(maxheight / ky));
    _hb = MathLib.sqrt(MathLib.sqr(_h) - 1.0);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D};
  }

}

