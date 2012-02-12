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
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

public class MagnetTransformer extends Mesh2DTransformer {
  @Property(category = PropertyCategory.PRIMARY, description = "How many pixels are translated at the centre")
  private double amount = -10.0;

  @Property(category = PropertyCategory.PRIMARY, description = "Damping of the effect")
  private double damping = -0.05;

  @Property(category = PropertyCategory.SECONDARY, description = "Global zoom factor for the whole image")
  @PropertyMin(0.01)
  @PropertyMax(100.0)
  private double zoom = 1.0;

  @Property(category = PropertyCategory.PRIMARY, description = "X-coordinate of the centre")
  private int centreX = 400;

  @Property(category = PropertyCategory.PRIMARY, description = "Y-coordinate of the centre")
  private int centreY = 400;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    double cx = centreX - 0.5;
    double cy = centreY - 0.5;
    double zoom = 1.0 / this.zoom;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double famount = this.amount;
    double fdamping = this.damping;

    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    Pixel pPixel = new Pixel();
    for (int i = 0; i < height; i++) {
      double dyq = (double) i - cy;
      double y0 = dyq * zoom;
      dyq *= dyq;
      for (int j = 0; j < width; j++) {
        pPixel.setARGBValue(img.getARGBValue(j, i));

        /* transform the point */
        double x0 = (double) j - cx;
        double rr = Math.sqrt(x0 * x0 + dyq);
        x0 *= zoom;
        double amount = famount * Math.exp(rr * fdamping);
        double scl;
        if (rr != 0.0)
          scl = (rr + amount) / rr;
        else
          scl = 0.0;
        double x = x0 * scl + cx;
        double y = y0 * scl + cy;

        /* render it */
        double xi = Tools.fmod33(x);
        double yi = Tools.fmod33(y);
        if ((x < 0.0) || (x > w1) || (y < 0.0) || (y > h1)) {
          pPixel.r = pPixel.g = pPixel.b = 0;
        }
        else {
          readSrcPixels(x, y);
          pPixel.r = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.r) + xi * (srcQ.r)) + yi
              * ((1.0 - xi) * (srcR.r) + xi * (srcS.r))));
          pPixel.g = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.g) + xi * (srcQ.g)) + yi
              * ((1.0 - xi) * (srcR.g) + xi * (srcS.g))));
          pPixel.b = roundColor(((1.0 - yi) * ((1.0 - xi) * (srcP.b) + xi * (srcQ.b)) + yi
              * ((1.0 - xi) * (srcR.b) + xi * (srcS.b))));
        }
        img.setRGB(j, i, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    zoom = 1.0;
    centreX = (int) (width / 2.1 + 0.5);
    centreY = (int) (height / 5.0 + 0.5);
    double rr = Math.sqrt(width * width + height * height);
    amount = -rr / 3.0;
    damping = -0.005;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getDamping() {
    return damping;
  }

  public void setDamping(double damping) {
    this.damping = damping;
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = zoom;
  }

  public int getCentreX() {
    return centreX;
  }

  public void setCentreX(int centreX) {
    this.centreX = centreX;
  }

  public int getCentreY() {
    return centreY;
  }

  public void setCentreY(int centreY) {
    this.centreY = centreY;
  }

}
