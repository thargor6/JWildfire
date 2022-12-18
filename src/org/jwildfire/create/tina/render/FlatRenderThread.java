/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2022 Andreas Maschke

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

public final class FlatRenderThread extends DefaultRenderThread {

  public FlatRenderThread(Prefs pPrefs, int pThreadId, int pThreadGroupSize, FlameRenderer pRenderer, List<RenderPacket> pRenderPackets, long pSamples, List<RenderSlice> pSlices, int pSleepAmount) {
    super(pPrefs, pThreadId, pThreadGroupSize, pRenderer, pRenderPackets, pSamples, pSlices, pSleepAmount);
  }

  @Override
  protected DefaultRenderIterationState createState(RenderPacket pRenderPacket, Layer pLayer) {
    if (pRenderPacket.getFlame().getPostBlurRadius() > 0) {
      return new PostBlurRenderIterationState(this, renderer, pRenderPacket, pLayer, ctx, randGen, slices == null);
    }
    else {
      return new DefaultRenderIterationState(this, renderer, pRenderPacket, pLayer, ctx, randGen, slices == null);
    }
  }

}
