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
package org.jwildfire.swing;

import java.io.File;

import javax.swing.JDesktopPane;

import org.jwildfire.base.Tools;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.script.Action;
import org.jwildfire.script.ActionList;
import org.jwildfire.script.Parameter;


public class RenderThread implements Runnable {
  public enum Mode {
    SINGLE_FRAME, BATCH
  }

  private boolean forceAbort;
  private boolean done;
  private final JDesktopPane desktop;
  private RenderProgressReporter reporter;
  private final ActionList actionList;
  private final int frameMin;
  private final int frameMax;
  private final String basePath;
  private final Mode mode;
  private Throwable error;
  private SimpleImage lastImage;

  public RenderThread(Mode pMode, JDesktopPane pDesktop, RenderProgressReporter pReporter,
      ActionList pActionList, int pFrameMin, int pFrameMax, String pBasePath) {
    desktop = pDesktop;
    reporter = pReporter;
    actionList = pActionList;
    frameMin = pFrameMin;
    frameMax = pFrameMax;
    basePath = pBasePath;
    mode = pMode;
  }

  @Override
  public void run() {
    done = false;
    try {
      forceAbort = false;
      error = null;
      try {
        for (int frame = frameMin; frame <= frameMax; frame++) {
          if (forceAbort)
            break;
          ActionList actions = actionList.clone();
          // generate Filename
          String frameStr = String.valueOf(frame);
          while (frameStr.length() < 4)
            frameStr = "0" + frameStr;
          String filename = basePath + frameStr + ".jpg";
          // calculate parameters
          for (Action action : actions) {
            if (action.hasEnvelopes()) {
              for (Parameter parameter : action.getParameterList()) {
                Envelope envelope = parameter.getEnvelope();
                if (envelope != null) {
                  double val = envelope.evaluate(frame);
                  parameter.setValue(Tools.doubleToString(val));
                }
              }
            }
          }
          // render the image
          ScriptProcessor scriptProcessor = new ScriptProcessor(desktop);
          scriptProcessor.setAddBuffersToDesktop(false);
          SimpleImage paramInitImg = null; // dummy image just to call the init-method of transformers (which makes sense if script lack some parameters)
          for (Action action : actions) {
            switch (action.getActionType()) {
              case EXECUTE_CREATOR:
                scriptProcessor.selectCreator(action.getParameter());
                action.setProperties(scriptProcessor.getCreator(), scriptProcessor.getBufferList());
                scriptProcessor.executeCreator(action.getWidth(), action.getHeight(),
                    action.getOutputBuffer(), false);
                break;
              case EXECUTE_LOADER:
                scriptProcessor.selectLoader(action.getParameter());
                action.setProperties(scriptProcessor.getLoader(), scriptProcessor.getBufferList());
                scriptProcessor.executeLoader(action.getOutputBuffer(), false);
                break;
              case EXECUTE_TRANSFORMER:
                scriptProcessor.selectTransformer(action.getParameter());
                if (paramInitImg == null)
                  paramInitImg = new SimpleImage(320, 256);
                scriptProcessor.getTransformer().initDefaultParams(paramInitImg);
                action.setProperties(scriptProcessor.getTransformer(),
                    scriptProcessor.getBufferList());
                scriptProcessor.executeTransformer(action.getInputBuffer(),
                    action.getOutputBuffer3D() != null, action.getOutputBuffer(),
                    action.getOutputBuffer3D(), false);
                break;
              case LOAD_IMAGE:
                scriptProcessor.loadImage(action.getParameter());
                break;
            }
          }
          // Save file
          SimpleImage img = scriptProcessor.getLastImage();
          switch (mode) {
            case BATCH:
              scriptProcessor.saveLastImage(filename);
              reporter.showProgress(frame, new File(filename).getName(), img);
              break;
            case SINGLE_FRAME:
              lastImage = img;
              break;
          }
        }
      }
      catch (Exception ex) {
        error = ex;
      }
    }
    finally {
      done = true;
      reporter.renderingFinished();
    }
  }

  public boolean isDone() {
    return done;
  }

  public void setForceAbort(boolean forceAbort) {
    this.forceAbort = forceAbort;
  }

  public Throwable getError() {
    return error;
  }

  public boolean isForceAbort() {
    return forceAbort;
  }

  public SimpleImage getLastImage() {
    return lastImage;
  }

}
