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
package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.log10;
import static org.jwildfire.base.mathlib.MathLib.sin;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathlib.VecMathLib;
import org.jwildfire.base.mathlib.VecMathLib.RGBColorD;
import org.jwildfire.base.mathlib.VecMathLib.UVPairD;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.raster.AbstractRaster;
import org.jwildfire.create.tina.base.raster.RasterPoint;
import org.jwildfire.create.tina.base.solidrender.MaterialSettings;
import org.jwildfire.create.tina.base.solidrender.PointLight;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.swing.ChannelMixerCurves;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;

@SuppressWarnings("serial")
public class LogDensityFilter extends FilterHolder {
  private final ColorFunc colorFunc;

  private AbstractRaster raster;
  private int rasterWidth, rasterHeight, rasterSize;
  private static final int PRECALC_LOG_ARRAY_SIZE = 512;
  private double precalcLogArray[];
  private double k1, k2;
  private double motionBlurScl;
  private final AbstractRandomGenerator jitterRandGen, dofRandGen;
  private final boolean jitter;
  private final int colorOversampling;
  private boolean solidRendering;

  public LogDensityFilter(Flame pFlame, AbstractRandomGenerator pRandGen) {
    super(pFlame);
    colorFunc = pFlame.getChannelMixerMode().getColorFunc(pFlame, pRandGen);
    motionBlurScl = flame.getMotionBlurLength() <= 0 ? 1.0 : 1.0 / (flame.getMotionBlurLength() + 1.0);
    jitter = pFlame.isSampleJittering();
    colorOversampling = jitter ? pFlame.getColorOversampling() : 1;
    if (jitter) {
      jitterRandGen = new MarsagliaRandomGenerator();
      jitterRandGen.randomize(pFlame.hashCode());
    }
    else {
      jitterRandGen = null;
    }
    solidRendering = flame.getSolidRenderSettings().isSolidRenderingEnabled();
    if (solidRendering && flame.getCamDOF() > MathLib.EPSILON) {
      dofRandGen = new MarsagliaRandomGenerator();
      dofRandGen.randomize(pFlame.hashCode());
    }
    else {
      dofRandGen = null;
    }
  }

  public void setRaster(AbstractRaster pRaster, int pRasterWidth, int pRasterHeight, int pImageWidth, int pImageHeight) {
    raster = pRaster;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    rasterSize = rasterWidth * rasterHeight;
    k1 = flame.getContrast() * 2.0 * flame.getBrightness() / (double) (oversample);
    switch (flame.getPostSymmetryType()) {
      case POINT:
        k1 /= (double) flame.getPostSymmetryOrder();
        break;
      case X_AXIS:
      case Y_AXIS:
        k1 /= 2.0;
    }
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double area = ((double) pImageWidth * (double) pImageHeight) / (pixelsPerUnit * pixelsPerUnit);
    k2 = 1.0 / (flame.getContrast() * area * flame.getSampleDensity());

    precalcLogArray = new double[PRECALC_LOG_ARRAY_SIZE + 1];
    for (int i = 1; i <= PRECALC_LOG_ARRAY_SIZE; i++) {
      double x = i * motionBlurScl;
      precalcLogArray[i] = (k1 * log10(1 + x * k2)) / (flame.getWhiteLevel() * x);
    }
  }

