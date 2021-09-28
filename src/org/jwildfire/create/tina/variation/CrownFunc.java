package org.jwildfire.create.tina.variation;

import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class CrownFunc extends VariationFunc implements SupportsGPU {

  /**
   * Roger Bagula Function
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/crown/
   */

  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";

  private static final String[] paramNames = {PARAM_A, PARAM_B};

  double a = 5, b = Math.log(2) / Math.log(3);

  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Roger Bagula  Reference:
    //	http://paulbourke.net/fractals/crown/

    double x, y, z;

    double t = (-M_PI + 2.0 * M_PI * pContext.random());

    Complex wt = new Complex(0.0, 0.0);

    for (int k = 1; k < 15; k++) {
      double denom = MathLib.pow(a, b * k);
      double sign= (k%2==0)?-1.0:1.0; 
      double th = MathLib.pow(a, (double) k)* t *sign;
      wt.Add(new Complex(sin(th) / denom, cos(th) / denom));
    }
    x = wt.re;
    y = wt.im;
    z = MathLib.pow(wt.Mag2(), 2);

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;
    pVarTP.z += z * pAmount;

    pVarTP.color = fmod(fabs((sqr(pVarTP.x) + sqr(pVarTP.y))), 1.0);
  }

  public String getName() {
    return "crown_js";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{a, b};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_A)) {
      a = pValue;

    } else if (pName.equalsIgnoreCase(PARAM_B)) {
      b = pValue;

    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float x, y, z;"
    		+"    float t = (-PI + 2.0 * PI * RANDFLOAT());"
    		+"    Complex wt;"
    		+"    Complex_Init(&wt,0.0, 0.0);"
    		+"    Complex tmp;"
    		+"    for (int k = 1; k < 15; k++) {"
    		+"      float denom = powf( __crown_js_a , __crown_js_b  * (float) k);"
    		+"      float sign=(k%2==0)?-1.0:1.0;"
    		+"      float th = powf(__crown_js_a, (float) k)*t*sign;"
    		+ "     Complex_Init(&tmp,sinf(th) / denom, cosf(th) / denom);"
    		+"      Complex_Add(&wt,&tmp );"
    		+"    }"
    		+"    x = wt.re;"
    		+"    y = wt.im;"
    		+"    float m=Complex_Mag2(&wt);"
    		+"    z = powf(m, 2.0);"
    		+"    __px += x * __crown_js;"
    		+"    __py += y * __crown_js;"
    		+"    __pz += z * __crown_js;"
    		+"    __pal = fmodf(fabsf((__px*__px + __py*__py)), 1.0);";
  }
}

