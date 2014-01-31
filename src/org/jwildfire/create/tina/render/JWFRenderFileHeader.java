/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
import java.util.HashMap;
import java.util.Map;

public class JWFRenderFileHeader implements Serializable {
  private static final long serialVersionUID = 2L;
  protected final int numThreads;
  protected final int width, height;
  protected final long sampleCount;
  protected final long elapsedMilliseconds;
  protected final int spatialOversample;
  protected final int colorOversample;
  protected final int quality;
  protected final boolean withHDR;
  protected final boolean withHDRIntensityMap;
  protected final boolean withTransparency;

  private final Map<String, Serializable> additionalParams = new HashMap<String, Serializable>();

  public JWFRenderFileHeader(int pNumThreads, int pWidth, int pHeight, long pSampleCount, long pElapsedMilliseconds, int pSpatialOversample, int pColorOversample, int pQuality, boolean pWithHDR, boolean pWithHDRIntensityMap, boolean pWithTransparency) {
    numThreads = pNumThreads;
    width = pWidth;
    height = pHeight;
    sampleCount = pSampleCount;
    elapsedMilliseconds = pElapsedMilliseconds;
    spatialOversample = pSpatialOversample;
    colorOversample = pColorOversample;
    quality = pQuality;
    withHDR = pWithHDR;
    withHDRIntensityMap = pWithHDRIntensityMap;
    withTransparency = pWithTransparency;
  }

  public int getNumThreads() {
    return numThreads;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public long getSampleCount() {
    return sampleCount;
  }

  public long getElapsedMilliseconds() {
    return elapsedMilliseconds;
  }

  public void setParam(String pName, Serializable pValue) {
    additionalParams.put(pName, pValue);
  }

  public Serializable getParam(String pName) {
    return additionalParams.get(pName);
  }

  public void clearParams() {
    additionalParams.clear();
  }

  public int getSpatialOversample() {
    return spatialOversample;
  }

  public int getColorOversample() {
    return colorOversample;
  }

  public int getQuality() {
    return quality;
  }

  public boolean isWithHDR() {
    return withHDR;
  }

  public boolean isWithHDRIntensityMap() {
    return withHDRIntensityMap;
  }

  public boolean isWithTransparency() {
    return withTransparency;
  }
}
