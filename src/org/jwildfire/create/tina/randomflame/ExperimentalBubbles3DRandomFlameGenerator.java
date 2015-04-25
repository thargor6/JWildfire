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
import org.jwildfire.create.tina.transform.XFormTransformService;

public class ExperimentalBubbles3DRandomFlameGenerator extends Bubbles3DRandomFlameGenerator {

  @Override
  public Flame prepareFlame(RandomFlameGeneratorState pState) {
    Flame flame = super.prepareFlame(pState);
    Layer layer = flame.getFirstLayer();
    // modify last xForm
    {
      XForm xForm = layer.getXForms().get(layer.getXForms().size() - 1);
      XFormTransformService.scale(xForm, 0.5 + 5.0 * Math.random(), Math.random() < 0.75, Math.random() < 0.75, false);
      XFormTransformService.rotate(xForm, 180.0 - Math.random() * 360.0, false);
      XFormTransformService.localTranslate(xForm, 4.0 - 8.0 * Math.random(), 4.0 - 8.0 * Math.random(), false);
    }
    return flame;
  }

  @Override
  public String getName() {
    return "Bubbles3D (experimental)";
  }

}
