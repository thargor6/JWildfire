/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.render;

import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;

public class FlameRenderThread implements Runnable {
  private final FlameRenderer renderer;
  private final Flame flame;
  private final long samples;
  private final AffineZStyle affineZStyle;
  private boolean finished = true;

  public FlameRenderThread(FlameRenderer pRenderer, Flame pFlame, long pSamples, AffineZStyle pAffineZStyle) {
    renderer = pRenderer;
    flame = pFlame;
    samples = pSamples;
    affineZStyle = pAffineZStyle;
  }

  @Override
  public void run() {
    finished = false;
    try {
      try {
        iterate();
      }
      catch (RuntimeException ex) {
        ex.printStackTrace();
        throw ex;
      }
    }
    finally {
      finished = true;
    }
  }

  private void iterate() {
    XYZPoint affineT = new XYZPoint(); // affine part of the transformation
    XYZPoint varT = new XYZPoint(); // complete transformation
    XYZPoint p = new XYZPoint();
    XYZPoint q;
    p.x = 2.0 * renderer.random.random() - 1.0;
    p.y = 2.0 * renderer.random.random() - 1.0;
    p.z = 2.0 * renderer.random.random() - 1.0;
    p.color = renderer.random.random();

    XForm xf = flame.getXForms().get(0);
    xf.transformPoint(renderer, affineT, varT, p, p, affineZStyle);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
    }

    for (long i = 0; i < samples; i++) {
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      xf.transformPoint(renderer, affineT, varT, p, p, affineZStyle);

      if (xf.getDrawMode() == DrawMode.HIDDEN)
        continue;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (renderer.random.random() > xf.getOpacity()))
        continue;

      XForm finalXForm = flame.getFinalXForm();
      double px, py;
      if (finalXForm != null) {
        q = new XYZPoint();
        finalXForm.transformPoint(renderer, affineT, varT, p, q, affineZStyle);
        renderer.project(flame, q);
        px = q.x * renderer.cosa + q.y * renderer.sina + renderer.rcX;
        if ((px < 0) || (px > renderer.camW))
          continue;
        py = q.y * renderer.cosa - q.x * renderer.sina + renderer.rcY;
        if ((py < 0) || (py > renderer.camH))
          continue;
      }
      else {
        q = new XYZPoint();
        q.assign(p);
        renderer.project(flame, q);
        px = q.x * renderer.cosa + q.y * renderer.sina + renderer.rcX;
        if ((px < 0) || (px > renderer.camW))
          continue;
        py = q.y * renderer.cosa - q.x * renderer.sina + renderer.rcY;
        if ((py < 0) || (py > renderer.camH))
          continue;
      }

      RasterPoint rp = renderer.raster[(int) (renderer.bhs * py + 0.5)][(int) (renderer.bws * px + 0.5)];
      RenderColor color = renderer.colorMap[(int) (p.color * renderer.paletteIdxScl + 0.5)];

      rp.red += color.red;
      rp.green += color.green;
      rp.blue += color.blue;
      rp.count++;
    }
  }

  public boolean isFinished() {
    return finished;
  }

}
