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

public class WaveTransformer extends Mesh2DTransformer {

  public enum Axis {
    X, Y
  };

  @Property(description = "X-coordinate of the wave origin")
  private double centreX = 0.0;

  @Property(description = "Y-coordinate of the wave origin")
  private double centreY = 0.0;

  @Property(description = "Propagation axis of the wave", editorClass = AxisEditor.class)
  private Axis axis = Axis.X;

  @Property(description = "Number of frames to describe a complete phase", category = PropertyCategory.SECONDARY)
  @PropertyMin(1)
  private int frames = 60;

  @Property(description = "Current frames", category = PropertyCategory.SECONDARY)
  @PropertyMin(1)
  private int frame = 33;

  @Property(category = PropertyCategory.SECONDARY, description = "Global zoom factor for the whole image")
  @PropertyMin(0.01)
  @PropertyMax(100.0)
  private double zoom = 1.0;

  @Property(description = "Damping of the wave", category = PropertyCategory.SECONDARY)
  private double damping = -0.5;

  @Property(description = "Amplitude of the wave")
  @PropertyMin(0.0)
  private double amplitude = 30.0;

  @Property(description = "Wavelength of the wave")
  @PropertyMin(0.0)
  private double wavelength = 150.0;

  @Property(description = "Phase shift of the Wave", category = PropertyCategory.SECONDARY)
  private double phase = 0.0;

  // TODO unklar: was genau macht dieser Parameter?
  // @Property()
  private double shift = 0.0;

  @Property(description = "Damping on/off", category = PropertyCategory.SECONDARY)
  private boolean damp = true;

