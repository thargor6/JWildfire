package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class HadamardFunc extends SimpleVariationFunc {

  /**
   * Hadamard IFS
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/pascaltriangle/roger9.c
   */

  private static final long serialVersionUID = 1L;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    //               Hadamard IFS Reference:
    //				 http://paulbourke.net/fractals/pascaltriangle/roger18.c

    double x, y;

    if (pContext.random() < 0.333) {
      x = pAffineTP.x / 2.0;
      y = pAffineTP.y / 2.0;
    } else if (pContext.random() < 0.666) {
      x = pAffineTP.y / 2.0;
      y = -pAffineTP.x / 2.0 - 0.5;
    } else {
      x = -pAffineTP.y / 2.0 - 0.5;
      y = pAffineTP.x / 2.0;
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "hadamard_js";
  }
}