  public void transformPointSimple(LogDensityPoint pFilteredPnt, int pX, int pY) {
    pFilteredPnt.clear();
    int solidSampleCount = 0;
    for (int px = 0; px < oversample; px++) {
      for (int py = 0; py < oversample; py++) {
        getSample(pFilteredPnt, pX * oversample + px, pY * oversample + py);

        if (solidRendering && pFilteredPnt.rp.hasSolidColors && addSolidColors(pFilteredPnt, pFilteredPnt.rp, 1.0)) {
          solidSampleCount++;
          pFilteredPnt.dofDist += pFilteredPnt.rp.dofDist;
          pFilteredPnt.intensity = 1.0;
        }
        else {
          double logScale;
          long pCount = pFilteredPnt.rp.count;
          if (pCount < precalcLogArray.length) {
            logScale = precalcLogArray[(int) pCount];
          }
          else {
            logScale = (k1 * log10(1.0 + pCount * motionBlurScl * k2)) / (flame.getWhiteLevel() * pCount * motionBlurScl);
          }
          if (pCount > 0) {
            if (colorFunc == ColorFunc.NULL) {
              pFilteredPnt.red += logScale * pFilteredPnt.rp.red;
              pFilteredPnt.green += logScale * pFilteredPnt.rp.green;
              pFilteredPnt.blue += logScale * pFilteredPnt.rp.blue;
            }
            else {
              final double scale = ChannelMixerCurves.FILTER_SCALE;
              double rawR = pFilteredPnt.rp.red * scale / pCount;
              double rawG = pFilteredPnt.rp.green * scale / pCount;
              double rawB = pFilteredPnt.rp.blue * scale / pCount;

              pFilteredPnt.red += logScale * colorFunc.mapRGBToR(rawR, rawG, rawB) * pCount / scale;
              pFilteredPnt.green += logScale * colorFunc.mapRGBToG(rawR, rawG, rawB) * pCount / scale;
              pFilteredPnt.blue += logScale * colorFunc.mapRGBToB(rawR, rawG, rawB) * pCount / scale;
            }
            pFilteredPnt.intensity += logScale * pCount * flame.getWhiteLevel();
          }
        }
      }
    }

    if (solidRendering && (solidSampleCount > 0)) {
      double dCount = 1.0 / (double) solidSampleCount;
      pFilteredPnt.dofDist *= dCount;
      pFilteredPnt.solidRed *= dCount;
      pFilteredPnt.solidGreen *= dCount;
      pFilteredPnt.solidBlue *= dCount;
    }

  }

  private void getZSample(ZBufferSample pDest, int pX, int pY) {
    raster.readZBufferSafe(pX, pY, pDest);
  }

  private void getSample(LogDensityPoint pFilteredPnt, int pX, int pY) {
    if (jitter && !solidRendering) {
      final double epsilon = 0.0001;
      final double radius = 0.25;
      double dr = log(jitterRandGen.random() + 0.1) + 1;
      if (dr < epsilon) {
        dr = epsilon;
      }
      else if (dr > 1.0 - epsilon) {
        dr = 1.0 - epsilon;
      }
      double da = epsilon + (jitterRandGen.random() - 2 * epsilon) * M_PI * 2.0;
      double x = dr * cos(da) * radius;
      int xi = x < 0 ? -1 : 1;
      x = MathLib.fabs(x);
      double y = dr * sin(da) * radius;
      int yi = y < 0 ? -1 : 1;
      y = MathLib.fabs(y);

      raster.readRasterPointSafe(pX, pY, pFilteredPnt.lu);
      raster.readRasterPointSafe(pX + xi, pY, pFilteredPnt.ru);
      raster.readRasterPointSafe(pX, pY + yi, pFilteredPnt.lb);
      raster.readRasterPointSafe(pX + xi, pY + yi, pFilteredPnt.rb);
      pFilteredPnt.rp.clear();
      pFilteredPnt.rp.red = GfxMathLib.blerp(pFilteredPnt.lu.red, pFilteredPnt.ru.red, pFilteredPnt.lb.red, pFilteredPnt.rb.red, x, y);
      pFilteredPnt.rp.green = GfxMathLib.blerp(pFilteredPnt.lu.green, pFilteredPnt.ru.green, pFilteredPnt.lb.green, pFilteredPnt.rb.green, x, y);
      pFilteredPnt.rp.blue = GfxMathLib.blerp(pFilteredPnt.lu.blue, pFilteredPnt.ru.blue, pFilteredPnt.lb.blue, pFilteredPnt.rb.blue, x, y);
      pFilteredPnt.rp.count = Math.round(GfxMathLib.blerp(pFilteredPnt.lu.count, pFilteredPnt.ru.count, pFilteredPnt.lb.count, pFilteredPnt.rb.count, x, y));

      pFilteredPnt.rp.hasMaterial = pFilteredPnt.lu.hasMaterial;
      pFilteredPnt.rp.material = pFilteredPnt.lu.material;
      pFilteredPnt.rp.hasSSAO = pFilteredPnt.lu.hasSSAO;
      pFilteredPnt.rp.ao = pFilteredPnt.lu.ao;
      pFilteredPnt.rp.hasNormals = pFilteredPnt.lu.hasNormals;
      pFilteredPnt.rp.nx = pFilteredPnt.lu.nx;
      pFilteredPnt.rp.ny = pFilteredPnt.lu.ny;
      pFilteredPnt.rp.nz = pFilteredPnt.lu.nz;
      pFilteredPnt.rp.zBuf = pFilteredPnt.lu.zBuf;

      pFilteredPnt.rp.hasSolidColors = pFilteredPnt.lu.hasSolidColors;
      pFilteredPnt.rp.solidRed = pFilteredPnt.lu.solidRed;
      pFilteredPnt.rp.solidGreen = pFilteredPnt.lu.solidGreen;
      pFilteredPnt.rp.solidBlue = pFilteredPnt.lu.solidBlue;
    }
    else {
      raster.readRasterPointSafe(pX, pY, pFilteredPnt.rp);
    }
  }

