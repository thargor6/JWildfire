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

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Unchecker;
import org.jwildfire.base.WindowPrefs;

public class InternalFrameHolder<T extends JInternalFrame> {
  protected JCheckBoxMenuItem menuItem = null;
  protected T internalFrame;
  private final Desktop desktop;
  private final Class<T> frameType;
  private final String windowPrefsName;
  private final String menuCaption;

  public InternalFrameHolder(Class<T> frameType, Desktop desktop, String windowPrefsName, String menuCaption) {
    this.frameType = frameType;
    this.desktop = desktop;
    this.windowPrefsName = windowPrefsName;
    this.menuCaption = menuCaption;
  }

  public JCheckBoxMenuItem getMenuItem() {
    if (menuItem == null) {
      menuItem = new JCheckBoxMenuItem();
      menuItem.setText(menuCaption);
      menuItem.setEnabled(true);
      menuItem
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              menuItem_actionPerformed(e);
            }
          });
    }
    return menuItem;
  }

  private void menuItem_actionPerformed(java.awt.event.ActionEvent e) {
    if (internalFrame != null && menuItem != null) {
      if (menuItem.isSelected()) {
        internalFrame.setVisible(true);
        try {
          internalFrame.setSelected(true);
        }
        catch (PropertyVetoException ex) {
          ex.printStackTrace();
        }
      }
      else {
        internalFrame.setVisible(false);
      }
    }
  }

  public T getInternalFrame() {
    if (internalFrame == null) {
      try {
        internalFrame = frameType.newInstance();
      }
      catch (Exception ex) {
        Unchecker.rethrow(ex);
      }
      WindowPrefs wPrefs = Prefs.getPrefs().getWindowPrefs(windowPrefsName);
      internalFrame.setLocation(wPrefs.getLeft(), wPrefs.getTop());
      internalFrame.setSize(wPrefs.getWidth(1188), wPrefs.getHeight(740));
      internalFrame
          .addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            public void internalFrameDeactivated(
                javax.swing.event.InternalFrameEvent e) {
              desktop.enableControls();
            }

            public void internalFrameClosed(
                javax.swing.event.InternalFrameEvent e) {
              desktop.enableControls();
            }
          });
      try {
        internalFrame.setMaximum(wPrefs.isMaximized());
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    return internalFrame;
  }

  public void enableMenu() {
    if (internalFrame != null && menuItem != null) {
      menuItem.setSelected(internalFrame.isVisible());
    }
  }

  public void saveWindowPrefs() {
    if (internalFrame != null && internalFrame.isVisible()) {
      Dimension size = internalFrame.getSize();
      Point pos = internalFrame.getLocation();
      WindowPrefs wPrefs = Prefs.getPrefs().getWindowPrefs(windowPrefsName);
      wPrefs.setLeft(pos.x);
      wPrefs.setTop(pos.y);
      wPrefs.setWidth(size.width);
      wPrefs.setHeight(size.height);
      wPrefs.setMaximized(internalFrame.isMaximum());
    }
  }

  public Class<T> getFrameType() {
    return frameType;
  }
}
