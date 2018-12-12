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

import static org.jwildfire.base.mathlib.MathLib.*;

public class Spherical3DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_INVERT = "invert";
  private static final String PARAM_EXPONENT = "exponent";

  private static final String[] paramNames = {PARAM_INVERT, PARAM_EXPONENT};

  private int invert = 0;
  private double exponent = 2.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r;
    if (_regularForm) {
      r = pAmount / (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + pAffineTP.z * pAffineTP.z + SMALL_EPSILON);
    } else {
      r = pAmount / pow(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + pAffineTP.z * pAffineTP.z + SMALL_EPSILON, exponent / 2.0);
    }
    if (invert == 0) {
      pVarTP.x += pAffineTP.x * r;
      pVarTP.y += pAffineTP.y * r;
      pVarTP.z += pAffineTP.z * r;
    } else /*if (invert == 1)*/ {
      pVarTP.x -= pAffineTP.x * r;
      pVarTP.y -= pAffineTP.y * r;
      pVarTP.z -= pAffineTP.z * r;
    }
  }

  @Override
  public String getName() {
    return "spherical3D_wf";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{invert, exponent};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_INVERT.equalsIgnoreCase(pName))
      invert = Tools.FTOI(pValue);
    else if (PARAM_EXPONENT.equalsIgnoreCase(pName))
      exponent = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  private boolean _regularForm;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _regularForm = fabs(exponent - 2.0) < EPSILON;
  }

}
