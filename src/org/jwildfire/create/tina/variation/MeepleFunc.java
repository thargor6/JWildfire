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
 */
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * MeepleFunc Variation
 *
 * Creates shapes based on the classic "meeple" board game piece.
 * Includes parameters to dynamically adjust the proportions of the meeple's body parts.
 *
 * @author Brad Stefanov, implemented by Gemini
 */
public class MeepleFunc extends VariationFunc {
    private static final long serialVersionUID = 1L;

    // Parameter names
    private static final String PARAM_SCALE = "scale";
    private static final String PARAM_FILLED = "filled";
    private static final String PARAM_HEAD_WIDTH = "headWidth";
    private static final String PARAM_HEAD_HEIGHT = "headHeight";
    private static final String PARAM_ARM_LENGTH = "armLength";
    private static final String PARAM_ARM_SPREAD = "armSpread";
    private static final String PARAM_BODY_WIDTH = "bodyWidth";
    private static final String PARAM_LEG_HEIGHT = "legHeight";
    private static final String PARAM_LEG_SPREAD = "legSpread";
    private static final String PARAM_LEG_SEPARATION_WIDTH = "legSeparationWidth";
    private static final String PARAM_LEG_SEPARATION_HEIGHT = "legSeparationHeight";

    private static final String[] paramNames = {PARAM_SCALE, PARAM_FILLED, PARAM_HEAD_WIDTH, PARAM_HEAD_HEIGHT, PARAM_ARM_LENGTH, PARAM_ARM_SPREAD, PARAM_BODY_WIDTH, PARAM_LEG_HEIGHT, PARAM_LEG_SPREAD, PARAM_LEG_SEPARATION_WIDTH, PARAM_LEG_SEPARATION_HEIGHT};

    // Parameter defaults
    private double scale = 2.5;
    private double filled = 1.0;
    private double headWidth = 1.0;
    private double headHeight = 1.0;
    private double armLength = 2.0;
    private double armSpread = 1.0;
    private double bodyWidth = 1.0;
    private double legHeight = 1.0;
    private double legSpread = 1.0;
    private double legSeparationWidth = 0.1;
    private double legSeparationHeight = 0.0;

    // Base shape definition with more points for a rounded look
    private static final double[] baseMeepleX = {0.00, 0.15, 0.28, 0.35, 0.35, 0.40, 0.55, 0.55, 0.45, 0.40, 0.45, 0.40, 0.20, 0.00};
    private static final double[] baseMeepleY = {-1.00, -0.99, -0.90, -0.75, -0.60, -0.50, -0.45, -0.25, -0.15, 0.10, 0.80, 0.98, 1.00, 0.85};

    // Linear interpolation function
    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        
        // Dynamically generate the meeple shape based on parameters
        double[] dynamicMeepleX = new double[baseMeepleX.length];
        double[] dynamicMeepleY = new double[baseMeepleY.length];

        // Define the adjusted Y position of the leg separation point. Positive value moves it UP.
        double separationPointY = baseMeepleY[9] - legSeparationHeight;

