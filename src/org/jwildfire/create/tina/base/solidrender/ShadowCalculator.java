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
package org.jwildfire.create.tina.base.solidrender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.raster.NormalsCalculator;
import org.jwildfire.create.tina.base.raster.RasterPoint;
import org.jwildfire.create.tina.render.LightViewCalculator;
import org.jwildfire.create.tina.render.PlotSample;
import org.jwildfire.create.tina.render.filter.FilterKernel;
import org.jwildfire.create.tina.render.filter.GaussianFilterKernel;

@SuppressWarnings("serial")
public class ShadowCalculator implements Serializable {
  private final int rasterWidth, rasterHeight;
  private final float originXBuf[][];
  private final float originYBuf[][];
  private final float originZBuf[][];

  private double[] lightX, lightY, shadowIntensity;

  private double shadowDistBias;

  private final int lightCount;
  private static final boolean withAcceleration = true;
  private float[][][] accLightProjectionZBuf;

  private int shadowMapSize;
  private final double shadowMapXScale[], shadowMapYScale[], shadowMapXCentre[], shadowMapYCentre[];

  private static final int PRE_SHADOWMAP_SIZE = 40960;
  private final float pre_shadowXBuf[][], pre_shadowYBuf[][], pre_shadowZBuf[][];
  private final int pre_shadowIndex[];

  private final int shadowSmoothRadius;
  private boolean shadowSoften;
  private final double shadowSmoothKernel[][];

  private final float shadowZBuf[][][];

  private LightViewCalculator lightViewCalculator;

  public ShadowCalculator(int rasterWidth, int rasterHeight, float[][] originXBuf, float[][] originYBuf, float[][] originZBuf, double imgSize, Flame flame) {
    this.rasterWidth = rasterWidth;
    this.rasterHeight = rasterHeight;
    this.originXBuf = originXBuf;
    this.originYBuf = originYBuf;
    this.originZBuf = originZBuf;

    lightCount = flame.getSolidRenderSettings().getLights().size();
    shadowZBuf = new float[lightCount][][];

    pre_shadowIndex = new int[lightCount];
    pre_shadowXBuf = new float[lightCount][];
    pre_shadowYBuf = new float[lightCount][];
    pre_shadowZBuf = new float[lightCount][];
    shadowMapXCentre = new double[lightCount];
    shadowMapYCentre = new double[lightCount];
    shadowMapXScale = new double[lightCount];
    shadowMapYScale = new double[lightCount];

    lightX = new double[lightCount];
    lightY = new double[lightCount];
    shadowIntensity = new double[lightCount];

    shadowMapSize = flame.getSolidRenderSettings().getShadowmapSize();
    if (shadowMapSize < 64) {
      shadowMapSize = 64;
    }
    shadowDistBias = flame.getSolidRenderSettings().getShadowmapBias();

    for (int i = 0; i < lightCount; i++) {
      DistantLight light = flame.getSolidRenderSettings().getLights().get(i);
      if (light.isCastShadows()) {
        shadowZBuf[i] = new float[shadowMapSize][shadowMapSize];
        pre_shadowXBuf[i] = new float[PRE_SHADOWMAP_SIZE];
        pre_shadowYBuf[i] = new float[PRE_SHADOWMAP_SIZE];
        pre_shadowZBuf[i] = new float[PRE_SHADOWMAP_SIZE];
        for (int k = 0; k < shadowZBuf[i].length; k++) {
          for (int l = 0; l < shadowZBuf[i][0].length; l++) {
            shadowZBuf[i][k][l] = NormalsCalculator.ZBUF_ZMIN;
          }
        }
        lightX[i] = light.getAltitude();
        lightY[i] = light.getAzimuth();
        shadowIntensity[i] = 1.0 - GfxMathLib.clamp(light.getShadowIntensity(), 0.0, 1.0);
      }
      else {
        shadowZBuf[i] = null;
        pre_shadowXBuf[i] = pre_shadowYBuf[i] = pre_shadowZBuf[i] = null;
      }
    }

    shadowSoften = ShadowType.SMOOTH.equals(flame.getSolidRenderSettings().getShadowType());

    double rawSmoothRadius = flame.getSolidRenderSettings().getShadowSmoothRadius();
    if (rawSmoothRadius < MathLib.EPSILON) {
      rawSmoothRadius = 0.0;
    }

    shadowSmoothRadius = clipSmoothRadius(Tools.FTOI(rawSmoothRadius * 6.0 * imgSize / 1000.0));
    if (shadowSmoothRadius < 1.0) {
      shadowSoften = false;
      shadowSmoothKernel = null;
    }
    else {
      shadowSmoothKernel = getShadowSmoothKernel(shadowSmoothRadius);
    }
  }

