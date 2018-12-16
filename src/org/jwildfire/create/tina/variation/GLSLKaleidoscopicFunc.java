package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Random;


public class GLSLKaleidoscopicFunc extends GLSLFunc {

  /*
   * Variation : glsl_kaleidoscopic
   * Date: October 31, 2018
   * Reference
   */


  private static final long serialVersionUID = 1L;

  private static final String PARAM_RESOLUTIONX = "Density Pixels";
  private static final String PARAM_SEED = "Seed";
  private static final String PARAM_TIME = "time";
  private static final String PARAM_NSIDES = "Sides";
  private static final String PARAM_ZOOM = "zoom";
  private static final String PARAM_P1 = "P1";
  private static final String PARAM_RADIAL = "Radial";

  private static final String PARAM_GRADIENT = "Gradient";

  private int seed = 10000;
  int resolutionX = 1000000;
  int resolutionY = resolutionX;
  double time = 0.0;
  int sides = 8;
  double zoom = 0.0;
  double p1 = 0.0;
  int radial = 0;

  int gradient = 0;
  Random randomize = new Random(seed);

  double KA = Math.PI / sides;


  private static final String[] paramNames = {PARAM_RESOLUTIONX, PARAM_SEED, PARAM_TIME, PARAM_NSIDES, PARAM_ZOOM, PARAM_P1, PARAM_RADIAL, PARAM_GRADIENT};

  public double random(double r1, double r2) {
    return r1 + (r2 - r1) * randomize.nextDouble();
  }


  //----------------------------------------------------------------
  // transformation to koleidoscopic coordinates
  //----------------------------------------------------------------
  public vec2 koleidoscope(vec2 uv) {
    // get the angle in radians of the current coords relative to origin (i.e. center of screen)
    double angle = G.atan2(uv.y, uv.x);
    // repeat image over evenly divided rotations around the center
    angle = G.mod(angle, 2.0 * KA);
    // reflect the image within each subdivision to create a tilelable appearance
    angle = G.abs(angle - KA);
    // rotate image over time
    angle += 0.1 * time;
    // get the distance of the coords from the uv origin (i.e. center of the screen)
    double d = G.length(uv);
    // map the calculated angle to the uv coordinate system at the given distance
    vec2 uvr = new vec2(G.cos(angle), G.sin(angle)).multiply(2.0);

    return uvr.multiply(d);
  }

  //----------------------------------------------------------------
  // equal to koleidoscope, but more compact
  //----------------------------------------------------------------
  public vec2 smallKoleidoscope(vec2 uv) {

    double angle = G.abs(G.mod(G.atan2(uv.y, uv.x), 2.0 * KA) - KA) + 0.1 * time;
    vec2 uvr = new vec2(G.cos(angle), G.sin(angle)).multiply(G.length(uv));
    return uvr;
  }


  public vec3 getRGBColor(int xp, int yp) {
    double x = (double) xp + 0.5;
    double y = (double) yp + 0.5;

    vec2 uv = new vec2(2.0 * x / resolutionX - 1.0, 2.0 * y / resolutionY - 1.0).multiply(12.0);


    //	  uv.x += 2.*G.sin(2.*time);

    uv = uv.multiply(0.1 + zoom); // zoom from 0 to 1

    if (radial == 1)
      uv = smallKoleidoscope(uv);
    // uv=koleidoscope(uv);  // does the same as smallKoleidoscope(uv)

    // Fractal Colors by Robert Schütze (trirop): http://glslsandbox.com/e#29611
    vec3 p = new vec3(uv, zoom);
    for (int i = 0; i < 44; i++) {
//     p.xzy = vec3(1.3,0.999,0.678)*(abs((abs(p)/dot(p,p)-vec3(1.0,1.02,mouse.y*0.4))));
      vec3 t1 = G.abs(p).division(G.dot(p, p));
      vec3 t0 = t1.minus(new vec3(1.0, 1.02, p1 * 0.4));

      vec3 t2 = new vec3(1.3, 0.999, 0.678).multiply(G.abs(t0));

      p = new vec3(t2.x, t2.z, t2.y);
    }

    return p;
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
    return "glsl_kaleidoscopic";
  }

  public String[] getParameterNames() {
    return paramNames;
  }


  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{resolutionX, seed, time, sides, zoom, p1, radial, gradient};
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
    } else if (pName.equalsIgnoreCase(PARAM_NSIDES)) {
      sides = (int) Tools.limitValue(pValue, 2, 20);
      KA = Math.PI / sides;
    } else if (pName.equalsIgnoreCase(PARAM_ZOOM)) {
      zoom = Tools.limitValue(pValue, 0.0, 1.0);
    } else if (pName.equalsIgnoreCase(PARAM_P1)) {
      p1 = Tools.limitValue(pValue, 0.0, 1.0);
    } else if (pName.equalsIgnoreCase(PARAM_RADIAL)) {
      radial = (int) Tools.limitValue(pValue, 0, 1);
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

