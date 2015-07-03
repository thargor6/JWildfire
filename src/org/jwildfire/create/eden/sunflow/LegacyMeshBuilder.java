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

import org.jwildfire.create.eden.sunflow.base.PartBuilder;
import org.jwildfire.create.tina.meshgen.marchingcubes.Face;
import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point2f;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point3f;

public class LegacyMeshBuilder extends PrimitiveBuilder<LegacyMeshBuilder> implements PartBuilder {
  private Mesh mesh = new Mesh();

  public LegacyMeshBuilder(SunflowSceneBuilder pParent) {
    super(pParent);
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    pTarget.append("object {\n");

    if (!shader.isEmpty())
      pTarget.append("  shader " + shader.toSceneStringPart() + "\n");
    transform.buildPart(pTarget);

    pTarget.append("  type generic-mesh\n");

    if (!name.isEmpty())
      pTarget.append("  name " + name.toSceneStringPart() + "\n");

    pTarget.append("  points " + mesh.getVertices().size() + "\n");
    for (Point3f point : mesh.getVertices()) {
      pTarget.append("    " + point.x + " " + point.y + " " + point.z + "\n");
    }

    pTarget.append("  triangles " + mesh.getFaces().size() + "\n");
    for (Face face : mesh.getFaces()) {
      pTarget.append("    " + face.a + " " + face.b + " " + face.c + "\n");
    }

    if (mesh.getVertexNormals() != null && mesh.getVertexNormals().size() == mesh.getVertices().size()) {
      pTarget.append("  normals vertex\n");
      for (Point3f point : mesh.getVertexNormals()) {
        pTarget.append("    " + point.x + " " + point.y + " " + point.z + "\n");
      }
    }
    else {
      pTarget.append("  normals none\n");
    }
    if (mesh.getTextureCoords() != null && mesh.getTextureCoords().size() == mesh.getVertices().size()) {
      pTarget.append("   uvs vertex\n");
      for (Point2f uv : mesh.getTextureCoords()) {
        pTarget.append("    " + uv.u + " " + uv.v + "\n");
      }
    }
    else {
      pTarget.append("  uvs none\n");
    }

    pTarget.append("}\n");
  }

  public LegacyMeshBuilder withMesh(Mesh pMesh) {
    mesh = pMesh;
    return this;
  }

}
