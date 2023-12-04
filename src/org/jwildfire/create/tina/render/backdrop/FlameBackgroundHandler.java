/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2023 Andreas Maschke

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
package org.jwildfire.create.tina.render.backdrop;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.ThreadTools;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.BGColorType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.LogDensityFilter;
import org.jwildfire.create.tina.render.LogDensityPoint;
import org.jwildfire.create.tina.render.image.AbstractImageRenderThread;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.image.SimpleImage;

import java.util.ArrayList;
import java.util.List;

public class FlameBackgroundHandler {
  private final Flame flame;

  public FlameBackgroundHandler(Flame flame) {
    super();
    this.flame = flame;
  }

  public void fillBackground(SimpleImage image) {
    // fast skip
    if(!flame.hasBGTransforms()) {
      if (BGColorType.SINGLE_COLOR.equals(flame.getBgColorType())) {
        if (!(flame.getBgColorRed() > 0 || flame.getBgColorGreen() > 0 || flame.getBgColorBlue() > 0)) {
          image.fillBackground(0, 0, 0);
          return;
        }
      } else if (BGColorType.GRADIENT_2X2.equals(flame.getBgColorType())) {
        if (!(flame.getBgColorULRed() > 0 || flame.getBgColorULGreen() > 0 || flame.getBgColorULBlue() > 0 ||
                flame.getBgColorURRed() > 0 || flame.getBgColorURGreen() > 0 || flame.getBgColorURBlue() > 0 ||
                flame.getBgColorLLRed() > 0 || flame.getBgColorLLGreen() > 0 || flame.getBgColorLLBlue() > 0 ||
                flame.getBgColorLRRed() > 0 || flame.getBgColorLRGreen() > 0 || flame.getBgColorLRBlue() > 0)) {
          image.fillBackground(0, 0, 0);
          return;
        }
      } else if (BGColorType.GRADIENT_2X2_C.equals(flame.getBgColorType())) {
        if (!(flame.getBgColorULRed() > 0 || flame.getBgColorULGreen() > 0 || flame.getBgColorULBlue() > 0 ||
                flame.getBgColorURRed() > 0 || flame.getBgColorURGreen() > 0 || flame.getBgColorURBlue() > 0 ||
                flame.getBgColorLLRed() > 0 || flame.getBgColorLLGreen() > 0 || flame.getBgColorLLBlue() > 0 ||
                flame.getBgColorLRRed() > 0 || flame.getBgColorLRGreen() > 0 || flame.getBgColorLRBlue() > 0 ||
                flame.getBgColorCCRed() > 0 || flame.getBgColorCCGreen() > 0 || flame.getBgColorCCBlue() > 0)) {
          image.fillBackground(0, 0, 0);
          return;
        }
      }
    }

    int threadCount = Prefs.getPrefs().getTinaRenderThreads();
    if (threadCount < 1 || image.getImageHeight() < 8 * threadCount) {
      threadCount = 1;
    }
    int rowsPerThread = image.getImageHeight() / threadCount;
    List<FlameBackgroundRenderThread> threads = new ArrayList<>();
    for (int i = 0; i < threadCount; i++) {
      int startRow = i * rowsPerThread;
      int endRow = i < threadCount - 1 ? startRow + rowsPerThread : image.getImageHeight();
      FlameBackgroundRenderThread thread = new FlameBackgroundRenderThread(flame, startRow, endRow, image, threads.size());
      threads.add(thread);
      if (threadCount > 1) {
        Thread t = new Thread(thread);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
      }
      else {
        thread.run();
      }
    }
    ThreadTools.waitForThreads(threadCount, threads);
  }


}
