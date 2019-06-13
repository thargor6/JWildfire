package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;


import static org.jwildfire.base.mathlib.MathLib.*;

public class Waves3Func extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scalex";
  private static final String PARAM_SCALEY = "scaley";
  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";
  private static final String PARAM_SX_FREQ = "sx_freq";
  private static final String PARAM_SY_FREQ = "sy_freq";
  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALEY, PARAM_FREQX, PARAM_FREQY, PARAM_SX_FREQ, PARAM_SY_FREQ};

  private double scalex = 0.05;
  private double scaley = 0.05;
  private double freqx = 7.0;
  private double freqy = 13.0;
  private double sx_freq = 0.0;
  private double sy_freq = 2.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* waves3 from Tatyana Zabanova converted by Brad Stefanov https://www.deviantart.com/tatasz/art/Weird-Waves-Plugin-Pack-1-783560564*/
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double scalexx = 0.5 * scalex * (1.0 + sin(y0 * sx_freq));
	double scaleyy = 0.5 * scaley * (1.0 + sin(x0 * sy_freq));
    pVarTP.x += pAmount * (x0 + sin(y0 * freqx) * scalexx);
    pVarTP.y += pAmount * (y0 + sin(x0 * freqy) * scaleyy);
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
    return new Object[]{scalex, scaley, freqx, freqy, sx_freq, sy_freq};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scalex = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaley = pValue;
    else if (PARAM_FREQX.equalsIgnoreCase(pName))
      freqx = pValue;
    else if (PARAM_FREQY.equalsIgnoreCase(pName))
      freqy = pValue;
    else if (PARAM_SX_FREQ.equalsIgnoreCase(pName))
      sx_freq = pValue;
    else if (PARAM_SY_FREQ.equalsIgnoreCase(pName))
      sy_freq = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "waves3";
  }
	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will make them refresh
		return true;
	}

}
