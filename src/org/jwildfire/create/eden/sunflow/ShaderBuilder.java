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
import org.jwildfire.create.eden.sunflow.base.RGBColorWithAmount;
import org.jwildfire.create.eden.sunflow.base.ShaderType;
import org.jwildfire.create.eden.sunflow.base.StringSingle;

public class ShaderBuilder implements PartBuilder {
  private final SunflowSceneBuilder parent;
  private StringSingle name = new StringSingle();
  private ShaderType type = ShaderType.SHINY;
  private FloatTriple diff = new FloatTriple();
  private FloatSingle refl = new FloatSingle();
  private FloatSingle eta = new FloatSingle();
  private StringSingle texture = new StringSingle();
  private RGBColorWithAmount spec = new RGBColorWithAmount();
  private IntSingle samples = new IntSingle();
  private FloatTriple color = new FloatTriple();
  private FloatSingle absorbtionDistance = new FloatSingle();
  private FloatTriple absorbtionColor = new FloatTriple();

  public ShaderBuilder(SunflowSceneBuilder pParent) {
    parent = pParent;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    pTarget.append("shader {\n");

    if (!name.isEmpty())
      pTarget.append("  name " + name.toSceneStringPart() + "\n");

    if (!type.isEmpty())
      pTarget.append("  type " + type.toSceneStringPart() + "\n");

    if (!eta.isEmpty())
      pTarget.append("  eta " + eta.toSceneStringPart() + "\n");

    if (!color.isEmpty())
      pTarget.append("  color " + color.toSceneStringPart() + "\n");

    if (!absorbtionDistance.isEmpty())
      pTarget.append("  absorbtion.distance " + absorbtionDistance.toSceneStringPart() + "\n");

    if (!absorbtionColor.isEmpty())
      pTarget.append("  absorbtion.color " + absorbtionColor.toSceneStringPart() + "\n");

    if (!diff.isEmpty())
      pTarget.append("  diff " + diff.toSceneStringPart() + "\n");

    if (!refl.isEmpty())
      pTarget.append("  refl " + refl.toSceneStringPart() + "\n");

    if (!texture.isEmpty())
      pTarget.append("  texture " + texture.toSceneStringPart() + "\n");

    if (!spec.isEmpty())
      pTarget.append("  spec " + spec.toSceneStringPart() + "\n");

    if (!samples.isEmpty())
      pTarget.append("  samples " + samples.toSceneStringPart() + "\n");

    pTarget.append("}\n");
  }

  public SunflowSceneBuilder close() {
    return parent;
  }

  public ShaderBuilder withName(String pName) {
    name = new StringSingle(pName);
    return this;
  }

  public ShaderBuilder withType(ShaderType pType) {
    type = pType;
    return this;
  }

  public ShaderBuilder withDiff(double pDiffX, double pDiffY, double pDiffZ) {
    diff = new FloatTriple(pDiffX, pDiffY, pDiffZ);
    return this;
  }

  public ShaderBuilder withRefl(double pRefl) {
    refl = new FloatSingle(pRefl);
    return this;
  }

  public ShaderBuilder withTexture(String pTexture) {
    texture = new StringSingle(pTexture);
    return this;
  }

  public ShaderBuilder withSpec(double pR, double pG, double pB, double pAmount) {
    spec = new RGBColorWithAmount(pR, pG, pB, pAmount);
    return this;
  }

  public ShaderBuilder withSamples(int pSamples) {
    samples = new IntSingle(pSamples);
    return this;
  }

  public ShaderBuilder withEta(double pEta) {
    eta = new FloatSingle(pEta);
    return this;
  }

  public ShaderBuilder withAbsorbtionDistance(double pabsorbtionDistance) {
    absorbtionDistance = new FloatSingle(pabsorbtionDistance);
    return this;
  }

  public ShaderBuilder withColor(double pR, double pG, double pB) {
    color = new FloatTriple(pR, pG, pB);
    return this;
  }

  public ShaderBuilder withAbsorbtionColor(double pR, double pG, double pB) {
    absorbtionColor = new FloatTriple(pR, pG, pB);
    return this;
  }

}
