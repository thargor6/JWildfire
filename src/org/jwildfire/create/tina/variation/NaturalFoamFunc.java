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

import java.util.Random;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import static java.lang.Math.sqrt;
import static java.lang.Math.abs;

public class NaturalFoamFunc extends VariationFunc {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_DENSITY = "density";
  private static final String PARAM_ITERATIONS = "iterations";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_SPREAD = "spread";
  private static final String PARAM_MIN_RADIUS = "minRadius";
  private static final String PARAM_MAX_RADIUS = "maxRadius";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_SHAPE_AMOUNT = "shapeAmount";
  private static final String PARAM_BLUR_RADIUS = "blur_radius";
  private static final String PARAM_BLUR_QUALITY = "blur_quality";
  private static final String PARAM_COLOR_DATA = "color_data";
  private static final String PARAM_ZOOM = "zoom";
  
  private static final String[] paramNames = {PARAM_DENSITY, PARAM_ITERATIONS, PARAM_SCALE, PARAM_SPREAD, PARAM_MIN_RADIUS, PARAM_MAX_RADIUS, PARAM_SEED, PARAM_SHAPE_AMOUNT, PARAM_BLUR_RADIUS, PARAM_BLUR_QUALITY, PARAM_COLOR_DATA, PARAM_ZOOM};

  private int density = 50;
  private int iterations = 3;
  private double scale = 0.8;
  private double spread = 1.0;
  private double minRadius = 0.1;
  private double maxRadius = 0.9;
  private int seed = 123;
  private double shapeAmount = 0.0;
  private double blur_radius = 0.1;
  private int blur_quality = 1;
  private int color_data = 0;
  private double zoom = 1.0;

  private transient Bubble[] bubbles;
  private transient Random rand;
  private transient int lastDensity = -1;
  private transient int lastSeed = -1;
  private transient double lastSpread = -1.0;

  private class Bubble {
      double x, y, z, radius;
  }

  private void initializeBubbles() {
    rand = new Random(seed);
    bubbles = new Bubble[density];
    for (int i = 0; i < density; i++) {
        bubbles[i] = new Bubble();
        bubbles[i].x = (2.0 * rand.nextDouble() - 1.0) * spread;
        bubbles[i].y = (2.0 * rand.nextDouble() - 1.0) * spread;
        bubbles[i].z = (2.0 * rand.nextDouble() - 1.0) * spread;
        bubbles[i].radius = minRadius + (maxRadius - minRadius) * rand.nextDouble();
    }
    lastDensity = density;
    lastSeed = seed;
    lastSpread = spread;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    if (bubbles == null || density != lastDensity || seed != lastSeed || spread != lastSpread) {
        initializeBubbles();
    }
    
    double x = pAffineTP.x;
    double y = pAffineTP.y;
    double z = pAffineTP.z;

    // --- Standard Foam Repulsion Logic ---
    for (int i = 0; i < iterations; i++) {
        for (int j = 0; j < density; j++) {
            Bubble b = bubbles[j];
            double dx = x - b.x;
            double dy = y - b.y;
            double dz = z - b.z;
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq < b.radius * b.radius) {
                double dist = sqrt(distSq);
                if (dist > 1.0E-6) {
                    double pushFactor = (b.radius - dist) / dist;
                    x += dx * pushFactor * scale;
                    y += dy * pushFactor * scale;
                    z += dz * pushFactor * scale;
                }
            }
        }
    }

    // --- Contained Diffusion Blur ---
    if (blur_radius > 0.0 && blur_quality > 0) {
        double step_size = blur_radius / blur_quality;
        for(int i=0; i < blur_quality; i++) {
            double prev_x = x;
            double prev_y = y;
            double prev_z = z;
            x += (rand.nextDouble() - 0.5) * 2.0 * step_size;
            y += (rand.nextDouble() - 0.5) * 2.0 * step_size;
            z += (rand.nextDouble() - 0.5) * 2.0 * step_size;
            boolean is_contained = false;
            for(int j=0; j < density; j++) {
                Bubble b = bubbles[j];
                double dx = x - b.x;
                double dy = y - b.y;
                double dz = z - b.z;
                if((dx*dx + dy*dy + dz*dz) < (b.radius * b.radius)) {
                    is_contained = true;
                    break;
                }
            }
            if(!is_contained) {
                x = prev_x;
                y = prev_y;
                z = prev_z;
            }
        }
    }
    
