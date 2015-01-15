package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.WindowPrefs;

public class DetachedPreviewWindow {
  private static final String DFLT_TITLE = "Detached preview";
  private DetachedPreviewController controller;
  private JFrame frame;
  private JPanel imageRootPanel;

  /**
   * Create the application.
   */
  public DetachedPreviewWindow() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frame = new JFrame();
    frame.addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) {
        onClose(true);
      }
    });

    frame.setTitle(DFLT_TITLE);
    frame.setBounds(100, 100, 450, 300);
    WindowPrefs wPrefs = Prefs.getPrefs().getWindowPrefs(WindowPrefs.WINDOW_TINA_PREVIEW);
    frame.setLocation(wPrefs.getLeft(), wPrefs.getTop());
    frame.setSize(wPrefs.getWidth(450), wPrefs.getHeight(300));

    frame.setAlwaysOnTop(true);
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    frame.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(java.awt.event.ComponentEvent e) {
        if (getController() != null) {
          getController().startRender();
        }
      }
    });

    imageRootPanel = new JPanel();
    imageRootPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          getController().togglePause();
        }
      }
    });
    frame.getContentPane().add(imageRootPanel, BorderLayout.CENTER);

    configureHotKeys();
  }

  private void configureHotKeys() {
    frame.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("SPACE"),
        "toggleRender");
    Action toggleRender = new AbstractAction() {
      private static final long serialVersionUID = 1L;

      public void actionPerformed(ActionEvent e) {
        getController().togglePause();
      }
    };
    frame.getRootPane().getActionMap().put("toggleRender",
        toggleRender);
  }

  protected void onClose(boolean doNotify) {
    try {
      Dimension size = frame.getSize();
      Point pos = frame.getLocation();
      WindowPrefs wPrefs = Prefs.getPrefs().getWindowPrefs(WindowPrefs.WINDOW_TINA_PREVIEW);
      wPrefs.setLeft(pos.x);
      wPrefs.setTop(pos.y);
      wPrefs.setWidth(size.width);
      wPrefs.setHeight(size.height);
      if (doNotify)
        getController().notifyWindowIsClosing();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public JPanel getImageRootPanel() {
    return imageRootPanel;
  }

  public JFrame getFrame() {
    return frame;
  }

  public DetachedPreviewController getController() {
    return controller;
  }

  public void setController(DetachedPreviewController controller) {
    this.controller = controller;
  }

  public void setTitle(DetachedPreviewController.State pState, double pQuality) {
    switch (pState) {
      case PAUSE:
        getFrame().setTitle("Quality " + Tools.doubleToString(Tools.FTOI(pQuality)) + " (dbl-click/SPACE to continue)");
        break;
      case RENDER:
        getFrame().setTitle("Quality " + Tools.doubleToString(Tools.FTOI(pQuality)) + " (dbl-click/SPACE to pause)");
        break;
      default:
        getFrame().setTitle(DFLT_TITLE);
    }
  }

}
