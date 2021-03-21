/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2017 Andreas Maschke

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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.variation.VariationFuncList;
import org.jwildfire.swing.JWildfire;

public class VariationProfilesFrame extends JFrame {
  private final VariationProfilesController controller;
  private static final long serialVersionUID = 1L;
  private JPanel jContentPane = null;
  private JPanel mainPanel = null;
  private JScrollPane variationsScrollPane = null;

  public VariationProfilesFrame(VariationProfilesController controller) {
    super();
    this.controller = controller;
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(1195, 766);
    this.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
    this.setLocation(new Point(200 + JWildfire.DEFAULT_WINDOW_LEFT, 50 + JWildfire.DEFAULT_WINDOW_TOP));
    this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    this.setTitle("Variation profiles");
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
      jContentPane.add(getMainPanel(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes batchRenderPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getMainPanel() {
    if (mainPanel == null) {
      mainPanel = new JPanel();
      mainPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new EmptyBorder(10, 10, 10, 10));
      mainPanel.add(panel_1);
      panel_1.setLayout(new BorderLayout(0, 0));

      JPanel editPanel = new JPanel();
      editPanel.setBorder(new TitledBorder(null, "Editing variation profile", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      editPanel.setPreferredSize(new Dimension(200, 10));
      editPanel.setSize(new Dimension(200, 0));
      panel_1.add(editPanel, BorderLayout.CENTER);
      editPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_2 = new JPanel();
      panel_2.setPreferredSize(new Dimension(72, 72));
      editPanel.add(panel_2, BorderLayout.NORTH);
      panel_2.setLayout(null);

      JLabel lblProfiueltype = new JLabel();
      lblProfiueltype.setToolTipText("");
      lblProfiueltype.setText("Profile type");
      lblProfiueltype.setPreferredSize(new Dimension(120, 22));
      lblProfiueltype.setFont(null);
      lblProfiueltype.setAlignmentX(1.0f);
      lblProfiueltype.setBounds(279, 10, 81, 16);
      panel_2.add(lblProfiueltype);

      profileTypeCmb = new JComboBox();
      profileTypeCmb.setToolTipText("Random-flame-generator");
      profileTypeCmb.setPreferredSize(new Dimension(130, 24));
      profileTypeCmb.setMinimumSize(new Dimension(110, 24));
      profileTypeCmb.setMaximumSize(new Dimension(32767, 24));
      profileTypeCmb.setMaximumRowCount(48);
      profileTypeCmb.setFont(null);
      profileTypeCmb.setBounds(351, 6, 241, 24);
      profileTypeCmb.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            controller.profileTypeCmbChanged();
          }
        }
      });

      panel_2.add(profileTypeCmb);

      profileNameEdit = new JTextField();
      profileNameEdit.getDocument().addDocumentListener(new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
          textChanged();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          textChanged();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          textChanged();
        }

        private void textChanged() {
          controller.profileNameChanged();
        }
      });
      profileNameEdit.setBounds(90, 4, 161, 28);
      panel_2.add(profileNameEdit);
      profileNameEdit.setColumns(10);

      JLabel lblProfileName = new JLabel();
      lblProfileName.setToolTipText("");
      lblProfileName.setText("Profile name");
      lblProfileName.setPreferredSize(new Dimension(120, 22));
      lblProfileName.setFont(null);
      lblProfileName.setAlignmentX(1.0f);
      lblProfileName.setBounds(4, 10, 81, 16);
      panel_2.add(lblProfileName);

      JLabel lblStatus = new JLabel();
      lblStatus.setToolTipText("");
      lblStatus.setText("Status");
      lblStatus.setPreferredSize(new Dimension(120, 22));
      lblStatus.setFont(null);
      lblStatus.setAlignmentX(1.0f);
      lblStatus.setBounds(4, 38, 81, 16);
      panel_2.add(lblStatus);

      profileStatusEdit = new JTextField();
      profileStatusEdit.setEnabled(false);
      profileStatusEdit.setEditable(false);
      profileStatusEdit.setColumns(10);
      profileStatusEdit.setBounds(90, 32, 618, 28);
      panel_2.add(profileStatusEdit);

