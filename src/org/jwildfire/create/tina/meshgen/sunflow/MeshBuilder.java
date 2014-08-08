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

import org.jwildfire.create.tina.meshgen.marchingcubes.Face;
import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point;
import org.jwildfire.create.tina.meshgen.sunflow.base.PartBuilder;
import org.jwildfire.create.tina.meshgen.sunflow.base.StringSingle;

public class MeshBuilder implements PartBuilder {
  private final SceneBuilder parent;
  private StringSingle name = new StringSingle();
  private StringSingle shader = new StringSingle();
  private Mesh mesh = new Mesh();

  public MeshBuilder(SceneBuilder pParent) {
    parent = pParent;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    pTarget.append("object {\n");

    if (!shader.isEmpty())
      pTarget.append("  shader " + shader.toSceneStringPart() + "\n");

    pTarget.append("  type generic-mesh\n");

    if (!name.isEmpty())
      pTarget.append("  name " + name.toSceneStringPart() + "\n");

    pTarget.append("  points " + mesh.getPoints().size() + "\n");
    for (Point point : mesh.getPoints()) {
      pTarget.append("    " + point.x + " " + point.y + " " + point.z + "\n");
    }

    pTarget.append("  triangles " + mesh.getFaces().size() + "\n");
    for (Face face : mesh.getFaces()) {
      pTarget.append("    " + face.a + " " + face.b + " " + face.c + "\n");
    }

    pTarget.append("  normals none\n");
    pTarget.append("  uvs none\n");

    pTarget.append("}\n");
  }

  public SceneBuilder close() {
    return parent;
  }

  public MeshBuilder withName(String pName) {
    name = new StringSingle(pName);
    return this;
  }

  public MeshBuilder withShader(String pShader) {
    shader = new StringSingle(pShader);
    return this;
  }

  public MeshBuilder withMesh(Mesh pMesh) {
    mesh = pMesh;
    return this;
  }

}
