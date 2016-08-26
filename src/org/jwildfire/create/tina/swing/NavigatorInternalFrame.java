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

import com.l2fprod.common.demo.OutlookBarMain;
import com.l2fprod.common.swing.JOutlookBar;
import com.l2fprod.common.swing.PercentLayout;

public class NavigatorInternalFrame extends JInternalFrame {
  private TinaController tinaController;
  private JPanel jContentPane = null;

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

  public void setTinaController(TinaController tinaController) {
    this.tinaController = tinaController;
  }

  JPanel makeOutlookPanel(int alignment) {
    JOutlookBar outlook = new JOutlookBar();
    outlook.setTabPlacement(JTabbedPane.LEFT);
    addTab(outlook, "Flames");
    addTab(outlook, "Misc");
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

  void addTab(JOutlookBar tabs, String title) {
    JPanel panel = new JPanel();
    panel.setLayout(new PercentLayout(PercentLayout.VERTICAL, 0));
    panel.setOpaque(false);

    String[] buttons = new String[] {
        "Flame editor", "/org/jwildfire/swing/icons/new/brick2.png",
        "Interactive renderer", "/org/jwildfire/swing/icons/new/fraqtive.png",
        "Flame browser", "/org/jwildfire/swing/icons/new/application-view-tile.png",
        "Easy movie maker", "/org/jwildfire/swing/icons/new/applications-multimedia.png",
        "Dancing flames movies", "/org/jwildfire/swing/icons/new/kipina.png",
        "Dancing flames movies", "/org/jwildfire/swing/icons/new/kipina.png",
        "Batch flame renderer", "/org/jwildfire/swing/icons/new/images.png",
        "3DMesh generation", "/org/jwildfire/swing/icons/new/sports-soccer.png",
    };

    for (int i = 0, c = buttons.length; i < c; i += 2) {
      JButton button = new JButton(buttons[i]);
      try {
        //        button.setUI((ButtonUI) Class.forName(
        //            (String) UIManager.get("OutlookButtonUI")).newInstance());
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      button.setIcon(new ImageIcon(OutlookBarMain.class
          .getResource(buttons[i + 1])));
      panel.add(button);
    }

    JScrollPane scroll = tabs.makeScrollPane(panel);
    tabs.addTab("", scroll);

    // this to test the UI gets notified of changes
    int index = tabs.indexOfComponent(scroll);
    tabs.setTitleAt(index, title);
    tabs.setToolTipTextAt(index, title + " Tooltip");
  }

}
