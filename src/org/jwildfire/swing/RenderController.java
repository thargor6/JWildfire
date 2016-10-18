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
package org.jwildfire.swing;

import java.awt.BorderLayout;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jwildfire.image.SimpleImage;
import org.jwildfire.script.ActionList;


public class RenderController implements RenderProgressReporter {
  private final ErrorHandler errorHandler;
  private final JDesktopPane desktop;
  private final JDialog renderDialog;
  private final JButton renderCancelButton;
  private final JButton renderRenderButton;
  private final JButton renderCloseButton;
  private final JTextField renderPrefixREd;
  private final JTextField renderOutputREd;
  private final JLabel renderFrameValueLabel;
  private final JLabel renderSizeValueLabel;
  private final JProgressBar renderProgressBar;
  private final List<PreviewPanel> previewPanels;
  private final JTextField frameStartREd;
  private final JTextField frameEndREd;

  private ActionList actionList;

  private RenderThread renderThread = null;

  private int frames;

  public RenderController(ErrorHandler pErrorHandler, JDesktopPane pDesktop,
      JDialog pRenderDialog, JButton pRenderCancelButton, JButton pRenderRenderButton,
      JButton pRenderCloseButton, JTextField pRenderPrefixREd, JTextField pRenderOutputREd,
      JLabel pRenderFrameValueLabel, JLabel pRenderSizeValueLabel, JProgressBar pRenderProgressBar,
      JPanel pRenderPreviewImg1Panel, JLabel pRenderPreviewImg1Label,
      JPanel pRenderPreviewImg2Panel, JLabel pRenderPreviewImg2Label,
      JPanel pRenderPreviewImg3Panel, JLabel pRenderPreviewImg3Label, JTextField pFrameStartREd,
      JTextField pFrameEndREd) {
    errorHandler = pErrorHandler;
    desktop = pDesktop;
    renderDialog = pRenderDialog;
    renderCancelButton = pRenderCancelButton;
    renderRenderButton = pRenderRenderButton;
    renderCloseButton = pRenderCloseButton;
    renderPrefixREd = pRenderPrefixREd;
    renderOutputREd = pRenderOutputREd;
    renderFrameValueLabel = pRenderFrameValueLabel;
    renderSizeValueLabel = pRenderSizeValueLabel;
    renderProgressBar = pRenderProgressBar;
    previewPanels = new ArrayList<PreviewPanel>();
    previewPanels.add(new PreviewPanel(pRenderPreviewImg1Label, pRenderPreviewImg1Panel));
    previewPanels.add(new PreviewPanel(pRenderPreviewImg2Label, pRenderPreviewImg2Panel));
    previewPanels.add(new PreviewPanel(pRenderPreviewImg3Label, pRenderPreviewImg3Panel));
    frameStartREd = pFrameStartREd;
    frameEndREd = pFrameEndREd;
  }

  public void showRenderDialog(int pFrame, int pFrames) {
    Point dPos = desktop.getRootPane().getLocation();
    int dWidth = desktop.getWidth();
    int dHeight = desktop.getHeight();
    int wWidth = renderDialog.getWidth();
    int wHeight = renderDialog.getHeight();
    renderDialog.setLocation(dPos.x + (dWidth - wWidth) / 2, dPos.y + (dHeight - wHeight) / 2);
    frameStartREd.setText(String.valueOf(1));
    frameEndREd.setText(String.valueOf(pFrames));
    frames = pFrames;
    initControls(1, pFrames);
    enableControls(false);
    renderDialog.setVisible(true);
  }

  private static class PreviewPanel {
    private final JLabel label;
    private String caption;
    private final JPanel panel;
    private ImagePanel imagePanel;
    private SimpleImage img;

    public PreviewPanel(JLabel pLabel, JPanel pPanel) {
      label = pLabel;
      panel = pPanel;
    }

    public String getCaption() {
      return caption;
    }

    public void setCaption(String caption) {
      this.caption = caption;
    }

    public ImagePanel getImagePanel() {
      return imagePanel;
    }

    public void setImagePanel(ImagePanel imagePanel) {
      this.imagePanel = imagePanel;
    }

    public JLabel getLabel() {
      return label;
    }

    public JPanel getPanel() {
      return panel;
    }

    public SimpleImage getImg() {
      return img;
    }

    public void setImg(SimpleImage img) {
      this.img = img;
    }
  }

  public void render() throws Exception {
    if (renderThread != null)
      return;
    for (PreviewPanel panel : previewPanels) {
      if (panel.getImagePanel() != null) {
        panel.getPanel().remove(panel.getImagePanel());
        panel.setImagePanel(null);
        panel.setCaption(null);
      }
    }
    enableControls(true);
    String basePath = renderOutputREd.getText();
    if ((basePath != null) && (basePath.length() > 0)
        && (basePath.charAt(basePath.length() - 1) != '\\')
        && (basePath.charAt(basePath.length() - 1) != '/'))
      basePath += File.separatorChar;
    String prefix = renderPrefixREd.getText();
    if ((prefix != null) && (prefix.length() > 0))
      basePath += prefix;

    int startFrame = Integer.parseInt(frameStartREd.getText());
    if (startFrame < 1) {
      startFrame = 1;
      frameStartREd.setText(String.valueOf(startFrame));
    }

    int endFrame = Integer.parseInt(frameEndREd.getText());
    if (endFrame < startFrame)
      endFrame = startFrame;
    if (endFrame < frames)
      endFrame = frames;

    renderThread = new RenderThread(RenderThread.Mode.BATCH, desktop, this, actionList, startFrame,
        endFrame, basePath);
    (new Thread(renderThread)).start();
  }

