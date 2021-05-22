/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
  General Public License as published by the Free Software Foundation; either version 2.1 of the
  License, or (at your option) any later version.

  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software;
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
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
 *  WARNING: assumes centered on (0,0,0), can disappear if move too far off in pre-transforms etc.
 */
public class FlowerDbFunc extends VariationFunc implements SupportsGPU {
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

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float r = varpar->flower_db * sqrtf(__r2);\n"
        + "float t = __theta;\n"
        + "r = r * (fabsf((varpar->flower_db_petal_spread + sinf(varpar->flower_db_petals * t)) * cosf(varpar->flower_db_petal_split * varpar->flower_db_petals * t)));\n"
        + "__px += sinf(t) * r;\n"
        + "__py += cosf(t) * r;\n"
        + "__pz -= varpar->flower_db_stem_thickness * ((2.0f / r) - 1.0f);\n"
        + "float rnew = sqrtf((__px * __px) + (__py * __py));\n"
        + "if (rnew > varpar->flower_db_petal_fold_radius) {\n"
        + "   __pz += (rnew - varpar->flower_db_petal_fold_radius) * varpar->flower_db_petal_fold_strength;\n"
        + "}\n"
        + "if ((varpar->flower_db_stem_length != 0) && (__pz <= (-1 * varpar->flower_db_stem_length))) {\n"
        + "  __pz = (-1 * varpar->flower_db_stem_length);\n"
        + "}";
  }
}
