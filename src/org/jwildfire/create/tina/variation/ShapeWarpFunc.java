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

// Standard Java imports
import java.lang.Math;

// JWildfire base classes
// NO import org.jwildfire.create.tina.base.FlameTransformationContext;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

/**
 * Shape Warp Variation for JWildfire.
 * Defines a base shape (Square/Rect/Circle/Ellipse/Diamond/Triangle/Pentagon/Hexagon/Flower/Star/Cloud)
 * and warps points based on distance from the center, relative to inner/outer radii.
 * Warp effect can originate from a separate warp center.
 * Warp type, amount, and falloff curve are controllable via parameters.
 * pAmount acts as a final scaling factor on the output position.
 */
public class ShapeWarpFunc extends VariationFunc {
    private static final long serialVersionUID = 1L;
    private static final String NAME = "shape_warp";

    // --- Parameters ---
    private static final String PARAM_SHAPE = "shape"; // 0=Sqr, 1=Rect, 2=Circ/Ellipse, 3=Diam, 4=Tri, 5=Penta, 6=Hexa, 7=Flower, 8=Star, 9=Cloud
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_ASPECT = "aspect"; // W/H ratio (Used for Rect, Ellipse, Diamond)
    private static final String PARAM_CENTER_X = "centerX";
    private static final String PARAM_CENTER_Y = "centerY";
    private static final String PARAM_INNER_RADIUS = "innerRadius";
    private static final String PARAM_OUTER_RADIUS = "outerRadius";
    private static final String PARAM_WARP_MODE = "warpMode"; // 0=Rotate, 1=ScaleRad, 2=Swirl, 3=ScaleXY, 4=Fisheye, 5=Shear
    private static final String PARAM_WARP_AMOUNT = "warpAmount";
    private static final String PARAM_WARP_AMOUNT_X = "warpAmountX";
    private static final String PARAM_WARP_AMOUNT_Y = "warpAmountY";
    private static final String PARAM_WARP_CENTER_X = "warpCenterX";
    private static final String PARAM_WARP_CENTER_Y = "warpCenterY";
    private static final String PARAM_WARP_CURVE = "warpCurve";
    private static final String PARAM_WARP_INVERT = "warpInvert";
    private static final String PARAM_FLOWER_PETALS = "flowerPetals"; // Num petals for Flower
    // New parameters for Star shape
    private static final String PARAM_STAR_POINTS = "starPoints"; // Number of points on the star
    private static final String PARAM_STAR_DEPTH = "starDepth"; // How deep the cuts are (0=deep, 1=none)
    // New parameters for Cloud shape
    private static final String PARAM_CLOUD_AMPLITUDE = "cloudAmplitude"; // Bumpiness of cloud
    private static final String PARAM_CLOUD_FREQUENCY = "cloudFrequency"; // Frequency of bumps

    // Update paramNames array
    private static final String[] paramNames = {
        PARAM_SHAPE, PARAM_SIZE, PARAM_ASPECT,
        PARAM_CENTER_X, PARAM_CENTER_Y,
        PARAM_INNER_RADIUS, PARAM_OUTER_RADIUS,
        PARAM_WARP_MODE, PARAM_WARP_AMOUNT,
        PARAM_WARP_AMOUNT_X, PARAM_WARP_AMOUNT_Y,
        PARAM_WARP_CENTER_X, PARAM_WARP_CENTER_Y,
        PARAM_WARP_CURVE,
        PARAM_WARP_INVERT,
        PARAM_FLOWER_PETALS,
        PARAM_STAR_POINTS,
        PARAM_STAR_DEPTH,
        PARAM_CLOUD_AMPLITUDE,
        PARAM_CLOUD_FREQUENCY
    };

    // Instance variables for parameters
    private int shape = 0; private double size = 1.0; private double aspect = 1.0;
    private double centerX = 0.0; private double centerY = 0.0;
    private double innerRadius = 0.0; private double outerRadius = 1.0;
    private int warpMode = 0; private double warpAmount = Math.PI;
    private double warpAmountX = 1.0; private double warpAmountY = 1.0;
    private double warpCenterX = 0.0; private double warpCenterY = 0.0;
    private double warpCurve = 1.0; private int warpInvert = 0;
    private int flowerPetals = 5;
    private int starPoints = 5; // Default 5 points
    private double starDepth = 0.5; // Default 50% depth
    private double cloudAmplitude = 0.2; // Default 20% bumpiness
    private double cloudFrequency = 5.0; // Default frequency

