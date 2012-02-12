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
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class CropTransformer extends Mesh2DTransformer {

  @Property(description = "Left edge of the cropped region")
  @PropertyMin(0)
  private int left = 0;

  @Property(description = "Top edge of the cropped region")
  @PropertyMin(0)
  private int top = 0;

  @Property(description = "Width of the cropped region")
  private int width = 200;

  @Property(description = "Height of the cropped region")
  private int height = 200;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int imgWidth = pImg.getImageWidth();
    int imgHeight = pImg.getImageHeight();
    int left = this.left;
    if (left < 0)
      left = 0;
    else if (left > imgWidth - 1)
      left = imgWidth - 1;
    int top = this.top;
    if (top < 0)
      top = 0;
    else if (top > imgHeight - 1)
      top = imgHeight - 1;
    int width = this.width;
    if (width < 1)
      width = 1;
    else if (left + width > imgWidth)
      width = imgWidth - left;
    int height = this.height;
    if (height < 1)
      height = 1;
    else if (top + height > imgHeight)
      height = imgHeight - top;
    if ((width == imgWidth) && (height == imgHeight))
      return;
    img.resetImage(width, height);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        img.setARGB(j, i, srcImg.getARGBValue(j + left, i + top));
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    this.left = width / 7;
    this.top = height / 7;
    this.width = width - 2 * this.left;
    this.height = height - 2 * this.height;
  }

  public int getLeft() {
    return left;
  }

  public void setLeft(int left) {
    this.left = left;
  }

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }
}
