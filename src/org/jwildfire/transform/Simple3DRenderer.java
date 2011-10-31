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
package org.jwildfire.transform;

import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.Mesh3DTransformer.Faces;
import org.jwildfire.transform.Mesh3DTransformer.Light;

public class Simple3DRenderer extends Mesh3DRenderer {

  @Override
  public void renderImage(Mesh3D pMesh3D, Mesh3DTransformer pMesh3DTransformer, SimpleImage pImg) {
    init(pMesh3D, pMesh3DTransformer, pImg);
    viewz = 1.0;
    for (int i = 0; i < f3Count; i++) {
      int p1 = p31[i];
      int p2 = p32[i];
      int p3 = p33[i];
      fZSortArray[i] = 0.0 - (z[p1] + z[p2] + z[p3]) * 0.33;
      fInd[i] = i;
    }
    heapSortFloat1(fZSortArray, fInd, f3Count);
    for (int i = 0; i < f3Count; i++) {
      int p1 = p31[fInd[i]];
      int p2 = p32[fInd[i]];
      int p3 = p33[fInd[i]];

      double x1 = cx + x[p1];
      double y1 = cy + y[p1];
      double z1 = z[p1];
      double x2 = cx + x[p2];
      double y2 = cy + y[p2];
      double z2 = z[p2];
      double x3 = cx + x[p3];
      double y3 = cy + y[p3];
      double z3 = z[p3];
      lux = (int) (x1 + 0.5);
      luy = (int) (y1 + 0.5);
      luz = (int) (z1 + 0.5);
      rux = (int) (x2 + 0.5);
      ruy = (int) (y2 + 0.5);
      ruz = (int) (z2 + 0.5);
      rbx = (int) (x3 + 0.5);
      rby = (int) (y3 + 0.5);
      rbz = (int) (z3 + 0.5);

      double vax = x2 - x1;
      double vay = y2 - y1;
      double vaz = z2 - z1;
      double vbx = x3 - x1;
      double vby = y3 - y1;
      double vbz = z3 - z1;
      nfx = vay * vbz - vaz * vby;
      nfy = vaz * vbx - vax * vbz;
      nfz = vax * vby - vay * vbx;
      rr = Math.sqrt(nfx * nfx + nfy * nfy + nfz * nfz);
      if (Math.abs(rr) <= PZERO) {
        nfx = nfy = 0.0;
        nfz = -1.0;
      }
      else {
        nfx = 0.0 - nfx / rr;
        nfy = 0.0 - nfy / rr;
        nfz = 0.0 - nfz / rr;
      }
      if (faces == Faces.MIRRORED) {
        nfx = 0.0 - nfx;
        nfy = 0.0 - nfy;
        nfz = 0.0 - nfz;
      }

      reflectViewVector();
      cosa = 0.0 - nfz;
      if ((faces == Faces.DOUBLE) && (cosa >= 0.0)) {
        nfx = 0.0 - nfx;
        nfy = 0.0 - nfy;
        nfz = 0.0 - nfz;
        reflectViewVector();
        cosa = 0.0 - nfz;
      }
      if (cosa < 0.0) {
        if (u != null) {
          int px = (int) (u[p1] * texture.getImageWidth() + 0.5);
          int py = (int) (v[p1] * texture.getImageHeight() + 0.5);
          toolPixel.setARGBValue(texture.getARGBValueIgnoreBounds(px, py));
        }
        else {
          toolPixel.setARGBValue(coloro[fInd[i]]);
        }
        r = toolPixel.r;
        g = toolPixel.g;
        b = toolPixel.b;
        /* fill the transformed rectangle */
        /* create the edges */
        /* 1->2 */
        if (p1 < p2) {
          n12 = bresenham3D(lux, luy, luz, rux, ruy, ruz, x12, y12, z12);
        }
        else {
          n12 = bresenham3D(rux, ruy, ruz, lux, luy, luz, x12, y12, z12);
        }
        /* 2->3 */
        if (p2 < p3) {
          n23 = bresenham3D(rux, ruy, ruz, rbx, rby, rbz, x23, y23, z23);
        }
        else {
          n23 = bresenham3D(rbx, rby, rbz, rux, ruy, ruz, x23, y23, z23);
        }
        /* 3->1 */
        if (p3 < p1) {
          n41 = bresenham3D(rbx, rby, rbz, lux, luy, luz, x41, y41, z41);
        }
        else {
          n41 = bresenham3D(lux, luy, luz, rbx, rby, rbz, x41, y41, z41);
        }
        /* create the bounding box */
        int xmin, xmax;
        xmin = xmax = lux;
        if (rux < xmin)
          xmin = rux;
        else if (rux > xmax)
          xmax = rux;
        if (rbx < xmin)
          xmin = rbx;
        else if (rbx > xmax)
          xmax = rbx;
        int ymin, ymax;
        ymin = ymax = luy;
        if (ruy < ymin)
          ymin = ruy;
        else if (ruy > ymax)
          ymax = ruy;
        if (rby < ymin)
          ymin = rby;
        else if (rby > ymax)
          ymax = rby;

        /* fill the area */
        for (k = ymin; k <= ymax; k++) {
          if ((k >= 0) && (k < height)) {
            rFill3();
            double zs;
            int zz = zMin;
            if (min != max)
              zs = (double) (zMax - zMin) / (double) (max - min);
            else
              zs = 0.0;
            for (int l = min; l <= max; l++) {
              if ((l >= 0) && (l < width)) {
                if (doLight != Light.OFF) {
                  addLight((double) l, (double) k, zz);
                }
                else {
                  pr = r;
                  pg = g;
                  pb = b;
                }
                pImg.setRGB(l, k, pr, pg, pb);
              }
              zz += zs;
            }
          }
        } /* k loop */
      } /* cosa */
    }/* loop */
  }

}
