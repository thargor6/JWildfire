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
    final double amp0 = 3.0;
    final double amp1 = 1.0;
    rotateXForm(pFlame, 0, amp0);
    scaleXForm(pFlame, 0, 0.95, 0.01);

    //    for (int i = 1; i <= ; i++) {
    //      if (Math.random() > 0.5) {
    //        rotateXForm(pFlame, 1, amp1);
    //        if (Math.random() > 0.67) {
    //          addFlatten(pFlame, 1);
    //        }
    //      }
    //    }

    return pFlame;
  }

  @Override
  protected Flame postProcessFlame(RandomFlameGeneratorState pState, Flame pFlame) {
    return pFlame;
  }
}
