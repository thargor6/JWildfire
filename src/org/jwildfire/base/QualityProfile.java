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
package org.jwildfire.base;

import java.io.Serializable;

import org.jwildfire.create.tina.edit.Assignable;

public class QualityProfile implements Assignable<QualityProfile>, Serializable {
  private static final long serialVersionUID = 1L;
  private String caption;
  private int spatialOversample;
  private int colorOversample;
  private int quality;
  private boolean withHDR;
  private boolean withHDRIntensityMap;
  private boolean defaultProfile;

  public QualityProfile() {

  }

  public QualityProfile(boolean pDefaultProfile, String pCaption, int pSpatialOversample, int pColorOversample, int pQuality, boolean pWithHDR, boolean pWithHDRIntensityMap) {
    defaultProfile = pDefaultProfile;
    caption = pCaption;
    spatialOversample = pSpatialOversample;
    colorOversample = pColorOversample;
    quality = pQuality;
    withHDR = pWithHDR;
    withHDRIntensityMap = pWithHDRIntensityMap;
  }

  @Override
  public String toString() {
    return caption;
  }

  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public int getSpatialOversample() {
    return spatialOversample;
  }

  public void setSpatialOversample(int spatialOversample) {
    this.spatialOversample = spatialOversample;
  }

  public int getColorOversample() {
    return colorOversample;
  }

  public void setColorOversample(int colorOversample) {
    this.colorOversample = colorOversample;
  }

  public int getQuality() {
    return quality;
  }

  public void setQuality(int quality) {
    this.quality = quality;
  }

  public boolean isWithHDR() {
    return withHDR;
  }

  public void setWithHDR(boolean withHDR) {
    this.withHDR = withHDR;
  }

  public boolean isWithHDRIntensityMap() {
    return withHDRIntensityMap;
  }

  public void setWithHDRIntensityMap(boolean withHDRIntensityMap) {
    this.withHDRIntensityMap = withHDRIntensityMap;
  }

  public boolean isDefaultProfile() {
    return defaultProfile;
  }

  public void setDefaultProfile(boolean defaultProfile) {
    this.defaultProfile = defaultProfile;
  }

  public static final int calculateQualityIndex(int pSpatialOversample, int pColorOversample, int pQuality) {
    return pSpatialOversample * pColorOversample * pQuality;
  }

  public int getQualityIndex() {
    return calculateQualityIndex(spatialOversample, colorOversample, quality);
  }

  @Override
  public void assign(QualityProfile pSrc) {
    caption = pSrc.caption;
    spatialOversample = pSrc.spatialOversample;
    colorOversample = pSrc.colorOversample;
    quality = pSrc.quality;
    withHDR = pSrc.withHDR;
    withHDRIntensityMap = pSrc.withHDRIntensityMap;
    defaultProfile = pSrc.defaultProfile;
  }

  @Override
  public QualityProfile makeCopy() {
    QualityProfile res = new QualityProfile();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(QualityProfile pSrc) {
    if (caption != pSrc.caption || spatialOversample != pSrc.spatialOversample ||
        colorOversample != pSrc.colorOversample || quality != pSrc.quality ||
        withHDR != pSrc.withHDR || withHDRIntensityMap != pSrc.withHDRIntensityMap ||
        defaultProfile != pSrc.defaultProfile) {
      return false;
    }
    return true;
  }

}
