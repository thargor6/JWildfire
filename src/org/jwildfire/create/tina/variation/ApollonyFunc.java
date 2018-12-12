package org.jwildfire.create.tina.variation;


import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ApollonyFunc extends SimpleVariationFunc {


  /**
   * Apollony IFS
   *
   * @author Jesus Sosa
   * @date January 22, 2018
   * based on a work of:
   * http://paulbourke.net/fractals/apollony/apollony.c
   */


  private static final long serialVersionUID = 1L;


  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
//               Apollony IFS Reference:
//				 http://paulbourke.net/fractals/apollony/apollony.c

    double x, y, a0, b0, f1x, f1y;
    double r = Math.sqrt(3.0);

    a0 = 3.0 * (1.0 + r - pAffineTP.x) / (Math.pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y) - (1.0 + r) / (2.0 + r);
    b0 = 3.0 * pAffineTP.y / (Math.pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y);
    f1x = a0 / (a0 * a0 + b0 * b0);
    f1y = -b0 / (a0 * a0 + b0 * b0);

    int w = (int) (4.0 * Math.random());

    if ((w % 3) == 0) {
      x = a0;
      y = b0;
    } else if ((w % 3) == 1) {
      x = -f1x / 2.0 - f1y * r / 2.0;
      y = f1x * r / 2.0 - f1y / 2.0;
    } else {
      x = -f1x / 2.0 + f1y * r / 2.0;
      y = -f1x * r / 2.0 - f1y / 2.0;
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  public String getName() {
    return "apollony";
  }
}