/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static org.jwildfire.base.MathLib.log;
import static org.jwildfire.base.MathLib.pow;
import static org.jwildfire.base.MathLib.sqrt;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;
import org.jwildfire.create.tina.render.filter.FilterKernel;

// bases on the DE filter code from flam3: http://flam3.com/index.cgi?&menu=code
public class Flam3DEFilter {
  private final Flame flame;
  final static int DE_THRESH = 100;
  private int max_filtered_counts;
  private int max_filter_index;
  private int kernel_size;
  private double filter_widths[];
  private double filter_coefs[];
  private final FilterKernel filterKernel;

  public Flam3DEFilter(Flame pFlame) {
    flame = pFlame;
    filterKernel = pFlame.getDeFilterKernel().createFilterInstance();
    int ss = 1;
    double max_rad = flame.getDeFilterMaxRadius();
    double min_rad = flame.getDeFilterMinRadius();
    double curve = flame.getDeFilterCurve();
    double comp_max_radius, comp_min_radius;
    double num_de_filters_d;
    int num_de_filters, de_max_ind;
    int de_row_size, de_half_size;
    int filtloop;
    int keep_thresh = 100;

    this.kernel_size = -1;

    if (curve <= 0.0) {
      throw new RuntimeException("estimator curve must be > 0");
    }

    if (max_rad < min_rad) {
      throw new RuntimeException("estimator must be larger than estimator_minimum.");
    }

    /* We should scale the filter width by the oversample          */
    /* The '+1' comes from the assumed distance to the first pixel */
    comp_max_radius = max_rad * ss + 1;
    comp_min_radius = min_rad * ss + 1;

    /* Calculate how many filter kernels we need based on the decay function */
    /*                                                                       */
    /*    num filters = (de_max_width / de_min_width)^(1/estimator_curve)    */
    /*                                                                       */
    num_de_filters_d = pow(comp_max_radius / comp_min_radius, 1.0 / curve);
    if (num_de_filters_d > 1e7) {
      throw new RuntimeException("too many filters required in this configuration");
    }
    num_de_filters = (int) ceil(num_de_filters_d);

    /* Condense the smaller kernels to save space */
    if (num_de_filters > keep_thresh) {
      de_max_ind = (int) ceil(DE_THRESH + pow(num_de_filters - DE_THRESH, curve)) + 1;
      this.max_filtered_counts = (int) pow((double) (de_max_ind - DE_THRESH), 1.0 / curve) + DE_THRESH;
    }
    else {
      de_max_ind = num_de_filters;
      this.max_filtered_counts = de_max_ind;
    }
    /* Allocate the memory for these filters */
    /* and the hit/width lookup vector       */
    de_row_size = (int) (2 * ceil(comp_max_radius) - 1);
    de_half_size = (de_row_size - 1) / 2;
    this.kernel_size = (de_half_size + 1) * (2 + de_half_size) / 2;

    this.filter_coefs = new double[de_max_ind * this.kernel_size];
    this.filter_widths = new double[de_max_ind];

    /* Generate the filter coefficients */
    this.max_filter_index = 0;
    for (filtloop = 0; filtloop < de_max_ind; filtloop++) {

      double de_filt_sum = 0.0, de_filt_d;
      double de_filt_h;
      int dej, dek;
      double adjloop;
      int filter_coef_idx;

      /* Calculate the filter width for this number of hits in a bin */
      if (filtloop < keep_thresh)
        de_filt_h = (comp_max_radius / pow(filtloop + 1, curve));
      else {
        adjloop = pow(filtloop - keep_thresh, (1.0 / curve)) + keep_thresh;
        de_filt_h = (comp_max_radius / pow(adjloop + 1, curve));
      }

      /* Once we've reached the min radius, don't populate any more */
      if (de_filt_h <= comp_min_radius) {
        de_filt_h = comp_min_radius;
        this.max_filter_index = filtloop;
      }

      this.filter_widths[filtloop] = de_filt_h;

      /* Calculate norm of kernel separately (easier) */
      for (dej = -de_half_size; dej <= de_half_size; dej++) {
        for (dek = -de_half_size; dek <= de_half_size; dek++) {

          de_filt_d = sqrt((double) (dej * dej + dek * dek)) / de_filt_h;

          /* Only populate the coefs within this radius */
          if (de_filt_d <= 1.0) {

            /* Gaussian */
            de_filt_sum += filterKernel.getFilterCoeff(filterKernel.getSpatialSupport() * de_filt_d);

            /* Epanichnikov */
            //              de_filt_sum += (1.0 - (de_filt_d * de_filt_d));
          }
        }
      }

      filter_coef_idx = filtloop * this.kernel_size;

      /* Calculate the unique entries of the kernel */
      for (dej = 0; dej <= de_half_size; dej++) {
        for (dek = 0; dek <= dej; dek++) {
          de_filt_d = sqrt((double) (dej * dej + dek * dek)) / de_filt_h;

          /* Only populate the coefs within this radius */
          if (de_filt_d > 1.0)
            this.filter_coefs[filter_coef_idx] = 0.0;
          else {

            /* Gaussian */
            this.filter_coefs[filter_coef_idx] = filterKernel.getFilterCoeff(filterKernel.getSpatialSupport() * de_filt_d) / de_filt_sum;
            /* Epanichnikov */
            //            this.filter_coefs[filter_coef_idx] = (1.0 - (de_filt_d * de_filt_d)) / de_filt_sum;
          }

          filter_coef_idx++;
        }
      }

      if (this.max_filter_index > 0)
        break;
    }

    if (this.max_filter_index == 0)
      this.max_filter_index = de_max_ind - 1;
  }

