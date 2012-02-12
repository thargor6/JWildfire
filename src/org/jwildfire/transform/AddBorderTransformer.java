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

import java.awt.Color;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.create.PlainImageCreator;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer.BufferType;

public class AddBorderTransformer extends Transformer {
  @Property(description = "Size of the left border")
  @PropertyMin(0)
  private int leftSize = 24;

  @Property(description = "Size of the top border")
  @PropertyMin(0)
  private int topSize = 24;

  @Property(description = "Size of the right border")
  @PropertyMin(0)
  private int rightSize = 24;

  @Property(description = "Size of the bottom border")
  @PropertyMin(0)
  private int bottomSize = 24;

  @Property(description = "Border color")
  private Color color = new Color(240, 240, 222);

  @Override
  public boolean supports3DOutput() {
    return false;
  }

  @Override
  protected void performImageTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    if ((leftSize > 0) || (topSize > 0) || (rightSize > 0) || (bottomSize > 0)) {
      PlainImageCreator creator = new PlainImageCreator();
      creator.setBgColor(color);
      SimpleImage bgImg = creator.createImage(pImg.getImageWidth() + leftSize + rightSize,
          pImg.getImageHeight() + topSize + bottomSize);
      ComposeTransformer cT = new ComposeTransformer();
      cT.setForegroundImage(img);
      cT.setLeft(leftSize);
      cT.setTop(topSize);
      cT.setGenlock(ComposeTransformer.Genlock.NONE);
      cT.setHAlign(ComposeTransformer.HAlignment.OFF);
      cT.setVAlign(ComposeTransformer.VAlignment.OFF);
      cT.transformImage(bgImg);
      img.setBufferedImage(bgImg.getBufferedImg(), bgImg.getImageWidth(), bgImg.getImageHeight());
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    leftSize = 24;
    topSize = 24;
    rightSize = 24;
    bottomSize = 24;
    color = new Color(240, 240, 222);
  }

  @Override
  public boolean acceptsInputBufferType(BufferType pBufferType) {
    return pBufferType.equals(BufferType.IMAGE);
  }

  public int getLeftSize() {
    return leftSize;
  }

  public void setLeftSize(int leftSize) {
    this.leftSize = leftSize;
  }

  public int getTopSize() {
    return topSize;
  }

  public void setTopSize(int topSize) {
    this.topSize = topSize;
  }

  public int getRightSize() {
    return rightSize;
  }

  public void setRightSize(int rightSize) {
    this.rightSize = rightSize;
  }

  public int getBottomSize() {
    return bottomSize;
  }

  public void setBottomSize(int bottomSize) {
    this.bottomSize = bottomSize;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

}
