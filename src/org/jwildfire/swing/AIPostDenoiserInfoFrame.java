/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.SystemColor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jwildfire.base.Prefs;

@SuppressWarnings("serial")
public class AIPostDenoiserInfoFrame extends JFrame {
  private JPanel jContentPane = null;

  public AIPostDenoiserInfoFrame() {
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
    this.setTitle("AI Post Denoiser Info");
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
      jContentPane.add(getMainTabbedPane(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  private JScrollPane optiXDenoiserScrollPane;
  private JTextPane optiXInfoPane;
  private JTabbedPane mainTabbedPane;
  private JScrollPane oidnDenoiserScrollPane;
  private JTextPane oidnInfoPane;

  private JScrollPane getOptiXDenoiserScrollPane() {
    if (optiXDenoiserScrollPane == null) {
      optiXDenoiserScrollPane = new JScrollPane();
      optiXDenoiserScrollPane.setPreferredSize(new Dimension(6, 400));
      optiXDenoiserScrollPane.setViewportView(getOptiXInfoPane());
    }
    return optiXDenoiserScrollPane;
  }

  JTextPane getOptiXInfoPane() {
    if (optiXInfoPane == null) {
      optiXInfoPane = new JTextPane();
      optiXInfoPane.setBackground(SystemColor.menu);
      optiXInfoPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 14));
      optiXInfoPane.addHyperlinkListener(new HyperlinkListener() {
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
      optiXInfoPane.setEditable(false);
    }
    return optiXInfoPane;
  }

  public void initOptiXInfoPane() {
    optiXInfoPane.setContentType("text/html");
    try {
      InputStream is = this.getClass().getResourceAsStream("OptiX_Denoiser.txt");
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();
      optiXInfoPane.setText("<pre>" + content.toString() + "</pre>");
      optiXInfoPane.setSelectionStart(0);
      optiXInfoPane.setSelectionEnd(0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void initOIDNInfoPane() {
    oidnInfoPane.setContentType("text/html");
    try {
      InputStream is = this.getClass().getResourceAsStream("OIDN_Denoiser.txt");
      StringBuffer content = new StringBuffer();
      String lineFeed = System.getProperty("line.separator");
      String line;
      Reader r = new InputStreamReader(is, "utf-8");
      BufferedReader in = new BufferedReader(r);
      while ((line = in.readLine()) != null) {
        content.append(line).append(lineFeed);
      }
      in.close();
      oidnInfoPane.setText("<pre>" + content.toString() + "</pre>");
      oidnInfoPane.setSelectionStart(0);
      oidnInfoPane.setSelectionEnd(0);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private JTabbedPane getMainTabbedPane() {
    if (mainTabbedPane == null) {
      mainTabbedPane = new JTabbedPane(JTabbedPane.TOP);
      mainTabbedPane.addTab("NVidia's OptiX Denoiser", null, getOptiXDenoiserScrollPane(), null);
      mainTabbedPane.addTab("Intel's OIDN Denoiser", null, getOidnDenoiserScrollPane(), null);
    }
    return mainTabbedPane;
  }

  private JScrollPane getOidnDenoiserScrollPane() {
    if (oidnDenoiserScrollPane == null) {
      oidnDenoiserScrollPane = new JScrollPane();
      oidnDenoiserScrollPane.setViewportView(getOidnInfoPane());
    }
    return oidnDenoiserScrollPane;
  }

  private JTextPane getOidnInfoPane() {
    if (oidnInfoPane == null) {
      oidnInfoPane = new JTextPane();
      oidnInfoPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 14));
      oidnInfoPane.addHyperlinkListener(new HyperlinkListener() {
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
      oidnInfoPane.setEditable(false);
    }
    return oidnInfoPane;
  }
}
