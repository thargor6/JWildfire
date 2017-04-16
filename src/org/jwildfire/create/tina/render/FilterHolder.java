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
package org.jwildfire.create.tina.render;

import java.io.Serializable;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.filter.FilterKernel;
import org.jwildfire.create.tina.render.filter.FilterKernelType;

public class FilterHolder implements Serializable {
  protected double filter[][];
  protected int noiseFilterSize;
  protected int noiseFilterSizeHalve;
  protected final FilterKernel filterKernel;
  private final FilterKernelType filterKernelType;
  protected final int oversample;
  private final double intensity;
  private FilterIndicator filterIndicator = FilterIndicator.GREY;

  public FilterHolder(Flame pFlame) {
    this(pFlame.getSpatialFilterKernel(), pFlame.getSpatialOversampling(), pFlame.getSpatialFilterRadius());
  }

  public FilterHolder(FilterKernelType pSpatialFilterKernel, int pSpatialOversampling, double pSpatialFilterRadius) {
    oversample = pSpatialOversampling;
    filterKernelType = pSpatialFilterKernel;
    filterKernel = filterKernelType.createFilterInstance();
    noiseFilterSize = filterKernel.getFilterSize(pSpatialFilterRadius, oversample);
    noiseFilterSizeHalve = noiseFilterSize / 2 - 1;
    intensity = pSpatialFilterRadius;
    filter = new double[noiseFilterSize][noiseFilterSize];
    initFilter(pSpatialFilterRadius, noiseFilterSize, filter);
  }

  private void initFilter(double pFilterRadius, int pFilterSize, double[][] pFilter) {
    double adjust = filterKernel.getFilterAdjust(pFilterRadius, oversample);
    double sum = 0.0;
    for (int i = 0; i < pFilterSize; i++) {
      for (int j = 0; j < pFilterSize; j++) {
        double ii = ((2.0 * i + 1.0) / pFilterSize - 1.0) * adjust;
        double jj = ((2.0 * j + 1.0) / pFilterSize - 1.0) * adjust;
        double f = filterKernel.getFilterCoeff(MathLib.sqrt(ii * ii + jj * jj));
        //        double f = filterKernel.getFilterCoeff(ii) * filterKernel.getFilterCoeff(jj);
        sum += f;
        pFilter[i][j] = f;
      }
    }
    // normalize
    {
      for (int i = 0; i < pFilterSize; i++) {
        for (int j = 0; j < pFilterSize; j++) {
          pFilter[i][j] = pFilter[i][j] / sum * oversample * oversample;
        }
      }
    }
  }

  public int getNoiseFilterSize() {
    return noiseFilterSize;
  }

  public double[][] getFilter() {
    return filter;
  }

  public boolean isEmpty() {
    return noiseFilterSize <= 1;
  }

  public FilterIndicator getFilterIndicator() {
    return filterIndicator;
  }

  public void setFilterIndicator(FilterIndicator filterIndicator) {
    this.filterIndicator = filterIndicator;
  }

  public double getIntensity() {
    return intensity;
  }

  public FilterKernelType getFilterKernelType() {
    return filterKernelType;
  }

}
