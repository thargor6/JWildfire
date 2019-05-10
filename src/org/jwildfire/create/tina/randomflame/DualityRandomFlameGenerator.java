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
package org.jwildfire.create.tina.randomflame;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.mutagen.RandomParamMutation;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class DualityRandomFlameGenerator extends RandomFlameGenerator {
  private final List<String> preferedVariations = new ArrayList<String>();
  private final double preferedVariationsProb1;
  private final double preferedVariationsProb2;

  public DualityRandomFlameGenerator() {
    Prefs prefs = Prefs.getPrefs();

    String vString = prefs.getTinaRandGenDualityPreferedVariation();
    if (vString != null && vString.length() > 0) {
      StringTokenizer tokenizer = new StringTokenizer(vString, ",");
      while (tokenizer.hasMoreElements()) {
        String vName = tokenizer.nextToken().trim();
        try {
          VariationFuncList.getVariationFuncInstance(vName);
          preferedVariations.add(vName);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    preferedVariationsProb1 = prefs.getTinaRandGenDualityPreferedVariationProbability1() >= 0.0 && prefs.getTinaRandGenDualityPreferedVariationProbability1() <= 1.0 ? prefs.getTinaRandGenDualityPreferedVariationProbability1() : 0.0;
    preferedVariationsProb2 = prefs.getTinaRandGenDualityPreferedVariationProbability2() >= 0.0 && prefs.getTinaRandGenDualityPreferedVariationProbability2() <= 1.0 ? prefs.getTinaRandGenDualityPreferedVariationProbability2() : 0.0;
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.01 + Math.random() * 50);
      xForm.setColor(0.74488914);
      xForm.setColorSymmetry(0);

      // variation 1
      xForm.addVariation(1.0, getRandomVariation1());
      // random affine transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, false);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), false);
      XFormTransformService.localTranslate(xForm, 2.0 - 4.0 * Math.random(), 2.0 - 4.0 * Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }
    if (Math.random() > 0.25) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }
    if (Math.random() > 0.50) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }
    if (Math.random() > 0.75) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }
    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.1 + Math.random() * 400.0);
      xForm.setColor(0.90312262);
      xForm.setColorSymmetry(0.95);

      if (Math.random() < 0.33) {
        xForm.addVariation(0.5 + Math.random() * 0.5, getRandomVariation2());
      }
      else {
        xForm.addVariation(1.0, getRandomVariation2());
      }

      // random affine transforms (uncomment to play around)
      XFormTransformService.scale(xForm, 1.25 - Math.random() * 0.5, true, true, false);
      XFormTransformService.rotate(xForm, 36.0 * Math.random(), false);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
      if (Math.random() < 0.5) {
        if (Math.random() < 0.5) {
          xForm.getModifiedWeights()[0] = 0.1 + Math.random() * 0.5;
        }
        else {
          xForm.getModifiedWeights()[0] = 3.0 + Math.random() * 7;
        }
      }

    }
    if (Math.random() > 0.25) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }
    if (Math.random() > 0.50) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }
    if (Math.random() > 0.75) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }

    // create transform 3
    if (Math.random() < 0.1) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.01 + Math.random() * 200.0);
      xForm.setColor(0.6312262);
      xForm.setColorSymmetry(0.12);

      xForm.addVariation(0.5 + Math.random() * 0.75, getRandomVariation3());
      xForm.getModifiedWeights()[2] = 0.0;

      // random affine transforms (uncomment to play around)
      XFormTransformService.scale(xForm, 1.05 - Math.random() * 0.45, true, true, false);
      XFormTransformService.rotate(xForm, 24.0 * Math.random(), false);
      XFormTransformService.localTranslate(xForm, 1.0 - 2.0 * Math.random(), 1.0 - 2.0 * Math.random(), false);
      // random affine post transforms (uncomment to play around)
      //   XFormTransformService.scale(xForm, 1.25-Math.random()*0.5, true, true, true);
      //   XFormTransformService.rotate(xForm, 360.0*Math.random(), true);
      //   XFormTransformService.localTranslate(xForm, 1.0-2.0*Math.random(), 1.0-2.0*Math.random(), true);
    }

    if (Math.random() > 0.25) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }
    if (Math.random() > 0.50) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }
    if (Math.random() > 0.75) {
      new RandomParamMutation().execute(flame.getFirstLayer());
    }

    return flame;
  }

  private VariationFunc getRandomVariation1() {
    String name = "";
    if (preferedVariations.size() > 0 && Math.random() < preferedVariationsProb1) {
      name = preferedVariations.get((int) (Math.random() * preferedVariations.size()));
    }
    else {
      name = getRandomVariationName();
    }
    return VariationFuncList.getVariationFuncInstance(name, true);
  }

  private VariationFunc getRandomVariation2() {
    String name = "";
    if (preferedVariations.size() > 0 && Math.random() < preferedVariationsProb2) {
      name = preferedVariations.get((int) (Math.random() * preferedVariations.size()));
    }
    else {
      name = getRandomVariationName();
    }
    return VariationFuncList.getVariationFuncInstance(name, true);
  }

  private VariationFunc getRandomVariation3() {
    String name = getRandomVariationName();
    return VariationFuncList.getVariationFuncInstance(name, true);
  }

  private String getRandomVariationName() {
    while (true) {
      String name = VariationFuncList.getRandomVariationname();
      if (!name.startsWith("fract") && !name.startsWith("inflate") && !name.startsWith("pre_") && !name.startsWith("post_") 
          && !name.startsWith("prepost_") && !name.equals("flatten")) {
        return name;
      }
    }
  }

  @Override
  public String getName() {
    return "Duality";
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
