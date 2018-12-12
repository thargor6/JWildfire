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

public class SynthFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_MODE = "mode";
  private static final String PARAM_POWER = "power";
  private static final String PARAM_MIX = "mix";
  private static final String PARAM_SMOOTH = "smooth";

  private static final String PARAM_B = "b";
  private static final String PARAM_B_TYPE = "b_type";
  private static final String PARAM_B_SKEW = "b_skew";
  private static final String PARAM_B_FRQ = "b_frq";
  private static final String PARAM_B_PHS = "b_phs";
  private static final String PARAM_B_LAYER = "b_layer";

  private static final String PARAM_C = "c";
  private static final String PARAM_C_TYPE = "c_type";
  private static final String PARAM_C_SKEW = "c_skew";
  private static final String PARAM_C_FRQ = "c_frq";
  private static final String PARAM_C_PHS = "c_phs";
  private static final String PARAM_C_LAYER = "c_layer";

  private static final String PARAM_D = "d";
  private static final String PARAM_D_TYPE = "d_type";
  private static final String PARAM_D_SKEW = "d_skew";
  private static final String PARAM_D_FRQ = "d_frq";
  private static final String PARAM_D_PHS = "d_phs";
  private static final String PARAM_D_LAYER = "d_layer";

  private static final String PARAM_E = "e";
  private static final String PARAM_E_TYPE = "e_type";
  private static final String PARAM_E_SKEW = "e_skew";
  private static final String PARAM_E_FRQ = "e_frq";
  private static final String PARAM_E_PHS = "e_phs";
  private static final String PARAM_E_LAYER = "e_layer";

  private static final String PARAM_F = "f";
  private static final String PARAM_F_TYPE = "f_type";
  private static final String PARAM_F_SKEW = "f_skew";
  private static final String PARAM_F_FRQ = "f_frq";
  private static final String PARAM_F_PHS = "f_phs";
  private static final String PARAM_F_LAYER = "f_layer";

  private static final String[] paramNames = {PARAM_A, PARAM_MODE, PARAM_POWER, PARAM_MIX, PARAM_SMOOTH, PARAM_B, PARAM_B_TYPE, PARAM_B_SKEW, PARAM_B_FRQ, PARAM_B_PHS, PARAM_B_LAYER, PARAM_C, PARAM_C_TYPE, PARAM_C_SKEW, PARAM_C_FRQ, PARAM_C_PHS, PARAM_C_LAYER, PARAM_D, PARAM_D_TYPE, PARAM_D_SKEW, PARAM_D_FRQ, PARAM_D_PHS, PARAM_D_LAYER, PARAM_E, PARAM_E_TYPE, PARAM_E_SKEW, PARAM_E_FRQ, PARAM_E_PHS, PARAM_E_LAYER, PARAM_F, PARAM_F_TYPE, PARAM_F_SKEW, PARAM_F_FRQ, PARAM_F_PHS, PARAM_F_LAYER};

  private double a = 1.0;
  private int mode = 3;
  private double power = -2.0;
  private double mix = 1.0;
  private int smooth = 0;

  private double b = 0.0;
  private int b_type = 0;
  private double b_skew = 0.0;
  private double b_frq = 1.0;
  private double b_phs = 0.0;
  private int b_layer = 0;

  private double c = 0.0;
  private int c_type = 0;
  private double c_skew = 0.0;
  private double c_frq = 1.0;
  private double c_phs = 0.0;
  private int c_layer = 0;

  private double d = 0.0;
  private int d_type = 0;
  private double d_skew = 0.0;
  private double d_frq = 1.0;
  private double d_phs = 0.0;
  private int d_layer = 0;

  private double e = 0.0;
  private int e_type = 0;
  private double e_skew = 0.0;
  private double e_frq = 1.0;
  private double e_phs = 0.0;
  private int e_layer = 0;

  private double f = 0.0;
  private int f_type = 0;
  private double f_skew = 0.0;
  private double f_frq = 1.0;
  private double f_phs = 0.0;
  private int f_layer = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* synth by slobo777, http://slobo777.deviantart.com/art/Synth-V2-128594088 */
    double Vx, Vy, radius, theta; // Position vector in cartesian and polar co-ords
    double theta_factor; // Evaluation of synth() function for current point
    double s, c, mu; // Handy temp variables, s & c => sine & cosine, mu = generic temp param
    SinCosPair pair = new SinCosPair();

    switch (mode) {

      case MODE_RAWCIRCLE: // Power NO, Smooth YES
        // Get current radius and angle
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;
        radius = sqrt(Vx * Vx + Vy * Vy);
        theta = atan2(Vx, Vy);

        // Calculate new radius
        theta_factor = synth_value(theta);
        radius = interpolate(radius, theta_factor, smooth);
        s = sin(theta);
        c = cos(theta);

        // Write to running totals for transform
        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * c;
        break;

      case MODE_RAWY: // Power NO, Smooth YES
        // Use x and y values directly
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        // y value will be mapped according to synth(x) value
        theta_factor = synth_value(Vx);

        // Write to running totals for transform
        pVarTP.x += pAmount * Vx;
        pVarTP.y += pAmount * interpolate(Vy, theta_factor, smooth);
        break;

      case MODE_RAWX: // Power NO, Smooth YES
        // Use x and y values directly
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        // x value will be mapped according to synth(y) value
        theta_factor = synth_value(Vy);

        // Write to running totals for transform
        pVarTP.x += pAmount * interpolate(Vx, theta_factor, smooth);
        pVarTP.y += pAmount * Vy;
        break;

      case MODE_RAWXY: // Power NO, Smooth YES
        // Use x and y values directly
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        // x value will be mapped according to synth(y) value
        theta_factor = synth_value(Vy);
        pVarTP.x += pAmount * interpolate(Vx, theta_factor, smooth);

        // y value will be mapped according to synth(x) value
        theta_factor = synth_value(Vx);
        pVarTP.y += pAmount * interpolate(Vy, theta_factor, smooth);
        break;

      case MODE_SPHERICAL: // Power YES, Smooth YES
        // Re-write of spherical with synth tweak
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;
        radius = pow(Vx * Vx + Vy * Vy + EPS, (power + 1.0) / 2.0);

        // Get angle and angular factor
        theta = atan2(Vx, Vy);
        theta_factor = synth_value(theta);
        radius = interpolate(radius, theta_factor, smooth);
        s = sin(theta);
        c = cos(theta);

        // Write to running totals for transform
        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * c;
        break;

      case MODE_BUBBLE: // Power NO, Smooth YES
        // Re-write of bubble with synth tweak
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;
        radius = sqrt(Vx * Vx + Vy * Vy) / ((Vx * Vx + Vy * Vy) / 4 + 1);

        // Get angle and angular factor
        theta = atan2(Vx, Vy);
        theta_factor = synth_value(theta);
        radius = interpolate(radius, theta_factor, smooth);
        s = sin(theta);
        c = cos(theta);

        // Write to running totals for transform
        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * c;
        break;

      case MODE_BLUR_LEGACY: // Power YES, Smooth YES
        // "old" blur style, has some problems with moire-style artefacts
        radius = (pContext.random() + pContext.random() + 0.002 * pContext.random()) / 2.002;
        theta = 2.0 * M_PI * pContext.random() - M_PI;
        Vx = radius * sin(theta);
        Vy = radius * cos(theta);
        radius = pow(radius * radius + EPS, power / 2.0);

        // Get angle and angular factor
        theta_factor = synth_value(theta);
        radius = pAmount * interpolate(radius, theta_factor, smooth);

        // Write back to running totals for new vector
        pVarTP.x += Vx * radius;
        pVarTP.y += Vy * radius;
        break;

      case MODE_BLUR_NEW: // Power YES, Smooth YES
        // Blur style, with normal smoothing function

        // Choose radius randomly, then adjust distribution using pow
        radius = 0.5 * (pContext.random() + pContext.random());
        theta = 2 * M_PI * pContext.random() - M_PI;
        radius = pow(radius * radius + EPS, -power / 2.0);

        // Get angular factor defining the shape
        theta_factor = synth_value(theta);

        // Get final radius after synth applied
        radius = interpolate(radius, theta_factor, smooth);
        s = sin(theta);
        c = cos(theta);

        // Write to running totals for transform
        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * c;
        break;

      case MODE_BLUR_RING: // Power YES, Smooth YES
        // Blur style, with normal smoothing function

        radius = 1.0 + 0.1 * (pContext.random() + pContext.random() - 1.0) * power;
        theta = 2 * M_PI * pContext.random() - M_PI;

        // Get angular factor defining the shape
        theta_factor = synth_value(theta);

        // Get final radius after synth applied
        radius = interpolate(radius, theta_factor, smooth);
        s = sin(theta);
        c = cos(theta);

        // Write to running totals for transform
        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * c;
        break;

      case MODE_BLUR_RING2: // Power YES, Smooth NO
        // Simple, same-thickness ring

        // Choose radius randomly, then adjust distribution using pow
        theta = 2 * M_PI * pContext.random() - M_PI;

        radius = pow(pContext.random() + EPS, power);

        // Get final radius after synth applied
        radius = synth_value(theta) + 0.1 * radius;
        s = sin(theta);
        c = cos(theta);

        // Write to running totals for transform
        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * c;
        break;

      case MODE_SHIFTNSTRETCH: // Power YES, Smooth NO
        // Use (adjusted) radius to move point around circle
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        radius = pow(Vx * Vx + Vy * Vy + EPS, power / 2.0);

        theta = atan2(Vx, Vy) - 1.0 + synth_value(radius);

        s = sin(theta);
        c = cos(theta);

        // Write to running totals for transform
        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * c;
        break;

      case MODE_SHIFTTANGENT: // Power YES, Smooth NO
        // Use (adjusted) radius to move point tangentially to circle
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        radius = pow(Vx * Vx + Vy * Vy + EPS, power / 2.0);

        theta = atan2(Vx, Vy);
        s = sin(theta);
        c = cos(theta);

        // Adjust  Vx and Vy directly
        mu = synth_value(radius) - 1.0;
        Vx += mu * c;
        Vy -= mu * s;

        // Write to running totals for transform
        pVarTP.x += pAmount * Vx;
        pVarTP.y += pAmount * Vy;
        break;

      case MODE_SHIFTTHETA: // Power YES, Smooth NO
        // Use (adjusted) radius to move point around circle
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        radius = pow(Vx * Vx + Vy * Vy + EPS, power / 2.0);

        theta = atan2(Vx, Vy) - 1.0 + synth_value(radius);

        s = sin(theta);
        c = cos(theta);
        radius = sqrt(Vx * Vx + Vy * Vy);

        // Write to running totals for transform
        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * c;
        break;

      case MODE_BLUR_ZIGZAG: // Power YES, Smooth YES
        // Blur effect based on line segment
        // theta is used as x value
        // Vy is y value
        Vy = 1.0 + 0.1 * (pContext.random() + pContext.random() - 1.0) * power;
        theta = 2.0 * asin((pContext.random() - 0.5) * 2.0);

        // Get angular factor defining the shape
        theta_factor = synth_value(theta);

        // Get new location
        Vy = interpolate(Vy, theta_factor, smooth);

        // Write to running totals for transform
        pVarTP.x += pAmount * (theta / M_PI);
        pVarTP.y += pAmount * (Vy - 1.0);
        break;

      case MODE_SHIFTX: // Power NO, Smooth YES
        // Use x and y values directly
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        // Write to running totals for transform
        pVarTP.x += pAmount * (Vx + synth_value(Vy) - 1.0);
        pVarTP.y += pAmount * Vy;
        break;

      case MODE_SHIFTY: // Power NO, Smooth NO
        // Use x and y values directly
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        // Write to running totals for transform
        pVarTP.x += pAmount * Vx;
        pVarTP.y += pAmount * (Vy + synth_value(Vx) - 1.0);

        break;

      case MODE_SHIFTXY: // Power NO, Smooth NO
        // Use x and y values directly
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        // Write to running totals for transform
        pVarTP.x += pAmount * (Vx + synth_value(Vy) - 1.0);
        pVarTP.y += pAmount * (Vy + synth_value(Vx) - 1.0);

        break;

      case MODE_SINUSOIDAL: // Power NO, Smooth NO
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        // The default mix=0 is same as normal sin
        pVarTP.x += pAmount * (synth_value(Vx) - 1.0 + (1.0 - mix) * sin(Vx));
        pVarTP.y += pAmount * (synth_value(Vy) - 1.0 + (1.0 - mix) * sin(Vy));

        break;

      case MODE_SWIRL: // Power YES, Smooth WAVE
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        radius = pow(Vx * Vx + Vy * Vy + EPS, power / 2.0);

        // Synth-modified sine & cosine
        synthsincos(radius, pair, smooth);
        s = pair.s;
        c = pair.c;

        pVarTP.x += pAmount * (s * Vx - c * Vy);
        pVarTP.y += pAmount * (c * Vx + s * Vy);

        break;

      case MODE_HYPERBOLIC: // Power YES, Smooth WAVE
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        radius = pow(Vx * Vx + Vy * Vy + EPS, power / 2.0);

        theta = atan2(Vx, Vy);

        // Synth-modified sine & cosine
        synthsincos(theta, pair, smooth);
        s = pair.s;
        c = pair.c;

        pVarTP.x += pAmount * s / radius;
        pVarTP.y += pAmount * c * radius;

        break;

      case MODE_JULIA: // Power YES, Smooth WAVE
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        radius = pow(Vx * Vx + Vy * Vy + EPS, power / 4.0);

        theta = atan2(Vx, Vy) / 2.0;

        if (pContext.random() < 0.5)
          theta += M_PI;

        // Synth-modified sine & cosine
        synthsincos(theta, pair, smooth);
        s = pair.s;
        c = pair.c;

        pVarTP.x += pAmount * radius * c;
        pVarTP.y += pAmount * radius * s;

        break;

      case MODE_DISC: // Power YES, Smooth WAVE
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        theta = atan2(Vx, Vy) / M_PI;

        radius = M_PI * pow(Vx * Vx + Vy * Vy + EPS, power / 2.0);

        // Synth-modified sine & cosine
        synthsincos(radius, pair, smooth);
        s = pair.s;
        c = pair.c;

        pVarTP.x = pAmount * s * theta;
        pVarTP.y = pAmount * c * theta;

        break;

      case MODE_RINGS: // Power PARAM, Smooth WAVE

        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        radius = sqrt(Vx * Vx + Vy * Vy);
        theta = atan2(Vx, Vy);

        mu = power * power + EPS;

        radius += -2.0 * mu * (int) ((radius + mu) / (2.0 * mu)) + radius * (1.0 - mu);

        synthsincos(theta, pair, smooth);
        s = pair.s;
        c = pair.c;

        pVarTP.x += pAmount * s * radius;
        pVarTP.y += pAmount * c * radius;

        break;

      case MODE_CYLINDER: // Power YES, Smooth WAVE
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;
        radius = pow(Vx * Vx + Vy * Vy + EPS, power / 2.0);

        // Modified sine only used here
        synthsincos(Vx, pair, smooth);
        s = pair.s;
        c = pair.c;

        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * Vy;

        break;

      case MODE_XMIRROR: // Power NO, Smooth NO
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        // Modified sine only used here
        mu = synth_value(Vx) - 1.0;
        Vy = 2.0 * mu - Vy;

        pVarTP.x += pAmount * Vx;
        pVarTP.y += pAmount * Vy;

        break;

      case MODE_XYMIRROR: // Power NO, Smooth NO
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;

        // radius sneakily being used to represent something completely different, sorry!
        mu = synth_value(Vx) - 1.0;
        radius = synth_value(Vy) - 1.0;
        Vy = 2.0 * mu - Vy;
        Vx = 2.0 * radius - Vx;

        pVarTP.x += pAmount * Vx;
        pVarTP.y += pAmount * Vy;

        break;

      case MODE_SPHERICAL2: // Power YES, Smooth YES
        Vx = pAffineTP.x;
        Vy = pAffineTP.y;
        radius = sqrt(Vx * Vx + Vy * Vy);

        // Get angle and angular factor
        theta = atan2(Vx, Vy);
        theta_factor = synth_value(theta);
        radius = interpolate(radius, theta_factor, smooth);
        radius = pow(radius, power);
        s = sin(theta);
        c = cos(theta);

        // Write to running totals for transform
        pVarTP.x += pAmount * radius * s;
        pVarTP.y += pAmount * radius * c;
        break;
      default: // nothing to do
        break;
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
    return new Object[]{a, mode, power, mix, smooth, b, b_type, b_skew, b_frq, b_phs, b_layer, c, c_type, c_skew, c_frq, c_phs, c_layer, d, d_type, d_skew, d_frq, d_phs, d_layer, e, e_type, e_skew, e_frq, e_phs, e_layer, f, f_type, f_skew, f_frq, f_phs, f_layer};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_MODE.equalsIgnoreCase(pName))
      mode = Tools.FTOI(pValue);
    else if (PARAM_POWER.equalsIgnoreCase(pName))
      power = pValue;
    else if (PARAM_MIX.equalsIgnoreCase(pName))
      mix = pValue;
    else if (PARAM_SMOOTH.equalsIgnoreCase(pName))
      smooth = Tools.FTOI(pValue);

    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_B_TYPE.equalsIgnoreCase(pName))
      b_type = Tools.FTOI(pValue);
    else if (PARAM_B_SKEW.equalsIgnoreCase(pName))
      b_skew = pValue;
    else if (PARAM_B_FRQ.equalsIgnoreCase(pName))
      b_frq = pValue;
    else if (PARAM_B_PHS.equalsIgnoreCase(pName))
      b_phs = pValue;
    else if (PARAM_B_LAYER.equalsIgnoreCase(pName))
      b_layer = Tools.FTOI(pValue);

    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_C_TYPE.equalsIgnoreCase(pName))
      c_type = Tools.FTOI(pValue);
    else if (PARAM_C_SKEW.equalsIgnoreCase(pName))
      c_skew = pValue;
    else if (PARAM_C_FRQ.equalsIgnoreCase(pName))
      c_frq = pValue;
    else if (PARAM_C_PHS.equalsIgnoreCase(pName))
      c_phs = pValue;
    else if (PARAM_C_LAYER.equalsIgnoreCase(pName))
      c_layer = Tools.FTOI(pValue);

    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
    else if (PARAM_D_TYPE.equalsIgnoreCase(pName))
      d_type = Tools.FTOI(pValue);
    else if (PARAM_D_SKEW.equalsIgnoreCase(pName))
      d_skew = pValue;
    else if (PARAM_D_FRQ.equalsIgnoreCase(pName))
      d_frq = pValue;
    else if (PARAM_D_PHS.equalsIgnoreCase(pName))
      d_phs = pValue;
    else if (PARAM_D_LAYER.equalsIgnoreCase(pName))
      d_layer = Tools.FTOI(pValue);

    else if (PARAM_E.equalsIgnoreCase(pName))
      e = pValue;
    else if (PARAM_E_TYPE.equalsIgnoreCase(pName))
      e_type = Tools.FTOI(pValue);
    else if (PARAM_E_SKEW.equalsIgnoreCase(pName))
      e_skew = pValue;
    else if (PARAM_E_FRQ.equalsIgnoreCase(pName))
      e_frq = pValue;
    else if (PARAM_E_PHS.equalsIgnoreCase(pName))
      e_phs = pValue;
    else if (PARAM_E_LAYER.equalsIgnoreCase(pName))
      e_layer = Tools.FTOI(pValue);

    else if (PARAM_F.equalsIgnoreCase(pName))
      f = pValue;
    else if (PARAM_F_TYPE.equalsIgnoreCase(pName))
      f_type = Tools.FTOI(pValue);
    else if (PARAM_F_SKEW.equalsIgnoreCase(pName))
      f_skew = pValue;
    else if (PARAM_F_FRQ.equalsIgnoreCase(pName))
      f_frq = pValue;
    else if (PARAM_F_PHS.equalsIgnoreCase(pName))
      f_phs = pValue;
    else if (PARAM_F_LAYER.equalsIgnoreCase(pName))
      f_layer = Tools.FTOI(pValue);

    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "synth";
  }

  // -------------------------------------------------------------
  // Modes
  // "Lagacy" modes from v1
  private static final int MODE_SPHERICAL = 0;
  private static final int MODE_BUBBLE = 1;
  private static final int MODE_BLUR_LEGACY = 2;
  // New modes in v2
  private static final int MODE_BLUR_NEW = 3;
  private static final int MODE_BLUR_ZIGZAG = 4;
  private static final int MODE_RAWCIRCLE = 5;
  private static final int MODE_RAWX = 6;
  private static final int MODE_RAWY = 7;
  private static final int MODE_RAWXY = 8;
  private static final int MODE_SHIFTX = 9;
  private static final int MODE_SHIFTY = 10;
  private static final int MODE_SHIFTXY = 11;
  private static final int MODE_BLUR_RING = 12;
  private static final int MODE_BLUR_RING2 = 13;
  private static final int MODE_SHIFTNSTRETCH = 14;
  private static final int MODE_SHIFTTANGENT = 15;
  private static final int MODE_SHIFTTHETA = 16;
  private static final int MODE_XMIRROR = 17;
  private static final int MODE_XYMIRROR = 18;
  private static final int MODE_SPHERICAL2 = 19;

  // Ideas:
  // Rectangle
  // Grid
  // Spiral grid
  // Ortho?

  // Failed experiments (were 12-18)
  private static final int MODE_SINUSOIDAL = 1001;
  private static final int MODE_SWIRL = 1002;
  private static final int MODE_HYPERBOLIC = 1003;
  private static final int MODE_JULIA = 1004;
  private static final int MODE_DISC = 1005;
  private static final int MODE_RINGS = 1006;
  private static final int MODE_CYLINDER = 1007;

  // -------------------------------------------------------------
  // Wave types
  private static final int WAVE_SIN = 0;
  private static final int WAVE_COS = 1;
  private static final int WAVE_SQUARE = 2;
  private static final int WAVE_SAW = 3;
  private static final int WAVE_TRIANGLE = 4;
  private static final int WAVE_CONCAVE = 5;
  private static final int WAVE_CONVEX = 6;
  private static final int WAVE_NGON = 7;
  // New wave types in v2
  private static final int WAVE_INGON = 8;

  // -------------------------------------------------------------
  // Layer types
  private static final int LAYER_ADD = 0;
  private static final int LAYER_MULT = 1;
  private static final int LAYER_MAX = 2;
  private static final int LAYER_MIN = 3;

  // -------------------------------------------------------------
  // Interpolation types
  private static final int LERP_LINEAR = 0;
  private static final int LERP_BEZIER = 1;

  // -------------------------------------------------------------
  // Sine/Cosine interpretation types
  private static final int SINCOS_MULTIPLY = 0;
  private static final int SINCOS_MIXIN = 1;

  private static final double EPS = SMALL_EPSILON;

  // -------------------------------------------------------------
  // synth_value calculates the wave height y from theta, which is an abstract
  // angle that could come from any other calculation - for circular modes
  // it will be the angle between the positive y axis and the vector from
  // the origin to the pont i.e. atan2(x,y)
  // You must call the argument "vp".
  private double synth_value(double theta) {
    double theta_factor = a;
    double x = 0, y, z;

    if (b != 0.0) {

      z = b_phs + theta * b_frq;
      y = z / (2 * M_PI);
      y -= floor(y);

      // y is in range 0 - 1. Now skew according to synth_b_skew
      if (b_skew != 0.0) {
        z = 0.5 + 0.5 * b_skew;
        if (y > z) {
          // y is 0.5 if equals z, up to 1.0
          y = 0.5 + 0.5 * (y - z) / (1.0 - z + EPS);
        } else {
          // y is 0.5 if equals z, down to 0.0
          y = 0.5 - 0.5 * (z - y) / (z + EPS);
        }
      }

      switch (b_type) {
        case WAVE_SIN:
          x = sin(y * 2 * M_PI);
          break;
        case WAVE_COS:
          x = cos(y * 2 * M_PI);
          break;
        case WAVE_SQUARE:
          x = y > 0.5 ? 1.0 : -1.0;
          break;
        case WAVE_SAW:
          x = 1.0 - 2.0 * y;
          break;
        case WAVE_TRIANGLE:
          x = y > 0.5 ? 3.0 - 4.0 * y : 2.0 * y - 1.0;
          break;
        case WAVE_CONCAVE:
          x = 8.0 * (y - 0.5) * (y - 0.5) - 1.0;
          break;
        case WAVE_CONVEX:
          x = 2.0 * sqrt(y) - 1.0;
          break;
        case WAVE_NGON:
          y -= 0.5;
          y *= (2.0 * M_PI / b_frq);
          x = (1.0 / (cos(y) + EPS) - 1.0);
          break;
        case WAVE_INGON:
          y -= 0.5;
          y *= (2.0 * M_PI / b_frq);
          z = cos(y);
          x = z / (1.0 + EPS - z);
          break;
        default: // nothing to do
          break;
      }

      switch (b_layer) {
        case LAYER_ADD:
          theta_factor += b * x;
          break;
        case LAYER_MULT:
          theta_factor *= (1.0 + b * x);
          break;
        case LAYER_MAX:
          z = a + b * x;
          theta_factor = (theta_factor > z ? theta_factor : z);
          break;
        case LAYER_MIN:
          z = a + b * x;
          theta_factor = (theta_factor < z ? theta_factor : z);
          break;
        default: // nothing to do
          break;
      }
    }

    if (c != 0.0) {

      z = c_phs + theta * c_frq;
      y = z / (2 * M_PI);
      y -= floor(y);

      // y is in range 0 - 1. Now skew according to synth_c_skew
      if (c_skew != 0.0) {
        z = 0.5 + 0.5 * c_skew;
        if (y > z) {
          // y is 0.5 if equals z, up to 1.0
          y = 0.5 + 0.5 * (y - z) / (1.0 - z + EPS);
        } else {
          // y is 0.5 if equals z, down to 0.0
          y = 0.5 - 0.5 * (z - y) / (z + EPS);
        }
      }

      switch (c_type) {
        case WAVE_SIN:
          x = sin(y * 2 * M_PI);
          break;
        case WAVE_COS:
          x = cos(y * 2 * M_PI);
          break;
        case WAVE_SQUARE:
          x = y > 0.5 ? 1.0 : -1.0;
          break;
        case WAVE_SAW:
          x = 1.0 - 2.0 * y;
          break;
        case WAVE_TRIANGLE:
          x = y > 0.5 ? 3.0 - 4.0 * y : 2.0 * y - 1.0;
          break;
        case WAVE_CONCAVE:
          x = 8.0 * (y - 0.5) * (y - 0.5) - 1.0;
          break;
        case WAVE_CONVEX:
          x = 2.0 * sqrt(y) - 1.0;
          break;
        case WAVE_NGON:
          y -= 0.5;
          y *= (2.0 * M_PI / c_frq);
          x = (1.0 / (cos(y) + EPS) - 1.0);
          break;
        case WAVE_INGON:
          y -= 0.5;
          y *= (2.0 * M_PI / c_frq);
          z = cos(y);
          x = z / (1.0 + EPS - z);
          break;
        default: // nothing to do
          break;
      }

      switch (c_layer) {
        case LAYER_ADD:
          theta_factor += c * x;
          break;
        case LAYER_MULT:
          theta_factor *= (1.0 + c * x);
          break;
        case LAYER_MAX:
          z = a + c * x;
          theta_factor = (theta_factor > z ? theta_factor : z);
          break;
        case LAYER_MIN:
          z = a + c * x;
          theta_factor = (theta_factor < z ? theta_factor : z);
          break;
        default: // nothing to do
          break;
      }
    }

    if (d != 0.0) {

      z = d_phs + theta * d_frq;
      y = z / (2 * M_PI);
      y -= floor(y);

      // y is in range 0 - 1. Now skew according to synth_d_skew
      if (d_skew != 0.0) {
        z = 0.5 + 0.5 * d_skew;
        if (y > z) {
          // y is 0.5 if equals z, up to 1.0
          y = 0.5 + 0.5 * (y - z) / (1.0 - z + EPS);
        } else {
          // y is 0.5 if equals z, down to 0.0
          y = 0.5 - 0.5 * (z - y) / (z + EPS);
        }
      }

      switch (d_type) {
        case WAVE_SIN:
          x = sin(y * 2 * M_PI);
          break;
        case WAVE_COS:
          x = cos(y * 2 * M_PI);
          break;
        case WAVE_SQUARE:
          x = y > 0.5 ? 1.0 : -1.0;
          break;
        case WAVE_SAW:
          x = 1.0 - 2.0 * y;
          break;
        case WAVE_TRIANGLE:
          x = y > 0.5 ? 3.0 - 4.0 * y : 2.0 * y - 1.0;
          break;
        case WAVE_CONCAVE:
          x = 8.0 * (y - 0.5) * (y - 0.5) - 1.0;
          break;
        case WAVE_CONVEX:
          x = 2.0 * sqrt(y) - 1.0;
          break;
        case WAVE_NGON:
          y -= 0.5;
          y *= (2.0 * M_PI / d_frq);
          x = (1.0 / (cos(y) + EPS) - 1.0);
          break;
        case WAVE_INGON:
          y -= 0.5;
          y *= (2.0 * M_PI / d_frq);
          z = cos(y);
          x = z / (1.0 + EPS - z);
          break;
        default: // nothing to do
          break;
      }

      switch (d_layer) {
        case LAYER_ADD:
          theta_factor += d * x;
          break;
        case LAYER_MULT:
          theta_factor *= (1.0 + d * x);
          break;
        case LAYER_MAX:
          z = a + d * x;
          theta_factor = (theta_factor > z ? theta_factor : z);
          break;
        case LAYER_MIN:
          z = a + d * x;
          theta_factor = (theta_factor < z ? theta_factor : z);
          break;
        default: // nothing to do
          break;
      }
    }

    if (e != 0.0) {

      z = e_phs + theta * e_frq;
      y = z / (2 * M_PI);
      y -= floor(y);

      // y is in range 0 - 1. Now skew according to synth_e_skew
      if (e_skew != 0.0) {
        z = 0.5 + 0.5 * e_skew;
        if (y > z) {
          // y is 0.5 if equals z, up to 1.0
          y = 0.5 + 0.5 * (y - z) / (1.0 - z + EPS);
        } else {
          // y is 0.5 if equals z, down to 0.0
          y = 0.5 - 0.5 * (z - y) / (z + EPS);
        }
      }

      switch (e_type) {
        case WAVE_SIN:
          x = sin(y * 2 * M_PI);
          break;
        case WAVE_COS:
          x = cos(y * 2 * M_PI);
          break;
        case WAVE_SQUARE:
          x = y > 0.5 ? 1.0 : -1.0;
          break;
        case WAVE_SAW:
          x = 1.0 - 2.0 * y;
          break;
        case WAVE_TRIANGLE:
          x = y > 0.5 ? 3.0 - 4.0 * y : 2.0 * y - 1.0;
          break;
        case WAVE_CONCAVE:
          x = 8.0 * (y - 0.5) * (y - 0.5) - 1.0;
          break;
        case WAVE_CONVEX:
          x = 2.0 * sqrt(y) - 1.0;
          break;
        case WAVE_NGON:
          y -= 0.5;
          y *= (2.0 * M_PI / e_frq);
          x = (1.0 / (cos(y) + EPS) - 1.0);
          break;
        case WAVE_INGON:
          y -= 0.5;
          y *= (2.0 * M_PI / e_frq);
          z = cos(y);
          x = z / (1.0 + EPS - z);
          break;
        default: // nothing to do
          break;
      }

      switch (e_layer) {
        case LAYER_ADD:
          theta_factor += e * x;
          break;
        case LAYER_MULT:
          theta_factor *= (1.0 + e * x);
          break;
        case LAYER_MAX:
          z = a + e * x;
          theta_factor = (theta_factor > z ? theta_factor : z);
          break;
        case LAYER_MIN:
          z = a + e * x;
          theta_factor = (theta_factor < z ? theta_factor : z);
          break;
        default: // nothing to do
          break;
      }
    }

    if (f != 0.0) {

      z = f_phs + theta * f_frq;
      y = z / (2 * M_PI);
      y -= floor(y);

      // y is in range 0 - 1. Now skew according to synth_f_skew
      if (f_skew != 0.0) {
        z = 0.5 + 0.5 * f_skew;
        if (y > z) {
          // y is 0.5 if equals z, up to 1.0
          y = 0.5 + 0.5 * (y - z) / (1.0 - z + EPS);
        } else {
          // y is 0.5 if equals z, down to 0.0
          y = 0.5 - 0.5 * (z - y) / (z + EPS);
        }
      }

      switch (f_type) {
        case WAVE_SIN:
          x = sin(y * 2 * M_PI);
          break;
        case WAVE_COS:
          x = cos(y * 2 * M_PI);
          break;
        case WAVE_SQUARE:
          x = y > 0.5 ? 1.0 : -1.0;
          break;
        case WAVE_SAW:
          x = 1.0 - 2.0 * y;
          break;
        case WAVE_TRIANGLE:
          x = y > 0.5 ? 3.0 - 4.0 * y : 2.0 * y - 1.0;
          break;
        case WAVE_CONCAVE:
          x = 8.0 * (y - 0.5) * (y - 0.5) - 1.0;
          break;
        case WAVE_CONVEX:
          x = 2.0 * sqrt(y) - 1.0;
          break;
        case WAVE_NGON:
          y -= 0.5;
          y *= (2.0 * M_PI / f_frq);
          x = (1.0 / (cos(y) + EPS) - 1.0);
          break;
        case WAVE_INGON:
          y -= 0.5;
          y *= (2.0 * M_PI / f_frq);
          z = cos(y);
          x = z / (1.0 + EPS - z);
          break;
        default: // nothing to do
          break;
      }

      switch (f_layer) {
        case LAYER_ADD:
          theta_factor += f * x;
          break;
        case LAYER_MULT:
          theta_factor *= (1.0 + f * x);
          break;
        case LAYER_MAX:
          z = a + f * x;
          theta_factor = (theta_factor > z ? theta_factor : z);
          break;
        case LAYER_MIN:
          z = a + f * x;
          theta_factor = (theta_factor < z ? theta_factor : z);
          break;
        default: // nothing to do
          break;
      }
    }

    // Mix is applied here, assuming 1.0 to be the "flat" line for legacy support
    return theta_factor * mix + (1.0 - mix);
  }

  // -------------------------------------------------------------
  // Mapping function y = fn(x) based on quadratic Bezier curves for smooth type 1
  // Returns close to y = x for high/low values of x, y = m when x = 1.0, and
  // something in-between y = m*x and y = x lines when x is close-ish to 1.0
  // Function always has slope of 0.0 or greater, so no x' values "overlap"
  private double bezier_quad_map(double x, double m) {
    double a = 1.0; // a is used to control sign of result
    double t = 0.0; // t is the Bezier curve parameter

    // Simply reflect in the y axis for negative values
    if (m < 0.0) {
      m = -m;
      a = -1.0;
    }
    if (x < 0.0) {
      x = -x;
      a = -a;
    }

    // iM is "inverse m" used in a few places below
    double iM = 1e10;
    if (m > 1.0e-10) {
      iM = 1.0 / m;
    }

    double L = (2.0 - m) > 2.0 * m ? (2.0 - m) : 2.0 * m;

    // "Non Curved"
    // Covers x >= L, or always true if m == 1.0
    // y = x  i.e. not distorted
    if ((x > L) || (m == 1.0)) {
      return a * x;
    }

    if ((m < 1.0) && (x <= 1.0)) {
      // Bezier Curve #1
      // Covers 0 <= $m <= 1.0, 0 <= $x <= 1.0
      // Control points are (0,0), (m,m) and (1,m)

      t = x; // Special case when m == 0.5
      if ((m - 0.5) * (m - 0.5) > 1e-10) {
        t = (-1.0 * m + sqrt(m * m + (1.0 - 2.0 * m) * x)) / (1.0 - 2.0 * m);
      }

      return a * (x + (m - 1.0) * t * t);
    }

    if ((1.0 < m) && (x <= 1.0)) {
      // Bezier Curve #2
      // Covers m >= 1.0, 0 <= x <= 1.0
      // Control points are (0,0), (iM,iM) and (1,m)

      t = x; // Special case when m == 2
      if ((m - 2.0) * (m - 2.0) > 1e-10) {
        t = (-1.0 * iM + sqrt(iM * iM + (1.0 - 2.0 * iM) * x)) / (1 - 2 * iM);
      }
      return a * (x + (m - 1.0) * t * t);
    }

    // Deliberate divide by zero to rule out code causing a bug

    if (m < 1.0) {
      // Bezier Curve #3
      // Covers 0 <= m <= 1.0, 1 <= x <= L
      // Control points are (1,m), (1,1) and (L,L)
      // (L is x value (>1) where we re-join y = x line, and is maximum( iM, 2 * m )

      t = sqrt((x - 1.0) / (L - 1.0));
      return a * (x + (m - 1.0) * t * t + 2 * (1.0 - m) * t + (m - 1.0));
    }

    // Curve #4
    // Covers 1.0 <= m, 1 <= x <= L
    // Control points are (1,m), (m,m) and (L,L)
    // (L is x value (>1) where we re-join y = x line, and is maximum( iM, 2 *  m )

    t = (1.0 - m) + sqrt((m - 1.0) * (m - 1.0) + (x - 1.0));
    return a * (x + (m - 1.0) * t * t - 2.0 * (m - 1.0) * t + (m - 1.0));
  }

  // Handle potentially many types of interpolation routine in future  . . .
  private double interpolate(double x, double m, int lerp_type) {
    switch (lerp_type) {
      case LERP_LINEAR:
        return x * m;
      case LERP_BEZIER:
        return bezier_quad_map(x, m);
    }
    return x * m;
  }

  private static class SinCosPair {
    public double s;
    public double c;
  }

  private void synthsincos(double theta, SinCosPair pair, int sine_type) {
    pair.s = sin(theta);
    pair.c = cos(theta);
    switch (sine_type) {
      case SINCOS_MULTIPLY:
        pair.s = pair.s * synth_value(theta);
        pair.c = pair.c * synth_value(theta + M_PI / 2.0);
        break;
      case SINCOS_MIXIN:
        pair.s = (1.0 - mix) * pair.s + (synth_value(theta) - 1.0);
        pair.c = (1.0 - mix) * pair.c + (synth_value(theta + M_PI / 2.0) - 1.0);
        break;
      default: // nothing to do
        break;
    }
    return;
  }
}
