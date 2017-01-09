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
package org.jwildfire.base;

import java.io.Serializable;

import org.jwildfire.create.tina.edit.Assignable;

public class QualityProfile implements Assignable<QualityProfile>, Serializable {
  private static final long serialVersionUID = 1L;
  private String caption;
  private int quality;
  private boolean withHDR;
  private boolean withZBuffer;
  private boolean defaultProfile;

  public QualityProfile() {

  }

  public QualityProfile(boolean pDefaultProfile, String pCaption, int pQuality, boolean pWithHDR, boolean pWithZBuffer) {
    defaultProfile = pDefaultProfile;
    caption = pCaption;
    quality = pQuality;
    withHDR = pWithHDR;
    withZBuffer = pWithZBuffer;
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

  public boolean isDefaultProfile() {
    return defaultProfile;
  }

  public void setDefaultProfile(boolean defaultProfile) {
    this.defaultProfile = defaultProfile;
  }

  public static final int calculateQualityIndex(int pQuality) {
    return pQuality;
  }

  public int getQualityIndex() {
    return calculateQualityIndex(quality);
  }

  @Override
  public void assign(QualityProfile pSrc) {
    caption = pSrc.caption;
    quality = pSrc.quality;
    withHDR = pSrc.withHDR;
    withZBuffer = pSrc.withZBuffer;
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
    if (!Tools.stringEquals(caption, pSrc.caption) || quality != pSrc.quality ||
        withHDR != pSrc.withHDR || withZBuffer != pSrc.withZBuffer ||
        defaultProfile != pSrc.defaultProfile) {
      return false;
    }
    return true;
  }

  public boolean isWithZBuffer() {
    return withZBuffer;
  }

  public void setWithZBuffer(boolean withZBuffer) {
    this.withZBuffer = withZBuffer;
  }

}
