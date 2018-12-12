package org.jwildfire.create.tina.variation;


/**
 * Symetric Icon Attractors
 *
 * @author Jesus Sosa
 * @date January 11 , 2018
 * Attractors came from the book  “Symmetry in Chaos” by Michael Field and Martin Golubitsky
 * Reference:
 * https://softologyblog.wordpress.com/2017/03/04/2d-strange-attractors/
 */

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class IconAttractorFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;
  private static final String PARAM_PRESETID = "presetId";
  private static final String PARAM_DEGREE = "degree";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_G = "g";
  private static final String PARAM_O = "o";
  private static final String PARAM_L = "l";


  private static final String PARAM_CENTERX = "centerx";
  private static final String PARAM_CENTERY = "centery";
  private static final String PARAM_SCALE = "scale";


  private static final String[] paramNames = {PARAM_PRESETID, PARAM_DEGREE, PARAM_A, PARAM_B, PARAM_G, PARAM_O, PARAM_L, PARAM_CENTERX, PARAM_CENTERY, PARAM_SCALE};


  int[] pdeg = {5, 3, 5, 3, 3, 9, 6, 23, 7, 5, 5, 5, 4, 3, 3, 3, 16};
  double[] pa = {5.0, -1.0, 1.806, 10.0, -2.5, 3.0, 5.0, -2.5, 1.0, 2.32, -2.0, 2.0, 2.0, -1.0, -1.0, -1.0, -2.5};
  double[] pb = {-1.9, 0.1, 0.0, -12.0, 0.0, -16.79, 1.5, 0.0, -0.1, 0.0, 0.0, 0.2, 0.0, 0.1, 0.1, 0.03, -0.1};
  double[] pg = {1.0, -0.82, 1.0, 1.0, 0.9, 1.0, 1.0, 0.9, 0.167, 0.75, -0.5, 0.1, 1.0, -0.82, -0.805, -0.80, 0.90};
  double[] po = {0.188, 0.12, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1, 0.0, 0.0, 0.0, -0.15};
  double[] pl = {-2.5, 1.56, -1.806, -2.195, 2.5, -2.05, -2.7, 2.409, -2.08, -2.32, 2.6, -2.34, -1.86, 1.56, 1.5, 1.455, 2.39};

  int presetId = (int) (pdeg.length * Math.random());


  int degree = pdeg[presetId];
  double a = pa[presetId];
  double b = pb[presetId];
  double g = pg[presetId];
  double o = po[presetId];
  double l = pl[presetId];


  private double centerx = 0.0;
  private double centery = 0.0;
  private double scale = 5.0;
  private double bdcs;

  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    bdcs = 1.0 / (scale == 0.0 ? 10E-6 : scale);
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
// Reference:
//	https://softologyblog.wordpress.com/2017/03/04/2d-strange-attractors/
    double x, y;


    double zzbar = sqr(pAffineTP.x) + sqr(pAffineTP.y);
    double p = a * zzbar + l;

    double zreal = pAffineTP.x;
    double zimag = pAffineTP.y;

    for (int i = 1; i <= degree - 2; i++) {
      double za = zreal * pAffineTP.x - zimag * pAffineTP.y;
      double zb = zimag * pAffineTP.x + zreal * pAffineTP.y;
      zreal = za;
      zimag = zb;
    }
    double zn = pAffineTP.x * zreal - pAffineTP.y * zimag;
    p = p + b * zn;

    x = p * pAffineTP.x + g * zreal - o * pAffineTP.y;
    y = p * pAffineTP.y - g * zimag + o * pAffineTP.x;


    pVarTP.x = x * pAmount;
    pVarTP.y = y * pAmount;

    pVarTP.color = fmod(fabs(bdcs * (sqr(pVarTP.x + centerx) + sqr(pVarTP.y + centery))), 1.0);

  }

  public String getName() {
    return "iconattractor_js";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{presetId, degree, a, b, g, o, l, centerx, centery, scale};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_PRESETID)) {
      presetId = Tools.limitValue((int) pValue, 0, pdeg.length - 1);
      degree = pdeg[presetId];
      a = pa[presetId];
      b = pb[presetId];
      g = pg[presetId];
      o = po[presetId];
      l = pl[presetId];
    } else if (pName.equalsIgnoreCase(PARAM_DEGREE)) {
      degree = Tools.limitValue((int) pValue, 3, 50);
    } else if (pName.equalsIgnoreCase(PARAM_A)) {
//		      a = Tools.limitValue(pValue, -3.0, 3.0);
      a = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_B)) {
//	      b = Tools.limitValue(pValue, -3.0, 3.0);
      b = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_G)) {
//		      g = Tools.limitValue(pValue, -3.0, 3.0);
      g = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_O)) {
//		      o = Tools.limitValue(pValue, -3.0, 3.0);
      o = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_L)) {
//		      l = Tools.limitValue(pValue, -3.0, 3.0);
      l = pValue;
    } else if (PARAM_CENTERX.equalsIgnoreCase(pName))
      centerx = pValue;
    else if (PARAM_CENTERY.equalsIgnoreCase(pName))
      centery = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else
      throw new IllegalArgumentException(pName);
  }
}



