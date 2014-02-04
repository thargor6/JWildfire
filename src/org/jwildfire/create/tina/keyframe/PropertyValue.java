/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.keyframe;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.keyframe.model.PropertyPath;

public class PropertyValue implements Assignable<PropertyValue> {
  private PropertyPath path = new PropertyPath();
  private double value;

  @Override
  public void assign(PropertyValue pSrc) {
    path.assign(pSrc.path);
    value = pSrc.value;
  }

  @Override
  public PropertyValue makeCopy() {
    PropertyValue res = new PropertyValue();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(PropertyValue pSrc) {
    if (!comparePath(pSrc) || (fabs(value - pSrc.value) > EPSILON)) {
      return false;
    }
    return true;
  }

  private boolean comparePath(PropertyValue pSrc) {
    if (pSrc.path == null && path != null) {
      return false;
    }
    else if (pSrc.path == null && path == null) {
      return true;
    }
    else {
      return pSrc.path.isEqual(path);
    }
  }

}
