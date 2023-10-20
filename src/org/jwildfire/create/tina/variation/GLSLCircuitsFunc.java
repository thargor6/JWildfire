package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.mat2;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class GLSLCircuitsFunc extends GLSLFunc {

  /*
   * Variation : glsl_circuits
   * Autor: Jesus Sosa
   * Date: October 31, 2018
   * Reference
   */


  private static final long serialVersionUID = 1L;

  private static final String PARAM_RESOLUTIONX = "Density Pixels";
  private static final String PARAM_TIME = "time";
  private static final String PARAM_RATE = "rate";
  private static final String PARAM_INTENSITY = "intensity";
  private static final String PARAM_FOCUS = "focus";
  private static final String PARAM_PULSE = "pulse";
  private static final String PARAM_GLOW = "glow";
  private static final String PARAM_LOOPS = "loops";
  private static final String PARAM_ZOOM = "zoom";
  private static final String PARAM_GRADIENT = "Gradient";

  int resolutionX = 1000000;
  int resolutionY = resolutionX;
  double time = 0.0;
  double rate = .8;
  double intensity = 0.9;
  double focus = 1.5;
  double pulse = 10.;
  double glow = 2.;
  double loops = 15.;
  double zoom = 1.0;
  int gradient = 0;


  private static final String[] paramNames = {PARAM_RESOLUTIONX, PARAM_TIME, PARAM_RATE, PARAM_INTENSITY, PARAM_FOCUS, PARAM_PULSE, PARAM_GLOW, PARAM_LOOPS, PARAM_ZOOM, PARAM_GRADIENT};


  double S = (101.0 + glow) * intensity;

  public vec3 formula(vec2 z, double t) {
    double M = 0.0;
    double o, ot2, ot = ot2 = 1000.0;
    double K = G.floor(loops / 4.0 + G.floor(5.0 * zoom));
    vec3 color = new vec3(0.0);

    for (int i = 0; i < 11; i++) {
      z = G.abs(z).division(G.clamp(G.dot(z, z), 0.1, 0.5)).minus(t);
      double l = G.length(z);
      o = G.min(G.max(G.abs(G.min(z.x, z.y)), -l + 0.25), G.abs(l - 0.25));
      ot = G.min(ot, o);
      ot2 = G.min(l * 0.1, ot2);
      M = G.max(M, (double) i * (1.0 - G.abs(G.sign(ot - o))));
      if (K <= 0.0)
        break;
      K -= 1.0;
    }
    M += 1.0;
    double w = (intensity * zoom) * M;
    double circ = Math.pow(G.max(0.0, w - ot2) / w, 6.0);
    S += G.max(Math.pow(G.max(0.0, w - ot) / w, 0.25), circ);
    vec3 t1 = new vec3(0.1).add(new vec3(0.45, 0.75, M * 0.1));
    vec3 col = G.normalize(t1);
    vec3 t2 = new vec3(0.4 + G.mod(M / 9.0 - t * pulse + ot2 * 2.0, 1.));
    color = color.add(col.multiply(t2));
    double f1 = circ * (10.0 - M) * 3.0;
    color = color.add(new vec3(1.0, 0.7, 0.3).multiply(f1));
    return color;
  }


  public vec3 getRGBColor(int xp, int yp) {
    double xt = (double) xp + 0.5;
    double yt = (double) yp + 0.5;
    vec2 pos = new vec2(xt / resolutionX - 0.5, yt / resolutionY - 0.5);
    vec3 color = new vec3(0.0);
    vec2 center = new vec2(0.0);
    double R = 0.0;
    double N = time * 0.01 * rate;

    double T = 2.0 * rate;
    if (N > 6.0 * rate) {
      R += 1.0;
      N -= (R * 8.0 * rate);
    }
    if (N < 4.0 * rate) T += N;
    else T = 8.0 * rate - N;
    double Z = (1.05 - zoom);

    vec2 uv = pos.plus(center);
    double sph = G.length(uv) * 0.1;
    sph = Math.sqrt(1.0 - sph * sph) * 2.0;
    double a = T * Math.PI;
    double b = a + T;
    double c = G.cos(a) + G.sin(b);
    uv = uv.times(new mat2(G.cos(b), G.sin(b), -G.sin(b), G.cos(b)));
    uv = uv.times(new mat2(G.cos(a), -G.sin(a), G.sin(a), G.cos(a)));
    uv = uv.minus(new vec2(G.sin(c), G.cos(c)).division(Math.PI));
    uv = uv.multiply(Z);
    double pix = 0.5 / resolutionX * Z / sph;
    double dof = (zoom * focus) + (T * 0.25);
    double L = G.floor(loops);
    for (int aa = 0; aa < 24; aa++) {
      vec2 aauv = G.floor(new vec2((double) aa / 6.0, G.mod((double) aa, 6.0)));
      color = formula(uv.plus(aauv.multiply(pix).multiply(dof)), T);
      if (L <= 0.0)
        break;
      L -= 1.0;
    }
    S /= G.floor(loops);
    color = color.division(G.floor(loops));
    vec3 colo = G.mix(new vec3(0.15), color, S).multiply((1.0 - G.length(pos)));
    colo = colo.multiply(new vec3(1.2, 1.1, 1.0));

    return colo;
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
    return "glsl_circuits";
  }

  public String[] getParameterNames() {
    return paramNames;
  }


  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{resolutionX, time, rate, intensity, focus, pulse, glow, loops, zoom, gradient};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_RESOLUTIONX)) {
      resolutionX = (int) Tools.limitValue(pValue, 100, 10000000);
    } else if (pName.equalsIgnoreCase(PARAM_TIME)) {
      time = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_RATE)) {
      rate = pValue;
      ;
    } else if (pName.equalsIgnoreCase(PARAM_INTENSITY)) {
      intensity = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_FOCUS)) { // 0.0- 2*Math.PI radians
      focus = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_PULSE)) {
      pulse = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_GLOW)) {
      glow = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_LOOPS)) {
      loops = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
      zoom = pValue;
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

