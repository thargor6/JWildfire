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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.variation.RessourceManager;

public class SystemInfoFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private JTextPane textPane;
  private JButton clearCacheButton;

  /**
   * give components names so we can test them
   * This frame displays system info from the help menu, and should disappear when ok is clicked
   */
  public SystemInfoFrame() {

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowActivated(WindowEvent e) {
        refresh();
      }
    });

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setResizable(true);
    setName("siif");

    JButton refreshButton = new JButton("Refresh");
    refreshButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        refresh();
      }
    });
    refreshButton.setPreferredSize(new Dimension(36, 36));
    refreshButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));

    getContentPane().add(refreshButton, BorderLayout.NORTH);
    JPanel mainPane = new JPanel();
    mainPane.setName("siif.sysInfoPanel");
    getContentPane().add(mainPane, BorderLayout.CENTER);
    mainPane.setLayout(new BorderLayout(0, 0));

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setName("siif.scrollPane");
    mainPane.add(scrollPane, BorderLayout.CENTER);

    textPane = new JTextPane();
    textPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    textPane.setEditable(false);
    textPane.setName("siif.text");
    scrollPane.setViewportView(textPane);
    setTitle("System Information");
    setBounds(320, 140, 297, 208);
    textPane.setText(collectInfo());

    clearCacheButton = new JButton("Clear cache");
    clearCacheButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        RessourceManager.clearAll();
        System.gc();
        refresh();
      }
    });
    clearCacheButton.setPreferredSize(new Dimension(36, 36));
    clearCacheButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    getContentPane().add(clearCacheButton, BorderLayout.SOUTH);
    pack();
  }

  public void refresh() {
    textPane.setText(collectInfo());
  }

  private String collectInfo() {
    StringBuffer sb = new StringBuffer();
    sb.append("Operating system: " + System.getProperty("os.name") + "\n");
    sb.append("Available processors: " + Runtime.getRuntime().availableProcessors() + " cores\n\n");
    long allocatedMemory =
        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
    sb.append("Maximum memory: " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "no limit" : formatMemoryInGB(Runtime.getRuntime().maxMemory()) + " GB") + "\n");
    sb.append("Free memory (approximated): " + formatMemoryInGB(presumableFreeMemory) + " GB\n\n");
    sb.append("Press the [Clear cache]-button to free any resources (images, meshes, fonts, ...) which are currently hold in memory in order to speed up future calculations.\n\n");
    return sb.toString();
  }

  private String formatMemoryInGB(long memory) {
    NumberFormat fmt = DecimalFormat.getInstance(Locale.US);
    fmt.setGroupingUsed(false);
    fmt.setMaximumFractionDigits(1);
    fmt.setMinimumIntegerDigits(1);
    return fmt.format(memory / 1024.0 / 1024.0 / 1024.0);
  }

  public JButton getClearCacheButton() {
    return clearCacheButton;
  }
}
