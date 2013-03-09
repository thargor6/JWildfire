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

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;

public class FFTMotion extends Motion {
  private static final long serialVersionUID = 1L;

  @Property(description = "FFT channel", category = PropertyCategory.GENERAL)
  private int fftChannel = 0;

  @Property(description = "Amplitude", category = PropertyCategory.GENERAL)
  private double amplitude = 1.0;

  @Property(description = "Offset", category = PropertyCategory.GENERAL)
  private double offset = 0.0;

  @Property(description = "Average", category = PropertyCategory.GENERAL)
  private int avgSize = 3;
  private double avgBuffer[] = new double[avgSize];

  public int getFftChannel() {
    return fftChannel;
  }

  public void setFftChannel(int pFFTIndex) {
    fftChannel = pFFTIndex;
  }

  public double getAmplitude() {
    return amplitude;
  }

  public void setAmplitude(double pAmplitude) {
    amplitude = pAmplitude;
  }

  public double getOffset() {
    return offset;
  }

  public void setOffset(double offset) {
    this.offset = offset;
  }

  @Override
  public double computeValue(short[] pFFTData, long pTime, int pFPS) {
    short val = pFFTData != null ? fftChannel < pFFTData.length ? pFFTData[fftChannel] : 0 : 0;
    double rawValue = 2.0 * val / (double) Short.MAX_VALUE;
    double currValue = offset + amplitude * rawValue;
    if (avgSize <= 1) {
      return currValue;
    }
    else {
      if (avgBuffer == null) {
        avgBuffer = new double[avgSize];
      }
      double avgValue = currValue;
      for (int i = 1; i < avgSize; i++) {
        avgValue += avgBuffer[i];
        avgBuffer[i - 1] = avgBuffer[i];
      }
      avgBuffer[avgSize - 1] = currValue;
      avgValue /= (double) avgSize;
      return (currValue >= 0.0) ? (avgValue > currValue ? avgValue : currValue) : (avgValue < currValue ? avgValue : currValue);
    }
  }

  public int getAvgSize() {
    return avgSize;
  }

  public void setAvgSize(int pAvgSize) {
    avgSize = pAvgSize;
    avgBuffer = new double[pAvgSize];
  }

}
