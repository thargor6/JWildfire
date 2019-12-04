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
import java.awt.SystemColor;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.swing.JWildfire;

public class HelpFrame extends JFrame {
  private JPanel jContentPane = null;
  private JTabbedPane rootTabbedPane = null;

  public HelpFrame() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1188, 740);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(JWildfire.DEFAULT_WINDOW_LEFT, JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Hints, Help and About");
    this.setVisible(false);
    this.setResizable(true);
    this.setContentPane(getJContentPane());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      jContentPane.setSize(new Dimension(1097, 617));
      jContentPane.add(getRootTabbedPane(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes rootTabbedPane	
   * 	
   * @return javax.swing.JTabbedPane	
   */
  private JTabbedPane getRootTabbedPane() {
    if (rootTabbedPane == null) {
      rootTabbedPane = new JTabbedPane();
      rootTabbedPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
      rootTabbedPane.setEnabled(true);
      // "FFmpeg Video Encoder" "emblem-videos.png"
      JPanel helpPanel = new JPanel();
      rootTabbedPane.addTab("Hints, Help and About ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/dialog-information-3.png")), helpPanel, null);
      helpPanel.setLayout(new BorderLayout(0, 0));

      JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
      helpPanel.add(tabbedPane_1);

      JPanel panel_1 = new JPanel();
      tabbedPane_1.addTab("About JWildfire ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/dialog-information-3.png")), panel_1, null);
      panel_1.setLayout(new BorderLayout(0, 0));
      panel_1.add(getScrollPane(), BorderLayout.CENTER);
      tabbedPane_1.addTab("Tips for Apophysis users ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/dialog-information-2.png")), getPanel_103(), null);
      tabbedPane_1.addTab("3DMesh Generation tips ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/sports-soccer.png")), getPanel_105(), null);
      tabbedPane_1.addTab("JWildfire Coloring Types ", new ImageIcon(MainEditorFrame.class.getResource("/org/jwildfire/swing/icons/new/color-wheel.png")), getPanel_109(), null);
    }
    return rootTabbedPane;
  }

  private JScrollPane scrollPane;
  private JTextPane helpPane;
  private JScrollPane scrollPane_10;
  private JTextPane meshGenHintPane;
  private JPanel panel_103;
  private JPanel panel_105;
  private JPanel panel_109;
  private JScrollPane scrollPane_11;
  private JScrollPane scrollPane_12;
  private JTextPane apophysisHintsPane;
  private JTextPane colorTypesPane;

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setPreferredSize(new Dimension(6, 400));
      scrollPane.setViewportView(getHelpPane());
    }
    return scrollPane;
  }

  JTextPane getHelpPane() {
    if (helpPane == null) {
      helpPane = new JTextPane();
      helpPane.setBackground(SystemColor.menu);
      helpPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 14));
      helpPane.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
              java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        }
      });
      helpPane.setEditable(false);
    }
    return helpPane;
  }

  private JScrollPane getScrollPane_10() {
    if (scrollPane_10 == null) {
      scrollPane_10 = new JScrollPane();
      scrollPane_10.setViewportView(getMeshGenHintPane());
    }
    return scrollPane_10;
  }

  JTextPane getMeshGenHintPane() {
    if (meshGenHintPane == null) {
      meshGenHintPane = new JTextPane();
      meshGenHintPane.setBackground(SystemColor.menu);
      meshGenHintPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 14));
      meshGenHintPane.setEditable(false);
    }
    return meshGenHintPane;
  }

  private JPanel getPanel_103() {
    if (panel_103 == null) {
      panel_103 = new JPanel();
      panel_103.setLayout(new BorderLayout(0, 0));
      panel_103.add(getScrollPane_11(), BorderLayout.CENTER);
    }
    return panel_103;
  }

  private JPanel getPanel_105() {
    if (panel_105 == null) {
      panel_105 = new JPanel();
      panel_105.setLayout(new BorderLayout(0, 0));
      panel_105.add(getScrollPane_10(), BorderLayout.CENTER);
    }
    return panel_105;
  }

  private JPanel getPanel_109() {
    if (panel_109 == null) {
      panel_109 = new JPanel();
      panel_109.setLayout(new BorderLayout(0, 0));
      panel_109.add(getScrollPane_12(), BorderLayout.CENTER);
    }
    return panel_109;
  }

  private JScrollPane getScrollPane_11() {
    if (scrollPane_11 == null) {
      scrollPane_11 = new JScrollPane();
      scrollPane_11.setViewportView(getApophysisHintsPane());
    }
    return scrollPane_11;
  }

  private JScrollPane getScrollPane_12() {
    if (scrollPane_12 == null) {
      scrollPane_12 = new JScrollPane();
      scrollPane_12.setViewportView(getColorTypesPane());
    }
    return scrollPane_12;
  }

  JTextPane getApophysisHintsPane() {
    if (apophysisHintsPane == null) {
      apophysisHintsPane = new JTextPane();
      apophysisHintsPane.setBackground(SystemColor.menu);
      apophysisHintsPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 14));
      apophysisHintsPane.setEditable(false);
    }
    return apophysisHintsPane;
  }

  JTextPane getColorTypesPane() {
    if (colorTypesPane == null) {
      colorTypesPane = new JTextPane();
      colorTypesPane.setBackground(SystemColor.menu);
      colorTypesPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 14));
      colorTypesPane.setEditable(false);
    }
    return colorTypesPane;
  }

  public void setTinaController(TinaController tinaController) {
  }

}
