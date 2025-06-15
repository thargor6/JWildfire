
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

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

/**
 * FloraFunc Variation
 *
 * A collection of several parametric leaf and flower formulas in a single variation.
 * Use the 'leafType' parameter to switch between different shapes.
 * Includes a 'distort' parameter for a more natural look.
 * Includes a 'shapeMod' parameter to modify each shape in a unique way.
 *
 * Based off of Maple leaf variaiton by Andreas Maschke
 * @author Inspired by Brad Stefanov, implemented by Gemini
 */
public class FloraFunc extends VariationFunc {
    private static final long serialVersionUID = 1L;

    // Parameter names
    private static final String PARAM_LEAF_TYPE = "leafType";
    private static final String PARAM_FILLED = "filled";
    private static final String PARAM_SCALE = "scale";
    private static final String PARAM_DISTORT = "distort";
    private static final String PARAM_SHAPE_MOD = "shapeMod";

    private static final String[] paramNames = {PARAM_LEAF_TYPE, PARAM_FILLED, PARAM_SCALE, PARAM_DISTORT, PARAM_SHAPE_MOD};

    // Parameter defaults
    private int leafType = 0; // 0=Ginkgo, 1=Oak, 2=Clover, 3=Rose, 4=Daisy, 5=Butterfly, 6=Cannabis, 7=Teardrop, 8=Lotus, 9=Sycamore, 10=Ash, 11=Monstera, 12=Star Anise, 13=Holly, 14=Sweetgum, 15=Grape, 16=Castor Bean, 17=Hosta, 18=Alocasia, 19=Dandelion, 20=Columbine, 21=Birch, 22=Tulip, 23=Linden, 24=Fiddlehead, 25=Barnsley Fern
    private double filled = 1.0;
    private double scale = 1.0;
    private double distort = 0.1; // Controls the amount of organic distortion
    private double shapeMod = 0.5; // Modifies each shape in a unique way (0-1)

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        double t = pAffineTP.getPrecalcAtan() - M_PI_2;
        double r = (filled > 0 && filled > pContext.random()) ? pAmount * pContext.random() : pAmount;

        double lx = 0, ly = 0;

        // For the fern, we operate directly on the affine coordinates
        double x = pAffineTP.x;
        double y = pAffineTP.y;

