/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.RandomGeneratorFactory;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;

public class FlameRenderer {
  // constants
  private final static int MAX_FILTER_WIDTH = 25;
  // init in initRaster
  protected int imageWidth;
  protected int imageHeight;
  int rasterWidth;
  int rasterHeight;
  private int rasterSize;
  protected int borderWidth;
  protected int maxBorderWidth;
  private boolean withAlpha;
  LogDensityFilter logDensityFilter;
  GammaCorrectionFilter gammaCorrectionFilter;
  AbstractRasterPoint[][] raster;
  // init in initView
  private int renderScale = 1;
  protected AbstractRandomGenerator randGen;
  //
  private ProgressUpdater progressUpdater;
  // 
  private final FlameTransformationContext flameTransformationContext;
  private RenderInfo renderInfo;

  protected final Flame flame;
  private final Prefs prefs;
  private boolean preview;

  private List<IterationObserver> iterationObservers;
  private List<AbstractRenderThread> runningThreads;
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

  public FlameRenderer(Flame pFlame, Prefs pPrefs, boolean pWithAlpha, boolean pPreview) {
    flame = pFlame;
    prefs = pPrefs;
    withAlpha = pWithAlpha;
    preview = pPreview;
    randGen = RandomGeneratorFactory.getInstance(prefs.getTinaRandomNumberGenerator());
    flameTransformationContext = new FlameTransformationContext(this, randGen);
    flameTransformationContext.setPreserveZCoordinate(pFlame.isPreserveZ());
    flameTransformationContext.setPreview(pPreview);
  }

  public void initRasterSizes(int pImageWidth, int pImageHeight) {
    Flame flameForInit;
    if (flame.hasPreRenderMotionProperty() && Tools.V2_FEATURE_ENABLE) {
      double time = flame.getFrame() >= 0 ? flame.getFrame() : 0;
      flameForInit = AnimationService.evalMotionCurves(flame.makeCopy(), time);
    }
    else {
      flameForInit = flame;
    }

    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    logDensityFilter = new LogDensityFilter(flameForInit);
    gammaCorrectionFilter = new GammaCorrectionFilter(flameForInit, withAlpha);
    maxBorderWidth = (MAX_FILTER_WIDTH - 1) / 2;
    borderWidth = (logDensityFilter.getNoiseFilterSize() - 1) / 2;
    rasterWidth = imageWidth + 2 * maxBorderWidth;
    rasterHeight = imageHeight + 2 * maxBorderWidth;
    rasterSize = rasterWidth * rasterHeight;
  }

