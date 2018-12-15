package org.jwildfire.create.tina.render;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.BGColorType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.transform.ComposeTransformer;

import java.io.File;

public class QuiltFlameRenderer {
  private boolean done;
  private boolean cancelSignalled;
  private FlameRenderer currRenderer;
  private boolean finalImageMerged;
  private Throwable renderException;

  public void renderFlame(Flame flame, int destWidth, int destHeight, int xSegmentationLevel, int ySegmentationLevel, int qualityLevel, String filename, ProgressUpdater totalProgressUpdater, ProgressUpdater detailProgressUpdater) {
    renderException = null;
    try {
      done = finalImageMerged = false;
      try {
        cancelSignalled = false;
        Flame clone = createAdjustedClone(flame, destWidth, destHeight);
        int spentHeight = 0;
        totalProgressUpdater.initProgress(xSegmentationLevel * ySegmentationLevel);
        for (int y = 0; y < ySegmentationLevel && !cancelSignalled; y++) {
          int currHeight = y < ySegmentationLevel - 1 ? getSegmentHeight(destHeight, ySegmentationLevel) : destHeight - spentHeight;
          spentHeight += currHeight;
          int spentWidth = 0;
          for (int x = 0; x < xSegmentationLevel && !cancelSignalled; x++) {
            String segmentFilename = getSegmentFilename(filename, destWidth, destHeight, xSegmentationLevel, ySegmentationLevel, qualityLevel, x, y);
            Flame renderClone = clone.makeCopy();
            // current bg-rendering does not support segments
            if (renderClone.getBgColorType() != BGColorType.SINGLE_COLOR) {
              renderClone.setBgColorType(BGColorType.SINGLE_COLOR);
              renderClone.setBgColorRed(0);
              renderClone.setBgColorGreen(0);
              renderClone.setBgColorBlue(0);
            }
            int currWidth = x < xSegmentationLevel - 1 ? getSegmentWidth(destWidth, xSegmentationLevel) : destWidth - spentWidth;
            spentWidth += currWidth;

            if (!new File(segmentFilename).exists()) {
              RenderInfo info = new RenderInfo(currWidth, currHeight, RenderMode.PRODUCTION);

              info.setSegmentRenderingCamXModifier(-(-xSegmentationLevel / 2 + x + (xSegmentationLevel%2==0 ? 0.5 : 0)) * getSegmentWidth(destWidth, xSegmentationLevel) / renderClone.getPixelsPerUnit() / renderClone.getCamZoom());
              info.setSegmentRenderingCamYModifier(-(-ySegmentationLevel / 2 + y + (ySegmentationLevel%2==0 ? 0.5 : 0)) * getSegmentHeight(destHeight, ySegmentationLevel) / renderClone.getPixelsPerUnit() / renderClone.getCamZoom());
              currRenderer = new FlameRenderer(renderClone, Prefs.getPrefs(), flame.isBGTransparency(), false);
              currRenderer.setProgressUpdater(detailProgressUpdater);
              RenderedFlame res = currRenderer.renderFlame(info);
              if (!cancelSignalled) {
                try {
                  new ImageWriter().saveImage(res.getImage(), segmentFilename);
                } catch (Exception ex) {
                  throw new RuntimeException(ex);
                }
              }
            }
            totalProgressUpdater.updateProgress(y * ySegmentationLevel + x + 1);
          }
        }
        totalProgressUpdater.updateProgress(xSegmentationLevel * ySegmentationLevel);
        if (!cancelSignalled) {
          mergeSegments(destWidth, destHeight, xSegmentationLevel, ySegmentationLevel, qualityLevel, filename);
        }
      } finally {
        done = true;
      }
    }
    catch(Throwable ex) {
      renderException = ex;
      throw ex;
    }
  }

  public boolean isDone() {
    return done;
  }

  private int getSegmentWidth(int destWidth, int segmentationLevel) {
    return (int) ((double) destWidth / (double) segmentationLevel + 0.5);
  }

  private int getSegmentHeight(int destHeight, int segmentationLevel) {
    return (int) ((double) destHeight / (double) segmentationLevel + 0.5);
  }

