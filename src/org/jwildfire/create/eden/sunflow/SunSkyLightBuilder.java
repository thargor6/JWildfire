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
import org.jwildfire.create.eden.sunflow.base.IntSingle;
import org.jwildfire.create.eden.sunflow.base.PartBuilder;

public class SunSkyLightBuilder implements PartBuilder {
  private final SunflowSceneBuilder parent;
  private FloatTriple up = new FloatTriple();
  private FloatTriple east = new FloatTriple();
  private FloatTriple sundir = new FloatTriple();
  private FloatSingle turbidity = new FloatSingle();
  private IntSingle samples = new IntSingle();

  public SunSkyLightBuilder(SunflowSceneBuilder pParent) {
    parent = pParent;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    pTarget.append("light {\n");

    pTarget.append("  type sunsky\n");

    if (!up.isEmpty())
      pTarget.append("  up " + up.toSceneStringPart() + "\n");

    if (!east.isEmpty())
      pTarget.append("  east " + east.toSceneStringPart() + "\n");

    if (!sundir.isEmpty())
      pTarget.append("  sundir " + sundir.toSceneStringPart() + "\n");

    if (!turbidity.isEmpty())
      pTarget.append("  turbidity " + turbidity.toSceneStringPart() + "\n");

    if (!samples.isEmpty())
      pTarget.append("  samples " + samples.toSceneStringPart() + "\n");

    pTarget.append("}\n");
  }

  public SunflowSceneBuilder close() {
    return parent;
  }

  public SunSkyLightBuilder withUp(double pUpX, double pUpY, double pUpZ) {
    up = new FloatTriple(pUpX, pUpY, pUpZ);
    return this;
  }

  public SunSkyLightBuilder withEast(double pEastX, double pEastY, double pEastZ) {
    east = new FloatTriple(pEastX, pEastY, pEastZ);
    return this;
  }

  public SunSkyLightBuilder withSundir(double pSundirX, double pSundirY, double pSundirZ) {
    sundir = new FloatTriple(pSundirX, pSundirY, pSundirZ);
    return this;
  }

  public SunSkyLightBuilder withTurbidity(double pTurbidity) {
    turbidity = new FloatSingle(pTurbidity);
    return this;
  }

  public SunSkyLightBuilder withSamples(int pSamples) {
    samples = new IntSingle(pSamples);
    return this;
  }

}
