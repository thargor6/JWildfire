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
package org.jwildfire.create.tina.render;

import static org.jwildfire.base.MathLib.M_PI;
import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.fabs;
import static org.jwildfire.base.MathLib.sin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.MathLib;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.RandomNumberGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;

public final class FlameRenderer {
  // constants
  private final static int MAX_FILTER_WIDTH = 25;
  // init in initRaster
  private int imageWidth;
  private int imageHeight;
  int rasterWidth;
  int rasterHeight;
  private int rasterSize;
  private int borderWidth;
  private int maxBorderWidth;
  LogDensityFilter logDensityFilter;
  GammaCorrectionFilter gammaCorrectionFilter;
  RasterPoint[][] raster;
  // init in initView
  double cosa;
  double sina;
  double camW;
  double camH;
  double rcX;
  double rcY;
  double bws;
  double bhs;
  private int renderScale = 1;
  // init in createColorMap
  RenderColor[] colorMap;
  double paletteIdxScl;
  RandomNumberGenerator random = new RandomNumberGenerator();
  // 3D stuff
  private boolean doProject3D = false;
  // init in init3D()
  private double cameraMatrix[][] = new double[3][3];
  //
  private ProgressUpdater progressUpdater;
  // 
  private final FlameTransformationContext flameTransformationContext;
  private RenderInfo renderInfo;

  private final Flame flame;
  private final Prefs prefs;

  private List<IterationObserver> iterationObservers;

  public void registerIterationObserver(IterationObserver pObserver) {
    if (iterationObservers == null) {
      iterationObservers = new ArrayList<IterationObserver>();
    }
    else if (iterationObservers.indexOf(pObserver) >= 0) {
      return;
    }
    iterationObservers.add(pObserver);
  }

  public FlameRenderer(Flame pFlame, Prefs pPrefs) {
    flame = pFlame;
    prefs = pPrefs;
    flameTransformationContext = new FlameTransformationContext(this);
    flameTransformationContext.setPreserveZCoordinate(pFlame.isPreserveZ());
  }

  private void init3D() {
    double yaw = -flame.getCamYaw() * M_PI / 180.0;
    double pitch = flame.getCamPitch() * M_PI / 180.0;
    cameraMatrix[0][0] = cos(yaw);
    cameraMatrix[1][0] = -sin(yaw);
    cameraMatrix[2][0] = 0;
    cameraMatrix[0][1] = cos(pitch) * sin(yaw);
    cameraMatrix[1][1] = cos(pitch) * cos(yaw);
    cameraMatrix[2][1] = -sin(pitch);
    cameraMatrix[0][2] = sin(pitch) * sin(yaw);
    cameraMatrix[1][2] = sin(pitch) * cos(yaw);
    cameraMatrix[2][2] = cos(pitch);
    doProject3D = fabs(flame.getCamYaw()) > MathLib.EPSILON || fabs(flame.getCamPitch()) > MathLib.EPSILON || fabs(flame.getCamPerspective()) > MathLib.EPSILON || fabs(flame.getCamDOF()) > MathLib.EPSILON;
  }

  private void initRaster(int pImageWidth, int pImageHeight) {
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    logDensityFilter = new LogDensityFilter(flame);
    gammaCorrectionFilter = new GammaCorrectionFilter(flame);
    maxBorderWidth = (MAX_FILTER_WIDTH - 1) / 2;
    borderWidth = (logDensityFilter.getNoiseFilterSize() - 1) / 2;
    rasterWidth = imageWidth + 2 * maxBorderWidth;
    rasterHeight = imageHeight + 2 * maxBorderWidth;
    rasterSize = rasterWidth * rasterHeight;
    raster = new RasterPoint[rasterHeight][rasterWidth];
    for (int i = 0; i < rasterHeight; i++) {
      for (int j = 0; j < rasterWidth; j++) {
        raster[i][j] = new RasterPoint();
      }
    }
  }

