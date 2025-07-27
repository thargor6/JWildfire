
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
 * Lesser General Public License for more details *
 * You should have received a copy of the GNU Lesser General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */
package org.jwildfire.create.tina.variation;

import java.util.Random;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import java.lang.Math; // Using standard Math functions

/**
 * Drunken Tiles Variation for JWildfire.
 *
 * Applies LazyJess+Twist effects locally around centers based on a grid,
 * where each center's position is randomly perturbed ("drunken" tiles).
 * Confined within various boundary shapes (including Flower, StarN, Cloud).
 */
public class DrunkenTilesFunc extends VariationFunc {
    private static final long serialVersionUID = 2L;

    // --- Parameters ---
    private static final String PARAM_SEED = "seed";
    private static final String PARAM_CELLSIZE = "cellsize";
    private static final String PARAM_RADIUS_FACTOR = "radius_factor";
    private static final String PARAM_OFFSET_STRENGTH = "offset_strength";
    private static final String PARAM_SHAPE_BOUNDARY_TYPE = "shape_boundary_type"; // 0:Circ, 1:Sq, 2:Elps, 3:Tri, 4:Rhom, 5:Hex, 6:Star5, 7:Cross, 8:Ring, 9:Flower, 10:StarN, 11:Cloud
    private static final String PARAM_ASPECT_RATIO = "aspect_ratio"; // Ellipse
    private static final String PARAM_INNER_RADIUS_FACTOR = "inner_radius_factor"; // Star5, StarN (depth), Ring
    private static final String PARAM_ARM_WIDTH_FACTOR = "arm_width_factor"; // Cross
    private static final String PARAM_SPACING = "spacing"; // LazyJess scaling
    private static final String PARAM_INNER_TWIST = "inner_twist";
    private static final String PARAM_OUTER_TWIST = "outer_twist";
    private static final String PARAM_FLOWER_PETALS = "flowerPetals"; // Flower(9)
    private static final String PARAM_STAR_POINTS = "starPoints"; // StarN(10)
    private static final String PARAM_CLOUD_AMPLITUDE = "cloudAmplitude"; // Cloud(11)
    private static final String PARAM_CLOUD_FREQUENCY = "cloudFrequency"; // Cloud(11)


    private static final String[] paramNames = {
            PARAM_SEED, PARAM_CELLSIZE, PARAM_RADIUS_FACTOR, PARAM_OFFSET_STRENGTH,
            PARAM_SHAPE_BOUNDARY_TYPE, PARAM_ASPECT_RATIO,
            PARAM_INNER_RADIUS_FACTOR, PARAM_ARM_WIDTH_FACTOR,
            PARAM_SPACING, PARAM_INNER_TWIST, PARAM_OUTER_TWIST,
            PARAM_FLOWER_PETALS,
            PARAM_STAR_POINTS,
            PARAM_CLOUD_AMPLITUDE,
            PARAM_CLOUD_FREQUENCY 
    };

    // Default values
    private int seed = 1; private double cellsize = 0.5; private double radius_factor = 1.0;
    private double offset_strength = 0.25; private int shape_boundary_type = 0; // Default Circle
    private double aspect_ratio = 1.0; private double inner_radius_factor = 0.5;
    private double arm_width_factor = 0.3; private double spacing = 0.1;
    private double inner_twist = 0.0; private double outer_twist = Math.PI;
    private int flowerPetals = 5;
    private int starPoints = 5;
    private double cloudAmplitude = 0.2;
    private double cloudFrequency = 5.0;


    // --- Internal State --- 
    private transient Random random = new Random();
    private transient boolean needsReinitCalcs = true;
    private transient double radius;
    private transient double _r2; // radius squared

    // Constants for shape types 
    private static final int BOUNDARY_CIRCLE=0; 
    private static final int BOUNDARY_SQUARE=1; 
    private static final int BOUNDARY_ELLIPSE=2; 
    private static final int BOUNDARY_TRIANGLE=3; 
    private static final int BOUNDARY_RHOMBUS=4; 
    private static final int BOUNDARY_HEXAGON=5; 
    private static final int BOUNDARY_STAR5=6; 
    private static final int BOUNDARY_CROSS=7; 
    private static final int BOUNDARY_RING=8;
    private static final int BOUNDARY_FLOWER=9; 
    private static final int BOUNDARY_STAR_N=10; 
    private static final int BOUNDARY_CLOUD=11;
    private static final int NUM_BOUNDARY_TYPES = 12;

    // Other Constants
    private static final double TWO_PI = 2.0 * Math.PI;
    private static final double PI = Math.PI;

