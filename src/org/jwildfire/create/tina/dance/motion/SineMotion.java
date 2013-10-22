/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.dance.motion;

import static org.jwildfire.base.mathlib.MathLib.M_2PI;
import static org.jwildfire.base.mathlib.MathLib.sin;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;

public class SineMotion extends Motion {
  private static final long serialVersionUID = 1L;

  @Property(description = "Start value", category = PropertyCategory.GENERAL)
  private double minValue = 0.0;
  @Property(description = "End value", category = PropertyCategory.GENERAL)
  private double maxValue = 1.0;
  @Property(description = "Frequency in rpm (revolutions per minute)", category = PropertyCategory.GENERAL)
  private double frequency = 10.0;

  public double getMinValue() {
    return minValue;
  }

  public void setMinValue(double minValue) {
    this.minValue = minValue;
  }

  public double getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(double maxValue) {
    this.maxValue = maxValue;
  }

  public double getFrequency() {
    return frequency;
  }

  public void setFrequency(double frequency) {
    this.frequency = frequency;
  }

  @Override
  public double computeValue(short[] pFFTData, long pTime, int pFPS) {
    double t = pTime / 1000.0;
    double x = frequency * t / 60.0 * M_2PI;
    double r = minValue + (maxValue - minValue) * sin(x);
    return r;
  }

}
