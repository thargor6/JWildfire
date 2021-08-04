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

import static org.jwildfire.base.Tools.FTOI;
import static org.jwildfire.base.mathlib.MathLib.*;

public class XTrbFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWER = "power";
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_DIST = "dist";
  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String[] paramNames = {PARAM_POWER, PARAM_RADIUS, PARAM_WIDTH, PARAM_DIST, PARAM_A, PARAM_B};

  private int power = 2;
  private double radius = 1.0;
  private double width = 0.5;
  private double dist = 1.0;
  private double a = 1.0;
  private double b = 1.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // xtrb by Xyrus02, http://xyrus02.deviantart.com/art/XTrb-Plugin-for-Apophysis-136800563
    double Alpha, Beta, OffsetAl, OffsetBe, OffsetGa, X, Y;
    int M, N;

    // transfer to trilinear coordinates, normalized to real distances from triangle sides
    {
      DirectTrilinearTO to = new DirectTrilinearTO();
      DirectTrilinear(pAffineTP.x, pAffineTP.y, to);
      Alpha = to.Al;
      Beta = to.Be;
    }

    M = (int) Math.floor(Alpha / S2a);
    OffsetAl = Alpha - M * S2a;
    N = (int) Math.floor(Beta / S2b);
    OffsetBe = Beta - N * S2b;
    OffsetGa = S2c - ac * OffsetAl - bc * OffsetBe;

    if (OffsetGa > 0) {
      HexTo to = new HexTo();
      Hex(pContext, OffsetAl, OffsetBe, OffsetGa, to);
      Alpha = to.Al1;
      Beta = to.Be1;
    } else {
      OffsetAl = S2a - OffsetAl;
      OffsetBe = S2b - OffsetBe;
      OffsetGa = -OffsetGa;
      {
        HexTo to = new HexTo();
        Hex(pContext, OffsetAl, OffsetBe, OffsetGa, to);
        Alpha = to.Al1;
        Beta = to.Be1;
      }
      Alpha = S2a - Alpha;
      Beta = S2b - Beta;

    }
    Alpha = Alpha + M * S2a;
    Beta = Beta + N * S2b;
    {
      InverseTrilinearTO to = new InverseTrilinearTO();
      InverseTrilinear(pContext, pAmount, Alpha, Beta, to);
      X = to.x;
      Y = to.y;
    }
    pVarTP.x += pAmount * X;
    pVarTP.y += pAmount * Y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }
/*
  public void transform2(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // xtrb by Xyrus02, http://xyrus02.deviantart.com/art/XTrb-Plugin-for-Apophysis-136800563
    double Alpha, Beta, OffsetAl, OffsetBe, OffsetGa, X, Y;
    int M, N;

    // transfer to trilinear coordinates, normalized to real distances from triangle sides
    {
      double U = pAffineTP.y + radius;
      double V = pAffineTP.x * sinC - pAffineTP.y * cosC + radius;
      Alpha = U;
      Beta = V;
    }

    M = (int) Math.floor(Alpha / S2a);
    OffsetAl = Alpha - M * S2a;
    N = (int) Math.floor(Beta / S2b);
    OffsetBe = Beta - N * S2b;
    OffsetGa = S2c - ac * OffsetAl - bc * OffsetBe;

    if (OffsetGa > 0) {
      double Al=OffsetAl;
      double Be=OffsetBe;
      double Ga=OffsetGa;
      double Ga1, De1, R;
      R = pContext.random();
      if (Be < Al) {
        if (Ga < Be) {
          if (R >= width3) {
            De1 = width * Be;
            Ga1 = width * Ga;
          } else {
            Ga1 = width1 * Ga + width2 * Hc * Ga / Be;
            De1 = width1 * Be + width2 * S2ab * (3 - Ga / Be);

          }
          Alpha = S2a - ba * De1 - ca * Ga1;
          Beta = De1;
        } else {
          if (Ga < Al) {
            if (R >= width3) {
              Ga1 = width * Ga;
              De1 = width * Be;
            } else {
              De1 = width1 * Be + width2 * Hb * Be / Ga;
              Ga1 = width1 * Ga + width2 * S2ac * (3 - Be / Ga);

            }
            Alpha = S2a - ba * De1 - ca * Ga1;
            Beta = De1;
          } else {
            if (R >= width3) {
              Alpha = width * Al;
              Beta = width * Be;
            } else {
              Beta = width1 * Be + width2 * Hb * Be / Al;
              Alpha = width1 * Al + width2 * S2ac * (3 - Be / Al);
            }
          }
        }
      } else {
        if (Ga < Al) {
          if (R >= width3) {
            De1 = width * Al;
            Ga1 = width * Ga;

          } else {
            Ga1 = width1 * Ga + width2 * Hc * Ga / Al;
            De1 = width1 * Al + width2 * S2ab * (3 - Ga / Al);

          }
          Beta = S2b - ab * De1 - cb * Ga1;
          Alpha = De1;
        } else {
          if (Ga < Be) {
            if (R >= width3) {
              Ga1 = width * Ga;
              De1 = width * Al;
            } else {
              De1 = width1 * Al + width2 * Ha * Al / Ga;
              Ga1 = width1 * Ga + width2 * S2bc * (3 - Al / Ga);

            }
            Beta = S2b - ab * De1 - cb * Ga1;
            Alpha = De1;
          } else {
            if (R >= width3) {
              Beta = width * Be;
              Alpha = width * Al;
            } else {
              Alpha = width1 * Al + width2 * Ha * Al / Be;
              Beta = width1 * Be + width2 * S2bc * (3 - Al / Be);
            }
          }
        }
      }
    } else {
      OffsetAl = S2a - OffsetAl;
      OffsetBe = S2b - OffsetBe;
      OffsetGa = -OffsetGa;
      {
        double Al=OffsetAl;
        double Be=OffsetBe;
        double Ga=OffsetGa;
        double Ga1, De1, R;
        R = pContext.random();
        if (Be < Al) {
          if (Ga < Be) {
            if (R >= width3) {
              De1 = width * Be;
              Ga1 = width * Ga;
            } else {
              Ga1 = width1 * Ga + width2 * Hc * Ga / Be;
              De1 = width1 * Be + width2 * S2ab * (3 - Ga / Be);

            }
            Alpha = S2a - ba * De1 - ca * Ga1;
            Beta = De1;
          } else {
            if (Ga < Al) {
              if (R >= width3) {
                Ga1 = width * Ga;
                De1 = width * Be;
              } else {
                De1 = width1 * Be + width2 * Hb * Be / Ga;
                Ga1 = width1 * Ga + width2 * S2ac * (3 - Be / Ga);

              }
              Alpha = S2a - ba * De1 - ca * Ga1;
              Beta = De1;
            } else {
              if (R >= width3) {
                Alpha = width * Al;
                Beta = width * Be;
              } else {
                Beta = width1 * Be + width2 * Hb * Be / Al;
                Alpha = width1 * Al + width2 * S2ac * (3 - Be / Al);
              }
            }
          }
        } else {
          if (Ga < Al) {
            if (R >= width3) {
              De1 = width * Al;
              Ga1 = width * Ga;

            } else {
              Ga1 = width1 * Ga + width2 * Hc * Ga / Al;
              De1 = width1 * Al + width2 * S2ab * (3 - Ga / Al);

            }
            Beta = S2b - ab * De1 - cb * Ga1;
            Alpha = De1;
          } else {
            if (Ga < Be) {
              if (R >= width3) {
                Ga1 = width * Ga;
                De1 = width * Al;
              } else {
                De1 = width1 * Al + width2 * Ha * Al / Ga;
                Ga1 = width1 * Ga + width2 * S2bc * (3 - Al / Ga);

              }
              Beta = S2b - ab * De1 - cb * Ga1;
              Alpha = De1;
            } else {
              if (R >= width3) {
                Beta = width * Be;
                Alpha = width * Al;
              } else {
                Alpha = width1 * Al + width2 * Ha * Al / Be;
                Beta = width1 * Be + width2 * S2bc * (3 - Al / Be);
              }
            }
          }
        }

      }
      Alpha = S2a - Alpha;
      Beta = S2b - Beta;

    }
    Alpha = Alpha + M * S2a;
    Beta = Beta + N * S2b;
    {
      double inx = (Beta - radius + (Alpha - radius) * cosC) / sinC;
      double iny = Alpha - radius;

      double angle = (atan2(iny, inx) + M_2PI * (pContext.random(Integer.MAX_VALUE) % (int) absN)) / power;
      double r = pAmount * pow(inx * inx + iny * iny, cN);

      double sina = sin(angle);
      double cosa = cos(angle);
      X = r * cosa;
      Y = r * sina;
    }
    pVarTP.x += pAmount * X;
    pVarTP.y += pAmount * Y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }
*/
  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{power, radius, width, dist, a, b};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = FTOI(pValue);
    else if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName))
      width = pValue;
    else if (PARAM_DIST.equalsIgnoreCase(pName))
      dist = pValue;
    else if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "xtrb";
  }

  private double width1, width2, width3;
  private double sinC, cosC;
  private double ab, ac, ba, bc, ca, cb;
  private double Ha, Hb, Hc;
  private double S2a, S2b, S2c, S2bc, S2ab, S2ac;
  private int absN;
  private double cN;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double angle_Br = 0.047 + a;///180.0*M_PI; // angeles in radians
    double angle_Cr = 0.047 + b;///180.0*M_PI;
    double angle_Ar = M_PI - angle_Br - angle_Cr;

    double sinA2, cosA2;
    sinA2 = sin(0.5 * angle_Ar);
    cosA2 = cos(0.5 * angle_Ar);
    double sinB2, cosB2;
    sinB2 = sin(0.5 * angle_Br);
    cosB2 = cos(0.5 * angle_Br);
    double sinC2, cosC2;
    sinC2 = sin(0.5 * angle_Cr);
    cosC2 = cos(0.5 * angle_Cr);
    sinC = sin(angle_Cr);
    cosC = cos(angle_Cr);

    double a = radius * (sinC2 / cosC2 + sinB2 / cosB2); //sides
    double b = radius * (sinC2 / cosC2 + sinA2 / cosA2);
    double c = radius * (sinB2 / cosB2 + sinA2 / cosA2);

    width1 = 1.0 - width;
    width2 = 2.0 * width;
    width3 = 1.0 - width * width;

    double S2 = radius * (a + b + c); //square

    Ha = S2 / a / 6.0; //Hight div on 6.0
    Hb = S2 / b / 6.0;
    Hc = S2 / c / 6.0;

    ab = a / b;// a div on b
    ac = a / c;
    ba = b / a;
    bc = b / c;
    ca = c / a;
    cb = c / b;
    S2a = 6.0 * Ha;
    S2b = 6.0 * Hb;
    S2c = 6.0 * Hc;
    S2bc = S2 / (b + c) / 6.0;
    S2ab = S2 / (a + b) / 6.0;
    S2ac = S2 / (a + c) / 6.0;

    absN = (int) fabs(power);
    cN = dist / (double) power / 2.0;
  }

  private static class DirectTrilinearTO {
    double Al, Be;
  }

  private void DirectTrilinear(double x, double y, DirectTrilinearTO res) {
    double U = y + radius;
    double V = x * sinC - y * cosC + radius;
    res.Al = U;
    res.Be = V;
  }

  private static class InverseTrilinearTO {
    double x, y;
  }

  private void InverseTrilinear(FlameTransformationContext pContext, double pAmount, double Al, double Be, InverseTrilinearTO res) {
    double inx = (Be - radius + (Al - radius) * cosC) / sinC;
    double iny = Al - radius;

    double angle = (atan2(iny, inx) + M_2PI * (pContext.random(Integer.MAX_VALUE) % (int) absN)) / power;
    double r = pAmount * pow(inx * inx + iny * iny, cN);

    double sina = sin(angle);
    double cosa = cos(angle);
    res.x = r * cosa;
    res.y = r * sina;
  }

  private static class HexTo {
    double Al1, Be1;
  }

  private void Hex(FlameTransformationContext pContext, double Al, double Be, double Ga, HexTo res) {
    double Ga1, De1, R;
    R = pContext.random();
    if (Be < Al) {
      if (Ga < Be) {
        if (R >= width3) {
          De1 = width * Be;
          Ga1 = width * Ga;
        } else {
          Ga1 = width1 * Ga + width2 * Hc * Ga / Be;
          De1 = width1 * Be + width2 * S2ab * (3 - Ga / Be);

        }
        res.Al1 = S2a - ba * De1 - ca * Ga1;
        res.Be1 = De1;

      } else {
        if (Ga < Al) {
          if (R >= width3) {
            Ga1 = width * Ga;
            De1 = width * Be;
          } else {
            De1 = width1 * Be + width2 * Hb * Be / Ga;
            Ga1 = width1 * Ga + width2 * S2ac * (3 - Be / Ga);

          }
          res.Al1 = S2a - ba * De1 - ca * Ga1;
          res.Be1 = De1;
        } else {
          if (R >= width3) {
            res.Al1 = width * Al;
            res.Be1 = width * Be;
          } else {
            res.Be1 = width1 * Be + width2 * Hb * Be / Al;
            res.Al1 = width1 * Al + width2 * S2ac * (3 - Be / Al);

          }

        }
      }
    } else {
      if (Ga < Al) {
        if (R >= width3) {
          De1 = width * Al;
          Ga1 = width * Ga;

        } else {
          Ga1 = width1 * Ga + width2 * Hc * Ga / Al;
          De1 = width1 * Al + width2 * S2ab * (3 - Ga / Al);

        }
        res.Be1 = S2b - ab * De1 - cb * Ga1;
        res.Al1 = De1;

      } else {
        if (Ga < Be) {
          if (R >= width3) {
            Ga1 = width * Ga;
            De1 = width * Al;
          } else {
            De1 = width1 * Al + width2 * Ha * Al / Ga;
            Ga1 = width1 * Ga + width2 * S2bc * (3 - Al / Ga);

          }
          res.Be1 = S2b - ab * De1 - cb * Ga1;
          res.Al1 = De1;

        } else {
          if (R >= width3) {
            res.Be1 = width * Be;
            res.Al1 = width * Al;
          } else {
            res.Al1 = width1 * Al + width2 * Ha * Al / Be;
            res.Be1 = width1 * Be + width2 * S2bc * (3 - Al / Be);
          }
        }
      }
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return " float width1, width2, width3;\n"
        + "   float sinC, cosC;\n"
        + "   float ab, ac, ba, bc, ca, cb;\n"
        + "   float Ha, Hb, Hc;\n"
        + "   float S2a, S2b, S2c, S2bc, S2ab, S2ac;\n"
        + "   int absN;\n"
        + "   float cN;\n"
        + "    int power = lroundf(__xtrb_power);\n"
        + "  {\n"
        + "    float angle_Br = 0.047 + __xtrb_a;\n"
        + "    float angle_Cr = 0.047 + __xtrb_b;\n"
        + "    float angle_Ar = PI - angle_Br - angle_Cr;\n"
        + "\n"
        + "    float sinA2, cosA2;\n"
        + "    sinA2 = sinf(0.5 * angle_Ar);\n"
        + "    cosA2 = cosf(0.5 * angle_Ar);\n"
        + "    float sinB2, cosB2;\n"
        + "    sinB2 = sinf(0.5 * angle_Br);\n"
        + "    cosB2 = cosf(0.5 * angle_Br);\n"
        + "    float sinC2, cosC2;\n"
        + "    sinC2 = sinf(0.5 * angle_Cr);\n"
        + "    cosC2 = cosf(0.5 * angle_Cr);\n"
        + "    sinC = sinf(angle_Cr);\n"
        + "    cosC = cosf(angle_Cr);\n"
        + "\n"
        + "    float a = __xtrb_radius * (sinC2 / cosC2 + sinB2 / cosB2);\n"
        + "    float b = __xtrb_radius * (sinC2 / cosC2 + sinA2 / cosA2);\n"
        + "    float c = __xtrb_radius * (sinB2 / cosB2 + sinA2 / cosA2);\n"

        + "\n"
        + "    width1 = 1.0 - __xtrb_width;\n"
        + "    width2 = 2.0 * __xtrb_width;\n"
        + "    width3 = 1.0 - __xtrb_width * __xtrb_width;\n"


        + "\n"
        + "    float S2 = __xtrb_radius * (a + b + c); \n"
        + "\n"
        + "    Ha = S2 / a / 6.0;\n"
        + "    Hb = S2 / b / 6.0;\n"
        + "    Hc = S2 / c / 6.0;\n"

        + "\n"
        + "    ab = a / b;\n"
        + "    ac = a / c;\n"
        + "    ba = b / a;\n"
        + "    bc = b / c;\n"
        + "    ca = c / a;\n"
        + "    cb = c / b;\n"
        + "    S2a = 6.0 * Ha;\n"
        + "    S2b = 6.0 * Hb;\n"
        + "    S2c = 6.0 * Hc;\n"
        + "    S2bc = S2 / (b + c) / 6.0;\n"
        + "    S2ab = S2 / (a + b) / 6.0;\n"
        + "    S2ac = S2 / (c) / 6.0;\n"
        + "\n"
        + "    absN = power < 0 ? -power : power;\n"
        + "    cN = __xtrb_dist / (float) power / 2.0;\n"
        + "}\n"
        + " float Alpha, Beta, OffsetAl, OffsetBe, OffsetGa, X, Y;\n"
        + "    int M, N;\n"
        + "\n"
        + "    \n"
        + "    {\n"
        + "      float U = __y + __xtrb_radius;\n"
        + "      float V = __x * sinC - __y * cosC + __xtrb_radius;\n"
        + "      Alpha = U;\n"
        + "      Beta = V;\n"
        + "    }\n"
        + "\n"
        + "    M = (int) floorf(Alpha / S2a);\n"
        + "    OffsetAl = Alpha - M * S2a;\n"
        + "    N = (int) floorf(Beta / S2b);\n"
        + "    OffsetBe = Beta - N * S2b;\n"
        + "    OffsetGa = S2c - ac * OffsetAl - bc * OffsetBe;\n"

        + "\n"
        + "    if (OffsetGa > 0) {\n"
        + "      float Al=OffsetAl;\n"
        + "      float Be=OffsetBe;\n"
        + "      float Ga=OffsetGa;\n"
        + "      float Ga1, De1, R;\n"
        + "      R = RANDFLOAT();\n"
        + "      if (Be < Al) {\n"
        + "        if (Ga < Be) {\n"
        + "          if (R >= width3) {\n"
        + "            De1 = __xtrb_width * Be;\n"
        + "            Ga1 = __xtrb_width * Ga;\n"
        + "          } else {\n"
        + "            Ga1 = width1 * Ga + width2 * Hc * Ga / Be;\n"
        + "            De1 = width1 * Be + width2 * S2ab * (3 - Ga / Be);\n"
        + "\n"
        + "          }\n"
        + "          Alpha = S2a - ba * De1 - ca * Ga1;\n"
        + "          Beta = De1;\n"
        + "        } else {\n"
        + "          if (Ga < Al) {\n"
        + "            if (R >= width3) {\n"
        + "              Ga1 = __xtrb_width * Ga;\n"
        + "              De1 = __xtrb_width * Be;\n"
        + "            } else {\n"
        + "              De1 = width1 * Be + width2 * Hb * Be / Ga;\n"
        + "              Ga1 = width1 * Ga + width2 * S2ac * (3 - Be / Ga);\n"
        + "\n"
        + "            }\n"
        + "            Alpha = S2a - ba * De1 - ca * Ga1;\n"
        + "            Beta = De1;\n"
        + "          } else {\n"
        + "            if (R >= width3) {\n"
        + "              Alpha = __xtrb_width * Al;\n"
        + "              Beta = __xtrb_width * Be;\n"
        + "            } else {\n"
        + "              Beta = width1 * Be + width2 * Hb * Be / Al;\n"
        + "              Alpha = width1 * Al + width2 * S2ac * (3 - Be / Al);\n"
        + "            }\n"
        + "          }\n"
        + "        }\n"
        + "      } else {\n"
        + "        if (Ga < Al) {\n"
        + "          if (R >= width3) {\n"
        + "            De1 = __xtrb_width * Al;\n"
        + "            Ga1 = __xtrb_width * Ga;\n"
        + "\n"
        + "          } else {\n"
        + "            Ga1 = width1 * Ga + width2 * Hc * Ga / Al;\n"
        + "            De1 = width1 * Al + width2 * S2ab * (3 - Ga / Al);\n"
        + "\n"
        + "          }\n"
        + "          Beta = S2b - ab * De1 - cb * Ga1;\n"
        + "          Alpha = De1;\n"
        + "        } else {\n"
        + "          if (Ga < Be) {\n"
        + "            if (R >= width3) {\n"
        + "              Ga1 = __xtrb_width * Ga;\n"
        + "              De1 = __xtrb_width * Al;\n"
        + "            } else {\n"
        + "              De1 = width1 * Al + width2 * Ha * Al / Ga;\n"
        + "              Ga1 = width1 * Ga + width2 * S2bc * (3 - Al / Ga);\n"
        + "\n"
        + "            }\n"
        + "            Beta = S2b - ab * De1 - cb * Ga1;\n"
        + "            Alpha = De1;\n"
        + "          } else {\n"
        + "            if (R >= width3) {\n"
        + "              Beta = __xtrb_width * Be;\n"
        + "              Alpha = __xtrb_width * Al;\n"
        + "            } else {\n"
        + "              Alpha = width1 * Al + width2 * Ha * Al / Be;\n"
        + "              Beta = width1 * Be + width2 * S2bc * (3 - Al / Be);\n"
        + "            }\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "    } else {\n"
        + "      OffsetAl = S2a - OffsetAl;\n"
        + "      OffsetBe = S2b - OffsetBe;\n"
        + "      OffsetGa = -OffsetGa;\n"
        + "      {\n"
        + "        float Al=OffsetAl;\n"
        + "        float Be=OffsetBe;\n"
        + "        float Ga=OffsetGa;\n"
        + "        float Ga1, De1, R;\n"
        + "        R = RANDFLOAT();\n"
        + "        if (Be < Al) {\n"
        + "          if (Ga < Be) {\n"
        + "            if (R >= width3) {\n"
        + "              De1 = __xtrb_width * Be;\n"
        + "              Ga1 = __xtrb_width * Ga;\n"
        + "            } else {\n"
        + "              Ga1 = width1 * Ga + width2 * Hc * Ga / Be;\n"
        + "              De1 = width1 * Be + width2 * S2ab * (3 - Ga / Be);\n"
        + "\n"
        + "            }\n"
        + "            Alpha = S2a - ba * De1 - ca * Ga1;\n"
        + "            Beta = De1;\n"
        + "          } else {\n"
        + "            if (Ga < Al) {\n"
        + "              if (R >= width3) {\n"
        + "                Ga1 = __xtrb_width * Ga;\n"
        + "                De1 = __xtrb_width * Be;\n"
        + "              } else {\n"
        + "                De1 = width1 * Be + width2 * Hb * Be / Ga;\n"
        + "                Ga1 = width1 * Ga + width2 * S2ac * (3 - Be / Ga);\n"
        + "\n"
        + "              }\n"
        + "              Alpha = S2a - ba * De1 - ca * Ga1;\n"
        + "              Beta = De1;\n"
        + "            } else {\n"
        + "              if (R >= width3) {\n"
        + "                Alpha = __xtrb_width * Al;\n"
        + "                Beta = __xtrb_width * Be;\n"
        + "              } else {\n"
        + "                Beta = width1 * Be + width2 * Hb * Be / Al;\n"
        + "                Alpha = width1 * Al + width2 * S2ac * (3 - Be / Al);\n"
        + "              }\n"
        + "            }\n"
        + "          }\n"
        + "        } else {\n"
        + "          if (Ga < Al) {\n"
        + "            if (R >= width3) {\n"
        + "              De1 = __xtrb_width * Al;\n"
        + "              Ga1 = __xtrb_width * Ga;\n"
        + "\n"
        + "            } else {\n"
        + "              Ga1 = width1 * Ga + width2 * Hc * Ga / Al;\n"
        + "              De1 = width1 * Al + width2 * S2ab * (3 - Ga / Al);\n"
        + "\n"
        + "            }\n"
        + "            Beta = S2b - ab * De1 - cb * Ga1;\n"
        + "            Alpha = De1;\n"
        + "          } else {\n"
        + "            if (Ga < Be) {\n"
        + "              if (R >= width3) {\n"
        + "                Ga1 = __xtrb_width * Ga;\n"
        + "                De1 = __xtrb_width * Al;\n"
        + "              } else {\n"
        + "                De1 = width1 * Al + width2 * Ha * Al / Ga;\n"
        + "                Ga1 = width1 * Ga + width2 * S2bc * (3 - Al / Ga);\n"
        + "\n"
        + "              }\n"
        + "              Beta = S2b - ab * De1 - cb * Ga1;\n"
        + "              Alpha = De1;\n"
        + "            } else {\n"
        + "              if (R >= width3) {\n"
        + "                Beta = __xtrb_width * Be;\n"
        + "                Alpha = __xtrb_width * Al;\n"
        + "              } else {\n"
        + "                Alpha = width1 * Al + width2 * Ha * Al / Be;\n"
        + "                Beta = width1 * Be + width2 * S2bc * (3 - Al / Be);\n"
        + "              }\n"
        + "            }\n"
        + "          }\n"
        + "        }\n"
        + "\n"
        + "      }\n"
        + "      Alpha = S2a - Alpha;\n"
        + "      Beta = S2b - Beta;\n"
        + "\n"
        + "    }\n"
        + "    Alpha = Alpha + M * S2a;\n"
        + "    Beta = Beta + N * S2b;\n"
        + "    {\n"
        + "      float inx = (Beta - __xtrb_radius + (Alpha - __xtrb_radius) * cosC) / sinC;\n"
        + "      float iny = Alpha - __xtrb_radius;\n"
        + "\n"
        + "      float angle = (atan2f(iny, inx) + (2.0f*PI) * (lroundf(RANDFLOAT()*0x00001fff) % (int) absN)) / power;\n"
        + "      float r = __xtrb * powf(inx * inx + iny * iny, cN);\n"
        + "\n"
        + "      float sina = sinf(angle);\n"
        + "      float cosa = cosf(angle);\n"
        + "      X = r * cosa;\n"
        + "      Y = r * sina;\n"
        + "    }\n"
        + "    __px += __xtrb * X;\n"
        + "    __py += __xtrb * Y;\n"
        + (context.isPreserveZCoordinate() ? "      __pz += __xtrb * __z;\n" : "");
  }

}
