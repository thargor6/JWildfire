/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2025 Andreas Maschke
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

import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XForm;



public class BoxfoldFunc extends VariationFunc {

  private static final long serialVersionUID = 2L;

  // --- Parameter Names ---
  private static final String PARAM_FOLD_LIMIT = "foldLimit";
  private static final String PARAM_ROTATE_X = "rotateX";
  private static final String PARAM_ROTATE_Y = "rotateY";
  private static final String PARAM_ROTATE_Z = "rotateZ";

  // --- Parameter Variables ---
  private double pFoldLimit = 1.0; 
  private double pRotateX = 0.0; // Degrees
  private double pRotateY = 0.0; // Degrees
  private double pRotateZ = 0.0; // Degrees

  // --- Manual Math Helpers (Workaround for reported missing library methods) ---
  private static XYZPoint _multiply(XYZPoint p, double factor) { 
    XYZPoint result = new XYZPoint();
     if (p == null || Double.isNaN(factor) || Double.isInfinite(factor) || 
         Double.isNaN(p.x) || Double.isNaN(p.y) || Double.isNaN(p.z) ||
         Double.isInfinite(p.x) || Double.isInfinite(p.y) || Double.isInfinite(p.z)) {
         result.x = Double.NaN; result.y = Double.NaN; result.z = Double.NaN;
     } else {
        result.x = p.x * factor;
        result.y = p.y * factor;
        result.z = p.z * factor;
     }
    return result;
  }

  private static XYZPoint _negate(XYZPoint p) { 
      XYZPoint result = new XYZPoint();
       if (p == null) {
           result.x = Double.NaN; result.y = Double.NaN; result.z = Double.NaN;
       } else {
          result.x = -p.x;
          result.y = -p.y;
          result.z = -p.z;
       }
      return result;
  }

  private static double _limitValueDouble(double value, double min, double max) { 
      if(Double.isNaN(value)) return min; 
      double actualMin = Math.min(min, max); double actualMax = Math.max(min, max);
      double minnedValue = Math.min(actualMax, value); 
      return Math.max(actualMin, minnedValue);        
  }

  @Override
  public String getName() {
    return "boxfold"; 
  }

  @Override
  public String[] getParameterNames() {
      return new String[]{ 
          PARAM_FOLD_LIMIT, 
          PARAM_ROTATE_X, PARAM_ROTATE_Y, PARAM_ROTATE_Z 
      };
  }

  @Override
  public Object[] getParameterValues() {
      return new Object[]{ 
          pFoldLimit,
          pRotateX, pRotateY, pRotateZ
      };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_FOLD_LIMIT.equalsIgnoreCase(pName))
        pFoldLimit = _limitValueDouble(pValue, 1e-9, 10000.0); 
    else if (PARAM_ROTATE_X.equalsIgnoreCase(pName))
        pRotateX = pValue;
    else if (PARAM_ROTATE_Y.equalsIgnoreCase(pName))
    		pRotateY = pValue;
    else if (PARAM_ROTATE_Z.equalsIgnoreCase(pName))
        pRotateZ = pValue;
    else 
    		throw new IllegalArgumentException(pName);
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    
    double F = this.pFoldLimit; 

    // Create points for rotation angles (using manual instantiation workaround)
    XYZPoint anglesRad = new XYZPoint();
    anglesRad.x = Math.toRadians(pRotateX);
    anglesRad.y = Math.toRadians(pRotateY);
    anglesRad.z = Math.toRadians(pRotateZ);
    
    XYZPoint negatedAnglesRad = _negate(anglesRad); // Use helper

    // 1. Rotate input point to local coordinate system
    XYZPoint pLocal = rotatePoint(pAffineTP, negatedAnglesRad);

    // 2. Perform axis-aligned boxfold in local coordinates
    double localX = pLocal.x;
    double localY = pLocal.y;
    double localZ = pLocal.z;

    double foldedLocalX = localX; 
    if (localX > F) foldedLocalX = 2.0 * F - localX;
    else if (localX < -F) foldedLocalX = -2.0 * F - localX;

    double foldedLocalY = localY; 
    if (localY > F) foldedLocalY = 2.0 * F - localY;
    else if (localY < -F) foldedLocalY = -2.0 * F - localY;

    double foldedLocalZ = localZ; 
    if (localZ > F) foldedLocalZ = 2.0 * F - localZ;
    else if (localZ < -F) foldedLocalZ = -2.0 * F - localZ;

    // Create the folded point in local coordinates
    XYZPoint pLocalFolded = new XYZPoint();
    pLocalFolded.x = foldedLocalX;
    pLocalFolded.y = foldedLocalY;
    pLocalFolded.z = foldedLocalZ;
    
    // 3. Rotate folded point back to world coordinate system
    XYZPoint pWorldFolded = rotatePoint(pLocalFolded, anglesRad);

    // 4. Set output (JWildfire handles pAmount blending)
    pVarTP.x += pWorldFolded.x * pAmount;
    pVarTP.y += pWorldFolded.y * pAmount;
    pVarTP.z += pWorldFolded.z * pAmount;
  }


  // Uses manual XYZPoint creation workaround
  private XYZPoint rotatePoint(XYZPoint pInput, XYZPoint anglesRad) {
    XYZPoint p = new XYZPoint(); 
    // Check if input is null before accessing fields
     if (pInput == null || anglesRad == null) {
         p.x = Double.NaN; p.y = Double.NaN; p.z = Double.NaN;
         return p;
     }
    p.x = pInput.x; p.y = pInput.y; p.z = pInput.z;

    double rotZ = anglesRad.z;
    double rotY = anglesRad.y;
    double rotX = anglesRad.x;

    double cosVal, sinVal;
    double tempX, tempY, tempZ;

    // Z-axis rotation
    if (rotZ != 0) {
        cosVal = Math.cos(rotZ); sinVal = Math.sin(rotZ);
        tempX = p.x * cosVal - p.y * sinVal;
        tempY = p.x * sinVal + p.y * cosVal;
        p.x = tempX; p.y = tempY;
    }

    // Y-axis rotation
    if (rotY != 0) {
        cosVal = Math.cos(rotY); sinVal = Math.sin(rotY);
        tempX = p.x * cosVal + p.z * sinVal;
        tempZ = -p.x * sinVal + p.z * cosVal;
        p.x = tempX; p.z = tempZ;
    }
    
    // X-axis rotation
    if (rotX != 0) {
        cosVal = Math.cos(rotX); sinVal = Math.sin(rotX);
        tempY = p.y * cosVal - p.z * sinVal;
        tempZ = p.y * sinVal + p.z * cosVal;
        p.y = tempY; p.z = tempZ;
    }
    
    return p; 
  }
  
  @Override
  public void randomize() {
  	pFoldLimit = Math.random() * 5.0 + 0.05;
  	pRotateX = Math.random() * 360.0 - 180.0;
  	pRotateY = Math.random() * 360.0 - 180.0;
  	pRotateZ = Math.random() * 360.0 - 180.0;
  }
  
  public void mutate(double pAmount) {
    switch ((int) (Math.random() * 4)) {
    case 0:
      pFoldLimit += mutateStep(pFoldLimit, pAmount);
      break;
    case 1:
    	pRotateX += 1.0;
    	break;
    case 2:
    	pRotateY += 1.0;
    	break;
    case 3:
    	pRotateZ += 1.0;
    	break;
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D};
  }

}