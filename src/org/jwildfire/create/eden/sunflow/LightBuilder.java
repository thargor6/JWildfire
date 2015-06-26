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
package org.jwildfire.create.eden.sunflow;

import org.jwildfire.create.eden.sunflow.base.FloatSingle;
import org.jwildfire.create.eden.sunflow.base.FloatTriple;
import org.jwildfire.create.eden.sunflow.base.LightType;
import org.jwildfire.create.eden.sunflow.base.PartBuilder;
import org.jwildfire.create.eden.sunflow.base.RGBColor;

public class LightBuilder implements PartBuilder {
  private final SunflowSceneBuilder parent;
  private LightType type = LightType.POINT;
  private RGBColor color = new RGBColor();
  private FloatSingle power = new FloatSingle();
  private FloatTriple p = new FloatTriple();

  public LightBuilder(SunflowSceneBuilder pParent) {
    parent = pParent;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    pTarget.append("light {\n");

    pTarget.append("  type " + type.toSceneStringPart() + "\n");

    if (!color.isEmpty())
      pTarget.append("  color " + color.toSceneStringPart() + "\n");

    if (!power.isEmpty())
      pTarget.append("  power " + power.toSceneStringPart() + "\n");

    if (!p.isEmpty())
      pTarget.append("  p " + p.toSceneStringPart() + "\n");

    pTarget.append("}\n");
  }

  public SunflowSceneBuilder close() {
    return parent;
  }

  public LightBuilder withType(LightType pType) {
    type = pType;
    return this;
  }

  public LightBuilder withColor(double pR, double pG, double pB) {
    color = new RGBColor(pR, pG, pB);
    return this;
  }

  public LightBuilder withPower(double pPower) {
    power = new FloatSingle(pPower);
    return this;
  }

  public LightBuilder withP(double pPX, double pPY, double pPZ) {
    p = new FloatTriple(pPX, pPY, pPZ);
    return this;
  }

}
