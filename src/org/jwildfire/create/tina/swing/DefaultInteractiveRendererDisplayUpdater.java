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

import javax.swing.JPanel;

import org.jwildfire.create.tina.render.AbstractRenderThread;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;

public class DefaultInteractiveRendererDisplayUpdater implements InteractiveRendererDisplayUpdater {
  private long sampleCount;
  private final JPanel imageRootPanel;
  private final SimpleImage image;
  private final int imageWidth;
  private final int imageHeight;
  private boolean showPreview;
  private long[] iterationCount;

  public DefaultInteractiveRendererDisplayUpdater(JPanel pImageRootPanel, SimpleImage pImage, boolean pShowPreview) {
    imageRootPanel = pImageRootPanel;
    image = pImage;
    imageWidth = image.getImageWidth();
    imageHeight = image.getImageHeight();
    showPreview = pShowPreview;
  }

  @Override
  public void iterationFinished(AbstractRenderThread pEventSource, int pX, int pY) {
    int x = pX / pEventSource.getOversample();
    int y = pY / pEventSource.getOversample();
    iterationCount[pEventSource.getThreadId()] = pEventSource.getCurrSample();
    sampleCount = calculateSampleCount();
    if (showPreview && x >= 0 && x < image.getImageWidth() && y >= 0 && y < image.getImageHeight()) {
      image.setARGB(x, y, pEventSource.getTonemapper().tonemapSample(x, y));
    }
  }

  @Override
  public void updateImage(InteractiveRendererImagePostProcessor pProcessor) {
    if (showPreview) {
      if (pProcessor != null) {
        pProcessor.postProcessImage(image);
      }
      imageRootPanel.repaint();
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
    if (pBGRed > 0 || pBGGreen > 0 || pBGBlue > 0) {
      SimpleImage img = new SimpleImage(image.getBufferedImg(), imageWidth, imageHeight);
      img.fillBackground(pBGRed, pBGGreen, pBGBlue);
    }
    if (pBGImagefile != null && pBGImagefile.length() > 0) {
      try {
        SimpleImage img = new SimpleImage(image.getBufferedImg(), imageWidth, imageHeight);
        SimpleImage bgImg = (SimpleImage) RessourceManager.getImage(pBGImagefile);
        img.fillBackground(bgImg);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
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
