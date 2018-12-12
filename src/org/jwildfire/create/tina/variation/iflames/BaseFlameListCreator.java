/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.variation.iflames;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.render.GammaCorrectionFilter.HSLRGBConverter;
import org.jwildfire.create.tina.render.ProgressUpdater;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.iflames.CreationStatistics.ActionType;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.transform.ConvolveTransformer;
import org.jwildfire.transform.ConvolveTransformer.ColorMode;
import org.jwildfire.transform.ConvolveTransformer.KernelDirection;
import org.jwildfire.transform.ConvolveTransformer.KernelType;
import org.jwildfire.transform.ErodeTransformer;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;

import java.util.*;

public class BaseFlameListCreator {
  private final FlameParamsList flameParams;
  private final MotionParams motionParams;
  private final ImageParams imageParams;
  private final Pixel toolPixel = new Pixel();
  private final CreationStatistics statistics;
  private final ProgressUpdater progressUpdater;
  private final AbstractRandomGenerator randGen;

  private final boolean preview;

  private static final int mainProgressSteps = 25;
  private int currProgress;
  private int updateMainProgressStep;
  private int updatePreprocessStep;

  private final Map<FlameParams, IFlamesIterator> instances = new HashMap<FlameParams, IFlamesIterator>();
  private int iteratorCount;

  private static final ProgressUpdater BLANK = new ProgressUpdater() {

    @Override
    public void initProgress(int pMaxSteps) {
    }

    @Override
    public void updateProgress(int pStep) {
    }

  };

  public BaseFlameListCreator(FlameParamsList pFlameParams, MotionParams pMotionParams, ImageParams pImageParams) {
    flameParams = pFlameParams;
    motionParams = pMotionParams;
    imageParams = pImageParams;
    randGen = imageParams.getRandGen();

    CreationStatistics cStatistics = (CreationStatistics) RessourceManager.getRessource(ImageParams.CACHE_KEY_PREFIX_STATISTICS + "#" + imageParams.getRenderId());
    statistics = cStatistics != null ? cStatistics : new CreationStatistics();

    ProgressUpdater cProgressUpdater = (ProgressUpdater) RessourceManager.getRessource(ImageParams.CACHE_KEY_PREFIX_PROGRESS_UPDATER + "#" + imageParams.getRenderId());
    progressUpdater = cProgressUpdater != null ? cProgressUpdater : BLANK;

    preview = motionParams.getPreview() == 1;
  }

