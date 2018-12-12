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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class TruchetAEFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SEED = "seed";
  private static final String PARAM_STEP = "step";
  private static final String PARAM_WIDTH = "width";
  private static final String PARAM_ANGLE = "angle"; // added Dark
  private static final String PARAM_LAYOUT = "layout"; // added Dark
  private static final String PARAM_STYLE = "style"; // added Dark
  private static final String PARAM_K01A = "K01A";
  private static final String PARAM_K02A = "K02A";
  private static final String PARAM_K01B = "K01B";
  private static final String PARAM_K02B = "K02B";
  private static final String PARAM_K11A = "K11A";
  private static final String PARAM_K12A = "K12A";
  private static final String PARAM_K11B = "K11B";
  private static final String PARAM_K12B = "K12B";
  private static final String PARAM_K21A = "K21A";
  private static final String PARAM_K22A = "K22A";
  private static final String PARAM_K21B = "K21B";
  private static final String PARAM_K22B = "K22B";
  private static final String PARAM_K31A = "K31A";
  private static final String PARAM_K32A = "K32A";
  private static final String PARAM_K31B = "K31B";
  private static final String PARAM_K32B = "K32B";
  private static final String[] paramNames = {PARAM_SEED, PARAM_STEP, PARAM_WIDTH, PARAM_ANGLE, PARAM_LAYOUT, PARAM_STYLE,
          PARAM_K01A, PARAM_K02A, PARAM_K01B, PARAM_K02B, PARAM_K11A, PARAM_K12A, PARAM_K11B, PARAM_K12B,
          PARAM_K21A, PARAM_K22A, PARAM_K21B, PARAM_K22B, PARAM_K31A, PARAM_K32A, PARAM_K31B, PARAM_K32B};

  private int seed = 0;
  private double step = 0.5;
  private double width = 0.25;
  private double angle = 0.0; // added Dark
  private int layout = 0; // added Dark
  private int style = 0; // added Dark
  private double k01a = 0.5;
  private double k02a = 0.5;
  private double k01b = 0.5;
  private double k02b = 0.5;
  private double k11a = 0.5;
  private double k12a = 0.5;
  private double k11b = 0.5;
  private double k12b = 0.5;
  private double k21a = 0.5;
  private double k22a = 0.5;
  private double k21b = 0.5;
  private double k22b = 0.5;
  private double k31a = 0.5;
  private double k32a = 0.5;
  private double k31b = 0.5;
  private double k32b = 0.5;

  private final static double AM = (1.0 / 2147483647);
  private static final double M_SQ3_2 = 0.86602540378443864676372317075294;

  private double x = 0.0, y = 0.0, z1 = 0.0, z2 = 0.0;

  private double dn1(int x) {
    int n = x;
    n = (n << 13) ^ n;
    return ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) * AM;
  }

  private double fmod2(double num, double den) {
    double tmp = fmod(num, den);
    if ((tmp < 0) && (den > 0)) tmp += den;
    return tmp;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // Truchet by Alexey Ermushev, http://eralex61.deviantart.com/art/Controlled-IFS-beyond-chaos-257891160
    // Implemented bu rsidwell. Tweaks made by DarkBeam 2017
    double u1 = 0, v1 = 0, z = 0.;
    int i = (int) floor(4 * pContext.random());

    // infinite layouts are possible, here I listed the most famous / interesting / useful
    if (layout == 1) { // symmetrized square
      switch (i) {
        case 0:
          u1 = -x * 0.5 + 1.0;
          v1 = y * 0.5 + 1.0;
          z1 = fmod2(k01a * z1 + k02a, 1.0);
          z2 = fmod2(k01b * z2 + k02b, 1.0);
          break;
        case 1:
          u1 = x * 0.5 - 1.0;
          v1 = y * 0.5 + 1.0;
          z1 = fmod2(k11a * z1 + k12a, 1.0);
          z2 = fmod2(k11b * z2 + k12b, 1.0);
          break;
        case 2:
          u1 = -x * 0.5 + 1.0;
          v1 = -y * 0.5 - 1.0;
          z1 = fmod2(k21a * z1 + k22a, 1.0);
          z2 = fmod2(k21b * z2 + k22b, 0.5);
          break;
        case 3:
          u1 = x * 0.5 - 1.0;
          v1 = -y * 0.5 - 1.0;
          z1 = fmod2(k31a * z1 + k32a, 1.0);
          z2 = fmod2(k31b * z2 + k32b, 1.0);
          break;
      }
    } else if (layout == 2) { // Sierpinski 45°
      switch (i) {
        case 0:
          u1 = x * 0.5 + 1.0;
          v1 = y * 0.5 + 1.0;
          z1 = fmod2(k01a * z1 + k02a, 1.0);
          z2 = fmod2(k01b * z2 + k02b, 1.0);
          break;
        case 1:
          u1 = x * 0.5 - 1.0;
          v1 = y * 0.5 - 1.0;
          z1 = fmod2(k11a * z1 + k12a, 1.0);
          z2 = fmod2(k11b * z2 + k12b, 1.0);
          break;
        case 2:
          u1 = x * 0.5 - 1.0;
          v1 = y * 0.5 + 1.0;
          z1 = fmod2(k21a * z1 + k22a, 1.0);
          z2 = fmod2(k21b * z2 + k22b, 0.5);
          break;
        case 3:
          u1 = -x * 0.5 - 1.0;
          v1 = -y * 0.5 + 1.0;
          z1 = fmod2(k31a * z1 + k32a, 1.0);
          z2 = fmod2(k31b * z2 + k32b, 1.0);
          break;
      }
    } else if (layout == 3) { // Chair
      switch (i) {
        case 0:
          u1 = x * 0.5;
          v1 = y * 0.5;
          z1 = fmod2(k01a * z1 + k02a, 1.0);
          z2 = fmod2(k01b * z2 + k02b, 1.0);
          break;
        case 1:
          u1 = x * 0.5 - 1.0;
          v1 = y * 0.5 + 1.0;
          z1 = fmod2(k11a * z1 + k12a, 1.0);
          z2 = fmod2(k11b * z2 + k12b, 1.0);
          break;
        case 2:
          u1 = -y * 0.5 - 1.0;
          v1 = x * 0.5 - 1.0;
          z1 = fmod2(k21a * z1 + k22a, 1.0);
          z2 = fmod2(k21b * z2 + k22b, 0.5);
          break;
        case 3:
          u1 = y * 0.5 + 1.0;
          v1 = -x * 0.5 + 1.0;
          z1 = fmod2(k31a * z1 + k32a, 1.0);
          z2 = fmod2(k31b * z2 + k32b, 1.0);
          break;
      }
    } else if (layout == 4) { // EquilaterTri
      switch (i) {
        case 0:
          u1 = x * 0.5 + 1;
          v1 = y * 0.5 + M_SQ3_2;
          z1 = fmod2(k01a * z1 + k02a, 1.0);
          z2 = fmod2(k01b * z2 + k02b, 1.0);
          break;
        case 1:
          u1 = x * 0.5 - 1;
          v1 = y * 0.5 + M_SQ3_2;
          z1 = fmod2(k11a * z1 + k12a, 1.0);
          z2 = fmod2(k11b * z2 + k12b, 1.0);
          break;
        case 2:
          u1 = x * 0.5;
          v1 = y * 0.5 - M_SQ3_2;
          z1 = fmod2(k21a * z1 + k22a, 1.0);
          z2 = fmod2(k21b * z2 + k22b, 0.5);
          break;
        case 3:
          u1 = x * 0.5;
          v1 = -y * 0.5 + M_SQ3_2;
          z1 = fmod2(k31a * z1 + k32a, 1.0);
          z2 = fmod2(k31b * z2 + k32b, 1.0);
          break;
      }
    } else { // default = asymm. square
      double j;
      switch (i) {
        case 0:
          u1 = x * 0.5 + 1.0;
          v1 = y * 0.5 + 1.0;
          j = v1 + 0.7;
          if (j > 2.) j = j - 2.;
          v1 = j;
          z1 = fmod2(k01a * z1 + k02a, 1.0);
          z2 = fmod2(k01b * z2 + k02b, 1.0);
          break;
        case 1:
          u1 = x * 0.5 - 1.0;
          v1 = y * 0.5 + 1.0;
          z1 = fmod2(k11a * z1 + k12a, 1.0);
          z2 = fmod2(k11b * z2 + k12b, 1.0);
          break;
        case 2:
          u1 = x * 0.5 + 1.0;
          v1 = y * 0.5 - 1.0;
          z1 = fmod2(k21a * z1 + k22a, 1.0);
          z2 = fmod2(k21b * z2 + k22b, 0.5);
          break;
        case 3:
          u1 = x * 0.5 - 1.0;
          v1 = y * 0.5 - 1.0;
          z1 = fmod2(k31a * z1 + k32a, 1.0);
          z2 = fmod2(k31b * z2 + k32b, 1.0);
          break;
      }
    }
    double uu1 = cosang * u1 - sinang * v1;
    double vv1 = sinang * u1 + cosang * v1;
    int m = (int) floor(uu1 / step);
    int n = (int) floor(vv1 / step);
    double u = uu1 / step - m;
    double v = vv1 / step - n;
    double chance = dn1((m + 34 * n + seed));
    double cir = 0.0;
    double square = 0.;
    boolean inv = false;

    // following lines define the truchet shapes
    // tweaking them generates different styles
    switch (style) {
      case 0: // Apo (default) by Eralex
        if (chance > 0.5) {
          if (((v < u + 1 - width) && (v > u + width)) || ((v < u - width) && (v > u - 1 + width))) z = z1;
          else z = z2;
        } else {
          if (((v < -u + 1 - width) && (v > -u + width)) || ((v > -u + 1 + width) && (v < -u + 2 - width))) z = z1;
          else z = z2;
        }
        break;
      case 1: // Original by Truchet (added width for effect) was with four tiles
        square = max(fabs(u - .5), fabs(v - .5)) - 0.5 * (1. - width);
        if (chance < 0.25) {
          if (((square < 0) ^ (u > v))) z = z1;
          else z = z2;
        } else if (chance < 0.5) {
          if (((square < 0) ^ (u > 1. - v))) z = z1;
          else z = z2;
        } else if (chance < 0.75) {
          if (((square < 0) ^ (u < v))) z = z1;
          else z = z2;
        } else {
          if (((square < 0) ^ (u < 1. - v))) z = z1;
          else z = z2;
        }
        break;
      case 2: // circle Apo truchet (simulated) :P
        if (chance < 0.5) {
          cir = sqrt((u - 1.) * (u - 1.) + (v - 1.) * (v - 1.));
          cir = min(cir, sqrt(u * u + v * v));
          cir = fabs(fabs(cir - 0.5) - 0.5) - 0.5 * (1. - width);
          if (cir < 0) z = z1;
          else z = z2;
        } else {
          cir = sqrt((u - 1.) * (u - 1.) + v * v);
          cir = min(cir, sqrt(u * u + (v - 1.) * (v - 1.)));
          cir = fabs(fabs(cir - 0.5) - 0.5) - 0.5 * (1. - width);
          if (cir < 0) z = z1;
          else z = z2;
        }
        break;
      case 3: // circle Apo truchet plus + tile - looks cool 8)
        if (chance < 0.333333) {
          cir = sqrt((u - 1.) * (u - 1.) + (v - 1.) * (v - 1.));
          cir = min(cir, sqrt(u * u + v * v));
          cir = fabs(fabs(cir - 0.5) - 0.5) - 0.5 * (1. - width);
          if (cir < 0) z = z1;
          else z = z2;
        } else if (chance < 0.666666) {
          cir = sqrt((u - 1.) * (u - 1.) + v * v);
          cir = min(cir, sqrt(u * u + (v - 1.) * (v - 1.)));
          cir = fabs(fabs(cir - 0.5) - 0.5) - 0.5 * (1. - width);
          if (cir < 0) z = z1;
          else z = z2;
        } else {
          cir = 0.5 * (width) - min(fabs(u - .5), fabs(v - .5)); // PLUS!
          if (cir < 0) z = z1;
          else z = z2;
        }
        break;
      case 4: // Original Modded (added width for effect) 8 tiles
        square = max(fabs(u - .5), fabs(v - .5)) - 0.5 * (1. - width);
        if (chance < 0.125) {
          if (((square < 0) ^ (u > v))) z = z1;
          else z = z2;
        } else if (chance < 0.25) {
          if (((square < 0) ^ (u > 1. - v))) z = z1;
          else z = z2;
        } else if (chance < 0.375) {
          if (((square < 0) ^ (u < v))) z = z1;
          else z = z2;
        } else if (chance < 0.5) {
          if (((square < 0) ^ (u < 1. - v))) z = z1;
          else z = z2;
          // ---------------------
        } else if (chance < 0.625) {
          if (((square < 0) ^ (u < 0.5))) z = z1;
          else z = z2;
        } else if (chance < 0.75) {
          if (((square < 0) ^ (v < 0.5))) z = z1;
          else z = z2;
        } else if (chance < 0.875) {
          if (((square < 0) ^ (u > 0.5))) z = z1;
          else z = z2;
        } else {
          if (((square < 0) ^ (v > 0.5))) z = z1;
          else z = z2;
        }
        break;
      case 5: // circle filled truchet + width for effect
        inv = (m % 2 == 0) ^ (n % 2 == 0);
        if (chance < 0.5) {
          cir = sqrt((u - 1.) * (u - 1.) + (v - 1.) * (v - 1.));
          cir = min(cir, sqrt(u * u + v * v));
          square = cir;
          cir = fabs(cir) - 0.5;
          // dots effect (suggested a width = 0.25)
          square = min(square, sqrt((u - 1.) * (u - 1.) + v * v));
          square = min(square, sqrt(u * u + (v - 1.) * (v - 1.)));
          square = fabs(square) - width;
          if ((cir < 0) ^ (square < 0) ^ inv) z = z1;
          else z = z2;
        } else {
          cir = sqrt((u - 1.) * (u - 1.) + v * v);
          cir = min(cir, sqrt(u * u + (v - 1.) * (v - 1.)));
          square = cir;
          cir = fabs(cir) - 0.5;
          // dots effect (suggested a width = 0.25)
          square = min(square, sqrt((u - 1.) * (u - 1.) + (v - 1.) * (v - 1.)));
          square = min(square, sqrt(u * u + v * v));
          square = fabs(square) - width;
          if ((cir < 0) ^ (square < 0) ^ inv) z = z2;
          else z = z1;
        }
        break;
    }
    x = u1;
    y = v1;

    pVarTP.x += u1;
    pVarTP.y += v1;
    if (pContext.isPreserveZCoordinate()) pVarTP.z += pAmount * pAffineTP.z;

    pVarTP.color = z;
    z1 = z;
    z2 = z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{seed, step, width, angle, layout, style, k01a, k02a, k01b, k02b, k11a, k12a, k11b, k12b, k21a, k22a, k21b, k22b, k31a, k32a, k31b, k32b};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = Tools.FTOI(pValue);
    else if (PARAM_STEP.equalsIgnoreCase(pName))
      step = pValue;
    else if (PARAM_WIDTH.equalsIgnoreCase(pName))
      width = pValue;
    else if (PARAM_ANGLE.equalsIgnoreCase(pName)) {
      angle = pValue;
      sinang = sin(angle * M_PI); // plz move this to init
      cosang = cos(angle * M_PI);  // plz move this to init
    } else if (PARAM_LAYOUT.equalsIgnoreCase(pName))
      layout = limitIntVal(Tools.FTOI(pValue), 0, 4);
    else if (PARAM_STYLE.equalsIgnoreCase(pName))
      style = limitIntVal(Tools.FTOI(pValue), 0, 5);
    else if (PARAM_K01A.equalsIgnoreCase(pName))
      k01a = pValue;
    else if (PARAM_K02A.equalsIgnoreCase(pName))
      k02a = pValue;
    else if (PARAM_K01B.equalsIgnoreCase(pName))
      k01b = pValue;
    else if (PARAM_K02B.equalsIgnoreCase(pName))
      k02b = pValue;
    else if (PARAM_K11A.equalsIgnoreCase(pName))
      k11a = pValue;
    else if (PARAM_K12A.equalsIgnoreCase(pName))
      k12a = pValue;
    else if (PARAM_K11B.equalsIgnoreCase(pName))
      k11b = pValue;
    else if (PARAM_K12B.equalsIgnoreCase(pName))
      k12b = pValue;
    else if (PARAM_K21A.equalsIgnoreCase(pName))
      k21a = pValue;
    else if (PARAM_K22A.equalsIgnoreCase(pName))
      k22a = pValue;
    else if (PARAM_K21B.equalsIgnoreCase(pName))
      k21b = pValue;
    else if (PARAM_K22B.equalsIgnoreCase(pName))
      k22b = pValue;
    else if (PARAM_K31A.equalsIgnoreCase(pName))
      k31a = pValue;
    else if (PARAM_K32A.equalsIgnoreCase(pName))
      k32a = pValue;
    else if (PARAM_K31B.equalsIgnoreCase(pName))
      k31b = pValue;
    else if (PARAM_K32B.equalsIgnoreCase(pName))
      k32b = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  // -------------------

  private double sinang, cosang;
  // init does not work, why ;_;
  /*
  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    sinang = sin(angle*M_PI);
    cosang = cos(angle*M_PI); 
  }
*/
  // -------------------

  @Override
  public String getName() {
    return "truchet_ae";
  }


}
