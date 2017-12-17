/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
package org.jwildfire.create.tina.base.solidrender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.ThreadTools;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.raster.NormalsCalculator;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.render.FilterHolder;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.render.image.AbstractImageRenderThread;

@SuppressWarnings("serial")
public class AOCalculator implements Serializable {
  private final float zBuf[][];

  private final float nxBuf[][];
  private final float nyBuf[][];
  private final float nzBuf[][];

  private final int rasterWidth, rasterHeight;
  private final double aoSearchRadius, aoBlurRadius, aoFalloff;
  private final int aoRadiusSamples, aoAzimuthSamples;
  private final AbstractRandomGenerator randGen;

  public AOCalculator(Flame flame, int rasterWidth, int rasterHeight, float zBuf[][], float nxBuf[][], float nyBuf[][], float nzBuf[][]) {
    this.rasterWidth = rasterWidth;
    this.rasterHeight = rasterHeight;
    this.zBuf = zBuf;
    this.nxBuf = nxBuf;
    this.nyBuf = nyBuf;
    this.nzBuf = nzBuf;
    aoSearchRadius = Tools.limitValue(flame.getSolidRenderSettings().getAoSearchRadius(), 0.25, 120.0);
    aoBlurRadius = Tools.limitValue(flame.getSolidRenderSettings().getAoBlurRadius(), 0.25, 10.0);
    aoFalloff = Tools.limitValue(flame.getSolidRenderSettings().getAoFalloff(), 0.0, 10.0);
    aoRadiusSamples = Tools.limitValue(flame.getSolidRenderSettings().getAoRadiusSamples(), 1, 128);
    aoAzimuthSamples = Tools.limitValue(flame.getSolidRenderSettings().getAoAzimuthSamples(), 1, 128);
    randGen = new MarsagliaRandomGenerator();
    randGen.randomize(32851137);
  }

  public void refreshAO(float destBuf[][]) {
    double imgSize = MathLib.sqrt(rasterWidth * rasterWidth + rasterHeight * rasterHeight);
    double blurRadius = aoBlurRadius * imgSize / 500.0;
    boolean doSmooth = blurRadius >= 0.42;

    float[][] aoBuf = doSmooth ? new float[rasterWidth][rasterHeight] : destBuf;

    int threadCount = Prefs.getPrefs().getTinaRenderThreads();
    if (threadCount < 1 || rasterHeight < 8 * threadCount) {
      threadCount = 1;
    }

    int rowsPerThread = rasterHeight / threadCount;

    {
      List<RefreshAOThread> threads = new ArrayList<>();
      for (int i = 0; i < threadCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = i < threadCount - 1 ? startRow + rowsPerThread : rasterHeight;
        RefreshAOThread thread = new RefreshAOThread(startRow, endRow, aoBuf);
        threads.add(thread);
        if (threadCount > 1) {
          new Thread(thread).start();
        }
        else {
          thread.run();
        }
      }
      ThreadTools.waitForThreads(threadCount, threads);
    }

    if (doSmooth) {
      List<SmoothAOBufferThread> threads = new ArrayList<>();
      for (int i = 0; i < threadCount; i++) {
        int startRow = i * rowsPerThread;
        int endRow = i < threadCount - 1 ? startRow + rowsPerThread : rasterHeight;
        SmoothAOBufferThread thread = new SmoothAOBufferThread(startRow, endRow, aoBuf, destBuf, FilterKernelType.GAUSSIAN, blurRadius, 0.1);
        threads.add(thread);
        if (threadCount > 1) {
          new Thread(thread).start();
        }
        else {
          thread.run();
        }
      }
      ThreadTools.waitForThreads(threadCount, threads);
    }
  }

  private double getZ(double x, double y) {
    return zBuf[getXCoord(x)][getYCoord(y)];
  }

  private int getXCoord(double x) {
    int xi = Tools.FTOI(x);
    if (xi < 0) {
      xi = 0;
    }
    else if (xi >= rasterWidth) {
      xi = rasterWidth - 1;
    }
    return xi;
  }

  private int getYCoord(double y) {
    int yi = Tools.FTOI(y);
    if (yi < 0) {
      yi = 0;
    }
    else if (yi >= rasterHeight) {
      yi = rasterHeight - 1;
    }
    return yi;
  }

  private class SmoothAOBufferThread extends AbstractImageRenderThread {
    private final float[][] srcBuf, dstBuf;
    private final int startRow, endRow;
    private final double scale;
    private final double[][] f;
    private final int filterPixSize, fCenter;

