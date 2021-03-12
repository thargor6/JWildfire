package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Random;


public class GLSLAcrilicFunc extends GLSLFunc {

  /*
   * Variation : glsl_acrilic
   * Date: October 31, 2018
   * Reference
   */


  private static final long serialVersionUID = 1L;

  private static final String PARAM_RESOLUTIONX = "Density Pixels";
  private static final String PARAM_SEED = "Seed";
  private static final String PARAM_TIME = "time";
  private static final String PARAM_STEPS = "Steps";
  private static final String PARAM_P1 = "p1";
  private static final String PARAM_P2 = "p2";
  private static final String PARAM_P3 = "p3";
  private static final String PARAM_P4 = "p4";
  private static final String PARAM_P5 = "p5";
  private static final String PARAM_P6 = "p6";
  private static final String PARAM_FR = "Red Fac.";
  private static final String PARAM_FG = "Green Fac.";
  private static final String PARAM_FB = "Blue Fac.";
  private static final String PARAM_GRADIENT = "Gradient";


  int resolutionX = 1000000;
  int resolutionY = resolutionX;
  private int seed = 10000;
  double time = 0.0;
  int steps = 10;
  double p1 = 12.0;
  double p2 = 12.0;
  double p3 = 12.0;
  double p4 = 12.0;
  double p5 = 75;
  double p6 = 75;
  double FR = 1.0, FG = 1.0, FB = 1.0;
  int gradient = 0;


  Random randomize = new Random(seed);


  private static final String[] paramNames = {PARAM_RESOLUTIONX, PARAM_SEED, PARAM_TIME, PARAM_STEPS, PARAM_P1, PARAM_P2, PARAM_P3, PARAM_P4, PARAM_P5, PARAM_P6,
          PARAM_FR, PARAM_FG, PARAM_FB, PARAM_GRADIENT};

  public double random(double r1, double r2) {
    return r1 + (r2 - r1) * randomize.nextDouble();
  }

  public double sq(double x) {
    return x * x;
  }


  public vec3 getRGBColor(int xp, int yp) {


    double x = (double) xp + 0.5;
    double y = (double) yp + 0.5;

    vec2 p = new vec2(0.7 * x / resolutionX, 0.7 * y / resolutionY);
    vec3 col = new vec3(0.0);

    for (int j = 0; j < 3; j++) {
      for (int i = 1; i < steps; i++) {
        p.x += 0.1 / (i + j) * G.sin(i * p1 * p.y + time + G.cos((time / (p2 * i)) * i + j));
        p.y += 0.1 / (i + j) * G.cos(i * p3 * p.x + time + G.sin((time / (p4 * i)) * i + j));
      }
      if (j == 0)
        col.x = G.sin(p5 * sq(p.x)) + G.sin(p6 * sq(p.y));
      if (j == 1)
        col.y = G.sin(p5 * sq(p.x)) + G.sin(p6 * sq(p.y));
      if (j == 2)
        col.z = G.sin(p5 * sq(p.x)) + G.sin(p6 * sq(p.y));
    }
    col = new vec3(G.cos(col.x * FR), G.cos(col.y * FG), G.cos(col.z * FB));
    return col;
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {


    int i = (int) (pContext.random() * resolutionX);
    int j = (int) (pContext.random() * resolutionY);

    vec3 color = new vec3(0.0);

    color = getRGBColor(i, j);

    if (gradient == 0) {
      int[] tcolor = new int[3];
      tcolor = dbl2int(color);

      pVarTP.rgbColor = true;
      ;
      pVarTP.redColor = tcolor[0];
      pVarTP.greenColor = tcolor[1];
      pVarTP.blueColor = tcolor[2];

    } else {
      double s = (color.x + color.y + color.z);
      double red = color.x / s;

      pVarTP.color = Math.sin(red);

    }
    pVarTP.x += pAmount * ((double) (i) / resolutionX - 0.5);
    pVarTP.y += pAmount * ((double) (j) / resolutionY - 0.5);

  }


  public String getName() {
    return "glsl_acrilic";
  }

  public String[] getParameterNames() {
    return paramNames;
  }


  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{resolutionX, seed, time, steps, p1, p2, p3, p4, p5, p6, FR, FG, FB, gradient};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_RESOLUTIONX)) {
      resolutionX = (int) Tools.limitValue(pValue, 100, 10000000);
    } else if (PARAM_SEED.equalsIgnoreCase(pName)) {
      seed = (int) pValue;
      randomize = new Random(seed);
      time = random(0.0, 10000000.0);
    } else if (pName.equalsIgnoreCase(PARAM_TIME)) {
      time = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_STEPS)) {
      steps = (int) Tools.limitValue(pValue, 3, 100);
    } else if (pName.equalsIgnoreCase(PARAM_P1)) {
      p1 = Tools.limitValue(pValue, 1., 100.);
    } else if (pName.equalsIgnoreCase(PARAM_P2)) {
      p2 = Tools.limitValue(pValue, 1., 100.);
    } else if (pName.equalsIgnoreCase(PARAM_P3)) {
      p3 = Tools.limitValue(pValue, 1., 100.);
    } else if (pName.equalsIgnoreCase(PARAM_P4)) {
      p4 = Tools.limitValue(pValue, 1., 100.);
    } else if (pName.equalsIgnoreCase(PARAM_P5)) {
      p5 = Tools.limitValue(pValue, 1., 100.);
    } else if (pName.equalsIgnoreCase(PARAM_P6)) {
      p6 = Tools.limitValue(pValue, 1., 100.);
    } else if (pName.equalsIgnoreCase(PARAM_FR)) {
      FR = Tools.limitValue(pValue, 0.0, 5.0);
    } else if (pName.equalsIgnoreCase(PARAM_FG)) {
      FG = Tools.limitValue(pValue, 0.0, 5.0);
    } else if (pName.equalsIgnoreCase(PARAM_FB)) {
      FB = Tools.limitValue(pValue, 0.0, 5.0);
    } else if (pName.equalsIgnoreCase(PARAM_GRADIENT)) {
      gradient = (int) Tools.limitValue(pValue, 0, 1);
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // preset_id doesn't really expand parameters, but it changes them; this will make them refresh
    return true;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
  }

}

