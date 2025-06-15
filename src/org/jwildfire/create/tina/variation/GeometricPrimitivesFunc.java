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
// Inspired by AI images by Mufti
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import java.util.ArrayList;
import java.util.List;

import static org.jwildfire.base.mathlib.MathLib.*;

public class GeometricPrimitivesFunc extends VariationFunc {
  private static final long serialVersionUID = 29L; // Fixed Hexagon drawing logic

  //<editor-fold defaultstate="collapsed" desc="Parameters">
  private static final String PARAM_LINE_VS_SHAPE = "line_vs_shape_mix";
  private static final String PARAM_DENSITY = "density";
  private static final String PARAM_SCALE = "scale";
  private static final String PARAM_GRID_SIZE = "gridSize";
  private static final String PARAM_SEED = "seed";
  private static final String PARAM_GRID_JITTER = "grid_jitter";

  private static final String PARAM_SHAPE_TYPE = "shape_type";
  private static final String PARAM_SHAPE_MIX_ENABLE = "shape_mix_enable";
  private static final String PARAM_FILL_PROB = "fill_prob";
  private static final String PARAM_RADIUS = "radius";
  private static final String PARAM_HOLLOW = "hollow";
  private static final String PARAM_SQUARE_SIZE = "square_size";

  private static final String PARAM_CONNECTION_MODE = "connection_mode";
  private static final String PARAM_RECURSION_DEPTH = "recursion_depth";
  private static final String PARAM_RECURSION_SHAPE_MIX = "recursion_shape_mix";
  private static final String PARAM_RECURSION_SCALE = "recursion_scale";


  private static final String[] paramNames = {
          PARAM_LINE_VS_SHAPE, PARAM_DENSITY, PARAM_SCALE, PARAM_GRID_SIZE, PARAM_SEED, PARAM_GRID_JITTER,
          PARAM_SHAPE_TYPE, PARAM_SHAPE_MIX_ENABLE, PARAM_FILL_PROB,
          PARAM_RADIUS, PARAM_HOLLOW, PARAM_SQUARE_SIZE,
          PARAM_CONNECTION_MODE, PARAM_RECURSION_DEPTH, PARAM_RECURSION_SHAPE_MIX, PARAM_RECURSION_SCALE
  };

  private double line_vs_shape_mix = 0.5;
  private double density = 0.5;
  private double scale = 2.0;
  private double gridSize = 1.0;
  private double seed = 0.0;
  private double grid_jitter = 0.0;

  private int shape_type = 0; // 0=Circle, 1=Square, 2=Triangle, 3=Hexagon
  private int shape_mix_enable = 0; // 0=Off, 1=On
  private double fill_prob = 0.5;
  private double radius = 0.45; // Used for Circles, Triangles, Hexagons
  private double hollow = 0.8;
  private double square_size = 0.9;

  private int connection_mode = 0; // 0=Random, 1=All
  private int recursion_depth = 0;
  private double recursion_shape_mix = 0.5;
  private double recursion_scale = 0.25;
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Helper Functions">
  private double randomForCell(double gx, double gy, double salt) {
      double val = gx * 13.9898 + gy * 73.233 + salt;
      return frac(sin(val) * 43758.5453);
  }

  private boolean shapeExistsAt(double gx, double gy) {
    return randomForCell(gx, gy, this.seed) < this.density;
  }
  
  private void drawCircle(FlameTransformationContext pContext, XYZPoint out, boolean isFilled, double cx, double cy, double rad, double hol) {
    double angle = pContext.random() * 2.0 * Math.PI;
    if(isFilled) {
        double r = sqrt(pContext.random()) * rad;
        out.x = cx + r * cos(angle);
        out.y = cy + r * sin(angle);
    } else {
        double innerRadius = rad * hol;
        double r = innerRadius + pContext.random() * (rad - innerRadius);
        out.x = cx + r * cos(angle);
        out.y = cy + r * sin(angle);
    }
  }

