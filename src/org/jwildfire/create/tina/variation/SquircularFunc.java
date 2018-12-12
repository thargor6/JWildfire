package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class SquircularFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {


    double u = pAffineTP.x;
    double v = pAffineTP.y;
    double r = u * u + v * v;
    double rs = Math.sqrt(r);
    double xs = u > 0 ? 1.0 : -1.0;

    r = Math.sqrt(pAmount * pAmount * r - 4 * u * u * v * v);
    r = Math.sqrt(1 + u * u / (v * v) - rs / (pAmount * v * v) * r);
    r = r / Math.sqrt(2);

    pVarTP.x += xs * r;
    pVarTP.y += v / u * r;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "squircular";
  }


}
