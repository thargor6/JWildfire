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

public class RomanescoFunc extends VariationFunc {
    private static final long serialVersionUID = 1L;

    // Main Parameters
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_RECURSION_DEPTH = "recursion_depth";
    
    // Branching Parameters
    private static final String PARAM_NUM_ARMS = "num_arms";
    private static final String PARAM_ARM_SPREAD = "arm_spread";
    private static final String PARAM_ARM_ELEVATION = "arm_elevation";
    private static final String PARAM_ARM_TWIST = "arm_twist";

    // Floret (cone) parameters
    private static final String PARAM_FLORET_COUNT = "floret_count";
    private static final String PARAM_FLORET_SCALE = "floret_scale";
    private static final String PARAM_PATTERN_SPREAD = "pattern_spread";
    private static final String PARAM_SPIRAL_TWIST = "spiral_twist";
    private static final String PARAM_CONE_STEEPNESS = "cone_steepness";
    private static final String PARAM_FLORET_DETAIL_SIZE = "floret_detail_size";
    private static final String PARAM_FLORET_SHAPE = "floret_shape";
    
    // New Rotation Parameters
    private static final String PARAM_PITCH = "pitch";
    private static final String PARAM_YAW = "yaw";
    private static final String PARAM_ROLL = "roll";

    // Coloring Parameters
    private static final String PARAM_COLOR_MODE = "color_mode";
    private static final String PARAM_SOLID_COLOR_IDX = "solid_color_idx";
    private static final String PARAM_COLOR_RANGE_MIN = "color_range_min";
    private static final String PARAM_COLOR_RANGE_MAX = "color_range_max";

    private static final String RESSOURCE_DESCRIPTION = "description";

    private static final String[] paramNames = {
            PARAM_SIZE,
            PARAM_RECURSION_DEPTH,
            PARAM_NUM_ARMS, PARAM_ARM_SPREAD, PARAM_ARM_ELEVATION, PARAM_ARM_TWIST,
            PARAM_FLORET_COUNT,
            PARAM_FLORET_SCALE,
            PARAM_PATTERN_SPREAD,
            PARAM_SPIRAL_TWIST,
            PARAM_CONE_STEEPNESS,
            PARAM_FLORET_DETAIL_SIZE,
            PARAM_FLORET_SHAPE,
            PARAM_PITCH, PARAM_YAW, PARAM_ROLL, // New
            PARAM_COLOR_MODE,
            PARAM_SOLID_COLOR_IDX,
            PARAM_COLOR_RANGE_MIN,
            PARAM_COLOR_RANGE_MAX
    };
    private static final String[] ressourceNames = {RESSOURCE_DESCRIPTION};

    private double size = 0.5;
    private int recursion_depth = 7;
    private int num_arms = 1;
    private double arm_spread = 1.0;
    private double arm_elevation = 45.0;
    private double arm_twist = 0.0;
    private int floret_count = 100;
    private double floret_scale = 0.28;
    private double pattern_spread = 0.4;
    private double spiral_twist = 1.0;
    private double cone_steepness = 1.0;
    private double floret_detail_size = 0.1;
    private int floret_shape = 0;
    private double pitch = 180.0; // New
    private double yaw = 0.0;   // New
    private double roll = 0.0;  // New
    private int color_mode = 0;
    private double solid_color_idx = 0.5;
    private double color_range_min = 0.0;
    private double color_range_max = 9.0;

    private String description = "org.jwildfire.create.tina.variation.reference.ReferenceFile romanesco.txt";

