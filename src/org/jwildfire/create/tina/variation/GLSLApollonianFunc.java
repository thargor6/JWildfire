package org.jwildfire.create.tina.variation;

import java.util.Random;
import js.glsl.G;
import js.glsl.mat3;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class GLSLApollonianFunc extends GLSLFunc {

  /*
   * Variation : glsl_Apollonian
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

  public vec3 getRGBColor(int i, int j) {
    double x = (double) i + 0.5;
    double y = (double) j + 0.5;
    vec2 uv = new vec2(2.0 * x / resolutionX - 1.0, 2.0 * y / resolutionY - 1.0);
    vec3 col = new vec3(0.0);

    double t = 0.05 * time;

    mat3 m = G.rot(new vec3(t).add(new vec3(1, 2, 3)));
    double k = 1.2 + 0.1 * G.sin(0.1 * time);

    double f1 = (2. + 0.25 * G.sin(0.3 * time));
    vec2 v2 = new vec2(2.0).multiply(uv);
    vec3 v = m.times(f1).times(new vec3(v2, 0.0));
    vec3 v3 = G.sin(G.app(v, k, m));
    col = v3.multiply(new vec3(0.6)).add(new vec3(0.5));

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
      //	pVarTP.color=color.r;
      pVarTP.color = color.r * color.g;
      //	pVarTP.color=color.r*color.g*color.b;
    }
    pVarTP.x += pAmount * ((double) (i) / resolutionX - 0.5);
    pVarTP.y += pAmount * ((double) (j) / resolutionY - 0.5);

  }


  public String getName() {
    return "glsl_apollonian";
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
    } else if (PARAM_SEED.equalsIgnoreCase(pName)) {
      seed = (int) pValue;
      randomize = new Random(seed);
      time = 5000.0 * randomize.nextDouble();
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
    return new VariationFuncType[]{VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE};
  }

}

