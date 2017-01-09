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
package org.jwildfire.create.tina.swing;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.RootPaneContainer;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.Envelope.EditMode;
import org.jwildfire.envelope.EnvelopePanel;
import org.jwildfire.envelope.EnvelopeView;
import org.jwildfire.swing.ErrorHandler;

public class EnvelopeDlgController {
  private enum MouseClickWaitMode {
    ADD_POINT, REMOVE_POINT, NONE
  }

  private enum Direction {
    HORIZ, VERT
  }

  private final JButton addPointButton;
  private final JButton removePointButton;
  private final JButton clearButton;
  private final JButton viewAllButton;
  private final JButton viewLeftButton;
  private final JButton viewRightButton;
  private final JButton viewUpButton;
  private final JButton viewDownButton;
  private final JWFNumberField xMinREd;
  private final JWFNumberField xMaxREd;
  private final JWFNumberField yMinREd;
  private final JWFNumberField yMaxREd;
  private final JWFNumberField xREd;
  private final JWFNumberField yREd;
  private final JComboBox interpolationCmb;
  private final EnvelopePanel envelopePanel;
  private final JComboBox envelopeInterpolationCmb;
  private final JWFNumberField xScaleREd;
  private final JWFNumberField xOffsetREd;
  private final JWFNumberField yScaleREd;
  private final JWFNumberField yOffsetREd;
  private final JButton applyTransformBtn;
  private final JButton applyTransformReverseBtn;
  private final JButton mp3ImportBtn;
  private final JWFNumberField mp3ChannelREd;
  private final JWFNumberField mp3FPSREd;
  private final JWFNumberField mp3OffsetREd;
  private final JWFNumberField mp3DurationREd;
  private final ErrorHandler errorHandler;
  private final JCheckBox autofitCBx;
  private final JWFNumberField curveFPSField;
  private final JWFNumberField timeField;
  private final JComboBox editModeCmb;
  private final JButton smoothCurveBtn;
  private final JButton rawDataImportFromFileButton;
  private final JButton rawDataImportFromClipboardButton;
  private final JButton rawDataExportToFileButton;
  private final JButton rawDataExportToClipboardButton;
  private final JWFNumberField rawDataFrameColumnField;
  private final JWFNumberField rawDataFrameScaleField;
  private final JWFNumberField rawDataAmplitudeColumnField;
  private final JWFNumberField rawDataAmplitudeScaleField;
  private final JWFNumberField smoothCurveAmountField;

  private final List<EnvelopeChangeListener> valueChangeListeners = new ArrayList<EnvelopeChangeListener>();
  private final List<EnvelopeChangeListener> selectionChangeListeners = new ArrayList<EnvelopeChangeListener>();

  private boolean noRefresh;
  private Envelope envelope;

  private EnvelopeMP3Data mp3Data;

  private MouseClickWaitMode mouseClickWaitMode = MouseClickWaitMode.NONE;

  private int lastMouseX, lastMouseY;

