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
package org.jwildfire.create.tina.random;


public abstract class AbstractRandomGenerator {
  private RandGenStatus status = RandGenStatus.DEFAULT;
  private double buffer[] = new double[40960];
  private int bufferIdx;

  public double random() {
    switch (status) {
      case DEFAULT: {
        return getNext();
      }
      case RECORDING: {
        double res = getNext();
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
    int res = (int) (random() * pMax);
    return res < pMax ? res : pMax - 1;
  }

  public enum RandGenStatus {
    DEFAULT, RECORDING, REPLAY
  }

  public abstract double getNext();

  public abstract void randomize(long pSeed);

}
