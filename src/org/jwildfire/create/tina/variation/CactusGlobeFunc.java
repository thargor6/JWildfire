
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

public class CactusGlobeFunc extends VariationFunc {
    private static final long serialVersionUID = 1L;

    // Mode Selection
    private static final String PARAM_PATTERN_MODE = "pattern_mode"; // 0=Ridge, 1=Intersection
    private static final String PARAM_GENERATOR_MODE = "generator_mode";
    // Main Parameters
    private static final String PARAM_SIZE = "size";
    
    // Rib Pattern 1
    private static final String PARAM_RIBS_1 = "ribs_1";
    private static final String PARAM_RIB_DEPTH_1 = "ribDepth_1";
    private static final String PARAM_SPIRAL_1 = "spiral_1";

    // Rib Pattern 2 (for Intersection mode)
    private static final String PARAM_RIBS_2 = "ribs_2";
    private static final String PARAM_RIB_DEPTH_2 = "ribDepth_2";
    private static final String PARAM_SPIRAL_2 = "spiral_2";

    // Feature Parameters
    private static final String PARAM_AREOLES_PER_RIB = "areoles_per_rib";
    private static final String PARAM_AREOLE_DENSITY = "areole_density";
    private static final String PARAM_AREOLE_FOCUS = "areole_focus";
    private static final String PARAM_AREOLE_SIZE = "areole_size";
    private static final String PARAM_SPIKE_LENGTH = "spike_length";
    private static final String PARAM_SPIKE_ANGLE_RAND = "spike_angle_rand";
    private static final String PARAM_SPIKE_DROOP = "spike_droop";
    
    // Recursive Pup Params
    private static final String PARAM_RECURSION_DEPTH = "recursion_depth";
    private static final String PARAM_PUP_CHANCE = "pup_chance";
    private static final String PARAM_PUPS_COUNT = "pups_count";
    private static final String PARAM_PUP_SIZE = "pup_size";
    private static final String PARAM_PUPS_SPREAD = "pups_spread";
    private static final String PARAM_PUPS_VERTICAL_OFFSET = "pups_vertical_offset";

    private static final String PARAM_FEATURE_COLOR = "feature_color";

    private static final String RESSOURCE_DESCRIPTION = "description";

    private static final String[] paramNames = {
            PARAM_PATTERN_MODE, PARAM_GENERATOR_MODE, PARAM_SIZE,
            PARAM_RIBS_1, PARAM_RIB_DEPTH_1, PARAM_SPIRAL_1,
            PARAM_RIBS_2, PARAM_RIB_DEPTH_2, PARAM_SPIRAL_2,
            PARAM_AREOLES_PER_RIB,
            PARAM_AREOLE_DENSITY, PARAM_AREOLE_FOCUS,
            PARAM_AREOLE_SIZE, PARAM_SPIKE_LENGTH, PARAM_SPIKE_ANGLE_RAND, PARAM_SPIKE_DROOP,
            PARAM_RECURSION_DEPTH, PARAM_PUP_CHANCE, PARAM_PUPS_COUNT, PARAM_PUP_SIZE, PARAM_PUPS_SPREAD, PARAM_PUPS_VERTICAL_OFFSET,
            PARAM_FEATURE_COLOR
    };
    private static final String[] ressourceNames = {RESSOURCE_DESCRIPTION};

    private int pattern_mode = 0;
    private int generator_mode = 1;
    private double size = 1.0;
    private double ribs_1 = 12.0;
    private double ribDepth_1 = 0.1;
    private double spiral_1 = 0.5;
    private double ribs_2 = 0.0;
    private double ribDepth_2 = 0.0;
    private double spiral_2 = 0.0;
    private double areoles_per_rib = 10.0;
    private double areole_density = 1.0;
    private double areole_focus = 16.0;
    private double areole_size = 0.05;
    private double spike_length = 0.1;
    private double spike_angle_rand = 0.0;
    private double spike_droop = 0.0;
    private int recursion_depth = 3;
    private double pup_chance = 0.3;
    private int pups_count = 3;
    private double pup_size = 0.5;
    private double pups_spread = 1.0;
    private double pups_vertical_offset = -0.5;
    private double feature_color = 1.0;

