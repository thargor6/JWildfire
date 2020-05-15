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
import org.jwildfire.base.ThreadTools;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.Stereo3dColor;
import org.jwildfire.create.tina.base.Stereo3dEye;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.raster.AbstractRaster;
import org.jwildfire.create.tina.base.raster.PointCloudOBJWriter;
import org.jwildfire.create.tina.base.raster.PointCloudPLYWriter;
import org.jwildfire.create.tina.base.raster.RasterPointCloud;
import org.jwildfire.create.tina.base.raster.RasterPointCloud.PCPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.RandomGeneratorFactory;
import org.jwildfire.create.tina.render.filter.FilteringType;
import org.jwildfire.create.tina.render.image.PostFilterImageThread;
import org.jwildfire.create.tina.render.image.RenderHDRImageThread;
import org.jwildfire.create.tina.render.image.RenderImageSimpleScaledThread;
import org.jwildfire.create.tina.render.image.RenderImageSimpleThread;
import org.jwildfire.create.tina.render.image.RenderImageThread;
import org.jwildfire.create.tina.render.image.RenderZBufferThread;
import org.jwildfire.create.tina.render.optix.OptixCmdLineDenoiser;
import org.jwildfire.create.tina.render.postdof.PostDOFBuffer;
import org.jwildfire.create.tina.render.postdof.PostDOFCalculator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleGrayImage;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.transform.ComposeTransformer;
import org.jwildfire.transform.ComposeTransformer.HAlignment;
import org.jwildfire.transform.ComposeTransformer.VAlignment;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;

public class FlameRenderer {
  // init in initRaster
  protected int imageWidth;
  protected int imageHeight;
  int rasterWidth;
  int rasterHeight;
  private int rasterSize;
  protected int borderWidth;
  private boolean withAlpha;
  LogDensityFilter logDensityFilter;
  GammaCorrectionFilter gammaCorrectionFilter;
  private AbstractRaster raster;
  private int oversample;
  // init in initView
  private int renderScale = 1;
  protected AbstractRandomGenerator randGen;
  //
  private ProgressUpdater progressUpdater;
  private int progressDisplayPhaseCount = 1;
  private int progressChangePerPhase = 0;
  private int progressDisplayPhase = 0;
  // 
  protected final FlameTransformationContext flameTransformationContext;
  private RenderInfo renderInfo;

  protected final Flame flame;
  private final Prefs prefs;
  private boolean preview;

  private List<IterationObserver> iterationObservers;
  private List<AbstractRenderThread> runningThreads;
  private boolean forceAbort;
  private Stereo3dEye eye = Stereo3dEye.UNSPECIFIED;

