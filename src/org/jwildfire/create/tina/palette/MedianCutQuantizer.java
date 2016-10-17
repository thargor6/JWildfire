/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
package org.jwildfire.create.tina.palette;

import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

public class MedianCutQuantizer {
  // Median cut quantization based on jquant2.c by Thomas G. Lane. 

  private final static int HIST_C0_BITS = 5; /* bits of precision in R/B histogram */
  private final static int HIST_C1_BITS = 6; /* bits of precision in G histogram */
  private final static int HIST_C2_BITS = 5; /* bits of precision in B/R histogram */

  /* These are the amounts to shift an input value to get a histogram index. */
  private final static int BITS_IN_JSAMPLE = 8;
  private final static int C0_SHIFT = (BITS_IN_JSAMPLE - HIST_C0_BITS);
  private final static int C1_SHIFT = (BITS_IN_JSAMPLE - HIST_C1_BITS);
  private final static int C2_SHIFT = (BITS_IN_JSAMPLE - HIST_C2_BITS);

  /* Number of elements along histogram axes. */
  private final static int HIST_C0_ELEMS = (1 << HIST_C0_BITS);
  private final static int HIST_C1_ELEMS = (1 << HIST_C1_BITS);
  private final static int HIST_C2_ELEMS = (1 << HIST_C2_BITS);

  private final static int R_SCALE = 2; /* scale R distances by this much */
  private final static int G_SCALE = 3; /* scale G distances by this much */
  private final static int B_SCALE = 1; /* and B by this much */

  private final static int C0_SCALE = R_SCALE;
  private final static int C1_SCALE = G_SCALE;
  private final static int C2_SCALE = B_SCALE;

  private final Pixel toolPixel = new Pixel();
  private static final int colors = RGBPalette.PALETTE_SIZE;
  private final int cmap[][] = new int[3][colors];
  private int histogram[][][] = new int[HIST_C0_ELEMS][HIST_C1_ELEMS][HIST_C2_ELEMS];

  private void prescan_quantize(SimpleImage pImg) {
    int num_rows = pImg.getImageHeight();
    int width = pImg.getImageWidth();
    for (int row = 0; row < num_rows; row++) {
      for (int col = 0; col < width; col++) {
        toolPixel.setARGBValue(pImg.getARGBValue(col, row));
        int i0 = toolPixel.r >> C0_SHIFT;
        int i1 = toolPixel.g >> C1_SHIFT;
        int i2 = toolPixel.b >> C2_SHIFT;
        if (++histogram[i0][i1][i2] <= 0)
          histogram[i0][i1][i2]--;
      }
    }
  }

  private static class Box {
    public int c0min, c0max;/* The bounds of the box (inclusive); expressed as histogram indexes */
    public int c1min, c1max;
    public int c2min, c2max;
    public long volume; /* The volume (actually 2-norm) of the box */
    public long colorcount; /* The number of nonzero histogram cells within this box */
  }

  /* Find the splittable box with the largest color population, returns NULL if no splittable boxes remain */
  private Box find_biggest_color_pop(Box boxlist[]) {
    long maxc = 0;
    Box which = null;
    for (Box box : boxlist) {
      if ((box.colorcount > maxc) && (box.volume > 0)) {
        which = box;
        maxc = box.colorcount;
      }
    }
    return which;
  }

  /* Find the splittable box with the largest (scaled) volume, returns NULL if no splittable boxes remain */
  private Box find_biggest_volume(Box boxlist[]) {
    long maxv = 0;
    Box which = null;
    for (Box box : boxlist) {
      if (box.volume > maxv) {
        which = box;
        maxv = box.volume;
      }
    }
    return which;
  }

