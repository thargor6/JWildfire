/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
import java.util.HashMap;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.variation.PostMirrorWFFunc;
import org.jwildfire.create.tina.variation.Spherical3DWFFunc;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class FlameMorphService {
  public static Flame morphFlames(Prefs pPrefs, FlameMorphType pFlameMorphType, Flame pFlame1, Flame pFlame2, int pFrame, int pFrames, boolean pCompat) {
    switch (pFlameMorphType) {
      case FADE:
        return morphFlames_fade(pPrefs, pFlame1, pFlame2, pFrame, pFrames, pCompat);
      case MORPH:
        return morphFlames_morph(pPrefs, pFlame1, pFlame2, pFrame, pFrames, pCompat);
      default:
        throw new IllegalArgumentException(pFlameMorphType.toString());
    }
  }

  private static Flame morphFlames_fade(Prefs pPrefs, Flame pFlame1, Flame pFlame2, int pFrame, int pFrames, boolean pCompat) {
    if (pFrame < 1 || pFrames < 2)
      return pFlame1;
    double fScl = (double) (pFrame - 1) / (pFrames - 1);
    if (fScl <= MathLib.EPSILON) {
      return pFlame1;
    }
    else if (fScl >= 1.0 - MathLib.EPSILON) {
      return pFlame2;
    }
    if (pCompat) {
      // fade out layers of the source flame (using layer weight for compatibility)
      Flame res = pFlame1.makeCopy();
      morphFlameValues(pFlame1, pFlame2, fScl, res, pCompat);
      for (Layer layer : res.getLayers()) {
        layer.setWeight(layer.getWeight() * (1.0 - fScl));
      }
      // add and fade in layers of the dest flame (using layer weight for compatibility)
      for (Layer layer : pFlame2.getLayers()) {
        Layer copy = layer.makeCopy();
        copy.setWeight(copy.getWeight() * fScl);
        res.getLayers().add(copy);
      }
      return res;
    }
    else {
      // fade out layers of the source flame (using layer density)
      Flame res = pFlame1.makeCopy();
      morphFlameValues(pFlame1, pFlame2, fScl, res, pCompat);
      for (Layer layer : res.getLayers()) {
        layer.setDensity(layer.getDensity() * (1.0 - fScl));
      }
      // add and fade in layers of the dest flame (using layer density)
      for (Layer layer : pFlame2.getLayers()) {
        Layer copy = layer.makeCopy();
        copy.setDensity(copy.getDensity() * fScl);
        res.getLayers().add(copy);
      }
      return res;      
    }
  }

  private static Flame morphFlames_morph(Prefs pPrefs, Flame pFlame1, Flame pFlame2, int pFrame, int pFrames, boolean pCompat) {
    if (pFrame < 1 || pFrames < 2)
      return pFlame1;
    double fScl = (double) (pFrame - 1) / (pFrames - 1);
    if (fScl <= MathLib.EPSILON) {
      return pFlame1;
    }
    else if (fScl >= 1.0 - MathLib.EPSILON) {
      return pFlame2;
    }
    Flame res = pFlame1.makeCopy();
    res.getLayers().clear();
    int layerSize1 = pFlame1.getLayers().size();
    int layerSize2 = pFlame2.getLayers().size();
    int maxLayerSize = layerSize1 > layerSize2 ? layerSize1 : layerSize2;
    for (int lIdx = 0; lIdx < maxLayerSize; lIdx++) {
      Layer layer = new Layer();
      res.getLayers().add(layer);
      // Morph layers
      if (lIdx < layerSize1 && lIdx < layerSize2) {
        Layer layer1 = pFlame1.getLayers().get(lIdx);
        Layer layer2 = pFlame2.getLayers().get(lIdx);
        layer.assign(layer1);
        layer.getXForms().clear();
        layer.getFinalXForms().clear();
        layer.setWeight(morphValue(layer1.getWeight(), layer2.getWeight(), fScl));
        layer.setDensity(morphValue(layer1.getDensity(), layer2.getDensity(), fScl));
        // morph XForms
        {
          int size1 = layer1.getXForms().size();
          int size2 = layer2.getXForms().size();
          int maxSize = size1 > size2 ? size1 : size2;
          for (int i = 0; i < maxSize; i++) {
            XForm xForm1 = i < size1 ? layer1.getXForms().get(i) : null;
            if (xForm1 == null) {
              xForm1 = new XForm();
              xForm1.addVariation(0.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
              xForm1.setWeight(0.0);
            }

            XForm xForm2 = i < size2 ? layer2.getXForms().get(i) : null;
            if (xForm2 == null) {
              xForm2 = new XForm();
              xForm2.addVariation(0.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
              xForm2.setWeight(0.0);
            }

            XForm morphedXForm = morphXForms(pPrefs, xForm1, xForm2, fScl, pFrame, pFrames, pCompat);
            layer.getXForms().add(morphedXForm);
          }
        }
        // morph final XForms
        {
          int size1 = layer1.getFinalXForms().size();
          int size2 = layer2.getFinalXForms().size();
          int maxSize = size1 > size2 ? size1 : size2;
          for (int i = 0; i < maxSize; i++) {
            XForm xForm1 = i < size1 ? layer1.getFinalXForms().get(i) : null;
            if (xForm1 == null) {
              xForm1 = new XForm();
              xForm1.addVariation(0.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
              xForm1.setWeight(0.0);
            }

            XForm xForm2 = i < size2 ? layer2.getFinalXForms().get(i) : null;
            if (xForm2 == null) {
              xForm2 = new XForm();
              xForm2.addVariation(0.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
              xForm2.setWeight(0.0);
            }

            XForm morphedXForm = morphXForms(pPrefs, xForm1, xForm2, fScl, pFrame, pFrames, pCompat);
            layer.getFinalXForms().add(morphedXForm);
          }
        }
        // morph colors
        RGBPalette palette1 = layer1.getPalette();
        RGBPalette palette2 = layer2.getPalette();
        for (int i = 0; i < RGBPalette.PALETTE_SIZE; i++) {
          RGBColor color1 = palette1.getColor(i);
          RGBColor color2 = palette2.getColor(i);
          int red = Tools.roundColor(color1.getRed() + (color2.getRed() - color1.getRed()) * fScl);
          int green = Tools.roundColor(color1.getGreen() + (color2.getGreen() - color1.getGreen()) * fScl);
          int blue = Tools.roundColor(color1.getBlue() + (color2.getBlue() - color1.getBlue()) * fScl);
          layer.getPalette().setColor(i, red, green, blue);
        }
      }
      // fade out layer1
      else if (lIdx < layerSize1) {
        Layer layer1 = pFlame1.getLayers().get(lIdx);
        layer.assign(layer1);
        if (pCompat) {
          layer.setWeight(morphValue(layer1.getWeight(), 0.0, fScl));
        }
        else {
          layer.setDensity(morphValue(layer1.getDensity(), 0.0, fScl));
        }
      }
      // fade in layer2
      else if (lIdx < layerSize2) {
        Layer layer2 = pFlame2.getLayers().get(lIdx);
        layer.assign(layer2);
        if (pCompat) {
          layer.setWeight(morphValue(0.0, layer2.getWeight(), fScl));
        }
        else {
          layer.setDensity(morphValue(0.0, layer2.getDensity(), fScl));
        }
      }
    }
    // morph camera settings etc.
    morphFlameValues(pFlame1, pFlame2, fScl, res, pCompat);
    return res;
  }

  private static void morphFlameValues(Flame pFlame1, Flame pFlame2, double fScl, Flame res, boolean pCompat) {
    res.setCamDOF(morphValue(pFlame1.getCamDOF(), pFlame2.getCamDOF(), fScl));
    res.setCamZ(morphValue(pFlame1.getCamZ(), pFlame2.getCamZ(), fScl));
    res.setCamPerspective(morphValue(pFlame1.getCamPerspective(), pFlame2.getCamPerspective(), fScl));
    res.setCamPitch(morphValue(pFlame1.getCamPitch(), pFlame2.getCamPitch(), fScl));
    res.setCamYaw(morphValue(pFlame1.getCamYaw(), pFlame2.getCamYaw(), fScl));
    res.setCamBank(morphValue(pFlame1.getCamBank(), pFlame2.getCamBank(), fScl));
    res.setCamRoll(morphValue(pFlame1.getCamRoll(), pFlame2.getCamRoll(), fScl));
    res.setFocusX(morphValue(pFlame1.getFocusX(), pFlame2.getFocusX(), fScl));
    res.setFocusY(morphValue(pFlame1.getFocusY(), pFlame2.getFocusY(), fScl));
    res.setFocusZ(morphValue(pFlame1.getFocusZ(), pFlame2.getFocusZ(), fScl));
    res.setCamZoom(morphValue(pFlame1.getCamZoom(), pFlame2.getCamZoom(), fScl));
    res.setCamPosX(morphValue(pFlame1.getCamPosX(), pFlame2.getCamPosX(), fScl));
    res.setCamPosY(morphValue(pFlame1.getCamPosY(), pFlame2.getCamPosY(), fScl));
    res.setCamPosZ(morphValue(pFlame1.getCamPosZ(), pFlame2.getCamPosZ(), fScl));
    res.setCamDOFArea(morphValue(pFlame1.getCamDOFArea(), pFlame2.getCamDOFArea(), fScl));
    res.setCamDOFExponent(morphValue(pFlame1.getCamDOFExponent(), pFlame2.getCamDOFExponent(), fScl));
    res.setDimishZ(morphValue(pFlame1.getDimishZ(), pFlame2.getDimishZ(), fScl));
    res.setDimishZRed(morphColorValue(pFlame1.getDimishZRed(), pFlame2.getDimishZRed(), fScl));
    res.setDimishZGreen(morphColorValue(pFlame1.getDimishZGreen(), pFlame2.getDimishZGreen(), fScl));
    res.setDimishZBlue(morphColorValue(pFlame1.getDimishZBlue(), pFlame2.getDimishZBlue(), fScl));
    res.setDimZDistance(morphValue(pFlame1.getDimZDistance(), pFlame2.getDimZDistance(), fScl));
    res.setBgColorRed(morphColorValue(pFlame1.getBgColorRed(), pFlame2.getBgColorRed(), fScl));
    res.setBgColorGreen(morphColorValue(pFlame1.getBgColorGreen(), pFlame2.getBgColorGreen(), fScl));
    res.setBgColorBlue(morphColorValue(pFlame1.getBgColorBlue(), pFlame2.getBgColorBlue(), fScl));
    res.setBgColorType(pFlame2.getBgColorType());
    res.setBgColorULRed(morphColorValue(pFlame1.getBgColorULRed(), pFlame2.getBgColorULRed(), fScl));
    res.setBgColorULGreen(morphColorValue(pFlame1.getBgColorULGreen(), pFlame2.getBgColorULGreen(), fScl));
    res.setBgColorULBlue(morphColorValue(pFlame1.getBgColorULBlue(), pFlame2.getBgColorULBlue(), fScl));
    res.setBgColorURRed(morphColorValue(pFlame1.getBgColorURRed(), pFlame2.getBgColorURRed(), fScl));
    res.setBgColorURGreen(morphColorValue(pFlame1.getBgColorURGreen(), pFlame2.getBgColorURGreen(), fScl));
    res.setBgColorURBlue(morphColorValue(pFlame1.getBgColorURBlue(), pFlame2.getBgColorURBlue(), fScl));
    res.setBgColorLLRed(morphColorValue(pFlame1.getBgColorLLRed(), pFlame2.getBgColorLLRed(), fScl));
    res.setBgColorLLGreen(morphColorValue(pFlame1.getBgColorLLGreen(), pFlame2.getBgColorLLGreen(), fScl));
    res.setBgColorLLBlue(morphColorValue(pFlame1.getBgColorLLBlue(), pFlame2.getBgColorLLBlue(), fScl));
    res.setBgColorLRRed(morphColorValue(pFlame1.getBgColorLRRed(), pFlame2.getBgColorLRRed(), fScl));
    res.setBgColorLRGreen(morphColorValue(pFlame1.getBgColorLRGreen(), pFlame2.getBgColorLRGreen(), fScl));
    res.setBgColorLRBlue(morphColorValue(pFlame1.getBgColorLRBlue(), pFlame2.getBgColorLRBlue(), fScl));
    res.setBgColorCCRed(morphColorValue(pFlame1.getBgColorCCRed(), pFlame2.getBgColorCCRed(), fScl));
    res.setBgColorCCGreen(morphColorValue(pFlame1.getBgColorCCGreen(), pFlame2.getBgColorCCGreen(), fScl));
    res.setBgColorCCBlue(morphColorValue(pFlame1.getBgColorCCBlue(), pFlame2.getBgColorCCBlue(), fScl));
    res.setForegroundOpacity(morphValue(pFlame1.getForegroundOpacity(), pFlame2.getForegroundOpacity(), fScl));
    res.setBrightness(morphValue(pFlame1.getBrightness(), pFlame2.getBrightness(), fScl));
    res.setCentreX(morphValue(pFlame1.getCentreX(), pFlame2.getCentreX(), fScl));
    res.setCentreY(morphValue(pFlame1.getCentreY(), pFlame2.getCentreY(), fScl));
    res.setContrast(morphValue(pFlame1.getContrast(), pFlame2.getContrast(), fScl));
    res.setGamma(morphValue(pFlame1.getGamma(), pFlame2.getGamma(), fScl));
    res.setGammaThreshold(morphValue(pFlame1.getGammaThreshold(), pFlame2.getGammaThreshold(), fScl));
    res.setPixelsPerUnit(morphValue(pFlame1.getPixelsPerUnit(), pFlame2.getPixelsPerUnit(), fScl));
    res.setWidth(morphValue(pFlame1.getWidth(), pFlame2.getWidth(), fScl));
    res.setHeight(morphValue(pFlame1.getHeight(), pFlame2.getHeight(), fScl));
    res.setPreserveZ(morphValue(pFlame1.isPreserveZ(), pFlame2.isPreserveZ(), fScl));
    res.setSpatialFilterRadius(morphValue(pFlame1.getSpatialFilterRadius(), pFlame2.getSpatialFilterRadius(), fScl));
    res.setSpatialFilterSharpness(morphValue(pFlame1.getSpatialFilterSharpness(), pFlame2.getSpatialFilterSharpness(), fScl));
    res.setSpatialFilterLowDensity(morphValue(pFlame1.getSpatialFilterLowDensity(), pFlame2.getSpatialFilterLowDensity(), fScl));
    res.setAntialiasAmount(morphValue(pFlame1.getAntialiasAmount(), pFlame2.getAntialiasAmount(), fScl));
    res.setAntialiasRadius(morphValue(pFlame1.getAntialiasRadius(), pFlame2.getAntialiasRadius(), fScl));
    res.setVibrancy(morphValue(pFlame1.getVibrancy(), pFlame2.getVibrancy(), fScl));
    res.setSaturation(morphValue(pFlame1.getSaturation(), pFlame2.getSaturation(), fScl));
    res.setLowDensityBrightness(morphValue(pFlame1.getLowDensityBrightness(), pFlame2.getLowDensityBrightness(), fScl));
    res.setBalanceRed(morphValue(pFlame1.getBalanceRed(), pFlame2.getBalanceRed(), fScl));
    res.setBalanceGreen(morphValue(pFlame1.getBalanceGreen(), pFlame2.getBalanceGreen(), fScl));
    res.setBalanceBlue(morphValue(pFlame1.getBalanceBlue(), pFlame2.getBalanceBlue(), fScl));
    res.setWhiteLevel(morphValue(pFlame1.getWhiteLevel(), pFlame2.getWhiteLevel(), fScl));
  }

  private static XForm morphXForms(Prefs pPrefs, XForm pXForm1, XForm pXForm2, double pFScl, int pFrame, int pFrames, boolean pCompat) {
    pXForm1 = pXForm1.makeCopy();
    pXForm2 = pXForm2.makeCopy();
    prepareMorphXForm(pXForm1);
    prepareMorphXForm(pXForm2);
    XForm res = new XForm();
    res.setWeight(morphValue(pXForm1.getWeight(), pXForm2.getWeight(), pFScl));
    res.setColor(morphValue(pXForm1.getColor(), pXForm2.getColor(), pFScl));
    res.setColorSymmetry(morphValue(pXForm1.getColorSymmetry(), pXForm2.getColorSymmetry(), pFScl));
    res.setMaterial(morphValue(pXForm1.getMaterial(), pXForm2.getMaterial(), pFScl));
    res.setMaterialSpeed(morphValue(pXForm1.getMaterialSpeed(), pXForm2.getMaterialSpeed(), pFScl));

    res.setXYCoeff00(morphValue(pXForm1.getXYCoeff00(), pXForm2.getXYCoeff00(), pFScl));
    res.setXYCoeff01(morphValue(pXForm1.getXYCoeff01(), pXForm2.getXYCoeff01(), pFScl));
    res.setXYCoeff10(morphValue(pXForm1.getXYCoeff10(), pXForm2.getXYCoeff10(), pFScl));
    res.setXYCoeff11(morphValue(pXForm1.getXYCoeff11(), pXForm2.getXYCoeff11(), pFScl));
    res.setXYCoeff20(morphValue(pXForm1.getXYCoeff20(), pXForm2.getXYCoeff20(), pFScl));
    res.setXYCoeff21(morphValue(pXForm1.getXYCoeff21(), pXForm2.getXYCoeff21(), pFScl));

    res.setYZCoeff00(morphValue(pXForm1.getYZCoeff00(), pXForm2.getYZCoeff00(), pFScl));
    res.setYZCoeff01(morphValue(pXForm1.getYZCoeff01(), pXForm2.getYZCoeff01(), pFScl));
    res.setYZCoeff10(morphValue(pXForm1.getYZCoeff10(), pXForm2.getYZCoeff10(), pFScl));
    res.setYZCoeff11(morphValue(pXForm1.getYZCoeff11(), pXForm2.getYZCoeff11(), pFScl));
    res.setYZCoeff20(morphValue(pXForm1.getYZCoeff20(), pXForm2.getYZCoeff20(), pFScl));
    res.setYZCoeff21(morphValue(pXForm1.getYZCoeff21(), pXForm2.getYZCoeff21(), pFScl));

    res.setZXCoeff00(morphValue(pXForm1.getZXCoeff00(), pXForm2.getZXCoeff00(), pFScl));
    res.setZXCoeff01(morphValue(pXForm1.getZXCoeff01(), pXForm2.getZXCoeff01(), pFScl));
    res.setZXCoeff10(morphValue(pXForm1.getZXCoeff10(), pXForm2.getZXCoeff10(), pFScl));
    res.setZXCoeff11(morphValue(pXForm1.getZXCoeff11(), pXForm2.getZXCoeff11(), pFScl));
    res.setZXCoeff20(morphValue(pXForm1.getZXCoeff20(), pXForm2.getZXCoeff20(), pFScl));
    res.setZXCoeff21(morphValue(pXForm1.getZXCoeff21(), pXForm2.getZXCoeff21(), pFScl));

    res.setXYPostCoeff00(morphValue(pXForm1.getXYPostCoeff00(), pXForm2.getXYPostCoeff00(), pFScl));
    res.setXYPostCoeff01(morphValue(pXForm1.getXYPostCoeff01(), pXForm2.getXYPostCoeff01(), pFScl));
    res.setXYPostCoeff10(morphValue(pXForm1.getXYPostCoeff10(), pXForm2.getXYPostCoeff10(), pFScl));
    res.setXYPostCoeff11(morphValue(pXForm1.getXYPostCoeff11(), pXForm2.getXYPostCoeff11(), pFScl));
    res.setXYPostCoeff20(morphValue(pXForm1.getXYPostCoeff20(), pXForm2.getXYPostCoeff20(), pFScl));
    res.setXYPostCoeff21(morphValue(pXForm1.getXYPostCoeff21(), pXForm2.getXYPostCoeff21(), pFScl));

    res.setYZPostCoeff00(morphValue(pXForm1.getYZPostCoeff00(), pXForm2.getYZPostCoeff00(), pFScl));
    res.setYZPostCoeff01(morphValue(pXForm1.getYZPostCoeff01(), pXForm2.getYZPostCoeff01(), pFScl));
    res.setYZPostCoeff10(morphValue(pXForm1.getYZPostCoeff10(), pXForm2.getYZPostCoeff10(), pFScl));
    res.setYZPostCoeff11(morphValue(pXForm1.getYZPostCoeff11(), pXForm2.getYZPostCoeff11(), pFScl));
    res.setYZPostCoeff20(morphValue(pXForm1.getYZPostCoeff20(), pXForm2.getYZPostCoeff20(), pFScl));
    res.setYZPostCoeff21(morphValue(pXForm1.getYZPostCoeff21(), pXForm2.getYZPostCoeff21(), pFScl));

    res.setZXPostCoeff00(morphValue(pXForm1.getZXPostCoeff00(), pXForm2.getZXPostCoeff00(), pFScl));
    res.setZXPostCoeff01(morphValue(pXForm1.getZXPostCoeff01(), pXForm2.getZXPostCoeff01(), pFScl));
    res.setZXPostCoeff10(morphValue(pXForm1.getZXPostCoeff10(), pXForm2.getZXPostCoeff10(), pFScl));
    res.setZXPostCoeff11(morphValue(pXForm1.getZXPostCoeff11(), pXForm2.getZXPostCoeff11(), pFScl));
    res.setZXPostCoeff20(morphValue(pXForm1.getZXPostCoeff20(), pXForm2.getZXPostCoeff20(), pFScl));
    res.setZXPostCoeff21(morphValue(pXForm1.getZXPostCoeff21(), pXForm2.getZXPostCoeff21(), pFScl));

    res.setOpacity(morphValue(pXForm1.getOpacity(), pXForm2.getOpacity(), pFScl));
    res.setDrawMode(pFScl >= 0.5 ? pXForm2.getDrawMode() : pXForm1.getDrawMode());
    res.setColorType(pFScl >= 0.5 ? pXForm2.getColorType() : pXForm1.getColorType());
    {
      RGBColor tc1 = pXForm1.getTargetColor();
      RGBColor tc2 = pXForm2.getTargetColor();
      res.setTargetColor(morphColorValue(tc1.getRed(), tc2.getRed(), pFScl), morphColorValue(tc1.getGreen(), tc2.getGreen(), pFScl), morphColorValue(tc1.getBlue(), tc2.getBlue(), pFScl));
    }
    for (int i = 0; i < pXForm1.getModifiedWeights().length; i++) {
      res.getModifiedWeights()[i] = morphValue(pXForm1.getModifiedWeights()[i], pXForm2.getModifiedWeights()[i], pFScl);
    }
    res.clearVariations();
    List<Variation> vars1 = new ArrayList<Variation>();
    List<Variation> vars2 = new ArrayList<Variation>();

    HashMap<String, String> processed = new HashMap<String, String>();
    for (int i = 0; i < pXForm1.getVariationCount(); i++) {
      Variation var1 = pXForm1.getVariation(i);
      String fncName = var1.getFunc().getName();
      processed.put(fncName, fncName);
      vars1.add(var1);
      // search the same func in xForm2
      Variation var2 = null;
      for (int j = 0; j < pXForm2.getVariationCount(); j++) {
        var2 = pXForm2.getVariation(j);
        if (var2.getFunc().getName().equals(fncName)) {
          break;
        }
        else {
          var2 = null;
        }
      }
      if (var2 != null) {
        vars2.add(var2);
      }
      else {
        vars2.add(new Variation(0.0, VariationFuncList.getVariationFuncInstance(var1.getFunc().getName(), true)));
      }
    }
    for (int i = 0; i < pXForm2.getVariationCount(); i++) {
      Variation var2 = pXForm2.getVariation(i);
      String fncName = var2.getFunc().getName();
      if (processed.get(fncName) == null) {
        vars2.add(var2);
        vars1.add(new Variation(0.0, VariationFuncList.getVariationFuncInstance(var2.getFunc().getName(), true)));
      }
    }
    if (vars1.size() != vars2.size()) {
      throw new IllegalStateException();
    }
    for (int i = 0; i < vars1.size(); i++) {
      Variation var1 = vars1.get(i);
      Variation var2 = vars2.get(i);
      if (!var1.getFunc().getName().equals(var2.getFunc().getName())) {
        throw new IllegalStateException();
      }
      //      System.out.println(i + ": " + var1.getFunc().getName() + " " + var1.getAmount() + " " + var2.getAmount());
      double amount = morphValue(var1.getAmount(), var2.getAmount(), pFScl);
      Variation var = res.addVariation(amount, var1.getFunc());
      //  params
      if (var.getFunc().getParameterNames() != null && var.getFunc().getParameterNames().length > 0) {
        Object val[] = var.getFunc().getParameterValues();
        Object val1[] = var1.getFunc().getParameterValues();
        Object val2[] = var2.getFunc().getParameterValues();
        for (int j = 0; j < var.getFunc().getParameterNames().length; j++) {
          String name = var.getFunc().getParameterNames()[j];
          if (val[j] instanceof Integer) {
            int mVal = morphValue((Integer) val1[j], (Integer) val2[j], pFScl);
            if (mVal == 0 && name.equals("power")) {
              mVal = 1;
            }
            //    int mVal = pFScl >= 0.5 ? (Integer) val2[j] : (Integer) val1[j];
            //          System.out.println("  " + name + " " + mVal + " (" + val1[j] + " " + val2[j] + ")");
            var.getFunc().setParameter(name, mVal);
          }
          else if (val[j] instanceof Double) {
            double mVal = morphValue((Double) val1[j], (Double) val2[j], pFScl);
            //          System.out.println("  " + name + " " + mVal + " (" + val1[j] + " " + val2[j] + ")");
            var.getFunc().setParameter(name, mVal);
          }
          else {
            throw new IllegalStateException();
          }
        }
      }
      // ressources
      if (var.getFunc().getRessourceNames() != null && var.getFunc().getRessourceNames().length > 0) {
        Object ress1[] = var1.getFunc().getRessourceValues();
        Object ress2[] = var2.getFunc().getRessourceValues();
        for (int j = 0; j < var.getFunc().getRessourceNames().length; j++) {
          String name = var.getFunc().getRessourceNames()[j];
          if (name.equalsIgnoreCase(SubFlameWFFunc.RESSOURCE_FLAME) && var.getFunc().getName().indexOf("subflame_wf") >= 0) {
            String flame1XML = new String((byte[]) ress1[j]);
            String flame2XML = new String((byte[]) ress2[j]);
            try {
              Flame flame1 = new FlameReader(pPrefs).readFlamesfromXML(flame1XML).get(0);
              Flame flame2 = new FlameReader(pPrefs).readFlamesfromXML(flame2XML).get(0);
              Flame morphedFlame = morphFlames(pPrefs, FlameMorphType.MORPH, flame1, flame2, pFrame, pFrames, pCompat);
              String morphedFlameXML = new FlameWriter().getFlameXML(morphedFlame);
              var.getFunc().setRessource(SubFlameWFFunc.RESSOURCE_FLAME, morphedFlameXML.getBytes());
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }

          }

        }
      }
    }
    return res;
  }

  private static void prepareMorphXForm(XForm pXForm) {
    int i = 0;
    while (i < pXForm.getVariationCount()) {
      Variation var = pXForm.getVariation(i);
      if (var.getFunc() instanceof Spherical3DWFFunc) {
        Object invert = var.getFunc().getParameter(Spherical3DWFFunc.PARAM_INVERT);
        if (invert != null && ((Integer) invert) == 1) {
          var.getFunc().setParameter(Spherical3DWFFunc.PARAM_INVERT, 0);
          var.setAmount(-var.getAmount());
        }
      }
      else if (var.getFunc() instanceof PostMirrorWFFunc) {
        pXForm.removeVariation(var);
        i--;
      }
      i++;
    }
  }

  public static double morphValue(double pValue1, double pValue2, double pFScl) {
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