  public void project(XYZPoint pPoint) {
    if (!doProject3D) {
      return;
    }
    double z = pPoint.z;
    double px = cameraMatrix[0][0] * pPoint.x + cameraMatrix[1][0] * pPoint.y;
    double py = cameraMatrix[0][1] * pPoint.x + cameraMatrix[1][1] * pPoint.y + cameraMatrix[2][1] * z;
    double pz = cameraMatrix[0][2] * pPoint.x + cameraMatrix[1][2] * pPoint.y + cameraMatrix[2][2] * z;
    double zr = 1.0 - flame.getCamPerspective() * pz;
    if (fabs(flame.getCamDOF()) > MathLib.EPSILON) {
      double a = 2.0 * M_PI * random.random();
      double dsina = sin(a);
      double dcosa = cos(a);
      double zdist = (flame.getCamZ() - pz);
      double dr;
      if (zdist > 0.0) {
        dr = random.random() * flame.getCamDOF() * 0.1 * zdist;
      }
      else {
        dr = 0.0;
      }
      pPoint.x = (px + dr * dcosa) / zr;
      pPoint.y = (py + dr * dsina) / zr;

    }
    else {
      pPoint.x = px / zr;
      pPoint.y = py / zr;
    }
  }

  public RenderedFlame finishRenderFlame(long pSampleCount) {
    if (renderInfo == null) {
      throw new IllegalStateException();
    }
    double oldDensity = flame.getSampleDensity();
    try {
      double quality = logDensityFilter.calcDensity(pSampleCount, rasterSize);
      flame.setSampleDensity(quality);
      RenderedFlame res = new RenderedFlame();
      res.init(renderInfo);
      renderImage(res.getImage(), res.getHDRImage(), res.getHDRIntensityMap());
      return res;
    }
    finally {
      flame.setSampleDensity(oldDensity);
    }
  }

