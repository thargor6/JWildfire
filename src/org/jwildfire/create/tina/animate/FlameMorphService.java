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
import java.util.HashMap;
import java.util.List;

import org.jwildfire.base.MathLib;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.io.Flam3Writer;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.variation.PostMirrorWFFunc;
import org.jwildfire.create.tina.variation.Spherical3DWFFunc;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class FlameMorphService {

  public static Flame morphFlames(Flame pFlame1, Flame pFlame2, int pFrame, int pFrames) {
    if (pFrame < 1 || pFrames < 2)
      return pFlame1;
    double fScl = (double) (pFrame - 1) / (pFrames - 1);
    if (fScl <= MathLib.EPSILON) {
      return pFlame1;
    }
    else if (fScl >= 1.0 - MathLib.EPSILON) {
      return pFlame2;
    }
    //    System.out.println("MMMMMMMMM: " + fScl);
    Flame res = pFlame1.makeCopy();
    res.getXForms().clear();
    // morph XForms  
    int size1 = pFlame1.getXForms().size();
    int size2 = pFlame2.getXForms().size();
    int maxSize = size1 > size2 ? size1 : size2;
    for (int i = 0; i < maxSize; i++) {
      XForm xForm1 = i < size1 ? pFlame1.getXForms().get(i) : null;
      if (xForm1 == null) {
        xForm1 = new XForm();
        xForm1.addVariation(0.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
        xForm1.setWeight(0.0);
      }

      XForm xForm2 = i < size2 ? pFlame2.getXForms().get(i) : null;
      if (xForm2 == null) {
        xForm2 = new XForm();
        xForm2.addVariation(0.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
        xForm2.setWeight(0.0);
      }

      XForm morphedXForm = morphXForms(xForm1, xForm2, fScl, pFrame, pFrames);
      res.getXForms().add(morphedXForm);
    }
    // morph final XForms
    if (pFlame1.getFinalXForm() != null || pFlame2.getFinalXForm() != null) {
      XForm xForm1 = pFlame1.getFinalXForm();
      if (xForm1 == null) {
        xForm1 = new XForm();
        xForm1.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      }

      XForm xForm2 = pFlame2.getFinalXForm();
      if (xForm2 == null) {
        xForm2 = new XForm();
        xForm2.addVariation(1.0, VariationFuncList.getVariationFuncInstance("linear3D", true));
      }

      XForm morphedXForm = morphXForms(xForm1, xForm2, fScl, pFrame, pFrames);
      res.setFinalXForm(morphedXForm);
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
    res.setWidth(morphValue(pFlame1.getWidth(), pFlame2.getWidth(), fScl));
    res.setHeight(morphValue(pFlame1.getHeight(), pFlame2.getHeight(), fScl));
    res.setPreserveZ(morphValue(pFlame1.isPreserveZ(), pFlame2.isPreserveZ(), fScl));
    res.setSpatialFilterRadius(morphValue(pFlame1.getSpatialFilterRadius(), pFlame2.getSpatialFilterRadius(), fScl));
    res.setVibrancy(morphValue(pFlame1.getVibrancy(), pFlame2.getVibrancy(), fScl));
    res.setWhiteLevel(morphValue(pFlame1.getWhiteLevel(), pFlame2.getWhiteLevel(), fScl));

    return res;
  }

  private static XForm morphXForms(XForm pXForm1, XForm pXForm2, double pFScl, int pFrame, int pFrames) {
    pXForm1 = pXForm1.makeCopy();
    pXForm2 = pXForm2.makeCopy();
    prepareMorphXForm(pXForm1);
    prepareMorphXForm(pXForm2);
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
    res.setPostCoeff00(morphValue(pXForm1.getPostCoeff00(), pXForm2.getPostCoeff00(), pFScl));
    res.setPostCoeff01(morphValue(pXForm1.getPostCoeff01(), pXForm2.getPostCoeff01(), pFScl));
    res.setPostCoeff10(morphValue(pXForm1.getPostCoeff10(), pXForm2.getPostCoeff10(), pFScl));
    res.setPostCoeff11(morphValue(pXForm1.getPostCoeff11(), pXForm2.getPostCoeff11(), pFScl));
    res.setPostCoeff20(morphValue(pXForm1.getPostCoeff20(), pXForm2.getPostCoeff20(), pFScl));
    res.setPostCoeff21(morphValue(pXForm1.getPostCoeff21(), pXForm2.getPostCoeff21(), pFScl));
    res.setOpacity(morphValue(pXForm1.getOpacity(), pXForm2.getOpacity(), pFScl));
    res.setDrawMode(pFScl >= 0.5 ? pXForm2.getDrawMode() : pXForm1.getDrawMode());
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
              Flame flame1 = new Flam3Reader().readFlamesfromXML(flame1XML).get(0);
              Flame flame2 = new Flam3Reader().readFlamesfromXML(flame2XML).get(0);
              Flame morphedFlame = morphFlames(flame1, flame2, pFrame, pFrames);
              String morphedFlameXML = new Flam3Writer().getFlameXML(morphedFlame);
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
