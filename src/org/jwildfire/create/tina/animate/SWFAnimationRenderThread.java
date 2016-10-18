/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;

public class SWFAnimationRenderThread implements Runnable {
  private final SWFAnimationRenderThreadController controller;
  private final String outputFilename;
  private boolean cancelSignalled;
  private FlameMovie flameMovie;
  private Throwable lastError;
  private final List<SimpleImage> renderedImages = new ArrayList<SimpleImage>();

  public SWFAnimationRenderThread(SWFAnimationRenderThreadController pController, FlameMovie pAnimation, String pOutputFilename) {
    controller = pController;
    flameMovie = pAnimation;
    outputFilename = pOutputFilename;
  }

  @Override
  public void run() {
    try {
      try {
        cancelSignalled = false;
        lastError = null;
        renderedImages.clear();
        controller.getProgressUpdater().initProgress(flameMovie.getFrameCount());
        int startFrame = 1;
        int endFrame = flameMovie.getFrameCount();
        for (int i = startFrame; i <= endFrame; i++) {
          if (cancelSignalled) {
            break;
          }
          Flame currFlame = createFlame(i);
          processFlame(currFlame, i);
          controller.getProgressUpdater().updateProgress(i);
        }
        finishSequence();
      }
      catch (Throwable ex) {
        lastError = ex;
        throw new RuntimeException(ex);
      }
    }
    finally {
      controller.onRenderFinished();
    }
  }

  private void finishSequence() throws Exception {
    switch (flameMovie.getSequenceOutputType()) {
      case ANB:
        createANB();
        break;
      default: // nothing to do
        break;
    }
  }

  private void createANB() throws Exception {
    int width = renderedImages.get(0).getImageWidth();
    int height = renderedImages.get(0).getImageHeight();

    int frameCount = renderedImages.size();
    int size = (width + 1) * (height + 1);
    int totalSize = 16 + 20 + 4 * frameCount * size; // header + (r + g + b + alpha) * frameCount 
    int direction = 1;
    int endBehaviour = 0;
    int step = 16;
    int reserved1 = 0;
    int reserved2 = 0;

    byte buffer[] = new byte[totalSize];
    int offset = 0;
    buffer[offset++] = 'A';
    buffer[offset++] = 'N';
    buffer[offset++] = 'B';
    buffer[offset++] = 'R';
    buffer[offset++] = (byte) (width );
    buffer[offset++] = (byte) (width >> 8);
    buffer[offset++] = (byte) (width >> 16);
    buffer[offset++] = (byte) (width >> 24);
    buffer[offset++] = (byte) (height);
    buffer[offset++] = (byte) (height >> 8);
    buffer[offset++] = (byte) (height >> 16);
    buffer[offset++] = (byte) (height >> 24);
    buffer[offset++] = (byte) ((frameCount - 1));
    buffer[offset++] = (byte) ((frameCount - 1) >> 8);
    buffer[offset++] = (byte) ((frameCount - 1) >> 16);
    buffer[offset++] = (byte) ((frameCount - 1) >> 24);
    buffer[offset++] = (byte) (direction);
    buffer[offset++] = (byte) (direction >> 8);
    buffer[offset++] = (byte) (direction >> 16);
    buffer[offset++] = (byte) (direction >> 24);
    buffer[offset++] = (byte) (endBehaviour);
    buffer[offset++] = (byte) (endBehaviour >> 8);
    buffer[offset++] = (byte) (endBehaviour >> 16);
    buffer[offset++] = (byte) (endBehaviour >> 24);
    buffer[offset++] = (byte) (step);
    buffer[offset++] = (byte) (step >> 8);
    buffer[offset++] = (byte) (step >> 16);
    buffer[offset++] = (byte) (step >> 24);
    buffer[offset++] = (byte) (reserved1);
    buffer[offset++] = (byte) (reserved1 >> 8);
    buffer[offset++] = (byte) (reserved1 >> 16);
    buffer[offset++] = (byte) (reserved1 >> 24);
    buffer[offset++] = (byte) (reserved2);
    buffer[offset++] = (byte) (reserved2 >> 8);
    buffer[offset++] = (byte) (reserved2 >> 16);
    buffer[offset++] = (byte) (reserved2 >> 24);
    for (int channel = 0; channel < 4; channel++) {
      for (int i = 0; i < frameCount; i++) {
        fillBuffer(buffer, offset, renderedImages.get(i), channel);
        offset += size;
      }
    }
    String filename = outputFilename;
    if (!filename.endsWith(Tools.FILEEXT_ANB)) {
      filename = filename + "." + Tools.FILEEXT_ANB;
    }
    Tools.writeFile(filename, buffer);
  }