    // --- Apply final shaping ---
    if(shapeAmount > 0.0) {
        int nearestBubbleIndex = -1;
        double minDistanceSq = Double.MAX_VALUE;
        for (int j = 0; j < density; j++) {
            Bubble b = bubbles[j];
            double dx = x - b.x;
            double dy = y - b.y;
            double dz = z - b.z;
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq < minDistanceSq) {
                minDistanceSq = distSq;
                nearestBubbleIndex = j;
            }
        }
        if(nearestBubbleIndex != -1) {
            Bubble nearest = bubbles[nearestBubbleIndex];
            double localX = x - nearest.x;
            double localY = y - nearest.y;
            double localZ = z - nearest.z;
            double localDistSq = localX * localX + localY * localY + localZ * localZ;
            double radiusSq = nearest.radius * nearest.radius;
            double inversionFactor = radiusSq / (localDistSq + 1.0E-9);
            double bubbleX = localX * inversionFactor + nearest.x;
            double bubbleY = localY * inversionFactor + nearest.y;
            double bubbleZ = localZ * inversionFactor + nearest.z;
            x = x * (1.0 - shapeAmount) + bubbleX * shapeAmount;
            y = y * (1.0 - shapeAmount) + bubbleY * shapeAmount;
            z = z * (1.0 - shapeAmount) + bubbleZ * shapeAmount;
        }
    }
    
    // --- Data-Driven Coloring ---
    if (color_data > 0) {
        double colorIndex = 0.5;
        int nearestBubbleIndex = -1;
        double minDistanceSq = Double.MAX_VALUE;
        for (int j = 0; j < density; j++) {
            Bubble b = bubbles[j];
            double dx = x - b.x;
            double dy = y - b.y;
            double dz = z - b.z;
            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq < minDistanceSq) {
                minDistanceSq = distSq;
                nearestBubbleIndex = j;
            }
        }
        if (nearestBubbleIndex != -1) {
            switch(color_data) {
                case 1:
                    Bubble nearest = bubbles[nearestBubbleIndex];
                    double radiusRange = maxRadius - minRadius;
                    if(radiusRange > 1.0E-6) { colorIndex = (nearest.radius - minRadius) / radiusRange; }
                    break;
                case 2:
                    int pressureCount = 0;
                    for (int j=0; j < density; j++) {
                        Bubble b = bubbles[j];
                        double dx = x - b.x;
                        double dy = y - b.y;
                        double dz = z - b.z;
                        if((dx*dx + dy*dy + dz*dz) < (b.radius * b.radius)) { pressureCount++; }
                    }
                    if(density > 0) { colorIndex = (double)pressureCount / (double)density; }
                    break;
                case 3:
                    if(density > 1) { colorIndex = (double)nearestBubbleIndex / (double)(density - 1); }
                    break;
            }
        }
        pVarTP.color = Math.max(0.0, Math.min(1.0, colorIndex));
    }

    pVarTP.x += x * zoom * pAmount;
    pVarTP.y += y * zoom * pAmount;
    pVarTP.z += z * zoom * pAmount;
  }
  
  @Override
  public String[] getParameterNames() { return paramNames; }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{density, iterations, scale, spread, minRadius, maxRadius, seed, shapeAmount, blur_radius, blur_quality, color_data, zoom};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_DENSITY.equalsIgnoreCase(pName))
      density = (int) pValue;
    else if (PARAM_ITERATIONS.equalsIgnoreCase(pName))
      iterations = (int) pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName))
      scale = pValue;
    else if (PARAM_SPREAD.equalsIgnoreCase(pName))
      spread = pValue;
    else if (PARAM_MIN_RADIUS.equalsIgnoreCase(pName))
      minRadius = pValue;
    else if (PARAM_MAX_RADIUS.equalsIgnoreCase(pName))
      maxRadius = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName))
      seed = (int) pValue;
    else if (PARAM_SHAPE_AMOUNT.equalsIgnoreCase(pName))
        shapeAmount = pValue;
    else if (PARAM_BLUR_RADIUS.equalsIgnoreCase(pName))
        blur_radius = pValue;
    else if (PARAM_BLUR_QUALITY.equalsIgnoreCase(pName))
        blur_quality = (int) pValue;
    else if (PARAM_COLOR_DATA.equalsIgnoreCase(pName))
        color_data = (int) pValue;
    else if (PARAM_ZOOM.equalsIgnoreCase(pName))
      zoom = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() { return "naturalFoam"; }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D};
  }
}
