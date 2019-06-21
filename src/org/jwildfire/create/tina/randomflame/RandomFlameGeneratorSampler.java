/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2019 Andreas Maschke

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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGenerator;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.ConvolveTransformer;
import org.jwildfire.transform.ConvolveTransformer.KernelType;
import org.jwildfire.transform.PixelizeTransformer;

public class RandomFlameGeneratorSampler {
  private final int imageWidth;
  private final int imageHeight;
  private final Prefs prefs;
  private final int paletteSize;
  private final boolean fadePaletteColors;
  private final boolean uniformWidth;
  private RandomFlameGenerator randGen;
  private RandomSymmetryGenerator randSymmGen;
  private RandomGradientGenerator randGradientGen;
  private RandomWeightingFieldGenerator randWeightingFieldGen;

  private final RandomBatchQuality quality;

  public RandomFlameGeneratorSampler(int pImageWidth, int pImageHeight, Prefs pPrefs, RandomFlameGenerator pRandGen, RandomSymmetryGenerator pRandSymmGen, RandomGradientGenerator pRandGradientGen, RandomWeightingFieldGenerator pRandWeightingFieldGen, int pPaletteSize, boolean pFadePaletteColors, boolean pUniformWidth, RandomBatchQuality pQuality) {
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    prefs = pPrefs;
    randGen = pRandGen;
    randSymmGen = pRandSymmGen;
    randGradientGen = pRandGradientGen;
    randWeightingFieldGen = pRandWeightingFieldGen;
    paletteSize = pPaletteSize;
    fadePaletteColors = pFadePaletteColors;
    uniformWidth = pUniformWidth;
    quality = pQuality;
  }

  public static double calculateCoverage(SimpleImage pImg, int bgRed, int bgGreen, int bgBlue, boolean useFilter) {
    SimpleImage img;
    if (useFilter) {
      SimpleImage filteredImg = new SimpleImage(pImg.getBufferedImg(), pImg.getImageWidth(), pImg.getImageHeight());
      ConvolveTransformer transformer = new ConvolveTransformer();
      transformer.initDefaultParams(filteredImg);
      transformer.setKernelType(KernelType.SOBEL_3X3);
      transformer.transformImage(filteredImg);
      img = filteredImg;
    }
    else {
      img = pImg;
    }

    long maxCoverage = img.getImageWidth() * img.getImageHeight();
    long coverage = 0;
    Pixel pixel = new Pixel();
    if (bgRed == 0 && bgGreen == 0 && bgBlue == 0) {
      for (int k = 0; k < img.getImageHeight(); k++) {
        for (int l = 0; l < img.getImageWidth(); l++) {
          pixel.setARGBValue(img.getARGBValue(l, k));
          if (pixel.r > 29 || pixel.g > 15 || pixel.b > 78) {
            coverage++;
          }
        }
      }
    }
    else {
      for (int k = 0; k < img.getImageHeight(); k++) {
        for (int l = 0; l < img.getImageWidth(); l++) {
          pixel.setARGBValue(img.getARGBValue(l, k));
          if (Math.abs(pixel.r - bgRed) > 29.0 && Math.abs(pixel.g - bgGreen) > 15.0 && Math.abs(pixel.b - bgBlue) > 78.0) {
            coverage++;
          }
        }
      }
    }
    return (double) coverage / (double) maxCoverage;
  }

  public RandomFlameGeneratorSample createSample() {
    RenderInfo info = new RenderInfo(imageWidth, imageHeight, RenderMode.PREVIEW);
    Flame bestFlame = null;
    int bgRed = prefs.getTinaRandomBatchBGColorRed();
    int bgGreen = prefs.getTinaRandomBatchBGColorGreen();
    int bgBlue = prefs.getTinaRandomBatchBGColorBlue();
    RandomFlameGeneratorState randGenState = randGen.initState(prefs, randGradientGen);
    double bestCoverage = 0.0;
    for (int j = 0; j < quality.getMaxSamples(); j++) {
      // create flame
      Flame flame;
      try {
        flame = randGen.createFlame(prefs, randGenState);
        if (randGen.supportsSymmetry()) {
          randSymmGen.addSymmetry(flame);
        }
        if(randGen.supportsWeightingField()) {
          randWeightingFieldGen.addWeightingField(flame);
        }
      }
      catch (Exception ex) {
        flame = new Flame();
        ex.printStackTrace();
      }
      flame.setWidth(imageWidth);
      flame.setHeight(imageHeight);
      flame.setPixelsPerUnit(10);
      for (Layer layer : flame.getLayers()) {
        RGBPalette palette = randGradientGen.generatePalette(paletteSize, fadePaletteColors, uniformWidth);
        layer.setPalette(palette);
      }
      flame = randGen.postProcessFlameBeforeRendering(randGenState, flame);
      // render it   
      flame.setSampleDensity(25);
      RenderedFlame renderedFlame;

      flame.applyFastOversamplingSettings();
      try {
        FlameRenderer renderer = new FlameRenderer(flame, prefs, false, true);
        renderedFlame = renderer.renderFlame(info);
        flame = randGen.postProcessFlameAfterRendering(randGenState, flame);
        if (j == quality.getMaxSamples() - 1) {
          renderedFlame = new FlameRenderer(bestFlame, prefs, false, true).renderFlame(info);
          return new RandomFlameGeneratorSample(bestFlame, renderedFlame.getImage());
        }
        else {
          double fCoverage = calculateCoverage(renderedFlame.getImage(), bgRed, bgGreen, bgBlue, randGen.isUseFilter(randGenState));
          if (fCoverage >= quality.getCoverage() && fCoverage < randGen.getMaxCoverage()) {
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
      finally {
        flame.applyDefaultOversamplingSettings();
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
        if (fabs(pixel.r - refPixel.r) > 29.0 || fabs(pixel.g - refPixel.g) > 15.0 || fabs(pixel.b - refPixel.b) > 78.0) {
          coverage++;
        }
      }
    }
    //    System.out.println((double) coverage / (double) maxCoverage);
    return (double) coverage / (double) maxCoverage;
  }
}
