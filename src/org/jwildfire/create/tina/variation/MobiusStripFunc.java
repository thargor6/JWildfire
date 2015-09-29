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

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.floor;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.cos;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.Layer;

/*
 * Apophysis avMobius transformation by slobo777: 
 *    http://slobo777.deviantart.com/art/Mobius-Apo-Variation-60525279
 * Transcribed into Java by chronologicaldot -- August 31, 2015
 * modifications by CozyG -- September 8, 2015
*/
public class MobiusStripFunc extends VariationFunc {
   private static final long serialVersionUID = 1L;

   double radius = 2;
   double width = 1;
   double rect_x = M_2PI;
   double rect_y = 1;
   double rotate_x = 0;
   double rotate_y = 0;

   double rotxSin, rotxCos, rotySin, rotyCos;

   private static final String PARAM_RADIUS = "radius";
   private static final String PARAM_WIDTH = "width";
   private static final String PARAM_RECT_X = "rect x";
   private static final String PARAM_RECT_Y = "rect y";
   private static final String PARAM_ROTATE_X = "rot x";
   private static final String PARAM_ROTATE_Y = "rot y";
   private static final String[] paramNames = { PARAM_RADIUS, PARAM_WIDTH, PARAM_RECT_X, PARAM_RECT_Y, PARAM_ROTATE_X, PARAM_ROTATE_Y };

   @Override
   public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
      rotxSin = sin( rotate_x * M_2PI );
      rotxCos = cos( rotate_x * M_2PI );
      rotySin = sin( rotate_y * M_2PI );
      rotyCos = cos( rotate_y * M_2PI );
   }

   @Override
   public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
      double s, t, Mx, My, Mz, Rx, Ry, Rz, dT, dS;

      t = pAffineTP.x;

      // Put t in range -rectX to +rectX
      // Then map that to 0 - 2pi
      if ( rect_x == 0 ) {
         t = 0;
      } else {
         dT = (t + rect_x) / (2 * rect_x);
         dT -= floor(dT);
         t = dT * M_2PI;
      }

      s = pAffineTP.y;

      // Put s in range -rectY to +rectY
      // Then map that to -width to +width
      if ( rect_y == 0 ) {
         s = 0;
      } else {
         dS = (s + rect_y) / (2 * rect_y);
         dS -= floor(dS);
         s = width * ( 2 * dS - 1 ); 
      }

      // Initial "object" co-ordinates

      Mx = ( radius + s * cos( t / 2 ) ) * cos( t );
      My = ( radius + s * cos( t / 2 ) ) * sin( t );
      Mz = s * sin( t / 2 );

      // Rotate around X axis (change y and z)
      // Store temporarily in R variables

      Rx = Mx;
      Ry = My * rotyCos + Mz * rotySin;
      Rz = Mz * rotyCos - My * rotySin;

      // Rotate around Y axis (change x and z)
      // Store back in M variables

      Mx = Rx * rotxCos - Rz * rotxSin;
      My = Ry;
      Mz = Rz * rotxCos + Rx * rotxSin;

      // Add final values in to variations totals

      pVarTP.x += pAmount * Mx;
      pVarTP.y += pAmount * My;
   }

   @Override
   public String[] getParameterNames() {
      return paramNames;
   }

   @Override
   public Object[] getParameterValues() {
      return new Object[] { radius, width, rect_x, rect_y, rotate_x, rotate_y };
   }

   @Override
   public void setParameter(String pName, double pValue) {
      if (PARAM_RADIUS.equalsIgnoreCase(pName))
         radius = pValue;
      else if (PARAM_WIDTH.equalsIgnoreCase(pName))
         width = pValue;
      else if (PARAM_RECT_X.equalsIgnoreCase(pName))
         rect_x = pValue;
      else if (PARAM_RECT_Y.equalsIgnoreCase(pName))
         rect_y = pValue;
      else if (PARAM_ROTATE_X.equalsIgnoreCase(pName))
         rotate_x = pValue;
      else if (PARAM_ROTATE_Y.equalsIgnoreCase(pName))
         rotate_y = pValue;
      else
         throw new IllegalArgumentException(pName);
   }

   @Override
   public String getName() {
      return "mobius_strip";
   }

   @Override
   public int getPriority() {
      return 0;
   }
}
