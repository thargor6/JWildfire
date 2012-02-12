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
import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

public class BalancingGradientTransformer extends Mesh2DTransformer {
  public enum Mode {
    RED, GREEN, BLUE, CONTRAST, BRIGHTNESS, GAMMA, SATURATION
  }

  public enum Transition {
    RADIAL, PARALLEL
  }

  @Property(description = "Balancing mode", editorClass = ModeEditor.class)
  private Mode mode = Mode.BRIGHTNESS;

  @Property(description = "X-coordinate of effect source")
  private int x1;

  @Property(description = "Y-coordinate of effect source")
  private int y1;

  @Property(description = "Effect value at the effect source")
  private int value1;

  @Property(description = "Transition mode", editorClass = TransitionEditor.class)
  private Transition transition = Transition.RADIAL;

  @Property(description = "X-coordinate of effect destination (parallel transition)")
  private int x2;

  @Property(description = "Y-coordinate of effect destination (parallel transition)")
  private int y2;

  @Property(description = "Effect value at the effect destination")
  private int value2;

  @Property(description = "Effect radius (radial transition)")
  private int radius;

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

    Pixel pixel = new Pixel();

    final double brMAX = 256.0;

    switch (this.mode) {
      case RED:
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
              int ival = v1 + (int) (dv * prj + 0.5);
              int tt = (int) ((double) ival / (double) brMAX * (double) 255.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              pixel.r += tt;
              if (pixel.r < 0)
                pixel.r = 0;
              else if (pixel.r > 255)
                pixel.r = 255;
              img.setRGB(j, i, pixel);
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
              int ival = v1 + (int) (dv * prj + 0.5);
              int tt = (int) ((double) ival / (double) brMAX * (double) 255.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              pixel.r += tt;
              if (pixel.r < 0)
                pixel.r = 0;
              else if (pixel.r > 255)
                pixel.r = 255;
              img.setRGB(j, i, pixel);
            }
          }
        }
        break;
      case GREEN:
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
              int ival = v1 + (int) (dv * prj + 0.5);