  public EnvelopeDlgController(EnvelopePanel pEnvelopePanel, ErrorHandler pErrorHandler) {
    this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, pEnvelopePanel,
        null, null, null, null, null, null, null, null, null, null, null, null, pErrorHandler, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null);
  }

  public EnvelopeDlgController(Envelope pEnvelope, JButton pAddPointButton, JButton pRemovePointButton, JButton pClearButton,
      JWFNumberField pXMinREd, JWFNumberField pXMaxREd, JWFNumberField pYMinREd, JWFNumberField pYMaxREd,
      JWFNumberField pXREd, JWFNumberField pYREd, JComboBox pInterpolationCmb, JButton pViewAllButton,
      JButton pViewLeftButton, JButton pViewRightButton, JButton pViewUpButton,
      JButton pViewDownButton, EnvelopePanel pEnvelopePanel, JComboBox pEnvelopeInterpolationCmb,
      JWFNumberField pXScaleREd, JWFNumberField pXOffsetREd, JWFNumberField pYScaleREd, JWFNumberField pYOffsetREd,
      JButton pApplyTransformBtn, JButton pApplyTransformReverseBtn, JButton pMp3ImportBtn,
      JWFNumberField pMp3ChannelREd, JWFNumberField pMp3FPSREd, JWFNumberField pMp3OffsetREd,
      JWFNumberField pMp3DurationREd, ErrorHandler pErrorHandler, JCheckBox pAutofitCBx,
      JWFNumberField pCurveFPSField, JComboBox pEditModeCmb, JButton pSmoothCurveBtn, JWFNumberField pTimeField,
      JButton pRawDataImportFromFileButton, JButton pRawDataImportFromClipboardButton,
      JButton pRawDataExportToFileButton, JButton pRawDataExportToClipboardButton, JWFNumberField pRawDataFrameColumnField,
      JWFNumberField pRawDataFrameScaleField, JWFNumberField pRawDataAmplitudeColumnField, JWFNumberField pRawDataAmplitudeScaleField,
      JWFNumberField pSmoothCurveAmountField) {
    envelope = pEnvelope;
    addPointButton = pAddPointButton;
    removePointButton = pRemovePointButton;
    clearButton = pClearButton;
    xMinREd = pXMinREd;
    xMaxREd = pXMaxREd;
    yMinREd = pYMinREd;
    yMaxREd = pYMaxREd;
    xREd = pXREd;
    yREd = pYREd;
    interpolationCmb = pInterpolationCmb;
    viewAllButton = pViewAllButton;
    viewLeftButton = pViewLeftButton;
    viewRightButton = pViewRightButton;
    viewUpButton = pViewUpButton;
    viewDownButton = pViewDownButton;
    xScaleREd = pXScaleREd;
    xOffsetREd = pXOffsetREd;
    yScaleREd = pYScaleREd;
    yOffsetREd = pYOffsetREd;
    applyTransformBtn = pApplyTransformBtn;
    applyTransformReverseBtn = pApplyTransformReverseBtn;
    mp3ImportBtn = pMp3ImportBtn;
    mp3ChannelREd = pMp3ChannelREd;
    mp3FPSREd = pMp3FPSREd;
    mp3OffsetREd = pMp3OffsetREd;
    mp3DurationREd = pMp3DurationREd;
    errorHandler = pErrorHandler;
    autofitCBx = pAutofitCBx;
    curveFPSField = pCurveFPSField;
    editModeCmb = pEditModeCmb;
    smoothCurveBtn = pSmoothCurveBtn;
    timeField = pTimeField;
    rawDataImportFromFileButton = pRawDataImportFromFileButton;
    rawDataImportFromClipboardButton = pRawDataImportFromClipboardButton;
    rawDataExportToFileButton = pRawDataExportToFileButton;
    rawDataExportToClipboardButton = pRawDataExportToClipboardButton;
    rawDataFrameColumnField = pRawDataFrameColumnField;
    rawDataFrameScaleField = pRawDataFrameScaleField;
    rawDataAmplitudeColumnField = pRawDataAmplitudeColumnField;
    rawDataAmplitudeScaleField = pRawDataAmplitudeScaleField;
    smoothCurveAmountField = pSmoothCurveAmountField;

    envelopePanel = pEnvelopePanel;
    envelopeInterpolationCmb = pEnvelopeInterpolationCmb;
    envelopePanel.addMouseListener(new java.awt.event.MouseAdapter() {

      @Override
      public void mousePressed(MouseEvent e) {
        switch (mouseClickWaitMode) {
          case NONE:
            selectPoint(e);
            break;
          default: // nothing to do
            break;
        }
      }
    });
    envelopePanel.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent e) {
        editModeChanged();
        if (e.getClickCount() > 1) {
          viewAll();
          return;
        }
        try {
          switch (mouseClickWaitMode) {
            case ADD_POINT:
              finishAddPoint(e);
              break;
            case REMOVE_POINT:
              finishRemovePoint(e);
              break;
            default:
              // selectPoint(e);
              break;
          }
        }
        finally {
          mouseClickWaitMode = MouseClickWaitMode.NONE;
          clearCrosshairCursor();
        }
      }
    });
    envelopePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent e) {
        if (mouseClickWaitMode == MouseClickWaitMode.NONE) {
          if (lastMouseX < 0) {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
          }
          switch (getEditMode()) {
            case DRAG_POINTS:
              movePoint(e);
              break;
            case DRAG_CURVE_HORIZ:
              moveCurve(e, Direction.HORIZ);
              break;
            case DRAG_CURVE_VERT:
              moveCurve(e, Direction.VERT);
              break;
            case SCALE_CURVE_HORIZ:
              scaleCurve(e, Direction.HORIZ);
              break;
            case SCALE_CURVE_VERT:
              scaleCurve(e, Direction.VERT);
              break;
          }
          lastMouseX = e.getX();
          lastMouseY = e.getY();
        }
      }

    });

  }

  private void resetEditMode() {
    if (editModeCmb != null) {
      editModeCmb.setSelectedItem(EditMode.DRAG_POINTS);
      editModeChanged();
    }
  }

  private Envelope.EditMode getEditMode() {
    if (editModeCmb != null && editModeCmb.getSelectedItem() != null) {
      return (EditMode) editModeCmb.getSelectedItem();
    }
    else {
      return EditMode.DRAG_POINTS;
    }
  }

  public Envelope getCurrEnvelope() {
    return envelope;
  }

  public void enableControls() {
    boolean hasEnvelope = (envelope != null);
    boolean editable = hasEnvelope && !envelope.isLocked();
    if (interpolationCmb != null) {
      interpolationCmb.setEnabled(editable);
    }
    if (xMinREd != null) {
      xMinREd.setEnabled(hasEnvelope);
    }
    if (xMaxREd != null) {
      xMaxREd.setEnabled(hasEnvelope);
    }
    if (yMinREd != null) {
      yMinREd.setEnabled(hasEnvelope);
    }
    if (yMaxREd != null) {
      yMaxREd.setEnabled(hasEnvelope);
    }
    if (xREd != null) {
      xREd.setEnabled(editable);
    }
    if (yREd != null) {
      yREd.setEnabled(editable);
    }
    if (addPointButton != null) {
      addPointButton.setEnabled(editable);
    }
    if (removePointButton != null) {
      removePointButton.setEnabled(editable && (envelope.size() > 1));
    }
    if (clearButton != null) {
      clearButton.setEnabled(editable);
    }
    if (viewAllButton != null) {
      viewAllButton.setEnabled(hasEnvelope);
    }
    if (viewLeftButton != null) {
      viewLeftButton.setEnabled(hasEnvelope);
    }
    if (viewRightButton != null) {
      viewRightButton.setEnabled(hasEnvelope);
    }
    if (viewUpButton != null) {
      viewUpButton.setEnabled(hasEnvelope);
    }
    if (viewDownButton != null) {
      viewDownButton.setEnabled(hasEnvelope);
    }
    if (mp3ChannelREd != null) {
      mp3ChannelREd.setEnabled(mp3Data != null);
    }
    if (mp3FPSREd != null) {
      mp3FPSREd.setEnabled(mp3Data != null);
    }
    if (mp3OffsetREd != null) {
      mp3OffsetREd.setEnabled(mp3Data != null);
    }
    if (mp3DurationREd != null) {
      mp3DurationREd.setEnabled(mp3Data != null);
    }
  }

  private void refreshXMinField() {
    if (xMinREd != null) {
      boolean oldNoRefresh = noRefresh;
      noRefresh = true;
      try {
        xMinREd.setValue(envelope.getViewXMin());
      }
      finally {
        noRefresh = oldNoRefresh;
      }
    }
  }

  private void refreshXMaxField() {
    if (xMaxREd != null) {
      boolean oldNoRefresh = noRefresh;
      noRefresh = true;
      try {
        xMaxREd.setValue(envelope.getViewXMax());
      }
      finally {
        noRefresh = oldNoRefresh;
      }
    }
  }

  private void refreshYMinField() {
    if (yMinREd != null) {
      boolean oldNoRefresh = noRefresh;
      noRefresh = true;
      try {
        yMinREd.setValue(envelope.getViewYMin());
      }
      finally {
        noRefresh = oldNoRefresh;
      }
    }
  }

  private void refreshYMaxField() {
    if (yMaxREd != null) {
      boolean oldNoRefresh = noRefresh;
      noRefresh = true;
      try {
        yMaxREd.setValue(envelope.getViewYMax());
      }
      finally {
        noRefresh = oldNoRefresh;
      }
    }
  }

  private void refreshXField() {
    if (xREd != null) {
      boolean oldNoRefresh = noRefresh;
      noRefresh = true;
      try {
        xREd.setValue(envelope.getSelectedX());
        timeField.setValue((double) envelope.getSelectedX() / curveFPSField.getDoubleValue());
      }
      finally {
        noRefresh = oldNoRefresh;
      }
    }
  }

  private void refreshYField() {
    if (yREd != null) {
      boolean oldNoRefresh = noRefresh;
      noRefresh = true;
      try {
        yREd.setValue(envelope.getSelectedY());
      }
      finally {
        noRefresh = oldNoRefresh;
      }
    }
  }

  private void refreshInterpolationField() {
    if (envelopeInterpolationCmb != null) {
      boolean oldNoRefresh = noRefresh;
      noRefresh = true;
      try {
        envelopeInterpolationCmb.setSelectedItem(envelope.getInterpolation());
      }
      finally {
        noRefresh = oldNoRefresh;
      }
    }
  }

  public void refreshEnvelope() {
    try {
      refreshInterpolationField();
      refreshXField();
      refreshXMinField();
      refreshXMaxField();
      refreshYField();
      refreshYMinField();
      refreshYMaxField();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    envelopePanel.repaint();
  }

  public void removePoint() {
    resetEditMode();
    setCrosshairCursor();
    mouseClickWaitMode = MouseClickWaitMode.REMOVE_POINT;
  }

  public void addPoint() {
    resetEditMode();
    setCrosshairCursor();
    mouseClickWaitMode = MouseClickWaitMode.ADD_POINT;
  }

  public void interpolationCmbChanged() {
    if (envelope != null && !noRefresh) {
      Envelope.Interpolation interpolation = (Envelope.Interpolation) interpolationCmb
          .getSelectedItem();
      envelope.setInterpolation(interpolation);
      refreshEnvelope();
    }
  }

  public void clearEnvelope() {
    envelope.clear();
    refreshWithAutoFit();
  }

  public void smoothEnvelope() {
    int times = smoothCurveAmountField.getIntValue();
    if (times < 1) {
      times = 1;
    }
    else if (times > 1000) {
      times = 1000;
    }
    for (int i = 0; i < times; i++) {
      envelope.smooth(3);
    }
    refreshWithAutoFit();
  }

  public void editFieldChanged(boolean pAutoFit) {
    if (!noRefresh) {
      envelope.setViewXMin(getIntValue(xMinREd));
      envelope.setViewXMax(getIntValue(xMaxREd));
      envelope.setSelectedX(getIntValue(xREd));
      envelope.setViewYMin((Double) yMinREd.getValue());
      envelope.setViewYMax((Double) yMaxREd.getValue());
      envelope.setSelectedY((Double) yREd.getValue());
      if ((envelope.getViewXMax() - envelope.getViewXMin()) < 1)
        envelope.setViewXMax(envelope.getViewXMin() + 1);
      if ((envelope.getViewYMax() - envelope.getViewYMin()) < 0.001)
        envelope.setViewYMax(envelope.getViewYMin() + 0.001);
      if (pAutoFit) {
        refreshWithAutoFit();
      }
      else {
        refreshEnvelope();
      }
    }
  }

  private int getIntValue(JWFNumberField pEdit) {
    Object val = pEdit.getValue();
    if (val != null && val instanceof Double) {
      return Tools.FTOI((Double) val);
    }
    else if (val != null && val instanceof Integer) {
      return (Integer) val;
    }
    else {
      return 0;
    }
  }

  private final Cursor CROSSHAIR_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
  private final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

  private void setCrosshairCursor() {
    RootPaneContainer root = (RootPaneContainer) envelopePanel.getTopLevelAncestor();
    root.getGlassPane().setCursor(CROSSHAIR_CURSOR);
    root.getGlassPane().setVisible(true);
  }

  private void clearCrosshairCursor() {
    RootPaneContainer root = (RootPaneContainer) envelopePanel.getTopLevelAncestor();
    root.getGlassPane().setCursor(DEFAULT_CURSOR);
    root.getGlassPane().setVisible(false);
  }

  private void finishRemovePoint(MouseEvent e) {
    if ((envelope != null) && (envelope.size() > 1)) {
      int lx = e.getX();
      EnvelopeView envelopeView = new EnvelopeView(envelopePanel);
      double x = ((double) lx + envelopeView.getEnvelopeXTrans())
          / envelopeView.getEnvelopeXScale();
      int sel = 0;
      double dist = x - envelope.getX()[0];
      if (dist < 0)
        dist = 0 - dist;
      for (int i = 1; i < envelope.size(); i++) {
        double dist2 = x - envelope.getX()[i];
        if (dist2 < 0)
          dist2 = 0 - dist2;
        if (dist2 < dist) {
          dist = dist2;
          sel = i;
        }
      }

      int cnt = envelope.size() - 1;
      int[] xVals = new int[cnt];
      double[] yVals = new double[cnt];

      int curr = 0;
      for (int i = 0; i < envelope.size(); i++) {
        if (i != sel) {
          xVals[curr] = envelope.getX()[i];
          yVals[curr++] = envelope.getY()[i];
        }
      }
      envelope.setValues(xVals, yVals);
      //      refreshEnvelope();
      refreshWithAutoFit();
      enableControls();
    }
  }

  private void selectPoint(MouseEvent e) {
    if (envelope != null) {
      int lx = e.getX();
      EnvelopeView envelopeView = new EnvelopeView(envelopePanel);
      double x = ((double) lx + envelopeView.getEnvelopeXTrans())
          / envelopeView.getEnvelopeXScale();
      int sel = 0;
      double dist = x - envelope.getX()[0];
      if (dist < 0)
        dist = 0 - dist;
      for (int i = 1; i < envelope.size(); i++) {
        double dist2 = x - envelope.getX()[i];
        if (dist2 < 0)
          dist2 = 0 - dist2;
        if (dist2 < dist) {
          dist = dist2;
          sel = i;
        }
      }
      envelope.select(sel);
      notifySelectionChange(envelope.getSelectedIdx(), envelope.getSelectedX(), envelope.getSelectedY());
      refreshXField();
      refreshYField();
      envelopePanel.repaint();
    }
  }

  private void finishAddPoint(MouseEvent e) {
    if (envelope != null) {
      int lx = e.getX();
      int ly = e.getY();
      EnvelopeView envelopeView = new EnvelopeView(envelopePanel);

      double x = (double) Tools.FTOI(((double) lx + envelopeView.getEnvelopeXTrans())
          / envelopeView.getEnvelopeXScale());
      double y = ((double) ly + envelopeView.getEnvelopeYTrans())
          / envelopeView.getEnvelopeYScale();
      {
        for (int i = 0; i < envelope.size(); i++) {
          if (MathLib.fabs(x - envelope.getX()[i]) < 0.01)
            return;
        }
      }

      int xl = (Tools.FTOI(x));

      int pred = -1;
      for (int i = 0; i < envelope.size(); i++) {
        if (envelope.getX()[i] < xl)
          pred = i;
        else if (envelope.getX()[i] == xl) {
          xl += 1.0;
          pred = i;
        }
      }
      int cnt = envelope.size() + 1;
      int[] xVals = new int[cnt];
      double[] yVals = new double[cnt];

      if (pred >= 0) {
        for (int i = 0; i <= pred; i++) {
          xVals[i] = envelope.getX()[i];
          yVals[i] = envelope.getY()[i];
        }
        int curr = pred + 1;
        xVals[curr] = xl;
        yVals[curr++] = y;
        for (int i = pred + 1; i < envelope.size(); i++) {
          xVals[curr] = envelope.getX()[i];
          yVals[curr++] = envelope.getY()[i];
        }
      }
      else {
        xVals[0] = xl;
        yVals[0] = y;
        int curr = 1;
        for (int i = 0; i < envelope.size(); i++) {
          xVals[curr] = envelope.getX()[i];
          yVals[curr++] = envelope.getY()[i];
        }
      }
      envelope.setValues(xVals, yVals);
      envelope.select(pred + 1);
      notifySelectionChange(envelope.getSelectedIdx(), envelope.getSelectedX(), envelope.getSelectedY());

      //      refreshEnvelope();
      refreshWithAutoFit();
      enableControls();
    }
  }

  private void scaleCurve(MouseEvent e, Direction pDirection) {
    if (envelope != null && !envelope.isLocked()) {
      int lx = pDirection == Direction.HORIZ ? (e.getX() - lastMouseX) : 0;
      int ly = pDirection == Direction.VERT ? (e.getY() - lastMouseY) : 0;

      double viewXMin = xMinREd.getDoubleValue();
      double viewXMax = xMaxREd.getDoubleValue();
      double viewYMin = yMinREd.getDoubleValue();
      double viewYMax = yMaxREd.getDoubleValue();

      if (viewXMax > viewXMin && viewYMax > viewYMin) {
        double scl = 1.1;
        double dx = 1.0, dy = 1.0;
        if (lx > 0) {
          dx = scl;
        }
        else if (lx < 0) {
          if (allowFrameDownScale()) {
            dx = 1.0 / scl;
          }
        }
        if (ly > 0) {
          dy = 1.0 / scl;
        }
        else if (ly < 0) {
          dy = scl;
        }

        if (MathLib.fabs(dx) > 0 || MathLib.fabs(dy) > 0) {
          for (int i = 0; i < envelope.getX().length; i++) {
            envelope.getX()[i] = Tools.FTOI(dx * envelope.getX()[i]);
            envelope.getY()[i] *= dy;
          }
          refreshXMaxField();
          refreshYMaxField();
          refreshXField();
          refreshYField();
          notifySelectionChange(envelope.getSelectedIdx(), envelope.getSelectedX(), envelope.getSelectedY());
          envelopePanel.repaint();
        }
      }
    }
  }

  private boolean allowFrameDownScale() {
    if (envelope != null && envelope.getX().length > 1) {
      for (int i = 0; i < envelope.getX().length - 1; i++) {
        if ((envelope.getX()[i] + 1) == envelope.getX()[i + 1]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  private void moveCurve(java.awt.event.MouseEvent e, Direction pDirection) {
    if (envelope != null && !envelope.isLocked()) {
      int lx = pDirection == Direction.HORIZ ? (e.getX() - lastMouseX) : 0;
      int ly = pDirection == Direction.VERT ? (e.getY() - lastMouseY) : 0;

      double viewXMin = xMinREd.getDoubleValue();
      double viewXMax = xMaxREd.getDoubleValue();
      double viewYMin = yMinREd.getDoubleValue();
      double viewYMax = yMaxREd.getDoubleValue();
      double X_SCALE = 75.0;
      double Y_SCALE = X_SCALE * 0.666;

      if (viewXMax > viewXMin && viewYMax > viewYMin) {
        double dx = 0.0, dy = 0.0;
        if (lx > 0) {
          dx = (viewXMax - viewXMin) / X_SCALE;
          if (dx < 1.0) {
            dx = 1.0;
          }
        }
        else if (lx < 0) {
          dx = -(viewXMax - viewXMin) / X_SCALE;
          if (dx > -1.0) {
            dx = -1.0;
          }
        }
        if (ly > 0) {
          dy = -(viewYMax - viewYMin) / Y_SCALE;
        }
        else if (ly < 0) {
          dy = (viewYMax - viewYMin) / Y_SCALE;
        }
        if (MathLib.fabs(dx) > 0 || MathLib.fabs(dy) > 0) {
          for (int i = 0; i < envelope.getX().length; i++) {
            envelope.getX()[i] += dx;
            envelope.getY()[i] += dy;
          }
          refreshXMaxField();
          refreshYMaxField();
          refreshXField();
          refreshYField();
          notifySelectionChange(envelope.getSelectedIdx(), envelope.getSelectedX(), envelope.getSelectedY());
          envelopePanel.repaint();
        }
      }
    }
  }

  private void movePoint(java.awt.event.MouseEvent e) {
    if (envelope != null && !envelope.isLocked()) {
      int lx = e.getX();
      int ly = e.getY();
      EnvelopeView envelopeView = new EnvelopeView(envelopePanel);

      double x = (double) Tools.FTOI(((double) lx + envelopeView.getEnvelopeXTrans())
          / envelopeView.getEnvelopeXScale());
      double y = ((double) ly + envelopeView.getEnvelopeYTrans())
          / envelopeView.getEnvelopeYScale();
      if (envelope.getSelectedIdx() > 0) {
        double xc;
        xc = envelope.getX()[envelope.getSelectedIdx() - 1];
        if (x <= xc)
          x = xc + 1.0;
      }
      if (envelope.getSelectedIdx() < (envelope.size() - 1)) {
        double xc;
        xc = envelope.getX()[envelope.getSelectedIdx() + 1];
        if (x >= xc)
          x = xc - 1.0;
      }
      if (lx <= envelopeView.getEnvelopeLeft()) {
        //        int xi = Tools.FTOI(x);
        int xi = envelope.getViewXMin() - 1;
        if (xi < -100)
          xi = -100;
        envelope.setViewXMin(xi);
        envelope.getX()[envelope.getSelectedIdx()] = xi;
        envelope.getY()[envelope.getSelectedIdx()] = y;
        notifyValueChange(envelope.getSelectedIdx(), xi, y);
        refreshXMinField();
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
      else if (lx >= envelopeView.getEnvelopeRight()) {
        //        int xi = Tools.FTOI(x);
        int xi = envelope.getViewXMax() + 1;
        if (xi > 99999)
          xi = 99999;
        envelope.setViewXMax(xi);
        envelope.getX()[envelope.getSelectedIdx()] = xi;
        envelope.getY()[envelope.getSelectedIdx()] = y;
        notifyValueChange(envelope.getSelectedIdx(), xi, y);
        refreshXMaxField();
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
      else if (ly <= envelopeView.getEnvelopeTop()) {
        int xi = Tools.FTOI(x);
        y = envelope.getViewYMax() + 1.0;
        if (y > 32000.0)
          y = 32000.0;
        envelope.setViewYMax(y);
        envelope.getX()[envelope.getSelectedIdx()] = xi;
        envelope.getY()[envelope.getSelectedIdx()] = y;
        notifyValueChange(envelope.getSelectedIdx(), xi, y);
        refreshYMaxField();
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
      else if (ly >= envelopeView.getEnvelopeBottom()) {
        int xi = Tools.FTOI(x);
        y = envelope.getViewYMin() - 1.0;
        if (y < -32000.0)
          y = -32000.0;
        envelope.setViewYMin(y);
        envelope.getX()[envelope.getSelectedIdx()] = xi;
        envelope.getY()[envelope.getSelectedIdx()] = y;
        notifyValueChange(envelope.getSelectedIdx(), xi, y);
        refreshYMinField();
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
      else {
        int xi = Tools.FTOI(x);
        envelope.getX()[envelope.getSelectedIdx()] = xi;
        envelope.getY()[envelope.getSelectedIdx()] = y;
        notifyValueChange(envelope.getSelectedIdx(), xi, y);
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
    }
  }

  private void notifyValueChange(int pSelectedPoint, int pX, double pY) {
    for (EnvelopeChangeListener listener : valueChangeListeners) {
      listener.notify(pSelectedPoint, pX, pY);
    }
  }

  private void notifySelectionChange(int pSelectedPoint, int pX, double pY) {
    for (EnvelopeChangeListener listener : selectionChangeListeners) {
      listener.notify(pSelectedPoint, pX, pY);
    }
  }

  public void viewDown() {
    if (envelope != null) {
      double dy = ((envelope.getViewYMax() - envelope.getViewYMin()) / 10.0);
      envelope.setViewYMin(envelope.getViewYMin() + dy);
      envelope.setViewYMax(envelope.getViewYMax() + dy);
      refreshYMinField();
      refreshYMaxField();
      envelopePanel.repaint();
    }
  }

  public void viewUp() {
    if (envelope != null) {
      double dy = ((envelope.getViewYMax() - envelope.getViewYMin()) / 10.0);
      envelope.setViewYMin(envelope.getViewYMin() - dy);
      envelope.setViewYMax(envelope.getViewYMax() - dy);
      refreshYMinField();
      refreshYMaxField();
      envelopePanel.repaint();
    }
  }

  public void viewAll() {
    if (envelope != null) {
      double xmin, xmax, ymin, ymax;
      if (envelope.size() == 1) {
        xmin = envelope.getX()[0] - 1.0;
        xmax = envelope.getX()[0] + 10.0;
        ymin = envelope.getY()[0] - 1.0;
        ymax = envelope.getY()[0] + 1.0;
      }
      else {
        xmin = xmax = envelope.getX()[0];
        ymin = ymax = envelope.getY()[0];
        for (int i = 1; i < envelope.size(); i++) {
          if (envelope.getX()[i] < xmin)
            xmin = envelope.getX()[i];
          else if (envelope.getX()[i] > xmax)
            xmax = envelope.getX()[i];
          if (envelope.getY()[i] < ymin)
            ymin = envelope.getY()[i];
          else if (envelope.getY()[i] > ymax)
            ymax = envelope.getY()[i];
        }
        double dx = (xmax - xmin) / 10.0;
        double dy = (ymax - ymin) / 8.0;
        xmin = xmin - dx;
        xmax = xmax + dx;
        ymin = ymin - dy;
        ymax = ymax + dy;
      }
      if ((xmax - xmin) < 10.0)
        xmax = xmin + 10.0;
      if ((ymax - ymin) < 0.00005)
        ymax = ymin + 0.00005;

      envelope.setViewXMin(Tools.FTOI(xmin));
      envelope.setViewXMax(Tools.FTOI(xmax));
      envelope.setViewYMin(ymin);
      envelope.setViewYMax(ymax);

      refreshXMinField();
      refreshXMaxField();
      refreshYMinField();
      refreshYMaxField();
      envelopePanel.repaint();
    }
  }

  public void viewLeft() {
    if (envelope != null) {
      int dx = (int) ((envelope.getViewXMax() - envelope.getViewXMin()) / 20.0 + 0.5);
      envelope.setViewXMin(envelope.getViewXMin() + dx);
      envelope.setViewXMax(envelope.getViewXMax() + dx);
      refreshXMinField();
      refreshXMaxField();
      envelopePanel.repaint();
    }
  }

  public void viewRight() {
    if (envelope != null) {
      int dx = (int) ((envelope.getViewXMax() - envelope.getViewXMin()) / 20.0 + 0.5);
      envelope.setViewXMin(envelope.getViewXMin() - dx);
      envelope.setViewXMax(envelope.getViewXMax() - dx);
      refreshXMinField();
      refreshXMaxField();
      envelopePanel.repaint();
    }
  }

  public void setNoRefresh(boolean pNoRefresh) {
    noRefresh = pNoRefresh;
  }

  public void registerValueChangeListener(EnvelopeChangeListener pEnvelopeValueChangeListener) {
    if (!valueChangeListeners.contains(pEnvelopeValueChangeListener)) {
      valueChangeListeners.add(pEnvelopeValueChangeListener);
    }
  }

  public void registerSelectionChangeListener(EnvelopeChangeListener pEnvelopeSelectionChangeListener) {
    if (!selectionChangeListeners.contains(pEnvelopeSelectionChangeListener)) {
      selectionChangeListeners.add(pEnvelopeSelectionChangeListener);
    }
  }

  public void setEnvelope(Envelope pEnvelope) {
    envelope = pEnvelope;
  }

  public void importMP3() {
    try {
      Prefs prefs = Prefs.getPrefs();
      JFileChooser chooser = new SoundFileChooser(prefs);
      if (prefs.getInputSoundFilePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getInputSoundFilePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(envelopePanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        prefs.setLastInputSoundFile(file);
        mp3Data = new EnvelopeMP3Data(file.getAbsolutePath());
        mp3SettingsChanged();
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void applyTransform(boolean pReverse) {
    try {
      int x[] = envelope.getX();
      double y[] = envelope.getY();
      if (x != null && x.length > 0) {
        double xscale = xScaleREd != null ? xScaleREd.getDoubleValue() : 1.0;
        double xoffset = xOffsetREd != null ? xOffsetREd.getDoubleValue() : 0.0;
        double yscale = yScaleREd != null ? yScaleREd.getDoubleValue() : 1.0;
        double yoffset = yOffsetREd != null ? yOffsetREd.getDoubleValue() : 0.0;
        if (xscale < MathLib.EPSILON || yscale < MathLib.EPSILON) {
          throw new RuntimeException("Specified scale-value is too small");
        }

        if (pReverse) {
          xscale = 1.0 / xscale;
          xoffset = 0.0 - xoffset;
          yscale = 1.0 / yscale;
          yoffset = 0.0 - yoffset;
        }
        double xmin = x[0], xmax = x[0];
        double ymin = y[0], ymax = y[0];
        for (int i = 1; i < x.length; i++) {
          if (x[i] < xmin)
            xmin = x[i];
          else if (x[i] > xmax)
            xmax = x[i];
          if (y[i] < ymin)
            ymin = y[i];
          else if (y[i] > ymax)
            ymax = y[i];
        }
        double cx = 0.0;
        double cy = 0.0;

        int newx[] = new int[x.length];
        double newy[] = new double[y.length];
        for (int i = 0; i < x.length; i++) {
          double dx = x[i] - cx;
          newx[i] = (int) (dx * xscale + xoffset + cx + 0.5);
          double dy = y[i] - cy;
          newy[i] = dy * yscale + yoffset + cy;
        }
        envelope.setValues(newx, newy);
        refreshWithAutoFit();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void refreshWithAutoFit() {
    if (autofitCBx != null && autofitCBx.isSelected()) {
      viewAll();
      refreshXField();
      refreshYField();
    }
    else {
      refreshEnvelope();
    }
  }

  public void mp3SettingsChanged() {
    try {
      if (mp3Data != null) {
        mp3Data.applyToEvelope(envelope, mp3FPSREd.getIntValue(), mp3ChannelREd.getIntValue() - 1, mp3OffsetREd.getIntValue(), mp3DurationREd.getIntValue());
        refreshEnvelope();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void editModeChanged() {
    lastMouseX = lastMouseY = -1;
  }

  public void importRawDataFromFile() {
    try {
      Prefs prefs = Prefs.getPrefs();
      JFileChooser chooser = new SplineDataFileChooser(prefs);
      if (prefs.getTinaRawMotionDataPath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getTinaRawMotionDataPath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      else if (prefs.getTinaFlamePath() != null) {
        try {
          chooser.setCurrentDirectory(new File(prefs.getTinaFlamePath()));
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(envelopePanel) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        String data = Tools.readUTF8Textfile(file.getAbsolutePath());
        importRawMotionData(data);
        enableControls();
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  public void importRawDataFromClipboard() {
    try {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable clipData = clipboard.getContents(clipboard);
      if (clipData != null) {
        if (clipData.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          String data = (String) (clipData.getTransferData(
              DataFlavor.stringFlavor));
          importRawMotionData(data);
          enableControls();
        }
        else {
          throw new Exception("No text-data in the clipboard");
        }
      }
      else {
        throw new RuntimeException("No data in the clipboard");
      }
    }
    catch (Throwable ex) {
      errorHandler.handleError(ex);
    }
  }

  private void importRawMotionData(String pData) {
    List<List<Double>> rows = new ArrayList<List<Double>>();
    StringTokenizer rowTokenizer = new StringTokenizer(pData, "\n\r");
    while (rowTokenizer.hasMoreElements()) {
      String row = rowTokenizer.nextToken().trim();
      if (row.length() > 0 && !row.startsWith("#") && !row.startsWith(";") && !row.startsWith("DFSP")) {
        List<Double> col = new ArrayList<Double>();
        rows.add(col);
        StringTokenizer colTokenizer = new StringTokenizer(row, " \t");
        while (colTokenizer.hasMoreElements()) {
          String colData = colTokenizer.nextToken().trim();
          Double vlaue = Double.parseDouble(colData);
          col.add(vlaue);
        }
      }
    }
    int frameIndex = rawDataFrameColumnField.getIntValue() - 1;
    int amplitudeIndex = rawDataAmplitudeColumnField.getIntValue() - 1;
    double frameScale = rawDataFrameScaleField.getDoubleValue();
    double amplitudeScale = rawDataAmplitudeScaleField.getDoubleValue();
    if (rows.size() > 0) {
      Map<Integer, Double> keyFrames = new HashMap<Integer, Double>();
      for (int i = 0; i < rows.size(); i++) {
        List<Double> row = rows.get(i);
        double frame = frameIndex >= 0 && frameIndex < row.size() ? row.get(frameIndex) : 0.0;
        double amplitude = amplitudeIndex >= 0 && amplitudeIndex < row.size() ? row.get(amplitudeIndex) : 0.0;
        keyFrames.put(Tools.FTOI(frame * frameScale), amplitude * amplitudeScale);
      }

      List<Integer> sortedKeys = new ArrayList<Integer>(keyFrames.keySet());
      Collections.sort(sortedKeys);
      int newx[] = new int[sortedKeys.size()];
      double newy[] = new double[sortedKeys.size()];
      for (int i = 0; i < sortedKeys.size(); i++) {
        newx[i] = sortedKeys.get(i);
        newy[i] = keyFrames.get(newx[i]);
      }
      envelope.setValues(newx, newy);
      viewAll();
      refreshXField();
      refreshYField();
    }
  }

  public void exportRawDataToFile() {
    Prefs prefs = Prefs.getPrefs();
    JFileChooser chooser = new SplineDataFileChooser(prefs);
    if (prefs.getTinaRawMotionDataPath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getTinaRawMotionDataPath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    else if (prefs.getTinaFlamePath() != null) {
      try {
        chooser.setCurrentDirectory(new File(prefs.getTinaFlamePath()));
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    if (chooser.showSaveDialog(envelopePanel) == JFileChooser.APPROVE_OPTION) {
      try {
        String motionData = getRawMotionData(chooser.getSelectedFile().getAbsolutePath());
        Tools.writeUTF8Textfile(chooser.getSelectedFile().getAbsolutePath(), motionData);
      }
      catch (Exception ex) {
        errorHandler.handleError(ex);
      }
    }
  }

  public void exportRawDataToClipboard() {
    String motionData = getRawMotionData("");
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection data = new StringSelection(motionData);
    clipboard.setContents(data, data);
  }

  private String getRawMotionData(String pFilename) {
    boolean fusionCompatible = Tools.FILEEXT_SPL.equalsIgnoreCase(Tools.getFileExt(pFilename));
    StringBuilder sb = new StringBuilder();
    if (fusionCompatible) {
      sb.append("DFSP\n");
    }
    int x[] = envelope.getX();
    double y[] = envelope.getY();
    for (int i = 0; i < x.length; i++) {
      sb.append(String.valueOf(x[i]));
      sb.append(" ");
      sb.append(Tools.doubleToString(y[i]));
      sb.append("\n");
    }
    return sb.toString();
  }
}
