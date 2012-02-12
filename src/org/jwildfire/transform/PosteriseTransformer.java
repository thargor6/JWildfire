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
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.WFImage;

public class PosteriseTransformer extends PixelTransformer {

  @Property(description = "Number of remaining bits of the color information")
  @PropertyMin(1)
  @PropertyMax(7)
  private int bits = 2;

  private int dd, mm;

  @Override
  protected void initTransformation(WFImage pImg) {
    dd = 256 / (1 << bits);
    if (bits == 1) {
      mm = 255 / ((1 << bits) - 1);
    }
    else {
      mm = 256 / ((1 << bits) - 1);
    }
  }

  @Override
  protected void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    pPixel.r = (pPixel.r / dd) * mm;
    pPixel.g = (pPixel.g / dd) * mm;
    pPixel.b = (pPixel.b / dd) * mm;
  }

  @Override
  public void initDefaultParams(WFImage pImg) {
    bits = 2;
  }

  public int getBits() {
    return bits;
  }

  public void setBits(int bits) {
    this.bits = bits;
  }
}