  private void mergeSegments(int destWidth, int destHeight, int xSegmentationLevel, int ySegmentationLevel, int qualityLevel,String filename) {
    SimpleImage img = new SimpleImage(destWidth, destHeight);
    for (int y = 0; y < ySegmentationLevel; y++) {
      for (int x = 0; x < xSegmentationLevel; x++) {
        String segmentFilename = getSegmentFilename(filename, destWidth, destHeight, xSegmentationLevel, ySegmentationLevel, qualityLevel, x, y);
        try {
          SimpleImage segment = new ImageReader().loadImage(segmentFilename);
          ComposeTransformer compose = new ComposeTransformer();
          compose.setForegroundImage(segment);
          compose.setHAlign(ComposeTransformer.HAlignment.OFF);
          compose.setVAlign(ComposeTransformer.VAlignment.OFF);
          compose.setLeft(x * getSegmentWidth(destWidth, xSegmentationLevel));
          compose.setTop(y * getSegmentHeight(destHeight, ySegmentationLevel));
          compose.transformImage(img);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
    }

    MarsagliaRandomGenerator randGen = new MarsagliaRandomGenerator();
    randGen.randomize(destWidth*16+destHeight);

    // hide unwanted borders
    for (int y = 0; y < ySegmentationLevel - 1; y++) {
      int yBottom = (y + 1) * getSegmentHeight(destHeight, ySegmentationLevel);
      for(int x=0;x<destWidth;x++) {
        if(randGen.random()<0.5) {
          int argb = img.getARGBValue(x, yBottom);
          img.setARGB(x, yBottom -1, argb);
        }
        else {
          int argb = img.getARGBValue(x, yBottom-1);
          img.setARGB(x, yBottom, argb);
        }
      }
    }
    for (int x = 0; x < xSegmentationLevel - 1; x++) {
      int xRight = (x + 1) * getSegmentWidth(destWidth, xSegmentationLevel);
      for(int y=0;y<destHeight;y++) {
        if(randGen.random()<0.5) {
          int argb = img.getARGBValue(xRight, y);
          img.setARGB(xRight-1, y, argb);
        }
        else {
          int argb = img.getARGBValue(xRight-1, y);
          img.setARGB(xRight, y, argb);
        }
      }
    }

    try {
      new ImageWriter().saveImage(img, filename);
      finalImageMerged = true;
      // delete segments
/*
      for (int y = 0; y < ySegmentationLevel; y++) {
        for (int x = 0; x < xSegmentationLevel; x++) {
          String segmentFilename = getSegmentFilename(filename, destWidth, destHeight, xSegmentationLevel, ySegmentationLevel, qualityLevel, x, y);
          new File(segmentFilename).delete();
        }
      }
*/
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private Flame createAdjustedClone(Flame flame, int destWidth, int destHeight) {
    Flame clone = flame.makeCopy();
    double wScl = (double) destWidth / (double) clone.getWidth();
    double hScl = (double) destHeight / (double) clone.getHeight();
    clone.setPixelsPerUnit((wScl + hScl) * 0.5 * clone.getPixelsPerUnit());
    clone.setWidth(destWidth);
    clone.setHeight(destHeight);
    return clone;
  }

  public String getSegmentFilename(String filename, int destinationWidth, int destinationHeight, int xSegmentationLevel, int ySegmentationLevel, int qualityLevel, int x, int y) {
    File file = new File(filename);
    String name = file.getName();
    int p = name.lastIndexOf(".");
    if (p > 0) {
      name = name.substring(0, p);
    }

    File segmentFile = new File(file.getParent(), name + "_" + destinationWidth + "x" + destinationHeight + "_" + xSegmentationLevel + "x" + ySegmentationLevel +"x" + qualityLevel + "_" + y + "_" + x + "_jwfseg.png");
    return segmentFile.getAbsolutePath();
  }


  public void cancel() {
    cancelSignalled = true;
    try {
      if (currRenderer != null) {
        currRenderer.cancel();
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public boolean finalImageWasMerged() {
    return finalImageMerged;
  }

  public Throwable getRenderException() {
    return renderException;
  }
}
