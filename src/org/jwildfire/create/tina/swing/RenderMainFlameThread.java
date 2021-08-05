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

import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jcodec.javase.api.awt.AWTSequenceEncoder;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.farender.FAFlameWriter;
import org.jwildfire.create.tina.farender.FARenderResult;
import org.jwildfire.create.tina.farender.FARenderTools;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.render.*;
import org.jwildfire.create.tina.render.denoiser.AIPostDenoiserFactory;
import org.jwildfire.create.tina.render.denoiser.AIPostDenoiserType;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;
import java.util.List;

public class RenderMainFlameThread implements Runnable {
  private final Prefs prefs;
  private final Flame flame;
  private final File outFile;
  private final QualityProfile qualProfile;
  private final ResolutionProfile resProfile;
  private final RenderMainFlameThreadFinishEvent finishEvent;
  private final ProgressUpdater progressUpdater;
  private boolean finished;
  private boolean forceAbort;
  private final boolean useGPU;
  private FlameRenderer renderer;

  public RenderMainFlameThread(Prefs pPrefs, Flame pFlame, File pOutFile, QualityProfile pQualProfile, ResolutionProfile pResProfile, RenderMainFlameThreadFinishEvent pFinishEvent, ProgressUpdater pProgressUpdater, boolean pUseGPU) {
    prefs = pPrefs;
    flame = pFlame.makeCopy();
    outFile = pOutFile;
    qualProfile = pQualProfile;
    resProfile = pResProfile;
    finishEvent = pFinishEvent;
    progressUpdater = pProgressUpdater;
    useGPU = pUseGPU;
  }

  @Override
  public void run() {
    finished = forceAbort = false;
    try {
      if (Tools.isMovieFile(outFile.getAbsolutePath())) {
        renderMovie();
      } else {
        renderSingleFrame();
      }
    } catch (Throwable ex) {
      finished = true;
      finishEvent.failed(ex);
    }
  }

  private void renderMovie() throws Exception {
    long t0 = Calendar.getInstance().getTimeInMillis();
    if (flame.getFrameCount() < 1) {
      throw new Exception("Invalif frame count <" + flame.getFrameCount() + ">");
    }

    int totalProgress = flame.getFrameCount() + flame.getFrameCount() / 4;
    progressUpdater.initProgress(totalProgress);
    int step = 1;

    for (int i = 1; i < flame.getFrameCount(); i++) {
      if (forceAbort) {
        finished = true;
        return;
      }
      String fn = RenderMovieUtil.makeFrameName(outFile.getAbsolutePath(), i, flame.getName(), qualProfile.getQuality(), resProfile.getWidth(), resProfile.getHeight());
      if (!new File(fn).exists()) {
        renderMovieFrame(i);
      }
      progressUpdater.updateProgress(step++);
    }
    if (forceAbort) {
      finished = true;
      return;
    }

    SeekableByteChannel out = null;
    try {
      out = NIOUtils.writableFileChannel(outFile.getAbsolutePath());
      AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(flame.getFps(), 1));
      for (int i = 1; i < flame.getFrameCount(); i++) {
        if (forceAbort) {
          finished = true;
          return;
        }
        String fn = RenderMovieUtil.makeFrameName(outFile.getAbsolutePath(), i, flame.getName(), qualProfile.getQuality(), resProfile.getWidth(), resProfile.getHeight());
        BufferedImage image = new ImageReader().loadBufferedRGBImage(fn);
        encoder.encodeImage(image);
        if (i % 4 == 0) {
          progressUpdater.updateProgress(step++);
        }
      }
      encoder.finish();
    } finally {
      NIOUtils.closeQuietly(out);
    }

    if (!Prefs.getPrefs().isTinaKeepTempMp4Frames()) {
      for (int i = 1; i < flame.getFrameCount(); i++) {
        if (forceAbort) {
          finished = true;
          return;
        }
        File f = new File(RenderMovieUtil.makeFrameName(outFile.getAbsolutePath(), i, flame.getName(), qualProfile.getQuality(), resProfile.getWidth(), resProfile.getHeight()));
        try {
          if (!f.delete()) {
            f.deleteOnExit();
          }
        } finally {
          f.deleteOnExit();
        }
      }
    }

