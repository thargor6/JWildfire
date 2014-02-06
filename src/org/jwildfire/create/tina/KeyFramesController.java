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
import javax.swing.table.DefaultTableModel;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.keyframe.KeyFrame;
import org.jwildfire.create.tina.keyframe.KeyFrameService;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.StandardDialogs;
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

  public int getCurrKeyFrameFrame() {
    Integer value = keyframesFrameField.getIntValue();
    return (value != null ? value.intValue() : -1);
  }

  public KeyFrame getCurrKeyFrame() {
    Flame flame = tinaController.getFlame();
    if (flame != null) {
      return KeyFrameService.getKeyFrame(flame, getCurrKeyFrameFrame());
    }
    return null;
  }

  public boolean hasKeyFrames() {
    Flame flame = tinaController.getFlame();
    return flame != null && flame.getKeyFrames().size() > 0;
  }

  public void enableControls() {
    Flame flame = tinaController.getFlame();
    KeyFrame keyFrame = getCurrKeyFrame();
    boolean hasKeyFrames = hasKeyFrames();

    keyframesFrameField.setEnabled(hasKeyFrames);
    keyframesFrameSlider.setEnabled(hasKeyFrames);
    keyframesFrameCountField.setEnabled(flame != null);
    addKeyframeBtn.setEnabled(flame != null);
    duplicateKeyframeBtn.setEnabled(keyFrame != null && flame != null);
    deleteKeyframeBtn.setEnabled(keyFrame != null && flame != null);
    keyFramesTable.setEnabled(flame != null);
  }

  private void adjustFrameControls(int frame) {
    if (keyframesFrameField.getMinValue() - 1 >= frame) {
      keyframesFrameField.setMinValue(frame - 1);
    }
    if (keyframesFrameField.getMaxValue() + 1 <= frame) {
      keyframesFrameField.setMaxValue(frame + 1);
    }
    keyframesFrameField.setValue(frame);

    if (keyframesFrameCountField.getMaxValue() + 1 <= frame) {
      keyframesFrameCountField.setMaxValue(frame + 1);
    }
    if (keyframesFrameCountField.getIntValue() == null || keyframesFrameCountField.getIntValue() + 1 <= frame) {
      keyframesFrameCountField.setValue(frame + 1);
    }

    if (keyframesFrameSlider.getMinimum() > frame) {
      keyframesFrameSlider.setMinimum(frame);
    }
    if (keyframesFrameSlider.getMaximum() < frame) {
      keyframesFrameSlider.setMaximum(frame);
    }
    keyframesFrameSlider.setValue(frame);
  }

  private void refreshKeyFramesTable() {
    final int COL_FRAME = 0;
    final int COL_PROPERTIES = 1;
    keyFramesTable.setModel(new DefaultTableModel() {
      private static final long serialVersionUID = 1L;

      @Override
      public int getRowCount() {
        Flame flame = tinaController.getCurrFlame();
        return flame != null ? flame.getKeyFrames().size() : 0;
      }

      @Override
      public int getColumnCount() {
        return 2;
      }

      @Override
      public String getColumnName(int columnIndex) {
        switch (columnIndex) {
          case COL_FRAME:
            return "Frame";
          case COL_PROPERTIES:
            return "Properties";
        }
        return null;
      }

      @Override
      public Object getValueAt(int rowIndex, int columnIndex) {
        Flame flame = tinaController.getCurrFlame();
        KeyFrame keyFrame = flame != null && rowIndex >= 0 && rowIndex < flame.getKeyFrames().size() ? flame.getKeyFrames().get(rowIndex) : null;
        if (keyFrame != null) {
          switch (columnIndex) {
            case COL_FRAME:
              return String.valueOf(keyFrame.getFrame());
            case COL_PROPERTIES:
              return keyFrame.getValues().size() > 0 ? String.valueOf(keyFrame.getValues().size()) : null;
          }
        }
        return null;
      }

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    });
    keyFramesTable.getTableHeader().setFont(tinaController.getTableFont());
    keyFramesTable.getColumnModel().getColumn(COL_FRAME).setWidth(60);
    keyFramesTable.getColumnModel().getColumn(COL_PROPERTIES).setPreferredWidth(200);
  }

  public void duplicateKeyFrameBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void deleteKeyFrameBtn_clicked() {
    // TODO Auto-generated method stub

  }

  public void keyFrameFieldChanged() {
    if (!tinaController.isNoRefresh()) {
      boolean oldNoRefresh = tinaController.isNoRefresh();
      try {
        tinaController.setNoRefresh(true);
        int frame = keyframesFrameField.getIntValue() != null ? keyframesFrameField.getIntValue().intValue() : -1;
        adjustFrameControls(frame);
        // TODO
      }
      finally {
        tinaController.setNoRefresh(oldNoRefresh);
      }
    }
  }

  public void keyFrameSliderChanged() {
    if (!tinaController.isNoRefresh()) {
      boolean oldNoRefresh = tinaController.isNoRefresh();
      try {
        tinaController.setNoRefresh(true);
        int frame = keyframesFrameSlider.getValue();
        adjustFrameControls(frame);
        // TODO
      }
      finally {
        tinaController.setNoRefresh(oldNoRefresh);
      }
    }
  }

  public void refreshUI() {
    int frame = 1;
    Flame flame = tinaController.getFlame();
    KeyFrame keyFrame = flame != null ? KeyFrameService.getKeyFrame(flame, frame) : null;
    adjustFrameControls(frame);
    refreshKeyFramesTable();
    int row = flame.getKeyFrames().indexOf(keyFrame);
    if (row >= 0) {
      keyFramesTable.setRowSelectionInterval(row, row);
    }
    enableControls();
  }

  public void keyFramesTableClicked() {
    // TODO Auto-generated method stub

  }

  public void addKeyFrameBtn_clicked() {
    try {
      String s = StandardDialogs.promptForText(rootPanel, "Please enter the keyframe number:", null);
      if (s != null) {
        int frame = Integer.parseInt(s);
        Flame flame = tinaController.getFlame();
        tinaController.saveUndoPoint();
        KeyFrame keyFrame = KeyFrameService.addKeyFrame(flame, frame);
        boolean oldNoRefresh = tinaController.isNoRefresh();
        try {
          tinaController.setNoRefresh(true);
          adjustFrameControls(keyFrame.getFrame());
          refreshKeyFramesTable();
          int row = flame.getKeyFrames().indexOf(keyFrame);
          keyFramesTable.setRowSelectionInterval(row, row);
          enableControls();
        }
        finally {
          tinaController.setNoRefresh(oldNoRefresh);
        }
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }
}
