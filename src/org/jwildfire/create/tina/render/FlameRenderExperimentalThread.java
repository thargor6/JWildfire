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
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.transform.XFormTransformService;

public final class FlameRenderExperimentalThread extends FlameRenderThread {
  private long startIter;
  private long iter;

  private class RenderState {
    private XYZPoint affineT;
    private XYZPoint varT;
    private XYZPoint p;
    private XYZPoint q;
    private XForm xf;
  }

  private List<RenderState> state = new ArrayList<RenderState>();
  private int stateIdx = 0;

  private final static int FLAME_COUNT = 20;
  private List<Flame> flames = new ArrayList<Flame>();

  public FlameRenderExperimentalThread(Prefs pPrefs, int pThreadId, FlameRenderer pRenderer, Flame pFlame, long pSamples) {
    super(pPrefs, pThreadId, pRenderer, pFlame, pSamples);
    createFlames(pFlame);
  }

  private void createFlames(Flame pFlame) {
    flames.clear();
    state.clear();
    for (int i = 0; i < FLAME_COUNT; i++) {
      Flame newFlame = flame.makeCopy();
      newFlame.getLayers().get(0).refreshModWeightTables(ctx);
      XForm xForm = newFlame.getXForms().get(0);
      double dx = 0.2 * i;
      double dy = 0.1 * i;
      XFormTransformService.globalTranslate(xForm, dx, dy, false);
      flames.add(newFlame);

      RenderState renderState = new RenderState();
      state.add(renderState);
    }
    stateIdx = 0;
  }

  @Override
  protected void preFuseIter() {
    for (int j = 0; j < flames.size(); j++) {
      Flame flame = flames.get(j);
      RenderState renderState = state.get(j);
      renderState.affineT = new XYZPoint(); // affine part of the transformation
      renderState.varT = new XYZPoint(); // complete transformation
      renderState.p = new XYZPoint();
      renderState.q = new XYZPoint();
      renderState.p.x = 2.0 * randGen.random() - 1.0;
      renderState.p.y = 2.0 * randGen.random() - 1.0;
      renderState.p.z = 0.0;
      renderState.p.color = randGen.random();

      renderState.xf = flame.getXForms().get(0);
      renderState.xf.transformPoint(ctx, renderState.affineT, renderState.varT, renderState.p, renderState.p);
      for (int i = 0; i <= Constants.INITIAL_ITERATIONS; i++) {
        renderState.xf = renderState.xf.getNextAppliedXFormTable()[randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE)];
        if (renderState.xf == null) {
          renderState.xf = flame.getXForms().get(0);
          break;
        }
        renderState.xf.transformPoint(ctx, renderState.affineT, renderState.varT, renderState.p, renderState.p);
      }
    }
    stateIdx = 0;
  }

  @Override
  protected void initState() {
    startIter = 0;
    preFuseIter();
  }

  @Override
  protected void iterate() {
    List<IterationObserver> observers = renderer.getIterationObservers();
    for (iter = startIter; !forceAbort && (samples < 0 || iter < samples); iter++) {
      stateIdx++;
      if (stateIdx >= flames.size()) {
        stateIdx = 0;
      }
      Flame flame = flames.get(stateIdx);
      RenderState renderState = state.get(stateIdx);

      if (iter % 10000 == 0) {
        preFuseIter();
      }
      else if (iter % 100 == 0) {
        currSample = iter;
        if (Double.isInfinite(renderState.p.x) || Double.isInfinite(renderState.p.y) || Double.isInfinite(renderState.p.z) || Double.isNaN(renderState.p.x) || Double.isNaN(renderState.p.y) || Double.isNaN(renderState.p.z)) {
          preFuseIter();
        }
      }

      int nextXForm = randGen.random(Constants.NEXT_APPLIED_XFORM_TABLE_SIZE);
      renderState.xf = renderState.xf.getNextAppliedXFormTable()[nextXForm];
      if (renderState.xf == null) {
        return;
      }

      renderState.xf.transformPoint(ctx, renderState.affineT, renderState.varT, renderState.p, renderState.p);
      if (renderState.xf.getDrawMode() == DrawMode.HIDDEN)
        continue;
      else if ((renderState.xf.getDrawMode() == DrawMode.OPAQUE) && (randGen.random() > renderState.xf.getOpacity()))
        continue;
      List<XForm> finalXForms = flame.getFinalXForms();

      int xIdx, yIdx;
      if (finalXForms.size() > 0) {
        finalXForms.get(0).transformPoint(ctx, renderState.affineT, renderState.varT, renderState.p, renderState.q);
        for (int i = 1; i < finalXForms.size(); i++) {
          finalXForms.get(i).transformPoint(ctx, renderState.affineT, renderState.varT, renderState.q, renderState.q);
        }
        if (!renderer.project(renderState.q, prj))
          continue;
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
        renderState.q.assign(renderState.p);
        if (!renderer.project(renderState.q, prj))
          continue;
        if ((renderState.xf.getAntialiasAmount() > EPSILON) && (renderState.xf.getAntialiasRadius() > EPSILON) && (randGen.random() > 1.0 - renderState.xf.getAntialiasAmount())) {
          double dr = exp(renderState.xf.getAntialiasRadius() * sqrt(-log(randGen.random()))) - 1.0;
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
        continue;
      if (yIdx < 0 || yIdx >= renderer.rasterHeight)
        continue;
      AbstractRasterPoint rp = renderer.raster[yIdx][xIdx];

      if (renderState.p.rgbColor) {
        rp.setRed(rp.getRed() + renderState.p.redColor * prj.intensity);
        rp.setGreen(rp.getGreen() + renderState.p.greenColor * prj.intensity);
        rp.setBlue(rp.getBlue() + renderState.p.blueColor * prj.intensity);
      }
      else {
        int colorIdx = (int) (renderState.p.color * renderer.paletteIdxScl + 0.5);
        RenderColor color = renderer.colorMap[colorIdx];
        rp.setRed(rp.getRed() + color.red * prj.intensity);
        rp.setGreen(rp.getGreen() + color.green * prj.intensity);
        rp.setBlue(rp.getBlue() + color.blue * prj.intensity);
      }
      rp.incCount();
      if (observers != null && observers.size() > 0) {
        for (IterationObserver observer : observers) {
          observer.notifyIterationFinished(this, xIdx, yIdx);
        }
      }
    }
  }

  @Override
  protected FlameRenderThreadState saveState() {
    // TODO
    return null;
  }

  @Override
  protected void restoreState(FlameRenderThreadState pState) {
    // TODO
  }
}