  public RenderedFlame renderFlame(RenderInfo pRenderInfo) {
    RenderedFlame res = new RenderedFlame();
    res.init(pRenderInfo);

    boolean renderNormal = true;
    boolean renderHDR = pRenderInfo.isRenderHDR();
    boolean renderHDRIntensityMap = pRenderInfo.isRenderHDRIntensityMap();

    if (flame.getXForms().size() == 0) {
      if (renderNormal) {
        if (renderScale > 0) {
          res.getImage().resetImage(res.getImage().getImageWidth() * renderScale, res.getImage().getImageHeight() * renderScale);
        }
        res.getImage().fillBackground(flame.getBGColorRed(), flame.getBGColorGreen(), flame.getBGColorBlue());
      }
      if (renderHDR) {
        res.getHDRImage().fillBackground(flame.getBGColorRed(), flame.getBGColorGreen(), flame.getBGColorBlue());
      }
      return res;
    }
    int spatialOversample = flame.getSampleDensity() >= 100.0 ? flame.getSpatialOversample() : 1;
    if (spatialOversample < 1 || spatialOversample > 6) {
      throw new IllegalArgumentException(String.valueOf(spatialOversample));
    }
    int colorOversample = flame.getSampleDensity() >= 100.0 ? flame.getColorOversample() : 1;
    if (colorOversample < 1 || colorOversample > 10) {
      throw new IllegalArgumentException(String.valueOf(colorOversample));
    }

    double origZoom = flame.getCamZoom();
    try {
      SimpleImage images[] = renderNormal ? new SimpleImage[colorOversample] : null;
      SimpleHDRImage hdrImages[] = renderHDR ? new SimpleHDRImage[colorOversample] : null;
      SimpleHDRImage hdrIntensityMapImages[] = renderHDRIntensityMap ? new SimpleHDRImage[colorOversample] : null;
      for (int i = 0; i < colorOversample; i++) {
        SimpleImage img = null;
        SimpleHDRImage hdrImg = null;
        SimpleHDRImage hdrIntensityMapImg = null;
        // spatial oversampling: create enlarged image
        if (spatialOversample > 1) {
          if (renderNormal) {
            img = new SimpleImage(res.getImage().getImageWidth() * spatialOversample, res.getImage().getImageHeight() * spatialOversample);
          }
          if (renderHDR) {
            hdrImg = new SimpleHDRImage(res.getHDRImage().getImageWidth() * spatialOversample, res.getHDRImage().getImageHeight() * spatialOversample);
          }
          //
          if (renderHDRIntensityMap) {
            hdrIntensityMapImg = new SimpleHDRImage(res.getHDRIntensityMap().getImageWidth() * spatialOversample, res.getHDRIntensityMap().getImageHeight() * spatialOversample);
          }
          if (i == 0) {
            flame.setCamZoom((double) spatialOversample * flame.getCamZoom());
          }
        }
        else {
          if (colorOversample > 1) {
            if (renderNormal) {
              img = new SimpleImage(res.getImage().getImageWidth(), res.getImage().getImageHeight());
            }
            if (renderHDR) {
              hdrImg = new SimpleHDRImage(res.getHDRImage().getImageWidth(), res.getHDRImage().getImageHeight());
            }
            if (renderHDRIntensityMap) {
              hdrIntensityMapImg = new SimpleHDRImage(res.getHDRIntensityMap().getImageWidth(), res.getHDRIntensityMap().getImageHeight());
            }
          }
          else {
            if (renderNormal) {
              img = res.getImage();
            }
            if (renderHDR) {
              hdrImg = res.getHDRImage();
            }
            if (renderHDR) {
              hdrIntensityMapImg = res.getHDRIntensityMap();
            }
          }
        }
        if (renderNormal) {
          images[i] = img;
        }
        if (renderHDR) {
          hdrImages[i] = hdrImg;
        }
        if (renderHDRIntensityMap) {
          hdrIntensityMapImages[i] = hdrIntensityMapImg;
        }

        if (renderNormal) {
          initRaster(img.getImageWidth(), img.getImageHeight());
        }
        else if (renderHDR) {
          initRaster(hdrImg.getImageWidth(), hdrImg.getImageHeight());
        }
        else if (renderHDRIntensityMap) {
          initRaster(hdrIntensityMapImg.getImageWidth(), hdrIntensityMapImg.getImageHeight());
        }
        else {
          throw new IllegalStateException();
        }

        init3D();
        createColorMap();
        initView();
        List<Flame> renderFlames = new ArrayList<Flame>();
        for (int t = 0; t < prefs.getTinaRenderThreads(); t++) {
          Flame renderFlame = flame.makeCopy();
          renderFlames.add(renderFlame);
          renderFlame.refreshModWeightTables(flameTransformationContext);
        }

        iterate(i, colorOversample, renderFlames);
        if (flame.getSampleDensity() <= 10.0) {
          renderImageSimple(img);
        }
        else {
          renderImage(img, hdrImg, hdrIntensityMapImg);
        }
      }

      // color oversampling
      SimpleImage img = null;
      SimpleHDRImage hdrImg = null;
      SimpleHDRImage hdrIntensityMapImg = null;
      if (colorOversample == 1) {
        if (renderNormal) {
          img = images[0];
        }
        if (renderHDR) {
          hdrImg = hdrImages[0];
        }
        if (renderHDRIntensityMap) {
          hdrIntensityMapImg = hdrIntensityMapImages[0];
        }
      }
      else {
        if (renderNormal) {
          int width = images[0].getImageWidth();
          int height = images[0].getImageHeight();
          if (spatialOversample > 1) {
            img = new SimpleImage(width, height);
          }
          else {
            img = res.getImage();
          }
          Pixel toolPixel = new Pixel();
          for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
              int r = 0, g = 0, b = 0;
              for (int k = 0; k < colorOversample; k++) {
                toolPixel.setARGBValue(images[k].getARGBValue(j, i));
                r += toolPixel.r;
                g += toolPixel.g;
                b += toolPixel.b;
              }
              toolPixel.r = Tools.roundColor((double) r / (double) colorOversample);
              toolPixel.g = Tools.roundColor((double) g / (double) colorOversample);
              toolPixel.b = Tools.roundColor((double) b / (double) colorOversample);
              img.setARGB(j, i, toolPixel.getARGBValue());
            }
          }
        }
        if (renderHDR) {
          int width = hdrImages[0].getImageWidth();
          int height = hdrImages[0].getImageHeight();
          if (spatialOversample > 1) {
            hdrImg = new SimpleHDRImage(width, height);
          }
          else {
            hdrImg = res.getHDRImage();
          }
          for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
              double r = 0, g = 0, b = 0;
              for (int k = 0; k < colorOversample; k++) {
                r += hdrImages[k].getRValue(j, i);
                g += hdrImages[k].getGValue(j, i);
                b += hdrImages[k].getBValue(j, i);
              }
              r /= (double) colorOversample;
              g /= (double) colorOversample;
              b /= (double) colorOversample;
              hdrImg.setRGB(j, i, (float) r, (float) g, (float) b);
            }
          }
        }
        if (renderHDRIntensityMap) {
          int width = hdrIntensityMapImages[0].getImageWidth();
          int height = hdrIntensityMapImages[0].getImageHeight();
          if (spatialOversample > 1) {
            hdrIntensityMapImg = new SimpleHDRImage(width, height);
          }
          else {
            hdrIntensityMapImg = res.getHDRIntensityMap();
          }
          for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
              double r = 0, g = 0, b = 0;
              for (int k = 0; k < colorOversample; k++) {
                r += hdrIntensityMapImages[k].getRValue(j, i);
                g += hdrIntensityMapImages[k].getGValue(j, i);
                b += hdrIntensityMapImages[k].getBValue(j, i);
              }
              r /= (double) colorOversample;
              g /= (double) colorOversample;
              b /= (double) colorOversample;
              hdrIntensityMapImg.setRGB(j, i, (float) r, (float) g, (float) b);
            }
          }
        }

      }

      // spatial oversampling: scale down      
      if (spatialOversample > 1) {
        if (renderNormal) {
          ScaleTransformer scaleT = new ScaleTransformer();
          scaleT.setScaleWidth(res.getImage().getImageWidth());
          scaleT.setScaleHeight(res.getImage().getImageHeight());
          scaleT.setAspect(ScaleAspect.IGNORE);
          scaleT.transformImage(img);
          res.getImage().setBufferedImage(img.getBufferedImg(), res.getImage().getImageWidth(), res.getImage().getImageHeight());
        }
        if (renderHDR) {
          hdrImg.sampleDown(spatialOversample);
          res.getHDRImage().assignImage(hdrImg);
        }
        if (renderHDRIntensityMap) {
          hdrIntensityMapImg.sampleDown(spatialOversample);
          res.getHDRIntensityMap().assignImage(hdrIntensityMapImg);
        }
      }
    }
    finally {
      flame.setCamZoom(origZoom);
    }
    return res;
  }

  private void renderImage(SimpleImage pImage, SimpleHDRImage pHDRImage, SimpleHDRImage pHDRIntensityMap) {
    if (renderScale > 1) {
      throw new IllegalArgumentException("renderScale != 1");
    }
    LogDensityPoint logDensityPnt = new LogDensityPoint();
    if (pImage != null) {
      logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pImage.getImageWidth(), pImage.getImageHeight());
    }
    else if (pHDRImage != null) {
      logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pHDRImage.getImageWidth(), pHDRImage.getImageHeight());
    }
    else if (pHDRIntensityMap != null) {
      logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pHDRIntensityMap.getImageWidth(), pHDRIntensityMap.getImageHeight());
    }
    else {
      throw new IllegalStateException();
    }

    if (pImage != null) {
      GammaCorrectedRGBPoint rbgPoint = new GammaCorrectedRGBPoint();
      for (int i = 0; i < pImage.getImageHeight(); i++) {
        for (int j = 0; j < pImage.getImageWidth(); j++) {
          logDensityFilter.transformPoint(logDensityPnt, j, i);
          gammaCorrectionFilter.transformPoint(logDensityPnt, rbgPoint);
          pImage.setRGB(j, i, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
        }
      }
    }

    if (pHDRImage != null) {
      GammaCorrectedHDRPoint rbgPoint = new GammaCorrectedHDRPoint();
      boolean hasBGColor = flame.getBGColorRed() > 0 || flame.getBGColorGreen() > 0 || flame.getBGColorBlue() > 0;
      boolean setBG = false;
      if (hasBGColor) {
        boolean bgMap[][] = new boolean[pHDRImage.getImageHeight()][pHDRImage.getImageWidth()];
        double minLum = Double.MAX_VALUE, maxLum = 0.0;
        for (int i = 0; i < pHDRImage.getImageHeight(); i++) {
          for (int j = 0; j < pHDRImage.getImageWidth(); j++) {
            logDensityFilter.transformPointHDR(logDensityPnt, j, i);
            gammaCorrectionFilter.transformPointHDR(logDensityPnt, rbgPoint);
            if (rbgPoint.red < 0.0) {
              bgMap[i][j] = true;
              setBG = true;
            }
            else {
              pHDRImage.setRGB(j, i, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
              double lum = (rbgPoint.red * 0.299 + rbgPoint.green * 0.588 + rbgPoint.blue * 0.113);
              if (lum > maxLum) {
                maxLum = lum;
              }
              else if (lum < minLum) {
                minLum = lum;
              }
            }
          }
        }
        if (setBG) {
          float weight = (float) (0.25 * (maxLum + 3 * minLum));
          float bgRed = (float) (gammaCorrectionFilter.getBgRedDouble() * weight);
          float bgGreen = (float) (gammaCorrectionFilter.getBgGreenDouble() * weight);
          float bgBlue = (float) (gammaCorrectionFilter.getBgBlueDouble() * weight);
          for (int i = 0; i < pHDRImage.getImageHeight(); i++) {
            for (int j = 0; j < pHDRImage.getImageWidth(); j++) {
              if (bgMap[i][j]) {
                pHDRImage.setRGB(j, i, bgRed, bgGreen, bgBlue);
              }
            }
          }
        }
      }
      else {
        for (int i = 0; i < pHDRImage.getImageHeight(); i++) {
          for (int j = 0; j < pHDRImage.getImageWidth(); j++) {
            logDensityFilter.transformPointHDR(logDensityPnt, j, i);
            gammaCorrectionFilter.transformPointHDR(logDensityPnt, rbgPoint);
            pHDRImage.setRGB(j, i, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
          }
        }
      }
    }

    if (pHDRIntensityMap != null) {
      for (int i = 0; i < pHDRIntensityMap.getImageHeight(); i++) {
        for (int j = 0; j < pHDRIntensityMap.getImageWidth(); j++) {
          logDensityFilter.transformPointHDR(logDensityPnt, j, i);
          pHDRIntensityMap.setRGB(j, i, (float) logDensityPnt.intensity, (float) logDensityPnt.intensity, (float) logDensityPnt.intensity);
        }
      }
    }
  }

  private void renderImageSimple(SimpleImage pImage) {
    LogDensityPoint logDensityPnt = new LogDensityPoint();
    GammaCorrectedRGBPoint rbgPoint = new GammaCorrectedRGBPoint();
    logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pImage.getImageWidth(), pImage.getImageHeight());
    if (renderScale == 2) {
      SimpleImage newImg = new SimpleImage(pImage.getImageWidth() * renderScale, pImage.getImageHeight() * renderScale);
      for (int i = 0; i < pImage.getImageHeight(); i++) {
        for (int j = 0; j < pImage.getImageWidth(); j++) {
          logDensityFilter.transformPointSimple(logDensityPnt, j, i);
          gammaCorrectionFilter.transformPointSimple(logDensityPnt, rbgPoint);
          int x = j * renderScale;
          int y = i * renderScale;

          newImg.setRGB(x, y, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
          newImg.setRGB(x + 1, y, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
          newImg.setRGB(x, y + 1, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
          newImg.setRGB(x + 1, y + 1, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
        }
      }
      pImage.setBufferedImage(newImg.getBufferedImg(), newImg.getImageWidth(), newImg.getImageHeight());
    }
    else if (renderScale == 1) {
      for (int i = 0; i < pImage.getImageHeight(); i++) {
        for (int j = 0; j < pImage.getImageWidth(); j++) {
          logDensityFilter.transformPointSimple(logDensityPnt, j, i);
          gammaCorrectionFilter.transformPointSimple(logDensityPnt, rbgPoint);
          pImage.setRGB(j, i, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
        }
      }
    }
    else {
      throw new IllegalArgumentException("renderScale " + renderScale);
    }
  }

  private FlameRenderThread createFlameRenderThread(Flame pFlame, long pSamples) {
    switch (flame.getShadingInfo().getShading()) {
      case FLAT:
        return new FlameRenderFlatThread(this, pFlame, pSamples);
      case BLUR:
        return new FlameRenderBlurThread(this, pFlame, pSamples);
      case PSEUDO3D:
        return new FlameRenderPseudo3DThread(this, pFlame, pSamples);
      default:
        throw new IllegalArgumentException(flame.getShadingInfo().getShading().toString());
    }
  }

  private void iterate(int pPart, int pParts, List<Flame> pFlames) {
    long nSamples = (long) ((flame.getSampleDensity() * (double) rasterSize + 0.5));
    //    if (flame.getSampleDensity() > 50) {
    //      System.err.println("SAMPLES: " + nSamples);
    //    }
    int PROGRESS_STEPS = 50;
    if (progressUpdater != null && pPart == 0) {
      progressUpdater.initProgress((PROGRESS_STEPS - 1) * pParts);
    }
    long sampleProgressUpdateStep = nSamples / PROGRESS_STEPS;
    long nextProgressUpdate = sampleProgressUpdateStep;
    List<FlameRenderThread> threads = new ArrayList<FlameRenderThread>();
    int nThreads = pFlames.size();
    if (nThreads <= 1) {
      FlameRenderThread t = createFlameRenderThread(pFlames.get(0), nSamples / (long) nThreads);
      t.run();
    }
    else {
      for (int i = 0; i < nThreads; i++) {
        FlameRenderThread t = createFlameRenderThread(pFlames.get(i), nSamples / (long) nThreads);
        threads.add(t);
        new Thread(t).start();
      }
      boolean done = false;
      while (!done) {
        try {
          Thread.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
        done = true;
        long currSamples = 0;
        for (FlameRenderThread t : threads) {
          if (!t.isFinished()) {
            done = false;
          }
          currSamples += t.getCurrSample();
        }
        if (currSamples >= nextProgressUpdate) {
          if (progressUpdater != null) {
            int currProgress = (int) ((currSamples * PROGRESS_STEPS) / nSamples);
            progressUpdater.updateProgress(currProgress + pPart * PROGRESS_STEPS);
            nextProgressUpdate = (currProgress + 1) * sampleProgressUpdateStep;
          }
        }
      }
    }
  }

  private List<FlameRenderThread> startIterate(List<Flame> pFlames, FlameRenderThreadState pState[], boolean pStartThreads) {
    List<FlameRenderThread> threads = new ArrayList<FlameRenderThread>();
    int nThreads = pFlames.size();
    for (int i = 0; i < nThreads; i++) {
      FlameRenderThread t = createFlameRenderThread(pFlames.get(i), -1);
      if (pState != null) {
        t.setResumeState(pState[i]);
      }
      t.setTonemapper(new SampleTonemapper(flame, raster, rasterWidth, rasterHeight, imageWidth, imageHeight));
      threads.add(t);
      if (pStartThreads) {
        new Thread(t).start();
      }
    }
    return threads;
  }

  private void initView() {
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double corner_x = flame.getCentreX() - (double) imageWidth / pixelsPerUnit / 2.0;
    double corner_y = flame.getCentreY() - (double) imageHeight / pixelsPerUnit / 2.0;
    double t0 = borderWidth / pixelsPerUnit;
    double t1 = borderWidth / pixelsPerUnit;
    double t2 = (2 * maxBorderWidth - borderWidth) / pixelsPerUnit;
    double t3 = (2 * maxBorderWidth - borderWidth) / pixelsPerUnit;

    double camX0 = corner_x - t0;
    double camY0 = corner_y - t1;
    double camX1 = corner_x + (double) imageWidth / pixelsPerUnit + t2;
    double camY1 = corner_y + (double) imageHeight / pixelsPerUnit + t3;

    camW = camX1 - camX0;
    double Xsize, Ysize;
    if (fabs(camW) > 0.01)
      Xsize = 1.0 / camW;
    else
      Xsize = 1.0;
    camH = camY1 - camY0;
    if (fabs(camH) > 0.01)
      Ysize = 1.0 / camH;
    else
      Ysize = 1;
    bws = (rasterWidth - 0.5) * Xsize;
    bhs = (rasterHeight - 0.5) * Ysize;

    cosa = cos(-M_PI * (flame.getCamRoll()) / 180.0);
    sina = sin(-M_PI * (flame.getCamRoll()) / 180.0);
    rcX = flame.getCentreX() * (1 - cosa) - flame.getCentreY() * sina - camX0;
    rcY = flame.getCentreY() * (1 - cosa) + flame.getCentreX() * sina - camY0;
  }

  public void createColorMap() {
    colorMap = flame.getPalette().createRenderPalette(flame.getWhiteLevel());
    paletteIdxScl = colorMap.length - 1;
  }

  public void setRandomNumberGenerator(RandomNumberGenerator random) {
    this.random = random;
  }

  public void setProgressUpdater(ProgressUpdater pProgressUpdater) {
    progressUpdater = pProgressUpdater;
  }

  public RasterPoint getRasterPoint(double pX, double pY) {
    int xIdx = (int) (bws * pX + 0.5);
    int yIdx = (int) (bhs * pY + 0.5);
    if (xIdx >= 0 && xIdx < rasterWidth && yIdx >= 0 && yIdx < rasterHeight) {
      return raster[yIdx][xIdx];
    }
    else {
      return null;
    }
  }

  public RandomNumberGenerator getRandomNumberGenerator() {
    return random;
  }

  public FlameTransformationContext getFlameTransformationContext() {
    return flameTransformationContext;
  }

  public void setRenderScale(int pRenderScale) {
    renderScale = pRenderScale;
  }

  public RenderColor[] getColorMap() {
    return colorMap;
  }

  protected List<IterationObserver> getIterationObservers() {
    return iterationObservers;
  }

  private void pauseThreads(List<FlameRenderThread> pThreads) {
    while (true) {
      boolean done = true;
      for (FlameRenderThread thread : pThreads) {
        if (!thread.isFinished()) {
          done = false;
          thread.cancel();
          try {
            Thread.sleep(1);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
          break;
        }
      }
      if (done) {
        break;
      }
    }
  }

  public static class JWFRenderFileHeader implements Serializable {
    private static final long serialVersionUID = 1L;
    public final String id = "JWFRender";
    public final int version = 1;
    public final int numThreads;

    public JWFRenderFileHeader(int pNumThreads) {
      numThreads = pNumThreads;
    }
  }

  public void saveState(String pAbsolutePath, List<FlameRenderThread> pThreads) {
    pauseThreads(pThreads);
    // store thread state
    FlameRenderThreadState state[] = new FlameRenderThreadState[pThreads.size()];
    for (int i = 0; i < pThreads.size(); i++) {
      state[i] = pThreads.get(i).saveState();
    }
    try {
      try {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(pAbsolutePath));
        try {
          // save header
          outputStream.writeObject(new JWFRenderFileHeader(pThreads.size()));
          // save flame
          outputStream.writeObject(flame);
          // save renderInfo          
          outputStream.writeObject(renderInfo);
          // save thread state
          for (int i = 0; i < pThreads.size(); i++) {
            outputStream.writeObject(state[i]);
          }
          // save raster
          outputStream.writeObject(raster);
        }
        finally {
          outputStream.flush();
          outputStream.close();
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
        throw new RuntimeException(ex);
      }
    }
    finally {
      resumeThreads(pThreads, state);
    }
  }

  private void resumeThreads(List<FlameRenderThread> pThreads, FlameRenderThreadState pState[]) {
    for (int i = 0; i < pThreads.size(); i++) {
      FlameRenderThread t = pThreads.get(i);
      t.setResumeState(pState[i]);
      new Thread(t).start();
    }
  }

  public List<FlameRenderThread> startRenderFlame(RenderInfo pRenderInfo) {
    renderInfo = pRenderInfo;
    initRaster(pRenderInfo.getImageWidth(), pRenderInfo.getImageHeight());
    init3D();
    createColorMap();
    initView();
    List<Flame> renderFlames = new ArrayList<Flame>();
    for (int t = 0; t < prefs.getTinaRenderThreads(); t++) {
      Flame renderFlame = flame.makeCopy();
      renderFlames.add(renderFlame);
      renderFlame.refreshModWeightTables(flameTransformationContext);
    }
    return startIterate(renderFlames, null, true);
  }

  public List<FlameRenderThread> resumeRenderFlame(String pAbsolutePath) {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(pAbsolutePath));
      try {
        // read header
        JWFRenderFileHeader header = (JWFRenderFileHeader) in.readObject();
        // read flame
        Flame rdFlame = (Flame) in.readObject();
        flame.assign(rdFlame);
        // restore renderInfo
        renderInfo = (RenderInfo) in.readObject();
        // restore thread state
        FlameRenderThreadState state[] = new FlameRenderThreadState[header.numThreads];
        for (int i = 0; i < header.numThreads; i++) {
          state[i] = (FlameRenderThreadState) in.readObject();
        }

        initRaster(renderInfo.getImageWidth(), renderInfo.getImageHeight());
        init3D();
        createColorMap();
        initView();
        List<Flame> renderFlames = new ArrayList<Flame>();
        for (int t = 0; t < header.numThreads; t++) {
          Flame renderFlame = flame.makeCopy();
          renderFlames.add(renderFlame);
          renderFlame.refreshModWeightTables(flameTransformationContext);
        }
        raster = null;
        // read raster
        raster = (RasterPoint[][]) in.readObject();
        // create threads
        return startIterate(renderFlames, state, false);
      }
      finally {
        in.close();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }

  public Flame getFlame() {
    return flame;
  }

  public long calcSampleCount() {
    long res = 0;
    if (raster != null) {
      for (int i = 0; i < rasterHeight; i++) {
        for (int j = 0; j < rasterWidth; j++) {
          res += raster[i][j].count;
        }
      }
    }
    return res;
  }

  public RenderInfo getRenderInfo() {
    return renderInfo;
  }

}
