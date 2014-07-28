/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen;

import java.io.File;
import java.util.Calendar;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.SliceRenderInfo;

public class MeshGenGenerateThread implements Runnable {
  public static final double DFLT_ANTIALIAS_RADIUS = 0.36;
  public static final double DFLT_ANTIALIAS_AMOUNT = 0.75;
  private final Prefs prefs;
  private final Flame flame;
  private final File outFile;
  private final MeshGenGenerateThreadFinishEvent finishEvent;
  private final ProgressUpdater progressUpdater;
  private boolean finished;
  private int renderWidth, renderHeight;
  private int slicesCount, slicesPerRender;
  private int quality;
  private double zmin, zmax;
  private FlameRenderer renderer;

  public MeshGenGenerateThread(Prefs pPrefs, Flame pFlame, File pOutFile, MeshGenGenerateThreadFinishEvent pFinishEvent, ProgressUpdater pProgressUpdater, int pRenderWidth, int pRenderHeight, int pSlicesCount, int pSlicesPerRender, int pQuality,
      double pZMin, double pZMax) {
    prefs = pPrefs;
    flame = pFlame.makeCopy();
    outFile = pOutFile;
    finishEvent = pFinishEvent;
    renderWidth = pRenderWidth;
    renderHeight = pRenderHeight;
    slicesCount = pSlicesCount;
    slicesPerRender = pSlicesPerRender;
    quality = pQuality;
    progressUpdater = pProgressUpdater;
    zmin = pZMin;
    zmax = pZMax;
  }

  @Override
  public void run() {
    finished = false;
    try {
      long t0, t1;
      renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
      renderer.setProgressUpdater(progressUpdater);
      t0 = Calendar.getInstance().getTimeInMillis();

      renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);

      int width = renderWidth;
      int height = renderHeight;
      RenderInfo info = new RenderInfo(width, height, RenderMode.PRODUCTION);
      double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
      double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
      flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
      flame.setWidth(info.getImageWidth());
      flame.setHeight(info.getImageHeight());
      flame.setSampleDensity(quality);

      flame.setAntialiasRadius(DFLT_ANTIALIAS_RADIUS);
      flame.setAntialiasAmount(DFLT_ANTIALIAS_AMOUNT);

      renderer.setProgressUpdater(progressUpdater);
      SliceRenderInfo renderInfo = new SliceRenderInfo(renderWidth, renderHeight, RenderMode.PRODUCTION, slicesCount, zmin, zmax, slicesPerRender);

      renderer.renderSlices(renderInfo, createFilenamePattern());

      t1 = Calendar.getInstance().getTimeInMillis();

      finished = true;
      finishEvent.succeeded((t1 - t0) * 0.001);
    }
    catch (Throwable ex) {
      finished = true;
      finishEvent.failed(ex);
    }
  }

  private String createFilenamePattern() {
    String res = outFile.getName();
    int p = res.lastIndexOf(".");
    if (p >= 0) {
      String ext = res.substring(p + 1, res.length());
      if (Tools.FILEEXT_PNG.equalsIgnoreCase(ext)) {
        res = res.substring(0, p);
      }
    }
    if (outFile.getParentFile() != null) {
      res = new File(outFile.getParentFile(), res).getAbsolutePath();
    }
    res += "%04d." + Tools.FILEEXT_PNG;
    return res;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setForceAbort() {
    if (renderer != null) {
      renderer.cancel();
    }
  }

}
