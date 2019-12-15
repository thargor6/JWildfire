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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.motion.MotionCurve;
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
  }

  private final static int PANEL_HUE = 0;
  private final static int PANEL_SATURATION = 1;
  private final static int PANEL_LUMINOSITY = 2;

  private List<GradientCurveEditorPanelDelegate> createEditorPanels() {
    List<GradientCurveEditorPanelDelegate> res = new ArrayList<>();
    res.add(new GradientCurveEditorPanelDelegate(this, data.gradientCurveEditorHueRootPanel, "hue") {

      @Override
      public MotionCurve getCurve(Layer pLayer) {
        return pLayer.getGradientEditorHueCurve();
      }

    });
    res.add(new GradientCurveEditorPanelDelegate(this, data.gradientCurveEditorSaturationRootPanel, "saturation") {

      @Override
      public MotionCurve getCurve(Layer pLayer) {
        return pLayer.getGradientEditorSaturationCurve();
      }

    });
    res.add(new GradientCurveEditorPanelDelegate(this, data.gradientCurveEditorLuminosityRootPanel, "luminosity") {

      @Override
      public MotionCurve getCurve(Layer pLayer) {
        return pLayer.getGradientEditorLuminosityCurve();
      }

    });
    return res;
  }

  public void enableControls() {
    boolean hasFlame = owner.getCurrFlame() != null;
    data.gradientCurveEditorSaveBtn.setEnabled(hasFlame);
  }

  public void refreshValues(boolean pSwitchPanels) {
    boolean oldRefreshing = owner.refreshing;
    owner.refreshing = true;
    try {
      Layer layer = owner.getCurrLayer();
      if(layer!=null) {
        if (pSwitchPanels) {
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
