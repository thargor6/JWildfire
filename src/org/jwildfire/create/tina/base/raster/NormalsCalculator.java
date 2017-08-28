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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.ThreadTools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.render.image.AbstractImageRenderThread;

@SuppressWarnings("serial")
public class NormalsCalculator implements Serializable {
  private final int rasterWidth, rasterHeight;

  private final float nxBuf[][];
  private final float nyBuf[][];
  private final float nzBuf[][];

  private final float originXBuf[][];
  private final float originYBuf[][];
  private final float originZBuf[][];

  public final static float ZBUF_ZMIN = -Float.MAX_VALUE;

  private class Corner implements Serializable {
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

  public static class CornerPair implements Serializable {
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

  public static class Corners implements Serializable {
    public final Corner a, b, c;

    public Corners(Corner a, Corner b, Corner c) {
      super();
      this.a = a;
      this.b = b;
      this.c = c;
    }

    public boolean isInside(int xOff, int yOff) {
      return a.isInside(xOff, yOff) && b.isInside(xOff, yOff) && c.isInside(xOff, yOff);
    }
  }

  private final CornerPair[] NNEIGHBOURS_COARSE = new CornerPair[] {
      new CornerPair(new Corner(-1, 0), new Corner(-1, -1)),
      new CornerPair(new Corner(0, -1), new Corner(1, -1)),
      new CornerPair(new Corner(1, 0), new Corner(1, 1)),
      new CornerPair(new Corner(0, 1), new Corner(-1, 1)),
      new CornerPair(new Corner(-1, -1), new Corner(0, -1)),
      new CornerPair(new Corner(1, -1), new Corner(1, 0)),
      new CornerPair(new Corner(1, 1), new Corner(0, 1)),
      new CornerPair(new Corner(-1, 1), new Corner(-1, 0))
  };

  public boolean hasNormalAtLocation(int x, int y) {
    return nxBuf[x][y] != ZBUF_ZMIN;
  }

  public void refreshNormalAtLocation(int x, int y) {
    double xb = originXBuf[x][y];
    double yb = originYBuf[x][y];
    double zb = originZBuf[x][y];
    nxBuf[x][y] = nyBuf[x][y] = nzBuf[x][y] = ZBUF_ZMIN;
    if (zb != ZBUF_ZMIN) {
      double nx = 0.0, ny = 0.0, nz = 0.0;
      double minzdist = MathLib.fabs((double) ZBUF_ZMIN * 10.0);
      int samples = 0;
      for (int k = 0; k < NNEIGHBOURS_COARSE.length; k++) {
        if (NNEIGHBOURS_COARSE[k].isInside(x, y)) {
          int adx = NNEIGHBOURS_COARSE[k].a.dx;
          int ady = NNEIGHBOURS_COARSE[k].a.dy;
          double azb = originZBuf[x + adx][y + ady];
          if (azb != ZBUF_ZMIN) {
            double ax = xb - originXBuf[x + adx][y + ady];
            double ay = yb - originYBuf[x + adx][y + ady];
            double az = zb - azb;

            int bdx = NNEIGHBOURS_COARSE[k].b.dx;
            int bdy = NNEIGHBOURS_COARSE[k].b.dy;
            double bzb = originZBuf[x + bdx][y + bdy];
            if (bzb != ZBUF_ZMIN) {
              samples++;
              double bx = xb - originXBuf[x + bdx][y + bdy];
              double by = yb - originYBuf[x + bdx][y + bdy];
              double bz = zb - bzb;
              double zdist = MathLib.fabs(az) + MathLib.fabs(bz);
              if (zdist < minzdist) {
                minzdist = zdist;
                nx = ay * bz - az * by;
                ny = az * bx - ax * bz;
                nz = ax * by - ay * bx;
              }
            }
          }
        }
        if (samples > 5) {
          break;
        }
      }
      if (samples > 0) {
        double r = MathLib.sqrt(nx * nx + ny * ny + nz * nz);
        if (r > MathLib.EPSILON) {
          nx /= r;
          ny /= r;
          nz /= r;
        }
        nxBuf[x][y] = (float) nx;
        nyBuf[x][y] = (float) ny;
        nzBuf[x][y] = (float) nz;
      }
    }
  }

  private class RefreshNormalsThread extends AbstractImageRenderThread {
    private final int startRow, endRow;

    public RefreshNormalsThread(int pStartRow, int pEndRow) {
      startRow = pStartRow;
      endRow = pEndRow;
    }

    @Override
    public void run() {
      setDone(false);
      try {
        for (int i = startRow; i < endRow; i++) {
          for (int j = 0; j < rasterWidth; j++) {
            refreshNormalAtLocation(j, i);
          }
        }
      }
      finally {
        setDone(true);
      }
    }
  }

  public void refreshAllNormals() {
    int threadCount = Prefs.getPrefs().getTinaRenderThreads();
    if (threadCount < 1 || rasterHeight < 8 * threadCount) {
      threadCount = 1;
    }

    int rowsPerThread = rasterHeight / threadCount;
    List<RefreshNormalsThread> threads = new ArrayList<>();
    for (int i = 0; i < threadCount; i++) {
      int startRow = i * rowsPerThread;
      int endRow = i < threadCount - 1 ? startRow + rowsPerThread : rasterHeight;
      RefreshNormalsThread thread = new RefreshNormalsThread(startRow, endRow);
      threads.add(thread);
      if (threadCount > 1) {
        new Thread(thread).start();
      }
      else {
        thread.run();
      }
    }
    ThreadTools.waitForThreads(threadCount, threads);
    /*
        for (int i = 0; i < rasterWidth; i++) {
          for (int j = 0; j < rasterHeight; j++) {
            refreshNormalsAtLocation(i, j);
          }
        }*/
  }

  public NormalsCalculator(int rasterWidth, int rasterHeight, float[][] nxBuf, float[][] nyBuf, float[][] nzBuf, float[][] originXBuf, float[][] originYBuf, float[][] originZBuf) {
    super();
    this.rasterWidth = rasterWidth;
    this.rasterHeight = rasterHeight;
    this.nxBuf = nxBuf;
    this.nyBuf = nyBuf;
    this.nzBuf = nzBuf;
    this.originXBuf = originXBuf;
    this.originYBuf = originYBuf;
    this.originZBuf = originZBuf;
  }

}
