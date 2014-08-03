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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.meshgen.marchingcubes.Face;
import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point;

public class SimpleWavefrontObjLoader {

  public static Mesh readMesh(String pFilename) {
    try {
      long t0 = System.currentTimeMillis();
      String file = Tools.readUTF8Textfile(pFilename);

      List<Point> points = new ArrayList<Point>();
      Set<Face> faces = new HashSet<Face>();

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
              Point point = new Point(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z));
              points.add(point);
            }
            else if ("f".equals(key)) {
              List<String> pointIdxList = new ArrayList<String>();
              while (lineTokenizer.hasMoreElements()) {
                pointIdxList.add(lineTokenizer.nextToken());
              }
              if (pointIdxList.size() == 3) {
                Face face = new Face(Integer.parseInt(pointIdxList.get(0)) - 1, Integer.parseInt(pointIdxList.get(1)) - 1,
                    Integer.parseInt(pointIdxList.get(2)) - 1);
                faces.add(face);
              }
            }
          }
        }
        catch (Exception ex) {
          throw new RuntimeException("Error parsing line <" + line + ">");
        }
      }

      long t1 = System.currentTimeMillis();
      System.out.println("LOAD MESH: " + (t1 - t0) / 1000.0 + "s");
      System.out.println("  POINTS: " + points.size());
      System.out.println("  FACES: " + faces.size());

      return new Mesh(points, faces);
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }

  }
}
