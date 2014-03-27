/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.animate;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameWriter;

public class SWFAnimationRenderThread implements Runnable {
  private final SWFAnimationRenderThreadController controller;
  private final String outputFilename;
  private boolean cancelSignalled;
  private FlameMovie flameMovie;
  private Throwable lastError;

  public SWFAnimationRenderThread(SWFAnimationRenderThreadController pController, FlameMovie pAnimation, String pOutputFilename) {
    controller = pController;
    flameMovie = pAnimation;
    outputFilename = pOutputFilename;
  }

  @Override
  public void run() {
    try {
      try {
        cancelSignalled = false;
        lastError = null;
        controller.getProgressUpdater().initProgress(flameMovie.getFrameCount());
        int startFrame = 1;
        int endFrame = flameMovie.getFrameCount();
        for (int i = startFrame; i <= endFrame; i++) {
          if (cancelSignalled) {
            break;
          }
          Flame currFlame = createFlame(i);
          saveFlame(currFlame, i);
          controller.getProgressUpdater().updateProgress(i);
        }
      }
      catch (Throwable ex) {
        lastError = ex;
        throw new RuntimeException(ex);
      }
    }
    finally {
      controller.onRenderFinished();
    }
  }

  private Flame createFlame(int pFrame) throws Exception {
    Flame flame1 = flameMovie.getFlame(pFrame);
    Flame res = flameMovie.createAnimatedFlame(flame1, pFrame);
    return res;
  }

  private void saveFlame(Flame pFlame, int pFrame) throws Exception {
    String filename = outputFilename;
    {
      int pSlash = filename.lastIndexOf("/");
      int pSlash2 = filename.lastIndexOf("\\");
      if (pSlash2 > pSlash) {
        pSlash = pSlash2;
      }
      int pDot = filename.lastIndexOf(".");
      if (pDot > pSlash) {
        filename = filename.substring(0, pDot);
      }
    }
    String hs = String.valueOf(pFrame);
    int length = calcFrameNumberLength();
    while (hs.length() < length) {
      hs = "0" + hs;
    }
    filename += hs + "." + Tools.FILEEXT_FLAME;
    new FlameWriter().writeFlame(pFlame, filename);
  }

  private int calcFrameNumberLength() {
    return Math.max(4, String.valueOf(flameMovie.getFrameCount()).length());
  }

  public void setCancelSignalled(boolean cancelSignalled) {
    this.cancelSignalled = cancelSignalled;
  }

  public Throwable getLastError() {
    return lastError;
  }

  public boolean isCancelSignalled() {
    return cancelSignalled;
  }
}
