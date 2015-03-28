package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.Layer;

import static org.jwildfire.base.mathlib.MathLib.sqrt;
import static org.jwildfire.base.mathlib.MathLib.sin;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.fabs;


/*
 *  Original author dark-beam
 *  code snippets relayed by Don Town
 *  transcribed and turned into full variation by CozyG
 */
public class FlowerDbFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PETALS = "petals";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_SPREAD = "spread";
  
  private static final String[] paramNames = { PARAM_PETALS, PARAM_THICKNESS, PARAM_SPREAD };

  private double petals = 6;
  private double thickness = 1;
  private double spread = 1;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
       double r =  pAmount *sqrt(pAffineTP.getPrecalcSumsq());
       double t = pAffineTP.getPrecalcAtanYX();
       r = r * (fabs ( (spread + sin(petals*t)) * cos(thickness*t) ) );
       pVarTP.x += sin(t) * r;
       pVarTP.y += cos(t) * r;
       pVarTP.z += (2.0 / r - 1.0);
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {

  }
  
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[] { petals, thickness, spread };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PETALS.equalsIgnoreCase(pName))
      petals = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = pValue;
    else if (PARAM_SPREAD.equalsIgnoreCase(pName))
      spread = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "flower_db";
  }
  
}
