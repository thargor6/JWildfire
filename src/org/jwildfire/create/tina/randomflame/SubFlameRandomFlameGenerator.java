/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2019 Andreas Maschke

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
package org.jwildfire.create.tina.randomflame;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.randomgradient.RandomGradientGeneratorList;
import org.jwildfire.create.tina.randomsymmetry.RandomSymmetryGeneratorList;
import org.jwildfire.create.tina.randomweightingfield.RandomWeightingFieldGeneratorList;
import org.jwildfire.create.tina.swing.RandomBatchQuality;
import org.jwildfire.create.tina.variation.SubFlameWFFunc;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class SubFlameRandomFlameGenerator extends RandomFlameGenerator {

  public Flame embedFlame(Flame pSubFlame) throws Exception {
    Flame flame = new Flame();
    flame.assign(pSubFlame);
    Layer layer = flame.getFirstLayer();

    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    flame.getFirstLayer().setPalette(pSubFlame.getFirstLayer().getPalette().makeCopy());
    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.5);
      {
        {
          SubFlameWFFunc var = new SubFlameWFFunc();
          String flameXML = new FlameWriter().getFlameXML(pSubFlame);
          var.setRessource("flame", flameXML.getBytes());
          xForm.addVariation(1, var);
        }
      }
      xForm.setColor(0);
      xForm.setColorSymmetry(-0.22);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.25 + Math.random() * 0.5);
      xForm.setCoeff00(0.17254603006834707);
      xForm.setCoeff01(0.6439505508593787);
      xForm.setCoeff10(-0.6439505508593787);
      xForm.setCoeff11(0.17254603006834707);
      xForm.setCoeff20(1.5 + Math.random() * 2.5);
      xForm.setCoeff21(-0.25 - Math.random() * 0.35);
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("linear3D", true));
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(-0.62);
    }
    // 3rd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.25 + Math.random() * 0.5);
      xForm.setCoeff00(0.17254603006834707);
      xForm.setCoeff01(0.6439505508593787);
      xForm.setCoeff10(-0.6439505508593787);
      xForm.setCoeff11(0.17254603006834707);
      xForm.setCoeff20(-3.0);
      xForm.setCoeff21(0.3);
      {
        VariationFunc varFunc;
        varFunc = VariationFuncList.getVariationFuncInstance("curl3D", true);
        varFunc.setParameter("cx", -0.2 + 0.4 * Math.random());
        varFunc.setParameter("cy", 0);
        varFunc.setParameter("cz", 0);
        xForm.addVariation(1, varFunc);
      }
      xForm.setColor(Math.random());
      xForm.setColorSymmetry(0);
    }
    return flame;
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Prefs prefs = Prefs.getPrefs();

    AllRandomFlameGenerator randGen = new AllRandomFlameGenerator();
    randGen.setUseSimpleGenerators(true);
    final int IMG_WIDTH = 80;
    final int IMG_HEIGHT = 60;
    int palettePoints = 3 + Tools.randomInt(68);
    boolean fadePaletteColors = Math.random() > 0.33;
    boolean uniformWidth = Math.random() > 0.75;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, RandomSymmetryGeneratorList.NONE, RandomGradientGeneratorList.DEFAULT, RandomWeightingFieldGeneratorList.NONE, palettePoints, fadePaletteColors, uniformWidth, RandomBatchQuality.LOW);
    Flame subFlame = sampler.createSample().getFlame();

    Flame flame;
    try {
      flame = embedFlame(subFlame);
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    flame.setCentreX(2);
    flame.setCentreY(1);
    flame.setCamPitch(0);
    flame.setCamRoll(-2);
    flame.setCamYaw(0);
    flame.setCamBank(0);
    flame.setPixelsPerUnit(200);
    return flame;
  }

  @Override
  public String getName() {
    return "SubFlame";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return true;
  }

  @Override
  protected Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }
}
