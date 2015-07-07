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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.PostSymmetryType;
import org.jwildfire.create.tina.base.Shading;
import org.jwildfire.create.tina.base.ShadingInfo;
import org.jwildfire.create.tina.base.Stereo3dColor;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.Stereo3dPreview;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.render.dof.DOFBlurShapeType;

public class FlameControlsDelegate extends AbstractControlsDelegate {
  private final List<DOFParamControl> dofParamControls;

  private static class DOFParamControl {
    private final JWFNumberField editField;
    private final JSlider slider;
    private final JLabel label;

    public DOFParamControl(JWFNumberField pEditField, JSlider pSlider, JLabel pLabel) {
      editField = pEditField;
      slider = pSlider;
      label = pLabel;
    }

    public void setVisible(boolean pVisible) {
      editField.setVisible(pVisible);
      slider.setVisible(pVisible);
      label.setVisible(pVisible);
    }

    public void setLabel(String pLabel) {
      label.setText(pLabel);
    }

  }

  public FlameControlsDelegate(TinaController pOwner, TinaControllerData pData, JTabbedPane pRootTabbedPane) {
    super(pOwner, pData, pRootTabbedPane, true);
    dofParamControls = initDofParamControls(pData);
  }

  private List<DOFParamControl> initDofParamControls(TinaControllerData pData) {
    List<DOFParamControl> res = new ArrayList<DOFParamControl>();
    res.add(new DOFParamControl(pData.dofDOFParam1REd, pData.dofDOFParam1Slider, pData.dofDOFParam1Lbl));
    res.add(new DOFParamControl(pData.dofDOFParam2REd, pData.dofDOFParam2Slider, pData.dofDOFParam2Lbl));
    res.add(new DOFParamControl(pData.dofDOFParam3REd, pData.dofDOFParam3Slider, pData.dofDOFParam3Lbl));
    res.add(new DOFParamControl(pData.dofDOFParam4REd, pData.dofDOFParam4Slider, pData.dofDOFParam4Lbl));
    res.add(new DOFParamControl(pData.dofDOFParam5REd, pData.dofDOFParam5Slider, pData.dofDOFParam5Lbl));
    res.add(new DOFParamControl(pData.dofDOFParam6REd, pData.dofDOFParam6Slider, pData.dofDOFParam6Lbl));
    return res;
  }

  @Override
  public String getEditingTitle(JWFNumberField sender) {
    return "flame property \"" + sender.getLinkedLabelControl().getText() + "\"";
  }

  @Override
  public MotionCurve getCurveToEdit(String pPropName) {
    return AnimationService.getPropertyCurve(owner.getCurrFlame(), pPropName);
  }

  @Override
  public double getInitialValue(String pPropName) {
    return AnimationService.getPropertyValue(owner.getCurrFlame(), pPropName);
  }

  @Override
  public boolean isEnabled() {
    return owner.getCurrFlame() != null;
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
    res.add(data.camPosXREd);
    res.add(data.camPosYREd);
    res.add(data.camPosZREd);

    res.add(data.brightnessREd);
    res.add(data.contrastREd);
    res.add(data.whiteLevelREd);
    res.add(data.vibrancyREd);
    res.add(data.saturationREd);
    res.add(data.gammaREd);
    res.add(data.gammaThresholdREd);

    res.add(data.cameraDOFREd);
    res.add(data.cameraDOFAreaREd);
    res.add(data.cameraDOFExponentREd);
    res.add(data.camZREd);
    res.add(data.dimishZREd);
    res.add(data.focusXREd);
    res.add(data.focusYREd);
    res.add(data.focusZREd);

    res.add(data.dofDOFScaleREd);
    res.add(data.dofDOFAngleREd);
    res.add(data.dofDOFFadeREd);
    res.add(data.dofDOFParam1REd);
    res.add(data.dofDOFParam2REd);
    res.add(data.dofDOFParam3REd);
    res.add(data.dofDOFParam4REd);
    res.add(data.dofDOFParam5REd);
    res.add(data.dofDOFParam6REd);
    return res;
  }

  public void enableControls() {
    enableControl(data.cameraRollREd, false);
    enableControl(data.cameraPitchREd, false);
    enableControl(data.cameraYawREd, false);
    enableControl(data.cameraPerspectiveREd, false);
    enableControl(data.cameraCentreXREd, false);
    enableControl(data.cameraCentreYREd, false);
    enableControl(data.cameraZoomREd, false);
    enableControl(data.pixelsPerUnitREd, false);
    enableControl(data.camPosXREd, false);
    enableControl(data.camPosYREd, false);
    enableControl(data.camPosZREd, false);

    enableControl(data.bgTransparencyCBx, false);
    enableControl(data.foregroundOpacityField, !data.bgTransparencyCBx.isSelected());

    enableControl(data.motionBlurLengthField, false);
    enableControl(data.motionBlurTimeStepField, false);
    enableControl(data.motionBlurDecayField, false);
    enableControl(data.flameFPSField, false);

    enableControl(data.brightnessREd, false);
    enableControl(data.contrastREd, false);
    enableControl(data.whiteLevelREd, false);
    enableControl(data.vibrancyREd, false);
    enableControl(data.saturationREd, false);
    enableControl(data.gammaREd, false);
    enableControl(data.gammaThresholdREd, false);

    enableControl(data.dofDOFShapeCmb, false);
    setupDOFParamsControls((DOFBlurShapeType) data.dofDOFShapeCmb.getSelectedItem());
    enableControl(data.dofDOFScaleREd, false);
    enableControl(data.dofDOFAngleREd, false);
    enableControl(data.dofDOFFadeREd, false);
    enableControl(data.dofDOFParam1REd, false);
    enableControl(data.dofDOFParam2REd, false);
    enableControl(data.dofDOFParam3REd, false);
    enableControl(data.dofDOFParam4REd, false);
    enableControl(data.dofDOFParam5REd, false);
    enableControl(data.dofDOFParam6REd, false);

    enableStereo3dUI();
    enableDEFilterUI();
    enablePostSymmetryUI();

    enableDOFUI();

    enableShadingUI();
  }

