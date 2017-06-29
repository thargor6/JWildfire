/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen.render;

import java.util.Calendar;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.meshgen.MeshGenGenerateThreadFinishEvent;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;

public abstract class MeshGenRenderThread implements Runnable {
  protected boolean finished;
  protected FlameRenderer renderer;
  protected final Prefs prefs;
  protected final Flame flame;
  protected final String outFilePattern;
  protected final MeshGenGenerateThreadFinishEvent finishEvent;
  protected final ProgressUpdater progressUpdater;
  protected int renderWidth, renderHeight;
  protected int quality;
  protected double zmin, zmax;

  public MeshGenRenderThread(Prefs pPrefs, Flame pFlame, String pOutFilePattern, MeshGenGenerateThreadFinishEvent pFinishEvent, ProgressUpdater pProgressUpdater, int pRenderWidth, int pRenderHeight, int pQuality,
      double pZMin, double pZMax) {
    prefs = pPrefs;
    flame = pFlame.makeCopy();
    outFilePattern = pOutFilePattern;
    finishEvent = pFinishEvent;
    renderWidth = pRenderWidth;
    renderHeight = pRenderHeight;
    quality = pQuality;
    progressUpdater = pProgressUpdater;
    zmin = pZMin;
    zmax = pZMax;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setForceAbort() {
    if (renderer != null) {
      renderer.cancel();
    }
  }

  @Override
  public void run() {
    finished = false;
    try {
      long t0 = Calendar.getInstance().getTimeInMillis();

      doRender();

      long t1 = Calendar.getInstance().getTimeInMillis();

      finished = true;
      finishEvent.succeeded((t1 - t0) * 0.001);
    }
    catch (Throwable ex) {
      finished = true;
      finishEvent.failed(ex);
    }
  }

  protected abstract void doRender() throws Exception;

}
