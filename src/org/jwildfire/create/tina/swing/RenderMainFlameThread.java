/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Calendar;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;

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
  private FlameRenderer renderer;

  public RenderMainFlameThread(Prefs pPrefs, Flame pFlame, File pOutFile, QualityProfile pQualProfile, ResolutionProfile pResProfile, RenderMainFlameThreadFinishEvent pFinishEvent, ProgressUpdater pProgressUpdater) {
    prefs = pPrefs;
    flame = pFlame.makeCopy();
    outFile = pOutFile;
    qualProfile = pQualProfile;
    resProfile = pResProfile;
    finishEvent = pFinishEvent;
    progressUpdater = pProgressUpdater;
  }

  @Override
  public void run() {
    finished = forceAbort = false;
    try {
      if(Tools.isMovieFile(outFile.getAbsolutePath())) {
        renderMovie();
      }
      else {
        renderSingleFrame();
      }
    }
    catch (Throwable ex) {
      finished = true;
      finishEvent.failed(ex);
    }
  }

  private void renderMovie() throws Exception {
    long t0 = Calendar.getInstance().getTimeInMillis();
    if(flame.getFrameCount()<1) {
      throw new Exception("Invalif frame count <" + flame.getFrameCount()+">");
    }

    int totalProgress = flame.getFrameCount()+flame.getFrameCount()/4;
    progressUpdater.initProgress(totalProgress);
    int step=1;

    for(int i=1;i<flame.getFrameCount();i++) {
      if (forceAbort) {
        finished = true;
        return;
      }
      String fn = makeFrameName(outFile.getAbsolutePath(),i);
      if(!new File(fn).exists()) {
        renderMovieFrame(i);
      }
      progressUpdater.updateProgress(step++);
    }
    if(forceAbort) {
      finished = true;
      return;
    }

    SeekableByteChannel out = null;
    try {
      out = NIOUtils.writableFileChannel(outFile.getAbsolutePath());
      AWTSequenceEncoder encoder = new AWTSequenceEncoder(out, Rational.R(flame.getFps(), 1));
      for(int i=1;i<flame.getFrameCount();i++) {
        if (forceAbort) {
          finished = true;
          return;
        }
        String fn = makeFrameName(outFile.getAbsolutePath(),i);
        SimpleImage img = new ImageReader().loadImage(fn);
        BufferedImage image = img.getBufferedImg();
        encoder.encodeImage(image);
        if(i%4==0) {
          progressUpdater.updateProgress(step++);
        }
      }
      encoder.finish();
    } finally {
      NIOUtils.closeQuietly(out);
    }

    for(int i=1;i<flame.getFrameCount();i++) {
      if (forceAbort) {
        finished = true;
        return;
      }
      File f = new File(makeFrameName(outFile.getAbsolutePath(), i));
      try {
        if(!f.delete()) {
          f.deleteOnExit();
        }
      }
      finally {
        f.deleteOnExit();
      }
    }

    progressUpdater.updateProgress(totalProgress);
    long t1 = Calendar.getInstance().getTimeInMillis();
    finished = true;
    finishEvent.succeeded((t1 - t0) * 0.001);
  }

  private String makeFrameName(String moveFilename, int frame) {
    String titleStr = flame.getName().trim().replace(" - ","_").replace(' ', '_').replace('\\','_').replace('/','_').replace(':','_');
    if(titleStr.length()>30) {
      titleStr=titleStr.substring(0,30);
    }
    String optionsStr = qualProfile.getQuality()+"_"+resProfile.getWidth()+"_"+resProfile.getHeight();
    File basefn =new File(moveFilename.substring(0, moveFilename.lastIndexOf(".")));
    File tmpFn;
    if(basefn.getParentFile()!=null) {
      tmpFn = new File(basefn.getParent(), "_"+basefn.getName()+"_"+optionsStr+"_"+titleStr);
    }
    else {
      tmpFn = new File("_"+basefn.getAbsolutePath()+"_"+optionsStr+"_"+titleStr);
    }
    return String.format (tmpFn.getAbsolutePath() + "_%04d.png", frame);
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
    renderer = new FlameRenderer(currFlame, prefs, false, false);
    RenderedFlame res = renderer.renderFlame(info);
    if (!forceAbort) {
      new ImageWriter().saveImage(res.getImage(), makeFrameName(outFile.getAbsolutePath(), frame));
    }
  }

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
    renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
    renderer.setProgressUpdater(progressUpdater);
    long t0 = Calendar.getInstance().getTimeInMillis();
    RenderedFlame res = renderer.renderFlame(info);
    if (forceAbort) {
      finished = true;
      return;
    }
    long t1 = Calendar.getInstance().getTimeInMillis();
    new ImageWriter().saveImage(res.getImage(), outFile.getAbsolutePath());
    if (res.getHDRImage() != null) {
      new ImageWriter().saveImage(res.getHDRImage(), Tools.makeHDRFilename(outFile.getAbsolutePath()));
    }
    if (res.getZBuffer() != null) {
      new ImageWriter().saveImage(res.getZBuffer(), Tools.makeZBufferFilename(outFile.getAbsolutePath(), flame.getZBufferFilename()));
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
