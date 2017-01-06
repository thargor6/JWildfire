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

import static org.jwildfire.base.mathlib.MathLib.sqrt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.ColorMapHolder;
import org.jwildfire.create.tina.variation.DisplacementMapHolder;
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

  protected static final String PARAM_BLEND_COLORMAP = "blend_colormap";
  protected static final String PARAM_DISPL_AMOUNT = "displ_amount";
  protected static final String PARAM_BLEND_DISPLMAP = "blend_displ_map";

  protected static final String PARAM_RECEIVE_ONLY_SHADOWS = "receive_only_shadows";

  private static final String[] paramNames = { PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_SUBDIV_LEVEL, PARAM_SUBDIV_SMOOTH_PASSES, PARAM_SUBDIV_SMOOTH_LAMBDA, PARAM_SUBDIV_SMOOTH_MU, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_RECEIVE_ONLY_SHADOWS };

  protected static final String RESSOURCE_COLORMAP_FILENAME = "colormap_filename";
  protected static final String RESSOURCE_DISPL_MAP_FILENAME = "displ_map_filename";

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

  protected int receive_only_shadows = 0;

  protected SimpleMesh mesh;

  protected ColorMapHolder colorMapHolder = new ColorMapHolder();
  protected DisplacementMapHolder displacementMapHolder = new DisplacementMapHolder();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (mesh == null || mesh.getFaceCount() == 0) {
      return;
    }
    Face f = mesh.getFace(pContext.random(mesh.getFaceCount()));
    Vertex rawP1 = mesh.getVertex(f.v1);
    Vertex rawP2 = mesh.getVertex(f.v2);
    Vertex rawP3 = mesh.getVertex(f.v3);
    if ((colorMapHolder.isActive() || displacementMapHolder.isActive()) && rawP1 instanceof VertexWithUV) {
      VertexWithUV p1 = transform((VertexWithUV) rawP1);
      VertexWithUV p2 = transform((VertexWithUV) rawP2);
      VertexWithUV p3 = transform((VertexWithUV) rawP3);

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

      double u = a * p1.u + b * p2.u + c * p3.u;
      double v = a * p1.v + b * p2.v + c * p3.v;

      if (colorMapHolder.isActive()) {
        double iu = GfxMathLib.clamp(u * (colorMapHolder.getColorMapWidth() - 1.0), 0.0, colorMapHolder.getColorMapWidth() - 1.0);
        double iv = GfxMathLib.clamp(colorMapHolder.getColorMapHeight() - 1.0 - v * (colorMapHolder.getColorMapHeight() - 1.0), 0, colorMapHolder.getColorMapHeight() - 1.0);
        int ix = (int) MathLib.trunc(iu);
        int iy = (int) MathLib.trunc(iv);
        colorMapHolder.applyImageColor(pVarTP, ix, iy, iu, iv);
        pVarTP.color = getUVColorIdx(Tools.FTOI(pVarTP.redColor), Tools.FTOI(pVarTP.greenColor), Tools.FTOI(pVarTP.blueColor));
      }
      if (displacementMapHolder.isActive()) {
        VectorD av = new VectorD(p2.x - p1.x, p2.y - p1.y, p2.y - p1.y);
        VectorD bv = new VectorD(p3.x - p1.x, p3.y - p1.y, p3.y - p1.y);
        VectorD n = VectorD.cross(av, bv);
        n.normalize();
        double iu = GfxMathLib.clamp(u * (displacementMapHolder.getDisplacementMapWidth() - 1.0), 0.0, displacementMapHolder.getDisplacementMapWidth() - 1.0);
        double iv = GfxMathLib.clamp(displacementMapHolder.getDisplacementMapHeight() - 1.0 - v * (displacementMapHolder.getDisplacementMapHeight() - 1.0), 0, displacementMapHolder.getDisplacementMapHeight() - 1.0);
        int ix = (int) MathLib.trunc(iu);
        int iy = (int) MathLib.trunc(iv);
        double d = displacementMapHolder.calculateImageDisplacement(ix, iy, iu, iv) * _displ_amount;
        pVarTP.x += pAmount * n.x * d;
        pVarTP.y += pAmount * n.y * d;
        pVarTP.z += pAmount * n.z * d;
      }
    }
    else {
      Vertex p1 = transform(rawP1);
      Vertex p2 = transform(rawP2);
      Vertex p3 = transform(rawP3);

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
    if (receive_only_shadows == 1) {
      pVarTP.receiveOnlyShadows = true;
    }
  }

  private Vertex transform(Vertex p) {
    Vertex res = new Vertex();
    res.x = (float) (p.x * scaleX + offsetX);
    res.y = (float) (p.y * scaleY + offsetY);
    res.z = (float) (p.z * scaleZ + offsetZ);
    return res;
  }

  private VertexWithUV transform(VertexWithUV p) {
    VertexWithUV res = new VertexWithUV();
    res.x = (float) (p.x * scaleX + offsetX);
    res.y = (float) (p.y * scaleY + offsetY);
    res.z = (float) (p.z * scaleZ + offsetZ);
    res.u = p.u;
    res.v = p.v;
    return res;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, subdiv_level, subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), receive_only_shadows };
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
    else if (PARAM_BLEND_COLORMAP.equalsIgnoreCase(pName))
      colorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    else if (PARAM_DISPL_AMOUNT.equalsIgnoreCase(pName))
      displacementMapHolder.setDispl_amount(pValue);
    else if (PARAM_BLEND_DISPLMAP.equalsIgnoreCase(pName))
      displacementMapHolder.setBlend_displ_map(limitIntVal(Tools.FTOI(pValue), 0, 1));
    else if (PARAM_RECEIVE_ONLY_SHADOWS.equalsIgnoreCase(pName))
      receive_only_shadows = limitIntVal(Tools.FTOI(pValue), 0, 1);
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
    /*    Parse obj =*/new Parse(builder, pFilename);

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
        }
        else {
          v0 = mesh.addVertex(f1.v.x, f1.v.y, f1.v.z);
          v1 = mesh.addVertex(f2.v.x, f2.v.y, f2.v.z);
          v2 = mesh.addVertex(f3.v.x, f3.v.y, f3.v.z);
        }
        mesh.addFace(v0, v1, v2);
      }
      else if (vertices.size() == 4) {
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
        }
        else {
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
    }
    else {
      mesh.distributeFaces();
    }
    return mesh;
  }

  private RenderColor[] uvColors;
  protected Map<RenderColor, Double> uvIdxMap = new HashMap<RenderColor, Double>();

  private double getUVColorIdx(int pR, int pG, int pB) {
    RenderColor pColor = new RenderColor(pR, pG, pB);
    Double res = uvIdxMap.get(pColor);
    if (res == null) {

      int nearestIdx = 0;
      RenderColor color = uvColors[0];
      double dr, dg, db;
      dr = (color.red - pR);
      dg = (color.green - pG);
      db = (color.blue - pB);
      double nearestDist = sqrt(dr * dr + dg * dg + db * db);
      for (int i = 1; i < uvColors.length; i++) {
        color = uvColors[i];
        dr = (color.red - pR);
        dg = (color.green - pG);
        db = (color.blue - pB);
        double dist = sqrt(dr * dr + dg * dg + db * db);
        if (dist < nearestDist) {
          nearestDist = dist;
          nearestIdx = i;
        }
      }
      res = (double) nearestIdx / (double) (uvColors.length - 1);
      uvIdxMap.put(pColor, res);
    }
    return res;
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

  private double _displ_amount;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    colorMapHolder.init();
    uvColors = pLayer.getPalette().createRenderPalette(pContext.getFlameRenderer().getFlame().getWhiteLevel());
    displacementMapHolder.init();
    _displ_amount = displacementMapHolder.getDispl_amount() / 255.0;
  }

}
