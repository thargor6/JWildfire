/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.audio.JLayerInterface;
import org.jwildfire.create.tina.audio.RecordedFFT;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.randomflame.RandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorList;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.create.tina.render.RenderedFlame;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;

public class DancingFractalsController implements FlameHolder {
  private final ErrorHandler errorHandler;
  private final TinaController parent;
  private final Prefs prefs;
  final JPanel flameRootPanel;
  final JPanel graph1RootPanel;
  JLayerInterface jLayer = new JLayerInterface();
  private FlamePanel flamePanel = null;
  private ImagePanel graph1Panel = null;
  private Flame flame;
  private RealtimeAnimRenderThread renderThread;

  private String soundFilename;

  public DancingFractalsController(TinaController pParent, ErrorHandler pErrorHandler, JPanel pRealtimeFlamePnl, JPanel pRealtimeGraph1Pnl) {
    parent = pParent;
    errorHandler = pErrorHandler;
    prefs = parent.getPrefs();
    flameRootPanel = pRealtimeFlamePnl;
    graph1RootPanel = pRealtimeGraph1Pnl;
  }

  // getFlamePanel().getParent().paint(getFlamePanel().getParent().getGraphics());

  private FlamePanel getFlamePanel() {
    if (flamePanel == null && flameRootPanel != null) {
      int width = flameRootPanel.getWidth();
      int height = flameRootPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      flamePanel = new FlamePanel(img, 0, 0, flameRootPanel.getWidth(), this, null, null);
      flamePanel.setRenderWidth(640);
      flamePanel.setRenderHeight(480);
      flamePanel.setFocusable(true);
      flameRootPanel.add(flamePanel, BorderLayout.CENTER);
      flameRootPanel.getParent().validate();
      flameRootPanel.repaint();
    }
    return flamePanel;
  }

  private ImagePanel getGraph1Panel() {
    if (graph1Panel == null && graph1RootPanel != null) {
      int width = graph1RootPanel.getWidth();
      int height = graph1RootPanel.getHeight();
      SimpleImage img = new SimpleImage(width, height);
      img.fillBackground(0, 0, 0);
      graph1Panel = new ImagePanel(img, 0, 0, graph1RootPanel.getWidth());
      graph1RootPanel.add(graph1Panel, BorderLayout.CENTER);
      graph1RootPanel.getParent().validate();
      graph1RootPanel.repaint();
    }
    return graph1Panel;
  }

  public void refreshFlameImage(Flame flame, boolean pDrawTriangles) {
    FlamePanel imgPanel = getFlamePanel();
    Rectangle bounds = imgPanel.getImageBounds();
    int width = bounds.width;
    int height = bounds.height;
    if (width >= 16 && height >= 16) {
      RenderInfo info = new RenderInfo(width, height);
      if (flame != null) {
        double oldSpatialFilterRadius = flame.getSpatialFilterRadius();
        int oldSpatialOversample = flame.getSpatialOversample();
        int oldColorOversample = flame.getColorOversample();
        double oldSampleDensity = flame.getSampleDensity();
        imgPanel.setDrawTriangles(pDrawTriangles);
        try {
          double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
          double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
          flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
          flame.setWidth(info.getImageWidth());
          flame.setHeight(info.getImageHeight());

          FlameRenderer renderer = new FlameRenderer(flame, prefs, false);
          renderer.setProgressUpdater(null);

          flame.setGamma(1.6);
          flame.setSampleDensity(15);
          flame.setSpatialFilterRadius(0.75);

          flame.setSpatialOversample(1);
          flame.setColorOversample(1);
          RenderedFlame res = renderer.renderFlame(info);
          imgPanel.setImage(res.getImage());
        }
        finally {
          flame.setSpatialFilterRadius(oldSpatialFilterRadius);
          flame.setSpatialOversample(oldSpatialOversample);
          flame.setColorOversample(oldColorOversample);
          flame.setSampleDensity(oldSampleDensity);
        }
      }
    }
    else {
      imgPanel.setImage(new SimpleImage(width, height));
    }
    flameRootPanel.repaint();
  }

  private RecordedFFT fft;

  public void play() {
    try {
      jLayer.stop();
      jLayer.play(soundFilename);
      startRender();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void stop() {
    try {
      jLayer.stop();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }

  }

  @Override
  public Flame getFlame() {
    return flame;
  }

  public void importFlame(Flame pFlame) {
    flame = pFlame;
    if (renderThread != null) {
      renderThread.notifyFlameChange(flame);
    }
    refreshFlameImage(flame, false);
  }

  public void startRender() {
    stopRender();
    renderThread = new RealtimeAnimRenderThread(this);
    renderThread.notifyFlameChange(flame);
    renderThread.setFFTData(fft);
    renderThread.setMusicPlayer(jLayer);
    renderThread.setFFTPanel(getGraph1Panel());
    new Thread(renderThread).start();
  }

  public void stopRender() {
    if (renderThread != null) {
      renderThread.setForceAbort(true);
      //        while (!renderThread.isDone()) {
      //          try {
      //            Thread.sleep(1);
      //          }
      //          catch (InterruptedException e) {
      //            e.printStackTrace();
      //          }
      //        }
      renderThread = null;
    }
  }

  public void randomFlame(Flame currFlame, String pRandomStyle) {
    final int IMG_WIDTH = 80;
    final int IMG_HEIGHT = 60;

    RandomFlameGenerator randGen = RandomFlameGeneratorList.getRandomFlameGeneratorInstance(pRandomStyle, true);
    int palettePoints = 3 + (int) (Math.random() * 68.0);
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, randGen, palettePoints);
    flame = sampler.createSample().getFlame();
    if (renderThread != null) {
      renderThread.notifyFlameChange(flame);
    }
    refreshFlameImage(flame, false);
  }

  public void loadSoundButton_clicked() {
    try {
      JFileChooser chooser = new SoundFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputSoundFilePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(flameRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastInputSoundFile(file);
        setSoundFilename(file.getAbsolutePath());
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void setSoundFilename(String pFilename) throws Exception {
    if (pFilename != null && pFilename.length() > 0) {
      soundFilename = pFilename;
      fft = jLayer.recordFFT(soundFilename);
    }
  }

}
