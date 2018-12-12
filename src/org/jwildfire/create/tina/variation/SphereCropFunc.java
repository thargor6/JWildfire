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

public class SphereCropFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_Z = "z";
  private static final String PARAM_SCATTER_AREA = "scatter_area";
  private static final String PARAM_ZERO = "zero";

  private static final String[] paramNames = {PARAM_RADIUS, PARAM_X, PARAM_Y, PARAM_Z, PARAM_SCATTER_AREA, PARAM_ZERO};

  private double radius = 1.0;
  private double x = 0.0;
  private double y = 0.0;
  private double z = 0.0;
  private double scatter_area = 0.0;
  private int zero = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // 3D version of circlecrop by Xyrus02, http://xyrus02.deviantart.com/art/CircleCrop-Plugins-185353309
    double x0 = x;
    double y0 = y;
    double z0 = z;
    double cr = radius;
    double ca = cA;
    double vv = pAmount;

    pAffineTP.x -= x0;
    pAffineTP.y -= y0;
    pAffineTP.z -= z0;

    double rad = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y + pAffineTP.z * pAffineTP.z);
    double ang = atan2(pAffineTP.y, pAffineTP.x);
    double az = acos(pAffineTP.z / rad);
    double rdc = cr + (pContext.random() * 0.5 * ca);

    boolean esc = rad > cr;
    boolean cr0 = zero == 1;

    double s = sin(ang);
    double c = cos(ang);
    double saz = sin(az);
    double caz = cos(az);

    pVarTP.doHide = false;
    if (cr0 && esc) {
      pVarTP.x = pVarTP.y = pVarTP.z = 0;
      pVarTP.doHide = true;
    } else if (cr0 && !esc) {
      pVarTP.x += vv * pAffineTP.x + x0;
      pVarTP.y += vv * pAffineTP.y + y0;
      pVarTP.z += vv * pAffineTP.z + z0;
    } else if (!cr0 && esc) {
      pVarTP.x += vv * rdc * c * saz + x0;
      pVarTP.y += vv * rdc * s * saz + y0;
      pVarTP.z += vv * rdc * caz + z0;
    } else if (!cr0 && !esc) {
      pVarTP.x += vv * pAffineTP.x + x0;
      pVarTP.y += vv * pAffineTP.y + y0;
      pVarTP.z += vv * pAffineTP.z + z0;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{radius, x, y, z, scatter_area, zero};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue;
    else if (PARAM_SCATTER_AREA.equalsIgnoreCase(pName))
      scatter_area = pValue;
    else if (PARAM_ZERO.equalsIgnoreCase(pName))
      zero = Tools.FTOI(pValue);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "spherecrop";
  }

  private double cA;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    cA = max(-1.0, min(scatter_area, 1.0));
  }

}
