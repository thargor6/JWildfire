package org.jwildfire.create.tina.variation;

import org.jwildfire.base.mathlib.DoubleWrapperWF;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class VogelFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_N = "n";
  private static final String PARAM_SCALE = "scale";
  private static final String[] paramNames = {PARAM_N, PARAM_SCALE};

  private final double M_PHI = 1.61803398874989484820;
  private final double M_2PI_PHI2 = M_2PI / (M_PHI * M_PHI);

  private final DoubleWrapperWF sina = new DoubleWrapperWF();
  private final DoubleWrapperWF cosa = new DoubleWrapperWF();

  private int n = 20;
  private double scale = 1.;

  @Override
  public String getName() {
    return "vogel";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{n, scale};
  }

  @Override
  public void setParameter(final String pName, final double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName)) {
      n = limitIntVal(Tools.FTOI(pValue), 1, Integer.MAX_VALUE);
    } else if (PARAM_SCALE.equalsIgnoreCase(pName)) {
      scale = pValue;
    } else {
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Vogel function by Victor Ganora
    final int i = pContext.random(n) + 1;
    final double a = i * M_2PI_PHI2;
    sinAndCos(a, sina, cosa);
    final double r = pAmount * (pAffineTP.getPrecalcSqrt() + sqrt(i));
    pVarTP.x += r * (cosa.value + (scale * pAffineTP.x));
    pVarTP.y += r * (sina.value + (scale * pAffineTP.y));
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }
}
