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

public class LeapMotionConnectedProperty {
  private LeapMotionHand leapMotionHand = LeapMotionHand.RIGHT;
  private LeapMotionInputChannel inputChannel = LeapMotionInputChannel.POSITION_X;
  private LeapMotionOutputChannel outputChannel = LeapMotionOutputChannel.XFORM_MOVE_X;
  private int index = 0;
  private double offset = 0.0;
  private double invScale = 1.0;
  private boolean enabled = true;

  public LeapMotionConnectedProperty() {

  }

  public LeapMotionConnectedProperty(LeapMotionHand pLeapMotionHand, LeapMotionInputChannel pInputChannel, LeapMotionOutputChannel pOutputChannel, int pIndex, double pOffset, double pInvScale) {
    leapMotionHand = pLeapMotionHand;
    inputChannel = pInputChannel;
    outputChannel = pOutputChannel;
    index = pIndex;
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

  public int getIndex() {
    return index;
  }

  public void setIndex(int pIndex) {
    index = pIndex;
  }

  public double getInvScale() {
    return invScale;
  }

  public void setInvScale(double pInvScale) {
    invScale = pInvScale;
  }

  public void init(Flame pFlame) {
    outputChannel.init(this, pFlame);
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean pEnabled) {
    enabled = pEnabled;
  }

  public void apply(LeapMotionEditorEvent pEvent, Flame pFlame) {
    double value = inputChannel.getValue(leapMotionHand, pEvent) / invScale + offset;
    outputChannel.apply(this, pFlame, value);
  }

}
