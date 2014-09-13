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
import org.jwildfire.create.tina.variation.VariationFuncList;

public class Brokat3DRandomFlameGenerator extends AbstractExtrude3DRandomFlameGenerator {

  @Override
  public String getName() {
    return "Brokat3D";
  }

  @Override
  protected RandomFlameGenerator selectRandGen() {
    return new BrokatRandomFlameGenerator();
  }

  @Override
  protected Flame preProcessFlame(Flame pFlame) {
    Layer layer = pFlame.getFirstLayer();
    for (XForm xForm : layer.getXForms()) {
      if (Math.random() > 0.33 && !xForm.hasVariation("flatten")) {
        xForm.addVariation(1.0, VariationFuncList.getVariationFuncInstance("flatten", true));
      }
    }
    return pFlame;
  }

  @Override
  protected Flame postProcessFlame(Flame pFlame) {
    pFlame.setCamZoom(2.0 * pFlame.getCamZoom());
    pFlame.setCamYaw(Math.random() * 30.0 + pFlame.getCamYaw());
    pFlame.setCamPitch(Math.random() * 30.0 + pFlame.getCamPitch());
    return pFlame;
  }

  @Override
  protected Flame postProcessFlame(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }

}
