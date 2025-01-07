/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2023 Andreas Maschke

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
package org.jwildfire.create.tina.mutagen;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.create.tina.variation.VariationFuncType;

public
class RandomBackgroundMutation extends AbstractMutation {

  // for Script-compatibility
  @Deprecated
  public void execute(Flame pFlame) {
    execute(pFlame.getFirstLayer(), 1.0);
  }

  @Override
  public void execute(Layer pLayer, double mutationStrength) {
    pLayer.getBGXForms().clear();
    XForm xForm = new XForm();
    pLayer.getBGXForms().add(xForm);
    applyToXForm(xForm, mutationStrength);
  }

  public void applyToXForm(XForm xForm, double mutationStrength) {
    xForm.setWeight((0.25 + Math.random() * 0.5)* mutationStrength);
    String fName = VariationFuncList.getRandomVariationname(VariationFuncType.VARTYPE_SUPPORTS_BACKGROUND);
    VariationFunc varFunc = VariationFuncList.getVariationFuncInstance(fName, true);
    xForm.addVariation((0.5 + Math.random()), varFunc);
    new AffineMutation().applyToXForm(xForm, mutationStrength);
    varFunc.mutate(mutationStrength);
  }

}
