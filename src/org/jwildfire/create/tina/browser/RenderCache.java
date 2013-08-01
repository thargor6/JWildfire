package org.jwildfire.create.tina.browser;

import java.util.HashMap;
import java.util.Map;

import org.jwildfire.image.SimpleImage;

public class RenderCache {
  private Map<String, SimpleImage> renderCache = new HashMap<String, SimpleImage>();

  public SimpleImage getImage(FlameFlatNode pNode) {
    return renderCache.get(pNode.getFilename());
  }

  public void putImage(FlameFlatNode pNode, SimpleImage pImg) {
    renderCache.put(pNode.getFilename(), pImg);
  }

}
