/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.dance.motion;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.dance.DancingFlameProject;

public class BalancingPreprocessor extends FlamePreprocessor {
  private static final long serialVersionUID = 1L;

  @Property(description = "Red", category = PropertyCategory.GENERAL)
  private int red = 0;
  @Property(description = "Green", category = PropertyCategory.GENERAL)
  private int green = 0;
  @Property(description = "Blue", category = PropertyCategory.GENERAL)
  private int blue = 0;

  @Override
  public Flame preprocessFlame(DancingFlameProject pProject, Flame pFlame) {
    for (Layer layer : pFlame.getLayers()) {
      layer.getPalette().setModRed(red);
      layer.getPalette().setModGreen(green);
      layer.getPalette().setModBlue(blue);
    }
    return pFlame;
  }

  public int getRed() {
    return red;
  }

  public void setRed(int red) {
    this.red = red;
  }

  public int getGreen() {
    return green;
  }

  public void setGreen(int green) {
    this.green = green;
  }

  public int getBlue() {
    return blue;
  }

  public void setBlue(int blue) {
    this.blue = blue;
  }

}
