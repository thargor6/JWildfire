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
package org.jwildfire.create.tina;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.ErrorHandler;

public class KeyFramesController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JWFNumberField keyframesFrameField;
  private final JSlider keyframesFrameSlider;
  private final JWFNumberField keyframesFrameCountField;
  private final JButton addKeyframeBtn;
  private final JButton duplicateKeyframeBtn;
  private final JButton deleteKeyframeBtn;
  private final JTable keyFramesTable;

  public KeyFramesController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel,
      JWFNumberField pKeyframesFrameField, JSlider pKeyframesFrameSlider, JWFNumberField pKeyframesFrameCountField,
      JButton pAddKeyframeBtn, JButton pDuplicateKeyframeBtn, JButton pDeleteKeyframeBtn, JTable pKeyFramesTable) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    keyframesFrameField = pKeyframesFrameField;
    keyframesFrameSlider = pKeyframesFrameSlider;
    keyframesFrameCountField = pKeyframesFrameCountField;
    addKeyframeBtn = pAddKeyframeBtn;
    duplicateKeyframeBtn = pDuplicateKeyframeBtn;
    deleteKeyframeBtn = pDeleteKeyframeBtn;
    keyFramesTable = pKeyFramesTable;
    enableControls();
  }

  public void enableControls() {
    // TODO
  }

  public void addKeyFrameBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void duplicateKeyFrameBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void deleteKeyFrameBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void keyFrameChanged() {
    // TODO Auto-generated method stub

  }

}