  public List<BaseFlame> calcBaseFlameList(FlameTransformationContext pContext, WFImage pColorMap) {
    instances.clear();
    currProgress = 0;
    iteratorCount = 0;
    long totalT0 = System.currentTimeMillis();
    if (!(pColorMap instanceof SimpleImage)) {
      return Collections.emptyList();
    }

    SimpleImage colorMap = (SimpleImage) pColorMap;
    initStatistics(colorMap);
    SimpleImage structureMap = (SimpleImage) RessourceManager.getRessource(imageParams.getCachedPreprocessedImageKey());
    if (structureMap == null) {
      structureMap = colorMap.clone();
      if (structureMap.getImageWidth() > imageParams.getMaxImgWidth()) {
        long t0 = System.currentTimeMillis();
        structureMap = structureMap.clone();
        ScaleTransformer scaleT = new ScaleTransformer();
        scaleT.setScaleWidth(imageParams.getMaxImgWidth());
        scaleT.setAspect(ScaleAspect.KEEP_WIDTH);
        scaleT.transformImage(structureMap);
        long t1 = System.currentTimeMillis();
        statistics.getAction(ActionType.SCALE).setDuration(t1 - t0);
        currProgress += updatePreprocessStep;
        progressUpdater.updateProgress(currProgress);
      }
      preprocessImage(structureMap);
      RessourceManager.putRessource(imageParams.getCachedPreprocessedImageKey(), structureMap);
      System.out.println("CALCED STRUCTURE MAP");
    } else {
      System.out.println("STRUCTUREMAP FROM CACHE");
    }
    List<SimpleImage> paramMaps1 = createParamMapList(structureMap, 0);
    List<SimpleImage> paramMaps2 = createParamMapList(structureMap, 1);
    List<SimpleImage> paramMaps3 = createParamMapList(structureMap, 2);

    List<BaseFlame> res = new ArrayList<BaseFlame>();
    int nextProgressUpdate = updateMainProgressStep;
    long t0 = System.currentTimeMillis();
    if (ShapeDistribution.GRID.equals(imageParams.getShape_distribution())) {
      for (int i = 0; i < structureMap.getImageHeight(); i++) {
        for (int j = 0; j < structureMap.getImageWidth(); j++) {
          toolPixel.setARGBValue(structureMap.getARGBValue(j, i));
          double r = (double) toolPixel.r / COLORSCL;
          double g = (double) toolPixel.g / COLORSCL;
          double b = (double) toolPixel.b / COLORSCL;
          double intensity = calcIntensity(r, g, b);
          for (int k = 0; k < IFlamesFunc.MAX_FLAME_COUNT; k++) {
            FlameParams params = flameParams.get(k);
            if (params != null && params.getFlameXML() != null && params.getFlameXML().length() > 0) {
              int xoffset = params.getGridXOffset();
              int xsize = params.getGridXSize();
              if (xsize > 0 && (xoffset + j) % xsize == 0) {
                int yoffset = params.getGridYOffset();
                int ysize = params.getGridYSize();
                if (ysize > 0 && (yoffset + i) % ysize == 0) {
                  addBaseFlame(pContext, paramMaps1, paramMaps2, paramMaps3, res, i, j, intensity, k, params);
                }
              }
            }
          }
          nextProgressUpdate--;
          if (nextProgressUpdate == 0) {
            nextProgressUpdate = updateMainProgressStep;
            currProgress += updateMainProgressStep;
            progressUpdater.updateProgress(currProgress);
          }
        }
      }
    } else {
      for (int i = 0; i < structureMap.getImageHeight(); i++) {
        for (int j = 0; j < structureMap.getImageWidth(); j++) {
          toolPixel.setARGBValue(structureMap.getARGBValue(j, i));
          double r = (double) toolPixel.r / COLORSCL;
          double g = (double) toolPixel.g / COLORSCL;
          double b = (double) toolPixel.b / COLORSCL;
          double intensity = calcIntensity(r, g, b);
          if (intensity > imageParams.getStructure_threshold()) {
            if (imageParams.getStructure_density() > randGen.random()) {
              toolPixel.setARGBValue(colorMap.getARGBValueIgnoreBounds(Tools.FTOI(j * imageParams.getScaleColorMap()), Tools.FTOI(i * imageParams.getScaleColorMap())));
              int shapeIndex = calcShapeIndex(toolPixel.r, toolPixel.g, toolPixel.b);
              FlameParams params = shapeIndex >= 0 && shapeIndex < flameParams.size() ? flameParams.get(shapeIndex) : null;
              if (params != null && params.getFlameXML() != null && params.getFlameXML().length() > 0) {
                addBaseFlame(pContext, paramMaps1, paramMaps2, paramMaps3, res, i, j, intensity, shapeIndex, params);
              }
            }
          }
          nextProgressUpdate--;
          if (nextProgressUpdate == 0) {
            nextProgressUpdate = updateMainProgressStep;
            currProgress += updateMainProgressStep;
            progressUpdater.updateProgress(currProgress);
          }
        }
      }
    }
    long t1 = System.currentTimeMillis();

    statistics.addAction(ActionType.CREATE_STRUCTURE);
    statistics.getAction(ActionType.CREATE_STRUCTURE).setDuration(t1 - t0);
    statistics.setIteratorCount(iteratorCount);
    statistics.setFlameCount(res.size());

    long totalT1 = System.currentTimeMillis();
    statistics.setTotalDuration(totalT1 - totalT0);
    return res;
  }

  private static class FlameColor {
    double r, g, b;

    public FlameColor(double pR, double pG, double pB) {
      r = pR;
      b = pB;
      g = pG;
    }
  }

