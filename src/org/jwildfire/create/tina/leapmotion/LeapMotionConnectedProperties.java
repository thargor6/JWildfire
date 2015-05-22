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
import java.util.List;

public class LeapMotionConnectedProperties {
  private final List<LeapMotionConnectedProperty> properties = new ArrayList<LeapMotionConnectedProperty>();

  public List<LeapMotionConnectedProperty> getProperties() {
    return properties;
  }

  public static LeapMotionConnectedProperties getDefaultConfig() {
    LeapMotionConnectedProperties res = new LeapMotionConnectedProperties();
    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.RIGHT, LeapMotionInputChannel.NULL, LeapMotionOutputChannel.XFORM_RESET_ANGLES, 0, 0, 0, 0.0, 1.0));
    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.RIGHT, LeapMotionInputChannel.ROLL, LeapMotionOutputChannel.XFORM_ROTATE, 0, 0, 0, 0.0, 3.0));
    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.RIGHT, LeapMotionInputChannel.PITCH, LeapMotionOutputChannel.XFORM_SCALE, 0, 0, 0, 1.0, 270.0));
    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.RIGHT, LeapMotionInputChannel.POSITION_X, LeapMotionOutputChannel.XFORM_MOVE_X, 0, 0, 0, 0, 150.0));
    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.RIGHT, LeapMotionInputChannel.POSITION_Y, LeapMotionOutputChannel.XFORM_MOVE_Y, 0, 0, 0, 0, -150.0));

    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.LEFT, LeapMotionInputChannel.NULL, LeapMotionOutputChannel.XFORM_RESET_ANGLES, 1, 0, 0, 0.0, 1.0));
    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.LEFT, LeapMotionInputChannel.ROLL, LeapMotionOutputChannel.XFORM_ROTATE, 1, 0, 0, 0.0, 3.0));
    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.LEFT, LeapMotionInputChannel.PITCH, LeapMotionOutputChannel.XFORM_SCALE, 1, 0, 0, 1.0, 270.0));
    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.LEFT, LeapMotionInputChannel.POSITION_X, LeapMotionOutputChannel.XFORM_MOVE_X, 1, 0, 0, 0, 150.0));
    res.getProperties().add(new LeapMotionConnectedProperty(LeapMotionHand.LEFT, LeapMotionInputChannel.POSITION_Y, LeapMotionOutputChannel.XFORM_MOVE_Y, 1, 0, 0, 0, -150.0));

    return res;
  }
}
