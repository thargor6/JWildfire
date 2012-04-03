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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.randomflame.AllRandomFlameGenerator;
import org.jwildfire.create.tina.randomflame.RandomFlameGeneratorSampler;
import org.jwildfire.create.tina.render.FlameRenderThread;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.render.IterationInfo;
import org.jwildfire.create.tina.render.IterationObserver;
import org.jwildfire.create.tina.render.RenderInfo;
import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;

public class TinaInteractiveRendererController implements IterationObserver {
  private enum State {
    IDLE, RENDER
  }

  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  private final JButton randomButton;
  private final JButton fromClipboardButton;
  private final JButton loadFlameButton;
  private final JButton renderButton;
  private final JButton stopButton;
  private final JButton refreshButton;
  private final JButton saveImageButton;
  private final JPanel imageRootPanel;
  private JScrollPane imageScrollPane;
  private final JTextArea statsTextArea;
  private SimpleImage image;
  private Flame currFlame;
  private State state = State.IDLE;

  public TinaInteractiveRendererController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs,
      JButton pRandomButton, JButton pFromClipboardButton, JButton pLoadFlameButton, JButton pRenderButton,
      JButton pStopButton, JButton pRefreshButton, JButton pSaveImageButton, JPanel pImagePanel,
      JTextArea pStatsTextArea) {
    parentCtrl = pParentCtrl;
    prefs = pPrefs;
    errorHandler = pErrorHandler;
    randomButton = pRandomButton;
    fromClipboardButton = pFromClipboardButton;
    loadFlameButton = pLoadFlameButton;
    renderButton = pRenderButton;
    stopButton = pStopButton;
    refreshButton = pRefreshButton;
    saveImageButton = pSaveImageButton;
    imageRootPanel = pImagePanel;
    refreshImagePanel();
    statsTextArea = pStatsTextArea;
    state = State.IDLE;
  }

  private void refreshImagePanel() {
    if (imageScrollPane != null) {
      imageRootPanel.remove(imageScrollPane);
      imageScrollPane = null;
    }

    int width = prefs.getTinaRenderImageWidth();
    int height = prefs.getTinaRenderImageHeight();
    image = new SimpleImage(width, height);
    image.fillBackground(0, 0, 0);
    ImagePanel imagePanel = new ImagePanel(image, 0, 0, image.getImageWidth());
    imagePanel.setSize(image.getImageWidth(), image.getImageHeight());

    imageScrollPane = new JScrollPane(imagePanel);
    imageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    imageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    imageRootPanel.add(imageScrollPane, BorderLayout.CENTER);

    //    imageRootPanel.getParent().validate();
  }

  public void enableControls() {
    randomButton.setEnabled(state == State.IDLE);
    fromClipboardButton.setEnabled(state == State.IDLE);
    loadFlameButton.setEnabled(state == State.IDLE);
    renderButton.setEnabled(state == State.IDLE && currFlame != null);
    stopButton.setEnabled(state == State.RENDER);
    refreshButton.setEnabled(state == State.RENDER);
    saveImageButton.setEnabled(state == State.IDLE && image != null);
  }

  public void randomButton_clicked() {
    final int IMG_WIDTH = 80;
    final int IMG_HEIGHT = 60;
    final int PALETTE_SIZE = 11;
    RandomFlameGeneratorSampler sampler = new RandomFlameGeneratorSampler(IMG_WIDTH, IMG_HEIGHT, prefs, new AllRandomFlameGenerator(), false, false, PALETTE_SIZE);
    currFlame = sampler.createSample().getFlame();
    enableControls();
  }

  public void fromClipboardButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void loadFlameButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void renderButton_clicked() {
    int width = prefs.getTinaRenderImageWidth();
    int height = prefs.getTinaRenderImageHeight();
    RenderInfo info = new RenderInfo(width, height);
    Flame flame = getCurrFlame();
    double wScl = (double) info.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) info.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());
    flame.setWidth(info.getImageWidth());
    flame.setHeight(info.getImageHeight());
    boolean renderHDR = prefs.isTinaRenderNormalHDR();
    info.setRenderHDR(renderHDR);
    boolean renderHDRIntensityMap = prefs.isTinaRenderNormalHDRIntensityMap();
    info.setRenderHDRIntensityMap(renderHDRIntensityMap);
    double oldFilterRadius = flame.getSpatialFilterRadius();
    try {
      flame.setSpatialFilterRadius(prefs.getTinaRenderNormalSpatialOversample());
      FlameRenderer renderer = new FlameRenderer(flame, prefs);
      renderer.registerIterationObserver(this);
      List<FlameRenderThread> threads = renderer.startRenderFlame(info);

      //      RenderedFlame res = renderer.renderFlame(info);
      //      new ImageWriter().saveImage(res.getImage(), file.getAbsolutePath());
      //      if (res.getHDRImage() != null) {
      //        new ImageWriter().saveImage(res.getHDRImage(), file.getAbsolutePath() + ".hdr");
      //      }
      //      if (res.getHDRIntensityMap() != null) {
      //        new ImageWriter().saveImage(res.getHDRIntensityMap(), file.getAbsolutePath() + ".intensity.hdr");
      //      }
    }
    finally {
      flame.setSpatialFilterRadius(oldFilterRadius);
    }

    state = State.RENDER;
    enableControls();
  }

  public void stopButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void refreshButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void saveImageButton_clicked() {
    // TODO Auto-generated method stub

  }

  public Flame getCurrFlame() {
    return currFlame;
  }

  @Override
  public void notifyIterationFinished(IterationInfo pIterationInfo) {
    int x = pIterationInfo.getX();
    int y = pIterationInfo.getY();
    if (x >= 0 && x < image.getImageWidth() && y >= 0 && y < image.getImageHeight()) {
      Pixel toolPixel = new Pixel();
      toolPixel.r = 255;
      toolPixel.g = 155;
      toolPixel.b = 55;
      image.setARGB(x, y, toolPixel.getARGBValue());
      imageRootPanel.validate();
      imageRootPanel.repaint();
    }
  }

}
