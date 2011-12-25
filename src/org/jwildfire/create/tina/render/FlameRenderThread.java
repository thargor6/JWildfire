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
import org.jwildfire.create.tina.variation.XFormTransformationContextImpl;

public class FlameRenderThread implements Runnable {
  private final FlameRenderer renderer;
  private final Flame flame;
  private final long samples;
  private volatile long currSample;
  private final AffineZStyle affineZStyle;

  private boolean finished;

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
        switch (flame.getShadingInfo().getShading()) {
          case FLAT:
            iterate_flat();
            break;
          case PSEUDO3D:
            iterate_pseudo3D();
            break;
          default:
            throw new IllegalArgumentException(flame.getShadingInfo().getShading().toString());
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
        throw new RuntimeException(ex);
      }
    }
    finally {
      finished = true;
    }
  }

  private void iterate_flat() {
    XFormTransformationContextImpl ctx = new XFormTransformationContextImpl(renderer);
    XYZPoint affineT = new XYZPoint(); // affine part of the transformation
    XYZPoint varT = new XYZPoint(); // complete transformation
    XYZPoint p = new XYZPoint();
    XYZPoint q;
    p.x = 2.0 * renderer.random.random() - 1.0;
    p.y = 2.0 * renderer.random.random() - 1.0;
    p.z = 2.0 * renderer.random.random() - 1.0;
    p.color = renderer.random.random();

    XForm xf = flame.getXForms().get(0);
    xf.transformPoint(ctx, affineT, varT, p, p, affineZStyle);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
    }

    for (long i = 0; i < samples; i++) {
      if (i % 100 == 0) {
        currSample = i;
      }
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      xf.transformPoint(ctx, affineT, varT, p, p, affineZStyle);

      if (xf.getDrawMode() == DrawMode.HIDDEN)
        continue;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (renderer.random.random() > xf.getOpacity()))
        continue;

      XForm finalXForm = flame.getFinalXForm();
      double px, py;
      if (finalXForm != null) {
        q = new XYZPoint();
        finalXForm.transformPoint(ctx, affineT, varT, p, q, affineZStyle);
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

      int xIdx = (int) (renderer.bws * px + 0.5);
      int yIdx = (int) (renderer.bhs * py + 0.5);
      int colorIdx = (int) (p.color * renderer.paletteIdxScl + 0.5);
      RasterPoint rp = renderer.raster[yIdx][xIdx];
      RenderColor color = renderer.colorMap[colorIdx];

      rp.red += color.red;
      rp.green += color.green;
      rp.blue += color.blue;
      rp.count++;

      //      if (i < samples / 3) {
      //        if (xIdx > 1) {
      //          rp = renderer.raster[yIdx][xIdx - 1];
      //          rp.red += color.red / 2;
      //          rp.green += color.green / 2;
      //          rp.blue += color.blue / 2;
      //          rp.count++;
      //        }
      //        if (xIdx < renderer.rasterWidth - 1) {
      //          rp = renderer.raster[yIdx][xIdx + 1];
      //          rp.red += color.red / 2;
      //          rp.green += color.green / 2;
      //          rp.blue += color.blue / 2;
      //          rp.count++;
      //        }
      //        if (yIdx > 1) {
      //          rp = renderer.raster[yIdx - 1][xIdx];
      //          rp.red += color.red / 2;
      //          rp.green += color.green / 2;
      //          rp.blue += color.blue / 2;
      //          rp.count++;
      //        }
      //        if (yIdx < renderer.rasterHeight - 1) {
      //          rp = renderer.raster[yIdx + 1][xIdx];
      //          rp.red += color.red / 2;
      //          rp.green += color.green / 2;
      //          rp.blue += color.blue / 2;
      //          rp.count++;
      //        }
      //
      //        if (xIdx > 1 && yIdx > 1) {
      //          rp = renderer.raster[yIdx - 1][xIdx - 1];
      //          rp.red += color.red / 4;
      //          rp.green += color.green / 4;
      //          rp.blue += color.blue / 4;
      //          rp.count++;
      //        }
      //        if (xIdx > 1 && yIdx < renderer.rasterHeight - 1) {
      //          rp = renderer.raster[yIdx + 1][xIdx - 1];
      //          rp.red += color.red / 4;
      //          rp.green += color.green / 4;
      //          rp.blue += color.blue / 4;
      //          rp.count++;
      //        }
      //        if (xIdx < renderer.rasterWidth - 1 && yIdx > 1) {
      //          rp = renderer.raster[yIdx - 1][xIdx + 1];
      //          rp.red += color.red / 4;
      //          rp.green += color.green / 4;
      //          rp.blue += color.blue / 4;
      //          rp.count++;
      //        }
      //        if (xIdx < renderer.rasterWidth - 1 && yIdx < renderer.rasterHeight - 1) {
      //          rp = renderer.raster[yIdx + 1][xIdx + 1];
      //          rp.red += color.red / 4;
      //          rp.green += color.green / 4;
      //          rp.blue += color.blue / 4;
      //          rp.count++;
      //        }
      //      }
    }
  }

  private void iterate_pseudo3D() {
    XFormTransformationContextImpl ctx = new XFormTransformationContextImpl(renderer);
    Pseudo3DShader shader = new Pseudo3DShader(flame.getShadingInfo());
    shader.init();

    XYZPoint[] affineT = new XYZPoint[3]; // affine part of the transformation
    for (int i = 0; i < affineT.length; i++) {
      affineT[i] = new XYZPoint();
    }
    XYZPoint[] varT = new XYZPoint[3]; // complete transformation
    for (int i = 0; i < varT.length; i++) {
      varT[i] = new XYZPoint();
    }
    XYZPoint[] p = new XYZPoint[3];
    for (int i = 0; i < p.length; i++) {
      p[i] = new XYZPoint();
    }
    XYZPoint r = new XYZPoint();

    p[0].x = 2.0 * renderer.random.random() - 1.0;
    p[0].y = 2.0 * renderer.random.random() - 1.0;
    p[0].z = 2.0 * renderer.random.random() - 1.0;
    p[0].color = renderer.random.random();

    shader.distributeInitialPoints(p);
    XYZPoint[] q = new XYZPoint[3];
    for (int i = 0; i < q.length; i++) {
      q[i] = new XYZPoint();
    }

    XForm xf = flame.getXForms().get(0);
    xf.transformPoints(ctx, affineT, varT, p, p, affineZStyle);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
    }

    for (long i = 0; i < samples; i++) {
      if (i % 100 == 0) {
        currSample = i;
      }
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      xf.transformPoints(ctx, affineT, varT, p, p, affineZStyle);
      if (xf.getDrawMode() == DrawMode.HIDDEN)
        continue;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (renderer.random.random() > xf.getOpacity()))
        continue;

      XForm finalXForm = flame.getFinalXForm();
      double px = 0.0, py = 0.0;
      if (finalXForm != null) {
        for (int pIdx = 0; pIdx < p.length; pIdx++) {
          q[pIdx] = new XYZPoint();
        }
        finalXForm.transformPoints(ctx, affineT, varT, p, q, affineZStyle);
        r.assign(q[0]);
        renderer.project(flame, r);
        px = r.x * renderer.cosa + r.y * renderer.sina + renderer.rcX;
        py = r.y * renderer.cosa - r.x * renderer.sina + renderer.rcY;
      }
      else {
        for (int pIdx = 0; pIdx < p.length; pIdx++) {
          q[pIdx] = new XYZPoint();
          q[pIdx].assign(p[pIdx]);
        }
        r.assign(q[0]);
        renderer.project(flame, r);
        px = r.x * renderer.cosa + r.y * renderer.sina + renderer.rcX;
        py = r.y * renderer.cosa - r.x * renderer.sina + renderer.rcY;
      }

      if ((px < 0) || (px > renderer.camW))
        continue;
      if ((py < 0) || (py > renderer.camH))
        continue;

      RasterPoint rp = renderer.raster[(int) (renderer.bhs * py + 0.5)][(int) (renderer.bws * px + 0.5)];
      RenderColor color = renderer.colorMap[(int) (p[0].color * renderer.paletteIdxScl + 0.5)];

      RenderColor shadedColor = shader.calculateColor(q, color);

      rp.red += shadedColor.red;
      rp.green += shadedColor.green;
      rp.blue += shadedColor.blue;
      rp.count++;

    }
  }

  public long getCurrSample() {
    return currSample;
  }

  public boolean isFinished() {
    return finished;
  }

}
