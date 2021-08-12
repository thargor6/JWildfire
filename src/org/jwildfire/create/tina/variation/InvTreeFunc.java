package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class InvTreeFunc extends SimpleVariationFunc implements SupportsGPU {

  /**
   * Inverse Tree IFS
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/trifraction2/
   */

  private static final long serialVersionUID = 1L;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //               Inverse Tree IFS Reference:
    //				 http://paulbourke.net/fractals/trifraction2/

    double x, y;

    if (pContext.random() < 0.333) {
      x = pAffineTP.x / 2.0;
      y = pAffineTP.y / 2.0;
    } else if (pContext.random() < 0.666) {
      x = 1.0 / (pAffineTP.x + 1);
      y = pAffineTP.y / (pAffineTP.y + 1.0);
    } else {
      x = pAffineTP.x / (pAffineTP.x + 1);
      y = 1.0 / (pAffineTP.y + 1.0);
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "invtree_js";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float x, y;"
    		+"    if (RANDFLOAT() < 0.333) {"
    		+"      x = __x / 2.0;"
    		+"      y = __y / 2.0;"
    		+"    } else if (RANDFLOAT() < 0.666) {"
    		+"      x = 1.0 / (__x + 1);"
    		+"      y = __y / (__y + 1.0);"
    		+"    } else {"
    		+"      x = __x / (__x + 1);"
    		+"      y = 1.0 / (__y + 1.0);"
    		+"    }"
    		+"    __px += x * __invtree_js;"
    		+"    __py += y * __invtree_js;"
            + (context.isPreserveZCoordinate() ? "__pz += __invtree_js * __z;" : "");
  }
}