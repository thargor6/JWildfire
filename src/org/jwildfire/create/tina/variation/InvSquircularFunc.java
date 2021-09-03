package org.jwildfire.create.tina.variation;


import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class InvSquircularFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (pAmount != 0) {
      double u = pAffineTP.x;
      double v = pAffineTP.y;
      double r = u * u + v * v;
      //  r = Math.sqrt((r-u*u*v*v)/r);
      double r2 = Math.sqrt(r * (pAmount * pAmount * r - 4 * u * u * v * v) / pAmount);
      r = Math.sqrt(r - r2) / MathLib.M_SQRT2;
      pVarTP.x += r / u;
      pVarTP.y += r / v;
    }
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "invsquircular";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D,VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float M_SQRT2 = sqrtf(2.0);"
    		+"    if (__invsquircular != 0.0) {"
    		+"      float u = __x;"
    		+"      float v = __y;"
    		+"      float r = u * u + v * v;"
    		+"      "
    		+"      float r2 = sqrtf(r * (__invsquircular * __invsquircular * r - 4.0 * u * u * v * v) / __invsquircular);"
    		+"      r = sqrtf(r - r2) / M_SQRT2;"
    		+"      __px += r / u;"
    		+"      __py += r / v;"
    		+"    }"
    		+ (context.isPreserveZCoordinate() ? "__pz += __invsquircular *__z;" : "");
  }

}
