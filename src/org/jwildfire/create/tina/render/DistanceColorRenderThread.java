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

import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Layer;

public final class DistanceColorRenderThread extends DefaultRenderThread {

  public DistanceColorRenderThread(Prefs pPrefs, int pThreadId, FlameRenderer pRenderer, List<RenderPacket> pRenderPackets, long pSamples, List<RenderSlice> pSlices, double pSliceThicknessMod, int pSliceThicknessSamples) {
    super(pPrefs, pThreadId, pRenderer, pRenderPackets, pSamples, pSlices, pSliceThicknessMod, pSliceThicknessSamples);
  }

  @Override
  protected DefaultRenderIterationState createState(RenderPacket pRenderPacket, Layer pLayer) {
    return new DistanceColorRenderIterationState(this, renderer, pRenderPacket, pLayer, ctx, randGen);
  }
}
