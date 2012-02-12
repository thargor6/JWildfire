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

import java.awt.image.BufferedImage;

import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.Buffer.BufferType;

public abstract class PixelTransformer extends Transformer {

  protected abstract void transformPixel(Pixel pPixel, int pX, int pY, int pImageWidth,
      int pImageHeight);

  @Override
  public boolean acceptsInputBufferType(BufferType pBufferType) {
    return pBufferType == BufferType.IMAGE;
  }

  protected void performImageTransformation(WFImage pImg) {
    SimpleImage img = (SimpleImage) pImg;
    final int BLOCK_SIZE = 64;
    final int imageWidth = pImg.getImageWidth();
    final int imageHeight = pImg.getImageHeight();
    final BufferedImage bufferedImg = img.getBufferedImg();
    final int[] pixels = new int[imageWidth * BLOCK_SIZE];
    final Pixel p = new Pixel();
    for (int b = 0; b < imageHeight; b += BLOCK_SIZE) {
      int blockSize = BLOCK_SIZE;
      int currHeight = b + BLOCK_SIZE;
      if (currHeight > imageHeight) {
        blockSize -= (currHeight - imageHeight);
      }
      bufferedImg.getRGB(0, b, imageWidth, blockSize, pixels, 0, imageWidth);
      for (int y = 0; y < blockSize; y++) {
        for (int x = 0; x < imageWidth; x++) {
          p.setARGBValue(pixels[y * imageWidth + x]);
          transformPixel(p, x, y + b, imageWidth, imageHeight);
          pixels[y * imageWidth + x] = p.getARGBValue();
        }
      }
      bufferedImg.setRGB(0, b, imageWidth, blockSize, pixels, 0, imageWidth);
    }
  }

  @Override
  public boolean supports3DOutput() {
    return false;
  }

}