  @Property(description = "Repeat the image at the borders")
  private boolean wrap = true;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    if (!damp) {
      if (axis == Axis.X)
        waveX(img);
      else
        waveY(img);
    }
    else {
      if (axis == Axis.X)
        waveX_damp(img);
      else
        waveY_damp(img);
    }
  }

  private void waveY_damp(SimpleImage pImg) {
    double damping = this.damping;
    double cx = this.centreX - 0.5;
    double cy = this.centreY - 0.5;
    double zoom = 1.0 / this.zoom;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double PI2 = 2.0 * Math.PI;
    double shift = this.shift;
    double wavelength = this.wavelength * this.zoom;
    double amplitude = 0.0 - this.amplitude * this.zoom;
    double phase = this.phase;
    double amp = amplitude;
    double t = this.frames != 0 ? (double) this.frame / (double) this.frames : 0.0;
    shift = 2.0 * shift / (double) (width - 1);
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;

    Pixel pPixel = new Pixel();
    for (int pY = 0; pY < height; pY++) {
      for (int pX = 0; pX < width; pX++) {
        pPixel.setARGBValue(pImg.getARGBValue(pX, pY));

        double x0 = (double) pX - cx;
        double y0 = (double) pY - cy;
        double sangle = shift * pX;
        double dl = (y0 - sangle) / wavelength;

        double dl2 = dl - sangle;
        if (dl2 < 0)
          dl2 = 0.0 - dl2;
        amp = amplitude * Math.exp(dl2 * damping);

        double zz = amp * Math.sin((PI2 * (t - dl)) + phase);
        x0 += zz;
        double x = x0 * zoom + cx;
        double y = y0 * zoom + cy;

        /* color-wrapping */
        if (this.wrap) {
          while (x >= ((double) width - 0.5))
            x -= (double) (width - 1);
          while ((int) x < 0.5)
            x += (double) (width - 1);
          while (y >= ((double) height - 0.5))
            y -= (double) (height - 1);
          while ((int) y < 0.5)
            y += (double) (height - 1);
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
        pImg.setRGB(pX, pY, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  private void waveX_damp(SimpleImage pImg) {
    double damping = this.damping;
    double cx = this.centreX - 0.5;
    double cy = this.centreY - 0.5;
    double zoom = 1.0 / this.zoom;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double PI2 = 2.0 * Math.PI;
    double shift = this.shift;
    double wavelength = this.wavelength * this.zoom;
    double amplitude = 0.0 - this.amplitude * this.zoom;
    double phase = this.phase;
    double amp = amplitude;
    double t = this.frames != 0 ? (double) this.frame / (double) this.frames : 0.0;
    shift = 2.0 * shift / (double) (height - 1);
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;

    Pixel pPixel = new Pixel();
    for (int pY = 0; pY < height; pY++) {
      for (int pX = 0; pX < width; pX++) {
        pPixel.setARGBValue(pImg.getARGBValue(pX, pY));
        /* transform the point */
        double x0 = (double) pX - cx;
        double y0 = (double) pY - cy;
        double sangle = shift * pY;
        double dl = (x0 - sangle) / wavelength;

        double dl2 = dl - sangle;
        if (dl2 < 0)
          dl2 = 0.0 - dl2;
        amp = amplitude * Math.exp(dl2 * damping);

        double zz = amp * Math.sin((PI2 * (t - dl)) + phase);
        y0 += zz;
        double x = x0 * zoom + cx;
        double y = y0 * zoom + cy;

        /* color-wrapping */
        if (this.wrap) {
          while (x >= ((double) width - 0.5))
            x -= (double) (width - 1);
          while ((int) x < 0.5)
            x += (double) (width - 1);
          while (y >= ((double) height - 0.5))
            y -= (double) (height - 1);
          while ((int) y < 0.5)
            y += (double) (height - 1);
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
        pImg.setRGB(pX, pY, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  private void waveY(SimpleImage pImg) {
    double cx = this.centreX - 0.5;
    double cy = this.centreY - 0.5;
    double zoom = 1.0 / this.zoom;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double PI2 = 2.0 * Math.PI;
    double shift = this.shift;
    double wavelength = this.wavelength * this.zoom;
    double amplitude = 0.0 - this.amplitude * this.zoom;
    double phase = this.phase;
    shift = 2.0 * shift / (double) (width - 1);
    double amp = amplitude;
    double t = this.frames != 0 ? (double) this.frame / (double) this.frames : 0.0;
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    Pixel pPixel = new Pixel();
    for (int pY = 0; pY < height; pY++) {
      for (int pX = 0; pX < width; pX++) {
        pPixel.setARGBValue(pImg.getARGBValue(pX, pY));
        /* transform the point */
        double x0 = (double) pX - cx;
        double y0 = (double) pY - cy;
        double dl = (y0 - shift * pX) / wavelength;
        double zz = amp * Math.sin((PI2 * (t - dl)) + phase);
        x0 += zz;
        double x = x0 * zoom + cx;
        double y = y0 * zoom + cy;

        /* color-wrapping */
        if (this.wrap) {
          while (x >= ((double) width - 0.5))
            x -= (double) (width - 1);
          while ((int) x < 0.5)
            x += (double) (width - 1);
          while (y >= ((double) height - 0.5))
            y -= (double) (height - 1);
          while ((int) y < 0.5)
            y += (double) (height - 1);
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
        pImg.setRGB(pX, pY, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  private void waveX(SimpleImage pImg) {
    double cx = this.centreX - 0.5;
    double cy = this.centreY - 0.5;
    double zoom = 1.0 / this.zoom;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double PI2 = 2.0 * Math.PI;
    double shift = this.shift;
    double wavelength = this.wavelength * this.zoom;
    double amplitude = 0.0 - this.amplitude * this.zoom;
    double phase = this.phase;
    shift = 2.0 * shift / (double) (height - 1);
    double amp = amplitude;
    double t = this.frames != 0 ? (double) this.frame / (double) this.frames : 0.0;
    double w1 = (double) width - 1.0;
    double h1 = (double) height - 1.0;
    Pixel pPixel = new Pixel();
    for (int pY = 0; pY < height; pY++) {
      for (int pX = 0; pX < width; pX++) {
        pPixel.setARGBValue(pImg.getARGBValue(pX, pY));
        /* transform the point */
        double x0 = (double) pX - cx;
        double y0 = (double) pY - cy;
        double dl = (x0 - shift * pY) / wavelength;
        double zz = amp * Math.sin((PI2 * (t - dl)) + phase);
        y0 += zz;
        double x = x0 * zoom + cx;
        double y = y0 * zoom + cy;
        /* color-wrapping */
        if (this.wrap) {
          while (x >= ((double) width - 0.5))
            x -= (double) (width - 1);
          while ((int) x < 0.5)
            x += (double) (width - 1);
          while (y >= ((double) height - 0.5))
            y -= (double) (height - 1);
          while ((int) y < 0.5)
            y += (double) (height - 1);
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
        pImg.setRGB(pX, pY, pPixel.r, pPixel.g, pPixel.b);
      }
    }
  }

  @Override
  protected void cleanupTransformation(WFImage pImg) {
    super.cleanupTransformation(pImg);
    applySmoothing((SimpleImage) pImg, 1);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    centreX = 0.10;
    centreY = -0.50;
    axis = Axis.X;
    frames = 60;
    frame = 33;
    zoom = 1.0;
    damping = -0.5;
    amplitude = Math.round(rr / 20.0);
    wavelength = Math.round(rr / 4.0);
    phase = 0.0;
    shift = 0.0;
    damp = true;
    wrap = true;
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

  public int getFrames() {
    return frames;
  }

  public void setFrames(int frames) {
    this.frames = frames;
  }

  public int getFrame() {
    return frame;
  }

  public void setFrame(int frame) {
    this.frame = frame;
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

  public double getAmplitude() {
    return amplitude;
  }

  public void setAmplitude(double amplitude) {
    this.amplitude = amplitude;
  }

  public double getWavelength() {
    return wavelength;
  }

  public void setWavelength(double wavelength) {
    this.wavelength = wavelength;
  }

  public double getPhase() {
    return phase;
  }

  public void setPhase(double phase) {
    this.phase = phase;
  }

  public double getShift() {
    return shift;
  }

  public void setShift(double shift) {
    this.shift = shift;
  }

  public boolean isDamp() {
    return damp;
  }

  public void setDamp(boolean damp) {
    this.damp = damp;
  }

  public boolean isWrap() {
    return wrap;
  }

  public void setWrap(boolean wrap) {
    this.wrap = wrap;
  }

  public static class AxisEditor extends ComboBoxPropertyEditor {
    public AxisEditor() {
      super();
      setAvailableValues(new Axis[] { Axis.X, Axis.Y });
    }
  }
}
