/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
import org.jwildfire.create.tina.base.Layer;

public abstract class DefaultRenderThread extends AbstractRenderThread {
  private static final int MAX_ERRORS = 100;
  protected long startIter;
  protected long iter;

  protected List<DefaultRenderIterationState> iterationState;

  public DefaultRenderThread(Prefs pPrefs, int pThreadId, int pThreadGroupSize, FlameRenderer pRenderer, List<RenderPacket> pRenderPackets, long pSamples, List<RenderSlice> pSlices) {
    super(pPrefs, pThreadId, pThreadGroupSize, pRenderer, pRenderPackets, pSamples, pSlices);

    iterationState = new ArrayList<DefaultRenderIterationState>();

    for (RenderPacket packet : pRenderPackets) {
      List<Layer> layers = getValidLayers(packet.getFlame());
      for (Layer layer : layers) {
        DefaultRenderIterationState state = createState(packet, layer);
        iterationState.add(state);
      }
    }
  }

  protected abstract DefaultRenderIterationState createState(RenderPacket pRenderPacket, Layer pLayer);

  @Override
  protected RenderThreadPersistentState saveState() {
    DefaultRenderThreadPersistentState res = new DefaultRenderThreadPersistentState();
    res.currSample = currSample;
    res.startIter = iter;
    for (DefaultRenderIterationState state : iterationState) {
      DefaultRenderThreadPersistentState.IterationState persist = new DefaultRenderThreadPersistentState.IterationState();
      persist.packetIdx = renderPackets.indexOf(state.packet);
      persist.layerIdx = state.flame.getLayers().indexOf(state.layer);
      persist.xfIndex = (state.xf != null) ? state.layer.getXForms().indexOf(state.xf) : -1;
      persist.affineT = state.affineT != null ? state.affineT.makeCopy() : null;
      persist.varT = state.varT != null ? state.varT.makeCopy() : null;
      persist.p = state.p != null ? state.p.makeCopy() : null;
      persist.q = state.q != null ? state.q.makeCopy() : null;
      res.getLayerState().add(persist);
    }
    return res;
  }

  @Override
  protected void restoreState(RenderThreadPersistentState pState) {
    iterationState.clear();
    DefaultRenderThreadPersistentState state = (DefaultRenderThreadPersistentState) pState;
    currSample = state.currSample;
    startIter = state.startIter;
    for (DefaultRenderThreadPersistentState.IterationState persist : state.getLayerState()) {
      RenderPacket packet = renderPackets.get(persist.packetIdx);
      Layer layer = packet.getFlame().getLayers().get(persist.layerIdx);
      DefaultRenderIterationState restored = createState(packet, layer);
      restored.xf = (persist.xfIndex >= 0) ? restored.layer.getXForms().get(persist.xfIndex) : null;
      restored.affineT = persist.affineT != null ? persist.affineT.makeCopy() : null;
      restored.varT = persist.varT != null ? persist.varT.makeCopy() : null;
      restored.p = persist.p != null ? persist.p.makeCopy() : null;
      restored.q = persist.q != null ? persist.q.makeCopy() : null;
      iterationState.add(restored);
    }
  }

  @Override
  protected void preFuseIter() {
    for (DefaultRenderIterationState state : iterationState) {
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
    for (DefaultRenderIterationState state : iterationState) {
      state.init();
    }
    int errors = 0;
    try {
      for (iter = startIter; !forceAbort && (samples < 0 || iter < samples); iter += iterInc) {
        if (iter % 1000 == 0) {
          currSample = iter;
          for (DefaultRenderIterationState state : iterationState) {
            state.validateState();
          }
        }
        if (iter % 100000 == 0) {
          try {
            Thread.sleep(3);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        for (DefaultRenderIterationState state : iterationState) {
          try {
            state.iterateNext();
          }
          catch (Exception ex) {
            errors++;
            state.validateState();
            if (errors > MAX_ERRORS) {
              System.out.println("INVALID FLAME");
              iter = samples;
              break;
            }
          }
        }
      }
    }
    finally {
      for (DefaultRenderIterationState state : iterationState) {
        try {
          state.cleanup();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  @Override
  protected void iterateSlices(List<RenderSlice> pSlices) {
    final int iterInc = iterationState.size();
    if (iterInc < 1) {
      return;
    }
    for (DefaultRenderIterationState state : iterationState) {
      state.init();
    }

    for (iter = startIter; !forceAbort && (samples < 0 || iter < samples); iter += iterInc) {
      if (iter % 10000000 == 0) {
        preFuseIter();
      }
      else if (iter % 10000 == 0) {
        currSample = iter;
        for (DefaultRenderIterationState state : iterationState) {
          state.validateState();
        }
      }
      for (DefaultRenderIterationState state : iterationState) {
        state.iterateNext(pSlices);
      }
    }
  }

}
