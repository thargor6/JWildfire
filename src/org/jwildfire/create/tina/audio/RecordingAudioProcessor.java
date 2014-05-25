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

public abstract class RecordingAudioProcessor implements AudioProcessor {
  private int bufferSize = 32768;
  private short buffer[] = new short[bufferSize];

  private int totalSize = 0;
  private int deletedOffset = 0;
  private int currOffset = 0;

  protected void setBufferSize(int pBufferSize) {
    if (bufferSize != pBufferSize) {
      buffer = new short[pBufferSize];
      bufferSize = pBufferSize;
    }
  }

  protected int getBufferSize() {
    return bufferSize;
  }

  protected short[] getRecordedSamples(int pOffset, int pCount) {
    // System.out.println("READ SAMPLES (" + pOffset + " " + deletedOffset + "..." + totalSize + ")");

    short[] res = new short[pCount];
    synchronized (buffer) {
      int offset = pOffset - deletedOffset;
      if (offset >= 0 && offset < buffer.length) {
        int end = offset + pCount;
        if (end > buffer.length)
          end = buffer.length;
        System.arraycopy(buffer, offset, res, 0, end - offset);
      }
    }
    return res;
  }

  @Override
  public void process(short[] pSamples, int pOffs, int pLen) {
    if (currOffset + pLen < bufferSize) {
      System.arraycopy(pSamples, pOffs, buffer, currOffset, pLen);
      currOffset += pLen;
    }
    else {
      short newBuffer[] = new short[bufferSize];
      // discard 2/3 of buffer
      int remaining = currOffset / 3;
      deletedOffset += (currOffset - remaining);
      System.arraycopy(buffer, currOffset - remaining, newBuffer, 0, remaining);
      currOffset = remaining;
      System.arraycopy(pSamples, pOffs, newBuffer, currOffset, pLen);
      currOffset += pLen;
      buffer = null;
      buffer = newBuffer;
    }
    totalSize += pLen;
    processRecordedSamples(buffer, deletedOffset, totalSize);
    //    System.out.println("RECORD TOTAL: " + totalSize + " DEL " + deletedOffset);
  }

  @Override
  public void finish() {
    processRecordedSamples(buffer, deletedOffset, totalSize);
  }

  protected abstract void processRecordedSamples(short pBuffer[], int pDeletedOffset, int pTotalSize);

}