  public SimpleImage renderFrame(int pFrame) throws Throwable {
    RenderProgressReporter reporter = new RenderProgressReporter() {
      @Override
      public void showProgress(int pFrame, String pName, SimpleImage pImg) {
      }

      @Override
      public void renderingFinished() {
      }
    };
    try {
      renderThread = new RenderThread(RenderThread.Mode.SINGLE_FRAME, desktop, reporter,
          actionList, pFrame, pFrame, null);
      renderThread.run();
      if (renderThread.getError() != null)
        throw renderThread.getError();
      return renderThread.getLastImage();
    }
    finally {
      renderThread = null;
    }
  }

  @Override
  public void showProgress(int pFrame, String pName, SimpleImage pImg) {
    try {
      renderProgressBar.setValue(pFrame);
      renderFrameValueLabel.setText(pFrame + " / " + frames);
      renderSizeValueLabel.setText(pImg.getImageWidth() + " x " + pImg.getImageHeight());

      for (int i = 0; i < previewPanels.size() - 1; i++) {
        PreviewPanel panel = previewPanels.get(i);
        PreviewPanel nextPanel = previewPanels.get(i + 1);
        panel.setCaption(nextPanel.getCaption());
        panel.getLabel().setText(panel.getCaption());
        if (panel.getImagePanel() != null) {
          panel.getPanel().remove(panel.getImagePanel());
          panel.setImagePanel(null);
        }
        panel.setImg(nextPanel.getImg());
        if (panel.getImg() != null) {
          ImagePanel imagePanel = new ImagePanel(panel.getImg(), 0, 0, panel.getPanel().getWidth());
          panel.setImagePanel(imagePanel);
          if (panel.getImagePanel() != null)
            panel.getPanel().add(panel.getImagePanel(), BorderLayout.CENTER);
        }
      }

      {
        PreviewPanel panel = previewPanels.get(previewPanels.size() - 1);
        panel.setCaption(pName);
        panel.getLabel().setText(panel.getCaption());
        if (panel.getImagePanel() != null) {
          panel.getPanel().remove(panel.getImagePanel());
          panel.setImagePanel(null);
        }
        panel.setImg(pImg.clone());
        ImagePanel imagePanel = new ImagePanel(panel.getImg(), 0, 0, panel.getPanel().getWidth());
        panel.setImagePanel(imagePanel);
        panel.getPanel().add(panel.getImagePanel(), BorderLayout.CENTER);
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void renderingFinished() {
    try {
      if (renderThread == null)
        return;
      if (renderThread.getError() != null) {
        errorHandler.handleError(renderThread.getError());
      }
    }
    finally {
      renderThread = null;
      enableControls(false);
    }
  }

  private void enableControls(boolean pRendering) {
    renderDialog.setDefaultCloseOperation(pRendering ? WindowConstants.DO_NOTHING_ON_CLOSE
        : WindowConstants.HIDE_ON_CLOSE);
    renderRenderButton.setEnabled(!pRendering);
    renderCancelButton.setEnabled(pRendering);
    renderCloseButton.setEnabled(!pRendering);
    renderPrefixREd.setEnabled(!pRendering);
    renderOutputREd.setEnabled(!pRendering);
  }

  public void close() {
    if (renderThread == null)
      renderDialog.setVisible(false);
  }

  public void cancel() {
    if ((renderThread == null) || (renderThread.isForceAbort()))
      return;
    renderThread.setForceAbort(true);
  }

  private void initControls(int pFrameStart, int pFrameEnd) {
    int frames = pFrameEnd - pFrameStart + 1;
    renderFrameValueLabel.setText(1 + "/" + frames);
    renderProgressBar.setMinimum(1);
    renderProgressBar.setMaximum(frames);
    renderProgressBar.setValue(1);
    renderSizeValueLabel.setText("n/a");
    for (PreviewPanel panel : previewPanels) {
      panel.getLabel().setText("");
    }
  }

  public void frameEndChanged() {
    int frameStart = Integer.parseInt(frameStartREd.getText());
    int frameEnd = Integer.parseInt(frameEndREd.getText());
    if (frameEnd < frameStart) {
      frameEnd = frameStart;
    }
    else if (frameEnd > frames) {
      frameEnd = frames;
    }
    frameEndREd.setText(String.valueOf(frameEnd));
    initControls(frameStart, frameEnd);
  }

  public void frameStartChanged() {
    int frameEnd = Integer.parseInt(frameEndREd.getText());
    int frameStart = Integer.parseInt(frameStartREd.getText());
    if (frameStart > frameEnd) {
      frameStart = frameEnd;
    }
    else if (frameStart < 1) {
      frameStart = 1;
    }
    frameStartREd.setText(String.valueOf(frameStart));
    initControls(frameStart, frameEnd);
  }

  public void setActionList(ActionList actionList) {
    this.actionList = actionList;
  }
}
