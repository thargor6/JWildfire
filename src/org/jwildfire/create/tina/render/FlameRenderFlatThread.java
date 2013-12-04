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
package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Constants;
import org.jwildfire.create.tina.base.DrawMode;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public final class FlameRenderFlatThread extends FlameRenderThread {
  private long startIter;
  private long iter;
  private List<IterationState> iterationState;

  private static class IterationState extends AbstractIterationState {
    private XYZPoint affineT;
    private XYZPoint varT;
    private XYZPoint p;
    private XYZPoint q;
    private XForm xf;
    protected final XYZProjectedPoint prj = new XYZProjectedPoint();

    public IterationState(FlameRenderThread pRenderThread, FlameRenderer pRenderer, Flame pFlame, Layer pLayer, FlameTransformationContext pCtx, AbstractRandomGenerator pRandGen) {
      super(pRenderThread, pRenderer, pFlame, pLayer, pCtx, pRandGen);
    }

    public void preFuseIter() {
      affineT = new XYZPoint(); // affine part of the transformation
      varT = new XYZPoint(); // complete transformation
      p = new XYZPoint();
      q = new XYZPoint();
      p.x = 2.0 * randGen.random() - 1.0;
      p.y = 2.0 * randGen.random() - 1.0;
      p.z = 0.0;
      p.color = randGen.random();

      xf = layer.getXForms().get(0);
      xf.transformPoint(ctx, affineT, varT, p, p);
      for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
        xf = xf.getNextAppliedXFormTable()[randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
        if (xf == null) {
          xf = layer.getXForms().get(0);
          return;
        }
        xf.transformPoint(ctx, affineT, varT, p, p);
      }
    }

    public void validateState() {
      if (Double.isInfinite(p.x) || Double.isInfinite(p.y) || Double.isInfinite(p.z) || Double.isNaN(p.x) || Double.isNaN(p.y) || Double.isNaN(p.z)) {
        preFuseIter();
      }
    }

    public void iterateNext() {
      int nextXForm = randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE);
      xf = xf.getNextAppliedXFormTable()[nextXForm];
      if (xf == null) {
        return;
      }

      xf.transformPoint(ctx, affineT, varT, p, p);
      if (xf.getDrawMode() == DrawMode.HIDDEN)
        return;
      else if ((xf.getDrawMode() == DrawMode.OPAQUE) && (randGen.random() > xf.getOpacity()))
        return;
      List<XForm> finalXForms = layer.getFinalXForms();

      int xIdx, yIdx;
      if (finalXForms.size() > 0) {
        finalXForms.get(0).transformPoint(ctx, affineT, varT, p, q);
        for (int i = 1; i < finalXForms.size(); i++) {
          finalXForms.get(i).transformPoint(ctx, affineT, varT, q, q);
        }
        if (!renderer.project(q, prj))
          return;
        XForm finalXForm = finalXForms.get(finalXForms.size() - 1);
        if ((finalXForm.getAntialiasAmount() > EPSILON) && (finalXForm.getAntialiasRadius() > EPSILON) && (randGen.random() > 1.0 - finalXForm.getAntialiasAmount())) {
          double dr = exp(finalXForm.getAntialiasRadius() * sqrt(-log(randGen.random()))) - 1.0;
          double da = randGen.random() * 2.0 * M_PI;
          xIdx = (int) (renderer.bws * prj.x + dr * cos(da) + 0.5);
          yIdx = (int) (renderer.bhs * prj.y + dr * sin(da) + 0.5);
        }
        else {
          xIdx = (int) (renderer.bws * prj.x + 0.5);
          yIdx = (int) (renderer.bhs * prj.y + 0.5);
        }
      }
      else {
        q.assign(p);
        if (!renderer.project(q, prj))
          return;
        if ((xf.getAntialiasAmount() > EPSILON) && (xf.getAntialiasRadius() > EPSILON) && (randGen.random() > 1.0 - xf.getAntialiasAmount())) {
          double dr = exp(xf.getAntialiasRadius() * sqrt(-log(randGen.random()))) - 1.0;
          double da = randGen.random() * 2.0 * M_PI;
          xIdx = (int) (renderer.bws * prj.x + dr * cos(da) + 0.5);
          yIdx = (int) (renderer.bhs * prj.y + dr * sin(da) + 0.5);
        }
        else {
          xIdx = (int) (renderer.bws * prj.x + 0.5);
          yIdx = (int) (renderer.bhs * prj.y + 0.5);
        }
      }
      if (xIdx < 0 || xIdx >= renderer.rasterWidth)
        return;
      if (yIdx < 0 || yIdx >= renderer.rasterHeight)
        return;
      AbstractRasterPoint rp = renderer.raster[yIdx][xIdx];

      if (p.rgbColor) {
        rp.setRed(rp.getRed() + p.redColor * prj.intensity);
        rp.setGreen(rp.getGreen() + p.greenColor * prj.intensity);
        rp.setBlue(rp.getBlue() + p.blueColor * prj.intensity);
      }
      else {
        int colorIdx = (int) (p.color * paletteIdxScl + 0.5);
        RenderColor color = colorMap[colorIdx];
        rp.setRed(rp.getRed() + color.red * prj.intensity);
        rp.setGreen(rp.getGreen() + color.green * prj.intensity);
        rp.setBlue(rp.getBlue() + color.blue * prj.intensity);
      }
      rp.incCount();
      if (observers != null && observers.size() > 0) {
        for (IterationObserver observer : observers) {
          observer.notifyIterationFinished(renderThread, xIdx, yIdx);
        }
      }
    }
  }

  public FlameRenderFlatThread(Prefs pPrefs, int pThreadId, FlameRenderer pRenderer, Flame pFlame, long pSamples) {
    super(pPrefs, pThreadId, pRenderer, pFlame, pSamples);
    // TODO: remove
    iterationState = new ArrayList<IterationState>();
    for (Layer layer : pFlame.getLayers()) {
      iterationState.add(new IterationState(this, renderer, flame, layer, ctx, randGen));
    }
    //
    iterationState = new ArrayList<IterationState>();
    double weightSum = 0.0;
    int layerCount = 0;
    for (Layer layer : pFlame.getLayers()) {
      if (layer.isVisible() && layer.getWeight() > EPSILON) {
        weightSum += layer.getWeight();
        layerCount++;
      }
    }

    if (layerCount == 1) {
      for (Layer layer : pFlame.getLayers()) {
        if (layer.isVisible() && layer.getWeight() > EPSILON) {
          iterationState.add(new IterationState(this, renderer, flame, layer, ctx, randGen));
          break;
        }
      }
    }
    else if (layerCount > 1) {
      final int LAYER_STEPS = 1000;
      for (Layer layer : pFlame.getLayers()) {
        if (layer.isVisible() && layer.getWeight() > EPSILON) {
          int cnt = (int) (layer.getWeight() / weightSum * LAYER_STEPS + 0.5);
          for (int j = 0; j < cnt; j++) {
            iterationState.add(new IterationState(this, renderer, flame, layer, ctx, randGen));
          }
        }
      }
    }
  }

  @Override
  protected void preFuseIter() {
    for (IterationState state : iterationState) {
      state.preFuseIter();
    }
  }

  @Override
  protected void initState() {
    startIter = 0;
    preFuseIter();
  }

  @Override
  protected void iterate() {
    final int iterInc = iterationState.size();
    if (iterInc < 1) {
      return;
    }
    for (iter = startIter; !forceAbort && (samples < 0 || iter < samples); iter += iterInc) {
      if (iter % 10000 == 0) {
        preFuseIter();
      }
      else if (iter % 100 == 0) {
        currSample = iter;
        for (IterationState state : iterationState) {
          state.validateState();
        }
      }
      for (IterationState state : iterationState) {
        state.iterateNext();
      }
    }
  }

  @Override
  protected FlameRenderThreadState saveState() {
    return null;
    // TODO
    //    FlameRenderFlatThreadState res = new FlameRenderFlatThreadState();
    //    res.currSample = currSample;
    //    res.xfIndex = (xf != null) ? flame.getXForms().indexOf(xf) : -1;
    //    res.startIter = iter;
    //    res.affineT = affineT != null ? affineT.makeCopy() : null;
    //    res.varT = varT != null ? varT.makeCopy() : null;
    //    res.p = p != null ? p.makeCopy() : null;
    //    res.q = q != null ? q.makeCopy() : null;
    //    return res;
  }

  @Override
  protected void restoreState(FlameRenderThreadState pState) {
    // TODO    
    //    FlameRenderFlatThreadState state = (FlameRenderFlatThreadState) pState;
    //    currSample = state.currSample;
    //    xf = (state.xfIndex >= 0) ? flame.getXForms().get(state.xfIndex) : null;
    //    startIter = state.startIter;
    //    affineT = state.affineT != null ? state.affineT.makeCopy() : null;
    //    varT = state.varT != null ? state.varT.makeCopy() : null;
    //    p = state.p != null ? state.p.makeCopy() : null;
    //    q = state.q != null ? state.q.makeCopy() : null;
  }
}
