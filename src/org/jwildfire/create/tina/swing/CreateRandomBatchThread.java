package org.jwildfire.create.tina.swing;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSample;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGenerator;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.image.SimpleImage;

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
  private final RandomBatchQuality quality;
  private boolean done;
  private boolean cancelSignalled;

  public CreateRandomBatchThread(TinaController parentController, ProgressUpdater mainProgressUpdater, int maxCount, List<SimpleImage> imgList, List<FlameThumbnail> randomBatch, RandomFlameGenerator randGen, RandomSymmetryGenerator randSymmGen, RandomGradientGenerator randGradientGen, RandomBatchQuality quality) {
    this.parentController = parentController;
    this.mainProgressUpdater = mainProgressUpdater;
    this.maxCount = maxCount;
    this.imgList = imgList;
    this.randomBatch = randomBatch;
    this.randGen = randGen;
    this.randSymmGen = randSymmGen;
    this.randGradientGen = randGradientGen;
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
        RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(FlameThumbnail.IMG_WIDTH / 2, FlameThumbnail.IMG_HEIGHT / 2, prefs, randGen, randSymmGen, randGradientGen, palettePoints, fadePaletteColors, uniformWidth, quality);
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
        mainProgressUpdater.updateProgress(i + 1);
        parentController.updateThumbnails();
        parentController.scrollThumbnailsToBottom();
      }
    }
    finally {
      done = true;
      parentController.notifyRandGenFinished();
    }
  }

  public boolean isDone() {
    return done;
  }

  public void signalCancel() {
    cancelSignalled = true;
  }
}
