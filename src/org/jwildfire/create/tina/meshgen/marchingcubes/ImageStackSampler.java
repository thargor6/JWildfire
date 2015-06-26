/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen.marchingcubes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.meshgen.filter.PreFilter;
import org.jwildfire.create.tina.render.filter.FilterKernel;
import org.jwildfire.create.tina.render.filter.GaussianFilterKernel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;

public class ImageStackSampler {
  private static final int MAX_IMAGES_IN_CACHE = 128;
  private static final double MIN_SPATIAL_FILTER_RADIUS = 0.0;
  private static final double MAX_SPATIAL_FILTER_RADIUS = 0.5;

  private final List<PreFilter> preFilterList;

  private final int stackXSize;
  private final int stackYSize;
  private final int stackZSize;

  private final List<SimpleImage> images = new ArrayList<SimpleImage>();
  private final List<Integer> imagesIndex = new ArrayList<Integer>();

  private final String inputfilename;
  private final double spatialFilterRadius;
  private final int downsample;

  private final FilterKernel filterKernel = new GaussianFilterKernel();
  private final double[][][] filter;
  private final int noiseFilterSize;

  public ImageStackSampler(String pInputfilename, int pSlices, int inputSequenceStep, double pSpatialFilterRadius, int pDownsample, List<PreFilter> pPreFilterList) throws Exception {
    if (pPreFilterList != null) {
      preFilterList = pPreFilterList;
    }
    else {
      preFilterList = Collections.emptyList();
    }
    inputfilename = pInputfilename;
    downsample = pDownsample >= 1 ? pDownsample : 1;
    for (int i = 1; i <= pSlices; i += inputSequenceStep) {
      images.add(null);
      imagesIndex.add(Integer.valueOf(i));
    }
    String filename = String.format(inputfilename, 1);
    images.set(0, new ImageReader().loadImage(filename));

    stackXSize = images.get(0).getImageWidth() / downsample;
    stackYSize = images.get(0).getImageHeight() / downsample;
    stackZSize = images.size();

    spatialFilterRadius = pSpatialFilterRadius < MIN_SPATIAL_FILTER_RADIUS ? MIN_SPATIAL_FILTER_RADIUS : pSpatialFilterRadius > MAX_SPATIAL_FILTER_RADIUS ? MAX_SPATIAL_FILTER_RADIUS : pSpatialFilterRadius;

    noiseFilterSize = filterKernel.getFilterSize(spatialFilterRadius, 1);

    filter = new double[noiseFilterSize][noiseFilterSize][noiseFilterSize];
    initFilter(spatialFilterRadius, noiseFilterSize, filter);
  }

  public int getIntensity(Point3f pPoint) {
    return getIntensity((int) pPoint.x, (int) pPoint.y, (int) pPoint.z);
  }

  public int getIntensity(int pX, int pY, int pZ) {
    if (noiseFilterSize > 1) {
      double sum = 0.0;
      for (int i = 0; i < noiseFilterSize; i++) {
        for (int j = 0; j < noiseFilterSize; j++) {
          for (int k = 0; k < noiseFilterSize; k++) {
            sum += filter[i][j][k] * getIntensityInternal(pX + i, pY + j, pZ + k);
          }
        }
      }
      return Tools.FTOI(sum);
    }
    else {
      return getIntensityInternal(pX, pY, pZ);
    }
  }

  private void initFilter(double pFilterRadius, int pFilterSize, double[][][] pFilter) {
    double adjust = filterKernel.getFilterAdjust(pFilterRadius, 1);
    for (int i = 0; i < pFilterSize; i++) {
      for (int j = 0; j < pFilterSize; j++) {
        for (int k = 0; k < pFilterSize; k++) {
          double ii = ((2.0 * i + 1.0) / pFilterSize - 1.0) * adjust;
          double jj = ((2.0 * j + 1.0) / pFilterSize - 1.0) * adjust;
          double kk = ((2.0 * k + 1.0) / pFilterSize - 1.0) * adjust;
          pFilter[i][j][k] = filterKernel.getFilterCoeff(ii) * filterKernel.getFilterCoeff(jj) * filterKernel.getFilterCoeff(kk);
        }
      }
    }
    // normalize
    {
      double t = 0.0;
      for (int i = 0; i < pFilterSize; i++) {
        for (int j = 0; j < pFilterSize; j++) {
          for (int k = 0; k < pFilterSize; k++) {
            t += pFilter[i][j][k];
          }
        }
      }
      for (int i = 0; i < pFilterSize; i++) {
        for (int j = 0; j < pFilterSize; j++) {
          for (int k = 0; k < pFilterSize; k++) {
            pFilter[i][j][k] = pFilter[i][j][k] / t;
          }
        }
      }
    }
  }

  private int getIntensityInternal(int pX, int pY, int pZ) {
    if (pX < 0 || pX >= stackXSize || pY < 0 || pY >= stackYSize || pZ < 0 || pZ >= stackZSize)
      return 0;
    SimpleImage img = images.get(pZ);
    if (img == null) {
      String filename = String.format(inputfilename, imagesIndex.get(pZ));
      try {
        SimpleImage loadedImage = new ImageReader().loadImage(filename);
        for (PreFilter filter : preFilterList) {
          filter.apply(loadedImage);
        }
        if (downsample > 1) {
          double square = downsample * downsample;
          img = new SimpleImage(loadedImage.getImageWidth() / downsample, loadedImage.getImageHeight() / downsample);
          for (int i = 0; i < img.getImageHeight(); i++) {
            for (int j = 0; j < img.getImageWidth(); j++) {
              long sum = 0;
              for (int ii = 0; ii < downsample; ii++) {
                for (int jj = 0; jj < downsample; jj++) {
                  sum += loadedImage.getRValue(j * downsample + jj, i * downsample + ii);
                }
              }
              int value = (int) (sum / square + 0.5);
              img.setRGB(j, i, value, value, value);
            }
          }
        }
        else {
          img = loadedImage;
        }
      }
      catch (Exception ex) {
        throw new RuntimeException(ex);
      }
      images.set(pZ, img);
      int imgCount = 1;
      for (int i = pZ - 1; i >= 0; i--) {
        if (images.get(i) != null) {
          imgCount++;
          if (imgCount >= MAX_IMAGES_IN_CACHE) {
            images.set(i, null);
            imgCount--;
          }
        }
        else {
          break;
        }
      }
    }
    return img.getRValue(pX, pY);
  }

  public int getStackXSize() {
    return stackXSize;
  }

  public int getStackYSize() {
    return stackYSize;
  }

  public int getStackZSize() {
    return stackZSize;
  }

}
