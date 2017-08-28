/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.meshgen.MeshGenGenerateThreadFinishEvent;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.SliceRenderInfo;

public class RenderSlicesThread extends MeshGenRenderThread {
  public static final double DFLT_ANTIALIAS_RADIUS = 0.16;
  public static final double DFLT_ANTIALIAS_AMOUNT = 0.5;
  private int slicesCount, slicesPerRender;

  public RenderSlicesThread(Prefs pPrefs, Flame pFlame, String pOutFilePattern, MeshGenGenerateThreadFinishEvent pFinishEvent, ProgressUpdater pProgressUpdater, int pRenderWidth, int pRenderHeight, int pSlicesCount, int pSlicesPerRender, int pQuality,
      double pZMin, double pZMax) {
    super(pPrefs, pFlame, pOutFilePattern, pFinishEvent, pProgressUpdater, pRenderWidth, pRenderHeight, pQuality,
        pZMin, pZMax);
    slicesCount = pSlicesCount;
    slicesPerRender = pSlicesPerRender;
  }

  @Override
  protected void doRender() {
    renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
    renderer.setProgressUpdater(progressUpdater);

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

    renderer.setProgressUpdater(progressUpdater);
    SliceRenderInfo renderInfo = new SliceRenderInfo(renderWidth, renderHeight, RenderMode.PRODUCTION, slicesCount, zmin, zmax, slicesPerRender);

    renderer.renderSlices(renderInfo, outFilePattern);
  }

}
