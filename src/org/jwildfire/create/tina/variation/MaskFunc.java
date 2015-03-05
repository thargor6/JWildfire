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

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.tan;
import static org.jwildfire.base.mathlib.MathLib.*;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
 * Mask Apophysis plugin by Raykoid666
 * ported to JWildfire variation by CozyG
 */
public class MaskFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_XSHIFT = "xshift";
  private static final String PARAM_YSHIFT = "yshift";
  private static final String[] paramNames = { PARAM_XSHIFT, PARAM_YSHIFT };

  double xshift = 0.0;
  double yshift = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Prevent divide by zero error
    if (pAffineTP.x + xshift == 0 || pAffineTP.x + xshift == M_PI) {
      pVarTP.doHide = true;
    }
    else {
      // d = FTx*FTx+FTy*FTy;
      // FPx = (VVAR/d)*sin(FTx)*(cosh(FTy)+1)*sqr(sin(FTx));
      // FPy = (VVAR/d)*cos(FTx)*(cosh(FTy)+1)*sqr(sin(FTx));
      double d = pAffineTP.getPrecalcSumsq();
      pVarTP.x = (pAmount / d) * sin(pAffineTP.x + xshift) * (cosh(pAffineTP.y + yshift) + 1) * sqr(sin(pAffineTP.x + xshift)); 
      pVarTP.y += (pAmount / d) * cos(pAffineTP.x + xshift) * (cosh(pAffineTP.y + yshift) + 1) * sqr(sin(pAffineTP.x + xshift)); 
    }
  }

  @Override
  public String getName() {
    return "mask";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { xshift, yshift };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_XSHIFT)) {
      xshift = pValue;
    }
    else if (pName.equalsIgnoreCase(PARAM_YSHIFT)) {
      yshift = pValue;
    }
    else
      throw new IllegalArgumentException(pName);
  }
}
