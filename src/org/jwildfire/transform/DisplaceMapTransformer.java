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
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer;
import org.jwildfire.swing.NonHDRImageBufferComboBoxEditor;

public class DisplaceMapTransformer extends Mesh2DTransformer {
  @Property(description = "Image which holds the displacement information for the x-axis (red channel)", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer displaceXMap;
  @Property(description = "Image which holds the displacement information for the y-axis (green channel)", editorClass = NonHDRImageBufferComboBoxEditor.class)
  private Buffer displaceYMap;

  @Property(category = PropertyCategory.SECONDARY, description = "Global zoom factor for the whole image")
  @PropertyMin(0.01)
  @PropertyMax(100.0)
  private double zoom;

  @Property(description = "Displacement amount")
  private double amount;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    if ((Math.abs(this.amount) < MathLib.EPSILON) || ((displaceXMap == null) && (displaceYMap == null)))
      return;
    SimpleImage img = (SimpleImage) pImg;
    double nAmount = this.amount / 127.5;
    double rZoom = 1.0 / this.zoom;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    SimpleImage xMap = (displaceXMap != null) ? displaceXMap.getImage() : null;
    SimpleImage yMap = (displaceYMap != null) ? displaceYMap.getImage() : null;
    Pixel pPixel = new Pixel();
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    double cx = (double) width / 2.0;
    double cy = (double) height / 2.0;
    for (int pY = 0; pY < height; pY++) {
      for (int pX = 0; pX < width; pX++) {
        double x0 = pX - cx;
        double y0 = pY - cy;

        double dX, dY;
        if (xMap == null)
          dX = 0.0;
        else
          dX = (double) (xMap.getRValueIgnoreBounds(pX, pY) - 127.5) * nAmount;
        if (yMap == null)
          dY = 0.0;
        else
          dY = (double) (yMap.getGValueIgnoreBounds(pX, pY) - 127.5) * nAmount;

        double x = cx + (x0 + dX) * rZoom;
        double y = cy + (y0 + dY) * rZoom;

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
        img.setRGB(pX, pY, pPixel.r, pPixel.g, pPixel.b);
      }
    }
    applySmoothing(img, 3);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    zoom = 1.0;
    amount = 50.0;
  }

  public Buffer getDisplaceXMap() {
    return displaceXMap;
  }

  public void setDisplaceXMap(Buffer displaceXMap) {
    this.displaceXMap = displaceXMap;
  }

  public Buffer getDisplaceYMap() {
    return displaceYMap;
  }

  public void setDisplaceYMap(Buffer displaceYMap) {
    this.displaceYMap = displaceYMap;
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
}
