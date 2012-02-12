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
package org.jwildfire.transform;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ColorToGrayTransformer extends PixelTransformer {
  public enum Weights {
    AVERAGE, LUMINANCE, CUSTOM
  };

  @Property(category = PropertyCategory.PRIMARY, description = "How to weight the different color components")
  private Weights weights = Weights.LUMINANCE;

  @Property(category = PropertyCategory.SECONDARY, description = "Weight of the red component (if custom weights are selected)")
  @PropertyMin(0)
  @PropertyMax(10000)
  private int redWeight = 2990;

  @Property(category = PropertyCategory.SECONDARY, description = "Weight of the green component (if custom weights are selected)")
  @PropertyMin(0)
  @PropertyMax(10000)
  private int greenWeight = 5880;

  @Property(category = PropertyCategory.SECONDARY, description = "Weight of the blue component (if custom weights are selected)")
  @PropertyMin(0)
  @PropertyMax(10000)
  private int blueWeight = 1130;

  private long rs, gs, bs;

  @Override
  protected void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    int lval = (int) (((rs * (long) pPixel.r) + gs * (long) (pPixel.g) + bs * (long) (pPixel.b)) >> Tools.SPREC);
    if (lval < 0)
      lval = 0;
    else if (lval > 255)
      lval = 255;
    pPixel.r = pPixel.g = pPixel.b = lval;
  }

  @Override
  protected void initTransformation(WFImage pImg) {
    switch (weights) {
      case AVERAGE:
        rs = 3333;
        gs = 3334;
        bs = 3333;
        break;
      case LUMINANCE:
        rs = 2990;
        gs = 5880;
        bs = 1130;
        break;
      case CUSTOM:
        rs = redWeight;
        gs = greenWeight;
        bs = blueWeight;
        break;
    }
    rs = (rs * Tools.VPREC) / 10000;
    gs = (gs * Tools.VPREC) / 10000;
    bs = (bs * Tools.VPREC) / 10000;
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    weights = Weights.LUMINANCE;
    redWeight = 2990;
    greenWeight = 5880;
    blueWeight = 1130;
  }

  public Weights getWeights() {
    return weights;
  }

  public void setWeights(Weights weights) {
    this.weights = weights;
  }

  public int getRedWeight() {
    return redWeight;
  }

  public void setRedWeight(int redWeight) {
    this.redWeight = redWeight;
  }

  public int getGreenWeight() {
    return greenWeight;
  }

  public void setGreenWeight(int greenWeight) {
    this.greenWeight = greenWeight;
  }

  public int getBlueWeight() {
    return blueWeight;
  }

  public void setBlueWeight(int blueWeight) {
    this.blueWeight = blueWeight;
  }

  public static class WeightsEditor extends ComboBoxPropertyEditor {
    public WeightsEditor() {
      super();
      setAvailableValues(new Weights[] { Weights.LUMINANCE, Weights.AVERAGE, Weights.CUSTOM });
    }
  }

}
