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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
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

  protected static final String PARAM_SUBDIV_LEVEL = "subdiv_level";
  protected static final String PARAM_SUBDIV_SMOOTH_PASSES = "subdiv_smooth_passes";
  protected static final String PARAM_SUBDIV_SMOOTH_LAMBDA = "subdiv_smooth_lambda";
  protected static final String PARAM_SUBDIV_SMOOTH_MU = "subdiv_smooth_mu";

  private static final String[] paramNames = { PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_SUBDIV_LEVEL, PARAM_SUBDIV_SMOOTH_PASSES, PARAM_SUBDIV_SMOOTH_LAMBDA, PARAM_SUBDIV_SMOOTH_MU };

  protected double scaleX = 1.0;
  protected double scaleY = 1.0;
  protected double scaleZ = 1.0;
  protected double offsetX = 0.0;
  protected double offsetY = 0.0;
  protected double offsetZ = 0.0;

  protected int subdiv_level = 0;
  protected int subdiv_smooth_passes = 12;
  protected double subdiv_smooth_lambda = 0.42;
  protected double subdiv_smooth_mu = -0.45;

  protected SimpleMesh mesh;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    Face f = mesh.getFace(pContext.random(mesh.getFaceCount()));
    Vertex p1 = transform(mesh.getVertex(f.v1));
    Vertex p2 = transform(mesh.getVertex(f.v2));
    Vertex p3 = transform(mesh.getVertex(f.v3));

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

  private Vertex transform(Vertex p) {
    Vertex res = new Vertex();
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
    return new Object[] { scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, subdiv_level, subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu };
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
    else if (PARAM_SUBDIV_LEVEL.equalsIgnoreCase(pName))
      subdiv_level = limitIntVal(Tools.FTOI(pValue), 0, 6);
    else if (PARAM_SUBDIV_SMOOTH_PASSES.equalsIgnoreCase(pName))
      subdiv_smooth_passes = limitIntVal(Tools.FTOI(pValue), 0, 24);
    else if (PARAM_SUBDIV_SMOOTH_LAMBDA.equalsIgnoreCase(pName))
      subdiv_smooth_lambda = pValue;
    else if (PARAM_SUBDIV_SMOOTH_MU.equalsIgnoreCase(pName))
      subdiv_smooth_mu = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  protected SimpleMesh createDfltMesh() {
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

  protected SimpleMesh loadMeshFromFile(String pFilename) throws Exception {
    Build builder = new Build();
    /*Parse obj =*/new Parse(builder, pFilename);

    SimpleMesh mesh = new SimpleMesh();
    for (com.owens.oobjloader.builder.Face face : builder.faces) {
      ArrayList<com.owens.oobjloader.builder.FaceVertex> vertices = face.vertices;
      if (vertices.size() == 3) {
        com.owens.oobjloader.builder.FaceVertex f1 = vertices.get(0);
        com.owens.oobjloader.builder.FaceVertex f2 = vertices.get(1);
        com.owens.oobjloader.builder.FaceVertex f3 = vertices.get(2);
        int v0 = mesh.addVertex(f1.v.x, f1.v.y, f1.v.z);
        int v1 = mesh.addVertex(f2.v.x, f2.v.y, f2.v.z);
        int v2 = mesh.addVertex(f3.v.x, f3.v.y, f3.v.z);
        mesh.addFace(v0, v1, v2);
      }
      else if (vertices.size() == 4) {
        com.owens.oobjloader.builder.FaceVertex f1 = vertices.get(0);
        com.owens.oobjloader.builder.FaceVertex f2 = vertices.get(1);
        com.owens.oobjloader.builder.FaceVertex f3 = vertices.get(2);
        com.owens.oobjloader.builder.FaceVertex f4 = vertices.get(3);
        int v0 = mesh.addVertex(f1.v.x, f1.v.y, f1.v.z);
        int v1 = mesh.addVertex(f2.v.x, f2.v.y, f2.v.z);
        int v2 = mesh.addVertex(f3.v.x, f3.v.y, f3.v.z);
        int v3 = mesh.addVertex(f4.v.x, f4.v.y, f4.v.z);
        mesh.addFace(v0, v1, v2);
        mesh.addFace(v0, v2, v3);
      }
    }
    if (subdiv_level > 0) {
      for (int i = 0; i < subdiv_level; i++) {
        mesh = mesh.interpolate();
        mesh.taubinSmooth(subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu);
      }
    }
    else {
      mesh.distributeFaces();
    }
    return mesh;
  }

  protected String getMeshname(String prefix) {
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
