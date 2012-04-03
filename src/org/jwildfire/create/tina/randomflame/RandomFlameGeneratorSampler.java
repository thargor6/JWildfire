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
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RandomRGBPaletteGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.Pixel;

public class RandomFlameGeneratorSampler {
  private final int imageWidth;
  private final int imageHeight;
  private final Prefs prefs;
  private final boolean withSymmetry;
  private final boolean withPostTransforms;
  private final int paletteSize;
  private RandomFlameGenerator randGen;
  final int MAX_IMG_SAMPLES = 10;
  final double MIN_COVERAGE = 0.33;

  public RandomFlameGeneratorSampler(int pImageWidth, int pImageHeight, Prefs pPrefs, RandomFlameGenerator pRandGen, boolean pWithSymmetry, boolean pWithPostTransforms, int pPaletteSize) {
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    prefs = pPrefs;
    randGen = pRandGen;
    withSymmetry = pWithSymmetry;
    withPostTransforms = pWithPostTransforms;
    paletteSize = pPaletteSize;
  }

  public RandomFlameGeneratorSample createSample() {
    RenderInfo info = new RenderInfo(imageWidth, imageHeight);
    Flame bestFlame = null;
    int bgRed = prefs.getTinaRandomBatchBGColorRed();
    int bgGreen = prefs.getTinaRandomBatchBGColorGreen();
    int bgBlue = prefs.getTinaRandomBatchBGColorBlue();
    double bestCoverage = 0.0;
    for (int j = 0; j < MAX_IMG_SAMPLES; j++) {
      // create flame
      Flame flame = randGen.createFlame(prefs, withSymmetry, withPostTransforms);
      flame.setWidth(imageWidth);
      flame.setHeight(imageHeight);
      flame.setPixelsPerUnit(10);
      RGBPalette palette = new RandomRGBPaletteGenerator().generatePalette(paletteSize);
      flame.setPalette(palette);
      // render it   
      flame.setSampleDensity(50);
      FlameRenderer renderer = new FlameRenderer(flame, prefs);
      RenderedFlame renderedFlame = renderer.renderFlame(info);
      if (j == MAX_IMG_SAMPLES - 1) {
        renderedFlame = new FlameRenderer(bestFlame, prefs).renderFlame(info);
        return new RandomFlameGeneratorSample(bestFlame, renderedFlame.getImage());
      }
      else {
        long maxCoverage = info.getImageWidth() * info.getImageHeight();
        long coverage = 0;
        Pixel pixel = new Pixel();
        if (bgRed == 0 && bgGreen == 0 && bgBlue == 0) {
          for (int k = 0; k < info.getImageHeight(); k++) {
            for (int l = 0; l < info.getImageWidth(); l++) {
              pixel.setARGBValue(renderedFlame.getImage().getARGBValue(l, k));
              if (pixel.r > 20 || pixel.g > 20 || pixel.b > 20) {
                coverage++;
              }
            }
          }
        }
        else {
          for (int k = 0; k < info.getImageHeight(); k++) {
            for (int l = 0; l < info.getImageWidth(); l++) {
              pixel.setARGBValue(renderedFlame.getImage().getARGBValue(l, k));
              if (Math.abs(pixel.r - bgRed) > 20.0 && Math.abs(pixel.g - bgGreen) > 20.0 && Math.abs(pixel.b - bgBlue) > 20.0) {
                coverage++;
              }
            }
          }
        }
        double fCoverage = (double) coverage / (double) maxCoverage;
        if (fCoverage >= MIN_COVERAGE) {
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

}
