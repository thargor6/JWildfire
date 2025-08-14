
/*
 * JWildfire - an image and animation processor written in Java
 * Copyright (C) 1995-2021 Andreas Maschke
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * ---
 *
 * Mandelbox2DFunc
 *
 * A 2D fractal variation with multiple Mandelbox calculation modes and simplified coloring options.
 *
 */
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;

public class Mandelbox2DFunc extends VariationFunc {
  private static final long serialVersionUID = 11L; // Bumped version ID

  // Parameter names for the UI
  private static final String PARAM_MANDELBOX_MODE = "mandelboxMode";
  private static final String PARAM_ITERATIONS = "iterations";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_FOLD_LIMIT = "foldLimit";
  private static final String PARAM_MIN_RADIUS = "minRadius";
  private static final String PARAM_FIXED_RADIUS = "fixedRadius";
  private static final String PARAM_ROTATION = "rotation";
  private static final String PARAM_JULIA_MODE = "juliaMode";
  private static final String PARAM_JULIA_X = "juliaX";
  private static final String PARAM_JULIA_Y = "juliaY";
  private static final String PARAM_COLORING_MODE = "coloringMode";
  private static final String PARAM_COLOR_SPEED = "colorSpeed";

  private static final String[] paramNames = {PARAM_MANDELBOX_MODE, PARAM_ITERATIONS, PARAM_SCALE, PARAM_FOLD_LIMIT, PARAM_MIN_RADIUS, PARAM_FIXED_RADIUS, PARAM_ROTATION, PARAM_JULIA_MODE, PARAM_JULIA_X, PARAM_JULIA_Y, PARAM_COLORING_MODE, PARAM_COLOR_SPEED};

  // Default parameter values
  private int mandelboxMode = 0; // 0=A, 1=B, 2=C
  private int iterations = 10;
  private double scale = -1.5;
  private double foldLimit = 1.0;
  private double minRadius = 0.5;
  private double fixedRadius = 1.0;
  private double rotation = 0.0;
  private int juliaMode = 0; // 0=off, 1=on
  private double juliaX = 0.4;
  private double juliaY = 0.6;
  private int coloringMode = 0; // 0=None, 1=FinalPos, 2=IterativeBlend
  private double colorSpeed = 0.5;

  /**
   * The main transformation method.
   * This is where the selected fractal algorithm is applied to the point.
   */
  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double x = pAffineTP.x;
    double y = pAffineTP.y;

    double cx, cy;
    if (juliaMode == 1) {
      cx = juliaX;
      cy = juliaY;
    } else {
      cx = x;
      cy = y;
    }

    double minRadius2 = minRadius * minRadius;
    double fixedRadius2 = fixedRadius * fixedRadius;
    double rotRad = toRadians(rotation);
    double cos_r = cos(rotRad);
    double sin_r = sin(rotRad);

    // Variable for iterative blend coloring
    double blendedColor = pAffineTP.color; 

    for (int i = 0; i < iterations; i++) {
        // Accumulate values for coloring modes that need them
        if (coloringMode == 2) { // Iterative Blend
            double targetColor = (atan2(y, x) / (2.0 * PI)) + 0.5; // Target color is based on current angle
            blendedColor += (targetColor - blendedColor) * colorSpeed;
        }

        // --- Start of Mandelbox Logic ---
        if (mandelboxMode == 0) {
            if (x > foldLimit) x = (2.0 * foldLimit) - x; else if (x < -foldLimit) x = (-2.0 * foldLimit) - x;
            if (y > foldLimit) y = (2.0 * foldLimit) - y; else if (y < -foldLimit) y = (-2.0 * foldLimit) - y;
            double r2 = x * x + y * y; if (r2 < minRadius2) { double f = fixedRadius2 / minRadius2; x *= f; y *= f; }
            x *= scale; y *= scale;
            if (rotation != 0.0) { double xn = x * cos_r - y * sin_r; y = x * sin_r + y * cos_r; x = xn; }
            x += cx; y += cy;
        } else if (mandelboxMode == 1) {
            x *= scale; y *= scale;
            x += cx; y += cy;
            if (rotation != 0.0) { double xn = x * cos_r - y * sin_r; y = x * sin_r + y * cos_r; x = xn; }
            if (x > foldLimit) x = (2.0 * foldLimit) - x; else if (x < -foldLimit) x = (-2.0 * foldLimit) - x;
            if (y > foldLimit) y = (2.0 * foldLimit) - y; else if (y < -foldLimit) y = (-2.0 * foldLimit) - y;
            double r2 = x * x + y * y; if (r2 < minRadius2) { double f = fixedRadius2 / r2; x *= f; y *= f; }
        } else {
            if (x > foldLimit) x = (2.0 * foldLimit) - x; else if (x < -foldLimit) x = (-2.0 * foldLimit) - x;
            if (y > foldLimit) y = (2.0 * foldLimit) - y; else if (y < -foldLimit) y = (-2.0 * foldLimit) - y;
            x += cx; y += cy;
            x *= scale; y *= scale;
            if (rotation != 0.0) { double xn = x * cos_r - y * sin_r; y = x * sin_r + y * cos_r; x = xn; }
            double r2 = x * x + y * y; if (r2 < minRadius2) { double f = fixedRadius2 / r2; x *= f; y *= f; }
        }
        // --- End of Mandelbox Logic ---
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    // Apply color based on selected mode
    if (coloringMode == 1) { // Final Position
        pVarTP.color = (atan2(y, x) / (2.0 * PI)) + 0.5; 
    } else if (coloringMode == 2) { // Iterative Blend
        pVarTP.color = blendedColor; 
    }
    // Note: coloringMode 0 (None) does nothing, so pVarTP.color is not set.

    if (pContext.isPreserveZCoordinate()) { pVarTP.z += pAffineTP.z * pAmount; }
  }

