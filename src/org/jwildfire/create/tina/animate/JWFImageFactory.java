/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
package org.jwildfire.create.tina.animate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import org.jwildfire.image.SimpleImage;

import com.flagstone.transform.image.DefineImage;
import com.flagstone.transform.image.ImageTag;
import com.flagstone.transform.util.image.ImageDecoder;
import com.flagstone.transform.util.image.ImageFactory;
import com.flagstone.transform.util.image.ImageProvider;

// This class enhances the standard ImageFactory by a feature to accept an image which is already in memory.
// (This was at least necessary because both the PNG anf JPG reader seam to crash in some cases.)  
public class JWFImageFactory extends ImageFactory {

  public void read(SimpleImage pImage) {
    decoder = new SimpleImageDecoder(pImage);
  }

  public static class SimpleImageDecoder implements ImageProvider, ImageDecoder {
    private final SimpleImage img;

    public SimpleImageDecoder(SimpleImage pImage) {
      img = pImage;
    }

    @Override
    public void read(File file) throws IOException, DataFormatException {
      throw new RuntimeException("Not implemented");
    }

    @Override
    public void read(URL url) throws IOException, DataFormatException {
      throw new RuntimeException("Not implemented");
    }

    @Override
    public void read(InputStream stream) throws IOException, DataFormatException {
      throw new RuntimeException("Not implemented");
    }

    @Override
    public int getWidth() {
      return img.getImageWidth();
    }

    @Override
    public int getHeight() {
      return img.getImageHeight();
    }

    @Override
    public byte[] getImage() {
      int size = img.getImageWidth() * img.getImageHeight();
      byte res[] = new byte[size * 4];
      int idx = 0;
      for (int i = 0; i < img.getImageHeight(); i++) {
        for (int j = 0; j < img.getImageWidth(); j++) {
          int rgb = img.getARGBValue(j, i);
          //        byte a = (byte) ((val >> 24) & 0xff);
          byte r = (byte) ((rgb >> 16) & 0xff);
          byte g = (byte) ((rgb >> 8) & 0xff);
          byte b = (byte) ((rgb) & 0xff);
          res[idx++] = (byte) 255;
          res[idx++] = b;
          res[idx++] = g;
          res[idx++] = r;
        }
      }
      return res;
    }

    @Override
    public ImageTag defineImage(int identifier) {
      int RGB8_SIZE = 24;
      ImageTag object = new DefineImage(identifier, img.getImageWidth(), img.getImageHeight(), zip(getImage()),
          RGB8_SIZE);
      return object;
    }

    @Override
    public ImageDecoder newDecoder() {
      return new SimpleImageDecoder(img);
    }

    private byte[] zip(final byte[] img) {
      final Deflater deflater = new Deflater();
      deflater.setInput(img);
      deflater.finish();
      final byte[] compressedData = new byte[img.length * 2];
      final int bytesCompressed = deflater.deflate(compressedData);
      final byte[] newData = Arrays.copyOf(compressedData, bytesCompressed);
      return newData;
    }
  }

}
