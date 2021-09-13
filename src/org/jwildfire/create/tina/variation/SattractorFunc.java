package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class SattractorFunc extends VariationFunc implements SupportsGPU {

  /**
   * Hennon IFS
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/peterdejong/
   */

  private static final long serialVersionUID = 1L;

  private static final String PARAM_M = "m";

  private static final String[] paramNames = {PARAM_M};

  double[] a = new double[13];
  double[] b = new double[13];

  int m = 10;

  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    for (int i = 1; i <= m; i++) {
      a[i] = cos(2.0 * M_PI * (double) i / (double) m);
      b[i] = sin(2.0 * M_PI * (double) i / (double) m);
    }
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //              Hennon IFS Reference:
    //				http://paulbourke.net/fractals/henonattractor/roger18.c

    double x, y;

    int l = (int) (pContext.random() * (double) m) + 1;

    if (pContext.random() < 0.5) {
      x = pAffineTP.x / 2.0 + a[l];
      y = pAffineTP.y / 2.0 + b[l];
    } else {
      x = pAffineTP.x * a[l] + pAffineTP.y * b[l] + pAffineTP.x * pAffineTP.x * b[l];
      y = pAffineTP.y * a[l] - pAffineTP.x * b[l] + pAffineTP.x * pAffineTP.x * a[l];
    }
    x /= 2.0;
    y /= 2.0;

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "sattractor_js";
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

      for (int i = 1; i <= m; i++) {
        a[i] = cos(2.0 * M_PI * (double) i / (double) m);
        b[i] = sin(2.0 * M_PI * (double) i / (double) m);
        //			    	  System.out.println("i , a[i] ,b[i] " + i + " , " + a[i] +" , "+ b[i]);
      }

    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "  float a[13];"
    		+"  float b[13];"
    		+"    for (int i = 1; i <= __sattractor_js_m; i++) {"
    		+"      a[i] = cosf(2.0 * PI * (float) i /  __sattractor_js_m);"
    		+"      b[i] = sinf(2.0 * PI * (float) i /  __sattractor_js_m);"
    		+"    }"
    		+"    float x, y;"
    		+"    int l = (int) (RANDFLOAT() *  __sattractor_js_m) + 1;"
    		+"    if (RANDFLOAT() < 0.5) {"
    		+"      x = __x / 2.0 + a[l];"
    		+"      y = __y / 2.0 + b[l];"
    		+"    } else {"
    		+"      x = __x * a[l] + __y * b[l] + __x * __x * b[l];"
    		+"      y = __y * a[l] - __x * b[l] + __x * __x * a[l];"
    		+"    }"
    		+"    x /= 2.0;"
    		+"    y /= 2.0;"
    		+"    __px += x * __sattractor_js;"
    		+"    __py += y * __sattractor_js;"
            + (context.isPreserveZCoordinate() ? "__pz += __sattractor_js *__z;" : "");
  }
}