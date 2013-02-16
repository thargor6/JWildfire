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

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class ShearTransformer extends Mesh2DTransformer {

  public enum Axis {
    X, Y
  };

  @Property(description = "X-coordinate of the effect origin")
  private double centreX = 400.0;

  @Property(description = "Y-coordinate of the effect origin")
  private double centreY = 400.0;

  @Property(description = "Shear axis", editorClass = AxisEditor.class)
  private Axis axis = Axis.X;

  @Property(category = PropertyCategory.SECONDARY, description = "Global zoom factor for the whole image")
  @PropertyMin(0.01)
  @PropertyMax(100.0)
  private double zoom = 1.0;

  @Property(description = "Damping of the effect", category = PropertyCategory.SECONDARY)
  private double damping = -0.04;

  @Property(description = "Shear amount")
  private double amount = 60.0;

  @Property(description = "Damping on/off", category = PropertyCategory.SECONDARY)
  private boolean damp = true;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    if (axis == Axis.X) {
      if (!damp || (Math.abs(damping) < MathLib.EPSILON))
        shearX(img);
      else
        shearX_Damp(img);
    }
    else if (axis == Axis.Y) {
      if (!damp || (Math.abs(damping) < MathLib.EPSILON))
        shearY(img);
      else
        shearY_Damp(img);
    }
    applySmoothing(img, 1);
  }

  private void shearX(SimpleImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel pPixel = new Pixel();
    double cx = (double) width / 2.0 - 0.5;
    double cy = (double) height / 2.0 - 0.5;
    double oy = this.centreY - cy;
    double zoom = this.zoom;
    if (zoom < 0.01)
      zoom = 0.01;
    zoom = 1.0 / zoom;
    double fshift = this.amount / (100.0 - 1.0);
    double shift = 0.0 - fshift;
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    for (int i = 0; i < height; i++) {
      double y0 = (double) i - cy;
      for (int j = 0; j < width; j++) {
        /* transform the point */
        double x0 = (double) j - cx;
        double amp = (y0 - oy) * shift;
        double x = (x0 + amp) * zoom + cx;
        double y = y0 * zoom + cy;
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
        pImg.setRGB(j, i, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  private void shearX_Damp(SimpleImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel pPixel = new Pixel();
    double cx = (double) width / 2.0 - 0.5;
    double cy = (double) height / 2.0 - 0.5;
    double ox = this.centreX - cx;
    double oy = this.centreY - cy;
    double zoom = this.zoom;
    if (zoom < 0.01)
      zoom = 0.01;
    zoom = 1.0 / zoom;
    double fshift = this.amount / (100.0 - 1.0);
    double shift = 0.0 - fshift;
    double damping = this.damping;
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    for (int i = 0; i < height; i++) {
      double y0 = (double) i - cy;
      for (int j = 0; j < width; j++) {
        /* transform the point */
        double x0 = (double) j - cx;
        double amp = (y0 - oy) * shift;
        double dl2 = x0 - ox;
        if (dl2 < 0.0)
          dl2 = 0.0 - dl2;
        amp = amp * Math.exp(dl2 * damping);
        double x = (x0 + amp) * zoom + cx;
        double y = y0 * zoom + cy;
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
        pImg.setRGB(j, i, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  private void shearY(SimpleImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel pPixel = new Pixel();
    double cx = (double) width / 2.0 - 0.5;
    double cy = (double) height / 2.0 - 0.5;
    double ox = this.centreX - cx;
    double zoom = this.zoom;
    if (zoom < 0.01)
      zoom = 0.01;
    zoom = 1.0 / zoom;
    double fshift = this.amount / (100.0 - 1.0);
    double shift = 0.0 - fshift;
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    for (int i = 0; i < height; i++) {
      double y0 = (double) i - cy;
      for (int j = 0; j < width; j++) {
        /* transform the point */
        double x0 = (double) j - cx;
        double amp = (x0 - ox) * shift;
        double x = x0 * zoom + cx;
        double y = (y0 + amp) * zoom + cy;
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
        pImg.setRGB(j, i, pPixel.r, pPixel.g, pPixel.b);

      }
    }
  }

  private void shearY_Damp(SimpleImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    Pixel pPixel = new Pixel();
    double cx = (double) width / 2.0 - 0.5;
    double cy = (double) height / 2.0 - 0.5;
    double ox = this.centreX - cx;
    double oy = this.centreY - cy;
    double zoom = this.zoom;
    if (zoom < 0.01)
      zoom = 0.01;
    zoom = 1.0 / zoom;
    double fshift = this.amount / (100.0 - 1.0);
    double shift = 0.0 - fshift;
    double damping = this.damping;
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    for (int i = 0; i < height; i++) {
      double y0 = (double) i - cy;
      for (int j = 0; j < width; j++) {
        /* transform the point */
        double x0 = (double) j - cx;
        double amp = (x0 - ox) * shift;
        double dl2 = y0 - oy;
        if (dl2 < 0.0)
          dl2 = 0.0 - dl2;
        amp = amp * Math.exp(dl2 * damping);
        double x = x0 * zoom + cx;
        double y = (y0 + amp) * zoom + cy;
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
        pImg.setRGB(j, i, pPixel.r, pPixel.g, pPixel.b);

      }
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    centreX = Math.round((double) width / 2.0);
    centreY = Math.round((double) height / 2.0);
    amount = Math.round(rr / 6);
    axis = Axis.X;
    zoom = 1.0;
    damping = -0.01;
    damp = false;
  }

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y });
    }
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

  public Axis getAxis() {
    return axis;
  }

  public void setAxis(Axis axis) {
    this.axis = axis;
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom = zoom;
  }

  public double getDamping() {
    return damping;
  }

  public void setDamping(double damping) {
    this.damping = damping;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public boolean isDamp() {
    return damp;
  }

  public void setDamp(boolean damp) {
    this.damp = damp;
  }

}
