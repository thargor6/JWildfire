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

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XYZPoint;

public final class Pseudo3DRenderThread extends DefaultRenderThread {

  public Pseudo3DRenderThread(Prefs pPrefs, int pThreadId, int pThreadGroupSize, FlameRenderer pRenderer, List<RenderPacket> pRenderPackets, long pSamples, List<RenderSlice> pSlices, double pSliceThicknessMod, int pSliceThicknessSamples) {
    super(pPrefs, pThreadId, pThreadGroupSize, pRenderer, pRenderPackets, pSamples, pSlices, pSliceThicknessMod, pSliceThicknessSamples);
  }

  @Override
  protected RenderThreadPersistentState saveState() {
    Pseudo3DRenderThreadPersistentState res = new Pseudo3DRenderThreadPersistentState();
    res.currSample = currSample;
    res.startIter = iter;
    for (DefaultRenderIterationState state : iterationState) {
      Pseudo3DRenderIterationState iState = (Pseudo3DRenderIterationState) state;
      Pseudo3DRenderThreadPersistentState.IterationState persist = new Pseudo3DRenderThreadPersistentState.IterationState();
      persist.packetIdx = renderPackets.indexOf(state.packet);
      persist.layerIdx = state.flame.getLayers().indexOf(state.layer);
      persist.xfIndex = (state.xf != null) ? state.layer.getXForms().indexOf(state.xf) : -1;
      persist.q = iState.q != null ? iState.q.makeCopy() : null;
      persist.affineTA = copyXYZPointArray(iState.affineTA);
      persist.varTA = copyXYZPointArray(iState.varTA);
      persist.pA = copyXYZPointArray(iState.pA);
      persist.qA = copyXYZPointArray(iState.qA);
      res.getLayerState().add(persist);
    }
    return res;
  }

  @Override
  protected void restoreState(RenderThreadPersistentState pState) {
    iterationState.clear();
    Pseudo3DRenderThreadPersistentState state = (Pseudo3DRenderThreadPersistentState) pState;
    currSample = state.currSample;
    startIter = state.startIter;
    for (Pseudo3DRenderThreadPersistentState.IterationState persist : state.getLayerState()) {
      RenderPacket packet = renderPackets.get(persist.packetIdx);
      Layer layer = packet.getFlame().getLayers().get(persist.layerIdx);
      Pseudo3DRenderIterationState restored = (Pseudo3DRenderIterationState) createState(packet, layer);
      restored.xf = (persist.xfIndex >= 0) ? restored.layer.getXForms().get(persist.xfIndex) : null;
      restored.q = persist.q != null ? persist.q.makeCopy() : null;
      restored.affineTA = copyXYZPointArray(persist.affineTA);
      restored.varTA = copyXYZPointArray(persist.varTA);
      restored.pA = copyXYZPointArray(persist.pA);
      restored.qA = copyXYZPointArray(persist.qA);
      iterationState.add(restored);
    }
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

  @Override
  protected DefaultRenderIterationState createState(RenderPacket pPacket, Layer pLayer) {
    return new Pseudo3DRenderIterationState(this, renderer, pPacket, pLayer, ctx, randGen);
  }

}