  /* Shrink the min/max bounds of a box to enclose only nonzero elements, and recompute its volume and population */
  private void update_box(Box box) {
    int c0, c1, c2;
    int c0min, c0max, c1min, c1max, c2min, c2max;
    long dist0, dist1, dist2;
    long ccount;
    c0min = box.c0min;
    c0max = box.c0max;
    c1min = box.c1min;
    c1max = box.c1max;
    c2min = box.c2min;
    c2max = box.c2max;

    search_have_c0min: if (c0max > c0min) {
      for (c0 = c0min; c0 <= c0max; c0++) {
        for (c1 = c1min; c1 <= c1max; c1++) {
          for (c2 = c2min; c2 <= c2max; c2++) {
            if (histogram[c0][c1][c2] != 0) {
              box.c0min = c0min = c0;
              break search_have_c0min;
            }
          }
        }
      }
    }

    search_c0max: if (c0max > c0min) {
      for (c0 = c0max; c0 >= c0min; c0--) {
        for (c1 = c1min; c1 <= c1max; c1++) {
          for (c2 = c2min; c2 <= c2max; c2++) {
            if (histogram[c0][c1][c2] != 0) {
              box.c0max = c0max = c0;
              break search_c0max;
            }
          }
        }
      }
    }

    search_have_c1min: if (c1max > c1min) {
      for (c1 = c1min; c1 <= c1max; c1++) {
        for (c0 = c0min; c0 <= c0max; c0++) {
          for (c2 = c2min; c2 <= c2max; c2++) {
            if (histogram[c0][c1][c2] != 0) {
              box.c1min = c1min = c1;
              break search_have_c1min;
            }
          }
        }
      }
    }

    search_c1max: if (c1max > c1min) {
      for (c1 = c1max; c1 >= c1min; c1--) {
        for (c0 = c0min; c0 <= c0max; c0++) {
          for (c2 = c2min; c2 <= c2max; c2++) {
            if (histogram[c0][c1][c2] != 0) {
              box.c1max = c1max = c1;
              break search_c1max;
            }
          }
        }
      }
    }

    search_c2min: if (c2max > c2min) {
      for (c2 = c2min; c2 <= c2max; c2++) {
        for (c0 = c0min; c0 <= c0max; c0++) {
          for (c1 = c1min; c1 <= c1max; c1++) {
            if (histogram[c0][c1][c2] != 0) {
              box.c2min = c2min = c2;
              break search_c2min;
            }
          }
        }
      }
    }

    search_c2max: if (c2max > c2min) {
      for (c2 = c2max; c2 >= c2min; c2--) {
        for (c0 = c0min; c0 <= c0max; c0++) {
          for (c1 = c1min; c1 <= c1max; c1++) {
            if (histogram[c0][c1][c2] != 0) {
              box.c2max = c2max = c2;
              break search_c2max;
            }
          }
        }
      }
    }

    dist0 = ((c0max - c0min) << C0_SHIFT) * C0_SCALE;
    dist1 = ((c1max - c1min) << C1_SHIFT) * C1_SCALE;
    dist2 = ((c2max - c2min) << C2_SHIFT) * C2_SCALE;
    box.volume = dist0 * dist0 + dist1 * dist1 + dist2 * dist2;

    /* Now scan remaining volume of box and compute population */
    ccount = 0;
    for (c0 = c0min; c0 <= c0max; c0++) {
      for (c1 = c1min; c1 <= c1max; c1++) {
        for (c2 = c2min; c2 <= c2max; c2++) {
          if (histogram[c0][c1][c2] != 0) {
            ccount++;
          }
        }
      }
    }
    box.colorcount = ccount;
  }

