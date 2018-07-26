/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2014 Andreas Maschke

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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.*;

import org.jwildfire.base.Prefs;

public class LookAndFeelFrame extends JFrame {
  private static final long serialVersionUID = 1L;
  private final ErrorHandler errorHandler;
  private final JWildfire desktop;
  private final JFrame rootPane;

  private final Prefs prefs;
  private JComboBox mainThemeCmb;
  private JComboBox subThemeCmb;
  private boolean noRefresh;
  private JButton applyToApplicationBtn;

  public LookAndFeelFrame(JWildfire pDesktop, JFrame pRootPane, ErrorHandler pErrorHandler, Prefs pPrefs) {
    desktop = pDesktop;
    noRefresh = true;
    rootPane = pRootPane;
    errorHandler = pErrorHandler;
    prefs = pPrefs;

    this.setSize(600, 400);

    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    JPanel northPanel = new JPanel();
    northPanel.setPreferredSize(new Dimension(10, 8));
    getContentPane().add(northPanel, BorderLayout.NORTH);
    northPanel.setLayout(null);

    JPanel southPanel = new JPanel();
    southPanel.setPreferredSize(new Dimension(36, 36));
    getContentPane().add(southPanel, BorderLayout.SOUTH);
    southPanel.setLayout(null);

    JButton saveSettingsBtn = new JButton();
    saveSettingsBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          LookAndFeelType lfType = (LookAndFeelType) getMainThemeCmb().getSelectedItem();
          if (lfType != null) {
            prefs.setLookAndFeelType(lfType);
            prefs.setLookAndFeelTheme((String) getSubThemeCmb().getSelectedItem());
          }

          prefs.saveToFile();
        }
        catch (Exception ex) {
          errorHandler.handleError(ex);
        }

        try {
          JWildfire.setUserLookAndFeel();
          applyThemeToApplication();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }

        setVisible(false);
        desktop.enableControls();
      }
    });
    saveSettingsBtn.setText("Save and Close");
    saveSettingsBtn.setPreferredSize(new Dimension(125, 24));
    saveSettingsBtn.setMnemonic(KeyEvent.VK_S);
    saveSettingsBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    saveSettingsBtn.setBounds(179, 6, 125, 24);
    southPanel.add(saveSettingsBtn);

    JButton cancelBtn = new JButton();
    cancelBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          JWildfire.setUserLookAndFeel();
          applyThemeToApplication();
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
        setVisible(false);
        desktop.enableControls();
      }
    });
    cancelBtn.setText("Cancel and Close");
    cancelBtn.setPreferredSize(new Dimension(125, 24));
    cancelBtn.setMnemonic(KeyEvent.VK_C);
    cancelBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    cancelBtn.setBounds(309, 6, 125, 24);
    southPanel.add(cancelBtn);

    JPanel panel_2 = new JPanel();
    getContentPane().add(panel_2, BorderLayout.CENTER);
    panel_2.setLayout(null);

    mainThemeCmb = new JComboBox();
    mainThemeCmb.setMaximumRowCount(24);
    mainThemeCmb.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        themeChanged();
      }
    });
    mainThemeCmb.setSize(new Dimension(125, 22));
    mainThemeCmb.setPreferredSize(new Dimension(125, 22));
    mainThemeCmb.setLocation(new Point(100, 4));
    mainThemeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    mainThemeCmb.setBounds(102, 6, 322, 24);
    panel_2.add(mainThemeCmb);

    JLabel lblMainTheme = new JLabel();
    lblMainTheme.setText("Main theme");
    lblMainTheme.setSize(new Dimension(94, 22));
    lblMainTheme.setPreferredSize(new Dimension(94, 22));
    lblMainTheme.setLocation(new Point(488, 2));
    lblMainTheme.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    lblMainTheme.setBounds(6, 6, 94, 22);
    panel_2.add(lblMainTheme);

    subThemeCmb = new JComboBox();
    subThemeCmb.setMaximumRowCount(24);
    subThemeCmb.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        subThemeChanged();
      }
    });
    subThemeCmb.setSize(new Dimension(125, 22));
    subThemeCmb.setPreferredSize(new Dimension(125, 22));
    subThemeCmb.setLocation(new Point(100, 4));
    subThemeCmb.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    subThemeCmb.setBounds(102, 33, 322, 24);
    panel_2.add(subThemeCmb);

    JLabel lblSubTheme = new JLabel();
    lblSubTheme.setText("Sub theme");
    lblSubTheme.setSize(new Dimension(94, 22));
    lblSubTheme.setPreferredSize(new Dimension(94, 22));
    lblSubTheme.setLocation(new Point(488, 2));
    lblSubTheme.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    lblSubTheme.setBounds(6, 33, 94, 22);
    panel_2.add(lblSubTheme);
    setTitle("UI Themes");
    setBounds(320, 140, 604, 136);
    fillThemeCmb(getMainThemeCmb());
    try {
      getMainThemeCmb().setSelectedItem(prefs.getLookAndFeelType());
      refreshSubThemesCmb(prefs.getLookAndFeelType());
      getSubThemeCmb().setSelectedItem(prefs.getLookAndFeelTheme());
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    applyToApplicationBtn = new JButton();
    applyToApplicationBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        applyThemeToApplication();
      }
    });
    applyToApplicationBtn.setToolTipText("Apply this theme to the whole application");
    applyToApplicationBtn.setText("Apply to application");
    applyToApplicationBtn.setPreferredSize(new Dimension(125, 24));
    applyToApplicationBtn.setMnemonic(KeyEvent.VK_S);
    applyToApplicationBtn.setFont(Prefs.getPrefs().getFont("Dialog", Font.BOLD, 10));
    applyToApplicationBtn.setBounds(436, 6, 147, 51);
    panel_2.add(applyToApplicationBtn);

    noRefresh = false;
  }

  protected void applyThemeToApplication() {

    //rootPane.updateUI();
    SwingUtilities.updateComponentTreeUI(rootPane);
    Window windows[] = Window.getWindows();
    for (int i = 0; i < windows.length; i++) {
      if (windows[i].isDisplayable()) {
        SwingUtilities.updateComponentTreeUI(windows[i]);
      }
    }
  }

  private void fillThemeCmb(JComboBox pCmb) {
    pCmb.removeAllItems();
    for (LookAndFeelType lfType : LookAndFeelType.values()) {
      pCmb.addItem(lfType);
    }
  }

  protected void subThemeChanged() {
    if (noRefresh)
      return;
    try {
      LookAndFeelType lfType = (LookAndFeelType) getMainThemeCmb().getSelectedItem();
      if (lfType != null) {
        String subTheme = (String) getSubThemeCmb().getSelectedItem();
        if (subTheme != null && subTheme.length() > 0) {
          lfType.changeTheme(subTheme);
        }
      }
      applyTheme();
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private static final String DEFAULT = "Default";

  protected void themeChanged() {
    if (noRefresh)
      return;
    try {
      LookAndFeelType lfType = (LookAndFeelType) getMainThemeCmb().getSelectedItem();
      if (lfType != null) {
        boolean oldNoRefresh = noRefresh;
        try {
          noRefresh = true;
          refreshSubThemesCmb(lfType);
        }
        finally {
          noRefresh = oldNoRefresh;
        }
        lfType.changeTo();
        subThemeChanged();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void refreshSubThemesCmb(LookAndFeelType lfType) {
    getSubThemeCmb().removeAllItems();
    List<String> subThemes = lfType.getFilteredSubThemes();
    for (String subTheme : subThemes) {
      getSubThemeCmb().addItem(subTheme);
    }
    if (subThemes.indexOf(DEFAULT) >= 0) {
      getSubThemeCmb().setSelectedItem(DEFAULT);
    }
    else if (subThemes.size() > 0) {
      getSubThemeCmb().setSelectedIndex(0);
    }
  }

  private void applyTheme() {
    SwingUtilities.updateComponentTreeUI(this);
  }

  public JComboBox getMainThemeCmb() {
    return mainThemeCmb;
  }

  public JComboBox getSubThemeCmb() {
    return subThemeCmb;
  }

  public JButton getApplyToApplicationBtn() {
    return applyToApplicationBtn;
  }
}
