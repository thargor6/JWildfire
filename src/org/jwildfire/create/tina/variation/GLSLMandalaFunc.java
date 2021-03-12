package org.jwildfire.create.tina.variation;

import js.glsl.G;
import js.glsl.vec2;
import js.glsl.vec3;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;


public class GLSLMandalaFunc extends GLSLFunc {

  /*
   * Variation : glsl_mandala
   *
   * Autor: Jesus Sosa
   * Date: October 31, 2018
   * Reference
   */


  private static final long serialVersionUID = 1L;

  private static final String PARAM_RESOLUTIONX = "Density Pixels";
  private static final String PARAM_MX = "mX";

  private static final String PARAM_MY = "mY";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_SIDES = "sides";
  private static final String PARAM_MULTIPLY = "multiply";
  private static final String PARAM_LOOPS = "loops";
  private static final String PARAM_IR = "iR";
  private static final String PARAM_IG = "iG";
  private static final String PARAM_IB = "iB";
  private static final String PARAM_GRADIENT = "Gradient";

  int resolutionX = 1000000;
  int resolutionY = resolutionX;
  double mX = 0.025;
  double mY = -0.001245675;
  double scale = 2.;
  double sides = 12.;
  double multiply = 1.5;
  double loops = 64.;

  int iR = 0;
  int iG = 0;
  int iB = 1;
  int gradient = 0;

  private static final String[] paramNames = {PARAM_RESOLUTIONX, PARAM_MX, PARAM_MY, PARAM_SCALE, PARAM_SIDES, PARAM_MULTIPLY, PARAM_LOOPS, PARAM_IR, PARAM_IG, PARAM_IB, PARAM_GRADIENT};

	    



/*	@Override
	public void initOnce(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
		super.initOnce(pContext, pLayer, pXForm, pAmount);

	}	 

	@Override
	public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) 
	{

	}*/


  public vec3 getRGBColor(int i, int j) {
    double xt = (double) i + 0.5;
    double yt = (double) j + 0.5;


    double rcpi = 0.318309886183791;

    vec2 uv = new vec2(0.0, 0.0);
    uv.x = (5.5 - scale) * (2.0 * xt / resolutionX - 1.0);
    uv.y = (5.5 - scale) * (2.0 * yt / resolutionY - 1.0);

    double k = Math.PI / Math.floor(sides);
    vec2 s = G.Kscope(uv, k);
    vec2 t = G.Kscope(s, k);
    double v = G.dot(t, s);
    vec2 u = G.mix(s, t, Math.cos(v));

    if (multiply > 0.001) {
      vec2 t1 = new vec2(u.y, u.x);
      vec2 t2 = G.mod(t1, Math.floor(multiply));
      vec2 t3 = new vec2(-u.x, -u.y);
      vec2 t4 = new vec2(u.y, u.x);
      vec2 t5 = t4.plus(G.mod(t2, t3));
      u = new vec2(t5.y, t5.x);
    }
    vec3 p = new vec3(u, mX * v);
    for (int l = 0; l < 73; l++) {
      if ((double) l > Math.floor(loops)) {
        break;
      }
      vec3 t1 = new vec3(1.3, 0.999, 0.678);
      vec3 t2 = G.abs(p).division(G.dot(p, p)).minus(new vec3(1.0, 1.02, mY * rcpi));
      vec3 t3 = G.abs(t2);
      vec3 t4 = t1.multiply(t3);
      p = new vec3(t4.x, t4.z, t4.y);
    }
    if (iR == 1) {
      p.x = 1.0 - p.x;
    }
    if (iG == 1) {
      p.y = 1.0 - p.y;
    }
    if (iB == 1) {
      p.z = 1.0 - p.z;
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
      //	pVarTP.color=color.r;
      pVarTP.color = color.r * color.g;
      //	pVarTP.color=color.r*color.g*color.b;
    }
    pVarTP.x += pAmount * ((double) (i) / resolutionX - 0.5);
    pVarTP.y += pAmount * ((double) (j) / resolutionY - 0.5);

  }


  public String getName() {
    return "glsl_mandala";
  }

  public String[] getParameterNames() {
    return paramNames;
  }


  public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
    return new Object[]{resolutionX, mX, mY, scale, sides, multiply, loops, iR, iG, iB, gradient};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_RESOLUTIONX)) {
      resolutionX = (int) Tools.limitValue(pValue, 100, 10000000);
    } else if (pName.equalsIgnoreCase(PARAM_MX)) {
      mX = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_MY)) {
      mY = pValue;
      ;
    } else if (pName.equalsIgnoreCase(PARAM_SCALE)) {
      scale = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_SIDES)) { // 0.0- 2*Math.PI radians
      sides = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_MULTIPLY)) {
      multiply = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_LOOPS)) {
      loops = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_IR)) {
      iR = (int) Tools.limitValue(pValue, 0, 1);
    } else if (pName.equalsIgnoreCase(PARAM_IG)) {
      iG = (int) Tools.limitValue(pValue, 0, 1);
    } else if (pName.equalsIgnoreCase(PARAM_IB)) {
      iB = (int) Tools.limitValue(pValue, 0, 1);
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

