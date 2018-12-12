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

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.sin;


public class Pressure_WaveFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_pressure_wave_x_freq = "x_freq";
  private static final String PARAM_pressure_wave_y_freq = "y_freq";

  private static final String[] paramNames = {PARAM_pressure_wave_x_freq, PARAM_pressure_wave_y_freq};

  private double x_freq = 1.0;
  private double y_freq = 1.0;
  private double pwx = 1.0;
  private double pwy = 1.0;
  private double ipwx = 1.0;
  private double ipwy = 1.0;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pressure_wave by timothy-vincent (2010) implemented into JWildfire by DarkBeam
    // multiplications and stuff moved into setParameter for speedup plus avoid 1./0.

    pVarTP.x += pAmount * (pAffineTP.x + (ipwx * sin(pwx * pAffineTP.x)));
    pVarTP.y += pAmount * (pAffineTP.y + (ipwy * sin(pwy * pAffineTP.y)));
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
    return new Object[]{x_freq, y_freq};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_pressure_wave_x_freq.equalsIgnoreCase(pName)) {
      x_freq = pValue;
      if (x_freq == 0.0) {
        pwx = 1.0;
        ipwx = pwx;
      } else {
        pwx = pValue * M_2PI;
        ipwx = 1.0 / pwx;
      }
    } else if (PARAM_pressure_wave_y_freq.equalsIgnoreCase(pName)) {
      y_freq = pValue;
      if (y_freq == 0.0) {
        pwy = 1.0;
        ipwy = pwy;
      } else {
        pwy = pValue * M_2PI;
        ipwy = 1.0 / pwy;
      }
    }
  }

  @Override
  public String getName() {
    return "pressure_wave";
  }

}
