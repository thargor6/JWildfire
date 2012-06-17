/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2012 Andreas Maschke

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class SystemInfoInternalFrame extends JInternalFrame {
  private static final long serialVersionUID = 1L;

  public SystemInfoInternalFrame() {
    JPanel northPanel = new JPanel();

    northPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        setVisible(false);
      }
    });
    northPanel.setPreferredSize(new Dimension(10, 8));
    getContentPane().add(northPanel, BorderLayout.NORTH);
    northPanel.setLayout(null);

    JPanel southPanel = new JPanel();
    southPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        setVisible(false);
      }
    });
    southPanel.setPreferredSize(new Dimension(36, 36));
    getContentPane().add(southPanel, BorderLayout.SOUTH);
    southPanel.setLayout(null);

    JPanel panel_2 = new JPanel();
    panel_2.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        setVisible(false);
      }
    });
    getContentPane().add(panel_2, BorderLayout.CENTER);
    panel_2.setLayout(new BorderLayout(0, 0));

    JScrollPane scrollPane = new JScrollPane();
    panel_2.add(scrollPane, BorderLayout.CENTER);

    JTextPane textPane = new JTextPane();
    textPane.addHyperlinkListener(new HyperlinkListener() {
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
    scrollPane.setViewportView(textPane);
    setTitle("System Information");
    setBounds(320, 140, 641, 499);

    collectInfo();
  }

  private void collectInfo() {
    SystemInfo sysInfo = new SystemInfo();

    StringBuffer sb = new StringBuffer();
    sb.append("<html><body>");
    sb.append("<h1>System Information: </h1>");
    sb.append("</body></html>");

  }
}