  private void initRaster(int pImageWidth, int pImageHeight) {
    initRasterSizes(pImageWidth, pImageHeight);
    Class<? extends AbstractRasterPoint> rpClass = prefs.getTinaRasterPointPrecision().getRasterPointClass();
    AbstractRasterPoint rp;
    try {
      rp = rpClass.newInstance();
    }
    catch (InstantiationException e) {
      throw new RuntimeException(e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    raster = rp.allocRaster(rasterWidth, rasterHeight);
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

    if (!flame.isRenderable()) {
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
      List<List<RenderPacket>> renderFlames = new ArrayList<List<RenderPacket>>();
      for (int t = 0; t < prefs.getTinaRenderThreads(); t++) {
        renderFlames.add(createRenderPackets(flame, flame.getFrame()));
      }
      forceAbort = false;
      iterate(0, 1, renderFlames);
      if (!forceAbort) {
        if (flame.getSampleDensity() <= 10.0 || renderScale > 1) {
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
    AbstractRasterPoint accumRaster[][] = null;
    Flam3DEFilter deFilter = null;
    if (useDEFilter) {
      try {
        Class<? extends AbstractRasterPoint> rpClass = prefs.getTinaRasterPointPrecision().getRasterPointClass();
        AbstractRasterPoint rp = rpClass.newInstance();
        accumRaster = rp.allocRaster(rasterWidth, rasterHeight);
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

  private AbstractRenderThread createFlameRenderThread(int pThreadId, List<RenderPacket> pRenderPackets, long pSamples) {
    switch (flame.getShadingInfo().getShading()) {
      case FLAT:
        return new FlatRenderThread(prefs, pThreadId, this, pRenderPackets, pSamples);
      case BLUR:
        return new BlurRenderThread(prefs, pThreadId, this, pRenderPackets, pSamples);
      case DISTANCE_COLOR:
        return new DistanceColorRenderThread(prefs, pThreadId, this, pRenderPackets, pSamples);
      case PSEUDO3D:
        return new Pseudo3DRenderThread(prefs, pThreadId, this, pRenderPackets, pSamples);
      default:
        throw new IllegalArgumentException(flame.getShadingInfo().getShading().toString());
    }
  }

  private void iterate(int pPart, int pParts, List<List<RenderPacket>> pPackets) {
    long nSamples = (long) ((flame.getSampleDensity() * (double) rasterSize + 0.5));
    int PROGRESS_STEPS = 50;
    if (progressUpdater != null && pPart == 0) {
      progressUpdater.initProgress((PROGRESS_STEPS - 1) * pParts);
    }
    long sampleProgressUpdateStep = nSamples / PROGRESS_STEPS;
    long nextProgressUpdate = sampleProgressUpdateStep;
    runningThreads = new ArrayList<AbstractRenderThread>();
    int nThreads = pPackets.size();
    for (int i = 0; i < nThreads; i++) {
      AbstractRenderThread t = createFlameRenderThread(i, pPackets.get(i), nSamples / (long) nThreads);
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
      for (AbstractRenderThread t : runningThreads) {
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

  private List<AbstractRenderThread> startIterate(List<List<RenderPacket>> pFlames, RenderThreadPersistentState pState[], boolean pStartThreads) {
    List<AbstractRenderThread> threads = new ArrayList<AbstractRenderThread>();
    int nThreads = pFlames.size();
    for (int i = 0; i < nThreads; i++) {
      AbstractRenderThread t = createFlameRenderThread(i, pFlames.get(i), -1);
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

  public void setRandomNumberGenerator(AbstractRandomGenerator random) {
    this.randGen = random;
  }

  public void setProgressUpdater(ProgressUpdater pProgressUpdater) {
    progressUpdater = pProgressUpdater;
  }

  public void setRenderScale(int pRenderScale) {
    renderScale = pRenderScale;
  }

  protected List<IterationObserver> getIterationObservers() {
    return iterationObservers;
  }

  private void pauseThreads(List<AbstractRenderThread> pThreads) {
    while (true) {
      boolean done = true;
      for (AbstractRenderThread thread : pThreads) {
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

  public void saveState(String pAbsolutePath, List<AbstractRenderThread> pThreads, long pSampleCount, long pElapsedMilliseconds, QualityProfile pQualityProfile) {
    pauseThreads(pThreads);
    // store thread state
    RenderThreadPersistentState state[] = new RenderThreadPersistentState[pThreads.size()];
    for (int i = 0; i < pThreads.size(); i++) {
      state[i] = pThreads.get(i).saveState();
    }
    try {
      try {
        ObjectOutputStream outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pAbsolutePath)));
        try {
          // save header
          JWFRenderFileHeader header = new JWFRenderFileHeader(pThreads.size(), flame.getWidth(), flame.getHeight(),
              pSampleCount, pElapsedMilliseconds, 1, 1, (int) (flame.getSampleDensity() + 0.5),
              true, false, withAlpha);
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

  private void resumeThreads(List<AbstractRenderThread> pThreads, RenderThreadPersistentState pState[]) {
    for (int i = 0; i < pThreads.size(); i++) {
      AbstractRenderThread t = pThreads.get(i);
      t.setResumeState(pState[i]);
      new Thread(t).start();
    }
  }

  private List<RenderPacket> createRenderPackets(Flame pFlame, int pFrame) {
    List<RenderPacket> res = new ArrayList<RenderPacket>();
    {
      double time = pFrame >= 0 ? pFrame : 0;
      Flame newFlame = AnimationService.evalMotionCurves(pFlame.makeCopy(), time);

      for (Layer layer : newFlame.getLayers()) {
        layer.refreshModWeightTables(flameTransformationContext);
      }
      FlameRendererView view = createView(newFlame);
      res.add(new RenderPacket(newFlame, view));
    }
    if (pFlame.getMotionBlurLength() > 0 && Tools.V2_FEATURE_ENABLE) {
      double time = pFrame >= 0 ? pFrame : 0;
      for (int p = 1; p < pFlame.getMotionBlurLength(); p++) {
        time += pFlame.getMotionBlurTimeStep();
        Flame newFlame = AnimationService.evalMotionCurves(pFlame.makeCopy(), time);
        for (Layer layer : newFlame.getLayers()) {
          layer.refreshModWeightTables(flameTransformationContext);

          double brightnessScl = (1.0 - p * pFlame.getMotionBlurDecay() * 0.01);
          if (brightnessScl < 0.01) {
            brightnessScl = 0.01;
          }
          layer.setWeight(brightnessScl * layer.getWeight());
        }
        FlameRendererView newView = createView(newFlame);
        res.add(new RenderPacket(newFlame, newView));
      }
    }
    return res;
  }

  protected FlameRendererView createView(Flame initialFlame) {
    return new FlameRendererView(initialFlame, randGen, borderWidth, maxBorderWidth, imageWidth, imageHeight, rasterWidth, rasterHeight);
  }

  public List<AbstractRenderThread> startRenderFlame(RenderInfo pRenderInfo) {
    // TODO remove frame from renderInfo? (color)
    renderInfo = pRenderInfo;
    initRaster(pRenderInfo.getImageWidth(), pRenderInfo.getImageHeight());
    List<List<RenderPacket>> renderFlames = new ArrayList<List<RenderPacket>>();
    for (int t = 0; t < prefs.getTinaRenderThreads(); t++) {
      renderFlames.add(createRenderPackets(flame, flame.getFrame()));
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
        // restore thread state
        withAlpha = header.withTransparency;
        RenderThreadPersistentState state[] = new RenderThreadPersistentState[header.numThreads];
        for (int i = 0; i < header.numThreads; i++) {
          state[i] = (RenderThreadPersistentState) in.readObject();
        }
        initRaster(renderInfo.getImageWidth(), renderInfo.getImageHeight());
        List<List<RenderPacket>> renderFlames = new ArrayList<List<RenderPacket>>();
        for (int t = 0; t < header.numThreads; t++) {
          renderFlames.add(createRenderPackets(flame, flame.getFrame()));
        }
        raster = null;
        // read raster
        raster = (AbstractRasterPoint[][]) in.readObject();
        // create threads
        List<AbstractRenderThread> threads = startIterate(renderFlames, state, false);
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
          res += raster[i][j].getCount();
        }
      }
    }
    return res;
  }

  public RenderInfo getRenderInfo() {
    return renderInfo;
  }

  public void signalCancel() {
    forceAbort = true;
  }

  public void cancel() {
    forceAbort = true;
    if (runningThreads != null) {
      while (true) {
        boolean done = true;
        for (AbstractRenderThread thread : runningThreads) {
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

  public boolean isPreview() {
    return preview;
  }

  public void setPreview(boolean pPreview) {
    preview = pPreview;
  }

}