    /**
     * Initializes calculated values based on parameters if needed.
     */
    private void initializeIfNeeded() {
        if (needsReinitCalcs) {
            if (cellsize <= 1e-9) cellsize = 1e-9;
            radius = Math.abs(cellsize * 0.5 * radius_factor);
            if (radius <= 1e-9) radius = 1e-9;
            _r2 = radius * radius;
            needsReinitCalcs = false;
        }
        // Safety checks in case state is inconsistent after deserialization or edits
         if (_r2 <= 0 && radius > 1e-9) _r2 = radius * radius;
         if (radius <= 0 && cellsize > 1e-9 && radius_factor > 1e-9) {
             radius = Math.abs(cellsize * 0.5 * radius_factor);
             _r2 = radius * radius;
         }
    }

    /** Helper function for Triangle boundary check. */
    private static double pointSign(double p1x, double p1y, double p2x, double p2y, double p3x, double p3y) {
        return (p1x - p3x) * (p2y - p3y) - (p2x - p3x) * (p1y - p3y);
    }

    @Override
    public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
        initializeIfNeeded();

        if (cellsize <= 1e-9 || radius <= 1e-9) {
            // Passthrough if cellsize or radius is invalid
            pVarTP.x += pAmount * pAffineTP.x; pVarTP.y += pAmount * pAffineTP.y;
            if (pContext.isPreserveZCoordinate()) pVarTP.z += pAmount * pAffineTP.z;
            return;
        }

        double inputX = pAffineTP.x; double inputY = pAffineTP.y;
        double finalX = inputX; double finalY = inputY;

        // Determine Grid Cell and Perturbed Center 
        double invCellSize = 1.0 / cellsize;
        int ix = (int)Math.floor(inputX * invCellSize); int iy = (int)Math.floor(inputY * invCellSize);
        double Cx_reg = (ix + 0.5) * cellsize; double Cy_reg = (iy + 0.5) * cellsize;
        long cellSeed = seed ^ (Integer.hashCode(ix) * 31) ^ Integer.hashCode(iy);
        if(random == null) random = new Random(); random.setSeed(cellSeed);
        double Ox = (random.nextDouble()*2.0-1.0)*offset_strength*cellsize; double Oy = (random.nextDouble()*2.0-1.0)*offset_strength*cellsize;
        double Cx_pert = Cx_reg + Ox; double Cy_pert = Cy_reg + Oy;
        // Point relative to perturbed center
        double Lx = inputX - Cx_pert; double Ly = inputY - Cy_pert;
        double localDistSq = -1.0; // Calculated if needed

