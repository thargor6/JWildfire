package org.jwildfire.create.tina.variation;

import org.jwildfire.base.mathlib.Complex;
import static org.jwildfire.base.mathlib.MathLib.sqrt;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class PlusRecipFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_AR = "ar";
  private static final String PARAM_AI = "ai";

  private static final String[] paramNames = {PARAM_AR, PARAM_AI};

  private double ar = 4.0;
  private double ai = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // author DarkBeam. Implemented by DarkBeam 2019
    Complex z = new Complex(pAffineTP.x, pAffineTP.y);
    Complex k = new Complex(z);
    Complex a = new Complex(ar, ai);
    double aa = sqrt(a.Mag2eps());
    k.Sqr();
    k.Sub(a);
    k.Sqrt();
    k.Add(z);
    z.Copy(k);
    z.Sqr();

    if (sqrt(z.Mag2eps()) < aa) {  // forces it to stay in its half plane but ONLY when imag(a) = 0. Else... ;)
      k.Conj();
      a.Scale(-1.0 / aa);
      k.Mul(a);
    }

    if (k.re < 0) {
      k.Neg(); // fixes the issue when imag(a) != 0.
    }

    k.Scale(pAmount);
    pVarTP.x += k.re;
    pVarTP.y += k.im;
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
    return new Object[]{ar, ai};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_AR.equalsIgnoreCase(pName))
      ar = pValue;
    else if (PARAM_AI.equalsIgnoreCase(pName))
      ai = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "plusrecip";
  }

}