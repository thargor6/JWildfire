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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jwildfire.base.Tools;

public class LeapMotionEditorListenerRecorder {
  private final LeapMotionConnectedProperties config;
  private final int fps;
  private List<LeapMotionEditorHandEvent> leftHandMotion = new ArrayList<LeapMotionEditorHandEvent>();
  private List<LeapMotionEditorHandEvent> rightHandMotion = new ArrayList<LeapMotionEditorHandEvent>();

  public LeapMotionEditorListenerRecorder(LeapMotionConnectedProperties pConfig, int pFps) {
    config = pConfig;
    fps = pFps;
  }

  public void recordEvent(LeapMotionEditorEvent pEvent) {
    if (pEvent.getLeftHand() != null) {
      leftHandMotion.add(pEvent.getLeftHand());
    }
    if (pEvent.getRightHand() != null) {
      rightHandMotion.add(pEvent.getRightHand());
    }
  }

  public boolean isEmpty() {
    return leftHandMotion.isEmpty() && rightHandMotion.isEmpty();
  }

  public static class LeapMotionEditorEventWithFrame extends LeapMotionEditorEvent {

    public LeapMotionEditorEventWithFrame() {
      super(new LeapMotionEditorHandEvent(), new LeapMotionEditorHandEvent());
    }

    private double frame;

    public double getFrame() {
      return frame;
    }

    public void setFrame(double pFrame) {
      frame = pFrame;
    }
  }

  public List<LeapMotionEditorEventWithFrame> getTransformedData() {
    Set<Long> keyFrames = new HashSet<Long>();
    Map<Long, LeapMotionEditorHandEvent> leftMap = new HashMap<Long, LeapMotionEditorHandEvent>();
    Map<Long, LeapMotionEditorHandEvent> rightMap = new HashMap<Long, LeapMotionEditorHandEvent>();
    for (LeapMotionEditorHandEvent event : leftHandMotion) {
      keyFrames.add(event.getTimestamp());
      leftMap.put(event.getTimestamp(), event);
    }
    for (LeapMotionEditorHandEvent event : rightHandMotion) {
      keyFrames.add(event.getTimestamp());
      rightMap.put(event.getTimestamp(), event);
    }
    List<Long> sortedKeyFrames = new ArrayList<Long>(keyFrames);
    Collections.sort(sortedKeyFrames);

    LeapMotionEditorHandEvent lastLeftEvent = new LeapMotionEditorHandEvent();
    LeapMotionEditorHandEvent lastRightEvent = new LeapMotionEditorHandEvent();
    List<LeapMotionEditorEventWithFrame> res = new ArrayList<LeapMotionEditorEventWithFrame>();
    for (Long keyFrame : sortedKeyFrames) {
      LeapMotionEditorHandEvent leftEvent = leftMap.get(keyFrame);
      if (leftEvent == null) {
        leftEvent = lastLeftEvent;
      }
      else {
        lastLeftEvent = leftEvent;
      }
      LeapMotionEditorHandEvent rightEvent = rightMap.get(keyFrame);
      if (rightEvent == null) {
        rightEvent = lastRightEvent;
      }
      else {
        lastRightEvent = rightEvent;
      }
      LeapMotionEditorEventWithFrame data = new LeapMotionEditorEventWithFrame();
      data.setFrame(((double) keyFrame * fps) / 1000.0);

      data.getLeftHand().setPosX(leftEvent.getPosX());
      data.getLeftHand().setPosY(leftEvent.getPosY());
      data.getLeftHand().setPosZ(leftEvent.getPosZ());
      data.getLeftHand().setRoll(leftEvent.getRoll());
      data.getLeftHand().setPitch(leftEvent.getPitch());
      data.getLeftHand().setYaw(leftEvent.getYaw());

      data.getRightHand().setPosX(rightEvent.getPosX());
      data.getRightHand().setPosY(rightEvent.getPosY());
      data.getRightHand().setPosZ(rightEvent.getPosZ());
      data.getRightHand().setRoll(rightEvent.getRoll());
      data.getRightHand().setPitch(rightEvent.getPitch());
      data.getRightHand().setYaw(rightEvent.getYaw());

      res.add(data);
    }
    return res;
  }

  public String getDataAsString() {
    StringBuilder sb = new StringBuilder();
    addHeader(sb);
    for (LeapMotionEditorEventWithFrame data : getTransformedData()) {
      addKeyFrame(sb, data);
    }
    return sb.toString();
  }

  private void addKeyFrame(StringBuilder pSb, LeapMotionEditorEventWithFrame pData) {
    pSb.append(Tools.doubleToString(pData.getFrame()) + " ");

    pSb.append(Tools.doubleToString(pData.getLeftHand().getPosX()) + " ");
    pSb.append(Tools.doubleToString(pData.getLeftHand().getPosY()) + " ");
    pSb.append(Tools.doubleToString(pData.getLeftHand().getPosZ()) + " ");
    pSb.append(Tools.doubleToString(pData.getLeftHand().getRoll()) + " ");
    pSb.append(Tools.doubleToString(pData.getLeftHand().getPitch()) + " ");
    pSb.append(Tools.doubleToString(pData.getLeftHand().getYaw()) + " ");

    pSb.append(Tools.doubleToString(pData.getRightHand().getPosX()) + " ");
    pSb.append(Tools.doubleToString(pData.getRightHand().getPosY()) + " ");
    pSb.append(Tools.doubleToString(pData.getRightHand().getPosZ()) + " ");
    pSb.append(Tools.doubleToString(pData.getRightHand().getRoll()) + " ");
    pSb.append(Tools.doubleToString(pData.getRightHand().getPitch()) + " ");
    pSb.append(Tools.doubleToString(pData.getRightHand().getYaw()) + "\n");
  }

  private void addHeader(StringBuilder pSb) {
    pSb.append("#");
    pSb.append("time ");

    pSb.append("leftPosX ");
    pSb.append("leftPosY ");
    pSb.append("leftPosZ ");
    pSb.append("leftRoll ");
    pSb.append("leftPitch ");
    pSb.append("leftYaw ");

    pSb.append("rightPosX ");
    pSb.append("rightPosY ");
    pSb.append("rightPosZ ");
    pSb.append("rightRoll ");
    pSb.append("rightPitch ");
    pSb.append("rightYaw\n");
  }

  public LeapMotionConnectedProperties getConfig() {
    return config;
  }

  public int getFps() {
    return fps;
  }

}