  private void drawSquare(FlameTransformationContext pContext, XYZPoint out, boolean isFilled, double cx, double cy, double size) {
    double halfWidth = size * 0.5;
    if(isFilled) {
        out.x = cx + (pContext.random() * 2.0 - 1.0) * halfWidth;
        out.y = cy + (pContext.random() * 2.0 - 1.0) * halfWidth;
    } else {
        double side = floor(pContext.random() * 4.0);
        double t = (pContext.random() * 2.0 - 1.0) * halfWidth;
        if(side == 0)      { out.x = cx + t;   out.y = cy + halfWidth; }
        else if(side == 1) { out.x = cx + t;   out.y = cy - halfWidth; }
        else if(side == 2) { out.x = cx + halfWidth; out.y = cy + t;   }
        else               { out.x = cx - halfWidth; out.y = cy + t;   }
    }
  }

  private void drawTriangle(FlameTransformationContext pContext, XYZPoint out, boolean isFilled, double cx, double cy, double size) {
    XYZPoint v1 = new XYZPoint(); v1.x = cx; v1.y = cy + size;
    XYZPoint v2 = new XYZPoint(); v2.x = cx - size * 0.866; v2.y = cy - size * 0.5;
    XYZPoint v3 = new XYZPoint(); v3.x = cx + size * 0.866; v3.y = cy - size * 0.5;

    if(isFilled) {
        double r1 = pContext.random();
        double r2 = pContext.random();
        if (r1 + r2 > 1.0) {
            r1 = 1.0 - r1;
            r2 = 1.0 - r2;
        }
        out.x = (1.0 - r1 - r2) * v1.x + r1 * v2.x + r2 * v3.x;
        out.y = (1.0 - r1 - r2) * v1.y + r1 * v2.y + r2 * v3.y;
    } else {
        double side = floor(pContext.random() * 3.0);
        double t = pContext.random();
        if(side == 0) { out.x = v1.x * (1-t) + v2.x * t; out.y = v1.y * (1-t) + v2.y * t; }
        else if(side == 1) { out.x = v2.x * (1-t) + v3.x * t; out.y = v2.y * (1-t) + v3.y * t; }
        else { out.x = v3.x * (1-t) + v1.x * t; out.y = v3.y * (1-t) + v1.y * t; }
    }
  }

  private void drawHexagon(FlameTransformationContext pContext, XYZPoint out, boolean isFilled, double cx, double cy, double size) {
    if(isFilled) {
        // Correct Filled Hexagon Logic: Pick a random triangle slice and find a point within it.
        double segment = floor(pContext.random() * 6.0);
        double angle1 = segment * (Math.PI/3.0);
        double angle2 = (segment + 1) * (Math.PI/3.0);

        XYZPoint v_center = new XYZPoint(); v_center.x = cx; v_center.y = cy;
        XYZPoint v1 = new XYZPoint(); v1.x = cx + size * cos(angle1); v1.y = cy + size * sin(angle1);
        XYZPoint v2 = new XYZPoint(); v2.x = cx + size * cos(angle2); v2.y = cy + size * sin(angle2);

        double r1 = pContext.random();
        double r2 = pContext.random();
        if (r1 + r2 > 1.0) {
            r1 = 1.0 - r1;
            r2 = 1.0 - r2;
        }
        out.x = (1.0 - r1 - r2) * v_center.x + r1 * v1.x + r2 * v2.x;
        out.y = (1.0 - r1 - r2) * v_center.y + r1 * v1.y + r2 * v2.y;
    } else {
        // Outline Logic: Pick a random edge and a random point along it.
        double angle = pContext.random() * 2.0 * Math.PI;
        double segment = floor(angle / (Math.PI/3.0));
        double angle1 = segment * (Math.PI/3.0);
        double angle2 = (segment + 1) * (Math.PI/3.0);
        
        XYZPoint v1 = new XYZPoint(); v1.x = cx + size * cos(angle1); v1.y = cy + size * sin(angle1);
        XYZPoint v2 = new XYZPoint(); v2.x = cx + size * cos(angle2); v2.y = cy + size * sin(angle2);
        
        double t = pContext.random();
        out.x = v1.x * (1-t) + v2.x * t;
        out.y = v1.y * (1-t) + v2.y * t;
    }
  }
  //</editor-fold>

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double u = pAffineTP.x * scale;
    double v = pAffineTP.y * scale;
    
    XYZPoint out = new XYZPoint();
    boolean pointIsValid = false;

    double gridX = round(u);
    double gridY = round(v);
    