      defaultCheckbox = new JCheckBox("Default profile");
      defaultCheckbox.setActionCommand("");
      defaultCheckbox.setBounds(604, 9, 104, 18);
      defaultCheckbox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          controller.defaultCheckboxChanged();
        }
      });

      panel_2.add(defaultCheckbox);
      editPanel.add(getVariationsScrollPane(), BorderLayout.CENTER);

      JPanel profilesPanel = new JPanel();
      profilesPanel.setBorder(new TitledBorder(null, "Variation profile", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      profilesPanel.setPreferredSize(new Dimension(400, 10));
      panel_1.add(profilesPanel, BorderLayout.WEST);profilesPanel.setLayout(new BorderLayout(0, 0));

      JPanel panel_3 = new JPanel();
      panel_3.setBounds(new Rectangle(4, 4, 4, 4));
      panel_3.setPreferredSize(new Dimension(128, 10));
      profilesPanel.add(panel_3, BorderLayout.EAST);

      newProfileBtn = new JButton();
      panel_3.add(newProfileBtn);
      newProfileBtn.setToolTipText("");
      newProfileBtn.setText("New profile");
      newProfileBtn.setPreferredSize(new Dimension(120, 24));
      newProfileBtn.setMinimumSize(new Dimension(209, 24));
      newProfileBtn.setMaximumSize(new Dimension(209, 24));
      newProfileBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      newProfileBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          controller.newProfileClicked();
        }
      });

      duplicateProfileBtn = new JButton();
      panel_3.add(duplicateProfileBtn);
      duplicateProfileBtn.setToolTipText("");
      duplicateProfileBtn.setText("Duplicate profile");
      duplicateProfileBtn.setPreferredSize(new Dimension(120, 24));
      duplicateProfileBtn.setMinimumSize(new Dimension(209, 24));
      duplicateProfileBtn.setMaximumSize(new Dimension(209, 24));
      duplicateProfileBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      duplicateProfileBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          controller.duplicateProfileClicked();
        }
      });

      JLabel lblNewLabel = new JLabel(" ");
      panel_3.add(lblNewLabel);

      deleteProfileBtn = new JButton();
      panel_3.add(deleteProfileBtn);
      deleteProfileBtn.setToolTipText("");
      deleteProfileBtn.setText("Delete profile");
      deleteProfileBtn.setPreferredSize(new Dimension(120, 24));
      deleteProfileBtn.setMinimumSize(new Dimension(209, 24));
      deleteProfileBtn.setMaximumSize(new Dimension(209, 24));
      deleteProfileBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      deleteProfileBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          controller.deleteProfileClicked();
        }
      });

      JScrollPane scrollPane = new JScrollPane();
      profilesPanel.add(scrollPane);
      scrollPane.setFont(null);

      profilesTable = new JTable();
      profilesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      profilesTable.setFont(Prefs.getPrefs().getFont("Dialog", Font.PLAIN, 10));
      profilesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting()) {
            controller.profilesTableClicked();
          }
        }

      });

      scrollPane.setColumnHeaderView(profilesTable);

      JPanel actionsPanel = new JPanel();
      actionsPanel.setPreferredSize(new Dimension(10, 32));
      panel_1.add(actionsPanel, BorderLayout.SOUTH);
      actionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

      saveBtn = new JButton();
      saveBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          controller.saveAndApplyChanges();
          setVisible(false);
        }
      });
      saveBtn.setMnemonic(KeyEvent.VK_S);
      saveBtn.setText("Save and Apply");
      saveBtn.setPreferredSize(new Dimension(125, 24));
      saveBtn.setMinimumSize(new Dimension(159, 24));
      saveBtn.setMaximumSize(new Dimension(159, 24));
      saveBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      saveBtn.setAlignmentX(0.5f);
      actionsPanel.add(saveBtn);

      cancelBtn = new JButton();
      cancelBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          controller.cancelChanges();
          setVisible(false);
        }
      });
      cancelBtn.setMnemonic(KeyEvent.VK_C);
      cancelBtn.setText("Cancel Changes");
      cancelBtn.setPreferredSize(new Dimension(125, 24));
      cancelBtn.setMinimumSize(new Dimension(159, 24));
      cancelBtn.setMaximumSize(new Dimension(159, 24));
      cancelBtn.setFont(new Font("Dialog", Font.BOLD, 10));
      cancelBtn.setAlignmentX(0.5f);
      actionsPanel.add(cancelBtn);
    }
    return mainPanel;
  }

  private JTextField profileNameEdit;
  private JTable profilesTable;
  private JTextField profileStatusEdit;
  private JButton newProfileBtn;
  private JButton duplicateProfileBtn;
  private JButton deleteProfileBtn;
  private JComboBox profileTypeCmb;
  private JButton saveBtn;
  private JButton cancelBtn;
  private JCheckBox defaultCheckbox;

  /**
   * This method initializes renderBatchJobsScrollPane	
   * 	
   * @return javax.swing.JScrollPane	
   */
  public JScrollPane getVariationsScrollPane() {
    if (variationsScrollPane == null) {
      variationsScrollPane = new JScrollPane();
      variationsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
      variationsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }
    return variationsScrollPane;
  }

  public JButton getNewProfileBtn() {
    return newProfileBtn;
  }

  public JButton getDuplicateProfileBtn() {
    return duplicateProfileBtn;
  }

  public JButton getDeleteProfileBtn() {
    return deleteProfileBtn;
  }

  public JTable getProfilesTable() {
    return profilesTable;
  }

  public JTextField getProfileNameEdit() {
    return profileNameEdit;
  }

  public JComboBox getProfileTypeCmb() {
    return profileTypeCmb;
  }

  public JTextField getProfileStatusEdit() {
    return profileStatusEdit;
  }

  public JButton getSaveBtn() {
    return saveBtn;
  }

  public JButton getCancelBtn() {
    return cancelBtn;
  }

  public JCheckBox getDefaultCheckbox() {
    return defaultCheckbox;
  }

}
