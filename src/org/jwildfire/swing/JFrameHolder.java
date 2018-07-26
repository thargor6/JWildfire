package org.jwildfire.swing;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.WindowPrefs;

import javax.swing.*;
import java.awt.*;

public abstract class JFrameHolder<T extends JFrame> implements FrameHolder {
  protected JCheckBoxMenuItem menuItem = null;
  protected T frame;
  protected final JWildfire desktop;
  protected final Class<T> frameType;
  protected final String windowPrefsName;
  protected final String menuCaption;

  public JFrameHolder(Class<T> frameType, JWildfire desktop, String windowPrefsName, String menuCaption) {
    this.frameType = frameType;
    this.desktop = desktop;
    this.windowPrefsName = windowPrefsName;
    this.menuCaption = menuCaption;
  }

  protected void menuItem_actionPerformed(java.awt.event.ActionEvent e) {
    if (frame != null && menuItem != null) {
      if (menuItem.isSelected()) {
        frame.setVisible(true);
        frame.toFront();
      }
      else {
        frame.setVisible(false);
      }
    }
  }

  @Override
  public void enableMenu() {
    if (frame != null && menuItem != null) {
      menuItem.setSelected(frame.isVisible());
    }
  }

  @Override
  public void saveWindowPrefs() {
    if (frame != null) {
      Dimension size = frame.getSize();
      Point pos = frame.getLocation();
      WindowPrefs wPrefs = Prefs.getPrefs().getWindowPrefs(windowPrefsName);
      boolean visible = frame.isVisible()/* && !frame.isClosed() && !frame.isIcon()*/;
      if (visible) {
        wPrefs.setLeft(pos.x);
        wPrefs.setTop(pos.y);
        wPrefs.setWidth(size.width);
        wPrefs.setHeight(size.height);
        //wPrefs.setMaximized(frame.isMaximum());
      }
      wPrefs.setVisible(visible);
    }
  }

  public Class<T> getFrameType() {
    return frameType;
  }

  @Override
  public JCheckBoxMenuItem getMenuItem() {
    if (menuItem == null) {
      menuItem = createMenuItem();
    }
    return menuItem;
  }

  public T getFrame() {
    if (frame == null) {
      frame = createFrame();
    }
    return frame;
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

  protected void applyWindowPrefs(JFrame frame) {
    WindowPrefs wPrefs = Prefs.getPrefs().getWindowPrefs(windowPrefsName);
    boolean hasPrefs = wPrefs.getWidth(-1) > 0;
    if (hasPrefs) {
      frame.setLocation(wPrefs.getLeft(), wPrefs.getTop());
      frame.setSize(wPrefs.getWidth(frame.getSize().width), wPrefs.getHeight(frame.getSize().height));
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

  protected abstract T createFrame();
}
