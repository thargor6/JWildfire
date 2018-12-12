package org.jwildfire.create.tina.variation;


import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class InvSquircularFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double u = pAffineTP.x;
    double v = pAffineTP.y;
    double r = u * u + v * v;
    r = Math.sqrt((r - u * u * v * v) / r);
    pVarTP.x += pAmount * u * r;
    pVarTP.y += pAmount * v * r;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "invsquircular";
  }


}