    // Shape type constants
    private static final int SHAPE_SQUARE = 0; private static final int SHAPE_RECTANGLE = 1;
    private static final int SHAPE_CIRCLE_ELLIPSE = 2; private static final int SHAPE_DIAMOND = 3;
    private static final int SHAPE_TRIANGLE = 4; private static final int SHAPE_PENTAGON = 5;
    private static final int SHAPE_HEXAGON = 6; private static final int SHAPE_FLOWER = 7;
    private static final int SHAPE_STAR = 8;
    private static final int SHAPE_CLOUD = 9;
    private static final int MAX_SHAPE_INDEX = 9;

    // Warp mode constants
    private static final int WARP_ROTATE = 0; private static final int WARP_SCALE_RADIAL = 1;
    private static final int WARP_SWIRL = 2; private static final int WARP_SCALE_XY = 3;
    private static final int WARP_FISHEYE = 4; private static final int WARP_SHEAR = 5;
    private static final int MAX_WARP_MODE_INDEX = 5;

    // Constants for calculation
    private static final double TWO_PI = 2.0 * Math.PI;
    private static final double PI = Math.PI;
    private static final double PI_OVER_3 = Math.PI / 3.0;
    private static final double PI_OVER_5 = Math.PI / 5.0;
    private static final double PI_OVER_6 = Math.PI / 6.0;
    private static final double COS_PI_OVER_3 = Math.cos(PI_OVER_3);
    private static final double COS_PI_OVER_5 = Math.cos(PI_OVER_5);
    private static final double COS_PI_OVER_6 = Math.cos(PI_OVER_6);


