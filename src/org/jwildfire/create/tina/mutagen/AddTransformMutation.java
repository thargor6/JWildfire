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

public class AddTransformMutation implements Mutation {

  @Override
  public void execute(Layer pLayer) {
    if (Math.random() < 0.75) {
      if (Math.random() < 0.5 && pLayer.getXForms().size() > 0) {
        int idx = (int) (Math.random() * pLayer.getXForms().size());
        pLayer.getXForms().remove(idx);
      }
      XForm xForm = new XForm();
      pLayer.getXForms().add(xForm);
      xForm.setWeight(0.1 + Math.random() * 2.0);
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
    else {
      if (Math.random() < 0.5 && pLayer.getFinalXForms().size() > 0) {
        int idx = (int) (Math.random() * pLayer.getFinalXForms().size());
        pLayer.getFinalXForms().remove(idx);
      }
      XForm xForm = new XForm();
      pLayer.getFinalXForms().add(xForm);
      xForm.setWeight(0.1 + Math.random() * 2.0);
      String fName;
      if (Math.random() < 0.33) {
        int idx = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL.length;
        fName = ExperimentalSimpleRandomFlameGenerator.FNCLST_EXPERIMENTAL[Tools.randomInt(idx)];
      }
      else {
        while (true) {
          fName = VariationFuncList.getRandomVariationname();
          if (fName.indexOf("blur") < 0 && fName.indexOf("Blur") < 0 && fName.indexOf("fract_") != 0) {
            break;
          }
        }
      }

      xForm.addVariation(0.01 + Math.random() * 10.0, VariationFuncList.getVariationFuncInstance(fName, true));
    }
  }

}
