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

public class Polar2CartesianTransformer extends Mesh2DTransformer {

  @Property(category = PropertyCategory.SECONDARY, description = "Global zoom factor for the whole image")
  @PropertyMin(0.01)
  @PropertyMax(100.0)
  private double zoom = 0.80;

  @Property(description = "Repeat the image at the borders")
  private boolean wrap = true;

  @Property(description = "R offset")
  private double r0 = -60.0;

  @Property(description = "Phi offset")
  private double phi0 = 12.0;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    double zoom;
    if (this.zoom != 0.0)
      zoom = 1.0 / this.zoom;
    else
      zoom = 1.0;
    zoom *= 0.8;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    boolean wrap = this.wrap;

    double cx = (double) width / 2 - 0.5;
    double cy = (double) height / 2 - 0.5;
    double daScale = (double) (width - 1) / (Math.PI + Math.PI);

    double a0 = this.phi0;
    double r0 = this.r0;
    Pixel pPixel = new Pixel();
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    for (int i = 0; i < height; i++) {
      double dr = zoom * ((double) i + r0);
      if (dr < 0)
        dr = 0 - dr;
      for (int j = 0; j < width; j++) {
        /* transform the point */
        double da = ((double) j + a0) / daScale;
        double x = dr * Math.sin(da) + cx;
        double y = dr * Math.cos(da) + cy;

        if (wrap) {
          while (x > ((double) width - 0.1))
            x -= (double) width - 1.2;
          while ((int) x < 0.1)
            x += (double) width - 1.2;
        }
        if (wrap) {
          while (y > ((double) height - 0.1))
            y -= (double) height - 1.2;
          while ((int) y < 0.1)
            y += (double) height - 1.2;
        }

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
        img.setRGB(j, height - i - 1, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    zoom = 0.80;
    wrap = true;
    r0 = -60.0;
    phi0 = -12.0;
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = zoom;
  }

  public boolean isWrap() {
    return wrap;
  }

  public void setWrap(boolean wrap) {
    this.wrap = wrap;
  }

  public double getR0() {
    return r0;
  }

  public void setR0(double r0) {
    this.r0 = r0;
  }

  public double getPhi0() {
    return phi0;
  }

  public void setPhi0(double phi0) {
    this.phi0 = phi0;
  }

}
