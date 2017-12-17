/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.render.AbstractRenderThread;
import org.jwildfire.create.tina.render.FlameBGColorHandler;
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

  private static class ImgSize {
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

  private final TinaController tinaController;
  private final DetachedPreviewWindow detachedPreviewWindow;
  private final Prefs prefs;
  private final JPanel imageRootPanel;
  private Flame flame;
  private State state = State.IDLE;
  private RenderThreads threads;
  private Thread updateDisplayExecuteThread;
  private FlameRenderer renderer;
  private ImagePanel imagePanel;
  private final JToggleButton toggleDetachedPreviewButton;
  private SimpleImage image;
  private boolean refreshing;
  private boolean paused;
  private double currQuality;
  private InteractiveRendererDisplayUpdater displayUpdater = new EmptyInteractiveRendererDisplayUpdater();
  private UpdateDisplayThread updateDisplayThread;

  public DetachedPreviewController(TinaController pTinaController, DetachedPreviewWindow pDetachedPreviewWindow, JToggleButton pToggleDetachedPreviewButton) {
    tinaController = pTinaController;
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
    info.setRenderHDR(false);
    info.setRenderZBuffer(false);
    new FlameBGColorHandler(flame).fillBackground(image);
    renderer = new FlameRenderer(flame, prefs, flame.isBGTransparency(), false);
    renderer.registerIterationObserver(this);

    displayUpdater = createDisplayUpdater();
    displayUpdater.initRender(prefs.getTinaRenderThreads());
    threads = renderer.startRenderFlame(info);
    for (Thread thread : threads.getExecutingThreads()) {
      thread.setPriority(Thread.MIN_PRIORITY);
    }

    updateDisplayThread = new UpdateDisplayThread();
    updateDisplayExecuteThread = new Thread(updateDisplayThread);
    updateDisplayExecuteThread.setPriority(Thread.MIN_PRIORITY);
    updateDisplayExecuteThread.start();

    state = State.RENDER;
  }

  public void cancelRender() {
    if (refreshing || threads == null)
      return;
    if (state == State.PAUSE) {
      togglePause();
    }

    if (updateDisplayThread != null) {
      updateDisplayThread.cancel();
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

      if (updateDisplayExecuteThread != null) {
        updateDisplayExecuteThread.setPriority(Thread.NORM_PRIORITY);
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
      threads = null;

      if (updateDisplayThread != null) {
        updateDisplayThread.cancel();
        while (!updateDisplayThread.isFinished()) {
          try {
            updateDisplayThread.cancel();
            Thread.sleep(1);
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        updateDisplayThread = null;
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

  private synchronized void updateImage() {
    displayUpdater.updateImage(null);
  }

  @Override
  public void notifyIterationFinished(AbstractRenderThread pEventSource, int pPlotX, int pPlotY, XYZProjectedPoint pProjectedPoint, double pX, double pY, double pZ, double pColorRed, double pColorGreen, double pColorBlue) {
    while (paused) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    displayUpdater.iterationFinished(pEventSource, pPlotX, pPlotY);
  }

  private void updateStats() {
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
    tinaController.closeDetachedPreview();
    //cancelRender();
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

  private class UpdateDisplayThread implements Runnable {
    private int nextImageUpdate;
    private int nextStatsUpdate;
    private int lastImageUpdateInterval;
    private boolean cancelSignalled;
    private boolean finished;

    public UpdateDisplayThread() {
      nextImageUpdate = INITIAL_IMAGE_UPDATE_INTERVAL;
      lastImageUpdateInterval = INITIAL_IMAGE_UPDATE_INTERVAL;
      nextStatsUpdate = STATS_UPDATE_INTERVAL;
    }

    @Override
    public void run() {
      finished = cancelSignalled = false;
      try {
        {
          AbstractRenderThread thread = threads.getRenderThreads().get(0);
          initImage(thread.getBgRed(), thread.getBgGreen(), thread.getBgBlue(), thread.getBgImagefile());
        }
        while (!cancelSignalled) {
          try {
            if (--nextImageUpdate <= 0) {
              lastImageUpdateInterval += IMAGE_UPDATE_INC_INTERVAL;
              if (lastImageUpdateInterval > MAX_UPDATE_INC_INTERVAL) {
                lastImageUpdateInterval = MAX_UPDATE_INC_INTERVAL;
              }
              updateImage();
              nextImageUpdate = lastImageUpdateInterval;
            }
            else if (--nextStatsUpdate <= 0) {
              currQuality = threads.getRenderThreads().get(0).getTonemapper().calcDensity(displayUpdater.getSampleCount());
              updateStats();
              for (AbstractRenderThread thread : threads.getRenderThreads()) {
                thread.getTonemapper().setDensity(currQuality);
              }
              nextStatsUpdate = STATS_UPDATE_INTERVAL;
            }
            else
              Thread.sleep(1);
          }
          catch (Throwable e) {
            e.printStackTrace();
          }
        }
      }
      finally {
        finished = true;
      }
    }

    private void initImage(int pBGRed, int pBGGreen, int pBGBlue, String pBGImagefile) {
      displayUpdater.initImage(pBGRed, pBGGreen, pBGBlue, pBGImagefile);
    }

    public void cancel() {
      cancelSignalled = true;
    }

    public boolean isFinished() {
      return finished;
    }

  }

  private final static int STATS_UPDATE_INTERVAL = 24;
  private final static int INITIAL_IMAGE_UPDATE_INTERVAL = 3;
  private final static int IMAGE_UPDATE_INC_INTERVAL = 1;
  private final static int MAX_UPDATE_INC_INTERVAL = 200;

  private InteractiveRendererDisplayUpdater createDisplayUpdater() {
    return prefs.isTinaOptimizedRenderingIR() ? new BufferedInteractiveRendererDisplayUpdater(imageRootPanel, image, true) : new DefaultInteractiveRendererDisplayUpdater(imageRootPanel, image, true);
  }

}
