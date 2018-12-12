package org.jwildfire.create.tina.variation;


import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.variation.mesh.AbstractOBJMeshWFFunc;
import org.jwildfire.create.tina.variation.mesh.SimpleMesh;
import org.jwildfire.create.tina.variation.plot.Knots3DFormulaEvaluator;
import org.jwildfire.create.tina.variation.plot.Knots3DWFFuncPreset;
import org.jwildfire.create.tina.variation.plot.WFFuncPresetsStore;

import static org.jwildfire.base.mathlib.MathLib.*;


public class Knots3DFunc extends AbstractOBJMeshWFFunc {
  private static final long serialVersionUID = 1L;
  /**
   * Knots3D variation
   *
   * @author Jesus Sosa
   * @date January 22, 2018
   * based on a work of Jürgen Meier
   * http://www.3d-meier.de/tut8/Seite0.html
   * Knot thickness from the book Interactive 3D Computer Graphics
   * by Leendert Ammeraal
   * John Wiley & Sons.
   */
  private static final String PARAM_PRESETID = "presetId";
  private static final String PARAM_STEPS = "steps";
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_FACETS = "facets";

  private static final String PARAM_A = "param_a";
  private static final String PARAM_B = "param_b";
  private static final String PARAM_C = "param_c";
  private static final String PARAM_D = "param_d";
  private static final String PARAM_E = "param_e";
  private static final String PARAM_F = "param_f";
  private static final String PARAM_G = "param_g";
  private static final String PARAM_H = "param_h";

  int preset_id = -1;
  int steps = 1500;
  double radius = 2.0;
  int facets = 4;
  double param_a = 1.0;
  double param_b = 1.0;
  double param_c = 1.0;
  double param_d = 1.0;
  double param_e = 1.0;
  double param_f = 1.0;
  double param_g = 1.0;
  double param_h = 1.0;


  private static final String[] paramNames = {PARAM_PRESETID, PARAM_STEPS, PARAM_RADIUS, PARAM_FACETS, PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G, PARAM_H,
          PARAM_SCALEX, PARAM_SCALEY, PARAM_SCALEZ, PARAM_OFFSETX, PARAM_OFFSETY, PARAM_OFFSETZ, PARAM_SUBDIV_LEVEL, PARAM_SUBDIV_SMOOTH_PASSES, PARAM_SUBDIV_SMOOTH_LAMBDA, PARAM_SUBDIV_SMOOTH_MU, PARAM_BLEND_COLORMAP, PARAM_DISPL_AMOUNT, PARAM_BLEND_DISPLMAP, PARAM_RECEIVE_ONLY_SHADOWS};


  private static final String RESSOURCE_XFORMULA = "xformula";
  private static final String RESSOURCE_YFORMULA = "yformula";
  private static final String RESSOURCE_ZFORMULA = "zformula";

  private static final String[] ressourceNames = {RESSOURCE_XFORMULA, RESSOURCE_YFORMULA, RESSOURCE_ZFORMULA};

  private String xformula;
  private String yformula;
  private String zformula;

  private Knots3DFormulaEvaluator evaluator;