    double jitterX = 0.0, jitterY = 0.0;
    if (this.grid_jitter > 0.0) {
        double maxJitter = this.grid_jitter * this.gridSize * 0.5;
        jitterX = (randomForCell(gridX, gridY, this.seed + 40.0) * 2.0 - 1.0) * maxJitter;
        jitterY = (randomForCell(gridX, gridY, this.seed + 50.0) * 2.0 - 1.0) * maxJitter;
    }
    double finalCenterX = gridX * gridSize + jitterX;
    double finalCenterY = gridY * gridSize + jitterY;


    if (pContext.random() < this.line_vs_shape_mix) {
        if (shapeExistsAt(gridX, gridY)) {
            double neighborGridX = gridX, neighborGridY = gridY;
            
            if(connection_mode == 0) { // Random neighbor
                double dir = pContext.random() * 4.0;
                if (dir < 1.0) neighborGridX++; else if (dir < 2.0) neighborGridX--;
                else if (dir < 3.0) neighborGridY++; else neighborGridY--;
            } else { // All neighbors - build a list and pick one
                List<double[]> validNeighbors = new ArrayList<>();
                if(shapeExistsAt(gridX + 1, gridY)) validNeighbors.add(new double[]{gridX+1, gridY});
                if(shapeExistsAt(gridX - 1, gridY)) validNeighbors.add(new double[]{gridX-1, gridY});
                if(shapeExistsAt(gridX, gridY + 1)) validNeighbors.add(new double[]{gridX, gridY+1});
                if(shapeExistsAt(gridX, gridY - 1)) validNeighbors.add(new double[]{gridX, gridY-1});

                if(!validNeighbors.isEmpty()) {
                    double[] chosen = validNeighbors.get((int)floor(pContext.random() * validNeighbors.size()));
                    neighborGridX = chosen[0];
                    neighborGridY = chosen[1];
                }
            }
            
            if (neighborGridX != gridX || neighborGridY != gridY) {
                double neighborJitterX = 0.0, neighborJitterY = 0.0;
                if (this.grid_jitter > 0.0) {
                    double maxJitter = this.grid_jitter * this.gridSize * 0.5;
                    neighborJitterX = (randomForCell(neighborGridX, neighborGridY, this.seed + 40.0) * 2.0 - 1.0) * maxJitter;
                    neighborJitterY = (randomForCell(neighborGridX, neighborGridY, this.seed + 50.0) * 2.0 - 1.0) * maxJitter;
                }
                double finalNeighborCenterX = neighborGridX * gridSize + neighborJitterX;
                double finalNeighborCenterY = neighborGridY * gridSize + neighborJitterY;

                if(this.recursion_depth > 0) {
                    int step = (int)floor(pContext.random() * this.recursion_depth);
                    double t = (double)(step + 0.5) / this.recursion_depth;
                    double shapeCenterX = finalCenterX * (1.0 - t) + finalNeighborCenterX * t;
                    double shapeCenterY = finalCenterY * (1.0 - t) + finalNeighborCenterY * t;
                    boolean isFilled = randomForCell(gridX, gridY, this.seed + 30.0 + step) < this.fill_prob;

                    if(pContext.random() < this.recursion_shape_mix) {
                         drawSquare(pContext, out, isFilled, shapeCenterX, shapeCenterY, this.square_size * this.recursion_scale);
                    } else {
                         drawCircle(pContext, out, isFilled, shapeCenterX, shapeCenterY, this.radius * this.recursion_scale, this.hollow);
                    }
                } else {
                    double t = pContext.random();
                    out.x = finalCenterX * (1.0 - t) + finalNeighborCenterX * t;
                    out.y = finalCenterY * (1.0 - t) + finalNeighborCenterY * t;
                }
                pointIsValid = true;
            }
        }
    } else {
        if (shapeExistsAt(gridX, gridY)) {
            int final_shape_type = this.shape_type;
            if(this.shape_mix_enable == 1) {
                final_shape_type = (int)floor(randomForCell(gridX, gridY, this.seed + 10.0) * 4.0);
            }

            boolean isFilled = randomForCell(gridX, gridY, this.seed + 20.0) < this.fill_prob;
            
            switch(final_shape_type) {
                case 0: // Circle
                    double currentRadius = randomForCell(gridX, gridY, -this.seed) * this.radius * gridSize;
                    drawCircle(pContext, out, isFilled, finalCenterX, finalCenterY, currentRadius, this.hollow);
                    break;
                case 1: // Square
                    double currentSize = randomForCell(gridX, gridY, 1.0 - this.seed) * this.square_size * gridSize;
                    drawSquare(pContext, out, isFilled, finalCenterX, finalCenterY, currentSize);
                    break;
                case 2: // Triangle
                    double triSize = randomForCell(gridX, gridY, 2.0 - this.seed) * this.radius * gridSize;
                    drawTriangle(pContext, out, isFilled, finalCenterX, finalCenterY, triSize);
                    break;
                case 3: // Hexagon
                    double hexSize = randomForCell(gridX, gridY, 3.0 - this.seed) * this.radius * gridSize;
                    drawHexagon(pContext, out, isFilled, finalCenterX, finalCenterY, hexSize);
                    break;
            }
            pointIsValid = true;
        }
    }
    
