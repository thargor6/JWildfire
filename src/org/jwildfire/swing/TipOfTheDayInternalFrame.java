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
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.random.MarsagliaRandomGenerator;

public class TipOfTheDayInternalFrame extends JInternalFrame {
  private static final long serialVersionUID = 1L;
  private JTextPane helpPane = null;
  private JScrollPane scrollPane = null;
  private int prevTipIndex = -1;

  public TipOfTheDayInternalFrame() {
    setClosable(true);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setIconifiable(true);
    setResizable(true);
    getContentPane().setBackground(UIManager.getColor("Button.background"));

    JPanel northPanel = new JPanel();
    northPanel.setPreferredSize(new Dimension(10, 4));
    getContentPane().add(northPanel, BorderLayout.NORTH);
    northPanel.setLayout(null);

    JPanel southPanel = new JPanel();
    southPanel.setPreferredSize(new Dimension(100, 40));
    getContentPane().add(southPanel, BorderLayout.SOUTH);
    southPanel.setLayout(null);

    JButton websiteButton = new JButton("Next tip");
    websiteButton.setDefaultCapable(false);
    websiteButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showNextTip();
      }
    });
    websiteButton.setPreferredSize(new Dimension(128, 24));
    websiteButton.setBorder(null);
    websiteButton.setBorderPainted(false);
    websiteButton.setBounds(6, 6, 134, 28);
    southPanel.add(websiteButton);

    showTipsAtStartupCBx = new JCheckBox("Show tips at startup");
    showTipsAtStartupCBx.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Prefs.getPrefs().setShowTipsAtStartup(showTipsAtStartupCBx.isSelected());
        try {
          Prefs.getPrefs().saveToFile();
        }
        catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    });
    showTipsAtStartupCBx.setActionCommand("");
    showTipsAtStartupCBx.setBounds(152, 11, 169, 18);
    showTipsAtStartupCBx.setSelected(Prefs.getPrefs().isShowTipsAtStartup());
    southPanel.add(showTipsAtStartupCBx);

    JPanel panel_2 = new JPanel();
    panel_2.setLayout(new BorderLayout(0, 0));
    panel_2.add(getScrollPane());

    getContentPane().add(panel_2, BorderLayout.CENTER);
    initializeTips();
    setTitle("Tip of the day (" + tips.size() + " tips available for now)");
    setBounds(1000, 400, 366, 295);
    showNextTip();
  }

  private void showNextTip() {
    String content = loadNextTip();
    helpPane.setContentType("text/html");

    helpPane.setText(content);

    helpPane.setSelectionStart(0);
    helpPane.setSelectionEnd(0);
  }

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setPreferredSize(new Dimension(6, 400));
      scrollPane.setViewportView(getHelpPane());
    }
    return scrollPane;
  }

  private JTextPane getHelpPane() {
    if (helpPane == null) {
      helpPane = new JTextPane();
      helpPane.setBackground(SystemColor.menu);

      helpPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 12));
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

  private List<String> tips = null;
  private AbstractRandomGenerator randGen = new MarsagliaRandomGenerator();
  private JCheckBox showTipsAtStartupCBx;

  private void initializeTips() {
    if (tips == null) {
      tips = new ArrayList<>();

      try {
        InputStream is = this.getClass().getResourceAsStream("TipsOfTheDay.html");
        StringBuffer content = new StringBuffer();
        String lineFeed = System.getProperty("line.separator");
        String line;
        Reader r = new InputStreamReader(is, "utf-8");
        BufferedReader in = new BufferedReader(r);
        while ((line = in.readLine()) != null) {
          content.append(line).append(lineFeed);
        }
        in.close();

        int p0 = -1;
        while (true) {
          final String startToken = "<html";
          final String endToken = "</html>";
          int p1 = content.indexOf(startToken, p0 + 1);
          if (p1 < 0) {
            break;
          }
          int p2 = content.indexOf(endToken, p1 + 1);
          if (p2 < 0) {
            break;
          }

          String fragment = content.substring(p1, p2 + endToken.length());
          tips.add(fragment);

          p0 = p2 + endToken.length();
        }

      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private String loadNextTip() {
    initializeTips();
    if (tips.size() == 0) {
      return "";
    }
    else if (tips.size() == 1) {
      return tips.get(0);
    }
    else {
      int idx = 0;
      while (idx == prevTipIndex) {
        idx = randGen.random(tips.size());
      }
      prevTipIndex = idx;
      return tips.get(idx);
    }
  }

  public JCheckBox getShowTipsAtStartupCBx() {
    return showTipsAtStartupCBx;
  }
}
