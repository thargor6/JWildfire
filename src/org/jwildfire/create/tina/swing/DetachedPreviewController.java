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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.AbstractRenderThread;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.IterationObserver;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderMode;
import org.jwildfire.create.tina.render.RenderThreads;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ImagePanel;

public class DetachedPreviewController implements IterationObserver {
  public enum State {
    IDLE, RENDER, PAUSE
  }

  private class ImgSize {
    private final int width;
    private final int height;

    public ImgSize(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

  }

  private final DetachedPreviewWindow detachedPreviewWindow;
  private final Prefs prefs;
  private final JPanel imageRootPanel;
  private Flame flame;
  private State state = State.IDLE;
  private RenderThreads threads;
  private FlameRenderer renderer;
  private long sampleCount = 0;
  private long renderStartTime = 0;
  private long pausedRenderTime = 0;
  private ImagePanel imagePanel;
  private final JToggleButton toggleDetachedPreviewButton;
  private SimpleImage image;
  private boolean refreshing;
  private boolean paused;
  private double currQuality;

  public DetachedPreviewController(DetachedPreviewWindow pDetachedPreviewWindow, JToggleButton pToggleDetachedPreviewButton) {
    detachedPreviewWindow = pDetachedPreviewWindow;
    imageRootPanel = detachedPreviewWindow.getImageRootPanel();
    prefs = Prefs.getPrefs();
    toggleDetachedPreviewButton = pToggleDetachedPreviewButton;
  }

  public void setFlame(Flame pFlame) {
    cancelRender();
    flame = pFlame != null ? pFlame.makeCopy() : null;
    startRender();
  }

  public void startRender() {
    cancelRender();
    setPaused(false);
    currQuality = 0.0;
    if (flame == null || refreshing) {
      return;
    }
    refreshImagePanel();
    clearScreen();
    ImgSize size = getImgSize();
    RenderInfo info = new RenderInfo(size.getWidth(), size.getHeight(), RenderMode.PREVIEW);
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    flame.setSampleDensity(10);
    info.setRenderHDR(prefs.isTinaSaveHDRInIR());
    info.setRenderHDRIntensityMap(false);
    if (flame.getBGColorRed() > 0 || flame.getBGColorGreen() > 0 || flame.getBGColorBlue() > 0) {
      image.fillBackground(flame.getBGColorRed(), flame.getBGColorGreen(), flame.getBGColorBlue());
    }
    renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
    renderer.registerIterationObserver(this);
    sampleCount = 0;
    renderStartTime = System.currentTimeMillis();
    pausedRenderTime = 0;
    threads = renderer.startRenderFlame(info);
    for (Thread thread : threads.getExecutingThreads()) {
      thread.setPriority(Thread.MIN_PRIORITY);
    }
    state = State.RENDER;
  }

  public void cancelRender() {
    if (refreshing || threads == null)
      return;
    if (state == State.PAUSE) {
      togglePause();
    }
    try {
      for (Thread thread : threads.getExecutingThreads()) {
        try {
          thread.setPriority(Thread.NORM_PRIORITY);
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    if (state == State.RENDER) {
      while (true) {
        boolean done = true;
        for (AbstractRenderThread thread : threads.getRenderThreads()) {
          if (!thread.isFinished()) {
            done = false;
            thread.cancel();
            try {
              Thread.sleep(1);
            }
            catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
        if (done) {
          break;
        }
      }
      state = State.IDLE;
    }
  }

  private ImgSize getImgSize() {
    return new ImgSize(imageRootPanel.getSize().width, imageRootPanel.getSize().height);
  }

  private void clearScreen() {
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    imageRootPanel.repaint();
  }

  private void refreshImagePanel() {
    if (imagePanel != null) {
      imageRootPanel.remove(imagePanel);
    }
    ImgSize size = getImgSize();
    image = new SimpleImage(size.getWidth(), size.getHeight());
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    imagePanel = new ImagePanel(image, 0, 0, image.getImageWidth());
    imagePanel.setSize(image.getImageWidth(), image.getImageHeight());
    imagePanel.setPreferredSize(new Dimension(image.getImageWidth(), image.getImageHeight()));

    imageRootPanel.add(imagePanel, BorderLayout.CENTER);

    imageRootPanel.getParent().validate();
  }

  private synchronized void incSampleCount() {
    sampleCount++;
  }

  private synchronized void updateImage() {
    imageRootPanel.repaint();
  }

  @Override
  public void notifyIterationFinished(AbstractRenderThread pEventSource, int pX, int pY) {
    while (paused) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    incSampleCount();
    if (pX >= 0 && pX < image.getImageWidth() && pY >= 0 && pY < image.getImageHeight()) {
      image.setARGB(pX, pY, pEventSource.getTonemapper().tonemapSample(pX, pY));
      if (sampleCount % 2000 == 0) {
        updateImage();
      }
      if (sampleCount % 4000 == 0) {
        double quality = pEventSource.getTonemapper().calcDensity(sampleCount);
        currQuality = quality;
        updateStats(pEventSource);
        pEventSource.getTonemapper().setDensity(quality);
        if (threads != null) {
          for (AbstractRenderThread thread : threads.getRenderThreads()) {
            try {
              thread.getTonemapper().setDensity(quality);
            }
            catch (Exception ex) {
              // no op
            }
          }
        }
      }
    }
  }

  private void updateStats(AbstractRenderThread pEventSource) {
    detachedPreviewWindow.setTitle(state, currQuality);
  }

  public void notifyWindowIsClosing() {
    if (toggleDetachedPreviewButton.isSelected()) {
      boolean oldRefreshing = refreshing;
      refreshing = true;
      try {
        toggleDetachedPreviewButton.setSelected(false);
      }
      finally {
        refreshing = oldRefreshing;
      }
    }
  }

  public void togglePause() {
    if (state == State.RENDER) {
      state = State.PAUSE;
      detachedPreviewWindow.setTitle(state, currQuality);
      setPaused(true);
    }
    else {
      state = State.RENDER;
      setPaused(false);
    }
  }

  public boolean isPaused() {
    return paused;
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
  }
}
