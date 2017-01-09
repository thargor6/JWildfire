/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.base.raster;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.LightViewCalculator;
import org.jwildfire.create.tina.render.PlotSample;
import org.jwildfire.create.tina.render.ZBufferSample;

public interface AbstractRaster {

  void incCount(int pX, int pY);

  void allocRaster(Flame flame, int pWidth, int pHeight);

  void readRasterPoint(int pX, int pY, RasterPoint pDestRasterPoint);

  void readRasterPointSafe(int pX, int pY, RasterPoint pDestRasterPoint);

  void addSamples(PlotSample[] pPlotBuffer, int pCount);

  void finalizeRaster();

  void addShadowMapSamples(int pShadowMapIdx, PlotSample[] pPlotBuffer, int pCount);

  void notifyInit(LightViewCalculator lightViewCalculator);

  void readZBuffer(int pX, int pY, ZBufferSample pDest);

  void readZBufferSafe(int pX, int pY, ZBufferSample pDest);

  public LightViewCalculator getLightViewCalculator();

}
