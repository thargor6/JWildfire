/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
package org.jwildfire.create.tina.animate;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.variation.Variation;

public class FlameMorphService {

  public static Flame morphFlames(Flame pFlame1, Flame pFlame2, int pFrame, int pFrames) {
    if (pFrame < 1 || pFrames < 2)
      throw new IllegalStateException();
    double fScl = (double) (pFrame - 1) / (pFrames - 1);
    Flame res = pFlame1.makeCopy();
    res.getXForms().clear();
    // morph XForms  
    int size1 = pFlame1.getXForms().size();
    int size2 = pFlame2.getXForms().size();
    int maxSize = size1 > size2 ? size1 : size2;
    for (int i = 0; i < maxSize; i++) {
      XForm xForm1 = i < size1 ? pFlame1.getXForms().get(i) : null;
      XForm xForm2 = i < size2 ? pFlame2.getXForms().get(i) : null;
      XForm morphedXForm = morphXForms(xForm1, xForm2, fScl);
      if (morphedXForm != null) {
        res.getXForms().add(morphedXForm);
      }
    }

    //    for (XForm xForm : pFlame1.getXForms()) {
    //      res.getXForms().add(morphXForms(xForm, null, fScl));
    //    }
    //    for (XForm xForm : pFlame2.getXForms()) {
    //      res.getXForms().add(morphXForms(null, xForm, fScl));
    //    }

    // morph final XForms
    {
      XForm xForm1 = pFlame1.getFinalXForm();
      XForm xForm2 = pFlame2.getFinalXForm();
      XForm morphedXForm = morphXForms(xForm1, xForm2, fScl);
      if (morphedXForm != null) {
        res.setFinalXForm(morphedXForm);
      }
    }
    // morph colors
    RGBPalette palette1 = pFlame1.getPalette();
    RGBPalette palette2 = pFlame2.getPalette();
    for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
      RGBColor color1 = palette1.getColor(i);
      RGBColor color2 = palette2.getColor(i);
      int red = Tools.roundColor(color1.getRed() + (color2.getRed() - color1.getRed()) * fScl);
      int green = Tools.roundColor(color1.getGreen() + (color2.getGreen() - color1.getGreen()) * fScl);
      int blue = Tools.roundColor(color1.getBlue() + (color2.getBlue() - color1.getBlue()) * fScl);
      res.getPalette().setColor(i, red, green, blue);
    }
    // morph camera settings etc.
    res.setCamDOF(morphValue(pFlame1.getCamDOF(), pFlame2.getCamDOF(), fScl));
    res.setCamPerspective(morphValue(pFlame1.getCamPerspective(), pFlame2.getCamPerspective(), fScl));
    res.setCamPitch(morphValue(pFlame1.getCamPitch(), pFlame2.getCamPitch(), fScl));
    res.setCamYaw(morphValue(pFlame1.getCamYaw(), pFlame2.getCamYaw(), fScl));
    res.setCamRoll(morphValue(pFlame1.getCamRoll(), pFlame2.getCamRoll(), fScl));
    res.setCamZ(morphValue(pFlame1.getCamZ(), pFlame2.getCamZ(), fScl));
    res.setCamZoom(morphValue(pFlame1.getCamZoom(), pFlame2.getCamZoom(), fScl));
    res.setBGColorRed(morphColorValue(pFlame1.getBGColorRed(), pFlame2.getBGColorRed(), fScl));
    res.setBGColorGreen(morphColorValue(pFlame1.getBGColorGreen(), pFlame2.getBGColorGreen(), fScl));
    res.setBGColorBlue(morphColorValue(pFlame1.getBGColorBlue(), pFlame2.getBGColorBlue(), fScl));
    res.setBrightness(morphValue(pFlame1.getBrightness(), pFlame2.getBrightness(), fScl));
    res.setCentreX(morphValue(pFlame1.getCentreX(), pFlame2.getCentreX(), fScl));
    res.setCentreY(morphValue(pFlame1.getCentreY(), pFlame2.getCentreY(), fScl));
    res.setContrast(morphValue(pFlame1.getContrast(), pFlame2.getContrast(), fScl));
    res.setGamma(morphValue(pFlame1.getGamma(), pFlame2.getGamma(), fScl));
    res.setGammaThreshold(morphValue(pFlame1.getGammaThreshold(), pFlame2.getGammaThreshold(), fScl));
    res.setPixelsPerUnit(morphValue(pFlame1.getPixelsPerUnit(), pFlame2.getPixelsPerUnit(), fScl));
    res.setPreserveZ(morphValue(pFlame1.isPreserveZ(), pFlame2.isPreserveZ(), fScl));
    res.setSpatialFilterRadius(morphValue(pFlame1.getSpatialFilterRadius(), pFlame2.getSpatialFilterRadius(), fScl));
    res.setVibrancy(morphValue(pFlame1.getVibrancy(), pFlame2.getVibrancy(), fScl));
    res.setWhiteLevel(morphValue(pFlame1.getWhiteLevel(), pFlame2.getWhiteLevel(), fScl));
    return res;
  }

  private static XForm morphXForms(XForm pXForm1, XForm pXForm2, double pFScl) {
    if (pXForm1 != null && pXForm2 == null) {
      XForm res = new XForm();
      res.setWeight(morphValue(pXForm1.getWeight(), 0.0, pFScl));
      res.setColor(morphValue(pXForm1.getColor(), 0.0, pFScl));
      res.setColorSymmetry(morphValue(pXForm1.getColorSymmetry(), 0.0, pFScl));
      res.setCoeff00(morphValue(pXForm1.getCoeff00(), 1.0, pFScl));
      res.setCoeff01(morphValue(pXForm1.getCoeff01(), 0.0, pFScl));
      res.setCoeff10(morphValue(pXForm1.getCoeff10(), 0.0, pFScl));
      res.setCoeff11(morphValue(pXForm1.getCoeff11(), 1.0, pFScl));
      res.setCoeff20(morphValue(pXForm1.getCoeff20(), 0.0, pFScl));
      res.setCoeff21(morphValue(pXForm1.getCoeff21(), 0.0, pFScl));
      res.setOpacity(morphValue(pXForm1.getOpacity(), 0.0, pFScl));
      res.setDrawMode(pXForm1.getDrawMode());
      for (int i = 0; i < pXForm1.getModifiedWeights().length; i++) {
        res.getModifiedWeights()[i] = morphValue(pXForm1.getModifiedWeights()[i], 1.0, pFScl);
      }
      res.clearVariations();
      for (int vIdx = 0; vIdx < pXForm1.getVariationCount(); vIdx++) {
        Variation var = pXForm1.getVariation(vIdx);
        Variation newVar = new Variation();
        newVar.setAmount(morphValue(var.getAmount(), 0.0, pFScl));
        newVar.setFunc(var.getFunc());
        res.addVariation(newVar);
      }
      return res;
    }
    else if (pXForm1 == null && pXForm2 != null) {
      XForm res = new XForm();
      res.setWeight(morphValue(0.0, pXForm2.getWeight(), pFScl));
      res.setColor(morphValue(0.0, pXForm2.getColor(), pFScl));
      res.setColorSymmetry(morphValue(0.0, pXForm2.getColorSymmetry(), pFScl));
      res.setCoeff00(morphValue(1.0, pXForm2.getCoeff00(), pFScl));
      res.setCoeff01(morphValue(0.0, pXForm2.getCoeff01(), pFScl));
      res.setCoeff10(morphValue(0.0, pXForm2.getCoeff10(), pFScl));
      res.setCoeff11(morphValue(1.0, pXForm2.getCoeff11(), pFScl));
      res.setCoeff20(morphValue(0.0, pXForm2.getCoeff20(), pFScl));
      res.setCoeff21(morphValue(0.0, pXForm2.getCoeff21(), pFScl));
      res.setOpacity(morphValue(0.0, pXForm2.getOpacity(), pFScl));
      res.setDrawMode(pXForm2.getDrawMode());
      for (int i = 0; i < pXForm2.getModifiedWeights().length; i++) {
        res.getModifiedWeights()[i] = morphValue(1.0, pXForm2.getModifiedWeights()[i], pFScl);
      }
      res.clearVariations();
      for (int vIdx = 0; vIdx < pXForm2.getVariationCount(); vIdx++) {
        Variation var = pXForm2.getVariation(vIdx);
        Variation newVar = new Variation();
        newVar.setAmount(morphValue(0.0, var.getAmount(), pFScl));
        newVar.setFunc(var.getFunc());
        res.addVariation(newVar);
      }
      return res;
    }
    else if (pXForm1 != null && pXForm2 != null) {
      XForm res = new XForm();
      res.setWeight(morphValue(pXForm1.getWeight(), pXForm2.getWeight(), pFScl));
      res.setColor(morphValue(pXForm1.getColor(), pXForm2.getColor(), pFScl));
      res.setColorSymmetry(morphValue(pXForm1.getColorSymmetry(), pXForm2.getColorSymmetry(), pFScl));
      res.setCoeff00(morphValue(pXForm1.getCoeff00(), pXForm2.getCoeff00(), pFScl));
      res.setCoeff01(morphValue(pXForm1.getCoeff01(), pXForm2.getCoeff01(), pFScl));
      res.setCoeff10(morphValue(pXForm1.getCoeff10(), pXForm2.getCoeff10(), pFScl));
      res.setCoeff11(morphValue(pXForm1.getCoeff11(), pXForm2.getCoeff11(), pFScl));
      res.setCoeff20(morphValue(pXForm1.getCoeff20(), pXForm2.getCoeff20(), pFScl));
      res.setCoeff21(morphValue(pXForm1.getCoeff21(), pXForm2.getCoeff21(), pFScl));
      res.setOpacity(morphValue(pXForm1.getOpacity(), pXForm2.getOpacity(), pFScl));
      res.setDrawMode(pXForm1.getDrawMode());
      for (int i = 0; i < pXForm1.getModifiedWeights().length; i++) {
        res.getModifiedWeights()[i] = morphValue(pXForm1.getModifiedWeights()[i], pXForm2.getModifiedWeights()[i], pFScl);
      }
      res.clearVariations();
      List<Integer> dstProcessed = new ArrayList<Integer>();
      for (int vIdx = 0; vIdx < pXForm1.getVariationCount(); vIdx++) {
        Variation var1 = pXForm1.getVariation(vIdx);
        Variation newVar = new Variation();
        Variation var2 = null;
        for (int idx = 0; idx < pXForm2.getVariationCount(); idx++) {
          if (dstProcessed.indexOf(idx) < 0 || pXForm2.getVariation(idx).getFunc().getName().equals(var1.getFunc().getName())) {
            var2 = pXForm2.getVariation(idx);
            dstProcessed.add(idx);
            break;
          }
        }
        newVar.setFunc(var1.getFunc());
        if (var2 == null) {
          newVar.setAmount(morphValue(var1.getAmount(), 0.0, pFScl));
        }
        else {
          newVar.setAmount(morphValue(var1.getAmount(), var2.getAmount(), pFScl));
        }
        res.addVariation(newVar);
      }
      for (int idx = 0; idx < pXForm2.getVariationCount(); idx++) {
        if (dstProcessed.indexOf(idx) < 0) {
          Variation var2 = pXForm2.getVariation(idx);
          Variation newVar = new Variation();
          newVar.setFunc(var2.getFunc());
          newVar.setAmount(morphValue(0.0, var2.getAmount(), pFScl));
          res.addVariation(newVar);
        }
      }
      return res;
    }
    else {
      return null;
    }
  }

  private static double morphValue(double pValue1, double pValue2, double pFScl) {
    if (pFScl < 0.0) {
      pFScl = 0.0;
    }
    else if (pFScl > 1.0) {
      pFScl = 1.0;
    }
    return pValue1 + (pValue2 - pValue1) * pFScl;
  }

  private static int morphValue(int pValue1, int pValue2, double pFScl) {
    if (pFScl < 0.0) {
      pFScl = 0.0;
    }
    else if (pFScl > 1.0) {
      pFScl = 1.0;
    }
    return Tools.FTOI(pValue1 + (double) (pValue2 - pValue1) * pFScl);
  }

  private static boolean morphValue(boolean pValue1, boolean pValue2, double pFScl) {
    return pFScl >= 0.5 ? pValue2 : pValue1;
  }

  private static int morphColorValue(int pValue1, int pValue2, double pFScl) {
    if (pFScl < 0.0) {
      pFScl = 0.0;
    }
    else if (pFScl > 1.0) {
      pFScl = 1.0;
    }
    return Tools.roundColor((double) pValue1 + (double) (pValue2 - pValue1) * pFScl);
  }
}
