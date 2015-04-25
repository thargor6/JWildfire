/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.swing;

import org.jwildfire.create.tina.audio.JLayerInterface;
import org.jwildfire.create.tina.audio.RecordedFFT;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.Envelope.Interpolation;

public class EnvelopeMP3Data {
  private final RecordedFFT fft;

  public EnvelopeMP3Data(String pMPFilename) {
    try {
      JLayerInterface jLayer = new JLayerInterface();
      fft = jLayer.recordFFT(pMPFilename);
    }
    catch (Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  public void applyToEvelope(Envelope envelope, int fps, int channel, int frameoffset, int duration) {
    int samplesPerFrame = 10;
    int dt = 1000 / fps / samplesPerFrame;

    int points = (int) ((fft.getRecordedTime() * fps) / 1000.0 + 0.5);
    if (duration > 0 && duration < points)
      points = duration;

    double scale = 0.01;
    int x[] = new int[points - frameoffset];
    double y[] = new double[points - frameoffset];
    double ymin = 0.0, ymax = 0.0;
    int idx = 0;
    for (int frame = frameoffset; frame < points; frame++) {
      long time = ((long) frame * (long) 1000) / (long) fps;
      x[idx] = frame;
      double fftValue = 0.0;
      for (int sample = 0; sample < samplesPerFrame; sample++) {
        fftValue += (double) fft.getDataByTimeOffset(time + sample * dt)[channel] * scale;
      }
      fftValue /= (double) samplesPerFrame;
      if (fftValue < ymin)
        ymin = fftValue;
      else if (fftValue > ymax)
        ymax = fftValue;
      y[idx++] = fftValue;

    }
    envelope.setValues(x, y);
    envelope.setViewXMin(-points / 25);
    envelope.setViewXMax(points + points / 25);
    envelope.setViewYMin(ymin - (ymax - ymin) * 0.04);
    envelope.setViewYMax(ymax + (ymax - ymin) * 0.04);
    envelope.setInterpolation(Interpolation.LINEAR);
  }
}
