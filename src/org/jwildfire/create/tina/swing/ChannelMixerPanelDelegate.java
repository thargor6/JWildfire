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

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.EnvelopePanel;

public abstract class ChannelMixerPanelDelegate {
  private final ChannelMixerControlsDelegate owner;
  private final JPanel rootPanel;
  private final EnvelopePanel envelopePanel;
  private final EnvelopeDlgController ctrl;

  public ChannelMixerPanelDelegate(ChannelMixerControlsDelegate pOwner, JPanel pRootPanel) {
    owner = pOwner;
    rootPanel = pRootPanel;
    envelopePanel = new EnvelopePanel();
    envelopePanel.setLayout(null);
    envelopePanel.setDrawTicks(false);
    rootPanel.add(envelopePanel, BorderLayout.CENTER);

    ctrl = new EnvelopeDlgController(envelopePanel);
    ctrl.setNoRefresh(true);

    EnvelopeChangeListener changeListener = new EnvelopeChangeListener() {

      @Override
      public void notify(int pSelectedPoint, int pX, double pY) {
        notifyChange(pSelectedPoint, pX, pY);
      }

    };
    ctrl.registerSelectionChangeListener(changeListener);
    ctrl.registerValueChangeListener(changeListener);
    try {
      ctrl.refreshEnvelope();
    }
    finally {
      ctrl.setNoRefresh(false);
    }

  }

  public abstract MotionCurve getCurve(Flame pFlame);

  public void refreshCurve(Flame pFlame) {
    Envelope envelope = getCurve(pFlame).toEnvelope();
    ctrl.setEnvelope(envelope);
    envelopePanel.setEnvelope(envelope);
    ctrl.refreshEnvelope();
  }

  private void notifyChange(int pSelectedPoint, int pX, double pY) {
    Flame flame = owner.getOwner().getCurrFlame();

    if (owner.isUseUndoManager()) {
      owner.getOwner().undoManager.saveUndoPoint(flame);
    }

    MotionCurve curve = getCurve(flame);
    curve.assignFromEnvelope(ctrl.getCurrEnvelope());

    owner.getOwner().refreshFlameImage(false);
  }

}
