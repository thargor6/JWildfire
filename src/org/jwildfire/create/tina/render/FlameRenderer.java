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
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.random.RandomNumberGenerator;
import org.jwildfire.create.tina.random.SimpleRandomNumberGenerator;
import org.jwildfire.create.tina.swing.ProgressUpdater;
import org.jwildfire.create.tina.variation.TransformationContext;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;

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
  private boolean doProject3D = false;
  // init in init3D()
  private double cameraMatrix[][] = new double[3][3];
  //
  private AffineZStyle affineZStyle = AffineZStyle.FLAT;
  private ProgressUpdater progressUpdater;

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
    doProject3D = Math.abs(pFlame.getCamYaw()) > Tools.EPSILON || Math.abs(pFlame.getCamPitch()) > Tools.EPSILON || Math.abs(pFlame.getCamPerspective()) > Tools.EPSILON || Math.abs(pFlame.getCamDOF()) > Tools.EPSILON;
  }

  private void initRaster(Flame pFlame, SimpleImage pImage) {
    imageWidth = pImage.getImageWidth();
    imageHeight = pImage.getImageHeight();
    logDensityFilter = new LogDensityFilter(pFlame);
    gammaCorrectionFilter = new GammaCorrectionFilter(pFlame);
    maxBorderWidth = (MAX_FILTER_WIDTH - 1) / 2;
    borderWidth = (logDensityFilter.getNoiseFilterSize() - 1) / 2;
    rasterWidth = imageWidth + 2 * maxBorderWidth;
    rasterHeight = imageHeight + 2 * maxBorderWidth;
    rasterSize = rasterWidth * rasterHeight;
    raster = new RasterPoint[rasterHeight][rasterWidth];
    for (int i = 0; i < rasterHeight; i++) {
      for (int j = 0; j < rasterWidth; j++) {
        raster[i][j] = new RasterPoint();
      }
    }
  }

  void project(Flame pFlame, XYZPoint pPoint) {
    if (!doProject3D) {
      return;
    }
    double z = pPoint.z;
    double px = cameraMatrix[0][0] * pPoint.x + cameraMatrix[1][0] * pPoint.y;
    double py = cameraMatrix[0][1] * pPoint.x + cameraMatrix[1][1] * pPoint.y + cameraMatrix[2][1] * z;
    double pz = cameraMatrix[0][2] * pPoint.x + cameraMatrix[1][2] * pPoint.y + cameraMatrix[2][2] * z;
    double zr = 1 - pFlame.getCamPerspective() * pz;
    if (Math.abs(pFlame.getCamDOF()) > Tools.EPSILON) {
      double a = 2.0 * Math.PI * random.random();
      double dsina = Math.sin(a);
      double dcosa = Math.cos(a);
      double zdist = (pFlame.getCamZ() - pz);
      double dr;
      if (zdist > 0.0) {
        dr = random.random() * pFlame.getCamDOF() * 0.1 * zdist;
      }
      else {
        dr = 0.0;
      }
      pPoint.x = (px + dr * dcosa) / zr;
      pPoint.y = (py + dr * dsina) / zr;

    }
    else {
      pPoint.x = px / zr;
      pPoint.y = py / zr;
    }
  }

  public void renderFlame(Flame pFlame, SimpleImage pImage, int pThreads) {
    if (pFlame.getXForms().size() == 0)
      return;
    int spatialOversample = pFlame.getSampleDensity() >= 100 ? pFlame.getSpatialOversample() : 1;
    if (spatialOversample < 1 || spatialOversample > 6) {
      throw new IllegalArgumentException(String.valueOf(spatialOversample));
    }
    int colorOversample = pFlame.getSampleDensity() >= 100 ? pFlame.getColorOversample() : 1;
    if (colorOversample < 1 || colorOversample > 10) {
      throw new IllegalArgumentException(String.valueOf(colorOversample));
    }

    double origZoom = pFlame.getCamZoom();
    try {
      SimpleImage images[] = new SimpleImage[colorOversample];
      for (int i = 0; i < colorOversample; i++) {
        SimpleImage img;
        // spatial oversampling: create enlarged image
        if (spatialOversample > 1) {
          img = new SimpleImage(pImage.getImageWidth() * spatialOversample, pImage.getImageHeight() * spatialOversample);
          if (i == 0) {
            pFlame.setCamZoom((double) spatialOversample * pFlame.getCamZoom());
          }
        }
        else {
          if (colorOversample > 1) {
            img = new SimpleImage(pImage.getImageWidth(), pImage.getImageHeight());
          }
          else {
            img = pImage;
          }
        }
        images[i] = img;
        initRaster(pFlame, img);
        init3D(pFlame);
        createColorMap(pFlame);
        initView(pFlame);
        createModWeightTables(pFlame);
        iterate(pFlame, i, colorOversample, pThreads);
        if (pFlame.getSampleDensity() <= 10) {
          renderImageSimple(pFlame, img);
        }
        else {
          renderImage(pFlame, img);
        }
      }

      // color oversampling
      SimpleImage img;
      if (colorOversample == 1) {
        img = images[0];
      }
      else {
        int width = images[0].getImageWidth();
        int height = images[0].getImageHeight();
        if (spatialOversample > 1) {
          img = new SimpleImage(width, height);
        }
        else {
          img = pImage;
        }
        Pixel toolPixel = new Pixel();
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            int r = 0, g = 0, b = 0;
            for (int k = 0; k < colorOversample; k++) {
              toolPixel.setARGBValue(images[k].getARGBValue(j, i));
              r += toolPixel.r;
              g += toolPixel.g;
              b += toolPixel.b;
            }
            toolPixel.r = Tools.roundColor((double) r / (double) colorOversample);
            toolPixel.g = Tools.roundColor((double) g / (double) colorOversample);
            toolPixel.b = Tools.roundColor((double) b / (double) colorOversample);
            img.setARGB(j, i, toolPixel.getARGBValue());
          }
        }
      }

      // spatial oversampling: scale down
      if (spatialOversample > 1) {
        ScaleTransformer scaleT = new ScaleTransformer();
        scaleT.setScaleWidth(pImage.getImageWidth());
        scaleT.setScaleHeight(pImage.getImageHeight());
        scaleT.setAspect(ScaleAspect.IGNORE);
        scaleT.transformImage(img);
        pImage.setBufferedImage(img.getBufferedImg(), pImage.getImageWidth(), pImage.getImageHeight());
      }
    }
    finally {
      pFlame.setCamZoom(origZoom);
    }
  }

  private void renderImage(Flame pFlame, SimpleImage pImage) {
    LogDensityPoint logDensityPnt = new LogDensityPoint();
    GammaCorrectedRGBPoint rbgPoint = new GammaCorrectedRGBPoint();
    logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pImage);
    for (int i = 0; i < pImage.getImageHeight(); i++) {
      for (int j = 0; j < pImage.getImageWidth(); j++) {
        logDensityFilter.transformPoint(logDensityPnt, j, i);
        gammaCorrectionFilter.transformPoint(logDensityPnt, rbgPoint);
        pImage.setRGB(j, i, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
      }
    }
  }

  private void renderImageSimple(Flame pFlame, SimpleImage pImage) {
    LogDensityPoint logDensityPnt = new LogDensityPoint();
    GammaCorrectedRGBPoint rbgPoint = new GammaCorrectedRGBPoint();
    logDensityFilter.setRaster(raster, rasterWidth, rasterHeight, pImage);
    for (int i = 0; i < pImage.getImageHeight(); i++) {
      for (int j = 0; j < pImage.getImageWidth(); j++) {
        logDensityFilter.transformPointSimple(logDensityPnt, j, i);
        gammaCorrectionFilter.transformPointSimple(logDensityPnt, rbgPoint);
        pImage.setRGB(j, i, rbgPoint.red, rbgPoint.green, rbgPoint.blue);
      }
    }
  }

  private void iterate(Flame pFlame, int pPart, int pParts, int pThreads) {
    long nSamples = (long) ((long) pFlame.getSampleDensity() * (long) rasterSize + 0.5);
    //    if (pFlame.getSampleDensity() > 50) {
    //      System.err.println("SAMPLES: " + nSamples);
    //    }
    int PROGRESS_STEPS = 100;
    if (progressUpdater != null && pPart == 0) {
      progressUpdater.initProgress(PROGRESS_STEPS * pParts);
    }
    long sampleProgressUpdateStep = nSamples / 100;
    long nextProgressUpdate = sampleProgressUpdateStep;
    List<FlameRenderThread> threads = new ArrayList<FlameRenderThread>();
    for (int i = 0; i < pThreads; i++) {
      FlameRenderThread t = new FlameRenderThread(this, pFlame, nSamples / (long) pThreads, affineZStyle);
      threads.add(t);
      new Thread(t).start();
    }
    boolean done = false;
    while (!done) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      done = true;
      long currSamples = 0;
      for (FlameRenderThread t : threads) {
        if (!t.isFinished()) {
          done = false;
        }
        currSamples += t.getCurrSample();
      }
      if (currSamples >= nextProgressUpdate) {
        if (progressUpdater != null) {
          int currProgress = (int) ((currSamples * PROGRESS_STEPS) / nSamples);
          progressUpdater.updateProgress(currProgress + pPart * PROGRESS_STEPS);
          nextProgressUpdate = (currProgress + 1) * sampleProgressUpdateStep;
        }
      }

    }
  }

  private void initView(Flame pFlame) {
    double pixelsPerUnit = pFlame.getPixelsPerUnit() * pFlame.getCamZoom();
    double corner_x = pFlame.getCentreX() - (double) imageWidth / pixelsPerUnit / 2.0;
    double corner_y = pFlame.getCentreY() - (double) imageHeight / pixelsPerUnit / 2.0;
    double t0 = borderWidth / pixelsPerUnit;
    double t1 = borderWidth / pixelsPerUnit;
    double t2 = (2 * maxBorderWidth - borderWidth) / pixelsPerUnit;
    double t3 = (2 * maxBorderWidth - borderWidth) / pixelsPerUnit;

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
      for (Variation var : xForm.getSortedVariations()) {
        var.getFunc().init(this, xForm);
      }
    }
    if (pFlame.getFinalXForm() != null) {
      XForm xForm = pFlame.getFinalXForm();
      xForm.initTransform();
      for (Variation var : xForm.getSortedVariations()) {
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

  public void setAffineZStyle(AffineZStyle affineZStyle) {
    this.affineZStyle = affineZStyle;
  }

  public void setProgressUpdater(ProgressUpdater pProgressUpdater) {
    progressUpdater = pProgressUpdater;
  }

}
