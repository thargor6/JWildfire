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
package org.jwildfire.create.eden.sunflow;

import org.jwildfire.create.eden.sunflow.base.FloatQuadruple;
import org.jwildfire.create.eden.sunflow.base.FloatSingle;
import org.jwildfire.create.eden.sunflow.base.IntSingle;
import org.jwildfire.create.eden.sunflow.base.PartBuilder;

public class JuliaBuilder extends PrimitiveBuilder<JuliaBuilder> implements PartBuilder {
  private FloatQuadruple c = new FloatQuadruple(0.15, -0.2, 0.24, 0.02);
  private IntSingle iterations = new IntSingle(8);
  private FloatSingle epsilon = new FloatSingle(0.0001);

  public JuliaBuilder(SunflowSceneBuilder pParent) {
    super(pParent);
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    pTarget.append("object {\n");
    if (!shader.isEmpty())
      pTarget.append("  shader " + shader.toSceneStringPart() + "\n");
    if (!name.isEmpty())
      pTarget.append("  name " + name.toSceneStringPart() + "\n");
    transform.buildPart(pTarget);
    pTarget.append("  type julia\n");
    if (!c.isEmpty())
      pTarget.append("  q " + c.toSceneStringPart() + "\n");
    if (!iterations.isEmpty())
      pTarget.append("  iterations " + iterations.toSceneStringPart() + "\n");
    if (!epsilon.isEmpty())
      pTarget.append("  epsilon " + epsilon.toSceneStringPart() + "\n");
    pTarget.append("}\n");
  }

  public JuliaBuilder withC(double pCW, double pCX, double pCY, double pCZ) {
    c = new FloatQuadruple(pCW, pCX, pCY, pCZ);
    return this;
  }

  public JuliaBuilder withIterations(int pIterations) {
    iterations = new IntSingle(pIterations);
    return this;
  }

  public JuliaBuilder withEpsilon(double pEpsilon) {
    epsilon = new FloatSingle(pEpsilon);
    return this;
  }

}