  public Knots3DFunc() {
    super();
    preset_id = WFFuncPresetsStore.getKnots3DWFFuncPresets().getRandomPresetId();
    refreshFormulaFromPreset(preset_id);
    String code = "import static org.jwildfire.base.mathlib.MathLib.*;\r\n" +
            "\r\n" +
            "  public double evaluateX( int  step) {\r\n" +
            "    double t = 2.0*M_PI* step/" + steps + ";\r\n" +
            "    double param_a = " + param_a + ";\r\n" +
            "    double param_b = " + param_b + ";\r\n" +
            "    double param_c = " + param_c + ";\r\n" +
            "    double param_d = " + param_d + ";\r\n" +
            "    double param_e = " + param_e + ";\r\n" +
            "    double param_f = " + param_f + ";\r\n" +
            "    double param_g = " + param_g + ";\r\n" +
            "    double param_h = " + param_h + ";\r\n" +
            "    return  " + (xformula != null && !xformula.isEmpty() ? xformula : "0.0") + ";\r\n" +
            "  }\r\n" +
            "  public double evaluateY( int step) {\r\n" +
            "    double t = 2.0*M_PI* step/" + steps + ";\r\n" +
            "    double param_a = " + param_a + ";\r\n" +
            "    double param_b = " + param_b + ";\r\n" +
            "    double param_c = " + param_c + ";\r\n" +
            "    double param_d = " + param_d + ";\r\n" +
            "    double param_e = " + param_e + ";\r\n" +
            "    double param_f = " + param_f + ";\r\n" +
            "    double param_g = " + param_g + ";\r\n" +
            "    double param_h = " + param_h + ";\r\n" +
            "    return  " + (yformula != null && !yformula.isEmpty() ? yformula : "0.0") + ";\r\n" +
            "  }\r\n" +
            "  public double evaluateZ(int step) {\r\n" +
            "    double t = 2.0*M_PI* step/" + steps + ";\r\n" +
            "    double param_a = " + param_a + ";\r\n" +
            "    double param_b = " + param_b + ";\r\n" +
            "    double param_c = " + param_c + ";\r\n" +
            "    double param_d = " + param_d + ";\r\n" +
            "    double param_e = " + param_e + ";\r\n" +
            "    double param_f = " + param_f + ";\r\n" +
            "    double param_g = " + param_g + ";\r\n" +
            "    double param_h = " + param_h + ";\r\n" +
            "    return  " + (zformula != null && !zformula.isEmpty() ? zformula : "0.0") + ";\r\n" +
            "  }\r\n";
    try {
      evaluator = Knots3DFormulaEvaluator.compile(code);
    } catch (Exception e) {
      evaluator = null;
      e.printStackTrace();
      System.out.println(code);
      throw new IllegalArgumentException(e);
    }
    scaleX = 0.02;
    scaleY = 0.02;
    scaleZ = 0.02;
  }

  private String calculateMeshKey() {
    return steps + "#" + xformula + "#" + yformula + "#" + zformula + "#" + radius + "#" + facets + "#" + param_a + "#" + param_b + "#" + param_c + "#" + param_d + "#" + param_e + "#" + param_f + "#" + param_g + "#" + param_h;
  }

