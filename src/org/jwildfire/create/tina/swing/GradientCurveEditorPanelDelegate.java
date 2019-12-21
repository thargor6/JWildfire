/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2019 Andreas Maschke

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

import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.envelope.EnvelopePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class GradientCurveEditorPanelDelegate {
  private final GradientCurveEditorControlsDelegate owner;
  private final Container rootPanel;
  private final Container envelopeContainerPanel;
  private final JPanel envelopeParentPanel;
  private final EnvelopePanel envelopePanel;
  private final EnvelopeDlgController ctrl;

  public GradientCurveEditorPanelDelegate(GradientCurveEditorControlsDelegate pOwner, JPanel pEnvelopeParentPanel, String panelCaption) {
    owner = pOwner;
    envelopeParentPanel = pEnvelopeParentPanel;
    envelopeContainerPanel = envelopeParentPanel.getParent();
    rootPanel = envelopeContainerPanel.getParent();
    envelopePanel = new EnvelopePanel();
    envelopePanel.setLayout(null);
    envelopePanel.setDrawTicks(false);
    envelopePanel.setCaption(panelCaption);
    envelopeParentPanel.add(envelopePanel, BorderLayout.CENTER);

    addButtons(envelopeParentPanel);

    ctrl = new EnvelopeDlgController(envelopePanel, owner.getErrorHandler());
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

  private void addButtons(JPanel pPanel) {
    int panelWidth = 42;

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(null);
    buttonPanel.setPreferredSize(new Dimension(panelWidth, 0));

    int xOffSet = 0;
    int yOffset = 0;
    int buttonWidth = 42;
    int buttonHeight = 24;
    int gap = 4;
    {
      JButton editCurveButton = createEditCurveButton("/org/jwildfire/swing/icons/new/curve-money2.png");
      editCurveButton.setBounds(xOffSet, yOffset, buttonWidth, buttonHeight);
      editCurveButton.setToolTipText("Edit this curve in the motion-curve-editor");
      yOffset += gap + buttonHeight;
      editCurveButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          editCurve(e);
        }
      });
      buttonPanel.add(editCurveButton);
    }

    {
      JButton createRampBtn = createEditCurveButton("/org/jwildfire/swing/icons/new/curve-ramp.png");
      createRampBtn.setBounds(xOffSet, yOffset, buttonWidth, buttonHeight);
      createRampBtn.setToolTipText("Turn the current curve into a ramp");
      createRampBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          modifyCurve(e, new CurveModifier() {
            @Override
            public void apply(MotionCurve curve) {
              for(int i=0;i<curve.getX().length;i++) {
                curve.getY()[i]=curve.getX()[i];
              }
            }
          });
        }
      });
      yOffset += gap + buttonHeight;
      buttonPanel.add(createRampBtn);
    }

    {
      JButton createBottomLineBtn = createEditCurveButton("/org/jwildfire/swing/icons/new/curve-bottom-line.png");
      createBottomLineBtn.setBounds(xOffSet, yOffset, buttonWidth, buttonHeight);
      createBottomLineBtn.setToolTipText("Turn the current curve into a line at the bottom");
      createBottomLineBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          modifyCurve(e, new CurveModifier() {
            @Override
            public void apply(MotionCurve curve) {
              for(int i=0;i<curve.getX().length;i++) {
                curve.getY()[i]=0.0;
              }
            }
          });
        }
      });
      yOffset += gap + buttonHeight;
      buttonPanel.add(createBottomLineBtn);
    }

    {
      JButton createMiddleLineBtn = createEditCurveButton("/org/jwildfire/swing/icons/new/curve-middle-line.png");
      createMiddleLineBtn.setBounds(xOffSet, yOffset, buttonWidth, buttonHeight);
      createMiddleLineBtn.setToolTipText("Turn the current curve into a line at the middle");
      createMiddleLineBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          modifyCurve(e, new CurveModifier() {
            @Override
            public void apply(MotionCurve curve) {
              for(int i=0;i<curve.getX().length;i++) {
                curve.getY()[i]=127.5;
              }
            }
          });
        }
      });
      yOffset += gap + buttonHeight;
      buttonPanel.add(createMiddleLineBtn);
    }

    {
      JButton createMirrorXBtn = createEditCurveButton("/org/jwildfire/swing/icons/new/curve-mirror-x.png");
      createMirrorXBtn.setBounds(xOffSet, yOffset, buttonWidth, buttonHeight);
      createMirrorXBtn.setToolTipText("Mirror the current curve at the vertical axis");
      createMirrorXBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          modifyCurve(e, new CurveModifier() {
            @Override
            public void apply(MotionCurve curve) {
              double newY[] = new double[curve.getX().length];
              for(int i=0;i<curve.getX().length;i++) {
                newY[i] = curve.getY()[curve.getX().length - 1 - i];
              }
              curve.setPoints(curve.getX(), newY);
            }
          });
        }
      });
      yOffset += gap + buttonHeight;
      buttonPanel.add(createMirrorXBtn);
    }
    {
      JButton createMirrorYBtn = createEditCurveButton("/org/jwildfire/swing/icons/new/curve-mirror-y.png");
      createMirrorYBtn.setBounds(xOffSet, yOffset, buttonWidth, buttonHeight);
      createMirrorYBtn.setToolTipText("Mirror the current curve at the horizontal axis");
      createMirrorYBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          modifyCurve(e, new CurveModifier() {
            @Override
            public void apply(MotionCurve curve) {
              for(int i=0;i<curve.getX().length;i++) {
                curve.getY()[i]=255.0 - curve.getY()[i];
              }
            }
          });
        }
      });
      yOffset += gap + buttonHeight;
      buttonPanel.add(createMirrorYBtn);
    }

    pPanel.add(buttonPanel, BorderLayout.EAST);
  }

  private JButton createEditCurveButton(String iconName) {
    JButton res = new JButton();
    res.setIcon(new ImageIcon(MainEditorFrame.class.getResource(iconName)));
    res.setMinimumSize(new Dimension(100, 24));
    res.setMaximumSize(new Dimension(32000, 24));
    res.setText("");
    res.setPreferredSize(new Dimension(125, 24));
    res.setFont(new Font("Dialog", Font.BOLD, 10));
    return res;
  }

  protected void editCurve(ActionEvent e) {
    Flame flame = owner.getOwner().getCurrFlame();
    Layer layer = owner.getOwner().getCurrLayer();
    if(flame!=null && layer!=null) {
      MotionCurve curve = getCurve(layer);
      Envelope envelope = curve.toEnvelope();
      EnvelopeDialog dlg = new EnvelopeDialog(SwingUtilities.getWindowAncestor(rootPanel), owner.getErrorHandler(), envelope, false);
      dlg.setFlameToPreview(EnvelopeDialogFlamePreviewType.COLOR_CURVE, flame, curve);

      dlg.setTitle("Editing motion curve");
      dlg.setModal(true);
      dlg.setVisible(true);
      if (dlg.isConfirmed()) {
        if (owner.isUseUndoManager()) {
          owner.getOwner().undoManager.saveUndoPoint(flame);
        }
        curve.assignFromEnvelope(envelope);
        owner.getOwner().getCurrLayer().refreshGradientFromCurves();
        owner.getOwner().refreshPaletteImg();
        owner.getOwner().refreshFlameImage(true, false, 1, true, false);
        refreshCurve(layer);
      }
    }
  }

  private interface CurveModifier {
    void apply(MotionCurve curve);
  }

  protected void modifyCurve(ActionEvent e, CurveModifier modifier) {
    Flame flame = owner.getOwner().getCurrFlame();
    Layer layer = owner.getOwner().getCurrLayer();
    if(flame!=null && layer!=null) {
      MotionCurve curve = getCurve(layer);
      modifier.apply(curve);
      Envelope envelope = curve.toEnvelope();
      curve.assignFromEnvelope(envelope);
      owner.getOwner().getCurrLayer().refreshGradientFromCurves();
      owner.getOwner().refreshPaletteImg();
      owner.getOwner().refreshFlameImage(true, false, 1, true, false);
      refreshCurve(layer);
    }
  }

  public abstract MotionCurve getCurve(Layer pLayer);

  public void refreshCurve(Layer pLayer) {
    Envelope envelope = getCurve(pLayer).toEnvelope();
    ctrl.setEnvelope(envelope);
    envelopePanel.setEnvelope(envelope);
    ctrl.refreshEnvelope();
  }

  private void notifyChange(int pSelectedPoint, int pX, double pY) {
    Layer layer = owner.getOwner().getCurrLayer();
    if(layer!=null) {

      if (owner.isUseUndoManager()) {
        owner.getOwner().undoManager.saveUndoPoint(owner.getOwner().getCurrFlame());
      }

      MotionCurve curve = getCurve(layer);
      curve.assignFromEnvelope(ctrl.getCurrEnvelope());
      owner.getOwner().getCurrLayer().refreshGradientFromCurves();
      owner.getOwner().refreshPaletteImg();
      owner.getOwner().refreshFlameImage(true, true, 1, true, false);
    }
  }

  public void setVisible(boolean pVisible) {
    if (pVisible) {
      if (envelopeContainerPanel.getParent() == null) {
        rootPanel.add(envelopeContainerPanel);
      }
    }
    else {
      if (envelopeContainerPanel.getParent() != null) {
        rootPanel.remove(envelopeContainerPanel);
      }
    }
  }

  public void repaintRoot() {
    rootPanel.getParent().invalidate();
    rootPanel.getParent().validate();
    rootPanel.getParent().repaint();
  }

}
