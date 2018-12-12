/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Grid3DWFFunc extends VariationFunc {

  private static final long serialVersionUID = 1L;

  private static final String PARAM_SIZE = "size";
  private static final String PARAM_SIZE_SPREAD = "size_spread";
  private static final String PARAM_SPACING = "spacing";
  private static final String PARAM_ALPHA = "alpha";
  private static final String PARAM_ALPHA_SPREAD = "alpha_spread";
  private static final String PARAM_BETA = "beta";
  private static final String PARAM_BETA_SPREAD = "beta_spread";
  private static final String PARAM_GAMMA = "gamma";
  private static final String PARAM_GAMMA_SPREAD = "gamma_spread";
  private static final String PARAM_C1 = "c1";
  private static final String PARAM_C2 = "c2";
  private static final String PARAM_C3 = "c3";
  private static final String PARAM_C4 = "c4";
  private static final String PARAM_C5 = "c5";
  private static final String PARAM_C6 = "c6";

  private static final String[] paramNames = {PARAM_SIZE, PARAM_SIZE_SPREAD, PARAM_SPACING, PARAM_ALPHA, PARAM_ALPHA_SPREAD, PARAM_BETA, PARAM_BETA_SPREAD, PARAM_GAMMA, PARAM_GAMMA_SPREAD, PARAM_C1, PARAM_C2, PARAM_C3, PARAM_C4, PARAM_C5, PARAM_C6};

  private double size = 0.1;
  private double size_spread = 0.0;
  private double spacing = 0.75;
  private double alpha = 0.0;
  private double alpha_spread = 0.0;
  private double beta = 0.0;
  private double beta_spread = 0.0;
  private double gamma = 0.0;
  private double gamma_spread = 0.0;
  private double c1 = 0.1;
  private double c2 = 0.2;
  private double c3 = 0.3;
  private double c4 = 0.4;
  private double c5 = 0.5;
  private double c6 = 0.6;
  private int random_seed = 123;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* grid3d_wf by thargor6, inspired by dc_cube by Xyrus02 */
    int cxn = (int) (pAffineTP.x / size);
    int cyn = (int) (pAffineTP.y / size);
    int czn = (int) (pAffineTP.z / size);
    double sizeSpread = getSizeSpread(cxn, cyn, czn);
    double sizescl = spacing * (size + sizeSpread * 0.1);

    double dx = 0.0, dy = 0.0, dz = 0.0;

    switch (pContext.random(3)) {
      case 0:
        boolean left = pContext.random() < 0.5;
        dx = sizescl * (left ? -0.5 : 0.5);
        dy = sizescl * (pContext.random() - 0.5);
        dz = sizescl * (pContext.random() - 0.5);
        pVarTP.color = left ? c1 : c2;
        break;
      case 1:
        boolean top = pContext.random() < 0.5;
        dx = sizescl * (pContext.random() - 0.5);
        dy = sizescl * (top ? -0.5 : 0.5);
        dz = sizescl * (pContext.random() - 0.5);
        pVarTP.color = top ? c3 : c4;
        break;
      case 2:
        boolean front = pContext.random() < 0.5;
        dx = sizescl * (pContext.random() - 0.5);
        dy = sizescl * (pContext.random() - 0.5);
        dz = sizescl * (front ? -0.5 : 0.5);
        pVarTP.color = front ? c5 : c6;
        break;
      default: // nothing to do
        break;
    }

    if (doRotate) {
      double a = alpha + getAlphaSpread(cxn, cyn, czn);
      double b = beta + getBetaSpread(cxn, cyn, czn);
      double g = gamma + getGammaSpread(cxn, cyn, czn);
      double sina = sin(a);
      double cosa = cos(a);
      double sinb = sin(b);
      double cosb = cos(b);
      double sing = sin(g);
      double cosg = cos(g);

      double dxr = dx * (cosb * cosg) + dy * (cosg * sina * sinb - cosa * sing) + dz * (cosa * cosg * sinb + sina * sing);
      double dyr = dx * (cosb * sing) + dy * (cosa * cosg + sina * sinb * sing) + dz * (-cosg * sina + cosa * sinb * sing);
      double dzr = dx * (-sinb) + dy * (cosb * sina) + dz * (cosa * cosb);

      dx = dxr;
      dy = dyr;
      dz = dzr;
    }

    pVarTP.x += cxn * size + dx;
    pVarTP.y += cyn * size + dy;
    pVarTP.z += czn * size + dz;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{size, size_spread, spacing, alpha, alpha_spread, beta, beta_spread, gamma, gamma_spread, c1, c2, c3, c4, c5, c6};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_C1.equalsIgnoreCase(pName))
      c1 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C2.equalsIgnoreCase(pName))
      c2 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C3.equalsIgnoreCase(pName))
      c3 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C4.equalsIgnoreCase(pName))
      c4 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C5.equalsIgnoreCase(pName))
      c5 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_C6.equalsIgnoreCase(pName))
      c6 = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_SIZE.equalsIgnoreCase(pName))
      size = pValue;
    else if (PARAM_SIZE_SPREAD.equalsIgnoreCase(pName))
      size_spread = pValue;
    else if (PARAM_SPACING.equalsIgnoreCase(pName))
      spacing = pValue;
    else if (PARAM_ALPHA.equalsIgnoreCase(pName))
      alpha = pValue;
    else if (PARAM_ALPHA_SPREAD.equalsIgnoreCase(pName))
      alpha_spread = pValue;
    else if (PARAM_BETA.equalsIgnoreCase(pName))
      beta = pValue;
    else if (PARAM_BETA_SPREAD.equalsIgnoreCase(pName))
      beta_spread = pValue;
    else if (PARAM_GAMMA.equalsIgnoreCase(pName))
      gamma = pValue;
    else if (PARAM_GAMMA_SPREAD.equalsIgnoreCase(pName))
      gamma_spread = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "grid3d_wf";
  }

  private String getSizeSpreadMapKey() {
    return getName() + "#" + PARAM_SIZE_SPREAD + "#" + size_spread;
  }

  private String getAlphaSpreadMapKey() {
    return getName() + "#" + PARAM_ALPHA_SPREAD + "#" + alpha_spread;
  }

  private String getBetaSpreadMapKey() {
    return getName() + "#" + PARAM_BETA_SPREAD + "#" + beta_spread;
  }

  private String getGammaSpreadMapKey() {
    return getName() + "#" + PARAM_GAMMA_SPREAD + "#" + gamma_spread;
  }

  private String makeXYZKey(int pX, int pY, int pZ) {
    return pX + "#" + pY + "#" + pZ;
  }

  private double getSizeSpread(int pX, int pY, int pZ) {
    if (size_spread > MathLib.EPSILON) {
      @SuppressWarnings("unchecked")
      Map<String, Double> map = (Map<String, Double>) RessourceManager.getRessource(getSizeSpreadMapKey());
      String key = makeXYZKey(pX, pY, pZ);
      Double storedValue = map.get(key);
      if (storedValue == null) {
        double spread = -sizeSpreadRnd.nextDouble() * size_spread + size_spread;
        map.put(key, spread);
        return spread;
      } else {
        return storedValue.doubleValue();
      }
    }
    return 0.0;
  }

  private double getAlphaSpread(int pX, int pY, int pZ) {
    if (alpha_spread > MathLib.EPSILON) {
      @SuppressWarnings("unchecked")
      Map<String, Double> map = (Map<String, Double>) RessourceManager.getRessource(getAlphaSpreadMapKey());
      String key = makeXYZKey(pX, pY, pZ);
      Double storedValue = map.get(key);
      if (storedValue == null) {
        double spread = -alphaSpreadRnd.nextDouble() * alpha_spread + alpha_spread;
        map.put(key, spread);
        return spread;
      } else {
        return storedValue.doubleValue();
      }
    }
    return 0.0;
  }

  private double getBetaSpread(int pX, int pY, int pZ) {
    if (beta_spread > MathLib.EPSILON) {
      @SuppressWarnings("unchecked")
      Map<String, Double> map = (Map<String, Double>) RessourceManager.getRessource(getBetaSpreadMapKey());
      String key = makeXYZKey(pX, pY, pZ);
      Double storedValue = map.get(key);
      if (storedValue == null) {
        double spread = -betaSpreadRnd.nextDouble() * beta_spread + beta_spread;
        map.put(key, spread);
        return spread;
      } else {
        return storedValue.doubleValue();
      }
    }
    return 0.0;
  }

  private double getGammaSpread(int pX, int pY, int pZ) {
    if (gamma_spread > MathLib.EPSILON) {
      @SuppressWarnings("unchecked")
      Map<String, Double> map = (Map<String, Double>) RessourceManager.getRessource(getGammaSpreadMapKey());
      String key = makeXYZKey(pX, pY, pZ);
      Double storedValue = map.get(key);
      if (storedValue == null) {
        double spread = -gammaSpreadRnd.nextDouble() * gamma_spread + gamma_spread;
        map.put(key, spread);
        return spread;
      } else {
        return storedValue.doubleValue();
      }
    }
    return 0.0;
  }

  private Random sizeSpreadRnd, alphaSpreadRnd, betaSpreadRnd, gammaSpreadRnd;
  private boolean doRotate;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    doRotate = fabs(alpha) > EPSILON || fabs(beta) > EPSILON || fabs(gamma) > EPSILON || fabs(alpha_spread) > EPSILON || fabs(beta_spread) > EPSILON || fabs(gamma_spread) > EPSILON;

    if (RessourceManager.getRessource(getSizeSpreadMapKey()) == null)
      RessourceManager.putRessource(getSizeSpreadMapKey(), new ConcurrentHashMap<String, Double>());
    sizeSpreadRnd = new Random();
    sizeSpreadRnd.setSeed(random_seed);

    if (RessourceManager.getRessource(getAlphaSpreadMapKey()) == null)
      RessourceManager.putRessource(getAlphaSpreadMapKey(), new ConcurrentHashMap<String, Double>());
    alphaSpreadRnd = new Random();
    alphaSpreadRnd.setSeed(random_seed);

    if (RessourceManager.getRessource(getBetaSpreadMapKey()) == null)
      RessourceManager.putRessource(getBetaSpreadMapKey(), new ConcurrentHashMap<String, Double>());
    betaSpreadRnd = new Random();
    betaSpreadRnd.setSeed(random_seed);

    if (RessourceManager.getRessource(getGammaSpreadMapKey()) == null)
      RessourceManager.putRessource(getGammaSpreadMapKey(), new ConcurrentHashMap<String, Double>());
    gammaSpreadRnd = new Random();
    gammaSpreadRnd.setSeed(random_seed);
  }
}
