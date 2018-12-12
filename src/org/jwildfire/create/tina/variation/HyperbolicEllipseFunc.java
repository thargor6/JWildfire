package org.jwildfire.create.tina.variation;


import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class HyperbolicEllipseFunc extends VariationFunc {
  /**
   *
   */

  static public class XY {
    public double x;
    public double y;

    public XY(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }

  private static final long serialVersionUID = 1L;


  private static final String PARAM_A = "a";


  private static final String[] paramNames = {PARAM_A};


  double a = 1.0;


  XY Eval(double x, double y) {
    XY p;
    double xt, yt;

    xt = ((Math.exp(x) - Math.exp(-x)) / 2) * Math.cos(a * y);
    yt = ((Math.exp(x) + Math.exp(-x)) / 2) * Math.sin(a * y);

    p = new XY(xt, yt);
    return (p);
  }


  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /*

     */
    double u;
    double v;
    u = pAffineTP.x;
    v = pAffineTP.y;

    XY p = Eval(u, v);

    pVarTP.x = p.x * pAmount;
    pVarTP.y = p.y * pAmount;

  }

  public String getName() {
    return "hyperbolicellipse";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{a};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_A)) {
      a = pValue;
    } else
      throw new IllegalArgumentException(pName);
  }
}
