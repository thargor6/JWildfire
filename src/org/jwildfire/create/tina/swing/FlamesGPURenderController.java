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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.faclrender.FACLFlameWriter;
import org.jwildfire.create.tina.faclrender.FACLRenderResult;
import org.jwildfire.create.tina.faclrender.FACLRenderTools;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.io.ImageReader;
import org.jwildfire.io.ImageWriter;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;

public class FlamesGPURenderController {
  private enum State {
    RENDERING, IDLE
  }

  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  private final JButton loadFlameButton;
  private final JButton fromClipboardButton;
  private final JButton fromEditorButton;
  private final JButton toClipboardButton;
  private final JButton saveImageButton;
  private final JButton saveFlameButton;
  private final JButton toEditorButton;
  private final JToggleButton halveSizeButton;
  private final JToggleButton quarterSizeButton;
  private final JToggleButton fullSizeButton;
  private final JComboBox interactiveResolutionProfileCmb;
  private final JComboBox interactiveQualityProfileCmb;
  private final JPanel imageRootPanel;
  private final JPanel progressPanel;
  private JScrollPane imageScrollPane;
  private final JTextArea statsTextArea;
  private SimpleImage image;
  private Flame currFlame;
  private boolean refreshing = false;
  private final JLabel gpuRenderInfoLbl;
  private final Icon loaderIcon;
  private JLabel loaderLabel;
  private State state = State.IDLE;

