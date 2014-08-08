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
package org.jwildfire.create.tina.meshgen.sunflow;

import org.jwildfire.create.tina.meshgen.sunflow.base.CameraType;
import org.jwildfire.create.tina.meshgen.sunflow.base.FloatSingle;
import org.jwildfire.create.tina.meshgen.sunflow.base.FloatTriple;
import org.jwildfire.create.tina.meshgen.sunflow.base.PartBuilder;

public class CameraBuilder implements PartBuilder {
  private final SceneBuilder parent;
  private CameraType type = CameraType.PINHOLE;
  private FloatTriple eye = new FloatTriple();
  private FloatTriple target = new FloatTriple();
  private FloatTriple up = new FloatTriple();
  private FloatSingle fov = new FloatSingle();
  private FloatSingle aspect = new FloatSingle();

  public CameraBuilder(SceneBuilder pParent) {
    parent = pParent;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    pTarget.append("camera {\n");

    pTarget.append("  type " + type.toSceneStringPart() + "\n");

    if (!eye.isEmpty())
      pTarget.append("  eye " + eye.toSceneStringPart() + "\n");

    if (!target.isEmpty())
      pTarget.append("  target " + target.toSceneStringPart() + "\n");

    if (!up.isEmpty())
      pTarget.append("  up " + up.toSceneStringPart() + "\n");

    if (!fov.isEmpty())
      pTarget.append("  fov " + fov.toSceneStringPart() + "\n");

    if (!aspect.isEmpty())
      pTarget.append("  aspect " + aspect.toSceneStringPart() + "\n");

    pTarget.append("}\n");
  }

  public SceneBuilder close() {
    return parent;
  }

  public CameraBuilder withType(CameraType pType) {
    type = pType;
    return this;
  }

  public CameraBuilder withEye(double pEyeX, double pEyeY, double pEyeZ) {
    eye = new FloatTriple(pEyeX, pEyeY, pEyeZ);
    return this;
  }

  public CameraBuilder withTarget(double pTargetX, double pTargetY, double pTargetZ) {
    target = new FloatTriple(pTargetX, pTargetY, pTargetZ);
    return this;
  }

  public CameraBuilder withUp(double pUpX, double pUpY, double pUpZ) {
    up = new FloatTriple(pUpX, pUpY, pUpZ);
    return this;
  }

  public CameraBuilder withFov(double pFov) {
    fov = new FloatSingle(pFov);
    return this;
  }

  public CameraBuilder withAspect(double pAspect) {
    aspect = new FloatSingle(pAspect);
    return this;
  }

}
