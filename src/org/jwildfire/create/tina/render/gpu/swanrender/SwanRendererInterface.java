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
import java.io.File;
import java.util.Collections;
import javax.swing.*;
import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.render.gpu.GPURenderer;
import org.jwildfire.create.tina.render.gpu.farender.FAFlameWriter;
import org.jwildfire.create.tina.render.gpu.farender.FARenderResult;
import org.jwildfire.create.tina.render.gpu.farender.FARenderTools;
import org.jwildfire.create.tina.swing.FileDialogTools;

public class SwanRendererInterface implements GPURenderer {

  @Override
  public boolean performSelfTests() {
    if(SwanRenderTools.getSwanApiVersion() > 0) {
      SwanRenderTools.renderFlame();
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
          SwanRenderTools.renderFlame();
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
}
