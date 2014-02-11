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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

import org.jwildfire.base.Tools;
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
      if (propName != null && propName.length() > 0) {
        MotionCurve curve = AnimationService.getPropertyCurve(flame, propName);
        enabled = !curve.isEnabled();
      }
      else {
        enabled = true;
      }
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

    enableFlameControl(data.motionBlurLengthField);
    enableFlameControl(data.motionBlurTimeStepField);
    enableFlameControl(data.motionBlurDecayField);
  }

  private Flame getCurrFlame() {
    return owner.getCurrFlame();
  }

  private boolean isNoRefresh() {
    return owner.isNoRefresh();
  }

  private void setNoRefresh(boolean pNoRefresh) {
    owner.setNoRefresh(pNoRefresh);
  }

  private void flameSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    setNoRefresh(true);
    try {
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Class<?> cls = getCurrFlame().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(getCurrFlame(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(getCurrFlame(), Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      owner.refreshFlameImage(false);
    }
    finally {
      setNoRefresh(false);
    }
  }

  public void cameraYawSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraYawSlider, data.cameraYawREd, "camYaw", 1.0);
  }

  public void cameraPerspectiveSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraPerspectiveSlider, data.cameraPerspectiveREd, "camPerspective", TinaController.SLIDER_SCALE_PERSPECTIVE);
  }

  public void cameraPitchSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraPitchSlider, data.cameraPitchREd, "camPitch", 1.0);
  }

  public void xFormAntialiasAmountSlider_changed() {
    flameSliderChanged(data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", TinaController.SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasRadiusSlider_changed() {
    flameSliderChanged(data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", TinaController.SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasAmountREd_changed() {
    flameTextFieldChanged(data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", TinaController.SLIDER_SCALE_COLOR);
  }

  public void xFormAntialiasRadiusREd_changed() {
    flameTextFieldChanged(data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", TinaController.SLIDER_SCALE_COLOR);
  }

  public void focusZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.focusZSlider, data.focusZREd, "focusZ", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void focusXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.focusXSlider, data.focusXREd, "focusX", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void focusYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.focusYSlider, data.focusYREd, "focusY", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void diminishZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dimishZSlider, data.dimishZREd, "dimishZ", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void camZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.camZSlider, data.camZREd, "camZ", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void cameraDOFSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraDOFSlider, data.cameraDOFREd, "camDOF", TinaController.SLIDER_SCALE_DOF);
  }

  public void cameraDOFAreaSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraDOFAreaSlider, data.cameraDOFAreaREd, "camDOFArea", TinaController.SLIDER_SCALE_DOF_AREA);
  }

  public void cameraDOFExponentSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraDOFExponentSlider, data.cameraDOFExponentREd, "camDOFExponent", TinaController.SLIDER_SCALE_DOF_EXPONENT);
  }

  public void cameraRollSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraRollSlider, data.cameraRollREd, "camRoll", 1.0);
  }

  public void cameraCentreYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraCentreYSlider, data.cameraCentreYREd, "centreY", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void cameraCentreXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraCentreXSlider, data.cameraCentreXREd, "centreX", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void cameraZoomSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraZoomSlider, data.cameraZoomREd, "camZoom", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void brightnessSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.brightnessSlider, data.brightnessREd, "brightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void vibrancySlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.vibrancySlider, data.vibrancyREd, "vibrancy", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void filterRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterMaxRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.deFilterMaxRadiusSlider, data.deFilterMaxRadiusREd, "deFilterMaxRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterMinRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.deFilterMinRadiusSlider, data.deFilterMinRadiusREd, "deFilterMinRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterCurveSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.deFilterCurveSlider, data.deFilterCurveREd, "deFilterCurve", TinaController.SLIDER_SCALE_FILTER_RADIUS);
  }

  public void gammaThresholdSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void vibrancyREd_changed() {
    flameTextFieldChanged(data.vibrancySlider, data.vibrancyREd, "vibrancy", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void pixelsPerUnitSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.pixelsPerUnitSlider, data.pixelsPerUnitREd, "pixelsPerUnit", 1.0);
  }

  private void flameTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale) {
    if (isNoRefresh()) {
      return;
    }
    if (getCurrFlame() == null) {
      return;
    }
    setNoRefresh(true);
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));

      Class<?> cls = getCurrFlame().getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(getCurrFlame(), propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(getCurrFlame(), Tools.FTOI(propValue));
        }
        else {
          throw new IllegalStateException();
        }
      }
      catch (Throwable ex) {
        ex.printStackTrace();
      }
      owner.refreshFlameImage(false);
    }
    finally {
      setNoRefresh(false);
    }
  }

  public void cameraCentreYREd_changed() {
    flameTextFieldChanged(data.cameraCentreYSlider, data.cameraCentreYREd, "centreY", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void cameraCentreXREd_changed() {
    flameTextFieldChanged(data.cameraCentreXSlider, data.cameraCentreXREd, "centreX", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void cameraZoomREd_changed() {
    flameTextFieldChanged(data.cameraZoomSlider, data.cameraZoomREd, "camZoom", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void filterRadiusREd_changed() {
    flameTextFieldChanged(data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterMaxRadiusREd_changed() {
    flameTextFieldChanged(data.deFilterMaxRadiusSlider, data.deFilterMaxRadiusREd, "deFilterMaxRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterMinRadiusREd_changed() {
    flameTextFieldChanged(data.deFilterMinRadiusSlider, data.deFilterMinRadiusREd, "deFilterMinRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS);
  }

  public void deFilterCurveREd_changed() {
    flameTextFieldChanged(data.deFilterCurveSlider, data.deFilterCurveREd, "deFilterCurve", TinaController.SLIDER_SCALE_FILTER_RADIUS);
  }

  public void gammaREd_changed() {
    flameTextFieldChanged(data.gammaSlider, data.gammaREd, "gamma", TinaController.SLIDER_SCALE_GAMMA);
  }

  public void gammaThresholdREd_changed() {
    flameTextFieldChanged(data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void contrastREd_changed() {
    flameTextFieldChanged(data.contrastSlider, data.contrastREd, "contrast", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void gammaSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.gammaSlider, data.gammaREd, "gamma", TinaController.SLIDER_SCALE_GAMMA);
  }

  public void pixelsPerUnitREd_changed() {
    flameTextFieldChanged(data.pixelsPerUnitSlider, data.pixelsPerUnitREd, "pixelsPerUnit", 1.0);
  }

  public void brightnessREd_changed() {
    flameTextFieldChanged(data.brightnessSlider, data.brightnessREd, "brightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void contrastSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.contrastSlider, data.contrastREd, "contrast", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void cameraRollREd_changed() {
    flameTextFieldChanged(data.cameraRollSlider, data.cameraRollREd, "camRoll", 1.0);
  }

  public void cameraPitchREd_changed() {
    flameTextFieldChanged(data.cameraPitchSlider, data.cameraPitchREd, "camPitch", 1.0);
  }

  public void cameraYawREd_changed() {
    flameTextFieldChanged(data.cameraYawSlider, data.cameraYawREd, "camYaw", 1.0);
  }

  public void cameraPerspectiveREd_changed() {
    flameTextFieldChanged(data.cameraPerspectiveSlider, data.cameraPerspectiveREd, "camPerspective", TinaController.SLIDER_SCALE_PERSPECTIVE);
  }

  public void focusXREd_changed() {
    flameTextFieldChanged(data.focusXSlider, data.focusXREd, "focusX", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void focusYREd_changed() {
    flameTextFieldChanged(data.focusYSlider, data.focusYREd, "focusY", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void focusZREd_changed() {
    flameTextFieldChanged(data.focusZSlider, data.focusZREd, "focusZ", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void diminishZREd_changed() {
    flameTextFieldChanged(data.dimishZSlider, data.dimishZREd, "dimishZ", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void cameraDOFREd_changed() {
    flameTextFieldChanged(data.cameraDOFSlider, data.cameraDOFREd, "camDOF", TinaController.SLIDER_SCALE_DOF);
  }

  public void camZREd_changed() {
    flameTextFieldChanged(data.camZSlider, data.camZREd, "camZ", TinaController.SLIDER_SCALE_ZPOS);
  }

  public void cameraDOFAreaREd_changed() {
    flameTextFieldChanged(data.cameraDOFAreaSlider, data.cameraDOFAreaREd, "camDOFArea", TinaController.SLIDER_SCALE_DOF_AREA);
  }

  public void cameraDOFExponentREd_changed() {
    flameTextFieldChanged(data.cameraDOFExponentSlider, data.cameraDOFExponentREd, "camDOFExponent", TinaController.SLIDER_SCALE_DOF_EXPONENT);
  }
}
