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
package org.jwildfire.create.tina.render.gpu;

import org.jwildfire.base.Prefs;
import org.jwildfire.cli.RenderOptions;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.batch.Job;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.swing.FlameMessageHelper;
import org.jwildfire.create.tina.swing.FlamesGPURenderController;
import org.jwildfire.create.tina.swing.GpuProgressUpdater;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.slf4j.Logger;

import javax.swing.*;

public interface GPURenderer {
  boolean performSelfTests();
  SimpleImage renderPreview(Flame flame, int width, int height, Prefs prefs, ProgressUpdater mainProgressUpdater, GpuProgressUpdater gpuProgressUpdater, FlameMessageHelper messageHelper, FlamePanelConfig flamePanelConfig, ErrorHandler errorHandler, Logger logger);
  void renderFlameFromCli(Flame renderFlame, String flameFilename, RenderOptions renderOptions)  throws Exception;
  void renderFlameForEditor(Flame newFlame, String gpuRenderFlameFilename, int width, int height, int quality, boolean zForPass) throws Exception;
  void renderFlameForGpuController(Flame currFlame, int width, int height, int quality, JTextArea statsTextArea, JTextArea gpuFlameParamsTextArea, JCheckBox aiPostDenoiserDisableCheckbox, JPanel imageRootPanel, FlamesGPURenderController controller, JLabel gpuRenderInfoLbl, SimpleImage image, boolean keepFlameFileOnError) throws Exception;
  void renderFlameForBatch(Flame newFlame, String openClFlameFilename, int width, int height, int quality, boolean zForPass, boolean disablePostDenoiser, boolean updateProgress, Job job) throws Exception;
}