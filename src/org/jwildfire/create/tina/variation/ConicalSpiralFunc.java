
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
 * JWildfire variation based on the parametric equations for a conical spiral.
 *
 * This variation interprets the input point's coordinates to drive the spiral's
 * generation, creating patterns that follow the path of a cone.
 * Includes Z-Axis modulation, twist/shear control, and built-in colorization.
 * This is a CPU-only version.
 */
public class ConicalSpiralFunc extends VariationFunc {
    private static final long serialVersionUID = 7L; // Incremented version

    // Spiral Shape Parameters
    private static final String PARAM_TURNS = "turns";
    private static final String PARAM_RADIUS = "radius";
    private static final String PARAM_HEIGHT = "height";
    private static final String PARAM_MODE = "mode";
    
    // Z-Axis Modulation Parameters
    private static final String PARAM_Z_WAVE_FREQ = "z_wave_freq";
    private static final String PARAM_Z_WAVE_AMP = "z_wave_amp";

    // Twist/Shear Parameter
    private static final String PARAM_TWIST = "twist";

    // Thickness Parameters
    private static final String PARAM_THICKNESS = "thickness";
    private static final String PARAM_SOLID = "solid";

    // Colorization Parameters
    private static final String PARAM_COLORIZE = "colorize";
    private static final String PARAM_COLOR_MODE = "colorMode";
    private static final String PARAM_COLOR_SPEED = "colorSpeed";
    private static final String PARAM_COLOR_OFFSET = "colorOffset";


    private static final String[] paramNames = {PARAM_TURNS, PARAM_RADIUS, PARAM_HEIGHT, PARAM_MODE, PARAM_Z_WAVE_FREQ, PARAM_Z_WAVE_AMP, PARAM_TWIST, PARAM_THICKNESS, PARAM_SOLID, PARAM_COLORIZE, PARAM_COLOR_MODE, PARAM_COLOR_SPEED, PARAM_COLOR_OFFSET};

    // Shape
    private double turns = 8.0;
    private double radius = 1.2;
    private double height = 1.0;
    private int mode = 0; // 0=x, 1=y, 2=z
    
    // Z-Modulation
    private double z_wave_freq = 0.0;
    private double z_wave_amp = 0.0;

    // Twist
    private double twist = 0.0;

    // Thickness
    private double thickness = 0.0;
    private int solid = 0; // 0=Flame mode, 1=Solid mode
    
    // Color
    private int colorize = 0; // 0=off, 1=on
    private int colorMode = 0; // 0=progression, 1=radius, 2=angle, 3=height
    private double colorSpeed = 1.0;
    private double colorOffset = 0.0;


    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        // We need a single parameter 't' to drive the spiral.
        double t;
        if (mode == 1) {
            t = fmod(fabs(pAffineTP.y), 1.0);
        } else if (mode == 2) {
            t = fmod(fabs(pAffineTP.z), 1.0);
        } else {
            t = fmod(fabs(pAffineTP.x), 1.0);
        }

        // Base spiral calculations
        double angle = t * M_2PI * this.turns;
        double currentRadius = t * this.radius;

        // Start with linear height and add sine wave modulation
        double z = t * this.height;
        if (this.z_wave_amp != 0.0) {
            z += sin(t * M_2PI * this.z_wave_freq) * this.z_wave_amp;
        }

        // Apply twist based on the calculated z-position
        double final_angle = angle;
        if (this.twist != 0.0) {
            final_angle += z * this.twist;
        }

        double x = currentRadius * cos(final_angle);
        double y = currentRadius * sin(final_angle);

        // Apply thickness
        if (this.thickness > 0.0) {
             if (this.solid > 0) {
                // "Solid" mode ON uses random scattering for a cloud-like effect.
                x += pContext.random() * this.thickness;
                y += pContext.random() * this.thickness;
                z += pContext.random() * this.thickness;
            } else {
                // "Solid" mode OFF uses the input coordinates for a structured, flame-like effect.
                x += pAffineTP.x * this.thickness;
                y += pAffineTP.y * this.thickness;
                z += pAffineTP.z * this.thickness;
            }
        }

        // Add the calculated point to the output, scaled by the variation amount
        pVarTP.x += x * pAmount;
        pVarTP.y += y * pAmount;
        pVarTP.z += z * pAmount;

        // Apply coloring if enabled
        if (this.colorize > 0) {
            double colorDriver = 0.0;
            switch(colorMode) {
                case 0: colorDriver = t; break;
                case 1: colorDriver = currentRadius; break;
                case 2: colorDriver = fmod(angle, M_2PI) / M_2PI; break;
                case 3: default: colorDriver = z; break;
            }
            pVarTP.color = fmod(fabs(colorDriver * this.colorSpeed + this.colorOffset), 1.0);
        }
    }

    @Override
    public String[] getParameterNames() {
        return paramNames;
    }

    @Override
    public Object[] getParameterValues() {
        return new Object[]{turns, radius, height, mode, z_wave_freq, z_wave_amp, twist, thickness, solid, colorize, colorMode, colorSpeed, colorOffset};
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_TURNS.equalsIgnoreCase(pName))
            turns = pValue;
        else if (PARAM_RADIUS.equalsIgnoreCase(pName))
            radius = pValue;
        else if (PARAM_HEIGHT.equalsIgnoreCase(pName))
            height = pValue;
        else if (PARAM_MODE.equalsIgnoreCase(pName))
            mode = limitIntVal(Tools.FTOI(pValue), 0, 3);
        else if (PARAM_Z_WAVE_FREQ.equalsIgnoreCase(pName))
            z_wave_freq = pValue;
        else if (PARAM_Z_WAVE_AMP.equalsIgnoreCase(pName))
            z_wave_amp = pValue;
        else if (PARAM_TWIST.equalsIgnoreCase(pName))
            twist = pValue;
        else if (PARAM_THICKNESS.equalsIgnoreCase(pName))
            thickness = pValue;
        else if (PARAM_SOLID.equalsIgnoreCase(pName))
            solid = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_COLORIZE.equalsIgnoreCase(pName))
            colorize = limitIntVal(Tools.FTOI(pValue), 0, 1);
        else if (PARAM_COLOR_MODE.equalsIgnoreCase(pName))
            colorMode = limitIntVal(Tools.FTOI(pValue), 0, 4);
        else if (PARAM_COLOR_SPEED.equalsIgnoreCase(pName))
            colorSpeed = pValue;
        else if (PARAM_COLOR_OFFSET.equalsIgnoreCase(pName))
            colorOffset = pValue;
        else
            throw new IllegalArgumentException(pName);
    }

    @Override
    public String getName() {
        return "conicalSpiral";
    }
    
    @Override
    public void randomize() {
    	turns = Math.random() * 50.0 - 25.0;
    	radius = Math.random() * 16.0 - 8.0;
    	height = Math.random() * 16.0 - 8.0;
    	z_wave_freq = Math.random() * 16.0 - 8.0;
    	z_wave_amp = Math.random() * 10.0 - 5.0;
    	twist = Math.random() * 10.0 - 5.0;
    	solid = (int) (Math.random() * 2);
    	if (solid > 0) thickness = Math.random() * 0.5 + 0.2;
    	else thickness = Math.random() * 0.1;
    	colorize = (int) (Math.random() * 2);
    	colorMode = (int) (Math.random() * 4);
    	colorSpeed = Math.random() * 3.0;
    	colorOffset = Math.random();
    }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_DC};
    }
}
