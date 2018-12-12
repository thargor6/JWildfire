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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class ProjectiveFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "A";
  private static final String PARAM_B = "B";
  private static final String PARAM_C = "C";
  private static final String PARAM_A1 = "A1";
  private static final String PARAM_B1 = "B1";
  private static final String PARAM_C1 = "C1";
  private static final String PARAM_A2 = "A2";
  private static final String PARAM_B2 = "B2";
  private static final String PARAM_C2 = "C2";
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_A1, PARAM_B1, PARAM_C1, PARAM_A2,
          PARAM_B2, PARAM_C2};
  private double A = 0.0;
  private double B = 0.0;
  private double C = 1.0;
  private double A1 = 1.0;
  private double B1 = 0.0;
  private double C1 = 0.0;
  private double A2 = 0.0;
  private double B2 = 1.0;
  private double C2 = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        // Projective by eralex61
                        // https://www.deviantart.com/eralex61/art/Projective-transform-295252418
                        double pAmount) {
    double U;

    U = A * pAffineTP.x + B * pAffineTP.y + C;

    pVarTP.x += pAmount * (A1 * pAffineTP.x + B1 * pAffineTP.y + C1) / U;
    pVarTP.y += pAmount * (A2 * pAffineTP.x + B2 * pAffineTP.y + C2) / U;

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
    return new Object[]{A, B, C, A1, B1, C1, A2, B2, C2};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"pr_A", "pr_B", "pr_C", "pr_A1", "pr_B1", "pr_C1", "pr_A2", "pr_B2", "pr_C2"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName)) {
      A = pValue;
    } else if (PARAM_B.equalsIgnoreCase(pName)) {
      B = pValue;
    } else if (PARAM_C.equalsIgnoreCase(pName)) {
      C = pValue;
    } else if (PARAM_A1.equalsIgnoreCase(pName)) {
      A1 = pValue;
    } else if (PARAM_B1.equalsIgnoreCase(pName)) {
      B1 = pValue;
    } else if (PARAM_C1.equalsIgnoreCase(pName)) {
      C1 = pValue;
    } else if (PARAM_A2.equalsIgnoreCase(pName)) {
      A2 = pValue;
    } else if (PARAM_B2.equalsIgnoreCase(pName)) {
      B2 = pValue;
    } else if (PARAM_C2.equalsIgnoreCase(pName)) {
      C2 = pValue;
    } else {
      System.out.println("pName not recognized: " + pName);
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "projective";
  }

}
