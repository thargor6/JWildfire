/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.pow;

public class PolylogarithmFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_N = "n";
  private static final String PARAM_ZPOW = "zpow";
  private static final String[] paramNames = {PARAM_N, PARAM_ZPOW};

  private int n = 2;
  private double zpow = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* polylogarithm by dark-beam */
    // approx (very good) of Li[n](z) for n > 1
    double vv = pAmount;
    Complex z = new Complex(pAffineTP.x, pAffineTP.y);
    z.Pow(zpow);
    z.Save();
    if (z.Mag2() > 250000.0 || N >= 20) { // no convergence, or N too big... When N is big then Li tends to z
      pVarTP.x += vv * z.re;
      pVarTP.y += vv * z.im;
      return;
    }

    Complex LiN = new Complex();
    int i;
    Complex T = new Complex();
    Complex zPl1 = new Complex(z);

    if (z.Mag2() < 0.07) { // normal series. Li = sum((z^k)/(k^n))
      for (i = 1; i < 20; i++) {
        T.Copy(new Complex(pow(i, N)));
        T.DivR(z);
        LiN.Add(T);
        z.NextPow();
      }
      pVarTP.x += vv * LiN.re;
      pVarTP.y += vv * LiN.im;
      return;
    }
    // Crandall method (very simple and fast!) that uses Erdelyi series
    // from now on we will use ln(z) only so switch to it
    z.Log();
    z.Save();
    z.One();

    for (i = 0; i < 20; i++) {
      double zee = Riem.Z((int) N - i);
      if (zee != 0.0) {
        T.Copy(z);
        T.Scale(zee / (cern.jet.math.Arithmetic.longFactorial(i)));
        LiN.Add(T);
      }
      if (i == N - 1) {
        zPl1.Copy(z);
      }
      z.NextPow();
    }
    z.Restore(); // back to log(z) again...
    z.Neg();
    z.Log();
    z.Neg(); // -log(-log(z)) must be added now...
    z.re += HSTerm;
    T.Copy(z);
    z.Copy(zPl1);
    z.Scale(1.0 / cern.jet.math.Arithmetic.longFactorial((int) N - 1));
    z.Mul(T);
    LiN.Add(z);
    pVarTP.x += vv * LiN.re;
    pVarTP.y += vv * LiN.im;
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
    return new Object[]{n, zpow};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_N.equalsIgnoreCase(pName))
      n = Tools.FTOI(pValue);
    else if (PARAM_ZPOW.equalsIgnoreCase(pName))
      zpow = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "polylogarithm";
  }

  public double HarmonicS(int N) { // Defined in Crandall's paper
    if (N < 1)
      return 0.;
    double Hs = 0.0;
    int i = 1;
    for (; i <= N; i++) {
      Hs += 1.0 / ((double) i);
    }
    return Hs;
  }

  public static class RiemannInt { // with table lookups (TLDR) :D
    private double[] RzNegOdd = {-0.08333333333333333, // zeta(-(2n+1)) because zeta (-2), zeta(-4) etc are zero
            0.008333333333333333, // zeta(-3)
            -0.003968253968253968, // zeta(-5)
            0.004166666666666667,
            -0.007575757575757576,
            0.021092796092796094,
            -0.08333333333333333, // they begin to grow from now on!
            0.4432598039215686,
            -3.0539543302701198,
            26.456212121212122,
            -281.46014492753625,
            3607.5105463980462,
            -54827.583333333336,
            974936.8238505747,
            -2.005269579668808e+7,
            4.723848677216299e+8,
            -1.2635724795916667e+10,
            3.8087931125245367e+11,
            -1.2850850499305083e+13,
            4.824144835485017e+14,
            -2.0040310656516253e+16,
            9.167743603195331e+17,
            -4.5979888343656505e+19,
            2.518047192145109e+21,
            -1.500173349215393e+23,
            9.689957887463594e+24,
            -6.764588237929281e+26,
            5.089065946866229e+28,
            -4.114728879255798e+30,
            3.5666582095375554e+32,
            -3.306608987657758e+34,
            3.2715634236478713e+36,
            -3.4473782558278054e+38,
            3.861427983270526e+40,
            -4.589297443245433e+42,
            5.777538634277042e+44,
            -7.691985875950713e+46,
            1.0813635449971654e+49,
            -1.6029364522008965e+51,
            2.501947904156046e+53,
            -4.106705233581021e+55,
            7.079877440849458e+57,
            -1.280454688793951e+60,
            2.4267340392333522e+62,
            -4.8143218874045757e+64,
            9.987557417572751e+66,
            -2.1645634868435182e+69,
            4.8962327039620546e+71,
            -1.1549023923963518e+74,
            2.83822495706937e+76,
            -7.26120088036067e+78,
            1.932351423341981e+81,
            -5.345016042528861e+83,
            1.535602884642242e+86,
            -4.5789872682265786e+88,
            1.4162025212194806e+91,
            -4.540065229609264e+93,
            1.5076656758807855e+96,
            -5.183094914826456e+98,
            1.843564742725653e+101,
            -6.780555475309095e+103,
            2.57733267027546e+106,
            -1.01191128757046e+109,
            4.101634616154228e+111,
            -1.715524453403202e+114,
            7.400342570526908e+116,
            -3.290922535705444e+119,
            1.507983153416477e+122,
            -7.116987918825454e+124,
            3.458042914157776e+127,
            -1.729090760667674e+130,
            8.893699169503295e+132,
            -4.7038470619636e+135,
            2.55719382310602e+138,
            -1.428406750044352e+141,
            8.195215221831376e+143,
            -4.827648542272736e+146,
            2.918961237477031e+149,
            -1.81089321625689e+152,
            1.152357722002117e+155,
            -7.519231195198174e+157,
            5.029401657641103e+160,
            -3.447342044447767e+163,
            2.420745864586851e+166,
            -1.740946592037767e+169,
            1.281948986348224e+172,
            -9.662412110856088e+174,
            7.452691030430086e+177,
            -5.880839331167436e+180,
            4.74627186549076e+183,
            -3.916913259477282e+186,
            3.304507144322603e+189,
            -2.849289055099457e+192,
            2.510332934507758e+195,
            -2.259390199547524e+198,
            2.07691380042876e+201,
            -1.949473217492725e+204,
            1.868073147126591e+207,
            -1.827075266281457e+210,
            1.823538632259567e+213};

    //From GNU sci lib (thanks) - just few terms
    private double[] RzPosInt = {-0.50000000000000000000000000000, /* zeta(0) */
            0.0, /* zeta(1) is infinite. But we don't care ;) */
            1.64493406684822643647241516665, /* ...     */
            1.20205690315959428539973816151,
            1.08232323371113819151600369654,
            1.03692775514336992633136548646,
            1.01734306198444913971451792979,
            1.00834927738192282683979754985,
            1.00407735619794433937868523851,
            1.00200839282608221441785276923,
            1.00099457512781808533714595890,
            1.00049418860411946455870228253,
            1.00024608655330804829863799805,
            1.00012271334757848914675183653,
            1.00006124813505870482925854511,
            1.00003058823630702049355172851,
            1.00001528225940865187173257149,
            1.00000763719763789976227360029,
            1.00000381729326499983985646164,
            1.00000190821271655393892565696,
            1.00000095396203387279611315204,
            1.00000047693298678780646311672,
            1.00000023845050272773299000365,
            1.00000011921992596531107306779,
            1.00000005960818905125947961244,
            1.00000002980350351465228018606,
            1.00000001490155482836504123466,
            1.00000000745071178983542949198,
            1.00000000372533402478845705482,
            1.00000000186265972351304900640,
            1.00000000093132743241966818287,
            1.00000000046566290650337840730,
            1.00000000023283118336765054920,
            1.00000000011641550172700519776}; // 33 ... it tends to 1...

    public double Z(int N) {
      if (N > 33)
        return 1.0;
      if (N < -200)
        return 1e250; // don't go over indices just make a bad guess
      int M = N;
      if (M < 0) {
        if (((-M) % 2) == 0)
          return 0.0; // even neg are triv zeros
        M = (-M - 1) / 2;
        return RzNegOdd[M];
      }

      return RzPosInt[M];

    } // END OF Z

  } // END OF RiemannInt

  RiemannInt Riem;
  double HSTerm;
  double N;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    N = n;
    Riem = new RiemannInt();
    HSTerm = HarmonicS((int) N - 1);
  }

}
