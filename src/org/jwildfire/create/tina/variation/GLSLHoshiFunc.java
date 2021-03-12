package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Random;


public class GLSLHoshiFunc extends GLSLFunc {

  /*
   * Variation : glsl_hoshi
   * Autor: Jesus Sosa
   * Date: October 31, 2018
   * Reference
   */


  private static final long serialVersionUID = 1L;

  private static final String PARAM_RESOLUTIONX = "Density Pixels";
  private static final String PARAM_SEED = "Seed";
  private static final String PARAM_TIME = "time";
  private static final String PARAM_STEPS = "Steps";
  private static final String PARAM_SCALE = "Scale";
  private static final String PARAM_TRANSLATE = "Translate";
  private static final String PARAM_GRADIENT = "Gradient";

  int resolutionX = 1000000;
  int resolutionY = resolutionX;
  int seed = 100000;
  double time = 10.0;
  int steps = 28;
  double sc = 1.25;
  double translt = 1.5;

  vec2 fold = new vec2(-0.5, -0.5);
  vec2 translate = new vec2(translt);
  double scale = sc;

  int gradient = 0;
  Random randomize = new Random(seed);

  private static final String[] paramNames = {PARAM_RESOLUTIONX, PARAM_SEED, PARAM_TIME, PARAM_STEPS, PARAM_SCALE, PARAM_TRANSLATE, PARAM_GRADIENT};

  public double random(double r1, double r2) {
    return r1 + (r2 - r1) * randomize.nextDouble();
  }

  public vec3 hsv(double h, double s, double v) {
    vec3 t1 = new vec3(3., 2., 1.);
    t1 = t1.add(h).division(3.0);
    vec3 t2 = G.fract(t1).multiply(6.0).minus(3.0);
    t2 = G.abs(t2).minus(1.0);
    t2 = G.clamp(t2, 0., 1.);
    return G.mix(new vec3(3.1), t2, s).multiply(v);
  }

  public vec2 rotate(vec2 p, double a) {
    return new vec2(p.x * G.cos(a) - p.y * G.sin(a), p.x * G.sin(a) + p.y * G.cos(a));
  }


  public vec3 getRGBColor(int xp, int yp) {
    vec2 p = new vec2(2.0 * xp / resolutionX - 1.0, 2.0 * yp / resolutionY - 1.0);

    p = p.multiply(0.182);

    double x = p.y;
    p = G.abs(G.mod(p, 4.0).minus(2.0));
    for (int i = steps; i > 0; i--) {
      p = G.abs(p.minus(fold)).plus(fold);
      p = p.multiply(scale).minus(translate);
      p = rotate(p, 3.14159 / (0.10 + G.sin(time * 0.0005 + (double) i * 0.5000001) * 0.4999 + 0.5 + (10. / time) + G.sin(time) / 100.));
    }
    double i = x * x + G.atan2(p.y, p.x) + time * 0.02;
    double h = G.floor(i * 4.0) / 8.0 + 1.107;
    h += G.smoothstep(-0.1, 0.8, G.mod(i * 2.0 / 5.0, 1.0 / 4.0) * 900.0) / 0.010 - 0.5;
    vec3 color = hsv(h, 1.0, G.smoothstep(-3.0, 3.0, G.length(p) * 1.0));
    return color;
  }


  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    translate = new vec2(translt);
    scale = sc;
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
      //	pVarTP.color=color.r;
      pVarTP.color = color.r * color.g;
      //	pVarTP.color=color.r*color.g*color.b;
    }
    pVarTP.x += pAmount * ((double) (i) / resolutionX - 0.5);
    pVarTP.y += pAmount * ((double) (j) / resolutionY - 0.5);

  }


  public String getName() {
    return "glsl_hoshi";
  }

  public String[] getParameterNames() {
    return paramNames;
  }


  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{resolutionX, seed, time, steps, sc, translt, gradient};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_RESOLUTIONX)) {
      resolutionX = (int) Tools.limitValue(pValue, 100, 10000000);
    } else if (PARAM_SEED.equalsIgnoreCase(pName)) {
      seed = (int) Tools.limitValue(pValue, 0, 10000);
      // seed=(int)ThreadLocalRandom.current().nextDouble(0.0, 10000.0);
      randomize = new Random((long) seed);
      time = random(0.0, 10000.0);
      // time=ThreadLocalRandom.current().nextDouble(0.0, 10000.0);
    } else if (pName.equalsIgnoreCase(PARAM_TIME)) {
      time = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_STEPS)) {
      steps = (int) Tools.limitValue(pValue, 1, 60);
    } else if (pName.equalsIgnoreCase(PARAM_SCALE)) {
      sc = Tools.limitValue(pValue, 1., 5.0);
    } else if (pName.equalsIgnoreCase(PARAM_TRANSLATE)) {
      translt = Tools.limitValue(pValue, 0., 5.0);
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