  private void enableStereo3dUI() {
    Stereo3dMode stereo3dMode = (Stereo3dMode) data.stereo3dModeCmb.getSelectedItem();
    enableControl(data.stereo3dModeCmb, false);
    enableControl(data.stereo3dAngleREd, Stereo3dMode.NONE.equals(stereo3dMode));
    enableControl(data.stereo3dEyeDistREd, Stereo3dMode.NONE.equals(stereo3dMode));
    enableControl(data.stereo3dFocalOffsetREd, Stereo3dMode.NONE.equals(stereo3dMode));
    enableControl(data.stereo3dLeftEyeColorCmb, !Stereo3dMode.ANAGLYPH.equals(stereo3dMode));
    enableControl(data.stereo3dRightEyeColorCmb, !Stereo3dMode.ANAGLYPH.equals(stereo3dMode));
    enableControl(data.stereo3dInterpolatedImageCountREd, !Stereo3dMode.INTERPOLATED_IMAGES.equals(stereo3dMode));
    enableControl(data.stereo3dPreviewCmb, Stereo3dMode.NONE.equals(stereo3dMode));
    enableControl(data.stereo3dSwapSidesCBx, Stereo3dMode.NONE.equals(stereo3dMode));
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

  public void saturationSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.saturationSlider, data.saturationREd, "saturation", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void filterRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS);
    owner.refreshFilterKernelPreviewImg();
  }

  public void gammaThresholdSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void vibrancyREd_changed() {
    flameTextFieldChanged(data.vibrancySlider, data.vibrancyREd, "vibrancy", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
  }

  public void saturationREd_changed() {
    flameTextFieldChanged(data.saturationSlider, data.saturationREd, "saturation", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
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
    owner.refreshFilterKernelPreviewImg();
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

  public void whiteLevelREd_changed() {
    flameTextFieldChanged(data.whiteLevelSlider, data.whiteLevelREd, "whiteLevel", 1.0);
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

  public void whiteLevelSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.whiteLevelSlider, data.whiteLevelREd, "whiteLevel", 1.0);
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

  public void enableDOFUI() {
    boolean newDOF = getCurrFlame() != null ? getCurrFlame().isNewCamDOF() : false;
    data.newDOFCBx.setEnabled(getCurrFlame() != null);
    data.mouseTransformEditFocusPointButton.setEnabled(newDOF);
    data.focusXREd.setEnabled(newDOF);
    data.focusXSlider.setEnabled(newDOF);
    data.focusYREd.setEnabled(newDOF);
    data.focusYSlider.setEnabled(newDOF);
    data.focusZREd.setEnabled(newDOF);
    data.focusZSlider.setEnabled(newDOF);
    data.cameraDOFAreaREd.setEnabled(newDOF);
    data.cameraDOFAreaSlider.setEnabled(newDOF);
    data.cameraDOFExponentREd.setEnabled(newDOF);
    data.cameraDOFExponentSlider.setEnabled(newDOF);
    data.camZREd.setEnabled(getCurrFlame() != null);
    data.camZSlider.setEnabled(getCurrFlame() != null);
    data.dimishZREd.setEnabled(getCurrFlame() != null);
    data.dimishZSlider.setEnabled(getCurrFlame() != null);
  }

  public void enablePostSymmetryUI() {
    boolean enabled = getCurrFlame() != null;
    boolean pointSymmEnabled = enabled && PostSymmetryType.POINT.equals(getCurrFlame().getPostSymmetryType());
    boolean axisSymmEnabled = enabled && (PostSymmetryType.X_AXIS.equals(getCurrFlame().getPostSymmetryType()) || PostSymmetryType.Y_AXIS.equals(getCurrFlame().getPostSymmetryType()));
    enableControl(data.postSymmetryTypeCmb, false);
    enableControl(data.postSymmetryDistanceREd, !axisSymmEnabled);
    enableControl(data.postSymmetryRotationREd, !axisSymmEnabled);
    enableControl(data.postSymmetryOrderREd, !pointSymmEnabled);
    enableControl(data.postSymmetryCentreXREd, !axisSymmEnabled && !pointSymmEnabled);
    enableControl(data.postSymmetryCentreYREd, !axisSymmEnabled && !pointSymmEnabled);
  }

  public void enableDEFilterUI() {
    enableControl(data.filterRadiusREd, false);
    enableControl(data.filterKernelCmb, false);
    enableControl(data.tinaSpatialOversamplingREd, false);
    enableControl(data.tinaColorOversamplingREd, !data.tinaSampleJitteringCheckBox.isSelected());
    enableControl(data.tinaSampleJitteringCheckBox, false);
    enableControl(data.tinaPostNoiseFilterCheckBox, false);
    enableControl(data.tinaPostNoiseThresholdField, !data.tinaPostNoiseFilterCheckBox.isSelected());
  }

  public void enableShadingUI() {
    ShadingInfo shadingInfo = getCurrFlame() != null ? getCurrFlame().getShadingInfo() : null;
    boolean pseudo3DEnabled;
    boolean blurEnabled;
    boolean distanceColorEnabled;
    if (shadingInfo != null) {
      data.shadingCmb.setEnabled(true);
      pseudo3DEnabled = shadingInfo.getShading().equals(Shading.PSEUDO3D);
      blurEnabled = shadingInfo.getShading().equals(Shading.BLUR);
      distanceColorEnabled = shadingInfo.getShading().equals(Shading.DISTANCE_COLOR);
    }
    else {
      data.shadingCmb.setEnabled(false);
      pseudo3DEnabled = false;
      blurEnabled = false;
      distanceColorEnabled = false;
    }
    // pseudo3d
    data.shadingAmbientREd.setEnabled(pseudo3DEnabled);
    data.shadingAmbientSlider.setEnabled(pseudo3DEnabled);
    data.shadingDiffuseREd.setEnabled(pseudo3DEnabled);
    data.shadingDiffuseSlider.setEnabled(pseudo3DEnabled);
    data.shadingPhongREd.setEnabled(pseudo3DEnabled);
    data.shadingPhongSlider.setEnabled(pseudo3DEnabled);
    data.shadingPhongSizeREd.setEnabled(pseudo3DEnabled);
    data.shadingPhongSizeSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightCmb.setEnabled(pseudo3DEnabled);
    data.shadingLightXREd.setEnabled(pseudo3DEnabled);
    data.shadingLightXSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightYREd.setEnabled(pseudo3DEnabled);
    data.shadingLightYSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightZREd.setEnabled(pseudo3DEnabled);
    data.shadingLightZSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightRedREd.setEnabled(pseudo3DEnabled);
    data.shadingLightRedSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightGreenREd.setEnabled(pseudo3DEnabled);
    data.shadingLightGreenSlider.setEnabled(pseudo3DEnabled);
    data.shadingLightBlueREd.setEnabled(pseudo3DEnabled);
    data.shadingLightBlueSlider.setEnabled(pseudo3DEnabled);
    // blur
    data.shadingBlurRadiusREd.setEnabled(blurEnabled);
    data.shadingBlurRadiusSlider.setEnabled(blurEnabled);
    data.shadingBlurFadeREd.setEnabled(blurEnabled);
    data.shadingBlurFadeSlider.setEnabled(blurEnabled);
    data.shadingBlurFallOffREd.setEnabled(blurEnabled);
    data.shadingBlurFallOffSlider.setEnabled(blurEnabled);
    // distanceColor
    data.shadingDistanceColorRadiusREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorRadiusSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorScaleREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorScaleSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorExponentREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorExponentSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetXREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetXSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetYREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetYSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetZREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorOffsetZSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorStyleREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorStyleSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorCoordinateREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorCoordinateSlider.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorShiftREd.setEnabled(distanceColorEnabled);
    data.shadingDistanceColorShiftSlider.setEnabled(distanceColorEnabled);
  }

  public void refreshFlameValues() {
    boolean oldNoRefrsh = isNoRefresh();
    setNoRefresh(true);
    try {

      data.camPosXREd.setText(Tools.doubleToString(getCurrFlame().getCamPosX()));
      data.camPosXSlider.setValue(Tools.FTOI(getCurrFlame().getCamPosX() * TinaController.SLIDER_SCALE_CENTRE));

      data.camPosYREd.setText(Tools.doubleToString(getCurrFlame().getCamPosY()));
      data.camPosYSlider.setValue(Tools.FTOI(getCurrFlame().getCamPosY() * TinaController.SLIDER_SCALE_CENTRE));

      data.camPosZREd.setText(Tools.doubleToString(getCurrFlame().getCamPosZ()));
      data.camPosZSlider.setValue(Tools.FTOI(getCurrFlame().getCamPosZ() * TinaController.SLIDER_SCALE_CENTRE));

      data.cameraPerspectiveREd.setText(Tools.doubleToString(getCurrFlame().getCamPerspective()));
      data.cameraPerspectiveSlider.setValue(Tools.FTOI(getCurrFlame().getCamPerspective() * TinaController.SLIDER_SCALE_PERSPECTIVE));

      data.cameraZoomREd.setText(Tools.doubleToString(getCurrFlame().getCamZoom()));
      data.cameraZoomSlider.setValue(Tools.FTOI(getCurrFlame().getCamZoom() * TinaController.SLIDER_SCALE_ZOOM));

      data.cameraDOFREd.setText(Tools.doubleToString(getCurrFlame().getCamDOF()));
      data.cameraDOFSlider.setValue(Tools.FTOI(getCurrFlame().getCamDOF() * TinaController.SLIDER_SCALE_DOF));

      data.cameraDOFAreaREd.setText(Tools.doubleToString(getCurrFlame().getCamDOFArea()));
      data.cameraDOFAreaSlider.setValue(Tools.FTOI(getCurrFlame().getCamDOFArea() * TinaController.SLIDER_SCALE_DOF_AREA));

      data.cameraDOFExponentREd.setText(Tools.doubleToString(getCurrFlame().getCamDOFExponent()));
      data.cameraDOFExponentSlider.setValue(Tools.FTOI(getCurrFlame().getCamDOFExponent() * TinaController.SLIDER_SCALE_DOF_EXPONENT));

      data.camZREd.setText(Tools.doubleToString(getCurrFlame().getCamZ()));
      data.camZSlider.setValue(Tools.FTOI(getCurrFlame().getCamZ() * TinaController.SLIDER_SCALE_ZPOS));

      data.newDOFCBx.setSelected(getCurrFlame().isNewCamDOF());

      data.brightnessREd.setText(Tools.doubleToString(getCurrFlame().getBrightness()));
      data.brightnessSlider.setValue(Tools.FTOI(getCurrFlame().getBrightness() * TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.saturationREd.setText(Tools.doubleToString(getCurrFlame().getSaturation()));
      data.saturationSlider.setValue(Tools.FTOI(getCurrFlame().getSaturation() * TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.contrastREd.setText(Tools.doubleToString(getCurrFlame().getContrast()));
      data.contrastSlider.setValue(Tools.FTOI(getCurrFlame().getContrast() * TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.whiteLevelREd.setText(Tools.doubleToString(getCurrFlame().getWhiteLevel()));
      data.whiteLevelSlider.setValue(Tools.FTOI(getCurrFlame().getWhiteLevel()));

      data.vibrancyREd.setText(Tools.doubleToString(getCurrFlame().getVibrancy()));
      data.vibrancySlider.setValue(Tools.FTOI(getCurrFlame().getVibrancy() * TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY));

      data.gammaREd.setText(Tools.doubleToString(getCurrFlame().getGamma()));
      data.gammaSlider.setValue(Tools.FTOI(getCurrFlame().getGamma() * TinaController.SLIDER_SCALE_GAMMA));

      data.filterRadiusREd.setText(Tools.doubleToString(getCurrFlame().getSpatialFilterRadius()));
      data.filterRadiusSlider.setValue(Tools.FTOI(getCurrFlame().getSpatialFilterRadius() * TinaController.SLIDER_SCALE_FILTER_RADIUS));
      data.filterKernelCmb.setSelectedItem(getCurrFlame().getSpatialFilterKernel());

      data.tinaSpatialOversamplingREd.setText(String.valueOf(getCurrFlame().getSpatialOversampling()));
      data.tinaSpatialOversamplingSlider.setValue(getCurrFlame().getSpatialOversampling());
      data.tinaColorOversamplingREd.setText(String.valueOf(getCurrFlame().getColorOversampling()));
      data.tinaColorOversamplingSlider.setValue(getCurrFlame().getColorOversampling());
      data.tinaSampleJitteringCheckBox.setSelected(getCurrFlame().isSampleJittering());
      data.tinaPostNoiseFilterCheckBox.setSelected(getCurrFlame().isPostNoiseFilter());
      data.tinaPostNoiseThresholdField.setText(String.valueOf(getCurrFlame().getPostNoiseFilterThreshold()));
      data.tinaPostNoiseThresholdSlider.setValue(Tools.FTOI(getCurrFlame().getPostNoiseFilterThreshold() * TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD));
      data.foregroundOpacityField.setText(String.valueOf(getCurrFlame().getForegroundOpacity()));
      data.foregroundOpacitySlider.setValue(Tools.FTOI(getCurrFlame().getForegroundOpacity() * TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD));

      data.gammaThresholdREd.setText(String.valueOf(getCurrFlame().getGammaThreshold()));
      data.gammaThresholdSlider.setValue(Tools.FTOI(getCurrFlame().getGammaThreshold() * TinaController.SLIDER_SCALE_GAMMA_THRESHOLD));

      data.bgTransparencyCBx.setSelected(getCurrFlame().isBGTransparency());

      data.motionBlurLengthField.setText(String.valueOf(getCurrFlame().getMotionBlurLength()));
      data.motionBlurLengthSlider.setValue(getCurrFlame().getMotionBlurLength());
      data.motionBlurTimeStepField.setText(Tools.doubleToString(getCurrFlame().getMotionBlurTimeStep()));
      data.motionBlurTimeStepSlider.setValue(Tools.FTOI(getCurrFlame().getMotionBlurTimeStep() * TinaController.SLIDER_SCALE_COLOR));
      data.motionBlurDecayField.setText(Tools.doubleToString(getCurrFlame().getMotionBlurDecay()));
      data.motionBlurDecaySlider.setValue(Tools.FTOI(getCurrFlame().getMotionBlurDecay() * TinaController.SLIDER_SCALE_ZOOM));
      data.flameFPSField.setValue(getCurrFlame().getFps());

      data.postSymmetryTypeCmb.setSelectedItem(getCurrFlame().getPostSymmetryType());
      enablePostSymmetryUI();
      data.postSymmetryDistanceREd.setText(Tools.doubleToString(getCurrFlame().getPostSymmetryDistance()));
      data.postSymmetryDistanceSlider.setValue(Tools.FTOI(getCurrFlame().getPostSymmetryDistance() * TinaController.SLIDER_SCALE_CENTRE));
      data.postSymmetryRotationREd.setText(Tools.doubleToString(getCurrFlame().getPostSymmetryRotation()));
      data.postSymmetryRotationSlider.setValue(Tools.FTOI(getCurrFlame().getPostSymmetryRotation()));
      data.postSymmetryOrderREd.setText(String.valueOf(getCurrFlame().getPostSymmetryOrder()));
      data.postSymmetryOrderSlider.setValue(Tools.FTOI(getCurrFlame().getPostSymmetryOrder()));
      data.postSymmetryCentreXREd.setText(Tools.doubleToString(getCurrFlame().getPostSymmetryCentreX()));
      data.postSymmetryCentreXSlider.setValue(Tools.FTOI(getCurrFlame().getPostSymmetryCentreX() * TinaController.SLIDER_SCALE_CENTRE));
      data.postSymmetryCentreYREd.setText(Tools.doubleToString(getCurrFlame().getPostSymmetryCentreY()));
      data.postSymmetryCentreYSlider.setValue(Tools.FTOI(getCurrFlame().getPostSymmetryCentreY() * TinaController.SLIDER_SCALE_CENTRE));

      data.stereo3dModeCmb.setSelectedItem(getCurrFlame().getStereo3dMode());
      enableStereo3dUI();
      data.stereo3dAngleREd.setText(Tools.doubleToString(getCurrFlame().getStereo3dAngle()));
      data.stereo3dAngleSlider.setValue(Tools.FTOI(getCurrFlame().getStereo3dAngle() * TinaController.SLIDER_SCALE_GAMMA_THRESHOLD));
      data.stereo3dEyeDistREd.setText(Tools.doubleToString(getCurrFlame().getStereo3dEyeDist()));
      data.stereo3dEyeDistSlider.setValue(Tools.FTOI(getCurrFlame().getStereo3dEyeDist() * TinaController.SLIDER_SCALE_GAMMA_THRESHOLD));
      data.stereo3dFocalOffsetREd.setText(Tools.doubleToString(getCurrFlame().getStereo3dFocalOffset()));
      data.stereo3dFocalOffsetSlider.setValue(Tools.FTOI(getCurrFlame().getStereo3dFocalOffset() * TinaController.SLIDER_SCALE_GAMMA_THRESHOLD));
      data.stereo3dLeftEyeColorCmb.setSelectedItem(getCurrFlame().getStereo3dLeftEyeColor());
      data.stereo3dRightEyeColorCmb.setSelectedItem(getCurrFlame().getStereo3dRightEyeColor());
      data.stereo3dInterpolatedImageCountREd.setText(String.valueOf(getCurrFlame().getStereo3dInterpolatedImageCount()));
      data.stereo3dInterpolatedImageCountSlider.setValue(Tools.FTOI(getCurrFlame().getStereo3dInterpolatedImageCount()));
      data.stereo3dPreviewCmb.setSelectedItem(getCurrFlame().getStereo3dPreview());
      data.stereo3dSwapSidesCBx.setSelected(getCurrFlame().isStereo3dSwapSides());

      setupDOFParamsControls(getCurrFlame().getCamDOFShape());
      data.dofDOFShapeCmb.setSelectedItem(getCurrFlame().getCamDOFShape());
      refreshBokehParams();
    }
    finally {
      setNoRefresh(oldNoRefrsh);
    }
    refreshVisualCamValues();
  }

  private void refreshBokehParams() {
    data.dofDOFScaleREd.setText(Tools.doubleToString(getCurrFlame().getCamDOFScale()));
    data.dofDOFScaleSlider.setValue(Tools.FTOI(getCurrFlame().getCamDOFScale() * TinaController.SLIDER_SCALE_ZOOM));
    data.dofDOFAngleREd.setText(Tools.doubleToString(getCurrFlame().getCamDOFAngle()));
    data.dofDOFAngleSlider.setValue(Tools.FTOI(getCurrFlame().getCamDOFAngle() * TinaController.SLIDER_SCALE_ZOOM));
    data.dofDOFFadeREd.setText(Tools.doubleToString(getCurrFlame().getCamDOFFade()));
    data.dofDOFFadeSlider.setValue(Tools.FTOI(getCurrFlame().getCamDOFFade() * TinaController.SLIDER_SCALE_ZOOM));
    data.dofDOFParam1REd.setText(Tools.doubleToString(getCurrFlame().getCamDOFParam1()));
    data.dofDOFParam1Slider.setValue(Tools.FTOI(getCurrFlame().getCamDOFParam1() * TinaController.SLIDER_SCALE_ZOOM));
    data.dofDOFParam2REd.setText(Tools.doubleToString(getCurrFlame().getCamDOFParam2()));
    data.dofDOFParam2Slider.setValue(Tools.FTOI(getCurrFlame().getCamDOFParam2() * TinaController.SLIDER_SCALE_ZOOM));
    data.dofDOFParam3REd.setText(Tools.doubleToString(getCurrFlame().getCamDOFParam3()));
    data.dofDOFParam3Slider.setValue(Tools.FTOI(getCurrFlame().getCamDOFParam3() * TinaController.SLIDER_SCALE_ZOOM));
    data.dofDOFParam4REd.setText(Tools.doubleToString(getCurrFlame().getCamDOFParam4()));
    data.dofDOFParam4Slider.setValue(Tools.FTOI(getCurrFlame().getCamDOFParam4() * TinaController.SLIDER_SCALE_ZOOM));
    data.dofDOFParam5REd.setText(Tools.doubleToString(getCurrFlame().getCamDOFParam5()));
    data.dofDOFParam5Slider.setValue(Tools.FTOI(getCurrFlame().getCamDOFParam5() * TinaController.SLIDER_SCALE_ZOOM));
    data.dofDOFParam6REd.setText(Tools.doubleToString(getCurrFlame().getCamDOFParam6()));
    data.dofDOFParam6Slider.setValue(Tools.FTOI(getCurrFlame().getCamDOFParam6() * TinaController.SLIDER_SCALE_ZOOM));
  }

  private void setupDOFParamsControls(DOFBlurShapeType pShapeType) {
    List<String> paramNames = pShapeType.getDOFBlurShape().getParamNames();
    for (int i = 0; i < dofParamControls.size(); i++) {
      DOFParamControl ctrl = dofParamControls.get(i);
      if (i < paramNames.size()) {
        String name = paramNames.get(i);
        name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        ctrl.setLabel(name);
        ctrl.setVisible(true);
      }
      else {
        ctrl.setVisible(false);
      }
    }
  }

  public void refreshVisualCamValues() {
    boolean oldNoRefrsh = isNoRefresh();
    setNoRefresh(true);
    try {
      data.cameraRollREd.setText(Tools.doubleToString(getCurrFlame().getCamRoll()));
      data.cameraRollSlider.setValue(Tools.FTOI(getCurrFlame().getCamRoll()));

      data.cameraPitchREd.setText(Tools.doubleToString(getCurrFlame().getCamPitch()));
      data.cameraPitchSlider.setValue(Tools.FTOI(getCurrFlame().getCamPitch()));

      data.cameraYawREd.setText(Tools.doubleToString(getCurrFlame().getCamYaw()));
      data.cameraYawSlider.setValue(Tools.FTOI(getCurrFlame().getCamYaw()));

      data.cameraCentreXREd.setText(Tools.doubleToString(getCurrFlame().getCentreX()));
      data.cameraCentreXSlider.setValue(Tools.FTOI(getCurrFlame().getCentreX() * TinaController.SLIDER_SCALE_CENTRE));

      data.cameraCentreYREd.setText(Tools.doubleToString(getCurrFlame().getCentreY()));
      data.cameraCentreYSlider.setValue(Tools.FTOI(getCurrFlame().getCentreY() * TinaController.SLIDER_SCALE_CENTRE));

      data.pixelsPerUnitREd.setText(Tools.doubleToString(getCurrFlame().getPixelsPerUnit()));
      data.pixelsPerUnitSlider.setValue(Tools.FTOI(getCurrFlame().getPixelsPerUnit()));

      data.focusXREd.setText(Tools.doubleToString(getCurrFlame().getFocusX()));
      data.focusXSlider.setValue(Tools.FTOI(getCurrFlame().getFocusX() * TinaController.SLIDER_SCALE_ZPOS));

      data.focusYREd.setText(Tools.doubleToString(getCurrFlame().getFocusY()));
      data.focusYSlider.setValue(Tools.FTOI(getCurrFlame().getFocusY() * TinaController.SLIDER_SCALE_ZPOS));

      data.focusZREd.setText(Tools.doubleToString(getCurrFlame().getFocusZ()));
      data.focusZSlider.setValue(Tools.FTOI(getCurrFlame().getFocusZ() * TinaController.SLIDER_SCALE_ZPOS));

      data.dimishZREd.setText(Tools.doubleToString(getCurrFlame().getDimishZ()));
      data.dimishZSlider.setValue(Tools.FTOI(getCurrFlame().getDimishZ() * TinaController.SLIDER_SCALE_ZPOS));
    }
    finally {
      setNoRefresh(oldNoRefrsh);
    }
  }

  public void refreshShadingUI() {
    ShadingInfo shadingInfo = getCurrFlame() != null ? getCurrFlame().getShadingInfo() : null;
    boolean pseudo3DEnabled;
    boolean blurEnabled;
    boolean distanceColorEnabled;
    if (shadingInfo != null) {
      data.shadingCmb.setSelectedItem(shadingInfo.getShading());
      pseudo3DEnabled = shadingInfo.getShading().equals(Shading.PSEUDO3D);
      blurEnabled = shadingInfo.getShading().equals(Shading.BLUR);
      distanceColorEnabled = shadingInfo.getShading().equals(Shading.DISTANCE_COLOR);
    }
    else {
      data.shadingCmb.setSelectedIndex(0);
      pseudo3DEnabled = false;
      blurEnabled = false;
      distanceColorEnabled = false;
    }
    if (pseudo3DEnabled) {
      data.shadingAmbientREd.setText(Tools.doubleToString(shadingInfo.getAmbient()));
      data.shadingAmbientSlider.setValue(Tools.FTOI(shadingInfo.getAmbient() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingDiffuseREd.setText(Tools.doubleToString(shadingInfo.getDiffuse()));
      data.shadingDiffuseSlider.setValue(Tools.FTOI(shadingInfo.getDiffuse() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingPhongREd.setText(Tools.doubleToString(shadingInfo.getPhong()));
      data.shadingPhongSlider.setValue(Tools.FTOI(shadingInfo.getPhong() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingPhongSizeREd.setText(Tools.doubleToString(shadingInfo.getPhongSize()));
      data.shadingPhongSizeSlider.setValue(Tools.FTOI(shadingInfo.getPhongSize() * TinaController.SLIDER_SCALE_PHONGSIZE));
      int cIdx = data.shadingLightCmb.getSelectedIndex();
      data.shadingLightXREd.setText(Tools.doubleToString(shadingInfo.getLightPosX()[cIdx]));
      data.shadingLightXSlider.setValue(Tools.FTOI(shadingInfo.getLightPosX()[cIdx] * TinaController.SLIDER_SCALE_LIGHTPOS));
      data.shadingLightYREd.setText(Tools.doubleToString(shadingInfo.getLightPosY()[cIdx]));
      data.shadingLightYSlider.setValue(Tools.FTOI(shadingInfo.getLightPosY()[cIdx] * TinaController.SLIDER_SCALE_LIGHTPOS));
      data.shadingLightZREd.setText(Tools.doubleToString(shadingInfo.getLightPosZ()[cIdx]));
      data.shadingLightZSlider.setValue(Tools.FTOI(shadingInfo.getLightPosZ()[cIdx] * TinaController.SLIDER_SCALE_LIGHTPOS));
      data.shadingLightRedREd.setText(String.valueOf(shadingInfo.getLightRed()[cIdx]));
      data.shadingLightRedSlider.setValue(shadingInfo.getLightRed()[cIdx]);
      data.shadingLightGreenREd.setText(String.valueOf(shadingInfo.getLightGreen()[cIdx]));
      data.shadingLightGreenSlider.setValue(shadingInfo.getLightGreen()[cIdx]);
      data.shadingLightBlueREd.setText(String.valueOf(shadingInfo.getLightBlue()[cIdx]));
      data.shadingLightBlueSlider.setValue(shadingInfo.getLightBlue()[cIdx]);
    }
    else {
      data.shadingAmbientREd.setText("");
      data.shadingAmbientSlider.setValue(0);
      data.shadingDiffuseREd.setText("");
      data.shadingDiffuseSlider.setValue(0);
      data.shadingPhongREd.setText("");
      data.shadingPhongSlider.setValue(0);
      data.shadingPhongSizeREd.setText("");
      data.shadingPhongSizeSlider.setValue(0);
      data.shadingLightXREd.setText("");
      data.shadingLightXSlider.setValue(0);
      data.shadingLightYREd.setText("");
      data.shadingLightYSlider.setValue(0);
      data.shadingLightZREd.setText("");
      data.shadingLightZSlider.setValue(0);
      data.shadingLightRedREd.setText("");
      data.shadingLightRedSlider.setValue(0);
      data.shadingLightGreenREd.setText("");
      data.shadingLightGreenSlider.setValue(0);
      data.shadingLightBlueREd.setText("");
      data.shadingLightBlueSlider.setValue(0);
    }
    if (blurEnabled) {
      data.shadingBlurRadiusREd.setText(Tools.doubleToString(shadingInfo.getBlurRadius()));
      data.shadingBlurRadiusSlider.setValue(shadingInfo.getBlurRadius());
      data.shadingBlurFadeREd.setText(Tools.doubleToString(shadingInfo.getBlurFade()));
      data.shadingBlurFadeSlider.setValue(Tools.FTOI(shadingInfo.getBlurFade() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingBlurFallOffREd.setText(Tools.doubleToString(shadingInfo.getBlurFallOff()));
      data.shadingBlurFallOffSlider.setValue(Tools.FTOI(shadingInfo.getBlurFallOff() * TinaController.SLIDER_SCALE_BLUR_FALLOFF));
    }
    else {
      data.shadingBlurRadiusREd.setText("");
      data.shadingBlurRadiusSlider.setValue(0);
      data.shadingBlurFadeREd.setText("");
      data.shadingBlurFadeSlider.setValue(0);
      data.shadingBlurFallOffREd.setText("");
      data.shadingBlurFallOffSlider.setValue(0);
    }
    if (distanceColorEnabled) {
      data.shadingDistanceColorRadiusREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorRadius()));
      data.shadingDistanceColorRadiusSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorRadius() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorExponentREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorExponent()));
      data.shadingDistanceColorExponentSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorExponent() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorScaleREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorScale()));
      data.shadingDistanceColorScaleSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorScale() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorOffsetXREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorOffsetX()));
      data.shadingDistanceColorOffsetXSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorOffsetX() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorOffsetYREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorOffsetY()));
      data.shadingDistanceColorOffsetYSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorOffsetY() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorOffsetZREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorOffsetZ()));
      data.shadingDistanceColorOffsetZSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorOffsetZ() * TinaController.SLIDER_SCALE_AMBIENT));
      data.shadingDistanceColorStyleREd.setText(String.valueOf(shadingInfo.getDistanceColorStyle()));
      data.shadingDistanceColorStyleSlider.setValue(shadingInfo.getDistanceColorStyle());
      data.shadingDistanceColorCoordinateREd.setText(String.valueOf(shadingInfo.getDistanceColorCoordinate()));
      data.shadingDistanceColorCoordinateSlider.setValue(shadingInfo.getDistanceColorCoordinate());
      data.shadingDistanceColorShiftREd.setText(Tools.doubleToString(shadingInfo.getDistanceColorShift()));
      data.shadingDistanceColorShiftSlider.setValue(Tools.FTOI(shadingInfo.getDistanceColorShift() * TinaController.SLIDER_SCALE_AMBIENT));
    }
    else {
      data.shadingDistanceColorRadiusREd.setText("");
      data.shadingDistanceColorRadiusSlider.setValue(0);
      data.shadingDistanceColorExponentREd.setText("");
      data.shadingDistanceColorExponentSlider.setValue(0);
      data.shadingDistanceColorScaleREd.setText("");
      data.shadingDistanceColorScaleSlider.setValue(0);
      data.shadingDistanceColorOffsetXREd.setText("");
      data.shadingDistanceColorOffsetXSlider.setValue(0);
      data.shadingDistanceColorOffsetYREd.setText("");
      data.shadingDistanceColorOffsetYSlider.setValue(0);
      data.shadingDistanceColorOffsetZREd.setText("");
      data.shadingDistanceColorOffsetZSlider.setValue(0);
      data.shadingDistanceColorStyleREd.setText("");
      data.shadingDistanceColorStyleSlider.setValue(0);
      data.shadingDistanceColorCoordinateREd.setText("");
      data.shadingDistanceColorCoordinateSlider.setValue(0);
      data.shadingDistanceColorShiftREd.setText("");
      data.shadingDistanceColorShiftSlider.setValue(0);
    }
  }

  public void shadingLightXSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightXSlider, data.shadingLightXREd, "lightPosX", TinaController.SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightYSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightYSlider, data.shadingLightYREd, "lightPosY", TinaController.SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightZSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightZSlider, data.shadingLightZREd, "lightPosZ", TinaController.SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  private void shadingInfoSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, int pIdx) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    ShadingInfo shadingInfo = getCurrFlame().getShadingInfo();
    setNoRefresh(true);
    try {
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Class<?> cls = shadingInfo.getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(shadingInfo, propValue);
        }
        else if (fieldCls == double[].class) {
          double[] arr = (double[]) field.get(shadingInfo);
          Array.set(arr, pIdx, propValue);
        }
        else if (fieldCls == Double[].class) {
          Double[] arr = (Double[]) field.get(shadingInfo);
          Array.set(arr, pIdx, propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(shadingInfo, Tools.FTOI(propValue));
        }
        else if (fieldCls == int[].class) {
          int[] arr = (int[]) field.get(shadingInfo);
          Array.set(arr, pIdx, Tools.FTOI(propValue));
        }
        else if (fieldCls == Integer[].class) {
          Integer[] arr = (Integer[]) field.get(shadingInfo);
          Array.set(arr, pIdx, Tools.FTOI(propValue));
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

  public void shadingLightRedSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightRedSlider, data.shadingLightRedREd, "lightRed", 1.0, cIdx);
  }

  public void shadingLightCmb_changed() {
    if (isNoRefresh()) {
      return;
    }
    setNoRefresh(true);
    try {
      refreshShadingUI();
    }
    finally {
      setNoRefresh(false);
    }
  }

  public void shadingPhongSizeSlider_changed() {
    shadingInfoSliderChanged(data.shadingPhongSizeSlider, data.shadingPhongSizeREd, "phongSize", TinaController.SLIDER_SCALE_PHONGSIZE, 0);
  }

  public void shadingLightBlueSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightBlueSlider, data.shadingLightBlueREd, "lightBlue", 1.0, cIdx);
  }

  public void shadingLightGreenSlider_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoSliderChanged(data.shadingLightGreenSlider, data.shadingLightGreenREd, "lightGreen", 1.0, cIdx);
  }

  public void shadingAmbientSlider_changed() {
    shadingInfoSliderChanged(data.shadingAmbientSlider, data.shadingAmbientREd, "ambient", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDiffuseSlider_changed() {
    shadingInfoSliderChanged(data.shadingDiffuseSlider, data.shadingDiffuseREd, "diffuse", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingPhongSlider_changed() {
    shadingInfoSliderChanged(data.shadingPhongSlider, data.shadingPhongREd, "phong", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  private void shadingInfoTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, int pIdx) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    ShadingInfo shadingInfo = getCurrFlame().getShadingInfo();
    setNoRefresh(true);
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));

      Class<?> cls = shadingInfo.getClass();
      Field field;
      try {
        field = cls.getDeclaredField(pProperty);
        field.setAccessible(true);
        Class<?> fieldCls = field.getType();
        if (fieldCls == double.class || fieldCls == Double.class) {
          field.setDouble(shadingInfo, propValue);
        }
        else if (fieldCls == double[].class) {
          double[] arr = (double[]) field.get(shadingInfo);
          Array.set(arr, pIdx, propValue);
        }
        else if (fieldCls == Double[].class) {
          Double[] arr = (Double[]) field.get(shadingInfo);
          Array.set(arr, pIdx, propValue);
        }
        else if (fieldCls == int.class || fieldCls == Integer.class) {
          field.setInt(shadingInfo, Tools.FTOI(propValue));
        }
        else if (fieldCls == int[].class) {
          int[] arr = (int[]) field.get(shadingInfo);
          Array.set(arr, pIdx, Tools.FTOI(propValue));
        }
        else if (fieldCls == Integer[].class) {
          Integer[] arr = (Integer[]) field.get(shadingInfo);
          Array.set(arr, pIdx, Tools.FTOI(propValue));
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

  public void shadingAmbientREd_changed() {
    shadingInfoTextFieldChanged(data.shadingAmbientSlider, data.shadingAmbientREd, "ambient", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDiffuseREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDiffuseSlider, data.shadingDiffuseREd, "diffuse", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingPhongREd_changed() {
    shadingInfoTextFieldChanged(data.shadingPhongSlider, data.shadingPhongREd, "phong", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingPhongSizeREd_changed() {
    shadingInfoTextFieldChanged(data.shadingPhongSizeSlider, data.shadingPhongSizeREd, "phongSize", TinaController.SLIDER_SCALE_PHONGSIZE, 0);
  }

  public void shadingLightXREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightXSlider, data.shadingLightXREd, "lightPosX", TinaController.SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightYREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightYSlider, data.shadingLightYREd, "lightPosY", TinaController.SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightZREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightZSlider, data.shadingLightZREd, "lightPosZ", TinaController.SLIDER_SCALE_LIGHTPOS, cIdx);
  }

  public void shadingLightRedREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightRedSlider, data.shadingLightRedREd, "lightRed", 1.0, cIdx);
  }

  public void shadingLightGreenREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightGreenSlider, data.shadingLightGreenREd, "lightGreen", 1.0, cIdx);
  }

  public void shadingLightBlueREd_changed() {
    int cIdx = data.shadingLightCmb.getSelectedIndex();
    shadingInfoTextFieldChanged(data.shadingLightBlueSlider, data.shadingLightBlueREd, "lightBlue", 1.0, cIdx);
  }

  public void shadingBlurFadeREd_changed() {
    shadingInfoTextFieldChanged(data.shadingBlurFadeSlider, data.shadingBlurFadeREd, "blurFade", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingBlurFallOffREd_changed() {
    shadingInfoTextFieldChanged(data.shadingBlurFallOffSlider, data.shadingBlurFallOffREd, "blurFallOff", TinaController.SLIDER_SCALE_BLUR_FALLOFF, 0);
  }

  public void shadingBlurRadiusREd_changed() {
    shadingInfoTextFieldChanged(data.shadingBlurRadiusSlider, data.shadingBlurRadiusREd, "blurRadius", 1.0, 0);
  }

  public void shadingBlurFallOffSlider_changed() {
    shadingInfoSliderChanged(data.shadingBlurFallOffSlider, data.shadingBlurFallOffREd, "blurFallOff", TinaController.SLIDER_SCALE_BLUR_FALLOFF, 0);
  }

  public void shadingBlurFadeSlider_changed() {
    shadingInfoSliderChanged(data.shadingBlurFadeSlider, data.shadingBlurFadeREd, "blurFade", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingBlurRadiusSlider_changed() {
    shadingInfoSliderChanged(data.shadingBlurRadiusSlider, data.shadingBlurRadiusREd, "blurRadius", 1.0, 0);
  }

  public void shadingDistanceColorRadiusREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorRadiusSlider, data.shadingDistanceColorRadiusREd, "distanceColorRadius", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorRadiusSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorRadiusSlider, data.shadingDistanceColorRadiusREd, "distanceColorRadius", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorStyleREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorStyleSlider, data.shadingDistanceColorStyleREd, "distanceColorStyle", 1.0, 0);
  }

  public void shadingDistanceColorStyleSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorStyleSlider, data.shadingDistanceColorStyleREd, "distanceColorStyle", 1.0, 0);
  }

  public void shadingDistanceColorCoordinateREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorCoordinateSlider, data.shadingDistanceColorCoordinateREd, "distanceColorCoordinate", 1.0, 0);
  }

  public void shadingDistanceColorCoordinateSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorCoordinateSlider, data.shadingDistanceColorCoordinateREd, "distanceColorCoordinate", 1.0, 0);
  }

  public void shadingDistanceColorShiftREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorShiftSlider, data.shadingDistanceColorShiftREd, "distanceColorShift", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorShiftSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorShiftSlider, data.shadingDistanceColorShiftREd, "distanceColorShift", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorExponentREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorExponentSlider, data.shadingDistanceColorExponentREd, "distanceColorExponent", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorExponentSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorExponentSlider, data.shadingDistanceColorExponentREd, "distanceColorExponent", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorScaleREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorScaleSlider, data.shadingDistanceColorScaleREd, "distanceColorScale", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorScaleSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorScaleSlider, data.shadingDistanceColorScaleREd, "distanceColorScale", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetXREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorOffsetXSlider, data.shadingDistanceColorOffsetXREd, "distanceColorOffsetX", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetXSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorOffsetXSlider, data.shadingDistanceColorOffsetXREd, "distanceColorOffsetX", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetYREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorOffsetYSlider, data.shadingDistanceColorOffsetYREd, "distanceColorOffsetY", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetYSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorOffsetYSlider, data.shadingDistanceColorOffsetYREd, "distanceColorOffsetY", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetZREd_changed() {
    shadingInfoTextFieldChanged(data.shadingDistanceColorOffsetZSlider, data.shadingDistanceColorOffsetZREd, "distanceColorOffsetZ", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void shadingDistanceColorOffsetZSlider_changed() {
    shadingInfoSliderChanged(data.shadingDistanceColorOffsetZSlider, data.shadingDistanceColorOffsetZREd, "distanceColorOffsetZ", TinaController.SLIDER_SCALE_AMBIENT, 0);
  }

  public void motionBlurLengthREd_changed() {
    flameTextFieldChanged(data.motionBlurLengthSlider, data.motionBlurLengthField, "motionBlurLength", 1.0);
  }

  public void flameFPSField_changed() {
    if (isNoRefresh() || getCurrFlame() == null) {
      return;
    }
    getCurrFlame().setFps(data.flameFPSField.getIntValue());
  }

  public void motionBlurLengthSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.motionBlurLengthSlider, data.motionBlurLengthField, "motionBlurLength", 1.0);
  }

  public void motionBlurTimeStepREd_changed() {
    flameTextFieldChanged(data.motionBlurTimeStepSlider, data.motionBlurTimeStepField, "motionBlurTimeStep", TinaController.SLIDER_SCALE_COLOR);
  }

  public void motionBlurTimeStepSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.motionBlurTimeStepSlider, data.motionBlurTimeStepField, "motionBlurTimeStep", TinaController.SLIDER_SCALE_COLOR);
  }

  public void motionBlurDecayREd_changed() {
    flameTextFieldChanged(data.motionBlurDecaySlider, data.motionBlurDecayField, "motionBlurDecay", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void motionBlurDecaySlider_changed(ChangeEvent e) {
    flameSliderChanged(data.motionBlurDecaySlider, data.motionBlurDecayField, "motionBlurDecay", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void postSymmetryCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setPostSymmetryType((PostSymmetryType) data.postSymmetryTypeCmb.getSelectedItem());
        enablePostSymmetryUI();
        owner.refreshFlameImage(false);
      }
    }
  }

  public void postSymmetryDistanceREd_changed() {
    flameTextFieldChanged(data.postSymmetryDistanceSlider, data.postSymmetryDistanceREd, "postSymmetryDistance", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void postSymmetryDistanceSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryDistanceSlider, data.postSymmetryDistanceREd, "postSymmetryDistance", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void postSymmetryRotationREd_changed() {
    flameTextFieldChanged(data.postSymmetryRotationSlider, data.postSymmetryRotationREd, "postSymmetryRotation", 1.0);
  }

  public void postSymmetryRotationSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryRotationSlider, data.postSymmetryRotationREd, "postSymmetryRotation", 1.0);
  }

  public void postSymmetryCentreXREd_changed() {
    flameTextFieldChanged(data.postSymmetryCentreXSlider, data.postSymmetryCentreXREd, "postSymmetryCentreX", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void postSymmetryCentreXSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryCentreXSlider, data.postSymmetryCentreXREd, "postSymmetryCentreX", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void postSymmetryCentreYREd_changed() {
    flameTextFieldChanged(data.postSymmetryCentreYSlider, data.postSymmetryCentreYREd, "postSymmetryCentreY", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void postSymmetryCentreYSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryCentreYSlider, data.postSymmetryCentreYREd, "postSymmetryCentreY", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void postSymmetryOrderREd_changed() {
    flameTextFieldChanged(data.postSymmetryOrderSlider, data.postSymmetryOrderREd, "postSymmetryOrder", 1.0);
  }

  public void postSymmetryOrderSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryOrderSlider, data.postSymmetryOrderREd, "postSymmetryOrder", 1.0);
  }

  public void stereo3dModeCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setStereo3dMode((Stereo3dMode) data.stereo3dModeCmb.getSelectedItem());
        enableStereo3dUI();
        owner.refreshFlameImage(false);
      }
    }
  }

  public void stereo3dLeftEyeColorCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setStereo3dLeftEyeColor((Stereo3dColor) data.stereo3dLeftEyeColorCmb.getSelectedItem());
        owner.refreshFlameImage(false);
      }
    }
  }

  public void stereo3dRightEyeColorCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setStereo3dRightEyeColor((Stereo3dColor) data.stereo3dRightEyeColorCmb.getSelectedItem());
        owner.refreshFlameImage(false);
      }
    }
  }

  public void stereo3dAngleREd_changed() {
    flameTextFieldChanged(data.stereo3dAngleSlider, data.stereo3dAngleREd, "stereo3dAngle", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void stereo3dAngleSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.stereo3dAngleSlider, data.stereo3dAngleREd, "stereo3dAngle", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void stereo3dEyeDistREd_changed() {
    flameTextFieldChanged(data.stereo3dEyeDistSlider, data.stereo3dEyeDistREd, "stereo3dEyeDist", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void stereo3dEyeDistSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.stereo3dEyeDistSlider, data.stereo3dEyeDistREd, "stereo3dEyeDist", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void stereo3dFocalOffsetREd_changed() {
    flameTextFieldChanged(data.stereo3dFocalOffsetSlider, data.stereo3dFocalOffsetREd, "stereo3dFocalOffset", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void stereo3dFocalOffsetSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.stereo3dFocalOffsetSlider, data.stereo3dFocalOffsetREd, "stereo3dFocalOffset", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
  }

  public void stereo3dInterpolatedImageCountREd_changed() {
    flameTextFieldChanged(data.stereo3dInterpolatedImageCountSlider, data.stereo3dInterpolatedImageCountREd, "stereo3dInterpolatedImageCount", 1.0);
  }

  public void stereo3dInterpolatedImageCountSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.stereo3dInterpolatedImageCountSlider, data.stereo3dInterpolatedImageCountREd, "stereo3dInterpolatedImageCount", 1.0);
  }

  public void stereo3dPreviewCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setStereo3dPreview((Stereo3dPreview) data.stereo3dPreviewCmb.getSelectedItem());
        enableStereo3dUI();
        owner.refreshFlameImage(false);
      }
    }
  }

  public void stereo3dSwapSidesCBx_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      boolean swap = data.stereo3dSwapSidesCBx.isSelected();
      if (swap != flame.isStereo3dSwapSides()) {
        owner.saveUndoPoint();
        flame.setStereo3dSwapSides(swap);
        owner.refreshFlameImage(false);
      }
    }
  }

  public void camPosXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.camPosXSlider, data.camPosXREd, "camPosX", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void camPosYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.camPosYSlider, data.camPosYREd, "camPosY", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void camPosZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.camPosZSlider, data.camPosZREd, "camPosZ", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void camPosXREd_changed() {
    flameTextFieldChanged(data.camPosXSlider, data.camPosXREd, "camPosX", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void camPosYREd_changed() {
    flameTextFieldChanged(data.camPosYSlider, data.camPosYREd, "camPosY", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void camPosZREd_changed() {
    flameTextFieldChanged(data.camPosZSlider, data.camPosZREd, "camPosZ", TinaController.SLIDER_SCALE_CENTRE);
  }

  public void dofDOFShapeCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        DOFBlurShapeType shape = (DOFBlurShapeType) data.dofDOFShapeCmb.getSelectedItem();
        flame.setCamDOFShape(shape);
        shape.getDOFBlurShape().setDefaultParams(getCurrFlame());
        setupDOFParamsControls(getCurrFlame().getCamDOFShape());
        refreshBokehParams();
        owner.refreshFlameImage(false);
      }
    }
  }

  public void dofDOFScaleSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFScaleSlider, data.dofDOFScaleREd, "camDOFScale", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFScaleREd_changed() {
    flameTextFieldChanged(data.dofDOFScaleSlider, data.dofDOFScaleREd, "camDOFScale", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFAngleSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFAngleSlider, data.dofDOFAngleREd, "camDOFAngle", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFAngleREd_changed() {
    flameTextFieldChanged(data.dofDOFAngleSlider, data.dofDOFAngleREd, "camDOFAngle", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFFadeSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFFadeSlider, data.dofDOFFadeREd, "camDOFFade", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFFadeREd_changed() {
    flameTextFieldChanged(data.dofDOFFadeSlider, data.dofDOFFadeREd, "camDOFFade", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam1Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam1Slider, data.dofDOFParam1REd, "camDOFParam1", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam1REd_changed() {
    flameTextFieldChanged(data.dofDOFParam1Slider, data.dofDOFParam1REd, "camDOFParam1", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam2Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam2Slider, data.dofDOFParam2REd, "camDOFParam2", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam2REd_changed() {
    flameTextFieldChanged(data.dofDOFParam2Slider, data.dofDOFParam2REd, "camDOFParam2", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam3Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam3Slider, data.dofDOFParam3REd, "camDOFParam3", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam3REd_changed() {
    flameTextFieldChanged(data.dofDOFParam3Slider, data.dofDOFParam3REd, "camDOFParam3", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam4Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam4Slider, data.dofDOFParam4REd, "camDOFParam4", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam4REd_changed() {
    flameTextFieldChanged(data.dofDOFParam4Slider, data.dofDOFParam4REd, "camDOFParam4", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam5Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam5Slider, data.dofDOFParam5REd, "camDOFParam5", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam5REd_changed() {
    flameTextFieldChanged(data.dofDOFParam5Slider, data.dofDOFParam5REd, "camDOFParam5", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam6Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam6Slider, data.dofDOFParam6REd, "camDOFParam6", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void dofDOFParam6REd_changed() {
    flameTextFieldChanged(data.dofDOFParam6Slider, data.dofDOFParam6REd, "camDOFParam6", TinaController.SLIDER_SCALE_ZOOM);
  }

  public void spatialOversamplingSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaSpatialOversamplingSlider, data.tinaSpatialOversamplingREd, "spatialOversampling", 1.0);
    owner.refreshFilterKernelPreviewImg();
  }

  public void spatialOversamplingREd_changed() {
    flameTextFieldChanged(data.tinaSpatialOversamplingSlider, data.tinaSpatialOversamplingREd, "spatialOversampling", 1.0);
    owner.refreshFilterKernelPreviewImg();
  }

  public void colorOversamplingSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaColorOversamplingSlider, data.tinaColorOversamplingREd, "colorOversampling", 1.0);
  }

  public void colorOversamplingREd_changed() {
    flameTextFieldChanged(data.tinaColorOversamplingSlider, data.tinaColorOversamplingREd, "colorOversampling", 1.0);
  }

  public void sampleJitteringCbx_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      owner.saveUndoPoint();
      flame.setSampleJittering(data.tinaSampleJitteringCheckBox.isSelected());
      enableControls();
    }
  }

  public void flameTransparencyCbx_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      owner.saveUndoPoint();
      flame.setBGTransparency(data.bgTransparencyCBx.isSelected());
      enableControls();
    }
  }

  public void postNoiseFilterCheckBox_changed() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      owner.saveUndoPoint();
      flame.setPostNoiseFilter(data.tinaPostNoiseFilterCheckBox.isSelected());
      enableControls();
    }
  }

  public void postNoiseFilterThresholdSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaPostNoiseThresholdSlider, data.tinaPostNoiseThresholdField, "postNoiseFilterThreshold", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD);
  }

  public void postNoiseFilterThresholdREd_changed() {
    flameTextFieldChanged(data.tinaPostNoiseThresholdSlider, data.tinaPostNoiseThresholdField, "postNoiseFilterThreshold", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD);
  }

  public void foregroundOpacitySlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.foregroundOpacitySlider, data.foregroundOpacityField, "foregroundOpacity", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD);
  }

  public void foregroundOpacityREd_changed() {
    flameTextFieldChanged(data.foregroundOpacitySlider, data.foregroundOpacityField, "foregroundOpacity", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD);
  }
}
