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
import org.jwildfire.create.tina.render.gpu.farender.FARendererInterface;
import org.jwildfire.create.tina.render.gpu.swanrender.SwanRendererInterface;

public class GPURendererFactory {
  private static Boolean selfTestResult = null;

  public static boolean isAvailable() {
    if (selfTestResult == null) {
      Prefs prefs = Prefs.getPrefs();
      if (prefs.isUseSwanForGpuRendering()) {
        try {
          GPURenderer swanRenderer = new SwanRendererInterface();
          selfTestResult = Boolean.valueOf(swanRenderer.performSelfTests());
          if (selfTestResult.booleanValue()) {
            gpuRenderer = swanRenderer;
            return true;
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      // fallback to FARendererInterface
      try {
        GPURenderer faRenderer = new FARendererInterface();
        selfTestResult = Boolean.valueOf(faRenderer.performSelfTests());
        if (selfTestResult.booleanValue()) {
          gpuRenderer = faRenderer;
          return true;
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        selfTestResult = Boolean.FALSE;
      }
    }
    return selfTestResult.booleanValue();
  }

  private static GPURenderer gpuRenderer = null;

  public static GPURenderer getGPURenderer() {
    return isAvailable() ? gpuRenderer : null;
  }
}
