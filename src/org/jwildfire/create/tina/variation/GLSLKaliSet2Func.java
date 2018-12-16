package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Random;


public class GLSLKaliSet2Func extends GLSLFunc {

  /*
   * Variation : glsl_kaliset2
   * Date: October 31, 2018
   * Reference
   */


  private static final long serialVersionUID = 1L;

  private static final String PARAM_RESOLUTIONX = "Density Pixels";
  private static final String PARAM_SEED = "Seed";
  private static final String PARAM_TIME = "time";
  private static final String PARAM_N = "N";
  private static final String PARAM_R2 = "radio";
  private static final String PARAM_SHIFT_X = "ShiftX";
  private static final String PARAM_SHIFT_Y = "ShiftY";
  private static final String PARAM_FR = "Red Fac.";
  private static final String PARAM_FG = "Green Fac.";
  private static final String PARAM_FB = "Blue Fac.";

  private static final String PARAM_GRADIENT = "Gradient";


  int resolutionX = 1000000;
  int resolutionY = resolutionX;
  private int seed = 5000;
  double time = 85.5;
  int N = 100;
  double rad2 = 1.0;
  double shiftx = -0.356, shifty = 0.686;
  double FR = 1.8, FG = 1.9, FB = 2.2;
  int gradient = 0;

  Random randomize = new Random(seed);


  private static final String[] paramNames = {PARAM_RESOLUTIONX, PARAM_SEED, PARAM_TIME, PARAM_N, PARAM_R2, PARAM_SHIFT_X, PARAM_SHIFT_Y, PARAM_FR, PARAM_FG, PARAM_FB, PARAM_GRADIENT};

  public double random(double r1, double r2) {
    return r1 + (r2 - r1) * randomize.nextDouble();
  }


  public vec3 getRGBColor(int xp, int yp) {
    double x = (double) xp + 0.5;
    double y = (double) yp + 0.5;

    // Half the width and half the height gives the position of the center of the screen

    vec2 v = new vec2(x / resolutionX - .5, y / resolutionY - .5).multiply(20.0);

    /*
     * inspired by http://www.fractalforums.com/new-theories-and-research/very-simple-formula-for-fractal-patterns/
     * a slight(?) different
     * public domain
     */


    double rsum = 0.0;
    double PI2 = 6.2831853070;

    double angle = PI2 * shiftx;

    double C = G.cos(angle);
    double S = G.sin(angle);

    vec2 shift = new vec2(0.0, 2.0 + 2. * G.sin(0.03 * time));
    double zoom = 2.0 + 5.0 * shifty;


    for (int i = 0; i < N; i++) {
      double rr = v.x * v.x + v.y * v.y;
      if (rr > rad2) {
        rr = rad2 / rr;
        v.x = v.x * rr;
        v.y = v.y * rr;
      }
      rsum = G.max(rsum, rr);

      v = new vec2(C * v.x - S * v.y, S * v.x + C * v.y).multiply(zoom).plus(shift);
    }

    double col = rsum * rsum * (500.0 / (double) N / rad2);
    return new vec3(G.cos(col * FR), G.cos(col * FG), G.cos(col * FB));
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
    return "glsl_kaliset2";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{resolutionX, seed, time, N, rad2, shiftx, shifty, FR, FG, FB, gradient};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_RESOLUTIONX)) {
      resolutionX = (int) Tools.limitValue(pValue, 100, 10000000);
    } else if (PARAM_SEED.equalsIgnoreCase(pName)) {
      seed = (int) pValue;
      randomize = new Random(seed);
      time = random(1.0, 1000.0);
    } else if (pName.equalsIgnoreCase(PARAM_TIME)) {
      time = Tools.limitValue(pValue, 1.0, 1000.0);
    } else if (pName.equalsIgnoreCase(PARAM_N)) {
      N = (int) Tools.limitValue(pValue, 1, 100);
    } else if (pName.equalsIgnoreCase(PARAM_R2)) {
      rad2 = Tools.limitValue(pValue, 0.1, 10.0);
    } else if (pName.equalsIgnoreCase(PARAM_SHIFT_X)) {
      shiftx = Tools.limitValue(pValue, -1.0, 1.0);
    } else if (pName.equalsIgnoreCase(PARAM_SHIFT_Y)) {
      shifty = Tools.limitValue(pValue, -1.0, 1.0);
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

}

