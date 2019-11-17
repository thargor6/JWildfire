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
package org.jwildfire.create.tina.render.image;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.LogDensityFilter;
import org.jwildfire.create.tina.render.ZBufferSample;
import org.jwildfire.image.SimpleGrayImage;

public class RenderZBufferThread extends AbstractImageRenderThread {
  private final LogDensityFilter logDensityFilter;
  private final int startRow, endRow;
  private final ZBufferSample accumSample, sample;
  private final SimpleGrayImage img;
  private final double zScale;
  private final double imgSize;
  private final int zBias;

  public RenderZBufferThread(Flame pFlame, LogDensityFilter pLogDensityFilter, int pStartRow, int pEndRow, SimpleGrayImage pImg, double pZScale, double pZBias) {
    logDensityFilter = pLogDensityFilter;
    startRow = pStartRow;
    endRow = pEndRow;
    sample = new ZBufferSample();
    accumSample = new ZBufferSample();
    img = pImg;
    imgSize = MathLib.sqrt(MathLib.sqr(pImg.getImageWidth()) + MathLib.sqr(pImg.getImageHeight()));
    zScale = pZScale * 1000.0 / imgSize;
    zBias = (pZBias <= 0.0) ? 0 : (pZBias >= 1.0) ? 0xffff : Tools.FTOI(pZBias * 0xffff);
  }

  @Override
  public void run() {
    setDone(false);
    try {
      for (int i = startRow; i < endRow; i++) {
        for (int j = 0; j < img.getImageWidth(); j++) {
          logDensityFilter.transformZPoint(accumSample, sample, j, i);
          // negative zScale: white to black (black near camera, white background)
          if (zScale < 0.0) {
            if (accumSample.hasZ) {
              int lvl = Tools.FTOI(-zScale * accumSample.z * 32767.0 + 32767.0);
              if (lvl < 0) {
                lvl = 0;
              } else if (lvl > 0xffff) {
                lvl = 0xffff;
              }
              int grayValue = 0xffff - lvl & 0xffff;
              img.setValue(j, i, grayValue);
            }
            else {
              img.setValue(j, i, 0xffff - zBias);
            }
          }
          // positive zScale: black to white (white near camera, black background, which is the default)
          else {
            if (accumSample.hasZ) {
              int lvl = Tools.FTOI(zScale * accumSample.z * 32767.0 + 32767.0);
              if (lvl < 0) {
                lvl = 0;
              } else if (lvl > 0xffff) {
                lvl = 0xffff;
              }
              int grayValue = lvl & 0xffff;
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
