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
/*
 Based on the script Julians.lua of Fractal Architect:
 --[[
 Copyright 2015 Steven Brodhead, Sr., Centcom Inc.
 
 This script is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This script is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 See <http://www.gnu.org/licenses/>.
--]]

require("Utils")

local requiredVariations = { "splits", "julian", "linear", "csch"  }

-- if the current variation set does not support these, create a linear random fractal instead --
if not variationSet.hasVariations(requiredVariations) then
    variationSet.switchToVariationSetWithName("Flam3 Legacy")
end


flame = makeBlankFractal(3)

-- First Xform -----------------
local xform = flame.xforms[1]

xform.weight = randInRange(0.00001, 2.)
xform.color  = math.random()

if math.random() < 0.5 then
    -- make sure there is a Pre Variation group
    if #xform.preVarGroups == 0 then
        table.insert(xform.preVarGroups, {})
    end    
    table.insert(xform.preVarGroups[1], { name="splits", weight=-0.5 + 0.2 * math.random(),
                                          splits_x = randInRange(-2, 2), splits_y = randInRange(-2, 2) })
end

table.insert(xform.variations, { name="julian", weight = randInRange(-2., 2.),
                                 julian_power = math.random(1, 2), julian_dist = randInRange(-2., 2.) })

randomPreMatrix(xform)

-- 2nd Xform -----------------
xform = flame.xforms[2]

table.insert(xform.variations, { name="julian", weight = randInRange(-2., 2.),
                                 julian_power = math.random(1, 2), julian_dist = randInRange(-2., 2.) })

if math.random() < 0.8 then
    -- make sure there is a Post Variation group
    if #xform.postVarGroups == 0 then
        table.insert(xform.postVarGroups, {})
    end    
    table.insert(xform.postVarGroups[1], { name="linear", weight=randInRange(-1., 1.) })
    table.insert(xform.postVarGroups[1], { name="csch",   weight=randInRange(-1., 1.) })
end
                            
xform.weight = randInRange(0.00001, 2.)
xform.color = math.random()

randomPreMatrix(xform)

-- 3rd Xform -----------------
xform = flame.xforms[3]

table.insert(xform.variations, { name="julian", weight = randInRange(-2., 2.),
                                 julian_power = math.random(1, 2), julian_dist = randInRange(-2., 2.) })

                            
xform.weight = randInRange(0.00001, 2.)
xform.color = math.random()

randomPreMatrix(xform)
randomPostMatrix(xform)
 */

package org.jwildfire.create.tina.randomflame;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class JuliansRandomFlameGenerator extends RandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = new Flame();
    flame.setCentreX(0.0);
    flame.setCentreY(0.0);
    flame.setPixelsPerUnit(200);
    flame.setCamZoom(1.0);
    Layer layer = flame.getFirstLayer();
    layer.getFinalXForms().clear();
    layer.getXForms().clear();

    String primary = Math.random() < 0.666 ? "julian" : "juliascope";

    // 1st xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.00001 + Math.random() * 20.0);
      xForm.setColor(Math.random());
      if (Math.random() < 0.5) {
        xForm.addVariation(-1.0 + Math.random() * 2.0, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true));
      }
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(primary, true);
      varFunc.setParameter("power", randInRange(1, 7));
      varFunc.setParameter("dist", -2.0 + 4.0 * Math.random());
      xForm.addVariation(-2.0 + Math.random() * 4.0, varFunc);
      randomAffine(xForm);
    }
    // 2nd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.00001 + Math.random() * 6.0);
      xForm.setColor(Math.random());
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(primary, true);
      varFunc.setParameter("power", randInRange(1, 7));
      varFunc.setParameter("dist", -2.0 + 4.0 * Math.random());
      xForm.addVariation(-2.0 + Math.random() * 4.0, varFunc);
      if (Math.random() < 0.8) {
        xForm.addVariation(-1.0 + Math.random() * 2.0, VariationFuncList.getVariationFuncInstance("linear", true));
        xForm.addVariation(-1.0 + Math.random() * 2.0, VariationFuncList.getVariationFuncInstance(VariationFuncList.getRandomVariationname(), true));
      }
      xForm.getModifiedWeights()[1] = Math.random() < 0.5 ? 0.0 : Math.random();
      XFormTransformService.scale(xForm, 0.5 + Math.random() * 0.5, Math.random() < 0.5, Math.random() < 0.5);
      randomAffine(xForm);
    }

    // 3rd xForm
    {
      XForm xForm = new XForm();
      layer.getXForms().add(xForm);
      xForm.setWeight(0.00001 + Math.random() * 2.0);
      xForm.setColor(Math.random());
      VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(primary, true);
      varFunc.setParameter("power", randInRange(1, 5));
      varFunc.setParameter("dist", -2.0 + 4.0 * Math.random());
      xForm.addVariation(-2.0 + Math.random() * 4.0, varFunc);
      randomAffine(xForm);
      randomPostAffine(xForm);
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Julians";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return false;
  }

  @Override
  protected Flame postProcessFlameBeforeRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }

  private void randomAffine(XForm pXForm) {
    double affineSelect = Math.random();
    if (affineSelect > 0.2 && affineSelect <= 0.4) {
      XFormTransformService.rotate(pXForm, randInRange(0., 360.0), false);
    }
    if (affineSelect > 0.4 && affineSelect <= 0.6) {
      XFormTransformService.localTranslate(pXForm, randInRange(0., 1.5), randInRange(0., 1.5), false);
    }
    if (affineSelect > 0.6 && affineSelect <= 0.8) {
      XFormTransformService.scale(pXForm, randInRange(0.25, 1.5), true, true, false);
    }
    else if (affineSelect > 0.8) {
      XFormTransformService.rotate(pXForm, randInRange(0., 360.0), false);
      XFormTransformService.localTranslate(pXForm, randInRange(0., 1.5), randInRange(0., 1.5), false);
      XFormTransformService.scale(pXForm, randInRange(0., 1.2), true, true, false);
    }
  }

  private void randomPostAffine(XForm pXForm) {
    double affineSelect = Math.random();
    if (affineSelect > 0.6 && affineSelect <= 0.7) {
      XFormTransformService.rotate(pXForm, randInRange(0.0, 360.0), true);
    }
    if (affineSelect > 0.7 && affineSelect <= 0.8) {
      XFormTransformService.localTranslate(pXForm, randInRange(0., 1.5), randInRange(0.0, 1.5), true);
    }
    if (affineSelect > 0.8 && affineSelect <= 0.9) {
      XFormTransformService.scale(pXForm, randInRange(0.25, 1.5), true, true);
    }
    else if (affineSelect > 0.9) {
      XFormTransformService.rotate(pXForm, randInRange(0.0, 360.0), true);
      XFormTransformService.localTranslate(pXForm, randInRange(0.0, 1.5), randInRange(0.0, 1.5), true);
      XFormTransformService.scale(pXForm, randInRange(0., 1.2), true, true, true);
    }
  }

  private double randInRange(double pMin, double pMax) {
    return pMin + Math.random() * (pMax - pMin);
  }

  private double randInRange(int pMin, int pMax) {
    return Tools.FTOI(pMin + Math.random() * (pMax - pMin));
  }

  @Override
  protected Flame postProcessFlameAfterRendering(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }
}