        for (int i = 0; i < baseMeepleX.length; i++) {
            // Start with the base shape coordinates
            dynamicMeepleX[i] = baseMeepleX[i];
            dynamicMeepleY[i] = baseMeepleY[i];

            if (i <= 4) { // Head
                dynamicMeepleX[i] *= headWidth;
                dynamicMeepleY[i] *= headHeight;
            } else if (i >= 5 && i <= 8) { // Arms
                double armBaseX = baseMeepleX[4] * bodyWidth;
                double armBaseY = baseMeepleY[4] * headHeight;
                double armOffsetX = (baseMeepleX[i] - baseMeepleX[4]) * armLength;
                double armOffsetY = (baseMeepleY[i] - baseMeepleY[4]) * armSpread;
                dynamicMeepleX[i] = armBaseX + armOffsetX;
                dynamicMeepleY[i] = armBaseY + armOffsetY;
            } else if (i == 9) { // Body/Waist (the point that becomes the top of the leg separation)
                dynamicMeepleX[i] *= bodyWidth;
                dynamicMeepleY[i] = separationPointY; // Set the adjusted Y
            } else if (i >= 10) { // All lower points
                double originalOffsetY = baseMeepleY[i] - baseMeepleY[9];
                
                // Default Y calculation for outer leg points, based on the original waistline
                dynamicMeepleY[i] = baseMeepleY[9] + (originalOffsetY * legHeight);
                
                // For the inner points (the bottom of the V-shape), base their height on the NEW separation point
                if (i == 13) {
                    dynamicMeepleY[i] = separationPointY + (originalOffsetY * legHeight);
                }

                // Apply horizontal adjustments ONLY to the leg's side-points
                if (i >= 10 && i <= 12) {
                    // Spread scales the base shape's width, Separation Width shifts the result.
                    dynamicMeepleX[i] = (baseMeepleX[i] * legSpread) + legSeparationWidth;
                }
            }
        }
        
        // Get a random number from 0 to 1 to trace along the outline
        double t = pContext.random();
        
        // Scale t to the number of segments in our outline
        double numSegments = dynamicMeepleX.length - 1;
        double scaled_t = t * numSegments;
        int segmentIndex = (int) scaled_t;
        
        // Ensure we don't go out of bounds
        if (segmentIndex >= numSegments) {
            segmentIndex = (int) numSegments - 1;
        }

        // Get the local progress within the current line segment
        double segment_t = scaled_t - segmentIndex;

        // Get the start and end points of the current segment
        double p1x = dynamicMeepleX[segmentIndex];
        double p1y = dynamicMeepleY[segmentIndex];
        double p2x = dynamicMeepleX[segmentIndex + 1];
        double p2y = dynamicMeepleY[segmentIndex + 1];

        // Linearly interpolate between the two points to find our position on the outline
        double lx = lerp(p1x, p2x, segment_t);
        double ly = lerp(p1y, p2y, segment_t);
        
        // Randomly choose to reflect the point across the Y-axis to draw the left half
        if (pContext.random() < 0.5) {
            lx = -lx;
        }

        // If 'filled' is active, place the point at a random radius from the center.
        double r = (filled > 0 && filled > pContext.random()) ? pContext.random() : 1.0;
        
        // Apply the final transformation
        pVarTP.x += r * lx * scale * pAmount;
        pVarTP.y += r * ly * scale * pAmount;

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
        return new Object[]{scale, filled, headWidth, headHeight, armLength, armSpread, bodyWidth, legHeight, legSpread, legSeparationWidth, legSeparationHeight};
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_SCALE.equalsIgnoreCase(pName))
            scale = pValue;
        else if (PARAM_FILLED.equalsIgnoreCase(pName))
            filled = pValue;
        else if (PARAM_HEAD_WIDTH.equalsIgnoreCase(pName))
            headWidth = pValue;
        else if (PARAM_HEAD_HEIGHT.equalsIgnoreCase(pName))
            headHeight = pValue;
        else if (PARAM_ARM_LENGTH.equalsIgnoreCase(pName))
            armLength = pValue;
        else if (PARAM_ARM_SPREAD.equalsIgnoreCase(pName))
            armSpread = pValue;
        else if (PARAM_BODY_WIDTH.equalsIgnoreCase(pName))
            bodyWidth = pValue;
        else if (PARAM_LEG_HEIGHT.equalsIgnoreCase(pName))
            legHeight = pValue;
        else if (PARAM_LEG_SPREAD.equalsIgnoreCase(pName))
            legSpread = pValue;
        else if (PARAM_LEG_SEPARATION_WIDTH.equalsIgnoreCase(pName))
            legSeparationWidth = pValue;
        else if (PARAM_LEG_SEPARATION_HEIGHT.equalsIgnoreCase(pName))
            legSeparationHeight = pValue;
        else
            throw new IllegalArgumentException(pName);
    }

    @Override
    public String getName() {
        return "meeple";
    }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE};
    }
}