  private double k1, k2;
  private RasterPoint[][] raster;
  private RasterPoint[][] accumRaster;
  private int rasterWidth, rasterHeight;

  private RasterPoint emptyRasterPoint = new RasterPoint();

  private RasterPoint getRasterPoint(int pX, int pY) {
    if (pX < 0 || pX >= rasterWidth || pY < 0 || pY >= rasterHeight)
      return emptyRasterPoint;
    else
      return raster[pY][pX];
  }

  public void setRaster(RasterPoint[][] pAccumRaster, RasterPoint[][] pRaster, int pRasterWidth, int pRasterHeight, int pImageWidth, int pImageHeight) {
    accumRaster = pAccumRaster;
    raster = pRaster;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    k1 = (flame.getContrast() * flame.getBrightness() * 268.0 * LogDensityFilter.FILTER_WHITE) / 256.0;
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double area = ((double) pImageWidth * (double) pImageHeight) / (pixelsPerUnit * pixelsPerUnit);
    k2 = 1.0 / (flame.getContrast() * area * (double) flame.getWhiteLevel() * flame.getSampleDensity());
  }

  public void transformPoint(int pX, int pY) {
    int ii, jj;
    double f_select = 0.0;
    int f_select_int, f_coef_idx;
    int arr_filt_width;
    double ls;
    int oversample = 1;
    boolean scf = (oversample & 1) == 0;
    double scfact = pow(oversample / (oversample + 1.0), 2.0);

    double red = 0;
    double green = 0;
    double blue = 0;
    double intensity = 0;

    RasterPoint point = getRasterPoint(pX, pY);

    /* Don't do anything if there's no hits here */
    if (point.count == 0 || point.blue == 0)
      return;

    /* Count density in ssxss area   */
    f_select += point.count / 255.0;

    if (scf)
      f_select *= scfact;

    if (f_select > this.max_filtered_counts)
      f_select_int = this.max_filter_index;
    else if (f_select <= DE_THRESH)
      f_select_int = (int) ceil(f_select) - 1;
    else
      f_select_int = (int) Flam3DEFilter.DE_THRESH +
          (int) floor(pow(f_select - Flam3DEFilter.DE_THRESH, flame.getDeFilterCurve()));

    /* If the filter selected below the min specified clamp it to the min */
    if (f_select_int > this.max_filter_index)
      f_select_int = this.max_filter_index;

    /* We only have to calculate the values for ~1/8 of the square */
    f_coef_idx = f_select_int * this.kernel_size;

    arr_filt_width = (int) ceil(this.filter_widths[f_select_int]) - 1;

    for (jj = 0; jj <= arr_filt_width; jj++) {
      for (ii = 0; ii <= jj; ii++) {

        /* Skip if coef is 0 */
        if (this.filter_coefs[f_coef_idx] == 0.0) {
          f_coef_idx++;
          continue;
        }

        red = point.red;
        green = point.green;
        blue = point.blue;
        intensity = point.count;

        ls = filter_coefs[f_coef_idx] * (k1 * log(1.0 + intensity * k2)) / intensity;

        red *= ls;
        green *= ls;
        blue *= ls;
        intensity *= ls;

        if (jj == 0 && ii == 0) {
          addToAccum(pX, ii, pY, jj, red, green, blue, intensity);
        }
        else if (ii == 0) {
          addToAccum(pX, jj, pY, 0, red, green, blue, intensity);
          addToAccum(pX, -jj, pY, 0, red, green, blue, intensity);
          addToAccum(pX, 0, pY, jj, red, green, blue, intensity);
          addToAccum(pX, 0, pY, -jj, red, green, blue, intensity);
        }
        else if (jj == ii) {
          addToAccum(pX, ii, pY, jj, red, green, blue, intensity);
          addToAccum(pX, -ii, pY, jj, red, green, blue, intensity);
          addToAccum(pX, ii, pY, -jj, red, green, blue, intensity);
          addToAccum(pX, -ii, pY, -jj, red, green, blue, intensity);
        }
        else {
          addToAccum(pX, ii, pY, jj, red, green, blue, intensity);
          addToAccum(pX, -ii, pY, jj, red, green, blue, intensity);
          addToAccum(pX, ii, pY, -jj, red, green, blue, intensity);
          addToAccum(pX, -ii, pY, -jj, red, green, blue, intensity);
          addToAccum(pX, jj, pY, ii, red, green, blue, intensity);
          addToAccum(pX, -jj, pY, ii, red, green, blue, intensity);
          addToAccum(pX, jj, pY, -ii, red, green, blue, intensity);
          addToAccum(pX, -jj, pY, -ii, red, green, blue, intensity);
        }
        f_coef_idx++;
      }
    }
  }

  private void addToAccum(int i, int ii, int j, int jj, double red, double green, double blue, double intensity) {
    if ((j) + (jj) >= 0 && (j) + (jj) < (rasterHeight) && (i) + (ii) >= 0 && (i) + (ii) < (rasterWidth)) {
      RasterPoint a = accumRaster[j + jj][i + ii];
      a.red += red;
      a.green += green;
      a.blue += blue;
      a.count += intensity;
    }
  }
}
