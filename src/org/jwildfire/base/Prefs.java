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
package org.jwildfire.base;

import java.io.File;

public class Prefs {

  private String lastScriptPath = null;
  private String scriptPath = "C:\\TMP\\wf\\";
  private String lastInputImagePath = null;
  private String inputImagePath = "C:\\TMP\\wf\\Apophysis";
  private String lastOutputImagePath = null;
  private String outputImagePath = "C:\\TMP\\wf\\";
  private String inputFlamePath = "C:\\TMP\\wf\\Apophysis";
  private String lastInputFlamePath = null;
  private String outputFlamePath = "C:\\TMP\\wf\\Apophysis";
  private String lastOutputFlamePath = null;

  public String getScriptPath() {
    return lastScriptPath != null ? lastScriptPath : scriptPath;
  }

  public void setScriptPath(String pScriptPath) {
    this.scriptPath = pScriptPath;
  }

  public String getInputImagePath() {
    return lastInputImagePath != null ? lastInputImagePath : inputImagePath;
  }

  public void setInputImagePath(String inputImagePath) {
    this.inputImagePath = inputImagePath;
  }

  public String getOutputImagePath() {
    return lastOutputImagePath != null ? lastOutputImagePath : outputImagePath;
  }

  public void setOutputImagePath(String outputImagePath) {
    this.outputImagePath = outputImagePath;
  }

  public void setLastScriptFile(File pFile) {
    lastScriptPath = pFile.getParent();
  }

  public void setLastInputImageFile(File pFile) {
    lastInputImagePath = pFile.getParent();
  }

  public void setLastOutputImageFile(File pFile) {
    lastOutputImagePath = pFile.getParent();
  }

  public String getInputFlamePath() {
    return lastInputFlamePath != null ? lastInputFlamePath : inputFlamePath;
  }

  public void setLastInputFlameFile(File pFile) {
    lastInputFlamePath = pFile.getParent();
  }

  public String getOutputFlamePath() {
    return lastOutputFlamePath != null ? lastOutputFlamePath : outputFlamePath;
  }

  public void setLastOutputFlameFile(File pFile) {
    lastOutputFlamePath = pFile.getParent();
  }
}
