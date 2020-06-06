/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.MainEditorFrame;
import org.jwildfire.create.tina.swing.MotionCurveEditor;
import org.jwildfire.create.tina.swing.RenderMainFlameThreadFinishEvent;
import org.jwildfire.create.tina.swing.StandardDialogs;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.swing.ErrorHandler;
import org.jwildfire.swing.ImagePanel;

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
        //// tinaController.refreshFlameImage(true, false, 1, true, false);
      }
      finally {
        tinaController.setNoRefresh(oldNoRefresh);
      }

      tinaController.refreshUI();
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
        /*
        if (playPreviewThread != null) {
          FlamePanelConfig cfg = tinaController.getFlamePanelConfig();
          boolean oldNoControls = cfg.isNoControls();
          try {
            cfg.setNoControls(true);
            tinaController.refreshFlameImage(true, true, 1, true, false);
          }
          finally {
            cfg.setNoControls(oldNoControls);
          }
        }
         */
      }
      finally {
        tinaController.setNoRefresh(oldNoRefresh);
      }

      if (playPreviewThread == null) {
        tinaController.refreshUI();
      }
    }
  }

  public void refreshUI() {
    keyframesFrameCountField.setValue(tinaController.getCurrFlame().getFrameCount());
    adjustFrameControls(tinaController.getCurrFlame().getFrame());
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

    ImagePanel imagePanel = null;

    public PlayPreviewThread(RenderMainFlameThreadFinishEvent pFinishEvent) {
      finishEvent = pFinishEvent;
    }

    @Override
    public void run() {
      finished = forceAbort = false;
      try {
        tinaController.cancelBackgroundRender();
        FlamePanel flamePanel = tinaController.getFlamePreviewHelper().getFlamePanelProvider().getFlamePanel();
        final int prevFrame = keyframesFrameField.getIntValue();

        double quality = Math.min(Math.max(0.1, prefs.getTinaRenderAnimPreviewQuality()), 5.0);
        double previewSize = Math.min(Math.max(0.1, prefs.getTinaRenderAnimPreviewSize()), 1.0);

        Container parentContainer = flamePanel.getParent();
        // flamePanel.setVisible(false);
        try {
          int frameCount = keyframesFrameCountField.getIntValue();
          int frameStart = 1;

          long t0 = System.currentTimeMillis();
          for (int i = frameStart; i <= frameCount; i++) {
            if (forceAbort) {
              break;
            }
            long f0 = System.currentTimeMillis();
            keyframesFrameSlider.setValue(i);

            SimpleImage img = tinaController.getFlamePreviewHelper().renderAnimFrame(previewSize, quality);
            if (imagePanel == null) {
              int x = (flamePanel.getImageWidth() - img.getImageWidth()) / 2;
              int y = (flamePanel.getImageHeight() - img.getImageHeight()) / 2;
              imagePanel = new ImagePanel(img, x, y, img.getImageWidth());
              imagePanel.setLocation(new Point(x, y));
              imagePanel.setSize(new Dimension(img.getImageWidth(), img.getImageHeight()));
              imagePanel.setPreferredSize(new Dimension(img.getImageWidth(), img.getImageHeight()));
              imagePanel.setBounds(x, y, img.getImageWidth(), img.getImageHeight());
              parentContainer.add(imagePanel);
              parentContainer.getParent().repaint();
              imagePanel.repaint();
            }
            else {
              imagePanel.setImage(img);
              imagePanel.repaint();
            }
            while (true) {
              long f1 = System.currentTimeMillis();
              if (f1 - f0 > 25) {
                break;
              }
              Thread.sleep(1);
            }
          }
          long t1 = System.currentTimeMillis();
          finished = true;
          //keyframesFrameField.setValue(keyframesFrameSlider.getValue());
          //keyframesFrameField.setValue(prevFrame);
          keyframesFrameSlider.setValue(prevFrame);
          finishEvent.succeeded((t1 - t0) * 0.001);
        }
        finally {
          try {
            if (imagePanel != null) {
              parentContainer.remove(imagePanel);
            }
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
          flamePanel.setVisible(true);
        }
      }
      catch (Throwable ex) {
        finished = true;
        keyframesFrameField.setValue(keyframesFrameSlider.getValue());
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
    motionCurvePlayPreviewButton.setText(playPreviewThread == null ? "Play" : "Stop");
    String iconname = playPreviewThread == null ? "media-playback-start-7" : "media-playback-stop-7";
    motionCurvePlayPreviewButton.setIcon(new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/" + iconname + ".png")));
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
          //  tinaController.refreshFlameImage(true, false, 1, true, false);
        }

        @Override
        public void failed(Throwable exception) {
          errorHandler.handleError(exception);
          playPreviewThread = null;
          enablePlayPreviewControls();
          //  tinaController.refreshFlameImage(true, false, 1, true, false);
        }
      };

      playPreviewThread = new PlayPreviewThread(finishEvent);
      enablePlayPreviewControls();
      new Thread(playPreviewThread).start();
    }
  }

  public void skipToLastFrameButtonClicked() {
    if (playPreviewThread == null) {
      int lastFrame = keyframesFrameCountField.getIntValue();
      tinaController.saveUndoPoint();
      tinaController.getCurrFlame().setFrame(lastFrame);
      keyframesFrameField.setValue(lastFrame);
      keyframesFrameSlider.setValue(lastFrame);
    }
  }

  public void skipToFirstFrameButtonClicked() {
    if (playPreviewThread == null) {
      int firstFrame = 1;
      tinaController.saveUndoPoint();
      tinaController.getCurrFlame().setFrame(firstFrame);
      keyframesFrameField.setValue(firstFrame);
      keyframesFrameSlider.setValue(firstFrame);
    }
  }

  public void resetMotionCurvesButtonClicked() {
    if (StandardDialogs.confirm(rootPanel, "Do you really want to reset all motion curves?\n(Don't worry too much, this can be undone)")) {
      tinaController.saveUndoPoint();
      AnimationService.resetMotionCurves(tinaController.getCurrFlame());
      tinaController.getCurrFlame().setFrame(AnimationService.STARTFRAME);
      tinaController.refreshUI();
    }
  }

  public void gotoToPrevKeyFrameButtonClicked() {
    Flame flame = tinaController.getCurrFlame();
    if(flame!=null) {
      int currFrame = flame.getFrame();
      int nextKeyFrame = AnimationService.getNextKeyFrame(flame, currFrame, false);
      if(nextKeyFrame<0) {
        StandardDialogs.message(rootPanel, "There is no keyframe before the current frame (" + currFrame +")");
      }
      else {
        tinaController.saveUndoPoint();
        tinaController.getCurrFlame().setFrame(nextKeyFrame);
        keyframesFrameField.setValue(nextKeyFrame);
        keyframesFrameSlider.setValue(keyframesFrameField.getIntValue());
      }
    }
  }

  public void gotoToNextKeyFrameButtonClicked() {
    Flame flame = tinaController.getCurrFlame();
    if(flame!=null) {
      int currFrame = flame.getFrame();
      int nextKeyFrame = AnimationService.getNextKeyFrame(flame, currFrame, true);
      if(nextKeyFrame<0) {
        StandardDialogs.message(rootPanel, "There is no keyframe after the current frame (" + currFrame +")");
      }
      else {
        tinaController.saveUndoPoint();
        tinaController.getCurrFlame().setFrame(nextKeyFrame);
        keyframesFrameField.setValue(nextKeyFrame);
        keyframesFrameSlider.setValue(keyframesFrameField.getIntValue());
      }
    }
  }

  public void deleteKeyFrameButtonClicked() {
    Flame flame = tinaController.getCurrFlame();
    if(flame!=null) {
      int currFrame = flame.getFrame();
      if (!AnimationService.hasKeyFrame(flame, currFrame)) {
        StandardDialogs.message(rootPanel, "There is no keyframe at the current frame (" + currFrame +")");
      }
      else {
        tinaController.saveUndoPoint();
        int prevKeyFrame = AnimationService.getNextKeyFrame(flame, currFrame, false);
        AnimationService.deleteKeyFrame(flame, currFrame);
        if(prevKeyFrame<0) {
          tinaController.getCurrFlame().setFrame(AnimationService.STARTFRAME);
          keyframesFrameField.setValue(AnimationService.STARTFRAME);
          keyframesFrameSlider.setValue(AnimationService.STARTFRAME);
        }
        else {
          tinaController.getCurrFlame().setFrame(prevKeyFrame);
          keyframesFrameField.setValue(prevKeyFrame);
          keyframesFrameSlider.setValue(prevKeyFrame);
        }
      }
    }
  }

  public void duplicateKeyFrameButtonClicked() {
    Flame flame = tinaController.getCurrFlame();
    if(flame!=null) {
      int currFrame = flame.getFrame();
      if (!AnimationService.hasKeyFrame(flame, currFrame)) {
        StandardDialogs.message(rootPanel, "There is no keyframe at the current frame (" + currFrame +")");
      }
      else {
        String frameStr = StandardDialogs.promptForText(rootPanel, "Destination frame", String.valueOf(currFrame));
        try {
          int dstFrame = Integer.parseInt(frameStr);
          if(dstFrame<AnimationService.STARTFRAME || dstFrame==currFrame) {
            throw new RuntimeException("Invalid frame number");
          }
          tinaController.saveUndoPoint();
          AnimationService.duplicateKeyFrame(flame, currFrame, dstFrame);
          tinaController.getCurrFlame().setFrame(dstFrame);
          keyframesFrameField.setValue(dstFrame);
          keyframesFrameSlider.setValue(dstFrame);
        }
        catch(Exception ex) {
          StandardDialogs.message(rootPanel, "Please enter a vlid frame number (which is not equal to the current frame)");
        }
      }
    }
  }
}
