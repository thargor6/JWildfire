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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;

public class RecordedFFT implements Serializable {
  private static final long serialVersionUID = 1L;

  private final int samplesPerSecond;
  private final int recordedIntervalInMilliseconds;
  private final int movingAvgSize;
  private final int inputSamplePerFFTRowCount;
  private final int storedValuesPerFFTRow;
  private final int recordedChannels;
  private List<long[]> fftList = new ArrayList<long[]>();

  public RecordedFFT(int pRecordedIntervalInMilliseconds, int pMovingAvgSize, int pInputSamplePerFFTRowCount, int pStoredValuesPerFFTRow, int pRecordedChannels, int pSamplesPerSecond) {
    recordedIntervalInMilliseconds = pRecordedIntervalInMilliseconds;
    movingAvgSize = pMovingAvgSize;
    inputSamplePerFFTRowCount = pInputSamplePerFFTRowCount;
    storedValuesPerFFTRow = pStoredValuesPerFFTRow;
    recordedChannels = pRecordedChannels;
    samplesPerSecond = pSamplesPerSecond;
  }

  public int getRecordedInterval() {
    return recordedIntervalInMilliseconds;
  }

  public int getMovingAvgSize() {
    return movingAvgSize;
  }

  public int getRecordedValueCount() {
    return inputSamplePerFFTRowCount;
  }

  public int getRecordedEachSampleCount() {
    return storedValuesPerFFTRow;
  }

  public void addData(short[] data) {
    {
      int length = inputSamplePerFFTRowCount / 2;
      float[] input = new float[length * 2];
      for (int i = 0; i < 2 * length; i++) {
        input[i] = (float) data[i];
      }

      FloatFFT_1D fftlib = new FloatFFT_1D(length);
      fftlib.complexForward(input);
      //        int multiplier = (int) (BASE_FREQUENCY / ((float) SAMPLE_RATE / (float) FFT_SIZE));

      float outputData[] = new float[(input.length + 1) / 2];
      for (int i = 0; i < length / 2; i++) {
        outputData[i] = (float) Math.sqrt((Math.pow(input[2 * i], 2)) + (Math.pow(input[2 * i + 1], 2)));
      }
      for (int i = 0; i < length; i++) {
        data[i] = (short) (outputData[i] * 0.07 + 0.5f);
      }
    }
    {
      int avgCount = storedValuesPerFFTRow;
      int avgWidth = inputSamplePerFFTRowCount / avgCount;

      long[] avgArray = new long[avgCount];
      int sidx = 0;
      for (int i = 0; i < avgCount; i++) {
        long v = 0;
        for (int j = 0; j < avgWidth; j++) {
          v += data[sidx++];
        }
        avgArray[i] = (v / (long) avgWidth);
      }
      fftList.add(avgArray);
    }
  }

  public short[] getDataByTimeOffset(long pTimeOffsetInMS) {
    double intervals = (double) pTimeOffsetInMS / (double) recordedIntervalInMilliseconds;
    long position = (long) (inputSamplePerFFTRowCount * intervals / (double) recordedChannels + 0.5);
    return getData(position);
  }

  public long getRecordedTime() {
    return ((long) fftList.size() * (long) inputSamplePerFFTRowCount * (long) 1000) / (long) samplesPerSecond;
  }

  public short[] getData(long pPosition) {
    short[] res = new short[storedValuesPerFFTRow];
    double fidx = (double) pPosition / (double) inputSamplePerFFTRowCount * (double) recordedChannels;
    int idx = (int) fidx;
    if (idx < 0 || idx >= fftList.size() - 1) {
      return res;
    }
    long data0[] = fftList.get(idx);
    long data1[] = fftList.get(idx + 1);
    for (int i = 0; i < storedValuesPerFFTRow; i++) {
      res[i] = (short) (0.5 + (double) data0[i] + ((double) (data1[i] - data0[i])) * (fidx - (double) idx));
    }
    if (movingAvgSize > 1 && idx - movingAvgSize >= 0) {
      double avgDiv = movingAvgSize - 1.0 + (fidx - idx);
      for (int i = 0; i < storedValuesPerFFTRow; i++) {
        long val = res[i];
        for (int j = 1; j <= movingAvgSize; j++) {
          val += fftList.get(idx - j)[i];
        }
        res[i] = (short) ((double) val / avgDiv + 0.5);
      }
    }
    return res;
  }

  public int getSize() {
    return fftList.size();
  }

}
