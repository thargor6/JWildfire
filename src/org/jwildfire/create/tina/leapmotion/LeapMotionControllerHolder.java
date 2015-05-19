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
package org.jwildfire.create.tina.leapmotion;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.swing.StandardDialogs;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.TxtFileChooser;
import org.jwildfire.swing.ErrorHandler;

import com.leapmotion.leap.Controller;

public class LeapMotionControllerHolder {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final JPanel rootPanel;
  private Controller leapMotionController = null;
  private LeapMotionEditorListener listener = null;
  private LeapMotionEditorListenerThread leapMotionEditorListenerThread = null;
  private LeapMotionEditorListenerRecorder recorder;

  public LeapMotionControllerHolder(TinaController pTinaController, ErrorHandler pErrorHandler, JPanel pRootPanel) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    rootPanel = pRootPanel;
  }

  public void stopLeapMotionListener() {
    try {
      try {
        if (leapMotionController != null && listener != null) {
          leapMotionController.removeListener(listener);
        }
      }
      finally {
        listener = null;
      }
      if (leapMotionEditorListenerThread != null) {
        leapMotionEditorListenerThread.signalCancel();
        leapMotionEditorListenerThread = null;
      }
      if (recorder != null && !recorder.isEmpty()) {
        if (StandardDialogs.confirm(rootPanel, "Do you want to save the recorded motion-data?")) {
          Prefs prefs = Prefs.getPrefs();
          JFileChooser chooser = new TxtFileChooser(prefs);
          if (prefs.getTinaRawMotionDataPath() != null) {
            try {
              chooser.setCurrentDirectory(new File(prefs.getTinaRawMotionDataPath()));
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
          else if (prefs.getTinaFlamePath() != null) {
            try {
              chooser.setCurrentDirectory(new File(prefs.getTinaFlamePath()));
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
          if (chooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
            try {
              exportMotion(chooser.getSelectedFile());
            }
            catch (Exception ex) {
              errorHandler.handleError(ex);
            }
          }
        }
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void exportMotion(File pFile) throws Exception {
    Tools.writeUTF8Textfile(pFile.getAbsolutePath(), recorder.getDataAsString());
  }

  public void startLeapMotionListener(LeapMotionConnectedProperties pConfig, int pFps) {
    try {
      tinaController.saveUndoPoint();
      if (leapMotionController == null) {
        leapMotionController = new Controller();
      }
      recorder = new LeapMotionEditorListenerRecorder(pFps);
      leapMotionEditorListenerThread = new LeapMotionEditorListenerThread(pConfig, tinaController);
      listener = new LeapMotionEditorListener(leapMotionEditorListenerThread, recorder);
      new Thread(leapMotionEditorListenerThread).start();
      leapMotionController.addListener(listener);
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

}