              int tt = (int) ((double) ival / (double) brMAX * (double) 255.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              pixel.g += tt;
              if (pixel.g < 0)
                pixel.g = 0;
              else if (pixel.g > 255)
                pixel.g = 255;
              img.setRGB(j, i, pixel);
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
              int ival = v1 + (int) (dv * prj + 0.5);

              int tt = (int) ((double) ival / (double) brMAX * (double) 255.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              pixel.g += tt;
              if (pixel.g < 0)
                pixel.g = 0;
              else if (pixel.g > 255)
                pixel.g = 255;
              img.setRGB(j, i, pixel);
            }
          }
        }
        break;
      case BLUE:
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
              int ival = v1 + (int) (dv * prj + 0.5);

              int tt = (int) ((double) ival / (double) brMAX * (double) 255.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              pixel.b += tt;
              if (pixel.b < 0)
                pixel.b = 0;
              else if (pixel.b > 255)
                pixel.b = 255;
              img.setRGB(j, i, pixel);
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
              int ival = v1 + (int) (dv * prj + 0.5);

              int tt = (int) ((double) ival / (double) brMAX * (double) 255.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              pixel.b += tt;
              if (pixel.b < 0)
                pixel.b = 0;
              else if (pixel.b > 255)
                pixel.b = 255;
              img.setRGB(j, i, pixel);
            }
          }
        }
        break;
      case BRIGHTNESS:
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
              int ival = v1 + (int) (dv * prj + 0.5);

              int tt = (int) ((double) ival / (double) brMAX * (double) 255.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              pixel.r += tt;
              if (pixel.r < 0)
                pixel.r = 0;
              else if (pixel.r > 255)
                pixel.r = 255;
              pixel.g += tt;
              if (pixel.g < 0)
                pixel.g = 0;
              else if (pixel.g > 255)
                pixel.g = 255;
              pixel.b += tt;
              if (pixel.b < 0)
                pixel.b = 0;
              else if (pixel.b > 255)
                pixel.b = 255;
              img.setRGB(j, i, pixel);
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
              int ival = v1 + (int) (dv * prj + 0.5);
              int tt = (int) ((double) ival / (double) brMAX * (double) 255.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              pixel.r += tt;
              if (pixel.r < 0)
                pixel.r = 0;
              else if (pixel.r > 255)
                pixel.r = 255;
              pixel.g += tt;
              if (pixel.g < 0)
                pixel.g = 0;
              else if (pixel.g > 255)
                pixel.g = 255;
              pixel.b += tt;
              if (pixel.b < 0)
                pixel.b = 0;
              else if (pixel.b > 255)
                pixel.b = 255;
              img.setRGB(j, i, pixel);
            }
          }
        }
        break;
      case CONTRAST:
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
              int ival = v1 + (int) (dv * prj + 0.5);

              double scale = (double) (ival) / brMAX;
              int sc = (int) (scale * (double) Tools.VPREC + 0.5);
              int dc;
              pixel.setARGBValue(img.getARGBValue(j, i));
              if (ival > 0) {
                dc = (int) (((int) (pixel.r - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                dc = pixel.r + dc;
                if (dc < 0)
                  dc = 0;
                else if (dc > 255)
                  dc = 255;
                pixel.r = dc;
                dc = (int) (((int) (pixel.g - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                dc = pixel.g + dc;
                if (dc < 0)
                  dc = 0;
                else if (dc > 255)
                  dc = 255;
                pixel.g = dc;
                dc = (int) ((int) ((pixel.b - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                dc = pixel.b + dc;
                if (dc < 0)
                  dc = 0;
                else if (dc > 255)
                  dc = 255;
                pixel.b = dc;
              }
              else {
                int val;
                dc = (int) (((int) (pixel.r - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                val = pixel.r + dc;
                if (pixel.r < 127) {
                  if (val > 127)
                    val = 127;
                }
                else {
                  if (val < 127)
                    val = 127;
                }
                pixel.r = val;
                dc = (int) ((int) ((pixel.g - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                val = pixel.g + dc;
                if (pixel.g < 127) {
                  if (val > 127)
                    val = 127;
                }
                else {
                  if (val < 127)
                    val = 127;
                }
                pixel.g = val;
                dc = (int) ((int) ((pixel.b - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                val = pixel.b + dc;
                if (pixel.b < 127) {
                  if (val > 127)
                    val = 127;
                }
                else {
                  if (val < 127)
                    val = 127;
                }
                pixel.b = val;
              }
              img.setRGB(j, i, pixel);
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
              int ival = v1 + (int) (dv * prj + 0.5);
              double scale = (double) (ival) / brMAX;
              int sc = (int) (scale * (double) Tools.VPREC + 0.5);
              int dc;
              pixel.setARGBValue(img.getARGBValue(j, i));
              if (ival > 0) {
                dc = (int) (((int) (pixel.r - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                dc = pixel.r + dc;
                if (dc < 0)
                  dc = 0;
                else if (dc > 255)
                  dc = 255;
                pixel.r = dc;
                dc = (int) (((int) (pixel.g - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                dc = pixel.g + dc;
                if (dc < 0)
                  dc = 0;
                else if (dc > 255)
                  dc = 255;
                pixel.g = dc;
                dc = (int) ((int) ((pixel.b - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                dc = pixel.b + dc;
                if (dc < 0)
                  dc = 0;
                else if (dc > 255)
                  dc = 255;
                pixel.b = dc;
              }
              else {
                int val;
                dc = (int) (((int) (pixel.r - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                val = pixel.r + dc;
                if (pixel.r < 127) {
                  if (val > 127)
                    val = 127;
                }
                else {
                  if (val < 127)
                    val = 127;
                }
                pixel.r = val;
                dc = (int) ((int) ((pixel.g - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                val = pixel.g + dc;
                if (pixel.g < 127) {
                  if (val > 127)
                    val = 127;
                }
                else {
                  if (val < 127)
                    val = 127;
                }
                pixel.g = val;
                dc = (int) ((int) ((pixel.b - (int) 127) * sc) >> Tools.SPREC);
                if (dc < (-255))
                  dc = (-255);
                else if (dc > 255)
                  dc = 255;
                val = pixel.b + dc;
                if (pixel.b < 127) {
                  if (val > 127)
                    val = 127;
                }
                else {
                  if (val < 127)
                    val = 127;
                }
                pixel.b = val;
              }
              img.setRGB(j, i, pixel);
            }
          }
        }
        break;
      case GAMMA: {
        final double max = 255.0;
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
              int ival = v1 + (int) (dv * prj + 0.5);

              double g = (double) 512.0 / (512.0 + (double) ival);

              pixel.setARGBValue(img.getARGBValue(j, i));
              int val;
              val = pixel.r;
              if (val == 0)
                val = 0;
              else
                val = (int) (max * Math.pow((double) val / 255.0, g) + 0.5);
              pixel.r = val;
              val = pixel.g;
              if (val == 0)
                val = 0;
              else
                val = (int) (max * Math.pow((double) val / 255.0, g) + 0.5);
              pixel.g = val;
              val = pixel.b;
              if (val == 0)
                val = 0;
              else
                val = (int) (max * Math.pow((double) val / 255.0, g) + 0.5);
              pixel.b = val;

              img.setRGB(j, i, pixel);
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
              int ival = v1 + (int) (dv * prj + 0.5);
              double g = (double) 512.0 / (512.0 + (double) ival);

              pixel.setARGBValue(img.getARGBValue(j, i));
              int val;
              val = pixel.r;
              if (val == 0)
                val = 0;
              else
                val = (int) (max * Math.pow((double) val / 255.0, g) + 0.5);
              pixel.r = val;
              val = pixel.g;
              if (val == 0)
                val = 0;
              else
                val = (int) (max * Math.pow((double) val / 255.0, g) + 0.5);
              pixel.g = val;
              val = pixel.b;
              if (val == 0)
                val = 0;
              else
                val = (int) (max * Math.pow((double) val / 255.0, g) + 0.5);
              pixel.b = val;
              img.setRGB(j, i, pixel);
            }
          }
        }
      }
        break;
      case SATURATION: {
        int rs = 2990;
        int gs = 5880;
        int bs = 1130;
        rs = (rs * Tools.VPREC) / 10000;
        gs = (gs * Tools.VPREC) / 10000;
        bs = (bs * Tools.VPREC) / 10000;
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
              int ival = v1 + (int) (dv * prj + 0.5);

              int scl = (int) ((double) ival / 255.0 * 1024.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              int rv = pixel.r;
              int gv = pixel.g;
              int bv = pixel.b;
              int avg = (rs * rv + gs * gv + bs * bv) >> Tools.SPREC;
              rv += ((rv - avg) * scl) >> Tools.SPREC;
              if (rv < 0)
                rv = 0;
              else if (rv > 255)
                rv = 255;
              pixel.r = rv;
              gv += ((gv - avg) * scl) >> Tools.SPREC;
              if (gv < 0)
                gv = 0;
              else if (gv > 255)
                gv = 255;
              pixel.g = gv;
              bv += ((bv - avg) * scl) >> Tools.SPREC;
              if (bv < 0)
                bv = 0;
              else if (bv > 255)
                bv = 255;
              pixel.b = bv;
              img.setRGB(j, i, pixel);
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
              int ival = v1 + (int) (dv * prj + 0.5);
              int scl = (int) ((double) ival / 255.0 * 1024.0 + 0.5);
              pixel.setARGBValue(img.getARGBValue(j, i));
              int rv = pixel.r;
              int gv = pixel.g;
              int bv = pixel.b;
              int avg = (rs * rv + gs * gv + bs * bv) >> Tools.SPREC;
              rv += ((rv - avg) * scl) >> Tools.SPREC;
              if (rv < 0)
                rv = 0;
              else if (rv > 255)
                rv = 255;
              pixel.r = rv;
              gv += ((gv - avg) * scl) >> Tools.SPREC;
              if (gv < 0)
                gv = 0;
              else if (gv > 255)
                gv = 255;
              pixel.g = gv;
              bv += ((bv - avg) * scl) >> Tools.SPREC;
              if (bv < 0)
                bv = 0;
              else if (bv > 255)
                bv = 255;
              pixel.b = bv;
              img.setRGB(j, i, pixel);
            }
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
    baseRadius = 0;
    value1 = 120;
    value2 = -180;
    mode = Mode.BLUE;
    transition = Transition.RADIAL;
  }

  public static class ModeEditor extends ComboBoxPropertyEditor {
    public ModeEditor() {
      super();
      setAvailableValues(new Mode[] { Mode.RED, Mode.GREEN, Mode.BLUE, Mode.CONTRAST,
          Mode.BRIGHTNESS, Mode.GAMMA, Mode.SATURATION });
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

  @Override
  protected boolean allowShowStats() {
    return false;
  }

}
