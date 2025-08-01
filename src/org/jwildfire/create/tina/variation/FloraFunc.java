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
import org.jwildfire.base.Tools;

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
    
    private static final String RESSOURCE_LEAFTYPE_REFERENCE = "leafType_reference";

    private static final String[] paramNames = {PARAM_LEAF_TYPE, PARAM_FILLED, PARAM_SCALE, PARAM_DISTORT, PARAM_SHAPE_MOD};
    private static final String[] ressourceNames = {RESSOURCE_LEAFTYPE_REFERENCE};
    
    // Parameter defaults
    private int leafType = 0; // 0=Ginkgo, 1=Cannabis, 2=Clover, 3=Rose, 4=Daisy, 5=Butterfly, 6=Oak, 7=Teardrop, 8=Lotus, 9=Sycamore, 10=Ash, 11=Monstera, 12=Star Anise, 13=Holly, 14=Sweetgum, 15=Grape, 16=Castor Bean, 17=Hosta, 18=Alocasia, 19=Dandelion, 20=Columbine, 21=Birch, 22=Tulip, 23=Linden, 24=Fiddlehead, 25=Barnsley Fern
    private double filled = 1.0;
    private double scale = 1.0;
    private double distort = 0.1; // Controls the amount of organic distortion
    private double shapeMod = 0.5; // Modifies each shape in a unique way (0-1)
    
    private String leaftype_reference = "org.jwildfire.create.tina.variation.reference.ReferenceFile flora-leaftypes.pdf";

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        double t = pAffineTP.getPrecalcAtan(); // Use direct angle for some formulas
        double r = (filled > 0 && filled > pContext.random()) ? pAmount * pContext.random() : pAmount;

        double lx = 0, ly = 0;

        // For the fern, we operate directly on the affine coordinates
        double x = pAffineTP.x;
        double y = pAffineTP.y;

        switch (leafType) {
            case 0: // Ginkgo Leaf
                t += M_PI_2; // Orient the fan upwards
                double ginkgo_r = 0;
                if(sin(t) > 0) {
                    ginkgo_r = 1.0;
                    ginkgo_r -= (shapeMod * 0.8) * pow(cos(t), 50.0);
                    ginkgo_r *= (1.0 + 0.05 * cos(8.0*t));
                }
                lx = ginkgo_r * cos(t);
                ly = ginkgo_r * sin(t);
                break;
            case 1: // Cannabis Leaf
                t += M_PI_2;
                double cannabis_r = (1.0 + (0.5 + shapeMod*0.8) * cos(8 * t)) * (1.0 + 0.1 * cos(24 * t)) * (0.9 + 0.05 * cos(200 * t)) * (1 + sin(t));
                lx = cannabis_r * sin(t);
                ly = -cannabis_r * cos(t);
                break;
            case 2: // Four-Leaf Clover
                double effectiveCloverMod = -6.0 * shapeMod + 0.5;
                double cleftTerm = 0.5 + effectiveCloverMod * 0.5;
                t += M_PI / 4.0;
                double clover_r = (1 + 0.8 * cos(4.0 * t)) * (1 - cleftTerm * pow(sin(4.0 * t), 2.0));
                lx = clover_r * cos(t);
                ly = clover_r * sin(t);
                break;
            case 3: // Rose Leaflet
                t += M_PI_2;
                double rose_r = sin(t);
                if (rose_r < 0) rose_r = 0;
                rose_r *= (1.0 + (shapeMod * 0.1) * cos(40.0 * t));
                lx = rose_r * cos(t) * 0.7;
                ly = rose_r * sin(t);
                break;
            case 4: // Daisy Flower
                t += M_PI_2;
                double daisy_r = 0.6 * (1.2 + cos((8.0 + shapeMod*16.0) * t));
                lx = daisy_r * cos(t);
                ly = daisy_r * sin(t);
                break;
            case 5: // Butterfly Curve
                t += M_PI_2;
                double butterfly_r = exp(cos(t)) - 2.0 * cos(4.0 * t) + pow(sin(t / 12.0), 5.0);
                butterfly_r *= (0.7 + shapeMod*0.6);
                lx = butterfly_r * sin(t);
                ly = -butterfly_r * cos(t);
                break;
            case 6: // Oak Leaf
                t += M_PI_2;
                double oak_r = (1.0 + 0.8 * sin(t)) * (1.0 + (0.1 + shapeMod*0.2) * cos(14.0 * t));
                lx = oak_r * sin(t);
                ly = -oak_r * cos(t);
                break;
            case 7: // Teardrop Leaf
                t += M_PI_2;
                double teardrop_r = (1.0 - 0.9 * sin(t));
                teardrop_r *= (1.0 + 0.02 * cos(60.0 * t));
                lx = teardrop_r * cos(t) * (0.6 + shapeMod * 0.4);
                ly = teardrop_r * sin(t);
                break;
            case 8: // Lotus Leaf
                double lotus_r = 1.0 + 0.05 * cos(12 * t);
                double center_offset = 0.1 + shapeMod * 0.4;
                lx = lotus_r * cos(t);
                ly = lotus_r * sin(t) + (center_offset * (1 - lotus_r));
                break;
            case 9: // Sycamore Leaf
                t += M_PI_2;
                double sycamore_r = (1.0 - 0.5 * sin(t));
                sycamore_r *= (1.0 + (0.1 + shapeMod * 0.4) * cos(5.0*t));
                sycamore_r *= (1.0 + 0.05 * cos(25.0*t));
                lx = sycamore_r * sin(t);
                ly = -sycamore_r * cos(t);
                break;
            case 10: // Ash Leaflet
                t += M_PI_2;
                double ash_r = sin(t) > 0 ? sin(t) * (1.0 + (0.05 + shapeMod*0.3) * cos(30.0 * t)) : 0;
                lx = ash_r * cos(t) * 0.6;
                ly = ash_r * sin(t);
                break;
            case 11: // Monstera Leaf
                t += M_PI_2;
                double monstera_base_r = 1.0 - 0.9 * sin(t);
                if (monstera_base_r < 0) monstera_base_r = 0;
                double fenestrations = 1.0 - (shapeMod * 0.8) * pow(sin(t * 2.5), 10.0);
                fenestrations *= 1.0 - (shapeMod * 0.7) * pow(cos(t * 3.5), 10.0);
                double monstera_r = monstera_base_r * fenestrations;
                lx = monstera_r * cos(t);
                ly = monstera_r * sin(t);
                break;
            case 12: // Star Anise
                t += M_PI_2;
                double anise_r = 1.0 + (0.2 + shapeMod*0.4) * cos(8.0 * t);
                lx = anise_r * cos(t);
                ly = anise_r * sin(t);
                break;
            case 13: // Holly Leaf
                t += M_PI_2;
                double holly_base_r = 1.0;
                double num_points = 10.0;
                double spike_depth = 0.15 + shapeMod * 0.2;
                double spikes = 1.0 - spike_depth * Math.abs(sin(t * num_points / 2.0));
                double holly_r = holly_base_r * spikes;
                lx = holly_r * cos(t) * 0.7;
                ly = holly_r * sin(t);
                break;
            case 14: // Sweetgum Leaf
                t += M_PI_2;
                double sweetgum_base_r = 1.0;
                double sweetgum_num_points = 5.0;
                double sweetgum_point_depth = 0.3 + shapeMod * 0.4;
                double sweetgum_sharp_valleys = 1.0 - sweetgum_point_depth * Math.abs(sin(t * sweetgum_num_points / 2.0));
                double sweetgum_serrations = 1.0 + 0.04 * cos(40 * t);
                double sweetgum_r = sweetgum_base_r * sweetgum_sharp_valleys * sweetgum_serrations;
                lx = sweetgum_r * cos(t);
                ly = sweetgum_r * sin(t);
                break;
            case 15: // Grape Leaf
                t += M_PI_2;
                double lobe_depth = 0.1 + shapeMod * 0.2;
                double base_shape = 1.0 + lobe_depth * cos(3.0 * t) - (lobe_depth * 0.5) * cos(5.0 * t);
                double serrations = 1.0 + 0.03 * cos(20.0*t) + 0.02 * cos(35.0*t);
                double grape_r = base_shape * serrations;
                lx = grape_r * cos(t);
                ly = grape_r * sin(t);
                break;
            case 16: // Castor Bean Leaf
                t += M_PI_2;
                double castor_lobe_depth = 0.3 + shapeMod * 0.5;
                double castor_base_shape = 1.0 + castor_lobe_depth * cos(7.0 * t);
                double castor_serration_depth = 0.05;
                double castor_serrations = 1.0 + castor_serration_depth * cos(42.0 * t);
                double castor_r = castor_base_shape * castor_serrations;
                lx = castor_r * cos(t);
                ly = castor_r * sin(t);
                break;
            case 17: // Hosta Leaf
                t += M_PI_2;
                double hosta_base_r = (1.0 - 0.9 * sin(t));
                double waviness = 0.02 + shapeMod * 0.05;
                double hosta_r = hosta_base_r * (1.0 + waviness * sin(10.0 * t));
                lx = hosta_r * cos(t) * 1.2;
                ly = hosta_r * sin(t);
                break;
            case 18: // Alocasia (Elephant Ear) Leaf
                t = pAffineTP.getPrecalcAtan(); // Use direct angle for this specific formula
                double alocasia_base_r = 1.0 - sin(t);
                double alocasia_lobe_definition = 1.0 - (0.1 + shapeMod * 0.4) * pow(cos(t), 2.0);
                double alocasia_r = alocasia_base_r * alocasia_lobe_definition;
                alocasia_r *= (1.0 + 0.03 * sin(15.0*t));
                lx = alocasia_r * cos(t) * 0.7; 
                ly = alocasia_r * sin(t);
                break;
            case 19: // Dandelion Flower
                t += M_PI_2;
                double dandelion_r = 0.8 * (1.1 + cos((20.0 + shapeMod*40.0) * t) * 0.3);
                lx = dandelion_r * cos(t);
                ly = dandelion_r * sin(t);
                break;
            case 20: // Columbine Flower
                t += M_PI_2;
                double columbine_r = 1.0 + sin(t) + (0.2 + shapeMod*0.6)*sin(5.0*t - M_PI_2);
                lx = columbine_r * cos(t);
                ly = columbine_r * sin(t);
                break;
            case 21: // Birch Leaf (Final Corrected Formula from Test)
                t = pAffineTP.getPrecalcAtan(); // Use direct angle
                double birch_base_r = 1.0 - 0.9 * sin(t);
                double primary_serrations = 1.0 + 0.05 * cos(20.0 * t);
                double secondary_serrations = 1.0 + (0.02 + shapeMod * 0.03) * cos(40.0 * t);
                double birch_r = birch_base_r * primary_serrations * secondary_serrations;
                lx = birch_r * cos(t) * 0.8;
                ly = birch_r * sin(t);
                break;
            case 22: // Tulip Flower
                t += M_PI_2;
                double tulip_r = pow(fabs(cos(t)), 0.3) + pow(fabs(sin(t)), 2.0 + shapeMod*2.0);
                lx = tulip_r * sin(t);
                ly = -tulip_r * cos(t) * 0.6;
                break;
            case 23: // Linden Leaf
                t += M_PI_2;
                double linden_r = 1.0 - 0.9*sin(t + (shapeMod - 0.5)*0.2);
                lx = linden_r * sin(t) * 1.1;
                ly = -linden_r * cos(t);
                break;
            case 24: // Fiddlehead Fern
                t = pAffineTP.getPrecalcAtan() * (2.0 + shapeMod * 4.0) * M_PI;
                double fiddlehead_r = 0.05 * t;
                lx = fiddlehead_r * cos(t);
                ly = fiddlehead_r * sin(t);
                break;
            case 25: // Barnsley Fern (no stem)
                double rand = pContext.random();
                double totalProb = 0.99;
                double p_frond = (0.85 - (shapeMod-0.5)*0.1) / totalProb;
                double p_left  = (0.07 + (shapeMod-0.5)*0.05) / totalProb;
                
                if (rand < p_frond) {
                    lx = 0.85 * x + 0.04 * y;
                    ly = -0.04 * x + 0.85 * y + 1.6;
                } else if (rand < p_frond + p_left) {
                    lx = 0.2 * x - 0.26 * y;
                    ly = 0.23 * x + 0.22 * y + 1.6;
                } else {
                    lx = -0.15 * x + 0.28 * y;
                    ly = 0.26 * x + 0.24 * y + 0.44;
                }
                
                pVarTP.x += lx * scale * pAmount;
                pVarTP.y += ly * scale * pAmount;
                if (pContext.isPreserveZCoordinate()) {
                    pVarTP.z += pAmount * pAffineTP.z;
                }
                return;
        }

        // Apply distortion for a more organic look
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
            leafType = limitIntVal(Tools.FTOI(pValue), 0, 25);
        else if (PARAM_FILLED.equalsIgnoreCase(pName))
            filled = Tools.limitValue(pValue, 0.0, 1.0);
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
    public String[] getRessourceNames() {
      return ressourceNames;
    }

    @Override
    public byte[][] getRessourceValues() {
      return new byte[][] {leaftype_reference.getBytes()};
    }

    @Override
    public RessourceType getRessourceType(String pName) {
      if (RESSOURCE_LEAFTYPE_REFERENCE.equalsIgnoreCase(pName)) {
        return RessourceType.REFERENCE;
      }
      else throw new IllegalArgumentException(pName);
    }
    
    @Override
    public void randomize() {
    	leafType = (int) (Math.random() * 26);
    	filled = Math.random();
    	scale = Math.random() * 2.0 + 0.5;
    	if (Math.random() < 0.5) distort = Math.random() * 0.2 - 0.1;
    	else distort = Math.random() - 0.5;
    	shapeMod = Math.random() * 4.0 - 2.0;
    }

    @Override
    public String getName() {
        return "flora";
    }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_BASE_SHAPE};
    }
}