  private void addBaseFlame(FlameTransformationContext pContext, List<SimpleImage> paramMaps1, List<SimpleImage> paramMaps2, List<SimpleImage> paramMaps3, List<BaseFlame> res, int i, int j, double intensity, int shapeIndex, FlameParams params) {
    if (preview) {
      double pInt = intensity / (1.0 - imageParams.getStructure_threshold());
      DynamicProperties motionProperties = calculateMotionProperties(imageParams.getScaleColorMap() * j, imageParams.getScaleColorMap() * i, pInt, params);
      BaseFlame p = new BaseFlame(pInt, 0, 0, 0, 1.0, imageParams, params, IFlamesIterator.BLANK, motionProperties);
      res.add(p);
    } else {
      FlameColor flameColor = createFlameColor(imageParams, toolPixel);

      // System.out.println("(" + toolPixel.r + " " + toolPixel.g + " " + toolPixel.b + ") -> (" + flameColor.r + " " + flameColor.g + " " + flameColor.b + ")");

      double flameBrightness;
      if (params.getBrightnessChange() > MathLib.EPSILON && (randGen.random() < params.getBrightnessChange() || (params.getBrightnessChange() - MathLib.EPSILON) >= 1.0) && params.getBrightnessMin() < params.getBrightnessMax()) {
        flameBrightness = params.getBrightnessMin() + randGen.random() * (params.getBrightnessMax() - params.getBrightnessMin());
      } else {
        flameBrightness = params.getBrightnessMin();
      }

      IFlamesIterator iterator = null;
      String param1 = params.getFlameParam1() != null && params.getFlameParam1().length() > 0 ? params.getFlameParam1() : null;
      String param2 = params.getFlameParam2() != null && params.getFlameParam2().length() > 0 ? params.getFlameParam2() : null;
      String param3 = params.getFlameParam3() != null && params.getFlameParam3().length() > 0 ? params.getFlameParam3() : null;

      if (params.isInstancing()) {
        iterator = instances.get(params);
        if (iterator == null) {
          iterator = new IFlamesIterator(pContext, params, null, null);
          iteratorCount++;
          instances.put(params, iterator);
        }
      } else {
        if (param1 != null || param2 != null || param3 != null) {
          List<String> paramNames = new ArrayList<String>();
          List<Double> paramValues = new ArrayList<Double>();
          if (param1 != null) {
            SimpleImage paramMap1 = paramMaps1.get(shapeIndex);
            double value;
            if (paramMap1 != null) {
              toolPixel.setARGBValue(paramMap1.getARGBValueIgnoreBounds(j, i));
              double paramInt = calcIntensity(toolPixel.r, toolPixel.g, toolPixel.b);
              value = params.getFlameParam1Min() + (paramInt / COLORSCL) * (params.getFlameParam1Max() - params.getFlameParam1Min());
            } else {
              value = params.getFlameParam1Min() + randGen.random() * (params.getFlameParam1Max() - params.getFlameParam1Min());
            }
            paramNames.add(params.getFlameParam1());
            paramValues.add(value);
          }
          if (param2 != null) {
            SimpleImage paramMap2 = paramMaps2.get(shapeIndex);
            double value;
            if (paramMap2 != null) {
              toolPixel.setARGBValue(paramMap2.getARGBValueIgnoreBounds(j, i));
              double paramInt = calcIntensity(toolPixel.r, toolPixel.g, toolPixel.b);
              value = params.getFlameParam2Min() + (paramInt / COLORSCL) * (params.getFlameParam2Max() - params.getFlameParam2Min());
            } else {
              value = params.getFlameParam2Min() + randGen.random() * (params.getFlameParam2Max() - params.getFlameParam2Min());
            }
            paramNames.add(params.getFlameParam2());
            paramValues.add(value);
          }
          if (param3 != null) {
            SimpleImage paramMap3 = paramMaps3.get(shapeIndex);
            double value;
            if (paramMap3 != null) {
              toolPixel.setARGBValue(paramMap3.getARGBValueIgnoreBounds(j, i));
              double paramInt = calcIntensity(toolPixel.r, toolPixel.g, toolPixel.b);
              value = params.getFlameParam3Min() + (paramInt / COLORSCL) * (params.getFlameParam3Max() - params.getFlameParam3Min());
            } else {
              value = params.getFlameParam3Min() + randGen.random() * (params.getFlameParam3Max() - params.getFlameParam3Min());
            }
            paramNames.add(params.getFlameParam3());
            paramValues.add(value);
          }
          iterator = new IFlamesIterator(pContext, params, paramNames, paramValues);
          iteratorCount++;
        } else {
          iterator = new IFlamesIterator(pContext, params, null, null);
          iteratorCount++;
        }
      }
      double pInt = intensity / (1.0 - imageParams.getStructure_threshold());
      DynamicProperties motionProperties = calculateMotionProperties(imageParams.getScaleColorMap() * j, imageParams.getScaleColorMap() * i, pInt, params);

      BaseFlame p = new BaseFlame(pInt, flameColor.r, flameColor.g, flameColor.b, flameBrightness, imageParams, params, iterator, motionProperties);

      res.add(p);
    }
  }

