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
package org.jwildfire.base;

import java.util.ArrayList;
import java.util.List;

public class Preset {
  private final String name;
  private final List<PresetProperty<?>> properties = new ArrayList<PresetProperty<?>>();

  public Preset(String pName) {
    name = pName;
  }

  public String getName() {
    return name;
  }

  public <T> Preset addProperty(String pName, T pValue) {
    PresetProperty<T> property = new PresetProperty<T>();
    property.setName(pName);
    property.setValue(pValue);
    properties.add(property);
    return this;
  }

  public PresetProperty<?> getPropertyByName(String pName) {
    for (PresetProperty<?> property : properties) {
      if (property.getName().equals(pName)) {
        return property;
      }
    }
    return null;
  }

}
