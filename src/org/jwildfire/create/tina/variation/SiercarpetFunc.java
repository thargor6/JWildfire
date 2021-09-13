package org.jwildfire.create.tina.variation;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class SiercarpetFunc extends VariationFunc implements SupportsGPU {

  /**
   * Cross Carpet by Bagula
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/crosscarpet/roger21.basic
   */

  private static final long serialVersionUID = 1L;

  private static final String PARAM_M = "m";

  private static final String[] paramNames = {PARAM_M};

  double[] a = new double[25];
  double[] b = new double[25];

  int m = 3;
  int d = 0;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //    Cross Carpet by Roger Bagula  Reference:
    //	http://paulbourke.net/fractals/crosscarpet/roger21.basic

    double x = 1.0, y = 1.0;

    for (int i = 1; i <= m; i++) {
      a[2 * i - 1] = cos(2.0 * M_PI * (double) i / (double) m);
      b[2 * i - 1] = sin(2.0 * M_PI * (double) i / (double) m);
    }
    for (int i = 1; i <= m; i++) {
      a[2 * i] = (cos(2.0 * M_PI * i / (double) m) + cos(2.0 * M_PI * (i - 1) / (double) m)) / 2.0;
      b[2 * i] = (sin(2.0 * M_PI * i / (double) m) + sin(2.0 * M_PI * (i - 1) / (double) m)) / 2.0;
    }

    int l = 1 + (int) (2.0 * (double) m * pContext.random());

 //   if (d % (2 * m) != (5) % 2 * m) {
      d = 1;
      x = pAffineTP.x / 3.0 + (a[l] - b[l]) / MathLib.sqrt(2.0);
      y = pAffineTP.y / 3.0 + (a[l] + b[l]) / MathLib.sqrt(2.0);
//    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

    //			    pVarTP.color = fmod(fabs( (sqr(pVarTP.x) + sqr(pVarTP.y ))), 1.0);
  }

  public String getName() {
    return "siercarpet_js";
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
      if (m < 3)
        m = 3;
      if (m > 12)
        m = 12;
    } else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float x = 1.0, y = 1.0;"
    		+ "   float a[25],b[25];"
    		+"    for (int i = 1; i <=  __siercarpet_js_m ; i++) {"
    		+"      a[2 * i - 1] = cosf(2.0 * PI * (float) i / (float)  __siercarpet_js_m );"
    		+"      b[2 * i - 1] = sinf(2.0 * PI * (float) i / (float)  __siercarpet_js_m );"
    		+"    }"
    		+"    for (int i = 1; i <=  __siercarpet_js_m ; i++) {"
    		+"      a[2 * i] = (cosf(2.0 * PI * i / (float)  __siercarpet_js_m ) + cosf(2.0 * PI * (i - 1) / (float)  __siercarpet_js_m )) / 2.0;"
    		+"      b[2 * i] = (sinf(2.0 * PI * i / (float)  __siercarpet_js_m ) + sinf(2.0 * PI * (i - 1) / (float)  __siercarpet_js_m )) / 2.0;"
    		+"    }"
    		+"    int l = 1 + (int) (2.0 *  __siercarpet_js_m  * RANDFLOAT());"
    		+"      x = __x / 3.0 + (a[l] - b[l]) / sqrtf(2.0);"
    		+"      y = __y / 3.0 + (a[l] + b[l]) / sqrtf(2.0);"
    		+"    __px += x * __siercarpet_js;"
    		+"    __py += y * __siercarpet_js;"
            + (context.isPreserveZCoordinate() ? "__pz += __siercarpet_js *__z;" : "");
  }
}
