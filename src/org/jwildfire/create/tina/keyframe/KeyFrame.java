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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.edit.Assignable;

public class KeyFrame implements Assignable<KeyFrame>, Serializable {
  private static final long serialVersionUID = 1L;

  private int frame;
  private final List<PropertyValue> values = new ArrayList<PropertyValue>();

  @Override
  public void assign(KeyFrame pSrc) {
    frame = pSrc.frame;
    values.clear();
    for (PropertyValue value : pSrc.values) {
      PropertyValue newValue = value.makeCopy();
      values.add(newValue);
    }
  }

  @Override
  public KeyFrame makeCopy() {
    KeyFrame res = new KeyFrame();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(KeyFrame pSrc) {
    if (frame != pSrc.frame || values.size() != pSrc.values.size()) {
      return false;
    }
    for (int i = 0; i < values.size(); i++) {
      if (!values.get(i).isEqual(pSrc.values.get(i))) {
        return false;
      }
    }
    return true;
  }

  public int getFrame() {
    return frame;
  }

  public void setFrame(int frame) {
    this.frame = frame;
  }

  public List<PropertyValue> getValues() {
    return values;
  }

}
