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

import java.util.HashMap;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyCategory;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.WFImage;

public class CubeTransformer extends Mesh3DTransformer {
  @Property(category = PropertyCategory.PRIMARY, description = "Size of the cube")
  @PropertyMin(1.0)
  private double size = 120.0;

  @Override
  protected void transformMesh(Mesh3D pMesh3D, int pImageWidth, int pImageHeight) {
    int pCount = pMesh3D.getPCount();
    double x[] = pMesh3D.getX();
    double y[] = pMesh3D.getY();
    double z[] = pMesh3D.getZ();
    double size = this.size;
    if (size < 1.0)
      size = 1.0;

    double xmin = x[0], xmax = xmin;
    double ymin = y[0], ymax = ymin;
    for (int i = 0; i < pCount; i++) {
      if (x[i] < xmin)
        xmin = x[i];
      else if (x[i] > xmax)
        xmax = x[i];
      if (y[i] < ymin)
        ymin = y[i];
      else if (y[i] > ymax)
        ymax = y[i];
    }
    double xsize = xmax - xmin;
    double ysize = ymax - ymin;

    HashMap<Integer, Integer> pointDestination = new HashMap<Integer, Integer>();

    // 1 | 2 | 3
    // ---------
    // 4 | 5 | 6    
    //    / ---
    //   / 5 / |   behind: 3 
    //  ----/  |   left: 4
    // | 1  | 2/   bottom: 6
    // |    | /
    //  ----
    if (xsize > ysize) {
      double areaWidth = xsize / 3.0;
      double areaHeight = ysize / 2.0;

      double xScale = size / areaWidth;
      double yScale = size / areaHeight;

      double x0 = xmin;
      double x1 = x0 + areaWidth;
      double x2 = x1 + areaWidth;
      //double x3 = xmax;

      double y0 = ymin;
      double y1 = y0 + areaHeight;
      //double y2 = ymax;

      double xTrans1 = areaWidth;
      double yTrans1 = areaHeight / 2.0;
      double zTrans1 = -size / 2.0;

      double xTrans2 = 0;
      double yTrans2 = areaHeight / 2.0;
      double zTrans2 = -size / 2.0;

      double xTrans3 = -areaWidth;
      double yTrans3 = areaHeight / 2.0;
      double zTrans3 = -size / 2.0;

      double xTrans4 = areaWidth;
      double yTrans4 = -areaHeight / 2.0;
      double zTrans4 = -size / 2.0;

      double xTrans5 = 0;
      double yTrans5 = -areaHeight / 2.0;
      double zTrans5 = -size / 2.0;

      double xTrans6 = -areaWidth;
      double yTrans6 = -areaHeight / 2.0;
      double zTrans6 = -size / 2.0;

      for (int i = 0; i < pCount; i++) {
        double xx = x[i];
        double yy = y[i];
        double zz = z[i];
        if (xx <= x1) {
          // 1
          if (yy <= y1) {
            xx = (xx + xTrans1) * xScale;
            yy = (yy + yTrans1) * yScale;
            zz = zz + zTrans1;
            x[i] = xx;
            y[i] = yy;
            z[i] = zz;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(1));
          }
          // 4
          else {
            xx = (xx + xTrans4) * xScale;
            yy = (yy + yTrans4) * yScale;
            zz = zz + zTrans4;
            x[i] = zz;
            y[i] = yy;
            z[i] = -xx;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(4));
          }
        }
        else if ((xx > x1) && (xx <= x2)) {
          // 2
          if (yy <= y1) {
            xx = (xx + xTrans2) * xScale;
            yy = (yy + yTrans2) * yScale;
            zz = zz + zTrans2;
            x[i] = -zz;
            y[i] = yy;
            z[i] = xx;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(2));
          }
          // 5
          else {
            xx = (xx + xTrans5) * xScale;
            yy = (yy + yTrans5) * yScale;
            zz = zz + zTrans5;
            x[i] = xx;
            y[i] = zz;
            z[i] = -yy;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(5));
          }
        }
        else /*if (xx > x2) */{
          // 3
          if (yy <= y1) {
            xx = (xx + xTrans3) * xScale;
            yy = (yy + yTrans3) * yScale;
            zz = zz + zTrans3;
            x[i] = -xx;
            y[i] = yy;
            z[i] = -zz;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(3));
          }
          // 6
          else {
            xx = (xx + xTrans6) * xScale;
            yy = (yy + yTrans6) * yScale;
            zz = zz + zTrans6;
            x[i] = xx;
            y[i] = -zz;
            z[i] = yy;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(6));
          }
        }
      }
    }
    // 1 | 2 
    // -----
    // 3 | 4
    // -----
    // 5 | 6  
    //    / ---
    //   / 5 / |   behind: 3 
    //  ----/  |   left: 6
    // | 1  | 2/   bottom: 4
    // |    | /
    //  ----    
    else {
      double areaWidth = xsize / 2.0;
      double areaHeight = ysize / 3.0;

      double xScale = size / areaWidth;
      double yScale = size / areaHeight;

      double x0 = xmin;
      double x1 = x0 + areaWidth;
      //double x2 = xmax;

      double y0 = ymin;
      double y1 = y0 + areaHeight;
      double y2 = y1 + areaHeight;
      // double y3 = ymax;

      double xTrans1 = areaWidth / 2.0;
      double yTrans1 = areaHeight;
      double zTrans1 = -size / 2.0;

      double xTrans2 = -areaWidth / 2.0;
      double yTrans2 = areaHeight;
      double zTrans2 = -size / 2.0;

      double xTrans3 = areaWidth / 2.0;
      double yTrans3 = 0.0;
      double zTrans3 = -size / 2.0;

      double xTrans4 = -areaWidth / 2.0;
      double yTrans4 = 0.0;
      double zTrans4 = -size / 2.0;

      double xTrans5 = areaWidth / 2.0;
      double yTrans5 = -areaHeight;
      double zTrans5 = -size / 2.0;

      double xTrans6 = -areaWidth / 2.0;
      double yTrans6 = -areaHeight;
      double zTrans6 = -size / 2.0;

      for (int i = 0; i < pCount; i++) {
        double xx = x[i];
        double yy = y[i];
        double zz = z[i];
        if (xx <= x1) {
          // 1
          if (yy <= y1) {
            xx = (xx + xTrans1) * xScale;
            yy = (yy + yTrans1) * yScale;
            zz = zz + zTrans1;
            x[i] = xx;
            y[i] = yy;
            z[i] = zz;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(1));
          }
          // 3
          else if ((yy > y1) && (yy <= y2)) {
            xx = (xx + xTrans3) * xScale;
            yy = (yy + yTrans3) * yScale;
            zz = zz + zTrans3;
            x[i] = -xx;
            y[i] = yy;
            z[i] = -zz;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(3));
          }
          // 5
          else /*if (yy > y2)*/{
            xx = (xx + xTrans5) * xScale;
            yy = (yy + yTrans5) * yScale;
            zz = zz + zTrans5;
            x[i] = xx;
            y[i] = zz;
            z[i] = -yy;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(5));
          }
        }
        else /*if(xx>x1)*/{
          // 2
          if (yy <= y1) {
            xx = (xx + xTrans2) * xScale;
            yy = (yy + yTrans2) * yScale;
            zz = zz + zTrans2;
            x[i] = -zz;
            y[i] = yy;
            z[i] = xx;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(2));
          }
          // 4
          else if ((yy > y1) && (yy <= y2)) {
            xx = (xx + xTrans4) * xScale;
            yy = (yy + yTrans4) * yScale;
            zz = zz + zTrans4;
            x[i] = xx;
            y[i] = -zz;
            z[i] = yy;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(4));
          }
          // 6
          else /*if (yy > y2)*/{
            xx = (xx + xTrans6) * xScale;
            yy = (yy + yTrans6) * yScale;
            zz = zz + zTrans6;
            x[i] = zz;
            y[i] = yy;
            z[i] = -xx;
            pointDestination.put(Integer.valueOf(i), Integer.valueOf(6));
          }
        }
      }

    }

    // remove unused/illegal faces
    int p1[] = pMesh3D.getPP1();
    int p2[] = pMesh3D.getPP2();
    int p3[] = pMesh3D.getPP3();
    int color[] = pMesh3D.getColor();
    int fCount = pMesh3D.getFCount();
    int newFCount = 0;
    int newP1[] = new int[fCount];
    int newP2[] = new int[fCount];
    int newP3[] = new int[fCount];
    int newColor[] = color != null ? new int[fCount] : null;
    for (int i = 0; i < fCount; i++) {
      int dest1 = pointDestination.get(Integer.valueOf(p1[i]));
      int dest2 = pointDestination.get(Integer.valueOf(p2[i]));
      if (dest1 != dest2)
        continue;
      int dest3 = pointDestination.get(Integer.valueOf(p3[i]));
      if (dest2 != dest3)
        continue;
      newP1[newFCount] = p1[i];
      newP2[newFCount] = p2[i];
      newP3[newFCount] = p3[i];
      if (newColor != null) {
        newColor[newFCount] = color[i];
      }
      newFCount++;
    }
    pMesh3D.setFCount(newFCount);
    pMesh3D.setPP1(newP1);
    pMesh3D.setPP2(newP2);
    pMesh3D.setPP3(newP3);
    pMesh3D.setColor(newColor);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    super.initDefaultParams(pImg);
    int width = pImg.getImageWidth();
    int height = pImg.getImageHeight();
    double rr = Math.sqrt(width * width + height * height);
    size = Math.round(rr / (3.0 * Math.sqrt(2.0)));
    zoom = 1.0;
    ambient = 0.40;
    alpha = 25.0;
    beta = -35.0;
  }

  public double getSize() {
    return size;
  }

  public void setSize(double size) {
    this.size = size;
  }

}
