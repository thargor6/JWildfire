/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.render.image;

import org.jwildfire.image.SimpleImage;

public class PostFilterImageThread extends AbstractImageRenderThread {
  private final int startRow, endRow;
  private final SimpleImage input, output;

  public PostFilterImageThread(int pStartRow, int pEndRow, SimpleImage pInput, SimpleImage pOutput) {
    input = pInput;
    output = pOutput;
    startRow = pStartRow;
    endRow = pEndRow;
  }

  @Override
  public void run() {
    setDone(false);
    try {
      for (int i = startRow; i < endRow; i++) {
        for (int j = 0; j < input.getImageWidth(); j++) {
          // TODO
        }
      }
    }
    finally {
      setDone(true);
    }
  }

}
