/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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

import java.io.File;
import java.util.Calendar;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.CRendererInterface;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.render.RendererType;
import org.jwildfire.io.ImageWriter;

public class RenderMainFlameThread implements Runnable {
  private final Prefs prefs;
  private final Flame flame;
  private final File outFile;
  private final QualityProfile qualProfile;
  private final ResolutionProfile resProfile;
  private final RendererType rendererType;
  private final RenderMainFlameThreadFinishEvent finishEvent;
  private final ProgressUpdater progressUpdater;
  private boolean finished;
  private boolean forceAbort;
  private FlameRenderer renderer;

  public RenderMainFlameThread(Prefs pPrefs, Flame pFlame, File pOutFile, QualityProfile pQualProfile, ResolutionProfile pResProfile, RendererType pRendererType, RenderMainFlameThreadFinishEvent pFinishEvent, ProgressUpdater pProgressUpdater) {
    prefs = pPrefs;
    flame = pFlame.makeCopy();
    outFile = pOutFile;
    qualProfile = pQualProfile;
    resProfile = pResProfile;
    rendererType = pRendererType != null ? pRendererType : RendererType.JAVA;
    finishEvent = pFinishEvent;
    progressUpdater = pProgressUpdater;
  }

  @Override
  public void run() {
    finished = forceAbort = false;
    try {
      int width = resProfile.getWidth();
      int height = resProfile.getHeight();
      RenderInfo info = new RenderInfo(width, height);
      double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
      double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
      flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
      flame.setWidth(info.getImageWidth());
      flame.setHeight(info.getImageHeight());
      boolean renderHDR = qualProfile.isWithHDR();
      info.setRenderHDR(renderHDR);
      boolean renderHDRIntensityMap = qualProfile.isWithHDRIntensityMap();
      info.setRenderHDRIntensityMap(renderHDRIntensityMap);
      flame.setSampleDensity(qualProfile.getQuality());
      long t0, t1;
      switch (rendererType) {
        case JAVA: {
          renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency());
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
            new ImageWriter().saveImage(res.getHDRImage(), outFile.getAbsolutePath() + ".hdr");
          }
          if (res.getHDRIntensityMap() != null) {
            new ImageWriter().saveImage(res.getHDRIntensityMap(), outFile.getAbsolutePath() + ".intensity.hdr");
          }
        }
          break;
        case C32:
        case C64: {
          CRendererInterface cudaRenderer = new CRendererInterface(rendererType, flame.isBGTransparency());
          CRendererInterface.checkFlameForCUDA(flame);
          cudaRenderer.setProgressUpdater(progressUpdater);
          if (info.isRenderHDR()) {
            String hdrFilename = outFile.getAbsolutePath() + ".hdr";
            cudaRenderer.setHDROutputfilename(hdrFilename);
            // do not allocate unnessary memory as the HDR file is completely generated and saved by the external renderer 
            info.setRenderHDR(false);
            info.setRenderHDRIntensityMap(false);
          }
          t0 = System.currentTimeMillis();
          RenderedFlame res = cudaRenderer.renderFlame(info, flame, prefs);
          t1 = System.currentTimeMillis();
          new ImageWriter().saveImage(res.getImage(), outFile.getAbsolutePath());
        }
          break;
        default:
          throw new Exception("Invalid call");
      }
      finished = true;
      finishEvent.succeeded((t1 - t0) * 0.001);
    }
    catch (Throwable ex) {
      finished = true;
      finishEvent.failed(ex);
    }
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
