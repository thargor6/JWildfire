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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSlider;

import org.jwildfire.base.Prefs;
import org.jwildfire.create.tina.swing.JWFNumberField;
import org.jwildfire.create.tina.swing.TinaController;
import org.jwildfire.swing.ErrorHandler;

public class AnimationController {
  private final TinaController tinaController;
  private final ErrorHandler errorHandler;
  private final Prefs prefs;
  private final JPanel rootPanel;
  private final JWFNumberField keyframesFrameField;
  private final JSlider keyframesFrameSlider;
  private final JWFNumberField keyframesFrameCountField;
  private final List<JWFNumberField> motionPropertyControls = new ArrayList<JWFNumberField>();

  public AnimationController(TinaController pTinaController, ErrorHandler pErrorHandler, Prefs pPrefs, JPanel pRootPanel,
      JWFNumberField pKeyframesFrameField, JSlider pKeyframesFrameSlider, JWFNumberField pKeyframesFrameCountField) {
    tinaController = pTinaController;
    errorHandler = pErrorHandler;
    prefs = pPrefs;
    rootPanel = pRootPanel;
    keyframesFrameField = pKeyframesFrameField;
    keyframesFrameSlider = pKeyframesFrameSlider;
    keyframesFrameCountField = pKeyframesFrameCountField;
    enableControls();
  }

  public int getCurrKeyFrameFrame() {
    Integer value = keyframesFrameField.getIntValue();
    return (value != null ? value.intValue() : -1);
  }

  public void enableControls() {
    // TODO
  }

  private void adjustFrameControls(int frame) {
    if (keyframesFrameField.getMinValue() >= frame) {
      keyframesFrameField.setMinValue(frame);
    }
    if (keyframesFrameField.getMaxValue() <= frame) {
      keyframesFrameField.setMaxValue(frame);
    }
    keyframesFrameField.setValue(frame);

    if (keyframesFrameCountField.getMaxValue() <= frame) {
      keyframesFrameCountField.setMaxValue(frame);
    }
    if (keyframesFrameCountField.getIntValue() == null || keyframesFrameCountField.getIntValue() <= frame) {
      keyframesFrameCountField.setValue(frame);
    }

    if (keyframesFrameSlider.getMinimum() > frame) {
      keyframesFrameSlider.setMinimum(frame);
    }
    if (keyframesFrameSlider.getMaximum() < frame) {
      keyframesFrameSlider.setMaximum(frame);
    }
    keyframesFrameSlider.setValue(frame);
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
    // TODO
  }

  public void registerMotionPropertyControls(JWFNumberField pComponent) {
    if (!motionPropertyControls.contains(pComponent)) {
      motionPropertyControls.add(pComponent);
    }
  }

  public void toggleMotionCurveEditing(boolean pEnabled) {
    for (JWFNumberField component : motionPropertyControls) {
      component.setWithMotionCurve(pEnabled);
    }
  }

}
