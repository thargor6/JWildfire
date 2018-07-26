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
package org.jwildfire.image;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;

public class FastHDRTonemapper {

  public SimpleImage renderImage(SimpleHDRImage pHDRImg) {
    SimpleImage res = new SimpleImage(pHDRImg.getImageWidth(), pHDRImg.getImageHeight());
    float minLum, maxLum;
    {
      float lum[] = new float[2];
      pHDRImg.getMinMaxLum(lum);
      minLum = lum[0];
      maxLum = lum[1];
    }
    double lumRange = maxLum - minLum;
    float rgb[] = new float[3];
    if (lumRange > MathLib.EPSILON) {
      for (int i = 0; i < pHDRImg.getImageHeight(); i++) {
        for (int j = 0; j < pHDRImg.getImageWidth(); j++) {
          pHDRImg.getRGBValues(rgb, j, i);
          float lum = pHDRImg.getLum(j, i);
          double normedLum = ((lum - minLum) / lumRange);
          double transformedLum = 1.0 - Math.exp(-2.2 * normedLum);
          float maxComp = rgb[0];
          if (rgb[1] > maxComp) {
            maxComp = rgb[1];
          }
          if (rgb[2] > maxComp) {
            maxComp = rgb[2];
          }
          int r = Tools.roundColor(rgb[0] / maxComp * transformedLum * 256.0);
          int g = Tools.roundColor(rgb[1] / maxComp * transformedLum * 256.0);
          int b = Tools.roundColor(rgb[2] / maxComp * transformedLum * 256.0);
          res.setRGB(j, i, r, g, b);
          //          int nLum = Tools.roundColor(transformedLum * 256.0);
          //          res.setRGB(j, i, nLum, nLum, nLum);
        }
      }
    }
    return res;
  }
}
