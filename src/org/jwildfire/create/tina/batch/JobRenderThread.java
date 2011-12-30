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
package org.jwildfire.create.tina.batch;

import java.awt.Graphics;
import java.util.Calendar;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;

public class JobRenderThread implements Runnable {
  private final List<Job> activeJobList;
  private final JobRenderThreadController controller;
  private boolean cancelSignalled;

  public JobRenderThread(JobRenderThreadController pController, List<Job> pActiveJobList) {
    controller = pController;
    activeJobList = pActiveJobList;
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
            int width = controller.getPrefs().getTinaRenderImageWidth();
            int height = controller.getPrefs().getTinaRenderImageHeight();
            SimpleImage img = new SimpleImage(width, height);
            List<Flame> flames = new Flam3Reader().readFlames(job.getFlameFilename());
            Flame flame = flames.get(0);
            double wScl = (double) img.getImageWidth() / (double) flame.getWidth();
            double hScl = (double) img.getImageHeight() / (double) flame.getHeight();
            flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
            flame.setWidth(img.getImageWidth());
            flame.setHeight(img.getImageHeight());

            double oldSampleDensity = flame.getSampleDensity();
            int oldSpatialOversample = flame.getSpatialOversample();
            int oldColorOversample = flame.getColorOversample();
            double oldFilterRadius = flame.getSpatialFilterRadius();
            try {
              flame.setSampleDensity(controller.getPrefs().getTinaRenderHighQuality());
              flame.setSpatialOversample(controller.getPrefs().getTinaRenderHighSpatialOversample());
              flame.setColorOversample(controller.getPrefs().getTinaRenderHighColorOversample());
              flame.setSpatialFilterRadius(controller.getPrefs().getTinaRenderHighSpatialOversample());
              long t0 = Calendar.getInstance().getTimeInMillis();
              FlameRenderer renderer = new FlameRenderer(flame, controller.getPrefs());
              renderer.setProgressUpdater(controller.getJobProgressUpdater());
              renderer.setAffineZStyle(controller.getZStyle());
              renderer.renderFlame(img);
              long t1 = Calendar.getInstance().getTimeInMillis();
              job.setFinished(true);
              job.setElapsedSeconds(((double) (t1 - t0) / 1000.0));
              System.err.println("RENDER TIME: " + job.getElapsedSeconds() + "s");
              new ImageWriter().saveImage(img, job.getImageFilename());
              try {
                {
                  controller.refreshRenderBatchJobsTable();
                  controller.getRenderBatchJobsTable().invalidate();
                  controller.getRenderBatchJobsTable().validate();
                  Graphics g = controller.getRenderBatchJobsTable().getParent().getGraphics();
                  if (g != null) {
                    controller.getRenderBatchJobsTable().getParent().paint(g);
                  }
                }
                {
                  controller.getTotalProgressBar().setValue(controller.getTotalProgressBar().getValue() + 1);
                  controller.getTotalProgressBar().invalidate();
                  controller.getTotalProgressBar().validate();
                  Graphics g = controller.getTotalProgressBar().getGraphics();
                  if (g != null) {
                    controller.getTotalProgressBar().paint(g);
                  }
                }
              }
              catch (Throwable ex) {
                //                ex.printStackTrace();
              }
            }
            finally {
              flame.setSampleDensity(oldSampleDensity);
              flame.setSpatialOversample(oldSpatialOversample);
              flame.setColorOversample(oldColorOversample);
              flame.setSpatialFilterRadius(oldFilterRadius);
            }
          }
          catch (Throwable ex) {
            job.setLastError(ex);
            //            ex.printStackTrace();
          }
        }
        try {
          controller.getTotalProgressBar().setValue(controller.getTotalProgressBar().getMaximum());
          controller.getJobProgressBar().setValue(0);
        }
        catch (Throwable ex) {
          //          ex.printStackTrace();
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
  }

}