  private int clipSmoothRadius(int radius) {
    return radius < 128 ? radius : 128;
  }

  public void accelerateShadows() {
    if (withAcceleration) {
      accLightProjectionZBuf = new float[lightCount][][];
      for (int i = 0; i < lightCount; i++) {
        if (shadowZBuf[i] != null) {
          accLightProjectionZBuf[i] = new float[rasterWidth][rasterHeight];
          for (int pX = 0; pX < rasterWidth; pX++) {
            for (int pY = 0; pY < rasterHeight; pY++) {
              accLightProjectionZBuf[i][pX][pY] = NormalsCalculator.ZBUF_ZMIN;
              float originX = originXBuf[pX][pY];
              if (originX != NormalsCalculator.ZBUF_ZMIN) {
                float originY = originYBuf[pX][pY];
                if (originY != NormalsCalculator.ZBUF_ZMIN) {
                  float originZ = originZBuf[pX][pY];
                  if (originZ != NormalsCalculator.ZBUF_ZMIN) {
                    int x = projectPointToLightMapX(i, lightViewCalculator.applyLightProjectionX(i, originX, originY, originZ));
                    int y = projectPointToLightMapY(i, lightViewCalculator.applyLightProjectionY(i, originX, originY, originZ));
                    if (x >= 0 && x < shadowZBuf[i].length && y >= 0 && y < shadowZBuf[i][0].length) {
                      double lightZ = lightViewCalculator.applyLightProjectionZ(i, originX, originY, originZ);
                      accLightProjectionZBuf[i][pX][pY] = (float) GfxMathLib.step(shadowZBuf[i][x][y] - shadowDistBias, lightZ);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private static Map<Integer, double[][]> shadowSmoothKernelMap = new HashMap<>();

  private double[][] getShadowSmoothKernel(int radius) {
    double res[][] = shadowSmoothKernelMap.get(Integer.valueOf(radius));
    if (res == null) {
      FilterKernel shadowSmoothKernel = new GaussianFilterKernel();
      double shadowKernelScale = 1.0;
      double shadowScaledInvRadius = 1.0 / shadowSmoothRadius * shadowKernelScale * shadowSmoothKernel.getSpatialSupport();

      int size = 2 * radius + 1;
      res = new double[size][size];

      for (int k = -radius; k <= radius; k++) {
        int x = radius + k;
        double k_square = MathLib.sqr(k * shadowScaledInvRadius);
        for (int l = -radius; l <= radius; l++) {
          int y = radius + l;
          double r = MathLib.sqrt(k_square + MathLib.sqr(l * shadowScaledInvRadius));
          res[x][y] = shadowSmoothKernel.getFilterCoeff(r);
        }
      }

      synchronized (shadowSmoothKernelMap) {
        shadowSmoothKernelMap.put(Integer.valueOf(radius), res);
      }
    }

    return res;
  }

  public void calcShadows(int pX, int pY, RasterPoint pDestRasterPoint) {
    pDestRasterPoint.hasShadows = true;
    for (int i = 0; i < shadowZBuf.length; i++) {
      pDestRasterPoint.visibility[i] = 1.0;
      if (shadowZBuf[i] != null) {
        if (shadowSoften) {
          calcSmoothShadowIntensity(pX, pY, pDestRasterPoint, i, shadowSmoothRadius, shadowSmoothKernel);
        }
        else {
          calcFastShadowIntensity(pX, pY, pDestRasterPoint, i);
        }
        pDestRasterPoint.visibility[i] = GfxMathLib.clamp(pDestRasterPoint.visibility[i] + shadowIntensity[i]);
      }
    }
  }

  private void calcFastShadowIntensity(int pX, int pY, RasterPoint pDestRasterPoint, int i) {
    if (accLightProjectionZBuf != null && accLightProjectionZBuf[i] != null) {
      if (accLightProjectionZBuf[i][pX][pY] > NormalsCalculator.ZBUF_ZMIN) {
        pDestRasterPoint.visibility[i] = accLightProjectionZBuf[i][pX][pY];
      }
    }
    else {
      float originX = originXBuf[pX][pY];
      if (originX != NormalsCalculator.ZBUF_ZMIN) {
        float originY = originYBuf[pX][pY];
        if (originY != NormalsCalculator.ZBUF_ZMIN) {
          float originZ = originZBuf[pX][pY];
          if (originZ != NormalsCalculator.ZBUF_ZMIN) {
            int x = projectPointToLightMapX(i, lightViewCalculator.applyLightProjectionX(i, originX, originY, originZ));
            int y = projectPointToLightMapY(i, lightViewCalculator.applyLightProjectionY(i, originX, originY, originZ));
            if (x >= 0 && x < shadowZBuf[i].length && y >= 0 && y < shadowZBuf[i][0].length) {
              double lightZ = lightViewCalculator.applyLightProjectionZ(i, originX, originY, originZ);
              if (lightZ < shadowZBuf[i][x][y] - shadowDistBias) {
                pDestRasterPoint.visibility[i] = 0.0;
              }
              //            pDestRasterPoint.visibility[i] = GfxMathLib.step(shadowZBuf[i][x][y] - bias, lightZ);
            }
          }
        }
      }
    }
  }

  private void calcSmoothShadowIntensity(int pX, int pY, RasterPoint pDestRasterPoint, int i, int shadowSmoothRadius, double[][] shadowSmoothKernel) {
    pDestRasterPoint.visibility[i] = 1.0;
    double totalIntensity = 0.0;

    if (accLightProjectionZBuf != null && accLightProjectionZBuf[i] != null) {
      int dl = shadowSmoothRadius / 8 + 1;
      for (int k = -shadowSmoothRadius; k <= shadowSmoothRadius; k += dl) {
        int dstX = pX + k;
        if (dstX >= 0 && dstX < originXBuf.length) {
          for (int l = -shadowSmoothRadius; l <= shadowSmoothRadius; l += dl) {
            int dstY = pY + l;
            if (dstY >= 0 && dstY < originXBuf[0].length) {
              if (accLightProjectionZBuf[i][dstX][dstY] > NormalsCalculator.ZBUF_ZMIN) {
                double intensity = shadowSmoothKernel[k + shadowSmoothRadius][l + shadowSmoothRadius];
                totalIntensity += intensity;
                pDestRasterPoint.visibility[i] += accLightProjectionZBuf[i][dstX][dstY] * intensity;
              }
            }
          }
        }
      }
    }
    else {

      for (int k = -shadowSmoothRadius; k <= shadowSmoothRadius; k++) {
        int dstX = pX + k;
        if (dstX >= 0 && dstX < originXBuf.length) {
          for (int l = -shadowSmoothRadius; l <= shadowSmoothRadius; l++) {
            int dstY = pY + l;
            if (dstY >= 0 && dstY < originXBuf[0].length) {
              float originX = originXBuf[dstX][dstY];
              if (originX != NormalsCalculator.ZBUF_ZMIN) {
                float originY = originYBuf[dstX][dstY];
                if (originY != NormalsCalculator.ZBUF_ZMIN) {
                  float originZ = originZBuf[dstX][dstY];
                  if (originZ != NormalsCalculator.ZBUF_ZMIN) {
                    int x = projectPointToLightMapX(i, lightViewCalculator.applyLightProjectionX(i, originX, originY, originZ));
                    int y = projectPointToLightMapY(i, lightViewCalculator.applyLightProjectionY(i, originX, originY, originZ));
                    if (x >= 0 && x < shadowZBuf[i].length && y >= 0 && y < shadowZBuf[i][0].length) {
                      double intensity = shadowSmoothKernel[k + shadowSmoothRadius][l + shadowSmoothRadius];
                      totalIntensity += intensity;
                      double lightZ = lightViewCalculator.applyLightProjectionZ(i, originX, originY, originZ);
                      pDestRasterPoint.visibility[i] += GfxMathLib.step(shadowZBuf[i][x][y] - shadowDistBias, lightZ) * intensity;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    if (totalIntensity > MathLib.EPSILON) {
      pDestRasterPoint.visibility[i] /= totalIntensity;
    }
  }

  private int projectPointToLightMapX(int shadowMapIdx, double x) {
    return (int) (shadowMapXScale[shadowMapIdx] * x + shadowMapXCentre[shadowMapIdx] + 0.5);
  }

  private int projectPointToLightMapY(int shadowMapIdx, double y) {
    return (int) (shadowMapYScale[shadowMapIdx] * y + shadowMapYCentre[shadowMapIdx] + 0.5);
  }

  public void addShadowMapSamples(int pShadowMapIdx, PlotSample[] pPlotBuffer, int pCount) {
    float[][] shadowMap = shadowZBuf[pShadowMapIdx];
    for (int i = 0; i < pCount; i++) {
      PlotSample sample = pPlotBuffer[i];

      if (pre_shadowIndex[pShadowMapIdx] < 0) {
        int x = projectPointToLightMapX(pShadowMapIdx, sample.x);
        if (x >= 0 && x < shadowMap.length) {
          int y = projectPointToLightMapY(pShadowMapIdx, sample.y);
          if (y >= 0 && y < shadowMap[0].length) {
            if (sample.z > shadowMap[x][y]) {
              shadowMap[x][y] = (float) sample.z;
            }
          }
        }
      }
      else {
        if (pre_shadowIndex[pShadowMapIdx] < pre_shadowXBuf[pShadowMapIdx].length) {
          int idx = pre_shadowIndex[pShadowMapIdx];
          pre_shadowXBuf[pShadowMapIdx][idx] = (float) sample.x;
          pre_shadowYBuf[pShadowMapIdx][idx] = (float) sample.y;
          pre_shadowZBuf[pShadowMapIdx][idx] = (float) sample.z;
          pre_shadowIndex[pShadowMapIdx]++;
        }
        else {
          float xmin = pre_shadowXBuf[pShadowMapIdx][0];
          float xmax = xmin;
          float ymin = pre_shadowYBuf[pShadowMapIdx][0];
          float ymax = ymin;
          for (int k = 0; k < pre_shadowXBuf[pShadowMapIdx].length; k++) {
            if (pre_shadowXBuf[pShadowMapIdx][k] < xmin) {
              xmin = pre_shadowXBuf[pShadowMapIdx][k];
            }
            else if (pre_shadowXBuf[pShadowMapIdx][k] > xmax) {
              xmax = pre_shadowXBuf[pShadowMapIdx][k];
            }
            if (pre_shadowYBuf[pShadowMapIdx][k] < ymin) {
              ymin = pre_shadowYBuf[pShadowMapIdx][k];
            }
            else if (pre_shadowYBuf[pShadowMapIdx][k] > ymax) {
              ymax = pre_shadowYBuf[pShadowMapIdx][k];
            }
          }

          {
            double safety = 0.03;
            double dx = xmax - xmin;
            double dy = ymax - ymin;
            xmin -= safety * dx;
            xmax += safety * dx;
            ymin -= safety * dy;
            ymax += safety * dy;
          }

          double eps = 0.001;
          double dx = xmax - xmin;
          double dy = ymax - ymin;
          if (dx < eps) {
            dx = eps;
          }
          if (dy < eps) {
            dy = eps;
          }

          shadowMapXScale[pShadowMapIdx] = (double) shadowMapSize / dx;
          shadowMapYScale[pShadowMapIdx] = (double) shadowMapSize / dy;

          shadowMapXCentre[pShadowMapIdx] = -xmin * shadowMapXScale[pShadowMapIdx];
          shadowMapYCentre[pShadowMapIdx] = -ymin * shadowMapYScale[pShadowMapIdx];

          pre_shadowIndex[pShadowMapIdx] = -1;

          for (int k = 0; k < pre_shadowXBuf[pShadowMapIdx].length; k++) {
            int x = projectPointToLightMapX(pShadowMapIdx, pre_shadowXBuf[pShadowMapIdx][k]);
            if (x >= 0 && x < shadowMap.length) {
              int y = projectPointToLightMapY(pShadowMapIdx, pre_shadowYBuf[pShadowMapIdx][k]);
              if (y >= 0 && y < shadowMap[0].length) {
                float z = pre_shadowZBuf[pShadowMapIdx][k];
                if (z > shadowMap[x][y]) {
                  shadowMap[x][y] = z;
                }
              }
            }
          }
        }
      }
    }
  }

  public void setLightViewCalculator(LightViewCalculator lightViewCalculator) {
    this.lightViewCalculator = lightViewCalculator;
  }

  public void finalizeRaster() {
    //    for (int i = 0; i < shadowZBuf.length; i++) {
    //      if (shadowZBuf[i] != null) {
    //        RasterTools.saveFloatBuffer(shadowZBuf[i], "D:\\TMP\\wf_shadowmap_" + i + ".png");
    //      }
    //      if (accLightProjectionZBuf[i] != null) {
    //        RasterTools.saveFloatBuffer(accLightProjectionZBuf[i], "D:\\TMP\\wf_acc_shadowmap_" + i + ".png");
    //      }
    //    }
  }

  public LightViewCalculator getLightViewCalculator() {
    return lightViewCalculator;
  }

}