  private SimpleMesh getMesh() {
    String key = calculateMeshKey();
    SimpleMesh simpleMesh = (SimpleMesh) RessourceManager.getRessource(key);
    if (simpleMesh == null) {
      //			    preset_id = WFFuncPresetsStore.getKnots3DWFFuncPresets().getRandomPresetId();
      //			    refreshFormulaFromPreset(preset_id);
      String code = "import static org.jwildfire.base.mathlib.MathLib.*;\r\n" +
              "\r\n" +
              "  public double evaluateX( int  step) {\r\n" +
              "    double t = 2.0*M_PI* step/" + steps + ";\r\n" +
              "    double param_a = " + param_a + ";\r\n" +
              "    double param_b = " + param_b + ";\r\n" +
              "    double param_c = " + param_c + ";\r\n" +
              "    double param_d = " + param_d + ";\r\n" +
              "    double param_e = " + param_e + ";\r\n" +
              "    double param_f = " + param_f + ";\r\n" +
              "    double param_g = " + param_g + ";\r\n" +
              "    double param_h = " + param_h + ";\r\n" +
              "    return  " + (xformula != null && !xformula.isEmpty() ? xformula : "0.0") + ";\r\n" +
              "  }\r\n" +
              "  public double evaluateY( int step) {\r\n" +
              "    double t = 2.0*M_PI* step/" + steps + ";\r\n" +
              "    double param_a = " + param_a + ";\r\n" +
              "    double param_b = " + param_b + ";\r\n" +
              "    double param_c = " + param_c + ";\r\n" +
              "    double param_d = " + param_d + ";\r\n" +
              "    double param_e = " + param_e + ";\r\n" +
              "    double param_f = " + param_f + ";\r\n" +
              "    double param_g = " + param_g + ";\r\n" +
              "    double param_h = " + param_h + ";\r\n" +
              "    return  " + (yformula != null && !yformula.isEmpty() ? yformula : "0.0") + ";\r\n" +
              "  }\r\n" +
              "  public double evaluateZ(int step) {\r\n" +
              "    double t = 2.0*M_PI* step/" + steps + ";\r\n" +
              "    double param_a = " + param_a + ";\r\n" +
              "    double param_b = " + param_b + ";\r\n" +
              "    double param_c = " + param_c + ";\r\n" +
              "    double param_d = " + param_d + ";\r\n" +
              "    double param_e = " + param_e + ";\r\n" +
              "    double param_f = " + param_f + ";\r\n" +
              "    double param_g = " + param_g + ";\r\n" +
              "    double param_h = " + param_h + ";\r\n" +
              "    return  " + (zformula != null && !zformula.isEmpty() ? zformula : "0.0") + ";\r\n" +
              "  }\r\n";
      try {
        evaluator = Knots3DFormulaEvaluator.compile(code);
      } catch (Exception e) {
        evaluator = null;
        e.printStackTrace();
        System.out.println(code);
        throw new IllegalArgumentException(e);
      }

      Knot knot = new Knot(evaluator, steps, radius, facets);
      knot.buildMesh();
      simpleMesh = knot.getMesh();
      RessourceManager.putRessource(key, simpleMesh);
    }
    return simpleMesh;
  }

