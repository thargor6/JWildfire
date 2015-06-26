/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.eden.scene;

import org.jwildfire.create.eden.scene.material.Material;

public class VisibleSceneElement extends PositionableSceneElement implements Cloneable {
  private String material = Material.MATERIAL_SHINY;

  public VisibleSceneElement(PositionableSceneElement pParent) {
    super(pParent);
  }

  public String getMaterial() {
    return material;
  }

  public void setMaterial(String pMaterial) {
    material = pMaterial;
  }

  @Override
  public VisibleSceneElement clone() throws CloneNotSupportedException {
    VisibleSceneElement copy = (VisibleSceneElement) super.clone();
    PositionableSceneElement parent = getParent();
    while (!(parent instanceof SceneElementGroup)) {
      parent = parent.getParent();
    }
    ((SceneElementGroup) parent).getElements().add(copy);
    return copy;
  }
}