        switch (leafType) {
            case 0: // Ginkgo Leaf
                // shapeMod controls the fan width
                t += M_PI_2;
                double ginkgo_r = pow(sin(t), 2.0) * pow(cos(t), 4.0) + pow(cos(2.0 * t), 2.0);
                lx = ginkgo_r * cos(t);
                ly = ginkgo_r * sin(t) * (0.4 + shapeMod);
                break;
            case 1: // Oak Leaf
                // shapeMod controls lobe depth
                t += M_PI_2;
                double oak_r = (1.0 + (0.5 + shapeMod*0.8) * cos(8 * t)) * (1.0 + 0.1 * cos(24 * t)) * (0.9 + 0.05 * cos(200 * t)) * (1 + sin(t));
                lx = oak_r * sin(t);
                ly = -oak_r * cos(t);
                break;
            case 2: // Four-Leaf Clover
                // shapeMod controls petal size
                double clover_r = pow(cos(2.0 * t), 4.0) + pow(sin(2.0 * t) * cos(6.0 * t), 2.0);
                clover_r *= (0.5 + shapeMod);
                lx = clover_r * cos(t);
                ly = clover_r * sin(t);
                break;
            case 3: // Rose Curve
                // shapeMod controls petal tightness
                t += M_PI_2;
                double rose_r = cos((2.0 + shapeMod * 2.0) * t);
                lx = rose_r * cos(t);
                ly = rose_r * sin(t);
                break;
            case 4: // Daisy Flower
                // shapeMod controls number of petals
                t += M_PI_2;
                double daisy_r = 0.6 * (1.2 + cos((8.0 + shapeMod*16.0) * t));
                lx = daisy_r * cos(t);
                ly = daisy_r * sin(t);
                break;
            case 5: // Butterfly Curve
                // shapeMod controls wing size
                t += M_PI_2;
                double butterfly_r = exp(cos(t)) - 2.0 * cos(4.0 * t) + pow(sin(t / 12.0), 5.0);
                butterfly_r *= (0.7 + shapeMod*0.6);
                lx = butterfly_r * sin(t);
                ly = -butterfly_r * cos(t); // Negate to orient upwards
                break;
            case 6: // Cannabis Leaf
                // shapeMod controls leaflet width
                t += M_PI_2;
                double cannabis_r = (1.0 + 0.8 * sin(t)) * (1.0 + (0.1 + shapeMod*0.2) * cos(14.0 * t));
                lx = cannabis_r * sin(t);
                ly = -cannabis_r * cos(t);
                break;
            case 7: // Teardrop Leaf
                // shapeMod controls pointiness
                t += M_PI_2;
                double teardrop_r = 1 - (0.5 + shapeMod) * sin(t);
                lx = teardrop_r * sin(t);
                ly = -teardrop_r * cos(t);
                break;
            case 8: // Lotus Flower
                // shapeMod controls petal openness
                t += M_PI_2;
                double lotus_r = (0.6 + shapeMod * 0.4) + 0.4 * sin(8.0 * t);
                lx = lotus_r * cos(t);
                ly = lotus_r * sin(t);
                break;
            case 9: // Sycamore Leaf
                // shapeMod controls lobe depth
                t += M_PI_2;
                double sycamore_r = (1.0 + cos(t)) * (1.0 + (0.2 + shapeMod*0.6) * cos(5.0 * t));
                lx = sycamore_r * sin(t);
                ly = -sycamore_r * cos(t);
                break;
            case 10: // Ash Leaflet
                // shapeMod controls serration depth
                t += M_PI_2;
                double ash_r = sin(t) * (1.0 + (0.05 + shapeMod*0.3) * cos(20.0 * t));
                lx = ash_r * sin(t); // Elongated shape
                ly = -ash_r * cos(t) * 0.5; // Make it narrower
                break;
            case 11: // Monstera Leaf (simplified)
                // shapeMod controls notch depth
                t += M_PI_2;
                double monstera_r = 1.0 - sin(t); // Base heart shape
                monstera_r *= (1.0 - (0.5 + shapeMod*0.4) * pow(fabs(cos(2.5 * t + 0.2)), 30.0)); // Create sharp notches
                lx = monstera_r * sin(t);
                ly = -monstera_r * cos(t);
                break;
            case 12: // Star Anise
                // shapeMod controls pointiness
                t += M_PI_2;
                double anise_r = 1.0 + (0.2 + shapeMod*0.4) * cos(8.0 * t);
                lx = anise_r * cos(t);
                ly = anise_r * sin(t);
                break;
            case 13: // Holly Leaf
                // shapeMod controls point sharpness
                t += M_PI_2;
                double holly_r = 1.0 + (0.1 + shapeMod*0.3) * cos(16.0 * t);
                lx = holly_r * sin(t) * 0.7; // Make it elliptical
                ly = -holly_r * cos(t);
                break;
            case 14: // Sweetgum Leaf
                // shapeMod controls point prominence
                t += M_PI_2;
                double sweetgum_r = (1.0 + 0.2 * sin(t) + (0.2 + shapeMod*0.6) * sin(5.0 * t));
                lx = sweetgum_r * sin(t);
                ly = -sweetgum_r * cos(t);
                break;
            case 15: // Grape Leaf
                // shapeMod controls jaggedness
                t += M_PI_2;
                double grape_r = 0.8 * (1.6 + cos(5.0*t)*(0.3 + shapeMod*0.6) + cos(10.0*t)*(0.1 + shapeMod*0.2));
                lx = grape_r * sin(t);
                ly = -grape_r * cos(t);
                break;
            case 16: // Castor Bean Leaf
                // shapeMod controls lobe depth
                t += M_PI_2;
                double castor_r = (1.0 + (0.4 + shapeMod*0.6) * cos(7.0 * t)) * (1.0 + 0.1 * cos(21.0 * t));
                lx = castor_r * sin(t);
                ly = -castor_r * cos(t);
                break;
            case 17: // Hosta Leaf
                // shapeMod controls width
                t += M_PI_2;
                double hosta_r = 1.0 - 0.9 * sin(t); // Pointed heart shape
                lx = hosta_r * sin(t) * (0.8 + shapeMod);
                ly = -hosta_r * cos(t);
                break;
            case 18: // Alocasia (Elephant Ear) Leaf
                // shapeMod controls waviness
                t += M_PI_2;
                double alocasia_r = 1.0 / (1.0 + pow(sin(t / 2.0), 2.0)) - 0.5;
                alocasia_r += (shapeMod - 0.5) * 0.1 * sin(10*t);
                lx = alocasia_r * sin(t) * 0.8;
                ly = -alocasia_r * cos(t);
                break;
            case 19: // Dandelion Flower
                // shapeMod controls floret density
                t += M_PI_2;
                double dandelion_r = 0.8 * (1.1 + cos((20.0 + shapeMod*40.0) * t) * 0.3);
                lx = dandelion_r * cos(t);
                ly = dandelion_r * sin(t);
                break;
            case 20: // Columbine Flower
                // shapeMod controls spur length
                t += M_PI_2;
                double columbine_r = 1.0 + sin(t) + (0.2 + shapeMod*0.6)*sin(5.0*t - M_PI_2);
                lx = columbine_r * cos(t);
                ly = columbine_r * sin(t);
                break;
            case 21: // Birch Leaf
                // shapeMod controls serration depth
                t += M_PI_2;
                double birch_r = (1.0 - 0.2 * cos(t)) * (1.0 + (0.05 + shapeMod*0.2) * cos(30.0 * t));
                lx = birch_r * sin(t);
                ly = -birch_r * cos(t);
                break;
            case 22: // Tulip Flower
                // shapeMod controls openness
                t += M_PI_2;
                double tulip_r = pow(fabs(cos(t)), 0.3) + pow(fabs(sin(t)), 2.0 + shapeMod*2.0);
                lx = tulip_r * sin(t);
                ly = -tulip_r * cos(t) * 0.6;
                break;
            case 23: // Linden Leaf
                // shapeMod controls asymmetry
                t += M_PI_2;
                double linden_r = 1.0 - 0.9*sin(t + (shapeMod - 0.5)*0.2);
                lx = linden_r * sin(t) * 1.1;
                ly = -linden_r * cos(t);
                break;
            case 24: // Fiddlehead Fern
                // shapeMod controls coil tightness
                t = pAffineTP.getPrecalcAtan() * (2.0 + shapeMod * 4.0) * M_PI;
                double fiddlehead_r = 0.05 * t; // Archimedean spiral
                lx = fiddlehead_r * cos(t);
                ly = fiddlehead_r * sin(t);
                break;
            case 25: // Barnsley Fern (no stem)
                // shapeMod adjusts leaflet distribution
                double rand = pContext.random();
                
                // Probabilities are re-normalized to sum to 1.0, excluding the original stem part.
                double totalProb = 0.99;
                double p_frond = (0.85 - (shapeMod-0.5)*0.1) / totalProb;
                double p_left  = (0.07 + (shapeMod-0.5)*0.05) / totalProb;
                
                if (rand < p_frond) { // Successively smaller leaflets
                    lx = 0.85 * x + 0.04 * y;
                    ly = -0.04 * x + 0.85 * y + 1.6;
                } else if (rand < p_frond + p_left) { // Largest left-hand leaflet
                    lx = 0.2 * x - 0.26 * y;
                    ly = 0.23 * x + 0.22 * y + 1.6;
                } else { // Largest right-hand leaflet
                    lx = -0.15 * x + 0.28 * y;
                    ly = 0.26 * x + 0.24 * y + 0.44;
                }
                
                // The fern IFS generates the shape directly, so we don't use the polar 'r' value.
                pVarTP.x += lx * scale * pAmount;
                pVarTP.y += ly * scale * pAmount;
                if (pContext.isPreserveZCoordinate()) {
                    pVarTP.z += pAmount * pAffineTP.z;
                }
                return; // Return early as the fern logic is self-contained.
        }

        // Apply distortion for a more organic look (for non-fern/non-IFS types)
        if (distort != 0) {
            double origX = pAffineTP.x;
            double origY = pAffineTP.y;
            lx += distort * sin(origY * 5.0);
            ly += distort * cos(origX * 5.0);
        }
        
        // Standard transformation for polar-based leaves
        pVarTP.x += r * lx * scale;
        pVarTP.y += r * ly * scale;

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
        return new Object[]{leafType, filled, scale, distort, shapeMod};
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_LEAF_TYPE.equalsIgnoreCase(pName))
            leafType = (int) pValue;
        else if (PARAM_FILLED.equalsIgnoreCase(pName))
            filled = pValue;
        else if (PARAM_SCALE.equalsIgnoreCase(pName))
            scale = pValue;
        else if (PARAM_DISTORT.equalsIgnoreCase(pName))
            distort = pValue;
        else if (PARAM_SHAPE_MOD.equalsIgnoreCase(pName))
            shapeMod = pValue;
        else
            throw new IllegalArgumentException(pName);
    }

    @Override
    public String getName() {
        return "flora";
    }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_BASE_SHAPE};
    }
}
