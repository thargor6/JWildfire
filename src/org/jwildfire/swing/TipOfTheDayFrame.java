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

public class TipOfTheDayFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private JTextPane helpPane = null;
  private JScrollPane scrollPane = null;
  private int prevTipIndex = -1;

  public TipOfTheDayFrame() {
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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

    JButton nextTipButton = new JButton("Next tip");
    nextTipButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    nextTipButton.setDefaultCapable(false);
    nextTipButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showTip(loadNextTip());
      }
    });
    nextTipButton.setPreferredSize(new Dimension(128, 24));
    nextTipButton.setBorder(null);
    nextTipButton.setBorderPainted(false);
    nextTipButton.setBounds(103, 6, 92, 28);
    southPanel.add(nextTipButton);

    showTipsAtStartupCBx = new JCheckBox("Show tips at startup");
    showTipsAtStartupCBx.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    showTipsAtStartupCBx.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Prefs.getPrefs().setShowTipsAtStartup(showTipsAtStartupCBx.isSelected());
        try {
          Prefs.getPrefs().saveToFile();
        }
        catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    });
    showTipsAtStartupCBx.setActionCommand("");
    showTipsAtStartupCBx.setBounds(200, 11, 148, 18);
    showTipsAtStartupCBx.setSelected(Prefs.getPrefs().isShowTipsAtStartup());
    southPanel.add(showTipsAtStartupCBx);

    JButton prevTipButton = new JButton("Prev tip");
    prevTipButton.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    prevTipButton.setPreferredSize(new Dimension(128, 24));
    prevTipButton.setDefaultCapable(false);
    prevTipButton.setBorderPainted(false);
    prevTipButton.setBounds(6, 6, 92, 28);
    prevTipButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showTip(loadPrevTip());
      }
    });

    southPanel.add(prevTipButton);

    JPanel panel_2 = new JPanel();
    panel_2.setLayout(new BorderLayout(0, 0));
    panel_2.add(getScrollPane());

    getContentPane().add(panel_2, BorderLayout.CENTER);
    initializeTips();
    setTitle("Tip of the day (" + tips.size() + " tips available for now)");
    setBounds(1000, 400, 366, 295);
    showTip(loadNextTip());
  }

  private void showTip(String content) {
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

      helpPane.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
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
      
      prevTipIndex = Prefs.getPrefs().getLastTip();
      if (prevTipIndex < 0 || prevTipIndex >= tips.size())
          prevTipIndex = 0;
      Prefs.getPrefs().setLastTip(prevTipIndex + 1);
      prevTipIndex -= 1;
      try {
        Prefs.getPrefs().saveToFile();
      }
      catch (Exception e1) {
        e1.printStackTrace();
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
      prevTipIndex++;
      if (prevTipIndex >= tips.size()) {
        prevTipIndex = tips.size() - 1;
      }
      return tips.get(prevTipIndex);
    }
  }

  private String loadPrevTip() {
    initializeTips();
    if (tips.size() == 0) {
      return "";
    }
    else if (tips.size() == 1) {
      return tips.get(0);
    }
    else {
      prevTipIndex--;
      if (prevTipIndex < 0) {
        prevTipIndex = 0;
      }
      return tips.get(prevTipIndex);
    }
  }

  public JCheckBox getShowTipsAtStartupCBx() {
    return showTipsAtStartupCBx;
  }
}
