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

import static org.jwildfire.base.mathlib.MathLib.fabs;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RandomRGBPaletteGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.PixelizeTransformer;

public class RandomFlameGeneratorSampler {
  private final int imageWidth;
  private final int imageHeight;
  private final Prefs prefs;
  private final int paletteSize;
  private final boolean fadePaletteColors;
  private RandomFlameGenerator randGen;
  private final RandomBatchQuality quality;

  public RandomFlameGeneratorSampler(int pImageWidth, int pImageHeight, Prefs pPrefs, RandomFlameGenerator pRandGen, int pPaletteSize, boolean pFadePaletteColors, RandomBatchQuality pQuality) {
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    prefs = pPrefs;
    randGen = pRandGen;
    paletteSize = pPaletteSize;
    fadePaletteColors = pFadePaletteColors;
    quality = pQuality;
  }

  public static double calculateCoverage(SimpleImage pImg, int bgRed, int bgGreen, int bgBlue) {
    long maxCoverage = pImg.getImageWidth() * pImg.getImageHeight();
    long coverage = 0;
    Pixel pixel = new Pixel();
    if (bgRed == 0 && bgGreen == 0 && bgBlue == 0) {
      for (int k = 0; k < pImg.getImageHeight(); k++) {
        for (int l = 0; l < pImg.getImageWidth(); l++) {
          pixel.setARGBValue(pImg.getARGBValue(l, k));
          if (pixel.r > 20 || pixel.g > 20 || pixel.b > 20) {
            coverage++;
          }
        }
      }
    }
    else {
      for (int k = 0; k < pImg.getImageHeight(); k++) {
        for (int l = 0; l < pImg.getImageWidth(); l++) {
          pixel.setARGBValue(pImg.getARGBValue(l, k));
          if (Math.abs(pixel.r - bgRed) > 20.0 && Math.abs(pixel.g - bgGreen) > 20.0 && Math.abs(pixel.b - bgBlue) > 20.0) {
            coverage++;
          }
        }
      }
    }
    return (double) coverage / (double) maxCoverage;
  }

  public RandomFlameGeneratorSample createSample() {
    RenderInfo info = new RenderInfo(imageWidth, imageHeight);
    Flame bestFlame = null;
    int bgRed = prefs.getTinaRandomBatchBGColorRed();
    int bgGreen = prefs.getTinaRandomBatchBGColorGreen();
    int bgBlue = prefs.getTinaRandomBatchBGColorBlue();
    RandomFlameGeneratorState randGenState = randGen.initState();
    double bestCoverage = 0.0;
    for (int j = 0; j < quality.getMaxSamples(); j++) {
      // create flame
      Flame flame = randGen.createFlame(prefs, randGenState);
      flame.setWidth(imageWidth);
      flame.setHeight(imageHeight);
      flame.setPixelsPerUnit(10);
      flame.setSpatialFilterRadius(0.0);
      RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(paletteSize, fadePaletteColors);
      if (Math.random() < 0.5) {
        palette.sort();
      }

      flame.setPalette(palette);
      // render it   
      flame.setSampleDensity(25);
      RenderedFlame renderedFlame;
      boolean oldDEEnabled = flame.isDeFilterEnabled();
      flame.setDeFilterEnabled(false);
      for (XForm xForm : flame.getXForms()) {
        xForm.setAntialiasAmount(0.0);
      }
      for (XForm xForm : flame.getFinalXForms()) {
        xForm.setAntialiasAmount(0.0);
      }
      try {
        FlameRenderer renderer = new FlameRenderer(flame, prefs, false, true);
        renderedFlame = renderer.renderFlame(info);
      }
      finally {
        flame.setDeFilterEnabled(oldDEEnabled);
        for (XForm xForm : flame.getXForms()) {
          xForm.setAntialiasAmount(prefs.getTinaDefaultAntialiasingAmount());
        }
        for (XForm xForm : flame.getFinalXForms()) {
          xForm.setAntialiasAmount(prefs.getTinaDefaultAntialiasingAmount());
        }
      }
      if (j == quality.getMaxSamples() - 1) {
        renderedFlame = new FlameRenderer(bestFlame, prefs, false, true).renderFlame(info);
        return new RandomFlameGeneratorSample(bestFlame, renderedFlame.getImage());
      }
      else {
        double fCoverage = calculateCoverage(renderedFlame.getImage(), bgRed, bgGreen, bgBlue);
        if (fCoverage >= quality.getCoverage()) {
          return new RandomFlameGeneratorSample(flame, renderedFlame.getImage());
        }
        else {
          if (bestFlame == null || fCoverage > bestCoverage) {
            bestFlame = flame;
            bestCoverage = fCoverage;
          }
        }
      }
    }
    throw new IllegalStateException();
  }

  public static SimpleImage createSimplifiedRefImage(SimpleImage pImg) {
    SimpleImage img = pImg.clone();
    PixelizeTransformer pT = new PixelizeTransformer();
    pT.setCentre(false);
    pT.setGridSize(5);
    pT.transformImage(img);
    return img;
  }

  public static double calculateDiffCoverage(SimpleImage pImg, SimpleImage pSimplifiedRefImg) {
    SimpleImage img = createSimplifiedRefImage(pImg);
    long maxCoverage = img.getImageWidth() * img.getImageHeight();
    long coverage = 0;
    Pixel pixel = new Pixel();
    Pixel refPixel = new Pixel();
    for (int k = 0; k < img.getImageHeight(); k++) {
      for (int l = 0; l < img.getImageWidth(); l++) {
        pixel.setARGBValue(img.getARGBValue(l, k));
        refPixel.setARGBValue(pSimplifiedRefImg.getARGBValue(l, k));
        if (fabs(pixel.r - refPixel.r) > 20 || fabs(pixel.g - refPixel.g) > 20 || fabs(pixel.b - refPixel.b) > 20) {
          coverage++;
        }
      }
    }
    //    System.out.println((double) coverage / (double) maxCoverage);
    return (double) coverage / (double) maxCoverage;
  }
}
