/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;

import org.jwildfire.create.tina.edit.Assignable;

public class SliceRenderInfo implements Assignable<SliceRenderInfo>, Serializable {
  private static final long serialVersionUID = 1L;
  private int imageWidth;
  private int imageHeight;
  private RenderMode renderMode = RenderMode.PRODUCTION;
  private int slices = 240;
  private double zmin = 0.0;
  private double zmax = 1.0;
  private int slicesPerRender = 10;

  protected SliceRenderInfo() {

  }

  public SliceRenderInfo(int pImageWidth, int pImageHeight, RenderMode pRenderMode, int pSlices, double pZMin, double pZMax, int pSlicesPerRender) {
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    renderMode = pRenderMode;
    slices = pSlices;
    zmin = pZMin;
    zmax = pZMax;
    slicesPerRender = pSlicesPerRender;
  }

  public RenderInfo createRenderInfo() {
    RenderInfo res = new RenderInfo(imageWidth, imageHeight, renderMode);
    res.setRenderHDR(false);
    res.setRenderZBuffer(false);
    return res;
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public void setImageWidth(int imageWidth) {
    this.imageWidth = imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public void setImageHeight(int imageHeight) {
    this.imageHeight = imageHeight;
  }

  @Override
  public void assign(SliceRenderInfo pSrc) {
    imageWidth = pSrc.imageWidth;
    imageHeight = pSrc.imageHeight;
    slices = pSrc.slices;
    zmin = pSrc.zmin;
    zmax = pSrc.zmax;
    slicesPerRender = pSrc.slicesPerRender;
  }

  @Override
  public SliceRenderInfo makeCopy() {
    SliceRenderInfo res = new SliceRenderInfo();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(SliceRenderInfo pSrc) {
    if (imageWidth != pSrc.imageWidth || imageHeight != pSrc.imageHeight || slices != pSrc.slices || (fabs(zmin - pSrc.zmin) > EPSILON) ||
        (fabs(zmax - pSrc.zmax) > EPSILON) || slicesPerRender != pSrc.slicesPerRender) {
      return false;
    }
    return true;
  }

  public RenderMode getRenderMode() {
    return renderMode;
  }

  public void setRenderMode(RenderMode pRenderMode) {
    renderMode = pRenderMode;
  }

  public int getSlices() {
    return slices;
  }

  public void setSlices(int pSlices) {
    slices = pSlices;
  }

  public double getZmin() {
    return zmin;
  }

  public void setZmin(double pZmin) {
    zmin = pZmin;
  }

  public double getZmax() {
    return zmax;
  }

  public void setZmax(double pZmax) {
    zmax = pZmax;
  }

  public int getSlicesPerRender() {
    return slicesPerRender;
  }

  public void setSlicesPerRender(int pSlicesPerRender) {
    slicesPerRender = pSlicesPerRender;
  }

}
