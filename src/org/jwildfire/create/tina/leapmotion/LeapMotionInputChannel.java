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


public enum LeapMotionInputChannel {
  POSITION_X {
    @Override
    public double getValue(LeapMotionHand pHand, LeapMotionEditorEvent pEvent) {
      LeapMotionEditorHandEvent handEvent = pHand.getHandEvent(pEvent);
      if (handEvent != null) {
        return handEvent.getPosX();
      }
      else {
        return 0.0;
      }
    }
  },
  POSITION_Y {
    @Override
    public double getValue(LeapMotionHand pHand, LeapMotionEditorEvent pEvent) {
      LeapMotionEditorHandEvent handEvent = pHand.getHandEvent(pEvent);
      if (handEvent != null) {
        return handEvent.getPosY();
      }
      else {
        return 0.0;
      }
    }
  },
  POSITION_Z {
    @Override
    public double getValue(LeapMotionHand pHand, LeapMotionEditorEvent pEvent) {
      LeapMotionEditorHandEvent handEvent = pHand.getHandEvent(pEvent);
      if (handEvent != null) {
        return handEvent.getPosZ();
      }
      else {
        return 0.0;
      }
    }
  },
  ROLL {
    @Override
    public double getValue(LeapMotionHand pHand, LeapMotionEditorEvent pEvent) {
      LeapMotionEditorHandEvent handEvent = pHand.getHandEvent(pEvent);
      if (handEvent != null) {
        return -handEvent.getRoll();
      }
      else {
        return 0.0;
      }
    }
  },
  PITCH {
    @Override
    public double getValue(LeapMotionHand pHand, LeapMotionEditorEvent pEvent) {
      LeapMotionEditorHandEvent handEvent = pHand.getHandEvent(pEvent);
      if (handEvent != null) {
        return -handEvent.getPitch();
      }
      else {
        return 0.0;
      }
    }
  },
  YAW {
    @Override
    public double getValue(LeapMotionHand pHand, LeapMotionEditorEvent pEvent) {
      LeapMotionEditorHandEvent handEvent = pHand.getHandEvent(pEvent);
      if (handEvent != null) {
        return -handEvent.getYaw();
      }
      else {
        return 0.0;
      }
    }
  },
  NULL {
    @Override
    public double getValue(LeapMotionHand pHand, LeapMotionEditorEvent pEvent) {
      return 0.0;
    }
  };

  public abstract double getValue(LeapMotionHand pHand, LeapMotionEditorEvent pEvent);

}
