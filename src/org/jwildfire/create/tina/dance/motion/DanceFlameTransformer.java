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
package org.jwildfire.create.tina.dance.motion;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.dance.DancingFlame;
import org.jwildfire.create.tina.transform.XFormTransformService;

public class DanceFlameTransformer {
  private double finalXFormAlpha = 0.0;

  public Flame createTransformedFlame(DancingFlame pFlame, short pFFTData[], long pTime) {
    Flame res = pFlame.getFlame().makeCopy();
    transformXForm1(pFFTData, res);
    transformXForm2(pFFTData, res);
    transformXForm3(pFFTData, res);
    transformXForm4(pFFTData, res);
    transformXFormF(pFFTData, res);
    return res;
  }

  private void transformXForm1(short[] pFFT, Flame pFlame) {
    if (pFlame.getXForms().size() > 0) {
      double amp0 = getFFTValue(pFFT, 0);
      double amp1 = getFFTValue(pFFT, 1);
      double amp2 = getFFTValue(pFFT, 2);
      XForm xForm = pFlame.getXForms().get(0);
      XFormTransformService.rotate(xForm, -amp0 * 7, false);
      XFormTransformService.localTranslate(xForm, -amp1 * 0.5, amp2 * 0.5);
    }
  }

  private void transformXForm2(short[] pFFT, Flame pFlame) {
    if (pFlame.getXForms().size() > 1) {
      double amp3 = getFFTValue(pFFT, 3);
      double amp4 = getFFTValue(pFFT, 4);
      double amp5 = getFFTValue(pFFT, 5);
      XForm xForm = pFlame.getXForms().get(1);
      XFormTransformService.rotate(xForm, -amp3 * 2, false);
      XFormTransformService.localTranslate(xForm, amp4 * 0.25, -amp5 * 0.25);
    }
  }

  private void transformXForm3(short[] pFFT, Flame pFlame) {
    if (pFlame.getXForms().size() > 2) {
      double amp7 = getFFTValue(pFFT, 47);
      double amp8 = getFFTValue(pFFT, 48);
      double amp9 = getFFTValue(pFFT, 49);
      XForm xForm = pFlame.getXForms().get(2);
      XFormTransformService.rotate(xForm, amp7 * 5, false);
      XFormTransformService.localTranslate(xForm, -amp8 * 0.75, amp9 * 0.5);
      pFlame.getXForms().set(2, xForm);
    }
  }

  private void transformXForm4(short[] pFFT, Flame pFlame) {
    if (pFlame.getXForms().size() > 3) {
      double amp10 = getFFTValue(pFFT, 50);
      double amp11 = getFFTValue(pFFT, 51);
      double amp12 = getFFTValue(pFFT, 52);
      XForm xForm = pFlame.getXForms().get(3);
      XFormTransformService.rotate(xForm, amp10 * 3, false);
      XFormTransformService.localTranslate(xForm, amp11 * 0.5, amp12 * 0.25);
      pFlame.getXForms().set(3, xForm);
    }
  }

  private void transformXFormF(short[] pFFT, Flame pFlame) {
    if (pFlame.getFinalXForms().size() > 0) {
      double amp2 = getFFTValue(pFFT, 2);
      double amp6 = getFFTValue(pFFT, 6);
      XForm xForm = pFlame.getFinalXForms().get(pFlame.getFinalXForms().size() - 1);
      finalXFormAlpha += amp6;
      if (finalXFormAlpha > 360)
        finalXFormAlpha -= 360;
      XFormTransformService.scale(xForm, 1.0 + (amp2 + amp6) * 0.25, true, true, false);
    }
  }

  private double getFFTValue(short[] pFFT, int pIdx) {
    short val = pIdx < pFFT.length ? pFFT[pIdx] : 0;
    return 2.0 * val / (double) Short.MAX_VALUE;
  }
}
