/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2025 Andreas Maschke

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
package org.jwildfire.create.tina.render.gpu.swanrender;

import java.awt.*;
import java.util.Calendar;
import javax.swing.*;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.cli.RenderOptions;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.batch.Job;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.denoiser.AIPostDenoiserFactory;
import org.jwildfire.create.tina.render.denoiser.AIPostDenoiserType;
import org.jwildfire.create.tina.render.gpu.GPURenderer;
import org.jwildfire.create.tina.render.gpu.farender.FARenderResult;
import org.jwildfire.create.tina.render.gpu.farender.FARenderTools;
import org.jwildfire.create.tina.swing.FlameMessageHelper;
import org.jwildfire.create.tina.swing.FlamesGPURenderController;
import org.jwildfire.create.tina.swing.GpuProgressUpdater;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.swing.ErrorHandler;
import org.slf4j.Logger;

public class SwanRendererInterface implements GPURenderer {

  @Override
  public boolean performSelfTests() {
    if(SwanRenderTools.getSwanApiVersion() > 0) {
      return true;
    }
    SwanRenderTools.launchSwan();
    try {
      Thread.currentThread().sleep(2000);
      final int maxRetries = 80;
      int retries = 0;
      while(retries < maxRetries) {
        if(SwanRenderTools.getSwanApiVersion() > 0) {
          System.err.println("Swan API version: " + SwanRenderTools.getSwanApiVersion());
          return true;
        }
        Thread.currentThread().sleep(250);
        retries++;
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return false;
  }

  public SimpleImage renderPreview(Flame flame, int width, int height, Prefs prefs, ProgressUpdater mainProgressUpdater, GpuProgressUpdater gpuProgressUpdater, FlameMessageHelper messageHelper, FlamePanelConfig flamePanelConfig, ErrorHandler errorHandler, Logger logger) {
    try {
      final int PROGRESS_STEPS = 25;
      mainProgressUpdater.initProgress(PROGRESS_STEPS);
      if (gpuProgressUpdater != null) {
        gpuProgressUpdater.signalCancel();
      }
      if (gpuProgressUpdater != null && gpuProgressUpdater.isFinished()) {
        gpuProgressUpdater = null;
      }
      if (gpuProgressUpdater == null) {
        gpuProgressUpdater = new GpuProgressUpdater(mainProgressUpdater, PROGRESS_STEPS);
        new Thread(gpuProgressUpdater).start();
      }
      try {
        long t0 = System.currentTimeMillis();
        SwanApiRenderFlameResultTo renderResult = SwanRenderTools.renderFlame(flame, width, height, prefs.getTinaRenderPreviewQuality());
        SimpleImage img = SwanRenderTools.decodeImageFromResult(renderResult);
        mainProgressUpdater.updateProgress(PROGRESS_STEPS);
        if (!flamePanelConfig.isNoControls() && messageHelper != null) {
          long t1 = System.currentTimeMillis();
          messageHelper.showStatusMessage(
                  flame, "render time (GPU): " + Tools.doubleToString((t1 - t0) * 0.001) + "s");
          //logger.info(openClRenderRes.getMessage());
        }
        return img;
      }
      finally {
        if (gpuProgressUpdater != null) {
          gpuProgressUpdater.signalCancel();
        }
     }
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
      errorHandler.handleError(ex);
    }
    return null;
  }

  @Override
  public void renderFlameFromCli(Flame renderFlame, String flameFilename, RenderOptions renderOptions) throws Exception {
    SwanApiRenderFlameResultTo renderResult = SwanRenderTools.renderFlame(renderFlame, renderOptions.getRenderWidth(), renderOptions.getRenderHeight(), renderOptions.getRenderQuality());
    SimpleImage img = SwanRenderTools.decodeImageFromResult(renderResult);
    new ImageWriter().saveImage(img, Tools.trimFileExt(flameFilename)+".png");
  }

  @Override
  public void renderFlameForEditor(Flame newFlame, String gpuRenderFlameFilename, int width, int height, int quality, boolean zForPass) throws Exception {
    SwanApiRenderFlameResultTo renderResult = SwanRenderTools.renderFlame(newFlame, width, height, quality);
    SimpleImage img = SwanRenderTools.decodeImageFromResult(renderResult);
    new ImageWriter().saveImage(img, Tools.trimFileExt(gpuRenderFlameFilename)+".png");
  }

  @Override
  public void renderFlameForGpuController(Flame currFlame, int width, int height, int quality, JTextArea statsTextArea, JTextArea gpuFlameParamsTextArea, JCheckBox aiPostDenoiserDisableCheckbox, JPanel imageRootPanel, FlamesGPURenderController controller, JLabel gpuRenderInfoLbl, SimpleImage image, boolean keepFlameFileOnError) throws Exception {
    //gpuFlameParamsTextArea.setText(gpuFlameParams);
    long t0 = System.currentTimeMillis();
    SwanApiRenderFlameResultTo renderResult = SwanRenderTools.renderFlame(currFlame, width, height, quality);
    SimpleImage img = SwanRenderTools.decodeImageFromResult(renderResult);
    long t1 = System.currentTimeMillis();
    if (img != null && img.getImageWidth() > 0 && img.getImageHeight() > 0) {
      if (img.getImageWidth() == image.getImageWidth()
              && img.getImageHeight() == image.getImageHeight()) {
        image.setBufferedImage(
                img.getBufferedImg(), img.getImageWidth(), img.getImageHeight());
        imageRootPanel.repaint();
        gpuRenderInfoLbl.setText(
                "Elapsed: " + Tools.doubleToString((t1 - t0) / 1000.0) + "s");
      } else {
        throw new Exception(
                "Invalid image size <"
                        + img.getImageWidth()
                        + "x"
                        + img.getImageHeight()
                        + ">");
      }
    }
  }

  @Override
  public void renderFlameForBatch(Flame newFlame, String openClFlameFilename, int width, int height, int quality, boolean zForPass, boolean disablePostDenoiser, boolean updateProgress, Job job) throws Exception {
    long t0 = Calendar.getInstance().getTimeInMillis();
    SwanApiRenderFlameResultTo renderResult = SwanRenderTools.renderFlame(newFlame, width, height, quality);
    SimpleImage img = SwanRenderTools.decodeImageFromResult(renderResult);
    new ImageWriter().saveImage(img, Tools.trimFileExt(openClFlameFilename)+".png");
    long t1 = Calendar.getInstance().getTimeInMillis();
    if (updateProgress) {
       job.setElapsedSeconds(((double) (t1 - t0) / 1000.0));
    }
  }
}
