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

import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;

public class NegativeTransformer extends PixelTransformer {

  @Override
  public void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth, int pImageHeight) {
    pPixel.r = 255 - pPixel.r;
    pPixel.g = 255 - pPixel.g;
    pPixel.b = 255 - pPixel.b;
  }

  @Override
  public void initDefaultParams(SimpleImage pImg) {
    // empty    
  }

}
