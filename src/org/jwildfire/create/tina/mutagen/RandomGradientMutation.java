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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.randomgradient.AllRandomGradientGenerator;

public class RandomGradientMutation implements Mutation {

  // for Script-compatibility
  @Deprecated
  public void execute(Flame pFlame) {
    execute(pFlame.getFirstLayer());
  }

  @Override
  public void execute(Layer pLayer) {
    int keyFrames = 3 + Tools.randomInt(56);
    boolean fadePaletteColors = Math.random() > 0.33;
    boolean uniformWidth = Math.random() > 0.75;
    RGBPalette palette = new AllRandomGradientGenerator().generatePalette(keyFrames, fadePaletteColors, uniformWidth);
    pLayer.setPalette(palette);
  }

}
