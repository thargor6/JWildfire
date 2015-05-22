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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;

public class LeapMotionConnectedProperty {
  private LeapMotionHand leapMotionHand = LeapMotionHand.RIGHT;
  private LeapMotionInputChannel inputChannel = LeapMotionInputChannel.POSITION_X;
  private LeapMotionOutputChannel outputChannel = LeapMotionOutputChannel.XFORM_MOVE_X;
  private int index1 = 0;
  private int index2 = 0;
  private int index3 = 0;
  private double offset = 0.0;
  private double invScale = 1.0;
  private boolean enabled = true;

  public LeapMotionConnectedProperty() {

  }

  public LeapMotionConnectedProperty(LeapMotionHand pLeapMotionHand, LeapMotionInputChannel pInputChannel, LeapMotionOutputChannel pOutputChannel, int pIndex1, int pIndex2, int pIndex3, double pOffset, double pInvScale) {
    leapMotionHand = pLeapMotionHand;
    inputChannel = pInputChannel;
    outputChannel = pOutputChannel;
    index1 = pIndex1;
    index2 = pIndex2;
    index3 = pIndex3;
    offset = pOffset;
    invScale = pInvScale;
  }

  public LeapMotionHand getLeapMotionHand() {
    return leapMotionHand;
  }

  public void setLeapMotionHand(LeapMotionHand pLeapMotionHand) {
    leapMotionHand = pLeapMotionHand;
  }

  public LeapMotionInputChannel getInputChannel() {
    return inputChannel;
  }

  public void setInputChannel(LeapMotionInputChannel pInputChannel) {
    inputChannel = pInputChannel;
  }

  public LeapMotionOutputChannel getOutputChannel() {
    return outputChannel;
  }

  public void setOutputChannel(LeapMotionOutputChannel pOutputChannel) {
    outputChannel = pOutputChannel;
  }

  public double getOffset() {
    return offset;
  }

  public void setOffset(double pOffset) {
    offset = pOffset;
  }

  public int getIndex1() {
    return index1;
  }

  public void setIndex1(int pIndex1) {
    index1 = pIndex1;
  }

  public int getIndex2() {
    return index2;
  }

  public void setIndex2(int pIndex2) {
    index2 = pIndex2;
  }

  public int getIndex3() {
    return index3;
  }

  public void setIndex3(int pIndex3) {
    index3 = pIndex3;
  }

  public double getInvScale() {
    return invScale;
  }

  public void setInvScale(double pInvScale) {
    invScale = pInvScale;
  }

  public void init(Flame pFlame) {
    outputChannel.init(this, pFlame);
    MotionCurve curve = outputChannel.getMotionCurve(this, pFlame);
    if (curve != null && curve.isEnabled()) {
      curve.setEnabled(false);
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean pEnabled) {
    enabled = pEnabled;
  }

  public double transformInputValue(double pValue) {
    return pValue / invScale + offset;
  }

  public void apply(LeapMotionEditorEvent pEvent, Flame pFlame) {
    double value = transformInputValue(inputChannel.getValue(leapMotionHand, pEvent));
    outputChannel.apply(this, pFlame, value);
  }

  public int getIndexCount() {
    return outputChannel.getIndexCount();
  }

}
