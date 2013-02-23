/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
import org.jwildfire.create.tina.base.raster.AbstractRasterPoint;
import org.jwildfire.image.Pixel;

public class SampleTonemapper {
  private final LogDensityPoint logDensityPnt;
  private Pixel toolPixel;
  private final GammaCorrectedRGBPoint rbgPoint;
  private final LogDensityFilter logDensityFilter;
  private final GammaCorrectionFilter gammaCorrectionFilter;

  public SampleTonemapper(Flame pFlame, AbstractRasterPoint[][] pRaster, int pRasterWidth, int pRasterHeight, int pImageWidth, int pImageHeight) {
    logDensityPnt = new LogDensityPoint();
    toolPixel = new Pixel();
    rbgPoint = new GammaCorrectedRGBPoint();
    logDensityFilter = new LogDensityFilter(pFlame);
    gammaCorrectionFilter = new GammaCorrectionFilter(pFlame, false);
    logDensityFilter.setRaster(pRaster, pRasterWidth, pRasterHeight, pImageWidth, pImageHeight);
  }

  public int tonemapSample(int pX, int pY) {
    logDensityFilter.transformPoint(logDensityPnt, pX, pY);
    gammaCorrectionFilter.transformPoint(logDensityPnt, rbgPoint);
    toolPixel.r = rbgPoint.red;
    toolPixel.g = rbgPoint.green;
    toolPixel.b = rbgPoint.blue;
    toolPixel.a = rbgPoint.alpha;
    return toolPixel.getARGBValue();
  }

  public double calcDensity(long pSampleCount) {
    return logDensityFilter.calcDensity(pSampleCount);
  }

}
