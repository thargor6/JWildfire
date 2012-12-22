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
package org.jwildfire.create.tina.dance;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.FlameMorphService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.swing.FlameHolder;

public class FlameStack implements FlameHolder {
  private final Prefs prefs;
  private List<Flame> flames = new ArrayList<Flame>();

  public FlameStack(Prefs pPrefs) {
    prefs = pPrefs;
  }

  public void addFlame(Flame pFlame, int pMorphFrameCount) {
    if (flames.size() == 0 || pMorphFrameCount < 2) {
      flames.add(pFlame);
    }
    else {
      Flame prevFlame = flames.get(flames.size() - 1);
      for (int i = 1; i < pMorphFrameCount; i++) {
        Flame flame = FlameMorphService.morphFlames(prefs, prevFlame, pFlame, i, pMorphFrameCount);
        flames.add(flame);
      }
      flames.add(pFlame);
    }
  }

  @Override
  public Flame getFlame() {
    if (flames.size() > 0) {
      Flame res = flames.get(0);
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
