/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.create.tina.animate.AnimationService.GlobalScript;
import org.jwildfire.create.tina.animate.AnimationService.XFormScript;
import org.jwildfire.create.tina.animate.SWFAnimationData;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThread;
import org.jwildfire.create.tina.animate.SWFAnimationRenderThreadController;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.SWFFileChooser;

public class TinaSWFAnimatorController implements SWFAnimationRenderThreadController {
  private SWFAnimationRenderThread renderThread = null;
  private final SWFAnimationData currAnimationData = new SWFAnimationData();
  private final TinaController parentCtrl;
  private final Prefs prefs;
  private final ErrorHandler errorHandler;
  private final JComboBox swfAnimatorGlobalScriptCmb;
  private final JComboBox swfAnimatorXFormScriptCmb;
  private final JTextField swfAnimatorFramesREd;
  private final JTextField swfAnimatorFramesPerSecondREd;
  private final JButton swfAnimatorGenerateButton;
  private final JTextArea swfAnimatorScriptArea;
  private final JComboBox swfAnimatorResolutionProfileCmb;
  private final JComboBox swfAnimatorQualityProfileCmb;
  private final JButton swfAnimatorCompileScriptButton;
  private final JButton swfAnimatorLoadFlameFromMainButton;
  private final JButton swfAnimatorLoadFlameFromClipboardButton;
  private final JButton swfAnimatorLoadFlameButton;
  private final JToggleButton swfAnimatorCustomScriptBtn;
  private final JToggleButton swfAnimatorHalveSizeButton;
  private final JProgressBar swfAnimatorProgressBar;
  private final JButton swfAnimatorCancelButton;
  private final JButton swfAnimatorLoadSoundButton;
  private final JButton swfAnimatorClearSoundButton;
  private final ProgressUpdater renderProgressUpdater;

  public TinaSWFAnimatorController(TinaController pParentCtrl, ErrorHandler pErrorHandler, Prefs pPrefs, JComboBox pSWFAnimatorGlobalScriptCmb,
      JComboBox pSWFAnimatorXFormScriptCmb, JTextField pSWFAnimatorFramesREd, JTextField pSWFAnimatorFramesPerSecondREd,
      JButton pSWFAnimatorGenerateButton, JTextArea pSWFAnimatorScriptArea, JComboBox pSWFAnimatorResolutionProfileCmb,
      JComboBox pSWFAnimatorQualityProfileCmb, JButton pSWFAnimatorCompileScriptButton, JButton pSWFAnimatorLoadFlameFromMainButton,
      JButton pSWFAnimatorLoadFlameFromClipboardButton, JButton pSWFAnimatorLoadFlameButton, JToggleButton pSWFAnimatorCustomScriptBtn,
      JToggleButton pSWFAnimatorHalveSizeButton, JProgressBar pSWFAnimatorProgressBar, JButton pSWFAnimatorCancelButton,
      JButton pSWFAnimatorLoadSoundButton, JButton pSWFAnimatorClearSoundButton, ProgressUpdater pRenderProgressUpdater) {
    parentCtrl = pParentCtrl;
    prefs = pPrefs;
    errorHandler = pErrorHandler;
    swfAnimatorGlobalScriptCmb = pSWFAnimatorGlobalScriptCmb;
    swfAnimatorXFormScriptCmb = pSWFAnimatorXFormScriptCmb;
    swfAnimatorFramesREd = pSWFAnimatorFramesREd;
    swfAnimatorFramesPerSecondREd = pSWFAnimatorFramesPerSecondREd;
    swfAnimatorGenerateButton = pSWFAnimatorGenerateButton;
    swfAnimatorScriptArea = pSWFAnimatorScriptArea;
    swfAnimatorResolutionProfileCmb = pSWFAnimatorResolutionProfileCmb;
    swfAnimatorQualityProfileCmb = pSWFAnimatorQualityProfileCmb;
    swfAnimatorCompileScriptButton = pSWFAnimatorCompileScriptButton;
    swfAnimatorLoadFlameFromMainButton = pSWFAnimatorLoadFlameFromMainButton;
    swfAnimatorLoadFlameFromClipboardButton = pSWFAnimatorLoadFlameFromClipboardButton;
    swfAnimatorLoadFlameButton = pSWFAnimatorLoadFlameButton;
    swfAnimatorCustomScriptBtn = pSWFAnimatorCustomScriptBtn;
    swfAnimatorHalveSizeButton = pSWFAnimatorHalveSizeButton;
    swfAnimatorProgressBar = pSWFAnimatorProgressBar;
    swfAnimatorCancelButton = pSWFAnimatorCancelButton;
    swfAnimatorLoadSoundButton = pSWFAnimatorLoadSoundButton;
    swfAnimatorClearSoundButton = pSWFAnimatorClearSoundButton;
    renderProgressUpdater = pRenderProgressUpdater;
    enableControls();
  }

