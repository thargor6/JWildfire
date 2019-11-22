/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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

public class Spirals3DRandomFlameGenerator extends AbstractAffine3DRandomFlameGenerator {

  @Override
  public String getName() {
    return "Spirals3D";
  }

  @Override
  protected RandomFlameGenerator selectRandGen() {
    return new SpiralsRandomFlameGenerator();
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
    pFlame.setCamYaw((0.5 - Math.random()) * 75.0 + pFlame.getCamYaw());
    pFlame.setCamPitch((0.5 - Math.random()) * 135.0 + pFlame.getCamPitch());
    pFlame.setCamBank((0.5 - Math.random()) * 75.0 + pFlame.getCamYaw());
    pFlame.setCamPerspective(0.1 + Math.random() * 0.4);
    pFlame.setCamZoom(2.0);
    rotateXForm(pFlame, 0, 30.0);
    scaleXForm(pFlame, 0, 0.97, 0.01);
    rotateXForm(pFlame, 1, 10.0);
    scaleXForm(pFlame, 1, 0.97, 0.01);
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
