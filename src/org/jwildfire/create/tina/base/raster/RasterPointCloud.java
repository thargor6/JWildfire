/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
package org.jwildfire.create.tina.base.raster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jwildfire.create.tina.OctreeNode;
import org.jwildfire.create.tina.OctreeNodeVisitor;
import org.jwildfire.create.tina.OctreeValue;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.LightViewCalculator;
import org.jwildfire.create.tina.render.LogScaleCalculator;
import org.jwildfire.create.tina.render.PlotSample;
import org.jwildfire.create.tina.render.ZBufferSample;

public class RasterPointCloud implements AbstractRaster, Serializable {
  private static final long serialVersionUID = 1L;
  protected int rasterWidth, rasterHeight;
  private int oversample;
  private double sampleDensity;
  private final double zmin, zmax;
  private Flame flame;

  public static final double DFLT_MAX_OCTREE_CELL_SIZE = 0.05;
  public static final double MIN_OCTREE_CELL_SIZE = 0.00001;
  public static final double MAX_OCTREE_CELL_SIZE = 1.0;
  private double maxOctreeCellSize = DFLT_MAX_OCTREE_CELL_SIZE;
  private OctreeNode octree;
  private AtomicInteger atomicAddCount;
  private List<PCPoint> generatedPoints;

  public static class PCRawPoint {
    float x, y, z;
    float r, g, b;
    int count;
  }

  public static class PCPoint {
    float x, y, z;
    float r, g, b;
    float intensity;
  }

  public RasterPointCloud(double pZmin, double pZmax, double pMaxOctreeCellSize) {
    zmin = pZmin;
    zmax = pZmax;
    maxOctreeCellSize = Math.min(Math.max(pMaxOctreeCellSize, MIN_OCTREE_CELL_SIZE), MAX_OCTREE_CELL_SIZE);
  }

  @Override
  public void allocRaster(Flame pFlame, int pWidth, int pHeight, int pOversample, double pSampleDensity) {
    rasterWidth = pWidth;
    rasterHeight = pHeight;
    oversample = pOversample;
    sampleDensity = pSampleDensity;
    flame = pFlame;

    octree = new OctreeNode(100.0);
    atomicAddCount = new AtomicInteger(0);
  }

  @Override
  public void readRasterPoint(int pX, int pY, RasterPoint pDestRasterPoint) {
    pDestRasterPoint.clear();
  }

  @Override
  public void readRasterPointSafe(int pX, int pY, RasterPoint pDestRasterPoint) {
    pDestRasterPoint.clear();
  }

  @Override
  public void addSamples(PlotSample[] pPlotBuffer, int pCount) {
    for (int i = 0; i < pCount; i++) {
      PlotSample sample = pPlotBuffer[i];

      if (sample.originalZ >= zmin && sample.originalZ <= zmax) {
        PCRawPoint p = new PCRawPoint();
        p.x = (float) sample.originalX;
        p.y = (float) sample.originalY;
        p.z = (float) sample.originalZ;
        p.r = (float) sample.r;
        p.g = (float) sample.g;
        p.b = (float) sample.b;
        p.count = 1;

        synchronized (octree) {
          octree.addValue(p.x, p.y, p.z, p, maxOctreeCellSize);
        }

        if (atomicAddCount.incrementAndGet() % 1000000 == 0) {
          condenseOctreeNodes();
        }

      }
    }
  }

  @Override
  public void incCount(int pX, int pY) {
    // EMPTY
  }

  @Override
  public void finalizeRaster() {
    condenseOctreeNodes();
    generatedPoints = collectOctreeNodes();
    octree = null;
  }

  private class Count {
    int count;
  }

  private List<PCPoint> collectOctreeNodes() {
    final Count maxAddCount = new Count();
    octree.accept(new OctreeNodeVisitor() {

      @Override
      public void visit(OctreeNode pNode) {
        if (pNode.getValues() != null && !pNode.getValues().isEmpty()) {
          int count = ((PCRawPoint) pNode.getValues().iterator().next().getValue()).count;
          if (count > maxAddCount.count) {
            maxAddCount.count = count;
          }
        }
      }

    });

    final LogScaleCalculator logScaleCalculator = new LogScaleCalculator(flame, rasterWidth, rasterHeight, 1);

    final List<PCPoint> res = new ArrayList<>();
    octree.accept(new OctreeNodeVisitor() {

      @Override
      public void visit(OctreeNode pNode) {
        if (pNode.getValues() != null && !pNode.getValues().isEmpty()) {
          if (pNode.getValues().size() > 1) {
            throw new RuntimeException("Call <condenseOctreeNodes> first");
          }
          PCRawPoint cumPoint = (PCRawPoint) pNode.getValues().iterator().next().getValue();
          PCPoint avgPoint = new PCPoint();
          avgPoint.x = (float) (cumPoint.x / (double) cumPoint.count);
          avgPoint.y = (float) (cumPoint.y / (double) cumPoint.count);
          avgPoint.z = (float) (cumPoint.z / (double) cumPoint.count);
          avgPoint.r = (float) (cumPoint.r / (double) cumPoint.count);
          avgPoint.g = (float) (cumPoint.g / (double) cumPoint.count);
          avgPoint.b = (float) (cumPoint.b / (double) cumPoint.count);
          avgPoint.intensity = (float) (logScaleCalculator.calcLogScale(cumPoint.count) * cumPoint.count * flame.getWhiteLevel());
          res.add(avgPoint);
        }
      }

    });
    return res;
  }

  private void condenseOctreeNodes() {
    System.out.println("CONDENSE " + atomicAddCount.get());
    synchronized (octree) {
      octree.accept(new OctreeNodeVisitor() {

        @Override
        public void visit(OctreeNode pNode) {
          if (pNode.getValues() != null && pNode.getValues().size() > 1) {
            PCRawPoint cumPoint = new PCRawPoint();
            for (OctreeValue val : pNode.getValues()) {
              PCRawPoint p = (PCRawPoint) val.getValue();
              cumPoint.x += p.x;
              cumPoint.y += p.y;
              cumPoint.z += p.z;
              cumPoint.r += p.r;
              cumPoint.g += p.g;
              cumPoint.b += p.b;
              cumPoint.count += p.count;
            }
            OctreeValue firstVal = pNode.getValues().iterator().next();

            OctreeValue cum = new OctreeValue(firstVal.getX(), firstVal.getY(), firstVal.getZ(), cumPoint);
            pNode.getValues().clear();
            pNode.getValues().add(cum);
          }

        }
      });
    }
  }

  @Override
  public void addShadowMapSamples(int pShadowMapIdx, PlotSample[] pPlotBuffer, int pCount) {
    // EMPTY
  }

  @Override
  public void notifyInit(LightViewCalculator lightViewCalculator) {
    // EMPTY    
  }

  @Override
  public void readZBuffer(int pX, int pY, ZBufferSample pDest) {
    // EMPTY    
  }

  @Override
  public void readZBufferSafe(int pX, int pY, ZBufferSample pDest) {
    // EMPTY    
  }

  @Override
  public LightViewCalculator getLightViewCalculator() {
    // EMPTY    
    return null;
  }

  @Override
  public int getRasterWidth() {
    return rasterWidth;
  }

  @Override
  public int getRasterHeight() {
    return rasterHeight;
  }

  @Override
  public int getOversample() {
    return oversample;
  }

  @Override
  public double getSampleDensity() {
    return sampleDensity;
  }

  public List<PCPoint> getPoints() {
    return generatedPoints;
  }

  public List<PCPoint> getGeneratedPoints() {
    return generatedPoints;
  }

}
