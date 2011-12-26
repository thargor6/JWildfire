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

import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class TBumpMapFunc extends VariationFunc {

  private static final String PARAM_SCALEX = "scale_x";
  private static final String PARAM_SCALEY = "scale_y";
  private static final String PARAM_SCALEZ = "scale_z";
  private static final String PARAM_OFFSETZ = "offset_z";
  private static final String PARAM_RESETZ = "reset_z";

  private static final String[] paramNames = { PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETZ, PARAM_RESETZ };

  private double scaleX = 0.0;
  private double scaleY = 0.0;
  private double scaleZ = 0.5;
  private double offsetZ = 0.0;
  private double resetZ = 0.0;

  static int cnt = 0;

  @Override
  public void transform(XFormTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    RasterPoint rp = pContext.getPass1RasterPoint(pAffineTP.x, pAffineTP.y);
    double dz;
    if (rp == null || rp.count == 0) {
      dz = 0.0;
    }
    else {
      dz = ((rp.red * 0.299 + rp.green * 0.588 + rp.blue * 0.113) / rp.count) / 255.0;
      if (cnt++ < 1000) {

        System.out.println(cnt + ": " + pAffineTP.x + " " + pAffineTP.y + " " + dz);
        System.out.println("  " + rp.red + " " + rp.green + " " + rp.blue + " " + rp.count);
      }
    }

    if (resetZ > 0) {
      pVarTP.z = dz;
    }
    else {
      pVarTP.z += dz;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { scaleX, scaleY, scaleZ, offsetZ, resetZ };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scaleX = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaleY = pValue;
    else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
      scaleZ = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offsetZ = pValue;
    else if (PARAM_RESETZ.equalsIgnoreCase(pName))
      resetZ = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "t_bump_map";
  }

  //  @Override
  //  public int getPriority() {
  //    return 1;
  //  }

  @Override
  public boolean requiresTwoPasses() {
    return true;
  }

}
