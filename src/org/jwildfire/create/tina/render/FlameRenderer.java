/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.RandomNumberGenerator;
import org.jwildfire.create.tina.random.SimpleRandomNumberGenerator;
import org.jwildfire.create.tina.variation.TransformationContext;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationPriorityComparator;
import org.jwildfire.image.SimpleImage;

public class FlameRenderer implements TransformationContext {
  // constants
  private final static int MAX_FILTER_WIDTH = 25;
  // init in initRaster
  private int imageWidth;
  private int imageHeight;
  private int rasterWidth;
  private int rasterHeight;
  private int rasterSize;
  private int borderWidth;
  private int maxBorderWidth;
  LogDensityFilter logDensityFilter;
  GammaCorrectionFilter gammaCorrectionFilter;
  RasterPoint[][] raster;
  // init in initView
  double cosa;
  double sina;
  double camW;
  double camH;
  double rcX;
  double rcY;
  double bws;
  double bhs;
  // init in createColorMap
  RenderColor[] colorMap;
  double paletteIdxScl;
  RandomNumberGenerator random = new SimpleRandomNumberGenerator();
  // 3D stuff
  private double cameraZpos = 0;
  // init in init3D()
  private double cameraMatrix[][] = new double[3][3];
  //
  private RenderMode renderMode = RenderMode.NORMAL;
  private AffineZStyle affineZStyle = AffineZStyle.FLAT;

  private void init3D(Flame pFlame) {
    double yaw = -pFlame.getCamYaw() * Math.PI / 180.0;
    double pitch = pFlame.getCamPitch() * Math.PI / 180.0;
    cameraMatrix[0][0] = Math.cos(yaw);
    cameraMatrix[1][0] = -Math.sin(yaw);
    cameraMatrix[2][0] = 0;
    cameraMatrix[0][1] = Math.cos(pitch) * Math.sin(yaw);
    cameraMatrix[1][1] = Math.cos(pitch) * Math.cos(yaw);
    cameraMatrix[2][1] = -Math.sin(pitch);
    cameraMatrix[0][2] = Math.sin(pitch) * Math.sin(yaw);
    cameraMatrix[1][2] = Math.sin(pitch) * Math.cos(yaw);
    cameraMatrix[2][2] = Math.cos(pitch);
  }

  private void initRaster(Flame pFlame, SimpleImage pImage) {
    imageWidth = pImage.getImageWidth();
    imageHeight = pImage.getImageHeight();
    logDensityFilter = new LogDensityFilter(pFlame);
    gammaCorrectionFilter = new GammaCorrectionFilter(pFlame);
    maxBorderWidth = (MAX_FILTER_WIDTH - pFlame.getSpatialOversample()) / 2;
    borderWidth = (logDensityFilter.getNoiseFilterSize() - pFlame.getSpatialOversample()) / 2;
    rasterWidth = pFlame.getSpatialOversample() * imageWidth + 2 * maxBorderWidth;
    rasterHeight = pFlame.getSpatialOversample() * imageHeight + 2 * maxBorderWidth;
    rasterSize = rasterWidth * rasterHeight;
    raster = new RasterPoint[rasterHeight][rasterWidth];
    for (int i = 0; i < rasterHeight; i++) {
      for (int j = 0; j < rasterWidth; j++) {
        raster[i][j] = new RasterPoint();
      }
    }
  }

  void project(Flame pFlame, XYZPoint pPoint) {
    double z = pPoint.z - cameraZpos;
    double x = cameraMatrix[0][0] * pPoint.x + cameraMatrix[1][0] * pPoint.y;
    double y = cameraMatrix[0][1] * pPoint.x + cameraMatrix[1][1] * pPoint.y + cameraMatrix[2][1] * z;
    double zr = 1 - pFlame.getCamPerspective() *
        (cameraMatrix[0][2] * pPoint.x + cameraMatrix[1][2] * pPoint.y + cameraMatrix[2][2] * z);
    pPoint.x = x / zr;
    pPoint.y = y / zr;
  }

  public void renderFlame(Flame pFlame, SimpleImage pImage) {
    if (pFlame.getXForms().size() == 0)
      return;
    initRaster(pFlame, pImage);
    init3D(pFlame);
    createColorMap(pFlame);
    initView(pFlame);
    createModWeightTables(pFlame);
    iterate(pFlame);
    switch (renderMode) {
      case NORMAL:
        renderImage(pFlame, pImage);
        break;
      case Z_MIN:
        renderZMinImage(pFlame, pImage);
        break;
      case Z_MAX:
        renderZMaxImage(pFlame, pImage);
        break;
    }
  }

