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

import odk.lang.FastMath;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class QuaternionFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_COSQPOW = "cosqpow";
  private static final String PARAM_COSQX1 = "cosqx1";
  private static final String PARAM_COSQX2 = "cosqx2";
  private static final String PARAM_COSQY1 = "cosqy1";
  private static final String PARAM_COSQY2 = "cosqy2";
  private static final String PARAM_COSQZ1 = "cosqz1";
  private static final String PARAM_COSQZ2 = "cosqz2";

  private static final String PARAM_COSHQPOW = "coshqpow";
  private static final String PARAM_COSHQX1 = "coshqx1";
  private static final String PARAM_COSHQX2 = "coshqx2";
  private static final String PARAM_COSHQY1 = "coshqy1";
  private static final String PARAM_COSHQY2 = "coshqy2";
  private static final String PARAM_COSHQZ1 = "coshqz1";
  private static final String PARAM_COSHQZ2 = "coshqz2";

  private static final String PARAM_COTQPOW = "cotqpow";
  private static final String PARAM_COTQX1 = "cotqx1";
  private static final String PARAM_COTQX2 = "cotqx2";
  private static final String PARAM_COTQY1 = "cotqy1";
  private static final String PARAM_COTQY2 = "cotqy2";
  private static final String PARAM_COTQZ1 = "cotqz1";
  private static final String PARAM_COTQZ2 = "cotqz2";

  private static final String PARAM_COTHQPOW = "cothqpow";
  private static final String PARAM_COTHQX1 = "cothqx1";
  private static final String PARAM_COTHQX2 = "cothqx2";
  private static final String PARAM_COTHQY1 = "cothqy1";
  private static final String PARAM_COTHQY2 = "cothqy2";
  private static final String PARAM_COTHQZ1 = "cothqz1";
  private static final String PARAM_COTHQZ2 = "cothqz2";

  private static final String PARAM_CSCQPOW = "cscqpow";
  private static final String PARAM_CSCQX1 = "cscqx1";
  private static final String PARAM_CSCQX2 = "cscqx2";
  private static final String PARAM_CSCQY1 = "cscqy1";
  private static final String PARAM_CSCQY2 = "cscqy2";
  private static final String PARAM_CSCQZ1 = "cscqz1";
  private static final String PARAM_CSCQZ2 = "cscqz2";

  private static final String PARAM_CSCHQPOW = "cschqpow";
  private static final String PARAM_CSCHQX1 = "cschqx1";
  private static final String PARAM_CSCHQX2 = "cschqx2";
  private static final String PARAM_CSCHQY1 = "cschqy1";
  private static final String PARAM_CSCHQY2 = "cschqy2";
  private static final String PARAM_CSCHQZ1 = "cschqz1";
  private static final String PARAM_CSCHQZ2 = "cschqz2";

  private static final String PARAM_ESTIQPOW = "estiqpow";
  private static final String PARAM_ESTIQX1 = "estiqx1";
  private static final String PARAM_ESTIQY1 = "estiqy1";
  private static final String PARAM_ESTIQY2 = "estiqy2";
  private static final String PARAM_ESTIQZ1 = "estiqz1";
  private static final String PARAM_ESTIQZ2 = "estiqz2";

  private static final String PARAM_LOGQPOW = "logqpow";
  private static final String PARAM_LOGQBASE = "logqbase";

  private static final String PARAM_SECQPOW = "secqpow";
  private static final String PARAM_SECQX1 = "secqx1";
  private static final String PARAM_SECQX2 = "secqx2";
  private static final String PARAM_SECQY1 = "secqy1";
  private static final String PARAM_SECQY2 = "secqy2";
  private static final String PARAM_SECQZ1 = "secqz1";
  private static final String PARAM_SECQZ2 = "secqz2";

  private static final String PARAM_SECHQPOW = "sechqpow";
  private static final String PARAM_SECHQX1 = "sechqx1";
  private static final String PARAM_SECHQX2 = "sechqx2";
  private static final String PARAM_SECHQY1 = "sechqy1";
  private static final String PARAM_SECHQY2 = "sechqy2";
  private static final String PARAM_SECHQZ1 = "sechqz1";
  private static final String PARAM_SECHQZ2 = "sechqz2";

  private static final String PARAM_SINQPOW = "sinqpow";
  private static final String PARAM_SINQX1 = "sinqx1";
  private static final String PARAM_SINQX2 = "sinqx2";
  private static final String PARAM_SINQY1 = "sinqy1";
  private static final String PARAM_SINQY2 = "sinqy2";
  private static final String PARAM_SINQZ1 = "sinqz1";
  private static final String PARAM_SINQZ2 = "sinqz2";

  private static final String PARAM_SINHQPOW = "sinhqpow";
  private static final String PARAM_SINHQX1 = "sinhqx1";
  private static final String PARAM_SINHQX2 = "sinhqx2";
  private static final String PARAM_SINHQY1 = "sinhqy1";
  private static final String PARAM_SINHQY2 = "sinhqy2";
  private static final String PARAM_SINHQZ1 = "sinhqz1";
  private static final String PARAM_SINHQZ2 = "sinhqz2";

  private static final String PARAM_TANQPOW = "tanqpow";
  private static final String PARAM_TANQX1 = "tanqx1";
  private static final String PARAM_TANQX2 = "tanqx2";
  private static final String PARAM_TANQY1 = "tanqy1";
  private static final String PARAM_TANQY2 = "tanqy2";
  private static final String PARAM_TANQZ1 = "tanqz1";
  private static final String PARAM_TANQZ2 = "tanqz2";

  private static final String PARAM_TANHQPOW = "tanhqpow";
  private static final String PARAM_TANHQX1 = "tanhqx1";
  private static final String PARAM_TANHQX2 = "tanhqx2";
  private static final String PARAM_TANHQY1 = "tanhqy1";
  private static final String PARAM_TANHQY2 = "tanhqy2";
  private static final String PARAM_TANHQZ1 = "tanhqz1";
  private static final String PARAM_TANHQZ2 = "tanhqz2";

  private static final String[] paramNames = {PARAM_COSQPOW, PARAM_COSQX1,
          PARAM_COSQX2, PARAM_COSQY1, PARAM_COSQY2, PARAM_COSQZ1,
          PARAM_COSQZ2, PARAM_COSHQPOW, PARAM_COSHQX1, PARAM_COSHQX2,
          PARAM_COSHQY1, PARAM_COSHQY2, PARAM_COSHQZ1, PARAM_COSHQZ2,
          PARAM_COTQPOW, PARAM_COTQX1, PARAM_COTQX2, PARAM_COTQY1,
          PARAM_COTQY2, PARAM_COTQZ1, PARAM_COTQZ2, PARAM_COTHQPOW,
          PARAM_COTHQX1, PARAM_COTHQX2, PARAM_COTHQY1, PARAM_COTHQY2,
          PARAM_COTHQZ1, PARAM_COTHQZ2, PARAM_CSCQPOW, PARAM_CSCQX1,
          PARAM_CSCQX2, PARAM_CSCQY1, PARAM_CSCQY2, PARAM_CSCQZ1,
          PARAM_CSCQZ2, PARAM_CSCHQPOW, PARAM_CSCHQX1, PARAM_CSCHQX2,
          PARAM_CSCHQY1, PARAM_CSCHQY2, PARAM_CSCHQZ1, PARAM_CSCHQZ2,
          PARAM_ESTIQPOW, PARAM_ESTIQX1, PARAM_ESTIQY1, PARAM_ESTIQY2,
          PARAM_ESTIQZ1, PARAM_ESTIQZ2, PARAM_LOGQPOW, PARAM_LOGQBASE, PARAM_SECQPOW,
          PARAM_SECQX1, PARAM_SECQX2, PARAM_SECQY1, PARAM_SECQY2,
          PARAM_SECQZ1, PARAM_SECQZ2, PARAM_SECHQPOW, PARAM_SECHQX1,
          PARAM_SECHQX2, PARAM_SECHQY1, PARAM_SECHQY2, PARAM_SECHQZ1,
          PARAM_SECHQZ2, PARAM_SINQPOW, PARAM_SINQX1, PARAM_SINQX2,
          PARAM_SINQY1, PARAM_SINQY2, PARAM_SINQZ1, PARAM_SINQZ2,
          PARAM_SINHQPOW, PARAM_SINHQX1, PARAM_SINHQX2, PARAM_SINHQY1,
          PARAM_SINHQY2, PARAM_SINHQZ1, PARAM_SINHQZ2, PARAM_TANQPOW,
          PARAM_TANQX1, PARAM_TANQX2, PARAM_TANQY1, PARAM_TANQY2,
          PARAM_TANQZ1, PARAM_TANQZ2, PARAM_TANHQPOW, PARAM_TANHQX1,
          PARAM_TANHQX2, PARAM_TANHQY1, PARAM_TANHQY2, PARAM_TANHQZ1,
          PARAM_TANHQZ2};
  private double cosqpow = 1.0;
  private double cosqx1 = 1.0;
  private double cosqx2 = 1.0;
  private double cosqy1 = 1.0;
  private double cosqy2 = 1.0;
  private double cosqz1 = 1.0;
  private double cosqz2 = 1.0;

  private double coshqpow = 0.0;
  private double coshqx1 = 1.0;
  private double coshqx2 = 1.0;
  private double coshqy1 = 1.0;
  private double coshqy2 = 1.0;
  private double coshqz1 = 1.0;
  private double coshqz2 = 1.0;

  private double cotqpow = 0.0;
  private double cotqx1 = 1.0;
  private double cotqx2 = 1.0;
  private double cotqy1 = 1.0;
  private double cotqy2 = 1.0;
  private double cotqz1 = 1.0;
  private double cotqz2 = 1.0;

  private double cothqpow = 0.0;
  private double cothqx1 = 1.0;
  private double cothqx2 = 1.0;
  private double cothqy1 = 1.0;
  private double cothqy2 = 1.0;
  private double cothqz1 = 1.0;
  private double cothqz2 = 1.0;

  private double cscqpow = 0.0;
  private double cscqx1 = 1.0;
  private double cscqx2 = 1.0;
  private double cscqy1 = 1.0;
  private double cscqy2 = 1.0;
  private double cscqz1 = 1.0;
  private double cscqz2 = 1.0;

  private double cschqpow = 0.0;
  private double cschqx1 = 1.0;
  private double cschqx2 = 1.0;
  private double cschqy1 = 1.0;
  private double cschqy2 = 1.0;
  private double cschqz1 = 1.0;
  private double cschqz2 = 1.0;

  private double estiqpow = 0.0;
  private double estiqx1 = 1.0;
  private double estiqy1 = 1.0;
  private double estiqy2 = 1.0;
  private double estiqz1 = 1.0;
  private double estiqz2 = 1.0;

  private double logqpow = 0;
  private double logqbase = M_E;
  private double denom;

  private double secqpow = 0.0;
  private double secqx1 = 1.0;
  private double secqx2 = 1.0;
  private double secqy1 = 1.0;
  private double secqy2 = 1.0;
  private double secqz1 = 1.0;
  private double secqz2 = 1.0;

  private double sechqpow = 0.0;
  private double sechqx1 = 1.0;
  private double sechqx2 = 1.0;
  private double sechqy1 = 1.0;
  private double sechqy2 = 1.0;
  private double sechqz1 = 1.0;
  private double sechqz2 = 1.0;

  private double sinqpow = 0.0;
  private double sinqx1 = 1.0;
  private double sinqx2 = 1.0;
  private double sinqy1 = 1.0;
  private double sinqy2 = 1.0;
  private double sinqz1 = 1.0;
  private double sinqz2 = 1.0;

  private double sinhqpow = 0.0;
  private double sinhqx1 = 1.0;
  private double sinhqx2 = 1.0;
  private double sinhqy1 = 1.0;
  private double sinhqy2 = 1.0;
  private double sinhqz1 = 1.0;
  private double sinhqz2 = 1.0;

  private double tanqpow = 0.0;
  private double tanqx1 = 1.0;
  private double tanqx2 = 1.0;
  private double tanqy1 = 1.0;
  private double tanqy2 = 1.0;
  private double tanqz1 = 1.0;
  private double tanqz2 = 1.0;

  private double tanhqpow = 0.0;
  private double tanhqx1 = 1.0;
  private double tanhqx2 = 1.0;
  private double tanhqy1 = 1.0;
  private double tanhqy2 = 1.0;
  private double tanhqz1 = 1.0;
  private double tanhqz2 = 1.0;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer,
                   XForm pXForm, double pAmount) {
    denom = 0.5 * pAmount / log(logqbase);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm,
                        XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /*
     * Quaternion vars by zephyrtronium
     * http://zephyrtronium.deviantart.com/art
     * /Quaternion-Apo-Plugin-Pack-165451482
     */
    /* Variables and combination by Brad Stefanov */

    double x = 0.0, y = 0.0, z = 0.0;

    if (cosqpow != 0) {
      double cosqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * cosqz1;
      double cosqs = sin(pAffineTP.x * cosqx1);
      double cosqc = cos(pAffineTP.x * cosqx2);
      double cosqsh = sinh(cosqabs_v * cosqy1);
      double cosqch = cosh(cosqabs_v * cosqy2);
      double cosqC = pAmount * cosqs * cosqsh / cosqabs_v * cosqz2;
      x += cosqpow * pAmount * cosqc * cosqch;
      y += cosqpow * cosqC * pAffineTP.y;
      z += cosqpow * cosqC * pAffineTP.z;
    }

    if (coshqpow != 0) {
      double coshqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * coshqz1;
      double coshqs = sin(coshqabs_v * coshqy1);
      double coshqc = cos(coshqabs_v * coshqy2);
      double coshqsh = sinh(pAffineTP.x * coshqx1);
      double coshqch = cosh(pAffineTP.x * coshqx2);
      double coshqC = pAmount * coshqsh * coshqs / coshqabs_v * coshqz2;
      x += coshqpow * pAmount * coshqch * coshqc;
      y += coshqpow * coshqC * pAffineTP.y;
      z += coshqpow * coshqC * pAffineTP.z;
    }
    if (cotqpow != 0) {
      double cotqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * cotqz1;
      double cotqs = sin(pAffineTP.x * cotqx1);
      double cotqc = cos(pAffineTP.x * cotqx2);
      double cotqsh = sinh(cotqabs_v * cotqy1);
      double cotqch = cosh(cotqabs_v * cotqy2);
      double cotqsysz = sqr(pAffineTP.y) + sqr(pAffineTP.z);
      double cotqni = pAmount / (sqr(pAffineTP.x) + cotqsysz);
      double cotqC = cotqc * cotqsh / cotqabs_v * cotqz2;
      double cotqB = -cotqs * cotqsh / cotqabs_v;
      double cotqstcv = cotqs * cotqch;
      double cotqnstcv = -cotqstcv;
      double cotqctcv = cotqc * cotqch;
      x += cotqpow * (cotqstcv * cotqctcv + cotqC * cotqB * cotqsysz)
              * cotqni;
      y -= cotqpow
              * (cotqnstcv * cotqB * pAffineTP.y + cotqC * pAffineTP.y
              * cotqctcv) * cotqni;
      z -= cotqpow
              * (cotqnstcv * cotqB * pAffineTP.z + cotqC * pAffineTP.z
              * cotqctcv) * cotqni;

    }
    if (cothqpow != 0) {
      double cothqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * cothqz1;
      double cothqs = sin(cothqabs_v * cothqy1);
      double cothqc = cos(cothqabs_v * cothqy2);
      double cothqsh = sinh(pAffineTP.x * cothqx1);
      double cothqch = cosh(pAffineTP.x * cothqx2);
      double cothqsysz = sqr(pAffineTP.y) + sqr(pAffineTP.z);
      double cothqni = pAmount / (sqr(pAffineTP.x) + cothqsysz);
      double cothqC = cothqch * cothqs / cothqabs_v * cothqz2;
      double cothqB = cothqsh * cothqs / cothqabs_v;
      double cothqstcv = cothqsh * cothqc;
      double cothqnstcv = -cothqstcv;
      double cothqctcv = cothqch * cothqc;
      x += cothqpow
              * (cothqstcv * cothqctcv + cothqC * cothqB * cothqsysz)
              * cothqni;
      y += cothqpow
              * (cothqnstcv * cothqB * pAffineTP.y + cothqC * pAffineTP.y
              * cothqctcv) * cothqni;
      z += cothqpow
              * (cothqnstcv * cothqB * pAffineTP.z + cothqC * pAffineTP.z
              * cothqctcv) * cothqni;
    }
    if (cscqpow != 0) {
      double cscqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * cscqz1;
      double cscqs = sin(pAffineTP.x * cscqx1);
      double cscqc = cos(pAffineTP.x * cscqx2);
      double cscqsh = sinh(cscqabs_v * cscqy1);
      double cscqch = cosh(cscqabs_v * cscqy2);
      double cscqni = pAmount
              / (sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z));
      double cscqC = cscqni * cscqc * cscqsh / cscqabs_v * cscqz2;
      x += cscqpow * cscqs * cscqch * cscqni;
      y -= cscqpow * cscqC * pAffineTP.y;
      z -= cscqpow * cscqC * pAffineTP.z;
    }

    if (cschqpow != 0) {
      double cschqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * cschqz1;
      double cschqs = sin(cschqabs_v * cschqy1);
      double cschqc = cos(cschqabs_v * cschqy2);
      double cschqsh = sinh(pAffineTP.x * cschqx1);
      double cschqch = cosh(pAffineTP.x * cschqx2);
      double cschqni = pAmount
              / (sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z));
      double cschqC = cschqni * cschqch * cschqs / cschqabs_v * cschqz2;
      x += cschqpow * cschqsh * cschqc * cschqni;
      y -= cschqpow * cschqC * pAffineTP.y;
      z -= cschqpow * cschqC * pAffineTP.z;
    }
    if (estiqpow != 0) {
      double estiqe = exp(pAffineTP.x * estiqx1);
      double estiqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * estiqz1;
      double estiqs = sin(estiqabs_v * estiqy1);
      double estiqc = cos(estiqabs_v * estiqy2);
      double estiqa = estiqe * estiqs / estiqabs_v * estiqz2;
      x += estiqpow * pAmount * estiqe * estiqc;
      y += estiqpow * pAmount * estiqa * pAffineTP.y;
      z += estiqpow * pAmount * estiqa * pAffineTP.z;

    }

    if (logqpow != 0) {
      double logqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z);
      double logqC = pAmount * atan2(logqabs_v, pAffineTP.x) / logqabs_v;
      x += logqpow * log(sqr(pAffineTP.x) + sqr(logqabs_v)) * denom;
      y += logqpow * logqC * pAffineTP.y;
      z += logqpow * logqC * pAffineTP.z;
    }

    if (secqpow != 0) {
      double secqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * secqz1;
      double secqs = sin(-pAffineTP.x * secqx1);
      double secqc = cos(-pAffineTP.x * secqx2);
      double secqsh = sinh(secqabs_v * secqy1);
      double secqch = cosh(secqabs_v * secqy2);
      double secqni = pAmount
              / (sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z));
      double secqC = secqni * secqs * secqsh / secqabs_v * secqz2;
      x += secqpow * secqc * secqch * secqni;
      y -= secqpow * secqC * pAffineTP.y;
      z -= secqpow * secqC * pAffineTP.z;
    }

    if (sechqpow != 0) {
      double sechqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * sechqz1;
      double sechqs = sin(sechqabs_v * sechqy1);
      double sechqc = cos(sechqabs_v * sechqy2);
      double sechqsh = sinh(pAffineTP.x * sechqx1);
      double sechqch = cosh(pAffineTP.x * sechqx2);
      double sechqni = pAmount
              / (sqr(pAffineTP.x) + sqr(pAffineTP.y) + sqr(pAffineTP.z));
      double sechqC = sechqni * sechqsh * sechqs / sechqabs_v * sechqz2;
      x += sechqpow * sechqch * sechqc * sechqni;
      y -= sechqpow * sechqC * pAffineTP.y;
      z -= sechqpow * sechqC * pAffineTP.z;
    }

    if (tanqpow != 0) {
      double tanqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * tanqz1;
      double tanqs = sin(pAffineTP.x * tanqx1);
      double tanqc = cos(pAffineTP.x * tanqx2);
      double tanqsh = sinh(tanqabs_v * tanqy1);
      double tanqch = cosh(tanqabs_v * tanqy2);
      double tanqsysz = sqr(pAffineTP.y) + sqr(pAffineTP.z);
      double tanqni = pAmount / (sqr(pAffineTP.x) + tanqsysz);
      double tanqC = tanqc * tanqsh / tanqabs_v * tanqz2;
      double tanqB = -tanqs * tanqsh / tanqabs_v;
      double tanqstcv = tanqs * tanqch;
      double tanqnstcv = -tanqstcv;
      double tanqctcv = tanqc * tanqch;
      x += tanqpow * (tanqstcv * tanqctcv + tanqC * tanqB * tanqsysz)
              * tanqni;
      y += tanqpow
              * (tanqnstcv * tanqB * pAffineTP.y + tanqC * pAffineTP.y
              * tanqctcv) * tanqni;
      z += tanqpow
              * (tanqnstcv * tanqB * pAffineTP.z + tanqC * pAffineTP.z
              * tanqctcv) * tanqni;
    }
    if (tanhqpow != 0) {
      double tanhqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * tanhqz1;
      double tanhqs = sin(tanhqabs_v * tanhqy1);
      double tanhqc = cos(tanhqabs_v * tanhqy2);
      double tanhqsh = sinh(pAffineTP.x * tanhqx1);
      double tanhqch = cosh(pAffineTP.x * tanhqx2);
      double tanhqsysz = sqr(pAffineTP.y) + sqr(pAffineTP.z);
      double tanhqni = pAmount / (sqr(pAffineTP.x) + tanhqsysz);
      double tanhqC = tanhqch * tanhqs / tanhqabs_v * tanhqz2;
      double tanhqB = tanhqsh * tanhqs / tanhqabs_v;
      double tanhqstcv = tanhqsh * tanhqc;
      double tanhqnstcv = -tanhqstcv;
      double tanhqctcv = tanhqc * tanhqch;
      x += tanhqpow
              * (tanhqstcv * tanhqctcv + tanhqC * tanhqB * tanhqsysz)
              * tanhqni;
      y += tanhqpow
              * (tanhqnstcv * tanhqB * pAffineTP.y + tanhqC * pAffineTP.y
              * tanhqctcv) * tanhqni;
      z += tanhqpow
              * (tanhqnstcv * tanhqB * pAffineTP.z + tanhqC * pAffineTP.z
              * tanhqctcv) * tanhqni;
    }
    if (sinqpow != 0) {
      double sinqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * sinqz1;
      double sinqs = sin(pAffineTP.x * sinqx1);
      double sinqc = cos(pAffineTP.x * sinqx2);
      double sinqsh = sinh(sinqabs_v * sinqy1);
      double sinqch = cosh(sinqabs_v * sinqy2);
      double sinqC = pAmount * sinqc * sinqsh / sinqabs_v * sinqz2;
      x += sinqpow * pAmount * sinqs * sinqch;
      y += sinqpow * sinqC * pAffineTP.y;
      z += sinqpow * sinqC * pAffineTP.z;
    }
    if (sinhqpow != 0) {
      double sinhqabs_v = FastMath.hypot(pAffineTP.y, pAffineTP.z) * sinhqz1;
      double sinhqs = sin(sinhqabs_v * sinhqy1);
      double sinhqc = cos(sinhqabs_v * sinhqy2);
      double sinhqsh = sinh(pAffineTP.x * sinhqx1);
      double sinhqch = cosh(pAffineTP.x * sinhqx2);
      double sinhqC = pAmount * sinhqch * sinhqs / sinhqabs_v * sinhqz2;
      x += sinhqpow * pAmount * sinhqsh * sinhqc;
      y += sinhqpow * sinhqC * pAffineTP.y;
      z += sinhqpow * sinhqC * pAffineTP.z;
    }
    pVarTP.x += x;
    pVarTP.y += y;
    pVarTP.z += z;

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{cosqpow, cosqx1, cosqx2, cosqy1, cosqy2, cosqz1,
            cosqz2, coshqpow, coshqx1, coshqx2, coshqy1, coshqy2, coshqz1,
            coshqz2, cotqpow, cotqx1, cotqx2, cotqy1, cotqy2, cotqz1,
            cotqz2, cothqpow, cothqx1, cothqx2, cothqy1, cothqy2, cothqz1,
            cothqz2, cscqpow, cscqx1, cscqx2, cscqy1, cscqy2, cscqz1,
            cscqz2, cschqpow, cschqx1, cschqx2, cschqy1, cschqy2, cschqz1,
            cschqz2, estiqpow, estiqx1, estiqy1, estiqy2, estiqz1, estiqz2,
            logqpow, logqbase, secqpow, secqx1, secqx2, secqy1, secqy2,
            secqz1, secqz2, sechqpow, sechqx1, sechqx2, sechqy1, sechqy2,
            sechqz1, sechqz2, sinqpow, sinqx1, sinqx2, sinqy1, sinqy2,
            sinqz1, sinqz2, sinhqpow, sinhqx1, sinhqx2, sinhqy1, sinhqy2,
            sinhqz1, sinhqz2, tanqpow, tanqx1, tanqx2, tanqy1, tanqy2,
            tanqz1, tanqz2, tanhqpow, tanhqx1, tanhqx2, tanhqy1, tanhqy2,
            tanhqz1, tanhqz2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_COSQPOW.equalsIgnoreCase(pName))
      cosqpow = pValue;
    else if (PARAM_COSQX1.equalsIgnoreCase(pName))
      cosqx1 = pValue;
    else if (PARAM_COSQX2.equalsIgnoreCase(pName))
      cosqx2 = pValue;
    else if (PARAM_COSQY1.equalsIgnoreCase(pName))
      cosqy1 = pValue;
    else if (PARAM_COSQY2.equalsIgnoreCase(pName))
      cosqy2 = pValue;
    else if (PARAM_COSQZ1.equalsIgnoreCase(pName))
      cosqz1 = pValue;
    else if (PARAM_COSQZ2.equalsIgnoreCase(pName))
      cosqz2 = pValue;
    else if (PARAM_COSHQPOW.equalsIgnoreCase(pName))
      coshqpow = pValue;
    else if (PARAM_COSHQX1.equalsIgnoreCase(pName))
      coshqx1 = pValue;
    else if (PARAM_COSHQX2.equalsIgnoreCase(pName))
      coshqx2 = pValue;
    else if (PARAM_COSHQY1.equalsIgnoreCase(pName))
      coshqy1 = pValue;
    else if (PARAM_COSHQY2.equalsIgnoreCase(pName))
      coshqy2 = pValue;
    else if (PARAM_COSHQZ1.equalsIgnoreCase(pName))
      coshqz1 = pValue;
    else if (PARAM_COSHQZ2.equalsIgnoreCase(pName))
      coshqz2 = pValue;
    else if (PARAM_COTQPOW.equalsIgnoreCase(pName))
      cotqpow = pValue;
    else if (PARAM_COTQX1.equalsIgnoreCase(pName))
      cotqx1 = pValue;
    else if (PARAM_COTQX2.equalsIgnoreCase(pName))
      cotqx2 = pValue;
    else if (PARAM_COTQY1.equalsIgnoreCase(pName))
      cotqy1 = pValue;
    else if (PARAM_COTQY2.equalsIgnoreCase(pName))
      cotqy2 = pValue;
    else if (PARAM_COTQZ1.equalsIgnoreCase(pName))
      cotqz1 = pValue;
    else if (PARAM_COTQZ2.equalsIgnoreCase(pName))
      cotqz2 = pValue;
    else if (PARAM_COTHQPOW.equalsIgnoreCase(pName))
      cothqpow = pValue;
    else if (PARAM_COTHQX1.equalsIgnoreCase(pName))
      cothqx1 = pValue;
    else if (PARAM_COTHQX2.equalsIgnoreCase(pName))
      cothqx2 = pValue;
    else if (PARAM_COTHQY1.equalsIgnoreCase(pName))
      cothqy1 = pValue;
    else if (PARAM_COTHQY2.equalsIgnoreCase(pName))
      cothqy2 = pValue;
    else if (PARAM_COTHQZ1.equalsIgnoreCase(pName))
      cothqz1 = pValue;
    else if (PARAM_COTHQZ2.equalsIgnoreCase(pName))
      cothqz2 = pValue;
    else if (PARAM_CSCQPOW.equalsIgnoreCase(pName))
      cscqpow = pValue;
    else if (PARAM_CSCQX1.equalsIgnoreCase(pName))
      cscqx1 = pValue;
    else if (PARAM_CSCQX2.equalsIgnoreCase(pName))
      cscqx2 = pValue;
    else if (PARAM_CSCQY1.equalsIgnoreCase(pName))
      cscqy1 = pValue;
    else if (PARAM_CSCQY2.equalsIgnoreCase(pName))
      cscqy2 = pValue;
    else if (PARAM_CSCQZ1.equalsIgnoreCase(pName))
      cscqz1 = pValue;
    else if (PARAM_CSCQZ2.equalsIgnoreCase(pName))
      cscqz2 = pValue;
    else if (PARAM_CSCHQPOW.equalsIgnoreCase(pName))
      cschqpow = pValue;
    else if (PARAM_CSCHQX1.equalsIgnoreCase(pName))
      cschqx1 = pValue;
    else if (PARAM_CSCHQX2.equalsIgnoreCase(pName))
      cschqx2 = pValue;
    else if (PARAM_CSCHQY1.equalsIgnoreCase(pName))
      cschqy1 = pValue;
    else if (PARAM_CSCHQY2.equalsIgnoreCase(pName))
      cschqy2 = pValue;
    else if (PARAM_CSCHQZ1.equalsIgnoreCase(pName))
      cschqz1 = pValue;
    else if (PARAM_CSCHQZ2.equalsIgnoreCase(pName))
      cschqz2 = pValue;
    else if (PARAM_ESTIQPOW.equalsIgnoreCase(pName))
      estiqpow = pValue;
    else if (PARAM_ESTIQX1.equalsIgnoreCase(pName))
      estiqx1 = pValue;
    else if (PARAM_ESTIQY1.equalsIgnoreCase(pName))
      estiqy1 = pValue;
    else if (PARAM_ESTIQY2.equalsIgnoreCase(pName))
      estiqy2 = pValue;
    else if (PARAM_ESTIQZ1.equalsIgnoreCase(pName))
      estiqz1 = pValue;
    else if (PARAM_ESTIQZ2.equalsIgnoreCase(pName))
      estiqz2 = pValue;
    else if (PARAM_LOGQPOW.equalsIgnoreCase(pName))
      logqpow = pValue;
    else if (PARAM_LOGQBASE.equalsIgnoreCase(pName))
      logqbase = pValue;
    else if (PARAM_SECQPOW.equalsIgnoreCase(pName))
      secqpow = pValue;
    else if (PARAM_SECQX1.equalsIgnoreCase(pName))
      secqx1 = pValue;
    else if (PARAM_SECQX2.equalsIgnoreCase(pName))
      secqx2 = pValue;
    else if (PARAM_SECQY1.equalsIgnoreCase(pName))
      secqy1 = pValue;
    else if (PARAM_SECQY2.equalsIgnoreCase(pName))
      secqy2 = pValue;
    else if (PARAM_SECQZ1.equalsIgnoreCase(pName))
      secqz1 = pValue;
    else if (PARAM_SECQZ2.equalsIgnoreCase(pName))
      secqz2 = pValue;
    else if (PARAM_SECHQPOW.equalsIgnoreCase(pName))
      sechqpow = pValue;
    else if (PARAM_SECHQX1.equalsIgnoreCase(pName))
      sechqx1 = pValue;
    else if (PARAM_SECHQX2.equalsIgnoreCase(pName))
      sechqx2 = pValue;
    else if (PARAM_SECHQY1.equalsIgnoreCase(pName))
      sechqy1 = pValue;
    else if (PARAM_SECHQY2.equalsIgnoreCase(pName))
      sechqy2 = pValue;
    else if (PARAM_SECHQZ1.equalsIgnoreCase(pName))
      sechqz1 = pValue;
    else if (PARAM_SECHQZ2.equalsIgnoreCase(pName))
      sechqz2 = pValue;
    else if (PARAM_SINQPOW.equalsIgnoreCase(pName))
      sinqpow = pValue;
    else if (PARAM_SINQX1.equalsIgnoreCase(pName))
      sinqx1 = pValue;
    else if (PARAM_SINQX2.equalsIgnoreCase(pName))
      sinqx2 = pValue;
    else if (PARAM_SINQY1.equalsIgnoreCase(pName))
      sinqy1 = pValue;
    else if (PARAM_SINQY2.equalsIgnoreCase(pName))
      sinqy2 = pValue;
    else if (PARAM_SINQZ1.equalsIgnoreCase(pName))
      sinqz1 = pValue;
    else if (PARAM_SINQZ2.equalsIgnoreCase(pName))
      sinqz2 = pValue;
    else if (PARAM_SINHQPOW.equalsIgnoreCase(pName))
      sinhqpow = pValue;
    else if (PARAM_SINHQX1.equalsIgnoreCase(pName))
      sinhqx1 = pValue;
    else if (PARAM_SINHQX2.equalsIgnoreCase(pName))
      sinhqx2 = pValue;
    else if (PARAM_SINHQY1.equalsIgnoreCase(pName))
      sinhqy1 = pValue;
    else if (PARAM_SINHQY2.equalsIgnoreCase(pName))
      sinhqy2 = pValue;
    else if (PARAM_SINHQZ1.equalsIgnoreCase(pName))
      sinhqz1 = pValue;
    else if (PARAM_SINHQZ2.equalsIgnoreCase(pName))
      sinhqz2 = pValue;
    else if (PARAM_TANQPOW.equalsIgnoreCase(pName))
      tanqpow = pValue;
    else if (PARAM_TANQX1.equalsIgnoreCase(pName))
      tanqx1 = pValue;
    else if (PARAM_TANQX2.equalsIgnoreCase(pName))
      tanqx2 = pValue;
    else if (PARAM_TANQY1.equalsIgnoreCase(pName))
      tanqy1 = pValue;
    else if (PARAM_TANQY2.equalsIgnoreCase(pName))
      tanqy2 = pValue;
    else if (PARAM_TANQZ1.equalsIgnoreCase(pName))
      tanqz1 = pValue;
    else if (PARAM_TANQZ2.equalsIgnoreCase(pName))
      tanqz2 = pValue;
    else if (PARAM_TANHQPOW.equalsIgnoreCase(pName))
      tanhqpow = pValue;
    else if (PARAM_TANHQX1.equalsIgnoreCase(pName))
      tanhqx1 = pValue;
    else if (PARAM_TANHQX2.equalsIgnoreCase(pName))
      tanhqx2 = pValue;
    else if (PARAM_TANHQY1.equalsIgnoreCase(pName))
      tanhqy1 = pValue;
    else if (PARAM_TANHQY2.equalsIgnoreCase(pName))
      tanhqy2 = pValue;
    else if (PARAM_TANHQZ1.equalsIgnoreCase(pName))
      tanhqz1 = pValue;
    else if (PARAM_TANHQZ2.equalsIgnoreCase(pName))
      tanhqz2 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "quaternion";
  }

}
