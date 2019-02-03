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
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.base.mathlib.VecMathLib.Matrix3D;
import org.jwildfire.base.mathlib.VecMathLib.VectorD;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.create.tina.variation.mesh.*;
import org.jwildfire.create.tina.variation.mesh.SimpleMesh.BoundingBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.sinAndCos;

public class DLA3DWFFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_MAX_ITER = "max_iter";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_INNER_BLUR_RADIUS = "inner_blur_radius";
  private static final String PARAM_OUTER_BLUR_RADIUS = "outer_blur_radius";
  private static final String PARAM_JUNCTION_SCALE = "junction_scale";
  private static final String PARAM_DC_COLOR = "dc_color";
  private static final String PARAM_GLUE_RADIUS = "glue_radius";
  private static final String PARAM_FORCE_X = "force_x";
  private static final String PARAM_FORCE_Y = "force_y";
  private static final String PARAM_FORCE_Z = "force_z";
  private static final String PARAM_DISPLAY_NODES = "display_nodes";
  private static final String PARAM_DISPLAY_JUNCTIONS = "display_junctions";
  private static final String PARAM_SINGLE_THREAD = "single_thread";

  private static final String PARAM_NODE_SDIV_LEVEL = "node_sdiv_level";
  private static final String PARAM_NODE_SDIV_SMOOTH_PASSES = "node_sdiv_smooth_passes";
  private static final String PARAM_NODE_SDIV_SMOOTH_LAMBDA = "node_sdiv_smooth_lambda";
  private static final String PARAM_NODE_SDIV_SMOOTH_MU = "node_sdiv_smooth_mu";
  private static final String PARAM_NODE_BLEND_COLORMAP = "node_blend_colormap";
  private static final String PARAM_NODE_MESH_SCALE = "node_mesh_scale";

  private static final String PARAM_JUNCT_SDIV_LEVEL = "junct_sdiv_level";
  private static final String PARAM_JUNCT_SDIV_SMOOTH_PASSES = "junct_sdiv_smooth_passes";
  private static final String PARAM_JUNCT_SDIV_SMOOTH_LAMBDA = "junct_sdiv_smooth_lambda";
  private static final String PARAM_JUNCT_SDIV_SMOOTH_MU = "junct_sdiv_smooth_mu";
  private static final String PARAM_JUNCT_BLEND_COLORMAP = "junct_blend_colormap";
  private static final String PARAM_JUNCT_MESH_SCALE = "junct_mesh_scale";

  private static final String RESSOURCE_NODE_OBJ_FILENAME = "node_obj_filename";
  private static final String RESSOURCE_NODE_COLORMAP_FILENAME = "node_colormap_filename";
  private static final String RESSOURCE_JUNCT_OBJ_FILENAME = "junct_obj_filename";
  private static final String RESSOURCE_JUNCT_COLORMAP_FILENAME = "junct_colormap_filename";

  private static final String[] paramNames = {PARAM_MAX_ITER, PARAM_SEED, PARAM_INNER_BLUR_RADIUS, PARAM_OUTER_BLUR_RADIUS, PARAM_JUNCTION_SCALE,
          PARAM_DC_COLOR, PARAM_GLUE_RADIUS, PARAM_FORCE_X, PARAM_FORCE_Y, PARAM_FORCE_Z, PARAM_DISPLAY_NODES,
          PARAM_DISPLAY_JUNCTIONS, PARAM_SINGLE_THREAD,
          PARAM_NODE_SDIV_LEVEL, PARAM_NODE_SDIV_SMOOTH_PASSES, PARAM_NODE_SDIV_SMOOTH_LAMBDA, PARAM_NODE_SDIV_SMOOTH_MU, PARAM_NODE_BLEND_COLORMAP, PARAM_NODE_MESH_SCALE,
          PARAM_JUNCT_SDIV_LEVEL, PARAM_JUNCT_SDIV_SMOOTH_PASSES, PARAM_JUNCT_SDIV_SMOOTH_LAMBDA, PARAM_JUNCT_SDIV_SMOOTH_MU, PARAM_JUNCT_BLEND_COLORMAP, PARAM_JUNCT_MESH_SCALE};

  private static final String[] ressourceNames = {RESSOURCE_NODE_OBJ_FILENAME, RESSOURCE_NODE_COLORMAP_FILENAME, RESSOURCE_JUNCT_OBJ_FILENAME, RESSOURCE_JUNCT_COLORMAP_FILENAME};

  private int max_iter = 320;
  private int seed = (int) (Math.random() * 10000);
  private double inner_blur_radius = 0.5;
  private double outer_blur_radius = 0.01;
  private double junction_scale = 1.4;
  private int dc_color = 1;
  private double glue_radius = 1.0;
  private double force_x = 0.0;
  private double force_y = 0.0;
  private double force_z = 0.0;
  private int display_nodes = 1;
  private int display_junctions = 1;
  private int single_thread = 0;

  protected int node_sdiv_level = 0;
  protected int node_sdiv_smooth_passes = 12;
  protected double node_sdiv_smooth_lambda = 0.42;
  protected double node_sdiv_smooth_mu = -0.45;
  protected double node_mesh_scale = 1.0;

  protected int junct_sdiv_level = 0;
  protected int junct_sdiv_smooth_passes = 12;
  protected double junct_sdiv_smooth_lambda = 0.42;
  protected double junct_sdiv_smooth_mu = -0.45;
  protected double junct_mesh_scale = 1.0;

  private SimpleMesh nodeMesh;
  private String nodeObjFilename = null;
  protected ColorMapHolder nodeColorMapHolder = new ColorMapHolder();
  protected UVColorMapper nodeUVColorMapper = new UVColorMapper();

  private SimpleMesh junctMesh;
  private String junctObjFilename = null;
  protected ColorMapHolder junctColorMapHolder = new ColorMapHolder();
  protected UVColorMapper junctUVColorMapper = new UVColorMapper();

  private class Sample {
    double x, y, z;
    double color;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    DLA3DWFFuncPoint point = getRandomPoint();
    double rnd = _randGen.random();

    boolean doBlur = outer_blur_radius > MathLib.EPSILON;

    Sample sample = null;
    if (point.parent == null) {
      if (display_nodes > 0 && doBlur)
        sample = displayCentreNode(point);
    } else {
      if (doBlur) {
        double minRadius, maxRadius;
        {
          double fromDepth = point.relDepth;
          double toDepth = point.parent.relDepth;

          double dRadius = outer_blur_radius - inner_blur_radius;

          minRadius = inner_blur_radius + dRadius * fromDepth;
          maxRadius = inner_blur_radius + dRadius * toDepth;
        }

        if (display_junctions > 0 && display_nodes > 0) {
          double shape = _randGen.random();
          if (shape < 0.333) {
            sample = displayOuterNode(point, minRadius);
          } else {
            sample = displayJunction(point, minRadius, maxRadius, rnd);
          }
        } else if (display_junctions > 0) {
          sample = displayJunction(point, minRadius, maxRadius, rnd);
        } else if (display_nodes > 0) {
          sample = displayOuterNode(point, maxRadius);
        }
      } else {
        sample = displayUnblurredPart(point, rnd);
      }
    }

    if (sample != null) {
      pVarTP.doHide = false;
      pVarTP.x += pAmount * sample.x;
      pVarTP.y += pAmount * sample.y;
      pVarTP.z += pAmount * sample.z;
      if (dc_color > 0) {
        pVarTP.color = Math.max(0.0, Math.min(1.0, sample.color));
      }
    } else {
      pVarTP.doHide = true;
    }
  }

  private Sample displayJunction(DLA3DWFFuncPoint point, double minRadius, double maxRadius, double rnd) {
    Sample sample = new Sample();
    double radius = minRadius + (maxRadius - minRadius) * rnd;
    double defaultColor = point.relDepth + (point.parent.relDepth - point.relDepth) * rnd;

    if (junctMesh == null || junctMesh.getFaceCount() == 0) {
      sample.x = point.x + (point.parent.x - point.x) * rnd;
      sample.y = point.y + (point.parent.y - point.y) * rnd;
      sample.z = point.z + (point.parent.z - point.z) * rnd;

      sample.color = defaultColor;

      double ax = (point.parent.x - point.x);
      double ay = (point.parent.y - point.y);
      double az = (point.parent.z - point.z);

      int iter = 0;
      while (true && iter++ < 10) {
        double bx = 0.5 - _randGen.random();
        double by = 0.5 - _randGen.random();
        double bz = 0.5 - _randGen.random();

        double cx = ay * bz - az * by;
        double cy = az * bx - ax * bz;
        double cz = ax * by - ay * bx;
        double r = MathLib.sqrt(cx * cx + cy * cy + cz * cz);
        if (r > 0.001) {
          sample.x += cx / r * radius;
          sample.y += cy / r * radius;
          sample.z += cz / r * radius;
          break;
        }
      }

    } else {
      sample = getRawSampleFromJunctMesh(radius, defaultColor, point.rotation);
      sample.x += point.x + (point.parent.x - point.x) * 0.5;
      sample.y += point.y + (point.parent.y - point.y) * 0.5;
      sample.z += point.z + (point.parent.z - point.z) * 0.5;
    }

    return sample;
  }

  private Sample displayCentreNode(DLA3DWFFuncPoint point) {
    Sample sample = new Sample();
    double radius = inner_blur_radius * junction_scale;
    if (nodeMesh == null || nodeMesh.getFaceCount() == 0) {
      double phi = _randGen.random() * (M_PI + M_PI);
      sinAndCos(phi, sinPhi, cosPhi);
      double theta = _randGen.random() * (M_PI + M_PI);
      sinAndCos(theta, sinTheta, cosTheta);

      sample.x = point.x + radius * sinTheta.value * cosPhi.value;
      sample.y = point.y + radius * sinTheta.value * sinPhi.value;
      sample.z = point.z + radius * cosTheta.value;
      sample.color = 0.0;
    } else {
      sample = getRawSampleFromNodeMesh(radius, 0.0, null);
      sample.x += point.x;
      sample.y += point.y;
      sample.z += point.z;
    }
    return sample;
  }

  private Sample displayOuterNode(DLA3DWFFuncPoint point, double minRadius) {
    Sample sample = new Sample();
    double radius = minRadius * junction_scale;
    if (nodeMesh == null || nodeMesh.getFaceCount() == 0) {
      double phi = _randGen.random() * (M_PI + M_PI);
      sinAndCos(phi, sinPhi, cosPhi);
      double theta = _randGen.random() * (M_PI + M_PI);
      sinAndCos(theta, sinTheta, cosTheta);

      sample.x = point.x + radius * sinTheta.value * cosPhi.value;
      sample.y = point.y + radius * sinTheta.value * sinPhi.value;
      sample.z = point.z + radius * cosTheta.value;
      sample.color = point.relDepth;
    } else {
      sample = getRawSampleFromNodeMesh(radius, point.relDepth, point.rotation);
      sample.x += point.x;
      sample.y += point.y;
      sample.z += point.z;
    }
    return sample;
  }

  private Sample getRawSampleFromNodeMesh(double scale, double defaultColor, Matrix3D rotation) {
    Sample sample = new Sample();
    Face f = nodeMesh.getFace(_randGen.random(nodeMesh.getFaceCount()));
    Vertex rawP1 = nodeMesh.getVertex(f.v1);
    Vertex rawP2 = nodeMesh.getVertex(f.v2);
    Vertex rawP3 = nodeMesh.getVertex(f.v3);
    if (nodeColorMapHolder.isActive() && rawP1 instanceof VertexWithUV) {
      VertexWithUV p1 = nodeTransform((VertexWithUV) rawP1, node_mesh_scale, scale, nodeMesh.getBoundingBox(), rotation);
      VertexWithUV p2 = nodeTransform((VertexWithUV) rawP2, node_mesh_scale, scale, nodeMesh.getBoundingBox(), rotation);
      VertexWithUV p3 = nodeTransform((VertexWithUV) rawP3, node_mesh_scale, scale, nodeMesh.getBoundingBox(), rotation);

      // uniform sampling:  http://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
      double sqrt_r1 = MathLib.sqrt(_randGen.random());
      double r2 = _randGen.random();
      double a = 1.0 - sqrt_r1;
      double b = sqrt_r1 * (1.0 - r2);
      double c = r2 * sqrt_r1;
      double dx = a * p1.x + b * p2.x + c * p3.x;
      double dy = a * p1.y + b * p2.y + c * p3.y;
      double dz = a * p1.z + b * p2.z + c * p3.z;

      sample.x = dx;
      sample.y = dy;
      sample.z = dz;
      sample.color = defaultColor;

      double u = a * p1.u + b * p2.u + c * p3.u;
      double v = a * p1.v + b * p2.v + c * p3.v;

      if (nodeColorMapHolder.isActive()) {
        double iu = GfxMathLib.clamp(u * (nodeColorMapHolder.getColorMapWidth() - 1.0), 0.0, nodeColorMapHolder.getColorMapWidth() - 1.0);
        double iv = GfxMathLib.clamp(nodeColorMapHolder.getColorMapHeight() - 1.0 - v * (nodeColorMapHolder.getColorMapHeight() - 1.0), 0, nodeColorMapHolder.getColorMapHeight() - 1.0);
        int ix = (int) MathLib.trunc(iu);
        int iy = (int) MathLib.trunc(iv);
        XYZPoint colorHolder = new XYZPoint();
        nodeColorMapHolder.applyImageColor(colorHolder, ix, iy, iu, iv);
        sample.color = nodeUVColorMapper.getUVColorIdx(Tools.FTOI(colorHolder.redColor), Tools.FTOI(colorHolder.greenColor), Tools.FTOI(colorHolder.blueColor));
      }
    } else {
      Vertex p1 = nodeTransform(rawP1, node_mesh_scale, scale, nodeMesh.getBoundingBox(), rotation);
      Vertex p2 = nodeTransform(rawP2, node_mesh_scale, scale, nodeMesh.getBoundingBox(), rotation);
      Vertex p3 = nodeTransform(rawP3, node_mesh_scale, scale, nodeMesh.getBoundingBox(), rotation);

      // uniform sampling:  http://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
      double sqrt_r1 = MathLib.sqrt(_randGen.random());
      double r2 = _randGen.random();
      double a = 1.0 - sqrt_r1;
      double b = sqrt_r1 * (1.0 - r2);
      double c = r2 * sqrt_r1;
      double dx = a * p1.x + b * p2.x + c * p3.x;
      double dy = a * p1.y + b * p2.y + c * p3.y;
      double dz = a * p1.z + b * p2.z + c * p3.z;

      sample.x = dx;
      sample.y = dy;
      sample.z = dz;
      sample.color = defaultColor;
    }

    return sample;
  }

  private Sample getRawSampleFromJunctMesh(double scale, double defaultColor, Matrix3D rotation) {
    Sample sample = new Sample();
    Face f = junctMesh.getFace(_randGen.random(junctMesh.getFaceCount()));
    Vertex rawP1 = junctMesh.getVertex(f.v1);
    Vertex rawP2 = junctMesh.getVertex(f.v2);
    Vertex rawP3 = junctMesh.getVertex(f.v3);
    if (junctColorMapHolder.isActive() && rawP1 instanceof VertexWithUV) {
      VertexWithUV p1 = nodeTransform((VertexWithUV) rawP1, junct_mesh_scale, scale, junctMesh.getBoundingBox(), rotation);
      VertexWithUV p2 = nodeTransform((VertexWithUV) rawP2, junct_mesh_scale, scale, junctMesh.getBoundingBox(), rotation);
      VertexWithUV p3 = nodeTransform((VertexWithUV) rawP3, junct_mesh_scale, scale, junctMesh.getBoundingBox(), rotation);

      // uniform sampling:  http://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
      double sqrt_r1 = MathLib.sqrt(_randGen.random());
      double r2 = _randGen.random();
      double a = 1.0 - sqrt_r1;
      double b = sqrt_r1 * (1.0 - r2);
      double c = r2 * sqrt_r1;
      double dx = a * p1.x + b * p2.x + c * p3.x;
      double dy = a * p1.y + b * p2.y + c * p3.y;
      double dz = a * p1.z + b * p2.z + c * p3.z;

      sample.x = dx;
      sample.y = dy;
      sample.z = dz;
      sample.color = defaultColor;

      double u = a * p1.u + b * p2.u + c * p3.u;
      double v = a * p1.v + b * p2.v + c * p3.v;

      if (junctColorMapHolder.isActive()) {
        double iu = GfxMathLib.clamp(u * (junctColorMapHolder.getColorMapWidth() - 1.0), 0.0, junctColorMapHolder.getColorMapWidth() - 1.0);
        double iv = GfxMathLib.clamp(junctColorMapHolder.getColorMapHeight() - 1.0 - v * (junctColorMapHolder.getColorMapHeight() - 1.0), 0, junctColorMapHolder.getColorMapHeight() - 1.0);
        int ix = (int) MathLib.trunc(iu);
        int iy = (int) MathLib.trunc(iv);
        XYZPoint colorHolder = new XYZPoint();
        junctColorMapHolder.applyImageColor(colorHolder, ix, iy, iu, iv);
        sample.color = junctUVColorMapper.getUVColorIdx(Tools.FTOI(colorHolder.redColor), Tools.FTOI(colorHolder.greenColor), Tools.FTOI(colorHolder.blueColor));
      }
    } else {
      Vertex p1 = nodeTransform(rawP1, junct_mesh_scale, scale, junctMesh.getBoundingBox(), rotation);
      Vertex p2 = nodeTransform(rawP2, junct_mesh_scale, scale, junctMesh.getBoundingBox(), rotation);
      Vertex p3 = nodeTransform(rawP3, junct_mesh_scale, scale, junctMesh.getBoundingBox(), rotation);

      // uniform sampling:  http://math.stackexchange.com/questions/18686/uniform-random-point-in-triangle
      double sqrt_r1 = MathLib.sqrt(_randGen.random());
      double r2 = _randGen.random();
      double a = 1.0 - sqrt_r1;
      double b = sqrt_r1 * (1.0 - r2);
      double c = r2 * sqrt_r1;
      double dx = a * p1.x + b * p2.x + c * p3.x;
      double dy = a * p1.y + b * p2.y + c * p3.y;
      double dz = a * p1.z + b * p2.z + c * p3.z;

      sample.x = dx;
      sample.y = dy;
      sample.z = dz;
      sample.color = defaultColor;
    }

    return sample;
  }

  private Vertex nodeTransform(Vertex p, double preScale, double relScale, BoundingBox boundingBox, Matrix3D rotation) {
    Vertex res = new Vertex();
    double px, py, pz;
    if (rotation != null) {
      VectorD d = new VectorD(p.x - boundingBox.getXcentre(), p.y - boundingBox.getYcentre(), p.z - boundingBox.getZcentre());
      VectorD r = Matrix3D.multiply(rotation, d);
      px = r.x;
      py = r.y;
      pz = r.z;
    } else {
      px = p.x;
      py = p.y;
      pz = p.z;
    }
    res.x = (float) (px * preScale * relScale);
    res.y = (float) (py * preScale * relScale);
    res.z = (float) (pz * preScale * relScale);
    return res;
  }

  private VertexWithUV nodeTransform(VertexWithUV p, double preScale, double relScale, BoundingBox boundingBox, Matrix3D rotation) {
    VertexWithUV res = new VertexWithUV();
    double px, py, pz;
    if (rotation != null) {
      VectorD d = new VectorD(p.x - boundingBox.getXcentre(), p.y - boundingBox.getYcentre(), p.z - boundingBox.getZcentre());
      VectorD r = Matrix3D.multiply(rotation, d);
      px = r.x;
      py = r.y;
      pz = r.z;
    } else {
      px = p.x;
      py = p.y;
      pz = p.z;
    }

    res.x = (float) (px * preScale * relScale);
    res.y = (float) (py * preScale * relScale);
    res.z = (float) (pz * preScale * relScale);
    res.u = p.u;
    res.v = p.v;
    return res;
  }

  private Sample displayUnblurredPart(DLA3DWFFuncPoint point, double rnd) {
    Sample sample = new Sample();
    sample.x = point.x + (point.parent.x - point.x) * rnd;
    sample.y = point.y + (point.parent.y - point.y) * rnd;
    sample.z = point.z + (point.parent.z - point.z) * rnd;
    sample.color = point.relDepth + (point.parent.relDepth - point.relDepth) * rnd;
    return sample;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{max_iter, seed, inner_blur_radius, outer_blur_radius, junction_scale, dc_color, glue_radius, force_x, force_y,
            force_z, display_nodes, display_junctions, single_thread,
            node_sdiv_level, node_sdiv_smooth_passes, node_sdiv_smooth_lambda, node_sdiv_smooth_mu, nodeColorMapHolder.getBlend_colormap(), node_mesh_scale,
            junct_sdiv_level, junct_sdiv_smooth_passes, junct_sdiv_smooth_lambda, junct_sdiv_smooth_mu, junctColorMapHolder.getBlend_colormap(), junct_mesh_scale};
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(nodeObjFilename != null ? nodeObjFilename.getBytes() : null),
            (nodeColorMapHolder.getColormap_filename() != null ? nodeColorMapHolder.getColormap_filename().getBytes() : null),
            (junctObjFilename != null ? junctObjFilename.getBytes() : null),
            (junctColorMapHolder.getColormap_filename() != null ? junctColorMapHolder.getColormap_filename().getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_NODE_OBJ_FILENAME.equalsIgnoreCase(pName)) {
      nodeObjFilename = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_NODE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      nodeColorMapHolder.setColormap_filename(pValue != null ? new String(pValue) : "");
      nodeColorMapHolder.clear();
      nodeUVColorMapper.clear();
    } else if (RESSOURCE_JUNCT_OBJ_FILENAME.equalsIgnoreCase(pName)) {
      junctObjFilename = pValue != null ? new String(pValue) : "";
    } else if (RESSOURCE_JUNCT_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      junctColorMapHolder.setColormap_filename(pValue != null ? new String(pValue) : "");
      junctColorMapHolder.clear();
      junctUVColorMapper.clear();
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_NODE_OBJ_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.OBJ_MESH;
    } else if (RESSOURCE_NODE_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else if (RESSOURCE_JUNCT_OBJ_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.OBJ_MESH;
    } else if (RESSOURCE_JUNCT_COLORMAP_FILENAME.equalsIgnoreCase(pName)) {
      return RessourceType.IMAGE_FILENAME;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_JUNCTION_SCALE.equalsIgnoreCase(pName))
      junction_scale = pValue;
    else if (PARAM_MAX_ITER.equalsIgnoreCase(pName))
      max_iter = Tools.FTOI(pValue);
    else if (PARAM_DC_COLOR.equalsIgnoreCase(pName))
      dc_color = Tools.FTOI(pValue);
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = Tools.FTOI(pValue);
    else if (PARAM_INNER_BLUR_RADIUS.equalsIgnoreCase(pName))
      inner_blur_radius = pValue;
    else if (PARAM_OUTER_BLUR_RADIUS.equalsIgnoreCase(pName))
      outer_blur_radius = pValue;
    else if (PARAM_GLUE_RADIUS.equalsIgnoreCase(pName)) {
      glue_radius = pValue;
      if (glue_radius < 0.1) {
        glue_radius = 0.1;
      }
    } else if (PARAM_FORCE_X.equalsIgnoreCase(pName))
      force_x = pValue;
    else if (PARAM_FORCE_Y.equalsIgnoreCase(pName))
      force_y = pValue;
    else if (PARAM_FORCE_Z.equalsIgnoreCase(pName))
      force_z = pValue;
    else if (PARAM_SINGLE_THREAD.equalsIgnoreCase(pName))
      single_thread = Tools.FTOI(pValue);
    else if (PARAM_DISPLAY_NODES.equalsIgnoreCase(pName))
      display_nodes = Tools.FTOI(pValue);
    else if (PARAM_DISPLAY_JUNCTIONS.equalsIgnoreCase(pName))
      display_junctions = Tools.FTOI(pValue);

    else if (PARAM_NODE_SDIV_LEVEL.equalsIgnoreCase(pName))
      node_sdiv_level = limitIntVal(Tools.FTOI(pValue), 0, 6);
    else if (PARAM_NODE_SDIV_SMOOTH_PASSES.equalsIgnoreCase(pName))
      node_sdiv_smooth_passes = limitIntVal(Tools.FTOI(pValue), 0, 24);
    else if (PARAM_NODE_SDIV_SMOOTH_LAMBDA.equalsIgnoreCase(pName))
      node_sdiv_smooth_lambda = pValue;
    else if (PARAM_NODE_SDIV_SMOOTH_MU.equalsIgnoreCase(pName))
      node_sdiv_smooth_mu = pValue;
    else if (PARAM_NODE_BLEND_COLORMAP.equalsIgnoreCase(pName))
      nodeColorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    else if (PARAM_NODE_MESH_SCALE.equalsIgnoreCase(pName))
      node_mesh_scale = pValue;

    else if (PARAM_JUNCT_SDIV_LEVEL.equalsIgnoreCase(pName))
      junct_sdiv_level = limitIntVal(Tools.FTOI(pValue), 0, 6);
    else if (PARAM_JUNCT_SDIV_SMOOTH_PASSES.equalsIgnoreCase(pName))
      junct_sdiv_smooth_passes = limitIntVal(Tools.FTOI(pValue), 0, 24);
    else if (PARAM_JUNCT_SDIV_SMOOTH_LAMBDA.equalsIgnoreCase(pName))
      junct_sdiv_smooth_lambda = pValue;
    else if (PARAM_JUNCT_SDIV_SMOOTH_MU.equalsIgnoreCase(pName))
      junct_sdiv_smooth_mu = pValue;
    else if (PARAM_JUNCT_BLEND_COLORMAP.equalsIgnoreCase(pName))
      junctColorMapHolder.setBlend_colormap(limitIntVal(Tools.FTOI(pValue), 0, 1));
    else if (PARAM_JUNCT_MESH_SCALE.equalsIgnoreCase(pName))
      junct_mesh_scale = pValue;

    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "dla3d_wf";
  }

  private String makeKey() {
    return "dla3d#" + String.valueOf(_max_iter) + "#" + String.valueOf(seed) + "#" + String.valueOf(force_x) + "#" + String.valueOf(force_y) + "#" + String.valueOf(force_z) + "#" + String.valueOf(glue_radius) + "#" + (single_thread > 0 ? 1 : 0);
  }

  private static Map<String, List<DLA3DWFFuncPoint>> cache = new HashMap<>();

  private List<DLA3DWFFuncPoint> getPoints() {
    String key = makeKey();
    List<DLA3DWFFuncPoint> res = cache.get(key);
    if (res == null) {
      //long t0 = System.currentTimeMillis();
      res = new DLA3DWFFuncIterator(_max_iter, seed, glue_radius, force_x, force_y, force_z, single_thread > 0).iterate();
      //long t1 = System.currentTimeMillis();
      //System.out.println("DLA3D(" + res.size() + "): " + (t1 - t0) + " ms");
      cache.put(key, res);
    }
    return res;
  }

  private DLA3DWFFuncPoint getRandomPoint() {
    return _points.get(_randGen.random(_points.size()));
  }

  private int _max_iter;
  private AbstractRandomGenerator _randGen;
  private DoubleWrapperWF sinPhi = new DoubleWrapperWF();
  private DoubleWrapperWF cosPhi = new DoubleWrapperWF();
  private DoubleWrapperWF sinTheta = new DoubleWrapperWF();
  private DoubleWrapperWF cosTheta = new DoubleWrapperWF();
  private List<DLA3DWFFuncPoint> _points;

  @Override
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _max_iter = pContext.isPreview() ? (max_iter < 1000) ? max_iter : 1000 : max_iter;
    if (_max_iter < 0)
      _max_iter = 0;
    _randGen = new MarsagliaRandomGenerator();
    _randGen.randomize(seed);
    // points written to cache
    getPoints();
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    _max_iter = pContext.isPreview() ? (max_iter < 1000) ? max_iter : 1000 : max_iter;
    if (_max_iter < 0)
      _max_iter = 0;
    _randGen = new MarsagliaRandomGenerator();
    _randGen.randomize(seed);

    nodeColorMapHolder.init();
    nodeUVColorMapper.initFromLayer(pContext, pLayer);
    junctColorMapHolder.init();
    junctUVColorMapper.initFromLayer(pContext, pLayer);

    // from cache
    _points = getPoints();

    if (nodeObjFilename != null && nodeObjFilename.length() > 0) {
      try {
        String meshKey = this.getClass().getName() + "_" + OBJMeshUtil.getMeshname(nodeObjFilename, node_sdiv_level, node_sdiv_smooth_passes, node_sdiv_smooth_lambda, node_sdiv_smooth_mu);
        nodeMesh = (SimpleMesh) RessourceManager.getRessource(meshKey);
        if (nodeMesh == null) {
          nodeMesh = OBJMeshUtil.loadAndSmoothMeshFromFile(nodeObjFilename, node_sdiv_smooth_passes, node_sdiv_level, node_sdiv_smooth_lambda, node_sdiv_smooth_mu);
          RessourceManager.putRessource(meshKey, nodeMesh);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (junctObjFilename != null && junctObjFilename.length() > 0) {
      try {
        String meshKey = this.getClass().getName() + "_" + OBJMeshUtil.getMeshname(junctObjFilename, junct_sdiv_level, junct_sdiv_smooth_passes, junct_sdiv_smooth_lambda, junct_sdiv_smooth_mu);
        junctMesh = (SimpleMesh) RessourceManager.getRessource(meshKey);
        if (junctMesh == null) {
          junctMesh = OBJMeshUtil.loadAndSmoothMeshFromFile(junctObjFilename, junct_sdiv_smooth_passes, junct_sdiv_level, junct_sdiv_smooth_lambda, junct_sdiv_smooth_mu);
          RessourceManager.putRessource(meshKey, junctMesh);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
