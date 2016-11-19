package org.jwildfire.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.WindowPrefs;

public abstract class InternalFrameHolder<T extends JInternalFrame> {
  protected JCheckBoxMenuItem menuItem = null;
  protected T internalFrame;
  protected final JWildfire desktop;
  protected final Class<T> frameType;
  protected final String windowPrefsName;
  protected final String menuCaption;

  public InternalFrameHolder(Class<T> frameType, JWildfire desktop, String windowPrefsName, String menuCaption) {
    this.frameType = frameType;
    this.desktop = desktop;
    this.windowPrefsName = windowPrefsName;
    this.menuCaption = menuCaption;
  }

  protected void menuItem_actionPerformed(java.awt.event.ActionEvent e) {
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

  public void enableMenu() {
    if (internalFrame != null && menuItem != null) {
      menuItem.setSelected(internalFrame.isVisible());
    }
  }

  public void saveWindowPrefs() {
    if (internalFrame != null) {
      Dimension size = internalFrame.getSize();
      Point pos = internalFrame.getLocation();
      WindowPrefs wPrefs = Prefs.getPrefs().getWindowPrefs(windowPrefsName);
      boolean visible = internalFrame.isVisible() && !internalFrame.isClosed() && !internalFrame.isIcon();
      if (visible) {
        wPrefs.setLeft(pos.x);
        wPrefs.setTop(pos.y);
        wPrefs.setWidth(size.width);
        wPrefs.setHeight(size.height);
        wPrefs.setMaximized(internalFrame.isMaximum());
      }
      wPrefs.setVisible(visible);
    }
  }

  public Class<T> getFrameType() {
    return frameType;
  }

  public JCheckBoxMenuItem getMenuItem() {
    if (menuItem == null) {
      menuItem = createMenuItem();
    }
    return menuItem;
  }

  public T getInternalFrame() {
    if (internalFrame == null) {
      internalFrame = createInternalFrame();
    }
    return internalFrame;
  }

  protected JCheckBoxMenuItem createMenuItem() {
    JCheckBoxMenuItem item = new JCheckBoxMenuItem();
    item.setText(menuCaption);
    item.setEnabled(true);
    item
        .addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent e) {
            menuItem_actionPerformed(e);
          }
        });
    return item;
  }

  protected void applyWindowPrefs(JInternalFrame frame) {
    WindowPrefs wPrefs = Prefs.getPrefs().getWindowPrefs(windowPrefsName);
    boolean hasPrefs = wPrefs.getWidth(-1) > 0;
    if (hasPrefs)
      frame.setLocation(wPrefs.getLeft(), wPrefs.getTop());
    frame.setSize(wPrefs.getWidth(frame.getSize().width), wPrefs.getHeight(frame.getSize().height));
    try {
      frame.setMaximum(wPrefs.isMaximized());
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    /*    
        try {
          frame.setVisible(wPrefs.isVisible());
        }
        catch (Exception e) {
          e.printStackTrace();
        }
    */
  }

  protected abstract T createInternalFrame();
}
