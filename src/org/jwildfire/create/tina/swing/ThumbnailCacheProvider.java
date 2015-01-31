package org.jwildfire.create.tina.swing;

import java.io.File;

import javax.swing.JLabel;

import org.jwildfire.base.Tools;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;

public class ThumbnailCacheProvider {

  public static SimpleImage getThumbnail(ThumbnailCacheKey pCacheKey, int pWidth) {
    File file = new File(System.getProperty("java.io.tmpdir"), pCacheKey.getCacheKey(pWidth) + "." + Tools.FILEEXT_PNG);
    return tryReadImage(pWidth, file);
  }

  public static SimpleImage getThumbnail(ThumbnailCacheKey pCacheKey, int pWidth, int pHeight) {
    File file = new File(System.getProperty("java.io.tmpdir"), pCacheKey.getCacheKey(pWidth, pHeight) + "." + Tools.FILEEXT_PNG);
    return tryReadImage(pWidth, pHeight, file);
  }

  public static SimpleImage getThumbnail(ThumbnailCacheKey pCacheKey, int pWidth, int pHeight, int pQuality) {
    File file = new File(System.getProperty("java.io.tmpdir"), pCacheKey.getCacheKey(pWidth, pHeight, pQuality) + "." + Tools.FILEEXT_PNG);
    return tryReadImage(pWidth, pHeight, file);
  }

  private static SimpleImage tryReadImage(int pWidth, int pHeight, File pFile) {
    if (pFile.exists()) {
      try {
        SimpleImage img = new ImageReader(new JLabel()).loadImage(pFile.getAbsolutePath());
        if (img != null && img.getImageWidth() == pWidth && img.getImageHeight() == pHeight) {
          return img;
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return null;
  }

  private static SimpleImage tryReadImage(int pWidth, File pFile) {
    if (pFile.exists()) {
      try {
        SimpleImage img = new ImageReader(new JLabel()).loadImage(pFile.getAbsolutePath());
        if (img != null && img.getImageWidth() == pWidth) {
          return img;
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return null;
  }

  public static void storeThumbnail(ThumbnailCacheKey pCacheKey, int pWidth, SimpleImage pThumbnail) {
    File file = new File(System.getProperty("java.io.tmpdir"), pCacheKey.getCacheKey(pWidth) + "." + Tools.FILEEXT_PNG);
    tryWriteImage(pThumbnail, file);
  }

  public static void storeThumbnail(ThumbnailCacheKey pCacheKey, int pWidth, int pHeight, SimpleImage pThumbnail) {
    File file = new File(System.getProperty("java.io.tmpdir"), pCacheKey.getCacheKey(pWidth, pHeight) + "." + Tools.FILEEXT_PNG);
    tryWriteImage(pThumbnail, file);
  }

  public static void storeThumbnail(ThumbnailCacheKey pCacheKey, int pWidth, int pHeight, int pQuality, SimpleImage pThumbnail) {
    File file = new File(System.getProperty("java.io.tmpdir"), pCacheKey.getCacheKey(pWidth, pHeight, pQuality) + "." + Tools.FILEEXT_PNG);
    tryWriteImage(pThumbnail, file);
  }

  private static void tryWriteImage(SimpleImage pThumbnail, File pFile) {
    try {
      new ImageWriter().saveAsPNG(pThumbnail, pFile.getAbsolutePath());
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
