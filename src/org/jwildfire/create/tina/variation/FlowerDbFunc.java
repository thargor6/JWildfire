package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

/*
 *  Original author dark-beam
 *       (see JWildfire forum post: http://jwildfire.org/forum/viewtopic.php?f=18&t=1444&p=3032)
 *  suggested/requested as full variation by Don Town
 *  transcribed, extended, and turned into full variation by CozyG
 *
 *  WARNING: assumes centered on (0,0,0), can disapear if move too far off in pre-transforms etc.
 */
public class FlowerDbFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_PETALS = "petals";
  private static final String PARAM_PETAL_SPLIT = "petal_split";
  private static final String PARAM_PETAL_SPREAD = "petal_spread";
  private static final String PARAM_STEM_THICKNESS = "stem_thickness";
  private static final String PARAM_STEM_LENGTH = "stem_length";
  private static final String PARAM_PETAL_FOLD_STRENGTH = "petal_fold_strength";
  private static final String PARAM_PETAL_FOLD_RADIUS = "petal_fold_radius";

  private static final String[] paramNames = {PARAM_PETALS, PARAM_PETAL_SPLIT, PARAM_PETAL_SPREAD, PARAM_STEM_THICKNESS, PARAM_STEM_LENGTH, PARAM_PETAL_FOLD_STRENGTH, PARAM_PETAL_FOLD_RADIUS};

  // non-integer petals is possible, changes relative size of petals
  private double petals = 6;
  // non-integer petal_splits are possible, changes relative size of petals
  private double petal_split = 0;
  private double petal_spread = 1;
  private double stem_thickness = 1;
  // default stem_length of 0 is considered special case,
  //   if stem_length = 0, then stem length doesn't truncate, so extends "infinitely" while fading away
  private double stem_length = 0;
  // how strong (added angle) the fold is, positive value fold up, negative values fold down
  private double petal_fold_strength = 0;
  // how far out along the petals the fold happens
  private double petal_fold_radius = 1;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double r = pAmount * sqrt(pAffineTP.getPrecalcSumsq());
    double t = pAffineTP.getPrecalcAtanYX();
    r = r * (fabs((petal_spread + sin(petals * t)) * cos(petal_split * petals * t)));

    pVarTP.x += sin(t) * r;
    pVarTP.y += cos(t) * r;
    pVarTP.z -= stem_thickness * ((2.0 / r) - 1.0);
    double rnew = sqrt((pVarTP.x * pVarTP.x) + (pVarTP.y * pVarTP.y));
    if (rnew > petal_fold_radius) {
      pVarTP.z += (rnew - petal_fold_radius) * petal_fold_strength;
    }
    if ((stem_length != 0) && (pVarTP.z <= (-1 * stem_length))) {
      pVarTP.z = (-1 * stem_length);
    }

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
    return new Object[]{petals, petal_split, petal_spread, stem_thickness, stem_length, petal_fold_strength, petal_fold_radius};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_PETALS.equalsIgnoreCase(pName))
      petals = pValue;
    else if (PARAM_PETAL_SPLIT.equalsIgnoreCase(pName))
      petal_split = pValue;
    else if (PARAM_PETAL_SPREAD.equalsIgnoreCase(pName))
      petal_spread = pValue;
    else if (PARAM_STEM_THICKNESS.equalsIgnoreCase(pName))
      stem_thickness = pValue;
    else if (PARAM_STEM_LENGTH.equalsIgnoreCase(pName))
      stem_length = pValue;
    else if (PARAM_PETAL_FOLD_STRENGTH.equalsIgnoreCase(pName))
      petal_fold_strength = pValue;
    else if (PARAM_PETAL_FOLD_RADIUS.equalsIgnoreCase(pName))
      petal_fold_radius = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "flower_db";
  }

}
