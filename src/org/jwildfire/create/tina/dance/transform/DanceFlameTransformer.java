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
package org.jwildfire.create.tina.dance.transform;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.dance.DanceFlameHolder;
import org.jwildfire.create.tina.transform.XFormTransformService;

public class DanceFlameTransformer {
  private final DanceFlameHolder flameHolder;
  private double finalXFormAlpha = 0.0;

  public DanceFlameTransformer(DanceFlameHolder pFlameHolder) {
    flameHolder = pFlameHolder;
  }

  public void transformFlame(Flame pFlame, short pFFTData[]) {
    transformXForm1(pFFTData, pFlame);
    transformXForm2(pFFTData, pFlame);
    transformXForm3(pFFTData, pFlame);
    transformXForm4(pFFTData, pFlame);
    transformXFormF(pFFTData, pFlame);
  }

  private void transformXForm1(short[] pFFT, Flame pFlame) {
    if (flameHolder.getxForm0() != null) {
      double amp0 = getFFTValue(pFFT, 0);
      double amp1 = getFFTValue(pFFT, 1);
      double amp2 = getFFTValue(pFFT, 2);
      XForm xForm = flameHolder.getxForm0().makeCopy();
      XFormTransformService.rotate(xForm, -amp0 * 7, false);
      XFormTransformService.localTranslate(xForm, -amp1 * 0.5, amp2 * 0.5);
      pFlame.getXForms().set(0, xForm);
    }
  }

  private void transformXForm2(short[] pFFT, Flame pFlame) {
    if (flameHolder.getxForm1() != null) {
      double amp3 = getFFTValue(pFFT, 3);
      double amp4 = getFFTValue(pFFT, 4);
      double amp5 = getFFTValue(pFFT, 5);
      XForm xForm = flameHolder.getxForm1().makeCopy();
      XFormTransformService.rotate(xForm, -amp3 * 2, false);
      XFormTransformService.localTranslate(xForm, amp4 * 0.25, -amp5 * 0.25);
      pFlame.getXForms().set(1, xForm);
    }
  }

  private void transformXForm3(short[] pFFT, Flame pFlame) {
    if (flameHolder.getxForm2() != null) {
      double amp7 = getFFTValue(pFFT, 47);
      double amp8 = getFFTValue(pFFT, 48);
      double amp9 = getFFTValue(pFFT, 49);
      XForm xForm = flameHolder.getxForm2().makeCopy();
      XFormTransformService.rotate(xForm, amp7 * 5, false);
      XFormTransformService.localTranslate(xForm, -amp8 * 0.75, amp9 * 0.5);
      pFlame.getXForms().set(2, xForm);
    }
  }

  private void transformXForm4(short[] pFFT, Flame pFlame) {
    if (flameHolder.getxForm3() != null) {
      double amp10 = getFFTValue(pFFT, 50);
      double amp11 = getFFTValue(pFFT, 51);
      double amp12 = getFFTValue(pFFT, 52);
      XForm xForm = flameHolder.getxForm3().makeCopy();
      XFormTransformService.rotate(xForm, amp10 * 3, false);
      XFormTransformService.localTranslate(xForm, amp11 * 0.5, amp12 * 0.25);
      pFlame.getXForms().set(3, xForm);
    }
  }

  private void transformXFormF(short[] pFFT, Flame pFlame) {
    if (flameHolder.getxFormF() != null) {
      double amp2 = getFFTValue(pFFT, 2);
      double amp6 = getFFTValue(pFFT, 6);
      XForm xForm = flameHolder.getxFormF().makeCopy();
      //      XFormTransformService.rotate(xForm, finalXFormAlpha, false);
      finalXFormAlpha += amp6;
      if (finalXFormAlpha > 360)
        finalXFormAlpha -= 360;
      XFormTransformService.scale(xForm, 1.0 + (amp2 + amp6) * 0.25, true, true, false);
      pFlame.getFinalXForms().clear();
      pFlame.getFinalXForms().add(xForm);
    }
  }

  private double getFFTValue(short[] pFFT, int pIdx) {
    short val = pIdx < pFFT.length ? pFFT[pIdx] : 0;
    return 2.0 * val / (double) Short.MAX_VALUE;
  }
}
