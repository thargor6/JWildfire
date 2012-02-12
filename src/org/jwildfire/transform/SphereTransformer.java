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

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.WFImage;

public class SphereTransformer extends Mesh3DTransformer {

  @Property(description = "Size of the sphere")
  @PropertyMin(0.0)
  private double radius = 120.0;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int pCount = pMesh3D.getPCount();
    int width = pImageWidth;
    int height = pImageHeight;

    double twoPi = 2.0 * Math.PI;
    double mPI2 = 0.0 - Math.PI / 2.0;
    double pPI2 = Math.PI / 2.0;
    double dx = (double) width;
    double dy = (double) height;
    double radius0;
    if (width > height)
      radius0 = dy / twoPi;
    else
      radius0 = dx / twoPi;
    int[] illPnt = new int[pCount];
    double radius = this.radius;
    if (radius < 0.5)
      radius = 0.5;
    double scale = radius / radius0;
    double rmax = radius * Math.PI;
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();
    for (int i = 0; i < pCount; i++) {
      x[i] *= scale;
      y[i] *= scale;
      z[i] *= scale;
    }
    for (int i = 0; i < pCount; i++) {
      dx = x[i];
      dy = y[i];
      double zz = z[i];
      double dr = Math.sqrt(dx * dx + dy * dy);
      if (dr <= rmax) {
        /* inverse rotation */
        double angle2;
        if (dx != 0.0) {
          angle2 = Math.atan(dy / dx);
        }
        else {
          if (dy < 0.0)
            angle2 = mPI2;
          else
            angle2 = pPI2;
        }
        /* wrapping */
        double angle = dr / radius;
        double sa = Math.sin(angle);
        double ca = Math.cos(angle);
        double rx;
        if (dx < 0)
          rx = 0.0 - (radius - zz) * sa;
        else
          rx = (radius - zz) * sa;
        z[i] = 0.0 - (radius - zz) * ca;
        /* rotation */
        x[i] = rx * Math.cos(angle2);
        y[i] = rx * Math.sin(angle2);
      }
      else {
        illPnt[i] = 1;
      }
    }
    // Fix points
    int fc = pMesh3D.getFCount();
    if (fc > 0) {
      int fInd[] = new int[fc];
      int p1[] = pMesh3D.getPP1();
      int p2[] = pMesh3D.getPP2();
      int p3[] = pMesh3D.getPP3();
      int color[] = pMesh3D.getColor();
      int curr = 0;
      for (int i = 0; i < fc; i++) {
        if ((illPnt[p1[i]] != 0) || (illPnt[p2[i]] != 0) || (illPnt[p3[i]] != 0)) {
          // currently nothing to do
        }
        else {
          fInd[curr++] = i;
        }
      }
      fc = curr;
      for (int i = 0; i < curr; i++) {
        int j = fInd[i];
        if (i != j) {
          if (color != null) {
            color[i] = color[j];
          }
          p1[i] = p1[j];
          p2[i] = p2[j];
          p3[i] = p3[j];
        }
      }
      pMesh3D.setReducedFCount(fc);
    }
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    radius = rr / 15.0;
    zoom = 2.0;
    ambient = 0.40;
    alpha = 15.0;
    beta = -35.0;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }
}
