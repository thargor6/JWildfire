package org.jwildfire.create.tina.swing;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.envelope.Envelope;

public abstract class AbstractControlsDelegate {
  protected final TinaController owner;
  protected final TinaControllerData data;
  protected final JTabbedPane rootTabbedPane;
  protected final boolean useUndoManager;

  public abstract String getEditingTitle(JWFNumberField sender);

  public abstract MotionCurve getCurveToEdit(String pPropName);

  public abstract double getInitialValue(String pPropName);

  public abstract boolean isEnabled();

  public AbstractControlsDelegate(TinaController pOwner, TinaControllerData pData, JTabbedPane pRootTabbedPane, boolean pUseUndoManager) {
    owner = pOwner;
    data = pData;
    rootTabbedPane = pRootTabbedPane;
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

  private void editMotionCurve(String pPropName, String pLabel) {
    MotionCurve curve = getCurveToEdit(pPropName);
    Envelope envelope = curve.toEnvelope();
    if (envelope.getX().length == 0) {
      double initialValue = getInitialValue(pPropName);
      int[] x = new int[] { 0 };
      if (initialValue <= envelope.getViewYMin() + 1) {
        envelope.setViewYMin(initialValue - 1.0);
      }
      if (initialValue >= envelope.getViewYMax() - 1) {
        envelope.setViewYMax(initialValue + 1.0);
      }
      double[] y = new double[] { initialValue };
      envelope.setValues(x, y);
    }

    EnvelopeDialog dlg = new EnvelopeDialog(SwingUtilities.getWindowAncestor(rootTabbedPane), envelope, true);
    dlg.setTitle("Editing " + pLabel);
    dlg.setModal(true);
    dlg.setVisible(true);
    if (dlg.isConfirmed()) {
      Flame flame = owner.getCurrFlame();
      if (useUndoManager) {
        owner.undoManager.saveUndoPoint(flame);
      }
      if (dlg.isRemoved()) {
        curve.setEnabled(false);
      }
      else {
        curve.assignFromEnvelope(envelope);
        curve.setEnabled(true);
      }
      owner.refreshFlameImage(false);
    }
  }

  public void enableControl(JWFNumberField pSender, boolean pDisabled) {
    boolean controlEnabled = false;
    boolean curveBtnEnabled = false;
    boolean spinnerEnabled = false;
    if (!pDisabled && isEnabled()) {
      controlEnabled = true;
      String propName = pSender.getMotionPropertyName();
      if (propName != null && propName.length() > 0) {
        MotionCurve curve = getCurveToEdit(propName);
        curveBtnEnabled = true;
        spinnerEnabled = !curve.isEnabled();
      }
      else {
        curveBtnEnabled = false;
        spinnerEnabled = true;
      }
    }
    pSender.setEnabled(controlEnabled);
    pSender.enableMotionCurveBtn(controlEnabled && curveBtnEnabled);
    pSender.enableSpinnerField(controlEnabled && spinnerEnabled);
    if (pSender.getLinkedMotionControl() != null) {
      pSender.getLinkedMotionControl().setEnabled(controlEnabled && spinnerEnabled);
    }
  }

  public void enableControl(JCheckBox pSender, boolean pDisabled) {
    pSender.setEnabled(!pDisabled && owner.getCurrFlame() != null);
  }

  public void enableControl(JComboBox pSender, boolean pDisabled) {
    pSender.setEnabled(!pDisabled && owner.getCurrFlame() != null);
  }

}
