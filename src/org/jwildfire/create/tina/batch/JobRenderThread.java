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
package org.jwildfire.create.tina.batch;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.faclrender.FACLFlameWriter;
import org.jwildfire.create.tina.faclrender.FACLRenderResult;
import org.jwildfire.create.tina.faclrender.FACLRenderTools;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.io.ImageWriter;

public class JobRenderThread implements Runnable {
  private final List<Job> activeJobList;
  private final JobRenderThreadController controller;
  private final QualityProfile qualityProfile;
  private final ResolutionProfile resolutionProfile;
  private boolean cancelSignalled;
  private final boolean doOverwriteExisting;
  private FlameRenderer renderer;
  private final boolean useOpenCl;

  public JobRenderThread(JobRenderThreadController pController, List<Job> pActiveJobList, ResolutionProfile pResolutionProfile, QualityProfile pQualityProfile, boolean pDoOverwriteExisting, boolean pUseOpenCl) {
    controller = pController;
    activeJobList = pActiveJobList;
    resolutionProfile = pResolutionProfile;
    qualityProfile = pQualityProfile;
    doOverwriteExisting = pDoOverwriteExisting;
    useOpenCl = pUseOpenCl;
  }

  @Override
  public void run() {
    try {
      try {
        cancelSignalled = false;
        controller.getTotalProgressBar().setMinimum(0);
        controller.getTotalProgressBar().setValue(0);
        controller.getTotalProgressBar().setMaximum(activeJobList.size());

        for (Job job : activeJobList) {
          if (cancelSignalled) {
            break;
          }
          try {
            int width, height;
            if (job.getCustomWidth() > 0 && job.getCustomHeight() > 0) {
              width = job.getCustomWidth();
              height = job.getCustomHeight();
            }
            else {
              width = resolutionProfile.getWidth();
              height = resolutionProfile.getHeight();
            }
            RenderInfo info = new RenderInfo(width, height, RenderMode.PRODUCTION);
            info.setRenderHDR(qualityProfile.isWithHDR());
            info.setRenderZBuffer(qualityProfile.isWithZBuffer());
            List<Flame> flames = new FlameReader(Prefs.getPrefs()).readFlames(job.getFlameFilename());
            Flame flame = flames.get(0);
            String primaryFilename = job.getImageFilename(flame.getStereo3dMode());
            double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
            double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
            flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
            flame.setWidth(info.getImageWidth());
            flame.setHeight(info.getImageHeight());
            double oldSampleDensity = flame.getSampleDensity();
            double oldFilterRadius = flame.getSpatialFilterRadius();
            try {
              if (!doOverwriteExisting && new File(primaryFilename).exists()) {
                controller.getJobProgressUpdater().initProgress(1);
                controller.getJobProgressUpdater().updateProgress(1);
              }
              else {
                if (useOpenCl) {
                  String openClFlameFilename = Tools.trimFileExt(job.getFlameFilename()) + ".flam3";
                  try {
                    Flame newFlame = AnimationService.evalMotionCurves(flame.makeCopy(), flame.getFrame());
                    new FACLFlameWriter().writeFlame(newFlame, openClFlameFilename);
                    long t0 = Calendar.getInstance().getTimeInMillis();
                    FACLRenderResult openClRenderRes = FACLRenderTools.invokeFACLRender(openClFlameFilename, width, height, qualityProfile.getQuality());
                    long t1 = Calendar.getInstance().getTimeInMillis();
                    if (openClRenderRes.getReturnCode() != 0) {
                      throw new Exception(openClRenderRes.getMessage());
                    }
                    else {
                      job.setElapsedSeconds(((double) (t1 - t0) / 1000.0));
                    }
                  }
                  finally {
                    if (!new File(openClFlameFilename).delete()) {
                      new File(openClFlameFilename).deleteOnExit();
                    }
                  }
                }
                else {
                  flame.setSampleDensity(job.getCustomQuality() > 0 ? job.getCustomQuality() : qualityProfile.getQuality());
                  renderer = new FlameRenderer(flame, Prefs.getPrefs(), flame.isBGTransparency(), false);
                  renderer.setProgressUpdater(controller.getJobProgressUpdater());
                  long t0 = Calendar.getInstance().getTimeInMillis();
                  RenderedFlame res = renderer.renderFlame(info);
                  if (!cancelSignalled) {
                    long t1 = Calendar.getInstance().getTimeInMillis();
                    job.setElapsedSeconds(((double) (t1 - t0) / 1000.0));
                    new ImageWriter().saveImage(res.getImage(), primaryFilename);
                    if (res.getHDRImage() != null) {
                      new ImageWriter().saveImage(res.getHDRImage(), Tools.makeHDRFilename(job.getImageFilename(flame.getStereo3dMode())));
                    }
                    if (res.getZBuffer() != null) {
                      new ImageWriter().saveImage(res.getZBuffer(), Tools.makeZBufferFilename(job.getImageFilename(flame.getStereo3dMode()), flame.getZBufferFilename()));
                    }
                  }
                }
              }
              if (!cancelSignalled) {
                job.setFinished(true);
              }
              if (Prefs.getPrefs().isTinaFreeCacheInBatchRenderer()) {
                RessourceManager.clearAll();
                System.gc();
              }
              try {
                {
                  controller.refreshRenderBatchJobsTable();
                  controller.getRenderBatchJobsTable().invalidate();
                  controller.getRenderBatchJobsTable().validate();
                  //                  Graphics g = controller.getRenderBatchJobsTable().getParent().getGraphics();
                  //                  if (g != null) {
                  //                    controller.getRenderBatchJobsTable().getParent().paint(g);
                  //                  }
                }
                {
                  controller.getTotalProgressBar().setValue(controller.getTotalProgressBar().getValue() + 1);
                  controller.getTotalProgressBar().invalidate();
                  controller.getTotalProgressBar().validate();
                  //                  Graphics g = controller.getTotalProgressBar().getGraphics();
                  //                  if (g != null) {
                  //                    controller.getTotalProgressBar().paint(g);
                  //                  }
                }
              }
              catch (Throwable ex) {
                // ex.printStackTrace();
              }
            }
            finally {
              flame.setSampleDensity(oldSampleDensity);
              flame.setSpatialFilterRadius(oldFilterRadius);
            }
          }
          catch (Throwable ex) {
            job.setLastError(ex);
            // ex.printStackTrace();
          }
        }
        try {
          controller.getTotalProgressBar().setValue(controller.getTotalProgressBar().getMaximum());
          controller.getJobProgressBar().setValue(0);
        }
        catch (Throwable ex) {
          // ex.printStackTrace();
        }
      }
      catch (Throwable ex) {
        throw new RuntimeException(ex);
      }
    }
    finally {
      controller.onJobFinished();
    }
  }

  public void setCancelSignalled(boolean cancelSignalled) {
    this.cancelSignalled = cancelSignalled;
    if (cancelSignalled && renderer != null) {
      try {
        renderer.cancel();
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

}
