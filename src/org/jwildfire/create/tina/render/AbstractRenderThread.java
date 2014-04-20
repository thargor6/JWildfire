/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.RandomGeneratorFactory;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public abstract class AbstractRenderThread implements Runnable {
  protected final FlameRenderer renderer;
  protected final List<RenderPacket> renderPackets;
  protected final long samples;
  protected volatile long currSample;
  protected SampleTonemapper tonemapper;
  protected boolean forceAbort;
  protected boolean finished;
  protected RenderThreadPersistentState resumeState;
  protected FlameTransformationContext ctx;
  protected AbstractRandomGenerator randGen;

  public AbstractRenderThread(Prefs pPrefs, int pThreadId, FlameRenderer pRenderer, List<RenderPacket> pRenderPackets, long pSamples) {
    renderer = pRenderer;
    renderPackets = pRenderPackets;
    samples = pSamples;
    randGen = RandomGeneratorFactory.getInstance(pPrefs.getTinaRandomNumberGenerator(), pThreadId);
    ctx = new FlameTransformationContext(pRenderer, randGen, pRenderPackets.get(0).getFlame().getFrame());
    ctx.setPreserveZCoordinate(pRenderPackets.get(0).getFlame().isPreserveZ());
    ctx.setPreview(renderer.isPreview());
  }

  protected boolean isValidLayer(Layer pLayer) {
    return pLayer.isVisible() && pLayer.getPalette() != null && pLayer.getXForms().size() > 0;
  }

  protected List<Layer> getValidLayers(Flame pFlame) {
    List<Layer> res = new ArrayList<Layer>();
    for (Layer layer : pFlame.getLayers()) {
      if (isValidLayer(layer)) {
        res.add(layer);
      }
    }
    return res;
  }

  protected abstract void preFuseIter();

  protected abstract void initState();

  protected abstract void iterate();

  protected abstract RenderThreadPersistentState saveState();

  protected abstract void restoreState(RenderThreadPersistentState pState);

  @Override
  public void run() {
    finished = forceAbort = false;
    try {
      try {
        if (resumeState == null) {
          initState();
        }
        else {
          restoreState(resumeState);
        }
        iterate();
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

  public void setResumeState(RenderThreadPersistentState resumeState) {
    this.resumeState = resumeState;
  }

  protected abstract long getIter();
}
