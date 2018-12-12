/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser 
  General Public License as published by the Free Software Foundation; either version 2.1 of the 
  License, or (qat your option) any later version.
 
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

import static org.jwildfire.base.mathlib.MathLib.sqr;

public class MobiqFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_AT = "qat";
  private static final String PARAM_AX = "qax";
  private static final String PARAM_AY = "qay";
  private static final String PARAM_AZ = "qaz";
  private static final String PARAM_BT = "qbt";
  private static final String PARAM_BX = "qbx";
  private static final String PARAM_BY = "qby";
  private static final String PARAM_BZ = "qbz";
  private static final String PARAM_CT = "qct";
  private static final String PARAM_CX = "qcx";
  private static final String PARAM_CY = "qcy";
  private static final String PARAM_CZ = "qcz";
  private static final String PARAM_DT = "qdt";
  private static final String PARAM_DX = "qdx";
  private static final String PARAM_DY = "qdy";
  private static final String PARAM_DZ = "qdz";

  private static final String[] paramNames = {PARAM_AT, PARAM_AX, PARAM_AY, PARAM_AZ, PARAM_BT, PARAM_BX, PARAM_BY, PARAM_BZ, PARAM_CT, PARAM_CX, PARAM_CY, PARAM_CZ, PARAM_DT, PARAM_DX, PARAM_DY, PARAM_DZ};

  private double qat = 1.0;
  private double qax = 0.0;
  private double qay = 0.0;
  private double qaz = 0.0;
  private double qbt = 0.0;
  private double qbx = 0.0;
  private double qby = 0.0;
  private double qbz = 0.0;
  private double qct = 0.0;
  private double qcx = 0.0;
  private double qcy = 0.0;
  private double qcz = 0.0;
  private double qdt = 1.0;
  private double qdx = 0.0;
  private double qdy = 0.0;
  private double qdz = 0.0;


  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Mobiq by zephyrtronium converted to work in JWildfire by Brad Stefanov
	/*  Qlib uses the notation T + X i + Y j + Z k, so I used the following while
    simplifying. I took a usual Mobius transform (ax + b) / (cx + d) and made
    a, b, c, and d quaternions instead of just complex numbers, with x being
    a quaternion FTx + FTy i + FTz j + 0 k. Multiplying quaternions happens to
    be a very complex thing, and dividing is no simpler, so I needed to use
    names that are easily and quickly typed while simplifying to prevent
    massive insanity/violence/genocide. I then found switching back from those
    names in my head to be rather difficult and confusing, so I decided to
    use these macros instead.
*/

    double t1 = qat;
    double t2 = pAffineTP.x;
    double t3 = qbt;
    double t4 = qct;
    double t5 = qdt;
    double x1 = qax;
    double x2 = pAffineTP.y;
    double x3 = qbx;
    double x4 = qcx;
    double x5 = qdx;
    double y1 = qay;
    double y2 = pAffineTP.z;
    double y3 = qby;
    double y4 = qcy;
    double y5 = qdy;
    double z1 = qaz;
    /* z2 is 0 and simplified out (there is no fourth generated coordinate). */
    double z3 = qbz;
    double z4 = qcz;
    double z5 = qdz;

    double nt = t1 * t2 - x1 * x2 - y1 * y2 + t3;
    double nx = t1 * x2 + x1 * t2 - z1 * y2 + x3;
    double ny = t1 * y2 + y1 * t2 + z1 * x2 + y3;
    double nz = z1 * t2 + x1 * y2 - y1 * x2 + z3;
    double dt = t4 * t2 - x4 * x2 - y4 * y2 + t5;
    double dx = t4 * x2 + x4 * t2 - z4 * y2 + x5;
    double dy = t4 * y2 + y4 * t2 + z4 * x2 + y5;
    double dz = z4 * t2 + x4 * y2 - y4 * x2 + z5;
    double ni = pAmount / (sqr(dt) + sqr(dx) + sqr(dy) + sqr(dz));


    pVarTP.x += (nt * dt + nx * dx + ny * dy + nz * dz) * ni;
    pVarTP.y += (nx * dt - nt * dx - ny * dz + nz * dy) * ni;
    pVarTP.z += (ny * dt - nt * dy - nz * dx + nx * dz) * ni;


  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{qat, qax, qay, qaz, qbt, qbx, qby, qbz, qct, qcx, qcy, qcz, qdt, qdx, qdy, qdz};
  }


  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_AT.equalsIgnoreCase(pName))
      qat = pValue;
    else if (PARAM_AX.equalsIgnoreCase(pName))
      qax = pValue;
    else if (PARAM_AY.equalsIgnoreCase(pName))
      qay = pValue;
    else if (PARAM_AZ.equalsIgnoreCase(pName))
      qaz = pValue;
    else if (PARAM_BT.equalsIgnoreCase(pName))
      qbt = pValue;
    else if (PARAM_BX.equalsIgnoreCase(pName))
      qbx = pValue;
    else if (PARAM_BY.equalsIgnoreCase(pName))
      qby = pValue;
    else if (PARAM_BZ.equalsIgnoreCase(pName))
      qbz = pValue;
    else if (PARAM_CT.equalsIgnoreCase(pName))
      qct = pValue;
    else if (PARAM_CX.equalsIgnoreCase(pName))
      qcx = pValue;
    else if (PARAM_CY.equalsIgnoreCase(pName))
      qcy = pValue;
    else if (PARAM_CZ.equalsIgnoreCase(pName))
      qcz = pValue;
    else if (PARAM_DT.equalsIgnoreCase(pName))
      qdt = pValue;
    else if (PARAM_DX.equalsIgnoreCase(pName))
      qdx = pValue;
    else if (PARAM_DY.equalsIgnoreCase(pName))
      qdy = pValue;
    else if (PARAM_DZ.equalsIgnoreCase(pName))
      qdz = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "mobiq";
  }

}
