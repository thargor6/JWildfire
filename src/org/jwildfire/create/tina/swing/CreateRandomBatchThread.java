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
package org.jwildfire.create.tina.swing;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
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

public class CreateRandomBatchThread implements Runnable{
  private final TinaController parentController;
  private final Prefs prefs;
  private final ProgressUpdater mainProgressUpdater;
  private final int maxCount;
  private final List<SimpleImage> imgList;
  private final List<FlameThumbnail> randomBatch;
  private final RandomFlameGenerator randGen;
  private final RandomSymmetryGenerator randSymmGen;
  private final RandomGradientGenerator randGradientGen;
  private final RandomWeightingFieldGenerator randWeightingFieldGen;
  private final RandomBatchQuality quality;
  private boolean done;
  private boolean cancelSignalled;

  public CreateRandomBatchThread(TinaController parentController, ProgressUpdater mainProgressUpdater, int maxCount, List<SimpleImage> imgList, List<FlameThumbnail> randomBatch, RandomFlameGenerator randGen, RandomSymmetryGenerator randSymmGen, RandomGradientGenerator randGradientGen, RandomWeightingFieldGenerator randWeightingFieldGen, RandomBatchQuality quality) {
    this.parentController = parentController;
    this.mainProgressUpdater = mainProgressUpdater;
    this.maxCount = maxCount;
    this.imgList = imgList;
    this.randomBatch = randomBatch;
    this.randGen = randGen;
    this.randSymmGen = randSymmGen;
    this.randGradientGen = randGradientGen;
    this.randWeightingFieldGen = randWeightingFieldGen;
    this.quality = quality;
    this.prefs = Prefs.getPrefs();
  }

  @Override
  public void run() {
    done = cancelSignalled = false;
    try {
      mainProgressUpdater.initProgress(maxCount);
      for (int i = 0; i < maxCount && !cancelSignalled; i++) {
        int palettePoints = 7 + Tools.randomInt(24);
        boolean fadePaletteColors = Math.random() > 0.06;
        boolean uniformWidth = Math.random() > 0.75;
        RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(FlameThumbnail.IMG_WIDTH / 2, FlameThumbnail.IMG_HEIGHT / 2, prefs, randGen, randSymmGen, randGradientGen, randWeightingFieldGen, palettePoints, fadePaletteColors, uniformWidth, quality);
        RandomFlameGeneratorSample sample = sampler.createSample();
        FlameThumbnail thumbnail;
        thumbnail = new FlameThumbnail(sample.getFlame(), null, null);
        SimpleImage img = thumbnail.getPreview(3 * prefs.getTinaRenderPreviewQuality() / 4);
        if (prefs.getTinaRandomBatchRefreshType() == RandomBatchRefreshType.INSERT) {
          randomBatch.add(0, thumbnail);
          imgList.add(0, img);
        } else {
          randomBatch.add(thumbnail);
          imgList.add(img);
        }
        final int currProgress = i + 1;
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            try {
              mainProgressUpdater.updateProgress(currProgress);
              int scrollPos = parentController.getScrollThumbnailsPosition();
              parentController.updateThumbnails();
              parentController.scrollThumbnailsToPosition(scrollPos);
            }
            catch(Exception ex) {
              ex.printStackTrace();;
            }
          }
        });

      }
    }
    finally {
      done = true;
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          try {
            parentController.notifyRandGenFinished();
          }
          catch(Exception ex) {
            ex.printStackTrace();
          }
        }
      });

    }
  }

  public boolean isDone() {
    return done;
  }

  public void signalCancel() {
    cancelSignalled = true;
  }
}
