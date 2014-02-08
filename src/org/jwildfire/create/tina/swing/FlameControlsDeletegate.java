/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.MotionCurve;
import org.jwildfire.envelope.Envelope;

public class FlameControlsDeletegate {
  private final TinaController owner;
  private final TinaControllerData data;
  private final JTabbedPane rootTabbedPane;

  public FlameControlsDeletegate(TinaController pOwner, TinaControllerData pData, JTabbedPane pRootTabbedPane) {
    owner = pOwner;
    data = pData;
    rootTabbedPane = pRootTabbedPane;
  }

  public void enableFlameControl(JWFNumberField pSender) {
    Flame flame = owner.getCurrFlame();
    boolean enabled;
    if (flame == null) {
      enabled = false;
    }
    else {
      String propName = pSender.getMotionPropertyName();
      MotionCurve curve = AnimationService.getPropertyCurve(flame, propName);
      enabled = !curve.isEnabled();
    }
    pSender.setEnabled(enabled);
    if (pSender.getLinkedMotionControl() != null) {
      pSender.getLinkedMotionControl().setEnabled(enabled);
    }
  }

  public void editFlameMotionCurve(ActionEvent e) {
    JWFNumberField sender = ((JWFNumberField.JWFNumberFieldButton) e.getSource()).getOwner();
    editFlameMotionCurve(sender);
    enableFlameControl(sender);
  }

  public void editFlameMotionCurve(JWFNumberField pSender) {
    String propName = pSender.getMotionPropertyName();
    Flame flame = owner.getCurrFlame();

    MotionCurve curve = AnimationService.getPropertyCurve(flame, propName);
    Envelope envelope = curve.toEnvelope();
    if (envelope.getX().length == 0) {
      double initialValue = AnimationService.getPropertyValue(flame, propName);
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
    dlg.setModal(true);
    dlg.setVisible(true);
    if (dlg.isConfirmed()) {
      owner.undoManager.saveUndoPoint(flame);
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

  public List<JWFNumberField> getMotionControls() {
    List<JWFNumberField> res = new ArrayList<JWFNumberField>();
    res.add(data.cameraRollREd);
    res.add(data.cameraPitchREd);
    res.add(data.cameraYawREd);
    res.add(data.cameraPerspectiveREd);
    res.add(data.cameraCentreXREd);
    res.add(data.cameraCentreYREd);
    res.add(data.cameraZoomREd);
    res.add(data.pixelsPerUnitREd);
    return res;
  }

  public void enableControls() {
    enableFlameControl(data.cameraRollREd);
    enableFlameControl(data.cameraPitchREd);
    enableFlameControl(data.cameraYawREd);
    enableFlameControl(data.cameraPerspectiveREd);
    enableFlameControl(data.cameraCentreXREd);
    enableFlameControl(data.cameraCentreYREd);
    enableFlameControl(data.cameraZoomREd);
    enableFlameControl(data.pixelsPerUnitREd);
  }

}
