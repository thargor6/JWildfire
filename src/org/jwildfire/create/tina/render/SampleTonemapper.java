/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2023 Andreas Maschke

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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.raster.AbstractRaster;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.render.backdrop.FlameBackgroundRenderContext;
import org.jwildfire.image.Pixel;

public class SampleTonemapper {
  private final Flame flame;
  private final AbstractRandomGenerator randGen;
  private final LogDensityPoint logDensityPnt;
  private Pixel toolPixel;
  private final GammaCorrectedRGBPoint rbgPoint;
  private LogDensityFilter logDensityFilter;
  private GammaCorrectionFilter gammaCorrectionFilter;
  private AbstractRaster raster;
  private int rasterWidth;
  private int rasterHeight;
  private int imageWidth;
  private int imageHeight;

  private final FlameBackgroundRenderContext ctx;

  public SampleTonemapper(Flame pFlame, AbstractRaster pRaster, int pRasterWidth, int pRasterHeight, int pImageWidth, int pImageHeight, AbstractRandomGenerator pRandGen, int pThreadId) {
    logDensityPnt = new LogDensityPoint(pFlame.getActiveLightCount());
    toolPixel = new Pixel();
    flame = pFlame.makeCopy();
    randGen = pRandGen;
    rbgPoint = new GammaCorrectedRGBPoint();
    logDensityFilter = new LogDensityFilter(pFlame, randGen);
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    gammaCorrectionFilter = new GammaCorrectionFilter(pFlame, false, imageWidth, imageHeight);
    raster = pRaster;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    logDensityFilter.setRaster(pRaster, pRasterWidth, pRasterHeight, pImageWidth, pImageHeight);
    ctx = new FlameBackgroundRenderContext(flame, pThreadId);
  }

  public int tonemapSample(int pX, int pY) {
    logDensityFilter.transformPointSimple(ctx, logDensityPnt, pX-logDensityFilter.getBorderWidth(), pY-logDensityFilter.getBorderWidth());
    gammaCorrectionFilter.transformPoint(logDensityPnt, rbgPoint, pX, pY);
    toolPixel.r = rbgPoint.red;
    toolPixel.g = rbgPoint.green;
    toolPixel.b = rbgPoint.blue;
    toolPixel.a = rbgPoint.alpha;
    return toolPixel.getARGBValue();
  }

  public double calcDensity(long pSampleCount) {
    return logDensityFilter.calcDensity(pSampleCount);
  }

  public void setDensity(double quality) {
    flame.setSampleDensity(quality);

    LogDensityFilter newLogDensityFilter = new LogDensityFilter(flame, randGen);
    GammaCorrectionFilter newGammaCorrectionFilter = new GammaCorrectionFilter(flame, false, imageWidth, imageHeight);
    newLogDensityFilter.setRaster(raster, rasterWidth, rasterHeight, imageWidth, imageHeight);

    gammaCorrectionFilter = newGammaCorrectionFilter;
    logDensityFilter = newLogDensityFilter;
  }

}
