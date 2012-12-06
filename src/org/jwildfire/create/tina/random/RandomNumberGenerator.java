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
package org.jwildfire.create.tina.random;

public class RandomNumberGenerator {
  private RandGenStatus status = RandGenStatus.DEFAULT;
  private int u = 12244355;
  private int v = 34384;
  private double buffer[] = new double[40960];
  private int bufferIdx;

  private static final int RAND_MAX = 0x7fffffff + 1;

  public void randomize(long pSeed) {
    u = (int) (pSeed << 16);
    v = (int) (pSeed << 16) >> 16;
  }

  public double random() {
    switch (status) {
      case DEFAULT: {
        v = 36969 * (v & 65535) + (v >> 16);
        u = 18000 * (u & 65535) + (u >> 16);
        int rnd = (v << 16) + u;
        double res = (double) rnd / (double) RAND_MAX;
        if (res < 0) {
          res = 0.0 - res;
        }
        return res;
      }
      case RECORDING: {
        v = 36969 * (v & 65535) + (v >> 16);
        u = 18000 * (u & 65535) + (u >> 16);
        int rnd = (v << 16) + u;
        double res = (double) rnd / (double) RAND_MAX;
        if (res < 0) {
          res = 0.0 - res;
        }
        buffer[bufferIdx++] = res;
        return res;
      }
      case REPLAY:
        return buffer[bufferIdx++];
      default:
        throw new IllegalStateException();
    }
  }

  public void setStatus(RandGenStatus pRandGenStatus) {
    bufferIdx = 0;
    status = pRandGenStatus;
  }

  public int random(int pMax) {
    return (int) (random() * pMax);
  }

  public enum RandGenStatus {
    DEFAULT, RECORDING, REPLAY
  }

}
