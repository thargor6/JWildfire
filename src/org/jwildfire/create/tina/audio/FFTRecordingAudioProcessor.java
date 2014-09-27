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
package org.jwildfire.create.tina.audio;

public class FFTRecordingAudioProcessor extends RecordingAudioProcessor {
  public static final int FFT_SIZE = 64;

  private int recordingIntervalInMilliseconds = 5; // ms
  private int movingAvgSize = 4;
  private int storedValuesPerFFTRow = FFT_SIZE;
  private int inputSamplePerFFTRowCount;
  private int currOff = 0;
  private RecordedFFT fft = null;

  @Override
  public void init(JWFAudioDevice pAudioDevice) {
    int samplesPerSecond = pAudioDevice.getSamplesPerSecond();
    inputSamplePerFFTRowCount = (int) (((long) samplesPerSecond * (long) recordingIntervalInMilliseconds) / (long) 1000);
    if (getBufferSize() < 6 * inputSamplePerFFTRowCount) {
      setBufferSize(6 * inputSamplePerFFTRowCount);
    }
    fft = new RecordedFFT(recordingIntervalInMilliseconds, movingAvgSize, inputSamplePerFFTRowCount, storedValuesPerFFTRow, pAudioDevice.getChannelCount(), samplesPerSecond);
  }

  @Override
  protected void processRecordedSamples(short[] pBuffer, int pDeletedOffset, int pTotalSize) {
    while (pTotalSize >= (currOff + 1) * inputSamplePerFFTRowCount) {
      int dataOff = currOff * inputSamplePerFFTRowCount - pDeletedOffset;
      short[] data = new short[inputSamplePerFFTRowCount];
      System.arraycopy(pBuffer, dataOff, data, 0, inputSamplePerFFTRowCount);
      fft.addData(data);
      currOff++;
    }
  }

  public RecordedFFT getFFT() {
    return fft;
  }

}
