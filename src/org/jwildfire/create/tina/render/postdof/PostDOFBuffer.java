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
package org.jwildfire.create.tina.render.postdof;

import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;

public class PostDOFBuffer {
  private final int width, height;
  private final float[][] rBuf, gBuf, bBuf;

  public PostDOFBuffer(SimpleImage pImage) {
    width = pImage.getImageWidth();
    height = pImage.getImageHeight();
    rBuf = new float[getWidth()][getHeight()];
    gBuf = new float[getWidth()][getHeight()];
    bBuf = new float[getWidth()][getHeight()];
  }

  public PostDOFBuffer(SimpleHDRImage pImage) {
    width = pImage.getImageWidth();
    height = pImage.getImageHeight();
    rBuf = new float[getWidth()][getHeight()];
    gBuf = new float[getWidth()][getHeight()];
    bBuf = new float[getWidth()][getHeight()];
  }

  public void addColorToBuffer(int x, int y, PostDOFSample sample, double scale, double z) {
    if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
      rBuf[x][y] += (float) (sample.getR() * scale);
      gBuf[x][y] += (float) (sample.getG() * scale);
      bBuf[x][y] += (float) (sample.getB() * scale);
    }
  }

  public void renderToImage(SimpleImage pImage) {
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int r = Tools.roundColor(rBuf[i][j]);
        int g = Tools.roundColor(gBuf[i][j]);
        int b = Tools.roundColor(bBuf[i][j]);
        pImage.setARGB(i, j, pImage.getAValue(i, j), r, g, b);
      }
    }
  }

  public void renderToImage(SimpleHDRImage pHDRImage) {
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        pHDRImage.setRGB(i, j, rBuf[i][j], gBuf[i][j], bBuf[i][j]);
      }
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public synchronized void addSamples(List<PostDOFFilteredSample> filteredSamples, double intensitySum) {
    for (PostDOFFilteredSample filteredSample : filteredSamples) {
      addColorToBuffer(filteredSample.getX(), filteredSample.getY(), filteredSample.getSample(), filteredSample.getIntensity() / intensitySum, 0.0);
    }
  }

}
