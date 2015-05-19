/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.tina.leapmotion;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.ErrorHandler;

public class LeapMotionMainEditorController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JWFNumberField flameFPSField;
  private final JToggleButton leapMotionToggleButton;
  private final JTable leapMotionConfigTable;
  private final JComboBox leapMotionHandCmb;
  private final JComboBox leapMotionInputChannelCmb;
  private final JComboBox leapMotionOutputChannelCmb;
  private final JWFNumberField leapMotionIndexField;
  private final JWFNumberField leapMotionInvScaleField;
  private final JWFNumberField leapMotionOffsetField;
  private final JButton leapMotionAddButton;
  private final JButton leapMotionDuplicateButton;
  private final JButton leapMotionDeleteButton;

  public LeapMotionMainEditorController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel,
      JWFNumberField pFlameFPSField, JToggleButton pLeapMotionToggleButton, JTable pLeapMotionConfigTable, JComboBox pLeapMotionHandCmb,
      JComboBox pLeapMotionInputChannelCmb, JComboBox pLeapMotionOutputChannelCmb, JWFNumberField pLeapMotionIndexField,
      JWFNumberField pLeapMotionInvScaleField, JWFNumberField pLeapMotionOffsetField, JButton pLeapMotionAddButton,
      JButton pLeapMotionDuplicateButton, JButton pLeapMotionDeleteButton) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    leapMotionToggleButton = pLeapMotionToggleButton;
    flameFPSField = pFlameFPSField;
    leapMotionConfigTable = pLeapMotionConfigTable;
    leapMotionHandCmb = pLeapMotionHandCmb;
    leapMotionInputChannelCmb = pLeapMotionInputChannelCmb;
    leapMotionOutputChannelCmb = pLeapMotionOutputChannelCmb;
    leapMotionIndexField = pLeapMotionIndexField;
    leapMotionInvScaleField = pLeapMotionIndexField;
    leapMotionOffsetField = pLeapMotionOffsetField;
    leapMotionAddButton = pLeapMotionAddButton;
    leapMotionDuplicateButton = pLeapMotionDuplicateButton;
    leapMotionDeleteButton = pLeapMotionDeleteButton;
    enableControls();
  }

  public void enableControls() {
    // TODO Auto-generated method stub

  }

  private LeapMotionControllerHolder leapMotionControllerHolder;

  private final LeapMotionConnectedProperties config = LeapMotionConnectedProperties.getDefaultConfig();

  public void toggleLeapMotionMode() {
    if (leapMotionToggleButton.isSelected()) {
      if (leapMotionControllerHolder == null) {
        leapMotionControllerHolder = new LeapMotionControllerHolder(tinaController, errorHandler, rootPanel);
      }
      leapMotionControllerHolder.startLeapMotionListener(config, flameFPSField.getIntValue());
    }
    else {
      if (leapMotionControllerHolder != null) {
        leapMotionControllerHolder.stopLeapMotionListener();
      }
    }
  }

  public void configTableClicked() {
    // TODO Auto-generated method stub

  }

  public void leapMotionIndexField_changed() {
    // TODO Auto-generated method stub

  }

  public void leapMotionInvScaleField_changed() {
    // TODO Auto-generated method stub

  }

  public void leapMotionOffsetField_changed() {
    // TODO Auto-generated method stub

  }

  public void leapMotionOutputChannelCmb_changed() {
    // TODO Auto-generated method stub

  }

  public void leapMotionInputChannelCmb_changed() {
    // TODO Auto-generated method stub

  }

  public void leapMotionHandCmb_changed() {
    // TODO Auto-generated method stub

  }

  public void resetConfigButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void leapMotionDeleteButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void leapMotionDuplicateButton_clicked() {
    // TODO Auto-generated method stub

  }

  public void leapMotionAddButton_clicked() {
    // TODO Auto-generated method stub

  }

}