  public void deregisterIterationObserver(IterationObserver pObserver) {
    if (iterationObservers != null)
      iterationObservers.remove(pObserver);
  }

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
    if(pFlame.getMotionBlurLength()>0) {
      pFlame.setLowDensityBrightness(0.0);
    }
    flame = pFlame;
    if (flame.getSolidRenderSettings().isSolidRenderingEnabled()) {
      flame.setAntialiasAmount(0.0);
      flame.setAntialiasRadius(0.0);
    }
    prefs = pPrefs;
    withAlpha = pWithAlpha;
    preview = pPreview;
    randGen = RandomGeneratorFactory.getInstance(prefs, prefs.getTinaRandomNumberGenerator());
    flameTransformationContext = new FlameTransformationContext(this, randGen, 0, flame.getFrame());
    flameTransformationContext.setPreserveZCoordinate(pFlame.isPreserveZ());
    flameTransformationContext.setPreview(pPreview);
  }

  public void initRasterSizes(int pImageWidth, int pImageHeight) {
    Flame flameForInit;
    if (flame.hasPreRenderMotionProperty()) {
      double time = flame.getFrame() >= 0 ? flame.getFrame() : 0;
      flameForInit = AnimationService.evalMotionCurves(flame.makeCopy(), time);
    }
    else {
      flameForInit = flame;
    }

    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    oversample = flame.getSpatialOversampling();
    logDensityFilter = new LogDensityFilter(flameForInit, randGen);
    borderWidth =  Math.max((logDensityFilter.getNoiseFilterSize() - oversample),0) / 2;

    rasterWidth = oversample * imageWidth + 2 * borderWidth;
    rasterHeight = oversample * imageHeight + 2 * borderWidth;

    gammaCorrectionFilter = new GammaCorrectionFilter(flameForInit, withAlpha, rasterWidth, rasterHeight);
    rasterSize = rasterWidth * rasterHeight;
  }

  private void initRaster(RenderInfo pRenderInfo, int pImageWidth, int pImageHeight, double pSampleDensity) {
    initRasterSizes(pImageWidth, pImageHeight);
    if (pRenderInfo.getRestoredRaster() != null) {
      /*
      if (rasterWidth != pRenderInfo.getRestoredRaster().getRasterWidth()) {
        throw new RuntimeException("Raster width does not match (" + rasterWidth + " != " + pRenderInfo.getRestoredRaster().getRasterWidth() + ")");
      }
      if (rasterHeight != pRenderInfo.getRestoredRaster().getRasterHeight()) {
        throw new RuntimeException("Raster height does not match (" + rasterHeight + " != " + pRenderInfo.getRestoredRaster().getRasterHeight() + ")");
      }
      */
      raster = pRenderInfo.getRestoredRaster();
    }
    else {
      raster = allocRaster(oversample, pSampleDensity);
    }
  }

  private AbstractRaster allocRaster(int pOversample, double pSampleDensity) {
    Class<? extends AbstractRaster> rasterClass = prefs.getTinaRasterType().getRasterClass(flame);
    AbstractRaster raster;
    try {
      raster = rasterClass.newInstance();
    }
    catch (InstantiationException e) {
      throw new RuntimeException(e);
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    raster.allocRaster(flame, rasterWidth, rasterHeight, pOversample, pSampleDensity);
    return raster;
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
      raster.finalizeRaster();
      res.init(renderInfo, flame);
      renderImage(res.getImage(), res.getHDRImage(), res.getZBuffer());
      return res;
    }
    finally {
      flame.setSampleDensity(oldDensity);
    }
  }

  public RenderedFlame finishZBuffer(long pSampleCount) {
    if (renderInfo == null) {
      throw new IllegalStateException();
    }
    double oldDensity = flame.getSampleDensity();
    try {
      double quality = logDensityFilter.calcDensity(pSampleCount, rasterSize);

      flame.setSampleDensity(quality);
      RenderedFlame res = new RenderedFlame();
      RenderInfo tmpRenderInfo = new RenderInfo();
      tmpRenderInfo.assign(renderInfo);
      tmpRenderInfo.setRenderImage(false);
      tmpRenderInfo.setRenderHDR(false);
      tmpRenderInfo.setRenderZBuffer(true);
      res.init(tmpRenderInfo, flame);
      renderImage(res.getImage(), res.getHDRImage(), res.getZBuffer());
      return res;
    }
    finally {
      flame.setSampleDensity(oldDensity);
    }
  }

  public RenderedFlame renderFlame(RenderInfo pRenderInfo) {
    renderInfo = pRenderInfo;
    if (!Stereo3dMode.NONE.equals(flame.getStereo3dMode())) {
      return renderImageStereo3d(pRenderInfo);
    }
    else {
      return renderImageNormal(pRenderInfo, 1, 0);
    }
  }

  private RenderedFlame renderImageStereo3d(RenderInfo pRenderInfo) {
    Stereo3dEye storedEye = eye;
    double storedAngle = flame.getStereo3dAngle();
    double storedEyeDist = flame.getStereo3dEyeDist();
    try {
      switch (pRenderInfo.getRenderMode()) {
        case PREVIEW:
          switch (flame.getStereo3dPreview()) {
            case SIDE_BY_SIDE: {
              RenderedFlame result = renderStereo3dSideBySide(pRenderInfo);
              ScaleTransformer scaleTransformer = new ScaleTransformer();
              scaleTransformer.setAspect(ScaleAspect.IGNORE);
              scaleTransformer.setScaleWidth(pRenderInfo.getImageWidth() * renderScale);
              scaleTransformer.setScaleHeight(pRenderInfo.getImageHeight() * renderScale);
              scaleTransformer.transformImage(result.getImage());
              return result;
            }
            case SIDE_BY_SIDE_FULL: {
                int oldWidth = pRenderInfo.getImageWidth();
                pRenderInfo.setImageWidth(oldWidth/2);
                RenderedFlame result = renderStereo3dSideBySide(pRenderInfo);
                pRenderInfo.setImageWidth(oldWidth);
                return result;
            }
            case ANAGLYPH: {
              return renderStereo3dAnaglyph(pRenderInfo);
            }
            case NONE:
              return renderImageNormal(pRenderInfo, 1, 0);
            default:
              throw new IllegalStateException(pRenderInfo.getRenderMode().toString());
          }
        case PRODUCTION:
          switch (flame.getStereo3dMode()) {
            case ANAGLYPH:
              return renderStereo3dAnaglyph(pRenderInfo);
            case INTERPOLATED_IMAGES: {
              RenderInfo localRenderInfo = pRenderInfo.makeCopy();
              localRenderInfo.setRenderHDR(false);
              localRenderInfo.setRenderZBuffer(false);

              RenderedFlame leftRenders[] = new RenderedFlame[flame.getStereo3dInterpolatedImageCount()];
              double dAngle = storedAngle / (double) leftRenders.length;
              double dEyeDist = storedEyeDist / (double) leftRenders.length;
              int totalImageCount = 2 * leftRenders.length;
              int totalImageIdx = 0;
              for (int i = 0; i < leftRenders.length; i++) {
                eye = Stereo3dEye.LEFT;
                flame.setStereo3dAngle((i + 1) * dAngle);
                flame.setStereo3dEyeDist((i + 1) * dEyeDist);
                leftRenders[i] = renderImageNormal(localRenderInfo, totalImageCount, totalImageIdx++);
              }

              RenderedFlame rightRenders[] = new RenderedFlame[flame.getStereo3dInterpolatedImageCount()];
              for (int i = 0; i < rightRenders.length; i++) {
                eye = Stereo3dEye.RIGHT;
                flame.setStereo3dAngle((i + 1) * dAngle);
                flame.setStereo3dEyeDist((i + 1) * dEyeDist);
                rightRenders[i] = renderImageNormal(localRenderInfo, totalImageCount, totalImageIdx++);
              }

              if (flame.isStereo3dSwapSides()) {
                RenderedFlame tmp[] = leftRenders;
                leftRenders = rightRenders;
                rightRenders = tmp;
              }

              RenderedFlame mergedRender = new RenderedFlame();
              localRenderInfo.setImageWidth(2 * pRenderInfo.getImageWidth());
              localRenderInfo.setImageHeight(leftRenders.length * pRenderInfo.getImageHeight());
              mergedRender.init(localRenderInfo, flame);
              SimpleImage mergedImg = mergedRender.getImage();

              ComposeTransformer composeTransformer = new ComposeTransformer();
              composeTransformer.setHAlign(HAlignment.OFF);
              composeTransformer.setVAlign(VAlignment.OFF);

              int yOff = 0;
              for (int i = 0; i < leftRenders.length; i++) {
                composeTransformer.setLeft(0);
                composeTransformer.setTop(yOff);
                composeTransformer.setForegroundImage(leftRenders[i].getImage());
                composeTransformer.transformImage(mergedImg);
                yOff += pRenderInfo.getImageHeight();
              }

              yOff = 0;
              for (int i = 0; i < rightRenders.length; i++) {
                composeTransformer.setLeft(pRenderInfo.getImageWidth());
                composeTransformer.setTop(yOff);
                composeTransformer.setForegroundImage(rightRenders[i].getImage());
                composeTransformer.transformImage(mergedImg);
                yOff += pRenderInfo.getImageHeight();
              }

              return mergedRender;
            }

            case SIDE_BY_SIDE:
              return renderStereo3dSideBySide(pRenderInfo);
            case NONE:
              return renderImageNormal(pRenderInfo, 1, 0);
            default:
              throw new IllegalStateException(flame.getStereo3dMode().toString());
          }
        default:
          throw new IllegalStateException(pRenderInfo.getRenderMode().toString());
      }
    }
    finally {
      eye = storedEye;
      flame.setStereo3dAngle(storedAngle);
      flame.setStereo3dEyeDist(storedEyeDist);
    }
  }

  private RenderedFlame renderStereo3dSideBySide(RenderInfo pRenderInfo) {
    RenderInfo localRenderInfo = pRenderInfo.makeCopy();
    localRenderInfo.setRenderHDR(false);
    localRenderInfo.setRenderZBuffer(false);
    eye = Stereo3dEye.LEFT;
    RenderedFlame leftRender = renderImageNormal(localRenderInfo, 2, 0);
    eye = Stereo3dEye.RIGHT;
    RenderedFlame rightRender = renderImageNormal(localRenderInfo, 2, 1);

    if (flame.isStereo3dSwapSides()) {
      RenderedFlame tmp = leftRender;
      leftRender = rightRender;
      rightRender = tmp;
    }

    RenderedFlame mergedRender = new RenderedFlame();
    localRenderInfo.setImageWidth(2 * leftRender.getImage().getImageWidth());
    localRenderInfo.setImageHeight(leftRender.getImage().getImageHeight());
    mergedRender.init(localRenderInfo, flame);
    SimpleImage mergedImg = mergedRender.getImage();

    ComposeTransformer composeTransformer = new ComposeTransformer();
    composeTransformer.setHAlign(HAlignment.OFF);
    composeTransformer.setVAlign(VAlignment.OFF);
    composeTransformer.setForegroundImage(leftRender.getImage());
    composeTransformer.transformImage(mergedImg);

    composeTransformer.setForegroundImage(rightRender.getImage());
    composeTransformer.setLeft(leftRender.getImage().getImageWidth());
    composeTransformer.transformImage(mergedImg);

    return mergedRender;
  }

  private RenderedFlame renderStereo3dAnaglyph(RenderInfo pRenderInfo) {
    RenderInfo localRenderInfo = pRenderInfo.makeCopy();
    localRenderInfo.setRenderHDR(false);
    localRenderInfo.setRenderZBuffer(false);
    eye = Stereo3dEye.LEFT;
    RenderedFlame leftRender = renderImageNormal(localRenderInfo, 2, 0);
    eye = Stereo3dEye.RIGHT;
    RenderedFlame rightRender = renderImageNormal(localRenderInfo, 2, 1);

    if (flame.isStereo3dSwapSides()) {
      RenderedFlame tmp = leftRender;
      leftRender = rightRender;
      rightRender = tmp;
    }

    Pixel lPixel = new Pixel();
    Pixel rPixel = new Pixel();
    Stereo3dColor leftColor = flame.getAnaglyph3dLeftEyeColor();
    Stereo3dColor rightColor = flame.getAnaglyph3dRightEyeColor();
    SimpleImage leftImg = leftRender.getImage();
    SimpleImage rightImg = rightRender.getImage();

    RenderedFlame mergedRender = new RenderedFlame();
    localRenderInfo.setImageWidth(leftRender.getImage().getImageWidth());
    localRenderInfo.setImageHeight(leftRender.getImage().getImageHeight());
    mergedRender.init(localRenderInfo, flame);
    SimpleImage mergedImg = mergedRender.getImage();

    for (int i = 0; i < mergedImg.getImageHeight(); i++) {
      for (int j = 0; j < mergedImg.getImageWidth(); j++) {
        lPixel.setARGBValue(leftImg.getARGBValue(j, i));
        rPixel.setARGBValue(rightImg.getARGBValue(j, i));
        int mr = leftColor.calculateRed(lPixel.r, lPixel.g, lPixel.b) + rightColor.calculateRed(rPixel.r, rPixel.g, rPixel.b);
        if (mr < 0)
          mr = 0;
        else if (mr > 255)
          mr = 255;
        int mg = leftColor.calculateGreen(lPixel.r, lPixel.g, lPixel.b) + rightColor.calculateGreen(rPixel.r, rPixel.g, rPixel.b);
        if (mg < 0)
          mg = 0;
        else if (mg > 255)
          mg = 255;
        int mb = leftColor.calculateBlue(lPixel.r, lPixel.g, lPixel.b) + rightColor.calculateBlue(rPixel.r, rPixel.g, rPixel.b);
        if (mb < 0)
          mb = 0;
        else if (mb > 255)
          mb = 255;
        mergedImg.setRGB(j, i, mr, mg, mb);
      }
    }
    return mergedRender;
  }

  private RenderedFlame renderImageNormal(RenderInfo pRenderInfo, int pTotalImagePartCount, int pTotalImagePartIdx) {
    progressDisplayPhaseCount = pTotalImagePartCount;
    progressDisplayPhase = pTotalImagePartIdx;
    RenderedFlame res = new RenderedFlame();
    res.init(pRenderInfo, flame);
    if (forceAbort)
      return res;

    boolean renderNormal = true;
    boolean renderHDR = pRenderInfo.isRenderHDR();
    if (!flame.isRenderable()) {
      if (renderNormal) {
        if (renderScale > 0) {
          res.getImage().resetImage(res.getImage().getImageWidth() * renderScale, res.getImage().getImageHeight() * renderScale);
        }
        if (flame.getBGImageFilename().length() > 0) {
          try {
            res.getImage().fillBackground((SimpleImage) RessourceManager.getImage(flame.getBGImageFilename()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        else {
          new FlameBGColorHandler(flame).fillBackground(res.getImage());
        }
      }
      if (renderHDR) {
        res.getHDRImage().fillBackground(flame.getBgColorRed(), flame.getBgColorGreen(), flame.getBgColorBlue());
        try {
          res.getHDRImage().fillBackground((SimpleImage) RessourceManager.getImage(flame.getBGImageFilename()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      return res;
    }

    double origZoom = flame.getCamZoom();
    try {
      SimpleImage img = renderNormal ? res.getImage() : null;
      SimpleHDRImage hdrImg = renderHDR ? res.getHDRImage() : null;
      if (renderNormal) {
        initRaster(pRenderInfo, img.getImageWidth(), img.getImageHeight(), flame.getSampleDensity());
      }
      else if (renderHDR) {
        initRaster(pRenderInfo, hdrImg.getImageWidth(), hdrImg.getImageHeight(), flame.getSampleDensity());
      }
      else {
        throw new IllegalStateException();
      }
      if (pRenderInfo.getRestoredRaster() == null) {
        List<List<RenderPacket>> renderFlames = new ArrayList<List<RenderPacket>>();
        for (int t = 0; t < prefs.getTinaRenderThreads(); t++) {
          renderFlames.add(createRenderPackets(flame, t, flame.getFrame()));
        }
        forceAbort = false;
        iterate(0, 1, renderFlames, null);
        raster.finalizeRaster();
        if (pRenderInfo.isStoreRaster()) {
          res.setRaster(raster);
        }
      }
      else {
        if (flame.getSolidRenderSettings().isSolidRenderingEnabled()) {
          FlameRendererView view = createView(flame);
          raster.notifyInit(view.getLightViewCalculator());
        }
      }
      if (!forceAbort) {
        if ((flame.getSampleDensity() <= 10.0 && flame.getSpatialFilterRadius() <= MathLib.EPSILON) || renderScale > 1) {
          renderImageSimple(img);
        }
        else {
          renderImage(img, hdrImg, res.getZBuffer());
        }
      }
    }
    finally {
      flame.setCamZoom(origZoom);
    }
    return res;
  }

  private void renderImage(SimpleImage pImage, SimpleHDRImage pHDRImage, SimpleGrayImage pZBufferImg) {
    if (renderScale > 1) {
      throw new IllegalArgumentException("renderScale != 1");
    }

    if (pImage != null) {
      logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pImage.getImageWidth(), pImage.getImageHeight());
    }
    else if (pZBufferImg != null) {
      logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pZBufferImg.getImageWidth(), pZBufferImg.getImageHeight());
    }
    else if (pHDRImage != null) {
      logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pHDRImage.getImageWidth(), pHDRImage.getImageHeight());
    }
    else {
      throw new IllegalStateException();
    }

    renderImage(pImage);
    if (flame.isPostNoiseFilter() && flame.getPostNoiseFilterThreshold() > MathLib.EPSILON) {
      postFilterImage(pImage);
    }
    if (flame.isPostOptiXDenoiser() && flame.getPostOptiXDenoiserBlend() < 1.0 + MathLib.EPSILON &&
            OptixCmdLineDenoiser.isAvailable() &&
            !preview && (renderInfo.getRenderMode() == RenderMode.PRODUCTION || renderInfo.getRenderMode() == RenderMode.INTERACTIVE)) {
      applyPostOptiXDenoiser(pImage);
    }
    renderHDRImage(pHDRImage);
    renderZBuffer(pZBufferImg);
  }

  private void applyPostOptiXDenoiser(SimpleImage pImage) {
    SimpleImage denoisedImg = new OptixCmdLineDenoiser().denoise(pImage, flame.getPostOptiXDenoiserBlend());
    if(pImage.getImageWidth()!=denoisedImg.getImageWidth() || pImage.getImageHeight()!=denoisedImg.getImageHeight()) {
      throw new RuntimeException("Images sizes do not match");
    }
    pImage.setBufferedImage(denoisedImg.getBufferedImg(), denoisedImg.getImageWidth(), denoisedImg.getImageHeight());
  }

  private void renderZBuffer(SimpleGrayImage pGreyImage) {
    double zScale = 0.001 * flame.getZBufferScale();
    double zBias = flame.getZBufferBias();
    if (pGreyImage != null) {
      int threadCount = prefs.getTinaRenderThreads();
      if (threadCount < 1 || pGreyImage.getImageHeight() < 8 * threadCount) {
        threadCount = 1;
      }
      int rowsPerThread = pGreyImage.getImageHeight() / threadCount;
      List<RenderZBufferThread> threads = new ArrayList<>();
      for (int i = 0; i < threadCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = i < threadCount - 1 ? startRow + rowsPerThread : pGreyImage.getImageHeight();
        RenderZBufferThread thread = new RenderZBufferThread(flame, logDensityFilter, startRow, endRow, pGreyImage, zScale, zBias);
        threads.add(thread);
        if (threadCount > 1) {
          Thread t = new Thread(thread);
          t.setPriority(Thread.MIN_PRIORITY);
          t.start();
        }
        else {
          thread.run();
        }
      }
      ThreadTools.waitForThreads(threadCount, threads);
    }
  }

  private void renderHDRImage(SimpleHDRImage pHDRImage) {
    if (pHDRImage != null) {
      int threadCount = prefs.getTinaRenderThreads();
      if (threadCount < 1 || pHDRImage.getImageHeight() < 8 * threadCount) {
        threadCount = 1;
      }
      int rowsPerThread = pHDRImage.getImageHeight() / threadCount;
      PostDOFBuffer dofBuffer = flame.getCamDOF() > MathLib.EPSILON && flame.getSolidRenderSettings().isSolidRenderingEnabled() ? new PostDOFBuffer(pHDRImage) : null;
      List<RenderHDRImageThread> threads = new ArrayList<>();
      for (int i = 0; i < threadCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = i < threadCount - 1 ? startRow + rowsPerThread : pHDRImage.getImageHeight();
        RenderHDRImageThread thread = new RenderHDRImageThread(flame, logDensityFilter, gammaCorrectionFilter, startRow, endRow, pHDRImage, dofBuffer != null ? new PostDOFCalculator(dofBuffer, flame) : null);
        threads.add(thread);
        if (threadCount > 1) {
          Thread t= new Thread(thread);
          t.setPriority(Thread.MIN_PRIORITY);
          t.start();
        }
        else {
          thread.run();
        }
      }
      ThreadTools.waitForThreads(threadCount, threads);
      if (dofBuffer != null) {
        dofBuffer.renderToImage(pHDRImage);
      }
    }
  }

  private void renderImage(SimpleImage pImage) {
    if (pImage != null) {
      int threadCount = prefs.getTinaRenderThreads();
      if (threadCount < 1 || pImage.getImageHeight() < 8 * threadCount) {
        threadCount = 1;
      }
      PostDOFBuffer dofBuffer = flame.getCamDOF() > MathLib.EPSILON && flame.getSolidRenderSettings().isSolidRenderingEnabled() ? new PostDOFBuffer(pImage) : null;
      int rowsPerThread = pImage.getImageHeight() / threadCount;
      List<RenderImageThread> threads = new ArrayList<RenderImageThread>();
      for (int i = 0; i < threadCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = i < threadCount - 1 ? startRow + rowsPerThread : pImage.getImageHeight();
        RenderImageThread thread = new RenderImageThread(flame, logDensityFilter, gammaCorrectionFilter, startRow, endRow, pImage, dofBuffer != null ? new PostDOFCalculator(dofBuffer, flame) : null);
        threads.add(thread);
        if (threadCount > 1) {
          Thread t =  new Thread(thread);
          t.setPriority(Thread.MIN_PRIORITY);
          t.start();
        }
        else {
          thread.run();
        }
      }
      ThreadTools.waitForThreads(threadCount, threads);
      if (dofBuffer != null) {
        dofBuffer.renderToImage(pImage);
      }
    }
  }

  private void postFilterImage(SimpleImage pImage) {
    if (pImage != null) {
      int threadCount = prefs.getTinaRenderThreads();
      if (threadCount < 1 || pImage.getImageHeight() < 8 * threadCount) {
        threadCount = 1;
      }
      int rowsPerThread = pImage.getImageHeight() / threadCount;
      SimpleImage input = pImage.clone();
      List<PostFilterImageThread> threads = new ArrayList<PostFilterImageThread>();
      for (int i = 0; i < threadCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = i < threadCount - 1 ? startRow + rowsPerThread : pImage.getImageHeight();
        PostFilterImageThread thread = new PostFilterImageThread(startRow, endRow, input, pImage, flame.getPostNoiseFilterThreshold());
        threads.add(thread);
        if (threadCount > 1) {
          Thread t = new Thread(thread);
          t.setPriority(Thread.MIN_PRIORITY);
          t.start();
        }
        else {
          thread.run();
        }
      }
      ThreadTools.waitForThreads(threadCount, threads);
    }
  }

  private void renderImageSimple(SimpleImage pImage) {
    int threadCount = prefs.getTinaRenderThreads();
    if (threadCount < 1)
      threadCount = 1;
    logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pImage.getImageWidth(), pImage.getImageHeight());
    if (renderScale == 2) {
      SimpleImage newImg = new SimpleImage(pImage.getImageWidth() * renderScale, pImage.getImageHeight() * renderScale);
      int rowsPerThread = pImage.getImageHeight() / threadCount;
      List<RenderImageSimpleScaledThread> threads = new ArrayList<RenderImageSimpleScaledThread>();
      for (int i = 0; i < threadCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = i < threadCount - 1 ? startRow + rowsPerThread : pImage.getImageHeight();
        RenderImageSimpleScaledThread thread = new RenderImageSimpleScaledThread(flame, logDensityFilter, gammaCorrectionFilter, renderScale, startRow, endRow, pImage, newImg);
        threads.add(thread);
        if (threadCount > 1) {
          Thread t = new Thread(thread);
          t.setPriority(Thread.MIN_PRIORITY);
          t.start();
        }
        else {
          thread.run();
        }
      }
      ThreadTools.waitForThreads(threadCount, threads);
      pImage.setBufferedImage(newImg.getBufferedImg(), newImg.getImageWidth(), newImg.getImageHeight());
    }
    else if (renderScale == 1) {
      int rowsPerThread = pImage.getImageHeight() / threadCount;
      List<RenderImageSimpleThread> threads = new ArrayList<RenderImageSimpleThread>();
      for (int i = 0; i < threadCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = i < rowsPerThread - 1 ? startRow + rowsPerThread : pImage.getImageHeight();
        RenderImageSimpleThread thread = new RenderImageSimpleThread(flame, logDensityFilter, gammaCorrectionFilter, startRow, endRow, pImage);
        threads.add(thread);
        if (threadCount > 1) {
          Thread t = new Thread(thread);
          t.setPriority(Thread.MIN_PRIORITY);
          t.start();
        }
        else {
          thread.run();
        }
      }
      ThreadTools.waitForThreads(threadCount, threads);
    }
    else {
      throw new IllegalArgumentException("renderScale " + renderScale);
    }
  }

  private AbstractRenderThread createFlameRenderThread(int pThreadId, int pThreadGroupSize, List<RenderPacket> pRenderPackets, long pSamples, List<RenderSlice> pSlices) {
    return new FlatRenderThread(prefs, pThreadId, pThreadGroupSize, this, pRenderPackets, pSamples, pSlices);
  }

  private void iterate(int pPart, int pParts, List<List<RenderPacket>> pPackets, List<RenderSlice> pSlices) {
    long nSamples = (long) ((flame.getSampleDensity() * (double) rasterSize /  (double) flame.calcStereo3dSampleMultiplier() / (double) (oversample) + 0.5));
    int PROGRESS_STEPS = 21;
    if (progressUpdater != null && pPart == 0) {
      progressChangePerPhase = (PROGRESS_STEPS - 1) * pParts;
      progressUpdater.initProgress(progressChangePerPhase * progressDisplayPhaseCount);
    }
    long sampleProgressUpdateStep = nSamples / PROGRESS_STEPS;
    long nextProgressUpdate = sampleProgressUpdateStep;
    runningThreads = new ArrayList<AbstractRenderThread>();
    int nThreads = pPackets.size();
    for (int i = 0; i < nThreads; i++) {
      AbstractRenderThread t = createFlameRenderThread(i, nThreads, pPackets.get(i), nSamples / (long) nThreads, pSlices);
      runningThreads.add(t);
      Thread te = new Thread(t);
      te.setPriority(Thread.MIN_PRIORITY);
      te.start();
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
          progressUpdater.updateProgress(currProgress + pPart * PROGRESS_STEPS + progressChangePerPhase * progressDisplayPhase);
          nextProgressUpdate = (currProgress + 1) * sampleProgressUpdateStep;
        }
      }
    }
  }

  private RenderThreads startIterate(List<List<RenderPacket>> pFlames, RenderThreadPersistentState pState[], boolean pStartThreads) {
    List<AbstractRenderThread> renderThreads = new ArrayList<AbstractRenderThread>();
    List<Thread> executingThreads = new ArrayList<Thread>();
    int nThreads = pFlames.size();
    for (int i = 0; i < nThreads; i++) {
      AbstractRenderThread t = createFlameRenderThread(i, nThreads, pFlames.get(i), -1, null);
      if (pState != null) {
        t.setResumeState(pState[i]);
      }
      t.setTonemapper(new SampleTonemapper(flame, raster, rasterWidth, rasterHeight, imageWidth, imageHeight, randGen));
      renderThreads.add(t);
      if (pStartThreads) {
        Thread executingThread = new Thread(t);
        executingThread.setPriority(Thread.MIN_PRIORITY);
        executingThreads.add(executingThread);
        executingThread.start();
      }
    }
    return new RenderThreads(renderThreads, executingThreads);
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
      Thread te = new Thread(t);
      te.setPriority(Thread.MIN_PRIORITY);
      te.start();
    }
  }

  private List<RenderPacket> createRenderPackets(Flame pFlame, int pThreadId, int pFrame) {
    List<RenderPacket> res = new ArrayList<RenderPacket>();
    flameTransformationContext.setThreadId(pThreadId);
    {
      double time = pFrame >= 0 ? pFrame : 0;
      Flame newFlame = AnimationService.evalMotionCurves(pFlame.makeCopy(), time);

      for (Layer layer : newFlame.getLayers()) {
        layer.refreshModWeightTables(flameTransformationContext);
      }
      FlameRendererView view = createView(newFlame);
      res.add(new RenderPacket(newFlame, view));
    }
    if (pFlame.getMotionBlurLength() > 0) {
      double time = pFrame >= 0 ? pFrame : 0;
      double currTime = time + pFlame.getMotionBlurLength() * pFlame.getMotionBlurTimeStep() / 2.0;
      for (int p = 1; p <= pFlame.getMotionBlurLength(); p++) {
        currTime -= pFlame.getMotionBlurTimeStep();
        Flame newFlame = AnimationService.evalMotionCurves(pFlame.makeCopy(), currTime);
        for (Layer layer : newFlame.getLayers()) {
          layer.refreshModWeightTables(flameTransformationContext);
          double brightnessScl = (1.0 - p * p * pFlame.getMotionBlurDecay() * 0.07 / pFlame.getMotionBlurLength());
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
    if (!Stereo3dEye.UNSPECIFIED.equals(eye)) {
      switch (initialFlame.getAnaglyph3dMode()) {
        case INTERPOLATED_IMAGES:
        case SIDE_BY_SIDE:
        case ANAGLYPH:
          return new Stereo3dFlameRendererView(eye, initialFlame, randGen, borderWidth, imageWidth, imageHeight, rasterWidth, rasterHeight, flameTransformationContext, renderInfo);
        default: // nothing to do
          break;
      }
    }
    FlameRendererView view = new FlameRendererView(eye, initialFlame, randGen, borderWidth, imageWidth, imageHeight, rasterWidth, rasterHeight, flameTransformationContext, renderInfo);
    return view;
  }

  public RenderThreads startRenderFlame(RenderInfo pRenderInfo) {
    renderInfo = pRenderInfo;
    initRaster(pRenderInfo, pRenderInfo.getImageWidth(), pRenderInfo.getImageHeight(), flame.getSampleDensity());
    List<List<RenderPacket>> renderFlames = new ArrayList<List<RenderPacket>>();
    for (int t = 0; t < prefs.getTinaRenderThreads(); t++) {
      renderFlames.add(createRenderPackets(flame, t, flame.getFrame()));
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
        initRaster(renderInfo, renderInfo.getImageWidth(), renderInfo.getImageHeight(), flame.getSampleDensity());
        List<List<RenderPacket>> renderFlames = new ArrayList<List<RenderPacket>>();
        for (int t = 0; t < header.numThreads; t++) {
          renderFlames.add(createRenderPackets(flame, t, flame.getFrame()));
        }
        raster = null;
        // read raster
        raster = (AbstractRaster) in.readObject();
        // create threads
        RenderThreads threads = startIterate(renderFlames, state, false);
        return new ResumedFlameRender(header, threads.getRenderThreads());
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

  public void renderSlices(SliceRenderInfo pSliceRenderInfo, String pFilenamePattern) {
    if (!flame.isRenderable())
      throw new RuntimeException("Slices can not be created of empty flames");

    int fileIdx = 1;

    int passes = pSliceRenderInfo.getSlices() / pSliceRenderInfo.getSlicesPerRender();
    if (pSliceRenderInfo.getSlices() % pSliceRenderInfo.getSlicesPerRender() != 0)
      passes++;

    progressDisplayPhaseCount = passes;
    double zmin = pSliceRenderInfo.getZmin() < pSliceRenderInfo.getZmax() ? pSliceRenderInfo.getZmin() : pSliceRenderInfo.getZmax();
    double zmax = pSliceRenderInfo.getZmin() < pSliceRenderInfo.getZmax() ? pSliceRenderInfo.getZmax() : pSliceRenderInfo.getZmin();
    double thickness = (zmax - zmin) / (double) pSliceRenderInfo.getSlices();

    double currZ = zmax;
    int currSlice = 0;
    for (int pass = 0; pass < passes; pass++) {
      if (forceAbort) {
        break;
      }
      System.gc();
      progressDisplayPhase = pass;
      Flame currFlame = flame.makeCopy();
      prepareFlameForSliceRendering(currFlame);

      initRasterSizes(pSliceRenderInfo.getImageWidth(), pSliceRenderInfo.getImageHeight());
      List<RenderSlice> slices = new ArrayList<RenderSlice>();
      for (int i = 0; i < pSliceRenderInfo.getSlicesPerRender() && currSlice < pSliceRenderInfo.getSlices(); i++) {
        RenderSlice slice = new RenderSlice(allocRaster(flame.getSpatialOversampling(), flame.getSampleDensity()), currZ - thickness, currZ);
        slices.add(slice);
        currZ -= thickness;
        currSlice++;
      }

      List<List<RenderPacket>> renderFlames = new ArrayList<List<RenderPacket>>();
      for (int t = 0; t < prefs.getTinaRenderThreads(); t++) {
        renderFlames.add(createRenderPackets(flame, t, flame.getFrame()));
      }

      iterate(0, 1, renderFlames, slices);

      if (!forceAbort) {
        LogDensityPoint logDensityPnt = new LogDensityPoint(flame.getActiveLightCount());
        while (slices.size() > 0) {
          if (forceAbort) {
            break;
          }
          RenderSlice slice = slices.get(0);
          RenderedFlame renderedFlame = new RenderedFlame();
          renderedFlame.init(pSliceRenderInfo.createRenderInfo(), flame);
          SimpleImage img = renderedFlame.getImage();
          logDensityFilter.setRaster(slice.getRaster(), rasterWidth, rasterHeight, img.getImageWidth(), img.getImageHeight());
          GammaCorrectedRGBPoint rbgPoint = new GammaCorrectedRGBPoint();
          for (int i = 0; i < img.getImageHeight(); i++) {
            for (int j = 0; j < img.getImageWidth(); j++) {
              logDensityFilter.transformPoint(logDensityPnt, j, i);
              gammaCorrectionFilter.transformPoint(logDensityPnt, rbgPoint, j, i);
              img.setARGB(j, i, rbgPoint.alpha, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
            }
          }
          String filename = String.format(pFilenamePattern, fileIdx++);
          try {
            new ImageWriter().saveImage(renderedFlame.getImage(), filename);
          }
          catch (Exception ex) {
            throw new RuntimeException(ex);
          }

          slice = null;
          slices.remove(0);
        }
      }
    }
  }

  private void prepareFlameForSliceRendering(Flame pFlame) {
    pFlame.setStereo3dMode(Stereo3dMode.NONE);
    pFlame.setDimishZ(0.0);
    pFlame.setCamDOF(0.0);
    pFlame.setCamPerspective(0.0);
  }

  private void prepareFlameForPointCloudRendering(Flame pFlame) {
    pFlame.setStereo3dMode(Stereo3dMode.NONE);
    pFlame.setDimishZ(0.0);
    pFlame.setCamDOF(0.0);
    pFlame.setCamPerspective(0.0);
    pFlame.setSpatialOversampling(1);
    pFlame.setSpatialFilteringType(FilteringType.GLOBAL_SMOOTHING);
    pFlame.setSpatialFilterRadius(0.0);
  }

  public void renderPointCloud(PointCloudRenderInfo pPointCloudRenderInfo, String pFilename) {
    if (!flame.isRenderable())
      throw new RuntimeException("Point clouds can not be created of empty flames");

    renderInfo = pPointCloudRenderInfo.createRenderInfo();

    progressDisplayPhaseCount = 1;
    progressDisplayPhase = 0;

    double zmin = pPointCloudRenderInfo.getZmin() < pPointCloudRenderInfo.getZmax() ? pPointCloudRenderInfo.getZmin() : pPointCloudRenderInfo.getZmax();
    double zmax = pPointCloudRenderInfo.getZmin() < pPointCloudRenderInfo.getZmax() ? pPointCloudRenderInfo.getZmax() : pPointCloudRenderInfo.getZmin();

    Flame currFlame = flame.makeCopy();
    prepareFlameForPointCloudRendering(currFlame);

    initRasterSizes(pPointCloudRenderInfo.getImageWidth(), pPointCloudRenderInfo.getImageHeight());

    RasterPointCloud pcraster = new RasterPointCloud(zmin, zmax, pPointCloudRenderInfo.getMaxOctreeCellSize());

    raster = pcraster;
    raster.allocRaster(flame, rasterWidth, rasterHeight, flame.getSpatialOversampling(), flame.getSampleDensity());

    List<List<RenderPacket>> renderFlames = new ArrayList<List<RenderPacket>>();
    for (int t = 0; t < prefs.getTinaRenderThreads(); t++) {
      renderFlames.add(createRenderPackets(flame, t, flame.getFrame()));
    }

    iterate(0, 1, renderFlames, null);

    raster.finalizeRaster();

    List<PCPoint> points = pcraster.getGeneratedPoints();
    if (points != null && !points.isEmpty()) {
      if (Tools.getFileExt(pFilename).isEmpty()) {
        pFilename = pFilename + "." + Tools.FILEEXT_PLY;
      }
      if (Tools.FILEEXT_PLY.equalsIgnoreCase(Tools.getFileExt(pFilename))) {
        new PointCloudPLYWriter().writePLY(points, pFilename);
      }
      else if (Tools.FILEEXT_OBJ.equalsIgnoreCase(Tools.getFileExt(pFilename))) {
        new PointCloudOBJWriter().writeOBJ(points, pFilename);
      }
      else {
        throw new RuntimeException("Unvalid file extension <" + Tools.getFileExt(pFilename) + ">");
      }
    }
  }

  protected AbstractRaster getRaster() {
    return raster;
  }

}
