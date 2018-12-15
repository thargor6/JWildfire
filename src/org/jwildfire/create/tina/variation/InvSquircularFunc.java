package org.jwildfire.create.tina.variation;


import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class InvSquircularFunc extends SimpleVariationFunc {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
	  if (pAmount != 0) {
	    double u = pAffineTP.x;
	    double v = pAffineTP.y;
	    double r = u*u+v*v;
	  //  r = Math.sqrt((r-u*u*v*v)/r);
	    double r2 = Math.sqrt(r*(pAmount*pAmount*r-4*u*u*v*v)/pAmount); 
	    r = Math.sqrt(r-r2)/MathLib.M_SQRT2;
	    pVarTP.x += r/u;
	    pVarTP.y += r/v;
	}
    if (pContext.isPreserveZCoordinate()) {
       pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "invsquircular";
  }


}
