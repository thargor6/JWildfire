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
package org.jwildfire.create.tina.meshgen.marchingcubes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FacesMerger {

  private static final float DLFT_OBJSIZE = 10.0f;

  public static Mesh generateMesh(List<Point> pFaces) {
    Map<Point, Integer> index = new HashMap<Point, Integer>();
    List<Point> points = new ArrayList<Point>();

    Point first = pFaces.size() > 0 ? pFaces.get(0) : new Point();
    float xmin = first.x, xmax = first.x;
    float ymin = first.y, ymax = first.y;
    float zmin = first.z, zmax = first.z;

    for (Point point : pFaces) {
      if (index.get(point) == null) {
        index.put(point, points.size());
        points.add(point);

        if (point.x < xmin)
          xmin = point.x;
        else if (point.x > xmax)
          xmax = point.x;

        if (point.y < ymin)
          ymin = point.y;
        else if (point.y > ymax)
          ymax = point.y;

        if (point.z < zmin)
          zmin = point.z;
        else if (point.z > zmax)
          zmax = point.z;
      }
    }

    Set<Face> faces = new HashSet<Face>();
    for (int i = 0; i < pFaces.size(); i += 3) {
      int a = index.get(pFaces.get(i));
      int b = index.get(pFaces.get(i + 1));
      int c = index.get(pFaces.get(i + 2));
      if (a != b && b != c && a != c) {
        Face face = new Face(a, c, b);
        if (!faces.contains(face)) {
          faces.add(face);
        }
      }
    }

    float xsize = xmax - xmin;
    float ysize = ymax - ymin;
    float zsize = zmax - zmin;
    float size = (xsize + ysize + zsize) / 3.0f;

    float dx = -xmin - (xmax - xmin) / 2.0f;
    float dy = -ymin - (ymax - ymin) / 2.0f;
    float dz = -zmin - (zmax - zmin) / 2.0f;
    float scale = DLFT_OBJSIZE / size;
    for (Point point : points) {
      point.x = (point.x + dx) * scale;
      point.y = (point.y + dy) * scale;
      point.z = (point.z + dz) * scale;
    }

    return new Mesh(points, faces);
  }
}
