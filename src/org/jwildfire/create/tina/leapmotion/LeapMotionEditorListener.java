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


import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

public class LeapMotionEditorListener extends Listener {
  private final LeapMotionEditorListenerThread leapMotionEditorListenerThread;
  private final LeapMotionEditorListenerRecorder recorder;
  private boolean initFlag;
  private long refTime;

  public LeapMotionEditorListener(LeapMotionEditorListenerThread pLeapMotionEditorListenerThread, LeapMotionEditorListenerRecorder pRecorder) {
    leapMotionEditorListenerThread = pLeapMotionEditorListenerThread;
    recorder = pRecorder;
  }

  public void onInit(Controller controller) {
    // EMPTY for now
    initFlag = true;
  }

  public void onConnect(Controller controller) {
    //    controller.enableGesture(Gesture.Type.TYPE_SWIPE);
    //    controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
    //    controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
    //    controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
  }

  public void onDisconnect(Controller controller) {
    // EMPTY for now
  }

  public void onExit(Controller controller) {
    // EMPTY for now
  }

  public void onFrame(Controller controller) {
    Frame frame = controller.frame();
    if (frame != null && frame.hands().count() > 0) {
      long currTime = System.currentTimeMillis();
      if (initFlag) {
        initFlag = false;
        refTime = currTime;
      }
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
        handEvent.setTimestamp(currTime - refTime);
      }
      LeapMotionEditorEvent event = new LeapMotionEditorEvent(leftHand, rightHand);
      if (recorder != null) {
        recorder.recordEvent(event);
      }
      leapMotionEditorListenerThread.signalEvent(event);
    }
  }

}
