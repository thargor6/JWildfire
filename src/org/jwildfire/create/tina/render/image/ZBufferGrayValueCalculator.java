/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

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
package org.jwildfire.create.tina.render.image;

import org.jwildfire.base.Tools;

public class ZBufferGrayValueCalculator {
  private final double zScale;
  private final double zShift;

  public ZBufferGrayValueCalculator(double zScale, double zShift) {
    this.zScale = zScale;
    this.zShift = zShift;
  }

  public int calculateGrayValue(double zValue) {
    if (zScale < 0.0) {
      int lvl = Tools.FTOI((-zScale * zValue - zShift) * 32767.0 + 32767.0);
      if (lvl < 0) {
        lvl = 0;
      } else if (lvl > 0xffff) {
        lvl = 0xffff;
      }
      return 0xffff - lvl & 0xffff;
    }
    // positive zScale: black to white (white near camera, black background, which is the
    // default)
    else {
      int lvl = Tools.FTOI((zScale * zValue - zShift) * 32767.0 + 32767.0);
      if (lvl < 0) {
        lvl = 0;
      } else if (lvl > 0xffff) {
        lvl = 0xffff;
      }
      return lvl & 0xffff;
    }
  }

  public static class ZBufferParams {
    private final double zScale;
    private final double zShift;
    private final double zBias;

    public ZBufferParams(double zScale, double zShift, double zBias) {
      this.zScale = zScale;
      this.zShift = zShift;
      this.zBias = zBias;
    }

    public double getzScale() {
      return zScale;
    }

    public double getzShift() {
      return zShift;
    }

    public double getzBias() {
      return zBias;
    }
  }

  public ZBufferParams suggestParams(double zMin, double zMax, double coverage) {
    double scale = 2.0 / (zMax-zMin);
    double shift = 2.0 * zMin / (zMax-zMin) + 1.0;
    double bias;
    if(coverage>0.95) {
      bias = 0.0;
    }
    else if(coverage>0.85) {
      bias = 0.1;
    }
    else {
      bias = 0.2;
    }
    return new ZBufferParams(scale, shift, bias);
  }

}







