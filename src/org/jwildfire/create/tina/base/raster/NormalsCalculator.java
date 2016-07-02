/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.base.raster;

import org.jwildfire.base.mathlib.MathLib;

public class NormalsCalculator {
  private final int rasterWidth, rasterHeight;

  private final float zBuf[][];

  private final float nxBuf[][];
  private final float nyBuf[][];
  private final float nzBuf[][];

  public final static float ZBUF_ZMAX = -Float.MAX_VALUE;

  private class Corner {
    public final int dx, dy;

    public Corner(int dx, int dy) {
      super();
      this.dx = dx;
      this.dy = dy;
    }

    public boolean isInside(int xOff, int yOff) {
      int x = xOff + dx;
      if (x < 0 || x >= rasterWidth)
        return false;
      int y = yOff + dy;
      if (y < 0 || y >= rasterHeight)
        return false;
      return true;
    }
  }

  public static class CornerPair {
    public final Corner a, b;

    public CornerPair(Corner a, Corner b) {
      super();
      this.a = a;
      this.b = b;
    }

    public boolean isInside(int xOff, int yOff) {
      return a.isInside(xOff, yOff) && b.isInside(xOff, yOff);
    }
  }

  private final CornerPair[] NNEIGHBOURS_NORMAL = new CornerPair[] {
      new CornerPair(new Corner(-1, 0), new Corner(0, -1)),
      new CornerPair(new Corner(0, -1), new Corner(1, 0)),
      new CornerPair(new Corner(1, 0), new Corner(0, 1)),
      new CornerPair(new Corner(0, 1), new Corner(-1, 0))
  };

  private final CornerPair[] NNEIGHBOURS_DIAG = new CornerPair[] {
      new CornerPair(new Corner(-1, -1), new Corner(1, -1)),
      new CornerPair(new Corner(1, -1), new Corner(1, 1)),
      new CornerPair(new Corner(1, 1), new Corner(-1, 1)),
      new CornerPair(new Corner(-1, 1), new Corner(-1, -1))
  };

  private final CornerPair[] NNEIGHBOURS_COARSE = new CornerPair[] {
      new CornerPair(new Corner(-1, 0), new Corner(-1, -1)),
      new CornerPair(new Corner(-1, -1), new Corner(0, -1)),
      new CornerPair(new Corner(0, -1), new Corner(1, -1)),
      new CornerPair(new Corner(1, -1), new Corner(1, 0)),
      new CornerPair(new Corner(1, 0), new Corner(1, 1)),
      new CornerPair(new Corner(1, 1), new Corner(0, 1)),
      new CornerPair(new Corner(0, 1), new Corner(-1, 1)),
      new CornerPair(new Corner(-1, 1), new Corner(-1, 0))
  };

  private final CornerPair[][] NNEIGHBOURS = new CornerPair[][] { NNEIGHBOURS_NORMAL, NNEIGHBOURS_DIAG, NNEIGHBOURS_COARSE };

  public void refreshNormalsAtLocation(int x, int y) {
    double zb = zBuf[x][y];
    nxBuf[x][y] = nyBuf[x][y] = nzBuf[x][y] = ZBUF_ZMAX;
    if (zb != ZBUF_ZMAX) {
      for (int pass = 0; pass < NNEIGHBOURS.length; pass++) {
        double nx = 0.0, ny = 0.0, nz = 0.0;
        int samples = 0;
        for (int k = 0; k < NNEIGHBOURS[pass].length; k++) {
          if (NNEIGHBOURS[pass][k].isInside(x, y)) {
            double ax = NNEIGHBOURS[pass][k].a.dx;
            double ay = NNEIGHBOURS[pass][k].a.dy;
            double azb = zBuf[x + NNEIGHBOURS[pass][k].a.dx][y + NNEIGHBOURS[pass][k].a.dy];
            double az = zb - azb;

            double bx = NNEIGHBOURS[pass][k].b.dx;
            double by = NNEIGHBOURS[pass][k].b.dy;
            double bzb = zBuf[x + NNEIGHBOURS[pass][k].b.dx][y + NNEIGHBOURS[pass][k].b.dy];
            double bz = zb - bzb;
            if (azb != ZBUF_ZMAX && bzb != ZBUF_ZMAX) {
              samples++;
              nx += ay * bz - az * by;
              ny += az * bx - ax * bz;
              nz += ax * by - ay * bx;
            }
          }
        }
        if (samples >= 4) {
          double r = MathLib.sqrt(nx * nx + ny * ny + nz * nz);
          if (r > 0.000001) {
            nx /= r;
            ny /= r;
            nz /= r;
          }
          nxBuf[x][y] = (float) nx;
          nyBuf[x][y] = (float) ny;
          nzBuf[x][y] = (float) nz;
          break;
        }
      }
    }
  }

  public void refreshAllNormals() {
    for (int i = 0; i < rasterWidth; i++) {
      for (int j = 0; j < rasterHeight; j++) {
        double nx = 0.0, ny = 0.0, nz = 0.0;
        double zb = zBuf[i][j];
        nxBuf[i][j] = nyBuf[i][j] = nzBuf[i][j] = ZBUF_ZMAX;
        if (zb != ZBUF_ZMAX) {

          for (int pass = 0; pass < NNEIGHBOURS.length; pass++) {
            nx = ny = nz = 0.0;
            int samples = 0;

            for (int k = 0; k < NNEIGHBOURS[pass].length; k++) {
              if (NNEIGHBOURS[pass][k].isInside(i, j)) {
                double ax = NNEIGHBOURS[pass][k].a.dx;
                double ay = NNEIGHBOURS[pass][k].a.dy;
                double azb = zBuf[i + NNEIGHBOURS[pass][k].a.dx][j + NNEIGHBOURS[pass][k].a.dy];
                double az = zb - azb;

                double bx = NNEIGHBOURS[pass][k].b.dx;
                double by = NNEIGHBOURS[pass][k].b.dy;
                double bzb = zBuf[i + NNEIGHBOURS[pass][k].b.dx][j + NNEIGHBOURS[pass][k].b.dy];
                double bz = zb - bzb;
                if (azb != ZBUF_ZMAX && bzb != ZBUF_ZMAX) {
                  nx += ay * bz - az * by;
                  ny += az * bx - ax * bz;
                  nz += ax * by - ay * bx;
                  samples++;
                }
              }
            }
            if (samples >= 3) {
              double r = MathLib.sqrt(nx * nx + ny * ny + nz * nz);
              if (r > 0.000001) {
                nx /= r;
                ny /= r;
                nz /= r;
              }

              nxBuf[i][j] = (float) nx;
              nyBuf[i][j] = (float) ny;
              nzBuf[i][j] = (float) nz;
              break;
            }
          }
        }
      }
    }
  }

  public NormalsCalculator(int rasterWidth, int rasterHeight, float[][] zBuf, float[][] nxBuf, float[][] nyBuf, float[][] nzBuf) {
    super();
    this.rasterWidth = rasterWidth;
    this.rasterHeight = rasterHeight;
    this.zBuf = zBuf;
    this.nxBuf = nxBuf;
    this.nyBuf = nyBuf;
    this.nzBuf = nzBuf;
  }

}
