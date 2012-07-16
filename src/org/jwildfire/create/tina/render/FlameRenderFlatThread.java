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

public final class FlameRenderFlatThread extends FlameRenderThread {
  private XYZPoint affineT;
  private XYZPoint varT;
  private XYZPoint p;
  private XYZPoint q;
  private XForm xf;
  private long startIter;
  private long iter;

  public FlameRenderFlatThread(FlameRenderer pRenderer, Flame pFlame, long pSamples) {
    super(pRenderer, pFlame, pSamples);
  }

  @Override
  protected void initState() {
    startIter = 0;
    FlameTransformationContext ctx = renderer.getFlameTransformationContext();
    affineT = new XYZPoint(); // affine part of the transformation
    varT = new XYZPoint(); // complete transformation
    p = new XYZPoint();
    q = new XYZPoint();
    p.x = 2.0 * renderer.random.random() - 1.0;
    p.y = 2.0 * renderer.random.random() - 1.0;
    p.z = 0.0;
    p.color = renderer.random.random();

    xf = flame.getXForms().get(0);
    xf.transformPoint(ctx, affineT, varT, p, p);
    for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
      xf = xf.getNextAppliedXFormTable()[renderer.random.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
      if (xf == null) {
        return;
      }
    }
  }

  @Override
  protected void iterate() {
    List<IterationObserver> observers = renderer.getIterationObservers();
    FlameTransformationContext ctx = renderer.getFlameTransformationContext();
    final double cosa = renderer.cosa;
    final double sina = renderer.sina;
    for (iter = startIter; !forceAbort && (samples < 0 || iter < samples); iter++) {
      if (iter % 100 == 0) {
        currSample = iter;
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

  @Override
  protected FlameRenderThreadState saveState() {
    FlameRenderFlatThreadState res = new FlameRenderFlatThreadState();
    res.currSample = currSample;
    res.xfIndex = (xf != null) ? flame.getXForms().indexOf(xf) : -1;
    res.startIter = iter;
    res.affineT = affineT != null ? affineT.makeCopy() : null;
    res.varT = varT != null ? varT.makeCopy() : null;
    res.p = p != null ? p.makeCopy() : null;
    res.q = q != null ? q.makeCopy() : null;
    return res;
  }

  @Override
  protected void restoreState(FlameRenderThreadState pState) {
    FlameRenderFlatThreadState state = (FlameRenderFlatThreadState) pState;
    currSample = state.currSample;
    xf = (state.xfIndex >= 0) ? flame.getXForms().get(state.xfIndex) : null;
    startIter = state.startIter;
    affineT = state.affineT != null ? state.affineT.makeCopy() : null;
    varT = state.varT != null ? state.varT.makeCopy() : null;
    p = state.p != null ? state.p.makeCopy() : null;
    q = state.q != null ? state.q.makeCopy() : null;
  }
}
