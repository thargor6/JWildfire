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

public class Gnarl3DRandomFlameGenerator extends AbstractAffine3DRandomFlameGenerator {

  @Override
  public String getName() {
    return "Gnarl3D";
  }

  @Override
  protected RandomFlameGenerator selectRandGen() {
    return Math.random() > 0.33 ? new GnarlRandomFlameGenerator() : new ExperimentalGnarlRandomFlameGenerator();
  }

  @Override
  protected Flame preProcessFlame(Flame pFlame) {
    Layer layer = pFlame.getFirstLayer();
    while (layer.getXForms().size() > 2) {
      layer.getXForms().remove(layer.getXForms().size() - 1);
    }
    return pFlame;
  }

  @Override
  protected Flame postProcessFlame(Flame pFlame) {
    rotateXForm(pFlame, 0, 3.0);
    scaleXForm(pFlame, 0, 0.95, 0.01);
    return pFlame;
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
