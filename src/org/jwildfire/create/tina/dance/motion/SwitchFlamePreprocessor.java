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

import java.util.List;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.DancingFlameProject;

public class SwitchFlamePreprocessor extends FlamePreprocessor implements FlameMotionPreprocessor {
  private static final long serialVersionUID = 1L;

  @Property(description = "Flame index", category = PropertyCategory.GENERAL)
  private int index = 0;

  @Override
  public Flame preprocessFlame(DancingFlameProject pProject, Flame pFlame) {
    if (index >= 0 && index < pProject.getFlames().size()) {
      return pProject.getFlames().get(index);
    }
    return pFlame;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public List<Motion> preprocessMotions(DancingFlameProject pProject, List<Motion> pMotions) {
    if (index >= 0 && index < pProject.getFlames().size()) {
      Flame flame = pProject.getFlames().get(index);
      return pProject.getMotions(flame);
    }
    return pMotions;
  }

}
