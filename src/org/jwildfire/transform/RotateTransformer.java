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

public class RotateTransformer extends Mesh2DTransformer {

  @Property(description = "X-coordinate of the effect origin")
  private double centreX = 400.0;

  @Property(description = "Y-coordinate of the effect origin")
  private double centreY = 400.0;

  @Property(category = PropertyCategory.SECONDARY, description = "Global zoom factor for the whole image")
  @PropertyMin(0.01)
  @PropertyMax(100.0)
  private double zoom = 1.0;

  @Property(description = "Rotation radius")
  @PropertyMin(0.00)
  private double radius = 100.0;

  @Property(description = "Rotation amount")
  private double amount = 30.0;

  @Property(description = "Smoothing amount", category = PropertyCategory.SECONDARY)
  private int smoothing = 1;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel pPixel = new Pixel();
    double alpha = (0.0 - this.amount) * Math.PI / 180.0;
    double cosa = Math.cos(alpha);
    double sina = Math.sin(alpha);
    double cx = this.centreX - 0.5;
    double cy = this.centreY - 0.5;
    double zoom = this.zoom;
    if (zoom < 0.01)
      zoom = 0.01;
    zoom = 1.0 / zoom;
    double radius = this.radius * this.radius;

    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    for (int i = 0; i < height; i++) {
      double dyq = (double) i - cy;
      double y0 = dyq * zoom;
      dyq *= dyq;
      for (int j = 0; j < width; j++) {
        /* transform the point */
        double x0 = (double) j - cx;
        double rr = x0 * x0 + dyq;
        if (rr <= radius) {
          x0 *= zoom;
          double x = cosa * x0 + sina * y0 + cx;
          double y = -sina * x0 + cosa * y0 + cy;

          /* render it */
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
    applySmoothing(img, smoothing);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    centreX = Math.round((double) width / 2.0);
    centreY = Math.round((double) height / 2.0);
    radius = Math.round(rr / 6);
    amount = 30.0;
    zoom = 1.0;
    smoothing = 1;
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

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = zoom;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public int getSmoothing() {
    return smoothing;
  }

  public void setSmoothing(int smoothing) {
    this.smoothing = smoothing;
  }
}