  private void fillBuffer(byte[] pBuffer, int pOffset, SimpleImage pImage, int pChannel) {
    Pixel p = new Pixel();
    int offset = pOffset;
    int lineWidth = pImage.getImageWidth() + 1;
    for (int i = 0; i < pImage.getImageHeight(); i++) {
      for (int j = 0; j < pImage.getImageWidth(); j++) {
        p.setARGBValue(pImage.getARGBValue(j, i));
        switch (pChannel) {
          case 0:
            pBuffer[offset + j] = (byte) p.r;
            break;
          case 1:
            pBuffer[offset + j] = (byte) p.g;
            break;
          case 2:
            pBuffer[offset + j] = (byte) p.b;
            break;
          default:
            pBuffer[offset + j] = (byte) p.a;
            break;
        }
      }
      offset += lineWidth;
    }
  }

  private void processFlame(Flame pCurrFlame, int pFrame) throws Exception {
    switch (flameMovie.getSequenceOutputType()) {
      case FLAMES:
        saveFlame(pCurrFlame, pFrame);
        break;
      case PNG_IMAGES:
        saveImage(renderFlame(pCurrFlame), pFrame);
        break;
      case ANB:
        pCurrFlame.setBGTransparency(true);
        renderedImages.add(renderFlame(pCurrFlame));
        break;
    }
  }

  private void saveImage(SimpleImage pImage, int pFrame) throws Exception {
    String filename = generateFilename(pFrame, Tools.FILEEXT_PNG);
    new ImageWriter().saveAsPNG(pImage, filename);
  }

  private SimpleImage renderFlame(Flame pFlame) {
    RenderInfo info = new RenderInfo(flameMovie.getFrameWidth(), flameMovie.getFrameHeight(), RenderMode.PRODUCTION);
    double wScl = (double) info.getImageWidth() / (double) pFlame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) pFlame.getHeight();
    pFlame.setPixelsPerUnit((wScl + hScl) * 0.5 * pFlame.getPixelsPerUnit());
    pFlame.setWidth(info.getImageWidth());
    pFlame.setHeight(info.getImageHeight());

    FlameRenderer renderer = new FlameRenderer(pFlame, Prefs.getPrefs(), pFlame.isBGTransparency(), false);
    renderer.setProgressUpdater(null);
    pFlame.setSampleDensity(flameMovie.getQuality());
    RenderedFlame res = renderer.renderFlame(info);
    return res.getImage();
  }

  private Flame createFlame(int pFrame) throws Exception {
    Flame flame1 = flameMovie.getFlame(pFrame);
    Flame res = flameMovie.createAnimatedFlame(flame1, pFrame);
    return res;
  }

  private void saveFlame(Flame pFlame, int pFrame) throws Exception {
    String filename = generateFilename(pFrame, Tools.FILEEXT_FLAME);
    new FlameWriter().writeFlame(pFlame, filename);
  }

  private String generateFilename(int pFrame, String pFileExt) {
    String filename = outputFilename;
    {
      int pSlash = filename.lastIndexOf("/");
      int pSlash2 = filename.lastIndexOf("\\");
      if (pSlash2 > pSlash) {
        pSlash = pSlash2;
      }
      int pDot = filename.lastIndexOf(".");
      if (pDot > pSlash) {
        filename = filename.substring(0, pDot);
      }
    }
    String hs = String.valueOf(pFrame);
    int length = calcFrameNumberLength();
    while (hs.length() < length) {
      hs = "0" + hs;
    }
    filename += hs + "." + pFileExt;
    return filename;
  }

  private int calcFrameNumberLength() {
    return Math.max(4, String.valueOf(flameMovie.getFrameCount()).length());
  }

  public void setCancelSignalled(boolean cancelSignalled) {
    this.cancelSignalled = cancelSignalled;
  }

  public Throwable getLastError() {
    return lastError;
  }

  public boolean isCancelSignalled() {
    return cancelSignalled;
  }
}
