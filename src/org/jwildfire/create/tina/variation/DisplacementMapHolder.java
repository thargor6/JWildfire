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
package org.jwildfire.create.tina.variation;

import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DisplacementMapHolder implements Serializable {
  private double displ_amount = 0.1;
  private WFImage displacementMap;
  private int displacementMapWidth, displacementMapHeight;
  private String displ_map_filename = null;
  private int blend_displ_map = 1;

  private Pixel toolPixel = new Pixel();
  private float[] rgbArray = new float[3];

  public String getDispl_map_filename() {
    return displ_map_filename;
  }

  public void setDispl_map_filename(String displ_map_filename) {
    this.displ_map_filename = displ_map_filename;
  }

  public int getBlend_displ_map() {
    return blend_displ_map;
  }

  public void setBlend_displ_map(int blend_displ_map) {
    this.blend_displ_map = blend_displ_map;
  }

  public int getDisplacementMapWidth() {
    return displacementMapWidth;
  }

  public int getDisplacementMapHeight() {
    return displacementMapHeight;
  }

  public void init() {
    displacementMap = null;
    if (displ_map_filename != null && displ_map_filename.length() > 0) {
      try {
        displacementMap = RessourceManager.getImage(displ_map_filename);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (displacementMap != null) {
      displacementMapWidth = displacementMap.getImageWidth();
      displacementMapHeight = displacementMap.getImageHeight();
    } else {
      displacementMapWidth = displacementMapHeight = 0;
    }
  }

  public double getDispl_amount() {
    return displ_amount;
  }

  public void setDispl_amount(double displ_amount) {
    this.displ_amount = displ_amount;
  }

  public boolean isActive() {
    return displacementMap != null && MathLib.fabs(displ_amount) > MathLib.EPSILON;
  }

  public double calculateImageDisplacement(int ix, int iy, double iu, double iv) {
    if (ix >= 0 && ix < displacementMapWidth && iy >= 0 && iy < displacementMapHeight) {
      if (displacementMap instanceof SimpleImage) {
        if (blend_displ_map > 0) {
          int ix1 = ix + 1;
          if (ix1 >= displacementMapWidth)
            ix1 = 0;
          int iy1 = iy + 1;
          if (iy1 >= displacementMapHeight)
            iy1 = 0;
          double iufrac = MathLib.frac(iu);
          double ivfrac = MathLib.frac(iv);
          toolPixel.setARGBValue(((SimpleImage) displacementMap).getARGBValue(ix, iy));
          int lug = toolPixel.g;
          toolPixel.setARGBValue(((SimpleImage) displacementMap).getARGBValue(ix1, iy));
          int rug = toolPixel.g;
          toolPixel.setARGBValue(((SimpleImage) displacementMap).getARGBValue(ix, iy1));
          int lbg = toolPixel.g;
          toolPixel.setARGBValue(((SimpleImage) displacementMap).getARGBValue(ix1, iy1));
          int rbg = toolPixel.g;
          return GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac) / 255.0;
        } else {
          toolPixel.setARGBValue(((SimpleImage) displacementMap).getARGBValue(ix, iy));
          return toolPixel.g / 255.0;
        }
      } else {
        if (blend_displ_map > 0) {
          int ix1 = ix + 1;
          if (ix1 >= displacementMapWidth)
            ix1 = 0;
          int iy1 = iy + 1;
          if (iy1 >= displacementMapHeight)
            iy1 = 0;
          double iufrac = MathLib.frac(iu);
          double ivfrac = MathLib.frac(iv);
          ((SimpleHDRImage) displacementMap).getRGBValues(rgbArray, ix, iy);
          float lug = rgbArray[1];
          ((SimpleHDRImage) displacementMap).getRGBValues(rgbArray, ix1, iy);
          float rug = rgbArray[1];
          ((SimpleHDRImage) displacementMap).getRGBValues(rgbArray, ix, iy1);
          float lbg = rgbArray[1];
          ((SimpleHDRImage) displacementMap).getRGBValues(rgbArray, ix1, iy1);
          float rbg = rgbArray[1];
          return GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
        } else {
          ((SimpleHDRImage) displacementMap).getRGBValues(rgbArray, ix, iy);
          return rgbArray[1];
        }
      }
    }
    return 0.0;
  }

  public void clear() {
    displacementMap = null;
  }

}
