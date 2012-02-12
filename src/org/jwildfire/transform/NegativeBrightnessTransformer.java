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

public class NegativeBrightnessTransformer extends PixelTransformer {
  private final int rs = (3333 * Tools.VPREC) / 10000;
  private final int gs = (3334 * Tools.VPREC) / 10000;
  private final int bs = (3333 * Tools.VPREC) / 10000;

  @Override
  public void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    int lval = (rs * pPixel.r + gs * pPixel.g + bs * pPixel.b) >> Tools.SPREC;
    if (lval > 255)
      lval = 255;
    double scl = (double) ((double) (255 - lval) / (double) 255.0);
    pPixel.r = (int) ((double) pPixel.r * scl + 0.5);
    pPixel.g = (int) ((double) pPixel.g * scl + 0.5);
    pPixel.b = (int) ((double) pPixel.b * scl + 0.5);
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    // empty    
  }

}
