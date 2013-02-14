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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.MathLib;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.RandomGeneratorFactory;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;

public class FlameRenderer {
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
  private double cosa;
  private double sina;
  double camX0, camX1, camY0, camY1;
  double camW, camH;
  private double rcX;
  private double rcY;
  double bws;
  double bhs;
  private int renderScale = 1;
  // init in createColorMap
  RenderColor[] colorMap;
  double paletteIdxScl;
  private AbstractRandomGenerator randGen;
  // 3D stuff
  protected boolean doProject3D = false;
  // init in init3D()
  protected double cameraMatrix[][] = new double[3][3];
  protected double camDOF_10;
  protected boolean useDOF;
  protected boolean legacyDOF;
  private boolean withAlpha;
  //
  private ProgressUpdater progressUpdater;
  // 
  private final FlameTransformationContext flameTransformationContext;
  private RenderInfo renderInfo;

  protected final Flame flame;
  private final Prefs prefs;

  private List<IterationObserver> iterationObservers;
  private List<FlameRenderThread> runningThreads;
  private boolean forceAbort;

  public void registerIterationObserver(IterationObserver pObserver) {
    if (iterationObservers == null) {
      iterationObservers = new ArrayList<IterationObserver>();
    }
    else if (iterationObservers.indexOf(pObserver) >= 0) {
      return;
    }
    iterationObservers.add(pObserver);
  }

  public FlameRenderer(Flame pFlame, Prefs pPrefs, boolean pWithAlpha) {
    flame = pFlame;
    prefs = pPrefs;
    withAlpha = pWithAlpha;
    randGen = RandomGeneratorFactory.getInstance(prefs.getTinaRandomNumberGenerator());
    flameTransformationContext = new FlameTransformationContext(this, randGen);
    flameTransformationContext.setPreserveZCoordinate(pFlame.isPreserveZ());
  }

  public void init3D() {
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
    useDOF = fabs(flame.getCamDOF()) > MathLib.EPSILON;
    legacyDOF = !flame.isNewCamDOF();
    camDOF_10 = 0.1 * flame.getCamDOF();
  }

  public void initRasterSizes(int pImageWidth, int pImageHeight) {
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    logDensityFilter = new LogDensityFilter(flame);
    gammaCorrectionFilter = new GammaCorrectionFilter(flame, withAlpha);
    maxBorderWidth = (MAX_FILTER_WIDTH - 1) / 2;
    borderWidth = (logDensityFilter.getNoiseFilterSize() - 1) / 2;
    rasterWidth = imageWidth + 2 * maxBorderWidth;
    rasterHeight = imageHeight + 2 * maxBorderWidth;
    rasterSize = rasterWidth * rasterHeight;
  }