        // Determine Boundary Type and Check if Inside
        boolean apply_effect = false;
        int currentBoundaryType = shape_boundary_type;
        switch (currentBoundaryType) {
            case BOUNDARY_CIRCLE: localDistSq=Lx*Lx+Ly*Ly; if(localDistSq<=_r2) apply_effect=true; break;
            case BOUNDARY_SQUARE: if(Math.abs(Lx)<=radius && Math.abs(Ly)<=radius) { apply_effect=true; localDistSq=Lx*Lx+Ly*Ly;} break;
            case BOUNDARY_ELLIPSE: { double ar=Math.max(1e-9,Math.abs(aspect_ratio)); double rx=radius; double ry=radius/ar; double rx_sq=rx*rx; double ry_sq=ry*ry; if(rx_sq>1e-12 && ry_sq>1e-12){ if(((Lx*Lx)/rx_sq+(Ly*Ly)/ry_sq)<=1.0){apply_effect=true; localDistSq=Lx*Lx+Ly*Ly;}} break; }
            case BOUNDARY_TRIANGLE: { double r=radius; double s32=Math.sqrt(3.0)/2.0; double v1x=0,v1y=r; double v2x=r*s32,v2y=-r/2.0; double v3x=-r*s32,v3y=-r/2.0; double d1=pointSign(Lx,Ly,v1x,v1y,v2x,v2y); double d2=pointSign(Lx,Ly,v2x,v2y,v3x,v3y); double d3=pointSign(Lx,Ly,v3x,v3y,v1x,v1y); boolean hn=(d1<0)||(d2<0)||(d3<0); boolean hp=(d1>0)||(d2>0)||(d3>0); if(!(hn&&hp)){apply_effect=true; localDistSq=Lx*Lx+Ly*Ly;} break; }
            case BOUNDARY_RHOMBUS: { double a=-Math.PI/4.0; double c=Math.cos(a); double s=Math.sin(a); double rLx=c*Lx-s*Ly; double rLy=s*Lx+c*Ly; if(Math.abs(rLx)<=radius && Math.abs(rLy)<=radius){apply_effect=true; localDistSq=Lx*Lx+Ly*Ly;} break; }
            case BOUNDARY_HEXAGON: { double r=radius; double angleStep=Math.PI/3.0; boolean inside=true; double px=Lx; double py=Ly; double v1x=0,v1y=0,v2x=0,v2y=0; v2x=0; v2y=r; for(int i=0;i<6;i++){ v1x=v2x; v1y=v2y; double currentAngle=(Math.PI/2.0)+(i+1)*angleStep; v2x=r*Math.cos(currentAngle); v2y=r*Math.sin(currentAngle); if((v2x-v1x)*(py-v1y)-(v2y-v1y)*(px-v1x)<0){inside=false;break;} } if(inside){apply_effect=true; localDistSq=Lx*Lx+Ly*Ly;} break; }
            case BOUNDARY_STAR5: { double rO=radius; double rI=radius*Math.max(0.01,Math.min(1.0,inner_radius_factor)); double angleStep=Math.PI/5.0; boolean inside=true; double px=Lx; double py=Ly; double v1x=0,v1y=0,v2x=0,v2y=0; v2x=0; v2y=rO; for(int i=0;i<10;i++){ v1x=v2x; v1y=v2y; double currentAngle=(Math.PI/2.0)+(i+1)*angleStep; double cR=((i+1)%2==0)?rO:rI; v2x=cR*Math.cos(currentAngle); v2y=cR*Math.sin(currentAngle); if((v2x-v1x)*(py-v1y)-(v2y-v1y)*(px-v1x)<0){inside=false;break;} } if(inside){apply_effect=true; localDistSq=Lx*Lx+Ly*Ly;} break; }
            case BOUNDARY_CROSS: { double arm=radius*Math.max(0.01,Math.min(1.0,arm_width_factor)); if((Math.abs(Lx)<=arm && Math.abs(Ly)<=radius)||(Math.abs(Lx)<=radius && Math.abs(Ly)<=arm)){apply_effect=true; localDistSq=Lx*Lx+Ly*Ly;} break; }
            case BOUNDARY_RING: { double rI=radius*Math.max(0.0,Math.min(0.99,inner_radius_factor)); double inner_r2=rI*rI; localDistSq=Lx*Lx+Ly*Ly; if(localDistSq>=inner_r2 && localDistSq<=_r2){apply_effect=true;} break; }
            case BOUNDARY_FLOWER: {
                double currentRadius = Math.sqrt(Lx * Lx + Ly * Ly);
                if (currentRadius <= radius) { // Optimization: Check max radius first
                    if (currentRadius < 1e-9) { // Point at center is inside
                        apply_effect = true;
                        localDistSq = 0.0;
                    } else {
                        double angle = Math.atan2(Ly, Lx);
                         // No need to normalize angle for cos input
                        double k = Math.max(2, this.flowerPetals); // Use instance var
                        double flowerBoundaryR = radius * Math.abs(Math.cos(k * angle));
                        if (currentRadius <= flowerBoundaryR + 1e-9) { // Add tolerance
                            apply_effect = true;
                            localDistSq = currentRadius * currentRadius;
                        }
                    }
                }
                break;
            }
            case BOUNDARY_STAR_N: {
                 double currentRadius = Math.sqrt(Lx * Lx + Ly * Ly);
                 if (currentRadius <= radius) { // Optimization: Check max radius first
                     if (currentRadius < 1e-9) { // Point at center is inside
                         apply_effect = true;
                         localDistSq = 0.0;
                     } else {
                         double angle = Math.atan2(Ly, Lx);
                         while (angle < 0) { angle += TWO_PI; } // Normalize for sector calculation

                         double k = Math.max(3, this.starPoints); // Use instance var
                         // Reuse inner_radius_factor for depth, clamp 0-1
                         double depth = Math.max(0.0, Math.min(1.0, this.inner_radius_factor));
                         double R_outer = radius;
                         double R_inner = radius * depth;

                         double anglePerVertex = PI / k;
                         int sectorIndex = (int) Math.floor(angle / anglePerVertex);
                         double angleInSector = angle - sectorIndex * anglePerVertex;
                         double t = angleInSector / anglePerVertex; // Normalize 0-1
                         double t_norm = 2.0 * Math.abs(t - 0.5); // Normalize 0-1 from center

                         double starBoundaryR;
                         if (sectorIndex % 2 == 0) { // Point sector
                             starBoundaryR = R_inner + (R_outer - R_inner) * (1.0 - t_norm);
                         } else { // Valley sector
                             starBoundaryR = R_inner + (R_outer - R_inner) * t_norm;
                         }

                         if (currentRadius <= starBoundaryR + 1e-9) { // Add tolerance
                             apply_effect = true;
                             localDistSq = currentRadius * currentRadius;
                         }
                     }
                 }
                 break;
            }
            case BOUNDARY_CLOUD: {
                double currentRadius = Math.sqrt(Lx * Lx + Ly * Ly);
                // Estimate max possible boundary radius for optimization
                double maxPossibleR = radius * (1.0 + Math.abs(this.cloudAmplitude));
                if (currentRadius <= maxPossibleR) {
                     if (currentRadius < 1e-9) { // Point at center is inside
                         apply_effect = true;
                         localDistSq = 0.0;
                     } else {
                         double angle = Math.atan2(Ly, Lx);
                         double amp = Math.max(0.0, this.cloudAmplitude); // Use instance var
                         double freq = Math.max(1e-6, this.cloudFrequency); // Use instance var

                         double noise = 0.6 * Math.sin(freq * angle)
                                      + 0.3 * Math.sin(2.1 * freq * angle + 1.23)
                                      + 0.1 * Math.sin(4.3 * freq * angle + 4.56);

                         double cloudBoundaryR = radius * (1.0 + amp * noise);
                         // No need to clamp min here for the check, just compare
                         // cloudBoundaryR = Math.max(radius * 0.1, cloudBoundaryR);

                         if (currentRadius <= cloudBoundaryR + 1e-9) { // Add tolerance
                             apply_effect = true;
                             localDistSq = currentRadius * currentRadius;
                         }
                     }
                }
                break;
            }
            default: // Fallback to Circle if type is unknown/invalid
                localDistSq=Lx*Lx+Ly*Ly; if(localDistSq<=_r2) apply_effect=true; break;
        }

