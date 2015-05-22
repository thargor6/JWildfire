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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.leapmotion.LeapMotionEditorListenerRecorder.LeapMotionEditorEventWithFrame;
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
  private boolean doSave = false;

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
        tinaController.undoAction();
        applyMotionToFlame(recorder, tinaController.getCurrFlame());
        tinaController.refreshUI();
        if (doSave && StandardDialogs.confirm(rootPanel, "Do you want to save the recorded motion-data?")) {
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
              exportMotionToFile(chooser.getSelectedFile());
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

  private void applyMotionToFlame(LeapMotionEditorListenerRecorder pRecorder, Flame pFlame) {
    List<LeapMotionEditorEventWithFrame> dataList = pRecorder.getTransformedData();
    dataList = compress(dataList);
    if (dataList.size() > 1) {
      LeapMotionConnectedProperties config = pRecorder.getConfig();
      for (LeapMotionConnectedProperty property : config.getProperties()) {
        MotionCurve curve = property.getOutputChannel().getMotionCurve(property, pFlame);
        if (curve != null) {
          int frames[] = new int[dataList.size()];
          double amp[] = new double[dataList.size()];
          double ampMin = 0.0, ampMax = 0.0;
          for (int i = 0; i < dataList.size(); i++) {
            LeapMotionEditorEventWithFrame data = dataList.get(i);
            double recordedValue = property.getInputChannel().getValue(property.getLeapMotionHand(), data);
            double transformedValue = property.transformInputValue(recordedValue);
            frames[i] = Tools.FTOI(data.getFrame());
            amp[i] = transformedValue;
            if (amp[i] < ampMin) {
              ampMin = amp[i];
            }
            else if (amp[i] > ampMax) {
              ampMax = amp[i];
            }
          }
          curve.setPoints(frames, amp);
          curve.setViewXMin(-10);
          curve.setViewXMax(Tools.FTOI(dataList.get(dataList.size() - 1).getFrame()) + 10);
          curve.setViewYMin(ampMin + (ampMax - ampMin) / 10.0);
          curve.setViewYMax(ampMax + (ampMax - ampMin) / 10.0);
          curve.setEnabled(true);
        }
      }
    }
  }

  private List<LeapMotionEditorEventWithFrame> compress(List<LeapMotionEditorEventWithFrame> pInput) {
    Map<Integer, List<LeapMotionEditorEventWithFrame>> map = new HashMap<Integer, List<LeapMotionEditorEventWithFrame>>();
    for (LeapMotionEditorEventWithFrame event : pInput) {
      int frame = Tools.FTOI(event.getFrame());
      List<LeapMotionEditorEventWithFrame> lst = map.get(frame);
      if (lst == null) {
        lst = new ArrayList<LeapMotionEditorEventWithFrame>();
        map.put(frame, lst);
      }
      lst.add(event);
    }
    List<Integer> sortedFrames = new ArrayList<Integer>(map.keySet());
    Collections.sort(sortedFrames);

    List<LeapMotionEditorEventWithFrame> res = new ArrayList<LeapMotionEditorEventWithFrame>();
    for (Integer frame : sortedFrames) {
      LeapMotionEditorEventWithFrame compressed = new LeapMotionEditorEventWithFrame();
      compressed.setFrame(frame);
      int cnt = 0;
      for (LeapMotionEditorEventWithFrame src : map.get(frame)) {
        compressed.getLeftHand().setPosX(compressed.getLeftHand().getPosX() + src.getLeftHand().getPosX());
        compressed.getLeftHand().setPosY(compressed.getLeftHand().getPosY() + src.getLeftHand().getPosY());
        compressed.getLeftHand().setPosZ(compressed.getLeftHand().getPosZ() + src.getLeftHand().getPosZ());
        compressed.getLeftHand().setRoll(compressed.getLeftHand().getRoll() + src.getLeftHand().getRoll());
        compressed.getLeftHand().setPitch(compressed.getLeftHand().getPitch() + src.getLeftHand().getPitch());
        compressed.getLeftHand().setYaw(compressed.getLeftHand().getYaw() + src.getLeftHand().getYaw());
        compressed.getRightHand().setPosX(compressed.getRightHand().getPosX() + src.getRightHand().getPosX());
        compressed.getRightHand().setPosY(compressed.getRightHand().getPosY() + src.getRightHand().getPosY());
        compressed.getRightHand().setPosZ(compressed.getRightHand().getPosZ() + src.getRightHand().getPosZ());
        compressed.getRightHand().setRoll(compressed.getRightHand().getRoll() + src.getRightHand().getRoll());
        compressed.getRightHand().setPitch(compressed.getRightHand().getPitch() + src.getRightHand().getPitch());
        compressed.getRightHand().setYaw(compressed.getRightHand().getYaw() + src.getRightHand().getYaw());
        cnt++;
      }
      compressed.getLeftHand().setPosX(compressed.getLeftHand().getPosX() / (double) cnt);
      compressed.getLeftHand().setPosY(compressed.getLeftHand().getPosY() / (double) cnt);
      compressed.getLeftHand().setPosZ(compressed.getLeftHand().getPosZ() / (double) cnt);
      compressed.getLeftHand().setRoll(compressed.getLeftHand().getRoll() / (double) cnt);
      compressed.getLeftHand().setPitch(compressed.getLeftHand().getPitch() / (double) cnt);
      compressed.getLeftHand().setYaw(compressed.getLeftHand().getYaw() / (double) cnt);
      compressed.getRightHand().setPosX(compressed.getRightHand().getPosX() / (double) cnt);
      compressed.getRightHand().setPosY(compressed.getRightHand().getPosY() / (double) cnt);
      compressed.getRightHand().setPosZ(compressed.getRightHand().getPosZ() / (double) cnt);
      compressed.getRightHand().setRoll(compressed.getRightHand().getRoll() / (double) cnt);
      compressed.getRightHand().setPitch(compressed.getRightHand().getPitch() / (double) cnt);
      compressed.getRightHand().setYaw(compressed.getRightHand().getYaw() / (double) cnt);
      res.add(compressed);
    }
    return res;
  }

  private void exportMotionToFile(File pFile) throws Exception {
    Tools.writeUTF8Textfile(pFile.getAbsolutePath(), recorder.getDataAsString());
  }

  public void startLeapMotionListener(LeapMotionConnectedProperties pConfig, int pFps) {
    try {
      tinaController.saveUndoPoint();
      tinaController.countDown(3);
      if (leapMotionController == null) {
        leapMotionController = new Controller();
      }
      recorder = new LeapMotionEditorListenerRecorder(pConfig, pFps);
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
