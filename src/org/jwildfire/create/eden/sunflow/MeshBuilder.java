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

import org.jwildfire.create.eden.base.Face3i;
import org.jwildfire.create.eden.base.Point3f;
import org.jwildfire.create.eden.scene.primitive.Mesh;
import org.jwildfire.create.eden.sunflow.base.PartBuilder;

public class MeshBuilder extends PrimitiveBuilder<MeshBuilder> implements PartBuilder {
  private Mesh mesh;

  public MeshBuilder(SunflowSceneBuilder pParent) {
    super(pParent);
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    if (mesh != null && mesh.getPoints().size() > 0 && mesh.getFaces().size() > 0) {
      pTarget.append("object {\n");

      if (!shader.isEmpty())
        pTarget.append("  shader " + shader.toSceneStringPart() + "\n");
      transform.buildPart(pTarget);

      pTarget.append("  type generic-mesh\n");

      if (!name.isEmpty())
        pTarget.append("  name " + name.toSceneStringPart() + "\n");

      pTarget.append("  points " + mesh.getPoints().size() + "\n");
      for (Point3f point : mesh.getPoints()) {
        pTarget.append("    " + point.getX() + " " + point.getY() + " " + point.getZ() + "\n");
      }

      pTarget.append("  triangles " + mesh.getFaces().size() + "\n");
      for (Face3i face : mesh.getFaces()) {
        pTarget.append("    " + face.getA() + " " + face.getC() + " " + face.getB() + "\n");
      }
      pTarget.append("  normals none\n");
      pTarget.append("  uvs none\n");
      pTarget.append("}\n");
    }
  }

  public MeshBuilder withMesh(Mesh pMesh) {
    mesh = pMesh;
    return this;
  }

}
