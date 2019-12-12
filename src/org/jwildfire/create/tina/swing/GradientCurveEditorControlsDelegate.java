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
import org.jwildfire.create.tina.render.GradientCurveEditorMode;
import org.jwildfire.swing.ErrorHandler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GradientCurveEditorControlsDelegate {
  private final ErrorHandler errorHandler;
  private final TinaController owner;
  private final TinaControllerData data;
  private final JPanel rootPanel;
  private final boolean useUndoManager;
  private List<GradientCurveEditorPanelDelegate> editorPanels;


  public GradientCurveEditorControlsDelegate(TinaController pOwner, ErrorHandler pErrorHandler, TinaControllerData pData, JPanel pRootPanel, boolean pUseUndoManager) {
    owner = pOwner;
    errorHandler = pErrorHandler;
    data = pData;
    rootPanel = pRootPanel;
    useUndoManager = pUseUndoManager;
    editorPanels = createEditorPanels();
    setupPanels();
  }

  private final static int PANEL_HUE = 0;
  private final static int PANEL_SATURATION = 1;
  private final static int PANEL_LUMINOSITY = 2;
  private final static int PANEL_RED = 3;
  private final static int PANEL_GREEN = 4;
  private final static int PANEL_BLUE = 5;

  private List<GradientCurveEditorPanelDelegate> createEditorPanels() {
    List<GradientCurveEditorPanelDelegate> res = new ArrayList<>();
    res.add(new GradientCurveEditorPanelDelegate(this, data.gradientCurveEditorHueRootPanel) {

      @Override
      public MotionCurve getCurve(Layer pLayer) {
        return pLayer.getGradientEditorHueCurve();
      }

    });
    res.add(new GradientCurveEditorPanelDelegate(this, data.gradientCurveEditorSaturationRootPanel) {

      @Override
      public MotionCurve getCurve(Layer pLayer) {
        return pLayer.getGradientEditorSaturationCurve();
      }

    });
    res.add(new GradientCurveEditorPanelDelegate(this, data.gradientCurveEditorLuminosityRootPanel) {

      @Override
      public MotionCurve getCurve(Layer pLayer) {
        return pLayer.getGradientEditorLuminosityCurve();
      }

    });
    res.add(new GradientCurveEditorPanelDelegate(this, data.gradientCurveEditorRedRootPanel) {

      @Override
      public MotionCurve getCurve(Layer pLayer) {
        return pLayer.getGradientEditorRedCurve();
      }

    });
    res.add(new GradientCurveEditorPanelDelegate(this, data.gradientCurveEditorGreenRootPanel) {

      @Override
      public MotionCurve getCurve(Layer pLayer) {
        return pLayer.getGradientEditorGreenCurve();
      }

    });
    res.add(new GradientCurveEditorPanelDelegate(this, data.gradientCurveEditorBlueRootPanel) {

      @Override
      public MotionCurve getCurve(Layer pLayer) {
        return pLayer.getGradientEditorBlueCurve();
      }

    });
    return res;
  }

  public void enableControls() {
    boolean hasFlame = owner.getCurrFlame() != null;
    data.gradientCurveEditorModeCmb.setEnabled(hasFlame);
    data.gradientCurveEditorSaveBtn.setEnabled(hasFlame);
  }

  public void refreshValues(boolean pSwitchPanels) {
    boolean oldRefreshing = owner.refreshing;
    owner.refreshing = true;
    try {
      Layer layer = owner.getCurrLayer();
      if(layer!=null) {
        data.gradientCurveEditorModeCmb.setSelectedItem(layer.getGradientCurveEditorMode());
        if (pSwitchPanels) {
          setupPanels();
          enableControls();
        }
        for (GradientCurveEditorPanelDelegate editorPanel : editorPanels) {
          editorPanel.refreshCurve(layer);
        }
      }
    }
    finally {
      owner.refreshing = oldRefreshing;
    }
  }

  public void gradientCurveEditorModeCmb_changed() {
    if (!owner.refreshing) {
      Layer layer = owner.getCurrLayer();
      if(layer!=null) {
        if (useUndoManager) {
          owner.undoManager.saveUndoPoint(owner.getCurrFlame());
        }
        layer.setGradientCurveEditorMode((GradientCurveEditorMode) data.gradientCurveEditorModeCmb.getSelectedItem());
        refreshValues(true);
        owner.refreshFlameImage(true, false, 1, true, true);
      }
    }
  }

  private void setupPanels() {
    GradientCurveEditorMode mode = (GradientCurveEditorMode) data.gradientCurveEditorModeCmb.getSelectedItem();
    if (mode == null) {
      mode = GradientCurveEditorMode.HSL;
    }
    switch (mode) {
      case RGB:
        editorPanels.get(PANEL_HUE).setVisible(false);
        editorPanels.get(PANEL_SATURATION).setVisible(false);
        editorPanels.get(PANEL_LUMINOSITY).setVisible(false);
        editorPanels.get(PANEL_RED).setVisible(true);
        editorPanels.get(PANEL_GREEN).setVisible(true);
        editorPanels.get(PANEL_BLUE).setVisible(true);
        editorPanels.get(PANEL_RED).repaintRoot();
        break;
      case HSL:
      default:
        editorPanels.get(PANEL_HUE).setVisible(true);
        editorPanels.get(PANEL_SATURATION).setVisible(true);
        editorPanels.get(PANEL_LUMINOSITY).setVisible(true);
        editorPanels.get(PANEL_RED).setVisible(false);
        editorPanels.get(PANEL_GREEN).setVisible(false);
        editorPanels.get(PANEL_BLUE).setVisible(false);
        editorPanels.get(PANEL_HUE).repaintRoot();
        break;
    }
  }

  public void gradientCurveEditorSaveBtn_clicked() {
    // TODO
  }

  protected TinaController getOwner() {
    return owner;
  }

  protected boolean isUseUndoManager() {
    return useUndoManager;
  }

  protected ErrorHandler getErrorHandler() {
    return errorHandler;
  }

}
