/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2022 Andreas Maschke

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
package org.jwildfire.cli;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.farender.FAFlameWriter;
import org.jwildfire.create.tina.farender.FARenderResult;
import org.jwildfire.create.tina.farender.FARenderTools;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.randomgradient.AllRandomGradientGenerator;
import org.jwildfire.create.tina.randomgradient.RandomGradientGenerator;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGenerator;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGenerator;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.create.tina.render.denoiser.AIPostDenoiserFactory;
import org.jwildfire.create.tina.render.denoiser.AIPostDenoiserType;
import org.jwildfire.create.tina.render.filter.FilteringType;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.io.ImageWriter;

import java.io.File;
import java.util.List;

public class CliUtils {

  public static void renderFlame(Flame flame, RenderOptions renderOptions, String flameFilename) throws Exception {
    Flame renderFlame = AnimationService.evalMotionCurves(flame.makeCopy(), flame.getFrame());
    double wScl = (double) renderOptions.getRenderWidth() / (double) renderFlame.getWidth();
    double hScl = (double) renderOptions.getRenderHeight() / (double) renderFlame.getHeight();
    renderFlame.setPixelsPerUnitScale((wScl + hScl) * 0.5);
    renderFlame.setSampleDensity(renderOptions.getRenderQuality());
    renderFlame.setSpatialOversampling(renderOptions.getSpatialOversampling());
    renderFlame.setSpatialFilteringType(FilteringType.valueOf(renderOptions.getSpatialFilteringType()));
    renderFlame.setSpatialFilterRadius(renderOptions.getSpatialFilterRadius());
    renderFlame.setAiPostDenoiser(AIPostDenoiserType.valueOf(renderOptions.getAiPostDenoiserType()));
    if(renderOptions.isForceAiPostDenoiser()) {
      renderFlame.setPostDenoiserOnlyForCpuRender(false);
    }

    if(renderOptions.isUseGPU()) {
      boolean zForPass = false;
      String tmpFlam3Filename = Tools.trimFileExt(flameFilename) + ".flam3";
      String gpuRenderFlameFilename = zForPass ? Tools.makeZBufferFilename(tmpFlam3Filename, flame.getZBufferFilename()) : tmpFlam3Filename;
      try {
        List<Flame> preparedFlames = FARenderTools.prepareFlame(renderFlame, zForPass);
        new FAFlameWriter().writeFlame(preparedFlames, gpuRenderFlameFilename);
        FARenderResult gpuRenderRes = FARenderTools.invokeFARender(gpuRenderFlameFilename,
                renderOptions.getRenderWidth(), renderOptions.getRenderHeight(), Tools.FTOI(renderOptions.getRenderQuality()), preparedFlames.size() > 1);
        if (gpuRenderRes.getReturnCode() != 0) {
          throw new Exception(gpuRenderRes.getMessage());
        } else {
          if (!AIPostDenoiserType.NONE.equals(renderFlame.getAiPostDenoiser()) && !renderFlame.isPostDenoiserOnlyForCpuRender()) {
            AIPostDenoiserFactory.denoiseImage(new File(gpuRenderRes.getOutputFilename()).getAbsolutePath(), renderFlame.getAiPostDenoiser(), renderFlame.getPostOptiXDenoiserBlend());
          }
        }
      } finally {
        if (!new File(gpuRenderFlameFilename).delete()) {
          new File(gpuRenderFlameFilename).deleteOnExit();
        }
      }
    } else {
      RenderInfo renderInfo =
          new RenderInfo(
              renderOptions.getRenderWidth(),
              renderOptions.getRenderHeight(),
              RenderMode.PRODUCTION);
      renderInfo.setRenderHDR(false);
      renderInfo.setRenderZBuffer(false);

      RenderedFlame renderResult =
          new org.jwildfire.create.tina.render.FlameRenderer(
                  renderFlame, Prefs.getPrefs(), false, false)
              .renderFlame(renderInfo);
      new ImageWriter().saveImage(renderResult.getImage(), Tools.trimFileExt(flameFilename)+".png");
    }
  }

  public static Flame createRandomFlame(String randGenFlameName, String randGenGradientName, String randGenSymmetryName, String randGenWFieldName) {
    VariationFuncList.considerVariationCosts = false;
    final int IMG_WIDTH = 40;
    final int IMG_HEIGHT = 30;
    final org.jwildfire.create.tina.randomflame.RandomFlameGenerator randGenFlame = randGenFlameName!=null && randGenFlameName.length()>0 ? RandomFlameGeneratorList.getRandomFlameGeneratorInstance(randGenFlameName, true)  : new AllRandomFlameGenerator();
    final RandomGradientGenerator randGenGradient = randGenGradientName!=null && randGenGradientName.length()>0 ? RandomGradientGeneratorList.getRandomGradientGeneratorInstance(randGenGradientName, true) :  new AllRandomGradientGenerator();
    final RandomSymmetryGenerator randGenSymmetry = randGenSymmetryName!=null && randGenSymmetryName.length()>0 ? RandomSymmetryGeneratorList.getRandomSymmetryGeneratorInstance(randGenSymmetryName, true) : RandomSymmetryGeneratorList.SPARSE;
    final RandomWeightingFieldGenerator randGenWField = randGenWFieldName!=null && randGenWFieldName.length()>0 ? RandomWeightingFieldGeneratorList.getRandomWeightingFieldGeneratorInstance(randGenWFieldName, true) : RandomWeightingFieldGeneratorList.SPARSE;
    final int palettePoints = 3 + Tools.randomInt(21);
    final boolean fadePaletteColors = Math.random() > 0.09;
    final boolean uniformSize = Math.random() > 0.75;
    RandomBatchQuality quality = RandomBatchQuality.NORMAL;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, Prefs.getPrefs(), randGenFlame, randGenSymmetry,randGenGradient, randGenWField, palettePoints, fadePaletteColors, uniformSize, quality);
    return sampler.createSample().getFlame();
  }

}
