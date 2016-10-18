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
package org.jwildfire.create.tina.mutagen;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.randomflame.ExperimentalSimpleRandomFlameGenerator;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class AddVariationMutation implements Mutation {

  @Override
  public void execute(Layer pLayer) {
    XForm xForm;
    if (Math.random() < 0.75 || pLayer.getFinalXForms().size() == 0) {
      int idx = Tools.randomInt(pLayer.getXForms().size());
      xForm = pLayer.getXForms().get(idx);
    }
    else {
      int idx = Tools.randomInt(pLayer.getFinalXForms().size());
      xForm = pLayer.getFinalXForms().get(idx);
    }

    if (Math.random() < 0.75) {
      if (Math.random() < 0.67 && xForm.getVariationCount() > 0) {
        int idx = (int) (Math.random() * xForm.getVariationCount());
        xForm.removeVariation(xForm.getVariation(idx));
      }
      String fName;
      if (Math.random() < 0.33) {
        int idx = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length;
        fName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(idx)];
      }
      else {
        fName = VariationFuncList.getRandomVariationname();
      }
      xForm.addVariation(0.01 + Math.random() * 10.0, VariationFuncList.getVariationFuncInstance(fName, true));
    }
  }

}
