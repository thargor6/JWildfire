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
package org.jwildfire.create.tina.leapmotion;

public class LeapMotionEditorHandEvent {
  private long timestamp;
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

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long pTimestamp) {
    timestamp = pTimestamp;
  }
}