  @Override
  public String getName() {
    return "mandelbox2D";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{mandelboxMode, iterations, scale, foldLimit, minRadius, fixedRadius, rotation, juliaMode, juliaX, juliaY, coloringMode, colorSpeed};
  }
  
  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"mode", "iters", "mb_scale", "mb_fold_limit", "mb_min_radius", "mb_fixed_radius", "mb_rotation", "mb_julia_mode", "mb_julia_x", "mb_julia_y", "color_mode", "color_speed"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_MANDELBOX_MODE.equalsIgnoreCase(pName)) { mandelboxMode = limitIntVal(Tools.FTOI(pValue), 0, 2); }
    else if (PARAM_ITERATIONS.equalsIgnoreCase(pName)) { iterations = Math.max(0, (int) pValue); }
    else if (PARAM_SCALE.equalsIgnoreCase(pName)) { scale = pValue; }
    else if (PARAM_FOLD_LIMIT.equalsIgnoreCase(pName)) { foldLimit = pValue; }
    else if (PARAM_MIN_RADIUS.equalsIgnoreCase(pName)) { minRadius = pValue; }
    else if (PARAM_FIXED_RADIUS.equalsIgnoreCase(pName)) { fixedRadius = pValue; }
    else if (PARAM_ROTATION.equalsIgnoreCase(pName)) { rotation = pValue; }
    else if (PARAM_JULIA_MODE.equalsIgnoreCase(pName)) { juliaMode = limitIntVal(Tools.FTOI(pValue), 0, 1); }
    else if (PARAM_JULIA_X.equalsIgnoreCase(pName)) { juliaX = pValue; }
    else if (PARAM_JULIA_Y.equalsIgnoreCase(pName)) { juliaY = pValue; }
    else if (PARAM_COLORING_MODE.equalsIgnoreCase(pName)) { coloringMode = limitIntVal(Tools.FTOI(pValue), 0, 2); }
    else if (PARAM_COLOR_SPEED.equalsIgnoreCase(pName)) { colorSpeed = pValue; }
    else { throw new IllegalArgumentException("Unknown parameter: " + pName); }
  }
  
  @Override
  public void randomize() {
  	mandelboxMode = (int) (Math.random() * 3);
  	iterations = (int) (Math.random() * 25);
  	foldLimit = Math.random() * 2.0 + 0.5;
  	if (mandelboxMode == 0) {
  		scale = Math.random() * 4.5 - 3.0;
  		fixedRadius = Math.random() * 5.0 + 0.1;
  	} else if (mandelboxMode == 1) {
  		scale = Math.random() * 3.0 - 1.0;
  		fixedRadius = Math.random() * 1.5 + 0.1;
  	} else {
  		scale = Math.random() * 3.0 - 1.5;
  		fixedRadius = Math.random() * 2.0 + 0.1;
  	}
  	if (Math.random() < 0.2) minRadius = Math.random() * 5.0;
  	else minRadius = fixedRadius + Math.random() - 0.25;
  	if (Math.random() < 0.3) rotation = Math.random() * 360.0 - 180.0;
  	else rotation = Math.random() * 180.0 - 90.0;
  	juliaMode = (int) (Math.random() * 2);
  	juliaX = Math.random() * 5.0 - 2.5;
  	juliaY = Math.random() * 5.0 - 2.5;
  	coloringMode = (int) (Math.random() * 3);
  	colorSpeed = Math.random();
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }
}
