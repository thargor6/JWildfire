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
package org.jwildfire.create.tina;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.MotionCurveEditor;
import org.jwildfire.create.tina.swing.RenderMainFlameThreadFinishEvent;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.swing.ErrorHandler;

public class AnimationController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JToggleButton motionCurveEditModeButton;
  private final JWFNumberField keyframesFrameField;
  private final JSlider keyframesFrameSlider;
  private final JWFNumberField keyframesFrameCountField;
  private final List<MotionCurveEditor> motionPropertyControls = new ArrayList<MotionCurveEditor>();
  private final JPanel frameSliderPanel;
  private final JLabel keyframesFrameLbl;
  private final JLabel keyframesFrameCountLbl;
  private final JPanel motionBlurPanel;
  private final JButton motionCurvePlayPreviewButton;
  private final JWFNumberField flameFPSField;

  public AnimationController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel,
      JWFNumberField pKeyframesFrameField, JSlider pKeyframesFrameSlider, JWFNumberField pKeyframesFrameCountField,
      JPanel pFrameSliderPanel, JLabel pKeyframesFrameLbl, JLabel pKeyframesFrameCountLbl, JToggleButton pMotionCurveEditModeButton,
      JPanel pMotionBlurPanel, JButton pMotionCurvePlayPreviewButton, JWFNumberField pFlameFPSField) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    keyframesFrameField = pKeyframesFrameField;
    keyframesFrameSlider = pKeyframesFrameSlider;
    keyframesFrameCountField = pKeyframesFrameCountField;
    frameSliderPanel = pFrameSliderPanel;
    keyframesFrameLbl = pKeyframesFrameLbl;
    keyframesFrameCountLbl = pKeyframesFrameCountLbl;
    motionCurveEditModeButton = pMotionCurveEditModeButton;
    motionBlurPanel = pMotionBlurPanel;
    motionCurvePlayPreviewButton = pMotionCurvePlayPreviewButton;
    flameFPSField = pFlameFPSField;
    enableControls();
  }

  public int getCurrFrame() {
    Integer value = keyframesFrameField.getIntValue();
    return (value != null ? value.intValue() : -1);
  }

  public void enableControls() {
    boolean enabled = motionCurveEditModeButton.isSelected();
    for (MotionCurveEditor component : motionPropertyControls) {
      component.setWithMotionCurve(enabled);
    }
    frameSliderPanel.setPreferredSize(new Dimension(0, (enabled ? 28 : 4)));
    keyframesFrameField.setVisible(enabled);
    keyframesFrameSlider.setVisible(enabled);
    keyframesFrameCountField.setVisible(enabled);
    keyframesFrameLbl.setVisible(enabled);
    keyframesFrameCountLbl.setVisible(enabled);
    motionCurvePlayPreviewButton.setVisible(enabled);
  }

  private void adjustFrameControls(int frame) {
    if (keyframesFrameField.getMinValue() >= frame) {
      keyframesFrameField.setMinValue(frame);
    }
    if (keyframesFrameField.getMaxValue() <= frame) {
      keyframesFrameField.setMaxValue(frame);
    }
    keyframesFrameField.setValue(frame);

    if (keyframesFrameCountField.getMaxValue() <= frame) {
      keyframesFrameCountField.setMaxValue(frame);
    }
    if (keyframesFrameCountField.getIntValue() == null || keyframesFrameCountField.getIntValue() <= frame) {
      keyframesFrameCountField.setValue(frame);
    }

    if (keyframesFrameSlider.getMinimum() > frame) {
      keyframesFrameSlider.setMinimum(frame);
    }
    if (keyframesFrameSlider.getMaximum() < frame) {
      keyframesFrameSlider.setMaximum(frame);
    }
    keyframesFrameSlider.setValue(frame);
  }

  public void keyFrameFieldChanged() {
    if (!tinaController.isNoRefresh()) {
      boolean oldNoRefresh = tinaController.isNoRefresh();
      try {
        tinaController.setNoRefresh(true);
        int frame = keyframesFrameField.getIntValue() != null ? keyframesFrameField.getIntValue().intValue() : -1;
        adjustFrameControls(frame);
        if (tinaController.getCurrFlame() != null) {
          tinaController.getCurrFlame().setFrame(frame);
        }
        tinaController.refreshFlameImage(true, false, 1, true);
      }
      finally {
        tinaController.setNoRefresh(oldNoRefresh);
      }
    }
  }

  public void keyFrameSliderChanged() {
    if (!tinaController.isNoRefresh()) {
      boolean oldNoRefresh = tinaController.isNoRefresh();
      try {
        tinaController.setNoRefresh(true);
        int frame = keyframesFrameSlider.getValue();
        if (playPreviewThread == null) {
          adjustFrameControls(frame);
        }
        if (tinaController.getCurrFlame() != null) {
          tinaController.getCurrFlame().setFrame(frame);
        }
        if (playPreviewThread != null) {
          FlamePanelConfig cfg = tinaController.getFlamePanelConfig();
          boolean oldNoControls = cfg.isNoControls();
          try {
            cfg.setNoControls(true);
            tinaController.refreshFlameImage(true, true, 1, true);
          }
          finally {
            cfg.setNoControls(oldNoControls);
          }
        }
        else {
          tinaController.refreshFlameImage(true, false, 1, true);
        }

      }
      finally {
        tinaController.setNoRefresh(oldNoRefresh);
      }
    }
  }

  public void refreshUI() {
    // TODO
  }

  public void registerMotionPropertyControls(MotionCurveEditor pComponent) {
    if (!motionPropertyControls.contains(pComponent)) {
      motionPropertyControls.add(pComponent);
    }
  }

  public void toggleMotionCurveEditing() {
    enableControls();
  }

  public void keyFrameCountFieldChanged() {
    if (!tinaController.isNoRefresh()) {
      boolean oldNoRefresh = tinaController.isNoRefresh();
      try {
        tinaController.setNoRefresh(true);
        int frameCount = keyframesFrameCountField.getIntValue();
        if (tinaController.getCurrFlame() != null) {
          tinaController.getCurrFlame().setFrameCount(frameCount);
        }
      }
      finally {
        tinaController.setNoRefresh(oldNoRefresh);
      }
    }
  }

  public class PlayPreviewThread implements Runnable {
    private boolean finished;
    private boolean forceAbort;
    private final RenderMainFlameThreadFinishEvent finishEvent;

    public PlayPreviewThread(RenderMainFlameThreadFinishEvent pFinishEvent) {
      finishEvent = pFinishEvent;
    }

    @Override
    public void run() {
      finished = forceAbort = false;
      try {
        int frameCount = keyframesFrameCountField.getIntValue();
        long t0 = System.currentTimeMillis();
        for (int i = 1; i <= frameCount; i++) {
          if (forceAbort) {
            break;
          }
          long f0 = System.currentTimeMillis();
          keyframesFrameSlider.setValue(i);
          while (true) {
            long f1 = System.currentTimeMillis();
            if (f1 - f0 > 40) {
              break;
            }
            Thread.sleep(1);
          }
        }
        long t1 = System.currentTimeMillis();
        finished = true;
        finishEvent.succeeded((t1 - t0) * 0.001);
      }
      catch (Throwable ex) {
        finished = true;
        finishEvent.failed(ex);
      }
    }

    public boolean isFinished() {
      return finished;
    }

    public void setForceAbort() {
      forceAbort = true;
    }

  }

  private PlayPreviewThread playPreviewThread = null;

  private void enablePlayPreviewControls() {
    motionCurvePlayPreviewButton.setText(playPreviewThread == null ? "Play" : "Cancel");
  }

  public void playPreviewButtonClicked() {
    if (playPreviewThread != null) {
      playPreviewThread.setForceAbort();
      while (playPreviewThread.isFinished()) {
        try {
          Thread.sleep(10);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      playPreviewThread = null;
      enablePlayPreviewControls();
    }
    else {
      final int oldFrame = keyframesFrameField.getIntValue();

      RenderMainFlameThreadFinishEvent finishEvent = new RenderMainFlameThreadFinishEvent() {

        @Override
        public void succeeded(double pElapsedTime) {
          try {
          }
          catch (Throwable ex) {
            errorHandler.handleError(ex);
          }
          playPreviewThread = null;
          enablePlayPreviewControls();
          keyframesFrameField.setValue(oldFrame);
          tinaController.refreshFlameImage(true, false, 1, true);
        }

        @Override
        public void failed(Throwable exception) {
          errorHandler.handleError(exception);
          playPreviewThread = null;
          enablePlayPreviewControls();
          keyframesFrameField.setValue(oldFrame);
          tinaController.refreshFlameImage(true, false, 1, true);
        }
      };

      playPreviewThread = new PlayPreviewThread(finishEvent);
      enablePlayPreviewControls();
      new Thread(playPreviewThread).start();
    }
  }
}
