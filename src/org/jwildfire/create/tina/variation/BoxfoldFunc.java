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

  private static final long serialVersionUID = 2L; // Increment version ID

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
  private static XYZPoint _multiply(XYZPoint p, double factor) { /* ... Copy from previous versions ... */ 
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

  private static XYZPoint _negate(XYZPoint p) { /* ... Copy from previous versions ... */
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

  private static double _limitValueDouble(double value, double min, double max) { /* ... Copy from previous versions ... */
      if(Double.isNaN(value)) return min; 
      double actualMin = Math.min(min, max); double actualMax = Math.max(min, max);
      double minnedValue = Math.min(actualMax, value); 
      return Math.max(actualMin, minnedValue);        
  }

  // Constructor
  public BoxfoldFunc() {
    System.err.println("INFO: BoxfoldFunc (with rotation): Constructor called."); 
  }

  @Override
  public String getName() {
    // You could rename this to "boxfoldRot" but keeping "boxfold" is fine
    return "boxfold"; 
  }

  @Override
  public String[] getParameterNames() {
      System.err.println("INFO: BoxfoldFunc (with rotation): getParameterNames() called."); 
      // Added rotation parameters
      return new String[]{ 
          PARAM_FOLD_LIMIT, 
          PARAM_ROTATE_X, PARAM_ROTATE_Y, PARAM_ROTATE_Z 
      };
  }

  @Override
  public Object[] getParameterValues() {
      System.err.println("INFO: BoxfoldFunc (with rotation): getParameterValues() called."); 
      // Added rotation parameters
      return new Object[]{ 
          pFoldLimit,
          pRotateX, pRotateY, pRotateZ
      };
  }

  @Override
  public void setParameter(String pName, double pValue) {
    System.err.println("INFO: BoxfoldFunc (with rotation): setParameter received: " + pName + " = " + pValue); 
    try {
        if (PARAM_FOLD_LIMIT.equalsIgnoreCase(pName)) {
             if(Double.isNaN(pValue) || Double.isInfinite(pValue) || pValue <= 1e-9) { 
                 System.err.println("WARN: Invalid foldLimit value: " + pValue + ". Setting to 1.0");
                 pFoldLimit = 1.0;
             } else { 
                 pFoldLimit = _limitValueDouble(pValue, 1e-9, 10000.0); 
                 System.err.println("  -> pFoldLimit set to: " + pFoldLimit); 
             }
        // Handle rotation parameters
        } else if (PARAM_ROTATE_X.equalsIgnoreCase(pName)) {
             if(Double.isNaN(pValue) || Double.isInfinite(pValue)) { System.err.println("WARN: Invalid rotateX value: " + pValue); }
             else { pRotateX = pValue; System.err.println("  -> pRotateX set to: " + pRotateX); } // No specific limits on angle needed
        } else if (PARAM_ROTATE_Y.equalsIgnoreCase(pName)) {
             if(Double.isNaN(pValue) || Double.isInfinite(pValue)) { System.err.println("WARN: Invalid rotateY value: " + pValue); }
             else { pRotateY = pValue; System.err.println("  -> pRotateY set to: " + pRotateY); }
        } else if (PARAM_ROTATE_Z.equalsIgnoreCase(pName)) {
             if(Double.isNaN(pValue) || Double.isInfinite(pValue)) { System.err.println("WARN: Invalid rotateZ value: " + pValue); }
             else { pRotateZ = pValue; System.err.println("  -> pRotateZ set to: " + pRotateZ); }
        } else {
          System.err.println("WARN: BoxfoldFunc (with rotation): Unhandled parameter name in setParameter: " + pName);
        }
    } catch (Exception e) {
        System.err.println("ERROR: BoxfoldFunc (with rotation): Exception in setParameter for " + pName + " = " + pValue);
        e.printStackTrace(System.err); 
    }
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

    // Handle NaN/Inf after first rotation
     if (pLocal == null || Double.isNaN(pLocal.x) || Double.isNaN(pLocal.y) || Double.isNaN(pLocal.z) ||
         Double.isInfinite(pLocal.x) || Double.isInfinite(pLocal.y) || Double.isInfinite(pLocal.z)) {
         System.err.println("WARN: Boxfold input became NaN/Inf after initial rotation. Outputting (0,0,0).");
         pVarTP.x = 0.0; pVarTP.y = 0.0; pVarTP.z = 0.0;
         return;
     }

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
    
    // Check for NaN/Inf after folding (less likely but possible if F or input was extreme)
     if (Double.isNaN(foldedLocalX) || Double.isNaN(foldedLocalY) || Double.isNaN(foldedLocalZ) ||
         Double.isInfinite(foldedLocalX) || Double.isInfinite(foldedLocalY) || Double.isInfinite(foldedLocalZ)) {
         System.err.println("WARN: Boxfold calculation resulted in NaN/Inf. Outputting (0,0,0).");
         pVarTP.x = 0.0; pVarTP.y = 0.0; pVarTP.z = 0.0;
         return;
     }

    // 3. Rotate folded point back to world coordinate system
    XYZPoint pWorldFolded = rotatePoint(pLocalFolded, anglesRad);

    // Check for NaN/Inf after final rotation
     if (pWorldFolded == null || Double.isNaN(pWorldFolded.x) || Double.isNaN(pWorldFolded.y) || Double.isNaN(pWorldFolded.z) ||
         Double.isInfinite(pWorldFolded.x) || Double.isInfinite(pWorldFolded.y) || Double.isInfinite(pWorldFolded.z)) {
         System.err.println("WARN: Boxfold became NaN/Inf after final rotation. Outputting (0,0,0).");
         pVarTP.x = 0.0; pVarTP.y = 0.0; pVarTP.z = 0.0;
         return;
     }

    // 4. Set output (JWildfire handles pAmount blending)
    pVarTP.x += pWorldFolded.x * pAmount;
    pVarTP.y += pWorldFolded.y * pAmount;
    pVarTP.z += pWorldFolded.z * pAmount;
  }


  // --- Re-introduced rotatePoint Helper ---
  // Uses manual XYZPoint creation workaround
  private XYZPoint rotatePoint(XYZPoint pInput, XYZPoint anglesRad) {
    XYZPoint p = new XYZPoint(); 
    // Check if input is null before accessing fields
     if (pInput == null || anglesRad == null) {
         System.err.println("ERROR: null input to rotatePoint");
         p.x = Double.NaN; p.y = Double.NaN; p.z = Double.NaN;
         return p;
     }
    p.x = pInput.x; p.y = pInput.y; p.z = pInput.z;

    double rotZ = anglesRad.z;
    double rotY = anglesRad.y;
    double rotX = anglesRad.x;

    // Check for NaN/Inf angles
     if (Double.isNaN(rotX) || Double.isNaN(rotY) || Double.isNaN(rotZ) ||
         Double.isInfinite(rotX) || Double.isInfinite(rotY) || Double.isInfinite(rotZ)) {
           System.err.println("WARN: NaN/Inf angle in rotatePoint: " + anglesRad);
           // Return original point? Or NaN point? Let's return original.
           XYZPoint original = new XYZPoint();
           original.x = pInput.x; original.y = pInput.y; original.z = pInput.z;
           return original;
     }

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
    
     // Check for NaN/Inf results from trig functions or multiplications
     if (Double.isNaN(p.x) || Double.isNaN(p.y) || Double.isNaN(p.z) ||
         Double.isInfinite(p.x) || Double.isInfinite(p.y) || Double.isInfinite(p.z)) {
           System.err.println("WARN: NaN/Inf result in rotatePoint for input " + pInput + " angle " + anglesRad);
           // Return something safe, e.g., original point might be best default
            XYZPoint original = new XYZPoint();
            original.x = pInput.x; original.y = pInput.y; original.z = pInput.z;
            return original;
     }

    return p; 
  }


}
