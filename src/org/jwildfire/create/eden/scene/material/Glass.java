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

public class Glass extends Material {
  private double eta;
  private Color3f absorptionColor;

  public Glass(String pName) {
    setEta(1.33);
    setAbsorptionColor(new Color3f(0.7, 0.7, 0.7));
    setColor(new Color3f(0.8, 0.8, 0.8));
    setName(pName);
  }

  public double getEta() {
    return eta;
  }

  public void setEta(double eta) {
    this.eta = eta;
  }

  public Color3f getAbsorptionColor() {
    return absorptionColor;
  }

  public void setAbsorptionColor(Color3f pAbsorptionColor) {
    absorptionColor = pAbsorptionColor;
  }

}
