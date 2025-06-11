
/*
 * JWildfire - an image and animation processor written in Java
 * Copyright (C) 1995-2025 Andreas Maschke

 * This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.io.Serializable;

import static org.jwildfire.base.mathlib.MathLib.*;

public class GlynnSim2BFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_THICKNESS = "thickness";
  private static final String PARAM_CONTRAST = "contrast";
  private static final String PARAM_POW = "pow";
  private static final String PARAM_PHI1 = "phi1";
  private static final String PARAM_PHI2 = "phi2";
  private static final String PARAM_ROT_X = "rotX";
  private static final String PARAM_ROT_Y = "rotY";
  private static final String PARAM_ROT_Z = "rotZ";
  private static final String PARAM_OFFSET_Z = "offsetZ";
  private static final String PARAM_SCALE_X = "scaleX";
  private static final String PARAM_SCALE_Y = "scaleY";
  private static final String PARAM_SCALE_Z = "scaleZ";
  private static final String PARAM_SHEAR_XY = "shearXY";
  private static final String PARAM_SHEAR_XZ = "shearXZ";
  private static final String PARAM_SHEAR_YX = "shearYX";
  private static final String PARAM_SHEAR_YZ = "shearYZ";
  private static final String PARAM_SHEAR_ZX = "shearZX";
  private static final String PARAM_SHEAR_ZY = "shearZY";
  private static final String PARAM_CIRCLE_ROT_X = "circleRotX";
  private static final String PARAM_CIRCLE_ROT_Y = "circleRotY";
  private static final String PARAM_CIRCLE_THICKNESS_Z = "circleThicknessZ";

  // New Coloring Parameters (adjusted to output a hue/palette index)
  private static final String PARAM_COLOR_MODE = "colorMode"; // 0: Normal, 1: Z-Depth, 2: Random Hue, 3: Blend Z+Random Hue, 4: Fixed Hue
  private static final String PARAM_FIXED_HUE = "fixedHue"; // For mode 4: a specific hue value (0.0-1.0)
  private static final String PARAM_Z_HUE_SCALE = "zHueScale"; // Scale for Z-depth hue mapping
  private static final String PARAM_RANDOM_HUE_AMOUNT = "randomHueAmount"; // Influence of random hue


  private static final String[] paramNames = {PARAM_RADIUS, PARAM_THICKNESS, PARAM_CONTRAST, PARAM_POW, PARAM_PHI1, PARAM_PHI2,
      PARAM_ROT_X, PARAM_ROT_Y, PARAM_ROT_Z, PARAM_OFFSET_Z,
      PARAM_SCALE_X, PARAM_SCALE_Y, PARAM_SCALE_Z,
      PARAM_SHEAR_XY, PARAM_SHEAR_XZ, PARAM_SHEAR_YX, PARAM_SHEAR_YZ, PARAM_SHEAR_ZX, PARAM_SHEAR_ZY,
      PARAM_CIRCLE_ROT_X, PARAM_CIRCLE_ROT_Y, PARAM_CIRCLE_THICKNESS_Z,
      PARAM_COLOR_MODE, PARAM_FIXED_HUE, PARAM_Z_HUE_SCALE, PARAM_RANDOM_HUE_AMOUNT};

  private double radius = 1.0;
  private double thickness = 0.1;
  private double contrast = 0.5;
  private double pow = 1.5;
  private double phi1 = 110.0;
  private double phi2 = 150.0;
  private double rotX = 0.0;
  private double rotY = 0.0;
  private double rotZ = 0.0;
  private double offsetZ = 0.0;
  private double scaleX = 1.0;
  private double scaleY = 1.0;
  private double scaleZ = 1.0;
  private double shearXY = 0.0;
  private double shearXZ = 0.0;
  private double shearYX = 0.0;
  private double shearYZ = 0.0;
  private double shearZX = 0.0;
  private double shearZY = 0.0;
  private double circleRotX = 0.0;
  private double circleRotY = 0.0;
  private double circleThicknessZ = 0.0;

  // Default values for coloring (now focused on hue/palette index)
  private int colorMode = 0; // Default to 0: Normal JWildfire coloring
  private double fixedHue = 0.0; // Default hue (red on many palettes)
  private double zHueScale = 1.0; // How much Z affects hue mapping
  private double randomHueAmount = 0.0; // 0.0 for no random influence, 1.0 for full


  private static class Point implements Serializable {
    private static final long serialVersionUID = 1L;
    private double x, y, z;
  }

  private void circle(FlameTransformationContext pContext, Point p) {
    double r = this.radius + this.thickness - this._gamma * pContext.random();
    double Phi = this._phi10 + this._delta * pContext.random();
    double sinPhi = sin(Phi);
    double cosPhi = cos(Phi);

    double circX = r * cosPhi;
    double circY = r * sinPhi;
    double circZ = 0.0;

    circZ += (pContext.random() - 0.5) * 2.0 * circleThicknessZ;

    double sinCircleRotX = sin(this._circleRotXRad);
    double cosCircleRotX = cos(this._circleRotXRad);
    double sinCircleRotY = sin(this._circleRotYRad);
    double cosCircleRotY = cos(this._circleRotYRad);

    double tempY = circY * cosCircleRotX - circZ * sinCircleRotX;
    double tempZ = circY * sinCircleRotX + circZ * cosCircleRotX;
    circY = tempY;
    circZ = tempZ;

    double tempX = circX * cosCircleRotY + circZ * sinCircleRotY;
    tempZ = -circX * sinCircleRotY + circZ * cosCircleRotY;
    circX = tempX;
    circZ = tempZ;

    p.x = circX;
    p.y = circY;
    p.z = circZ;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* GlynnSim2 by eralex61, http://eralex61.deviantart.com/art/GlynnSim-plugin-112621621 */
    double x = pAffineTP.x;
    double y = pAffineTP.y;
    double z = pAffineTP.z;

    double r = sqrt(x * x + y * y);
    double Alpha = this.radius / r;

    if (r < this.radius) {
      circle(pContext, toolPoint);
      x = toolPoint.x;
      y = toolPoint.y;
      z = toolPoint.z;
    } else {
      if (pContext.random() > this.contrast * pow(Alpha, this._absPow)) {
        // x, y, z remain as they are
      } else {
        x = Alpha * Alpha * x;
        y = Alpha * Alpha * y;
        // z remains as it is
      }
    }

    // Apply scaling
    x *= scaleX;
    y *= scaleY;
    z *= scaleZ;

    // Apply shearing
    double tempX = x + shearYX * y + shearZX * z;
    double tempY = y + shearXY * x + shearZY * z;
    double tempZ = z + shearXZ * x + shearYZ * y;

    x = tempX;
    y = tempY;
    z = tempZ;

    // Apply main 3D rotations
    double sinRotX = sin(this._rotXRad);
    double cosRotX = cos(this._rotXRad);
    double sinRotY = sin(this._rotYRad);
    double cosRotY = cos(this._rotYRad);
    double sinRotZ = sin(this._rotZRad);
    double cosRotZ = cos(this._rotZRad);

    double rotatedY = y * cosRotX - z * sinRotX;
    double rotatedZ = y * sinRotX + z * cosRotX;
    y = rotatedY;
    z = rotatedZ;

    double rotatedX = x * cosRotY + z * sinRotY;
    rotatedZ = -x * sinRotY + z * cosRotY;
    x = rotatedX;
    z = rotatedZ;

    rotatedX = x * cosRotZ - y * sinRotZ;
    rotatedY = x * sinRotZ + y * cosRotZ;
    x = rotatedX;
    y = rotatedY;

    // Apply Z-offset
    z += this.offsetZ;

    pVarTP.x += pAmount * x;
    pVarTP.y += pAmount * y;
    pVarTP.z += pAmount * z;

    // --- Coloring Logic (using pVarTP.color as hue/palette index) ---
    if (colorMode == 0) { // Normal JWildfire coloring (default, use palette with iteration count)
      // Do nothing, let JWildfire's default color handling apply (based on iteration counts, usually)
      // This means pVarTP.color remains whatever it was before the variation (likely 0.0)
    } else {
      double hue = 0.0; // Initialize hue

      if (colorMode == 1 || colorMode == 3) { // Z-Depth coloring (map Z to hue)
        // Normalize Z to a 0-1 range for hue mapping.
        // The *zHueScale* parameter will stretch/compress this mapping.
        // fmod(..., 1.0) ensures hue wraps around 0-1.
        hue = fmod(z * zHueScale, 1.0);
        if (hue < 0.0) hue += 1.0; // Ensure positive hue
      }
      
      if (colorMode == 2 || colorMode == 3) { // Random Hue
        double random_hue_shift = (pContext.random() - 0.5) * 2.0 * randomHueAmount; // -randomHueAmount to +randomHueAmount
        hue = fmod(hue + random_hue_shift, 1.0);
        if (hue < 0.0) hue += 1.0; // Ensure positive hue
      }

      if (colorMode == 4) { // Fixed Hue
          hue = fixedHue;
      }

      // Assign the calculated hue to pVarTP.color
      pVarTP.color = hue;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{radius, thickness, contrast, pow, phi1, phi2, rotX, rotY, rotZ, offsetZ,
        scaleX, scaleY, scaleZ, shearXY, shearXZ, shearYX, shearYZ, shearZX, shearZY,
        circleRotX, circleRotY, circleThicknessZ,
        (double) colorMode, fixedHue, zHueScale, randomHueAmount};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_RADIUS.equalsIgnoreCase(pName))
      radius = pValue;
    else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
      thickness = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_CONTRAST.equalsIgnoreCase(pName))
      contrast = limitVal(pValue, 0.0, 1.0);
    else if (PARAM_POW.equalsIgnoreCase(pName))
      pow = pValue;
    else if (PARAM_PHI1.equalsIgnoreCase(pName))
      phi1 = pValue;
    else if (PARAM_PHI2.equalsIgnoreCase(pName))
      phi2 = pValue;
    else if (PARAM_ROT_X.equalsIgnoreCase(pName))
      rotX = pValue;
    else if (PARAM_ROT_Y.equalsIgnoreCase(pName))
      rotY = pValue;
    else if (PARAM_ROT_Z.equalsIgnoreCase(pName))
      rotZ = pValue;
    else if (PARAM_OFFSET_Z.equalsIgnoreCase(pName))
      offsetZ = pValue;
    else if (PARAM_SCALE_X.equalsIgnoreCase(pName))
      scaleX = pValue;
    else if (PARAM_SCALE_Y.equalsIgnoreCase(pName))
      scaleY = pValue;
    else if (PARAM_SCALE_Z.equalsIgnoreCase(pName))
      scaleZ = pValue;
    else if (PARAM_SHEAR_XY.equalsIgnoreCase(pName))
      shearXY = pValue;
    else if (PARAM_SHEAR_XZ.equalsIgnoreCase(pName))
      shearXZ = pValue;
    else if (PARAM_SHEAR_YX.equalsIgnoreCase(pName))
      shearYX = pValue;
    else if (PARAM_SHEAR_YZ.equalsIgnoreCase(pName))
      shearYZ = pValue;
    else if (PARAM_SHEAR_ZX.equalsIgnoreCase(pName))
      shearZX = pValue;
    else if (PARAM_SHEAR_ZY.equalsIgnoreCase(pName))
      shearZY = pValue;
    else if (PARAM_CIRCLE_ROT_X.equalsIgnoreCase(pName))
      circleRotX = pValue;
    else if (PARAM_CIRCLE_ROT_Y.equalsIgnoreCase(pName))
      circleRotY = pValue;
    else if (PARAM_CIRCLE_THICKNESS_Z.equalsIgnoreCase(pName))
      circleThicknessZ = pValue;
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName))
      colorMode = (int) pValue;
    else if (PARAM_FIXED_HUE.equalsIgnoreCase(pName))
      fixedHue = limitVal(pValue, 0.0, 1.0); // Hue is 0.0-1.0
    else if (PARAM_Z_HUE_SCALE.equalsIgnoreCase(pName))
      zHueScale = pValue;
    else if (PARAM_RANDOM_HUE_AMOUNT.equalsIgnoreCase(pName))
      randomHueAmount = limitVal(pValue, 0.0, 1.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "glynnSim2B";
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"GlynnSim2_radius", "GlynnSim2_thickness", "GlynnSim2_contrast", "GlynnSim2_pow", "GlynnSim2_Phi1", "GlynnSim2_Phi2",
        "GlynnSim2_rotX", "GlynnSim2_rotY", "GlynnSim2_rotZ", "GlynnSim2_offsetZ",
        "GlynnSim2_scaleX", "GlynnSim2_scaleY", "GlynnSim2_scaleZ",
        "GlynnSim2_shearXY", "GlynnSim2_shearXZ", "GlynnSim2_shearYX", "GlynnSim2_shearYZ", "GlynnSim2_shearZX", "GlynnSim2_shearZY",
        "GlynnSim2_circleRotX", "GlynnSim2_circleRotY", "GlynnSim2_circleThicknessZ",
        "GlynnSim2_colorMode", "GlynnSim2_fixedHue", "GlynnSim2_zHueScale", "GlynnSim2_randomHueAmount"};
  }

  private Point toolPoint = new Point();
  private double _phi10, _phi20, _gamma, _delta, _absPow;
  private double _rotXRad, _rotYRad, _rotZRad;
  private double _circleRotXRad, _circleRotYRad;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    this._phi10 = M_PI * this.phi1 / 180.0;
    this._phi20 = M_PI * this.phi2 / 180.0;
    this._gamma = this.thickness * (2.0 * this.radius + this.thickness) / (this.radius + this.thickness);
    this._delta = this._phi20 - this._phi10;
    this._absPow = fabs(this.pow);

    this._rotXRad = M_PI * this.rotX / 180.0;
    this._rotYRad = M_PI * this.rotY / 180.0;
    this._rotZRad = M_PI * this.rotZ / 180.0;

    this._circleRotXRad = M_PI * this.circleRotX / 180.0;
    this._circleRotYRad = M_PI * this.circleRotY / 180.0;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SIMULATION};
  }


}