  public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.initOnce(pContext, pLayer, pXForm, pAmount);
    // store to cache
    getMesh();
  }

  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    super.init(pContext, pLayer, pXForm, pAmount);
    // from cache
    mesh = getMesh();
  }


  public String getName() {
    return "knots3D";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{preset_id, steps, radius, facets, param_a, param_b, param_c, param_d, param_e, param_f, param_g, param_h, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, subdiv_level, subdiv_smooth_passes, subdiv_smooth_lambda, subdiv_smooth_mu, colorMapHolder.getBlend_colormap(), displacementMapHolder.getDispl_amount(), displacementMapHolder.getBlend_displ_map(), receive_only_shadows};
  }

  public void setParameter(String pName, double pValue) {
    if (PARAM_PRESETID.equalsIgnoreCase(pName)) {
      preset_id = Tools.FTOI(pValue);
      if (preset_id >= 0) {
        refreshFormulaFromPreset(preset_id);
      }
    } else if (PARAM_STEPS.equalsIgnoreCase(pName)) {
      steps = (int) pValue;
      //		          recalculate();
    } else if (PARAM_RADIUS.equalsIgnoreCase(pName)) {
      radius = pValue;
      //		          recalculate();
    } else if (PARAM_FACETS.equalsIgnoreCase(pName)) {
      facets = (int) pValue;
      //			          recalculate();
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

  private void validatePresetId() {
    if (preset_id >= 0) {
      Knots3DWFFuncPreset preset = WFFuncPresetsStore.getKnots3DWFFuncPresets().getPreset(preset_id);
      if (!preset.getXformula().equals(xformula) || !preset.getYformula().equals(yformula) || !preset.getZformula().equals(zformula)) {
        preset_id = -1;
      }
    }
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

  private void refreshFormulaFromPreset(int presetId) {
    Knots3DWFFuncPreset preset = WFFuncPresetsStore.getKnots3DWFFuncPresets().getPreset(presetId);

    xformula = preset.getXformula();
    yformula = preset.getYformula();
    zformula = preset.getZformula();


    steps = (int) preset.getSteps();
    radius = preset.getRadius();
    facets = (int) preset.getFacets();

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


  static class Vertex {

    public double x;
    public double y;
    public double z;

    public Vertex(double a, double b, double c) {
      // TODO Auto-generated constructor stub
      this.x = a;
      this.y = b;
      this.z = c;
    }
  }

  static class Knot {
    Knots3DFormulaEvaluator evaluator;
    public int count;
    public int facets;
    public double radius;
    SimpleMesh mesh;

    public static double r11;
    public static double r12;
    public static double r13;
    public static double r21;
    public static double r22;
    public static double r23;
    public static double r31;
    public static double r32;
    public static double r33;
    public static double r41;
    public static double r42;
    public static double r43;

    public Knot(Knots3DFormulaEvaluator evaluator, int count, double radius, int facets) {
      this.mesh = new SimpleMesh();
      this.count = count;
      this.radius = radius;
      this.facets = facets;
      this.evaluator = evaluator;
    }

    public SimpleMesh getMesh() {
      return this.mesh;
    }

    public void initrotate(Vertex start, Vertex dir, double angle)
	/* Computation of the rotation matrix
	
	            | r11  r12  r13  0 |
	       R =  | r21  r22  r23  0 |
	            | r31  r32  r33  0 |
	            | r41  r42  r43  1 |
	
	   to be used as [x1  y1  z1  1] = [x  y  z  1] R,
	   see function 'rotate'.
	   Point (x1, y1, z1) is the image of (x, y, z).
	   The rotation takes place about the axis  
	     (a1, a2, a3)+lambda(v1, v2, v3)
	   and through the angle alpha.
	 */ {
      double rho;
      double theta;
      double cal;
      double sal;
      double cph;
      double sph;
      double cth;
      double sth;
      double cph2;
      double sph2;
      double cth2;
      double sth2;
      double pi;
      double cal1;
      double a1, a2, a3, v1, v2, v3, alpha;

      a1 = start.x;
      a2 = start.y;
      a3 = start.z;
      v1 = dir.x;
      v2 = dir.y;
      v3 = dir.z;
      alpha = angle;

      cal = cos(alpha);
      sal = sin(alpha);
      cal1 = 1.0 - cal;
      rho = sqrt(v1 * v1 + v2 * v2 + v3 * v3);
      pi = 4.0 * atan(1.0);
      if (rho == 0.0) {
        theta = 0.0;
        cph = 1.0;
        sph = 0.0;
      } else {
        if (v1 == 0.0) {
          theta = (v2 >= 0.0 ? 0.5 * pi : 1.5 * pi);
        } else {
          theta = Math.atan(v2 / v1);
          if (v1 < 0) {
            theta += pi;
          }
        }
        cph = v3 / rho;
        sph = sqrt(1.0 - cph * cph);
        /* cph = cos(phi), sph = sin(phi)  */
      }
      cth = Math.cos(theta);
      sth = Math.sin(theta);
      cph2 = cph * cph;
      sph2 = 1.0 - cph2;
      cth2 = cth * cth;
      sth2 = 1.0 - cth2;
      r11 = (cal * cph2 + sph2) * cth2 + cal * sth2;
      r12 = sal * cph + cal1 * sph2 * cth * sth;
      r13 = sph * (cph * cth * cal1 - sal * sth);
      r21 = sph2 * cth * sth * cal1 - sal * cph;
      r22 = sth2 * (cal * cph2 + sph2) + cal * cth2;
      r23 = sph * (cph * sth * cal1 + sal * cth);
      r31 = sph * (cph * cth * cal1 + sal * sth);
      r32 = sph * (cph * sth * cal1 - sal * cth);
      r33 = cal * sph2 + cph2;
      r41 = a1 - a1 * r11 - a2 * r21 - a3 * r31;
      r42 = a2 - a1 * r12 - a2 * r22 - a3 * r32;
      r43 = a3 - a1 * r13 - a2 * r23 - a3 * r33;
    }

    public void rotate(Vertex ps, Vertex pt) {
      pt.x = (ps.x * r11 + ps.y * r21 + ps.z * r31 + r41);
      pt.y = (ps.x * r12 + ps.y * r22 + ps.z * r32 + r42);
      pt.z = (ps.x * r13 + ps.y * r23 + ps.z * r33 + r43);
    }

    public boolean zero(double x) {
      return fabs(x) < 1e-5;
    }
	
/*	
  	 public Vertex evaluateKnot(int i)
  	 {
  		 double t,x,y,z;

  			t=2.0*M_PI*i/count;
  	//Trefoil Knoten B
	//		x=-10.0*cos(t) - 2.*cos(5.0*t) + 15.0*sin(2.0*t);
	//	    y = - 15.0*cos(2.0*t) + 10.0*sin(t) - 2.0*sin(5.0*t);
	//	    z= 10.0*cos(3.0*t);
  // Torus Knoten (p=5,q=3)
  //	         x = (100 + 25 * cos(5 * t)) * cos(3 * t);
  //	         y = (100 + 25 * cos(5 * t)) * sin(3 * t);
  //	         z = 25 * sin(5 * t);
// Granny Knoten
x = - 22*cos(t) - 128*sin(t) - 44*cos(3*t) - 78*sin(3*t);
y = - 10*cos(2*t) - 27*sin(2*t) + 38*cos(4*t) + 46*sin(4*t);
z = 70*cos(3*t) - 40*sin(3*t);

//Square Knoten

x = - 22*cos(t) - 128*sin(t) - 44*cos(3*t) - 78*sin(3*t);
 
y = 11*cos(t) - 43*cos(3*t) + 34*cos(5*t) - 39*sin(5*t);
 
z = 70*cos(3*t) - 40*sin(3*t) + 8*cos(5*t) - 9*sin(5*t);

// Achterknoten
x = 10*cos(t) + 10*cos(3*t);
y = 6*sin(t) + 10*sin(3*t);
z = 4*sin(3*t) - 10*sin(6*t);
// torus Knoten (p=15,q=2)
         x = (100 + 25 * cos(15 * t)) * cos(2 * t);
         y = (100 + 25 * cos(15 * t)) * sin(2 * t);
         z = 25 * sin(15 * t);
// Trefoil Knoten A
 //            x = (2. + cos(1.5*t))*cos(t);
 //            y = (2. + cos(1.5*t))*sin(t);
 //            z = sin(1.5*t);
		    return new Vertex(x,y,z);
  	 }
  */

    public void buildMesh() {

      int i;
      int n;
      int j;
      int m;
      int jn;
      int jn0;

      double R;

      double xC0, yC0, zC0;
      double xC1, yC1, zC1;
      double xC2, yC2, zC2;

      double a, b, c, d;
      double rx, ry, rz;
      double pi;
      double theta;
      double Len;


      double xA, yA, zA, xB, yB, zB;
      double dx, dy, dz;
      double d0;
      double c1, c2, c0;
      double xM, yM, zM;
      double e1, e2, e0;
      double denom;
      double lambda;
      double mu;
      double xP, yP, zP;
      double xAP, yAP, zAP;

      double xBP, yBP, zBP;
      double v1, v2, v3;
      double cosphi;
      double phi;

      //    Vertex start=evaluateKnot(0);
      Vertex start = new Vertex(0, 0, 0);
      int t = 0;
      start.x = evaluator.evaluateX(t);
      start.y = evaluator.evaluateY(t);
      start.z = evaluator.evaluateZ(t);

      xC0 = start.x;
      yC0 = start.y;
      zC0 = start.z;

//     Vertex center=evaluateKnot(1);
      Vertex center = new Vertex(0, 0, 0);
      t = 1;
      center.x = evaluator.evaluateX(t);
      center.y = evaluator.evaluateY(t);
      center.z = evaluator.evaluateZ(t);

      xC1 = center.x;
      yC1 = center.y;
      zC1 = center.z;

//     Vertex last=evaluateKnot(2);
      Vertex last = new Vertex(0, 0, 0);
      t = 2;
      last.x = evaluator.evaluateX(t);
      last.y = evaluator.evaluateY(t);
      last.z = evaluator.evaluateZ(t);

      xC2 = last.x;
      yC2 = last.y;
      zC2 = last.z;

      n = facets;
      R = radius;


      a = xC2 - xC0;
      b = yC2 - yC0;
      c = zC2 - zC0;
      d = a * xC1 + b * yC1 + c * zC1;

	  /* First circle has center (xC1, yC1, zC1), radius R, and
	     it lies in plane ax + by + cz = d
	  */

      if (zero(a) && zero(b)) {
        rx = 0;
        ry = c;
        rz = -b;
      } else {
        rx = b;
        ry = -a;
        rz = 0;
      }
      Len = sqrt(rx * rx + ry * ry + rz * rz);
      rx /= Len;
      ry /= Len;
      rz /= Len;
      // (rx, ry, rz) is a unit vector perpendicular to (a, b, c)
//	  mesh.addVertex(0.,0.,0.);
      mesh.addVertex(xC1 + rx * R, yC1 + ry * R, zC1 + rz * R);

      //  x[0] = xC1 + rx * R;
      //  y[0] = yC1 + ry * R;
      //  z[0] = zC1 + rz * R;

      pi = 4.0 * atan(1.0);
      theta = 2 * pi / n;

      /* Computation of n points on the first circle: */

      Vertex C1 = new Vertex(xC1, yC1, zC1);
      Vertex dir = new Vertex(a, b, c);

      initrotate(C1, dir, theta);

      for (i = 1; i < n; i++) {
        org.jwildfire.create.tina.variation.mesh.Vertex vx = mesh.getVertex(i - 1);
        Vertex vr = new Vertex(0., 0., 0.);
        rotate(new Vertex(vx.x, vx.y, vx.z), vr);
        mesh.addVertex(vr.x, vr.y, vr.z);
      }
	  
	  /* Count number of circles
	     (number of points minus 1 read from input file):  */

//	  m-> (Number of Circles in cable)

      m = count;

	  
	  /* (xC[0], yC[0], zC[0]) is now the center of the given
	     circle, lying in plane ax + by + cz =d, and with radius
	     R. The n relevant points on this circle have already
	     been computed; their coordinates are 
	     x[0], y[0], z[0], ..., x[n-1], y[n-1], z[n-1].
	     The other m-1 circles will be derived from this first
	     one by means of rotations.
	  */

      Vertex vec1, vec2;
      for (j = 1; j < m; j++) {
        jn = j * n;
        jn0 = jn - n;
//		Vertex vec1 = evaluateKnot(j-1);
        vec1 = new Vertex(0, 0, 0);
        t = j - 1;
        vec1.x = evaluator.evaluateX(t);
        vec1.y = evaluator.evaluateY(t);
        vec1.z = evaluator.evaluateZ(t);

        xA = vec1.x;
        yA = vec1.y;
        zA = vec1.z;
//		Vertex vec2=  evaluateKnot(j);
        vec2 = new Vertex(0, 0, 0);
        t = j;
        vec2.x = evaluator.evaluateX(t);
        vec2.y = evaluator.evaluateY(t);
        vec2.z = evaluator.evaluateZ(t);

        xB = vec2.x;
        yB = vec2.y;
        zB = vec2.z;

        dx = xB - xA;
        dy = yB - yA;
        dz = zB - zA;
        c1 = a * a + b * b + c * c;
        c2 = a * dx + b * dy + c * dz;
        c0 = d - a * xA - b * yA - c * zA;
        xM = 0.5 * (xA + xB);
        yM = 0.5 * (yA + yB);
        zM = 0.5 * (zA + zB);
        d0 = dx * xM + dy * yM + dz * zM;
        e1 = dx * a + dy * b + dz * c;
        e2 = dx * dx + dy * dy + dz * dz;
        e0 = d0 - dx * xA - dy * yA - dz * zA;
        denom = c1 * e2 - c2 * e1;
        if (fabs(denom) < 1e-12) {
		/* Direction does not change.
		     Instead of using a point P infinitely far
		     away, we perform a simple translation:
		  */
          for (i = 0; i < n; i++) {
            org.jwildfire.create.tina.variation.mesh.Vertex vx = mesh.getVertex(jn0 + i);
            mesh.addVertex(vx.x, vx.y, vx.z);
          }
        } else
		  /* Direction changes.
		     The polygon will be rotated through the angle phi
		     about vector v passing through point P:
		  */ {
          lambda = (c0 * e2 - c2 * e0) / denom;
          mu = (c1 * e0 - c0 * e1) / denom;
          xP = xA + lambda * a + mu * dx;
          yP = yA + lambda * b + mu * dy;
          zP = zA + lambda * c + mu * dz;
		  /* Point P (of intersection of three planes) is
		     center of rotation 
		  */
          xAP = xA - xP;
          yAP = yA - yP;
          zAP = zA - zP;
          xBP = xB - xP;
          yBP = yB - yP;
          zBP = zB - zP;
          v1 = yAP * zBP - yBP * zAP;
          v2 = xBP * zAP - xAP * zBP;
          v3 = xAP * yBP - xBP * yAP;
          /* (v1, v2, v3) is direction of axis of rotation */
          cosphi = (xAP * xBP + yAP * yBP + zAP * zBP) / Math.sqrt((xAP * xAP + yAP * yAP + zAP * zAP) * (xBP * xBP + yBP * yBP + zBP * zBP));
          phi = (cosphi == 0 ? 0.5 * pi : Math.atan(Math.sqrt(1.0 - cosphi * cosphi) / cosphi));
          /* phi is the angle of rotation */
          Vertex P = new Vertex(xP, yP, zP);
          Vertex v = new Vertex(v1, v2, v3);

          initrotate(P, v, phi);
          for (i = 0; i < n; i++) {
            org.jwildfire.create.tina.variation.mesh.Vertex vx = mesh.getVertex(jn0 + i);
            Vertex vxx = new Vertex(vx.x, vx.y, vx.z);
            Vertex vr = new Vertex(0., 0., 0.);
            rotate(vxx, vr);
            mesh.addVertex(vr.x, vr.y, vr.z);
          }

          initrotate(new Vertex(0.0, 0.0, 0.0), v, phi);
          Vertex vr = new Vertex(0.0, 0.0, 0.0);
          rotate(new Vertex(a, b, c), vr);
          c = vr.z;
          b = vr.y;
          a = vr.x;
        }
//		Vertex XC=evaluateKnot(j);
        Vertex XC = new Vertex(0, 0, 0);
        t = j;
        XC.x = evaluator.evaluateX(t);
        XC.y = evaluator.evaluateY(t);
        XC.z = evaluator.evaluateZ(t);

        d = a * XC.x + b * XC.y + c * XC.z;
      }

      for (j = 1; j < m; j++) {
        jn = j * n + 1;
        jn0 = jn - n;
        for (i = 0; i < n - 1; i++) {
          mesh.addFace(jn + i + 1 - 1, (jn0 + i) - 1, jn0 + i + 1 - 1);
          mesh.addFace(jn0 + i - 1, (jn + i + 1) - 1, jn + i - 1);
        }
        mesh.addFace(jn - 1, (jn0 + n - 1) - 1, jn0 - 1);
        mesh.addFace(jn0 + n - 1 - 1, jn - 1, jn + n - 1 - 1);
      }
//	 	System.out.println("finished knot");
    }

  }
}