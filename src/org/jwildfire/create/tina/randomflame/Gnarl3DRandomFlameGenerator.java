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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationFuncList;

public class Gnarl3DRandomFlameGenerator extends GnarlRandomFlameGenerator {

  @Override
  protected Flame createFlame(RandomFlameGeneratorState pState) {
    Flame flame = super.createFlame(pState);
    flame.setCamPitch(25.0 + Math.random() * 30.0);
    flame.setCamYaw(10 + Math.random() * 20.0);
    flame.setCamPerspective(0.1 + Math.random() * 0.3);
    flame.setCamZoom(0.5);
    flame.setPreserveZ(true);

    Layer layer = flame.getFirstLayer();
    while (layer.getXForms().size() > 2) {
      layer.getXForms().remove(layer.getXForms().size() - 1);
    }

    XForm xForm = new XForm();
    layer.getFinalXForms().add(xForm);
    xForm.addVariation(0.25 + Math.random() * 0.25, VariationFuncList.getVariationFuncInstance("linear3D", true));
    VariationFunc post_dcztransl = VariationFuncList.getVariationFuncInstance("post_dcztransl", true);
    xForm.addVariation(0.75 + Math.random() * 0.25, post_dcztransl);
    double factor = 0.5 + Math.random() * 4.0;
    if (Math.random() < 0.42) {
      factor = 0.0 - factor;
    }
    post_dcztransl.setParameter("factor", factor);

    return flame;
  }

  @Override
  public String getName() {
    return "Gnarl3D";
  }

  @Override
  public boolean isUseFilter(RandomFlameGeneratorState pState) {
    return true;
  }

}
