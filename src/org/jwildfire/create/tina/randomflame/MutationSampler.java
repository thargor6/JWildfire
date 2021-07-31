/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.mutagen.Mutation;
import org.jwildfire.create.tina.mutagen.MutationType;
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

import static org.jwildfire.base.mathlib.MathLib.fabs;

public class MutationSampler {
  private final int imageWidth;
  private final int imageHeight;
  private final Prefs prefs;
  private final MutationType mutationType;
  private final double mutationStrength;
  private RandomBatchQuality quality = RandomBatchQuality.QUICK_MUTATIONS;
  private final Flame baseFlame;

  public MutationSampler(Flame pBaseFlame, int pImageWidth, int pImageHeight, Prefs pPrefs, MutationType pMutationType, double pMutationStrength) {
    baseFlame = pBaseFlame;
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    prefs = pPrefs;
    mutationType = pMutationType;
    mutationStrength = pMutationStrength;
  }

  public RandomFlameGeneratorSample createSample() {
    RenderInfo info = new RenderInfo(imageWidth, imageHeight, RenderMode.PREVIEW);
    Flame bestFlame = null;
    int bgRed = prefs.getTinaRandomBatchBGColorRed();
    int bgGreen = prefs.getTinaRandomBatchBGColorGreen();
    int bgBlue = prefs.getTinaRandomBatchBGColorBlue();
    double bestCoverage = 0.0;
    for (int j = 0; j < quality.getMaxSamples(); j++) {
      // create flame
      Flame flame = baseFlame.makeCopy();
      try {
        Mutation mutation = mutationType.createMutationInstance();
        for (Layer layer : flame.getLayers()) {
          mutation.execute(layer, mutationStrength);
        }
        String name = flame.getName();
        int p=name.lastIndexOf(" - ");
        if(p>0) {
          String postfix = name.substring(p+3, name.length());
          name = name.substring(0, p);
          try {
            Integer.parseInt(postfix);
          }
          catch(Exception ex) {
            // not a number, nothing to do
          }
        }
        flame.setName(name + " - " + mutation.getCaption() + " - " + flame.hashCode());
      }
      catch (Exception ex) {
        flame = new Flame();
        ex.printStackTrace();
      }
      flame.setWidth(imageWidth);
      flame.setHeight(imageHeight);
      flame.setPixelsPerUnit(10);
      // render it
      flame.setSampleDensity(25);
      RenderedFlame renderedFlame;

      flame.applyFastOversamplingSettings();
      try {
        FlameRenderer renderer = new FlameRenderer(flame, prefs, false, true);
        renderedFlame = renderer.renderFlame(info);
        if (j == quality.getMaxSamples() - 1) {
          renderedFlame = new FlameRenderer(bestFlame, prefs, false, true).renderFlame(info);
          return new RandomFlameGeneratorSample(bestFlame, renderedFlame.getImage());
        }
        else {
          double fCoverage = RandomFlameGeneratorSampler.calculateCoverage(renderedFlame.getImage(), bgRed, bgGreen, bgBlue, false);
          if (fCoverage >= quality.getCoverage() && fCoverage < 1.0) {
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

}
