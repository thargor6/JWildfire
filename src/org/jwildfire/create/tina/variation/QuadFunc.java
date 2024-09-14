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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.base.mathlib.Complex;
import org.jwildfire.base.Tools;
//import java.util.Random;
//import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;
import org.jwildfire.base.mathlib.DoubleWrapperWF;

import static org.jwildfire.base.mathlib.MathLib.*;

public class QuadFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;
  private static final String PARAM_MODE = "mode";
  private static final String PARAM_UL_QUAD = "ul_quad";
  private static final String PARAM_UL_AMOUNT = "ul_amount";
  private static final String PARAM_UL_SHIFTX = "ul_shiftx";
  private static final String PARAM_UL_SHIFTY = "ul_shifty";
  private static final String PARAM_UL_SEED = "ul_seed";
  private static final String PARAM_UL_1A = "ul_1a";
  private static final String PARAM_UL_1B = "ul_1b";
  private static final String PARAM_UR_QUAD = "ur_quad";
  private static final String PARAM_UR_AMOUNT = "ur_amount";
  private static final String PARAM_UR_SHIFTX = "ur_shiftx";
  private static final String PARAM_UR_SHIFTY = "ur_shifty";
  private static final String PARAM_UR_SEED = "ur_seed";
  private static final String PARAM_UR_1A = "ur_1a";
  private static final String PARAM_UR_1B = "ur_1b";
  private static final String PARAM_LL_QUAD = "ll_quad";
  private static final String PARAM_LL_AMOUNT = "ll_amount";
  private static final String PARAM_LL_SHIFTX = "ll_shiftx";
  private static final String PARAM_LL_SHIFTY = "ll_shifty";
  private static final String PARAM_LL_SEED = "ll_seed";
  private static final String PARAM_LL_1A = "ll_1a";
  private static final String PARAM_LL_1B = "ll_1b";
  private static final String PARAM_LR_QUAD = "lr_quad";
  private static final String PARAM_LR_AMOUNT = "lr_amount";
  private static final String PARAM_LR_SHIFTX = "lr_shiftx";
  private static final String PARAM_LR_SHIFTY = "lr_shifty";
  private static final String PARAM_LR_SEED = "lr_seed";
  private static final String PARAM_LR_1A = "lr_1a";
  private static final String PARAM_LR_1B = "lr_1b";  
  private static final String[] paramNames = { PARAM_MODE, PARAM_UL_QUAD, PARAM_UL_AMOUNT, PARAM_UL_SHIFTX, PARAM_UL_SHIFTY, PARAM_UL_SEED, PARAM_UL_1A, PARAM_UL_1B, PARAM_UR_QUAD, PARAM_UR_AMOUNT, PARAM_UR_SHIFTX, PARAM_UR_SHIFTY, PARAM_UR_SEED, PARAM_UR_1A, PARAM_UR_1B, PARAM_LL_QUAD, PARAM_LL_AMOUNT,  PARAM_LL_SHIFTX, PARAM_LL_SHIFTY, PARAM_LL_SEED, PARAM_LL_1A, PARAM_LL_1B, PARAM_LR_QUAD, PARAM_LR_AMOUNT, PARAM_LR_SHIFTX,PARAM_LR_SHIFTY, PARAM_LR_SEED, PARAM_LR_1A, PARAM_LR_1B };

  private int mode = 0;
  private int ul_quad = 0;
  private double ul_amount = 1.0;
  private double ul_shiftx = 0.0;
  private double ul_shifty = 0.0;
  private int ul_seed = 0;
  private double ul_1a = 1;
  private double ul_1b = 1;
  private int ur_quad = 0;
  private double ur_amount = 1.0;
  private double ur_shiftx = 0.0;
  private double ur_shifty = 0.0;
  private int ur_seed = 0;
  private double ur_1a = 1;
  private double ur_1b = 1;
  private int ll_quad = 1;
  private double ll_amount = 1.0;
  private double ll_shiftx = 0.0;
  private double ll_shifty = 0.0;
  private int ll_seed = 0;
  private double ll_1a = 1;
  private double ll_1b = 1;
  private int lr_quad = 1;
  private double lr_amount = 1.0;
  private double lr_shiftx = 0.0;
  private double lr_shifty = 0.0;
  private int lr_seed = 0;
  private double lr_1a = 1;
  private double lr_1b = 1;

  private MarsagliaRandomGenerator myRandGen = new MarsagliaRandomGenerator();

  //Taking the square root of numbers close to zero is dangerous.  If x is negative
  //due to floating point errors we get NaN results.
    private double sqrt_safe(double x) {
    if (x <= 0.0)
      return 0.0;
    return sqrt(x);
  }
    private double sqrt_safe_e(double x) {
    return (x < SMALL_EPSILON) ? 0.0 : sqrt(x);
  }
  private DoubleWrapperWF sina = new DoubleWrapperWF();
  private DoubleWrapperWF cosa = new DoubleWrapperWF();  
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP,
                        double pAmount) {
    /*
     * Quad by Brad Stefanov, Andreas Maschke and Rick Sidwell  used idea/code from the fourth variation by
     * guagapunyaimel,
     * https://www.deviantart.com/guagapunyaimel/art/Fourth-Plugin-175043938
	 * All sub variations credit to respective developers. 
	 * Thank you to Steph Tassuro and Mufti for beta testing. Special thank you to Amorina Ashton for beta testing, ideas, and documentation. 
     */

    int count = 0;
    double qquadxx = 0.0;
    double qquadyy = 0.0;

    if (pAffineTP.x > lr_shiftx && pAffineTP.y > lr_shifty) // kuadran IV: LowerRright
    {
      if   (lr_quad == 0) {
// Linear
        qquadxx += pAmount * lr_amount * pAffineTP.x;
        qquadyy += pAmount * lr_amount * pAffineTP.y;



      }
      else if   (lr_quad == 1) {
// Spherical
        double r = pAmount * lr_amount / (pAffineTP.x * pAffineTP.x * lr_1a + pAffineTP.y * pAffineTP.y * lr_1b + SMALL_EPSILON);
        qquadxx += pAffineTP.x * r;
        qquadyy += pAffineTP.y * r;



      }

      else if   (lr_quad == 2) {
// Acosech
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcosecH();
        z.Flip();
        z.Scale(pAmount * lr_amount * M_2_PI);

        if (pContext.random() < 0.5) {
          qquadyy += z.im;
          qquadxx += z.re;
        } else {
          qquadyy += -z.im;
          qquadxx += -z.re;
        }



      }


      else if   (lr_quad == 3) {
// Acosh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcosH();

        z.Scale(pAmount * lr_amount * M_2_PI);

        if (pContext.random() < 0.5) {
          qquadyy += z.im;
          qquadxx += z.re;
        } else {
          qquadyy += -z.im;
          qquadxx += -z.re;
        }



      }

      else if   (lr_quad == 4) {
// Acoth
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcotH();
        z.Flip();
        z.Scale(pAmount * lr_amount * M_2_PI);

        qquadyy += z.im;
        qquadxx += z.re;



      }

      else if   (lr_quad == 5) {
//Apollony
        double xxx, yyy, a0, b0, f1x, f1y;
        double r = sqrt(3.0);

        a0 = 3.0 * (1.0 + r - pAffineTP.x) / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y) - (1.0 + r) / (2.0 + r);
        b0 = 3.0 * pAffineTP.y / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y);
        f1x = a0 / (a0 * a0 + b0 * b0);
        f1y = -b0 / (a0 * a0 + b0 * b0);

        int w = (int) (4.0 * pContext.random());

        if ((w % 3) == 0) {
          xxx = a0;
          yyy = b0;
        } else if ((w % 3) == 1) {
          xxx = -f1x / 2.0 - f1y * r / 2.0;
          yyy = f1x * r / 2.0 - f1y / 2.0;
        } else {
          xxx = -f1x / 2.0 + f1y * r / 2.0;
          yyy = -f1x * r / 2.0 - f1y / 2.0;
        }

        qquadxx += xxx * pAmount *  lr_amount;
        qquadyy += yyy * pAmount *  lr_amount;



      }


      else if   (lr_quad == 6) {
// Arcsinh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AsinH();
        z.Scale(pAmount * lr_amount * M_2_PI);

        qquadyy += z.im;
        qquadxx += z.re;

      }



      else if   (lr_quad == 7) {
//Arctanh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);
        Complex z2 = new Complex(z);
        z2.Scale(-1.0);
        z2.Inc(); // -z+1
        Complex z3 = new Complex(z);
        z3.Inc(); // z+1
        z3.Div(z2);
        z3.Log();
        z3.Scale(pAmount *  lr_amount * M_2_PI);
        qquadxx += z3.re;
        qquadyy += z3.im;



      }

      else if   (lr_quad == 8) {
//Atan Mode 3
        //myRandGen = new MarsagliaRandomGenerator();
        //myRandGen.randomize(lr_seed);
        //	double rand = myRandGen.random() * 3;
        double norm = 1.0 / M_PI_2 * pAmount * lr_amount;
        qquadxx += norm * atan(lr_1a*pAffineTP.x);
        qquadyy += norm * atan(lr_1b*pAffineTP.y);



      }

      else if   (lr_quad == 9) {
//BCollide
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;
        int alt;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        alt = (int) (sigma * lr_bCn_pi);
        if (alt % 2 == 0)
          sigma = alt * lr_pi_bCn + fmod(sigma + lr_bCa_bCn, lr_pi_bCn);
        else
          sigma = alt * lr_pi_bCn + fmod(sigma - lr_bCa_bCn, lr_pi_bCn);
        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        qquadxx += pAmount * lr_amount * sinht / temp;
        qquadyy += pAmount * lr_amount * sins / temp;



      }


      else if   (lr_quad == 10) {
//BMod
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        if (tau < lr_1a && -tau < lr_1a) {
          tau = fmod(tau + lr_1a + lr_1b * lr_1a, 2.0 * lr_1a) - lr_1a;
        }

        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        if (temp == 0) {
          return;
        }
        qquadxx += pAmount * lr_amount * sinht / temp;
        qquadyy += pAmount * lr_amount * sins / temp;



      }

      else if   (lr_quad == 11) {
//BSwirl
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        sigma = sigma + tau * lr_1b + lr_1a / tau;

        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        if (temp == 0) {
          return;
        }
        qquadxx += pAmount * lr_amount * sinht / temp;
        qquadyy += pAmount * lr_amount * sins / temp;



      }


      else if   (lr_quad == 12) {
//BTransform
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y))) / lr_1a + 0;
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x) + 0;
        sigma = sigma / lr_1a + M_2PI / lr_1a * floor(pContext.random() * lr_1a);

        if (pAffineTP.x >= 0.0)
          tau += lr_1b;
        else
          tau -= lr_1b;
        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        qquadxx += pAmount * lr_amount * sinht / temp;
        qquadyy += pAmount * lr_amount * sins / temp;



      }

      else if   (lr_quad == 13) {
//Bent
        double nx = pAffineTP.x;
        double ny = pAffineTP.y;
        if (nx < 0)
          nx = nx * lr_1a;
        if (ny < 0)
          ny = ny * lr_1b;
        qquadxx += pAmount * lr_amount * nx;
        qquadyy += pAmount * lr_amount * ny;



      }



      else if   (lr_quad == 14) {
//Bipolar
        double x2y2 = (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
        double t = x2y2 +  lr_1b;
        double x2 = 2 * pAffineTP.x;
        double ps = -M_PI_2 * lr_1a;
        double yb = 0.5 * atan2(2.0 * pAffineTP.y, x2y2 - 1.0) + ps;

        if (yb > M_PI_2) {
          yb = -M_PI_2 + fmod(yb + M_PI_2, M_PI);
        } else if (yb < -M_PI_2) {
          yb = M_PI_2 - fmod(M_PI_2 - yb, M_PI);
        }

        double f = t + x2;
        double g = t - x2;

        if ((g == 0) || (f / g <= 0))
          return;
        qquadxx += pAmount * lr_amount * 0.25 * M_2_PI * log((t + x2) / (t - x2));
        qquadyy += pAmount * lr_amount * M_2_PI * yb;



      }


      else if   (lr_quad == 15) {
//Blob

        double a = atan2(pAffineTP.x, pAffineTP.y);
        double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
        r = r * (lr_1a + (lr_1b - lr_1a) * (0.5 + 0.5 * sin(6 * a)));
        double nx = sin(a) * r;
        double ny = cos(a) * r;

        qquadxx += pAmount * lr_amount * nx;
        qquadyy += pAmount * lr_amount * ny;

      }

      else if   (lr_quad == 16) {
//Butterfly
        double wx = pAmount * lr_amount * lr_1a;

        double y2 = pAffineTP.y * lr_1b;
        double r = wx * sqrt(fabs(pAffineTP.y * pAffineTP.x) / (SMALL_EPSILON + pAffineTP.x * pAffineTP.x + y2 * y2));

        qquadxx += r * pAffineTP.x;
        qquadyy += r * y2;
      }

      else if   (lr_quad == 17) {
//Collideoscope

        double aaa = atan2(pAffineTP.y, pAffineTP.x);
        double r = pAmount * lr_amount * sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
        int alt;

        if (aaa >= 0.0) {
          alt = (int) (aaa * lr_kn_pi);
          if (alt % 2 == 0) {
            aaa = alt * lr_pi_kn + fmod(lr_ka_kn + aaa, lr_pi_kn);
          } else {
            aaa = alt * lr_pi_kn + fmod(-lr_ka_kn + aaa, lr_pi_kn);
          }
        } else {
          alt = (int) (-aaa * lr_kn_pi);
          if (alt % 2 != 0) {
            aaa = -(alt * lr_pi_kn + fmod(-lr_ka_kn - aaa, lr_pi_kn));
          } else {
            aaa = -(alt * lr_pi_kn + fmod(lr_ka_kn - aaa, lr_pi_kn));
          }
        }

        double s = sin(aaa);
        double c = cos(aaa);

        qquadxx += r * c;
        qquadyy += r * s;



      }


      else if   (lr_quad == 18) {
//CPOW

        double a = pAffineTP.getPrecalcAtanYX();
        double lnr = 0.5 * log(pAffineTP.getPrecalcSumsq());
        double va = 2.0 * M_PI / 2;
        double vc = lr_1a / 2;
        double vd = lr_1b / 2;
        double ang = vc * a + vd * lnr + va * floor(2 * pContext.random());

        double m = pAmount * lr_amount * exp(vc * lnr - vd * a);
        double sa = sin(ang);
        double ca = cos(ang);

        qquadxx += m * ca;
        qquadyy += m * sa;



      }


      else if   (lr_quad == 19) {
//Curl
        double re = 1 + lr_1a * pAffineTP.x + lr_1b * (sqr(pAffineTP.x) - sqr(pAffineTP.y));
        double im = lr_1a * pAffineTP.y + lr_1b * 2 * pAffineTP.x * pAffineTP.y;

        double r = pAmount * lr_amount / (sqr(re) + sqr(im));

        qquadxx += (pAffineTP.x * re + pAffineTP.y * im) * r;
        qquadyy += (pAffineTP.y * re - pAffineTP.x * im) * r;



      }

      else if   (lr_quad == 20) {
//Devil Warp
        double xx = pAffineTP.x;
        double yy = pAffineTP.y;
        double r2 = 1.0 / (xx * xx + yy * yy);
        double r = pow(xx * xx + r2 * 1 * yy * yy, lr_1b) - pow(yy * yy + r2 * 2 * xx * xx, lr_1b);
        if (r > 100)
          r = 100;
        else if (r < -0.24)
          r = -0.24;
        r = lr_1a * (r);
        qquadxx += lr_amount * xx * (1 + r);
        qquadyy += lr_amount * yy * (1 + r);



      }

      else if   (lr_quad == 21) {
//Disc
        double rPI = M_PI * sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y)* lr_1a;
        double sinr = sin(rPI);
        double cosr = cos(rPI);
        double r = pAmount * lr_amount * pAffineTP.getPrecalcAtan() / M_PI;
        qquadxx += sinr * r;
        qquadyy += cosr * r;



      }

      else if   (lr_quad == 22) {
// ECollide


        double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        double sinnu, cosnu;
        int alt;
        if (xmax < 1.0)
          xmax = 1.0;

        double t = pAffineTP.x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;
        double nu = acos(t); // -Pi < nu < Pi

        alt = (int) (nu * lr_eCn_pi);
        if (alt % 2 == 0)
          nu = alt * lr_pi_eCn + fmod(nu + lr_eCa_eCn, lr_pi_eCn);
        else
          nu = alt * lr_pi_eCn + fmod(nu - lr_eCa_eCn, lr_pi_eCn);
        if (pAffineTP.y <= 0.0)
          nu *= -1.0;
        sinnu = sin(nu);
        cosnu = cos(nu);
        qquadxx += pAmount * lr_amount * xmax * cosnu;
        qquadyy += pAmount * lr_amount * sqrt(xmax - 1.0) * sqrt(xmax + 1.0) * sinnu;



      }

      else if   (lr_quad == 23) {
// EDisc
        double tmp = pAffineTP.getPrecalcSumsq() + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double r1 = sqrt(tmp + tmp2);
        double r2 = sqrt(tmp - tmp2);
        double xmax = (r1 + r2) * 0.5;
        double a1 = log(xmax + sqrt(xmax - 1.0));
        double a2 = -acos(pAffineTP.x / xmax);
        double w = pAmount * lr_amount / 11.57034632;

        double snv = sin(a1);
        double csv = cos(a1);
        double snhu = sinh(a2);
        double cshu = cosh(a2);

        if (pAffineTP.y > 0.0) {
          snv = -snv;
        }

        qquadxx += w * cshu * csv;
        qquadyy += w * snhu * snv;



      }

      else if   (lr_quad == 24) {
// EJulia
        double r2 = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x;
        double tmp2;
        double x;
        if (lr_sign == 1)
          x = pAffineTP.x;
        else {
          r2 = 1.0 / r2;
          x = pAffineTP.x * r2;
        }

        double tmp = r2 + 1.0;
        tmp2 = 2.0 * x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        if (xmax < 1.0)
          xmax = 1.0;
        double sinhmu, coshmu, sinnu, cosnu;

        double mu = acosh(xmax); //  mu > 0
        double t = x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;

        double nu = acos(t); // -Pi < nu < Pi
        if (pAffineTP.y < 0)
          nu *= -1.0;

        nu = nu / lr_1a + M_2PI / lr_1a * floor(pContext.random() * lr_1a);
        mu /= lr_1a;

        sinhmu = sinh(mu);
        coshmu = cosh(mu);

        sinnu = sin(nu);
        cosnu = cos(nu);
        qquadxx += pAmount * lr_amount * coshmu * cosnu;
        qquadyy += pAmount * lr_amount * sinhmu * sinnu;




      }

      else if   (lr_quad == 25) {
// EMOD

        double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        if (xmax < 1.0)
          xmax = 1.0;
        double sinhmu, coshmu;

        double mu = acosh(xmax); //  mu > 0
        double t = pAffineTP.x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;

        double nu = acos(t); // -Pi < nu < Pi
        if (pAffineTP.y < 0)
          nu *= -1.0;

        if (mu < lr_1a && -mu < lr_1a) {
          if (nu > 0.0)
            mu = fmod(mu + lr_1a + lr_1b * lr_1a, 2.0 * lr_1a) - lr_1a;
          else
            mu = fmod(mu - lr_1a - lr_1b * lr_1a, 2.0 * lr_1a) + lr_1a;
        }

        sinhmu = sinh(mu);
        coshmu = cosh(mu);

        qquadxx += pAmount * lr_amount * coshmu * cos(nu);
        qquadyy += pAmount * lr_amount * sinhmu * sin(nu);
      }
	  
	  
      else if   (lr_quad == 26) {
// Eclipse 
    if (fabs(pAffineTP.y) <= pAmount) {
      double c_2 = sqrt(sqr(pAmount)
              - sqr(pAffineTP.y));

      if (fabs(pAffineTP.x) <= c_2) {
        double x = pAffineTP.x + this.lr_1a * pAmount * lr_amount;
        if (fabs(x) >= c_2) {
          qquadxx -= pAmount * lr_amount * pAffineTP.x;
        } else {
          qquadxx += pAmount * lr_amount * x;
        }
      } else {
        qquadxx += pAmount * lr_amount * pAffineTP.x;
      }
      qquadyy += pAmount * lr_amount * pAffineTP.y;
    } else {
      qquadxx += pAmount * lr_amount * pAffineTP.x;
      qquadyy += pAmount * lr_amount * pAffineTP.y;
		}
	  }

      else if   (lr_quad == 27) {
// Elliptic 
    double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + lr_1a;
    double x2 = 2.0 * pAffineTP.x;
    double xmax = 0.5 * (sqrt(tmp + x2) + sqrt(tmp - x2));
    double a = pAffineTP.x / xmax;
    double b = sqrt_safe_e(lr_1b - a * a);
    qquadxx += lr_v * atan2(a, b);
    //    if (pAffineTP.y > 0)
    if (pContext.random() < 0.5)
      qquadyy += lr_v * log(xmax + sqrt_safe_e(xmax - 1.0));
    else
      qquadyy -= lr_v * log(xmax + sqrt_safe_e(xmax - 1.0));
	  }
 
      else if   (lr_quad == 28) {
// Fan2
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double angle;
    if ((pAffineTP.x < -SMALL_EPSILON) || (pAffineTP.x > SMALL_EPSILON) || (pAffineTP.y < -SMALL_EPSILON) || (pAffineTP.y > SMALL_EPSILON)) {
      angle = atan2(pAffineTP.x, pAffineTP.y);
    } else {
      angle = 0.0;
    }

    double dy = lr_1b;
    double dx = M_PI * (lr_1a * lr_1a) + SMALL_EPSILON;
    double dx2 = dx * 0.5;

    double t = angle + dy - (int) ((angle + dy) / dx) * dx;
    double a;
    if (t > dx2) {
      a = angle - dx2;
    } else {
      a = angle + dx2;
    }

    qquadxx += pAmount * lr_amount * r * sin(a);
    qquadyy += pAmount * lr_amount * r * cos(a);
	  }

      else if   (lr_quad == 29) {
// Foci
    double expx = exp(pAffineTP.x) * 0.5;
    double expnx = 0.25 / expx;
    if (expx <= SMALL_EPSILON || expnx <= SMALL_EPSILON) {
      return;
    }
    double siny = sin(pAffineTP.y);
    double cosy = cos(pAffineTP.y);

    double tmp = (expx + expnx - cosy);
    if (tmp == 0)
      return;
    tmp = pAmount * lr_amount / tmp;

    qquadxx += (expx - expnx) * tmp;
    qquadyy += siny * tmp;
	  }
  
      else if   (lr_quad == 30) {
// Glynnia
    double r = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double d;

    if (r >= 1.0) {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        qquadxx+= lr_vvar2 * d;
        qquadyy -= lr_vvar2 / d * pAffineTP.y; //+= _vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount * lr_amount  / dx;
        qquadxx += r * d;
        qquadyy += r * pAffineTP.y; //-= r * pAffineTP.y; 
      }
    } else {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        qquadxx -= lr_vvar2 * d;
        qquadyy -= lr_vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount * lr_amount  / dx;
        qquadxx -= r * d;
        qquadyy += r * pAffineTP.y;
		}
		}
	  }

      else if   (lr_quad == 31) {
// Hole
    double alpha = atan2(pAffineTP.y,pAffineTP.x);
    double delta = pow(alpha/M_PI + 1.f, lr_1a);
    double r;
    if (lr_1b!=0)
      r = pAmount * lr_amount *delta/(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    else
      r = pAmount * lr_amount *sqrt(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    double s = sin(alpha);
    double c = cos(alpha);
    qquadxx += r * c;
    qquadyy += r * s;
	  }

      else if   (lr_quad == 32) {
// Idisc
    double a = M_PI / (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + 1.0);
    double r = atan2(pAffineTP.y, pAffineTP.x) * lr_v_idisc;

    sinAndCos(a, sina, cosa);

    qquadxx += r * cosa.value;
    qquadyy += r * sina.value;
	  }

      else if   (lr_quad == 33) {
// JuliaN
    double a = (atan2(pAffineTP.y, pAffineTP.x) + 2 * M_PI * pContext.random(lr_absPower_julian)) / lr_1a;
    double sina = sin(a);
    double cosa = cos(a);
    double r = pAmount * lr_amount * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), lr_cPower_julian);

    qquadxx = qquadxx + r * cosa;
    qquadyy = qquadyy + r * sina;
	  }

      else if   (lr_quad == 34) {
// Lazy Sensen
    if (lr_1a != 0.0) {
      double nr = (int) floor(pAffineTP.x * lr_1a);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.x = -pAffineTP.x;
      } else {
        if (nr % 2 == 0)
          pAffineTP.x = -pAffineTP.x;
      }
    }
    if (lr_1b != 0.0) {
      double nr = (int) floor(pAffineTP.y * lr_1b);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.y = -pAffineTP.y;
      } else {
        if (nr % 2 == 0)
          pAffineTP.y = -pAffineTP.y;
      }
    }


    qquadxx += pAmount * lr_amount * pAffineTP.x;
    qquadyy += pAmount * lr_amount * pAffineTP.y;
	  }
 
      else if   (lr_quad == 35) {
// LazySusan
	double x = 0.0;
	double y = 0.0;
    double xx = pAffineTP.x - x;
    double yy = pAffineTP.y + y;
    double rr = sqrt(xx * xx + yy * yy);

    if (rr < pAmount) {
      double a = atan2(yy, xx) + lr_1b + lr_1a * (pAmount * lr_amount - rr);
      double sina = sin(a);
      double cosa = cos(a);
      rr = pAmount * lr_amount * rr;

      qquadxx += rr * cosa + x;
      qquadyy += rr * sina - y;
    } else {
      rr = pAmount * lr_amount * (1.0+0.0 / rr);

      qquadxx += rr * xx + x;
      qquadyy += rr * yy - y;
    }
    }

      else if   (lr_quad == 36) {
// Murl2
	double _p2, _invp, _vp;
	double _sina, _cosa, _a, _r, _re, _im, _rl;

    _p2 = (double) lr_1b / 2.0;

    if (lr_1b != 0) {
      _invp = 1.0 / (double) lr_1b;
      if (lr_1a == -1) {
        _vp = 0;
      } else {
        _vp = pAmount * lr_amount * pow(lr_1a + 1, 2.0 / ((double) lr_1b));
      }
    } else {
      _invp = 100000000000.0;
      _vp = pAmount * lr_amount * pow(lr_1a + 1, 4 /*Normally infinity, but we let this be a special case*/);
    }

    _a = atan2(pAffineTP.y, pAffineTP.x) * (double) lr_1b;
    _sina = sin(_a);
    _cosa = cos(_a);

    _r = lr_1a * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), _p2);

    _re = _r * _cosa + 1;
    _im = _r * _sina;

    _r = pow(sqr(_re) + sqr(_im), _invp);
    _a = atan2(_im, _re) * 2.0 * _invp;
    _re = _r * cos(_a);
    _im = _r * sin(_a);

    _rl = _vp / sqr(_r);

    qquadxx += _rl * (pAffineTP.x * _re + pAffineTP.y * _im);
    qquadyy += _rl * (pAffineTP.y * _re - pAffineTP.x * _im);
    }

      else if   (lr_quad == 37) {
// Ortho
    double r, a, ta;
    double xo;
    double ro;
    double c, s;
    double x, y, tc, ts;
    double theta;

    r = sqr(pAffineTP.x) + sqr(pAffineTP.y);

    if (r < 1.0) { // && FTx > 0.0 && FTy > 0.0) 
      if (pAffineTP.x >= 0.0) {
        xo = (r + 1.0) / (2.0 * pAffineTP.x);
        ro = sqrt(sqr(pAffineTP.x - xo) + sqr(pAffineTP.y));
        theta = atan2(1.0, ro);
        a = fmod(lr_1a * theta + atan2(pAffineTP.y, xo - pAffineTP.x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        qquadxx += pAmount * lr_amount * (xo - c * ro);
        qquadyy += pAmount * lr_amount * s * ro;
      } else {
        xo = -(r + 1.0) / (2.0 * pAffineTP.x);
        ro = sqrt(sqr(-pAffineTP.x - xo) + sqr(pAffineTP.y));
        theta = atan2(1.0, ro);
        a = fmod(lr_1a * theta + atan2(pAffineTP.y, xo + pAffineTP.x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        qquadxx -= pAmount * lr_amount * (xo - c * ro);
        qquadyy += pAmount * lr_amount * s * ro;
      }
    } else {
      r = 1.0 / sqrt(r);
      ta = atan2(pAffineTP.y, pAffineTP.x);
      ts = sin(ta);
      tc = cos(ta);

      x = r * tc;
      y = r * ts;

      if (x >= 0.0) {
        xo = (sqr(x) + sqr(y) + 1.0) / (2.0 * x);
        ro = sqrt(sqr(x - xo) + sqr(y));
        theta = atan2(1.0, ro);
        a = fmod(lr_1b * theta + atan2(y, xo - x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        x = (xo - c * ro);
        y = s * ro;
        ta = atan2(y, x);
        ts = sin(ta);
        tc = cos(ta);

        r = 1.0 / sqrt(sqr(x) + sqr(y));

        qquadxx += pAmount * lr_amount * r * tc;
        qquadyy += pAmount * lr_amount * r * ts;
      } else {
        xo = -(sqr(x) + sqr(y) + 1.0) / (2.0 * x);
        ro = sqrt(sqr(-x - xo) + sqr(y));
        theta = atan2(1.0, ro);
        a = fmod(lr_1b * theta + atan2(y, xo + x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        x = (xo - c * ro);
        y = s * ro;
        ta = atan2(y, x);
        ts = sin(ta);
        tc = cos(ta);
        r = 1.0 / sqrt(sqr(x) + sqr(y));

        qquadxx -= pAmount * lr_amount * r * tc;
        qquadyy += pAmount * lr_amount * r * ts;

      }
    }
    }


      else if   (lr_quad == 38) {
// Rectangles
    if (fabs(lr_1a) < EPSILON) {
      qquadxx += pAmount * lr_amount * pAffineTP.x;
    } else {
      qquadxx += pAmount * lr_amount * ((2 * floor(pAffineTP.x / lr_1a) + 1) * lr_1a - pAffineTP.x);
    }
    if (fabs(lr_1b) < EPSILON) {
      qquadyy += pAmount * lr_amount * pAffineTP.y;
    } else {
      qquadyy += pAmount * lr_amount * ((2 * floor(pAffineTP.y / lr_1b) + 1) * lr_1b - pAffineTP.y);
    }
    }


      else if   (lr_quad == 39) {
// Sinusoidal
    qquadxx += pAmount * lr_amount * sin(pAffineTP.x);
    qquadyy += pAmount * lr_amount * sin(pAffineTP.y);
    }


   
      else if   (lr_quad == 40) {
// SphericalN
    double R = pow(sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)), this.lr_1b);
    int N = (int) floor(this.lr_1a * pContext.random());
    double alpha = atan2(pAffineTP.y, pAffineTP.x) + N * M_2PI / floor(this.lr_1a);
    double sina = sin(alpha);
    double cosa = cos(alpha);

    if (R > SMALL_EPSILON) {
      qquadxx += pAmount * lr_amount * cosa / R;
      qquadyy += pAmount * lr_amount * sina / R;
    }
    }

      else if   (lr_quad == 41) {
// Waves2
    qquadxx += pAmount * lr_amount  * (pAffineTP.x + lr_1a * sin(pAffineTP.y * lr_1b));
    qquadyy += pAmount * lr_amount  * (pAffineTP.y + lr_1a * sin(pAffineTP.x * lr_1b));
    }

      else if   (lr_quad == 42) {
// Waves22
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	int modex = 0;
    int modey = 0;
	double sinx;
	double siny;
	int px =  (int)2.0;
	int py =  (int)2.0;	

	if (modex < 0.5){
        sinx = sin(y0 * lr_1b);
    } else {
        sinx = 0.5 * (1.0 + sin(y0 * lr_1b));
    }
	double offsetx = pow(sinx, px) * lr_1a;
	if (modey < 0.5){
        siny = sin(x0 * lr_1b);
    } else {
        siny = 0.5 * (1.0 + sin(x0 * lr_1b));
    }
    double offsety = pow(siny, py) * lr_1a;
    
    qquadxx += pAmount * lr_amount * (x0 + offsetx);
    qquadyy += pAmount * lr_amount * (y0 + offsety);
    }

      else if   (lr_quad == 43) {
// Waves23
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double mx = y0 * lr_1b * M_1_2PI;
	double fx = mx - floor(mx);
	if (fx > 0.5) fx = 0.5 - fx;
	double my = x0 * lr_1b * M_1_2PI;
	double fy = my - floor(my);
	if (fy > 0.5) fy = 0.5 - fy;
    qquadxx += pAmount * lr_amount * (x0 + fx * lr_1a);
    qquadyy += pAmount * lr_amount * (y0 + fy * lr_1a);
    }
 
      else if   (lr_quad == 44) {
// Waves3
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double scalexx = 0.5 * lr_1a * (1.0 + sin(y0 * 0.0));
	double scaleyy = 0.5 * lr_1a * (1.0 + sin(x0 * 2.0));
    qquadxx += pAmount * lr_amount * (x0 + sin(y0 * lr_1b) * scalexx);
    qquadyy += pAmount * lr_amount * (y0 + sin(x0 * lr_1b) * scaleyy);
    }

      else if   (lr_quad == 45) {
// Waves4
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	int cont = 0;
	double ax = floor(y0 * lr_1b / M_2PI);

    ax = sin(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * 0.1) * 43758.5453;
    ax = ax - (int) ax;
    if (cont == 1) ax = (ax > 0.5) ? 1.0 : 0.0;

    
    qquadxx += pAmount * lr_amount * (x0 + sin(y0 * lr_1b) * ax * ax * lr_1a);
    qquadyy += pAmount * lr_amount * (y0 + sin(x0 * lr_1b) * lr_1a);
    }

      else if   (lr_quad == 46) {
// Waves42
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	int cont = 0;
	double ax = floor(y0 * lr_1b);

    ax = sin(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * 0.1) * 43758.5453;
    ax = ax - (int) ax;
    if (cont == 1) ax = (ax > 0.5) ? 1.0 : 0.0;

    
    qquadxx += pAmount * lr_amount * (x0 + sin(y0 * lr_1b) * ax * ax * lr_1a);
    qquadyy += pAmount * lr_amount * (y0 + sin(x0 * lr_1b) * lr_1a);
    }

      else if   (lr_quad == 47) {
// Wdisc
    double a = M_PI / (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + lr_1a);
    double r = atan2(pAffineTP.y, pAffineTP.x) * M_1_PI;

    if (r > 0.0)
      a = M_PI - a;

    sinAndCos(a, sina, cosa);

    qquadxx += pAmount * lr_amount * r * cosa.value;
    qquadyy += pAmount * lr_amount * r * sina.value;
    }

      else if   (lr_quad == 48) {
// Whorl
    double r = pAffineTP.getPrecalcSqrt();
    double a;
    if (r < pAmount)
      a = pAffineTP.getPrecalcAtanYX() + lr_1a / (pAmount - r);
    else
      a = pAffineTP.getPrecalcAtanYX() + lr_1b / (pAmount - r);

    double sa = sin(a);
    double ca = cos(a);

    qquadxx += pAmount * lr_amount * r * ca;
    qquadyy += pAmount * lr_amount * r * sa;
    }
  
      count += 1;

    }
    if (pAffineTP.x > ur_shiftx && pAffineTP.y < ur_shifty) // kuadran I: UpperRight
    {
      if   (ur_quad == 0) {
// Linear
        qquadxx += pAmount * ur_amount * pAffineTP.x;
        qquadyy += pAmount * ur_amount * pAffineTP.y;



      }
      else if   (ur_quad == 1) {
// Spherical
        double r = pAmount * ur_amount / (pAffineTP.x * pAffineTP.x * ur_1a + pAffineTP.y * pAffineTP.y * ur_1b + SMALL_EPSILON);
        qquadxx += pAffineTP.x * r;
        qquadyy += pAffineTP.y * r;




      }

      else if   (ur_quad == 2) {
// Acosech
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcosecH();
        z.Flip();
        z.Scale(pAmount * ur_amount * M_2_PI);

        if (pContext.random() < 0.5) {
          qquadyy += z.im;
          qquadxx += z.re;
        } else {
          qquadyy += -z.im;
          qquadxx += -z.re;
        }



      }

      else if   (ur_quad == 3) {
// Acosh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcosH();

        z.Scale(pAmount * ur_amount * M_2_PI);

        if (pContext.random() < 0.5) {
          qquadyy += z.im;
          qquadxx += z.re;
        } else {
          qquadyy += -z.im;
          qquadxx += -z.re;
        }



      }

      else if   (ur_quad == 4) {
// Acoth
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcotH();
        z.Flip();
        z.Scale(pAmount * ur_amount * M_2_PI);

        qquadyy += z.im;
        qquadxx += z.re;




      }
      else if   (ur_quad == 5) {
//Apollony
        double xxx, yyy, a0, b0, f1x, f1y;
        double r = sqrt(3.0);

        a0 = 3.0 * (1.0 + r - pAffineTP.x) / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y) - (1.0 + r) / (2.0 + r);
        b0 = 3.0 * pAffineTP.y / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y);
        f1x = a0 / (a0 * a0 + b0 * b0);
        f1y = -b0 / (a0 * a0 + b0 * b0);

        int w = (int) (4.0 * pContext.random());

        if ((w % 3) == 0) {
          xxx = a0;
          yyy = b0;
        } else if ((w % 3) == 1) {
          xxx = -f1x / 2.0 - f1y * r / 2.0;
          yyy = f1x * r / 2.0 - f1y / 2.0;
        } else {
          xxx = -f1x / 2.0 + f1y * r / 2.0;
          yyy = -f1x * r / 2.0 - f1y / 2.0;
        }

        qquadxx += xxx * pAmount *  ur_amount;
        qquadyy += yyy * pAmount *  ur_amount;



      }

      else if   (ur_quad == 6) {
// Arcsinh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AsinH();
        z.Scale(pAmount * ur_amount * M_2_PI);

        qquadyy += z.im;
        qquadxx += z.re;




      }

      else if   (ur_quad == 7) {
//Arctanh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);
        Complex z2 = new Complex(z);
        z2.Scale(-1.0);
        z2.Inc(); // -z+1
        Complex z3 = new Complex(z);
        z3.Inc(); // z+1
        z3.Div(z2);
        z3.Log();
        z3.Scale(pAmount *  ur_amount * M_2_PI);
        qquadxx += z3.re;
        qquadyy += z3.im;



      }

      else if   (ur_quad == 8) {
//Atan Mode 3
        double norm = 1.0 / M_PI_2 * pAmount * ur_amount;
        qquadxx += norm * atan(ur_1a*pAffineTP.x);
        qquadyy += norm * atan(ur_1b*pAffineTP.y);



      }

      else if   (ur_quad == 9) {
//BCollide
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;
        int alt;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        alt = (int) (sigma * ur_bCn_pi);
        if (alt % 2 == 0)
          sigma = alt * ur_pi_bCn + fmod(sigma + ur_bCa_bCn, ur_pi_bCn);
        else
          sigma = alt * ur_pi_bCn + fmod(sigma - ur_bCa_bCn, ur_pi_bCn);
        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        qquadxx += pAmount * ur_amount * sinht / temp;
        qquadyy += pAmount * ur_amount * sins / temp;




      }



      else if   (ur_quad == 10) {
//BMod
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        if (tau < ur_1a && -tau < ur_1a) {
          tau = fmod(tau + ur_1a + ur_1b * ur_1a, 2.0 * ur_1a) - ur_1a;
        }

        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        if (temp == 0) {
          return;
        }
        qquadxx += pAmount * ur_amount * sinht / temp;
        qquadyy += pAmount * ur_amount * sins / temp;



      }


      else if   (ur_quad == 11) {
//BSwirl
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        sigma = sigma + tau * ur_1b + ur_1a / tau;

        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        if (temp == 0) {
          return;
        }
        qquadxx += pAmount * ur_amount * sinht / temp;
        qquadyy += pAmount * ur_amount * sins / temp;
      }

      else if   (ur_quad == 12) {
//BTransform
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y))) / ur_1a + 0;
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x) + 0;
        sigma = sigma / ur_1a + M_2PI / ur_1a * floor(pContext.random() * ur_1a);

        if (pAffineTP.x >= 0.0)
          tau += ur_1b;
        else
          tau -= ur_1b;
        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        qquadxx += pAmount * ur_amount * sinht / temp;
        qquadyy += pAmount * ur_amount * sins / temp;



      }

      else if   (ur_quad == 13) {
//Bent
        double nx = pAffineTP.x;
        double ny = pAffineTP.y;
        if (nx < 0)
          nx = nx * ur_1a;
        if (ny < 0)
          ny = ny * ur_1b;
        qquadxx += pAmount * ur_amount * nx;
        qquadyy += pAmount * ur_amount * ny;



      }

      else if   (ur_quad == 14) {
//Bipolar
        double x2y2 = (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
        double t = x2y2 +  ur_1b;
        double x2 = 2 * pAffineTP.x;
        double ps = -M_PI_2 * ur_1a;
        double yb = 0.5 * atan2(2.0 * pAffineTP.y, x2y2 - 1.0) + ps;

        if (yb > M_PI_2) {
          yb = -M_PI_2 + fmod(yb + M_PI_2, M_PI);
        } else if (yb < -M_PI_2) {
          yb = M_PI_2 - fmod(M_PI_2 - yb, M_PI);
        }

        double f = t + x2;
        double g = t - x2;

        if ((g == 0) || (f / g <= 0))
          return;
        qquadxx += pAmount * ur_amount * 0.25 * M_2_PI * log((t + x2) / (t - x2));
        qquadyy += pAmount * ur_amount * M_2_PI * yb;



      }


      else if   (ur_quad == 15) {
//Blob

        double a = atan2(pAffineTP.x, pAffineTP.y);
        double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
        r = r * (ur_1a + (ur_1b - ur_1a) * (0.5 + 0.5 * sin(6 * a)));
        double nx = sin(a) * r;
        double ny = cos(a) * r;

        qquadxx += pAmount * ur_amount * nx;
        qquadyy += pAmount * ur_amount * ny;



      }



      else if   (ur_quad == 16) {
//Butterfly
        double wx = pAmount * ur_amount * ur_1a;

        double y2 = pAffineTP.y * ur_1b;
        double r = wx * sqrt(fabs(pAffineTP.y * pAffineTP.x) / (SMALL_EPSILON + pAffineTP.x * pAffineTP.x + y2 * y2));

        qquadxx += r * pAffineTP.x;
        qquadyy += r * y2;

      }


      else if   (ur_quad == 17) {
//Collideoscope

        double aaa = atan2(pAffineTP.y, pAffineTP.x);
        double r = pAmount * ur_amount * sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
        int alt;

        if (aaa >= 0.0) {
          alt = (int) (aaa * ur_kn_pi);
          if (alt % 2 == 0) {
            aaa = alt * ur_pi_kn + fmod(ur_ka_kn + aaa, ur_pi_kn);
          } else {
            aaa = alt * ur_pi_kn + fmod(-ur_ka_kn + aaa, ur_pi_kn);
          }
        } else {
          alt = (int) (-aaa * ur_kn_pi);
          if (alt % 2 != 0) {
            aaa = -(alt * ur_pi_kn + fmod(-ur_ka_kn - aaa, ur_pi_kn));
          } else {
            aaa = -(alt * ur_pi_kn + fmod(ur_ka_kn - aaa, ur_pi_kn));
          }
        }

        double s = sin(aaa);
        double c = cos(aaa);

        qquadxx += r * c;
        qquadyy += r * s;



      }

      else if   (ur_quad == 18) {
//CPOW

        double a = pAffineTP.getPrecalcAtanYX();
        double lnr = 0.5 * log(pAffineTP.getPrecalcSumsq());
        double va = 2.0 * M_PI / 2;
        double vc = ur_1a / 2;
        double vd = ur_1b / 2;
        double ang = vc * a + vd * lnr + va * floor(2 * pContext.random());

        double m = pAmount * ur_amount * exp(vc * lnr - vd * a);
        double sa = sin(ang);
        double ca = cos(ang);

        qquadxx += m * ca;
        qquadyy += m * sa;



      }


      else if   (ur_quad == 19) {
//Curl
        double re = 1 + ur_1a * pAffineTP.x + ur_1b * (sqr(pAffineTP.x) - sqr(pAffineTP.y));
        double im = ur_1a * pAffineTP.y + ur_1b * 2 * pAffineTP.x * pAffineTP.y;

        double r = pAmount * ur_amount / (sqr(re) + sqr(im));

        qquadxx += (pAffineTP.x * re + pAffineTP.y * im) * r;
        qquadyy += (pAffineTP.y * re - pAffineTP.x * im) * r;



      }


      else if   (ur_quad == 20) {
//Devil Warp
        double xx = pAffineTP.x;
        double yy = pAffineTP.y;
        double r2 = 1.0 / (xx * xx + yy * yy);
        double r = pow(xx * xx + r2 * 1 * yy * yy, ur_1b) - pow(yy * yy + r2 * 2 * xx * xx, ur_1b);
        if (r > 100)
          r = 100;
        else if (r < -0.24)
          r = -0.24;
        r = ur_1a * (r);
        qquadxx += ur_amount * xx * (1 + r);
        qquadyy += ur_amount * yy * (1 + r);



      }


      else if   (ur_quad == 21) {
//Disc
        double rPI = M_PI * sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y)* ur_1a;
        double sinr = sin(rPI);
        double cosr = cos(rPI);
        double r = pAmount * ur_amount * pAffineTP.getPrecalcAtan() / M_PI;
        qquadxx += sinr * r;
        qquadyy += cosr * r;



      }

      else if   (ur_quad == 22) {
//ECollide


        double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        double sinnu, cosnu;
        int alt;
        if (xmax < 1.0)
          xmax = 1.0;

        double t = pAffineTP.x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;
        double nu = acos(t); // -Pi < nu < Pi

        alt = (int) (nu * ur_eCn_pi);
        if (alt % 2 == 0)
          nu = alt * ur_pi_eCn + fmod(nu + ur_eCa_eCn, ur_pi_eCn);
        else
          nu = alt * ur_pi_eCn + fmod(nu - ur_eCa_eCn, ur_pi_eCn);
        if (pAffineTP.y <= 0.0)
          nu *= -1.0;
        sinnu = sin(nu);
        cosnu = cos(nu);
        qquadxx += pAmount * ur_amount * xmax * cosnu;
        qquadyy += pAmount * ur_amount * sqrt(xmax - 1.0) * sqrt(xmax + 1.0) * sinnu;



      }


      else if   (ur_quad == 23) {
//EDisc
        double tmp = pAffineTP.getPrecalcSumsq() + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double r1 = sqrt(tmp + tmp2);
        double r2 = sqrt(tmp - tmp2);
        double xmax = (r1 + r2) * 0.5;
        double a1 = log(xmax + sqrt(xmax - 1.0));
        double a2 = -acos(pAffineTP.x / xmax);
        double w = pAmount * ur_amount / 11.57034632;

        double snv = sin(a1);
        double csv = cos(a1);
        double snhu = sinh(a2);
        double cshu = cosh(a2);

        if (pAffineTP.y > 0.0) {
          snv = -snv;
        }

        qquadxx += w * cshu * csv;
        qquadyy += w * snhu * snv;



      }

      else if   (ur_quad == 24) {
// EJulia
        double r2 = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x;
        double tmp2;
        double x;
        if (ur_sign == 1)
          x = pAffineTP.x;
        else {
          r2 = 1.0 / r2;
          x = pAffineTP.x * r2;
        }

        double tmp = r2 + 1.0;
        tmp2 = 2.0 * x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        if (xmax < 1.0)
          xmax = 1.0;
        double sinhmu, coshmu, sinnu, cosnu;

        double mu = acosh(xmax); //  mu > 0
        double t = x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;

        double nu = acos(t); // -Pi < nu < Pi
        if (pAffineTP.y < 0)
          nu *= -1.0;

        nu = nu / ur_1a + M_2PI / ur_1a * floor(pContext.random() * ur_1a);
        mu /= ur_1a;

        sinhmu = sinh(mu);
        coshmu = cosh(mu);

        sinnu = sin(nu);
        cosnu = cos(nu);
        qquadxx += pAmount * ur_amount * coshmu * cosnu;
        qquadyy += pAmount * ur_amount * sinhmu * sinnu;




      }

      else if   (ur_quad == 25) {
// EMOD

        double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        if (xmax < 1.0)
          xmax = 1.0;
        double sinhmu, coshmu;

        double mu = acosh(xmax); //  mu > 0
        double t = pAffineTP.x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;

        double nu = acos(t); // -Pi < nu < Pi
        if (pAffineTP.y < 0)
          nu *= -1.0;

        if (mu < ur_1a && -mu < ur_1a) {
          if (nu > 0.0)
            mu = fmod(mu + ur_1a + ur_1b * ur_1a, 2.0 * ur_1a) - ur_1a;
          else
            mu = fmod(mu - ur_1a - ur_1b * ur_1a, 2.0 * ur_1a) + ur_1a;
        }

        sinhmu = sinh(mu);
        coshmu = cosh(mu);

        qquadxx += pAmount * ur_amount * coshmu * cos(nu);
        qquadyy += pAmount * ur_amount * sinhmu * sin(nu);



      }

      else if   (ur_quad == 26) {
// Eclipse 
    if (fabs(pAffineTP.y) <= pAmount) {
      double c_2 = sqrt(sqr(pAmount)
              - sqr(pAffineTP.y));

      if (fabs(pAffineTP.x) <= c_2) {
        double x = pAffineTP.x + this.ur_1a * pAmount * ur_amount;
        if (fabs(x) >= c_2) {
          qquadxx -= pAmount * ur_amount * pAffineTP.x;
        } else {
          qquadxx += pAmount * ur_amount * x;
        }
      } else {
        qquadxx += pAmount * ur_amount * pAffineTP.x;
      }
      qquadyy += pAmount * ur_amount * pAffineTP.y;
    } else {
      qquadxx += pAmount * ur_amount * pAffineTP.x;
      qquadyy += pAmount * ur_amount * pAffineTP.y;
		}
	  }


      else if   (ur_quad == 27) {
// Elliptic 
    double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + ur_1a;
    double x2 = 2.0 * pAffineTP.x;
    double xmax = 0.5 * (sqrt(tmp + x2) + sqrt(tmp - x2));
    double a = pAffineTP.x / xmax;
    double b = sqrt_safe_e(ur_1b - a * a);
    qquadxx += ur_v * atan2(a, b);
    //    if (pAffineTP.y > 0)
    if (pContext.random() < 0.5)
      qquadyy += ur_v * log(xmax + sqrt_safe_e(xmax - 1.0));
    else
      qquadyy -= ur_v * log(xmax + sqrt_safe_e(xmax - 1.0));
	  }
 
      else if   (ur_quad == 28) {
// Fan2
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double angle;
    if ((pAffineTP.x < -SMALL_EPSILON) || (pAffineTP.x > SMALL_EPSILON) || (pAffineTP.y < -SMALL_EPSILON) || (pAffineTP.y > SMALL_EPSILON)) {
      angle = atan2(pAffineTP.x, pAffineTP.y);
    } else {
      angle = 0.0;
    }

    double dy = ur_1b;
    double dx = M_PI * (ur_1a * ur_1a) + SMALL_EPSILON;
    double dx2 = dx * 0.5;

    double t = angle + dy - (int) ((angle + dy) / dx) * dx;
    double a;
    if (t > dx2) {
      a = angle - dx2;
    } else {
      a = angle + dx2;
    }

    qquadxx += pAmount * ur_amount * r * sin(a);
    qquadyy += pAmount * ur_amount * r * cos(a);
	  }

      else if   (ur_quad == 29) {
// Foci
    double expx = exp(pAffineTP.x) * 0.5;
    double expnx = 0.25 / expx;
    if (expx <= SMALL_EPSILON || expnx <= SMALL_EPSILON) {
      return;
    }
    double siny = sin(pAffineTP.y);
    double cosy = cos(pAffineTP.y);

    double tmp = (expx + expnx - cosy);
    if (tmp == 0)
      return;
    tmp = pAmount * ur_amount / tmp;

    qquadxx += (expx - expnx) * tmp;
    qquadyy += siny * tmp;
	  }
  
      else if   (ur_quad == 30) {
// Glynnia
    double r = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double d;

    if (r >= 1.0) {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        qquadxx+= ur_vvar2 * d;
        qquadyy -= ur_vvar2 / d * pAffineTP.y; //+= _vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount * ur_amount / dx;
        qquadxx += r * d;
        qquadyy += r * pAffineTP.y; //-= r * pAffineTP.y; 
      }
    } else {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        qquadxx -= ur_vvar2 * d;
        qquadyy -= ur_vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount * ur_amount / dx;
        qquadxx -= r * d;
        qquadyy += r * pAffineTP.y;
		}
		}
	  }

      else if   (ur_quad == 31) {
// Hole
    double alpha = atan2(pAffineTP.y,pAffineTP.x);
    double delta = pow(alpha/M_PI + 1.f, ur_1a);
    double r;
    if (ur_1b!=0)
      r = pAmount * ur_amount *delta/(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    else
      r = pAmount * ur_amount *sqrt(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    double s = sin(alpha);
    double c = cos(alpha);
    qquadxx += r * c;
    qquadyy += r * s;
	  }

      else if   (ur_quad == 32) {
// Idisc
    double a = M_PI / (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + 1.0);
    double r = atan2(pAffineTP.y, pAffineTP.x) * ur_v_idisc;

    sinAndCos(a, sina, cosa);

    qquadxx += r * cosa.value;
    qquadyy += r * sina.value;
	  }

      else if   (ur_quad == 33) {
// JuliaN
    double a = (atan2(pAffineTP.y, pAffineTP.x) + 2 * M_PI * pContext.random(ur_absPower_julian)) / ur_1a;
    double sina = sin(a);
    double cosa = cos(a);
    double r = pAmount * ur_amount * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), ur_cPower_julian);

    qquadxx = qquadxx + r * cosa;
    qquadyy = qquadyy + r * sina;
	  }

      else if   (ur_quad == 34) {
// Lazy Sensen
    if (ur_1a != 0.0) {
      double nr = (int) floor(pAffineTP.x * ur_1a);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.x = -pAffineTP.x;
      } else {
        if (nr % 2 == 0)
          pAffineTP.x = -pAffineTP.x;
      }
    }
    if (ur_1b != 0.0) {
      double nr = (int) floor(pAffineTP.y * ur_1b);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.y = -pAffineTP.y;
      } else {
        if (nr % 2 == 0)
          pAffineTP.y = -pAffineTP.y;
      }
    }


    qquadxx += pAmount * ur_amount * pAffineTP.x;
    qquadyy += pAmount * ur_amount * pAffineTP.y;
	  }
 

      else if   (ur_quad == 35) {
// LazySusan
	double x = 0.0;
	double y = 0.0;
    double xx = pAffineTP.x - x;
    double yy = pAffineTP.y + y;
    double rr = sqrt(xx * xx + yy * yy);

    if (rr < pAmount) {
      double a = atan2(yy, xx) + ur_1b + ur_1a * (pAmount * ur_amount - rr);
      double sina = sin(a);
      double cosa = cos(a);
      rr = pAmount * ur_amount * rr;

      qquadxx += rr * cosa + x;
      qquadyy += rr * sina - y;
    } else {
      rr = pAmount * ur_amount * (1.0+0.0 / rr);

      qquadxx += rr * xx + x;
      qquadyy += rr * yy - y;
    }
    }
 
	  
      else if   (ur_quad == 36) {
// Murl2
	double _p2, _invp, _vp;
	double _sina, _cosa, _a, _r, _re, _im, _rl;

    _p2 = (double) ur_1b / 2.0;

    if (ur_1b != 0) {
      _invp = 1.0 / (double) ur_1b;
      if (ur_1a == -1) {
        _vp = 0;
      } else {
        _vp = pAmount * ur_amount * pow(ur_1a + 1, 2.0 / ((double) ur_1b));
      }
    } else {
      _invp = 100000000000.0;
      _vp = pAmount * ur_amount * pow(ur_1a + 1, 4 /*Normally infinity, but we let this be a special case*/);
    }

    _a = atan2(pAffineTP.y, pAffineTP.x) * (double) ur_1b;
    _sina = sin(_a);
    _cosa = cos(_a);

    _r = ur_1a * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), _p2);

    _re = _r * _cosa + 1;
    _im = _r * _sina;

    _r = pow(sqr(_re) + sqr(_im), _invp);
    _a = atan2(_im, _re) * 2.0 * _invp;
    _re = _r * cos(_a);
    _im = _r * sin(_a);

    _rl = _vp / sqr(_r);

    qquadxx += _rl * (pAffineTP.x * _re + pAffineTP.y * _im);
    qquadyy += _rl * (pAffineTP.y * _re - pAffineTP.x * _im);
    }
 
	  
      else if   (ur_quad == 37) {
// Ortho
    double r, a, ta;
    double xo;
    double ro;
    double c, s;
    double x, y, tc, ts;
    double theta;

    r = sqr(pAffineTP.x) + sqr(pAffineTP.y);

    if (r < 1.0) { // && FTx > 0.0 && FTy > 0.0) 
      if (pAffineTP.x >= 0.0) {
        xo = (r + 1.0) / (2.0 * pAffineTP.x);
        ro = sqrt(sqr(pAffineTP.x - xo) + sqr(pAffineTP.y));
        theta = atan2(1.0, ro);
        a = fmod(ur_1a * theta + atan2(pAffineTP.y, xo - pAffineTP.x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        qquadxx += pAmount * ur_amount * (xo - c * ro);
        qquadyy += pAmount * ur_amount * s * ro;
      } else {
        xo = -(r + 1.0) / (2.0 * pAffineTP.x);
        ro = sqrt(sqr(-pAffineTP.x - xo) + sqr(pAffineTP.y));
        theta = atan2(1.0, ro);
        a = fmod(ur_1a * theta + atan2(pAffineTP.y, xo + pAffineTP.x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        qquadxx -= pAmount * ur_amount * (xo - c * ro);
        qquadyy += pAmount * ur_amount * s * ro;
      }
    } else {
      r = 1.0 / sqrt(r);
      ta = atan2(pAffineTP.y, pAffineTP.x);
      ts = sin(ta);
      tc = cos(ta);

      x = r * tc;
      y = r * ts;

      if (x >= 0.0) {
        xo = (sqr(x) + sqr(y) + 1.0) / (2.0 * x);
        ro = sqrt(sqr(x - xo) + sqr(y));
        theta = atan2(1.0, ro);
        a = fmod(ur_1b * theta + atan2(y, xo - x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        x = (xo - c * ro);
        y = s * ro;
        ta = atan2(y, x);
        ts = sin(ta);
        tc = cos(ta);

        r = 1.0 / sqrt(sqr(x) + sqr(y));

        qquadxx += pAmount * ur_amount * r * tc;
        qquadyy += pAmount * ur_amount * r * ts;
      } else {
        xo = -(sqr(x) + sqr(y) + 1.0) / (2.0 * x);
        ro = sqrt(sqr(-x - xo) + sqr(y));
        theta = atan2(1.0, ro);
        a = fmod(ur_1b * theta + atan2(y, xo + x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        x = (xo - c * ro);
        y = s * ro;
        ta = atan2(y, x);
        ts = sin(ta);
        tc = cos(ta);
        r = 1.0 / sqrt(sqr(x) + sqr(y));

        qquadxx -= pAmount * ur_amount * r * tc;
        qquadyy += pAmount * ur_amount * r * ts;

      }
    }
    }
	  
      else if   (ur_quad == 38) {
// Rectangles
    if (fabs(ur_1a) < EPSILON) {
      qquadxx += pAmount * ur_amount * pAffineTP.x;
    } else {
      qquadxx += pAmount * ur_amount * ((2 * floor(pAffineTP.x / ur_1a) + 1) * ur_1a - pAffineTP.x);
    }
    if (fabs(ur_1b) < EPSILON) {
      qquadyy += pAmount * ur_amount * pAffineTP.y;
    } else {
      qquadyy += pAmount * ur_amount * ((2 * floor(pAffineTP.y / ur_1b) + 1) * ur_1b - pAffineTP.y);
    }
    }
	  
      else if   (ur_quad == 39) {
// Sinusoidal
    qquadxx += pAmount * ur_amount * sin(pAffineTP.x);
    qquadyy += pAmount * ur_amount * sin(pAffineTP.y);
    }
 	  
      else if   (ur_quad == 40) {
// SphericalN
    double R = pow(sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)), this.ur_1b);
    int N = (int) floor(this.ur_1a * pContext.random());
    double alpha = atan2(pAffineTP.y, pAffineTP.x) + N * M_2PI / floor(this.ur_1a);
    double sina = sin(alpha);
    double cosa = cos(alpha);

    if (R > SMALL_EPSILON) {
      qquadxx += pAmount * ur_amount * cosa / R;
      qquadyy += pAmount * ur_amount * sina / R;
    }
    }
	  
      else if   (ur_quad == 41) {
// Waves2
    qquadxx += pAmount * ur_amount  * (pAffineTP.x + ur_1a * sin(pAffineTP.y * ur_1b));
    qquadyy += pAmount * ur_amount  * (pAffineTP.y + ur_1a * sin(pAffineTP.x * ur_1b));
    }
	  
        else if   (ur_quad == 42) {
// Waves22
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	int modex = 0;
    int modey = 0;
	double sinx;
	double siny;
	int px =  (int)2.0;
	int py =  (int)2.0;	

	if (modex < 0.5){
        sinx = sin(y0 * ur_1b);
    } else {
        sinx = 0.5 * (1.0 + sin(y0 * ur_1b));
    }
	double offsetx = pow(sinx, px) * ur_1a;
	if (modey < 0.5){
        siny = sin(x0 * ur_1b);
    } else {
        siny = 0.5 * (1.0 + sin(x0 * ur_1b));
    }
    double offsety = pow(siny, py) * ur_1a;
    
    qquadxx += pAmount * ur_amount * (x0 + offsetx);
    qquadyy += pAmount * ur_amount * (y0 + offsety);
    }
	  
      else if   (ur_quad == 43) {
// Waves23
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double mx = y0 * ur_1b * M_1_2PI;
	double fx = mx - floor(mx);
	if (fx > 0.5) fx = 0.5 - fx;
	double my = x0 * ur_1b * M_1_2PI;
	double fy = my - floor(my);
	if (fy > 0.5) fy = 0.5 - fy;
    qquadxx += pAmount * ur_amount * (x0 + fx * ur_1a);
    qquadyy += pAmount * ur_amount * (y0 + fy * ur_1a);
    }
	  
      else if   (ur_quad == 44) {
// Waves3
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double scalexx = 0.5 * ur_1a * (1.0 + sin(y0 * 0.0));
	double scaleyy = 0.5 * ur_1a * (1.0 + sin(x0 * 2.0));
    qquadxx += pAmount * ur_amount * (x0 + sin(y0 * ur_1b) * scalexx);
    qquadyy += pAmount * ur_amount * (y0 + sin(x0 * ur_1b) * scaleyy);
    }
	  
      else if   (ur_quad == 45) {
// Waves4
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	
	double ax = floor(y0 * ur_1b / M_2PI);

    ax = sin(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * 0.1) * 43758.5453;
    ax = ax - (int) ax;
    //if (0.0 == 1) ax = (ax > 0.5) ? 1.0 : 0.0;

    
    qquadxx += pAmount * ur_amount * (x0 + sin(y0 * ur_1b) * ax * ax * ur_1a);
    qquadyy += pAmount * ur_amount * (y0 + sin(x0 * ur_1b) * ur_1a);
    }

	  
      else if   (ur_quad == 46) {
// Waves42
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	
	double ax = floor(y0 * ur_1b);

    ax = sin(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * 0.1) * 43758.5453;
    ax = ax - (int) ax;
    //if (0.0 == 1) ax = (ax > 0.5) ? 1.0 : 0.0;

    
    qquadxx += pAmount * ur_amount * (x0 + sin(y0 * ur_1b) * ax * ax * ur_1a);
    qquadyy += pAmount * ur_amount * (y0 + sin(x0 * ur_1b) * ur_1a);
    }
	  
      else if   (ur_quad == 47) {
// Wdisc
    double a = M_PI / (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + ur_1a);
    double r = atan2(pAffineTP.y, pAffineTP.x) * M_1_PI;

    if (r > 0.0)
      a = M_PI - a;

    sinAndCos(a, sina, cosa);

    qquadxx += pAmount * ur_amount * r * cosa.value;
    qquadyy += pAmount * ur_amount * r * sina.value;
    }
	  
      else if   (ur_quad == 48) {
// Whorl
    double r = pAffineTP.getPrecalcSqrt();
    double a;
    if (r < pAmount)
      a = pAffineTP.getPrecalcAtanYX() + ur_1a / (pAmount - r);
    else
      a = pAffineTP.getPrecalcAtanYX() + ur_1b / (pAmount - r);

    double sa = sin(a);
    double ca = cos(a);

    qquadxx += pAmount * ur_amount * r * ca;
    qquadyy += pAmount * ur_amount * r * sa;
    }

      count += 1;

    }
    if (pAffineTP.x < ll_shiftx && pAffineTP.y > ll_shifty) // kuadran III: LowerLeft
    {
      if   (ll_quad == 0) {
// Linear
        qquadxx += pAmount * ll_amount * pAffineTP.x;
        qquadyy += pAmount * ll_amount * pAffineTP.y;



      }
      else if   (ll_quad == 1) {
// Spherical
        double r = pAmount * ll_amount / (pAffineTP.x * pAffineTP.x * ll_1a + pAffineTP.y * pAffineTP.y * ll_1b + SMALL_EPSILON);
        qquadxx += pAffineTP.x * r;
        qquadyy += pAffineTP.y * r;



      }

      else if   (ll_quad == 2) {
// Acosech
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcosecH();
        z.Flip();
        z.Scale(pAmount * ll_amount * M_2_PI);

        if (pContext.random() < 0.5) {
          qquadyy += z.im;
          qquadxx += z.re;
        } else {
          qquadyy += -z.im;
          qquadxx += -z.re;
        }



      }


      else if   (ll_quad == 3) {
// Acosh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcosH();

        z.Scale(pAmount * ll_amount * M_2_PI);

        if (pContext.random() < 0.5) {
          qquadyy += z.im;
          qquadxx += z.re;
        } else {
          qquadyy += -z.im;
          qquadxx += -z.re;
        }



      }

      else if   (ll_quad == 4) {
// Acoth
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcotH();
        z.Flip();
        z.Scale(pAmount * ll_amount * M_2_PI);

        qquadyy += z.im;
        qquadxx += z.re;




      }

      else if   (ll_quad == 5) {
//Apollony
        double xxx, yyy, a0, b0, f1x, f1y;
        double r = sqrt(3.0);

        a0 = 3.0 * (1.0 + r - pAffineTP.x) / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y) - (1.0 + r) / (2.0 + r);
        b0 = 3.0 * pAffineTP.y / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y);
        f1x = a0 / (a0 * a0 + b0 * b0);
        f1y = -b0 / (a0 * a0 + b0 * b0);

        int w = (int) (4.0 * pContext.random());

        if ((w % 3) == 0) {
          xxx = a0;
          yyy = b0;
        } else if ((w % 3) == 1) {
          xxx = -f1x / 2.0 - f1y * r / 2.0;
          yyy = f1x * r / 2.0 - f1y / 2.0;
        } else {
          xxx = -f1x / 2.0 + f1y * r / 2.0;
          yyy = -f1x * r / 2.0 - f1y / 2.0;
        }

        qquadxx += xxx * pAmount *  ll_amount;
        qquadyy += yyy * pAmount *  ll_amount;



      }

      else if   (ll_quad == 6) {
// Arcsinh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AsinH();
        z.Scale(pAmount * ll_amount * M_2_PI);

        qquadyy += z.im;
        qquadxx += z.re;




      }

      else if   (ll_quad == 7) {
//Arctanh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);
        Complex z2 = new Complex(z);
        z2.Scale(-1.0);
        z2.Inc(); // -z+1
        Complex z3 = new Complex(z);
        z3.Inc(); // z+1
        z3.Div(z2);
        z3.Log();
        z3.Scale(pAmount *  ll_amount * M_2_PI);
        qquadxx += z3.re;
        qquadyy += z3.im;



      }

      else if   (ll_quad == 8) {
//Atan Mode 3
        double norm = 1.0 / M_PI_2 * pAmount * ll_amount;
        qquadxx += norm * atan(ll_1a*pAffineTP.x);
        qquadyy += norm * atan(ll_1b*pAffineTP.y);



      }


      else if   (ll_quad == 9) {
//BCollide
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;
        int alt;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        alt = (int) (sigma * ll_bCn_pi);
        if (alt % 2 == 0)
          sigma = alt * ll_pi_bCn + fmod(sigma + ll_bCa_bCn, ll_pi_bCn);
        else
          sigma = alt * ll_pi_bCn + fmod(sigma - ll_bCa_bCn, ll_pi_bCn);
        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        qquadxx += pAmount * ll_amount * sinht / temp;
        qquadyy += pAmount * ll_amount * sins / temp;



      }


      else if   (ll_quad == 10) {
//BMod
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        if (tau < ll_1a && -tau < ll_1a) {
          tau = fmod(tau + ll_1a + ll_1b * ll_1a, 2.0 * ll_1a) - ll_1a;
        }

        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        if (temp == 0) {
          return;
        }
        qquadxx += pAmount * ll_amount * sinht / temp;
        qquadyy += pAmount * ll_amount * sins / temp;



      }


      else if   (ll_quad == 11) {
//BSwirl
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        sigma = sigma + tau * ll_1b + ll_1a / tau;

        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        if (temp == 0) {
          return;
        }
        qquadxx += pAmount * ll_amount * sinht / temp;
        qquadyy += pAmount * ll_amount * sins / temp;



      }


      else if   (ll_quad == 12) {
//BTransform
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y))) / ll_1a + 0;
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x) + 0;
        sigma = sigma / ll_1a + M_2PI / ll_1a * floor(pContext.random() * ll_1a);

        if (pAffineTP.x >= 0.0)
          tau += ll_1b;
        else
          tau -= ll_1b;
        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        qquadxx += pAmount * ll_amount * sinht / temp;
        qquadyy += pAmount * ll_amount * sins / temp;



      }

      else if   (ll_quad == 13) {
//Bent
        double nx = pAffineTP.x;
        double ny = pAffineTP.y;
        if (nx < 0)
          nx = nx * ll_1a;
        if (ny < 0)
          ny = ny * ll_1b;
        qquadxx += pAmount * ll_amount * nx;
        qquadyy += pAmount * ll_amount * ny;



      }



      else if   (ll_quad == 14) {
//Bipolar
        double x2y2 = (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
        double t = x2y2 +  ll_1b;
        double x2 = 2 * pAffineTP.x;
        double ps = -M_PI_2 * ll_1a;
        double yb = 0.5 * atan2(2.0 * pAffineTP.y, x2y2 - 1.0) + ps;

        if (yb > M_PI_2) {
          yb = -M_PI_2 + fmod(yb + M_PI_2, M_PI);
        } else if (yb < -M_PI_2) {
          yb = M_PI_2 - fmod(M_PI_2 - yb, M_PI);
        }

        double f = t + x2;
        double g = t - x2;

        if ((g == 0) || (f / g <= 0))
          return;
        qquadxx += pAmount * ll_amount * 0.25 * M_2_PI * log((t + x2) / (t - x2));
        qquadyy += pAmount * ll_amount * M_2_PI * yb;



      }

      else if   (ll_quad == 15) {
//Blob

        double a = atan2(pAffineTP.x, pAffineTP.y);
        double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
        r = r * (ll_1a + (ll_1b - ll_1a) * (0.5 + 0.5 * sin(6 * a)));
        double nx = sin(a) * r;
        double ny = cos(a) * r;

        qquadxx += pAmount * ll_amount * nx;
        qquadyy += pAmount * ll_amount * ny;

      }


      else if   (ll_quad == 16) {
//Butterfly
        double wx = pAmount * ll_amount * ll_1a;

        double y2 = pAffineTP.y * ll_1b;
        double r = wx * sqrt(fabs(pAffineTP.y * pAffineTP.x) / (SMALL_EPSILON + pAffineTP.x * pAffineTP.x + y2 * y2));

        qquadxx += r * pAffineTP.x;
        qquadyy += r * y2;

      }


      else if   (ll_quad == 17) {
//Collideoscope

        double aaa = atan2(pAffineTP.y, pAffineTP.x);
        double r = pAmount * ll_amount * sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
        int alt;

        if (aaa >= 0.0) {
          alt = (int) (aaa * ll_kn_pi);
          if (alt % 2 == 0) {
            aaa = alt * ll_pi_kn + fmod(ll_ka_kn + aaa, ll_pi_kn);
          } else {
            aaa = alt * ll_pi_kn + fmod(-ll_ka_kn + aaa, ll_pi_kn);
          }
        } else {
          alt = (int) (-aaa * ll_kn_pi);
          if (alt % 2 != 0) {
            aaa = -(alt * ll_pi_kn + fmod(-ll_ka_kn - aaa, ll_pi_kn));
          } else {
            aaa = -(alt * ll_pi_kn + fmod(ll_ka_kn - aaa, ll_pi_kn));
          }
        }

        double s = sin(aaa);
        double c = cos(aaa);

        qquadxx += r * c;
        qquadyy += r * s;



      }


      else if   (ll_quad == 18) {
//CPOW

        double a = pAffineTP.getPrecalcAtanYX();
        double lnr = 0.5 * log(pAffineTP.getPrecalcSumsq());
        double va = 2.0 * M_PI / 2;
        double vc = ll_1a / 2;
        double vd = ll_1b / 2;
        double ang = vc * a + vd * lnr + va * floor(2 * pContext.random());

        double m = pAmount * ll_amount * exp(vc * lnr - vd * a);
        double sa = sin(ang);
        double ca = cos(ang);

        qquadxx += m * ca;
        qquadyy += m * sa;



      }



      else if   (ll_quad == 19) {
//Curl
        double re = 1 + ll_1a * pAffineTP.x + ll_1b * (sqr(pAffineTP.x) - sqr(pAffineTP.y));
        double im = ll_1a * pAffineTP.y + ll_1b * 2 * pAffineTP.x * pAffineTP.y;

        double r = pAmount * ll_amount / (sqr(re) + sqr(im));

        qquadxx += (pAffineTP.x * re + pAffineTP.y * im) * r;
        qquadyy += (pAffineTP.y * re - pAffineTP.x * im) * r;



      }

      else if   (ll_quad == 20) {
//Devil Warp
        double xx = pAffineTP.x;
        double yy = pAffineTP.y;
        double r2 = 1.0 / (xx * xx + yy * yy);
        double r = pow(xx * xx + r2 * 1 * yy * yy, ll_1b) - pow(yy * yy + r2 * 2 * xx * xx, ll_1b);
        if (r > 100)
          r = 100;
        else if (r < -0.24)
          r = -0.24;
        r = ll_1a * (r);
        qquadxx += ll_amount * xx * (1 + r);
        qquadyy += ll_amount * yy * (1 + r);



      }


      else if   (ll_quad == 21) {
//Disc
        double rPI = M_PI * sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y)* ll_1a;
        double sinr = sin(rPI);
        double cosr = cos(rPI);
        double r = pAmount * ll_amount * pAffineTP.getPrecalcAtan() / M_PI;
        qquadxx += sinr * r;
        qquadyy += cosr * r;



      }

      else if   (ll_quad == 22) {
// ECollide


        double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        double sinnu, cosnu;
        int alt;
        if (xmax < 1.0)
          xmax = 1.0;

        double t = pAffineTP.x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;
        double nu = acos(t); // -Pi < nu < Pi

        alt = (int) (nu * ll_eCn_pi);
        if (alt % 2 == 0)
          nu = alt * ll_pi_eCn + fmod(nu + ll_eCa_eCn, ll_pi_eCn);
        else
          nu = alt * ll_pi_eCn + fmod(nu - ll_eCa_eCn, ll_pi_eCn);
        if (pAffineTP.y <= 0.0)
          nu *= -1.0;
        sinnu = sin(nu);
        cosnu = cos(nu);
        qquadxx += pAmount * ll_amount * xmax * cosnu;
        qquadyy += pAmount * ll_amount * sqrt(xmax - 1.0) * sqrt(xmax + 1.0) * sinnu;



      }


      else if   (ll_quad == 23) {
// EDisc
        double tmp = pAffineTP.getPrecalcSumsq() + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double r1 = sqrt(tmp + tmp2);
        double r2 = sqrt(tmp - tmp2);
        double xmax = (r1 + r2) * 0.5;
        double a1 = log(xmax + sqrt(xmax - 1.0));
        double a2 = -acos(pAffineTP.x / xmax);
        double w = pAmount * ll_amount / 11.57034632;

        double snv = sin(a1);
        double csv = cos(a1);
        double snhu = sinh(a2);
        double cshu = cosh(a2);

        if (pAffineTP.y > 0.0) {
          snv = -snv;
        }

        qquadxx += w * cshu * csv;
        qquadyy += w * snhu * snv;



      }

      else if   (ll_quad == 24) {
// EJulia
        double r2 = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x;
        double tmp2;
        double x;
        if (ll_sign == 1)
          x = pAffineTP.x;
        else {
          r2 = 1.0 / r2;
          x = pAffineTP.x * r2;
        }

        double tmp = r2 + 1.0;
        tmp2 = 2.0 * x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        if (xmax < 1.0)
          xmax = 1.0;
        double sinhmu, coshmu, sinnu, cosnu;

        double mu = acosh(xmax); //  mu > 0
        double t = x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;

        double nu = acos(t); // -Pi < nu < Pi
        if (pAffineTP.y < 0)
          nu *= -1.0;

        nu = nu / ll_1a + M_2PI / ll_1a * floor(pContext.random() * ll_1a);
        mu /= ll_1a;

        sinhmu = sinh(mu);
        coshmu = cosh(mu);

        sinnu = sin(nu);
        cosnu = cos(nu);
        qquadxx += pAmount * ll_amount * coshmu * cosnu;
        qquadyy += pAmount * ll_amount * sinhmu * sinnu;




      }


      else if   (ll_quad == 25) {
// EMOD

        double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        if (xmax < 1.0)
          xmax = 1.0;
        double sinhmu, coshmu;

        double mu = acosh(xmax); //  mu > 0
        double t = pAffineTP.x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;

        double nu = acos(t); // -Pi < nu < Pi
        if (pAffineTP.y < 0)
          nu *= -1.0;

        if (mu < ll_1a && -mu < ll_1a) {
          if (nu > 0.0)
            mu = fmod(mu + ll_1a + ll_1b * ll_1a, 2.0 * ll_1a) - ll_1a;
          else
            mu = fmod(mu - ll_1a - ll_1b * ll_1a, 2.0 * ll_1a) + ll_1a;
        }

        sinhmu = sinh(mu);
        coshmu = cosh(mu);

        qquadxx += pAmount * ll_amount * coshmu * cos(nu);
        qquadyy += pAmount * ll_amount * sinhmu * sin(nu);

      }
	  
      else if   (ll_quad == 26) {
// Eclipse 
    if (fabs(pAffineTP.y) <= pAmount) {
      double c_2 = sqrt(sqr(pAmount)
              - sqr(pAffineTP.y));

      if (fabs(pAffineTP.x) <= c_2) {
        double x = pAffineTP.x + this.ll_1a * pAmount * ll_amount;
        if (fabs(x) >= c_2) {
          qquadxx -= pAmount * ll_amount * pAffineTP.x;
        } else {
          qquadxx += pAmount * ll_amount * x;
        }
      } else {
        qquadxx += pAmount * ll_amount * pAffineTP.x;
      }
      qquadyy += pAmount * ll_amount * pAffineTP.y;
    } else {
      qquadxx += pAmount * ll_amount * pAffineTP.x;
      qquadyy += pAmount * ll_amount * pAffineTP.y;
		}
	  }

      else if   (ll_quad == 27) {
// Elliptic 
    double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + ll_1a;
    double x2 = 2.0 * pAffineTP.x;
    double xmax = 0.5 * (sqrt(tmp + x2) + sqrt(tmp - x2));
    double a = pAffineTP.x / xmax;
    double b = sqrt_safe_e(ll_1b - a * a);
    qquadxx += ll_v * atan2(a, b);
    //    if (pAffineTP.y > 0)
    if (pContext.random() < 0.5)
      qquadyy += ll_v * log(xmax + sqrt_safe_e(xmax - 1.0));
    else
      qquadyy -= ll_v * log(xmax + sqrt_safe_e(xmax - 1.0));
	  }
 
      else if   (ll_quad == 28) {
// Fan2
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double angle;
    if ((pAffineTP.x < -SMALL_EPSILON) || (pAffineTP.x > SMALL_EPSILON) || (pAffineTP.y < -SMALL_EPSILON) || (pAffineTP.y > SMALL_EPSILON)) {
      angle = atan2(pAffineTP.x, pAffineTP.y);
    } else {
      angle = 0.0;
    }

    double dy = ll_1b;
    double dx = M_PI * (ll_1a * ll_1a) + SMALL_EPSILON;
    double dx2 = dx * 0.5;

    double t = angle + dy - (int) ((angle + dy) / dx) * dx;
    double a;
    if (t > dx2) {
      a = angle - dx2;
    } else {
      a = angle + dx2;
    }

    qquadxx += pAmount * ll_amount * r * sin(a);
    qquadyy += pAmount * ll_amount * r * cos(a);
	  }

      else if   (ll_quad == 29) {
// Foci
    double expx = exp(pAffineTP.x) * 0.5;
    double expnx = 0.25 / expx;
    if (expx <= SMALL_EPSILON || expnx <= SMALL_EPSILON) {
      return;
    }
    double siny = sin(pAffineTP.y);
    double cosy = cos(pAffineTP.y);

    double tmp = (expx + expnx - cosy);
    if (tmp == 0)
      return;
    tmp = pAmount * ll_amount / tmp;

    qquadxx += (expx - expnx) * tmp;
    qquadyy += siny * tmp;
	  }
  

      else if   (ll_quad == 30) {
// Glynnia
    double r = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double d;

    if (r >= 1.0) {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        qquadxx+= ll_vvar2 * d;
        qquadyy -= ll_vvar2 / d * pAffineTP.y; //+= _vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount * ll_amount / dx;
        qquadxx += r * d;
        qquadyy += r * pAffineTP.y; //-= r * pAffineTP.y; 
      }
    } else {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        qquadxx -= ll_vvar2 * d;
        qquadyy -= ll_vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount * ll_amount / dx;
        qquadxx -= r * d;
        qquadyy += r * pAffineTP.y;
		}
		}
	  }

      else if   (ll_quad == 31) {
// Hole
    double alpha = atan2(pAffineTP.y,pAffineTP.x);
    double delta = pow(alpha/M_PI + 1.f, ll_1a);
    double r;
    if (ll_1b!=0)
      r = pAmount * ll_amount *delta/(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    else
      r = pAmount * ll_amount *sqrt(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    double s = sin(alpha);
    double c = cos(alpha);
    qquadxx += r * c;
    qquadyy += r * s;
	  }
 
      else if   (ll_quad == 32) {
// Idisc
    double a = M_PI / (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + 1.0);
    double r = atan2(pAffineTP.y, pAffineTP.x) * ll_v_idisc;

    sinAndCos(a, sina, cosa);

    qquadxx += r * cosa.value;
    qquadyy += r * sina.value;
	  }
 
      else if   (ll_quad == 33) {
// JuliaN
    double a = (atan2(pAffineTP.y, pAffineTP.x) + 2 * M_PI * pContext.random(ll_absPower_julian)) / ll_1a;
    double sina = sin(a);
    double cosa = cos(a);
    double r = pAmount * ll_amount * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), ll_cPower_julian);

    qquadxx = qquadxx + r * cosa;
    qquadyy = qquadyy + r * sina;
	  }

      else if   (ll_quad == 34) {
// Lazy Sensen
    if (ll_1a != 0.0) {
      double nr = (int) floor(pAffineTP.x * ll_1a);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.x = -pAffineTP.x;
      } else {
        if (nr % 2 == 0)
          pAffineTP.x = -pAffineTP.x;
      }
    }
    if (ll_1b != 0.0) {
      double nr = (int) floor(pAffineTP.y * ll_1b);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.y = -pAffineTP.y;
      } else {
        if (nr % 2 == 0)
          pAffineTP.y = -pAffineTP.y;
      }
    }
 
    qquadxx += pAmount * ll_amount * pAffineTP.x;
    qquadyy += pAmount * ll_amount * pAffineTP.y;
	  }

      else if   (ll_quad == 35) {
// LazySusan
	double x = 0.0;
	double y = 0.0;
    double xx = pAffineTP.x - x;
    double yy = pAffineTP.y + y;
    double rr = sqrt(xx * xx + yy * yy);

    if (rr < pAmount) {
      double a = atan2(yy, xx) + ll_1b + ll_1a * (pAmount * ll_amount - rr);
      double sina = sin(a);
      double cosa = cos(a);
      rr = pAmount * ll_amount * rr;

      qquadxx += rr * cosa + x;
      qquadyy += rr * sina - y;
    } else {
      rr = pAmount * ll_amount * (1.0+0.0 / rr);

      qquadxx += rr * xx + x;
      qquadyy += rr * yy - y;
    }
    }

      else if   (ll_quad == 36) {
// Murl2
	double _p2, _invp, _vp;
	double _sina, _cosa, _a, _r, _re, _im, _rl;

    _p2 = (double) ll_1b / 2.0;

    if (ll_1b != 0) {
      _invp = 1.0 / (double) ll_1b;
      if (ll_1a == -1) {
        _vp = 0;
      } else {
        _vp = pAmount * ll_amount * pow(ll_1a + 1, 2.0 / ((double) ll_1b));
      }
    } else {
      _invp = 100000000000.0;
      _vp = pAmount * ll_amount * pow(ll_1a + 1, 4 /*Normally infinity, but we let this be a special case*/);
    }

    _a = atan2(pAffineTP.y, pAffineTP.x) * (double) ll_1b;
    _sina = sin(_a);
    _cosa = cos(_a);

    _r = ll_1a * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), _p2);

    _re = _r * _cosa + 1;
    _im = _r * _sina;

    _r = pow(sqr(_re) + sqr(_im), _invp);
    _a = atan2(_im, _re) * 2.0 * _invp;
    _re = _r * cos(_a);
    _im = _r * sin(_a);

    _rl = _vp / sqr(_r);

    qquadxx += _rl * (pAffineTP.x * _re + pAffineTP.y * _im);
    qquadyy += _rl * (pAffineTP.y * _re - pAffineTP.x * _im);
    }
 

      else if   (ll_quad == 37) {
// Ortho
    double r, a, ta;
    double xo;
    double ro;
    double c, s;
    double x, y, tc, ts;
    double theta;

    r = sqr(pAffineTP.x) + sqr(pAffineTP.y);

    if (r < 1.0) { // && FTx > 0.0 && FTy > 0.0) 
      if (pAffineTP.x >= 0.0) {
        xo = (r + 1.0) / (2.0 * pAffineTP.x);
        ro = sqrt(sqr(pAffineTP.x - xo) + sqr(pAffineTP.y));
        theta = atan2(1.0, ro);
        a = fmod(ll_1a * theta + atan2(pAffineTP.y, xo - pAffineTP.x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        qquadxx += pAmount * ll_amount * (xo - c * ro);
        qquadyy += pAmount * ll_amount * s * ro;
      } else {
        xo = -(r + 1.0) / (2.0 * pAffineTP.x);
        ro = sqrt(sqr(-pAffineTP.x - xo) + sqr(pAffineTP.y));
        theta = atan2(1.0, ro);
        a = fmod(ll_1a * theta + atan2(pAffineTP.y, xo + pAffineTP.x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        qquadxx -= pAmount * ll_amount * (xo - c * ro);
        qquadyy += pAmount * ll_amount * s * ro;
      }
    } else {
      r = 1.0 / sqrt(r);
      ta = atan2(pAffineTP.y, pAffineTP.x);
      ts = sin(ta);
      tc = cos(ta);

      x = r * tc;
      y = r * ts;

      if (x >= 0.0) {
        xo = (sqr(x) + sqr(y) + 1.0) / (2.0 * x);
        ro = sqrt(sqr(x - xo) + sqr(y));
        theta = atan2(1.0, ro);
        a = fmod(ll_1b * theta + atan2(y, xo - x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        x = (xo - c * ro);
        y = s * ro;
        ta = atan2(y, x);
        ts = sin(ta);
        tc = cos(ta);

        r = 1.0 / sqrt(sqr(x) + sqr(y));

        qquadxx += pAmount * ll_amount * r * tc;
        qquadyy += pAmount * ll_amount * r * ts;
      } else {
        xo = -(sqr(x) + sqr(y) + 1.0) / (2.0 * x);
        ro = sqrt(sqr(-x - xo) + sqr(y));
        theta = atan2(1.0, ro);
        a = fmod(ll_1b * theta + atan2(y, xo + x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        x = (xo - c * ro);
        y = s * ro;
        ta = atan2(y, x);
        ts = sin(ta);
        tc = cos(ta);
        r = 1.0 / sqrt(sqr(x) + sqr(y));

        qquadxx -= pAmount * ll_amount * r * tc;
        qquadyy += pAmount * ll_amount * r * ts;

      }
    }
    }
 
      else if   (ll_quad == 38) {
// Rectangles
    if (fabs(ll_1a) < EPSILON) {
      qquadxx += pAmount * ll_amount * pAffineTP.x;
    } else {
      qquadxx += pAmount * ll_amount * ((2 * floor(pAffineTP.x / ll_1a) + 1) * ll_1a - pAffineTP.x);
    }
    if (fabs(ll_1b) < EPSILON) {
      qquadyy += pAmount * ll_amount * pAffineTP.y;
    } else {
      qquadyy += pAmount * ll_amount * ((2 * floor(pAffineTP.y / ll_1b) + 1) * ll_1b - pAffineTP.y);
    }
    }

      else if   (ll_quad == 39) {
// Sinusoidal
    qquadxx += pAmount * ll_amount * sin(pAffineTP.x);
    qquadyy += pAmount * ll_amount * sin(pAffineTP.y);
    }

      else if   (ll_quad == 40) {
// SphericalN
    double R = pow(sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)), this.ll_1b);
    int N = (int) floor(this.ll_1a * pContext.random());
    double alpha = atan2(pAffineTP.y, pAffineTP.x) + N * M_2PI / floor(this.ll_1a);
    double sina = sin(alpha);
    double cosa = cos(alpha);

    if (R > SMALL_EPSILON) {
      qquadxx += pAmount * ll_amount * cosa / R;
      qquadyy += pAmount * ll_amount * sina / R;
    }
    }

      else if   (ll_quad == 41) {
// Waves2
    qquadxx += pAmount * ll_amount  * (pAffineTP.x + ll_1a * sin(pAffineTP.y * ll_1b));
    qquadyy += pAmount * ll_amount  * (pAffineTP.y + ll_1a * sin(pAffineTP.x * ll_1b));
    }


      else if   (ll_quad == 42) {
// Waves22
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	int modex = 0;
    int modey = 0;
	double sinx;
	double siny;
	int px =  (int)2.0;
	int py =  (int)2.0;	

	if (modex < 0.5){
        sinx = sin(y0 * ll_1b);
    } else {
        sinx = 0.5 * (1.0 + sin(y0 * ll_1b));
    }
	double offsetx = pow(sinx, px) * ll_1a;
	if (modey < 0.5){
        siny = sin(x0 * ll_1b);
    } else {
        siny = 0.5 * (1.0 + sin(x0 * ll_1b));
    }
    double offsety = pow(siny, py) * ll_1a;
    
    qquadxx += pAmount * ll_amount * (x0 + offsetx);
    qquadyy += pAmount * ll_amount * (y0 + offsety);
    }

      else if   (ll_quad == 43) {
// Waves23
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double mx = y0 * ll_1b * M_1_2PI;
	double fx = mx - floor(mx);
	if (fx > 0.5) fx = 0.5 - fx;
	double my = x0 * ll_1b * M_1_2PI;
	double fy = my - floor(my);
	if (fy > 0.5) fy = 0.5 - fy;
    qquadxx += pAmount * ll_amount * (x0 + fx * ll_1a);
    qquadyy += pAmount * ll_amount * (y0 + fy * ll_1a);
    }

      else if   (ll_quad == 44) {
// Waves3
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double scalexx = 0.5 * ll_1a * (1.0 + sin(y0 * 0.0));
	double scaleyy = 0.5 * ll_1a * (1.0 + sin(x0 * 2.0));
    qquadxx += pAmount * ll_amount * (x0 + sin(y0 * ll_1b) * scalexx);
    qquadyy += pAmount * ll_amount * (y0 + sin(x0 * ll_1b) * scaleyy);
    }


      else if   (ll_quad == 45) {
// Waves4
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	
	double ax = floor(y0 * ll_1b / M_2PI);

    ax = sin(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * 0.1) * 43758.5453;
    ax = ax - (int) ax;
    //if (0.0 == 1) ax = (ax > 0.5) ? 1.0 : 0.0;

    
    qquadxx += pAmount * ll_amount * (x0 + sin(y0 * ll_1b) * ax * ax * ll_1a);
    qquadyy += pAmount * ll_amount * (y0 + sin(x0 * ll_1b) * ll_1a);
    }

      else if   (ll_quad == 46) {
// Waves42
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	
	double ax = floor(y0 * ll_1b);

    ax = sin(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * 0.1) * 43758.5453;
    ax = ax - (int) ax;
    //if (0.0 == 1) ax = (ax > 0.5) ? 1.0 : 0.0;

    
    qquadxx += pAmount * ll_amount * (x0 + sin(y0 * ll_1b) * ax * ax * ll_1a);
    qquadyy += pAmount * ll_amount * (y0 + sin(x0 * ll_1b) * ll_1a);
    }

      else if   (ll_quad == 47) {
// Wdisc
    double a = M_PI / (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + ll_1a);
    double r = atan2(pAffineTP.y, pAffineTP.x) * M_1_PI;

    if (r > 0.0)
      a = M_PI - a;

    sinAndCos(a, sina, cosa);

    qquadxx += pAmount * ll_amount * r * cosa.value;
    qquadyy += pAmount * ll_amount * r * sina.value;
    }

      else if   (ll_quad == 48) {
// Whorl
    double r = pAffineTP.getPrecalcSqrt();
    double a;
    if (r < pAmount)
      a = pAffineTP.getPrecalcAtanYX() + ll_1a / (pAmount - r);
    else
      a = pAffineTP.getPrecalcAtanYX() + ll_1b / (pAmount - r);

    double sa = sin(a);
    double ca = cos(a);

    qquadxx += pAmount * ll_amount * r * ca;
    qquadyy += pAmount * ll_amount * r * sa;
    }

 


       count += 1;

    }
    if (pAffineTP.x < ul_shiftx && pAffineTP.y < ul_shifty) // kuadran II: UpperLeft
    {
      if   (ul_quad == 0) {
// Linear
        qquadxx += pAmount * ul_amount * pAffineTP.x;
        qquadyy += pAmount * ul_amount * pAffineTP.y;



      }
      else if   (ul_quad == 1) {
// Spherical
        double r = pAmount * ul_amount / (pAffineTP.x * pAffineTP.x * ul_1a + pAffineTP.y * pAffineTP.y * ul_1b + SMALL_EPSILON);
        qquadxx += pAffineTP.x * r;
        qquadyy += pAffineTP.y * r;



      }

      else if   (ul_quad == 2) {
// Acosech
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcosecH();
        z.Flip();
        z.Scale(pAmount * ul_amount * M_2_PI);

        if (pContext.random() < 0.5) {
          qquadyy += z.im;
          qquadxx += z.re;
        } else {
          qquadyy += -z.im;
          qquadxx += -z.re;
        }



      }

      else if   (ul_quad == 3) {
// Acosh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcosH();

        z.Scale(pAmount * ul_amount * M_2_PI);

        if (pContext.random() < 0.5) {
          qquadyy += z.im;
          qquadxx += z.re;
        } else {
          qquadyy += -z.im;
          qquadxx += -z.re;
        }



      }

      else if   (ul_quad == 4) {
// Acoth
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AcotH();
        z.Flip();
        z.Scale(pAmount * ul_amount * M_2_PI);

        qquadyy += z.im;
        qquadxx += z.re;




      }

      else if   (ul_quad == 5) {
//Apollony
        double xxx, yyy, a0, b0, f1x, f1y;
        double r = sqrt(3.0);

        a0 = 3.0 * (1.0 + r - pAffineTP.x) / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y) - (1.0 + r) / (2.0 + r);
        b0 = 3.0 * pAffineTP.y / (pow(1.0 + r - pAffineTP.x, 2.0) + pAffineTP.y * pAffineTP.y);
        f1x = a0 / (a0 * a0 + b0 * b0);
        f1y = -b0 / (a0 * a0 + b0 * b0);

        int w = (int) (4.0 * pContext.random());

        if ((w % 3) == 0) {
          xxx = a0;
          yyy = b0;
        } else if ((w % 3) == 1) {
          xxx = -f1x / 2.0 - f1y * r / 2.0;
          yyy = f1x * r / 2.0 - f1y / 2.0;
        } else {
          xxx = -f1x / 2.0 + f1y * r / 2.0;
          yyy = -f1x * r / 2.0 - f1y / 2.0;
  
  
  
        }

        qquadxx += xxx * pAmount *  ul_amount;
        qquadyy += yyy * pAmount *  ul_amount;
      }

      else if   (ul_quad == 6) {
// Arcsinh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);

        z.AsinH();
        z.Scale(pAmount * ul_amount * M_2_PI);

        qquadyy += z.im;
        qquadxx += z.re;



      }
      else if   (ul_quad == 7) {
//Arctanh
        Complex z = new Complex(pAffineTP.x, pAffineTP.y);
        Complex z2 = new Complex(z);
        z2.Scale(-1.0);
        z2.Inc(); // -z+1
        Complex z3 = new Complex(z);
        z3.Inc(); // z+1
        z3.Div(z2);
        z3.Log();
        z3.Scale(pAmount *  ul_amount * M_2_PI);
        qquadxx += z3.re;
        qquadyy += z3.im;



      }

      else if   (ul_quad == 8) {
//Atan Mode 3
        double norm = 1.0 / M_PI_2 * pAmount * ul_amount;
        qquadxx += norm * atan(ul_1a*pAffineTP.x);
        qquadyy += norm * atan(ul_1b*pAffineTP.y);



      }


      else if   (ul_quad == 9) {
//BCollide
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;
        int alt;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        alt = (int) (sigma * ul_bCn_pi);
        if (alt % 2 == 0)
          sigma = alt * ul_pi_bCn + fmod(sigma + ul_bCa_bCn, ul_pi_bCn);
        else
          sigma = alt * ul_pi_bCn + fmod(sigma - ul_bCa_bCn, ul_pi_bCn);
        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        qquadxx += pAmount * ul_amount * sinht / temp;
        qquadyy += pAmount * ul_amount * sins / temp;




      }


      else if   (ul_quad == 10) {
//BMod
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        if (tau < ul_1a && -tau < ul_1a) {
          tau = fmod(tau + ul_1a + ul_1b * ul_1a, 2.0 * ul_1a) - ul_1a;
        }

        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        if (temp == 0) {
          return;
        }
        qquadxx += pAmount * ul_amount * sinht / temp;
        qquadyy += pAmount * ul_amount * sins / temp;



      }


      else if   (ul_quad == 11) {
//BSwirl
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y)));
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x);

        sigma = sigma + tau * ul_1b + ul_1a / tau;

        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        if (temp == 0) {
          return;
        }
        qquadxx += pAmount * ul_amount * sinht / temp;
        qquadyy += pAmount * ul_amount * sins / temp;



      }


      else if   (ul_quad == 12) {
//BTransform
        double tau, sigma;
        double temp;
        double cosht, sinht;
        double sins, coss;

        tau = 0.5 * (log(sqr(pAffineTP.x + 1.0) + sqr(pAffineTP.y)) - log(sqr(pAffineTP.x - 1.0) + sqr(pAffineTP.y))) / ul_1a + 0;
        sigma = M_PI - atan2(pAffineTP.y, pAffineTP.x + 1.0) - atan2(pAffineTP.y, 1.0 - pAffineTP.x) + 0;
        sigma = sigma / ul_1a + M_2PI / ul_1a * floor(pContext.random() * ul_1a);

        if (pAffineTP.x >= 0.0)
          tau += ul_1b;
        else
          tau -= ul_1b;
        sinht = sinh(tau);
        cosht = cosh(tau);
        sins = sin(sigma);
        coss = cos(sigma);
        temp = cosht - coss;
        qquadxx += pAmount * ul_amount * sinht / temp;
        qquadyy += pAmount * ul_amount * sins / temp;



      }

      else if   (ul_quad == 13) {
//Bent
        double nx = pAffineTP.x;
        double ny = pAffineTP.y;
        if (nx < 0)
          nx = nx * ul_1a;
        if (ny < 0)
          ny = ny * ul_1b;
        qquadxx += pAmount * ul_amount * nx;
        qquadyy += pAmount * ul_amount * ny;



      }


      else if   (ul_quad == 14) {
//Bipolar
        double x2y2 = (pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
        double t = x2y2 +  ul_1b;
        double x2 = 2 * pAffineTP.x;
        double ps = -M_PI_2 * ul_1a;
        double yb = 0.5 * atan2(2.0 * pAffineTP.y, x2y2 - 1.0) + ps;

        if (yb > M_PI_2) {
          yb = -M_PI_2 + fmod(yb + M_PI_2, M_PI);
        } else if (yb < -M_PI_2) {
          yb = M_PI_2 - fmod(M_PI_2 - yb, M_PI);
        }

        double f = t + x2;
        double g = t - x2;

        if ((g == 0) || (f / g <= 0))
          return;
        qquadxx += pAmount * ul_amount * 0.25 * M_2_PI * log((t + x2) / (t - x2));
        qquadyy += pAmount * ul_amount * M_2_PI * yb;



      }

      else if   (ul_quad == 15) {
//Blob

        double a = atan2(pAffineTP.x, pAffineTP.y);
        double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
        r = r * (ul_1a + (ul_1b - ul_1a) * (0.5 + 0.5 * sin(6 * a)));
        double nx = sin(a) * r;
        double ny = cos(a) * r;

        qquadxx += pAmount * ul_amount * nx;
        qquadyy += pAmount * ul_amount * ny;

      }


      else if   (ul_quad == 16) {
//Butterfly
        double wx = pAmount * ul_amount * ul_1a;

        double y2 = pAffineTP.y * ul_1b;
        double r = wx * sqrt(fabs(pAffineTP.y * pAffineTP.x) / (SMALL_EPSILON + pAffineTP.x * pAffineTP.x + y2 * y2));

        qquadxx += r * pAffineTP.x;
        qquadyy += r * y2;

      }


      else if   (ul_quad == 17) {
//Collideoscope

        double aaa = atan2(pAffineTP.y, pAffineTP.x);
        double r = pAmount * ul_amount * sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
        int alt;

        if (aaa >= 0.0) {
          alt = (int) (aaa * ul_kn_pi);
          if (alt % 2 == 0) {
            aaa = alt * ul_pi_kn + fmod(ul_ka_kn + aaa, ul_pi_kn);
          } else {
            aaa = alt * ul_pi_kn + fmod(-ul_ka_kn + aaa, ul_pi_kn);
          }
        } else {
          alt = (int) (-aaa * ul_kn_pi);
          if (alt % 2 != 0) {
            aaa = -(alt * ul_pi_kn + fmod(-ul_ka_kn - aaa, ul_pi_kn));
          } else {
            aaa = -(alt * ul_pi_kn + fmod(ul_ka_kn - aaa, ul_pi_kn));
          }
        }

        double s = sin(aaa);
        double c = cos(aaa);

        qquadxx += r * c;
        qquadyy += r * s;



      }

      else if   (ul_quad == 18) {
//CPOW

        double a = pAffineTP.getPrecalcAtanYX();
        double lnr = 0.5 * log(pAffineTP.getPrecalcSumsq());
        double va = 2.0 * M_PI / 2;
        double vc = ul_1a / 2;
        double vd = ul_1b / 2;
        double ang = vc * a + vd * lnr + va * floor(2 * pContext.random());

        double m = pAmount * ul_amount * exp(vc * lnr - vd * a);
        double sa = sin(ang);
        double ca = cos(ang);

        qquadxx += m * ca;
        qquadyy += m * sa;



      }


      else if   (ul_quad == 19) {
//Curl
        double re = 1 + ul_1a * pAffineTP.x + ul_1b * (sqr(pAffineTP.x) - sqr(pAffineTP.y));
        double im = ul_1a * pAffineTP.y + ul_1b * 2 * pAffineTP.x * pAffineTP.y;

        double r = pAmount * ul_amount / (sqr(re) + sqr(im));

        qquadxx += (pAffineTP.x * re + pAffineTP.y * im) * r;
        qquadyy += (pAffineTP.y * re - pAffineTP.x * im) * r;



      }



      else if   (ul_quad == 20) {
//Devil Warp
        double xx = pAffineTP.x;
        double yy = pAffineTP.y;
        double r2 = 1.0 / (xx * xx + yy * yy);
        double r = pow(xx * xx + r2 * 1 * yy * yy, ul_1b) - pow(yy * yy + r2 * 2 * xx * xx, ul_1b);
        if (r > 100)
          r = 100;
        else if (r < -0.24)
          r = -0.24;
        r = ul_1a * (r);
        qquadxx += ul_amount * xx * (1 + r);
        qquadyy += ul_amount * yy * (1 + r);



      }


      else if   (ul_quad == 21) {
//Disc
        double rPI = M_PI * sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y)* ul_1a;
        double sinr = sin(rPI);
        double cosr = cos(rPI);
        double r = pAmount * ul_amount * pAffineTP.getPrecalcAtan() / M_PI;
        qquadxx += sinr * r;
        qquadyy += cosr * r;



      }


      else if   (ul_quad == 22) {
// ECollide


        double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        double sinnu, cosnu;
        int alt;
        if (xmax < 1.0)
          xmax = 1.0;

        double t = pAffineTP.x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;
        double nu = acos(t); // -Pi < nu < Pi

        alt = (int) (nu * ul_eCn_pi);
        if (alt % 2 == 0)
          nu = alt * ul_pi_eCn + fmod(nu + ul_eCa_eCn, ul_pi_eCn);
        else
          nu = alt * ul_pi_eCn + fmod(nu - ul_eCa_eCn, ul_pi_eCn);
        if (pAffineTP.y <= 0.0)
          nu *= -1.0;
        sinnu = sin(nu);
        cosnu = cos(nu);
        qquadxx += pAmount * ul_amount * xmax * cosnu;
        qquadyy += pAmount * ul_amount * sqrt(xmax - 1.0) * sqrt(xmax + 1.0) * sinnu;



      }


      else if   (ul_quad == 23) {
// EDisc
        double tmp = pAffineTP.getPrecalcSumsq() + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double r1 = sqrt(tmp + tmp2);
        double r2 = sqrt(tmp - tmp2);
        double xmax = (r1 + r2) * 0.5;
        double a1 = log(xmax + sqrt(xmax - 1.0));
        double a2 = -acos(pAffineTP.x / xmax);
        double w = pAmount * ul_amount / 11.57034632;

        double snv = sin(a1);
        double csv = cos(a1);
        double snhu = sinh(a2);
        double cshu = cosh(a2);

        if (pAffineTP.y > 0.0) {
          snv = -snv;
        }

        qquadxx += w * cshu * csv;
        qquadyy += w * snhu * snv;



      }

      else if   (ul_quad == 24) {
// EJulia
        double r2 = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x;
        double tmp2;
        double x;
        if (ul_sign == 1)
          x = pAffineTP.x;
        else {
          r2 = 1.0 / r2;
          x = pAffineTP.x * r2;
        }

        double tmp = r2 + 1.0;
        tmp2 = 2.0 * x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        if (xmax < 1.0)
          xmax = 1.0;
        double sinhmu, coshmu, sinnu, cosnu;

        double mu = acosh(xmax); //  mu > 0
        double t = x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;

        double nu = acos(t); // -Pi < nu < Pi
        if (pAffineTP.y < 0)
          nu *= -1.0;

        nu = nu / ul_1a + M_2PI / ul_1a * floor(pContext.random() * ul_1a);
        mu /= ul_1a;

        sinhmu = sinh(mu);
        coshmu = cosh(mu);

        sinnu = sin(nu);
        cosnu = cos(nu);
        qquadxx += pAmount * ul_amount * coshmu * cosnu;
        qquadyy += pAmount * ul_amount * sinhmu * sinnu;




      }


      else if   (ul_quad == 25) {
// EMOD

        double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + 1.0;
        double tmp2 = 2.0 * pAffineTP.x;
        double xmax = (sqrt_safe(tmp + tmp2) + sqrt_safe(tmp - tmp2)) * 0.5;
        if (xmax < 1.0)
          xmax = 1.0;
        double sinhmu, coshmu;

        double mu = acosh(xmax); //  mu > 0
        double t = pAffineTP.x / xmax;
        if (t > 1.0)
          t = 1.0;
        else if (t < -1.0)
          t = -1.0;

        double nu = acos(t); // -Pi < nu < Pi
        if (pAffineTP.y < 0)
          nu *= -1.0;

        if (mu < ll_1a && -mu < ll_1a) {
          if (nu > 0.0)
            mu = fmod(mu + ul_1a + ul_1b * ul_1a, 2.0 * ul_1a) - ul_1a;
          else
            mu = fmod(mu - ul_1a - ul_1b * ul_1a, 2.0 * ul_1a) + ul_1a;
        }

        sinhmu = sinh(mu);
        coshmu = cosh(mu);

        qquadxx += pAmount * ul_amount * coshmu * cos(nu);
        qquadyy += pAmount * ul_amount * sinhmu * sin(nu);

      }
	  
	  
      else if   (ul_quad == 26) {
// Eclipse 
    if (fabs(pAffineTP.y) <= pAmount) {
      double c_2 = sqrt(sqr(pAmount)
              - sqr(pAffineTP.y));

      if (fabs(pAffineTP.x) <= c_2) {
        double x = pAffineTP.x + this.ul_1a * pAmount * ul_amount;
        if (fabs(x) >= c_2) {
          qquadxx -= pAmount * ul_amount * pAffineTP.x;
        } else {
          qquadxx += pAmount * ul_amount * x;
        }
      } else {
        qquadxx += pAmount * ul_amount * pAffineTP.x;
      }
      qquadyy += pAmount * ul_amount * pAffineTP.y;
    } else {
      qquadxx += pAmount * ul_amount * pAffineTP.x;
      qquadyy += pAmount * ul_amount * pAffineTP.y;
		}
	  }

      else if   (ul_quad == 27) {
// Elliptic 
    double tmp = pAffineTP.y * pAffineTP.y + pAffineTP.x * pAffineTP.x + ul_1a;
    double x2 = 2.0 * pAffineTP.x;
    double xmax = 0.5 * (sqrt(tmp + x2) + sqrt(tmp - x2));
    double a = pAffineTP.x / xmax;
    double b = sqrt_safe_e(ul_1b - a * a);
    qquadxx += ul_v * atan2(a, b);
    //    if (pAffineTP.y > 0)
    if (pContext.random() < 0.5)
      qquadyy += ul_v * log(xmax + sqrt_safe_e(xmax - 1.0));
    else
      qquadyy -= ul_v * log(xmax + sqrt_safe_e(xmax - 1.0));
	  }
 	  

      else if   (ul_quad == 28) {
// Fan2
    double r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double angle;
    if ((pAffineTP.x < -SMALL_EPSILON) || (pAffineTP.x > SMALL_EPSILON) || (pAffineTP.y < -SMALL_EPSILON) || (pAffineTP.y > SMALL_EPSILON)) {
      angle = atan2(pAffineTP.x, pAffineTP.y);
    } else {
      angle = 0.0;
    }

    double dy = ul_1b;
    double dx = M_PI * (ul_1a * ul_1a) + SMALL_EPSILON;
    double dx2 = dx * 0.5;

    double t = angle + dy - (int) ((angle + dy) / dx) * dx;
    double a;
    if (t > dx2) {
      a = angle - dx2;
    } else {
      a = angle + dx2;
    }

    qquadxx += pAmount * ul_amount * r * sin(a);
    qquadyy += pAmount * ul_amount * r * cos(a);
	  }

      else if   (ul_quad == 29) {
// Foci
    double expx = exp(pAffineTP.x) * 0.5;
    double expnx = 0.25 / expx;
    if (expx <= SMALL_EPSILON || expnx <= SMALL_EPSILON) {
      return;
    }
    double siny = sin(pAffineTP.y);
    double cosy = cos(pAffineTP.y);

    double tmp = (expx + expnx - cosy);
    if (tmp == 0)
      return;
    tmp = pAmount * ul_amount / tmp;

    qquadxx += (expx - expnx) * tmp;
    qquadyy += siny * tmp;
	  }
 
      else if   (ul_quad == 30) {
// Glynnia
    double r = sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y));
    double d;

    if (r >= 1.0) {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        qquadxx+= ul_vvar2 * d;
        qquadyy -= ul_vvar2 / d * pAffineTP.y; //+= _vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount * ul_amount / dx;
        qquadxx += r * d;
        qquadyy += r * pAffineTP.y; //-= r * pAffineTP.y; 
      }
    } else {
      if (pContext.random() > 0.5) {
        d = sqrt(r + pAffineTP.x);
        if (d == 0) {
          return;
        }
        qquadxx -= ul_vvar2 * d;
        qquadyy -= ul_vvar2 / d * pAffineTP.y;
      } else {
        d = r + pAffineTP.x;
        double dx = sqrt(r * (sqr(pAffineTP.y) + sqr(d)));
        if (dx == 0) {
          return;
        }
        r = pAmount * ul_amount / dx;
        qquadxx -= r * d;
        qquadyy += r * pAffineTP.y;
		}
		}
	  }
 
      else if   (ul_quad == 31) {
// Hole
    double alpha = atan2(pAffineTP.y,pAffineTP.x);
    double delta = pow(alpha/M_PI + 1.f, ul_1a);
    double r;
    if (ul_1b!=0)
      r = pAmount * ul_amount *delta/(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    else
      r = pAmount * ul_amount *sqrt(pAffineTP.x*pAffineTP.x + pAffineTP.y*pAffineTP.y + delta);
    double s = sin(alpha);
    double c = cos(alpha);
    qquadxx += r * c;
    qquadyy += r * s;
	  }

      else if   (ul_quad == 32) {
// Idisc
    double a = M_PI / (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + 1.0);
    double r = atan2(pAffineTP.y, pAffineTP.x) * ul_v_idisc;

    sinAndCos(a, sina, cosa);

    qquadxx += r * cosa.value;
    qquadyy += r * sina.value;
	  }

      else if   (ul_quad == 33) {
// JuliaN
    double a = (atan2(pAffineTP.y, pAffineTP.x) + 2 * M_PI * pContext.random(ul_absPower_julian)) / ul_1a;
    double sina = sin(a);
    double cosa = cos(a);
    double r = pAmount * ul_amount * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), ul_cPower_julian);

    qquadxx = qquadxx + r * cosa;
    qquadyy = qquadyy + r * sina;
	  }

      else if   (ul_quad == 34) {
// Lazy Sensen
    if (ul_1a != 0.0) {
      double nr = (int) floor(pAffineTP.x * ul_1a);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.x = -pAffineTP.x;
      } else {
        if (nr % 2 == 0)
          pAffineTP.x = -pAffineTP.x;
      }
    }
    if (ul_1b != 0.0) {
      double nr = (int) floor(pAffineTP.y * ul_1b);
      if (nr >= 0) {
        if (nr % 2 == 1)
          pAffineTP.y = -pAffineTP.y;
      } else {
        if (nr % 2 == 0)
          pAffineTP.y = -pAffineTP.y;
      }
    }
 
    qquadxx += pAmount * ul_amount * pAffineTP.x;
    qquadyy += pAmount * ul_amount * pAffineTP.y;
	  }

      else if   (ul_quad == 35) {
// LazySusan
	double x = 0.0;
	double y = 0.0;
    double xx = pAffineTP.x - x;
    double yy = pAffineTP.y + y;
    double rr = sqrt(xx * xx + yy * yy);

    if (rr < pAmount) {
      double a = atan2(yy, xx) + ul_1b + ul_1a * (pAmount * ul_amount - rr);
      double sina = sin(a);
      double cosa = cos(a);
      rr = pAmount * ul_amount * rr;

      qquadxx += rr * cosa + x;
      qquadyy += rr * sina - y;
    } else {
      rr = pAmount * ul_amount * (1.0+0.0 / rr);

      qquadxx += rr * xx + x;
      qquadyy += rr * yy - y;
    }
    }

      else if   (ul_quad == 36) {
// Murl2
	double _p2, _invp, _vp;
	double _sina, _cosa, _a, _r, _re, _im, _rl;

    _p2 = (double) ul_1b / 2.0;

    if (ul_1b != 0) {
      _invp = 1.0 / (double) ul_1b;
      if (ul_1a == -1) {
        _vp = 0;
      } else {
        _vp = pAmount * ul_amount * pow(ul_1a + 1, 2.0 / ((double) ul_1b));
      }
    } else {
      _invp = 100000000000.0;
      _vp = pAmount * ul_amount * pow(ul_1a + 1, 4 /*Normally infinity, but we let this be a special case*/);
    }

    _a = atan2(pAffineTP.y, pAffineTP.x) * (double) ul_1b;
    _sina = sin(_a);
    _cosa = cos(_a);

    _r = ul_1a * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), _p2);

    _re = _r * _cosa + 1;
    _im = _r * _sina;

    _r = pow(sqr(_re) + sqr(_im), _invp);
    _a = atan2(_im, _re) * 2.0 * _invp;
    _re = _r * cos(_a);
    _im = _r * sin(_a);

    _rl = _vp / sqr(_r);

    qquadxx += _rl * (pAffineTP.x * _re + pAffineTP.y * _im);
    qquadyy += _rl * (pAffineTP.y * _re - pAffineTP.x * _im);
    }
 

      else if   (ul_quad == 37) {
// Ortho
    double r, a, ta;
    double xo;
    double ro;
    double c, s;
    double x, y, tc, ts;
    double theta;

    r = sqr(pAffineTP.x) + sqr(pAffineTP.y);

    if (r < 1.0) { // && FTx > 0.0 && FTy > 0.0) 
      if (pAffineTP.x >= 0.0) {
        xo = (r + 1.0) / (2.0 * pAffineTP.x);
        ro = sqrt(sqr(pAffineTP.x - xo) + sqr(pAffineTP.y));
        theta = atan2(1.0, ro);
        a = fmod(ul_1a * theta + atan2(pAffineTP.y, xo - pAffineTP.x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        qquadxx += pAmount * ul_amount * (xo - c * ro);
        qquadyy += pAmount * ul_amount * s * ro;
      } else {
        xo = -(r + 1.0) / (2.0 * pAffineTP.x);
        ro = sqrt(sqr(-pAffineTP.x - xo) + sqr(pAffineTP.y));
        theta = atan2(1.0, ro);
        a = fmod(ul_1a * theta + atan2(pAffineTP.y, xo + pAffineTP.x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        qquadxx -= pAmount * ul_amount * (xo - c * ro);
        qquadyy += pAmount * ul_amount * s * ro;
      }
    } else {
      r = 1.0 / sqrt(r);
      ta = atan2(pAffineTP.y, pAffineTP.x);
      ts = sin(ta);
      tc = cos(ta);

      x = r * tc;
      y = r * ts;

      if (x >= 0.0) {
        xo = (sqr(x) + sqr(y) + 1.0) / (2.0 * x);
        ro = sqrt(sqr(x - xo) + sqr(y));
        theta = atan2(1.0, ro);
        a = fmod(ul_1b * theta + atan2(y, xo - x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        x = (xo - c * ro);
        y = s * ro;
        ta = atan2(y, x);
        ts = sin(ta);
        tc = cos(ta);

        r = 1.0 / sqrt(sqr(x) + sqr(y));

        qquadxx += pAmount * ul_amount * r * tc;
        qquadyy += pAmount * ul_amount * r * ts;
      } else {
        xo = -(sqr(x) + sqr(y) + 1.0) / (2.0 * x);
        ro = sqrt(sqr(-x - xo) + sqr(y));
        theta = atan2(1.0, ro);
        a = fmod(ul_1b * theta + atan2(y, xo + x) + theta, 2.0 * theta) - theta;
        s = sin(a);
        c = cos(a);

        x = (xo - c * ro);
        y = s * ro;
        ta = atan2(y, x);
        ts = sin(ta);
        tc = cos(ta);
        r = 1.0 / sqrt(sqr(x) + sqr(y));

        qquadxx -= pAmount * ul_amount * r * tc;
        qquadyy += pAmount * ul_amount * r * ts;

      }
    }
    }

      else if   (ul_quad == 38) {
// Rectangles
    if (fabs(ul_1a) < EPSILON) {
      qquadxx += pAmount * ul_amount * pAffineTP.x;
    } else {
      qquadxx += pAmount * ul_amount * ((2 * floor(pAffineTP.x / ul_1a) + 1) * ul_1a - pAffineTP.x);
    }
    if (fabs(ul_1b) < EPSILON) {
      qquadyy += pAmount * ul_amount * pAffineTP.y;
    } else {
      qquadyy += pAmount * ul_amount * ((2 * floor(pAffineTP.y / ul_1b) + 1) * ul_1b - pAffineTP.y);
    }
    }
 

      else if   (ul_quad == 39) {
// Sinusoidal
    qquadxx += pAmount * ul_amount * sin(pAffineTP.x);
    qquadyy += pAmount * ul_amount * sin(pAffineTP.y);
    }


      else if   (ul_quad == 40) {
// SphericalN
    double R = pow(sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)), this.ul_1b);
    int N = (int) floor(this.ul_1a * pContext.random());
    double alpha = atan2(pAffineTP.y, pAffineTP.x) + N * M_2PI / floor(this.ul_1a);
    double sina = sin(alpha);
    double cosa = cos(alpha);

    if (R > SMALL_EPSILON) {
      qquadxx += pAmount * ul_amount * cosa / R;
      qquadyy += pAmount * ul_amount * sina / R;
    }
    }


      else if   (ul_quad == 41) {
// Waves2
    qquadxx += pAmount * ul_amount  * (pAffineTP.x + ul_1a * sin(pAffineTP.y * ul_1b));
    qquadyy += pAmount * ul_amount  * (pAffineTP.y + ul_1a * sin(pAffineTP.x * ul_1b));
    }

      else if   (ul_quad == 42) {
// Waves22
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double sinx;
	double siny;
	int px =  (int)2.0;
	int py =  (int)2.0;	
	int modex = 0;
    int modey = 0;

	if (modex < 0.5){
        sinx = sin(y0 * ul_1b);
    } else {
        sinx = 0.5 * (1.0 + sin(y0 * ul_1b));
    }
	double offsetx = pow(sinx, px) * ul_1a;
	if (modey < 0.5){
        siny = sin(x0 * ul_1b);
    } else {
        siny = 0.5 * (1.0 + sin(x0 * ul_1b));
    }
    double offsety = pow(siny, py) * ul_1a;
    
    qquadxx += pAmount * ul_amount * (x0 + offsetx);
    qquadyy += pAmount * ul_amount * (y0 + offsety);
    }

      else if   (ul_quad == 43) {
// Waves23
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double mx = y0 * ul_1b * M_1_2PI;
	double fx = mx - floor(mx);
	if (fx > 0.5) fx = 0.5 - fx;
	double my = x0 * ul_1b * M_1_2PI;
	double fy = my - floor(my);
	if (fy > 0.5) fy = 0.5 - fy;
    qquadxx += pAmount * ul_amount * (x0 + fx * ul_1a);
    qquadyy += pAmount * ul_amount * (y0 + fy * ul_1a);
    }
	
      else if   (ul_quad == 44) {
// Waves3
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double scalexx = 0.5 * ul_1a * (1.0 + sin(y0 * 0.0));
	double scaleyy = 0.5 * ul_1a * (1.0 + sin(x0 * 2.0));
    qquadxx += pAmount * ul_amount * (x0 + sin(y0 * ul_1b) * scalexx);
    qquadyy += pAmount * ul_amount * (y0 + sin(x0 * ul_1b) * scaleyy);
    }

      else if   (ul_quad == 45) {
// Waves4
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	
	double ax = floor(y0 * ul_1b / M_2PI);

    ax = sin(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * 0.1) * 43758.5453;
    ax = ax - (int) ax;
   // if (0.0 == 1) ax = (ax > 0.5) ? 1.0 : 0.0;

    
    qquadxx += pAmount * ul_amount * (x0 + sin(y0 * ul_1b) * ax * ax * ul_1a);
    qquadyy += pAmount * ul_amount * (y0 + sin(x0 * ul_1b) * ul_1a);
    }

      else if   (ul_quad == 46) {
// Waves42
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	
	double ax = floor(y0 * ul_1b);

    ax = sin(ax * 12.9898 + ax * 78.233 + 1.0 + y0 * 0.001 * 0.1) * 43758.5453;
    ax = ax - (int) ax;
    //if (0.0 == 1) ax = (ax > 0.5) ? 1.0 : 0.0;

    
    qquadxx += pAmount * ul_amount * (x0 + sin(y0 * ul_1b) * ax * ax * ul_1a);
    qquadyy += pAmount * ul_amount * (y0 + sin(x0 * ul_1b) * ul_1a);
    }

      else if   (ul_quad == 47) {
// Wdisc
    double a = M_PI / (sqrt(sqr(pAffineTP.x) + sqr(pAffineTP.y)) + ul_1a);
    double r = atan2(pAffineTP.y, pAffineTP.x) * M_1_PI;

    if (r > 0.0)
      a = M_PI - a;

    sinAndCos(a, sina, cosa);

    qquadxx += pAmount * ul_amount * r * cosa.value;
    qquadyy += pAmount * ul_amount * r * sina.value;
    }

      else if   (ul_quad == 48) {
// Whorl
    double r = pAffineTP.getPrecalcSqrt();
    double a;
    if (r < pAmount)
      a = pAffineTP.getPrecalcAtanYX() + ul_1a / (pAmount - r);
    else
      a = pAffineTP.getPrecalcAtanYX() + ul_1b / (pAmount - r);

    double sa = sin(a);
    double ca = cos(a);

    qquadxx += pAmount * ul_amount * r * ca;
    qquadyy += pAmount * ul_amount * r * sa;
    }

      count += 1;

    }
    if (mode != 0) {
      if (count == 0) { // point isn't in any quadrant
        pVarTP.x += pAmount / pAffineTP.y;
        pVarTP.y += pAmount / pAffineTP.x;
      } else {
        pVarTP.x += pAmount / qquadyy / count;
        pVarTP.y += pAmount / qquadxx / count;
      }
    } else {
      if (count == 0) { // point isn't in any quadrant
        pVarTP.x += pAmount * pAffineTP.x;
        pVarTP.y += pAmount * pAffineTP.y;
      } else {
        pVarTP.x += pAmount * qquadxx / count;
        pVarTP.y += pAmount * qquadyy / count;
      }
    }
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
    return new Object[] { mode, ul_quad, ul_amount, ul_shiftx, ul_shifty, ul_seed, ul_1a, ul_1b, ur_quad, ur_amount, ur_shiftx, ur_shifty, ur_seed, ur_1a, ur_1b, ll_quad, ll_amount, ll_shiftx, ll_shifty, ll_seed, ll_1a, ll_1b,  lr_quad, lr_amount, lr_shiftx, lr_shifty, lr_seed, lr_1a, lr_1b };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MODE.equalsIgnoreCase(pName))
      mode = (int) limitVal(pValue, 0, 1);
    else if (PARAM_UL_QUAD.equalsIgnoreCase(pName))
      ul_quad = (int) limitVal(pValue, 0, 50);
    else if (PARAM_UL_AMOUNT.equalsIgnoreCase(pName))
      ul_amount = pValue;
    else if (PARAM_UL_SHIFTX.equalsIgnoreCase(pName))
      ul_shiftx = pValue;
    else if (PARAM_UL_SHIFTY.equalsIgnoreCase(pName))
      ul_shifty = pValue;
    else if (PARAM_UL_SEED.equalsIgnoreCase(pName)) {
      ul_seed = Tools.FTOI(pValue);
      myRandGen.randomize(ul_seed);
      ul_1a = myRandGen.random()*2;
      ul_1b = myRandGen.random()*2;	}
    else if (PARAM_UL_1A.equalsIgnoreCase(pName))
      ul_1a = pValue;
    else if (PARAM_UL_1B.equalsIgnoreCase(pName))
      ul_1b = pValue;
    else if (PARAM_UR_QUAD.equalsIgnoreCase(pName))
      ur_quad = (int) limitVal(pValue, 0, 50);
    else if (PARAM_UR_AMOUNT.equalsIgnoreCase(pName))
      ur_amount = pValue;
    else if (PARAM_UR_SHIFTX.equalsIgnoreCase(pName))
      ur_shiftx = pValue;
    else if (PARAM_UR_SHIFTY.equalsIgnoreCase(pName))
      ur_shifty = pValue;
    else if (PARAM_UR_SEED.equalsIgnoreCase(pName)) {
      ur_seed = Tools.FTOI(pValue);
      myRandGen.randomize(ur_seed);
      ur_1a = myRandGen.random()*2;
      ur_1b = myRandGen.random()*2;	}
    else if (PARAM_UR_1A.equalsIgnoreCase(pName))
      ur_1a = pValue;
    else if (PARAM_UR_1B.equalsIgnoreCase(pName))
      ur_1b = pValue;
    else if (PARAM_LL_QUAD.equalsIgnoreCase(pName))
      ll_quad = (int) limitVal(pValue, 0, 50);
    else if (PARAM_LL_AMOUNT.equalsIgnoreCase(pName))
      ll_amount = pValue;
    else if (PARAM_LL_SHIFTX.equalsIgnoreCase(pName))
      ll_shiftx = pValue;
    else if (PARAM_LL_SHIFTY.equalsIgnoreCase(pName))
      ll_shifty = pValue;
    else if (PARAM_LL_SEED.equalsIgnoreCase(pName)) {
      ll_seed = Tools.FTOI(pValue);
      myRandGen.randomize(ll_seed);
      ll_1a = myRandGen.random()*2;
      ll_1b = myRandGen.random()*2;	}
    else if (PARAM_LL_1A.equalsIgnoreCase(pName))
      ll_1a = pValue;
    else if (PARAM_LL_1B.equalsIgnoreCase(pName))
      ll_1b = pValue;
    else if (PARAM_LR_QUAD.equalsIgnoreCase(pName))
      lr_quad = (int) limitVal(pValue, 0, 50);
    else if (PARAM_LR_AMOUNT.equalsIgnoreCase(pName))
      lr_amount = pValue;
    else if (PARAM_LR_SHIFTX.equalsIgnoreCase(pName))
      lr_shiftx = pValue;
    else if (PARAM_LR_SHIFTY.equalsIgnoreCase(pName))
      lr_shifty = pValue;
    else if (PARAM_LR_SEED.equalsIgnoreCase(pName)) {
      lr_seed = Tools.FTOI(pValue);
      myRandGen.randomize(lr_seed);
      lr_1a = myRandGen.random()*2;
      lr_1b = myRandGen.random()*2;		}
    else if (PARAM_LR_1A.equalsIgnoreCase(pName))
      lr_1a = pValue;
    else if (PARAM_LR_1B.equalsIgnoreCase(pName))
      lr_1b = pValue; 
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "quad";
  }

  private int lr_sign, ur_sign, ll_sign, ul_sign, lr_absPower_julian, ur_absPower_julian, ll_absPower_julian, ul_absPower_julian;
  private double lr_bCa, ur_bCa, ll_bCa, ul_bCa, lr_bCn_pi, ur_bCn_pi, ll_bCn_pi, ul_bCn_pi, lr_bCa_bCn, ur_bCa_bCn, ll_bCa_bCn, ul_bCa_bCn, lr_pi_bCn, ur_pi_bCn, ll_pi_bCn, ul_pi_bCn, lr_kn_pi, ur_kn_pi, ll_kn_pi, ul_kn_pi, lr_pi_kn, ur_pi_kn, ll_pi_kn, ul_pi_kn, lr_ka, ur_ka, ll_ka, ul_ka, lr_ka_kn, ur_ka_kn, ll_ka_kn, ul_ka_kn, lr_eCa, ur_eCa, ll_eCa, ul_eCa, lr_eCn_pi, ur_eCn_pi, ll_eCn_pi, ul_eCn_pi, lr_eCa_eCn, ur_eCa_eCn, ll_eCa_eCn, ul_eCa_eCn, lr_pi_eCn, ur_pi_eCn, ll_pi_eCn, ul_pi_eCn, lr_v, ur_v, ll_v, ul_v, lr_vvar2, ur_vvar2, ll_vvar2, ul_vvar2, lr_v_idisc, ur_v_idisc, ll_v_idisc, ul_v_idisc, lr_cPower_julian, ur_cPower_julian, ll_cPower_julian, ul_cPower_julian;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    lr_bCn_pi = (double) lr_1a * M_1_PI;
    ur_bCn_pi = (double) ur_1a * M_1_PI;
    ll_bCn_pi = (double) ll_1a * M_1_PI;
    ul_bCn_pi = (double) ul_1a * M_1_PI;
    lr_pi_bCn = M_PI / (double) lr_1a;
    ur_pi_bCn = M_PI / (double) ur_1a;
    ll_pi_bCn = M_PI / (double) ll_1a;
    ul_pi_bCn = M_PI / (double) ul_1a;
    lr_bCa = M_PI * lr_1b;
    ur_bCa = M_PI * ur_1b;
    ll_bCa = M_PI * ll_1b;
    ul_bCa = M_PI * ul_1b;
    lr_bCa_bCn = lr_bCa / (double) lr_1a;
    ur_bCa_bCn = ur_bCa / (double) ur_1a;
    ll_bCa_bCn = ll_bCa / (double) ll_1a;
    ul_bCa_bCn = ul_bCa / (double) ul_1a;
    lr_kn_pi = (double) lr_1b * M_1_PI;
    ur_kn_pi = (double) ur_1b * M_1_PI;
    ll_kn_pi = (double) ll_1b * M_1_PI;
    ul_kn_pi = (double) ul_1b * M_1_PI;
    lr_pi_kn = M_PI / (double) lr_1b;
    ur_pi_kn = M_PI / (double) ur_1b;
    ll_pi_kn = M_PI / (double) ll_1b;
    ul_pi_kn = M_PI / (double) ul_1b;
    lr_ka = M_PI * lr_1a;
    ur_ka = M_PI * ur_1a;
    ll_ka = M_PI *ll_1a;
    ul_ka = M_PI * ul_1a;
    lr_ka_kn = lr_ka / (double) lr_1b;
    ur_ka_kn = ur_ka / (double) ur_1b;
    ll_ka_kn = ll_ka / (double) ll_1b;
    ul_ka_kn = ul_ka / (double) ul_1b;
    lr_eCn_pi = (double) lr_1a * M_1_PI;
    ur_eCn_pi = (double) ur_1a * M_1_PI;
    ll_eCn_pi = (double) ll_1a * M_1_PI;
    ul_eCn_pi = (double) ul_1a * M_1_PI;
    lr_pi_eCn = M_PI / (double) lr_1a;
    ur_pi_eCn = M_PI / (double) ur_1a;
    ll_pi_eCn = M_PI / (double) ll_1a;
    ul_pi_eCn = M_PI / (double) ul_1a;
    lr_eCa = M_PI * lr_1b;
    ur_eCa = M_PI * ur_1b;
    ll_eCa = M_PI * ll_1b;
    ul_eCa = M_PI * ul_1b;
    lr_eCa_eCn = lr_eCa / (double) lr_1a;
    ur_eCa_eCn = ur_eCa / (double) ur_1a;
    ll_eCa_eCn = ll_eCa / (double) ll_1a;
    ul_eCa_eCn = ul_eCa / (double) ul_1a;
    lr_sign = 1;
    if (lr_1a < 0)
      lr_sign = -1;
    ur_sign = 1;
    if (ur_1a < 0)
      ur_sign = -1;
    ll_sign = 1;
    if (ll_1a < 0)
      ll_sign = -1;
    ul_sign = 1;
    if (ul_1a < 0)
      ul_sign = -1;
	//Elliptic
    lr_v = pAmount * lr_amount / (M_PI / 2.0);
	ur_v = pAmount * ur_amount / (M_PI / 2.0);
	ll_v = pAmount * ll_amount / (M_PI / 2.0);
	ul_v = pAmount * ul_amount / (M_PI / 2.0);
	//Glynnia
	lr_vvar2 = pAmount * lr_amount * sqrt(2.0) / 2.0;
	ur_vvar2 = pAmount * ur_amount * sqrt(2.0) / 2.0;
	ll_vvar2 = pAmount * ll_amount * sqrt(2.0) / 2.0;
	ul_vvar2 = pAmount * ul_amount * sqrt(2.0) / 2.0;
    lr_v_idisc = pAmount * lr_amount * M_1_PI;
	ur_v_idisc = pAmount * ur_amount * M_1_PI;
	ll_v_idisc = pAmount * ll_amount * M_1_PI;
	ul_v_idisc = pAmount * ul_amount * M_1_PI;
	lr_absPower_julian = iabs(Tools.FTOI(lr_1a));
    lr_cPower_julian = lr_1b / lr_1a * 0.5;
	ur_absPower_julian = iabs(Tools.FTOI(ur_1a));
    ur_cPower_julian = ur_1b / ur_1a * 0.5;
	ll_absPower_julian = iabs(Tools.FTOI(ll_1a));
    ll_cPower_julian = ll_1b / ll_1a * 0.5;	
	ul_absPower_julian = iabs(Tools.FTOI(ul_1a));
    ul_cPower_julian = ul_1b / ul_1a * 0.5;	
  }



  @Override
  public boolean dynamicParameterExpansion() {
    return true;
  }

  @Override
  public boolean dynamicParameterExpansion(String pName) {
    // param_seed doesn't really expand parameters, but it changes them; this will make them refresh

    return PARAM_LR_SEED.equalsIgnoreCase(pName) || PARAM_UR_SEED.equalsIgnoreCase(pName) || PARAM_LL_SEED.equalsIgnoreCase(pName) || PARAM_UL_SEED.equalsIgnoreCase(pName);
  }

  //  private double kn_pi, pi_kn, ka, ka_kn;

  // @Override
  // public void init(FlameTransformationContext pContext, Layer pLayer, //XForm pXForm, double pAmount) {
  //   kn_pi = (double) 2 * M_1_PI;
  //   pi_kn = M_PI / (double) 2;
//    ka = M_PI * 0.20;
//    ka_kn = ka / (double) 2;
  // }

  //private double lr_pz_sin, lr_pz_cos, ur_pz_sin, ur_pz_cos, ll_pz_sin, ll_pz_cos, ul_pz_sin, ul_pz_cos;

  //@Override
  //public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
//		lr_pz_sin = sin(lr_spin * M_PI_2);
//		lr_pz_cos = cos(lr_spin * M_PI_2);
//		ur_pz_sin = sin(ur_spin * M_PI_2);
//		ur_pz_cos = cos(ur_spin * M_PI_2);
//		ll_pz_sin = sin(ll_spin * M_PI_2);
//		ll_pz_cos = cos(ll_spin * M_PI_2);
//		ul_pz_sin = sin(ul_spin * M_PI_2);
//		ul_pz_cos = cos(ul_spin * M_PI_2);
//	}

//  @Override
//  public VariationFuncType[] getVariationTypes() {
//    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D};
//  }

}