    if (pointIsValid) {
        pVarTP.doHide = false;
        pVarTP.x += out.x * pAmount;
        pVarTP.y += out.y * pAmount;
    } else {
        pVarTP.doHide = true;
    }
  }
    
  //<editor-fold defaultstate="collapsed" desc="boilerplate">
  @Override
  public String[] getParameterNames() { return paramNames; }
  @Override
  public Object[] getParameterValues() { return new Object[]{line_vs_shape_mix, density, scale, gridSize, seed, grid_jitter, shape_type, shape_mix_enable, fill_prob, radius, hollow, square_size, connection_mode, recursion_depth, recursion_shape_mix, recursion_scale}; }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LINE_VS_SHAPE.equalsIgnoreCase(pName)) line_vs_shape_mix = Tools.limitValue(pValue, 0.0, 1.0);
    else if (PARAM_DENSITY.equalsIgnoreCase(pName)) density = pValue;
    else if (PARAM_SCALE.equalsIgnoreCase(pName)) scale = pValue;
    else if (PARAM_GRID_SIZE.equalsIgnoreCase(pName)) gridSize = pValue;
    else if (PARAM_SEED.equalsIgnoreCase(pName)) seed = pValue;
    else if (PARAM_GRID_JITTER.equalsIgnoreCase(pName)) grid_jitter = pValue;
    else if (PARAM_SHAPE_TYPE.equalsIgnoreCase(pName)) shape_type = (int)Tools.limitValue(pValue, 0, 3);
    else if (PARAM_SHAPE_MIX_ENABLE.equalsIgnoreCase(pName)) shape_mix_enable = (int)Tools.limitValue(pValue, 0, 1);
    else if(PARAM_FILL_PROB.equalsIgnoreCase(pName)) fill_prob = pValue;
    else if (PARAM_RADIUS.equalsIgnoreCase(pName)) radius = pValue;
    else if (PARAM_HOLLOW.equalsIgnoreCase(pName)) hollow = Tools.limitValue(pValue, 0.0, 1.0);
    else if(PARAM_SQUARE_SIZE.equalsIgnoreCase(pName)) square_size = pValue;
    else if(PARAM_CONNECTION_MODE.equalsIgnoreCase(pName)) connection_mode = (int)Tools.limitValue(pValue, 0, 1);
    else if (PARAM_RECURSION_DEPTH.equalsIgnoreCase(pName)) recursion_depth = (int)Tools.limitValue(pValue, 0, 50);
    else if (PARAM_RECURSION_SHAPE_MIX.equalsIgnoreCase(pName)) recursion_shape_mix = Tools.limitValue(pValue, 0.0, 1.0);
    else if (PARAM_RECURSION_SCALE.equalsIgnoreCase(pName)) recursion_scale = pValue;
    else throw new IllegalArgumentException(pName);
  }
  
  @Override
  public String[] getParameterAlternativeNames() { return new String[]{"line_mix", "density", "scale", "grid_size", "seed", "jitter", "shape", "mix_shapes", "fill", "radius", "hollow", "sq_size", "conn_mode", "rec_depth", "rec_shape", "rec_scale"}; }
  @Override
  public String getName() { return "geometricPrimitives"; }
  @Override
  public VariationFuncType[] getVariationTypes() { return new VariationFuncType[]{VariationFuncType.VARTYPE_2D}; }
  //</editor-fold>
}
