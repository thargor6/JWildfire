/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2026 Andreas Maschke

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

import org.jwildfire.base.ThreadTools;
import org.jwildfire.create.tina.render.image.AbstractImageRenderThread;

import java.util.ArrayList;
import java.util.List;

import static odk.lang.FastMath.abs;
import static odk.lang.FastMath.pow;
import static odk.lang.FastMath.sqrt;
import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.erf;

public class DeCalculator {
  private final float rawRed[][];
  private final float rawGreen[][];
  private final float rawBlue[][];
  private final float deRed[][];
  private final float deGreen[][];
  private final float deBlue[][];

  private final int rawCount[][];
  private final int deCount[][];
  private final int rasterWidth, rasterHeight;
  private final int estimatorRadius;
  private final double deCurve;
  private final double deSplitter;

  public DeCalculator(
      float[][] rawRed,
      float[][] rawGreen,
      float[][] rawBlue,
      float[][] deRed,
      float[][] deGreen,
      float[][] deBlue,
      int[][] rawCount,
      int[][] deCount,
      int rasterWidth,
      int rasterHeight,
      int estimatorRadius,
      double deCurve,
      double deSplitter) {
    this.rawRed = rawRed;
    this.rawGreen = rawGreen;
    this.rawBlue = rawBlue;
    this.deRed = deRed;
    this.deGreen = deGreen;
    this.deBlue = deBlue;
    this.rawCount = rawCount;
    this.deCount = deCount;
    this.rasterWidth = rasterWidth;
    this.rasterHeight = rasterHeight;
    this.estimatorRadius = estimatorRadius;
    this.deCurve = deCurve;
    this.deSplitter = deSplitter;
  }

  public void performDe(int threadCount) {
    if (threadCount > 0) {
      List<PerformDeThread> threads = new ArrayList<PerformDeThread>();
      int rowsPerThread = rasterHeight / threadCount;

      for (int i = 0; i < threadCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = i < threadCount - 1 ? startRow + rowsPerThread : rasterHeight;
        PerformDeThread thread = new PerformDeThread(startRow, endRow);
        threads.add(thread);
        if (threadCount > 1) {
          Thread t = new Thread(thread);
          t.setPriority(Thread.MIN_PRIORITY);
          t.start();
        } else {
          thread.run();
        }
      }
      ThreadTools.waitForThreads(threadCount, threads);
    } else {
      PerformDeThread thread = new PerformDeThread(0, rasterHeight);
      thread.run();
    }
  }

  public class PerformDeThread extends AbstractImageRenderThread {
    private final int startRow;
    private final int endRow;

    public PerformDeThread(int startRow, int endRow) {
      this.startRow = startRow;
      this.endRow = endRow;
    }

    @Override
    public void run() {
      setDone(false);
      try {
        boolean showDeSplitter = (deSplitter > EPSILON) && (deSplitter < 1.0 - EPSILON);
        int deSplitterPos = (int) (deSplitter * rasterWidth + 0.5);
        double invDistM[][] = new double[2 * estimatorRadius + 1][2 * estimatorRadius + 1];
        double invDistSqrtM[][] = new double[2 * estimatorRadius + 1][2 * estimatorRadius + 1];
        for (int dy = -estimatorRadius; dy <= estimatorRadius; dy++) {
          for (int dx = -estimatorRadius; dx <= estimatorRadius; dx++) {
            double invDist = 1.0 / ((float) (dx * dx + dy * dy) + 1.0);
            double invDistSqrt = sqrt(1.0 / invDist);
            invDistM[dx + estimatorRadius][dy + estimatorRadius] = invDist;
            invDistSqrtM[dx + estimatorRadius][dy + estimatorRadius] = invDistSqrt;
          }
        }

        for (int x = 0; x < rasterWidth; x++) {
          for (int y = startRow; y < endRow; y++) {
            double centerW = rawCount[x][y];
            double m1 = (sqrt(8.0 * centerW) + 5.0);
            double m2 = pow(centerW + 1.0, -0.25);
            double sumR = 0.0;
            double sumG = 0.0;
            double sumB = 0.0;
            double sumA = 0.0;
            double count = 0.0;
            for (int dy = -estimatorRadius; dy <= estimatorRadius; dy++) {
              int localY = y + dy;
              if (localY >= 0 && localY < rasterHeight) {
                for (int dx = -estimatorRadius; dx <= estimatorRadius; dx++) {
                  int localX = x + dx;
                  if (localX >= 0 && localX < rasterWidth) {
                    double invDist =
                        invDistM[dx + estimatorRadius][
                            dy + estimatorRadius]; // 1.0 / ((float)(dx * dx + dy * dy) + 1.0);
                    double localR = rawRed[localX][localY];
                    double localG = rawGreen[localX][localY];
                    double localB = rawBlue[localX][localY];
                    double localA = rawCount[localX][localY];
                    double deviation = abs(erf((localA - centerW) / m1));
                    if (deviation
                        <= pow(
                                deCurve,
                                invDistSqrtM[dx + estimatorRadius][
                                    dy + estimatorRadius] /* sqrt(1.0 / invDist) */)
                            * m2) {
                      sumR += localR * invDist;
                      sumG += localG * invDist;
                      sumB += localB * invDist;
                      sumA += localA * invDist;
                      count += invDist;
                    }
                  }
                }
              }
            }
            if (showDeSplitter && deSplitterPos >= x - 2 && deSplitterPos <= x + 2) {
              double spltColor = 180.0 * (rawCount[x][y] + 10);
              deRed[x][y] = (float) spltColor;
              deGreen[x][y] = (float) spltColor;
              deBlue[x][y] = (float) 0.0;
              deCount[x][y] = (rawCount[x][y] + 10);
            } else if (showDeSplitter && deSplitterPos < x - 2) {
              deRed[x][y] = rawRed[x][y];
              deGreen[x][y] = rawGreen[x][y];
              deBlue[x][y] = rawBlue[x][y];
              deCount[x][y] = rawCount[x][y];
            } else {

              if (count > EPSILON) {
                sumR /= count;
                sumG /= count;
                sumB /= count;
                sumA /= count;
                deRed[x][y] = (float) sumR;
                deGreen[x][y] = (float) sumG;
                deBlue[x][y] = (float) sumB;
                deCount[x][y] = (int) (sumA + 0.5);
              } else {
                deRed[x][y] = (float) 0.0;
                deGreen[x][y] = (float) 0.0;
                deBlue[x][y] = (float) 0.0;
                deCount[x][y] = 0;
              }
            }
          }
        }
      } finally {
        setDone(true);
      }
    }
  }
}
