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
package org.jwildfire.create.tina.render;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.image.SimpleGrayImage;
import org.jwildfire.image.SimpleHDRImage;
import org.jwildfire.image.SimpleImage;

public class RenderedFlame {
  private SimpleImage image;
  private SimpleHDRImage hdrImage;
  private SimpleGrayImage zBuffer;

  public SimpleImage getImage() {
    return image;
  }

  public SimpleHDRImage getHDRImage() {
    return hdrImage;
  }

  public SimpleGrayImage getZBuffer() {
    return zBuffer;
  }

  public void init(RenderInfo pRenderInfo, Flame pFlame) {
    if (pRenderInfo.isRenderImage()) {
      image = new SimpleImage(pRenderInfo.getImageWidth(), pRenderInfo.getImageHeight());
    }
    if (pRenderInfo.isRenderHDR()) {
      hdrImage = new SimpleHDRImage(pRenderInfo.getImageWidth(), pRenderInfo.getImageHeight());
    }
    if (pRenderInfo.isRenderZBuffer() && pFlame.getSolidRenderSettings().isSolidRenderingEnabled()) {
      zBuffer = new SimpleGrayImage(pRenderInfo.getImageWidth(), pRenderInfo.getImageHeight());
    }
  }

}
