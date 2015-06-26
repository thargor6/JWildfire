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

import org.jwildfire.create.eden.sunflow.base.IntSingle;
import org.jwildfire.create.eden.sunflow.base.PartBuilder;

public class TraceDepthsBuilder implements PartBuilder {
  private final SunflowSceneBuilder parent;
  private IntSingle diff = new IntSingle(2);
  private IntSingle refl = new IntSingle(2);
  private IntSingle refr = new IntSingle(2);

  public TraceDepthsBuilder(SunflowSceneBuilder pParent) {
    parent = pParent;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    pTarget.append("trace-depths {\n");
    pTarget.append("  diff " + diff.toSceneStringPart() + "\n");
    pTarget.append("  refl " + refl.toSceneStringPart() + "\n");
    pTarget.append("  refr " + refr.toSceneStringPart() + "\n");
    pTarget.append("}\n");
  }

  public SunflowSceneBuilder close() {
    return parent;
  }

  public TraceDepthsBuilder withDepths(int pDiff, int pRefl, int pRefr) {
    diff = new IntSingle(pDiff);
    refl = new IntSingle(pRefl);
    refr = new IntSingle(pRefr);
    return this;
  }
}
