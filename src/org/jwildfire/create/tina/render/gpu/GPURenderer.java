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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.swing.FlameMessageHelper;
import org.jwildfire.create.tina.swing.GpuProgressUpdater;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.slf4j.Logger;

public interface GPURenderer {
  boolean performSelfTests();
  SimpleImage renderPreview(Flame flame, int width, int height, Prefs prefs, ProgressUpdater mainProgressUpdater, GpuProgressUpdater gpuProgressUpdater, FlameMessageHelper messageHelper, FlamePanelConfig flamePanelConfig, ErrorHandler errorHandler, Logger logger);
}