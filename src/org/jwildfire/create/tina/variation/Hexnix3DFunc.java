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

public class Hexnix3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_MAJP = "majp";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_ZLIFT = "zlift";
  private static final String PARAM_3SIDE = "3side";
  private static final String[] paramNames = {PARAM_MAJP, PARAM_SCALE, PARAM_ZLIFT, PARAM_3SIDE};

  private double majp = 1.0; // establishes 1 or 2 planes, and if 2, the distance between them
  private double scale = 0.25; // scales the effect of X and Y
  private double zlift = 0.0; // scales the effect of Z axis within the snowflake
  private double _3side = 0.67; // scales the triangle within the hex - the 120 degree figure
  private double _seg60x[] = new double[6];
  private double _seg60y[] = new double[6];
  private double _seg120x[] = new double[3];
  private double _seg120y[] = new double[3];
  private int _rswtch; //  for choosing between 6 or 3 segments to a plane
  private int _fcycle; //  markers to count cycles... 
  private int _bcycle;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* hexnix3D by Larry Berlin, http://aporev.deviantart.com/art/3D-Plugins-Collection-One-138514007?q=gallery%3Aaporev%2F8229210&qo=15 */
    if (_fcycle > 5) {// Resets the cyclic counting
      _fcycle = 0;
      _rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses new 6 or 3 nodes
    }
    if (_bcycle > 2) {
      _bcycle = 0;
      _rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses new 6 or 3 nodes
    }

    double lrmaj = pAmount; // Sets hexagon length radius - major plane
    double smooth = 1.0;
    double smRotxTP = 0.0;
    double smRotyTP = 0.0;
    double smRotxFT = 0.0;
    double smRotyFT = 0.0;
    double gentleZ = 0.0;

    if (fabs(pAmount) <= 0.5) {
      smooth = pAmount * 2.0;
    } else {
      smooth = 1.0;
    }
    double boost = 0.0; //  Boost is the separation distance between the two planes
    int posNeg = 1;
    int loc60;
    int loc120;
    double scale = this.scale;
    double scale3 = this._3side;

    if (pContext.random() < 0.5) {
      posNeg = -1;
    }

    // Determine whether one or two major planes
    int majplane = 0;
    double abmajp = fabs(this.majp);
    if (abmajp <= 1.0) {
      majplane = 0; // 0= 1 plate active  1= transition to two plates active  2= defines two plates
      boost = 0.0;
    } else if (abmajp > 1.0 && abmajp < 2.0) {
      majplane = 1;
      boost = 0.0;
    } else {
      majplane = 2;
      boost = (abmajp - 2.0) * 0.5; // distance above and below XY plane
    }

    //      Creating Z factors relative to the planes 
    if (majplane == 0) {
      pVarTP.z += smooth * pAffineTP.z * scale * this.zlift; // single plate instructions
    } else if (majplane == 1 && this.majp < 0.0) {// Transition for reversing plates  because  majp is negative value
      if (this.majp < -1.0 && this.majp >= -2.0) {
        gentleZ = (abmajp - 1.0); //  Set transition smoothing values  0.00001 to 1.0    
      } else {
        gentleZ = 1.0; // full effect explicit default value
      }
      // Begin reverse transition - starts with pVarTP.z==pVarTP.z proceeds by gradual negative
      if (posNeg < 0) {
        pVarTP.z += -2.0 * (pVarTP.z * gentleZ); // gradually grows negative plate, in place, no boost,
      }
    }
    if (majplane == 2 && this.majp < 0.0) {// Begin the splitting operation, animation transition is done
      if (posNeg > 0) {//  The splitting operation positive side
        pVarTP.z += (smooth * (pAffineTP.z * scale * this.zlift + boost));
      } else {//  The splitting operation negative side
        pVarTP.z = (pVarTP.z - (2.0 * smooth * pVarTP.z)) + (smooth * posNeg * (pAffineTP.z * scale * this.zlift + boost));
      }
    } else {//  majp > 0.0       The splitting operation
      pVarTP.z += smooth * (pAffineTP.z * scale * this.zlift + (posNeg * boost));
    }

    if (this._rswtch <= 1) {//  Occasion to build using 60 degree segments    
      loc60 = (int) trunc(pContext.random() * 6.0); // random nodes selection
      //loc60 = this.fcycle;   // sequential nodes selection - seems to create artifacts that are progressively displaced
      smRotxTP = (smooth * scale * pVarTP.x * _seg60x[loc60]) - (smooth * scale * pVarTP.y * _seg60y[loc60]);
      smRotyTP = (smooth * scale * pVarTP.y * _seg60x[loc60]) + (smooth * scale * pVarTP.x * _seg60y[loc60]);
      smRotxFT = (pAffineTP.x * smooth * scale * _seg60x[loc60]) - (pAffineTP.y * smooth * scale * _seg60y[loc60]);
      smRotyFT = (pAffineTP.y * smooth * scale * _seg60x[loc60]) + (pAffineTP.x * smooth * scale * _seg60y[loc60]);

      pVarTP.x = pVarTP.x * (1.0 - smooth) + smRotxTP + smRotxFT + smooth * lrmaj * _seg60x[loc60];
      pVarTP.y = pVarTP.y * (1.0 - smooth) + smRotyTP + smRotyFT + smooth * lrmaj * _seg60y[loc60];

      this._fcycle += 1;
    } else { // Occasion to build on 120 degree segments
      loc120 = (int) trunc(pContext.random() * 3.0); // random nodes selection
      //loc120 = this.bcycle;  // sequential nodes selection - seems to create artifacts that are progressively displaced
      smRotxTP = (smooth * scale * pVarTP.x * _seg120x[loc120]) - (smooth * scale * pVarTP.y * _seg120y[loc120]);
      smRotyTP = (smooth * scale * pVarTP.y * _seg120x[loc120]) + (smooth * scale * pVarTP.x * _seg120y[loc120]);
      smRotxFT = (pAffineTP.x * smooth * scale * _seg120x[loc120]) - (pAffineTP.y * smooth * scale * _seg120y[loc120]);
      smRotyFT = (pAffineTP.y * smooth * scale * _seg120x[loc120]) + (pAffineTP.x * smooth * scale * _seg120y[loc120]);

      pVarTP.x = pVarTP.x * (1.0 - smooth) + smRotxTP + smRotxFT + smooth * lrmaj * scale3 * _seg120x[loc120];
      pVarTP.y = pVarTP.y * (1.0 - smooth) + smRotyTP + smRotyFT + smooth * lrmaj * scale3 * _seg120y[loc120];

      this._bcycle += 1;
    }
    /*
    Rotations need to interchange smoothly between x and y values (pseudo code)
    new x = in_x*cos(r) - in_y*sin(r) + movedLoci_x;
    new y = in_y*cos(r) + in_x*sin(r) + movedLoci_y;
    */
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{majp, scale, zlift, _3side};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MAJP.equalsIgnoreCase(pName))
      majp = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_ZLIFT.equalsIgnoreCase(pName))
      zlift = pValue;
    else if (PARAM_3SIDE.equalsIgnoreCase(pName))
      _3side = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "hexnix3D";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    /*  Set up two major angle systems */
    _rswtch = (int) trunc(pContext.random() * 3.0); //  Chooses 6 or 3 nodes
    double hlift = sin(M_PI / 3.0); // sin(60)
    _fcycle = 0;
    _bcycle = 0;
    _seg60x[0] = 1.0;
    _seg60x[1] = 0.5;
    _seg60x[2] = -0.5;
    _seg60x[3] = -1.0;
    _seg60x[4] = -0.5;
    _seg60x[5] = 0.5;

    _seg60y[0] = 0.0;
    _seg60y[1] = -hlift;
    _seg60y[2] = -hlift;
    _seg60y[3] = 0.0;
    _seg60y[4] = hlift;
    _seg60y[5] = hlift;

    _seg120x[0] = 0.0; // These settings cause the 3-node setting to 
    _seg120x[1] = cos(7.0 * M_PI / 6.0); // rotate 30 degrees relative to the hex structure.
    _seg120x[2] = cos(11.0 * M_PI / 6.0); // 

    _seg120y[0] = -1.0;
    _seg120y[1] = 0.5;
    _seg120y[2] = 0.5;
  }

}
