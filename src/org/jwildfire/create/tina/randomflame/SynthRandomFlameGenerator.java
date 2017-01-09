/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

import static org.jwildfire.base.mathlib.MathLib.M_PI;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class SynthRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    // Bases loosely on the SynthRandomBatch Script by slobo777, http://slobo777.deviantart.com/art/Synth-V2-128594088 */ 
    Flame flame = new Flame();
    Layer layer = flame.getFirstLayer();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    layer.getFinalXForms().clear();
    layer.getXForms().clear();
    // init
    // These vars affect the style of the centre effect
    double centre_synth = 0.7;
    double centre_mode = Tools.randomInt(20); //5;
    double centre_noise = .0;
    double centre_power = -1.0;
    double centre_smooth = 1;
    double centre_color = 0.4 + 0.2 * Math.random();
    double centre_symmetry = 0.6 + 0.4 * Math.random();
    XForm xForm1;
    // 1st XForm
    {
      XForm xForm = xForm1 = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(1.0);
      xForm.setColor(0.0);
      xForm.setColorSymmetry(-1.0);

      VariationFunc synth = VariationFuncList.getVariationFuncInstance("synth");
      xForm.addVariation(1.0, synth);
      synth.setParameter("mode", Tools.randomInt(20)); //3;
      synth.setParameter("power", -0.2);

      int numWaves = (int) (Math.random() * 3.5 + 2);
      // Starting circle . . .
      synth.setParameter("a", 0.8 + (Math.random() * 0.4));
      // Wave #1
      synth.setParameter("b", Math.random() * 2);
      synth.setParameter("b_type", Tools.randomInt(7));
      synth.setParameter("b_frq", Tools.randomInt(7) + 1);
      synth.setParameter("b_phs", Math.random() * M_PI);
      synth.setParameter("b_layer", Tools.randomInt(4));
      // Skew effect?
      if (Math.random() < 0.2) {
        synth.setParameter("b_skew", Math.random() * 2 - 1);
      }
      // Exceptionally high frequency?
      if (Math.random() < 0.1) {
        synth.setParameter("b_frq", Tools.randomInt(20) + 7);
      }
      // Usually higher frequencies affect the amplitude
      if (Math.random() < 0.8) {
        synth.setParameter("b", (Double) synth.getParameter("b") / (1 + 0.3 * (Double) synth.getParameter("b_frq")));
      }

      // Wave #2
      synth.setParameter("c", Math.random());
      synth.setParameter("c_type", Tools.randomInt(7));
      synth.setParameter("c_frq", Tools.randomInt(7) + 1);
      synth.setParameter("c_phs", Math.random() * M_PI);
      synth.setParameter("c_layer", Tools.randomInt(4));
      // Skew effect?
      if (Math.random() < 0.2) {
        synth.setParameter("c_skew", Math.random() * 2 - 1);
      }
      // Exceptionally high frequency?
      if (Math.random() < 0.1) {
        synth.setParameter("c_frq", Tools.randomInt(20) + 7);
      }
      // Usually higher frequencies affect the amplitude
      if (Math.random() < 0.8) {
        synth.setParameter("c", (Double) synth.getParameter("c") / (1 + 0.3 * (Double) synth.getParameter("c_frq")));
      }

      // Wave #3
      if (numWaves >= 3) {
        synth.setParameter("d", Math.random());
        synth.setParameter("d_type", Tools.randomInt(7));
        synth.setParameter("d_frq", Tools.randomInt(7) + 1);
        synth.setParameter("d_phs", Math.random() * M_PI);
        synth.setParameter("d_layer", Tools.randomInt(4));
        // Skew effect?
        if (Math.random() < 0.2) {
          synth.setParameter("d_skew", Math.random() * 2 - 1);
        }
        // Exceptionally high frequency?
        if (Math.random() < 0.1) {
          synth.setParameter("d_frq", Tools.randomInt(20) + 7);
        }
        // Usually higher frequencies affect the amplitude
        if (Math.random() < 0.8) {
          synth.setParameter("d", (Double) synth.getParameter("d") / (1 + 0.3 * (Double) synth.getParameter("d_frq")));
        }
      }

      // Wave #4
      if (numWaves >= 4) {
        synth.setParameter("e", Math.random());
        synth.setParameter("e_type", Tools.randomInt(7));
        synth.setParameter("e_frq", Tools.randomInt(7) + 1);
        synth.setParameter("e_phs", Math.random() * M_PI);
        synth.setParameter("e_layer", Tools.randomInt(4));
        // Skew effect?
        if (Math.random() < 0.2) {
          synth.setParameter("e_skew", Math.random() * 2 - 1);
        }
        // Exceptionally high frequency?
        if (Math.random() < 0.1) {
          synth.setParameter("e_frq", Tools.randomInt(20) + 7);
        }
        // Usually higher frequencies affect the amplitude
        if (Math.random() < 0.8) {
          synth.setParameter("e", (Double) synth.getParameter("e") / (1 + 0.3 * (Double) synth.getParameter("e_frq")));
        }
      }

      // Wave #5
      if (numWaves >= 5) {
        synth.setParameter("f", Math.random());
        synth.setParameter("f_type", Tools.randomInt(7));
        synth.setParameter("f_frq", Tools.randomInt(7) + 1);
        synth.setParameter("f_phs", Math.random() * M_PI);
        synth.setParameter("f_layer", Tools.randomInt(4));
        // Skew effect?
        if (Math.random() < 0.2) {
          synth.setParameter("f_skew", Math.random() * 2 - 1);
        }
        // Exceptionally high frequency?
        if (Math.random() < 0.1) {
          synth.setParameter("f_frq", Tools.randomInt(20) + 7);
        }
        // Usually higher frequencies affect the amplitude
        if (Math.random() < 0.8) {
          synth.setParameter("f", (Double) synth.getParameter("f") / (1 + 0.3 * (Double) synth.getParameter("f_frq")));
        }
      }
    }

    // Second "inner" transform is smaller with a little noise
    // added to remove annoying lines at the centre
    {
      XForm xForm = xForm1.makeCopy();
      layer.getXForms().add(xForm);
      xForm.setWeight(1.0);
      xForm.setColor(centre_color);
      xForm.setColorSymmetry(centre_symmetry);

      VariationFunc synth = xForm.getVariation(0).getFunc();
      xForm.getVariation(0).setAmount(centre_synth);
      synth.setParameter("power", centre_power);
      synth.setParameter("mode", centre_mode);
      synth.setParameter("smooth", centre_smooth);

      VariationFunc noise = VariationFuncList.getVariationFuncInstance("noise");
      xForm.addVariation(centre_noise, noise);

    }

    if (Math.random() < 0.55) {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(5 * Math.random() + 0.125);
      xForm.setColor(centre_color + 0.2 * Math.random());
      xForm.setColorSymmetry(centre_symmetry - 0.4 * Math.random());

      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname());
      xForm.addVariation(0.25 + Math.random() * 1.5, varFunc);

      xForm.getModifiedWeights()[1] = 0.0;
    }

    return flame;
  }

  @Override
  public String getName() {
    return "Synth";
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
