/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2022 Andreas Maschke

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

package org.jwildfire.create.tina.browser;

import java.util.List;

import javax.swing.JPanel;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.SimpleImage;

public class CacheRendererThread implements Runnable {
  private final Prefs prefs;
  private final RenderCache renderCache;
  private final List<RenderJobInfo> jobs;
  private boolean done;
  private boolean cancelSignalled;

  public CacheRendererThread(Prefs pPrefs, RenderCache pRenderCache, List<RenderJobInfo> pJobs) {
    prefs = pPrefs;
    renderCache = pRenderCache;
    jobs = pJobs;
  }

  @Override
  public void run() {
    done = false;
    cancelSignalled = false;
    try {
      for (RenderJobInfo job : jobs) {
        try {
          if (cancelSignalled) {
            break;
          }
          SimpleImage img = renderFlame(job.getFlame(), job.getRenderWidth(), job.getRenderHeight());
          renderCache.putImage(job.getFlame(), img, job.getRenderWidth(), job.getRenderHeight());
          job.getDestPnl().setImage(img);
          job.getDestPnl().setLocation(job.getLocationX(), job.getLocationY());
          redraw((JPanel) job.getDestPnl());
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    finally {
      done = true;
    }
  }

  private void redraw(JPanel pPnl) {
    pPnl.getParent().validate();
    pPnl.repaint();
  }

  public boolean isDone() {
    return done;
  }

  public void signalCancel() {
    cancelSignalled = true;
  }

  private SimpleImage renderFlame(FlameFlatNode pNode, int pImgWidth, int pImgHeight) {
    List<Flame> flames = new FlameReader(prefs).readFlames(pNode.getFilename());
    if (flames.size() > 0) {
      Flame renderFlame = flames.get(0);
      RenderInfo info = new RenderInfo(pImgWidth, pImgHeight, RenderMode.PREVIEW);
      double wScl = (double) info.getImageWidth() / (double) renderFlame.getWidth();
      double hScl = (double) info.getImageHeight() / (double) renderFlame.getHeight();
      renderFlame.setPixelsPerUnitScale((wScl + hScl) * 0.5);
      renderFlame.setSampleDensity(prefs.getTinaRenderPreviewQuality() / 3.0);
      FlameRenderer renderer = new FlameRenderer(renderFlame, prefs, false, true);
      RenderedFlame renderRes = renderer.renderFlame(info);
      return renderRes.getImage();
    }
    return null;
  }

}
