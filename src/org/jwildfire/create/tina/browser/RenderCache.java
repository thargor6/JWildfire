package org.jwildfire.create.tina.browser;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.create.tina.swing.ThumbnailCacheKey;
import org.jwildfire.create.tina.swing.ThumbnailCacheProvider;
import org.jwildfire.image.SimpleImage;

public class RenderCache {
  private Map<String, SimpleImage> renderCache = new HashMap<String, SimpleImage>();
  private boolean storeThumbnails = true;

  public SimpleImage getImage(FlameFlatNode pNode, int pWidth, int pHeight) {
    SimpleImage img = renderCache.get(pNode.getFilename());
    if (img == null && storeThumbnails) {
      img = ThumbnailCacheProvider.getThumbnail(new ThumbnailCacheKey(pNode.getFilename()), pWidth, pHeight);
    }
    return img;
  }

  public void putImage(FlameFlatNode pNode, SimpleImage pImg, int pWidth, int pHeight) {
    renderCache.put(pNode.getFilename(), pImg);
    if (storeThumbnails) {
      ThumbnailCacheProvider.storeThumbnail(new ThumbnailCacheKey(pNode.getFilename()), pWidth, pHeight, pImg);
    }
  }

}
