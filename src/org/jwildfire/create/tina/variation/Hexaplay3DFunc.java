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

public class Hexaplay3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private double _seg60x[] = new double[6];
  private double _seg60y[] = new double[6];
  private double _seg120x[] = new double[3];
  private double _seg120y[] = new double[3];
  private int _rswtch; //  A value for choosing between 6 or 3 segments to a plane
  private int _fcycle; //  markers to count cycles... 
  private int _bcycle;

  private static final String PARAM_MAJP = "majp";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_ZLIFT = "zlift";
  private static final String[] paramNames = {PARAM_MAJP, PARAM_SCALE, PARAM_ZLIFT};

  private double majp = 1.0; // establishes 1 or 2 planes, and if 2, the distance between them
  private double scale = 0.25; // scales the effect of X and Y
  private double zlift = 0.25; // scales the effect of Z axis within the snowflake

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* hexaplay3D by Larry Berlin, http://aporev.deviantart.com/art/3D-Plugins-Collection-One-138514007?q=gallery%3Aaporev%2F8229210&qo=15 */
    if (this._fcycle > 5) {
      this._fcycle = 0;
      this._rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses new 6 or 3 nodes
    }
    if (this._bcycle > 2) {
      this._bcycle = 0;
      this._rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses new 6 or 3 nodes
    }

    double lrmaj = pAmount; // Sets hexagon length radius - major plane
    double boost = 0; //  Boost is the separation distance between the two planes
    int posNeg = 1;
    int loc60;
    int loc120;
    double scale = this.scale * 0.5;

    if (pContext.random() < 0.5) {
      posNeg = -1;
    }

    // Determine whether one or two major planes
    int majplane = 1;
    double abmajp = fabs(this.majp);
    if (abmajp <= 1.0) {
      majplane = 1; // Want either 1 or 2
    } else {
      majplane = 2;
      boost = (abmajp - 1.0) * 0.5; // distance above and below XY plane
    }

    //      Creating Z factors relative to the planes
    if (majplane == 2) {
      pVarTP.z += pAffineTP.z * 0.5 * this.zlift + (posNeg * boost);
    } else {
      pVarTP.z += pAffineTP.z * 0.5 * this.zlift;
    }

    // Work out the segments and hexagonal nodes
    if (this._rswtch <= 1) { // Occasion to build using 60 degree segments
      //loc60 = trunc(pContext.random()*6.0);  // random nodes selection
      loc60 = this._fcycle; // sequential nodes selection
      pVarTP.x = ((pVarTP.x + pAffineTP.x) * scale) + (lrmaj * _seg60x[loc60]);
      pVarTP.y = ((pVarTP.y + pAffineTP.y) * scale) + (lrmaj * _seg60y[loc60]);
      this._fcycle += 1;
    } else {// Occasion to build on 120 degree segments

      //loc120 = trunc(pContext.random()*3.0);  // random nodes selection
      loc120 = this._bcycle; // sequential nodes selection
      pVarTP.x = ((pVarTP.x + pAffineTP.x) * scale) + (lrmaj * _seg120x[loc120]);
      pVarTP.y = ((pVarTP.y + pAffineTP.y) * scale) + (lrmaj * _seg120y[loc120]);
      this._bcycle += 1;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{majp, scale, zlift};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MAJP.equalsIgnoreCase(pName))
      majp = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_ZLIFT.equalsIgnoreCase(pName))
      zlift = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hexaplay3D";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    /*  Set up two major angle systems */
    _rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses 6 or 3 nodes
    double hlift = sin(M_PI / 3.0);
    _fcycle = 0;
    _bcycle = 0;
    _seg60x[0] = 1.0;
    _seg60x[1] = 0.5;
    _seg60x[2] = -0.5;
    _seg60x[3] = -1.0;
    _seg60x[4] = -0.5;
    _seg60x[5] = 0.5;

    _seg60y[0] = 0.0;
    _seg60y[1] = hlift;
    _seg60y[2] = hlift;
    _seg60y[3] = 0.0;
    _seg60y[4] = -hlift;
    _seg60y[5] = -hlift;

    _seg120x[0] = 1.0;
    _seg120x[1] = -0.5;
    _seg120x[2] = -0.5;

    _seg120y[0] = 0.0;
    _seg120y[1] = hlift;
    _seg120y[2] = -hlift;
  }

}
