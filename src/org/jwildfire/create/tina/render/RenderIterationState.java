/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

import java.io.Serializable;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.raster.AbstractRaster;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class RenderIterationState implements Serializable {
  private static final long serialVersionUID = 2L;
  protected final AbstractRenderThread renderThread;
  protected final FlameRenderer renderer;
  protected final FlameRendererView view;
  protected final RenderPacket packet;
  protected final Flame flame;
  protected final Layer layer;
  protected AbstractRaster raster;
  protected final FlameTransformationContext ctx;
  protected final AbstractRandomGenerator randGen;
  protected final List<IterationObserver> observers;
  protected final RenderColor[] colorMap;
  protected final double paletteIdxScl;

  public RenderIterationState(AbstractRenderThread pRenderThread, FlameRenderer pRenderer, RenderPacket pPacket, Layer pLayer, FlameTransformationContext pCtx, AbstractRandomGenerator pRandGen) {
    renderThread = pRenderThread;
    renderer = pRenderer;
    packet = pPacket;
    view = pPacket.getView();
    flame = pPacket.getFlame();
    layer = pLayer;
    ctx = pCtx;
    randGen = pRandGen;
    observers = renderer.getIterationObservers();
    colorMap = pLayer.getPalette().createRenderPalette(flame.getWhiteLevel());
    paletteIdxScl = colorMap.length - 2;
    raster = pRenderer.getRaster();
  }

}