    public SmoothAOBufferThread(int startRow, int endRow, float[][] srcBuf, float[][] dstBuf, FilterKernelType kernelType, double filterRadius, double scale) {
      this.startRow = startRow;
      this.endRow = endRow;
      this.srcBuf = srcBuf;
      this.dstBuf = dstBuf;
      this.scale = scale;
      FilterHolder filter = new FilterHolder(kernelType, 1, filterRadius);
      f = filter.getFilter();
      filterPixSize = f.length;
      fCenter = filterPixSize / 2;
    }

    public void run() {
      setDone(false);
      try {
        for (int i = startRow; i < endRow; i++) {
          for (int j = 0; j < rasterWidth; j++) {
            double v = 0.0;
            for (int k = 0; k < filterPixSize; k++) {
              int px = j + k - fCenter;
              if (px >= 0 && px < rasterWidth) {
                for (int l = 0; l < filterPixSize; l++) {
                  int py = i + l - fCenter;
                  if (py >= 0 && py < rasterHeight) {
                    v += srcBuf[px][py] * f[k][l];
                  }
                }
              }
            }
            dstBuf[j][i] = (float) (v * scale);
          }
        }
      }
      finally {
        setDone(true);
      }
    }
  }

  @SuppressWarnings("serial")
  private class RefreshAOThread extends AbstractImageRenderThread {
    private final float aoBuf[][];
    private final int startRow, endRow;
    private final double imgSize, sphere_radius, radius_stepsize, azimuth_stepsize;
    private final double r_jitter, angleBias, ao_falloff, ao_intensity;
    private final int radius_samples, azimuth_samples;

    public RefreshAOThread(int startRow, int endRow, float aoBuf[][]) {
      this.startRow = startRow;
      this.endRow = endRow;
      this.aoBuf = aoBuf;

      imgSize = MathLib.sqrt(rasterWidth * rasterWidth + rasterHeight * rasterHeight);

      sphere_radius = aoSearchRadius * imgSize / 500.0;
      radius_samples = aoRadiusSamples;
      radius_stepsize = sphere_radius / (double) radius_samples;

      azimuth_samples = aoAzimuthSamples;
      azimuth_stepsize = MathLib.M_2PI / (double) azimuth_samples;

      r_jitter = radius_stepsize / 10.0;

      angleBias = 0.001;
      ao_falloff = aoFalloff;
      ao_intensity = 0.5;
    }

    @Override
    public void run() {
      setDone(false);
      try {
        for (int i = startRow; i < endRow; i++) {
          for (int j = 0; j < rasterWidth; j++) {

            double x0 = j;
            double y0 = i;
            double z0 = zBuf[j][i];
            double nx = nxBuf[j][i];
            double ny = nyBuf[j][i];
            double nz = nzBuf[j][i];

            if (z0 != NormalsCalculator.ZBUF_ZMIN && nx != NormalsCalculator.ZBUF_ZMIN) {
              double rndAngle = (0.5 - randGen.random()) * azimuth_stepsize / 4.0;

              double angle = rndAngle;

              for (int k = 0; k < azimuth_samples; k++, angle += azimuth_stepsize) {
                double dx = MathLib.cos(angle);
                double dy = MathLib.sin(angle);
                double r0 = radius_stepsize;
                double prevH = 0.0;
                for (int l = 0; l < radius_samples; l++, r0 += radius_stepsize) {
                  double r = r0 + (0.5 - randGen.random()) * r_jitter;
                  double x = x0 + r * dx + (0.5 - randGen.random()) * r_jitter;
                  double y = y0 + r * dy + (0.5 - randGen.random()) * r_jitter;
                  double z = getZ(x, y);
                  double h = MathLib.atan2(z - z0, r) + angleBias;

                  double Rx = x;
                  double Ry = y;
                  double RR = MathLib.sqrt(Rx * Rx + Ry * Ry);
                  Rx /= RR;
                  Ry /= RR;

                  double tt = (nx * Rx + Ry * ny);
                  double tx = -(Ry * nx * ny - Rx * ny * ny - Rx * nz * nz) / tt;
                  double ty = (Ry * nx * nx + Ry * nz * nz - nx * Rx * ny) / tt;
                  double tz = (-Ry * ny * nz - nx * Rx * nz) / tt;

                  double ta = MathLib.atan2(tz, MathLib.sqrt(tx * tx + ty * ty));

                  double ao = MathLib.sin(h) - MathLib.sin(ta);

                  if (ao > prevH) {
                    prevH = ao;
                    double dist = r / sphere_radius;
                    aoBuf[j][i] += ao * MathLib.exp(-dist * dist * ao_falloff) * ao_intensity;
                  }
                }
              }
            }
          }
        }
      }
      finally {
        setDone(true);
      }
    }
  }

}
