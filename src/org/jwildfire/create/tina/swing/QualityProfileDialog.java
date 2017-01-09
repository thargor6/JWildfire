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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.QualityProfileComparator;

public class QualityProfileDialog extends JDialog {
  private static final long serialVersionUID = 1L;
  private final JPanel contentPanel = new JPanel();
  private boolean confirmed = false;
  private boolean configChanged = false;
  private JTextField captionREd;
  private EditStatus editStatus = EditStatus.CLOSE;
  private boolean refreshing = false;
  private JComboBox profileCmb;
  private JCheckBox defaultCBx;
  private JButton newBtn;
  private JButton editBtn;
  private JButton deleteBtn;
  private JButton saveButton;
  private JLabel statusLbl;
  private JTextField qualityREd;
  private JLabel lblQuality;
  private JCheckBox withHDRCBx;
  private JCheckBox withZBufferCBx;

  /**
   * Create the dialog.
   */
  public QualityProfileDialog(Window owner) {
    super(owner);
    setTitle("Edit quality profiles");
    setBounds(100, 100, 487, 338);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(null);

    captionREd = new JTextField();
    captionREd.setText("");
    captionREd.setSize(new Dimension(100, 22));
    captionREd.setPreferredSize(new Dimension(100, 22));
    captionREd.setLocation(new Point(100, 4));
    captionREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    captionREd.setBounds(125, 35, 223, 22);
    contentPanel.add(captionREd);

    JLabel lblProfile = new JLabel();
    lblProfile.setText("Profile");
    lblProfile.setPreferredSize(new Dimension(94, 22));
    lblProfile.setFont(new Font("Dialog", Font.BOLD, 10));
    lblProfile.setBounds(new Rectangle(135, 7, 94, 22));
    lblProfile.setBounds(6, 6, 118, 22);
    contentPanel.add(lblProfile);

    profileCmb = new JComboBox();
    profileCmb.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (!refreshing) {
          refreshProfileView();
        }
      }
    });
    profileCmb.setPreferredSize(new Dimension(125, 22));
    profileCmb.setMaximumRowCount(32);
    profileCmb.setFont(new Font("Dialog", Font.BOLD, 10));
    profileCmb.setBounds(new Rectangle(231, 7, 125, 22));
    profileCmb.setBounds(125, 6, 223, 22);
    contentPanel.add(profileCmb);

    JLabel lblSize = new JLabel();
    lblSize.setText("Caption");
    lblSize.setPreferredSize(new Dimension(94, 22));
    lblSize.setFont(new Font("Dialog", Font.BOLD, 10));
    lblSize.setBounds(new Rectangle(135, 7, 94, 22));
    lblSize.setBounds(6, 35, 118, 22);
    contentPanel.add(lblSize);

    defaultCBx = new JCheckBox("Default profile");
    defaultCBx.setBounds(125, 212, 223, 18);
    contentPanel.add(defaultCBx);

    newBtn = new JButton();
    newBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        editStatus = EditStatus.NEW;
        refreshProfileView();
        enableControls();
      }
    });
    newBtn.setToolTipText("Create a new profile");
    newBtn.setText("New");
    newBtn.setPreferredSize(new Dimension(81, 24));
    newBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    newBtn.setBounds(372, 4, 81, 24);
    contentPanel.add(newBtn);

    editBtn = new JButton();
    editBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        editStatus = EditStatus.EDIT;
        enableControls();
      }
    });
    editBtn.setToolTipText("Edit the current profile");
    editBtn.setText("Edit");
    editBtn.setPreferredSize(new Dimension(81, 24));
    editBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    editBtn.setBounds(372, 32, 81, 24);
    contentPanel.add(editBtn);

    deleteBtn = new JButton();
    deleteBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (profileCmb.getSelectedIndex() >= 0) {
          profileCmb.removeItemAt(profileCmb.getSelectedIndex());
          configChanged = true;
          if (profileCmb.getItemCount() > 0) {
            profileCmb.setSelectedIndex(0);
          }
        }
        editStatus = profileCmb.getSelectedItem() != null ? EditStatus.BROWSE : EditStatus.CLOSE;
        enableControls();
      }
    });
    deleteBtn.setToolTipText("Delete the current profile");
    deleteBtn.setText("Delete");
    deleteBtn.setPreferredSize(new Dimension(81, 24));
    deleteBtn.setFont(new Font("Dialog", Font.BOLD, 10));
    deleteBtn.setBounds(372, 60, 81, 24);
    contentPanel.add(deleteBtn);

    saveButton = new JButton();
    saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        applyChanges();
      }
    });
    saveButton.setToolTipText("Apply the current changes to the current profile");
    saveButton.setText("Apply");
    saveButton.setPreferredSize(new Dimension(81, 24));
    saveButton.setFont(new Font("Dialog", Font.BOLD, 10));
    saveButton.setEnabled(false);
    saveButton.setBounds(372, 88, 81, 24);
    contentPanel.add(saveButton);

    statusLbl = new JLabel();
    statusLbl.setPreferredSize(new Dimension(94, 22));
    statusLbl.setFont(new Font("Dialog", Font.PLAIN, 10));
    statusLbl.setBounds(new Rectangle(135, 7, 94, 22));
    statusLbl.setBounds(6, 242, 422, 22);
    contentPanel.add(statusLbl);

    qualityREd = new JTextField();
    qualityREd.setText("");
    qualityREd.setSize(new Dimension(100, 22));
    qualityREd.setPreferredSize(new Dimension(100, 22));
    qualityREd.setLocation(new Point(100, 4));
    qualityREd.setFont(new Font("Dialog", Font.PLAIN, 10));
    qualityREd.setEnabled(false);
    qualityREd.setBounds(125, 60, 100, 22);
    contentPanel.add(qualityREd);

    lblQuality = new JLabel();
    lblQuality.setText("Quality");
    lblQuality.setPreferredSize(new Dimension(94, 22));
    lblQuality.setFont(new Font("Dialog", Font.BOLD, 10));
    lblQuality.setBounds(new Rectangle(135, 7, 94, 22));
    lblQuality.setBounds(6, 60, 118, 22);
    contentPanel.add(lblQuality);

    withHDRCBx = new JCheckBox("with HDR");
    withHDRCBx.setEnabled(false);
    withHDRCBx.setBounds(125, 152, 223, 18);
    contentPanel.add(withHDRCBx);

    withZBufferCBx = new JCheckBox("with Z-Buffer");
    withZBufferCBx.setEnabled(false);
    withZBufferCBx.setBounds(125, 182, 223, 18);
    contentPanel.add(withZBufferCBx);
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);

      JButton cancelButton = new JButton();
      cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          confirmed = false;
          setVisible(false);
        }
      });

      JButton okButton = new JButton();
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (editStatus == EditStatus.EDIT || editStatus == EditStatus.NEW) {
            if (!applyChanges()) {
              return;
            }
          }
          confirmed = true;
          setVisible(false);
        }
      });
      okButton.setToolTipText("");
      okButton.setText("OK");
      okButton.setPreferredSize(new Dimension(81, 24));
      okButton.setFont(new Font("Dialog", Font.BOLD, 10));
      buttonPane.add(okButton);
      cancelButton.setToolTipText("");
      cancelButton.setText("Cancel");
      cancelButton.setPreferredSize(new Dimension(81, 24));
      cancelButton.setFont(new Font("Dialog", Font.BOLD, 10));
      buttonPane.add(cancelButton);
    }

    enableControls();
    Rectangle rootBounds = owner.getBounds();
    Dimension size = getSize();
    setLocation(rootBounds.x + (rootBounds.width - size.width) / 2, rootBounds.y + (rootBounds.height - size.height) / 2);
    configChanged = false;
  }

  public List<QualityProfile> getProfiles() {
    List<QualityProfile> res = new ArrayList<QualityProfile>();
    for (int i = 0; i < getProfileCmb().getItemCount(); i++) {
      res.add((QualityProfile) getProfileCmb().getItemAt(i));
    }
    Collections.sort(res, new QualityProfileComparator());
    return res;
  }

  public void setProfile(QualityProfile pProfile) {
    refreshing = true;
    try {
      if (pProfile == null) {
        getProfileCmb().setSelectedIndex(-1);
      }
      else {
        boolean found = false;
        for (int i = 0; i < getProfileCmb().getItemCount(); i++) {
          QualityProfile profile = (QualityProfile) getProfileCmb().getItemAt(i);
          if (profile.toString().equals(pProfile.toString())) {
            getProfileCmb().setSelectedIndex(i);
            found = true;
            break;
          }
        }
        if (!found) {
          getProfileCmb().setSelectedIndex(-1);
        }
      }
      editStatus = getProfileCmb().getSelectedItem() != null ? EditStatus.BROWSE : EditStatus.CLOSE;
      refreshProfileView();
      enableControls();
    }
    finally {
      refreshing = false;
    }
  }

  public void setProfiles(List<QualityProfile> pProfiles) {
    refreshing = true;
    try {
      getProfileCmb().removeAllItems();
      if (pProfiles != null) {
        for (QualityProfile profile : pProfiles) {
          QualityProfile clonedProfile = (QualityProfile) profile.makeCopy();
          getProfileCmb().addItem(clonedProfile);
        }
      }
      getProfileCmb().setSelectedIndex(-1);
      editStatus = EditStatus.CLOSE;
    }
    finally {
      refreshing = false;
    }
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public JComboBox getProfileCmb() {
    return profileCmb;
  }

  public JCheckBox getDefaultCBx() {
    return defaultCBx;
  }

  public JButton getNewBtn() {
    return newBtn;
  }

  public JButton getEditBtn() {
    return editBtn;
  }

  public JButton getDeleteBtn() {
    return deleteBtn;
  }

  private void enableControls() {
    boolean edit = editStatus == EditStatus.EDIT || editStatus == EditStatus.NEW;
    getProfileCmb().setEnabled(!edit);
    getNewBtn().setEnabled(!edit);
    getEditBtn().setEnabled(editStatus == EditStatus.BROWSE);
    getDeleteBtn().setEnabled(editStatus == EditStatus.BROWSE);
    getSaveButton().setEnabled(edit);
    getCaptionREd().setEnabled(edit);
    getQualityREd().setEnabled(edit);
    getWithHDRCBx().setEnabled(edit);
    getWithZBufferCBx().setEnabled(edit);
    getDefaultCBx().setEnabled(edit);
  }

  private void refreshProfileView() {
    QualityProfile profile;
    if (editStatus == EditStatus.NEW) {
      profile = null;
    }
    else {
      profile = (QualityProfile) getProfileCmb().getSelectedItem();
    }
    if (profile == null) {
      getCaptionREd().setText("");
      getQualityREd().setText("");
      getWithHDRCBx().setSelected(false);
      getWithZBufferCBx().setSelected(false);
      getDefaultCBx().setSelected(false);
    }
    else {
      getCaptionREd().setText(profile.getCaption());
      getQualityREd().setText(String.valueOf(profile.getQuality()));
      getWithHDRCBx().setSelected(profile.isWithHDR());
      getWithZBufferCBx().setSelected(profile.isWithZBuffer());
      getDefaultCBx().setSelected(profile.isDefaultProfile());
    }
  }

  private boolean applyChanges() {
    try {
      final int MIN_QUALITY = 1;
      String caption = getCaptionREd().getText();
      if (caption == null || caption.trim().length() == 0) {
        throw new Exception("Caption must not be empty");
      }
      int quality = Integer.parseInt(getQualityREd().getText());
      if (quality < MIN_QUALITY) {
        throw new Exception("Quality must be at least " + MIN_QUALITY);
      }

      QualityProfile profile;
      if (editStatus == EditStatus.NEW) {
        profile = new QualityProfile();
      }
      else {
        profile = (QualityProfile) getProfileCmb().getSelectedItem();
      }
      profile.setCaption(caption);
      profile.setQuality(quality);
      profile.setWithHDR(getWithHDRCBx().isSelected());
      profile.setWithZBuffer(getWithZBufferCBx().isSelected());
      profile.setDefaultProfile(getDefaultCBx().isSelected());
      if (editStatus == EditStatus.NEW) {
        refreshing = true;
        try {
          getProfileCmb().addItem(profile);
          getProfileCmb().setSelectedItem(profile);
        }
        finally {
          refreshing = false;
        }
      }
      if (profile.isDefaultProfile()) {
        for (int i = 0; i < getProfileCmb().getItemCount(); i++) {
          QualityProfile lProfile = (QualityProfile) getProfileCmb().getItemAt(i);
          if (lProfile != profile) {
            lProfile.setDefaultProfile(false);
          }
        }
      }
      getProfileCmb().requestFocus();
      editStatus = EditStatus.BROWSE;
      enableControls();
      configChanged = true;
      return true;
    }
    catch (Throwable ex) {
      getStatusLbl().setText(ex.getMessage());
      return false;
    }
  }

  public JButton getSaveButton() {
    return saveButton;
  }

  public JLabel getStatusLbl() {
    return statusLbl;
  }

  public boolean isConfigChanged() {
    return configChanged;
  }

  public JTextField getQualityREd() {
    return qualityREd;
  }

  public JCheckBox getWithHDRCBx() {
    return withHDRCBx;
  }

  public JCheckBox getWithZBufferCBx() {
    return withZBufferCBx;
  }

  public JTextField getCaptionREd() {
    return captionREd;
  }
}
