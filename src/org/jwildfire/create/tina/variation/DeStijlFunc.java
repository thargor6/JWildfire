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
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;

import static java.lang.Math.*;

public class DeStijlFunc extends VariationFunc {
    private static final long serialVersionUID = 1L;

    private static final String PARAM_MAX_SIZE = "max_size";
    private static final String PARAM_MIN_SIZE = "min_size";
    private static final String PARAM_CHAOS = "chaos";
    private static final String PARAM_LINE_THICKNESS = "line_thickness";
    private static final String PARAM_SEED = "seed";

    private static final String[] paramNames = { PARAM_MAX_SIZE, PARAM_MIN_SIZE, PARAM_CHAOS, PARAM_LINE_THICKNESS, PARAM_SEED };

    // Defaults
    private double max_size = 0.5;
    private double min_size = 0.1;
    private double chaos = 5.0;
    private double line_thickness = 0.02;
    private int seed = 12345;

    private transient MarsagliaRandomGenerator cell_rand = new MarsagliaRandomGenerator();
    
    // Hash function to get a consistent random value for a grid cell
    private double hash(int n) {
        int h = seed + n;
        h = (h ^ 61) ^ (h >> 16);
        h = h + (h << 3);
        h = h ^ (h >> 4);
        h = h * 0x27d4eb2d;
        h = h ^ (h >> 15);
        return (double)(h & 0x7fffffff) / (double)0x7fffffff;
    }

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        
        double x = pAffineTP.x;
        double y = pAffineTP.y;
        
        // Determine the "neighborhood" the point is in
        int region_ix = (int)floor(x / chaos);
        int region_iy = (int)floor(y / chaos);
        
        // Use a hash of the neighborhood to determine the local grid size
        double size_hash = hash(region_ix * 31337 ^ region_iy * 8191);
        double local_grid_size = min_size + (max_size - min_size) * size_hash;
        
        if (local_grid_size < 1.0E-6) local_grid_size = 1.0E-6;
        
        // Snap the point to the local, non-uniform grid
        double snapped_x = round(x / local_grid_size) * local_grid_size;
        double snapped_y = round(y / local_grid_size) * local_grid_size;

        double dist_to_line_x = abs(x - snapped_x);
        double dist_to_line_y = abs(y - snapped_y);

        double half_thickness = line_thickness * 0.025;

        // Check if the point is on a line
        if (dist_to_line_x < half_thickness || dist_to_line_y < half_thickness) {
            // It's a line, color it black
            pVarTP.color = 0.0;
            // Snap the point directly to the line for a clean look
            if(dist_to_line_x < dist_to_line_y) {
                 pVarTP.x += x * pAmount;
                 pVarTP.y += y * pAmount;
            } else {
                 pVarTP.x += x * pAmount;
                 pVarTP.y += y * pAmount;
            }
            
            if (pContext.isPreserveZCoordinate()) pVarTP.z += pAffineTP.z * pAmount;

        } else { // It's inside a cell, so color it
            
            // Get a deterministic random value for this specific cell
            int ix = (int)round(x / local_grid_size);
            int iy = (int)round(y / local_grid_size);
            long cell_seed = seed + (long)ix * 73856093L ^ (long)iy * 19349663L;
            cell_rand.randomize(cell_seed);
            double rand_val = cell_rand.random();

            // Use the random value to index the gradient
            pVarTP.color = rand_val; 
            
            // Pass the original point through
            pVarTP.x += x * pAmount;
            pVarTP.y += y * pAmount;
            if (pContext.isPreserveZCoordinate()) pVarTP.z += pAffineTP.z * pAmount;
        }
    }

    @Override
    public String[] getParameterNames() { return paramNames; }

    @Override
    public Object[] getParameterValues() {
        return new Object[]{ max_size, min_size, chaos, line_thickness, seed };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_MAX_SIZE.equalsIgnoreCase(pName)) max_size = pValue;
        else if (PARAM_MIN_SIZE.equalsIgnoreCase(pName)) min_size = pValue;
        else if (PARAM_CHAOS.equalsIgnoreCase(pName)) chaos = pValue;
        else if (PARAM_LINE_THICKNESS.equalsIgnoreCase(pName)) line_thickness = pValue;
        else if (PARAM_SEED.equalsIgnoreCase(pName)) seed = (int) pValue;
        else throw new IllegalArgumentException(pName);
    }
    
    @Override
    public void randomize() {
    	max_size = Math.random() * 3.0 + 0.25;
    	min_size = Math.random() * (max_size - 0.1) + 0.01;
    	line_thickness = Math.random() * 3.0;
    	chaos = Math.random() * 10 + 0.1;
    	seed = (int) (Math.random() * 1000000);
    }
    
    @Override
    public String getName() { return "de_stijl"; }

    @Override
    public VariationFuncType[] getVariationTypes() { return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_DC, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN}; }
}
