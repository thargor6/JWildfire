/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.create.tina.render.image;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.LogDensityFilter;
import org.jwildfire.create.tina.render.ZBufferSample;
import org.jwildfire.image.SimpleGrayImage;

public class ZBufferAnalyzer {
  private final LogDensityFilter logDensityFilter;
  private final ZBufferSample accumSample, sample;
  private final SimpleGrayImage img;
  private final double zScale;
  private final double zShift;

  public static double ZSCALE_SCALE = 0.001;

  public ZBufferAnalyzer(Flame pFlame, LogDensityFilter pLogDensityFilter, SimpleGrayImage pImg, double pZScale, double pZShift) {
    logDensityFilter = pLogDensityFilter;
    sample = new ZBufferSample();
    accumSample = new ZBufferSample();
    img = pImg;
    zScale = pZScale * ZSCALE_SCALE;
    zShift = pZShift;
  }

  public ZBufferInfo analyze() {
    double zMin = Double.MAX_VALUE;
    double zMax = Double.MIN_VALUE;
    long totalCount = 0;
    for (int i = 0; i < img.getImageHeight(); i++) {
      for (int j = 0; j < img.getImageWidth(); j++) {
        logDensityFilter.transformZPoint(accumSample, sample, j, i);
        if(accumSample.hasZ) {
          if(accumSample.z < zMin) {
            zMin = accumSample.z;
          }
          else if(accumSample.z > zMax) {
            zMax = accumSample.z;
          }
          totalCount++;
        }
      }
    }
    double coverage = (double)totalCount / (double)((long)img.getImageHeight() * (long)img.getImageWidth());

    final double BORDER_SIZE_PCT = 6;
    double zBorderMin = zMin - (zMax - zMin ) * BORDER_SIZE_PCT / 100.0;
    double zBorderMax = zMax + (zMax - zMin ) * BORDER_SIZE_PCT / 100.0;

    ZBufferGrayValueCalculator calculator = new ZBufferGrayValueCalculator(zScale, zShift);
    int grayBorderMinValue = calculator.calculateGrayValue(zBorderMin);
    int grayBorderMaxValue = calculator.calculateGrayValue(zBorderMax);

    ZBufferGrayValueCalculator.ZBufferParams suggestedParams = calculator.suggestParams(zBorderMin, zBorderMax, coverage);

    return new ZBufferInfo(zMin, zMax, zBorderMin, zBorderMax, grayBorderMinValue, grayBorderMaxValue, coverage,suggestedParams.getzScale() / ZSCALE_SCALE, suggestedParams.getzShift(), suggestedParams.getzBias());
  }

}
