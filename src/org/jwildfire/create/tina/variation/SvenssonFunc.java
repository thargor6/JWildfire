package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class SvenssonFunc extends VariationFunc {

  /**
   * Jhonny Svensson Attractor
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of:
   * http://paulbourke.net/fractals/peterdejong/
   */

  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";

  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D};

  double a = 1.4;
  double b = 1.56;
  double c = 1.4;
  double d = -6.56;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Reference:
    //				  http://paulbourke.net/fractals/peterdejong/
    //				  xn+1 = d*sin(a xn) - sin(b xn)
    //				  yn+1 = c*cos(a xn) + cos(b yn)

    double x = d * sin(a * pAffineTP.x) - sin(b * pAffineTP.y);
    double y = c * cos(a * pAffineTP.x) + cos(b * pAffineTP.y);

    pVarTP.x = x * pAmount;
    pVarTP.y = y * pAmount;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "svensson_js";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{a, b, c, d};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_A)) {
      a = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_B)) {
      b = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_C)) {
      c = pValue;
    } else if (pName.equalsIgnoreCase(PARAM_D)) {
      d = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }
}
