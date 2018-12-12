/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.sqr;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class BarycentroidFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D};

  private double a = 1.0;
  private double b = 0.0;
  private double c = 0.0;
  private double d = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* barycentroid from Xyrus02, http://xyrusworx.deviantart.com/art/Barycentroid-Plugin-144832371?q=sort%3Atime+favby%3Amistywisp&qo=0&offset=10 */
    // helpers

    /*  The code is supposed to be fast and you all can read it so I dont 
        create those aliases for readability in actual code:
               
        v0x = this.a
        v0y = this.b 
        v1x = this.c
        v1y = this.d
        v2x = pAffineTP.x
        v2y = pAffineTP.y
    */

    // compute dot products
    double dot00 = this.a * this.a + this.b * this.b; // v0 * v0
    double dot01 = this.a * this.c + this.b * this.d; // v0 * v1
    double dot02 = this.a * pAffineTP.x + this.b * pAffineTP.y; // v0 * v2
    double dot11 = this.c * this.c + this.d * this.d; // v1 * v1
    double dot12 = this.c * pAffineTP.x + this.d * pAffineTP.y; // v1 * v2

    // compute inverse denomiator
    double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);

    /* now we can pull [u,v] as the barycentric coordinates of the point 
       P in the triangle [A, B, C]
    */
    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    // now combine with input
    double um = sqrt(sqr(u) + sqr(pAffineTP.x)) * sgn(u);
    double vm = sqrt(sqr(v) + sqr(pAffineTP.y)) * sgn(v);

    pVarTP.x += pAmount * um;
    pVarTP.y += pAmount * vm;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  private double sgn(double v) {
    return v < 0.0 ? -1.0 : v > 0 ? 1.0 : 0.0;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{a, b, c, d};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "barycentroid";
  }

}
