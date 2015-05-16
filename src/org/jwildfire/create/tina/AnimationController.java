/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.MotionCurveEditor;
import org.jwildfire.create.tina.swing.RenderMainFlameThreadFinishEvent;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.create.tina.swing.flamepanel.FlamePanelConfig;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.swing.ErrorHandler;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

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
  private final JToggleButton leapMotionToggleButton;

  public AnimationController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel,
      JWFNumberField pKeyframesFrameField, JSlider pKeyframesFrameSlider, JWFNumberField pKeyframesFrameCountField,
      JPanel pFrameSliderPanel, JLabel pKeyframesFrameLbl, JLabel pKeyframesFrameCountLbl, JToggleButton pMotionCurveEditModeButton, JPanel pMotionBlurPanel,
      JButton pMotionCurvePlayPreviewButton, JToggleButton pLeapMotionToggleButton) {
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
    leapMotionToggleButton = pLeapMotionToggleButton;
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
        tinaController.refreshFlameImage(false);
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
        adjustFrameControls(frame);
        if (tinaController.getCurrFlame() != null) {
          tinaController.getCurrFlame().setFrame(frame);
        }
        if (playPreviewThread != null) {
          FlamePanelConfig cfg = tinaController.getFlamePanelConfig();
          boolean oldNoControls = cfg.isNoControls();
          try {
            cfg.setNoControls(true);
            tinaController.refreshFlameImage(true, false, 2);
          }
          finally {
            cfg.setNoControls(oldNoControls);
          }
        }
        else {
          tinaController.refreshFlameImage(true, false, 1);
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
          tinaController.refreshFlameImage(false);
        }

        @Override
        public void failed(Throwable exception) {
          errorHandler.handleError(exception);
          playPreviewThread = null;
          enablePlayPreviewControls();
          keyframesFrameField.setValue(oldFrame);
          tinaController.refreshFlameImage(false);
        }
      };

      playPreviewThread = new PlayPreviewThread(finishEvent);
      enablePlayPreviewControls();
      new Thread(playPreviewThread).start();
    }
  }

  class SampleListener2 extends Listener {
    public void onInit(Controller controller) {
      System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
      System.out.println("Connected");
      controller.enableGesture(Gesture.Type.TYPE_SWIPE);
      controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
      controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
      controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    public void onDisconnect(Controller controller) {
      //Note: not dispatched when running in a debugger.
      System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
      System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
      Frame frame = controller.frame();
      if (frame != null && frame.hands().count() > 0) {
        LeapMotionEditorHandEvent leftHand = null;
        LeapMotionEditorHandEvent rightHand = null;
        for (Hand hand : frame.hands()) {
          LeapMotionEditorHandEvent handEvent;
          if (hand.isLeft()) {
            leftHand = handEvent = new LeapMotionEditorHandEvent();
          }
          else {
            rightHand = handEvent = new LeapMotionEditorHandEvent();
          }
          Vector normal = hand.palmNormal();
          Vector direction = hand.direction();
          Vector position = hand.palmPosition();
          handEvent.setPosX(position.getX());
          handEvent.setPosY(position.getY());
          handEvent.setPosZ(position.getZ());
          handEvent.setPitch(Math.toDegrees(direction.pitch()));
          handEvent.setRoll(Math.toDegrees(normal.roll()));
          handEvent.setYaw(Math.toDegrees(direction.yaw()));
        }
        leapMotionEditorListenerThread.signalEvent(new LeapMotionEditorEvent(leftHand, rightHand));
      }
    }
  }

  private Controller leapMotionController = null;
  private SampleListener2 listener = null;
  private LeapMotionEditorListenerThread leapMotionEditorListenerThread = null;

  public void toggleLeapMotionMode() {
    if (leapMotionToggleButton.isSelected()) {
      startLeapMotionListener();
    }
    else {
      stopLeapMotionListener();
    }
  }

  private void stopLeapMotionListener() {
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
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void startLeapMotionListener() {
    try {
      tinaController.saveUndoPoint();
      if (leapMotionController == null) {
        leapMotionController = new Controller();
      }
      listener = new SampleListener2();
      leapMotionEditorListenerThread = new LeapMotionEditorListenerThread();
      new Thread(leapMotionEditorListenerThread).start();
      leapMotionController.addListener(listener);
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

  private class LeapMotionEditorHandEvent {
    private double roll;
    private double pitch;
    private double yaw;
    private double posX;
    private double posY;
    private double posZ;

    public double getRoll() {
      return roll;
    }

    public double getPitch() {
      return pitch;
    }

    public double getYaw() {
      return yaw;
    }

    public void setRoll(double pRoll) {
      roll = pRoll;
    }

    public void setPitch(double pPitch) {
      pitch = pPitch;
    }

    public void setYaw(double pYaw) {
      yaw = pYaw;
    }

    public double getPosX() {
      return posX;
    }

    public void setPosX(double pPosX) {
      posX = pPosX;
    }

    public double getPosY() {
      return posY;
    }

    public void setPosY(double pPosY) {
      posY = pPosY;
    }

    public double getPosZ() {
      return posZ;
    }

    public void setPosZ(double pPosZ) {
      posZ = pPosZ;
    }

  }

  private class LeapMotionEditorEvent {
    private final LeapMotionEditorHandEvent leftHand;
    private final LeapMotionEditorHandEvent rightHand;

    public LeapMotionEditorEvent(LeapMotionEditorHandEvent pLeftHand, LeapMotionEditorHandEvent pRightHand) {
      leftHand = pLeftHand;
      rightHand = pRightHand;
    }

    public LeapMotionEditorHandEvent getLeftHand() {
      return leftHand;
    }

    public LeapMotionEditorHandEvent getRightHand() {
      return rightHand;
    }
  }

  private class LeapMotionEditorListenerThread implements Runnable {
    private boolean cancelSignalled;

    private final Stack<LeapMotionEditorEvent> eventStack = new Stack<LeapMotionEditorEvent>();

    @Override
    public void run() {
      long t0 = System.currentTimeMillis();
      long frameCount = 0;
      while (!cancelSignalled) {
        try {
          if (!eventStack.isEmpty()) {
            //            LeapMotionEditorEvent event = eventStack.pop();
            //            eventStack.clear();

            List<LeapMotionEditorEvent> events = new ArrayList<LeapMotionEditorEvent>();
            while (!eventStack.isEmpty()) {
              events.add(eventStack.pop());
            }
            eventStack.clear();
            LeapMotionEditorEvent event = average(events);
            if (event != null) {
              Flame flame = tinaController.getCurrFlame();
              XForm xform = tinaController.getCurrXForm();
              if (xform == null && flame != null) {
                Layer layer = tinaController.getCurrLayer();
                if (layer == null) {
                  layer = flame.getFirstLayer();
                }
                if (layer != null && layer.getXForms().size() > 0) {
                  xform = layer.getXForms().get(0);
                }
              }
              if (flame != null && xform != null) {
                XFormTransformService.reset(xform, tinaController.data.affineEditPostTransformButton.isSelected());
                if (event.getRightHand() != null) {

                  //                  flame.setCamRoll(event.getRightHand().getRoll());
                  //                  flame.setCamPitch(event.getRightHand().getPitch());
                  //                  flame.setCamYaw(event.getRightHand().getYaw());
                  //
                  XFormTransformService.rotate(xform, event.getRightHand().getRoll(), tinaController.data.affineEditPostTransformButton.isSelected());
                  double SCALE_SCALE = 90.0;
                  double scale = 1.0 + event.getRightHand().getPitch() / SCALE_SCALE;
                  double POS_SCALE = 50.0;
                  double POS_OFFSET_X = 0.0;
                  double POS_OFFSET_Y = 1.5;
                  double dx = event.getRightHand().getPosX() / POS_SCALE + POS_OFFSET_X;
                  double dy = -event.getRightHand().getPosY() / POS_SCALE + POS_OFFSET_Y;
                  if (tinaController.data.affineEditPostTransformButton.isSelected()) {
                    xform.setPostCoeff20(dx);
                    xform.setPostCoeff21(dy);
                  }
                  else {
                    xform.setCoeff20(dx);
                    xform.setCoeff21(dy);
                  }
                  XFormTransformService.scale(xform, scale, true, true, tinaController.data.affineEditPostTransformButton.isSelected());
                }
                if (event.getLeftHand() != null) {
                  double dy = event.getLeftHand().getPosY() - 50;
                  if (dy < -255.0)
                    dy = -255;
                  else if (dy > 255)
                    dy = 255;
                  System.out.println(dy);
                  flame.getFirstLayer().getPalette().setModShift((int) dy);

                }
                tinaController.fastRefreshFlameImage(true, false, 1);
                frameCount++;
                if (frameCount % 30 == 0) {
                  long t1 = System.currentTimeMillis();
                  double t = (double) (t1 - t0) / 1000.0;
                  System.out.println(frameCount + ", time=" + t + "s, fps=" + (double) frameCount / t);
                }
              }
            }
          }
          else {
            Thread.sleep(1);
          }
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    // TODO
    private LeapMotionEditorEvent average(List<LeapMotionEditorEvent> pEvents) {
      if (pEvents != null && pEvents.size() > 0) {
        return pEvents.get(pEvents.size() / 2);
      }
      else {
        return null;
      }
    }

    public void signalCancel() {
      cancelSignalled = true;
    }

    public void signalEvent(LeapMotionEditorEvent pEvent) {
      eventStack.push(pEvent);
    }
  }
}
