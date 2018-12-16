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
package org.jwildfire.create.tina.meshgen;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.meshgen.marchingcubes.Face;
import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point3f;

public class SimpleWavefrontObjLoader {

  public static Mesh readMesh(String pFilename) {
    try {
      long t0 = System.currentTimeMillis();
      String file = Tools.readUTF8Textfile(pFilename);

      List<Point3f> vertices = new ArrayList<Point3f>();
      List<Point3f> vertexNormals = new ArrayList<Point3f>();
      List<Face> faces = new ArrayList<Face>();

      StringTokenizer fileTokenizer = new StringTokenizer(file, "\n\r");
      while (fileTokenizer.hasMoreElements()) {
        String line = fileTokenizer.nextToken().trim();
        StringTokenizer lineTokenizer = new StringTokenizer(line, " ");

        try {
          if (lineTokenizer.hasMoreElements()) {
            String key = lineTokenizer.nextToken();
            if ("v".equals(key)) {
              String x = lineTokenizer.nextToken();
              String y = lineTokenizer.nextToken();
              String z = lineTokenizer.nextToken();
              Point3f point = new Point3f(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z));
              vertices.add(point);
            }
            else if ("vn".equals(key)) {
              String x = lineTokenizer.nextToken();
              String y = lineTokenizer.nextToken();
              String z = lineTokenizer.nextToken();
              Point3f point = new Point3f(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z));
              vertexNormals.add(point);
            }
            else if ("f".equals(key)) {
              List<String> pointIdxList = new ArrayList<String>();
              while (lineTokenizer.hasMoreElements()) {
                pointIdxList.add(lineTokenizer.nextToken());
              }
              if (pointIdxList.size() == 3) {
                String a = stripTextureIndex(pointIdxList.get(0));
                String b = stripTextureIndex(pointIdxList.get(1));
                String c = stripTextureIndex(pointIdxList.get(2));
                Face face = new Face(Integer.parseInt(a) - 1, Integer.parseInt(b) - 1, Integer.parseInt(c) - 1);
                faces.add(face);
              }
            }
          }
        }
        catch (Exception ex) {
          throw new RuntimeException("Error parsing line <" + line + ">");
        }
      }

      /*
      long t1 = System.currentTimeMillis();
      System.out.println("LOAD MESH: " + (t1 - t0) / 1000.0 + "s");
      System.out.println("  VERTICES: " + vertices.size());
      System.out.println("  NORMALS: " + vertexNormals.size());
      System.out.println("  FACES: " + faces.size());
      */
      return new Mesh(vertices, vertexNormals.size() == vertices.size() && vertexNormals.size() > 0 ? vertexNormals : null, faces);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }

  }

  private static String stripTextureIndex(String pFaceIndex) {
    int p = pFaceIndex.indexOf("/");
    return p > 0 ? pFaceIndex.substring(0, p) : pFaceIndex;
  }
}
