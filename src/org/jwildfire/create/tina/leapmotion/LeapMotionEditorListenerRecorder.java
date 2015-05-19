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
  private final int fps;
  private List<LeapMotionEditorHandEvent> leftHandMotion = new ArrayList<LeapMotionEditorHandEvent>();
  private List<LeapMotionEditorHandEvent> rightHandMotion = new ArrayList<LeapMotionEditorHandEvent>();

  public LeapMotionEditorListenerRecorder(int pFps) {
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

  public String getDataAsString() {
    StringBuilder sb = new StringBuilder();
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
    addHeader(sb);
    LeapMotionEditorHandEvent lastLeftEvent = new LeapMotionEditorHandEvent();
    LeapMotionEditorHandEvent lastRightEvent = new LeapMotionEditorHandEvent();
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
      addKeyFrame(sb, keyFrame, leftEvent, rightEvent);
    }
    return sb.toString();
  }

  private void addKeyFrame(StringBuilder pSb, Long pKeyFrame, LeapMotionEditorHandEvent pLeftEvent, LeapMotionEditorHandEvent pRightEvent) {
    pSb.append(Tools.doubleToString(((double) pKeyFrame * fps) / 1000.0) + " ");

    pSb.append(Tools.doubleToString(pLeftEvent.getPosX()) + " ");
    pSb.append(Tools.doubleToString(pLeftEvent.getPosY()) + " ");
    pSb.append(Tools.doubleToString(pLeftEvent.getPosZ()) + " ");
    pSb.append(Tools.doubleToString(pLeftEvent.getRoll()) + " ");
    pSb.append(Tools.doubleToString(pLeftEvent.getPitch()) + " ");
    pSb.append(Tools.doubleToString(pLeftEvent.getYaw()) + " ");

    pSb.append(Tools.doubleToString(pRightEvent.getPosX()) + " ");
    pSb.append(Tools.doubleToString(pRightEvent.getPosY()) + " ");
    pSb.append(Tools.doubleToString(pRightEvent.getPosZ()) + " ");
    pSb.append(Tools.doubleToString(pRightEvent.getRoll()) + " ");
    pSb.append(Tools.doubleToString(pRightEvent.getPitch()) + " ");
    pSb.append(Tools.doubleToString(pRightEvent.getYaw()) + "\n");
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

}
