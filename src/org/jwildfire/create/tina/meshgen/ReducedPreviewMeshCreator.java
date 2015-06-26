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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jwildfire.create.tina.meshgen.marchingcubes.Face;
import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point3f;

public class ReducedPreviewMeshCreator {

  public static Mesh createReducedMesh(Mesh pMesh, int pFaceCount) {
    if (pFaceCount * 0.9 > pMesh.getFaces().size()) {
      return pMesh;
    }

    List<Point3f> points = new ArrayList<Point3f>();
    List<Face> faces = new ArrayList<Face>();

    List<Face> allFaces = new ArrayList<Face>(pMesh.getFaces());
    List<Point3f> allPoints = pMesh.getVertices();
    long t0 = System.currentTimeMillis();
    Set<Integer> indexSet = new HashSet<Integer>();

    for (int i = 0; i < pFaceCount; i++) {

      int fIdx = -1;
      for (int j = 0; j < 7; j++) {
        int idx = (int) (Math.random() * allFaces.size());
        if (!indexSet.contains(idx)) {
          fIdx = idx;
          break;
        }
      }
      if (fIdx >= 0) {
        Face face = new Face(allFaces.get(fIdx));
        Point3f p1 = new Point3f(allPoints.get(face.a));
        Point3f p2 = new Point3f(allPoints.get(face.b));
        Point3f p3 = new Point3f(allPoints.get(face.c));
        face.a = points.size();
        face.b = points.size() + 1;
        face.c = points.size() + 2;

        points.add(p1);
        points.add(p2);
        points.add(p3);
        faces.add(face);
      }
    }
    long t1 = System.currentTimeMillis();
    System.out.println("REDUCE MESH: " + (t1 - t0) / 1000.0 + "s");

    return new Mesh(points, faces);
  }
}