    // Helper methods
    private void cross(XYZPoint res, XYZPoint v1, XYZPoint v2) { res.x = v1.y * v2.z - v1.z * v2.y; res.y = v1.z * v2.x - v1.x * v2.z; res.z = v1.x * v2.y - v1.y * v2.x; }
    private void normalize(XYZPoint v) { double len = Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z); if (len > 1e-9) { v.x /= len; v.y /= len; v.z /= len; } }


    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        
        final double GOLDEN_ANGLE = Math.PI * (3.0 - Math.sqrt(5.0));

        // Initialize transformation
        double current_scale = this.size;
        XYZPoint pos = new XYZPoint();
        XYZPoint axis_x = new XYZPoint(); axis_x.x=1;
        XYZPoint axis_y = new XYZPoint(); axis_y.y=1;
        XYZPoint axis_z = new XYZPoint(); axis_z.z=1;
        
        // Arm Placement Logic
        if (num_arms > 1) {
            int arm_index = (int)(pContext.random() * num_arms);
            double arm_base_angle = (double)arm_index / (double)num_arms * 2.0 * Math.PI;
            
            pos.x = this.arm_spread * cos(arm_base_angle);
            pos.y = this.arm_spread * sin(arm_base_angle);
            pos.z = 0;

            double elevation_rad = this.arm_elevation * Math.PI / 180.0;
            double twist_rad = pos.y * this.arm_twist;
            double arm_angle = arm_base_angle + twist_rad;

            axis_z.x = cos(arm_angle) * cos(elevation_rad);
            axis_z.y = sin(arm_angle) * cos(elevation_rad);
            axis_z.z = sin(elevation_rad);
            
            XYZPoint up_vec = new XYZPoint(); up_vec.z=1;
            if (Math.abs(axis_z.z) > 0.999) { up_vec.x=1; up_vec.z=0;}
            
            cross(axis_x, axis_z, up_vec);
            normalize(axis_x);
            cross(axis_y, axis_z, axis_x);
        }

        // Iterative recursion
        for (int i = 0; i < this.recursion_depth; i++) {
            int floret_index = (int)(pow(pContext.random(), 1.5) * this.floret_count);
            double spiral_angle = floret_index * GOLDEN_ANGLE * this.spiral_twist;
            double r = this.pattern_spread * sqrt(floret_index);

            double local_x = r * cos(spiral_angle);
            double local_y = r * sin(spiral_angle);
            double local_z = r * this.cone_steepness;

            pos.x += (axis_x.x * local_x + axis_y.x * local_y + axis_z.x * local_z) * current_scale;
            pos.y += (axis_x.y * local_x + axis_y.y * local_y + axis_z.y * local_z) * current_scale;
            pos.z += (axis_x.z * local_x + axis_y.z * local_y + axis_z.z * local_z) * current_scale;

            XYZPoint normal = new XYZPoint();
            normal.x = -this.cone_steepness * cos(spiral_angle);
            normal.y = -this.cone_steepness * sin(spiral_angle);
            normal.z = 1.0;
            normalize(normal);

            XYZPoint new_axis_z = new XYZPoint();
            new_axis_z.x = axis_x.x * normal.x + axis_y.x * normal.y + axis_z.x * normal.z;
            new_axis_z.y = axis_x.y * normal.x + axis_y.y * normal.y + axis_z.y * normal.z;
            new_axis_z.z = axis_x.z * normal.x + axis_y.z * normal.y + axis_z.z * normal.z;
            
            axis_z = new_axis_z;
            normalize(axis_z);
            
            XYZPoint up_vec = new XYZPoint(); up_vec.y=1;
            if (Math.abs(axis_z.y) > 0.999) { up_vec.x=1; up_vec.y=0; }
            
            cross(axis_x, up_vec, axis_z);
            normalize(axis_x);
            cross(axis_y, axis_z, axis_x);

            current_scale *= this.floret_scale;
        }

        // Generate Final Geometry
        double local_x_final = 0, local_y_final = 0, local_z_final = 0;
        double shape_size = floret_detail_size * current_scale;

        switch(floret_shape) {
            case 0: // Sphere (default)
                double r_x = pContext.random() - 0.5;
                double r_y = pContext.random() - 0.5;
                double r_z = pContext.random() - 0.5;
                double r_len = sqrt(r_x*r_x + r_y*r_y + r_z*r_z);
                if (r_len > 1e-6) {
                    double bump_radius = shape_size * pContext.random();
                    local_x_final = (r_x/r_len) * bump_radius;
                    local_y_final = (r_y/r_len) * bump_radius;
                    local_z_final = (r_z/r_len) * bump_radius;
                }
                break;
            case 1: // Cube
                local_x_final = (pContext.random() - 0.5) * shape_size;
                local_y_final = (pContext.random() - 0.5) * shape_size;
                local_z_final = (pContext.random() - 0.5) * shape_size;
                break;
            case 2: // Spike
                local_z_final = shape_size * pContext.random();
                break;
            case 3: // Ring
                double ring_angle = pContext.random() * 2.0 * Math.PI;
                local_x_final = shape_size * cos(ring_angle);
                local_y_final = shape_size * sin(ring_angle);
                break;
        }

        double final_x = pos.x + (axis_x.x * local_x_final + axis_y.x * local_y_final + axis_z.x * local_z_final);
        double final_y = pos.y + (axis_x.y * local_x_final + axis_y.y * local_y_final + axis_z.y * local_z_final);
        double final_z = pos.z + (axis_x.z * local_x_final + axis_y.z * local_y_final + axis_z.z * local_z_final);
        
        // Apply Rotations to the final calculated point
        if (pitch != 0.0 || yaw != 0.0 || roll != 0.0) {
            double cos_p = Math.cos(Math.toRadians(pitch));
            double sin_p = Math.sin(Math.toRadians(pitch));
            double cos_y = Math.cos(Math.toRadians(yaw));
            double sin_y = Math.sin(Math.toRadians(yaw));
            double cos_r = Math.cos(Math.toRadians(roll));
            double sin_r = Math.sin(Math.toRadians(roll));

            // Yaw (Y-axis rotation)
            double tempX = final_x * cos_y - final_z * sin_y;
            double tempZ = final_x * sin_y + final_z * cos_y;
            final_x = tempX;
            final_z = tempZ;
            
            // Pitch (X-axis rotation)
            double tempY = final_y * cos_p - final_z * sin_p;
            tempZ = final_y * sin_p + final_z * cos_p;
            final_y = tempY;
            final_z = tempZ;

            // Roll (Z-axis rotation)
            tempX = final_x * cos_r - final_y * sin_r;
            tempY = final_x * sin_r + final_y * cos_r;
            final_x = tempX;
            final_y = tempY;
        }
        
        // Coloring Logic
        double color_index = 0.5;
        double range = color_range_max - color_range_min;

        switch(color_mode) {
            case 0: color_index = solid_color_idx; break;
            case 1: 
                double dist_from_center = sqrt(final_x*final_x + final_y*final_y + final_z*final_z);
                if(range > 1e-6) color_index = (dist_from_center - color_range_min) / range;
                break;
            case 2:
                double radius = sqrt(final_x*final_x + final_y*final_y);
                if(range > 1e-6) color_index = (radius - color_range_min) / range;
                break;
            case 3:
                if(range > 1e-6) color_index = (final_z - color_range_min) / range;
                break;
        }
        
        pVarTP.color = Math.max(0.0, Math.min(1.0, color_index));
        pVarTP.x += final_x * pAmount;
        pVarTP.y += final_y * pAmount;
        pVarTP.z += final_z * pAmount;
    }

    @Override
    public String[] getParameterNames() { return paramNames; }

    @Override
    public Object[] getParameterValues() {
        return new Object[]{
                size, recursion_depth,
                num_arms, arm_spread, arm_elevation, arm_twist,
                floret_count, floret_scale, pattern_spread, spiral_twist, cone_steepness, floret_detail_size, floret_shape,
                pitch, yaw, roll, // New
                color_mode, solid_color_idx, color_range_min, color_range_max
        };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_SIZE.equalsIgnoreCase(pName)) size = pValue;
        else if (PARAM_RECURSION_DEPTH.equalsIgnoreCase(pName)) recursion_depth = (int) pValue;
        else if (PARAM_NUM_ARMS.equalsIgnoreCase(pName)) num_arms = (int) pValue;
        else if (PARAM_ARM_SPREAD.equalsIgnoreCase(pName)) arm_spread = pValue;
        else if (PARAM_ARM_ELEVATION.equalsIgnoreCase(pName)) arm_elevation = pValue;
        else if (PARAM_ARM_TWIST.equalsIgnoreCase(pName)) arm_twist = pValue;
        else if (PARAM_FLORET_COUNT.equalsIgnoreCase(pName)) floret_count = (int) pValue;
        else if (PARAM_FLORET_SCALE.equalsIgnoreCase(pName)) floret_scale = pValue;
        else if (PARAM_PATTERN_SPREAD.equalsIgnoreCase(pName)) pattern_spread = pValue;
        else if (PARAM_SPIRAL_TWIST.equalsIgnoreCase(pName)) spiral_twist = pValue;
        else if (PARAM_CONE_STEEPNESS.equalsIgnoreCase(pName)) cone_steepness = pValue;
        else if (PARAM_FLORET_DETAIL_SIZE.equalsIgnoreCase(pName)) floret_detail_size = pValue;
        else if (PARAM_FLORET_SHAPE.equalsIgnoreCase(pName)) floret_shape = limitIntVal(Tools.FTOI(pValue), 0, 3);
        else if (PARAM_PITCH.equalsIgnoreCase(pName)) pitch = pValue; // New
        else if (PARAM_YAW.equalsIgnoreCase(pName)) yaw = pValue;     // New
        else if (PARAM_ROLL.equalsIgnoreCase(pName)) roll = pValue;   // New
        else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName)) color_mode = limitIntVal(Tools.FTOI(pValue), 0, 3);
        else if (PARAM_SOLID_COLOR_IDX.equalsIgnoreCase(pName)) solid_color_idx = pValue;
        else if (PARAM_COLOR_RANGE_MIN.equalsIgnoreCase(pName)) color_range_min = pValue;
        else if (PARAM_COLOR_RANGE_MAX.equalsIgnoreCase(pName)) color_range_max = pValue;
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
    	size = Math.random() * 1.75 + 0.25;
    	recursion_depth = (int) (Math.random() * 12 + 4);
    	if (Math.random() < 0.4) num_arms = 1;
    	else num_arms = (int) (Math.random() * 6 + 1);
    	if (Math.random() < 0.8) arm_spread = Math.random() * 3.0;
    	else arm_spread = Math.random() * 8.0 - 8.0;
    	arm_elevation = Math.random() * 360.0 - 180.0;
    	arm_twist = Math.random() * 4.8 - 2.4;
    	floret_count = (int) (Math.random() * 225 + 25);
    	floret_scale = Math.random() * 0.4 + 0.1;
    	pattern_spread = Math.random();
    	spiral_twist = Math.random() * 2.0;
    	if (Math.random() < 0.8) cone_steepness = Math.random() + 0.5;
    	else cone_steepness = Math.random() * 4.0 - 2.0;
    	floret_detail_size = Math.random() * 5.0 + 0.1;
    	floret_shape = (int) (Math.random() * 4);
    	pitch = Math.random() * 360.0 - 180.0;
    	yaw = Math.random() * 360.0 - 180.0;
    	roll = Math.random() * 360.0 - 180.0;
    	color_mode = (int) (Math.random() * 4);
    	solid_color_idx = Math.random();
    	color_range_min = Math.random() * 7.5;
    	color_range_max = Math.random() * (15.0 - color_range_min) + color_range_min;
    }
    
    @Override
    public String getName() { return "romanesco"; }

    @Override
    public VariationFuncType[] getVariationTypes() { 
    	return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_DC}; 
    	}
}