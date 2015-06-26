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
package org.jwildfire.create.eden.scene.material;

import org.jwildfire.create.eden.base.Color3f;
import org.jwildfire.create.eden.scene.SceneElement;

public abstract class Material extends SceneElement {
  public static final String MATERIAL_GLASS = "glass";
  public static final String MATERIAL_MIRROR = "mirror";
  public static final String MATERIAL_SHINY = "shiny";
  public static final String MATERIAL_SHINY_BLUE = "shiny_blue";
  public static final String MATERIAL_SHINY_GREEN = "shiny_green";
  public static final String MATERIAL_SHINY_RED = "shiny_red";

  private Color3f color = Color3f.OPTIONAL;

  public Color3f getColor() {
    return color;
  }

  public void setColor(Color3f pColor) {
    color = pColor;
  }

}
