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

public class RotateBlurTransformer extends Mesh2DTransformer {

  @Property(description = "X-coordinate of the effect origin")
  private double centreX = 400.0;

  @Property(description = "Y-coordinate of the effect origin")
  private double centreY = 400.0;

  @Property(description = "Blur radius")
  @PropertyMin(0.00)
  private int radius = 100;

  @Property(description = "Blur amount")
  private int amount = 3;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    if (this.radius < 1)
      return;
    SimpleImage img = (SimpleImage) pImg;
    int amount = this.amount;
    if (amount < 0)
      amount = 0 - amount;
    else if (amount == 0)
      return;
    for (int i = 1; i <= amount; i++) {
      SimpleImage tmpImg;
      for (int pass = 0; pass <= 1; pass++) {
        tmpImg = img.clone();
        {
          RotateTransformer rT = new RotateTransformer();
          rT.setZoom(1.0);
          rT.setCentreX(this.centreX);
          rT.setCentreY(this.centreY);
          rT.setRadius(this.radius);
          rT.setSmoothing(0);
          rT.setAmount((pass == 0) ? (double) i : (double) 0 - i);
          rT.transformImage(tmpImg);
        }
        {
          ComposeTransformer cT = new ComposeTransformer();
          cT.setTransparency(25);
          cT.setGenlock(ComposeTransformer.Genlock.NONE);
          cT.setForegroundImage(tmpImg);
          cT.transformImage(img);
        }
      }
    }
  }

  public void initDefaultParams(WFImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    centreX = Math.round((double) width / 2.0);
    centreY = Math.round((double) height / 2.0);
    radius = (int) (rr / 6.0 + 0.5);
    amount = 3;
  }

  public double getCentreX() {
    return centreX;
  }

  public void setCentreX(double centreX) {
    this.centreX = centreX;
  }

  public double getCentreY() {
    return centreY;
  }

  public void setCentreY(double centreY) {
    this.centreY = centreY;
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

}