  private void initRaster(int pImageWidth, int pImageHeight) {
    initRasterSizes(pImageWidth, pImageHeight);
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
    double px = cameraMatrix[0][0] * pPoint.x + cameraMatrix[1][0] * pPoint.y /*+ cameraMatrix[2][0] * z*/;
    double py = cameraMatrix[0][1] * pPoint.x + cameraMatrix[1][1] * pPoint.y + cameraMatrix[2][1] * z;
    double pz = cameraMatrix[0][2] * pPoint.x + cameraMatrix[1][2] * pPoint.y + cameraMatrix[2][2] * z;
    double zr = 1.0 - flame.getCamPerspective() * pz;
    if (useDOF) {
      if (legacyDOF) {
        double zdist = (flame.getCamZ() - pz);
        if (zdist > 0.0) {
          double dr = randGen.random() * camDOF_10 * zdist;
          double a = 2.0 * M_PI * randGen.random();
          double dsina = sin(a);
          double dcosa = cos(a);
          pPoint.x = (px + dr * dcosa) / zr;
          pPoint.y = (py + dr * dsina) / zr;
        }
        else {
          pPoint.x = px / zr;
          pPoint.y = py / zr;
        }
      }
      else {
        double xdist = (px - flame.getFocusX());
        double ydist = (py - flame.getFocusY());
        double zdist = (pz - flame.getFocusZ());
        double dist = Math.pow(xdist * xdist + ydist * ydist + zdist * zdist, 1 / flame.getCamDOFExponent()) - flame.getCamDOFArea();
        if (dist > 0.05) {
          double dr = randGen.random() * camDOF_10 * dist;
          double a = 2.0 * M_PI * randGen.random();
          double dsina = sin(a);
          double dcosa = cos(a);
          pPoint.x = (px + dr * dcosa) / zr;
          pPoint.y = (py + dr * dsina) / zr;
        }
        else {
          pPoint.x = px / zr;
          pPoint.y = py / zr;
        }
      }
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

    double origZoom = flame.getCamZoom();
    try {
      SimpleImage img = renderNormal ? res.getImage() : null;
      SimpleHDRImage hdrImg = renderHDR ? res.getHDRImage() : null;
      SimpleHDRImage hdrIntensityMapImg = renderHDRIntensityMap ? res.getHDRIntensityMap() : null;

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
      forceAbort = false;
      iterate(0, 1, renderFlames);
      if (!forceAbort) {
        if (flame.getSampleDensity() <= 10.0) {
          renderImageSimple(img);
        }
        else {
          renderImage(img, hdrImg, hdrIntensityMapImg);
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
    boolean useDEFilter = flame.isDeFilterEnabled() && (flame.getDeFilterMaxRadius() > 0);
    RasterPoint accumRaster[][] = null;
    Flam3DEFilter deFilter = null;
    if (useDEFilter) {
      try {
        accumRaster = new RasterPoint[rasterHeight][rasterWidth];
        for (int i = 0; i < rasterHeight; i++) {
          for (int j = 0; j < rasterWidth; j++) {
            accumRaster[i][j] = new RasterPoint();
          }
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
        useDEFilter = false;
      }
      deFilter = new Flam3DEFilter(flame);
    }

    if (useDEFilter) {
      if (pImage != null) {
        deFilter.setRaster(accumRaster, raster, rasterWidth, rasterHeight, pImage.getImageWidth(), pImage.getImageHeight());
      }
      else if (pHDRImage != null) {
        deFilter.setRaster(accumRaster, raster, rasterWidth, rasterHeight, pHDRImage.getImageWidth(), pHDRImage.getImageHeight());
      }
      else if (pHDRIntensityMap != null) {
        deFilter.setRaster(accumRaster, raster, rasterWidth, rasterHeight, pHDRIntensityMap.getImageWidth(), pHDRIntensityMap.getImageHeight());
      }
      else {
        throw new IllegalStateException();
      }

      for (int i = 0; i < pImage.getImageHeight(); i++) {
        for (int j = 0; j < pImage.getImageWidth(); j++) {
          deFilter.transformPoint(j, i);
        }
      }
      raster = null;
      raster = accumRaster;
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
          if (useDEFilter) {
            logDensityFilter.transformPointPastDE(logDensityPnt, j, i);
          }
          else {
            logDensityFilter.transformPoint(logDensityPnt, j, i);
          }
          gammaCorrectionFilter.transformPoint(logDensityPnt, rbgPoint);
          pImage.setARGB(j, i, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
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

  public class RenderImageSimpleScaledThread implements Runnable {
    private final int startRow, endRow;
    private final LogDensityPoint logDensityPnt;
    private final GammaCorrectedRGBPoint rbgPoint;
    private final SimpleImage img;
    private final SimpleImage newImg;
    private boolean done;

    public RenderImageSimpleScaledThread(int pStartRow, int pEndRow, SimpleImage pImg, SimpleImage pNewImg) {
      startRow = pStartRow;
      endRow = pEndRow;
      logDensityPnt = new LogDensityPoint();
      rbgPoint = new GammaCorrectedRGBPoint();
      img = pImg;
      newImg = pNewImg;
      done = false;
    }

    @Override
    public void run() {
      done = false;
      for (int i = startRow; i < endRow; i++) {
        for (int j = 0; j < img.getImageWidth(); j++) {
          logDensityFilter.transformPointSimple(logDensityPnt, j, i);
          gammaCorrectionFilter.transformPointSimple(logDensityPnt, rbgPoint);
          int x = j * renderScale;
          int y = i * renderScale;

          newImg.setARGB(x, y, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
          newImg.setARGB(x + 1, y, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
          newImg.setARGB(x, y + 1, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
          newImg.setARGB(x + 1, y + 1, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
        }
      }
      done = true;
    }

    public boolean isDone() {
      return done;
    }

  }

  public class RenderImageSimpleThread implements Runnable {
    private final int startRow, endRow;
    private final LogDensityPoint logDensityPnt;
    private final GammaCorrectedRGBPoint rbgPoint;
    private final SimpleImage img;
    private boolean done;

    public RenderImageSimpleThread(int pStartRow, int pEndRow, SimpleImage pImg) {
      startRow = pStartRow;
      endRow = pEndRow;
      logDensityPnt = new LogDensityPoint();
      rbgPoint = new GammaCorrectedRGBPoint();
      img = pImg;
      done = false;
    }

    @Override
    public void run() {
      done = false;
      for (int i = startRow; i < endRow; i++) {
        for (int j = 0; j < img.getImageWidth(); j++) {
          logDensityFilter.transformPointSimple(logDensityPnt, j, i);
          gammaCorrectionFilter.transformPointSimple(logDensityPnt, rbgPoint);
          img.setARGB(j, i, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
        }
      }
      done = true;
    }

    public boolean isDone() {
      return done;
    }

  }

  private void renderImageSimple(SimpleImage pImage) {
    int threadCount = prefs.getTinaRenderThreads() - 1;
    if (threadCount < 1)
      threadCount = 1;
    logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pImage.getImageWidth(), pImage.getImageHeight());
    if (renderScale == 2) {
      SimpleImage newImg = new SimpleImage(pImage.getImageWidth() * renderScale, pImage.getImageHeight() * renderScale);
      if (threadCount == 1) {
        LogDensityPoint logDensityPnt = new LogDensityPoint();
        GammaCorrectedRGBPoint rbgPoint = new GammaCorrectedRGBPoint();
        for (int i = 0; i < pImage.getImageHeight(); i++) {
          for (int j = 0; j < pImage.getImageWidth(); j++) {
            logDensityFilter.transformPointSimple(logDensityPnt, j, i);
            gammaCorrectionFilter.transformPointSimple(logDensityPnt, rbgPoint);
            int x = j * renderScale;
            int y = i * renderScale;

            newImg.setARGB(x, y, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
            newImg.setARGB(x + 1, y, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
            newImg.setARGB(x, y + 1, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
            newImg.setARGB(x + 1, y + 1, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
          }
        }
      }
      else {
        int rowsPerThread = pImage.getImageHeight() / threadCount;
        List<RenderImageSimpleScaledThread> threads = new ArrayList<RenderImageSimpleScaledThread>();
        for (int i = 0; i < threadCount; i++) {
          int startRow = i * rowsPerThread;
          int endRow = i < rowsPerThread - 1 ? startRow + rowsPerThread : pImage.getImageHeight();
          RenderImageSimpleScaledThread thread = new RenderImageSimpleScaledThread(startRow, endRow, pImage, newImg);
          threads.add(thread);
          thread.run();
        }
        while (true) {
          boolean ready = true;
          for (RenderImageSimpleScaledThread t : threads) {
            if (!t.isDone()) {
              ready = false;
              break;
            }
          }
          if (!ready) {
            try {
              Thread.sleep(1);
            }
            catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          else
            break;
        }
      }
      pImage.setBufferedImage(newImg.getBufferedImg(), newImg.getImageWidth(), newImg.getImageHeight());
    }
    else if (renderScale == 1) {
      if (threadCount == 1) {
        LogDensityPoint logDensityPnt = new LogDensityPoint();
        GammaCorrectedRGBPoint rbgPoint = new GammaCorrectedRGBPoint();
        for (int i = 0; i < pImage.getImageHeight(); i++) {
          for (int j = 0; j < pImage.getImageWidth(); j++) {
            logDensityFilter.transformPointSimple(logDensityPnt, j, i);
            gammaCorrectionFilter.transformPointSimple(logDensityPnt, rbgPoint);
            pImage.setARGB(j, i, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
          }
        }
      }
      else {
        int rowsPerThread = pImage.getImageHeight() / threadCount;
        List<RenderImageSimpleThread> threads = new ArrayList<RenderImageSimpleThread>();
        for (int i = 0; i < threadCount; i++) {
          int startRow = i * rowsPerThread;
          int endRow = i < rowsPerThread - 1 ? startRow + rowsPerThread : pImage.getImageHeight();
          RenderImageSimpleThread thread = new RenderImageSimpleThread(startRow, endRow, pImage);
          threads.add(thread);
          thread.run();
        }
        while (true) {
          boolean ready = true;
          for (RenderImageSimpleThread t : threads) {
            if (!t.isDone()) {
              ready = false;
              break;
            }
          }
          if (!ready) {
            try {
              Thread.sleep(1);
            }
            catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          else
            break;
        }
      }
    }
    else {
      throw new IllegalArgumentException("renderScale " + renderScale);
    }
  }

  private FlameRenderThread createFlameRenderThread(int pThreadId, Flame pFlame, long pSamples) {
    switch (flame.getShadingInfo().getShading()) {
      case FLAT:
        return new FlameRenderFlatThread(prefs, pThreadId, this, pFlame, pSamples);
      case BLUR:
        return new FlameRenderBlurThread(prefs, pThreadId, this, pFlame, pSamples);
      case PSEUDO3D:
        return new FlameRenderPseudo3DThread(prefs, pThreadId, this, pFlame, pSamples);
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
    runningThreads = new ArrayList<FlameRenderThread>();
    int nThreads = pFlames.size();
    for (int i = 0; i < nThreads; i++) {
      FlameRenderThread t = createFlameRenderThread(i, pFlames.get(i), nSamples / (long) nThreads);
      runningThreads.add(t);
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
      for (FlameRenderThread t : runningThreads) {
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

  private List<FlameRenderThread> startIterate(List<Flame> pFlames, FlameRenderThreadState pState[], boolean pStartThreads) {
    List<FlameRenderThread> threads = new ArrayList<FlameRenderThread>();
    int nThreads = pFlames.size();
    for (int i = 0; i < nThreads; i++) {
      FlameRenderThread t = createFlameRenderThread(i, pFlames.get(i), -1);
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

  public void initView() {
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double corner_x = flame.getCentreX() - (double) imageWidth / pixelsPerUnit / 2.0;
    double corner_y = flame.getCentreY() - (double) imageHeight / pixelsPerUnit / 2.0;
    double t0 = borderWidth / pixelsPerUnit;
    double t1 = borderWidth / pixelsPerUnit;
    double t2 = (2 * maxBorderWidth - borderWidth) / pixelsPerUnit;
    double t3 = (2 * maxBorderWidth - borderWidth) / pixelsPerUnit;

    camX0 = corner_x - t0;
    camY0 = corner_y - t1;
    camX1 = corner_x + (double) imageWidth / pixelsPerUnit + t2;
    camY1 = corner_y + (double) imageHeight / pixelsPerUnit + t3;

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
    rcX = flame.getCentreX() * (1 - getCosa()) - flame.getCentreY() * getSina() - camX0;
    rcY = flame.getCentreY() * (1 - getCosa()) + flame.getCentreX() * getSina() - camY0;
  }

  public void createColorMap() {
    colorMap = flame.getPalette().createRenderPalette(flame.getWhiteLevel());
    paletteIdxScl = colorMap.length - 2;
  }

  public void setRandomNumberGenerator(AbstractRandomGenerator random) {
    this.randGen = random;
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

  public void saveState(String pAbsolutePath, List<FlameRenderThread> pThreads, long pSampleCount, long pElapsedMilliseconds, QualityProfile pQualityProfile) {
    pauseThreads(pThreads);
    // store thread state
    FlameRenderThreadState state[] = new FlameRenderThreadState[pThreads.size()];
    for (int i = 0; i < pThreads.size(); i++) {
      state[i] = pThreads.get(i).saveState();
    }
    try {
      try {
        ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pAbsolutePath)));
        try {
          // save header
          JWFRenderFileHeader header = new JWFRenderFileHeader(pThreads.size(), flame.getWidth(), flame.getHeight(),
              pSampleCount, pElapsedMilliseconds, 1, 1, pQualityProfile.getQuality(),
              pQualityProfile.isWithHDR(), pQualityProfile.isWithHDRIntensityMap());
          outputStream.writeObject(header);
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
        try {
          new File(pAbsolutePath).delete();
        }
        catch (Exception ex2) {

        }

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

  public ResumedFlameRender resumeRenderFlame(String pAbsolutePath) {
    try {
      ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pAbsolutePath)));
      try {
        // read header
        JWFRenderFileHeader header = (JWFRenderFileHeader) in.readObject();
        // read flame
        Flame rdFlame = (Flame) in.readObject();
        flame.assign(rdFlame);
        // restore renderInfo
        renderInfo = (RenderInfo) in.readObject();

        //        renderInfo.setRenderHDR(true);
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
        List<FlameRenderThread> threads = startIterate(renderFlames, state, false);
        return new ResumedFlameRender(header, threads);
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

  public void cancel() {
    forceAbort = true;
    if (runningThreads != null) {
      while (true) {
        boolean done = true;
        for (FlameRenderThread thread : runningThreads) {
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
  }

  public double getCamX0() {
    return camX0;
  }

  public double getCamX1() {
    return camX1;
  }

  public double getCamY0() {
    return camY0;
  }

  public double getCamY1() {
    return camY1;
  }

  public double getRcX() {
    return rcX;
  }

  public double getRcY() {
    return rcY;
  }

  public double getCosa() {
    return cosa;
  }

  public double getSina() {
    return sina;
  }

}
