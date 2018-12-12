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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class IntersectionFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XWIDTH = "xwidth";
  private static final String PARAM_XTILESIZE = "xtilesize";
  private static final String PARAM_XMOD1 = "xmod1";
  private static final String PARAM_XMOD2 = "xmod2";
  private static final String PARAM_XHEIGHT = "xheight";
  private static final String PARAM_YHEIGHT = "yheight";
  private static final String PARAM_YTILESIZE = "ytilesize";
  private static final String PARAM_YMOD1 = "ymod1";
  private static final String PARAM_YMOD2 = "ymod2";
  private static final String PARAM_YWIDTH = "ywidth";

  private static final String[] paramNames = {PARAM_XWIDTH, PARAM_XTILESIZE, PARAM_XMOD1, PARAM_XMOD2, PARAM_XHEIGHT,
          PARAM_YHEIGHT, PARAM_YTILESIZE, PARAM_YMOD1, PARAM_YMOD2, PARAM_YWIDTH};

  private double xwidth = 5.0;
  private double xtilesize = 0.50;
  private double xmod1 = 0.30;
  private double xmod2 = 1.0;
  private double xheight = 0.50;
  private double yheight = 5.0;
  private double ytilesize = 0.50;
  private double ymod1 = 0.30;
  private double ymod2 = 1.0;
  private double ywidth = 0.50;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    /* intersection by Brad Stefanov */

    if (pContext.random() < 0.5) {

      double x = -xwidth;
      if (pContext.random() < 0.5)
        x = xwidth;

      pVarTP.x += xtilesize * (pAffineTP.x + round(x * log(pContext.random())));

      if (pAffineTP.y > xmod1) {
        pVarTP.y += xheight * (-xmod1 + fmod(pAffineTP.y + xmod1, _xr1));
      } else if (pAffineTP.y < -xmod1) {
        pVarTP.y += xheight * (xmod1 - fmod(xmod1 - pAffineTP.y, _xr1));
      } else {

        pVarTP.y += xheight * pAffineTP.y;
      }
    } else {

      double y = -yheight;
      if (pContext.random() < 0.5)
        y = yheight;

      pVarTP.y += ytilesize * (pAffineTP.y + round(y * log(pContext.random())));

      if (pAffineTP.x > ymod1) {
        pVarTP.x += ywidth * (-ymod1 + fmod(pAffineTP.x + ymod1, _yr1));
      } else if (pAffineTP.x < -ymod1) {
        pVarTP.x += ywidth * (ymod1 - fmod(ymod1 - pAffineTP.x, _yr1));
      } else {
        pVarTP.x += ywidth * pAffineTP.x;

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
    return new Object[]{xwidth, xtilesize, xmod1, xmod2, xheight, yheight, ytilesize, ymod1, ymod2, ywidth};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XWIDTH.equalsIgnoreCase(pName))
      xwidth = pValue;
    else if (PARAM_XTILESIZE.equalsIgnoreCase(pName))
      xtilesize = pValue;
    else if (PARAM_XMOD1.equalsIgnoreCase(pName))
      xmod1 = pValue;
    else if (PARAM_XMOD2.equalsIgnoreCase(pName))
      xmod2 = pValue;
    else if (PARAM_XHEIGHT.equalsIgnoreCase(pName))
      xheight = pValue;
    else if (PARAM_YHEIGHT.equalsIgnoreCase(pName))
      yheight = pValue;
    else if (PARAM_YTILESIZE.equalsIgnoreCase(pName))
      ytilesize = pValue;
    else if (PARAM_YMOD1.equalsIgnoreCase(pName))
      ymod1 = pValue;
    else if (PARAM_YMOD2.equalsIgnoreCase(pName))
      ymod2 = pValue;
    else if (PARAM_YWIDTH.equalsIgnoreCase(pName))
      ywidth = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "intersection";
  }

  private double _xr1, _yr1;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _xr1 = xmod2 * xmod1;
    _yr1 = ymod2 * ymod1;

  }

}
