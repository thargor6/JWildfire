/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.create.tina.swing;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.mutagen.MutationType;
import org.jwildfire.create.tina.randomflame.MutationSampler;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSample;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGenerator;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGenerator;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.image.SimpleImage;

import javax.swing.*;
import java.util.List;

public class CreateQuickMutationBatchThread implements Runnable{
  private final TinaController parentController;
  private final Flame baseFlame;
  private final Prefs prefs;
  private final ProgressUpdater mainProgressUpdater;
  private final int maxCount;
  private final List<SimpleImage> imgList;
  private final List<FlameThumbnail> randomBatch;
  private boolean done;
  private boolean cancelSignalled;
  private final int imgWidth, imgHeight;
  private final double mutationStrength;
  private final MutationType mutationType;

  public CreateQuickMutationBatchThread(TinaController parentController, Flame baseFlame, ProgressUpdater mainProgressUpdater, int maxCount, double mutationStrength, List<SimpleImage> imgList, List<FlameThumbnail> randomBatch, int imgWidth, int imgHeight, MutationType mutationType) {
    this.parentController = parentController;
    this.baseFlame = baseFlame;
    this.mainProgressUpdater = mainProgressUpdater;
    this.maxCount = maxCount;
    this.imgList = imgList;
    this.randomBatch = randomBatch;
    this.prefs = Prefs.getPrefs();
    this.imgWidth = imgWidth;
    this.imgHeight = imgHeight;
    this.mutationStrength = mutationStrength;
    this.mutationType = mutationType!=null ? mutationType: MutationType.ALL;
  }

  @Override
  public void run() {
    done = cancelSignalled = false;
    try {
      mainProgressUpdater.initProgress(maxCount);
      for (int i = 0; i < maxCount && !cancelSignalled; i++) {
        MutationSampler sampler = new MutationSampler( baseFlame,imgWidth / 4, imgHeight / 4, prefs, mutationType, mutationStrength);
        RandomFlameGeneratorSample sample = sampler.createSample();
        FlameThumbnail thumbnail;
        thumbnail = new FlameThumbnail( createFinalFlame(sample.getFlame(), baseFlame), null, null, imgWidth, imgHeight);
        SimpleImage img = thumbnail.getPreview(3 * prefs.getTinaRenderPreviewQuality() / 16);
        randomBatch.add(thumbnail);
        imgList.add(img);
        final int currProgress = i + 1;
        if (!cancelSignalled) {
          SwingUtilities.invokeLater(
              new Runnable() {
                @Override
                public void run() {
                  try {
                    mainProgressUpdater.updateProgress(currProgress);
                    int scrollPos = parentController.getScrollQuickMutationThumbnailsPosition();
                    parentController.updateQuickMutationThumbnails();
                    parentController.scrollQuickMutationThumbnailsToPosition(scrollPos);
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                }
            });
         }
      }
    }
    finally {
      done = true;
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          try {
            parentController.notifyQuickMutationsFinished();
          }
          catch(Exception ex) {
            ex.printStackTrace();
          }
        }
      });
    }
  }

  // create a mutation with the settings of the original flame
  private Flame createFinalFlame(Flame flame, Flame baseFlame) {
    Flame res = flame.makeCopy();
    res.setSampleDensity(baseFlame.getSampleDensity());
    res.setWidth(baseFlame.getWidth());
    res.setHeight(baseFlame.getHeight());
    res.setQualityProfile(baseFlame.getQualityProfile());
    res.setSpatialOversampling(baseFlame.getSpatialOversampling());
    res.setPixelsPerUnit(baseFlame.getPixelsPerUnit());
    return res;
  }

  public boolean isDone() {
    return done;
  }

  public void signalCancel() {
    cancelSignalled = true;
  }
}
