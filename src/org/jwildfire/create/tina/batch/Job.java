/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.create.tina.batch;

public class Job {
  private String flameFilename;
  private boolean finished;
  private double elapsedMilliseconds;
  private String imageFilename;
  private Throwable lastError;

  public String getFlameFilename() {
    return flameFilename;
  }

  public void setFlameFilename(String pFlameFilename) {
    flameFilename = pFlameFilename;
    imageFilename = pFlameFilename + ".png";
    int p = flameFilename.lastIndexOf(".");
    if (p > 0) {
      String ext = imageFilename.substring(p, imageFilename.length());
      if (ext.equalsIgnoreCase(".FLAME")) {
        imageFilename = pFlameFilename.substring(0, p) + ".png";
      }
    }
  }

  public boolean isFinished() {
    return finished;
  }

  public double getElapsedMilliseconds() {
    return elapsedMilliseconds;
  }

  public String getImageFilename() {
    return imageFilename;
  }

  public void setLastError(Throwable pLastError) {
    lastError = pLastError;
  }

  public String getLastErrorMsg() {
    if (lastError != null) {
      String msg = lastError.getMessage();
      if (msg != null && msg.length() > 0) {
        return msg;
      }
      return lastError.toString();
    }
    return "";
  }

}
