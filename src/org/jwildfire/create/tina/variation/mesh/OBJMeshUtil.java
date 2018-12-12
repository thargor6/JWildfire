/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
package org.jwildfire.create.tina.variation.mesh;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.parser.Parse;
import org.jwildfire.base.Tools;

import java.io.*;
import java.util.ArrayList;

public class OBJMeshUtil {
  public static SimpleMesh createDfltMesh() {
    SimpleMesh mesh = new SimpleMesh();
    int v0 = mesh.addVertex(-1.0, -1.0, 1.0);
    int v1 = mesh.addVertex(1.0, -1.0, 1.0);
    int v2 = mesh.addVertex(1.0, 1.0, 1.0);
    int v3 = mesh.addVertex(-1.0, 1.0, 1.0);
    int v4 = mesh.addVertex(-1.0, -1.0, -1.0);
    int v5 = mesh.addVertex(1.0, -1.0, -1.0);
    int v6 = mesh.addVertex(1.0, 1.0, -1.0);
    int v7 = mesh.addVertex(-1.0, 1.0, -1.0);
    mesh.addFace(v0, v1, v2, v3);
    mesh.addFace(v1, v5, v6, v2);
    mesh.addFace(v5, v6, v7, v4);
    mesh.addFace(v4, v7, v3, v0);
    mesh.addFace(v3, v2, v6, v7);
    mesh.addFace(v0, v1, v5, v4);
    return mesh;
  }

  public static SimpleMesh loadAndSmoothMeshFromFile(String pFilename, int subdiv_smooth_passes, int subdiv_level, double subdiv_smooth_lambda, double subdiv_smooth_mu) throws Exception {
    Build builder = new Build();
    /*    Parse obj =*/
    new Parse(builder, pFilename);

    SimpleMesh mesh = new SimpleMesh();
    for (com.owens.oobjloader.builder.Face face : builder.faces) {
      ArrayList<com.owens.oobjloader.builder.FaceVertex> vertices = face.vertices;

      if (vertices.size() == 3) {
        com.owens.oobjloader.builder.FaceVertex f1 = vertices.get(0);
        com.owens.oobjloader.builder.FaceVertex f2 = vertices.get(1);
        com.owens.oobjloader.builder.FaceVertex f3 = vertices.get(2);
        int v0, v1, v2;
        if (f1.t != null && f2.t != null && f3.t != null) {
          v0 = mesh.addVertex(f1.v.x, f1.v.y, f1.v.z, f1.t.u, f1.t.v);
          v1 = mesh.addVertex(f2.v.x, f2.v.y, f2.v.z, f2.t.u, f2.t.v);
          v2 = mesh.addVertex(f3.v.x, f3.v.y, f3.v.z, f3.t.u, f3.t.v);
        } else {
          v0 = mesh.addVertex(f1.v.x, f1.v.y, f1.v.z);
          v1 = mesh.addVertex(f2.v.x, f2.v.y, f2.v.z);
          v2 = mesh.addVertex(f3.v.x, f3.v.y, f3.v.z);
        }
        mesh.addFace(v0, v1, v2);
      } else if (vertices.size() == 4) {
        com.owens.oobjloader.builder.FaceVertex f1 = vertices.get(0);
        com.owens.oobjloader.builder.FaceVertex f2 = vertices.get(1);
        com.owens.oobjloader.builder.FaceVertex f3 = vertices.get(2);
        com.owens.oobjloader.builder.FaceVertex f4 = vertices.get(3);
        int v0, v1, v2, v3;
        if (f1.t != null && f2.t != null && f3.t != null && f4.t != null) {
          v0 = mesh.addVertex(f1.v.x, f1.v.y, f1.v.z, f1.t.u, f1.t.v);
          v1 = mesh.addVertex(f2.v.x, f2.v.y, f2.v.z, f2.t.u, f2.t.v);
          v2 = mesh.addVertex(f3.v.x, f3.v.y, f3.v.z, f3.t.u, f3.t.v);
          v3 = mesh.addVertex(f4.v.x, f4.v.y, f4.v.z, f4.t.u, f4.t.v);
        } else {
          v0 = mesh.addVertex(f1.v.x, f1.v.y, f1.v.z);
          v1 = mesh.addVertex(f2.v.x, f2.v.y, f2.v.z);
          v2 = mesh.addVertex(f3.v.x, f3.v.y, f3.v.z);
          v3 = mesh.addVertex(f4.v.x, f4.v.y, f4.v.z);
        }
        mesh.addFace(v0, v1, v2);
        mesh.addFace(v0, v2, v3);
      }
    }
    if (subdiv_level > 0) {
      for (int i = 0; i < subdiv_level; i++) {
        mesh = mesh.interpolate();
        mesh.taubinSmooth(subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu);
      }
    } else {
      mesh.distributeFaces();
    }
    return mesh;
  }

  public static File obj2file(InputStream in) throws Exception {
    final File tmpFile = File.createTempFile(Tools.APP_TITLE, ".obj");
    tmpFile.deleteOnExit();
    try (BufferedInputStream bin = new BufferedInputStream(in)) {
      try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmpFile))) {
        int c;
        while ((c = bin.read()) != -1) {
          out.write(c);
        }
      }
    }
    return tmpFile;
  }

  public static String getMeshname(String prefix, int subdiv_level, int subdiv_smooth_passes, double subdiv_smooth_lambda, double subdiv_smooth_mu) {
    String res = prefix + "#" + subdiv_level;
    if (subdiv_level > 0) {
      res += "#" + subdiv_smooth_passes;
      if (subdiv_smooth_passes > 0) {
        res += "#" + subdiv_smooth_lambda + "#" + subdiv_smooth_mu;
      }
    }
    return res;
  }

}
