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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.FlameReader;
import org.jwildfire.create.tina.io.FlameWriter;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImageFileChooser;
import org.jwildfire.swing.ImagePanel;

public class FlamesGPURenderController {
  private enum State {
    IDLE, RENDER
  }

  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  private final JButton loadFlameButton;
  private final JButton fromClipboardButton;
  private final JButton stopButton;
  private final JButton toClipboardButton;
  private final JButton saveImageButton;
  private final JButton saveFlameButton;
  private final JToggleButton halveSizeButton;
  private final JToggleButton quarterSizeButton;
  private final JToggleButton fullSizeButton;
  private final JComboBox interactiveResolutionProfileCmb;
  private final JPanel imageRootPanel;
  private JScrollPane imageScrollPane;
  private final JTextArea statsTextArea;
  private final JToggleButton showStatsButton;
  private final JToggleButton showPreviewButton;
  private SimpleImage image;
  private Flame currFlame;
  private State state = State.IDLE;
  private boolean refreshing = false;

  public FlamesGPURenderController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs,
      JButton pLoadFlameButton, JButton pFromClipboardButton,
      JButton pStopButton, JButton pToClipboardButton, JButton pSaveImageButton,
      JButton pSaveFlameButton,
      JPanel pImagePanel, JTextArea pStatsTextArea, JToggleButton pHalveSizeButton,
      JToggleButton pQuarterSizeButton, JToggleButton pFullSizeButton, JComboBox pInteractiveResolutionProfileCmb,
      JToggleButton pShowStatsButton, JToggleButton pShowPreviewButton) {
    parentCtrl = pParentCtrl;
    prefs = pPrefs;
    errorHandler = pErrorHandler;

    loadFlameButton = pLoadFlameButton;
    fromClipboardButton = pFromClipboardButton;
    stopButton = pStopButton;
    toClipboardButton = pToClipboardButton;
    saveImageButton = pSaveImageButton;
    saveFlameButton = pSaveFlameButton;
    halveSizeButton = pHalveSizeButton;
    quarterSizeButton = pQuarterSizeButton;
    fullSizeButton = pFullSizeButton;

    interactiveResolutionProfileCmb = pInteractiveResolutionProfileCmb;
    imageRootPanel = pImagePanel;
    showStatsButton = pShowStatsButton;
    showPreviewButton = pShowPreviewButton;
    // interactiveResolutionProfileCmb must be already filled here!
    refreshImagePanel();
    statsTextArea = pStatsTextArea;
    state = State.IDLE;
    //genRandomFlame();
    enableControls();
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) interactiveResolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
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
    saveImageButton.setEnabled(image != null);
    stopButton.setEnabled(state == State.RENDER);
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
        setupProfiles(currFlame);
        renderButton_clicked();
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
        currFlame = newFlame;
        importFlame(newFlame);
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importFlame(Flame flame) {
    currFlame = flame.makeCopy();
    setupProfiles(currFlame);
    renderButton_clicked();
    enableControls();
  }

  public void renderButton_clicked() {
    try {
      clearScreen();
      ResolutionProfile resProfile = getResolutionProfile();
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

      state = State.RENDER;
      enableControls();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void stopButton_clicked() {
    enableControls();
  }

  public boolean isRendering() {
    return state == State.RENDER;
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
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public Flame getCurrFlame() {
    return currFlame;
  }

  private long renderStartTime = 0;
  private long pausedRenderTime = 0;
  private boolean showStats = true;
  private final DateFormat timeFormat = createTimeFormat();

  private DateFormat createTimeFormat() {
    DateFormat res = new SimpleDateFormat("HH:mm:ss");
    res.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
    return res;
  }

  private void updateStats(double pQuality) {
    if (showStats) {
      StringBuilder sb = new StringBuilder();
      long currTime = System.currentTimeMillis();
      long renderTime = currTime - renderStartTime + pausedRenderTime;
      sb.append("Elapsed time: " + timeFormat.format(new Date(renderTime)));
      statsTextArea.setText(sb.toString());
      statsTextArea.validate();
    }
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
        boolean rendering = state == State.RENDER;
        if (rendering) {
          stopButton_clicked();
        }
        refreshImagePanel();
        enableControls();
        if (rendering) {
          renderButton_clicked();
        }
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
        setupProfiles(currFlame);
        renderButton_clicked();
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

  public void showStatsBtn_changed() {
    showStats = showStatsButton.isSelected();
  }

  public void showPreviewBtn_changed() {
    boolean showPreview = showPreviewButton.isSelected();
  }

}
