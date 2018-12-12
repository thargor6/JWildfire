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

public class CubicLattice3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XPAND = "xpand";
  private static final String PARAM_STYLE = "style";
  private static final String[] paramNames = {PARAM_XPAND, PARAM_STYLE};

  private double xpand = 0.2;
  private double style = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* cubicLattice_3D by Larry Berlin, http://aporev.deviantart.com/art/3D-Plugins-Collection-One-138514007?q=gallery%3Aaporev%2F8229210&qo=15 */
    double fill, exnze, wynze, znxy;
    if (fabs(this.xpand) <= 1.0) {
      fill = this.xpand * 0.5; // values up to 0.5
    } else {
      fill = sqrt(this.xpand) * 0.5; // values above 0.5
    }
    if (_iStyle == 2) {
      exnze = cos(atan2(pAffineTP.x, pAffineTP.z));
      wynze = sin(atan2(pAffineTP.y, pAffineTP.z));
      znxy = (exnze + wynze) / 2.0;
    } else {
      exnze = 1.0;
      wynze = 1.0;
      znxy = 1.0;
    }

    double lattd = pAmount; // optionally * 0.5;   
    int useNode = 0;
    int rchoice = (int) trunc(pContext.random() * 8.0);
    useNode = rchoice;
    if (useNode == 0) {
      pVarTP.x = (pVarTP.x + pAffineTP.x) * fill * exnze + lattd;
      pVarTP.y = (pVarTP.y + pAffineTP.y) * fill * wynze + lattd;
      pVarTP.z = (pVarTP.z + pAffineTP.z) * fill * znxy + lattd;
    } else if (useNode == 1) {
      pVarTP.x = (pVarTP.x + pAffineTP.x) * fill * exnze + lattd;
      pVarTP.y = (pVarTP.y + pAffineTP.y) * fill * wynze - lattd;
      pVarTP.z = (pVarTP.z + pAffineTP.z) * fill * znxy + lattd;
    } else if (useNode == 2) {
      pVarTP.x = (pVarTP.x + pAffineTP.x) * fill * exnze + lattd;
      pVarTP.y = (pVarTP.y + pAffineTP.y) * fill * wynze + lattd;
      pVarTP.z = (pVarTP.z + pAffineTP.z) * fill * znxy - lattd;
    } else if (useNode == 3) {
      pVarTP.x = (pVarTP.x + pAffineTP.x) * fill * exnze + lattd;
      pVarTP.y = (pVarTP.y + pAffineTP.y) * fill * wynze - lattd;
      pVarTP.z = (pVarTP.z + pAffineTP.z) * fill * znxy - lattd;
    } else if (useNode == 4) {
      pVarTP.x = (pVarTP.x + pAffineTP.x) * fill * exnze - lattd;
      pVarTP.y = (pVarTP.y + pAffineTP.y) * fill * wynze + lattd;
      pVarTP.z = (pVarTP.z + pAffineTP.z) * fill * znxy + lattd;
    } else if (useNode == 5) {
      pVarTP.x = (pVarTP.x + pAffineTP.x) * fill * exnze - lattd;
      pVarTP.y = (pVarTP.y + pAffineTP.y) * fill * wynze - lattd;
      pVarTP.z = (pVarTP.z + pAffineTP.z) * fill * znxy + lattd;
    } else if (useNode == 6) {
      pVarTP.x = (pVarTP.x + pAffineTP.x) * fill * exnze - lattd;
      pVarTP.y = (pVarTP.y + pAffineTP.y) * fill * wynze + lattd;
      pVarTP.z = (pVarTP.z + pAffineTP.z) * fill * znxy - lattd;
    } else if (useNode == 7) {
      pVarTP.x = (pVarTP.x + pAffineTP.x) * fill * exnze - lattd;
      pVarTP.y = (pVarTP.y + pAffineTP.y) * fill * wynze - lattd;
      pVarTP.z = (pVarTP.z + pAffineTP.z) * fill * znxy - lattd;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{xpand, style};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_XPAND.equalsIgnoreCase(pName))
      xpand = pValue;
    else if (PARAM_STYLE.equalsIgnoreCase(pName))
      style = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "cubicLattice_3D";
  }

  private int _iStyle;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    if (fabs(this.style) >= 2.0) {
      _iStyle = 2;
    } else {
      _iStyle = 1;
    }
  }

}
