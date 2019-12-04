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
package org.jwildfire.create.tina.dance;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.FlameMorphService;
import org.jwildfire.create.tina.animate.FlameMorphType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.motion.Motion;

public class DancingFlameStack {
  private final Prefs prefs;
  private List<DancingFlame> flames = new ArrayList<DancingFlame>();

  public DancingFlameStack(Prefs pPrefs) {
    prefs = pPrefs;
  }

  public void addFlame(Flame pFlame, int pMorphFrameCount, List<Motion> pMotions) {
    if (flames.size() == 0 || pMorphFrameCount < 2) {
      flames.add(new DancingFlame(pFlame, pMotions));
    }
    else {
      Flame prevFlame = flames.get(flames.size() - 1).getFlame();
      for (int i = 1; i < pMorphFrameCount; i++) {
        Flame flame = FlameMorphService.morphFlames(prefs, FlameMorphType.MORPH, prevFlame, pFlame, i, pMorphFrameCount, false);
        // TODO how to morph motions?
        flames.add(new DancingFlame(flame, pMotions));
      }
      flames.add(new DancingFlame(pFlame, pMotions));
    }
  }

  public DancingFlame getFlame() {
    if (flames.size() > 0) {
      DancingFlame res = flames.get(0);
      if (flames.size() > 1) {
        flames.remove(0);
      }
      return res;
    }
    else {
      return null;
    }
  }
}
