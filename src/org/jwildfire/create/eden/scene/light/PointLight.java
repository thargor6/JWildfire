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
package org.jwildfire.create.eden.scene.light;

import org.jwildfire.create.eden.base.Color3f;
import org.jwildfire.create.eden.scene.PositionableSceneElement;

public class PointLight extends PositionableSceneElement {
  private Color3f color = new Color3f(1.0, 1.0, 1.0);
  private double intensity = 1.0;

  public PointLight(PositionableSceneElement pParent) {
    super(pParent);
  }

  public Color3f getColor() {
    return color;
  }

  public void setColor(Color3f pColor) {
    color = pColor;
  }

  public double getIntensity() {
    return intensity;
  }

  public void setIntensity(double pIntensity) {
    intensity = pIntensity;
  }

}