    // --- Methods ---

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {

        // 1. Get Parameters
        int currentShape = this.shape;
        double currentSize = this.size;
        double currentAspect = (currentShape == SHAPE_RECTANGLE || currentShape == SHAPE_CIRCLE_ELLIPSE || currentShape == SHAPE_DIAMOND) ? this.aspect : 1.0;
        double currentCenterX = this.centerX;
        double currentCenterY = this.centerY;
        double currentInnerRadius = Math.max(0.0, this.innerRadius);
        double currentOuterRadius = Math.max(currentInnerRadius + 1e-9, this.outerRadius);
        int currentWarpMode = this.warpMode;
        double currentWarpAmount = this.warpAmount;
        double currentWarpAmountX = this.warpAmountX;
        double currentWarpAmountY = this.warpAmountY;
        double currentWarpCenterX = this.warpCenterX;
        double currentWarpCenterY = this.warpCenterY;
        double currentWarpCurve = Math.max(1e-6, this.warpCurve);
        boolean invert = (this.warpInvert != 0);
        // Shape specific params
        int currentFlowerPetals = (currentShape == SHAPE_FLOWER) ? this.flowerPetals : 0;
        int currentStarPoints = (currentShape == SHAPE_STAR) ? this.starPoints : 0;
        double currentStarDepth = (currentShape == SHAPE_STAR) ? this.starDepth : 0;
        double currentCloudAmplitude = (currentShape == SHAPE_CLOUD) ? this.cloudAmplitude : 0;
        double currentCloudFrequency = (currentShape == SHAPE_CLOUD) ? this.cloudFrequency : 0;

        // 2. Calculate coords relative to SHAPE center
        double shapeRelX = pAffineTP.x - currentCenterX;
        double shapeRelY = pAffineTP.y - currentCenterY;

        // 3. Calculate shape dimensions and distance metric
        double hw = Math.max(1e-9, currentSize * currentAspect * 0.5);
        double hh = Math.max(1e-9, currentSize * 0.5);
        double R = Math.max(1e-9, currentSize * 0.5); // Base radius/size factor
        double dist = 0.0; // Normalized distance

        // Pre-calculate polar coordinates
        double radius = Math.sqrt(shapeRelX * shapeRelX + shapeRelY * shapeRelY);
        double angle = 0.0;
        if (radius > 1e-9) {
             angle = Math.atan2(shapeRelY, shapeRelX);
             while (angle < 0) { angle += TWO_PI; } // Normalize angle to [0, 2*PI)
        }

        // Shape distance calculations
        if (currentShape == SHAPE_CIRCLE_ELLIPSE) {
            double normX = shapeRelX / hw; double normY = shapeRelY / hh;
            dist = Math.sqrt(normX*normX + normY*normY);
        } else if (currentShape == SHAPE_DIAMOND) {
             double normX = shapeRelX / hw; double normY = shapeRelY / hh;
            dist = Math.abs(normX) + Math.abs(normY);
        } else if (currentShape == SHAPE_TRIANGLE) {
            if (radius < 1e-9) { dist = 0.0; } else {
                double sectorAngle = TWO_PI / 3.0; double bisectorAngle = PI_OVER_3;
                double currentAngleInSector = angle % sectorAngle; double angle_diff = currentAngleInSector - bisectorAngle;
                double cos_term = Math.cos(angle_diff); double shapeBoundaryR = R * COS_PI_OVER_3 / Math.max(1e-9, Math.abs(cos_term));
                dist = radius / Math.max(1e-9, shapeBoundaryR);
            }
        } else if (currentShape == SHAPE_PENTAGON) {
             if (radius < 1e-9) { dist = 0.0; } else {
                 double sectorAngle = TWO_PI / 5.0; double bisectorAngle = PI_OVER_5;
                 double currentAngleInSector = angle % sectorAngle; double angle_diff = currentAngleInSector - bisectorAngle;
                 double cos_term = Math.cos(angle_diff); double shapeBoundaryR = R * COS_PI_OVER_5 / Math.max(1e-9, Math.abs(cos_term));
                 dist = radius / Math.max(1e-9, shapeBoundaryR);
             }
        } else if (currentShape == SHAPE_HEXAGON) {
             if (radius < 1e-9) { dist = 0.0; } else {
                 double sectorAngle = TWO_PI / 6.0; double bisectorAngle = PI_OVER_6;
                 double currentAngleInSector = angle % sectorAngle; double angle_diff = currentAngleInSector - bisectorAngle;
                 double cos_term = Math.cos(angle_diff); double shapeBoundaryR = R * COS_PI_OVER_6 / Math.max(1e-9, Math.abs(cos_term));
                 dist = radius / Math.max(1e-9, shapeBoundaryR);
             }
        } else if (currentShape == SHAPE_FLOWER) {
            if (radius < 1e-9) { dist = 0.0; } else {
                double flowerBoundaryR = R * Math.abs(Math.cos(currentFlowerPetals * angle));
                dist = radius / Math.max(1e-9, flowerBoundaryR);
            }
        } else if (currentShape == SHAPE_STAR) {
             if (radius < 1e-9) { dist = 0.0; } else {
                 double k = currentStarPoints; // Number of points
                 double depth = currentStarDepth; // Depth (0-1)
                 double R_outer = R;
                 double R_inner = R * depth;

                 double anglePerVertex = PI / k; // Angle between adjacent point and valley
                 int sectorIndex = (int) Math.floor(angle / anglePerVertex);
                 double angleInSector = angle - sectorIndex * anglePerVertex;

                 // Normalize angle within sector (0 to 1)
                 double t = angleInSector / anglePerVertex;
                 // Normalized distance from sector center (0 at center, 1 at edges)
                 double t_norm = 2.0 * Math.abs(t - 0.5);

                 double starBoundaryR;
                 if (sectorIndex % 2 == 0) { // Even index = point sector
                     starBoundaryR = R_inner + (R_outer - R_inner) * (1.0 - t_norm);
                 } else { // Odd index = valley sector
                     starBoundaryR = R_inner + (R_outer - R_inner) * t_norm;
                 }
                 dist = radius / Math.max(1e-9, starBoundaryR);
             }
        } else if (currentShape == SHAPE_CLOUD) {
             if (radius < 1e-9) { dist = 0.0; } else {
                  double amp = currentCloudAmplitude;
                  double freq = currentCloudFrequency;
                  // Simple pseudo-noise using sum of sines with different frequencies/phases
                  double noise = 0.6 * Math.sin(freq * angle)
                               + 0.3 * Math.sin(2.1 * freq * angle + 1.23)
                               + 0.1 * Math.sin(4.3 * freq * angle + 4.56);
                  // Modulate base radius R with noise
                  double cloudBoundaryR = R * (1.0 + amp * noise);
                  // Ensure boundary is positive and not too small
                  cloudBoundaryR = Math.max(R * 0.1, cloudBoundaryR); // Min 10% of base radius
                  dist = radius / cloudBoundaryR; // No need for max(1e-9,...) here due to clamp above
             }
        } else { // Square (shape=0) or Rectangle (shape=1)
            double normX = shapeRelX / hw; double normY = shapeRelY / hh;
            dist = Math.max(Math.abs(normX), Math.abs(normY));
        }

        // 4. Calculate Warp Factor
        double warpFactor = 0.0;
        double range = currentOuterRadius - currentInnerRadius;
        if (dist <= currentInnerRadius) { warpFactor = 0.0; }
        else if (dist >= currentOuterRadius) { warpFactor = 1.0; }
        else { if (range > 1e-9) { warpFactor = (dist - currentInnerRadius) / range; } else { warpFactor = 1.0; } }
        warpFactor = Math.pow(warpFactor, currentWarpCurve);
        if (invert) { warpFactor = 1.0 - warpFactor; }
        double effectiveWarp = warpFactor * currentWarpAmount;
        double effectiveWarpX = warpFactor * currentWarpAmountX;
        double effectiveWarpY = warpFactor * currentWarpAmountY;

        // 5. Calculate coords relative to WARP center
        double warpRelX = pAffineTP.x - currentWarpCenterX;
        double warpRelY = pAffineTP.y - currentWarpCenterY;
        double warpedX_rc = warpRelX;
        double warpedY_rc = warpRelY;

        // 6. Apply the selected warp mode
        switch (currentWarpMode) {
            case WARP_ROTATE: { double angleWarp = effectiveWarp; double cosA = Math.cos(angleWarp); double sinA = Math.sin(angleWarp); warpedX_rc = warpRelX * cosA - warpRelY * sinA; warpedY_rc = warpRelX * sinA + warpRelY * cosA; break; }
            case WARP_SCALE_RADIAL: { double scaleFactor = Math.max(1e-9, 1.0 + effectiveWarp); warpedX_rc = warpRelX * scaleFactor; warpedY_rc = warpRelY * scaleFactor; break; }
            case WARP_SWIRL: { double currentAngleSwirl = Math.atan2(warpRelY, warpRelX); double radiusSwirl = Math.sqrt(warpRelX * warpRelX + warpRelY * warpRelY); double swirlAngle = currentAngleSwirl + effectiveWarp; warpedX_rc = radiusSwirl * Math.cos(swirlAngle); warpedY_rc = radiusSwirl * Math.sin(swirlAngle); break; }
            case WARP_SCALE_XY: { double scaleX = Math.max(1e-9, 1.0 + effectiveWarpX); double scaleY = Math.max(1e-9, 1.0 + effectiveWarpY); warpedX_rc = warpRelX * scaleX; warpedY_rc = warpRelY * scaleY; break; }
            case WARP_FISHEYE: { double angleFish = Math.atan2(warpRelY, warpRelX); double radiusFish = Math.sqrt(warpRelX*warpRelX + warpRelY*warpRelY); if (radiusFish > 1e-9) { double power = 1.0 - effectiveWarp; power = Math.max(-5.0, Math.min(5.0, power)); if (Math.abs(power - 1.0) > 1e-9) { double newRadius = Math.pow(radiusFish, power); warpedX_rc = newRadius * Math.cos(angleFish); warpedY_rc = newRadius * Math.sin(angleFish); } } break; }
            case WARP_SHEAR: { warpedX_rc = warpRelX + effectiveWarpX * warpRelY; warpedY_rc = warpRelY + effectiveWarpY * warpRelX; break; }
            default: break;
        }

        // 7. Translate back & calculate final target
        double finalX = warpedX_rc + currentWarpCenterX;
        double finalY = warpedY_rc + currentWarpCenterY;

        // --- OUTPUT SECTION (Scaled by pAmount) ---
        pVarTP.x = finalX * pAmount;
        pVarTP.y = finalY * pAmount;
        
        if (pContext.isPreserveZCoordinate()) {
          pVarTP.z += pAmount * pAffineTP.z;
      }
        
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public String[] getParameterNames() { return paramNames; }

    @Override
    public Object[] getParameterValues() {
        return new Object[] {
            shape, size, aspect,
            centerX, centerY,
            innerRadius, outerRadius,
            warpMode, warpAmount,
            warpAmountX, warpAmountY,
            warpCenterX, warpCenterY,
            warpCurve,
            warpInvert,
            flowerPetals,
            starPoints,
            starDepth,
            cloudAmplitude,
            cloudFrequency 
        };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_SHAPE.equalsIgnoreCase(pName)) {
            shape = Math.max(0, Math.min(MAX_SHAPE_INDEX, (int) pValue));
        } else if (PARAM_SIZE.equalsIgnoreCase(pName)) {
            size = Math.max(1e-9, pValue);
        } else if (PARAM_ASPECT.equalsIgnoreCase(pName)) {
            aspect = Math.max(1e-9, pValue);
        } else if (PARAM_CENTER_X.equalsIgnoreCase(pName)) {
            centerX = pValue;
        } else if (PARAM_CENTER_Y.equalsIgnoreCase(pName)) {
            centerY = pValue;
        } else if (PARAM_INNER_RADIUS.equalsIgnoreCase(pName)) {
            innerRadius = Math.max(0.0, pValue);
            if (outerRadius < innerRadius) outerRadius = innerRadius;
        } else if (PARAM_OUTER_RADIUS.equalsIgnoreCase(pName)) {
            outerRadius = Math.max(this.innerRadius, pValue);
        } else if (PARAM_WARP_MODE.equalsIgnoreCase(pName)) {
            warpMode = Math.max(0, Math.min(MAX_WARP_MODE_INDEX, (int) pValue));
        } else if (PARAM_WARP_AMOUNT.equalsIgnoreCase(pName)) {
            warpAmount = pValue;
        } else if (PARAM_WARP_AMOUNT_X.equalsIgnoreCase(pName)) {
            warpAmountX = pValue;
        } else if (PARAM_WARP_AMOUNT_Y.equalsIgnoreCase(pName)) {
            warpAmountY = pValue;
        } else if (PARAM_WARP_CENTER_X.equalsIgnoreCase(pName)) {
            warpCenterX = pValue;
        } else if (PARAM_WARP_CENTER_Y.equalsIgnoreCase(pName)) {
            warpCenterY = pValue;
        } else if (PARAM_WARP_CURVE.equalsIgnoreCase(pName)) {
            warpCurve = Math.max(1e-6, pValue);
        } else if (PARAM_WARP_INVERT.equalsIgnoreCase(pName)) {
            warpInvert = (pValue >= 0.5) ? 1 : 0;
        } else if (PARAM_FLOWER_PETALS.equalsIgnoreCase(pName)) {
            flowerPetals = Math.max(2, (int) pValue);
        } else if (PARAM_STAR_POINTS.equalsIgnoreCase(pName)) { 
            starPoints = Math.max(3, (int) pValue); // Star needs at least 3 points
        } else if (PARAM_STAR_DEPTH.equalsIgnoreCase(pName)) {
            starDepth = Math.max(0.0, Math.min(1.0, pValue)); // Clamp depth between 0.0 and 1.0
        } else if (PARAM_CLOUD_AMPLITUDE.equalsIgnoreCase(pName)) {
            cloudAmplitude = Math.max(0.0, pValue); // Amplitude cannot be negative
        } else if (PARAM_CLOUD_FREQUENCY.equalsIgnoreCase(pName)) {
            cloudFrequency = Math.max(1e-6, pValue); // Frequency must be positive
        } else {
            throw new IllegalArgumentException("Unknown parameter: " + pName);
        }
    }
    
