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
package org.jwildfire.create.tina.swing;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;
import org.jwildfire.transform.ScaleAspect;
import org.jwildfire.transform.ScaleTransformer;

public class ImageThumbnail {
  public static final int IMG_WIDTH = 90;
  public static final int IMG_HEIGHT = 68;
  public static final int BORDER_SIZE = 8;

  private final String filename;
  private SimpleImage preview;
  private ImagePanel imgPanel;
  private final ThumbnailCacheKey cacheKey;

  public ImageThumbnail(String pFilename, SimpleImage pPreview, ThumbnailCacheKey pCacheKey) {
    filename = pFilename;
    preview = pPreview;
    cacheKey = pCacheKey;
  }

  private SimpleImage generatePreview() {
    if (cacheKey != null) {
      SimpleImage img = ThumbnailCacheProvider.getThumbnail(cacheKey, IMG_WIDTH);
      if (img != null) {
        return img;
      }
    }

    SimpleImage image;
    try {
      image = ((SimpleImage) RessourceManager.getImage(filename));
      RessourceManager.clearImage(filename);
      ScaleTransformer scaleT = new ScaleTransformer();
      scaleT.setScaleWidth(IMG_WIDTH);
      scaleT.setAspect(ScaleAspect.KEEP_WIDTH);
      scaleT.transformImage(image);

      if (cacheKey != null) {
        ThumbnailCacheProvider.storeThumbnail(cacheKey, IMG_WIDTH, image);
      }
      return image;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public SimpleImage getPreview() {
    if (preview == null) {
      preview = generatePreview();
    }
    return preview;
  }

  public ImagePanel getImgPanel() {
    return imgPanel;
  }

  public void setImgPanel(ImagePanel imgPanel) {
    this.imgPanel = imgPanel;
  }

  public String getFilename() {
    return filename;
  }

}