  private FlameColor createFlameColor(ImageParams pParams, Pixel pPixel) {
    double r = (double) pPixel.r / COLORSCL;
    double g = (double) pPixel.g / COLORSCL;
    double b = (double) pPixel.b / COLORSCL;
    if (MathLib.fabs(pParams.getColorChangeRed()) > MathLib.EPSILON) {
      r += pParams.getColorChangeRed();
      if (r < 0.0) {
        r = 0.0;
      } else if (r > 1.0) {
        r = 1.0;
      }
    }
    if (MathLib.fabs(pParams.getColorChangeGreen()) > MathLib.EPSILON) {
      g += pParams.getColorChangeGreen();
      if (g < 0.0) {
        g = 0.0;
      } else if (g > 1.0) {
        g = 1.0;
      }
    }
    if (MathLib.fabs(pParams.getColorChangeBlue()) > MathLib.EPSILON) {
      b += pParams.getColorChangeBlue();
      if (b < 0.0) {
        b = 0.0;
      } else if (b > 1.0) {
        b = 1.0;
      }
    }
    if (MathLib.fabs(pParams.getColorChangeHue()) > MathLib.EPSILON || MathLib.fabs(pParams.getColorChangeSaturation()) > MathLib.EPSILON || MathLib.fabs(pParams.getColorChangeLightness()) > MathLib.EPSILON) {
      hslrgbConverter.fromRgb(r, g, b);
      hslrgbConverter.fromHsl(hslrgbConverter.getHue() + pParams.getColorChangeHue(), hslrgbConverter.getSaturation() + pParams.getColorChangeSaturation(), hslrgbConverter.getLuminosity() + pParams.getColorChangeLightness());
      r = hslrgbConverter.getRed();
      g = hslrgbConverter.getGreen();
      b = hslrgbConverter.getBlue();
    }
    return new FlameColor(r * COLORSCL, g * COLORSCL, b * COLORSCL);
  }

  private void initStatistics(SimpleImage pColorMap) {
    statistics.clear();
    SimpleImage structureMap = (SimpleImage) RessourceManager.getRessource(imageParams.getCachedPreprocessedImageKey());
    if (structureMap == null) {
      if (pColorMap.getImageWidth() > imageParams.getMaxImgWidth()) {
        statistics.addAction(ActionType.SCALE);
        statistics.setStructureMapWidth(imageParams.getMaxImgWidth());
        double aspect = (double) pColorMap.getImageHeight() / (double) pColorMap.getImageWidth();
        statistics.setStructureMapHeight(Tools.FTOI(imageParams.getMaxImgWidth() * aspect));
      } else {
        statistics.setStructureMapWidth(pColorMap.getImageWidth());
        statistics.setStructureMapHeight(pColorMap.getImageHeight());
      }

      if (imageParams.getErode() == 1) {
        statistics.addAction(ActionType.ERODE);
      }
      int convCount = 0;
      if (imageParams.getConv_north() == 1) {
        convCount++;
        statistics.addAction(ActionType.CONV_NORTH);
      }
      if (imageParams.getConv_east() == 1) {
        convCount++;
        statistics.addAction(ActionType.CONV_EAST);
      }
      if (imageParams.getConv_south() == 1) {
        convCount++;
        statistics.addAction(ActionType.CONV_SOUTH);
      }
      if (imageParams.getConv_west() == 1) {
        convCount++;
        statistics.addAction(ActionType.CONV_WEST);
      }
      if (convCount > 1) {
        statistics.addAction(ActionType.CONV_MERGE);
      }
    } else {
      statistics.setStructureMapWidth(structureMap.getImageWidth());
      statistics.setStructureMapHeight(structureMap.getImageHeight());
    }

    updateMainProgressStep = (int) (((long) statistics.getStructureMapWidth() * (long) statistics.getStructureMapHeight()) / (long) mainProgressSteps);

    int preprocessSteps = statistics.getActions().size();
    updatePreprocessStep = (int) (preprocessSteps > 0 ? (long) updateMainProgressStep * (long) mainProgressSteps / (long) preprocessSteps : 0);

    progressUpdater.initProgress(updatePreprocessStep * preprocessSteps + updateMainProgressStep * mainProgressSteps);

  }

