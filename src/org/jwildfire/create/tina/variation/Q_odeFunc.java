/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2018 Andreas Maschke

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

public class Q_odeFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_Q_ODE01 = "q_ode01";
  private static final String PARAM_Q_ODE02 = "q_ode02";
  private static final String PARAM_Q_ODE03 = "q_ode03";
  private static final String PARAM_Q_ODE04 = "q_ode04";
  private static final String PARAM_Q_ODE05 = "q_ode05";
  private static final String PARAM_Q_ODE06 = "q_ode06";
  private static final String PARAM_Q_ODE07 = "q_ode07";
  private static final String PARAM_Q_ODE08 = "q_ode08";
  private static final String PARAM_Q_ODE09 = "q_ode09";
  private static final String PARAM_Q_ODE10 = "q_ode10";
  private static final String PARAM_Q_ODE11 = "q_ode11";
  private static final String PARAM_Q_ODE12 = "q_ode12";
  private static final String[] paramNames = {PARAM_Q_ODE01, PARAM_Q_ODE02, PARAM_Q_ODE03, PARAM_Q_ODE04,
          PARAM_Q_ODE05, PARAM_Q_ODE06, PARAM_Q_ODE07, PARAM_Q_ODE08, PARAM_Q_ODE09, PARAM_Q_ODE10, PARAM_Q_ODE11,
          PARAM_Q_ODE12};
  private double q_ode01 = 0.0;
  private double q_ode02 = 1.0;
  private double q_ode03 = 1.0 - 2.0 * Math.random();
  private double q_ode04 = 1.0 - 2.0 * Math.random();
  private double q_ode05 = 1.0 - 2.0 * Math.random();
  private double q_ode06 = 1.0 - 2.0 * Math.random();
  private double q_ode07 = 0.0;
  private double q_ode08 = 1.0;
  private double q_ode09 = 1.0 - 2.0 * Math.random();
  private double q_ode10 = 1.0 - 2.0 * Math.random();
  private double q_ode11 = 1.0 - 2.0 * Math.random();
  private double q_ode12 = 1.0 - 2.0 * Math.random();

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    // Q_ode by dark-beam
    // https://www.deviantart.com/dark-beam/art/q-ode-Apoplugin-Scripts-271504412
    // added randomization

    pVarTP.x += q_ode01 + pAmount * q_ode02 * pAffineTP.x + q_ode03 * pAffineTP.x * pAffineTP.x;
    pVarTP.x += q_ode04 * pAffineTP.x * pAffineTP.y + q_ode05 * pAffineTP.y + q_ode06 * pAffineTP.y * pAffineTP.y;
    pVarTP.y += q_ode07 + q_ode08 * pAffineTP.x + q_ode09 * pAffineTP.x * pAffineTP.x;
    pVarTP.y += q_ode10 * pAffineTP.x * pAffineTP.y + pAmount * q_ode11 * pAffineTP.y
            + q_ode12 * pAffineTP.y * pAffineTP.y;

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
    return new Object[]{q_ode01, q_ode02, q_ode03, q_ode04, q_ode05, q_ode06, q_ode07, q_ode08, q_ode09, q_ode10,
            q_ode11, q_ode12};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"q_ode01", "q_ode02", "q_ode03", "q_ode04", "q_ode05", "q_ode06", "q_ode07", "q_ode08",
            "q_ode09", "q_ode10", "q_ode11", "q_ode12"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_Q_ODE01.equalsIgnoreCase(pName)) {
      q_ode01 = pValue;
    } else if (PARAM_Q_ODE02.equalsIgnoreCase(pName)) {
      q_ode02 = pValue;
    } else if (PARAM_Q_ODE03.equalsIgnoreCase(pName)) {
      q_ode03 = pValue;
    } else if (PARAM_Q_ODE04.equalsIgnoreCase(pName)) {
      q_ode04 = pValue;
    } else if (PARAM_Q_ODE05.equalsIgnoreCase(pName)) {
      q_ode05 = pValue;
    } else if (PARAM_Q_ODE06.equalsIgnoreCase(pName)) {
      q_ode06 = pValue;
    } else if (PARAM_Q_ODE07.equalsIgnoreCase(pName)) {
      q_ode07 = pValue;
    } else if (PARAM_Q_ODE08.equalsIgnoreCase(pName)) {
      q_ode08 = pValue;
    } else if (PARAM_Q_ODE09.equalsIgnoreCase(pName)) {
      q_ode09 = pValue;
    } else if (PARAM_Q_ODE10.equalsIgnoreCase(pName)) {
      q_ode10 = pValue;
    } else if (PARAM_Q_ODE11.equalsIgnoreCase(pName)) {
      q_ode11 = pValue;
    } else if (PARAM_Q_ODE12.equalsIgnoreCase(pName)) {
      q_ode12 = pValue;
    } else {
      System.out.println("pName not recognized: " + pName);
      throw new IllegalArgumentException(pName);
    }
  }

  @Override
  public String getName() {
    return "q_ode";
  }

}
