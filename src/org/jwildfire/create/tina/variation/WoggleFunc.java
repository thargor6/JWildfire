package org.jwildfire.create.tina.variation;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class WoggleFunc extends VariationFunc implements SupportsGPU {

  /**
   * Woogle
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/woggle/roger14.c
   */

  private static final long serialVersionUID = 1L;

  private static final String PARAM_M = "m";

  int m = 2;
  private static final String[] paramNames = {PARAM_M};

  double[] a = new double[25];
  double[] b = new double[25];

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //       Woggle Reference:
    //		 http://paulbourke.net/fractals/woggle/roger14.c

    double x, y;

    double r = MathLib.sqrt(1.25) * MathLib.sqrt((double) m);
    int c = (int) ((double) m * pContext.random());
    double ra = 1.0 / (MathLib.sqrt(3.0) * MathLib.sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y));

    if (c % 2 == 0) {
      x = -pAffineTP.x / r + ra * pAffineTP.y / r + a[c];
      y = -ra * pAffineTP.x / r - pAffineTP.y / r + b[c];
    } else {
      x = pAffineTP.x / r + ra * pAffineTP.y / r + a[c];
      y = -ra * pAffineTP.x / r + pAffineTP.y / r + b[c];
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "woggle_js";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{m};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_M)) {
      m = (int) pValue;
      if (m < 2)
        m = 2;
      if (m > 12)
        m = 12;

      for (int i = 0; i < m; i++) {
        a[i] = cos(2 * M_PI * (double) i / (double) m);
        b[i] = sin(2 * M_PI * (double) i / (double) m);
      }
    } else
      throw new IllegalArgumentException(pName);
  }
  
  @Override
  public void randomize() {
  	setParameter(PARAM_M, Math.random() * 11 + 2);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float x, y;"
    		+"    float a[25];"  
    		+"    float b[25];"
    		+"    float r = sqrtf(1.25) * sqrtf(  __woggle_js_m );"
    		+"    int c = (int) ( __woggle_js_m  * RANDFLOAT());"
    		+"    for (int i = 0; i < (int) __woggle_js_m; i++) {" 
    		+"        a[i] = cosf(2.0 * PI * (float) i /  __woggle_js_m);" 
    		+"        b[i] = sinf(2.0 * PI * (float) i /  __woggle_js_m);" 
    		+"    }"
    		+"    float ra = 1.0 / (sqrtf(3.0) * sqrtf(__x * __x + __y * __y));"
    		+"    if (c % 2 == 0) {"
    		+"      x = -__x / r + ra * __y / r + a[c];"
    		+"      y = -ra * __x / r - __y / r + b[c];"
    		+"    } else {"
    		+"      x = __x / r + ra * __y / r  + a[c];"
    		+"      y = -ra * __x / r + __y /r  + b[c];"
    		+"    }"
    		+"    __px += x * __woggle_js;"
    		+"    __py += y * __woggle_js;"
            + (context.isPreserveZCoordinate() ? "__pz += __woggle_js *__z;" : "");
  }
}