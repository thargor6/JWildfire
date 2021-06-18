/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2021 Andreas Maschke

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

import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StandardErrorHandler implements ErrorHandler {
  private static final Logger logger = LoggerFactory.getLogger(StandardErrorHandler.class);
  private final JFrame rootFrame;
  private final JDialog showErrorDlg;
  private final JTextArea showErrorDlgMessageTextArea;
  private final JTextArea showErrorDlgStacktraceTextArea;

  public StandardErrorHandler(JFrame pRootFrame, JDialog pShowErrorDlg,
      JTextArea pShowErrorDlgMessageTextArea,
      JTextArea pShowErrorDlgStacktraceTextArea) {
    showErrorDlg = pShowErrorDlg;
    showErrorDlgMessageTextArea = pShowErrorDlgMessageTextArea;
    showErrorDlgStacktraceTextArea = pShowErrorDlgStacktraceTextArea;
    rootFrame = pRootFrame;
  }

  @Override
  public void handleError(Throwable pThrowable) {
    try {
      logger.error(pThrowable.getMessage(), pThrowable);
      Point dPos = rootFrame.getLocation();
      int dWidth = rootFrame.getWidth();
      int dHeight = rootFrame.getHeight();
      int wWidth = showErrorDlg.getWidth();
      int wHeight = showErrorDlg.getHeight();
      showErrorDlg.setLocation(dPos.x + (dWidth - wWidth) / 2, dPos.y
          + (dHeight - wHeight) / 2);
      {
        String msg = pThrowable.getMessage();
        showErrorDlgMessageTextArea.setText(msg);
        showErrorDlgMessageTextArea.select(0, 0);
      }
      {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        pThrowable.printStackTrace(new PrintStream(os));
        os.flush();
        os.close();
        showErrorDlgStacktraceTextArea.setText(new String(os
            .toByteArray()));
        showErrorDlgStacktraceTextArea.select(0, 0);
      }
      showErrorDlg.setModal(true);
      showErrorDlg.setVisible(true);
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
  }

  public void closeShowErrorDlg() {
    showErrorDlg.setVisible(false);
  }

}
