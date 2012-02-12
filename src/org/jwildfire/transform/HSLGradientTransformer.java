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
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class HSLGradientTransformer extends Mesh2DTransformer {
  public enum Mode {
    HUE, SATURATION, LUMINOSITY
  }

  public enum Transition {
    RADIAL, PARALLEL
  }

  @Property(description = "Balancing mode", editorClass = ModeEditor.class)
  private Mode mode = Mode.HUE;

  @Property(description = "X-coordinate of effect source")
  private int x1 = 64;

  @Property(description = "Y-coordinate of effect source")
  private int y1 = 51;

  @Property(description = "Effect value at the effect source")
  private int value1 = 64;

  @Property(description = "Transition mode", editorClass = TransitionEditor.class)
  private Transition transition = Transition.RADIAL;

  @Property(description = "X-coordinate of effect destination (parallel transition)")
  private int x2 = 256;

  @Property(description = "Y-coordinate of effect destination (parallel transition)")
  private int y2 = 204;

  @Property(description = "Effect value at the effect destination")
  private int value2 = 100;

  @Property(description = "Effect radius (radial transition)")
  private int radius = 120;

  @Property(description = "Base radius keeping the initial value (radial transition)")
  private int baseRadius;

  @Override
  protected void performPixelTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();

    int x1 = this.x1;
    int y1 = this.y1;
    int v1 = this.value1;
    int x2 = this.x2;
    int y2 = this.y2;
    int v2 = this.value2;

    double rx = (double) (x2 - x1);
    double ry = (double) (y2 - y1);
    double dv = (double) (v2 - v1);
    double vlen;
    if (this.transition == Transition.PARALLEL)
      vlen = Math.sqrt(rx * rx + ry * ry);
    else
      vlen = this.radius - this.baseRadius;
    if (vlen < 0.0001)
      vlen = 0.0001;
    double vlenq = vlen * vlen;

    Pixel rgbPixel = new Pixel();
    HSLTransformer.HSLPixel hslPixel = new HSLTransformer.HSLPixel();
    switch (this.mode) {
      case HUE:
        if (this.transition == Transition.PARALLEL) {
          for (int i = 0; i < height; i++) {
            double ay = (double) (i - y1);
            for (int j = 0; j < width; j++) {
              double ax = (double) (j - x1);
              double prj = (ax * rx + ay * ry) / vlenq;
              if (prj < 0.0)
                prj = 0.0;
              else if (prj > 1.0)
                prj = 1.0;
              double phue = (v1 + dv * prj) / 255.0;
              if (phue < (-1.0))
                phue = -1;
              else if (phue > 1.0)
                phue = 1.0;
              rgbPixel.setARGBValue(img.getARGBValue(j, i));
              HSLTransformer.rgb2hsl(rgbPixel, hslPixel);
              if (hslPixel.hue != (-1.0)) {
                hslPixel.hue += phue;
                if (hslPixel.hue < 0.0)
                  hslPixel.hue += 1.0;
                else if (hslPixel.hue > 1.0)
                  hslPixel.hue -= 1.0;
              }
              HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
              img.setRGB(j, i, rgbPixel);
            }
          }
        }
        else {
          for (int i = 0; i < height; i++) {
            double ay = (double) (i - y1);
            for (int j = 0; j < width; j++) {
              double ax = (double) (j - x1);
              double prj = (Math.sqrt(ax * ax + ay * ay) - baseRadius) / vlen;
              if (prj < 0.0)
                prj = 0.0;
              else if (prj > 1.0)
                prj = 1.0;
              double phue = (v1 + dv * prj) / 255.0;
              if (phue < (-1.0))
                phue = -1;
              else if (phue > 1.0)
                phue = 1.0;
              rgbPixel.setARGBValue(img.getARGBValue(j, i));
              HSLTransformer.rgb2hsl(rgbPixel, hslPixel);
              if (hslPixel.hue != (-1.0)) {
                hslPixel.hue += phue;
                if (hslPixel.hue < 0.0)
                  hslPixel.hue += 1.0;
                else if (hslPixel.hue > 1.0)
                  hslPixel.hue -= 1.0;
              }
              HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
              img.setRGB(j, i, rgbPixel);
            }
          }
        }
        break;
      case SATURATION:
        if (this.transition == Transition.PARALLEL) {
          for (int i = 0; i < height; i++) {
            double ay = (double) (i - y1);
            for (int j = 0; j < width; j++) {
              double ax = (double) (j - x1);
              double prj = (ax * rx + ay * ry) / vlenq;
              if (prj < 0.0)
                prj = 0.0;
              else if (prj > 1.0)
                prj = 1.0;
              double psaturation = (v1 + dv * prj) / 255.0;
              if (psaturation < (-1.0))
                psaturation = -1.0;
              else if (psaturation > 1.0)
                psaturation = 1.0;
              rgbPixel.setARGBValue(img.getARGBValue(j, i));
              HSLTransformer.rgb2hsl(rgbPixel, hslPixel);
              hslPixel.saturation += psaturation;
              if (hslPixel.saturation < 0.0)
                hslPixel.saturation = 0.0;
              else if (hslPixel.saturation > 1.0)
                hslPixel.saturation = 1.0;
              HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
              img.setRGB(j, i, rgbPixel);
            }
          }
        }
        else {
          for (int i = 0; i < height; i++) {
            double ay = (double) (i - y1);
            for (int j = 0; j < width; j++) {
              double ax = (double) (j - x1);
              double prj = (Math.sqrt(ax * ax + ay * ay) - baseRadius) / vlen;
              if (prj < 0.0)
                prj = 0.0;
              else if (prj > 1.0)
                prj = 1.0;
              double psaturation = (v1 + dv * prj) / 255.0;
              if (psaturation < (-1.0))
                psaturation = -1.0;
              else if (psaturation > 1.0)
                psaturation = 1.0;
              rgbPixel.setARGBValue(img.getARGBValue(j, i));
              HSLTransformer.rgb2hsl(rgbPixel, hslPixel);
              hslPixel.saturation += psaturation;
              if (hslPixel.saturation < 0.0)
                hslPixel.saturation = 0.0;
              else if (hslPixel.saturation > 1.0)
                hslPixel.saturation = 1.0;
              HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
              img.setRGB(j, i, rgbPixel);
            }
          }
        }
        break;
      case LUMINOSITY:
        if (this.transition == Transition.PARALLEL) {
          for (int i = 0; i < height; i++) {
            double ay = (double) (i - y1);
            for (int j = 0; j < width; j++) {
              double ax = (double) (j - x1);
              double prj = (ax * rx + ay * ry) / vlenq;
              if (prj < 0.0)
                prj = 0.0;
              else if (prj > 1.0)
                prj = 1.0;
              double pluminosity = (v1 + dv * prj) / 255.0;
              if (pluminosity < (-1.0))
                pluminosity = -1;
              else if (pluminosity > 1.0)
                pluminosity = 1.0;
              rgbPixel.setARGBValue(img.getARGBValue(j, i));
              HSLTransformer.rgb2hsl(rgbPixel, hslPixel);
              hslPixel.luminosity += pluminosity;
              if (hslPixel.luminosity < 0.0)
                hslPixel.luminosity = 0.0;
              else if (hslPixel.luminosity > 1.0)
                hslPixel.luminosity = 1.0;
              HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
              img.setRGB(j, i, rgbPixel);
            }
          }
        }
        else {
          for (int i = 0; i < height; i++) {
            double ay = (double) (i - y1);
            for (int j = 0; j < width; j++) {
              double ax = (double) (j - x1);
              double prj = (Math.sqrt(ax * ax + ay * ay) - baseRadius) / vlen;
              if (prj < 0.0)
                prj = 0.0;
              else if (prj > 1.0)
                prj = 1.0;
              double pluminosity = (v1 + dv * prj) / 255.0;
              if (pluminosity < (-1.0))
                pluminosity = -1;
              else if (pluminosity > 1.0)
                pluminosity = 1.0;
              rgbPixel.setARGBValue(img.getARGBValue(j, i));
              HSLTransformer.rgb2hsl(rgbPixel, hslPixel);
              hslPixel.luminosity += pluminosity;
              if (hslPixel.luminosity < 0.0)
                hslPixel.luminosity = 0.0;
              else if (hslPixel.luminosity > 1.0)
                hslPixel.luminosity = 1.0;
              HSLTransformer.hsl2rgb(hslPixel, rgbPixel);
              img.setRGB(j, i, rgbPixel);
            }
          }
        }
        break;
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    x1 = (int) ((double) width / 2.1 + 0.5);
    y1 = (int) ((double) height / 1.9 + 0.5);
    x2 = width - (int) ((double) width / 7.0 + 0.5);
    y2 = height - (int) ((double) height / 9.0 + 0.5);
    radius = (int) (rr * 0.45 + 0.5);
    value1 = 120;
    value2 = -180;
    mode = Mode.HUE;
    transition = Transition.RADIAL;
    baseRadius = 0;
  }

  public static class ModeEditor extends ComboBoxPropertyEditor {
    public ModeEditor() {
      super();
      setAvailableValues(new Mode[] { Mode.HUE, Mode.SATURATION, Mode.LUMINOSITY });
    }
  }

  public static class TransitionEditor extends ComboBoxPropertyEditor {
    public TransitionEditor() {
      super();
      setAvailableValues(new Transition[] { Transition.RADIAL, Transition.PARALLEL });
    }
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public int getX1() {
    return x1;
  }

  public void setX1(int x1) {
    this.x1 = x1;
  }

  public int getY1() {
    return y1;
  }

  public void setY1(int y1) {
    this.y1 = y1;
  }

  public int getValue1() {
    return value1;
  }

  public void setValue1(int value1) {
    this.value1 = value1;
  }

  public Transition getTransition() {
    return transition;
  }

  public void setTransition(Transition transition) {
    this.transition = transition;
  }

  public int getX2() {
    return x2;
  }

  public void setX2(int x2) {
    this.x2 = x2;
  }

  public int getY2() {
    return y2;
  }

  public void setY2(int y2) {
    this.y2 = y2;
  }

  public int getValue2() {
    return value2;
  }

  public void setValue2(int value2) {
    this.value2 = value2;
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  public int getBaseRadius() {
    return baseRadius;
  }

  public void setBaseRadius(int baseRadius) {
    this.baseRadius = baseRadius;
  }

}
