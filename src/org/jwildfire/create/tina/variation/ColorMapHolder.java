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
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ColorMapHolder implements Serializable {
  private WFImage colorMap;
  private int colorMapWidth, colorMapHeight;
  private String colormap_filename = null;
  private int blend_colormap = 1;
  private Pixel toolPixel = new Pixel();
  private float[] rgbArray = new float[3];

  public String getColormap_filename() {
    return colormap_filename;
  }

  public void setColormap_filename(String colormap_filename) {
    this.colormap_filename = colormap_filename;
  }

  public int getBlend_colormap() {
    return blend_colormap;
  }

  public void setBlend_colormap(int blend_colormap) {
    this.blend_colormap = blend_colormap;
  }

  public void init() {
    colorMap = null;
    if (colormap_filename != null && colormap_filename.length() > 0) {
      try {
        colorMap = RessourceManager.getImage(colormap_filename);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (colorMap != null) {
      colorMapWidth = colorMap.getImageWidth();
      colorMapHeight = colorMap.getImageHeight();
    } else {
      colorMapWidth = colorMapHeight = 0;
    }
  }

  public void applyImageColor(XYZPoint pVarTP, int ix, int iy, double iu, double iv) {
    if (ix >= 0 && ix < colorMapWidth && iy >= 0 && iy < colorMapHeight) {
      if (colorMap instanceof SimpleImage) {
        if (blend_colormap > 0) {
          int ix1 = ix + 1;
          if (ix1 >= colorMapWidth)
            ix1 = 0;
          int iy1 = iy + 1;
          if (iy1 >= colorMapHeight)
            iy1 = 0;
          double iufrac = MathLib.frac(iu);
          double ivfrac = MathLib.frac(iv);
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(ix, iy));
          int lur = toolPixel.r;
          int lug = toolPixel.g;
          int lub = toolPixel.b;
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(ix1, iy));
          int rur = toolPixel.r;
          int rug = toolPixel.g;
          int rub = toolPixel.b;
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(ix, iy1));
          int lbr = toolPixel.r;
          int lbg = toolPixel.g;
          int lbb = toolPixel.b;
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(ix1, iy1));
          int rbr = toolPixel.r;
          int rbg = toolPixel.g;
          int rbb = toolPixel.b;
          pVarTP.rgbColor = true;
          pVarTP.redColor = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
          pVarTP.greenColor = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
          pVarTP.blueColor = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
        } else {
          toolPixel.setARGBValue(((SimpleImage) colorMap).getARGBValue(ix, iy));
          pVarTP.rgbColor = true;
          pVarTP.redColor = toolPixel.r;
          pVarTP.greenColor = toolPixel.g;
          pVarTP.blueColor = toolPixel.b;
        }
      } else {
        if (blend_colormap > 0) {
          int ix1 = ix + 1;
          if (ix1 >= colorMapWidth)
            ix1 = 0;
          int iy1 = iy + 1;
          if (iy1 >= colorMapHeight)
            iy1 = 0;
          double iufrac = MathLib.frac(iu);
          double ivfrac = MathLib.frac(iv);
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
          float lur = rgbArray[0] * 255;
          float lug = rgbArray[1] * 255;
          float lub = rgbArray[2] * 255;
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix1, iy);
          float rur = rgbArray[0] * 255;
          float rug = rgbArray[1] * 255;
          float rub = rgbArray[2] * 255;
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy1);
          float lbr = rgbArray[0] * 255;
          float lbg = rgbArray[1] * 255;
          float lbb = rgbArray[2] * 255;
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix1, iy1);
          float rbr = rgbArray[0] * 255;
          float rbg = rgbArray[1] * 255;
          float rbb = rgbArray[2] * 255;
          pVarTP.rgbColor = true;
          pVarTP.redColor = GfxMathLib.blerp(lur, rur, lbr, rbr, iufrac, ivfrac);
          pVarTP.greenColor = GfxMathLib.blerp(lug, rug, lbg, rbg, iufrac, ivfrac);
          pVarTP.blueColor = GfxMathLib.blerp(lub, rub, lbb, rbb, iufrac, ivfrac);
        } else {
          ((SimpleHDRImage) colorMap).getRGBValues(rgbArray, ix, iy);
          pVarTP.rgbColor = true;
          pVarTP.redColor = rgbArray[0] * 255;
          pVarTP.greenColor = rgbArray[1] * 255;
          pVarTP.blueColor = rgbArray[2] * 255;
        }
      }
    } else {
      pVarTP.rgbColor = true;
      pVarTP.redColor = 0;
      pVarTP.greenColor = 0;
      pVarTP.blueColor = 0;
    }
  }

  public int getColorMapWidth() {
    return colorMapWidth;
  }

  public int getColorMapHeight() {
    return colorMapHeight;
  }

  public boolean isActive() {
    return (colorMap != null && colormap_filename.length() > 0);
  }

  public void clear() {
    colorMap = null;
  }
}
