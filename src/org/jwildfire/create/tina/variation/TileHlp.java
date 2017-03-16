package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.cos;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;


public class TileHlp extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_EDGE = "edge";
  private static final String[] paramNames = { PARAM_WIDTH, PARAM_EDGE };

  private double width = 1.0;
  private double edge = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

    double width2 = width * pAmount;
    double x = pAffineTP.x / width;
    double aux = 0;
    if (x > 0.0) {
      aux = x - (int) x;
    }
    else {
      aux = x + (int) x;
    }
    aux = cos (aux);
    double aux2 = 0;
    if (aux2 < pContext.random() * 2.0 - 1.0) {
      if (x > 0) {
        aux2 = -width2;
      }
      else {
        aux2 = width2;
      }
    }

    pVarTP.x += pAffineTP.x * pAmount + aux2;
    pVarTP.y += pAmount * pAffineTP.y;
if (pContext.isPreserveZCoordinate()) {
  pVarTP.z += pAmount * pAffineTP.z;
}
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { width, edge };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_WIDTH.equalsIgnoreCase(pName))
      width = pValue;
    else if (PARAM_EDGE.equalsIgnoreCase(pName))
      edge = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "tile_hlp";
  }

}