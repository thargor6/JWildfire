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
package org.jwildfire.create.tina.swing;

import javax.swing.JPanel;

import org.jwildfire.create.tina.render.AbstractRenderThread;
import org.jwildfire.image.SimpleImage;

public class DefaultInteractiveRendererDisplayUpdater implements InteractiveRendererDisplayUpdater {
  private long sampleCount;
  private final JPanel imageRootPanel;
  private final SimpleImage image;
  private boolean showPreview;

  public DefaultInteractiveRendererDisplayUpdater(JPanel pImageRootPanel, SimpleImage pImage, boolean pShowPreview) {
    imageRootPanel = pImageRootPanel;
    image = pImage;
    showPreview = pShowPreview;
  }

  @Override
  public void iterationFinished(AbstractRenderThread pEventSource, int pX, int pY) {
    sampleCount++;
    if (showPreview && pX >= 0 && pX < image.getImageWidth() && pY >= 0 && pY < image.getImageHeight()) {
      image.setARGB(pX, pY, pEventSource.getTonemapper().tonemapSample(pX, pY));
    }
  }

  @Override
  public void updateImage() {
    if (showPreview) {
      imageRootPanel.repaint();
    }
  }

  @Override
  public long getSampleCount() {
    return sampleCount;
  }

  @Override
  public void setSampleCount(long pSampleCount) {
    sampleCount = pSampleCount;
  }

  @Override
  public void setShowPreview(boolean pShowPreview) {
    showPreview = pShowPreview;
  }
}