    private String description = "org.jwildfire.create.tina.variation.reference.ReferenceFile cactusGlobe.txt";

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        
        double current_size = size;
        double total_offset_x = 0;
        double total_offset_y = 0;
        double total_offset_z = 0;

        // This loop iteratively calculates the transformation for a point in the fractal cluster.
        for(int i = 0; i < recursion_depth; i++) {
            if (pContext.random() < pup_chance) {
                // Descend to the next pup level
                int pup_idx = (int)(pContext.random() * pups_count);
                double angle = (double)pup_idx / (double)pups_count * 2.0 * Math.PI;
                
                double spread = current_size * pups_spread;
                double vertical_offset = current_size * pups_vertical_offset;

                total_offset_x += spread * cos(angle);
                total_offset_y += spread * sin(angle);
                total_offset_z += vertical_offset;

                current_size *= pup_size;
            } else {
                break;
            }
        }
        
        // finalCoords is a double array holding {x, y, z, color_flag}
        double[] finalCoords = calculateCactusPoint(pContext, pAffineTP, current_size);
        
        // Apply color if the point was a feature
        if(finalCoords[3] > 0) {
            pVarTP.color = feature_color;
        }

        // Add the accumulated offset to place the cactus in the cluster
        pVarTP.x += (finalCoords[0] + total_offset_x) * pAmount;
        pVarTP.y += (finalCoords[1] + total_offset_y) * pAmount;
        pVarTP.z += (finalCoords[2] + total_offset_z) * pAmount;
    }

    // This self-contained function calculates a single point on a cactus of a given size
    private double[] calculateCactusPoint(FlameTransformationContext pContext, XYZPoint pAffineTP, double current_size)
    {
        double base_rho, theta, phi;

        if (generator_mode == 1) {
            base_rho = current_size * pow(pContext.random(), 1.0 / 3.0);
            theta = pContext.random() * 2.0 * Math.PI;
            phi = acos(2.0 * pContext.random() - 1.0);
        } else {
            // Scale affine point to current size context
            double p_scale = (size > 0) ? current_size / size : current_size;
            XYZPoint scaledAffineTP = new XYZPoint();
            scaledAffineTP.x = pAffineTP.x * p_scale;
            scaledAffineTP.y = pAffineTP.y * p_scale;
            scaledAffineTP.z = pAffineTP.z * p_scale;

            base_rho = sqrt(scaledAffineTP.x * scaledAffineTP.x + scaledAffineTP.y * scaledAffineTP.y + scaledAffineTP.z * scaledAffineTP.z);
            if (base_rho == 0.0) return new double[]{0,0,0,-1.0};
            theta = atan2(scaledAffineTP.y, scaledAffineTP.x);
            phi = acos(scaledAffineTP.z / base_rho);
        }

        double sinPhi = sin(phi);
        double cosPhi = cos(phi);
        double final_x, final_y, final_z;
        boolean hasFeature = false;

        if (pattern_mode == 0) {
            // --- MODE 0: Single Spiral with Ridge Grid ---
            double rib_cos = cos(theta * ribs_1);
            double effective_ribDepth = ribDepth_1 * sinPhi;
            double cactus_rho = base_rho + (effective_ribDepth * rib_cos);
            double cactus_theta = theta + (phi * spiral_1);
            
            final_x = cactus_rho * sinPhi * cos(cactus_theta);
            final_y = cactus_rho * sinPhi * sin(cactus_theta);
            final_z = cactus_rho * cosPhi;
            
            if (areoles_per_rib > 0) {
                double rib_width = (2.0 * Math.PI) / ribs_1;
                double rib_center_theta = round(theta / rib_width) * rib_width;
                double areole_cell_height = Math.PI / areoles_per_rib;
                double areole_center_phi = floor(phi / areole_cell_height) * areole_cell_height + (areole_cell_height * 0.5);
                double dist_to_areole_center = acos(sin(phi)*sin(areole_center_phi)*cos(theta-rib_center_theta) + cos(phi)*cos(areole_center_phi));

                if (dist_to_areole_center < areole_size) {
                    hasFeature = true;
                    double[] spike_vec = applySpikeRealism(pContext, final_x, final_y, final_z);
                    final_x += spike_vec[0];
                    final_y += spike_vec[1];
                    final_z += spike_vec[2];
                }
            }
        } else {
            // --- MODE 1: Dual Spiral with Intersections ---
            double theta_1 = theta + (phi * spiral_1);
            double rib_cos_1 = cos(theta_1 * ribs_1);
            double theta_2 = theta + (phi * spiral_2);
            double rib_cos_2 = cos(theta_2 * ribs_2);

            double effective_ribDepth_1 = ribDepth_1 * sinPhi;
            double effective_ribDepth_2 = ribDepth_2 * sinPhi;
            double cactus_rho = base_rho + (effective_ribDepth_1 * rib_cos_1) + (effective_ribDepth_2 * rib_cos_2);
            
            final_x = cactus_rho * sinPhi * cos(theta);
            final_y = cactus_rho * sinPhi * sin(theta);
            final_z = cactus_rho * cosPhi;
            
            if (areole_density > 0 && pContext.random() < areole_density) {
                double intersection_factor = pow(((rib_cos_1 + 1.0)/2.0) * ((rib_cos_2 + 1.0)/2.0), areole_focus);
                if(pContext.random() < intersection_factor) {
                    hasFeature = true;
                    double[] spike_vec = applySpikeRealism(pContext, final_x, final_y, final_z);
                    final_x += spike_vec[0];
                    final_y += spike_vec[1];
                    final_z += spike_vec[2];
                }
            }
        }
        
        return new double[]{final_x, final_y, final_z, hasFeature ? 1.0 : -1.0};
    }
    
    // Helper method to apply the realistic spike logic
    private double[] applySpikeRealism(FlameTransformationContext pContext, double x, double y, double z) {
        double current_spike_length = spike_length * pContext.random();
        double body_len = sqrt(x*x + y*y + z*z);
        if(body_len < 1e-6) return new double[]{0,0,0};

        double nx = x/body_len;
        double ny = y/body_len;
        double nz = z/body_len;

        double[] spike_vec = {nx * current_spike_length, ny * current_spike_length, nz * current_spike_length};

        // Apply Droop
        spike_vec[2] -= spike_droop * current_spike_length;
        
        // Apply Angle Randomness
        if(spike_angle_rand > 0) {
            double r_x = pContext.random() - 0.5;
            double r_y = pContext.random() - 0.5;
            double r_z = pContext.random() - 0.5;
            double r_len = sqrt(r_x*r_x + r_y*r_y + r_z*r_z);
            if(r_len > 1e-6) {
                double splay_magnitude = spike_angle_rand * current_spike_length * pContext.random();
                spike_vec[0] += (r_x/r_len) * splay_magnitude;
                spike_vec[1] += (r_y/r_len) * splay_magnitude;
                spike_vec[2] += (r_z/r_len) * splay_magnitude;
            }
        }
        return spike_vec;
    }

    @Override
    public String[] getParameterNames() { return paramNames; }

    @Override
    public Object[] getParameterValues() {
        return new Object[]{
                pattern_mode, generator_mode, size,
                ribs_1, ribDepth_1, spiral_1,
                ribs_2, ribDepth_2, spiral_2,
                areoles_per_rib,
                areole_density, areole_focus,
                areole_size, spike_length, spike_angle_rand, spike_droop,
                recursion_depth, pup_chance, pups_count, pup_size, pups_spread, pups_vertical_offset,
                feature_color
        };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_PATTERN_MODE.equalsIgnoreCase(pName)) pattern_mode = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_GENERATOR_MODE.equalsIgnoreCase(pName)) generator_mode = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_SIZE.equalsIgnoreCase(pName)) size = pValue;
        else if (PARAM_RIBS_1.equalsIgnoreCase(pName)) ribs_1 = pValue;
        else if (PARAM_RIB_DEPTH_1.equalsIgnoreCase(pName)) ribDepth_1 = pValue;
        else if (PARAM_SPIRAL_1.equalsIgnoreCase(pName)) spiral_1 = pValue;
        else if (PARAM_RIBS_2.equalsIgnoreCase(pName)) ribs_2 = pValue;
        else if (PARAM_RIB_DEPTH_2.equalsIgnoreCase(pName)) ribDepth_2 = pValue;
        else if (PARAM_SPIRAL_2.equalsIgnoreCase(pName)) spiral_2 = pValue;
        else if (PARAM_AREOLES_PER_RIB.equalsIgnoreCase(pName)) areoles_per_rib = pValue;
        else if (PARAM_AREOLE_DENSITY.equalsIgnoreCase(pName)) areole_density = limitVal(pValue, 0.0, 1.0);
        else if (PARAM_AREOLE_FOCUS.equalsIgnoreCase(pName)) areole_focus = pValue;
        else if (PARAM_AREOLE_SIZE.equalsIgnoreCase(pName)) areole_size = pValue;
        else if (PARAM_SPIKE_LENGTH.equalsIgnoreCase(pName)) spike_length = pValue;
        else if (PARAM_SPIKE_ANGLE_RAND.equalsIgnoreCase(pName)) spike_angle_rand = pValue;
        else if (PARAM_SPIKE_DROOP.equalsIgnoreCase(pName)) spike_droop = pValue;
        else if (PARAM_RECURSION_DEPTH.equalsIgnoreCase(pName)) recursion_depth = limitIntVal(Tools.FTOI(pValue), 0, 8);
        else if (PARAM_PUP_CHANCE.equalsIgnoreCase(pName)) pup_chance = limitVal(pValue, 0.0, 1.0);
        else if (PARAM_PUPS_COUNT.equalsIgnoreCase(pName)) pups_count = (int)pValue;
        else if (PARAM_PUP_SIZE.equalsIgnoreCase(pName)) pup_size = pValue;
        else if (PARAM_PUPS_SPREAD.equalsIgnoreCase(pName)) pups_spread = pValue;
        else if (PARAM_PUPS_VERTICAL_OFFSET.equalsIgnoreCase(pName)) pups_vertical_offset = pValue;
        else if (PARAM_FEATURE_COLOR.equalsIgnoreCase(pName)) feature_color = limitVal(pValue, 0.0, 1.0);
        else throw new IllegalArgumentException(pName);
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
    public void randomize() {
    	pattern_mode = (int) (Math.random() * 2);
    	// Don't change generator_mode
    	size = Math.random() * 1.5 + 0.5;
    	if (Math.random() < 0.8) ribs_1 = (int) (Math.random() * 18 + 3);
    	else ribs_1 = Math.random() * 22.0 + 3.0;
    	if (Math.random() < 0.9) ribDepth_1 = Math.random() * 0.6;
    	else ribDepth_1 = Math.random() * 2.0 - 1.0;
    	spiral_1 = Math.random() * 3.0 - 1.5;
    	double r = Math.random();
    	if (r < 0.25) ribs_2 = 0;
    	else if (r < 0.65) ribs_2 = (int) (Math.random() * 18 + 3);
    	else ribs_2 = Math.random() * 22.0 + 3.0;
    	if (Math.random() < 0.9) ribDepth_2 = Math.random() * 0.6;
    	else ribDepth_2 = Math.random() * 2.0 - 1.0;
    	if (Math.random() < 0.5) spiral_2 = -spiral_1;
    	else spiral_2 = Math.random() * 3.0 - 1.5;
    	areoles_per_rib = Math.random() * 17.0 + 3.0;
    	areole_density = Math.random();
    	areole_focus = Math.random() * 19.0 + 1.0;
    	areole_size= Math.random() * 25.0;
    	spike_length = Math.random();
    	spike_angle_rand = Math.random() * 0.5;
    	spike_droop = Math.random() * 0.5;
    	recursion_depth = (int) (Math.random() * 5);
    	pup_chance = Math.random() * 0.75 + 0.1;
    	pups_count = (int) (Math.random() * 11);
    	pup_size = Math.random() * 0.8;
    	pups_spread = Math.random() * 2.5 + 0.5;
    	pups_vertical_offset = Math.random() * 2.0 - 1.0;
    	feature_color = Math.random();
    }
    
    @Override
    public String getName() { return "cactusGlobe"; }

    @Override
    public VariationFuncType[] getVariationTypes() { 
    	return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE}; 
    }
}