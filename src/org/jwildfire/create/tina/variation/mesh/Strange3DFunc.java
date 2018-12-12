/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

/*************************************************************************************************
 * @author Jesus Sosa
 * @date February 23, 2018
 *
 * Variation: sattractor3D
 *
 * Adapted from JavaScript code found in
 * https://syntopia.github.io/StrangeAttractors/
 *
 *
 * Also I´m using SimpleMesh.java and  WFFuncPresetsStore.java classes by Andreas Maschke
 * included in source code of Java WildFire.
 *************************************************************************************************/

package org.jwildfire.create.tina.variation.mesh;


import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.create.tina.variation.RessourceType;
import org.jwildfire.create.tina.variation.plot.SAttractor3DFormulaEvaluator;
import org.jwildfire.create.tina.variation.plot.SAttractor3DWFFuncPreset;
import org.jwildfire.create.tina.variation.plot.WFFuncPresetsStore;

import java.util.ArrayList;


public class Strange3DFunc extends AbstractOBJMeshWFFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PRESETID = "presetId";
  private static final String PARAM_STEPS = "steps";
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_STEPTIME = "stepTime";
  private static final String PARAM_FACETS = "facets";

  private static final String PARAM_START_X = "start_x";
  private static final String PARAM_START_Y = "start_y";
  private static final String PARAM_START_Z = "start_z";
  protected static final String PARAM_WARMUP = "warmup";

  private static final String PARAM_A = "param_a";
  private static final String PARAM_B = "param_b";
  private static final String PARAM_C = "param_c";
  private static final String PARAM_D = "param_d";
  private static final String PARAM_E = "param_e";
  private static final String PARAM_F = "param_f";
  private static final String PARAM_G = "param_g";
  private static final String PARAM_H = "param_h";


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

  private static final String[] paramNames = {PARAM_PRESETID, PARAM_STEPS, PARAM_RADIUS, PARAM_STEPTIME, PARAM_FACETS, PARAM_START_X, PARAM_START_Y, PARAM_START_Z, PARAM_WARMUP,
          PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G, PARAM_H,
          PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_SUBDIV_LEVEL, PARAM_SUBDIV_SMOOTH_PASSES, PARAM_SUBDIV_SMOOTH_LAMBDA, PARAM_SUBDIV_SMOOTH_MU, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_RECEIVE_ONLY_SHADOWS};

  private static final String RESSOURCE_XFORMULA = "xformula";
  private static final String RESSOURCE_YFORMULA = "yformula";
  private static final String RESSOURCE_ZFORMULA = "zformula";

  private static final String[] ressourceNames = {RESSOURCE_XFORMULA, RESSOURCE_YFORMULA, RESSOURCE_ZFORMULA};

  private String xformula;
  private String yformula;
  private String zformula;

  private SAttractor3DFormulaEvaluator evaluator;

  int preset_id = -1;
  int steps = 1;
  double radius = 0.005;
  double stepTime = 0.05;
  int facets = 3;

  double startx = 0.1;
  double starty = 1.0;
  double startz = 1.0;

  int warmup = 1000;

  double param_a = 1.0;
  double param_b = 1.0;
  double param_c = 1.0;
  double param_d = 1.0;
  double param_e = 1.0;
  double param_f = 1.0;
  double param_g = 1.0;
  double param_h = 1.0;

  @Override
  public Object[] getParameterValues() {
    return new Object[]{preset_id, steps, radius, stepTime, facets, startx, starty, startz, warmup, param_a, param_b, param_c, param_d, param_e, param_f, param_g, param_h, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, subdiv_level, subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), receive_only_shadows};
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][]{(xformula != null ? xformula.getBytes() : null), (yformula != null ? yformula.getBytes() : null), (zformula != null ? zformula.getBytes() : null), (colorMapHolder.getColormap_filename() != null ? colorMapHolder.getColormap_filename().getBytes() : null), (displacementMapHolder.getDispl_map_filename() != null ? displacementMapHolder.getDispl_map_filename().getBytes() : null)};
  }

  @Override
  public void setRessource(String pName, byte[] pValue) {
    if (RESSOURCE_XFORMULA.equalsIgnoreCase(pName)) {
      xformula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    } else if (RESSOURCE_YFORMULA.equalsIgnoreCase(pName)) {
      yformula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    } else if (RESSOURCE_ZFORMULA.equalsIgnoreCase(pName)) {
      zformula = pValue != null ? new String(pValue) : "";
      validatePresetId();
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_XFORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else if (RESSOURCE_YFORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else if (RESSOURCE_ZFORMULA.equalsIgnoreCase(pName)) {
      return RessourceType.BYTEARRAY;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESETID.equalsIgnoreCase(pName)) {
      preset_id = Tools.FTOI(pValue);
      if (preset_id >= 0) {
        refreshFormulaFromPreset(preset_id);
      }
    } else if (PARAM_STEPS.equalsIgnoreCase(pName)) {
      steps = (int) pValue;
    } else if (PARAM_RADIUS.equalsIgnoreCase(pName)) {
      radius = pValue;
    } else if (PARAM_STEPTIME.equalsIgnoreCase(pName)) {
      stepTime = pValue;
    } else if (PARAM_FACETS.equalsIgnoreCase(pName)) {
      facets = (int) pValue;
    } else if (PARAM_START_X.equalsIgnoreCase(pName)) {
      startx = pValue;
    } else if (PARAM_START_Y.equalsIgnoreCase(pName)) {
      starty = pValue;
    } else if (PARAM_START_Z.equalsIgnoreCase(pName)) {
      startz = pValue;
    } else if (PARAM_WARMUP.equalsIgnoreCase(pName)) {
      warmup = (int) pValue;
    } else if (PARAM_A.equalsIgnoreCase(pName))
      param_a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      param_b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      param_c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      param_d = pValue;
    else if (PARAM_E.equalsIgnoreCase(pName))
      param_e = pValue;
    else if (PARAM_F.equalsIgnoreCase(pName))
      param_f = pValue;
    else if (PARAM_G.equalsIgnoreCase(pName))
      param_g = pValue;
    else if (PARAM_H.equalsIgnoreCase(pName))
      param_h = pValue;
    else if (PARAM_SCALEX.equalsIgnoreCase(pName))
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

  @Override
  public String getName() {
    return "sattractor3D";
  }

  private String calculateMeshKey() {
    return xformula + "#" + yformula + "#" + zformula + "#" + steps + "#" + radius + "#" + stepTime + "#" + facets + "#" + startx + "#" + starty + "#" + startz + "#" +
            warmup + "#" + param_a + "#" + param_b + "#" + param_c + "#" + param_d + "#" + param_e + "#" + param_f + "#" + param_g + "#" + param_h;
  }

  private SimpleMesh getMesh() {
    String key = calculateMeshKey();
    SimpleMesh simpleMesh = (SimpleMesh) RessourceManager.getRessource(key);
    if (simpleMesh == null) {
      String code = "import static org.jwildfire.base.mathlib.MathLib.*;\r\n" +
              "\r\n" +
              "  public double evaluateX(double x,double y, double z, double delta_t) {\r\n" +
              "    double pi = M_PI;\r\n" +
              "    double param_a = " + param_a + ";\r\n" +
              "    double param_b = " + param_b + ";\r\n" +
              "    double param_c = " + param_c + ";\r\n" +
              "    double param_d = " + param_d + ";\r\n" +
              "    double param_e = " + param_e + ";\r\n" +
              "    double param_f = " + param_f + ";\r\n" +
              "    double param_g = " + param_g + ";\r\n" +
              "    double param_h = " + param_h + ";\r\n" +
              "    return  x + (" + (xformula != null && !xformula.isEmpty() ? xformula : "0.0") + ")*delta_t" + ";\r\n" +
              "  }\r\n" +
              "  public double evaluateY(double x,double y,double z, double delta_t) {\r\n" +
              "    double pi = M_PI;\r\n" +
              "    double param_a = " + param_a + ";\r\n" +
              "    double param_b = " + param_b + ";\r\n" +
              "    double param_c = " + param_c + ";\r\n" +
              "    double param_d = " + param_d + ";\r\n" +
              "    double param_e = " + param_e + ";\r\n" +
              "    double param_f = " + param_f + ";\r\n" +
              "    double param_g = " + param_g + ";\r\n" +
              "    double param_h = " + param_h + ";\r\n" +
              "    return  y + (" + (yformula != null && !yformula.isEmpty() ? yformula : "0.0") + ")*delta_t" + ";\r\n" +
              "  }\r\n" +
              "  public double evaluateZ(double x,double y, double z, double delta_t) {\r\n" +
              "    double pi = M_PI;\r\n" +
              "    double param_a = " + param_a + ";\r\n" +
              "    double param_b = " + param_b + ";\r\n" +
              "    double param_c = " + param_c + ";\r\n" +
              "    double param_d = " + param_d + ";\r\n" +
              "    double param_e = " + param_e + ";\r\n" +
              "    double param_f = " + param_f + ";\r\n" +
              "    double param_g = " + param_g + ";\r\n" +
              "    double param_h = " + param_h + ";\r\n" +
              "    return  z + (" + (zformula != null && !zformula.isEmpty() ? zformula : "0.0") + ")*delta_t" + ";\r\n" +
              "  }\r\n";
      try {
        evaluator = SAttractor3DFormulaEvaluator.compile(code);
      } catch (Exception e) {
        evaluator = null;
        e.printStackTrace();
        System.out.println(code);
        throw new IllegalArgumentException(e);
      }
      AttractorCurve attractor = new AttractorCurve(steps * 1000, evaluator, radius, stepTime, facets, new Vector3(startx, starty, startz), warmup);
      attractor.build();
      attractor.getGeometry();
      simpleMesh = attractor.getMesh();
      RessourceManager.putRessource(key, simpleMesh);
    }
    return simpleMesh;
  }

  @Override
  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.initOnce(pContext, pLayer, pXForm, pAmount);
    // store result to cache
    getMesh();
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    // get from cache
    mesh = getMesh();
  }


  public Strange3DFunc() {
    super();
    preset_id = WFFuncPresetsStore.getSAttractor3DWFFuncPresets().getRandomPresetId();
    refreshFormulaFromPreset(preset_id);
    String code = "import static org.jwildfire.base.mathlib.MathLib.*;\r\n" +
            "\r\n" +
            "  public double evaluateX(double x,double y, double z, double delta_t) {\r\n" +
            "    double pi = M_PI;\r\n" +
            "    double param_a = " + param_a + ";\r\n" +
            "    double param_b = " + param_b + ";\r\n" +
            "    double param_c = " + param_c + ";\r\n" +
            "    double param_d = " + param_d + ";\r\n" +
            "    double param_e = " + param_e + ";\r\n" +
            "    double param_f = " + param_f + ";\r\n" +
            "    double param_g = " + param_g + ";\r\n" +
            "    double param_h = " + param_h + ";\r\n" +
            "    return  x + (" + (xformula != null && !xformula.isEmpty() ? xformula : "0.0") + ")*delta_t" + ";\r\n" +
            "  }\r\n" +
            "  public double evaluateY(double x,double y,double z, double delta_t) {\r\n" +
            "    double pi = M_PI;\r\n" +
            "    double param_a = " + param_a + ";\r\n" +
            "    double param_b = " + param_b + ";\r\n" +
            "    double param_c = " + param_c + ";\r\n" +
            "    double param_d = " + param_d + ";\r\n" +
            "    double param_e = " + param_e + ";\r\n" +
            "    double param_f = " + param_f + ";\r\n" +
            "    double param_g = " + param_g + ";\r\n" +
            "    double param_h = " + param_h + ";\r\n" +
            "    return  y + (" + (yformula != null && !yformula.isEmpty() ? yformula : "0.0") + ")*delta_t" + ";\r\n" +
            "  }\r\n" +
            "  public double evaluateZ(double x,double y, double z, double delta_t) {\r\n" +
            "    double pi = M_PI;\r\n" +
            "    double param_a = " + param_a + ";\r\n" +
            "    double param_b = " + param_b + ";\r\n" +
            "    double param_c = " + param_c + ";\r\n" +
            "    double param_d = " + param_d + ";\r\n" +
            "    double param_e = " + param_e + ";\r\n" +
            "    double param_f = " + param_f + ";\r\n" +
            "    double param_g = " + param_g + ";\r\n" +
            "    double param_h = " + param_h + ";\r\n" +
            "    return  z + (" + (zformula != null && !zformula.isEmpty() ? zformula : "0.0") + ")*delta_t" + ";\r\n" +
            "  }\r\n";
    try {
      evaluator = SAttractor3DFormulaEvaluator.compile(code);
    } catch (Exception e) {
      evaluator = null;
      e.printStackTrace();
      System.out.println(code);
      throw new IllegalArgumentException(e);
    }
  }

  private void validatePresetId() {
    if (preset_id >= 0) {
      SAttractor3DWFFuncPreset preset = WFFuncPresetsStore.getSAttractor3DWFFuncPresets().getPreset(preset_id);
      if (!preset.getXformula().equals(xformula) || !preset.getYformula().equals(yformula) || !preset.getZformula().equals(zformula)) {
        preset_id = -1;
      }
    }
  }

  private void refreshFormulaFromPreset(int presetId) {
    SAttractor3DWFFuncPreset preset = WFFuncPresetsStore.getSAttractor3DWFFuncPresets().getPreset(presetId);

    xformula = preset.getXformula();
    yformula = preset.getYformula();
    zformula = preset.getZformula();

    startx = preset.getStartx();
    starty = preset.getStarty();
    startz = preset.getStartz();

    steps = (int) preset.getSteps();
    radius = preset.getRadius();
    stepTime = preset.getSteptime();

    param_a = preset.getParam_a();
    param_b = preset.getParam_b();
    param_c = preset.getParam_c();
    param_d = preset.getParam_d();
    param_e = preset.getParam_e();
    param_f = preset.getParam_f();
    param_g = preset.getParam_g();
    param_h = preset.getParam_h();

  }

  @Override
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // preset_id doesn't really expand parameters, but it changes them; this will make them refresh
    if (PARAM_PRESETID.equalsIgnoreCase(pName)) return true;
    else return false;
  }

  static class Vector2 {

    public double x;
    public double y;

    public Vector2(double a, double b) {
      // TODO Auto-generated constructor stub

      this.x = a;
      this.y = b;
    }

  }

  static class Vector3 {

    public double x;
    public double y;
    public double z;

    public Vector3(double a, double b, double c) {
      // TODO Auto-generated constructor stub

      this.x = a;
      this.y = b;
      this.z = c;
    }

    public Vector3 multiplyScalar(double d) {
      this.x *= d;
      this.y *= d;
      this.z *= d;
      return this;
    }

    public Vector3 divideScalar(double scalar) {
      return multiplyScalar(1.0 / scalar);

    }

    public double length() {
      return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSq() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3 copy(Vector3 v) {
      this.x = v.x;
      this.y = v.y;
      this.z = v.z;
      return this;
    }

    public Vector3 normalize() {
      return divideScalar(length());
    }

    public double dot(Vector3 v) {
      return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector3 crossVector(Vector3 a, Vector3 b) {
      double ax = a.x;
      double ay = a.y;
      double az = a.z;

      double bx = b.x;
      double by = b.y;
      double bz = b.z;

      this.x = ay * bz - az * by;
      this.y = az * bx - ax * bz;
      this.z = ax * by - ay * bx;

      return this;
    }

    public Vector3 projectOnVector(Vector3 v) {
      double scalar = v.dot(this) / v.lengthSq();
      return copy(v).multiplyScalar(scalar);

    }

    public Vector3 sub(Vector3 v) {
      this.x -= v.x;
      this.y -= v.y;
      this.z -= v.z;
      return this;
    }

    public Vector3 projectOnPlane(Vector3 planeNormal) {
      Vector3 v1 = new Vector3(0, 0, 0);
      v1.copy(this).projectOnVector(planeNormal);
      return sub(v1);
    }
  }


  static class AttractorCurve {
    // Strange Attractor curve points, normals tangents, binormals
    ArrayList<Vector3> pos = new ArrayList<Vector3>();
    ArrayList<Vector3> tangents = new ArrayList<Vector3>();
    ArrayList<Vector3> normals = new ArrayList<Vector3>();
    ArrayList<Vector3> binormals = new ArrayList<Vector3>();

    //2D shape points
    ArrayList<Vector2> points = new ArrayList<Vector2>();

    // 3D Attractor mesh
    SimpleMesh mesh = new SimpleMesh();

    int count, subAdvance = 2;
    double radius;
    double timeStep;
    double facets;
    int warmup;

    SAttractor3DFormulaEvaluator evaluator;
    Vector3 start;

    public AttractorCurve(int count, SAttractor3DFormulaEvaluator evaluator, double radius, double timeStep, int facets, Vector3 start, int warmup) {
      this.count = count;
      this.radius = radius;
      this.timeStep = timeStep;
      this.facets = facets;
      this.evaluator = evaluator;
      this.start = start;
      this.warmup = warmup;
    }

    public void build() {
      Vector3 p = start;
      Vector3 position = null, tangent = null, normal = null;

      double t = timeStep;
      //WarmUp
      for (int i = 0; i < warmup; i++) {
        p.x = evaluator.evaluateX(p.x, p.y, p.z, timeStep / subAdvance);
        p.y = evaluator.evaluateY(p.x, p.y, p.z, timeStep / subAdvance);
        p.z = evaluator.evaluateZ(p.x, p.y, p.z, timeStep / subAdvance);
      }

      //
      for (int i = 0; i < this.count; i++) {
        Vector3 old = new Vector3(p.x, p.y, p.z);
        for (int j = 0; j < subAdvance; j++) {
          p.x = evaluator.evaluateX(p.x, p.y, p.z, timeStep / subAdvance);
          p.y = evaluator.evaluateY(p.x, p.y, p.z, timeStep / subAdvance);
          p.z = evaluator.evaluateZ(p.x, p.y, p.z, timeStep / subAdvance);
          position = new Vector3(p.x, p.y, p.z);
          tangent = new Vector3(p.x - old.x, p.y - old.y, p.z - old.z);
          tangent = tangent.normalize();
        }

        if (i == 0) //First Step create a random Normal
        {
          normal = new Vector3(1, 0, 0);
          normal = normal.projectOnPlane(tangent);
          normal = normal.normalize();
          normals.add(normal);
        } else       // Move previous normal along the path (using the Attractor equations), and orthogonalize it.
        {
          normal = new Vector3(pos.get(i - 1).x + normals.get(i - 1).x,
                  pos.get(i - 1).y + normals.get(i - 1).y,
                  pos.get(i - 1).z + normals.get(i - 1).z);

          normal.x = evaluator.evaluateX(p.x, p.y, p.z, t);
          normal.y = evaluator.evaluateY(p.x, p.y, p.z, t);
          normal.z = evaluator.evaluateZ(p.x, p.y, p.z, t);

          normal = new Vector3(normal.x - p.x, normal.y - p.y, normal.z - p.z);
          normal = normal.projectOnPlane(tangent);
          normal = normal.normalize();
          normals.add(normal);
        }
        pos.add(position);
        tangents.add(tangent);
      }
    }


    public void get2DShape() {
      double size = this.radius;
      for (int i = 0; i < this.facets; i++) {
        double a = i / (this.facets / 2) * Math.PI;
        points.add(new Vector2(Math.cos(a) * size, Math.sin(a) * size));
//		   normals.add(Math.cos(a)*size,Math.sin(a)*size); //normals equals points in 2D
      }
    }

    public void getFrames() {
      for (int i = 0; i < pos.size(); i++) {
        Vector3 p = new Vector3(0, 0, 0);
        p.crossVector(tangents.get(i), normals.get(i));
        p = p.normalize();
        binormals.add(p);
      }
    }

    public void inFrame(Vector2 coord, Vector2 ncoord, Vector3 normal, Vector3 binormal, Vector3 pos) {
      double x = pos.x + coord.x * normal.x + coord.y * binormal.x;
      double y = pos.y + coord.x * normal.y + coord.y * binormal.y;
      double z = pos.z + coord.x * normal.z + coord.y * binormal.z;
      mesh.addVertex(x, y, z);

	   /* 
	   normals.add(ncoord.x*normal.x+ncoord.y*binormal.x);
	   normals.add(ncoord.x*normal.y + ncoord.y*binormal.y);
	   normals.add(ncoord.x*normal.z+ncoord.y*binormal.z);
     */
    }

    public void getGeometry() {
      get2DShape();
      int shapeCount = points.size();
      int vertexCount = this.count * (shapeCount) * 3 + 2 * 3; // +2*3 for caps
      int indicesCount = (this.count - 1 + 6) * (shapeCount) * 6;
      getFrames();
      int p = 0;
      int n = 0;
      for (int i = 0; i < this.count; i++) {
        for (int j = 0; j < shapeCount; j++) {
          Vector2 s1 = points.get(j);
          Vector2 sn1 = points.get(j);
          inFrame(s1, sn1, normals.get(i), binormals.get(i), pos.get(i));
          p += 3;
        }
      }
      int ind = 0;
      for (int i = 0; i < this.count - 1; i++) {
        for (int j = 0; j < shapeCount; j++) {
          int p1 = i * (shapeCount) + j;
          int p2 = (j == shapeCount - 1) ? p1 - (shapeCount - 1) : p1 + 1;
          int p1next = p1 + (shapeCount);
          int p2next = (j == shapeCount - 1) ? p1next - (shapeCount - 1) : p1next + 1;
          mesh.addFace(p1, p2, p1next);
          ind += 3;
          mesh.addFace(p1next, p2, p2next);
          ind += 3;
        }
      }

// create endcaps
      Vector3 firstPos = pos.get(0);
      Vector3 lastPos = pos.get(pos.size() - 1);
      mesh.addVertex(firstPos.x, firstPos.y, firstPos.z);
      p += 3;

/*
       normals[n++] = -frames.tangents[0].x;
       normals[n++] = -frames.tangents[0].y;
       normals[n++] = -frames.tangents[0].z;
*/
      mesh.addVertex(lastPos.x, lastPos.y, lastPos.z);
      p += 3;
/*
       normals[n++] = frames.tangents[this.pos.length - 1].x;
       normals[n++] = frames.tangents[this.pos.length - 1].y;
       normals[n++] = frames.tangents[this.pos.length - 1].z;
*/
      for (int j = 0; j < shapeCount; j++) {
        int p1 = j;
        int p2 = (j == shapeCount - 1) ? 0 : j + 1;
        int p3 = p / 3 - 2;
        mesh.addFace(p2, p1, p3);
        ind += 3;
        p1 = j + p / 3 - (shapeCount) - 2;
        p2 = (j == shapeCount - 1) ? p / 3 - (shapeCount) - 2 : p1 + 1;
        p3 = p / 3 - 1;
        mesh.addFace(p1, p2, p3);
        ind += 3;
      }
    }

    public SimpleMesh getMesh() {
      return this.mesh;
    }
  }


}
