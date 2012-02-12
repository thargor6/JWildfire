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

import org.jwildfire.base.Tools;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.WFImage;

public class AntiqueTransformer extends PixelTransformer {
  private final double BR_MAX = 51.0;

  @Override
  public void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    /* colorToGray */
    int rs = 2990; // luminance weights
    int gs = 5880;
    int bs = 1130;

    rs = (rs * Tools.VPREC) / 10000;
    gs = (gs * Tools.VPREC) / 10000;
    bs = (bs * Tools.VPREC) / 10000;

    int lval = (rs * pPixel.r + gs * pPixel.g + bs * pPixel.b) >> Tools.SPREC;
    if (lval > 255)
      lval = 255;
    pPixel.r = pPixel.g = pPixel.b = lval;
    /* green and blue contrast */
    int mgcontrast = -3;
    int mbcontrast = -6;
    int mgreen = -6;
    int mblue = -12;

    double scale = (double) mgcontrast / BR_MAX;
    int gSc = (int) (scale * (double) Tools.VPREC + 0.5);
    scale = (double) mbcontrast / BR_MAX;
    int bSc = (int) (scale * (double) Tools.VPREC + 0.5);

    int dc = ((pPixel.g - 127) * gSc) >> Tools.SPREC;
    if (dc < (-255))
      dc = -255;
    else if (dc > 255)
      dc = 255;
    int val = pPixel.g + dc;
    if (pPixel.g < 127) {
      if (val > 127)
        val = 127;
    }
    else {
      if (val < 127)
        val = 127;
    }
    pPixel.g = val;

    dc = ((pPixel.b - 127) * bSc) >> Tools.SPREC;
    if (dc < (-255))
      dc = -255;
    else if (dc > 255)
      dc = 255;
    val = pPixel.b + dc;
    if (pPixel.b < 127) {
      if (val > 127)
        val = 127;
    }
    else {
      if (val < 127)
        val = 127;
    }
    pPixel.b = val;

    /* green and blue brightness */
    int gtt = (int) ((double) mgreen / (double) BR_MAX * (double) 255.0 + 0.5);
    int btt = (int) ((double) mblue / (double) BR_MAX * (double) 255.0 + 0.5);

    int wval = pPixel.g + gtt;
    if (wval < 0)
      wval = 0;
    else if (wval > 255)
      wval = 255;
    pPixel.g = wval;

    wval = pPixel.b + btt;
    if (wval < 0)
      wval = 0;
    else if (wval > 255)
      wval = 255;
    pPixel.b = wval;

  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    // empty    
  }

}
