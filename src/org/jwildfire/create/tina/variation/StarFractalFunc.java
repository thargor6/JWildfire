package org.jwildfire.create.tina.variation;


import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Random;

public class StarFractalFunc extends VariationFunc {
  /**
   * Star Fractal
   *
   * @author Jesus Sosa
   * @date August 14, 2018
   * based on a work of:
   * http://paulbourke.net/fractals/star/index.html
   * parameters
   */


  private static final long serialVersionUID = 1L;


  private static final String PARAM_M = "m";
  private static final String[] paramNames = {PARAM_M};


  int m = 3;


  private double[] a;
  private double[] b;
  double x = 1, y = 1, x1, y1;

  Random randomize = new Random(1000);

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    a = new double[25];
    b = new double[25];

    for (int i = 0; i < m; i++) {
      a[2 * i] = Math.cos(2 * Math.PI * (i + 1) / (double) m);
      b[2 * i] = Math.sin(2 * Math.PI * (i + 1) / (double) m);
    }
    for (int i = 0; i < m; i++) {
      a[2 * i + 1] = 0.5 * (Math.cos(2 * Math.PI * (i + 1) / (double) m) + Math.cos(2 * Math.PI * i / (double) m));
      b[2 * i + 1] = 0.5 * (Math.sin(2 * Math.PI * (i + 1) / (double) m) + Math.sin(2 * Math.PI * i / (double) m));
    }
  }

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    int c;

    c = (int) (Math.abs(randomize.nextInt()) % (2 * m));
    x1 = (x / (x * x + y * y)) / 3 + a[c];
    y1 = (-y / (x * x + y * y)) / 3 + b[c];
    x = x1 / (x1 * x1 + y1 * y1);
    y = y1 / (x1 * x1 + y1 * y1);

    pVarTP.x = x * pAmount;
    pVarTP.y = y * pAmount;

  }

  public String getName() {
    return "starfractal";
  }

  public String[] getParameterNames() {
    return paramNames;
  }

  public Object[] getParameterValues() {
    return new Object[]{m};
  }

  public void setParameter(String pName, double pValue) {
    if (pName.equalsIgnoreCase(PARAM_M)) {
      m = (int) Tools.limitValue(pValue, 3, 10);
    } else
      throw new IllegalArgumentException(pName);
  }
}
