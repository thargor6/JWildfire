/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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

import javax.swing.*;

import org.jwildfire.create.tina.render.AbstractRenderThread;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageWriter;

public class BufferedInteractiveRendererDisplayUpdater implements InteractiveRendererDisplayUpdater {
  private long sampleCount;
  private final JPanel imageRootPanel;
  private final SimpleImage image;
  private final int imageWidth;
  private final int imageHeight;
  private int[] buffer;
  private long[] iterationCount;

  private boolean showPreview;

  public BufferedInteractiveRendererDisplayUpdater(JPanel pImageRootPanel, SimpleImage pImage, boolean pShowPreview) {
    imageRootPanel = pImageRootPanel;
    image = pImage;
    imageWidth = image.getImageWidth();
    imageHeight = image.getImageHeight();
    buffer = getBufferFromImage();
    showPreview = pShowPreview;
  }

  private int[] getBufferFromImage() {
    return image.getBufferedImg().getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth);
  }

  @Override
  public void iterationFinished(AbstractRenderThread pEventSource, int pX, int pY) {
    iterationCount[pEventSource.getThreadId()] = pEventSource.getCurrSample();
    sampleCount = calculateSampleCount();
    int x = pX / pEventSource.getOversample();
    int y = pY / pEventSource.getOversample();
    if (showPreview && x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
      int argb = pEventSource.getTonemapper().tonemapSample(x, y);
      int offset = imageWidth * y + x;
      buffer[offset] = argb;
    }
  }

  @Override
  public void updateImage(InteractiveRendererImagePostProcessor pProcessor) {
    if (showPreview) {
      image.getBufferedImg().setRGB(0, 0, imageWidth, imageHeight, buffer, 0, imageWidth);
      if (pProcessor != null) {
        pProcessor.postProcessImage(image);
      }
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          try {
            imageRootPanel.repaint();
          }
          catch(Exception ex) {
            ex.printStackTrace();
          }
        }
      });

    }
  }

  @Override
  public long getSampleCount() {
    return sampleCount;
  }

  @Override
  public void setShowPreview(boolean pShowPreview) {
    showPreview = pShowPreview;
  }

  @Override
  public void initImage(int pBGRed, int pBGGreen, int pBGBlue, String pBGImagefile) {
    boolean repaint = false;
    if (pBGRed > 0 || pBGGreen > 0 || pBGBlue > 0) {
      SimpleImage img = new SimpleImage(image.getBufferedImg(), imageWidth, imageHeight);
      img.fillBackground(pBGRed, pBGGreen, pBGBlue);
      repaint = true;
    }
    if (pBGImagefile != null && pBGImagefile.length() > 0) {
      try {
        SimpleImage img = new SimpleImage(image.getBufferedImg(), imageWidth, imageHeight);
        SimpleImage bgImg = (SimpleImage) RessourceManager.getImage(pBGImagefile);
        img.fillBackground(bgImg);
        repaint = true;
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (repaint) {
      buffer = getBufferFromImage();
    }
  }

  private long calculateSampleCount() {
    long res = 0;
    for (int i = 0; i < iterationCount.length; i++) {
      res += iterationCount[i];
    }
    return res;
  }

  @Override
  public void initRender(int pThreadGroupSize) {
    iterationCount = new long[pThreadGroupSize];
  }
}
