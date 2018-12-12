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

import static org.jwildfire.base.mathlib.MathLib.SMALL_EPSILON;

public class DCZTranslFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X0 = "x0";
  private static final String PARAM_X1 = "x1";
  private static final String PARAM_FACTOR = "factor";
  private static final String PARAM_OVERWRITE = "overwrite";
  private static final String PARAM_CLAMP = "clamp";

  private static final String[] paramNames = {PARAM_X0, PARAM_X1, PARAM_FACTOR, PARAM_OVERWRITE, PARAM_CLAMP};

  private double x0 = 0.0;
  private double x1 = 1.0;
  private double factor = 1.0;
  private int overwrite = 1;
  private int clamp = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* dc_ztransl by Xyrus02, http://xyrus02.deviantart.com/art/DC-ZTransl-plugins-for-Apo7X-210719008?q=gallery%3Afractal-resources%2F24660058&qo=32 */
    double zf = factor * (pAffineTP.color - _x0) / _x1_m_x0;
    if (clamp != 0)
      zf = zf < 0 ? 0 : zf > 1 ? 1 : zf;
    pVarTP.x += pAmount * pAffineTP.x;
    pVarTP.y += pAmount * pAffineTP.y;

    if (overwrite == 0)
      pVarTP.z += pAmount * pAffineTP.z * zf;
    else
      pVarTP.z += pAmount * zf;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{x0, x1, factor, overwrite, clamp};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X0.equalsIgnoreCase(pName))
      x0 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_X1.equalsIgnoreCase(pName))
      x1 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_FACTOR.equalsIgnoreCase(pName))
      factor = pValue;
    else if (PARAM_OVERWRITE.equalsIgnoreCase(pName))
      overwrite = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_CLAMP.equalsIgnoreCase(pName))
      clamp = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dc_ztransl";
  }

  private double _x0;
  private double _x1;
  private double _x1_m_x0;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _x0 = x0 < x1 ? x0 : x1;
    _x1 = x0 > x1 ? x0 : x1;
    _x1_m_x0 = _x1 - _x0 == 0 ? SMALL_EPSILON : _x1 - _x0;
  }

}
