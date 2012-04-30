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

import java.util.List;

import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public final class FlameRenderThread implements Runnable {
  private final FlameRenderer renderer;
  private final Flame flame;
  private final long samples;
  private volatile long currSample;
  private SampleTonemapper tonemapper;
  private boolean forceAbort;
  private boolean finished;

  public FlameRenderThread(FlameRenderer pRenderer, Flame pFlame, long pSamples) {
    renderer = pRenderer;
    flame = pFlame;
    samples = pSamples;
  }

  @Override
  public void run() {
    finished = forceAbort = false;
    try {
      try {
        switch (flame.getShadingInfo().getShading()) {
          case FLAT:
            iterate_flat();
            break;
          case BLUR:
            iterate_blur();
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
    List<IterationObserver> observers = renderer.getIterationObservers();
    FlameTransformationContext ctx = renderer.getFlameTransformationContext();
    XYZPoint affineT = new XYZPoint(); // affine part of the transformation
    XYZPoint varT = new XYZPoint(); // complete transformation
    XYZPoint p = new XYZPoint();
    XYZPoint q = new XYZPoint();
    p.x = 2.0 * renderer.random.random() - 1.0;
    p.y = 2.0 * renderer.random.random() - 1.0;
    p.z = 2.0 * renderer.random.random() - 1.0;
    p.color = renderer.random.random();

    XForm xf = flame.getXForms().get(0);
    xf.transformPoint(ctx, affineT, varT, p, p);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
    }

    final double cosa = renderer.cosa;
    final double sina = renderer.sina;

    for (long i = 0; !forceAbort && (samples < 0 || i < samples); i++) {
      if (i % 100 == 0) {
        currSample = i;
      }
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      xf.transformPoint(ctx, affineT, varT, p, p);

      if (xf.getDrawMode() == DrawMode.HIDDEN)
        continue;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (renderer.random.random() > xf.getOpacity()))
        continue;

      XForm finalXForm = flame.getFinalXForm();
      double px, py;
      if (finalXForm != null) {
        finalXForm.transformPoint(ctx, affineT, varT, p, q);
        renderer.project(q);
        px = q.x * cosa + q.y * sina + renderer.rcX;
        if ((px < 0) || (px > renderer.camW))
          continue;
        py = q.y * cosa - q.x * sina + renderer.rcY;
        if ((py < 0) || (py > renderer.camH))
          continue;
      }
      else {
        q.assign(p);
        renderer.project(q);
        px = q.x * cosa + q.y * sina + renderer.rcX;
        if ((px < 0) || (px > renderer.camW))
          continue;
        py = q.y * cosa - q.x * sina + renderer.rcY;
        if ((py < 0) || (py > renderer.camH))
          continue;
      }

      int xIdx = (int) (renderer.bws * px + 0.5);
      int yIdx = (int) (renderer.bhs * py + 0.5);
      RasterPoint rp = renderer.raster[yIdx][xIdx];

      if (p.rgbColor) {
        rp.red += p.redColor;
        rp.green += p.greenColor;
        rp.blue += p.blueColor;
      }
      else {
        int colorIdx = (int) (p.color * renderer.paletteIdxScl + 0.5);
        RenderColor color = renderer.colorMap[colorIdx];
        rp.red += color.red;
        rp.green += color.green;
        rp.blue += color.blue;
      }
      rp.count++;
      if (observers != null && observers.size() > 0) {
        for (IterationObserver observer : observers) {
          observer.notifyIterationFinished(this, xIdx, yIdx);
        }
      }
    }
  }

  private void iterate_blur() {
    List<IterationObserver> observers = renderer.getIterationObservers();
    FlameTransformationContext ctx = renderer.getFlameTransformationContext();
    XYZPoint affineT = new XYZPoint(); // affine part of the transformation
    XYZPoint varT = new XYZPoint(); // complete transformation
    XYZPoint p = new XYZPoint();
    XYZPoint q = new XYZPoint();
    p.x = 2.0 * renderer.random.random() - 1.0;
    p.y = 2.0 * renderer.random.random() - 1.0;
    p.z = 2.0 * renderer.random.random() - 1.0;
    p.color = renderer.random.random();

    XForm xf = flame.getXForms().get(0);
    xf.transformPoint(ctx, affineT, varT, p, p);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
    }

    double blurKernel[][] = flame.getShadingInfo().createBlurKernel();
    int blurRadius = flame.getShadingInfo().getBlurRadius();
    double fade = flame.getShadingInfo().getBlurFade();
    if (fade < 0.0) {
      fade = 0.0;
    }
    else if (fade > 1.0) {
      fade = 1.0;
    }
    long blurMax = (long) ((1 - fade) * samples);
    int rasterWidth = renderer.rasterWidth;
    int rasterHeight = renderer.rasterHeight;

    for (long i = 0; !forceAbort && (samples < 0 || i < samples); i++) {
      if (i % 100 == 0) {
        currSample = i;
      }
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      xf.transformPoint(ctx, affineT, varT, p, p);

      if (xf.getDrawMode() == DrawMode.HIDDEN)
        continue;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (renderer.random.random() > xf.getOpacity()))
        continue;

      XForm finalXForm = flame.getFinalXForm();
      double px, py;
      if (finalXForm != null) {
        finalXForm.transformPoint(ctx, affineT, varT, p, q);
        renderer.project(q);
        px = q.x * renderer.cosa + q.y * renderer.sina + renderer.rcX;
        if ((px < 0) || (px > renderer.camW))
          continue;
        py = q.y * renderer.cosa - q.x * renderer.sina + renderer.rcY;
        if ((py < 0) || (py > renderer.camH))
          continue;
      }
      else {
        q.assign(p);
        renderer.project(q);
        px = q.x * renderer.cosa + q.y * renderer.sina + renderer.rcX;
        if ((px < 0) || (px > renderer.camW))
          continue;
        py = q.y * renderer.cosa - q.x * renderer.sina + renderer.rcY;
        if ((py < 0) || (py > renderer.camH))
          continue;
      }

      int xIdx = (int) (renderer.bws * px + 0.5);
      int yIdx = (int) (renderer.bhs * py + 0.5);
      RenderColor color;
      if (p.rgbColor) {
        color = new RenderColor();
        color.red = p.redColor;
        color.green = p.greenColor;
        color.blue = p.blueColor;
      }
      else {
        int colorIdx = (int) (p.color * renderer.paletteIdxScl + 0.5);
        color = renderer.colorMap[colorIdx];
      }

      if (i < blurMax) {
        for (int k = yIdx - blurRadius, yk = 0; k <= yIdx + blurRadius; k++, yk++) {
          if (k >= 0 && k < rasterHeight) {
            for (int l = xIdx - blurRadius, xk = 0; l <= xIdx + blurRadius; l++, xk++) {
              if (l >= 0 && l < rasterWidth) {
                // y, x
                RasterPoint rp = renderer.raster[k][l];
                double scl = blurKernel[yk][xk];
                rp.red += color.red * scl;
                rp.green += color.green * scl;
                rp.blue += color.blue * scl;
                rp.count++;
                if (observers != null && observers.size() > 0) {
                  for (IterationObserver observer : observers) {
                    observer.notifyIterationFinished(this, k, l);
                  }
                }
              }
            }
          }
        }
      }
      else {
        RasterPoint rp = renderer.raster[yIdx][xIdx];
        rp.red += color.red;
        rp.green += color.green;
        rp.blue += color.blue;
        rp.count++;
        if (observers != null && observers.size() > 0) {
          for (IterationObserver observer : observers) {
            observer.notifyIterationFinished(this, xIdx, yIdx);
          }
        }
      }
    }
  }

  private void iterate_pseudo3D() {
    List<IterationObserver> observers = renderer.getIterationObservers();
    FlameTransformationContext ctx = renderer.getFlameTransformationContext();
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
    xf.transformPoints(ctx, affineT, varT, p, p);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
    }

    for (long i = 0; !forceAbort && (samples < 0 || i < samples); i++) {
      if (i % 100 == 0) {
        currSample = i;
      }
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      xf.transformPoints(ctx, affineT, varT, p, p);
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
        finalXForm.transformPoints(ctx, affineT, varT, p, q);
        r.assign(q[0]);
        renderer.project(r);
        px = r.x * renderer.cosa + r.y * renderer.sina + renderer.rcX;
        py = r.y * renderer.cosa - r.x * renderer.sina + renderer.rcY;
      }
      else {
        for (int pIdx = 0; pIdx < p.length; pIdx++) {
          q[pIdx] = new XYZPoint();
          q[pIdx].assign(p[pIdx]);
        }
        r.assign(q[0]);
        renderer.project(r);
        px = r.x * renderer.cosa + r.y * renderer.sina + renderer.rcX;
        py = r.y * renderer.cosa - r.x * renderer.sina + renderer.rcY;
      }

      if ((px < 0) || (px > renderer.camW))
        continue;
      if ((py < 0) || (py > renderer.camH))
        continue;

      int xIdx = (int) (renderer.bws * px + 0.5);
      int yIdx = (int) (renderer.bhs * py + 0.5);

      RasterPoint rp = renderer.raster[yIdx][xIdx];
      RenderColor color;
      if (p[0].rgbColor) {
        color = new RenderColor();
        color.red = p[0].redColor;
        color.green = p[0].greenColor;
        color.blue = p[0].blueColor;
      }
      else {
        color = renderer.colorMap[(int) (p[0].color * renderer.paletteIdxScl + 0.5)];
      }
      RenderColor shadedColor = shader.calculateColor(q, color);

      rp.red += shadedColor.red;
      rp.green += shadedColor.green;
      rp.blue += shadedColor.blue;
      rp.count++;
      if (observers != null && observers.size() > 0) {
        for (IterationObserver observer : observers) {
          observer.notifyIterationFinished(this, xIdx, yIdx);
        }
      }
    }
  }

  public long getCurrSample() {
    return currSample;
  }

  public boolean isFinished() {
    return finished;
  }

  public SampleTonemapper getTonemapper() {
    return tonemapper;
  }

  protected void setTonemapper(SampleTonemapper tonemapper) {
    this.tonemapper = tonemapper;
  }

  public void cancel() {
    forceAbort = true;
  }

}
