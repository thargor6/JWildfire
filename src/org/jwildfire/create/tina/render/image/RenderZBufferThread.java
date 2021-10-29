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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.LogDensityFilter;
import org.jwildfire.create.tina.render.ZBufferSample;
import org.jwildfire.image.SimpleGrayImage;

import static org.jwildfire.create.tina.render.image.ZBufferAnalyzer.ZSCALE_SCALE;

public class RenderZBufferThread extends AbstractImageRenderThread {
  private final LogDensityFilter logDensityFilter;
  private final int startRow, endRow;
  private final ZBufferSample accumSample, sample;
  private final SimpleGrayImage img;
  private final double zScale;
  private final int zBias;
  private final double zShift;

  public RenderZBufferThread(Flame pFlame, LogDensityFilter pLogDensityFilter, int pStartRow, int pEndRow, SimpleGrayImage pImg, double pZScale, double pZBias, double pZShift) {
    logDensityFilter = pLogDensityFilter;
    startRow = pStartRow;
    endRow = pEndRow;
    sample = new ZBufferSample();
    accumSample = new ZBufferSample();
    img = pImg;
    zScale = pZScale * ZSCALE_SCALE;
    zBias = (pZBias <= 0.0) ? 0 : (pZBias >= 1.0) ? 0xffff : Tools.FTOI(pZBias * 0xffff);
    zShift = pZShift;
  }

  @Override
  public void run() {
    setDone(false);
    try {
      for (int i = startRow; i < endRow; i++) {
        for (int j = 0; j < img.getImageWidth(); j++) {
          logDensityFilter.transformZPoint(accumSample, sample, j, i);
/*
          if(!accumSample.hasZ) {
            for(int k=1;k<img.getImageWidth();k++) {
              logDensityFilter.transformZPoint(accumSample, sample, j+k, i);
              if(accumSample.hasZ) {
                break;
              }
              logDensityFilter.transformZPoint(accumSample, sample, j, i+k);
              if(accumSample.hasZ) {
                break;
              }
              logDensityFilter.transformZPoint(accumSample, sample, j-k, i);
              if(accumSample.hasZ) {
                break;
              }
              logDensityFilter.transformZPoint(accumSample, sample, j, i-k);
              if(accumSample.hasZ) {
                break;
              }
            }
          }
*/
          // negative zScale: white to black (black near camera, white background)
          if (zScale < 0.0) {
            if (accumSample.hasZ) {
              int lvl = Tools.FTOI((-zScale * accumSample.z - zShift) * 32767.0 + 32767.0);
              if (lvl < 0) {
                lvl = 0;
              } else if (lvl > 0xffff) {
                lvl = 0xffff;
              }
              int grayValue = 0xffff - lvl & 0xffff;
              if(zBias>0 && grayValue > 0xffff - zBias) {
                grayValue = 0xffff - zBias;
              }
              img.setValue(j, i, grayValue);
            }
            else {
              img.setValue(j, i, 0xffff - zBias);
            }
          }
          // positive zScale: black to white (white near camera, black background, which is the default)
          else {
            if (accumSample.hasZ) {
              int lvl = Tools.FTOI((zScale * accumSample.z - zShift) * 32767.0 + 32767.0);
              if (lvl < 0) {
                lvl = 0;
              } else if (lvl > 0xffff) {
                lvl = 0xffff;
              }
              int grayValue = lvl & 0xffff;
              if(zBias>0 && grayValue < zBias) {
                grayValue = zBias;
              }
              img.setValue(j, i, grayValue);
            }
            else {
              img.setValue(j,  i, zBias);
            }
          }
        }
      }
    }
    finally {
      setDone(true);
    }
  }

}
