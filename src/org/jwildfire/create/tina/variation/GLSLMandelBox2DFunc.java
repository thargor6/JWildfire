package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import js.glsl.vec4;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Random;


public class GLSLMandelBox2DFunc extends GLSLFunc {

  /*
   * Variation : glsl_mandelbox2D
   * Autor: Jesus Sosa
   * Date: October 31, 2018
   * Reference
   */


  private static final long serialVersionUID = 1L;

  private static final String PARAM_RESOLUTIONX = "Density Pixels";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_TIME = "time";
  private static final String PARAM_GRADIENT = "Gradient";

  int resolutionX = 1000000;
  int resolutionY = resolutionX;
  private int seed = 10000;
  double time = 0.0;

  int gradient = 0;
  Random randomize = new Random(seed);

  private static final String[] paramNames = {PARAM_RESOLUTIONX, PARAM_SEED, PARAM_TIME, PARAM_GRADIENT};

  public double random(double r1, double r2) {
    return r1 + (r2 - r1) * randomize.nextDouble();
  }

  public vec3 getRGBColor(int xp, int yp) {
    double x = (double) xp + 0.5;
    double y = (double) yp + 0.5;
    vec2 I = new vec2(7 * (x + x - resolutionX) / resolutionX, 7 * (y + y - resolutionY) / resolutionY);

    vec4 O = new vec4(I, -5. * G.cos(time * .1), 1.);

    double d = 1.;
    for (int i = 0; i < 20; i++) { //this mandelbox loop should also work in 3D(with a proper DE (initialize d at O.z,remove the +1.,and the factor 5 at the end)
      I = G.clamp(I, -1., 1.).multiply(2.).minus(I);//boxfold
      double b = (O.a = G.length(I)) < .5 ? 4. : O.a < 1. ? 1. / O.a : 1.;//ballfold
      I = I.multiply(O.z * b).plus(new vec2(O.x, O.y)); //scaling
      d = b * d * G.abs(O.z) + 1.;//bound distance estimation
    }
    d = Math.pow(G.length(I) / d, .1) * 5.;
    O = new vec4(G.cos(d), G.sin(10. * d + 1.), G.cos(3. * d + 1.), 0).multiply(0.5).add(0.5);
    return new vec3(O.r, O.g, O.b);
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
    return "glsl_mandelbox2D";
  }

  public String[] getParameterNames() {
    return paramNames;
  }


  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{resolutionX, seed, time, gradient};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_RESOLUTIONX)) {
      resolutionX = (int) Tools.limitValue(pValue, 100, 10000000);
    } else if (pName.equalsIgnoreCase(PARAM_SEED)) {
      seed = (int) Tools.limitValue(pValue, 0, 10000);
      randomize = new Random((long) seed);
      time = random(0.0, 10000.0);
    } else if (pName.equalsIgnoreCase(PARAM_TIME)) {
      time = pValue;
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

