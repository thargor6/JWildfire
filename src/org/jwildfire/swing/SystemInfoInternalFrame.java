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

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class SystemInfoInternalFrame extends JInternalFrame {
  private static final long serialVersionUID = 1L;
  private JTextPane textPane;
  /**
   * give components names so we can test them
   * This frame displays system info from the help menu, and should disappear when ok is clicked
   */
  public SystemInfoInternalFrame() {
	setName("siif");
    JPanel mainPane = new JPanel();
    mainPane.setName("siif.sysInfoPanel");
    getContentPane().add(mainPane, BorderLayout.CENTER);
    mainPane.setLayout(new BorderLayout(0, 0));
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setName("siif.scrollPane");
    mainPane.add(scrollPane, BorderLayout.CENTER);
    
    textPane = new JTextPane();
    textPane.setEditable(false);
    textPane.setName("siif.text");
    scrollPane.setViewportView(textPane);
    setTitle("System Information");
    setBounds(320, 140, 641, 499);
    textPane.setText(collectInfo());
    
    JButton button = new JButton("Ok");
    button.setName("siif.okbutton");
    button.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        setVisible(false);
      }
    });
    button.setPreferredSize(new Dimension(36, 36));
    getContentPane().add(button, BorderLayout.SOUTH);
    //southPanel.setLayout(null);

    button.addMouseListener(new MouseAdapter() {
    	
      @Override
      public void mouseClicked(MouseEvent e) {
    	  setVisible(false);
      }
    });
    pack();
  }
  public void refresh() {
	  textPane.setText(collectInfo());
  }
  private String collectInfo() {
    SystemInfo sysInfo = new SystemInfo();

    StringBuffer sb = new StringBuffer();
    sb.append("OS: "+sysInfo.getOsName()+" "+sysInfo.getOsVersion() +"["+sysInfo.getOsArch()+"]\n");
    sb.append("Committed Memory: "+sysInfo.getTotalMemMB()+" MB\n");
    sb.append("Max Memory: "+sysInfo.getMaxMemMB()+" MB\n");
    sb.append("Used Memory: "+sysInfo.getUsedMemMB()+" MB\n");
    sb.append("CPU count(incl hyperthread): "+sysInfo.getProcessors()+" core\n\n");
    return sb.toString();
  }
}
