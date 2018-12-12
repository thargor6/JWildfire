package org.jwildfire.create.tina.variation;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ApocarpetFunc extends SimpleVariationFunc {

  /**
   * Roger Bagula Function
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/kissingcircles/roger17.c
   */

  private static final long serialVersionUID = 1L;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Apolonian Carpet Reference:
    //	http://paulbourke.net/fractals/kissingcircles/roger17.c

    double x = 0, y = 0;

    double r = 1.0 / (1.0 + MathLib.sqrt(2.0));

    double denom = pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y;

    int weight = (int) (6.0 * pContext.random());

    switch (weight) {
      case 0:
        x = 2.0 * pAffineTP.x * pAffineTP.y / denom;
        y = (pAffineTP.x * pAffineTP.x - pAffineTP.y * pAffineTP.y) / denom;
        break;
      case 1:
        x = pAffineTP.x * r - r;
        y = pAffineTP.y * r - r;
        break;
      case 2:
        x = pAffineTP.x * r + r;
        y = pAffineTP.y * r + r;
        break;
      case 3:
        x = pAffineTP.x * r + r;
        y = pAffineTP.y * r - r;
        break;
      case 4:
        x = pAffineTP.x * r - r;
        y = pAffineTP.y * r + r;
        break;
      case 5:
        x = (pAffineTP.x * pAffineTP.x - pAffineTP.y * pAffineTP.y) / denom;
        y = 2.0 * pAffineTP.x * pAffineTP.y / denom;
        break;
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

    //			    pVarTP.color = fmod(fabs( (sqr(pVarTP.x) + sqr(pVarTP.y ))), 1.0);
  }

  public String getName() {
    return "apocarpet_js";
  }
}