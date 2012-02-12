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

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class TwirlTransformer extends Mesh2DTransformer {

  public enum FallOff {
    IN, OUT
  };

  @Property(description = "Twirl radius")
  @PropertyMin(0.0)
  private double radius = 220.0;

  @Property(category = PropertyCategory.SECONDARY, description = "Global zoom factor for the whole image")
  @PropertyMin(0.01)
  @PropertyMax(100.0)
  private double zoom = 1.0;

  @Property(description = "Twirl angle")
  @PropertyMin(-360.0)
  @PropertyMax(360.0)
  private double amount = 120.0;

  @Property(category = PropertyCategory.SECONDARY, description = "Twirl power")
  @PropertyMin(0.01)
  @PropertyMax(10.0)
  private double power = 2.0;

  @Property(description = "X-coordinate of the effect origin")
  private double centreX = 200.0;

  @Property(description = "Y-coordinate of the effect origin")
  private double centreY = 180.0;

  @Property(description = "Direction of fall of", editorClass = FallOffEditor.class)
  private FallOff fallOff = FallOff.OUT;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    double w1 = (double) pImg.getImageWidth() - 1.0;
    double h1 = (double) pImg.getImageHeight() - 1.0;
    double alpha = (0.0 - (double) (this.amount) * Math.PI) / (double) 180.0;
    double cx = this.centreX - 0.5;
    double cy = this.centreY - 0.5;
    double zoom = 1.0 / this.zoom;
    double radius = this.radius;
    radius *= radius;
    double power = this.power;
    double da = alpha / Math.pow(radius, power);
    Pixel pPixel = new Pixel();
    for (int pY = 0; pY < pImg.getImageHeight(); pY++) {
      for (int pX = 0; pX < pImg.getImageWidth(); pX++) {
        pPixel.setARGBValue(img.getARGBValue(pX, pY));

        double dyq = (double) pY - cy;
        double y0 = dyq * zoom;
        dyq *= dyq;
        double x0 = (double) pX - cx;
        double rr = x0 * x0 + dyq;
        x0 *= zoom;
        if (rr <= radius) {
          if (this.fallOff == FallOff.OUT)
            rr = radius - rr;
          double am = Math.pow(rr, power) * da;
          double sina = Math.sin(am);
          double cosa = Math.cos(am);
          double x = cosa * x0 + sina * y0 + cx;
          double y = -sina * x0 + cosa * y0 + cy;
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
        }
        else {
          readSrcPixels(pX, pY);
          pPixel.r = srcP.r;
          pPixel.g = srcP.g;
          pPixel.b = srcP.b;
        }
        img.setRGB(pX, pY, pPixel.r, pPixel.g, pPixel.b);
      }
    }
    applySmoothing(img, 1);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    radius = rr / 3.0;
    zoom = 1.0;
    amount = rr / 12.0;
    power = 2.0;
    centreX = (double) width / 2.1;
    centreY = (double) height / 1.9;
    fallOff = FallOff.OUT;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = zoom;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getPower() {
    return power;
  }

  public void setPower(double power) {
    this.power = power;
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

  public FallOff getFallOff() {
    return fallOff;
  }

  public void setFallOff(FallOff fallOff) {
    this.fallOff = fallOff;
  }

  public static class FallOffEditor extends ComboBoxPropertyEditor {
    public FallOffEditor() {
      super();
      setAvailableValues(new FallOff[] { FallOff.IN, FallOff.OUT });
    }
  }

}