  public double calcDensity(long pSampleCount, long pRasterSize) {
    return (double) pSampleCount / (double) pRasterSize * oversample;
  }

  public double calcDensity(long pSampleCount) {
    if (rasterSize == 0) {
      throw new IllegalStateException();
    }
    return (double) pSampleCount / (double) rasterSize * oversample;
  }

  public void transformPoint(LogDensityPoint pFilteredPnt, int pX, int pY) {
    pFilteredPnt.clear();
    if (noiseFilterSize > 1) {
      if (solidRendering) {
        for (int c = 0; c < colorOversampling; c++) {
          for (int i = 0; i < noiseFilterSize; i++) {
            for (int j = 0; j < noiseFilterSize; j++) {
              getSample(pFilteredPnt, pX * oversample + j, pY * oversample + i);
              if (pFilteredPnt.rp.hasSolidColors) {
                double f = filter[i][j] / (double) (colorOversampling * oversample * oversample);
                if (addSolidColors(pFilteredPnt, pFilteredPnt.rp, f)) {
                  pFilteredPnt.dofDist += f * pFilteredPnt.rp.dofDist;
                  pFilteredPnt.intensity += f;
                }
              }
            }
          }
        }
      }
      else {
        if (colorFunc == ColorFunc.NULL) {
          for (int c = 0; c < colorOversampling; c++) {
            for (int i = 0; i < noiseFilterSize; i++) {
              for (int j = 0; j < noiseFilterSize; j++) {
                getSample(pFilteredPnt, pX * oversample + j, pY * oversample + i);
                long count = pFilteredPnt.rp.count;
                int pIdx = (int) count;
                if (pIdx > 0) {
                  double logScale;
                  if (pIdx < precalcLogArray.length) {
                    logScale = precalcLogArray[pIdx];
                  }
                  else {
                    logScale = (k1 * log10(1.0 + count * motionBlurScl * k2)) / (flame.getWhiteLevel() * count * motionBlurScl);
                  }
                  pFilteredPnt.red += filter[i][j] * logScale * pFilteredPnt.rp.red / (double) colorOversampling;
                  pFilteredPnt.green += filter[i][j] * logScale * pFilteredPnt.rp.green / (double) colorOversampling;
                  pFilteredPnt.blue += filter[i][j] * logScale * pFilteredPnt.rp.blue / (double) colorOversampling;
                  pFilteredPnt.intensity += filter[i][j] * logScale * count * flame.getWhiteLevel() / (double) colorOversampling;
                }
              }
            }
          }
        }
        else {
          for (int c = 0; c < colorOversampling; c++) {
            for (int i = 0; i < noiseFilterSize; i++) {
              for (int j = 0; j < noiseFilterSize; j++) {
                getSample(pFilteredPnt, pX * oversample + j, pY * oversample + i);
                long count = pFilteredPnt.rp.count;
                int pIdx = (int) count;
                if (pIdx > 0) {
                  double logScale;
                  if (pIdx < precalcLogArray.length) {
                    logScale = precalcLogArray[pIdx];
                  }
                  else {
                    logScale = (k1 * log10(1.0 + count * motionBlurScl * k2)) / (flame.getWhiteLevel() * count * motionBlurScl);
                  }
                  final double scale = ChannelMixerCurves.FILTER_SCALE;
                  double rawR = pFilteredPnt.rp.red * scale / (double) count;
                  double rawG = pFilteredPnt.rp.green * scale / (double) count;
                  double rawB = pFilteredPnt.rp.blue * scale / (double) count;
                  double transR = colorFunc.mapRGBToR(rawR, rawG, rawB) * count / scale;
                  double transG = colorFunc.mapRGBToG(rawR, rawG, rawB) * count / scale;
                  double transB = colorFunc.mapRGBToB(rawR, rawG, rawB) * count / scale;
                  pFilteredPnt.red += filter[i][j] * logScale * transR / (double) colorOversampling;
                  pFilteredPnt.green += filter[i][j] * logScale * transG / (double) colorOversampling;
                  pFilteredPnt.blue += filter[i][j] * logScale * transB / (double) colorOversampling;
                  pFilteredPnt.intensity += filter[i][j] * logScale * count * flame.getWhiteLevel() / (double) colorOversampling;
                }
              }
            }
          }
        }
      }
    }
    else {
      int solidSampleCount = 0;
      for (int c = 0; c < colorOversampling; c++) {
        for (int px = 0; px < oversample; px++) {
          for (int py = 0; py < oversample; py++) {
            getSample(pFilteredPnt, pX * oversample + px, pY * oversample + py);
            if (solidRendering) {
              if (pFilteredPnt.rp.hasSolidColors && addSolidColors(pFilteredPnt, pFilteredPnt.rp, 1.0)) {
                pFilteredPnt.dofDist += pFilteredPnt.rp.dofDist;
                solidSampleCount++;
              }
            }
            else {
              double logScale;
              long pCount = pFilteredPnt.rp.count;
              if (pCount > 0) {
                if (pCount < precalcLogArray.length) {
                  logScale = precalcLogArray[(int) pCount];
                }
                else {
                  logScale = (k1 * log10(1.0 + pCount * motionBlurScl * k2)) / (flame.getWhiteLevel() * pCount * motionBlurScl);
                }
                if (colorFunc == ColorFunc.NULL) {
                  pFilteredPnt.red += logScale * pFilteredPnt.rp.red / (double) colorOversampling;
                  pFilteredPnt.green += logScale * pFilteredPnt.rp.green / (double) colorOversampling;
                  pFilteredPnt.blue += logScale * pFilteredPnt.rp.blue / (double) colorOversampling;
                }
                else {
                  final double scale = ChannelMixerCurves.FILTER_SCALE;
                  double rawR = pFilteredPnt.rp.red * scale / pCount;
                  double rawG = pFilteredPnt.rp.green * scale / pCount;
                  double rawB = pFilteredPnt.rp.blue * scale / pCount;

                  pFilteredPnt.red += logScale * colorFunc.mapRGBToR(rawR, rawG, rawB) * pCount / scale / (double) colorOversampling;
                  pFilteredPnt.green += logScale * colorFunc.mapRGBToG(rawR, rawG, rawB) * pCount / scale / (double) colorOversampling;
                  pFilteredPnt.blue += logScale * colorFunc.mapRGBToB(rawR, rawG, rawB) * pCount / scale / (double) colorOversampling;
                }
                pFilteredPnt.intensity += logScale * pFilteredPnt.rp.count * flame.getWhiteLevel() / (double) colorOversampling;
              }
            }
          }
        }
      }
      if (solidRendering && (solidSampleCount > 0)) {
        double dCount = 1.0 / (double) solidSampleCount;
        pFilteredPnt.dofDist *= dCount;
        pFilteredPnt.solidRed *= dCount;
        pFilteredPnt.solidGreen *= dCount;
        pFilteredPnt.solidBlue *= dCount;
        pFilteredPnt.intensity = (double) solidSampleCount / (double) (colorOversampling * oversample * oversample);
      }
    }
    pFilteredPnt.clip();
  }