  /* Repeatedly select and split the largest box until we have enough boxes */
  private int median_cut(Box boxlist[], int numboxes, int desired_colors) {
    while (numboxes < desired_colors) {
      /* Select box to split. Current algorithm: by population for first half, then by volume.
       */
      Box b1;
      int lb;
      if (numboxes * 2 <= desired_colors) {
        b1 = find_biggest_color_pop(boxlist);
      }
      else {
        b1 = find_biggest_volume(boxlist);
      }
      if (b1 == null)
        break; /* no splittable boxes left! */
      Box b2 = boxlist[numboxes]; /* where new box will go */
      /* Copy the color bounds to the new box. */
      b2.c0max = b1.c0max;
      b2.c1max = b1.c1max;
      b2.c2max = b1.c2max;
      b2.c0min = b1.c0min;
      b2.c1min = b1.c1min;
      b2.c2min = b1.c2min;
      /* Choose which axis to split the box on.
       * Current algorithm: longest scaled axis.
       * See notes in update_box about scaling distances.
       */
      int c0 = ((b1.c0max - b1.c0min) << C0_SHIFT) * C0_SCALE;
      int c1 = ((b1.c1max - b1.c1min) << C1_SHIFT) * C1_SCALE;
      int c2 = ((b1.c2max - b1.c2min) << C2_SHIFT) * C2_SCALE;
      /* We want to break any ties in favor of green, then red, blue last.
       * This code does the right thing for R,G,B color order only.
       */
      int cmax = c1;
      int n = 1;
      if (c0 > cmax) {
        cmax = c0;
        n = 0;
      }
      if (c2 > cmax) {
        n = 2;
      }
      /* Choose split point along selected axis, and update box bounds.
       * Current algorithm: split at halfway point.
       * (Since the box has been shrunk to minimum volume,
       * any split will produce two nonempty subboxes.)
       * Note that lb value is max for lower box, so must be < old max.
       */
      switch (n) {
        case 0:
          lb = (b1.c0max + b1.c0min) / 2;
          b1.c0max = lb;
          b2.c0min = lb + 1;
          break;
        case 1:
          lb = (b1.c1max + b1.c1min) / 2;
          b1.c1max = lb;
          b2.c1min = lb + 1;
          break;
        case 2:
          lb = (b1.c2max + b1.c2min) / 2;
          b1.c2max = lb;
          b2.c2min = lb + 1;
          break;
      }
      /* Update stats for boxes */
      update_box(b1);
      update_box(b2);
      numboxes++;
    }
    return (numboxes);
  }

  private void compute_color(Box box, int icolor) {
    /* Current algorithm: mean weighted by pixels (not colors) */
    /* Note it is important to get the rounding correct! */
    int c0, c1, c2;
    int c0min, c0max, c1min, c1max, c2min, c2max;
    long count;
    long total = 0;
    long c0total = 0;
    long c1total = 0;
    long c2total = 0;
    c0min = box.c0min;
    c0max = box.c0max;
    c1min = box.c1min;
    c1max = box.c1max;
    c2min = box.c2min;
    c2max = box.c2max;
    for (c0 = c0min; c0 <= c0max; c0++) {
      for (c1 = c1min; c1 <= c1max; c1++) {
        for (c2 = c2min; c2 <= c2max; c2++) {
          if ((count = histogram[c0][c1][c2]) != 0) {
            total += count;
            c0total += ((c0 << C0_SHIFT) + ((1 << C0_SHIFT) >> 1)) * count;
            c1total += ((c1 << C1_SHIFT) + ((1 << C1_SHIFT) >> 1)) * count;
            c2total += ((c2 << C2_SHIFT) + ((1 << C2_SHIFT) >> 1)) * count;
          }
        }
      }
    }
    cmap[0][icolor] = (int) ((c0total + (total >> 1)) / total);
    cmap[1][icolor] = (int) ((c1total + (total >> 1)) / total);
    cmap[2][icolor] = (int) ((c2total + (total >> 1)) / total);
  }

  private void select_colors(RGBPalette pPalette, int pDesired) {
    int desired = pDesired;
    Box boxlist[] = new Box[desired];
    for (int i = 0; i < desired; i++) {
      boxlist[i] = new Box();
    }
    /* Initialize one box containing whole space */
    int numboxes = 1;
    boxlist[0].c0min = 0;
    boxlist[0].c0max = 255 >> C0_SHIFT;
    boxlist[0].c1min = 0;
    boxlist[0].c1max = 255 >> C1_SHIFT;
    boxlist[0].c2min = 0;
    boxlist[0].c2max = 255 >> C2_SHIFT;
    /* Shrink it to actually-used volume and set its statistics */
    update_box(boxlist[0]);
    /* Perform median-cut to produce final box list */
    numboxes = median_cut(boxlist, numboxes, desired);
    /* Compute the representative color for each box, fill colormap */
    for (int i = 0; i < numboxes; i++) {
      compute_color(boxlist[i], i);
      int r = cmap[0][i];
      int g = cmap[1][i];
      int b = cmap[2][i];
      pPalette.addColor(Tools.limitColor(r), Tools.limitColor(g), Tools.limitColor(b));
    }
  }

  public RGBPalette createPalette(SimpleImage pImage) {
    RGBPalette res = new RGBPalette();
    prescan_quantize(pImage);
    select_colors(res, 256);
    for (int i = res.getSize(); i < RGBPalette.PALETTE_SIZE; i++) {
      res.addColor(0, 0, 0);
    }
    res.sort();
    return res;
  }
}
