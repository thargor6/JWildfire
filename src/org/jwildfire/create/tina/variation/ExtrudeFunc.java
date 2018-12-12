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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ExtrudeFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ROOT_FACE = "root_face";
  private static final String[] paramNames = {PARAM_ROOT_FACE};

  private double root_face = 0.5;

  private int rand(FlameTransformationContext pContext) {
    return pContext.random(0xfffffff);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* extrude by Xyrus02, http://xyrus-02.deviantart.com/art/Extrude-Plugin-for-Apophysis-172778628 */
    if ((((rand(pContext) ^ (rand(pContext) << 15)) & 0xfffffff) / (double) 0xfffffff) < root_face) {
      pVarTP.z = (pAmount < 0 ? 0 : pAmount);
    } else {
      pVarTP.z = pAmount * (((rand(pContext) ^ (rand(pContext) << 15)) & 0xfffffff) / (double) 0xfffffff);
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{root_face};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ROOT_FACE.equalsIgnoreCase(pName))
      root_face = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "extrude";
  }

}