  public void transformZPoint(ZBufferSample pAccumSample, ZBufferSample pSample, int pX, int pY) {
    pAccumSample.clear();
    int solidSampleCount = 0;
    for (int c = 0; c < colorOversampling; c++) {
      for (int px = 0; px < oversample; px++) {
        for (int py = 0; py < oversample; py++) {
          getZSample(pSample, pX * oversample + px, pY * oversample + py);
          if (pSample.hasZ) {
            pAccumSample.z += pSample.z;
            pAccumSample.hasZ = true;
            solidSampleCount++;
          }
        }
      }
      if (solidSampleCount > 0) {
        pAccumSample.z /= (double) solidSampleCount;
      }
    }
  }

  private boolean addSolidColors(LogDensityPoint dest, RasterPoint rp, double colorScale) {
    if (solidRendering && rp.hasNormals) {
      MaterialSettings material = flame.getSolidRenderSettings().getInterpolatedMaterial(rp.material);
      if (material != null) {
        double aoInt = Tools.limitValue(flame.getSolidRenderSettings().getAoIntensity(), 0.0, 4.0);
        boolean withSSAO = flame.getSolidRenderSettings().isAoEnabled();
        double ambientIntensity = Math.max(0.0, withSSAO ? (material.getAmbient() - rp.ao * aoInt) : material.getAmbient());
        double aoDiffuseInfluence = flame.getSolidRenderSettings().getAoAffectDiffuse();
        double diffuseIntensity = Math.max(0.0, withSSAO ? (material.getDiffuse() - rp.ao * aoInt * aoDiffuseInfluence) : material.getDiffuse());
        double specularIntensity = material.getPhong();

        SimpleImage reflectionMap = null;
        if (material.getReflMapIntensity() > MathLib.EPSILON && material.getReflMapFilename() != null && material.getReflMapFilename().length() > 0) {
          try {
            reflectionMap = (SimpleImage) RessourceManager.getImage(material.getReflMapFilename());
          }
          catch (Exception e) {
            material.setReflMapFilename(null);
            e.printStackTrace();
          }
        }

        double avgVisibility;
        if (rp.hasShadows) {
          avgVisibility = 0.0;
          int shadowCount = 0;
          for (int i = 0; i < rp.visibility.length; i++) {
            avgVisibility += rp.visibility[i];
            shadowCount++;
          }
          if (shadowCount > 0) {
            avgVisibility /= (double) shadowCount;
          }
          else {
            avgVisibility = 1.0;
          }
        }
        else {
          avgVisibility = 1.0;
        }

        RGBColorD objColor = new RGBColorD(rp.solidRed, rp.solidGreen, rp.solidBlue, 1.0 / VecMathLib.COLORSCL);
        RGBColorD rawColor = new RGBColorD(objColor, ambientIntensity * avgVisibility);
        VectorD normal = new VectorD(rp.nx, rp.ny, rp.nz);
        VectorD viewDir = new VectorD(0.0, 0.0, 1.0);

        for (int i = 0; i < flame.getSolidRenderSettings().getLights().size(); i++) {
          PointLight light = flame.getSolidRenderSettings().getLights().get(i);
          VectorD lightDir = new VectorD(light.getX(), light.getY(), light.getZ());
          lightDir.normalize();
          double visibility = light.isCastShadows() && rp.hasShadows ? rp.visibility[i] : avgVisibility;

          double cosa = VectorD.dot(lightDir, normal);
          if (cosa > MathLib.EPSILON) {
            double diffResponse = material.getLightDiffFunc().evaluate(cosa);
            rawColor.addFrom(
                light.getRed() + objColor.r * ambientIntensity / 3.0,
                light.getGreen() + objColor.g * ambientIntensity / 3.0,
                light.getBlue() + objColor.b * ambientIntensity / 3.0, visibility * diffResponse * diffuseIntensity * light.getIntensity());
          }
          if (specularIntensity > MathLib.EPSILON) {
            VectorD r = VectorD.reflect(lightDir, normal);
            double vr = VectorD.dot(viewDir, r);
            if (vr > MathLib.EPSILON) {
              double specularResponse = MathLib.pow(material.getLightDiffFunc().evaluate(vr), material.getPhongSize());
              rawColor.addFrom(material.getPhongRed(), material.getPhongGreen(), material.getPhongBlue(),
                  visibility * specularResponse * specularIntensity);
            }
          }

          // http://www.reindelsoftware.com/Documents/Mapping/Mapping.html
          if (reflectionMap != null) {
            double reflectionMapIntensity = Math.max(0.0, withSSAO ? (material.getReflMapIntensity() - rp.ao * aoInt * aoDiffuseInfluence) : material.getReflMapIntensity());

            VectorD r = VectorD.reflect(viewDir, normal);
            UVPairD uv;
            switch (material.getReflectionMapping()) {
              case SPHERICAL:
                uv = UVPairD.sphericalOpenGlMapping(r);
                break;
              case BLINN_NEWELL:
              default:
                uv = UVPairD.sphericalBlinnNewellLatitudeMapping(r);
                break;
            }
            RGBColorD reflMapColor = uv.getColorFromMap(reflectionMap);
            rawColor.addFrom(reflMapColor.r, reflMapColor.g, reflMapColor.b, visibility * reflectionMapIntensity);
          }
        }
        dest.solidRed += rawColor.r * colorScale * VecMathLib.COLORSCL;
        dest.solidGreen += rawColor.g * colorScale * VecMathLib.COLORSCL;
        dest.solidBlue += rawColor.b * colorScale * VecMathLib.COLORSCL;
        dest.hasSolidColors = true;
        return true;
      }
    }
    return false;
  }
}
