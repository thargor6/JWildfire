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
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.Flam3Writer;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RandomRGBPaletteGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

public class SubFlameRandomFlameGenerator extends RandomFlameGenerator {

  public Flame embedFlame(Flame pSubFlame) {
    Flame flame = new Flame();
    flame.assign(pSubFlame);

    flame.getFinalXForms().clear();
    flame.getXForms().clear();
    flame.setPalette(pSubFlame.getPalette().makeCopy());
    // 1st xForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(0.5);
      {
        {
          SubFlameWFFunc var = new SubFlameWFFunc();
          String flameXML = new Flam3Writer().getFlameXML(pSubFlame);
          var.setRessource("flame", flameXML.getBytes());
          xForm.addVariation(1, var);
        }
      }
      xForm.setColor(0);
      xForm.setColorSymmetry(-0.22);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setCoeff00(0.17254603006834707);
      xForm.setCoeff01(0.6439505508593787);
      xForm.setCoeff10(-0.6439505508593787);
      xForm.setCoeff11(0.17254603006834707);
      xForm.setCoeff20(2 + Math.random() * 2.0);
      xForm.setCoeff21(-0.25 - Math.random() * 0.25);
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(-0.62);
    }
    // 3rd xForm
    {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      xForm.setWeight(0.5);
      xForm.setCoeff00(0.17254603006834707);
      xForm.setCoeff01(0.6439505508593787);
      xForm.setCoeff10(-0.6439505508593787);
      xForm.setCoeff11(0.17254603006834707);
      xForm.setCoeff20(-3.0);
      xForm.setCoeff21(0.3);
      {
        VariationFunc varFunc;
        varFunc = VariationFuncList.getVariationFuncInstance("curl3D", true);
        varFunc.setParameter("cx", -0.2);
        varFunc.setParameter("cy", 0);
        varFunc.setParameter("cz", 0);
        xForm.addVariation(1, varFunc);
      }
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(0);
    }
    return flame;
  }

  @Override
  protected Flame createFlame() {
    Prefs prefs = new Prefs();
    try {
      prefs.loadFromFile();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    Flame subFlame;
    while (true) {
      subFlame = new AllRandomFlameGenerator().createFlame();
      final int IMG_WIDTH = 160;
      final int IMG_HEIGHT = 100;
      final double MIN_COVERAGE = 0.25;

      subFlame.setWidth(IMG_WIDTH);
      subFlame.setHeight(IMG_HEIGHT);
      //          subFlame.setPixelsPerUnit(10);
      // render it   
      subFlame.setSampleDensity(50);
      subFlame.setSpatialFilterRadius(0.0);
      RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(11);
      subFlame.setPalette(palette);
      RenderedFlame res;
      boolean deEnabled = subFlame.isDeFilterEnabled();
      try {
        subFlame.setDeFilterEnabled(false);
        FlameRenderer renderer = new FlameRenderer(subFlame, prefs, false, true);
        RenderInfo info = new RenderInfo(IMG_WIDTH, IMG_HEIGHT);
        res = renderer.renderFlame(info);
      }
      finally {
        subFlame.setDeFilterEnabled(deEnabled);
      }
      SimpleImage img = res.getImage();
      long maxCoverage = img.getImageWidth() * img.getImageHeight();
      long coverage = 0;
      Pixel pixel = new Pixel();
      for (int k = 0; k < img.getImageHeight(); k++) {
        for (int l = 0; l < img.getImageWidth(); l++) {
          pixel.setARGBValue(img.getARGBValue(l, k));
          if (pixel.r > 20 || pixel.g > 20 || pixel.b > 20) {
            coverage++;
          }
        }
      }
      double fCoverage = (double) coverage / (double) maxCoverage;
      if (fCoverage >= MIN_COVERAGE) {
        break;
      }
    }
    Flame flame = embedFlame(subFlame);
    flame.setCentreX(2);
    flame.setCentreY(1);
    flame.setCamPitch(0);
    flame.setCamRoll(-2);
    flame.setCamYaw(0);
    flame.setPixelsPerUnit(200);
    return flame;

  }

  @Override
  public String getName() {
    return "SubFlame";
  }

}