    progressUpdater.updateProgress(totalProgress);
    long t1 = Calendar.getInstance().getTimeInMillis();
    finished = true;
    finishEvent.succeeded((t1 - t0) * 0.001);
  }


  private void renderMovieFrame(int frame) throws Exception {
    Flame currFlame = flame.makeCopy();
    currFlame.setFrame(frame);
    int width = resProfile.getWidth();
    int height = resProfile.getHeight();
    RenderInfo info = new RenderInfo(width, height, RenderMode.PRODUCTION);
    double wScl = (double) info.getImageWidth() / (double) currFlame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) currFlame.getHeight();
    currFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * currFlame.getPixelsPerUnit());
    currFlame.setWidth(info.getImageWidth());
    currFlame.setHeight(info.getImageHeight());
    info.setRenderHDR(false);
    info.setRenderZBuffer(false);
    currFlame.setSampleDensity(qualProfile.getQuality());
    String outputFilename = RenderMovieUtil.makeFrameName(
            outFile.getAbsolutePath(),
            frame,
            flame.getName(),
            qualProfile.getQuality(),
            resProfile.getWidth(),
            resProfile.getHeight());
    if(useGPU) {
      renderFlameOnGPU(currFlame, outputFilename, currFlame.getWidth(), currFlame.getHeight(), qualProfile.getQuality());
    } else {
      renderer = new FlameRenderer(currFlame, prefs, false, false);
      RenderedFlame res = renderer.renderFlame(info);
      if (!forceAbort) {
        new ImageWriter()
            .saveImage(
                res.getImage(),
                outputFilename);
      }
    }
  }

  private void renderFlameOnGPU(Flame flame, String primaryFilename, int width, int height, int quality) throws Exception {
      String gpuRenderFlameFilename = Tools.trimFileExt(primaryFilename) + ".flam3";
      try {
        Flame newFlame = AnimationService.evalMotionCurves(flame.makeCopy(), flame.getFrame());
        FileDialogTools.ensureFileAccess(null, null, gpuRenderFlameFilename);
        List<Flame> preparedFlames = FARenderTools.prepareFlame(newFlame);
        new FAFlameWriter().writeFlame(preparedFlames, gpuRenderFlameFilename);
        FARenderResult gpuRenderRes = FARenderTools.invokeFARender(gpuRenderFlameFilename, width, height, quality, preparedFlames.size() > 1);
        if (gpuRenderRes.getReturnCode() != 0) {
          throw new Exception(gpuRenderRes.getMessage());
        } else {
          if (!AIPostDenoiserType.NONE.equals(newFlame.getAiPostDenoiser()) && !flame.isPostDenoiserOnlyForCpuRender()) {
            AIPostDenoiserFactory.denoiseImage(gpuRenderRes.getOutputFilename(), newFlame.getAiPostDenoiser(), newFlame.getPostOptiXDenoiserBlend());
          }
        }
      } finally {
        if (!new File(gpuRenderFlameFilename).delete()) {
          new File(gpuRenderFlameFilename).deleteOnExit();
        }
      }
  }

  private GpuProgressUpdater gpuProgressUpdater=null;

  private void renderSingleFrame() throws Exception {
    int width = resProfile.getWidth();
    int height = resProfile.getHeight();
    RenderInfo info = new RenderInfo(width, height, RenderMode.PRODUCTION);
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    boolean renderHDR = qualProfile.isWithHDR();
    info.setRenderHDR(renderHDR);
    boolean renderZBuffer = qualProfile.isWithZBuffer();
    info.setRenderZBuffer(renderZBuffer);
    flame.setSampleDensity(qualProfile.getQuality());
    long t0, t1;
    if(useGPU) {
      final int PROGRESS_STEPS = 25;
      progressUpdater.initProgress(PROGRESS_STEPS);
      if(gpuProgressUpdater!=null) {
        gpuProgressUpdater.signalCancel();
      }
      if(gpuProgressUpdater!=null && gpuProgressUpdater.isFinished()) {
        gpuProgressUpdater = null;
      }
      if(gpuProgressUpdater==null) {
        gpuProgressUpdater = new GpuProgressUpdater(progressUpdater, PROGRESS_STEPS);
        new Thread(gpuProgressUpdater).start();
      }
      try {
        t0 = Calendar.getInstance().getTimeInMillis();
        renderFlameOnGPU(flame, outFile.getAbsolutePath(), flame.getWidth(), flame.getHeight(), qualProfile.getQuality());
        t1 = Calendar.getInstance().getTimeInMillis();
      }
      finally {
        try {
          if(gpuProgressUpdater!=null) {
            gpuProgressUpdater.signalCancel();
          }
        }
        catch(Exception ex) {
          // EMPTY
        }
      }
    } else {
      renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
      renderer.setProgressUpdater(progressUpdater);
      t0 = Calendar.getInstance().getTimeInMillis();
      RenderedFlame res = renderer.renderFlame(info);
      if (forceAbort) {
        finished = true;
        return;
      }
      t1 = Calendar.getInstance().getTimeInMillis();
      new ImageWriter().saveImage(res.getImage(), outFile.getAbsolutePath());
      if (res.getHDRImage() != null) {
        new ImageWriter()
            .saveImage(res.getHDRImage(), Tools.makeHDRFilename(outFile.getAbsolutePath()));
      }
      if (res.getZBuffer() != null) {
        new ImageWriter()
            .saveImage(
                res.getZBuffer(),
                Tools.makeZBufferFilename(outFile.getAbsolutePath(), flame.getZBufferFilename()));
      }
    }
    if (prefs.isTinaSaveFlamesWhenImageIsSaved()) {
      new FlameWriter().writeFlame(flame, outFile.getParentFile().getAbsolutePath() + File.separator + Tools.trimFileExt(outFile.getName()) + ".flame");
    }
    finished = true;
    finishEvent.succeeded((t1 - t0) * 0.001);
  }

  public boolean isFinished() {
    return finished;
  }

  public void setForceAbort() {
    forceAbort = true;
    if (renderer != null) {
      renderer.cancel();
    }
  }

}
