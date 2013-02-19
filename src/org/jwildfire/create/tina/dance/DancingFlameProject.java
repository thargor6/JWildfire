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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.dance.motion.Motion;
import org.jwildfire.create.tina.dance.motion.MotionLink;

public class DancingFlameProject implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<Flame> flames = new ArrayList<Flame>();
  private List<Motion> motions = new ArrayList<Motion>();

  public List<Flame> getFlames() {
    return flames;
  }

  public List<Motion> getMotions() {
    return motions;
  }

  public List<Motion> getMotions(Flame pFlame) {
    List<Motion> res = new ArrayList<Motion>();
    for (Motion motion : motions) {
      for (MotionLink link : motion.getMotionLinks()) {
        if (link.getProperyPath().getFlame().isEqual(pFlame)) {
          res.add(motion);
          break;
        }
      }
    }
    return res;
  }

}
