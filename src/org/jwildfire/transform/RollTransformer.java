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
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class RollTransformer extends Mesh2DTransformer {

  @Property(description = "Shift amount in horizontal direction")
  private int deltaX = 30;

  @Property(description = "Shift amount in vertical direction")
  private int deltaY = 0;

  @Property(description = "Repeat the image at the borders")
  private boolean wrap = true;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    if ((this.deltaX == 0) && (this.deltaY == 0))
      return;
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();

    if (!this.wrap) {
      img.fillBackground(0, 0, 0);
      if ((this.deltaX <= -width) || (this.deltaX >= width) || (this.deltaY < -height)
          || (this.deltaY > height))
      {
        return;
      }
      else {
        ComposeTransformer cT = new ComposeTransformer();
        cT.setHAlign(ComposeTransformer.HAlignment.OFF);
        cT.setVAlign(ComposeTransformer.VAlignment.OFF);
        cT.setLeft(this.deltaX);
        cT.setTop(this.deltaY);
        cT.setForegroundImage(srcImg);
        cT.transformImage(img);
      }
    }
    else {
      ComposeTransformer cT = new ComposeTransformer();
      cT.setHAlign(ComposeTransformer.HAlignment.OFF);
      cT.setVAlign(ComposeTransformer.VAlignment.OFF);
      cT.setForegroundImage(srcImg);
      int left = this.deltaX;
      int top = this.deltaY;
      while (left < 0)
        left += width;
      while (left >= width)
        left -= width;
      while (top < 0)
        top += height;
      while (top >= height)
        top -= height;

      cT.setLeft(left);
      cT.setTop(top);
      cT.transformImage(img);

      cT.setLeft(left - width);
      cT.setTop(top);
      cT.transformImage(img);

      cT.setLeft(left);
      cT.setTop(top - height);
      cT.transformImage(img);

      cT.setLeft(left - width);
      cT.setTop(top - height);
      cT.transformImage(img);
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    deltaX = pImg.getImageWidth() / 7;
    deltaY = 0;
    wrap = true;
  }

  public int getDeltaX() {
    return deltaX;
  }

  public void setDeltaX(int deltaX) {
    this.deltaX = deltaX;
  }

  public int getDeltaY() {
    return deltaY;
  }

  public void setDeltaY(int deltaY) {
    this.deltaY = deltaY;
  }

  public boolean isWrap() {
    return wrap;
  }

  public void setWrap(boolean wrap) {
    this.wrap = wrap;
  }
}
