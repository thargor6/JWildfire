/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2025 Andreas Maschke

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
package org.jwildfire.create.tina.render.gpu.farender;

import org.jwildfire.base.Tools;

public class FARenderResult {
  private String command;
  private int returnCode;
  private String message;
  private String outputFilename;

  public FARenderResult() {

  }

  public FARenderResult(int returnCode, String message) {
    this.returnCode = returnCode;
    this.message = message;
  }

  public FARenderResult(int returnCode, Exception ex) {
    this.returnCode = returnCode;
    this.message = Tools.getStacktrace(ex);
  }

  public int getReturnCode() {
    return returnCode;
  }

  public String getMessage() {
    return message;
  }

  public String getOutputFilename() {
    return outputFilename;
  }

  public void setReturnCode(int returnCode) {
    this.returnCode = returnCode;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setOutputFilename(String outputFilename) {
    this.outputFilename = outputFilename;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }
}
