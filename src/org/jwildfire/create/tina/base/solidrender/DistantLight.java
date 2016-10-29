/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.base.solidrender;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;

import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.edit.Assignable;

@SuppressWarnings("serial")
public class DistantLight implements Assignable<DistantLight>, Serializable {
  private double altitude;
  private final MotionCurve altitudeCurve = new MotionCurve();

  private double azimuth;
  private final MotionCurve azimuthCurve = new MotionCurve();

  private double intensity = 0.5;
  private double red, green, blue;
  private boolean castShadows = true;
  private double shadowIntensity = 0.8;

  public double getAltitude() {
    return altitude;
  }

  public double getAzimuth() {
    return azimuth;
  }

  public double getRed() {
    return red;
  }

  public void setRed(double red) {
    this.red = red;
  }

  public double getGreen() {
    return green;
  }

  public void setGreen(double green) {
    this.green = green;
  }

  public double getBlue() {
    return blue;
  }

  public void setBlue(double blue) {
    this.blue = blue;
  }

  public double getIntensity() {
    return intensity;
  }

  public void setIntensity(double intensity) {
    this.intensity = intensity;
  }

  public boolean isCastShadows() {
    return castShadows;
  }

  public void setCastShadows(boolean castShadows) {
    this.castShadows = castShadows;
  }

  @Override
  public void assign(DistantLight pSrc) {
    altitude = pSrc.altitude;
    altitudeCurve.assign(pSrc.altitudeCurve);
    azimuth = pSrc.azimuth;
    azimuthCurve.assign(pSrc.azimuthCurve);
    intensity = pSrc.intensity;
    red = pSrc.red;
    green = pSrc.green;
    blue = pSrc.blue;
    castShadows = pSrc.castShadows;
    shadowIntensity = pSrc.shadowIntensity;
  }

  @Override
  public DistantLight makeCopy() {
    DistantLight res = new DistantLight();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(DistantLight pSrc) {
    if (fabs(altitude - pSrc.altitude) > EPSILON || !altitudeCurve.isEqual(pSrc.altitudeCurve) ||
        fabs(azimuth - pSrc.azimuth) > EPSILON || !azimuthCurve.isEqual(pSrc.azimuthCurve) ||
        fabs(intensity - pSrc.intensity) > EPSILON ||
        fabs(red - pSrc.red) > EPSILON || fabs(green - pSrc.green) > EPSILON ||
        fabs(blue - pSrc.blue) > EPSILON || castShadows != pSrc.castShadows ||
        fabs(shadowIntensity - pSrc.shadowIntensity) > EPSILON) {
      return false;
    }
    return true;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }

  public void setAzimuth(double azimuth) {
    this.azimuth = azimuth;
  }

  public double getShadowIntensity() {
    return shadowIntensity;
  }

  public void setShadowIntensity(double shadowIntensity) {
    this.shadowIntensity = shadowIntensity;
  }

  public MotionCurve getAltitudeCurve() {
    return altitudeCurve;
  }

  public MotionCurve getAzimuthCurve() {
    return azimuthCurve;
  }
}