        // Apply Local LazyJess Logic + Twist if Inside Boundary (Unchanged logic)
        if (apply_effect) {
            double scaleFactor = (radius < 1e-9) ? 1.0 : 1.0 / radius;
            double scaledLx = Lx * scaleFactor; double scaledLy = Ly * scaleFactor;
            // LazyJess wrapping/tiling logic
            if (scaledLx > 1.0) scaledLx -= 2.0; if (scaledLx < -1.0) scaledLx += 2.0;
            if (scaledLy > 1.0) scaledLy -= 2.0; if (scaledLy < -1.0) scaledLy += 2.0;
            double k = 1.0 + spacing;
            double ljLx = scaledLx * k * radius; double ljLy = scaledLy * k * radius;

            // Twist Logic
            double twist_norm;
            // Determine twist normalization based on boundary shape for smoother twist near edges
            if (currentBoundaryType == BOUNDARY_SQUARE || currentBoundaryType == BOUNDARY_RHOMBUS) {
                 // Use Chebyshev distance for square-like shapes
                 twist_norm = (radius < 1e-9) ? 0.0 : Math.min(1.0, Math.max(Math.abs(Lx), Math.abs(Ly)) / radius);
                 if (currentBoundaryType == BOUNDARY_RHOMBUS) { // More accurate for Rhombus? Rotate first.
                     double a=-Math.PI/4.0; double c=Math.cos(a); double s=Math.sin(a);
                     double rLx=c*Lx-s*Ly; double rLy=s*Lx+c*Ly;
                     twist_norm = (radius < 1e-9) ? 0.0 : Math.min(1.0, Math.max(Math.abs(rLx), Math.abs(rLy)) / radius);
                 }
            }
            // Add specific twist norm calculation for other non-radial shapes?
            // else if (currentBoundaryType == BOUNDARY_TRIANGLE) { ... }
            else {
                // Default to radial distance for Circle, Ellipse, Polygons, Star, Flower, Cloud, Ring, Cross etc.
                 if (localDistSq < 0) localDistSq = Lx * Lx + Ly * Ly; // Recalculate if not done yet
                 twist_norm = (radius < 1e-9 || localDistSq < 0) ? 0.0 : Math.min(1.0, Math.sqrt(localDistSq) / radius);
             }
            // Interpolate twist angle
            double theta = inner_twist * (1.0 - twist_norm) + outer_twist * twist_norm;
            double s = Math.sin(theta); double c = Math.cos(theta);
            // Apply twist to the LazyJess-scaled coordinates
            double twistedLx = c * ljLx - s * ljLy; double twistedLy = s * ljLx + c * ljLy;
            // Final position is perturbed center + twisted local coordinates
            finalX = Cx_pert + twistedLx; finalY = Cy_pert + twistedLy;
        }

