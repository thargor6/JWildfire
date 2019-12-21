/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2019 Andreas Maschke

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
package org.jwildfire.create.tina.palette;

public class RGBColorKeyFrame extends RGBColor {
  private int hueKeyFrame, saturationKeyFrame, luminosityKeyFrame;
  private double hue, saturation, luminosity;

  public int getHueKeyFrame() {
    return hueKeyFrame;
  }

  public void setHueKeyFrame(int hueKeyFrame) {
    this.hueKeyFrame = hueKeyFrame;
  }

  public int getSaturationKeyFrame() {
    return saturationKeyFrame;
  }

  public void setSaturationKeyFrame(int saturationKeyFrame) {
    this.saturationKeyFrame = saturationKeyFrame;
  }

  public int getLuminosityKeyFrame() {
    return luminosityKeyFrame;
  }

  public void setLuminosityKeyFrame(int luminosityKeyFrame) {
    this.luminosityKeyFrame = luminosityKeyFrame;
  }

  public double getHue() {
    return hue;
  }

  public void setHue(double hue) {
    this.hue = hue;
  }

  public double getSaturation() {
    return saturation;
  }

  public void setSaturation(double saturation) {
    this.saturation = saturation;
  }

  public double getLuminosity() {
    return luminosity;
  }

  public void setLuminosity(double luminosity) {
    this.luminosity = luminosity;
  }
}
