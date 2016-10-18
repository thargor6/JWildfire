/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen;

import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;

public class MeshPair {
  private static final int PREVIEW_FACES = 150000;
  private final Mesh mesh;
  private Mesh reducedMesh;

  public MeshPair(Mesh pMesh) {
    mesh = pMesh;
  }

  public Mesh getReducedMesh() {
    if (reducedMesh == null) {
      reducedMesh = ReducedPreviewMeshCreator.createReducedMesh(mesh, PREVIEW_FACES);
    }
    return reducedMesh;
  }

  public void setReducedMesh(Mesh pReducedMesh) {
    reducedMesh = pReducedMesh;
  }

  public Mesh getMesh() {
    return mesh;
  }
}
