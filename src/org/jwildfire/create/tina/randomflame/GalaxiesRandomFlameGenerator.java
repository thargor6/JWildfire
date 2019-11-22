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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.mutagen.LocalGammaMutation;
import org.jwildfire.create.tina.mutagen.RandomParamMutation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class GalaxiesRandomFlameGenerator extends RandomFlameGenerator {

  public GalaxiesRandomFlameGenerator() {
  }

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCamRoll(1.49758722);
    flame.setCamPitch(0);
    flame.setCamYaw(0);
    flame.setCamBank(0);
    flame.setCamPerspective(0);
    flame.setWidth(601);
    flame.setHeight(338);
    flame.setPixelsPerUnit(92.48366013);
    flame.setCamZoom(0.72 + Math.random() * 0.42);
    flame.setCentreX(1.5357526);
    flame.setCentreY(-0.4416446);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    // create transform 1
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(25.75871591);
      xForm.setColor(0.74488914);
      xForm.setColorSymmetry(0);

      xForm.setCoeff00(1); // a
      xForm.setCoeff10(0); // b
      xForm.setCoeff20(1.09171281); // e
      xForm.setCoeff01(0); // c
      xForm.setCoeff11(1); // d
      xForm.setCoeff21(-1.22115911); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);
      // variation 1
      {
        double amount = Math.random() > 0.25 ? 1.0 - 2.0 * Math.random() : 1.0;
        xForm.addVariation(amount, getRandomVariation());
        if (Math.random() > 0.5) {
          new RandomParamMutation().setRandomFlameProperty(flame.getFirstLayer(), 1.0);
        }
      }

    }

    // create transform 2
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(286.87636036);
      xForm.setColor(0.90312262);
      xForm.setColorSymmetry(0.95);

      xForm.setCoeff00(0.96333808); // a
      xForm.setCoeff10(0.12845865); // b
      xForm.setCoeff20(0.31387449); // e
      xForm.setCoeff01(-0.12845865); // c
      xForm.setCoeff11(0.96333808); // d
      xForm.setCoeff21(0.08003269); // f

      xForm.setPostCoeff00(1);
      xForm.setPostCoeff10(0);
      xForm.setPostCoeff01(0);
      xForm.setPostCoeff11(1);
      xForm.setPostCoeff20(0);
      xForm.setPostCoeff21(0);

      // variation 1
      xForm.addVariation(1, VariationFuncList.getVariationFuncInstance("sec", true));
    }

    if (Math.random() > 0.5) {
      new LocalGammaMutation().execute(flame.getFirstLayer());
    }

    return flame;
  }

  private VariationFunc getRandomVariation() {
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
    return "Galaxies";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
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