    @Override
    public void randomize() {
    	shape = (int) (Math.random() * (MAX_SHAPE_INDEX+1));
    	size = Math.random() * 2.0 + 0.1;
    	aspect = Math.random() * 3.0 + 0.1;
    	centerX = Math.random() * 3.0 - 1.5;
    	centerY = Math.random() * 3.0 - 1.5;
    	innerRadius = Math.random() * 1.5;
    	outerRadius = Math.random() * 3.0 + innerRadius;
    	warpMode = (int) (Math.random() * (MAX_WARP_MODE_INDEX+1));
    	warpAmount = Math.random() * TWO_PI * 2 - TWO_PI;
    	warpAmountX = Math.random() * TWO_PI - PI;
    	warpAmountY = Math.random() * TWO_PI - PI;
    	warpCenterX = Math.random() * 3.0 - 1.5;
    	warpCenterY = Math.random() * 3.0 - 1.5;
    	warpCurve = Math.random() * 10.0 + 0.1;
    	warpInvert = (int) (Math.random() * 2);
    	flowerPetals = (int) (Math.random() * 8 + 2);
    	starPoints = (int) (Math.random() * 7 + 3);
    	starDepth = Math.random();
    	cloudAmplitude = Math.random() * 4.0;
    	if (Math.random() < 0.8) cloudFrequency = Math.random() * 9.9 + 0.1;
    	else cloudFrequency = Math.random() * 40.0 + 10.0;
    }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_2D};
    }
}