  public FlamesGPURenderController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs,
      JButton pLoadFlameButton, JButton pFromClipboardButton, JButton pToClipboardButton, JButton pSaveImageButton,
      JButton pSaveFlameButton, JButton pToEditorButton, JPanel pImagePanel, JTextArea pStatsTextArea,
      JToggleButton pHalveSizeButton, JToggleButton pQuarterSizeButton, JToggleButton pFullSizeButton,
      JComboBox pInteractiveResolutionProfileCmb, JComboBox pInteractiveQualityProfileCmb, JLabel pGpuRenderInfoLbl,
      JPanel pProgressPanel, JButton pFromEditorButton) {

    parentCtrl = pParentCtrl;
    prefs = pPrefs;
    errorHandler = pErrorHandler;

    loadFlameButton = pLoadFlameButton;
    fromClipboardButton = pFromClipboardButton;
    toClipboardButton = pToClipboardButton;
    saveImageButton = pSaveImageButton;
    saveFlameButton = pSaveFlameButton;
    halveSizeButton = pHalveSizeButton;
    quarterSizeButton = pQuarterSizeButton;
    fullSizeButton = pFullSizeButton;
    toEditorButton = pToEditorButton;
    fromEditorButton = pFromEditorButton;
    gpuRenderInfoLbl = pGpuRenderInfoLbl;
    progressPanel = pProgressPanel;

    interactiveResolutionProfileCmb = pInteractiveResolutionProfileCmb;
    interactiveQualityProfileCmb = pInteractiveQualityProfileCmb;
    imageRootPanel = pImagePanel;
    // interactiveResolutionProfileCmb must be already filled here!
    refreshImagePanel();
    statsTextArea = pStatsTextArea;
    loaderIcon = new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/ajax-loader.gif"));
    enableControls();
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) interactiveResolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private QualityProfile getQualityProfile() {
    QualityProfile res = (QualityProfile) interactiveQualityProfileCmb.getSelectedItem();
    if (res == null) {
      res = new QualityProfile(false, "", 100, false, false);
    }
    return res;
  }

  private void refreshImagePanel() {
    if (imageScrollPane != null) {
      imageRootPanel.remove(imageScrollPane);
      imageScrollPane = null;
    }
    ResolutionProfile profile = getResolutionProfile();
    int width = profile.getWidth();
    int height = profile.getHeight();
    if (quarterSizeButton.isSelected()) {
      width /= 4;
      height /= 4;
    }
    else if (halveSizeButton.isSelected()) {
      width /= 2;
      height /= 2;
    }
    image = new SimpleImage(width, height);
    image.getBufferedImg().setAccelerationPriority(1.0f);
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    ImagePanel imagePanel = new ImagePanel(image, 0, 0, image.getImageWidth());
    imagePanel.setSize(image.getImageWidth(), image.getImageHeight());
    imagePanel.setPreferredSize(new Dimension(image.getImageWidth(), image.getImageHeight()));

    imageScrollPane = new JScrollPane(imagePanel);
    imageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    imageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    imageRootPanel.add(imageScrollPane, BorderLayout.CENTER);

    imageRootPanel.getParent().validate();
  }

  public void enableControls() {
    boolean isRendering = State.RENDERING.equals(state);
    fromClipboardButton.setEnabled(!isRendering);
    fromEditorButton.setEnabled(!isRendering);
    loadFlameButton.setEnabled(!isRendering);
    halveSizeButton.setEnabled(!isRendering);
    quarterSizeButton.setEnabled(!isRendering);
    fullSizeButton.setEnabled(!isRendering);
    interactiveResolutionProfileCmb.setEnabled(!isRendering);
    interactiveQualityProfileCmb.setEnabled(!isRendering);

    toClipboardButton.setEnabled(currFlame != null && !isRendering);
    saveFlameButton.setEnabled(currFlame != null && !isRendering);
    toEditorButton.setEnabled(currFlame != null && !isRendering);
    saveImageButton.setEnabled(image != null && !isRendering);
  }

  public void fromClipboardButton_clicked() {
    Flame newFlame = null;
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          List<Flame> flames = new FlameReader(prefs).readFlamesfromXML(xml);
          if (flames.size() > 0) {
            newFlame = flames.get(0);
          }
        }
      }
      if (newFlame == null) {
        throw new Exception("There is currently no valid flame in the clipboard");
      }
      else {
        currFlame = newFlame;
        enableControls();
        setupProfiles(currFlame);
        renderFlame();
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void setupProfiles(Flame pFlame) {
    if (prefs.isTinaAssociateProfilesWithFlames()) {
      if (pFlame.getResolutionProfile() != null) {
        ResolutionProfile profile = null;
        for (int i = 0; i < interactiveResolutionProfileCmb.getItemCount(); i++) {
          profile = (ResolutionProfile) interactiveResolutionProfileCmb.getItemAt(i);
          if (pFlame.getResolutionProfile().equals(profile.toString()))
            break;
          else
            profile = null;
        }
        if (profile != null) {
          interactiveResolutionProfileCmb.setSelectedItem(profile);
        }
      }
    }
  }

  public void loadFlameButton_clicked() {
    try {
      JFileChooser chooser = new FlameFileChooser(prefs);
      if (prefs.getInputFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputFlamePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(imageRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new FlameReader(prefs).readFlames(file.getAbsolutePath());
        Flame newFlame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        importFlame(newFlame);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlame(Flame flame) {
    currFlame = flame.makeCopy();
    enableControls();
    setupProfiles(currFlame);
    renderFlame();
    enableControls();
  }

  private void renderFlame() {
    if (getCurrFlame() == null) {
      return;
    }
    try {
      clearScreen();
      ResolutionProfile resProfile = getResolutionProfile();
      QualityProfile qualityProfile = getQualityProfile();
      int width = resProfile.getWidth();
      int height = resProfile.getHeight();
      if (quarterSizeButton.isSelected()) {
        width /= 4;
        height /= 4;
      }
      else if (halveSizeButton.isSelected()) {
        width /= 2;
        height /= 2;
      }
      setState(State.RENDERING);

      GPURenderThread renderThread = new GPURenderThread(width, height, qualityProfile.getQuality());
      new Thread(renderThread).start();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private class GPURenderThread implements Runnable {
    private final int width, height, quality;
    private Throwable error;
    private boolean failed;

    public GPURenderThread(int width, int height, int quality) {
      this.width = width;
      this.height = height;
      this.quality = quality;
    }

    @Override
    public void run() {
      failed = false;
      try {
        try {
          File tmpFile = File.createTempFile(System.currentTimeMillis() + "_" + Thread.currentThread().getId(), ".flam3");
          try {
            new FACLFlameWriter().writeFlame(getCurrFlame(), tmpFile.getAbsolutePath());
            long t0 = System.currentTimeMillis();
            FACLRenderResult renderResult = FACLRenderTools.invokeFACLRender(tmpFile.getAbsolutePath(), width, height, quality);
            long t1 = System.currentTimeMillis();
            try {
              if (renderResult.getReturnCode() == 0) {
                if (renderResult.getMessage() != null) {
                  statsTextArea.setText(renderResult.getMessage() + "\n");
                }
                SimpleImage img = new ImageReader().loadImage(renderResult.getOutputFilename());
                if (img.getImageWidth() == image.getImageWidth() && img.getImageHeight() == image.getImageHeight()) {
                  image.setBufferedImage(img.getBufferedImg(), img.getImageWidth(), img.getImageHeight());
                  imageRootPanel.repaint();
                  gpuRenderInfoLbl.setText("Elapsed: " + Tools.doubleToString((t1 - t0) / 1000.0) + "s");
                }
                else {
                  throw new Exception("Invalid image size <" + img.getImageWidth() + "x" + img.getImageHeight() + ">");
                }
              }
              else {
                statsTextArea.setText((renderResult.getMessage() != null ? renderResult.getMessage() : "") + "\n\n" + (renderResult.getCommand() != null ? renderResult.getCommand() : ""));
              }
            }
            finally {
              try {
                if (renderResult.getOutputFilename() != null && !renderResult.getOutputFilename().isEmpty()) {
                  File f = new File(renderResult.getOutputFilename());
                  if (f.exists()) {
                    if (!f.delete()) {
                      f.deleteOnExit();
                    }
                  }
                }
              }
              catch (Exception innerError) {
                innerError.printStackTrace();
              }
            }
          }
          finally {
            if (!tmpFile.delete()) {
              tmpFile.deleteOnExit();
            }
          }
        }
        catch (Throwable ex) {
          failed = true;
          error = ex;
        }
      }
      finally {
        setState(State.IDLE);
        if (failed) {
          statsTextArea.setText(Tools.getStacktrace(error));
        }
      }
    }
  }

  public void saveImageButton_clicked() {
    try {
      JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
      if (prefs.getOutputImagePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getOutputImagePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(imageRootPanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        new ImageWriter().saveImage(image, file.getAbsolutePath());
        if (prefs.isTinaSaveFlamesWhenImageIsSaved()) {
          new FlameWriter().writeFlame(getCurrFlame(), file.getParentFile().getAbsolutePath() + File.separator + Tools.trimFileExt(file.getName()) + ".flame");
        }
        //        if (autoLoadImageCBx.isSelected()) {
        parentCtrl.mainController.loadImage(file.getAbsolutePath(), false);
        //        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public Flame getCurrFlame() {
    return currFlame;
  }

  private void clearScreen() {
    try {
      int scrollX = (image.getImageWidth() - (int) imageRootPanel.getBounds().getWidth()) / 2;
      if (scrollX > 0)
        imageScrollPane.getHorizontalScrollBar().setValue(scrollX);
      int scrollY = (image.getImageHeight() - (int) imageRootPanel.getBounds().getHeight()) / 2;
      if (scrollY > 0)
        imageScrollPane.getVerticalScrollBar().setValue(scrollY);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    image.fillBackground(prefs.getTinaRandomBatchBGColorRed(), prefs.getTinaRandomBatchBGColorGreen(), prefs.getTinaRandomBatchBGColorBlue());
    imageRootPanel.repaint();
  }

  public void saveFlameButton_clicked() {
    try {
      Flame currFlame = getCurrFlame();
      if (currFlame != null) {
        JFileChooser chooser = new FlameFileChooser(prefs);
        if (prefs.getOutputFlamePath() != null) {
          try {
            chooser.setCurrentDirectory(new File(prefs.getOutputFlamePath()));
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (chooser.showSaveDialog(imageRootPanel) == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          new FlameWriter().writeFlame(currFlame, file.getAbsolutePath());
          prefs.setLastOutputFlameFile(file);
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void toClipboardButton_clicked() {
    try {
      Flame currFlame = getCurrFlame();
      if (currFlame != null) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String xml = new FlameWriter().getFlameXML(currFlame);
        StringSelection data = new StringSelection(xml);
        clipboard.setContents(data, data);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void halveRenderSizeButton_clicked() {
    refreshing = true;
    try {
      halveSizeButton.setSelected(true);
      quarterSizeButton.setSelected(false);
      fullSizeButton.setSelected(false);
    }
    finally {
      refreshing = false;
    }
    changeRenderSizeButton_clicked();
  }

  public void quarterRenderSizeButton_clicked() {
    refreshing = true;
    try {
      halveSizeButton.setSelected(false);
      quarterSizeButton.setSelected(true);
      fullSizeButton.setSelected(false);
    }
    finally {
      refreshing = false;
    }
    changeRenderSizeButton_clicked();
  }

  public void fullRenderSizeButton_clicked() {
    refreshing = true;
    try {
      halveSizeButton.setSelected(false);
      quarterSizeButton.setSelected(false);
      fullSizeButton.setSelected(true);
    }
    finally {
      refreshing = false;
    }
    changeRenderSizeButton_clicked();
  }

  public void changeRenderSizeButton_clicked() {
    if (!refreshing) {
      boolean oldRefreshing = refreshing;
      refreshing = true;
      try {
        refreshImagePanel();
        renderFlame();
        enableControls();
      }
      finally {
        refreshing = oldRefreshing;
      }
    }
  }

  public void resolutionProfile_changed() {
    if (!parentCtrl.cmbRefreshing) {
      // Nothing special here
      changeRenderSizeButton_clicked();
    }
  }

  public void fromEditorButton_clicked() {
    try {
      Flame newFlame = parentCtrl.exportFlame();
      if (newFlame != null) {
        currFlame = newFlame;
        enableControls();
        setupProfiles(currFlame);
        renderFlame();
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void toEditorButton_clicked() {
    try {
      Flame currFlame = getCurrFlame();
      if (currFlame != null) {
        parentCtrl.importFlame(currFlame, true);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void qualityProfile_changed() {
    if (!parentCtrl.cmbRefreshing) {
      // Nothing special here
      changeRenderSizeButton_clicked();
    }
  }

  private void setState(State pState) {
    state = pState;
    enableControls();
    if (state == State.RENDERING) {
      showLoaderLabel(true);
    }
    else {
      showLoaderLabel(false);
    }
  }

  private void showLoaderLabel(boolean pShow) {
    if (pShow) {
      if (loaderLabel != null) {
        progressPanel.remove(loaderLabel);
        loaderLabel = null;
      }
      loaderLabel = new JLabel(loaderIcon);
      progressPanel.add(loaderLabel, BorderLayout.CENTER);
    }
    else {
      if (loaderLabel != null) {
        progressPanel.remove(loaderLabel);
        loaderLabel = null;
      }
    }
    progressPanel.invalidate();
    progressPanel.validate();
    progressPanel.repaint();
  }

}
