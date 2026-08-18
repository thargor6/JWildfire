/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2022 Andreas Maschke

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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jwildfire.base.Prefs;

import org.jwildfire.create.tina.variation.VariationFuncList;

@SuppressWarnings("serial")
public class VariationListFrame extends JFrame {
  private JPanel jContentPane = null;

  public VariationListFrame() {
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
    this.setTitle("Variation List (" + VariationFuncList.getNameList().size() + " variations)");
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
      jContentPane.add(getScrollPane(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  private JScrollPane scrollPane;
  private JTextPane variationPane;

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setPreferredSize(new Dimension(6, 400));
      scrollPane.setViewportView(getVariationPane());
    }
    return scrollPane;
  }

  JTextPane getVariationPane() {
    if (variationPane == null) {
      variationPane = new JTextPane();
      variationPane.setFont(Prefs.getPrefs().getFont("SansSerif", Font.PLAIN, 16));
      variationPane.addHyperlinkListener(new HyperlinkListener() {
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
      variationPane.setEditable(false);
    }
    return variationPane;
  }

  public void initVariationPane() {
  	String[] variations = new String[VariationFuncList.getNameList().size()];
  	int i = 0;
  	for (String var : VariationFuncList.getNameList()) {
  		variations[i] = var;
  		i++;
  	}
  	Arrays.sort(variations);
  	StringBuffer content = new StringBuffer();
  	for (i=0; i < variations.length-1; i++) {
  		content.append(variations[i] + ", ");
  	}
  	content.append(variations[variations.length-1]);
  	variationPane.setText(content.toString());
  }
}
