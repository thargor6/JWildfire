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

import org.jwildfire.create.tina.meshgen.RawFaces;

public class FacesMerger {

  private static final float DLFT_OBJSIZE = 10.0f;

  private static class AvgNormal extends Point {
    public int count;

    public AvgNormal(Point pPoint) {
      x = pPoint.x;
      y = pPoint.y;
      z = pPoint.z;
      count = 1;
    }

    public void addPoint(Point pPoint) {
      x += pPoint.x;
      y += pPoint.y;
      z += pPoint.z;
      count++;
    }

    public float getAvgX() {
      return count > 0 ? x / (float) count : 0.0f;
    }

    public float getAvgY() {
      return count > 0 ? y / (float) count : 0.0f;
    }

    public float getAvgZ() {
      return count > 0 ? z / (float) count : 0.0f;
    }

  }

  public static Mesh generateMesh(RawFaces pRawFaces) {
    List<Point> vertices = pRawFaces.getVertices();
    List<Point> faceNormals = pRawFaces.getNormals();

    Map<Point, Integer> index = new HashMap<Point, Integer>();
    List<Point> points = new ArrayList<Point>();

    Point first = vertices.size() > 0 ? vertices.get(0) : new Point();
    float xmin = first.x, xmax = first.x;
    float ymin = first.y, ymax = first.y;
    float zmin = first.z, zmax = first.z;

    long t0, t1;

    t0 = System.currentTimeMillis();
    for (Point point : vertices) {
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
    t1 = System.currentTimeMillis();
    System.out.println("CREATE UNIQUE POINTS: " + (t1 - t0) / 1000.0 + "s");

    t0 = System.currentTimeMillis();
    Set<Face> facesSet = new HashSet<Face>();
    List<Face> faces = new ArrayList<Face>();
    Map<Integer, AvgNormal> normalMap = new HashMap<Integer, AvgNormal>();
    for (int i = 0; i < vertices.size(); i += 3) {
      int a = index.get(vertices.get(i));
      int b = index.get(vertices.get(i + 1));
      int c = index.get(vertices.get(i + 2));
      if (a != b && b != c && a != c) {
        Face face = new Face(a, c, b);
        if (!facesSet.contains(face)) {
          facesSet.add(face);
          faces.add(face);
          if (faceNormals != null) {
            Point faceNormal = faceNormals.get(i / 3);
            addFaceNormalToNormalMap(normalMap, a, faceNormal);
            addFaceNormalToNormalMap(normalMap, b, faceNormal);
            addFaceNormalToNormalMap(normalMap, c, faceNormal);
          }
        }
      }
    }
    t1 = System.currentTimeMillis();
    System.out.println("CREATE FACES: " + (t1 - t0) / 1000.0 + "s");

    t0 = System.currentTimeMillis();
    float xsize = xmax - xmin;
    float ysize = ymax - ymin;
    float zsize = zmax - zmin;
    float size = (xsize + ysize + zsize) / 3.0f;

    float dx = -xmin - (xmax - xmin) / 2.0f;
    float dy = -ymin - (ymax - ymin) / 2.0f;
    float dz = -zmin - (zmax - zmin) / 2.0f;
    float scale = DLFT_OBJSIZE / size;

    if (faceNormals != null) {
      List<Point> vertexNormals = new ArrayList<Point>();
      for (int i = 0; i < points.size(); i++) {
        Point point = points.get(i);
        AvgNormal avgNormal = normalMap.get(i);
        if (avgNormal != null) {
          vertexNormals.add(new Point(avgNormal.getAvgX(), avgNormal.getAvgY(), avgNormal.getAvgZ()));
        }
        else {
          vertexNormals.add(new Point());
        }
        point.x = (point.x + dx) * scale;
        point.y = (point.y + dy) * scale;
        point.z = (point.z + dz) * scale;
      }
      t1 = System.currentTimeMillis();
      System.out.println("SCALING POINTS: " + (t1 - t0) / 1000.0 + "s");
      return new Mesh(points, vertexNormals, faces);
    }
    else {
      for (Point point : points) {
        point.x = (point.x + dx) * scale;
        point.y = (point.y + dy) * scale;
        point.z = (point.z + dz) * scale;
      }
      t1 = System.currentTimeMillis();
      System.out.println("SCALING POINTS: " + (t1 - t0) / 1000.0 + "s");
      return new Mesh(points, faces);
    }

  }

  private static void addFaceNormalToNormalMap(Map<Integer, AvgNormal> pNormalMap, int pPointIndex, Point pFaceNormal) {
    Integer key = Integer.valueOf(pPointIndex);
    AvgNormal avg = pNormalMap.get(key);
    if (avg == null) {
      avg = new AvgNormal(pFaceNormal);
      pNormalMap.put(key, avg);
    }
    else {
      avg.addPoint(pFaceNormal);
    }
  }
}
