/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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

import org.jwildfire.base.Unchecker;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DefaultJFrameHolder<T extends JFrame> extends JFrameHolder<T> {

  public DefaultJFrameHolder(Class<T> frameType, JWildfire desktop, String windowPrefsName, String menuCaption) {
    super(frameType, desktop, windowPrefsName, menuCaption);
  }

  @Override
  protected T createFrame() {
    T frame = null;
    try {
      frame = frameType.newInstance();
    }
    catch (Exception ex) {
      Unchecker.rethrow(ex);
      return null;  // NOT_REACHED
    }

    applyWindowPrefs(frame);

    frame
        .addWindowListener(new WindowAdapter() {
          public void windowDeactivated(
              WindowEvent e) {
            desktop.enableControls();
          }

          public void windowClosed(
              WindowEvent e) {
            desktop.enableControls();
          }
        });
    return frame;
  }

}
