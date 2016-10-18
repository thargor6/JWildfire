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

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.EnvelopePanel;
import org.jwildfire.envelope.EnvelopeView;
import org.jwildfire.script.Action;
import org.jwildfire.script.Parameter;

public class EnvelopeController {
  private enum MouseClickWaitMode {
    ADD_POINT, REMOVE_POINT, NONE
  }

  private final JComboBox propertyCmb;
  private final JButton createButton;
  private final JButton removeButton;
  private final JButton addPointButton;
  private final JButton removePointButton;
  private final JButton clearButton;
  private final JButton viewAllButton;
  private final JButton viewLeftButton;
  private final JButton viewRightButton;
  private final JButton viewUpButton;
  private final JButton viewDownButton;
  private final JTextField xMinREd;
  private final JTextField xMaxREd;
  private final JTextField yMinREd;
  private final JTextField yMaxREd;
  private final JTextField xREd;
  private final JTextField yREd;
  private final JComboBox interpolationCmb;
  private final JCheckBox lockedCBx;
  private final JCheckBox autofitCBx;
  private final EnvelopePanel envelopePanel;

  private Envelope currEnvelope = null;
  private Action currAction = null;

  private MouseClickWaitMode mouseClickWaitMode = MouseClickWaitMode.NONE;

  public EnvelopeController(JComboBox pPropertyCmb, JButton pCreateButton, JButton pRemoveButton,
      JButton pAddPointButton, JButton pRemovePointButton, JButton pClearButton,
      JTextField pXMinREd, JTextField pXMaxREd, JTextField pYMinREd, JTextField pYMaxREd,
      JTextField pXREd, JTextField pYREd, JComboBox pInterpolationCmb, JButton pViewAllButton,
      JButton pViewLeftButton, JButton pViewRightButton, JButton pViewUpButton,
      JButton pViewDownButton, JCheckBox pLockedCBx, EnvelopePanel pEnvelopePanel,
      JCheckBox pAutofitCBx) {
    propertyCmb = pPropertyCmb;
    createButton = pCreateButton;
    removeButton = pRemoveButton;
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
    lockedCBx = pLockedCBx;
    autofitCBx = pAutofitCBx;

    envelopePanel = pEnvelopePanel;
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
          movePoint(e);
        }
      }
    });

  }

  private void setCurrEnvelope(Envelope currEnvelope) {
    this.currEnvelope = currEnvelope;
    envelopePanel.setEnvelope(currEnvelope);
  }

  public Envelope getCurrEnvelope() {
    return currEnvelope;
  }

  public void enableControls() {
    boolean hasEnvelope = (currEnvelope != null);
    boolean editable = hasEnvelope && !currEnvelope.isLocked();
    propertyCmb.setEnabled(currAction != null);
    String propName = (String) propertyCmb.getSelectedItem();
    interpolationCmb.setEnabled(editable);
    xMinREd.setEnabled(hasEnvelope);
    xMaxREd.setEnabled(hasEnvelope);
    yMinREd.setEnabled(hasEnvelope);
    yMaxREd.setEnabled(hasEnvelope);
    xREd.setEnabled(editable);
    yREd.setEnabled(editable);
    addPointButton.setEnabled(editable);
    removePointButton.setEnabled(editable && (currEnvelope.size() > 1));
    clearButton.setEnabled(editable);
    createButton.setEnabled(!hasEnvelope && (currAction != null) && (propName != null));
    removeButton.setEnabled(hasEnvelope);
    viewAllButton.setEnabled(hasEnvelope);
    viewLeftButton.setEnabled(hasEnvelope);
    viewRightButton.setEnabled(hasEnvelope);
    viewUpButton.setEnabled(hasEnvelope);
    viewDownButton.setEnabled(hasEnvelope);
    lockedCBx.setEnabled(hasEnvelope);
  }

  public void setCurrAction(Action currAction) {
    this.currAction = currAction;
    setCurrEnvelope(null);
    propertyCmb.removeAllItems();
    if (currAction != null) {
      for (Parameter parameter : currAction.getParameterList()) {
        if (isNumber(parameter.getValue()))
          propertyCmb.addItem(parameter.getName());
      }
    }
  }

  private boolean isNumber(String value) {
    if (value != null) {
      try {
        Double.parseDouble(value);
        return true;
      }
      catch (Exception ex) {

      }
    }
    return false;
  }

  public Action getCurrAction() {
    return currAction;
  }

  private void refreshLockedCBx() {
    if (currEnvelope != null) {
      lockedCBx.setSelected(currEnvelope.isLocked());
    }
    else {
      lockedCBx.setSelected(false);
    }
  }

  private void refreshXMinField() {
    if (currEnvelope != null) {
      xMinREd.setText(Tools.intToString(currEnvelope.getViewXMin()));
    }
    else {
      xMinREd.setText("");
    }
  }

  private void refreshXMaxField() {
    if (currEnvelope != null) {
      xMaxREd.setText(Tools.intToString(currEnvelope.getViewXMax()));
    }
    else {
      xMaxREd.setText("");
    }
  }

  private void refreshYMinField() {
    if (currEnvelope != null) {
      yMinREd.setText(Tools.doubleToString(currEnvelope.getViewYMin()));
    }
    else {
      yMinREd.setText("");
    }
  }

  private void refreshYMaxField() {
    if (currEnvelope != null) {
      yMaxREd.setText(Tools.doubleToString(currEnvelope.getViewYMax()));
    }
    else {
      yMaxREd.setText("");
    }
  }

  private void refreshXField() {
    if (currEnvelope != null) {
      xREd.setText(Tools.intToString(currEnvelope.getSelectedX()));
    }
    else {
      xREd.setText("");
    }
  }

  private void refreshYField() {
    if (currEnvelope != null) {
      yREd.setText(Tools.doubleToString(currEnvelope.getSelectedY()));
    }
    else {
      yREd.setText("");
    }
  }

  public void refreshEnvelope() {
    refreshLockedCBx();
    refreshXField();
    refreshXMinField();
    refreshXMaxField();
    refreshYField();
    refreshYMinField();
    refreshYMaxField();
    envelopePanel.repaint();
  }

  public void removePoint() {
    setCrosshairCursor();
    mouseClickWaitMode = MouseClickWaitMode.REMOVE_POINT;
  }

  public void addPoint() {
    setCrosshairCursor();
    mouseClickWaitMode = MouseClickWaitMode.ADD_POINT;
  }

  public void interpolationCmbChanged() {
    if (currEnvelope != null) {
      Envelope.Interpolation interpolation = (Envelope.Interpolation) interpolationCmb
          .getSelectedItem();
      currEnvelope.setInterpolation(interpolation);
      refreshEnvelope();
    }
  }

  public void propertyCmbChanged() {
    String propName = (String) propertyCmb.getSelectedItem();
    Parameter parameter = currAction.getParameterByName(propName);
    Envelope envelope = (parameter != null) ? parameter.getEnvelope() : null;
    setCurrEnvelope(envelope);
    enableControls();
    refreshEnvelope();
  }

  public void removeEnvelope() {
    String propName = (String) propertyCmb.getSelectedItem();
    Parameter parameter = currAction.getParameterByName(propName);
    parameter.setEnvelope(null);
    setCurrEnvelope(null);
    enableControls();
    refreshEnvelope();
  }

  public void createEnvelope() {
    String propName = (String) propertyCmb.getSelectedItem();
    Parameter parameter = currAction.getParameterByName(propName);
    Double val = Double.parseDouble(parameter.getValue());
    Envelope env = new Envelope(val);
    env.select(0);
    parameter.setEnvelope(env);
    setCurrEnvelope(env);
    enableControls();
    refreshEnvelope();
  }

  public void clearEnvelope() {
    currEnvelope.clear();
    refreshEnvelope();
  }

  public void editFieldChanged() {
    currEnvelope.setViewXMin(Tools.stringToInt(xMinREd.getText()));
    currEnvelope.setViewXMax(Tools.stringToInt(xMaxREd.getText()));
    currEnvelope.setSelectedX(Tools.stringToInt(xREd.getText()));
    currEnvelope.setViewYMin(Tools.stringToDouble(yMinREd.getText()));
    currEnvelope.setViewYMax(Tools.stringToDouble(yMaxREd.getText()));
    currEnvelope.setSelectedY(Tools.stringToDouble(yREd.getText()));
    if ((currEnvelope.getViewXMax() - currEnvelope.getViewXMin()) < 1)
      currEnvelope.setViewXMax(currEnvelope.getViewXMin() + 1);
    if ((currEnvelope.getViewYMax() - currEnvelope.getViewYMin()) < 0.001)
      currEnvelope.setViewYMax(currEnvelope.getViewYMin() + 0.001);
    refreshEnvelope();
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
    if ((currEnvelope != null) && (currEnvelope.size() > 1)) {
      int lx = e.getX();
      EnvelopeView envelopeView = new EnvelopeView(envelopePanel);
      double x = ((double) lx + envelopeView.getEnvelopeXTrans())
          / envelopeView.getEnvelopeXScale();
      int sel = 0;
      double dist = x - currEnvelope.getX()[0];
      if (dist < 0)
        dist = 0 - dist;
      for (int i = 1; i < currEnvelope.size(); i++) {
        double dist2 = x - currEnvelope.getX()[i];
        if (dist2 < 0)
          dist2 = 0 - dist2;
        if (dist2 < dist) {
          dist = dist2;
          sel = i;
        }
      }

      int cnt = currEnvelope.size() - 1;
      int[] xVals = new int[cnt];
      double[] yVals = new double[cnt];

      int curr = 0;
      for (int i = 0; i < currEnvelope.size(); i++) {
        if (i != sel) {
          xVals[curr] = currEnvelope.getX()[i];
          yVals[curr++] = currEnvelope.getY()[i];
        }
      }
      currEnvelope.setValues(xVals, yVals);
      refreshEnvelope();
      enableControls();
    }
  }

  private void selectPoint(MouseEvent e) {
    if (currEnvelope != null) {
      int lx = e.getX();
      EnvelopeView envelopeView = new EnvelopeView(envelopePanel);
      double x = ((double) lx + envelopeView.getEnvelopeXTrans())
          / envelopeView.getEnvelopeXScale();
      int sel = 0;
      double dist = x - currEnvelope.getX()[0];
      if (dist < 0)
        dist = 0 - dist;
      for (int i = 1; i < currEnvelope.size(); i++) {
        double dist2 = x - currEnvelope.getX()[i];
        if (dist2 < 0)
          dist2 = 0 - dist2;
        if (dist2 < dist) {
          dist = dist2;
          sel = i;
        }
      }
      currEnvelope.select(sel);
      refreshXField();
      refreshYField();
      envelopePanel.repaint();
    }
  }

  private void finishAddPoint(MouseEvent e) {
    if (currEnvelope != null) {
      int lx = e.getX();
      int ly = e.getY();
      EnvelopeView envelopeView = new EnvelopeView(envelopePanel);

      double x = (double) Tools.FTOI(((double) lx + envelopeView.getEnvelopeXTrans())
          / envelopeView.getEnvelopeXScale());
      double y = ((double) ly + envelopeView.getEnvelopeYTrans())
          / envelopeView.getEnvelopeYScale();
      {
        for (int i = 0; i < currEnvelope.size(); i++) {
          if (MathLib.fabs(x - currEnvelope.getX()[i]) < 0.01)
            return;
        }
      }

      int xl = (Tools.FTOI(x));

      int pred = -1;
      for (int i = 0; i < currEnvelope.size(); i++) {
        if (currEnvelope.getX()[i] < xl)
          pred = i;
        else if (currEnvelope.getX()[i] == xl) {
          xl += 1.0;
          pred = i;
        }
      }
      int cnt = currEnvelope.size() + 1;
      int[] xVals = new int[cnt];
      double[] yVals = new double[cnt];

      if (pred >= 0) {
        for (int i = 0; i <= pred; i++) {
          xVals[i] = currEnvelope.getX()[i];
          yVals[i] = currEnvelope.getY()[i];
        }
        int curr = pred + 1;
        xVals[curr] = xl;
        yVals[curr++] = y;
        for (int i = pred + 1; i < currEnvelope.size(); i++) {
          xVals[curr] = currEnvelope.getX()[i];
          yVals[curr++] = currEnvelope.getY()[i];
        }
      }
      else {
        xVals[0] = xl;
        yVals[0] = y;
        int curr = 1;
        for (int i = 0; i < currEnvelope.size(); i++) {
          xVals[curr] = currEnvelope.getX()[i];
          yVals[curr++] = currEnvelope.getY()[i];
        }
      }
      currEnvelope.setValues(xVals, yVals);
      currEnvelope.select(pred + 1);
      refreshEnvelope();
      enableControls();
    }
  }

  private void movePoint(java.awt.event.MouseEvent e) {
    if ((currEnvelope != null) && (!currEnvelope.isLocked())) {
      int lx = e.getX();
      int ly = e.getY();
      EnvelopeView envelopeView = new EnvelopeView(envelopePanel);

      double x = (double) Tools.FTOI(((double) lx + envelopeView.getEnvelopeXTrans())
          / envelopeView.getEnvelopeXScale());
      double y = ((double) ly + envelopeView.getEnvelopeYTrans())
          / envelopeView.getEnvelopeYScale();
      if (currEnvelope.getSelectedIdx() > 0) {
        double xc;
        xc = currEnvelope.getX()[currEnvelope.getSelectedIdx() - 1];
        if (x <= xc)
          x = xc + 1.0;
      }
      if (currEnvelope.getSelectedIdx() < (currEnvelope.size() - 1)) {
        double xc;
        xc = currEnvelope.getX()[currEnvelope.getSelectedIdx() + 1];
        if (x >= xc)
          x = xc - 1.0;
      }
      if (lx <= envelopeView.getEnvelopeLeft()) {
        int xi = Tools.FTOI(x);
        if (xi < -9999)
          xi = -9999;
        currEnvelope.setViewXMin(xi);
        currEnvelope.getX()[currEnvelope.getSelectedIdx()] = xi;
        currEnvelope.getY()[currEnvelope.getSelectedIdx()] = y;
        refreshXMinField();
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
      else if (lx >= envelopeView.getEnvelopeRight()) {
        int xi = Tools.FTOI(x);
        if (xi > 9999)
          xi = 9999;
        currEnvelope.setViewXMax(xi);
        currEnvelope.getX()[currEnvelope.getSelectedIdx()] = xi;
        currEnvelope.getY()[currEnvelope.getSelectedIdx()] = y;
        refreshXMaxField();
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
      else if (ly <= envelopeView.getEnvelopeTop()) {
        int xi = Tools.FTOI(x);
        if (y > 32000.0)
          y = 32000.0;
        currEnvelope.setViewYMax(y);
        currEnvelope.getX()[currEnvelope.getSelectedIdx()] = xi;
        currEnvelope.getY()[currEnvelope.getSelectedIdx()] = y;
        refreshYMaxField();
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
      else if (ly >= envelopeView.getEnvelopeBottom()) {
        int xi = Tools.FTOI(x);
        if (y < -32000.0)
          y = -32000.0;
        currEnvelope.setViewYMin(y);
        currEnvelope.getX()[currEnvelope.getSelectedIdx()] = xi;
        currEnvelope.getY()[currEnvelope.getSelectedIdx()] = y;
        refreshYMinField();
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
      else {
        int xi = Tools.FTOI(x);
        currEnvelope.getX()[currEnvelope.getSelectedIdx()] = xi;
        currEnvelope.getY()[currEnvelope.getSelectedIdx()] = y;
        refreshXField();
        refreshYField();
        envelopePanel.repaint();
      }
    }
  }

  public void viewDown() {
    if (currEnvelope != null) {
      double dy = ((currEnvelope.getViewYMax() - currEnvelope.getViewYMin()) / 10.0);
      currEnvelope.setViewYMin(currEnvelope.getViewYMin() + dy);
      currEnvelope.setViewYMax(currEnvelope.getViewYMax() + dy);
      refreshYMinField();
      refreshYMaxField();
      envelopePanel.repaint();
    }
  }

  public void viewUp() {
    if (currEnvelope != null) {
      double dy = ((currEnvelope.getViewYMax() - currEnvelope.getViewYMin()) / 10.0);
      currEnvelope.setViewYMin(currEnvelope.getViewYMin() - dy);
      currEnvelope.setViewYMax(currEnvelope.getViewYMax() - dy);
      refreshYMinField();
      refreshYMaxField();
      envelopePanel.repaint();
    }
  }

  public void viewAll() {
    if (currEnvelope != null) {
      double xmin, xmax, ymin, ymax;
      if (currEnvelope.size() == 1) {
        xmin = currEnvelope.getX()[0] - 1.0;
        xmax = currEnvelope.getX()[0] + 10.0;
        ymin = currEnvelope.getY()[0] - 1.0;
        ymax = currEnvelope.getY()[0] + 1.0;
      }
      else {
        xmin = xmax = currEnvelope.getX()[0];
        ymin = ymax = currEnvelope.getY()[0];
        for (int i = 1; i < currEnvelope.size(); i++) {
          if (currEnvelope.getX()[i] < xmin)
            xmin = currEnvelope.getX()[i];
          else if (currEnvelope.getX()[i] > xmax)
            xmax = currEnvelope.getX()[i];
          if (currEnvelope.getY()[i] < ymin)
            ymin = currEnvelope.getY()[i];
          else if (currEnvelope.getY()[i] > ymax)
            ymax = currEnvelope.getY()[i];
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
      if ((ymax - ymin) < 0.5)
        ymax = ymin + 0.5;

      currEnvelope.setViewXMin(Tools.FTOI(xmin));
      currEnvelope.setViewXMax(Tools.FTOI(xmax));
      currEnvelope.setViewYMin(ymin);
      currEnvelope.setViewYMax(ymax);

      refreshXMinField();
      refreshXMaxField();
      refreshYMinField();
      refreshYMaxField();
      envelopePanel.repaint();
    }
  }

  public void viewLeft() {
    if (currEnvelope != null) {
      int dx = (int) ((currEnvelope.getViewXMax() - currEnvelope.getViewXMin()) / 20.0 + 0.5);
      currEnvelope.setViewXMin(currEnvelope.getViewXMin() + dx);
      currEnvelope.setViewXMax(currEnvelope.getViewXMax() + dx);
      refreshXMinField();
      refreshXMaxField();
      envelopePanel.repaint();
    }
  }

  public void viewRight() {
    if (currEnvelope != null) {
      int dx = (int) ((currEnvelope.getViewXMax() - currEnvelope.getViewXMin()) / 20.0 + 0.5);
      currEnvelope.setViewXMin(currEnvelope.getViewXMin() - dx);
      currEnvelope.setViewXMax(currEnvelope.getViewXMax() - dx);
      refreshXMinField();
      refreshXMaxField();
      envelopePanel.repaint();
    }
  }

  public void lockEnvelope() {
    if (currEnvelope != null) {
      currEnvelope.setLocked(lockedCBx.isSelected());
      enableControls();
    }
  }
}
