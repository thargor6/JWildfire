/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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

public final class FlameRenderPseudo3DThread extends FlameRenderThread {
  private XYZPoint[] affineTA;
  private XYZPoint[] varTA;
  private XYZPoint[] pA;
  private XYZPoint[] qA;
  private XYZPoint r;
  private XForm xf;
  private long iter;
  private long startIter;

  public FlameRenderPseudo3DThread(FlameRenderer pRenderer, Flame pFlame, long pSamples) {
    super(pRenderer, pFlame, pSamples);
  }

  @Override
  protected void initState() {
    startIter = 0;
    FlameTransformationContext ctx = renderer.getFlameTransformationContext();
    affineTA = new XYZPoint[3]; // affine part of the transformation
    for (int i = 0; i < affineTA.length; i++) {
      affineTA[i] = new XYZPoint();
    }
    varTA = new XYZPoint[3]; // complete transformation
    for (int i = 0; i < varTA.length; i++) {
      varTA[i] = new XYZPoint();
    }
    pA = new XYZPoint[3];
    for (int i = 0; i < pA.length; i++) {
      pA[i] = new XYZPoint();
    }
    r = new XYZPoint();

    pA[0].x = 2.0 * renderer.random.random() - 1.0;
    pA[0].y = 2.0 * renderer.random.random() - 1.0;
    pA[0].z = 0;
    pA[0].color = renderer.random.random();

    distributeInitialPoints(pA);
    qA = new XYZPoint[3];
    for (int i = 0; i < qA.length; i++) {
      qA[i] = new XYZPoint();
    }

    xf = flame.getXForms().get(0);
    xf.transformPoints(ctx, affineTA, varTA, pA, pA);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
    }
  }

  private void distributeInitialPoints(XYZPoint[] p) {
    p[1].x = p[0].x + 0.001;
    p[1].y = p[0].y;
    p[1].z = p[0].z;
    p[2].x = p[0].x;
    p[2].y = p[0].y + 0.001;
    p[2].z = p[0].z;
  }

  @Override
  protected void iterate() {
    List<IterationObserver> observers = renderer.getIterationObservers();
    FlameTransformationContext ctx = renderer.getFlameTransformationContext();
    Pseudo3DShader shader = new Pseudo3DShader(flame.getShadingInfo());
    shader.init();

    for (iter = startIter; !forceAbort && (samples < 0 || iter < samples); iter++) {
      if (iter % 100 == 0) {
        currSample = iter;
      }
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
      xf.transformPoints(ctx, affineTA, varTA, pA, pA);
      if (xf.getDrawMode() == DrawMode.HIDDEN)
        continue;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (renderer.random.random() > xf.getOpacity()))
        continue;

      XForm finalXForm = flame.getFinalXForm();
      double px = 0.0, py = 0.0;
      if (finalXForm != null) {
        for (int pIdx = 0; pIdx < pA.length; pIdx++) {
          qA[pIdx] = new XYZPoint();
        }
        finalXForm.transformPoints(ctx, affineTA, varTA, pA, qA);
        r.assign(qA[0]);
        renderer.project(r);
        px = r.x * renderer.cosa + r.y * renderer.sina + renderer.rcX;
        py = r.y * renderer.cosa - r.x * renderer.sina + renderer.rcY;
      }
      else {
        for (int pIdx = 0; pIdx < pA.length; pIdx++) {
          qA[pIdx] = new XYZPoint();
          qA[pIdx].assign(pA[pIdx]);
        }
        r.assign(qA[0]);
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
      if (pA[0].rgbColor) {
        color = new RenderColor();
        color.red = pA[0].redColor;
        color.green = pA[0].greenColor;
        color.blue = pA[0].blueColor;
      }
      else {
        color = renderer.colorMap[(int) (pA[0].color * renderer.paletteIdxScl + 0.5)];
      }
      RenderColor shadedColor = shader.calculateColor(qA, color);

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

  @Override
  protected FlameRenderThreadState saveState() {
    FlameRenderPseudo3DThreadState res = new FlameRenderPseudo3DThreadState();
    res.currSample = currSample;
    res.xfIndex = (xf != null) ? flame.getXForms().indexOf(xf) : -1;
    res.startIter = iter;
    res.r = r != null ? r.makeCopy() : null;
    res.affineTA = copyXYZPointArray(affineTA);
    res.varTA = copyXYZPointArray(varTA);
    res.pA = copyXYZPointArray(pA);
    res.qA = copyXYZPointArray(qA);
    return res;
  }

  @Override
  protected void restoreState(FlameRenderThreadState pState) {
    FlameRenderPseudo3DThreadState state = (FlameRenderPseudo3DThreadState) pState;
    currSample = state.currSample;
    xf = (state.xfIndex >= 0) ? flame.getXForms().get(state.xfIndex) : null;
    startIter = state.startIter;
    r = state.r != null ? state.r.makeCopy() : null;
    affineTA = copyXYZPointArray(state.affineTA);
    varTA = copyXYZPointArray(state.varTA);
    pA = copyXYZPointArray(state.pA);
    qA = copyXYZPointArray(state.qA);
  }

  private XYZPoint[] copyXYZPointArray(XYZPoint points[]) {
    if (points != null) {
      XYZPoint res[] = new XYZPoint[points.length];
      for (int i = 0; i < points.length; i++) {
        res[i] = points[i] != null ? points[i].makeCopy() : null;
      }
      return res;
    }
    else {
      return null;
    }
  }

}