  // TODO interface (the same in LogDensityFilter)
  private RasterPoint emptyRasterPoint = new RasterPoint();

  private RasterPoint getRasterPoint(int pX, int pY) {
    if (pX < 0 || pX >= rasterWidth || pY < 0 || pY >= rasterHeight)
      return emptyRasterPoint;
    else
      return raster[pY][pX];
  }

  private void renderImage(Flame pFlame, SimpleImage pImage) {
    LogDensityPoint logDensityPnt = new LogDensityPoint();
    GammaCorrectedRGBPoint rbgPoint = new GammaCorrectedRGBPoint();
    logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pImage);
    for (int i = 0, by = 0; i < pImage.getImageHeight(); i++) {
      for (int j = 0, bx = 0; j < pImage.getImageWidth(); j++) {
        logDensityFilter.transformPoint(logDensityPnt, bx, by);
        gammaCorrectionFilter.transformPoint(logDensityPnt, rbgPoint);
        pImage.setRGB(j, i, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
        bx += pFlame.getSpatialOversample();
      }
      by += pFlame.getSpatialOversample();
    }
  }

  private void iterate(Flame pFlame) {
    int nSamples = (int) (pFlame.getSampleDensity() * 1 * rasterSize / (pFlame.getSpatialOversample() * pFlame.getSpatialOversample()) + 0.5);
    int nThreads = 8;
    List<FlameRenderThread> threads = new ArrayList<FlameRenderThread>();
    for (int i = 0; i < nThreads; i++) {
      FlameRenderThread t = new FlameRenderThread(this, pFlame, nSamples / nThreads, renderMode, affineZStyle);
      threads.add(t);
      new Thread(t).start();
    }
    boolean done = false;
    while (!done) {
      try {
        Thread.sleep(33);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      done = true;
      for (FlameRenderThread t : threads) {
        if (!t.isFinished()) {
          done = false;
          break;
        }
      }
    }
  }

  private void initView(Flame pFlame) {
    double pixelsPerUnit = pFlame.getPixelsPerUnit() * pFlame.getCamZoom();
    double corner_x = pFlame.getCentreX() - (double) imageWidth / pixelsPerUnit / 2.0;
    double corner_y = pFlame.getCentreY() - (double) imageHeight / pixelsPerUnit / 2.0;
    double t0 = borderWidth / (pFlame.getSpatialOversample() * pixelsPerUnit);
    double t1 = borderWidth / (pFlame.getSpatialOversample() * pixelsPerUnit);
    double t2 = (2 * maxBorderWidth - borderWidth) / (pFlame.getSpatialOversample() * pixelsPerUnit);
    double t3 = (2 * maxBorderWidth - borderWidth) / (pFlame.getSpatialOversample() * pixelsPerUnit);

    double camX0 = corner_x - t0;
    double camY0 = corner_y - t1;
    double camX1 = corner_x + (double) imageWidth / pixelsPerUnit + t2;
    double camY1 = corner_y + (double) imageHeight / pixelsPerUnit + t3;

    camW = camX1 - camX0;
    double Xsize, Ysize;
    if (Math.abs(camW) > 0.01)
      Xsize = 1.0 / camW;
    else
      Xsize = 1.0;
    camH = camY1 - camY0;
    if (Math.abs(camH) > 0.01)
      Ysize = 1.0 / camH;
    else
      Ysize = 1;
    bws = (rasterWidth - 0.5) * Xsize;
    bhs = (rasterHeight - 0.5) * Ysize;

    cosa = Math.cos(-Math.PI * (pFlame.getCamRoll()) / 180.0);
    sina = Math.sin(-Math.PI * (pFlame.getCamRoll()) / 180.0);
    rcX = pFlame.getCentreX() * (1 - cosa) - pFlame.getCentreY() * sina - camX0;
    rcY = pFlame.getCentreY() * (1 - cosa) + pFlame.getCentreX() * sina - camY0;
  }

  private void createModWeightTables(Flame pFlame) {
    double tp[] = new double[100];
    int n = pFlame.getXForms().size();
    for (XForm xForm : pFlame.getXForms()) {
      xForm.initTransform();
      Collections.sort(xForm.getVariations(), new VariationPriorityComparator());
      for (Variation var : xForm.getVariations()) {
        var.getFunc().init(this, xForm);
      }
    }
    if (pFlame.getFinalXForm() != null) {
      XForm xForm = pFlame.getFinalXForm();
      xForm.initTransform();
      Collections.sort(xForm.getVariations(), new VariationPriorityComparator());
      for (Variation var : xForm.getVariations()) {
        var.getFunc().init(this, xForm);
      }
    }
    //
    for (int k = 0; k < n; k++) {
      double totValue = 0;
      XForm xform = pFlame.getXForms().get(k);
      for (int l = 0; l < xform.getNextAppliedXFormTable().length; l++) {
        xform.getNextAppliedXFormTable()[l] = new XForm();
      }
      for (int i = 0; i < n; i++) {
        tp[i] = pFlame.getXForms().get(i).getWeight() * pFlame.getXForms().get(k).getModifiedWeights()[i];
        totValue = totValue + tp[i];
      }

      if (totValue > 0) {
        double loopValue = 0;
        for (int i = 0; i < xform.getNextAppliedXFormTable().length; i++) {
          double totalProb = 0;
          int j = -1;
          do {
            j++;
            totalProb = totalProb + tp[j];
          }
          while (!((totalProb > loopValue) || (j == n - 1)));
          xform.getNextAppliedXFormTable()[i] = pFlame.getXForms().get(j);
          loopValue = loopValue + totValue / (double) xform.getNextAppliedXFormTable().length;
        }
      }
      else {
        for (int i = 0; i < xform.getNextAppliedXFormTable().length - 1; i++) {
          xform.getNextAppliedXFormTable()[i] = null;
        }
      }
    }
  }

  private void createColorMap(Flame pFlame) {
    colorMap = pFlame.getPalette().createRenderPalette(pFlame.getWhiteLevel());
    paletteIdxScl = colorMap.length - 1;
  }

  public void setRandomNumberGenerator(RandomNumberGenerator random) {
    this.random = random;
  }

  @Override
  public RandomNumberGenerator getRandomNumberGenerator() {
    return random;
  }

  public void setRenderMode(RenderMode renderMode) {
    this.renderMode = renderMode;
  }

  private void renderZMinImage(Flame pFlame, SimpleImage pImage) {
    RasterPoint p = getRasterPoint(0, 0);
    double zMin = p.zMax;
    double zMax = p.zMax;
    for (int i = 0, by = 0; i < pImage.getImageHeight(); i++) {
      for (int j = 0, bx = 0; j < pImage.getImageWidth(); j++) {
        p = getRasterPoint(bx, by);
        if (p.zMin < zMin) {
          zMin = p.zMin;
        }
        else if (p.zMin > zMax) {
          zMax = p.zMin;
        }
        bx += pFlame.getSpatialOversample();
      }
      by += pFlame.getSpatialOversample();
    }
    if ((zMax - zMin) < Tools.ZERO) {
      zMax = Tools.ZERO;
    }
    double dz = zMax - zMin;
    for (int i = 0, by = 0; i < pImage.getImageHeight(); i++) {
      for (int j = 0, bx = 0; j < pImage.getImageWidth(); j++) {
        p = getRasterPoint(bx, by);
        int val = Tools.roundColor((p.zMin - zMin) * 255.0 / dz);
        pImage.setRGB(j, i, val, val, val);
        bx += pFlame.getSpatialOversample();
      }
      by += pFlame.getSpatialOversample();
    }
  }

  private void renderZMaxImage(Flame pFlame, SimpleImage pImage) {
    RasterPoint p = getRasterPoint(0, 0);
    double zMin = p.zMax;
    double zMax = p.zMax;
    for (int i = 0, by = 0; i < pImage.getImageHeight(); i++) {
      for (int j = 0, bx = 0; j < pImage.getImageWidth(); j++) {
        p = getRasterPoint(bx, by);
        if (p.zMax < zMin) {
          zMin = p.zMax;
        }
        else if (p.zMax > zMax) {
          zMax = p.zMax;
        }
        bx += pFlame.getSpatialOversample();
      }
      by += pFlame.getSpatialOversample();
    }
    if ((zMax - zMin) < Tools.ZERO) {
      zMax = Tools.ZERO;
    }
    double dz = zMax - zMin;
    for (int i = 0, by = 0; i < pImage.getImageHeight(); i++) {
      for (int j = 0, bx = 0; j < pImage.getImageWidth(); j++) {
        p = getRasterPoint(bx, by);
        int val = Tools.roundColor((p.zMax - zMin) * 255.0 / dz);
        pImage.setRGB(j, i, val, val, val);
        bx += pFlame.getSpatialOversample();
      }
      by += pFlame.getSpatialOversample();
    }
  }

  public void setAffineZStyle(AffineZStyle affineZStyle) {
    this.affineZStyle = affineZStyle;
  }

}
