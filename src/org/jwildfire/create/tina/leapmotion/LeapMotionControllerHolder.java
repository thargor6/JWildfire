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

import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.ErrorHandler;

import com.leapmotion.leap.Controller;

public class LeapMotionControllerHolder {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private Controller leapMotionController = null;
  private LeapMotionEditorListener listener = null;
  private LeapMotionEditorListenerThread leapMotionEditorListenerThread = null;

  public LeapMotionControllerHolder(TinaController pTinaController, ErrorHandler pErrorHandler) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
  }

  public void stopLeapMotionListener() {
    try {
      try {
        if (leapMotionController != null && listener != null) {
          leapMotionController.removeListener(listener);
        }
      }
      finally {
        listener = null;
      }
      if (leapMotionEditorListenerThread != null) {
        leapMotionEditorListenerThread.signalCancel();
        leapMotionEditorListenerThread = null;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void startLeapMotionListener() {
    try {
      tinaController.saveUndoPoint();
      if (leapMotionController == null) {
        leapMotionController = new Controller();
      }
      leapMotionEditorListenerThread = new LeapMotionEditorListenerThread(tinaController);
      listener = new LeapMotionEditorListener(leapMotionEditorListenerThread);
      new Thread(leapMotionEditorListenerThread).start();
      leapMotionController.addListener(listener);
    }
    catch (Exception ex) {
      errorHandler.handleError(ex);
    }
  }

}
