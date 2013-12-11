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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;

public final class FlameRenderFlatThread extends FlameRenderThread {
  private long startIter;
  private long iter;
  private List<RenderFlatIterationState> iterationState;

  public FlameRenderFlatThread(Prefs pPrefs, int pThreadId, FlameRenderer pRenderer, Flame pFlame, long pSamples) {
    super(pPrefs, pThreadId, pRenderer, pFlame, pSamples);
    iterationState = new ArrayList<RenderFlatIterationState>();
    List<Layer> layers = getValidLayers(pFlame);
    for (Layer layer : layers) {
      iterationState.add(new RenderFlatIterationState(this, renderer, flame, layer, ctx, randGen));
    }
  }

  @Override
  protected void preFuseIter() {
    for (RenderFlatIterationState state : iterationState) {
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
        for (RenderFlatIterationState state : iterationState) {
          state.validateState();
        }
      }
      for (RenderFlatIterationState state : iterationState) {
        state.iterateNext();
      }
    }
  }

  @Override
  protected FlameRenderThreadState saveState() {
    FlameRenderFlatThreadState res = new FlameRenderFlatThreadState();
    res.currSample = currSample;
    res.startIter = iter;
    for (RenderFlatIterationState state : iterationState) {
      FlameRenderFlatThreadState.IterationState persist = new FlameRenderFlatThreadState.IterationState();
      persist.xfIndex = (state.xf != null) ? state.layer.getXForms().indexOf(state.xf) : -1;
      persist.affineT = state.affineT != null ? state.affineT.makeCopy() : null;
      persist.varT = state.varT != null ? state.varT.makeCopy() : null;
      persist.p = state.p != null ? state.p.makeCopy() : null;
      persist.q = state.q != null ? state.q.makeCopy() : null;
      res.getState().add(persist);
    }
    return res;
  }

  @Override
  protected void restoreState(FlameRenderThreadState pState) {
    iterationState.clear();
    FlameRenderFlatThreadState state = (FlameRenderFlatThreadState) pState;
    currSample = state.currSample;
    startIter = state.startIter;
    List<Layer> layers = getValidLayers(flame);
    int layerIdx = 0;
    for (FlameRenderFlatThreadState.IterationState persist : state.getState()) {
      RenderFlatIterationState restored = new RenderFlatIterationState(this, renderer, flame, layers.get(layerIdx++), ctx, randGen);
      restored.xf = (persist.xfIndex >= 0) ? restored.layer.getXForms().get(persist.xfIndex) : null;
      restored.affineT = persist.affineT != null ? persist.affineT.makeCopy() : null;
      restored.varT = persist.varT != null ? persist.varT.makeCopy() : null;
      restored.p = persist.p != null ? persist.p.makeCopy() : null;
      restored.q = persist.q != null ? persist.q.makeCopy() : null;
      iterationState.add(restored);
    }
  }
}
