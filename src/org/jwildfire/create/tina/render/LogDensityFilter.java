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
package org.jwildfire.create.tina.render;

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
import org.jwildfire.create.tina.base.solidrender.DistantLight;
import org.jwildfire.create.tina.base.solidrender.MaterialSettings;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.swing.ChannelMixerCurves;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

@SuppressWarnings("serial")
public class LogDensityFilter extends FilterHolder {
  private final ColorFunc colorFunc;

  private AbstractRaster raster;
  private int rasterWidth, rasterHeight, rasterSize;
  private int destImageWidth, destImageHeight;
  private double precalcLogArray[];
  private final AbstractRandomGenerator dofRandGen;
  private boolean solidRendering;
  private SimpleImage bgImage;
  private LogScaleCalculator logScaleCalculator;

  public LogDensityFilter(Flame pFlame, AbstractRandomGenerator pRandGen) {
    super(pFlame);
    colorFunc = pFlame.getChannelMixerMode().getColorFunc(pFlame, pRandGen);
    solidRendering = flame.getSolidRenderSettings().isSolidRenderingEnabled();
    if (solidRendering && flame.getCamDOF() > MathLib.EPSILON) {
      dofRandGen = new MarsagliaRandomGenerator();
      dofRandGen.randomize(pFlame.hashCode());
    }
    else {
      dofRandGen = null;
    }

    if (flame.getBGImageFilename().length() > 0) {
      try {
        bgImage = (SimpleImage) RessourceManager.getImage(flame.getBGImageFilename());
        if (bgImage.getImageWidth() < 2 || bgImage.getImageHeight() < 2) {
          bgImage = null;
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public void setRaster(AbstractRaster pRaster, int pRasterWidth, int pRasterHeight, int pImageWidth, int pImageHeight) {
    raster = pRaster;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    rasterSize = rasterWidth * rasterHeight;
    logScaleCalculator = new LogScaleCalculator(flame, pImageWidth, pImageHeight, oversample);
    precalcLogArray = logScaleCalculator.precalcLogArray();
    destImageWidth = pImageWidth;
    destImageHeight = pImageHeight;
  }

  public void transformPointSimple(LogDensityPoint pFilteredPnt, int pX, int pY) {
    double balanceRed = logScaleCalculator.getBalanceRed();
    double balanceGreen = logScaleCalculator.getBalanceGreen();
    double balanceBlue = logScaleCalculator.getBalanceBlue();
    pFilteredPnt.clear();
    int solidSampleCount = 0;
    for (int px = 0; px < oversample; px++) {
      for (int py = 0; py < oversample; py++) {
        getSample(pFilteredPnt, pX * oversample + px, pY * oversample + py);

        if (solidRendering) {
          if (pFilteredPnt.rp.hasSolidColors && addSolidColors(pFilteredPnt, pFilteredPnt.rp, 1.0)) {
            solidSampleCount++;
            pFilteredPnt.dofDist += pFilteredPnt.rp.dofDist;
            pFilteredPnt.intensity = 1.0;
          }
        }
        else {
          double logScale;
          long pCount = pFilteredPnt.rp.count;
          if (pCount < precalcLogArray.length) {
            logScale = precalcLogArray[(int) pCount];
          }
          else {
            logScale = logScaleCalculator.calcLogScale(pCount);
          }
          if (pCount > 0) {
            if (colorFunc == ColorFunc.NULL) {
              pFilteredPnt.red += logScale * pFilteredPnt.rp.red * balanceRed;
              pFilteredPnt.green += logScale * pFilteredPnt.rp.green * balanceGreen;
              pFilteredPnt.blue += logScale * pFilteredPnt.rp.blue * balanceBlue;
            }
            else {
              final double scale = ChannelMixerCurves.FILTER_SCALE;
              double rawR = pFilteredPnt.rp.red * scale / pCount;
              double rawG = pFilteredPnt.rp.green * scale / pCount;
              double rawB = pFilteredPnt.rp.blue * scale / pCount;

              pFilteredPnt.red += logScale * colorFunc.mapRGBToR(rawR, rawG, rawB) * pCount * balanceRed / scale;
              pFilteredPnt.green += logScale * colorFunc.mapRGBToG(rawR, rawG, rawB) * pCount * balanceGreen / scale;
              pFilteredPnt.blue += logScale * colorFunc.mapRGBToB(rawR, rawG, rawB) * pCount * balanceBlue / scale;
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
    calculateBGColor(pFilteredPnt, pX, pY);
  }

  private void getZSample(ZBufferSample pDest, int pX, int pY) {
    raster.readZBufferSafe(pX, pY, pDest);
  }

  private void getSample(LogDensityPoint pFilteredPnt, int pX, int pY) {
    raster.readRasterPointSafe(pX, pY, pFilteredPnt.rp);
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
    double balanceRed = logScaleCalculator.getBalanceRed();
    double balanceGreen = logScaleCalculator.getBalanceGreen();
    double balanceBlue = logScaleCalculator.getBalanceBlue();
    pFilteredPnt.clear();
    if (noiseFilterSize > 1) {
      if (solidRendering) {
        for (int i = 0; i < noiseFilterSize; i++) {
          for (int j = 0; j < noiseFilterSize; j++) {
            getSample(pFilteredPnt, pX * oversample + j - noiseFilterSizeHalve, pY * oversample + i - noiseFilterSizeHalve);
            if (pFilteredPnt.rp.hasSolidColors) {
              double f = filter[i][j] / (double) (oversample * oversample);
              if (addSolidColors(pFilteredPnt, pFilteredPnt.rp, f)) {
                pFilteredPnt.dofDist += f * pFilteredPnt.rp.dofDist;
                pFilteredPnt.intensity += f;
              }
            }
          }
        }
      }
      else {
        if (colorFunc == ColorFunc.NULL) {
          for (int i = 0; i < noiseFilterSize; i++) {
            for (int j = 0; j < noiseFilterSize; j++) {
              getSample(pFilteredPnt, pX * oversample + j - noiseFilterSizeHalve, pY * oversample + i - noiseFilterSizeHalve);
              long count = pFilteredPnt.rp.count;
              int pIdx = (int) count;
              if (pIdx > 0) {
                double logScale;
                if (pIdx < precalcLogArray.length) {
                  logScale = precalcLogArray[pIdx];
                }
                else {
                  logScale = logScaleCalculator.calcLogScale(count);
                }
                pFilteredPnt.red += filter[i][j] * logScale * pFilteredPnt.rp.red * balanceRed;
                pFilteredPnt.green += filter[i][j] * logScale * pFilteredPnt.rp.green * balanceGreen;
                pFilteredPnt.blue += filter[i][j] * logScale * pFilteredPnt.rp.blue * balanceBlue;
                pFilteredPnt.intensity += filter[i][j] * logScale * count * flame.getWhiteLevel();
              }
            }
          }
        }
        else {
          for (int i = 0; i < noiseFilterSize; i++) {
            for (int j = 0; j < noiseFilterSize; j++) {
              getSample(pFilteredPnt, pX * oversample + j - noiseFilterSizeHalve, pY * oversample + i - noiseFilterSizeHalve);
              long count = pFilteredPnt.rp.count;
              int pIdx = (int) count;
              if (pIdx > 0) {
                double logScale;
                if (pIdx < precalcLogArray.length) {
                  logScale = precalcLogArray[pIdx];
                }
                else {
                  logScale = logScaleCalculator.calcLogScale(count);
                }
                final double scale = ChannelMixerCurves.FILTER_SCALE;
                double rawR = pFilteredPnt.rp.red * scale / (double) count;
                double rawG = pFilteredPnt.rp.green * scale / (double) count;
                double rawB = pFilteredPnt.rp.blue * scale / (double) count;
                double transR = colorFunc.mapRGBToR(rawR, rawG, rawB) * count / scale;
                double transG = colorFunc.mapRGBToG(rawR, rawG, rawB) * count / scale;
                double transB = colorFunc.mapRGBToB(rawR, rawG, rawB) * count / scale;
                pFilteredPnt.red += filter[i][j] * logScale * transR * balanceRed;
                pFilteredPnt.green += filter[i][j] * logScale * transG * balanceGreen;
                pFilteredPnt.blue += filter[i][j] * logScale * transB * balanceBlue;
                pFilteredPnt.intensity += filter[i][j] * logScale * count * flame.getWhiteLevel();
              }
            }
          }
        }
      }
    }
    else {
      int solidSampleCount = 0;
      for (int px = 0; px < oversample; px++) {
        for (int py = 0; py < oversample; py++) {
          getSample(pFilteredPnt, pX * oversample + px - noiseFilterSizeHalve, pY * oversample + py - noiseFilterSizeHalve);
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
                logScale = logScaleCalculator.calcLogScale(pCount);
              }
              if (colorFunc == ColorFunc.NULL) {
                pFilteredPnt.red += logScale * pFilteredPnt.rp.red * balanceRed;
                pFilteredPnt.green += logScale * pFilteredPnt.rp.green * balanceGreen;
                pFilteredPnt.blue += logScale * pFilteredPnt.rp.blue * balanceBlue;
              }
              else {
                final double scale = ChannelMixerCurves.FILTER_SCALE;
                double rawR = pFilteredPnt.rp.red * scale / pCount;
                double rawG = pFilteredPnt.rp.green * scale / pCount;
                double rawB = pFilteredPnt.rp.blue * scale / pCount;

                pFilteredPnt.red += logScale * colorFunc.mapRGBToR(rawR, rawG, rawB) * pCount * balanceRed / scale;
                pFilteredPnt.green += logScale * colorFunc.mapRGBToG(rawR, rawG, rawB) * pCount * balanceGreen / scale;
                pFilteredPnt.blue += logScale * colorFunc.mapRGBToB(rawR, rawG, rawB) * pCount * balanceBlue / scale;
              }
              pFilteredPnt.intensity += logScale * pFilteredPnt.rp.count * flame.getWhiteLevel();
            }
          }
        }
      }
      if (solidRendering) {
        if (solidSampleCount > 0) {
          double dCount = 1.0 / (double) solidSampleCount;
          pFilteredPnt.dofDist *= dCount;
          pFilteredPnt.solidRed *= dCount;
          pFilteredPnt.solidGreen *= dCount;
          pFilteredPnt.solidBlue *= dCount;
          pFilteredPnt.intensity = (double) solidSampleCount / (double) (oversample * oversample);
        }
      }
    }
    pFilteredPnt.clip();
    calculateBGColor(pFilteredPnt, pX, pY);
  }

  public void transformZPoint(ZBufferSample pAccumSample, ZBufferSample pSample, int pX, int pY) {
    pAccumSample.clear();
    int solidSampleCount = 0;
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

  private boolean addSolidColors(LogDensityPoint dest, RasterPoint rp, double colorScale) {
    if (solidRendering && rp.hasNormals) {
      LightViewCalculator lightViewCalculator = raster.getLightViewCalculator();
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
      RGBColorD rawColor;

      MaterialSettings material = flame.getSolidRenderSettings().getInterpolatedMaterial(rp.material);
      if (material == null) {
        RGBColorD bgColor = new RGBColorD(dest.bgRed, dest.bgGreen, dest.bgBlue, 1.0 / VecMathLib.COLORSCL);
        double visibility = 0.0;
        for (int i = 0; i < flame.getSolidRenderSettings().getLights().size(); i++) {
          DistantLight light = flame.getSolidRenderSettings().getLights().get(i);
          visibility += light.isCastShadows() && rp.hasShadows ? rp.visibility[i] : avgVisibility;
        }
        visibility = GfxMathLib.clamp(visibility);
        rawColor = new RGBColorD(bgColor, visibility);
      }
      else {
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

        RGBColorD objColor = new RGBColorD(rp.solidRed, rp.solidGreen, rp.solidBlue, 1.0 / VecMathLib.COLORSCL);
        rawColor = new RGBColorD(objColor, ambientIntensity * avgVisibility);
        VectorD normal = new VectorD(rp.nx, rp.ny, rp.nz);
        VectorD viewDir = new VectorD(0.0, 0.0, 1.0);

        for (int i = 0; i < flame.getSolidRenderSettings().getLights().size(); i++) {
          DistantLight light = flame.getSolidRenderSettings().getLights().get(i);
          VectorD lightDir = lightViewCalculator.getLightDir()[i];
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
            if (vr < MathLib.EPSILON) {
              double specularResponse = MathLib.pow(material.getLightDiffFunc().evaluate(-vr), material.getPhongSize());
              rawColor.addFrom(material.getPhongRed(), material.getPhongGreen(), material.getPhongBlue(),
                  visibility * specularResponse * specularIntensity * light.getIntensity());
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
      }
      dest.solidRed += rawColor.r * colorScale * VecMathLib.COLORSCL;
      dest.solidGreen += rawColor.g * colorScale * VecMathLib.COLORSCL;
      dest.solidBlue += rawColor.b * colorScale * VecMathLib.COLORSCL;
      dest.hasSolidColors = true;
      return true;
    }
    return false;
  }

  private void calculateBGColor(LogDensityPoint dest, int pX, int pY) {
    if (bgImage != null) {
      Pixel toolPixel = dest.toolPixel;
      if (destImageWidth == bgImage.getImageWidth() && destImageHeight == bgImage.getImageHeight()) {
        toolPixel.setARGBValue(bgImage.getARGBValue(pX, pY));
        dest.bgRed = toolPixel.r;
        dest.bgGreen = toolPixel.g;
        dest.bgBlue = toolPixel.b;
      }
      else {
        double xCoord = (double) pX * (double) (bgImage.getImageWidth() - 1) / (double) (destImageWidth - 1);
        double yCoord = (double) pY * (double) (bgImage.getImageHeight() - 1) / (double) (destImageHeight - 1);

        toolPixel.setARGBValue(bgImage.getARGBValueIgnoreBounds((int) xCoord, (int) yCoord));
        int luR = toolPixel.r;
        int luG = toolPixel.g;
        int luB = toolPixel.b;

        toolPixel.setARGBValue(bgImage.getARGBValueIgnoreBounds(((int) xCoord) + 1, (int) yCoord));
        int ruR = toolPixel.r;
        int ruG = toolPixel.g;
        int ruB = toolPixel.b;
        toolPixel.setARGBValue(bgImage.getARGBValueIgnoreBounds((int) xCoord, ((int) yCoord) + 1));
        int lbR = toolPixel.r;
        int lbG = toolPixel.g;
        int lbB = toolPixel.b;
        toolPixel.setARGBValue(bgImage.getARGBValueIgnoreBounds(((int) xCoord) + 1, ((int) yCoord) + 1));
        int rbR = toolPixel.r;
        int rbG = toolPixel.g;
        int rbB = toolPixel.b;

        double x = MathLib.frac(xCoord);
        double y = MathLib.frac(yCoord);
        dest.bgRed = Tools.roundColor(GfxMathLib.blerp(luR, ruR, lbR, rbR, x, y));
        dest.bgGreen = Tools.roundColor(GfxMathLib.blerp(luG, ruG, lbG, rbG, x, y));
        dest.bgBlue = Tools.roundColor(GfxMathLib.blerp(luB, ruB, lbB, rbB, x, y));
      }
    }
    else {
      switch (flame.getBgColorType()) {
        case GRADIENT_2X2:
          double x = (double) pX / (double) (destImageWidth - 1);
          double y = (double) pY / (double) (destImageHeight - 1);
          dest.bgRed = Tools.roundColor(GfxMathLib.blerp(flame.getBgColorULRed(), flame.getBgColorURRed(), flame.getBgColorLLRed(), flame.getBgColorLRRed(), x, y));
          dest.bgGreen = Tools.roundColor(GfxMathLib.blerp(flame.getBgColorULGreen(), flame.getBgColorURGreen(), flame.getBgColorLLGreen(), flame.getBgColorLRGreen(), x, y));
          dest.bgBlue = Tools.roundColor(GfxMathLib.blerp(flame.getBgColorULBlue(), flame.getBgColorURBlue(), flame.getBgColorLLBlue(), flame.getBgColorLRBlue(), x, y));
          break;
        default:
          dest.bgRed = flame.getBgColorRed();
          dest.bgGreen = flame.getBgColorGreen();
          dest.bgBlue = flame.getBgColorBlue();
          break;
      }
    }
  }
}
