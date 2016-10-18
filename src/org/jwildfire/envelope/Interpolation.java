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
package org.jwildfire.envelope;

public abstract class Interpolation {
  protected static final double SMALL = 1.0e-5;
  protected double[] src;
  protected int snum;
  protected double[] dest;
  protected int dnum;
  protected int subdiv;

  public double[] getDest() {
    return dest;
  }

  public int getDnum() {
    return dnum;
  }

  public void setSrc(double[] src) {
    this.src = src;
  }

  public void setSrc(int[] src) {
    this.src = new double[src.length];
    for (int i = 0; i < src.length; i++)
      this.src[i] = (double) src[i];
  }

  public void setSnum(int snum) {
    this.snum = snum;
  }

  public void setSubdiv(int subdiv) {
    this.subdiv = subdiv;
  }

  public static int calcSubDivPRV(int[] x, int count) {
    int xdist, xdistmax;
    xdist = xdistmax = x[1] - x[0];
    for (int i = 1; i < (count - 1); i++) {
      xdist = x[i + 1] - x[i];
      if (xdist > xdistmax)
        xdistmax = xdist;
    }
    int subdiv = (int) (1.6 * xdistmax + 0.5);
    if (subdiv < 3)
      subdiv = 3;
    return (subdiv);
  }

  public abstract void interpolate();
}
