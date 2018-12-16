package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.mat3;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Random;


public class GLSLGrid3DFunc extends GLSLFunc {

  /*
   * Variation : glsl_grid3D
   * Date: October 31, 2018
   * Reference
   */


  private static final long serialVersionUID = 1L;

  private static final String PARAM_RESOLUTIONX = "Density Pixels";
  private static final String PARAM_SEED = "Seed";
  private static final String PARAM_TIME = "time";


  int resolutionX = 1000000;
  int resolutionY = resolutionX;
  private int seed = 10000;
  double time = 200.;


  Random randomize = new Random(seed);


  private static final String[] paramNames = {PARAM_RESOLUTIONX, PARAM_SEED, PARAM_TIME};

  public double random(double r1, double r2) {
    return r1 + (r2 - r1) * randomize.nextDouble();
  }


  public vec3 field(vec3 p) {
    p = p.multiply(0.1);
    double f = .1;
    for (int i = 0; i < 3; i++) {
      p = new vec3(p.y, p.z, p.x); //*mat3(.8,.6,0,-.6,.8,0,0,0,1);
//				p += vec3(.123,.456,.789)*float(i);
      p = G.abs(G.fract(p).minus(0.5));
      p = p.multiply(2.0);
      f *= 2.0;
    }
    p = p.multiply(p);
    return G.sqrt(p.add(new vec3(p.y, p.z, p.x))).division(f).minus(.05);
  }

  public vec3 getRGBColor(int xp, int yp) {
    double x = (double) xp + 0.5;
    double y = (double) yp + 0.5;

    // a raymarching experiment by kabuto
    //fork by tigrou ind (2013.01.22)
    // slow mod by kapsy1312.tumblr.com


    int MAXITER = 30;

    vec3 dir = G.normalize(new vec3(x / resolutionX - .5, y / resolutionY - .5, 1.));
    double a = time * 0.021;
    vec3 pos = new vec3(0.0, time * 0.1, 0.0);

    dir = dir.times(new mat3(1, 0, 0, 0, G.cos(a), -G.sin(a), 0, G.sin(a), G.cos(a)));
    dir = dir.times(new mat3(G.cos(a), 0, -G.sin(a), 0, 1, 0, G.sin(a), 0, G.cos(a)));
    vec3 color = new vec3(0);

    for (int i = 0; i < MAXITER; i++) {
      vec3 f2 = field(pos);
      double f = G.min(G.min(f2.x, f2.y), f2.z);

      pos = pos.add(dir.multiply(f));
      vec3 t0 = new vec3(((double) MAXITER - i)).division(f2.add(0.1));
      color = color.add(t0);
    }
    vec3 color3 = new vec3(1.0).minus(new vec3(1.0).division((new vec3(1.0).add(color.multiply(.09 / (double) (MAXITER * MAXITER))))));

    // color3 = color3.multiply(color3);
    return new vec3(color3.r + color3.g + color3.b);
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
    return "glsl_grid3D";
  }

  public String[] getParameterNames() {
    return paramNames;
  }


  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{resolutionX, seed, time};
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

