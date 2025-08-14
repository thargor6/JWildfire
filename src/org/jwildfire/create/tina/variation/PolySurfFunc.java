
/*
 * JWildfire - an image and animation processor written in Java
 * Copyright (C) 1995-2025 Andreas Maschke
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
 */
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class PolySurfFunc extends VariationFunc {
  private static final long serialVersionUID = 2L;

  // Parameters
  private static final String PARAM_C = "c";
  private static final String PARAM_MIRROR_X = "mirrorX";
  private static final String PARAM_MIRROR_Y = "mirrorY";
  private static final String PARAM_MIRROR_Z = "mirrorZ";
  private static final String PARAM_JULIA = "julia";
  private static final String PARAM_JULIA_X = "juliaX";
  private static final String PARAM_JULIA_Y = "juliaY";
  private static final String PARAM_JULIA_Z = "juliaZ";
  private static final String PARAM_ENABLE_FOLD = "enableFold";
  private static final String PARAM_FOLD_X = "foldX";
  private static final String PARAM_FOLD_Y = "foldY";
  private static final String PARAM_ROT_X = "rotX";
  private static final String PARAM_ROT_Y = "rotY";
  private static final String PARAM_ROT_Z = "rotZ";
  private static final String PARAM_ENABLE_OFFSET = "enableOffset";
  private static final String PARAM_OFFSET_X = "offsetX";
  private static final String PARAM_OFFSET_Y = "offsetY";
  private static final String PARAM_OFFSET_Z = "offsetZ";
  private static final String PARAM_ENABLE_INVERT = "enableInvert";
  private static final String PARAM_INVERT_RADIUS = "invertRadius";
  private static final String PARAM_COLOR_MODE = "colorMode";
  private static final String PARAM_COLOR_SPEED = "colorSpeed";
  
  private static final String RESSOURCE_DESCRIPTION = "description";

  private static final String[] paramNames = {PARAM_C, PARAM_MIRROR_X, PARAM_MIRROR_Y, PARAM_MIRROR_Z, PARAM_JULIA, PARAM_JULIA_X, PARAM_JULIA_Y, PARAM_JULIA_Z, PARAM_ENABLE_FOLD, PARAM_FOLD_X, PARAM_FOLD_Y, PARAM_ROT_X, PARAM_ROT_Y, PARAM_ROT_Z, PARAM_ENABLE_OFFSET, PARAM_OFFSET_X, PARAM_OFFSET_Y, PARAM_OFFSET_Z, PARAM_ENABLE_INVERT, PARAM_INVERT_RADIUS, PARAM_COLOR_MODE, PARAM_COLOR_SPEED};
  private static final String[] ressourceNames = {RESSOURCE_DESCRIPTION};

  // Defaults
  private double c = 0.3;
  private int mirrorX = 1;
  private int mirrorY = 1;
  private int mirrorZ = 0;
  private int julia = 0;
  private double juliaX = 0.0;
  private double juliaY = 0.0;
  private double juliaZ = 0.0;
  private int enableFold = 0;
  private double foldX = 1.0;
  private double foldY = 1.0;
  private double rotX = 0.0;
  private double rotY = 0.0;
  private double rotZ = 0.0;
  private int enableOffset = 1;
  private double offsetX = 1.0;
  private double offsetY = 1.0;
  private double offsetZ = 0.0;
  private int enableInvert = 0;
  private double invertRadius = 1.0;
  private int colorMode = 0;
  private double colorSpeed = 1.0;

  private String description = "org.jwildfire.create.tina.variation.reference.ReferenceFile polySurf.txt";

  private double sqr(double pVal) {
    return pVal * pVal;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // 1. Determine the original quadrant.
    double sign_x = pAffineTP.x >= 0 ? 1.0 : -1.0;
    double sign_y = pAffineTP.y >= 0 ? 1.0 : -1.0;

    // 2. Apply Julia offset if enabled.
    double x_calc = pAffineTP.x;
    double y_calc = pAffineTP.y;
    double z_calc = pAffineTP.z;
    if (julia > 0) {
      x_calc += juliaX;
      y_calc += juliaY;
      z_calc += juliaZ;
    }
    
    // 3. Apply folding logic if enabled.
    if (enableFold > 0) {
      x_calc = fabs(x_calc + foldX) - fabs(x_calc - foldX) - x_calc;
      y_calc = fabs(y_calc + foldY) - fabs(y_calc - foldY) - y_calc;
    }
    
    // 4. Apply pre-scaling offset if enabled.
    if (enableOffset > 0) {
        x_calc += offsetX;
        y_calc += offsetY;
        z_calc += offsetZ;
    }

    // 5. Use absolute values for the main calculation.
    double x_abs = fabs(x_calc);
    double y_abs = fabs(y_calc);
    
    // 6. Calculate the curl transformation.
    double r2 = sqr(x_abs) + sqr(y_abs) + sqr(z_calc);
    double r = pAmount / (r2 * c2 + c2z * z_calc + 1);

    // 7. Calculate the transformation's effect (the "delta" vector).
    double delta_x = r * x_abs;
    double delta_y = r * y_abs;
    double delta_z = r * (z_calc + c * r2);
    
    // 8. Apply rotation to the delta vector.
    if(rotX != 0.0 || rotY != 0.0 || rotZ != 0.0) {
      double radX = Math.toRadians(rotX);
      double radY = Math.toRadians(rotY);
      double radZ = Math.toRadians(rotZ);
      
      double cosX = cos(radX), sinX = sin(radX);
      double cosY = cos(radY), sinY = sin(radY);
      double cosZ = cos(radZ), sinZ = sin(radZ);

      double temp_x, temp_y, temp_z;

      // Rotate around X
      temp_y = delta_y * cosX - delta_z * sinX;
      temp_z = delta_y * sinX + delta_z * cosX;
      delta_y = temp_y;
      delta_z = temp_z;

      // Rotate around Y
      temp_x = delta_x * cosY + delta_z * sinY;
      temp_z = -delta_x * sinY + delta_z * cosY;
      delta_x = temp_x;
      delta_z = temp_z;

      // Rotate around Z
      temp_x = delta_x * cosZ - delta_y * sinZ;
      temp_y = delta_x * sinZ + delta_y * cosZ;
      delta_x = temp_x;
      delta_y = temp_y;
    }

    // 9. Apply spherical inversion if enabled.
    if (enableInvert > 0) {
      double r2_inv = sqr(delta_x) + sqr(delta_y) + sqr(delta_z);
      if (r2_inv > 1.0E-9) { // Avoid division by zero
        double factor = sqr(invertRadius) / r2_inv;
        delta_x *= factor;
        delta_y *= factor;
        delta_z *= factor;
      }
    }

    // 10. Apply the final delta with mirroring.
    pVarTP.x += (mirrorX > 0 && pContext.random() < 0.5 ? -1.0 : 1.0) * sign_x * delta_x;
    pVarTP.y += (mirrorY > 0 && pContext.random() < 0.5 ? -1.0 : 1.0) * sign_y * delta_y;
    pVarTP.z += (mirrorZ > 0 && pContext.random() < 0.5 ? -1.0 : 1.0) * delta_z;

    // 11. Apply coloring
    if (colorMode > 0) {
      double calculatedColor = 0.0;
      switch(colorMode) {
        case 1: // Distance from origin
          calculatedColor = sqrt(sqr(delta_x) + sqr(delta_y) + sqr(delta_z)); 
          break;
        case 2: // Angle in XY plane
          calculatedColor = (atan2(delta_y, delta_x) / (2.0 * Math.PI)) + 0.5;
          break;
        case 3: // Z-height
          calculatedColor = delta_z;
          break;
      }
      calculatedColor *= colorSpeed;
      calculatedColor = fmod(fmod(calculatedColor, 1.0) + 1.0, 1.0);
      pVarTP.color += (calculatedColor - pVarTP.color) * pAmount;
    }
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{c, mirrorX, mirrorY, mirrorZ, julia, juliaX, juliaY, juliaZ, enableFold, foldX, foldY, rotX, rotY, rotZ, enableOffset, offsetX, offsetY, offsetZ, enableInvert, invertRadius, colorMode, colorSpeed};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_MIRROR_X.equalsIgnoreCase(pName))
      mirrorX = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_MIRROR_Y.equalsIgnoreCase(pName))
      mirrorY = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_MIRROR_Z.equalsIgnoreCase(pName))
      mirrorZ = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_JULIA.equalsIgnoreCase(pName))
      julia = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_JULIA_X.equalsIgnoreCase(pName))
      juliaX = pValue;
    else if (PARAM_JULIA_Y.equalsIgnoreCase(pName))
      juliaY = pValue;
    else if (PARAM_JULIA_Z.equalsIgnoreCase(pName))
      juliaZ = pValue;
    else if (PARAM_ENABLE_FOLD.equalsIgnoreCase(pName))
      enableFold = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_FOLD_X.equalsIgnoreCase(pName))
      foldX = pValue;
    else if (PARAM_FOLD_Y.equalsIgnoreCase(pName))
      foldY = pValue;
    else if (PARAM_ROT_X.equalsIgnoreCase(pName))
      rotX = pValue;
    else if (PARAM_ROT_Y.equalsIgnoreCase(pName))
      rotY = pValue;
    else if (PARAM_ROT_Z.equalsIgnoreCase(pName))
      rotZ = pValue;
    else if (PARAM_ENABLE_OFFSET.equalsIgnoreCase(pName))
        enableOffset = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_OFFSET_X.equalsIgnoreCase(pName))
        offsetX = pValue;
    else if (PARAM_OFFSET_Y.equalsIgnoreCase(pName))
        offsetY = pValue;
    else if (PARAM_OFFSET_Z.equalsIgnoreCase(pName))
        offsetZ = pValue;
    else if (PARAM_ENABLE_INVERT.equalsIgnoreCase(pName))
        enableInvert = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else if (PARAM_INVERT_RADIUS.equalsIgnoreCase(pName))
        invertRadius = pValue;
    else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName))
        colorMode = limitIntVal(Tools.FTOI(pValue), 0, 2);
    else if (PARAM_COLOR_SPEED.equalsIgnoreCase(pName))
        colorSpeed = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String[] getRessourceNames() {
    return ressourceNames;
  }

  @Override
  public byte[][] getRessourceValues() {
    return new byte[][] {description.getBytes()};
  }

  @Override
  public RessourceType getRessourceType(String pName) {
    if (RESSOURCE_DESCRIPTION.equalsIgnoreCase(pName)) {
      return RessourceType.REFERENCE;
    }
    else throw new IllegalArgumentException(pName);
  }
  
  @Override
  public String getName() {
    return "polySurf";
  }

  // Pre-calculated constants
  private double c2z, c2;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    c2z = 2 * c;
    c2 = sqr(c);
  }
  
  @Override
  public void randomize() {
  	if (Math.random() < 0.3) c = Math.random() * 1.0 + 0.1;
  	else c = Math.random() * 0.4 + 0.1;
  	mirrorX = (int) (Math.random() * 2);
  	mirrorY = (int) (Math.random() * 2);
  	mirrorZ = (int) (Math.random() * 2);
  	julia = (int) (Math.random() * 2);
  	juliaX = Math.random() * 4.0 - 2.0;
  	juliaY = Math.random() * 4.0 - 2.0;
  	juliaZ = Math.random() * 4.0 - 2.0;
  	enableFold = (int) (Math.random() * 2);
  	foldX = Math.random() * 6.0 - 3.0;
  	foldY = Math.random() * 6.0 - 3.0;
  	rotX = Math.random() * 360.0 - 180.0;
  	rotY = Math.random() * 360.0 - 180.0;
  	rotZ = Math.random() * 360.0 - 180.0;
  	enableOffset = (int) (Math.random() * 2);
  	offsetX = Math.random() * 6.0 - 3.0;
  	offsetY = Math.random() * 6.0 - 3.0;
  	offsetZ = Math.random() * 6.0 - 3.0;
  	enableInvert = (Math.random() < 0.7) ? 0 : 1;
  	invertRadius = Math.random() * 3.0 + 0.25;
  	colorMode = (int) (Math.random() * 4);
  	colorSpeed = Math.random() * 2.0;
 }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D};
  }

}
