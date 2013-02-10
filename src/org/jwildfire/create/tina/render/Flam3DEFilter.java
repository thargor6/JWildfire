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
import static org.jwildfire.base.MathLib.M_PI;
import static org.jwildfire.base.MathLib.cos;
import static org.jwildfire.base.MathLib.exp;
import static org.jwildfire.base.MathLib.log10;
import static org.jwildfire.base.MathLib.pow;
import static org.jwildfire.base.MathLib.sin;
import static org.jwildfire.base.MathLib.sqrt;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.RasterPoint;

// port of the de filter from flam3: http://flam3.com/index.cgi?&menu=code
public class Flam3DEFilter {
  private final Flame flame;
  final static int DE_THRESH = 100;
  int max_filtered_counts;
  int max_filter_index;
  int kernel_size;
  double filter_widths[];
  double filter_coefs[];

  public Flam3DEFilter(Flame pFlame) {
    flame = pFlame;
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
            de_filt_sum += flam3_spatial_filter(flam3_gaussian_kernel,
                flam3_spatial_support[flam3_gaussian_kernel] * de_filt_d);

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
            this.filter_coefs[filter_coef_idx] = flam3_spatial_filter(flam3_gaussian_kernel,
                flam3_spatial_support[flam3_gaussian_kernel] * de_filt_d) / de_filt_sum;

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

  private double flam3_spatial_filter(int knum, double x) {
    if (knum == 0)
      return flam3_gaussian_filter(x);
    else if (knum == 1)
      return flam3_hermite_filter(x);
    else if (knum == 2)
      return flam3_box_filter(x);
    else if (knum == 3)
      return flam3_triangle_filter(x);
    else if (knum == 4)
      return flam3_bell_filter(x);
    else if (knum == 5)
      return flam3_b_spline_filter(x);
    else if (knum == 6)
      return flam3_mitchell_filter(x);
    else if (knum == 7)
      return flam3_sinc(x) * flam3_blackman_filter(x);
    else if (knum == 8)
      return flam3_catrom_filter(x);
    else if (knum == 9)
      return flam3_sinc(x) * flam3_hanning_filter(x);
    else if (knum == 10)
      return flam3_sinc(x) * flam3_hamming_filter(x);
    else if (knum == 11)
      return flam3_lanczos3_filter(x) * flam3_sinc(x / 3.0);
    else if (knum == 12)
      return flam3_lanczos2_filter(x) * flam3_sinc(x / 2.0);
    else if (knum == 13)
      return flam3_quadratic_filter(x);
    else
      throw new RuntimeException();
  }

  private double flam3_b_spline_filter(double t) {

    /* box (*) box (*) box (*) box */
    double tt;

    if (t < 0)
      t = -t;
    if (t < 1) {
      tt = t * t;
      return ((.5 * tt * t) - tt + (2.0 / 3.0));
    }
    else if (t < 2) {
      t = 2 - t;
      return ((1.0 / 6.0) * (t * t * t));
    }
    return (0.0);
  }

  private double flam3_sinc(double x) {
    x *= M_PI;
    if (x != 0)
      return (sin(x) / x);
    return (1.0);
  }

  private double flam3_blackman_filter(double x) {
    return (0.42 + 0.5 * cos(M_PI * x) + 0.08 * cos(2 * M_PI * x));
  }

  private double flam3_catrom_filter(double x) {
    if (x < -2.0)
      return (0.0);
    if (x < -1.0)
      return (0.5 * (4.0 + x * (8.0 + x * (5.0 + x))));
    if (x < 0.0)
      return (0.5 * (2.0 + x * x * (-5.0 - 3.0 * x)));
    if (x < 1.0)
      return (0.5 * (2.0 + x * x * (-5.0 + 3.0 * x)));
    if (x < 2.0)
      return (0.5 * (4.0 + x * (-8.0 + x * (5.0 - x))));
    return (0.0);
  }

  private double flam3_mitchell_filter(double t) {
    double tt;

    tt = t * t;
    if (t < 0)
      t = -t;
    if (t < 1.0) {
      t = (((12.0 - 9.0 * flam3_mitchell_b - 6.0 * flam3_mitchell_c) * (t * tt))
          + ((-18.0 + 12.0 * flam3_mitchell_b + 6.0 * flam3_mitchell_c) * tt)
          + (6.0 - 2 * flam3_mitchell_b));
      return (t / 6.0);
    }
    else if (t < 2.0) {
      t = (((-1.0 * flam3_mitchell_b - 6.0 * flam3_mitchell_c) * (t * tt))
          + ((6.0 * flam3_mitchell_b + 30.0 * flam3_mitchell_c) * tt)
          + ((-12.0 * flam3_mitchell_b - 48.0 * flam3_mitchell_c) * t)
          + (8.0 * flam3_mitchell_b + 24 * flam3_mitchell_c));
      return (t / 6.0);
    }
    return (0.0);
  }

  private double flam3_hanning_filter(double x) {
    return (0.5 + 0.5 * cos(M_PI * x));
  }

  private double flam3_hamming_filter(double x) {
    return (0.54 + 0.46 * cos(M_PI * x));
  }

  private double flam3_lanczos3_filter(double t) {
    if (t < 0)
      t = -t;
    if (t < 3.0)
      return (flam3_sinc(t) * flam3_sinc(t / 3.0));
    return (0.0);
  }

  private double flam3_lanczos2_filter(double t) {
    if (t < 0)
      t = -t;
    if (t < 2.0)
      return (flam3_sinc(t) * flam3_sinc(t / 2.0));
    return (0.0);
  }

  private double flam3_gaussian_filter(double x) {
    return (exp((-2.0 * x * x)) * sqrt(2.0 / M_PI));
  }

  private double flam3_quadratic_filter(double x) {
    if (x < -1.5)
      return (0.0);
    if (x < -0.5)
      return (0.5 * (x + 1.5) * (x + 1.5));
    if (x < 0.5)
      return (0.75 - x * x);
    if (x < 1.5)
      return (0.5 * (x - 1.5) * (x - 1.5));
    return (0.0);
  }

  private double flam3_hermite_filter(double t) {
    /* f(t) = 2|t|^3 - 3|t|^2 + 1, -1 <= t <= 1 */
    if (t < 0.0)
      t = -t;
    if (t < 1.0)
      return ((2.0 * t - 3.0) * t * t + 1.0);
    return (0.0);
  }

  private double flam3_box_filter(double t) {
    if ((t > -0.5) && (t <= 0.5))
      return (1.0);
    return (0.0);
  }

  private double flam3_triangle_filter(double t) {
    if (t < 0.0)
      t = -t;
    if (t < 1.0)
      return (1.0 - t);
    return (0.0);
  }

  private double flam3_bell_filter(double t) {
    /* box (*) box (*) box */
    if (t < 0)
      t = -t;
    if (t < .5)
      return (.75 - (t * t));
    if (t < 1.5) {
      t = (t - 1.5);
      return (.5 * (t * t));
    }
    return (0.0);
  }

  private double flam3_spatial_support[] = {
      1.5, /* gaussian */
      1.0, /* hermite */
      0.5, /* box */
      1.0, /* triangle */
      1.5, /* bell */
      2.0, /* b spline */
      2.0, /* mitchell */
      1.0, /* blackman */
      2.0, /* catrom */
      1.0, /* hanning */
      1.0, /* hamming */
      3.0, /* lanczos3 */
      2.0, /* lanczos2 */
      1.5 /* quadratic */
  };

  private final double flam3_mitchell_b = (1.0 / 3.0);
  private final double flam3_mitchell_c = (1.0 / 3.0);

  private final int flam3_gaussian_kernel = 0;
  private final int flam3_hermite_kernel = 1;
  private final int flam3_box_kernel = 2;
  private final int flam3_triangle_kernel = 3;
  private final int flam3_bell_kernel = 4;
  private final int flam3_b_spline_kernel = 5;
  private final int flam3_lanczos3_kernel = 6;
  private final int flam3_lanczos2_kernel = 7;
  private final int flam3_mitchell_kernel = 8;
  private final int flam3_blackman_kernel = 9;
  private final int flam3_catrom_kernel = 10;
  private final int flam3_hamming_kernel = 11;
  private final int flam3_hanning_kernel = 12;
  private final int flam3_quadratic_kernel = 13;

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

        ls = filter_coefs[f_coef_idx] * (k1 * log10(1.0 + intensity * k2)) / intensity;

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
