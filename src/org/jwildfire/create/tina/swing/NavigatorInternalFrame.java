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
package org.jwildfire.create.tina.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.Desktop;

import com.l2fprod.common.demo.OutlookBarMain;
import com.l2fprod.common.swing.JOutlookBar;
import com.l2fprod.common.swing.PercentLayout;

public class NavigatorInternalFrame extends JInternalFrame {
  private JPanel jContentPane = null;
  private Desktop desktop;

  public NavigatorInternalFrame() {
    super();
    initialize();
  }

  private void initialize() {
    this.setSize(120, 400);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(0, 0));
    this.setClosable(false);
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.setIconifiable(true);
    this.setTitle("Navigator");
    this.setVisible(true);
    this.setResizable(true);
    this.setIconifiable(false);
    this.setMaximizable(false);
    this.setContentPane(getJContentPane());
  }

  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      jContentPane.setSize(new Dimension(1097, 617));
      jContentPane.add(makeOutlookPanel(SwingConstants.CENTER), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  JPanel makeOutlookPanel(int alignment) {
    JOutlookBar outlook = new JOutlookBar();
    outlook.setTabPlacement(JTabbedPane.LEFT);
    addFlameWindowsTab(outlook, "Flames");
    /*
        // show it is possible to add any component to the bar
        JTree tree = new JTree();
        outlook.addTab("A JTree", outlook.makeScrollPane(tree));

        outlook.addTab("Disabled", new JButton());
        outlook.setEnabledAt(3, false);
        outlook.setAllTabsAlignment(alignment);
    */
    JPanel panel = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 3));
    panel.add(outlook, "100");
    return panel;
  }

  private static class WindowEntry {
    private final String caption;
    private final String iconpath;
    private final Class<? extends JInternalFrame> frameType;

    public WindowEntry(String caption, String iconpath, Class<? extends JInternalFrame> frameType) {
      super();
      this.caption = caption;
      this.iconpath = iconpath;
      this.frameType = frameType;
    }

    public String getCaption() {
      return caption;
    }

    public String getIconpath() {
      return iconpath;
    }

    public Class<? extends JInternalFrame> getFrameType() {
      return frameType;
    }

  }

  void addFlameWindowsTab(JOutlookBar tabs, String title) {
    JPanel panel = new JPanel();
    panel.setLayout(new PercentLayout(PercentLayout.VERTICAL, 0));
    panel.setOpaque(false);

    WindowEntry[] windows = new WindowEntry[] {
        new WindowEntry("Editor", "/org/jwildfire/swing/icons/new/brick2.png", TinaInternalFrame.class),
        new WindowEntry("IR", "/org/jwildfire/swing/icons/new/fraqtive.png", InteractiveRendererInternalFrame.class),
        new WindowEntry("Browser", "/org/jwildfire/swing/icons/new/application-view-tile.png", FlameBrowserInternalFrame.class),
        new WindowEntry("Movie maker", "/org/jwildfire/swing/icons/new/applications-multimedia.png", EasyMovieMakerInternalFrame.class),
        new WindowEntry("Dancing flames", "/org/jwildfire/swing/icons/new/kipina.png", DancingFlamesInternalFrame.class),
        new WindowEntry("Batch renderer", "/org/jwildfire/swing/icons/new/images.png", BatchFlameRendererInternalFrame.class),
        new WindowEntry("MutaGen", "/org/jwildfire/swing/icons/new/kdissert.png", MutaGenInternalFrame.class),
        new WindowEntry("3DMeshGen", "/org/jwildfire/swing/icons/new/sports-soccer.png", MeshGenInternalFrame.class)
    };

    for (final WindowEntry window : windows) {
      JButton button = new JButton(window.getCaption());
      button.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      button.setIcon(new ImageIcon(OutlookBarMain.class.getResource(window.getIconpath())));
      button.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          desktop.showInternalFrame(window.getFrameType());
        }
      });

      panel.add(button);
    }

    JScrollPane scroll = tabs.makeScrollPane(panel);
    tabs.addTab("", scroll);

    // this to test the UI gets notified of changes
    int index = tabs.indexOfComponent(scroll);
    tabs.setTitleAt(index, title);
    tabs.setToolTipTextAt(index, title + " Tooltip");
  }

  public void setDesktop(Desktop desktop) {
    this.desktop = desktop;
  }

}
