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

import javax.swing.JCheckBox;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class FlameThumbnail {
  public static final int IMG_WIDTH = 90;
  public static final int IMG_HEIGHT = 68;
  public static final int BORDER_SIZE = 8;

  private Flame flame;
  private SimpleImage preview;
  private ImagePanel imgPanel;
  private JCheckBox selectCheckbox;
  private final ThumbnailCacheKey cacheKey;

  public FlameThumbnail(Flame pFlame, SimpleImage pPreview, ThumbnailCacheKey pCacheKey) {
    cacheKey = pCacheKey;
    flame = pFlame;
    preview = pPreview;
  }

  private void generatePreview(int pQuality) {
    if (cacheKey != null) {
      preview = ThumbnailCacheProvider.getThumbnail(cacheKey, IMG_WIDTH, IMG_HEIGHT, pQuality);
      if (preview != null) {
        return;
      }
    }

    Prefs prefs = Prefs.getPrefs();
    RenderInfo info = new RenderInfo(IMG_WIDTH, IMG_HEIGHT, RenderMode.PREVIEW);
    Flame renderFlame = flame.makeCopy();
    double wScl = (double) info.getImageWidth() / (double) renderFlame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) renderFlame.getHeight();
    renderFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * renderFlame.getPixelsPerUnit());
    renderFlame.setWidth(IMG_WIDTH);
    renderFlame.setHeight(IMG_HEIGHT);
    renderFlame.setSampleDensity(prefs.getTinaRenderPreviewQuality());
    renderFlame.setSpatialFilterRadius(0.0);
    FlameRenderer renderer = new FlameRenderer(renderFlame, prefs, false, false);
    renderFlame.setSampleDensity(pQuality);
    RenderedFlame res = renderer.renderFlame(info);
    preview = res.getImage();
    if (cacheKey != null) {
      ThumbnailCacheProvider.storeThumbnail(cacheKey, IMG_WIDTH, IMG_HEIGHT, pQuality, preview);
    }
  }

  public SimpleImage getPreview(int pQuality) {
    if (preview == null) {
      generatePreview(pQuality);
    }
    return preview;
  }

  public Flame getFlame() {
    return flame;
  }

  public ImagePanel getImgPanel() {
    return imgPanel;
  }

  public void setImgPanel(ImagePanel imgPanel) {
    this.imgPanel = imgPanel;
  }

  public void setPreview(SimpleImage pPreview) {
    preview = pPreview;
  }

  public void setSelectCheckbox(JCheckBox checkbox) {
    selectCheckbox = checkbox;
  }

  public JCheckBox getSelectCheckbox() {
    return selectCheckbox;
  }

}