        // Apply final transform amount
        pVarTP.x += pAmount * finalX; pVarTP.y += pAmount * finalY;
        if (pContext.isPreserveZCoordinate()) pVarTP.z += pAmount * pAffineTP.z;
    }

    // --- Parameter Handling ---

    @Override
    public String[] getParameterNames() { return paramNames; } 

    @Override
    public Object[] getParameterValues() {
        return new Object[]{
                seed, cellsize, radius_factor, offset_strength,
                shape_boundary_type, aspect_ratio,
                inner_radius_factor, arm_width_factor,
                spacing, inner_twist, outer_twist,
                flowerPetals, starPoints, cloudAmplitude, cloudFrequency
        };
    }

    @Override
    public void setParameter(String pName, double pValue) {
        if (PARAM_SEED.equalsIgnoreCase(pName)) { seed = (int) pValue; }
        else if (PARAM_CELLSIZE.equalsIgnoreCase(pName)) { double v=Math.max(1e-9, pValue); if(Math.abs(cellsize-v)>1e-12){cellsize=v; needsReinitCalcs=true;} }
        else if (PARAM_RADIUS_FACTOR.equalsIgnoreCase(pName)) { double v=Math.max(1e-9, pValue); if(Math.abs(radius_factor-v)>1e-12){radius_factor=v; needsReinitCalcs=true;} }
        else if (PARAM_OFFSET_STRENGTH.equalsIgnoreCase(pName)) { offset_strength = pValue; }
        else if (PARAM_SHAPE_BOUNDARY_TYPE.equalsIgnoreCase(pName)) {
            // Clamp to valid range
            shape_boundary_type = Math.max(0, Math.min(NUM_BOUNDARY_TYPES - 1, (int) pValue));
        }
        else if (PARAM_ASPECT_RATIO.equalsIgnoreCase(pName)) { aspect_ratio = pValue; }
        else if (PARAM_INNER_RADIUS_FACTOR.equalsIgnoreCase(pName)) {
             // Clamp 0-1, used by Star5, StarN, Ring
            inner_radius_factor = Math.max(0.0, Math.min(1.0, pValue));
         }
        else if (PARAM_ARM_WIDTH_FACTOR.equalsIgnoreCase(pName)) {
             // Clamp 0-1, used by Cross
             arm_width_factor = Math.max(0.0, Math.min(1.0, pValue));
         }
        else if (PARAM_SPACING.equalsIgnoreCase(pName)) { spacing = pValue; }
        else if (PARAM_INNER_TWIST.equalsIgnoreCase(pName)) { inner_twist = pValue; }
        else if (PARAM_OUTER_TWIST.equalsIgnoreCase(pName)) { outer_twist = pValue; }
        else if (PARAM_FLOWER_PETALS.equalsIgnoreCase(pName)) {
            flowerPetals = Math.max(2, (int) pValue); // Flower needs >= 2 petals
        } else if (PARAM_STAR_POINTS.equalsIgnoreCase(pName)) {
            starPoints = Math.max(3, (int) pValue); // Star needs >= 3 points
        } else if (PARAM_CLOUD_AMPLITUDE.equalsIgnoreCase(pName)) {
            cloudAmplitude = Math.max(0.0, pValue); // Amplitude >= 0
        } else if (PARAM_CLOUD_FREQUENCY.equalsIgnoreCase(pName)) {
            cloudFrequency = Math.max(1e-6, pValue); // Frequency > 0
        }
        else { throw new IllegalArgumentException("Unknown parameter: " + pName); }
    }
    
    @Override
    public void randomize() {
    	seed = (int) (Math.random() * 1000000);
    	cellsize = Math.random() * 5.0 + 0.01;
    	radius_factor = Math.random() * 4.0 + 0.1;
    	offset_strength = Math.random() * 3.0 - 1.5;
    	shape_boundary_type = (int) (Math.random() * NUM_BOUNDARY_TYPES);
    	aspect_ratio = Math.random() * 3.75 + 0.25;
    	inner_radius_factor = Math.random();
    	arm_width_factor = Math.random();
    	spacing = Math.random();
    	inner_twist = Math.random() * TWO_PI - PI;
    	outer_twist = Math.random() * TWO_PI - PI;
    	flowerPetals = (int) (Math.random() * 8 + 2);
    	starPoints = (int) (Math.random() * 7 + 3);
    	cloudAmplitude = Math.random() * 2.0;
    	cloudFrequency =  Math.random() * 20 + 0.1;
    	needsReinitCalcs=true;
    }

    @Override
    public String getName() { return "drunken_tiles"; }

    @Override
    public VariationFuncType[] getVariationTypes() {
        return new VariationFuncType[]{VariationFuncType.VARTYPE_2D};
    }

}