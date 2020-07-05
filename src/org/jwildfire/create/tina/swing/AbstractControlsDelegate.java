/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2020 Andreas Maschke

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

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.envelope.Envelope;

public abstract class AbstractControlsDelegate {
  protected final TinaController owner;
  protected final TinaControllerData data;
  protected final JPanel rootPanel;
  protected final boolean useUndoManager;

  public abstract String getEditingTitle(JWFNumberField sender);

  public abstract MotionCurve getCurveToEdit(String pPropName);

  public abstract double getInitialValue(String pPropName);

  public abstract boolean isEnabled();

  public AbstractControlsDelegate(TinaController pOwner, TinaControllerData pData, JPanel pRootPanel, boolean pUseUndoManager) {
    owner = pOwner;
    data = pData;
    rootPanel = pRootPanel;
    useUndoManager = pUseUndoManager;
  }

  public void editMotionCurve(ActionEvent e) {
    JWFNumberField sender = ((JWFNumberField.JWFNumberFieldButton) e.getSource()).getOwner();
    editMotionCurve(sender);
  }

  public void editMotionCurve(JWFNumberField sender) {
    String propName = sender.getMotionPropertyName();
    editMotionCurve(propName, getEditingTitle(sender));
    enableControl(sender, false);
  }

  protected void editMotionCurve(MotionCurve pCurve, double pInitialValue, String pPropName, String pLabel) {
    Envelope envelope = pCurve.toEnvelope();
    if (envelope.getX().length == 0) {
      int[] x = new int[] { 0 };
      if (pInitialValue <= envelope.getViewYMin() + 1) {
        envelope.setViewYMin(pInitialValue - 1.0);
      }
      if (pInitialValue >= envelope.getViewYMax() - 1) {
        envelope.setViewYMax(pInitialValue + 1.0);
      }
      double[] y = new double[] { pInitialValue };
      envelope.setValues(x, y);
    }

    EnvelopeDialog dlg = new EnvelopeDialog(SwingUtilities.getWindowAncestor(rootPanel), owner.getErrorHandler(), envelope, true);
    dlg.setFlameToPreview(EnvelopeDialogFlamePreviewType.MOTION_CURVE, owner.getCurrFlame(), pCurve);

    dlg.setTitle("Editing " + pLabel);
    dlg.setModal(true);
    dlg.setVisible(true);
    if (dlg.isConfirmed()) {
      Flame flame = owner.getCurrFlame();
      if (useUndoManager) {
        owner.undoManager.saveUndoPoint(flame);
      }
      if (dlg.isRemoved()) {
        pCurve.setEnabled(false);
      }
      else {
        pCurve.assignFromEnvelope(envelope);
        pCurve.setEnabled(true);
      }
      owner.refreshFlameImage(true, false, 1, true, false);
    }
  }

  protected void editMotionCurve(String pPropName, String pLabel) {
    MotionCurve curve = getCurveToEdit(pPropName);
    double initialValue;
    try {
      initialValue = getInitialValue(pPropName);
    }
    catch (Exception ex) {
      initialValue = 0.0;
    }
    editMotionCurve(curve, initialValue, pPropName, pLabel);
  }

  public void enableControl(JButton pMainButton, JButton pCurveBtn, String pPropertyName, boolean pDisabled) {
    boolean controlEnabled = false;
    boolean curveBtnEnabled = false;
    // boolean mainButtonEnabled = false;
    final boolean mainButtonEnabled = true;
    boolean hasCurve = false;
    if (!pDisabled && isEnabled()) {
      controlEnabled = true;
      if (pPropertyName != null && pPropertyName.length() > 0) {
        MotionCurve curve = getCurveToEdit(pPropertyName);
        curveBtnEnabled = true;
        // mainButtonEnabled = !curve.isEnabled();
        hasCurve = curve.isEnabled();
      }
      else {
        curveBtnEnabled = false;
        // mainButtonEnabled = true;
      }
    }
    pMainButton.setEnabled(controlEnabled && mainButtonEnabled);
    if (hasCurve)
      pCurveBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/curve-money2a.png")));
    else
      pCurveBtn.setIcon(new ImageIcon(getClass().getResource("/org/jwildfire/swing/icons/new/curve-money2.png")));
    pCurveBtn.setEnabled(controlEnabled && curveBtnEnabled);
  }

  public void enableControl(JWFNumberField pSender, String pPropertyName, boolean pDisabled) {
    boolean controlEnabled = false;
    boolean curveBtnEnabled = false;
    //boolean spinnerEnabled = false;
    final boolean spinnerEnabled = true;
    boolean hasCurve = false;
    if (!pDisabled && isEnabled()) {
      controlEnabled = true;
      if (pPropertyName != null && pPropertyName.length() > 0) {
        MotionCurve curve = getCurveToEdit(pPropertyName);
        curveBtnEnabled = true;
        // spinnerEnabled = !curve.isEnabled();
        hasCurve = curve.isEnabled();
      }
      else {
        curveBtnEnabled = false;
        // spinnerEnabled = true;
      }
    }
    pSender.setEnabled(controlEnabled);
    pSender.enableMotionCurveBtn(controlEnabled && curveBtnEnabled);
    // pSender.enableSpinnerField(controlEnabled && spinnerEnabled);
    setupStyle(pSender, hasCurve);
    if (pSender.getLinkedMotionControl() != null) {
      pSender.getLinkedMotionControl().setEnabled(controlEnabled && spinnerEnabled);
    }
  }

  private void setupStyle(JWFNumberField pSender, boolean pHasCurve) {
    pSender.setHasCurve(pHasCurve);
  }

  public void enableControl(JWFNumberField pSender, MotionCurve pCurve, boolean pDisabled) {
    boolean controlEnabled = false;
    boolean curveBtnEnabled = false;
    // boolean spinnerEnabled = false;
    final boolean spinnerEnabled = true;
    boolean hasCurve = false;
    if (!pDisabled && isEnabled()) {
      controlEnabled = true;
      if (pCurve != null) {
        curveBtnEnabled = true;
        // spinnerEnabled = !pCurve.isEnabled();
        hasCurve = pCurve.isEnabled();
      }
      else {
        curveBtnEnabled = false;
        // spinnerEnabled = true;
      }
    }
    pSender.setEnabled(controlEnabled);
    pSender.enableMotionCurveBtn(controlEnabled && curveBtnEnabled);
    // pSender.enableSpinnerField(controlEnabled && spinnerEnabled);
    setupStyle(pSender, hasCurve);
    if (pSender.getLinkedMotionControl() != null) {
      pSender.getLinkedMotionControl().setEnabled(controlEnabled && spinnerEnabled);
    }
  }

  public void enableControl(JWFNumberField pSender, boolean pDisabled) {
    enableControl(pSender, pSender.getMotionPropertyName(), pDisabled);
  }

  public void enableControl(JCheckBox pSender, boolean pDisabled) {
    pSender.setEnabled(!pDisabled && owner.getCurrFlame() != null);
  }

  public void enableControl(JComboBox pSender, boolean pDisabled) {
    pSender.setEnabled(!pDisabled && owner.getCurrFlame() != null);
  }

  public void enableControl(JButton pSender, boolean pDisabled) {
    pSender.setEnabled(!pDisabled && owner.getCurrFlame() != null);
  }

}