  private DynamicProperties calculateMotionProperties(double pPosX, double pPosY, double pInt, FlameParams pParams) {
    float posZ = (float) (0.1 * (0.5 - pInt));

    float x0 = (float) (pPosX * imageParams.getScaleX() / (float) (imageParams.getImgWidth() - 1) + imageParams.getOffsetX());
    float y0 = (float) (pPosY * imageParams.getScaleY() / (float) (imageParams.getImgHeight() - 1) + imageParams.getOffsetY());
    float z0 = (float) (posZ * imageParams.getScaleZ() + imageParams.getOffsetZ());

    float alpha0 = (float) calculateStartRotation(pParams.getRotateAlpha(), pParams.getRotateAlphaVar());
    float beta0 = (float) calculateStartRotation(pParams.getRotateBeta(), pParams.getRotateBetaVar());
    float gamma0 = (float) calculateStartRotation(pParams.getRotateGamma(), pParams.getRotateGammaVar());

    float alphaSpeed = (float) calculateStartRotation(pParams.getRotateAlphaSpeed(), pParams.getRotateAlphaSpeedVar());
    float betaSpeed = (float) calculateStartRotation(pParams.getRotateBetaSpeed(), pParams.getRotateBetaSpeedVar());
    float gammaSpeed = (float) calculateStartRotation(pParams.getRotateGammaSpeed(), pParams.getRotateGammaSpeedVar());

    float vx0 = (float) calculateStartTranslation(pParams.getSpeedX(), pParams.getSpeedXVar());
    float vy0 = (float) calculateStartTranslation(pParams.getSpeedY(), pParams.getSpeedYVar());
    float vz0 = (float) calculateStartTranslation(pParams.getSpeedZ(), pParams.getSpeedZVar());

    float radialAcceleration = (float) calculateStartTranslation(pParams.getRadialAcceleration(), pParams.getRadialAccelerationVar());
    float tangentialAcceleration = (float) calculateStartTranslation(pParams.getTangentialAcceleration(), pParams.getTangentialAccelerationVar());

    float baseLife = (float) motionParams.getLife();
    float lifeVar = (float) (motionParams.getLifeVar() >= 0.0 ? motionParams.getLifeVar() / 100.0 : 0.0);

    float life = (float) (lifeVar > MathLib.EPSILON ? baseLife * (1.0f + lifeVar * (0.5f - randGen.random())) : baseLife);

    return new DynamicProperties(x0, y0, z0, vx0, vy0, vz0, alpha0, beta0, gamma0, alphaSpeed, betaSpeed, gammaSpeed, radialAcceleration, tangentialAcceleration, life);
  }

  private double calculateStartTranslation(double pBaseSpeed, double pVariation) {
    double var = pVariation >= 0.0 ? pVariation / 100.0 : 0.0;
    return var > MathLib.EPSILON ? pBaseSpeed * (1.0 + var * (0.5 - randGen.random())) : pBaseSpeed;
  }

