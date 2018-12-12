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

public class PRose3DFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_L = "l";
  private static final String PARAM_K = "k";
  private static final String PARAM_C = "c";
  private static final String PARAM_Z1 = "z1";
  private static final String PARAM_Z2 = "z2";
  private static final String PARAM_REF_SC = "refSc";
  private static final String PARAM_OPT = "opt";
  private static final String PARAM_OPT_SC = "optSc";
  private static final String PARAM_OPT3 = "opt3";
  private static final String PARAM_TRANSP = "transp";
  private static final String PARAM_DIST = "dist";
  private static final String PARAM_WAGSC = "wagsc";
  private static final String PARAM_CRVSC = "srvsc";
  private static final String PARAM_F = "f";
  private static final String PARAM_WIGSC = "wigsc";
  private static final String PARAM_OFFSET = "offset";
  private static final String[] paramNames = {PARAM_L, PARAM_K, PARAM_C, PARAM_Z1, PARAM_Z2, PARAM_REF_SC, PARAM_OPT, PARAM_OPT_SC, PARAM_OPT3, PARAM_TRANSP, PARAM_DIST, PARAM_WAGSC, PARAM_CRVSC, PARAM_F, PARAM_WIGSC, PARAM_OFFSET};

  private double l = 1.0;
  private double k = 3.0 + (Math.random() < 0.5 ? Math.random() * 10.0 : Tools.randomInt(15));
  private double c = 0.0;
  private double z1 = 1.0;
  private double z2 = 1.0;
  private double refSc = 1.0;
  private double opt = 1.0;
  private double optSc = 1.0;
  private double opt3 = 0.0;
  private double transp = 0.5;
  private double dist = 1.0;
  private double wagsc = 0.0;
  private double crvsc = 0.0;
  private double f = 3.0;
  private double wigsc = 0.0;
  private double offset = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // pRose3D by Larry Berlin, http://aporev.deviantart.com/gallery/#/d2blmhg 
    int posNeg = 1;
    double th = 0.0;
    double sth, cth, pang, wig, wag, wag2, wag3, wag12 = 0.0, waggle = 0.0, ghostPrep; // ?? offset as variable? or preset calculated value?
    double length = this.l;
    double numPetals = this.k;
    double constant = this.c;
    double scaleZ1 = this.z1;
    double scaleZ2 = this.z2;
    double smooth12 = fabs(this.opt);
    double smooth3 = fabs(this.opt3);
    double opScale = fabs(this.optSc);
    double ghost = sqr(this.transp); // Sets the testing limit for randoms to determine transparency
    double wagScale = this.wagsc;
    double curveScale = this.crvsc;
    double frequency = this.f * M_2PI;
    double wigScale = this.wigsc * 0.5;
    double rad = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double curve1 = rad / length;
    double curve2 = sqr(rad / length);
    double curve3 = (rad - length * 0.5) / length;
    double curve4 = sqr(sqr(rad / length));
    if (smooth12 > 2.0) {
      smooth12 = 2.0;
    }
    double antiOpt1 = 2.0 - smooth12;
    ghostPrep = pContext.getRandGen().random();
    th = atan2(pAffineTP.y, pAffineTP.x); // returns the angle this point has with the axis
    sth = sin(th);
    cth = cos(th);
    this._optDir = Math.copySign(1.0, this.opt);
    this._petalsSign = Math.copySign(1.0, this.k);

    if (this.opt3 < 0.0) {
      this._optDir = -1.0;
    }
    if (smooth3 > 1.0) {
      smooth3 = 1.0;
    }
    if (length == 0.0) {
      length = 0.000001;
    }

    if (fabs(numPetals) < 0.0001) {
      numPetals = 0.0001 * this._petalsSign; // need a non-zero minimum
    }

    if (pContext.getRandGen().random() < 0.5) {
      posNeg = -1;
    }

    if (this._cycle == 0.0) {
      pang = th / this._cycle + EPSILON; // point's angle location relative to which petal it belongs to
    } else {
      pang = th / this._cycle; // point's angle location relative to which petal it belongs to
    }

    // circumference relative offset, with high frequency values, this causes the ripples to progress along the perimeter
    wig = pang * frequency * 0.5 + this.offset * this._cycle;

    //  Develop means to structure Z 
    if (this._optDir < 0.0) {
      wag = sin(curve1 * M_PI * opScale) + wagScale * 0.4 * rad + curveScale * 0.5 * (sin(curve2 * M_PI)); //  length anchored
      wag3 = sin(curve4 * M_PI * opScale) + wagScale * sqr(rad) * 0.4 + curveScale * 0.5 * (cos(curve3 * M_PI)); //  length anchored
    } else {
      wag = sin(curve1 * M_PI * opScale) + wagScale * 0.4 * rad + curveScale * 0.5 * (cos(curve3 * M_PI)); //  two curveScale methods
      wag3 = sin(curve4 * M_PI * opScale) + wagScale * sqr(rad) * 0.4 + curveScale * 0.5 * (sin(curve2 * M_PI)); //  length anchored
    }

    wag2 = sin(curve2 * M_PI * opScale) + wagScale * 0.4 * rad + curveScale * 0.5 * (cos(curve3 * M_PI)); //  length anchored  

    /*
        Animation Method
        Start with option 1
      smoothly change to option 2
      Allow option 3 to replace either of the first two
    */

    if (smooth12 <= 1.0) {
      wag12 = wag;
    } else if (smooth12 <= 2.0 && smooth12 > 1.0) {
      wag12 = wag2 * (1.0 - antiOpt1) + wag * antiOpt1;
    } else if (smooth12 > 2.0) {
      wag12 = wag2;
    }
    if (smooth3 == 0.0) {
      waggle = wag12;
    } else if (smooth3 > 0.0) {
      waggle = wag12 * (1.0 - smooth3) + wag3 * smooth3;
    }

    //    Basic Polar Rose drawing method
    //pVarTP.x += pAmount*0.5*(length*cos(numPetals*th+constant))*cth; 
    //pVarTP.y += pAmount*0.5*(length*cos(numPetals*th+constant))*sth; 

    if (this._optDir > 0.0) {
      ghost = 0.0;
    }

    if (ghostPrep < ghost) // occasion to draw reflection... but only when posNeg is negative
    {
      if (posNeg < 0) // ghost starts with the reflection transparent
      {
        pVarTP.x += pAmount * 0.5 * this.refSc * (length * cos(numPetals * th + constant)) * cth;
        pVarTP.y += pAmount * 0.5 * this.refSc * (length * cos(numPetals * th + constant)) * sth;
        pVarTP.z += pAmount * -0.5 * ((scaleZ2 * waggle + sqr(rad * 0.5) * sin(wig) * wigScale) + (this.dist)); // adjustable height

      } else {
        pVarTP.x += pAmount * 0.5 * (length * cos(numPetals * th + constant)) * cth;
        pVarTP.y += pAmount * 0.5 * (length * cos(numPetals * th + constant)) * sth;
        pVarTP.z += pAmount * 0.5 * ((scaleZ1 * waggle + sqr(rad * 0.5) * sin(wig) * wigScale) + (this.dist));
      }
    } else // this is the option when optDir > 0.0
    {
      pVarTP.x += pAmount * 0.5 * (length * cos(numPetals * th + constant)) * cth;
      pVarTP.y += pAmount * 0.5 * (length * cos(numPetals * th + constant)) * sth;
      pVarTP.z += pAmount * 0.5 * ((scaleZ1 * waggle + sqr(rad * 0.5) * sin(wig) * wigScale) + (this.dist));
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{l, k, c, z1, z2, refSc, opt, optSc, opt3, transp, dist, wagsc, crvsc, f, wigsc, offset};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_L.equalsIgnoreCase(pName))
      l = pValue;
    else if (PARAM_K.equalsIgnoreCase(pName))
      k = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_Z1.equalsIgnoreCase(pName))
      z1 = pValue;
    else if (PARAM_Z2.equalsIgnoreCase(pName))
      z2 = pValue;
    else if (PARAM_REF_SC.equalsIgnoreCase(pName))
      refSc = pValue;
    else if (PARAM_OPT.equalsIgnoreCase(pName))
      opt = pValue;
    else if (PARAM_OPT_SC.equalsIgnoreCase(pName))
      optSc = pValue;
    else if (PARAM_OPT3.equalsIgnoreCase(pName))
      opt3 = pValue;
    else if (PARAM_TRANSP.equalsIgnoreCase(pName))
      transp = pValue;
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else if (PARAM_WAGSC.equalsIgnoreCase(pName))
      wagsc = pValue;
    else if (PARAM_CRVSC.equalsIgnoreCase(pName))
      crvsc = pValue;
    else if (PARAM_F.equalsIgnoreCase(pName))
      f = pValue;
    else if (PARAM_WIGSC.equalsIgnoreCase(pName))
      wigsc = pValue;
    else if (PARAM_OFFSET.equalsIgnoreCase(pName))
      offset = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pRose3D";
  }

  private double _cycle, _optDir, _petalsSign;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    // divide in pre-calc, multiply in calc, 
    if (k == 0.0) {
      _cycle = M_2PI / (k + 0.000001); // describes the segment of a full circle used for each petal
    } else {
      _cycle = M_2PI / (k); // describes the segment of a full circle used for each petal
    }
    _optDir = 0.0;
    _petalsSign = 0.0;
  }

}