  protected void enableControls() {
    // TODO Auto-generated method stub

  }

  public void loadFlameFromMainButton_clicked() {
    try {
      Flame newFlame = parentCtrl.exportFlame();
      if (newFlame != null) {
        currAnimationData.setFlame1(newFlame);
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void loadFlameFromClipboardButton_clicked() {
    Flame newFlame = null;
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String xml = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          List<Flame> flames = new Flam3Reader().readFlamesfromXML(xml);
          if (flames.size() > 0) {
            newFlame = flames.get(0);
          }
        }
      }
      if (newFlame == null) {
        throw new Exception("There is currently no valid flame in the clipboard");
      }
      else {
        currAnimationData.setFlame1(newFlame);
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
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
      if (chooser.showOpenDialog(swfAnimatorScriptArea) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        List<Flame> flames = new Flam3Reader().readFlames(file.getAbsolutePath());
        Flame newFlame = flames.get(0);
        prefs.setLastInputFlameFile(file);
        currAnimationData.setFlame1(newFlame);
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void customScriptButton_clicked() {
    enableControls();
  }

  public void compileScriptButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void cancelButton_clicked() {
    if (renderThread != null) {
      renderThread.setCancelSignalled(true);
    }
  }

  public void generateButton_clicked() {
    try {
      JFileChooser chooser = new SWFFileChooser();
      if (prefs.getSwfPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getSwfPath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showSaveDialog(swfAnimatorScriptArea) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastOutputSwfFile(file);
        int frameCount = Integer.parseInt(swfAnimatorFramesREd.getText());
        double framesPerSecond = Double.parseDouble(swfAnimatorFramesPerSecondREd.getText());
        ResolutionProfile resProfile = getResolutionProfile();
        QualityProfile qualityProfile = getQualityProfile();
        int frameWidth = resProfile.getWidth();
        int frameHeight = resProfile.getHeight();
        if (swfAnimatorHalveSizeButton.isSelected()) {
          frameWidth /= 2;
          frameHeight /= 2;
        }
        GlobalScript globalScript = (GlobalScript) swfAnimatorGlobalScriptCmb.getSelectedItem();
        XFormScript xFormScript = (XFormScript) swfAnimatorXFormScriptCmb.getSelectedItem();
        currAnimationData.setGlobalScript(globalScript);
        currAnimationData.setxFormScript(xFormScript);
        currAnimationData.setQualityProfile(qualityProfile);

        renderThread = new SWFAnimationRenderThread(this, frameCount, frameWidth, frameHeight, framesPerSecond, currAnimationData, file.getAbsolutePath());
        try {
          enableControls();
          new Thread(renderThread).start();
        }
        catch (Throwable ex) {
          renderThread = null;
          throw ex;
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
      enableControls();
    }
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
      if (chooser.showOpenDialog(swfAnimatorScriptArea) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastInputSoundFile(file);
        currAnimationData.setSoundFilename(file.getAbsolutePath());
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void clearSoundButton_clicked() {
    currAnimationData.setSoundFilename(null);
    enableControls();
  }

  @Override
  public void onRenderFinished() {
    renderThread = null;
    enableControls();
  }

  @Override
  public JProgressBar getProgressBar() {
    return swfAnimatorProgressBar;
  }

  @Override
  public ProgressUpdater getProgressUpdater() {
    return renderProgressUpdater;
  }

  private ResolutionProfile getResolutionProfile() {
    ResolutionProfile res = (ResolutionProfile) swfAnimatorResolutionProfileCmb.getSelectedItem();
    if (res == null) {
      res = new ResolutionProfile(false, 800, 600);
    }
    return res;
  }

  private QualityProfile getQualityProfile() {
    QualityProfile res = (QualityProfile) swfAnimatorQualityProfileCmb.getSelectedItem();
    if (res == null) {
      res = new QualityProfile(false, "default", 1, 1, 0, true, false);
    }
    return res;
  }

  @Override
  public Prefs getPrefs() {
    return prefs;
  }

}