  private double calculateStartRotation(double pBaseAngle, double pVariation) {
    double baseAlphaRad = MathLib.M_2PI * pBaseAngle / 180.0;
    double rotateVar = pVariation >= 0.0 ? pVariation / 100.0 : 0.0;
    return rotateVar > MathLib.EPSILON ? baseAlphaRad * (1.0 + rotateVar * (0.5 - randGen.random())) : baseAlphaRad;
  }

  private void preprocessImage(SimpleImage tImage) {
    if (imageParams.getErode() == 1) {
      long t0 = System.currentTimeMillis();

      ErodeTransformer eT = new ErodeTransformer();
      eT.initDefaultParams(tImage);
      eT.setMode(ErodeTransformer.Mode.DILATE);
      eT.setSize(imageParams.getErodeSize());
      eT.setShape(ErodeTransformer.Shape.DISK);
      eT.transformImage(tImage);

      long t1 = System.currentTimeMillis();
      statistics.getAction(ActionType.ERODE).setDuration(t1 - t0);
      currProgress += updatePreprocessStep;
      progressUpdater.updateProgress(currProgress);
    }

    List<SimpleImage> convImages = new ArrayList<SimpleImage>();
    if (imageParams.getConv_north() == 1) {
      long t0 = System.currentTimeMillis();

      SimpleImage north = tImage.clone();
      ConvolveTransformer transformer = new ConvolveTransformer();
      transformer.initDefaultParams(north);
      transformer.setKernelType(KernelType.SOBEL_3X3);
      transformer.setKernelDirection(KernelDirection.NORTH);
      transformer.setColorMode(ColorMode.GREY);
      transformer.transformImage(north);
      convImages.add(north);

      long t1 = System.currentTimeMillis();
      statistics.getAction(ActionType.CONV_NORTH).setDuration(t1 - t0);
      currProgress += updatePreprocessStep;
      progressUpdater.updateProgress(currProgress);
    }
    if (imageParams.getConv_east() == 1) {
      long t0 = System.currentTimeMillis();

      SimpleImage east = tImage.clone();
      ConvolveTransformer transformer = new ConvolveTransformer();
      transformer.initDefaultParams(east);
      transformer.setKernelType(KernelType.SOBEL_3X3);
      transformer.setKernelDirection(KernelDirection.EAST);
      transformer.setColorMode(ColorMode.GREY);
      transformer.transformImage(east);
      convImages.add(east);

      long t1 = System.currentTimeMillis();
      statistics.getAction(ActionType.CONV_EAST).setDuration(t1 - t0);
      currProgress += updatePreprocessStep;
      progressUpdater.updateProgress(currProgress);
    }
    if (imageParams.getConv_south() == 1) {
      long t0 = System.currentTimeMillis();

      SimpleImage south = tImage.clone();
      ConvolveTransformer transformer = new ConvolveTransformer();
      transformer.initDefaultParams(south);
      transformer.setKernelType(KernelType.SOBEL_3X3);
      transformer.setKernelDirection(KernelDirection.SOUTH);
      transformer.setColorMode(ColorMode.GREY);
      transformer.transformImage(south);
      convImages.add(south);

      long t1 = System.currentTimeMillis();
      statistics.getAction(ActionType.CONV_SOUTH).setDuration(t1 - t0);
      currProgress += updatePreprocessStep;
      progressUpdater.updateProgress(currProgress);
    }
    if (imageParams.getConv_west() == 1) {
      long t0 = System.currentTimeMillis();

      SimpleImage west = tImage.clone();
      ConvolveTransformer transformer = new ConvolveTransformer();
      transformer.initDefaultParams(west);
      transformer.setKernelType(KernelType.SOBEL_3X3);
      transformer.setKernelDirection(KernelDirection.WEST);
      transformer.setColorMode(ColorMode.GREY);
      transformer.transformImage(west);
      convImages.add(west);

      long t1 = System.currentTimeMillis();
      statistics.getAction(ActionType.CONV_WEST).setDuration(t1 - t0);
      currProgress += updatePreprocessStep;
      progressUpdater.updateProgress(currProgress);
    }

    if (convImages.size() == 1) {
      SimpleImage convImage = convImages.get(0);
      tImage.setBufferedImage(convImage.getBufferedImg(), convImage.getImageWidth(), convImage.getImageHeight());
    } else if (convImages.size() > 1) {
      long t0 = System.currentTimeMillis();

      for (int i = 0; i < tImage.getImageHeight(); i++) {
        for (int j = 0; j < tImage.getImageWidth(); j++) {
          int r = 0, g = 0, b = 0;
          for (SimpleImage convImage : convImages) {
            r += convImage.getRValue(j, i);
            g += convImage.getGValue(j, i);
            b += convImage.getBValue(j, i);
          }
          r = Tools.roundColor((double) r / (double) convImages.size());
          g = Tools.roundColor((double) g / (double) convImages.size());
          b = Tools.roundColor((double) b / (double) convImages.size());
          tImage.setRGB(j, i, r, g, b);
        }
      }

      long t1 = System.currentTimeMillis();
      statistics.getAction(ActionType.CONV_MERGE).setDuration(t1 - t0);
      currProgress += updatePreprocessStep;
      progressUpdater.updateProgress(currProgress);
    }
  }

