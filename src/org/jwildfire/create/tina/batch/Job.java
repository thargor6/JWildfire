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
package org.jwildfire.create.tina.batch;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Stereo3dMode;

public class Job {
  private String flameFilename;
  private boolean finished;
  private double elapsedSeconds;
  private String imageFilename;
  private int customWidth;
  private int customHeight;
  private int customQuality;

  private Throwable lastError;

  public String getFlameFilename() {
    return flameFilename;
  }

  public void setFlameFilename(String pFlameFilename) {
    flameFilename = pFlameFilename;
    imageFilename = null;
  }

  public boolean isFinished() {
    return finished;
  }

  public double getElapsedSeconds() {
    return elapsedSeconds;
  }

  public String getImageFilename(Stereo3dMode pStereo3dMode) {
    if (imageFilename == null) {
      String fileExt = Stereo3dMode.SIDE_BY_SIDE.equals(pStereo3dMode) ? Tools.FILEEXT_PNS : Tools.FILEEXT_PNG;
      imageFilename = flameFilename + "." + fileExt;
      int p = flameFilename.lastIndexOf(".");
      if (p > 0) {
        String ext = flameFilename.substring(p, flameFilename.length());
        if (ext.equalsIgnoreCase(".FLAME")) {
          imageFilename = flameFilename.substring(0, p) + "." + fileExt;
        }
      }
    }
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

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  public void setElapsedSeconds(double elapsedSeconds) {
    this.elapsedSeconds = elapsedSeconds;
  }

  public int getCustomWidth() {
    return customWidth;
  }

  public int getCustomHeight() {
    return customHeight;
  }

  public int getCustomQuality() {
    return customQuality;
  }

  public void setCustomWidth(int pCustomWidth) {
    customWidth = pCustomWidth;
  }

  public void setCustomHeight(int pCustomHeight) {
    customHeight = pCustomHeight;
  }

  public void setCustomQuality(int pCustomQuality) {
    customQuality = pCustomQuality;
  }

}
