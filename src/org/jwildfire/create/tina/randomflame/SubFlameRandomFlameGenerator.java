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
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.Linear3DFunc;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

public class SubFlameRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  protected Flame createFlame() {
    Prefs prefs = new Prefs();
    try {
      prefs.loadFromFile();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setSpatialFilterRadius(1.0);
    flame.setFinalXForm(null);
    flame.getXForms().clear();

    int maxXForms = (int) (2.0 + Math.random() * 5.0);
    double scl = 1.0;
    for (int i = 0; i < maxXForms; i++) {
      XForm xForm = new XForm();
      flame.getXForms().add(xForm);
      if (Math.random() < 0.5) {
        XFormTransformService.rotate(xForm, 90.0 * Math.random());
      }
      else {
        XFormTransformService.rotate(xForm, -90.0 * Math.random());
      }
      XFormTransformService.localTranslate(xForm, 2.0 * Math.random() - 1.0, 2.0 * Math.random() - 1.0);
      scl *= 0.75 + Math.random() / 4;
      XFormTransformService.scale(xForm, scl, true, true);

      xForm.setColor(Math.random());
      if (Math.random() < 0.7) {
        SubFlameWFFunc var = new SubFlameWFFunc();
        Flame subFlame;
        while (true) {
          double r = Math.random();
          if (r < 0.20) {
            subFlame = new LinearRandomFlameGenerator().createFlame();
          }
          else if (r < 0.40) {
            subFlame = new GnarlRandomFlameGenerator().createFlame();
          }
          else if (r < 0.60) {
            subFlame = new ExperimentalGnarlRandomFlameGenerator().createFlame();
          }
          else if (r < 0.80) {
            subFlame = new BubblesRandomFlameGenerator().createFlame();
          }
          else {
            subFlame = new ExperimentalSimpleRandomFlameGenerator().createFlame();
          }

          final int IMG_WIDTH = 160;
          final int IMG_HEIGHT = 100;
          final double MIN_COVERAGE = 0.25;

          subFlame.setWidth(IMG_WIDTH);
          subFlame.setHeight(IMG_HEIGHT);
          //          subFlame.setPixelsPerUnit(10);
          // render it   
          subFlame.setSampleDensity(50);
          RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(11);
          subFlame.setPalette(palette);
          FlameRenderer renderer = new FlameRenderer(subFlame, prefs);
          SimpleImage img = new SimpleImage(IMG_WIDTH, IMG_HEIGHT);
          renderer.renderFlame(img, null);
          renderer.renderFlame(img, null);
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

        String flameXML = new Flam3Writer().getFlameXML(subFlame);
        var.setRessource("flame", flameXML.getBytes());
        xForm.addVariation(Math.random() * 0.8 + 0.2, var);
      }
      else {
        xForm.addVariation(Math.random() * 0.8 + 0.2, new Linear3DFunc());
      }

      xForm.setWeight(0.5);
    }
    return flame;
  }

  @Override
  public String getName() {
    return "SubFlame";
  }

}
