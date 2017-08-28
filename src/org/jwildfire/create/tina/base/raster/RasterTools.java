/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
package org.jwildfire.create.tina.base.raster;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.jwildfire.base.Tools;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;

public class RasterTools {
  private final static double RANGE = 10000.0;

  private static boolean inRange(double value) {
    return value > -RANGE && value < RANGE;
  }

  public static void saveFloatBuffer(float buf[][], String filename) {
    double min, max;
    min = max = buf[0][0];
    for (int i = 0; i < buf.length; i++) {
      for (int j = 0; j < buf[0].length; j++) {
        if (inRange(buf[i][j])) {
          min = max = buf[i][j];
          break;
        }
      }
    }

    for (int i = 0; i < buf.length; i++) {
      for (int j = 0; j < buf[0].length; j++) {
        if (inRange(buf[i][j])) {
          if (buf[i][j] < min)
            min = buf[i][j];
          else if (buf[i][j] > max && !Double.isInfinite(buf[i][j]))
            max = buf[i][j];
        }
      }
    }
    double d = max - min;
    System.out.println("Saving float buffer \"" + new File(filename).getName() + "\", min = " + min + ", max = " + max);
    SimpleImage img = new SimpleImage(buf.length, buf[0].length);
    for (int i = 0; i < buf.length; i++) {
      for (int j = 0; j < buf[0].length; j++) {
        int a = Tools.roundColor((buf[i][j] - min) / d * 255.0);
        img.setARGB(i, j, 255, a, a, a);
      }
    }
    try {
      new ImageWriter().saveAsPNG(img, filename);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void saveZBufImg(float zbuf[][], String filename) {
    double zmin = 0.0, zmax = 0.0;
    for (int i = 0; i < zbuf.length; i++) {
      for (int j = 0; j < zbuf[0].length; j++) {
        if (zbuf[i][j] != NormalsCalculator.ZBUF_ZMIN) {
          zmin = zmax = zbuf[i][j];
          break;
        }
      }
    }

    for (int i = 0; i < zbuf.length; i++) {
      for (int j = 0; j < zbuf[0].length; j++) {
        if (zbuf[i][j] != NormalsCalculator.ZBUF_ZMIN) {
          if (zbuf[i][j] < zmin)
            zmin = zbuf[i][j];
          else if (zbuf[i][j] > zmax)
            zmax = zbuf[i][j];
        }
      }
    }
    double dz = zmax - zmin;
    SimpleImage img = new SimpleImage(zbuf.length, zbuf[0].length);
    for (int i = 0; i < zbuf.length; i++) {
      for (int j = 0; j < zbuf[0].length; j++) {
        if (zbuf[i][j] != NormalsCalculator.ZBUF_ZMIN) {
          int a = Tools.roundColor((zbuf[i][j] - zmin) / dz * 255.0);
          img.setARGB(i, j, 255, a, a, a);
        }
        else {
          img.setARGB(i, j, 255, 0, 0, 0);
        }
      }
    }
    try {
      new ImageWriter().saveAsPNG(img, filename);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static float[][] medianCut(float[][] buf) {
    final int windowSize = 1;
    final ArrayList<Float> values = new ArrayList<>();
    final int width = buf.length;
    final int height = buf[0].length;
    float[][] res = new float[width][height];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        values.clear();
        for (int k = -windowSize; k <= windowSize; k++) {
          int x = i + k;
          for (int l = -windowSize; l <= windowSize; l++) {
            int y = j + l;
            values.add(x >= 0 && x < width && y >= 0 && y < height ? buf[x][y] : 0.0f);
          }
        }
        Collections.sort(values);
        res[i][j] = values.get(values.size() / 2);
      }
    }
    return res;
  }

}