  private static final double COLORSCL = 255.0;
  private final HSLRGBConverter hslrgbConverter = new HSLRGBConverter();

  private int calcShapeIndex(int pR, int pG, int pB) {
    switch (imageParams.getShape_distribution()) {
      case RANDOM: {
        List<Integer> indexLst = new ArrayList<Integer>();
        for (int i = 0; i < flameParams.size(); i++) {
          if (flameParams.get(i).hasFlame()) {
            indexLst.add(i);
          }
        }
        // TODO weight
        return indexLst.size() > 0 ? (int) (randGen.random() * indexLst.size()) : 0;
      }
      case HUE:
        hslrgbConverter.fromRgb(pR / COLORSCL, pG / COLORSCL, pB / COLORSCL);
        return flameParams.getFlameIndex(hslrgbConverter.getHue());
      case LUMINOSITY:
        hslrgbConverter.fromRgb(pR / COLORSCL, pG / COLORSCL, pB / COLORSCL);
        return flameParams.getFlameIndex(hslrgbConverter.getLuminosity());
      case BRIGHTNESS:
        hslrgbConverter.fromRgb(pR / COLORSCL, pG / COLORSCL, pB / COLORSCL);
        return flameParams.getFlameIndex(calcIntensity(pR / COLORSCL, pG / COLORSCL, pB / COLORSCL));
      default: // nothing to do
        break;
    }
    return -1;
  }

  private double calcIntensity(double red, double green, double blue) {
    return (0.299 * red + 0.588 * green + 0.113 * blue);
  }

  private List<SimpleImage> createParamMapList(SimpleImage structureMap, int pIndex) {
    List<SimpleImage> res = new ArrayList<SimpleImage>();
    for (FlameParams flameParam : flameParams) {
      SimpleImage paramMap = null;
      try {
        String flameParamname;
        String flameParamMapFilename;
        switch (pIndex) {
          case 0:
            flameParamname = flameParam.getFlameParam1();
            flameParamMapFilename = flameParam.getFlameParamMap1Filename();
            break;
          case 1:
            flameParamname = flameParam.getFlameParam2();
            flameParamMapFilename = flameParam.getFlameParamMap2Filename();
            break;
          case 2:
            flameParamname = flameParam.getFlameParam3();
            flameParamMapFilename = flameParam.getFlameParamMap3Filename();
            break;
          default:
            throw new IllegalArgumentException();
        }
        if (flameParamname != null && flameParamname.length() > 0 && flameParamMapFilename != null && flameParamMapFilename.length() > 0) {
          SimpleImage map = (SimpleImage) RessourceManager.getImage(flameParamMapFilename);
          if ((map.getImageWidth() != structureMap.getImageWidth()) || (map.getImageHeight() != structureMap.getImageHeight())) {
            map = map.clone();
            ScaleTransformer scaleT = new ScaleTransformer();
            scaleT.setScaleWidth(structureMap.getImageWidth());
            scaleT.setScaleHeight(structureMap.getImageHeight());
            scaleT.setAspect(ScaleAspect.IGNORE);
            scaleT.transformImage(map);
            paramMap = map;
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      res.add(paramMap);
    }
    return res;
  }

}
