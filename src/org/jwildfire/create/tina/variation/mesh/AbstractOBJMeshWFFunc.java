/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.VariationFunc;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.parser.Parse;

public abstract class AbstractOBJMeshWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  public static final String PARAM_SCALEX = "scale_x";
  public static final String PARAM_SCALEY = "scale_y";
  protected static final String PARAM_SCALEZ = "scale_z";
  protected static final String PARAM_OFFSETX = "offset_x";
  protected static final String PARAM_OFFSETY = "offset_y";
  protected static final String PARAM_OFFSETZ = "offset_z";

  private static final String[] paramNames = { PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ };

  protected double scaleX = 1.0;
  protected double scaleY = 1.0;
  protected double scaleZ = 1.0;
  protected double offsetX = 0.0;
  protected double offsetY = 0.0;
  protected double offsetZ = 0.0;

  protected SimpleMesh mesh;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    Face f = mesh.getFace(pContext.random(mesh.getFaceCount()));
    Point p1 = transform(mesh.getPoint(f.p1));
    Point p2 = transform(mesh.getPoint(f.p2));
    Point p3 = transform(mesh.getPoint(f.p3));

    // uniform sampling:  http://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
    double sqrt_r1 = MathLib.sqrt(pContext.random());
    double r2 = pContext.random();
    double a = 1.0 - sqrt_r1;
    double b = sqrt_r1 * (1.0 - r2);
    double c = r2 * sqrt_r1;
    double dx = a * p1.x + b * p2.x + c * p3.x;
    double dy = a * p1.y + b * p2.y + c * p3.y;
    double dz = a * p1.z + b * p2.z + c * p3.z;

    pVarTP.x += pAmount * dx;
    pVarTP.y += pAmount * dy;
    pVarTP.z += pAmount * dz;
  }

  private Point transform(Point p) {
    Point res = new Point();
    res.x = (float) (p.x * scaleX + offsetX);
    res.y = (float) (p.y * scaleY + offsetY);
    res.z = (float) (p.z * scaleZ + offsetZ);
    return res;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scaleX = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaleY = pValue;
    else if (PARAM_SCALEZ.equalsIgnoreCase(pName))
      scaleZ = pValue;
    else if (PARAM_OFFSETX.equalsIgnoreCase(pName))
      offsetX = pValue;
    else if (PARAM_OFFSETY.equalsIgnoreCase(pName))
      offsetY = pValue;
    else if (PARAM_OFFSETZ.equalsIgnoreCase(pName))
      offsetZ = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  static class Point {
    float x, y, z;
  }

  static class Face {
    int p1, p2, p3;

    public Face() {

    }

    public Face(int p1, int p2, int p3) {
      this.p1 = p1;
      this.p2 = p2;
      this.p3 = p3;
    }
  }

  static class SimpleMesh {
    private List<Point> points = new ArrayList<>();
    private List<Face> faces = new ArrayList<>();

    public void addPoint(double x, double y, double z) {
      Point p = new Point();
      p.x = (float) x;
      p.y = (float) y;
      p.z = (float) z;
      points.add(p);
    }

    public void addFace(int p1, int p2, int p3) {
      Face f = new Face();
      f.p1 = p1;
      f.p2 = p2;
      f.p3 = p3;
      faces.add(f);
    }

    public void addFace(int p1, int p2, int p3, int p4) {
      Face f1 = new Face();
      f1.p1 = p1;
      f1.p2 = p2;
      f1.p3 = p3;
      faces.add(f1);
      Face f2 = new Face();
      f2.p1 = p1;
      f2.p2 = p3;
      f2.p3 = p4;
      faces.add(f2);
    }

    public int getFaceCount() {
      return faces.size();
    }

    public Face getFace(int idx) {
      return faces.get(idx);
    }

    public Point getPoint(int idx) {
      return points.get(idx);
    }

    public void distributeFaces() {
      List<Double> areaLst = new ArrayList<>();
      double areaMin = Double.MAX_VALUE, areaMax = 0.0;
      for (Face face : faces) {
        Point p1 = getPoint(face.p1);
        Point p2 = getPoint(face.p2);
        Point p3 = getPoint(face.p3);
        VectorD a = new VectorD(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        VectorD b = new VectorD(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);
        double area = VectorD.cross(a, b).length();

        areaLst.add(area);
        if (area < areaMin) {
          areaMin = area;
        }
        if (area > areaMax) {
          areaMax = area;
        }
      }
      if (MathLib.fabs(areaMin - areaMax) > MathLib.EPSILON) {
        int maxFaces = faces.size() < 10000 ? 10000 : 1000;
        List<Face> newFaces = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
          Face face = faces.get(i);
          int count = Math.min(Tools.FTOI(areaLst.get(i) / areaMin), maxFaces);
          for (int j = 0; j < count; j++) {
            newFaces.add(face);
            //newFaces.add(new Face(face.p1, face.p3, face.p2));
          }
        }
        faces = newFaces;
      }
    }
  }

  protected SimpleMesh createDfltMesh() {
    SimpleMesh mesh = new SimpleMesh();
    mesh.addPoint(-1.0, -1.0, 1.0);
    mesh.addPoint(1.0, -1.0, 1.0);
    mesh.addPoint(1.0, 1.0, 1.0);
    mesh.addPoint(-1.0, 1.0, 1.0);
    mesh.addPoint(-1.0, -1.0, -1.0);
    mesh.addPoint(1.0, -1.0, -1.0);
    mesh.addPoint(1.0, 1.0, -1.0);
    mesh.addPoint(-1.0, 1.0, -1.0);
    mesh.addFace(0, 1, 2, 3);
    mesh.addFace(1, 5, 6, 2);
    mesh.addFace(5, 6, 7, 4);
    mesh.addFace(4, 7, 3, 0);
    mesh.addFace(3, 2, 6, 7);
    mesh.addFace(0, 1, 5, 4);
    return mesh;
  }

  protected SimpleMesh loadMeshFromFile(String pFilename) throws Exception {
    Build builder = new Build();
    /*Parse obj =*/new Parse(builder, pFilename);

    SimpleMesh mesh = new SimpleMesh();
    int idx = 0;
    // not optimized yet, creates vertices per face, but affects only memory-consumption, not speed 
    for (com.owens.oobjloader.builder.Face face : builder.faces) {
      ArrayList<com.owens.oobjloader.builder.FaceVertex> vertices = face.vertices;
      if (vertices.size() == 3) {
        com.owens.oobjloader.builder.FaceVertex f1 = vertices.get(0);
        com.owens.oobjloader.builder.FaceVertex f2 = vertices.get(1);
        com.owens.oobjloader.builder.FaceVertex f3 = vertices.get(2);
        mesh.addPoint(f1.v.x, f1.v.y, f1.v.z);
        mesh.addPoint(f2.v.x, f2.v.y, f2.v.z);
        mesh.addPoint(f3.v.x, f3.v.y, f3.v.z);
        mesh.addFace(idx, idx + 1, idx + 2);
        idx += 3;
      }
      else if (vertices.size() == 4) {
        com.owens.oobjloader.builder.FaceVertex f1 = vertices.get(0);
        com.owens.oobjloader.builder.FaceVertex f2 = vertices.get(1);
        com.owens.oobjloader.builder.FaceVertex f3 = vertices.get(2);
        com.owens.oobjloader.builder.FaceVertex f4 = vertices.get(3);
        mesh.addPoint(f1.v.x, f1.v.y, f1.v.z);
        mesh.addPoint(f2.v.x, f2.v.y, f2.v.z);
        mesh.addPoint(f3.v.x, f3.v.y, f3.v.z);
        mesh.addPoint(f4.v.x, f4.v.y, f4.v.z);
        mesh.addFace(idx, idx + 1, idx + 2);
        mesh.addFace(idx, idx + 2, idx + 3);
        idx += 4;
      }
    }
    mesh.distributeFaces();
    return mesh;
  }

}
