package org.jwildfire.create.tina.swing;

import java.io.File;

public class ThumbnailCacheKey {
  private final String filename;
  private final String keyExt;
  private final long modificationtime;
  private static final int version = 2;

  public ThumbnailCacheKey(String pFilename) {
    this(pFilename, "");
  }

  public ThumbnailCacheKey(String pFilename, String pKeyExt) {
    filename = pFilename;
    modificationtime = new File(filename).lastModified();
    keyExt = pKeyExt;
  }

  public String getCacheKey(int pThumbnailWidth) {
    return filename.hashCode() + "#" + modificationtime + "#" + keyExt + "#" + pThumbnailWidth + "#" + version;
  }

  public String getCacheKey(int pThumbnailWidth, int pThumbnailHeight) {
    return getCacheKey(pThumbnailWidth) + "#" + pThumbnailWidth;
  }

  public String getCacheKey(int pThumbnailWidth, int pThumbnailHeight, double pQuality) {
    return getCacheKey(pThumbnailWidth, pThumbnailHeight) + "#" + pQuality;
  }
}
