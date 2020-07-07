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

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.animate.AnimationService;
import org.jwildfire.create.tina.base.BGColorType;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.PostSymmetryType;
import org.jwildfire.create.tina.base.Stereo3dColor;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.Stereo3dPreview;
import org.jwildfire.create.tina.base.ZBufferFilename;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.base.solidrender.DistantLight;
import org.jwildfire.create.tina.base.solidrender.LightDiffFunc;
import org.jwildfire.create.tina.base.solidrender.MaterialSettings;
import org.jwildfire.create.tina.base.solidrender.ReflectionMapping;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.base.solidrender.SolidRenderSettings;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.randomgradient.AllRandomGradientGenerator;
import org.jwildfire.create.tina.render.GammaCorrectionFilter;
import org.jwildfire.create.tina.render.dof.DOFBlurShapeType;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.render.filter.FilteringType;
import org.jwildfire.create.tina.variation.RessourceManager;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.image.WFImage;
import org.jwildfire.swing.ImageFileChooser;

import com.l2fprod.common.beans.editor.FilePropertyEditor;
import com.l2fprod.common.util.ResourceManager;

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

  public FlameControlsDelegate(TinaController pOwner, TinaControllerData pData, JPanel pRootPanel) {
    super(pOwner, pData, pRootPanel, true);
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
    if ("altitude".equals(pPropName)) {
      return getSolidRenderingSelectedLight().getAltitudeCurve();
    }
    else if ("azimuth".equals(pPropName)) {
      return getSolidRenderingSelectedLight().getAzimuthCurve();
    }
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
    res.add(data.cameraBankREd);
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
    res.add(data.lowDensityBrightnessREd);
    res.add(data.balanceRedREd);
    res.add(data.balanceGreenREd);
    res.add(data.balanceBlueREd);
    res.add(data.foregroundOpacityField);

    res.add(data.cameraDOFREd);
    res.add(data.cameraDOFAreaREd);
    res.add(data.cameraDOFExponentREd);
    res.add(data.camZREd);
    res.add(data.dimishZREd);
    res.add(data.focusXREd);
    res.add(data.focusYREd);
    res.add(data.focusZREd);
    res.add(data.dimZDistanceREd);

    res.add(data.dofDOFScaleREd);
    res.add(data.dofDOFAngleREd);
    res.add(data.dofDOFFadeREd);
    res.add(data.dofDOFParam1REd);
    res.add(data.dofDOFParam2REd);
    res.add(data.dofDOFParam3REd);
    res.add(data.dofDOFParam4REd);
    res.add(data.dofDOFParam5REd);
    res.add(data.dofDOFParam6REd);

    res.add(data.tinaSolidRenderingLightAltitudeREd);
    res.add(data.tinaSolidRenderingLightAzimuthREd);
    return res;
  }

  public void enableControls() {
    enableControl(data.cameraRollREd, false);
    enableControl(data.cameraPitchREd, false);
    enableControl(data.cameraYawREd, false);
    enableControl(data.cameraBankREd, false);
    enableControl(data.cameraPerspectiveREd, false);
    enableControl(data.cameraCentreXREd, false);
    enableControl(data.cameraCentreYREd, false);
    enableControl(data.cameraZoomREd, false);
    enableControl(data.pixelsPerUnitREd, false);
    enableControl(data.camPosXREd, false);
    enableControl(data.camPosYREd, false);
    enableControl(data.camPosZREd, false);

    enableControl(data.bgTransparencyCBx, false);
    enableControl(data.backgroundColorTypeCmb, false);
    enableControl(data.foregroundOpacityField, false);

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
    enableControl(data.lowDensityBrightnessREd, false);
    enableControl(data.balanceRedREd, false);
    enableControl(data.balanceGreenREd, false);
    enableControl(data.balanceBlueREd, false);

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
    enableFilterUI();
    enablePostSymmetryUI();
    enableDOFUI();

    enableSolidRenderUI();
  }

  private void enableSolidRenderUI() {
    boolean disabled = getCurrFlame() == null || !getCurrFlame().getSolidRenderSettings().isSolidRenderingEnabled();
    boolean hasLights = getCurrFlame() != null && getCurrFlame().getSolidRenderSettings().getLights().size() > 0;
    boolean hasMaterials = getCurrFlame() != null && getCurrFlame().getSolidRenderSettings().getMaterials().size() > 0;
    boolean aoEnabled = !disabled && getCurrFlame().getSolidRenderSettings().isAoEnabled();
    boolean shadowEnabled = !disabled && ShadowType.areShadowsEnabled(getCurrFlame().getSolidRenderSettings().getShadowType());
    enableControl(data.tinaSolidRenderingAOIntensityREd, disabled || !aoEnabled);
    enableControl(data.tinaSolidRenderingAOSearchRadiusREd, disabled || !aoEnabled);
    enableControl(data.tinaSolidRenderingAOBlurRadiusREd, disabled || !aoEnabled);
    enableControl(data.tinaSolidRenderingAOFalloffREd, disabled || !aoEnabled);
    enableControl(data.tinaSolidRenderingAORadiusSamplesREd, disabled || !aoEnabled);
    enableControl(data.tinaSolidRenderingAOAzimuthSamplesREd, disabled || !aoEnabled);
    enableControl(data.tinaSolidRenderingAOAffectDiffuseREd, disabled || !aoEnabled);
    enableControl(data.tinaSolidRenderingShadowTypeCmb, disabled);
    enableControl(data.tinaSolidRenderingShadowmapSizeCmb, disabled || !shadowEnabled);
    enableControl(data.tinaSolidRenderingShadowSmoothRadiusREd, disabled || !shadowEnabled || !ShadowType.SMOOTH.equals(getCurrFlame().getSolidRenderSettings().getShadowType()));
    enableControl(data.tinaSolidRenderingShadowmapBiasREd, disabled || !shadowEnabled);
    enableControl(data.tinaSolidRenderingEnableAOCBx, disabled);
    enableControl(data.resetSolidRenderingAmbientShadowOptionsBtn, disabled);
    enableControl(data.resetSolidRenderingHardShadowOptionsBtn, disabled);
    enableControl(data.resetSolidRenderingMaterialsBtn, disabled);
    enableControl(data.resetSolidRenderingLightsBtn, disabled);
    enableControl(data.tinaSolidRenderingSelectedLightCmb, disabled || !hasLights);
    enableControl(data.tinaSolidRenderingAddLightBtn, disabled);
    enableControl(data.tinaSolidRenderingDeleteLightBtn, disabled || !hasLights);
    enableControl(data.tinaSolidRenderingLightAltitudeREd, disabled || !hasLights);
    enableControl(data.tinaSolidRenderingLightAzimuthREd, disabled || !hasLights);
    enableControl(data.tinaSolidRenderingLightColorBtn, disabled || !hasLights);
    enableControl(data.tinaSolidRenderingLightCastShadowsCBx, disabled || !hasLights);
    enableControl(data.tinaSolidRenderingLightIntensityREd, disabled || !hasLights);
    enableControl(data.tinaSolidRenderingShadowIntensityREd, disabled || !hasLights);
    enableControl(data.tinaSolidRenderingSelectedMaterialCmb, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingAddMaterialBtn, disabled);
    enableControl(data.tinaSolidRenderingDeleteMaterialBtn, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialDiffuseREd, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialAmbientREd, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialSpecularREd, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialSpecularSharpnessREd, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialSpecularColorBtn, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialDiffuseResponseCmb, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialReflectionMapIntensityREd, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialReflMapBtn, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialReflectionMappingCmb, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialSelectReflMapBtn, disabled || !hasMaterials);
    enableControl(data.tinaSolidRenderingMaterialRemoveReflMapBtn, disabled || !hasMaterials);

    enableBokehPanels();
    enableControl(data.resetPostBokehSettingsBtn, disabled);
    enableControl(data.postBokehIntensityREd, disabled);
    enableControl(data.postBokehBrightnessREd, disabled);
    enableControl(data.postBokehSizeREd, disabled);
    enableControl(data.postBokehActivationREd, disabled);
    enableControl(data.postBokehFilterKernelCmb, disabled);
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

  private void flameSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    setNoRefresh(true);
    try {
      owner.getFrameControlsUtil().valueChangedBySlider(getCurrFlame(), pSlider, pTextField, pProperty, pSliderScale);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  public void cameraYawSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraYawSlider, data.cameraYawREd, "camYaw", 1.0, false);
  }

  public void cameraPerspectiveSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraPerspectiveSlider, data.cameraPerspectiveREd, "camPerspective", TinaController.SLIDER_SCALE_PERSPECTIVE, false);
  }

  public void cameraPitchSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraPitchSlider, data.cameraPitchREd, "camPitch", 1.0, false);
  }

  public void xFormAntialiasAmountSlider_changed() {
    flameSliderChanged(data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", TinaController.SLIDER_SCALE_COLOR, false);
  }

  public void xFormAntialiasRadiusSlider_changed() {
    flameSliderChanged(data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", TinaController.SLIDER_SCALE_COLOR, false);
  }

  public void xFormAntialiasAmountREd_changed() {
    flameTextFieldChanged(data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", TinaController.SLIDER_SCALE_COLOR, false);
  }

  public void xFormAntialiasRadiusREd_changed() {
    flameTextFieldChanged(data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", TinaController.SLIDER_SCALE_COLOR, false);
  }

  public void focusZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.focusZSlider, data.focusZREd, "focusZ", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void focusXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.focusXSlider, data.focusXREd, "focusX", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void focusYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.focusYSlider, data.focusYREd, "focusY", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void diminishZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dimishZSlider, data.dimishZREd, "dimishZ", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void dimZDistanceSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dimZDistanceSlider, data.dimZDistanceREd, "dimZDistance", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void camZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.camZSlider, data.camZREd, "camZ", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void cameraDOFSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraDOFSlider, data.cameraDOFREd, "camDOF", TinaController.SLIDER_SCALE_DOF, false);
  }

  public void cameraDOFAreaSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraDOFAreaSlider, data.cameraDOFAreaREd, "camDOFArea", TinaController.SLIDER_SCALE_DOF_AREA, false);
  }

  public void cameraDOFExponentSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraDOFExponentSlider, data.cameraDOFExponentREd, "camDOFExponent", TinaController.SLIDER_SCALE_DOF_EXPONENT, false);
  }

  public void cameraRollSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraRollSlider, data.cameraRollREd, "camRoll", 1.0, false);
  }

  public void cameraBankSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraBankSlider, data.cameraBankREd, "camBank", 1.0, false);
  }

  public void cameraCentreYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraCentreYSlider, data.cameraCentreYREd, "centreY", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void cameraCentreXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraCentreXSlider, data.cameraCentreXREd, "centreX", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void cameraZoomSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.cameraZoomSlider, data.cameraZoomREd, "camZoom", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void brightnessSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.brightnessSlider, data.brightnessREd, "brightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void vibrancySlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.vibrancySlider, data.vibrancyREd, "vibrancy", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void lowDensityBrightnessSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.lowDensityBrightnessSlider, data.lowDensityBrightnessREd, "lowDensityBrightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void balanceRedSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.balanceRedSlider, data.balanceRedREd, "balanceRed", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void balanceGreenSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.balanceGreenSlider, data.balanceGreenREd, "balanceGreen", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void balanceBlueSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.balanceBlueSlider, data.balanceBlueREd, "balanceBlue", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void saturationSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.saturationSlider, data.saturationREd, "saturation", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void filterRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
    owner.refreshFilterKernelPreviewImg();
  }

  public void filterSharpnessSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaFilterSharpnessSlider, data.tinaFilterSharpnessREd, "spatialFilterSharpness", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
  }

  public void filterLowDensitySlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaFilterLowDensitySlider, data.tinaFilterLowDensityREd, "spatialFilterLowDensity", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
  }

  public void gammaThresholdSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, true);
  }

  public void vibrancyREd_changed() {
    flameTextFieldChanged(data.vibrancySlider, data.vibrancyREd, "vibrancy", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void lowDensityBrightnessREd_changed() {
    flameTextFieldChanged(data.lowDensityBrightnessSlider, data.lowDensityBrightnessREd, "lowDensityBrightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void balanceRedREd_changed() {
    flameTextFieldChanged(data.balanceRedSlider, data.balanceRedREd, "balanceRed", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void balanceGreenREd_changed() {
    flameTextFieldChanged(data.balanceGreenSlider, data.balanceGreenREd, "balanceGreen", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void balanceBlueREd_changed() {
    flameTextFieldChanged(data.balanceBlueSlider, data.balanceBlueREd, "balanceBlue", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void saturationREd_changed() {
    flameTextFieldChanged(data.saturationSlider, data.saturationREd, "saturation", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void pixelsPerUnitSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.pixelsPerUnitSlider, data.pixelsPerUnitREd, "pixelsPerUnit", 1.0, false);
  }

  private void flameTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh()) {
      return;
    }
    if (getCurrFlame() == null) {
      return;
    }
    setNoRefresh(true);
    try {
      owner.getFrameControlsUtil().valueChangedByTextField(getCurrFlame(), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  public void cameraCentreYREd_changed() {
    flameTextFieldChanged(data.cameraCentreYSlider, data.cameraCentreYREd, "centreY", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void cameraCentreXREd_changed() {
    flameTextFieldChanged(data.cameraCentreXSlider, data.cameraCentreXREd, "centreX", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void cameraZoomREd_changed() {
    flameTextFieldChanged(data.cameraZoomSlider, data.cameraZoomREd, "camZoom", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void filterRadiusREd_changed() {
    flameTextFieldChanged(data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
    owner.refreshFilterKernelPreviewImg();
  }

  public void filterSharpnessREd_changed() {
    flameTextFieldChanged(data.tinaFilterSharpnessSlider, data.tinaFilterSharpnessREd, "spatialFilterSharpness", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
  }

  public void filterLowDensityREd_changed() {
    flameTextFieldChanged(data.tinaFilterLowDensitySlider, data.tinaFilterLowDensityREd, "spatialFilterLowDensity", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
  }

  public void gammaREd_changed() {
    flameTextFieldChanged(data.gammaSlider, data.gammaREd, "gamma", TinaController.SLIDER_SCALE_GAMMA, true);
  }

  public void gammaThresholdREd_changed() {
    flameTextFieldChanged(data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, true);
  }

  public void contrastREd_changed() {
    flameTextFieldChanged(data.contrastSlider, data.contrastREd, "contrast", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void whiteLevelREd_changed() {
    flameTextFieldChanged(data.whiteLevelSlider, data.whiteLevelREd, "whiteLevel", 1.0, true);
  }

  public void gammaSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.gammaSlider, data.gammaREd, "gamma", TinaController.SLIDER_SCALE_GAMMA, true);
  }

  public void pixelsPerUnitREd_changed() {
    flameTextFieldChanged(data.pixelsPerUnitSlider, data.pixelsPerUnitREd, "pixelsPerUnit", 1.0, false);
  }

  public void brightnessREd_changed() {
    flameTextFieldChanged(data.brightnessSlider, data.brightnessREd, "brightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void contrastSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.contrastSlider, data.contrastREd, "contrast", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void whiteLevelSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.whiteLevelSlider, data.whiteLevelREd, "whiteLevel", 1.0, true);
  }

  public void cameraRollREd_changed() {
    flameTextFieldChanged(data.cameraRollSlider, data.cameraRollREd, "camRoll", 1.0, false);
  }

  public void cameraPitchREd_changed() {
    flameTextFieldChanged(data.cameraPitchSlider, data.cameraPitchREd, "camPitch", 1.0, false);
  }

  public void cameraYawREd_changed() {
    flameTextFieldChanged(data.cameraYawSlider, data.cameraYawREd, "camYaw", 1.0, false);
  }

  public void cameraBankREd_changed() {
    flameTextFieldChanged(data.cameraBankSlider, data.cameraBankREd, "camBank", 1.0, false);
  }

  public void cameraPerspectiveREd_changed() {
    flameTextFieldChanged(data.cameraPerspectiveSlider, data.cameraPerspectiveREd, "camPerspective", TinaController.SLIDER_SCALE_PERSPECTIVE, false);
  }

  public void focusXREd_changed() {
    flameTextFieldChanged(data.focusXSlider, data.focusXREd, "focusX", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void focusYREd_changed() {
    flameTextFieldChanged(data.focusYSlider, data.focusYREd, "focusY", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void focusZREd_changed() {
    flameTextFieldChanged(data.focusZSlider, data.focusZREd, "focusZ", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void diminishZREd_changed() {
    flameTextFieldChanged(data.dimishZSlider, data.dimishZREd, "dimishZ", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void dimZDistanceREd_changed() {
    flameTextFieldChanged(data.dimZDistanceSlider, data.dimZDistanceREd, "dimZDistance", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void cameraDOFREd_changed() {
    flameTextFieldChanged(data.cameraDOFSlider, data.cameraDOFREd, "camDOF", TinaController.SLIDER_SCALE_DOF, false);
  }

  public void camZREd_changed() {
    flameTextFieldChanged(data.camZSlider, data.camZREd, "camZ", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void cameraDOFAreaREd_changed() {
    flameTextFieldChanged(data.cameraDOFAreaSlider, data.cameraDOFAreaREd, "camDOFArea", TinaController.SLIDER_SCALE_DOF_AREA, false);
  }

  public void cameraDOFExponentREd_changed() {
    flameTextFieldChanged(data.cameraDOFExponentSlider, data.cameraDOFExponentREd, "camDOFExponent", TinaController.SLIDER_SCALE_DOF_EXPONENT, false);
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
    data.camZREd.setEnabled(!newDOF);
    data.camZSlider.setEnabled(!newDOF);
    data.dimishZREd.setEnabled(getCurrFlame() != null);
    data.dimishZSlider.setEnabled(getCurrFlame() != null);
    data.dimishZColorButton.setEnabled(getCurrFlame() != null);
    data.dimZDistanceREd.setEnabled(getCurrFlame() != null);
    data.dimZDistanceSlider.setEnabled(getCurrFlame() != null);
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

  public void enableBGColorUI() {
    boolean enabled = getCurrFlame() != null && (BGColorType.GRADIENT_2X2.equals(getCurrFlame().getBgColorType()) || BGColorType.GRADIENT_2X2_C.equals(getCurrFlame().getBgColorType()));
    data.backgroundColorIndicatorBtn.setEnabled(enabled);
    data.backgroundColorURIndicatorBtn.setEnabled(enabled);
    data.backgroundColorLLIndicatorBtn.setEnabled(enabled);
    data.backgroundColorLRIndicatorBtn.setEnabled(enabled);
    data.backgroundColorCCIndicatorBtn.setEnabled(getCurrFlame() != null && (BGColorType.SINGLE_COLOR.equals(getCurrFlame().getBgColorType()) || BGColorType.GRADIENT_2X2_C.equals(getCurrFlame().getBgColorType())));
  }

  public void enableFilterUI() {
    enableControl(data.tinaFilterTypeCmb, false);
    boolean filterDisabled = data.tinaFilterTypeCmb.getSelectedItem() == null;
    enableControl(data.filterKernelCmb, filterDisabled);
    enableControl(data.filterRadiusREd, filterDisabled);
    boolean adapativeOptionsDisabled = !FilteringType.ADAPTIVE.equals(data.tinaFilterTypeCmb.getSelectedItem());
    enableControl(data.tinaFilterIndicatorCBx, adapativeOptionsDisabled);
    enableControl(data.tinaFilterLowDensityREd, adapativeOptionsDisabled);
    enableControl(data.tinaFilterIndicatorCBx, adapativeOptionsDisabled);

    enableControl(data.tinaSpatialOversamplingREd, false);
    enableControl(data.tinaPostNoiseFilterCheckBox, false);
    enableControl(data.tinaPostNoiseThresholdField, !data.tinaPostNoiseFilterCheckBox.isSelected());
    enableControl(data.tinaOptiXDenoiserCheckBox, false);
    //enableControl(data.tinaOptiXDenoiserBlendField, !data.tinaOptiXDenoiserCheckBox.isSelected());
    enableControl(data.tinaOptiXDenoiserBlendField, false);
  }

  public void refreshFlameValues() {
    boolean oldNoRefrsh = isNoRefresh();
    setNoRefresh(true);
    try {
      refreshReflMapColorIndicator();

      refreshSolidRenderingSelectedLightCmb();
      refreshSolidRenderingSelectedMaterialCmb();

      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.camPosXSlider, data.camPosXREd, "camPosX", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.camPosYSlider, data.camPosYREd, "camPosY", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.camPosZSlider, data.camPosZREd, "camPosZ", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraPerspectiveSlider, data.cameraPerspectiveREd, "camPerspective", TinaController.SLIDER_SCALE_PERSPECTIVE);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraZoomSlider, data.cameraZoomREd, "camZoom", TinaController.SLIDER_SCALE_ZOOM);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraDOFSlider, data.cameraDOFREd, "camDOF", TinaController.SLIDER_SCALE_DOF);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraDOFAreaSlider, data.cameraDOFAreaREd, "camDOFArea", TinaController.SLIDER_SCALE_DOF_AREA);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraDOFExponentSlider, data.cameraDOFExponentREd, "camDOFExponent", TinaController.SLIDER_SCALE_DOF_EXPONENT);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.camZSlider, data.camZREd, "camZ", TinaController.SLIDER_SCALE_ZPOS);
      data.newDOFCBx.setSelected(getCurrFlame().isNewCamDOF());
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.brightnessSlider, data.brightnessREd, "brightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.saturationSlider, data.saturationREd, "saturation", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.contrastSlider, data.contrastREd, "contrast", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.whiteLevelSlider, data.whiteLevelREd, "whiteLevel", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.vibrancySlider, data.vibrancyREd, "vibrancy", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.gammaSlider, data.gammaREd, "gamma", TinaController.SLIDER_SCALE_GAMMA);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.tinaFilterSharpnessSlider, data.tinaFilterSharpnessREd, "spatialFilterSharpness", TinaController.SLIDER_SCALE_FILTER_RADIUS);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.tinaFilterLowDensitySlider, data.tinaFilterLowDensityREd, "spatialFilterLowDensity", TinaController.SLIDER_SCALE_FILTER_RADIUS);

      data.tinaFilterIndicatorCBx.setSelected(getCurrFlame().isSpatialFilterIndicator());
      data.tinaFilterTypeCmb.setSelectedItem(getCurrFlame().getSpatialFilteringType());
      fillFilterKernelCmb(getCurrFlame().getSpatialFilteringType());
      data.filterKernelCmb.setSelectedItem(getCurrFlame().getSpatialFilterKernel());
      enableFilterUI();

      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.lowDensityBrightnessSlider, data.lowDensityBrightnessREd, "lowDensityBrightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.balanceRedSlider, data.balanceRedREd, "balanceRed", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.balanceGreenSlider, data.balanceGreenREd, "balanceGreen", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.balanceBlueSlider, data.balanceBlueREd, "balanceBlue", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY);

      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.tinaSpatialOversamplingSlider, data.tinaSpatialOversamplingREd, "spatialOversampling", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.tinaPostNoiseThresholdSlider, data.tinaPostNoiseThresholdField, "postNoiseFilterThreshold", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD);
      data.tinaPostNoiseFilterCheckBox.setSelected(getCurrFlame().isPostNoiseFilter());
      data.tinaOptiXDenoiserCheckBox.setSelected(getCurrFlame().isPostOptiXDenoiser());
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.tinaOptiXDenoiserBlendSlider, data.tinaOptiXDenoiserBlendField, "postOptiXDenoiserBlend", TinaController.SLIDER_SCALE_POST_OPTIX_DENOISER_BLEND);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.foregroundOpacitySlider, data.foregroundOpacityField, "foregroundOpacity", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
      data.bgTransparencyCBx.setSelected(getCurrFlame().isBGTransparency());
      data.backgroundColorTypeCmb.setSelectedItem(getCurrFlame().getBgColorType());
      enableBGColorUI();

      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", TinaController.SLIDER_SCALE_COLOR);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", TinaController.SLIDER_SCALE_COLOR);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.motionBlurLengthSlider, data.motionBlurLengthField, "motionBlurLength", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.motionBlurTimeStepSlider, data.motionBlurTimeStepField, "motionBlurTimeStep", TinaController.SLIDER_SCALE_COLOR);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.motionBlurDecaySlider, data.motionBlurDecayField, "motionBlurDecay", TinaController.SLIDER_SCALE_ZOOM);

      data.flameFPSField.setValue(getCurrFlame().getFps());
      data.postSymmetryTypeCmb.setSelectedItem(getCurrFlame().getPostSymmetryType());
      enablePostSymmetryUI();

      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.postSymmetryDistanceSlider, data.postSymmetryDistanceREd, "postSymmetryDistance", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.postSymmetryRotationSlider, data.postSymmetryRotationREd, "postSymmetryRotation", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.postSymmetryOrderSlider, data.postSymmetryOrderREd, "postSymmetryOrder", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.postSymmetryCentreXSlider, data.postSymmetryCentreXREd, "postSymmetryCentreX", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.postSymmetryCentreYSlider, data.postSymmetryCentreYREd, "postSymmetryCentreY", TinaController.SLIDER_SCALE_CENTRE);

      data.stereo3dModeCmb.setSelectedItem(getCurrFlame().getStereo3dMode());
      enableStereo3dUI();
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.stereo3dAngleSlider, data.stereo3dAngleREd, "stereo3dAngle", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.stereo3dEyeDistSlider, data.stereo3dEyeDistREd, "stereo3dEyeDist", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.stereo3dFocalOffsetSlider, data.stereo3dFocalOffsetREd, "stereo3dFocalOffset", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD);
      data.stereo3dLeftEyeColorCmb.setSelectedItem(getCurrFlame().getStereo3dLeftEyeColor());
      data.stereo3dRightEyeColorCmb.setSelectedItem(getCurrFlame().getStereo3dRightEyeColor());
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.stereo3dInterpolatedImageCountSlider, data.stereo3dInterpolatedImageCountREd, "stereo3dInterpolatedImageCount", 1.0);
      data.stereo3dPreviewCmb.setSelectedItem(getCurrFlame().getStereo3dPreview());
      data.stereo3dSwapSidesCBx.setSelected(getCurrFlame().isStereo3dSwapSides());

      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.postBlurRadiusSlider, data.postBlurRadiusREd, "postBlurRadius", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.postBlurFadeSlider, data.postBlurFadeREd, "postBlurFade", TinaController.SLIDER_SCALE_AMBIENT);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.postBlurFallOffSlider, data.postBlurFallOffREd, "postBlurFallOff", TinaController.SLIDER_SCALE_AMBIENT);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.tinaZBufferScaleSlider, data.tinaZBufferScaleREd, "zBufferScale", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.tinaZBufferBiasSlider, data.tinaZBufferBiasREd, "zBufferBias", TinaController.SLIDER_SCALE_CENTRE);
      data.tinaZBufferFilename1.setSelected(getCurrFlame().getZBufferFilename() == ZBufferFilename.PRE_ZBUF);
      data.tinaZBufferFilename2.setSelected(getCurrFlame().getZBufferFilename() == ZBufferFilename.POST_DEPTH);

      setupDOFParamsControls(getCurrFlame().getCamDOFShape());
      data.dofDOFShapeCmb.setSelectedItem(getCurrFlame().getCamDOFShape());
      refreshBokehParams();

      refreshSolidRenderSettingsUI();
    }
    finally {
      setNoRefresh(oldNoRefrsh);
    }
    refreshVisualCamValues();
  }

  private void refreshSolidRenderingSelectedMaterialCmb() {
    refreshSelectItemCmb(getCurrFlame().getSolidRenderSettings().getMaterials(), data.tinaSolidRenderingSelectedMaterialCmb, "Material ");
  }

  private void refreshSelectItemCmb(List<?> items, JComboBox cmb, String itemPrefix) {
    boolean oldNoRefresh = isNoRefresh();
    setNoRefresh(true);
    try {
      if (items.size() != cmb.getItemCount()) {
        int selected = cmb.getSelectedIndex();
        cmb.removeAllItems();
        for (int i = 0; i < items.size(); i++) {
          cmb.addItem(itemPrefix + String.valueOf(i));
        }
        if (selected < 0 && items.size() > 0) {
          selected = 0;
        }
        else if (selected >= items.size()) {
          selected = items.size() - 1;
        }
        cmb.setSelectedIndex(selected);
      }
    }
    finally {
      setNoRefresh(oldNoRefresh);
    }
  }

  private void refreshSolidRenderingSelectedLightCmb() {
    refreshSelectItemCmb(getCurrFlame().getSolidRenderSettings().getLights(), data.tinaSolidRenderingSelectedLightCmb, "Light ");
  }

  private int getSolidRenderingSelectedLightIndex() {
    return data.tinaSolidRenderingSelectedLightCmb.getSelectedIndex();
  }

  private DistantLight getSolidRenderingSelectedLight() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      SolidRenderSettings settings = flame.getSolidRenderSettings();
      int idx = getSolidRenderingSelectedLightIndex();
      if (idx >= 0 && idx < settings.getLights().size()) {
        return settings.getLights().get(idx);
      }
    }
    return null;
  }

  private int getSolidRenderingSelectedMaterialIndex() {
    return data.tinaSolidRenderingSelectedMaterialCmb.getSelectedIndex();
  }

  private MaterialSettings getSolidRenderingSelectedMaterial() {
    Flame flame = getCurrFlame();
    if (flame != null) {
      SolidRenderSettings settings = flame.getSolidRenderSettings();
      int idx = getSolidRenderingSelectedMaterialIndex();
      if (idx >= 0 && idx < settings.getMaterials().size()) {
        return settings.getMaterials().get(idx);
      }
    }
    return null;
  }

  private void refreshSolidRenderLightColorIndicator() {
    DistantLight light = getSolidRenderingSelectedLight();
    Color color = (light != null) ? new Color(Tools.roundColor(light.getRed() * GammaCorrectionFilter.COLORSCL),
        Tools.roundColor(light.getGreen() * GammaCorrectionFilter.COLORSCL), Tools.roundColor(light.getBlue() * GammaCorrectionFilter.COLORSCL)) : Color.BLACK;
    data.tinaSolidRenderingLightColorBtn.setBackground(color);
  }

  private void refreshSolidRenderMaterialSpecularColorIndicator() {
    MaterialSettings material = getSolidRenderingSelectedMaterial();
    Color color = (material != null) ? new Color(Tools.roundColor(material.getPhongRed() * GammaCorrectionFilter.COLORSCL),
        Tools.roundColor(material.getPhongGreen() * GammaCorrectionFilter.COLORSCL), Tools.roundColor(material.getPhongBlue() * GammaCorrectionFilter.COLORSCL)) : Color.BLACK;
    data.tinaSolidRenderingMaterialSpecularColorBtn.setBackground(color);
  }

  private void refreshSolidRenderSettingsUI() {
    refreshSolidRenderingGlobals();
    refreshSolidRenderingLightControls();
    refreshSolidRenderingMaterialControls();
  }

  private void enableBokehPanels() {
    if (getCurrFlame() != null) {
      SolidRenderSettings settings = getCurrFlame().getSolidRenderSettings();
      data.postBokehSettingsPnl.setEnabled(settings.isSolidRenderingEnabled());
      data.bokehSettingsPnl.setEnabled(!settings.isSolidRenderingEnabled());
      JTabbedPane parent = (JTabbedPane) data.bokehSettingsPnl.getParent();
      // only the following sequence of commands seems to actually work, i.e.: both select the tab and display its contents
      if(settings.isSolidRenderingEnabled() && parent.getSelectedIndex()==2) {
        parent.setSelectedIndex(1);
      }
      else if(!settings.isSolidRenderingEnabled() && parent.getSelectedIndex()==1) {
        parent.setSelectedIndex(2);
      }
      parent.setEnabledAt(1, !settings.isSolidRenderingEnabled());
      parent.setEnabledAt(2, settings.isSolidRenderingEnabled());
      parent.setSelectedIndex(0);
      // end of sequence
    }
  }

  private void refreshSolidRenderingGlobals() {
    enableBokehPanels();
    SolidRenderSettings settings = getCurrFlame().getSolidRenderSettings();
    data.solidRenderingToggleBtn.setSelected(settings.isSolidRenderingEnabled());
    data.tinaSolidRenderingEnableAOCBx.setSelected(settings.isAoEnabled());

    owner.getFrameControlsUtil().updateControl(settings, data.tinaSolidRenderingAOIntensitySlider, data.tinaSolidRenderingAOIntensityREd, "aoIntensity", TinaController.SLIDER_SCALE_CENTRE);
    owner.getFrameControlsUtil().updateControl(settings, data.tinaSolidRenderingAOSearchRadiusSlider, data.tinaSolidRenderingAOSearchRadiusREd, "aoSearchRadius", TinaController.SLIDER_SCALE_CENTRE);
    owner.getFrameControlsUtil().updateControl(settings, data.tinaSolidRenderingAOBlurRadiusSlider, data.tinaSolidRenderingAOBlurRadiusREd, "aoBlurRadius", TinaController.SLIDER_SCALE_CENTRE);
    owner.getFrameControlsUtil().updateControl(settings, data.tinaSolidRenderingAOFalloffSlider, data.tinaSolidRenderingAOFalloffREd, "aoFalloff", TinaController.SLIDER_SCALE_CENTRE);
    owner.getFrameControlsUtil().updateControl(settings, data.tinaSolidRenderingAORadiusSamplesSlider, data.tinaSolidRenderingAORadiusSamplesREd, "aoRadiusSamples", 1.0);
    owner.getFrameControlsUtil().updateControl(settings, data.tinaSolidRenderingAOAzimuthSamplesSlider, data.tinaSolidRenderingAOAzimuthSamplesREd, "aoAzimuthSamples", 1.0);
    owner.getFrameControlsUtil().updateControl(settings, data.tinaSolidRenderingAOAffectDiffuseSlider, data.tinaSolidRenderingAOAffectDiffuseREd, "aoAffectDiffuse", TinaController.SLIDER_SCALE_CENTRE);
    data.tinaSolidRenderingShadowTypeCmb.setSelectedItem(settings.getShadowType());
    data.tinaSolidRenderingShadowmapSizeCmb.setSelectedItem(String.valueOf(settings.getShadowmapSize()));
    if (data.tinaSolidRenderingShadowmapSizeCmb.getSelectedIndex() < 0) {
      data.tinaSolidRenderingShadowmapSizeCmb.setSelectedIndex(0);
    }
    owner.getFrameControlsUtil().updateControl(settings, data.tinaSolidRenderingShadowSmoothRadiusSlider, data.tinaSolidRenderingShadowSmoothRadiusREd, "shadowSmoothRadius", TinaController.SLIDER_SCALE_CENTRE);
    owner.getFrameControlsUtil().updateControl(settings, data.tinaSolidRenderingShadowmapBiasSlider, data.tinaSolidRenderingShadowmapBiasREd, "shadowmapBias", TinaController.SLIDER_SCALE_CENTRE);

    owner.getFrameControlsUtil().updateControl(settings, data.postBokehIntensitySlider, data.postBokehIntensityREd, "postBokehIntensity", TinaController.SLIDER_SCALE_CENTRE);
    owner.getFrameControlsUtil().updateControl(settings, data.postBokehBrightnessSlider, data.postBokehBrightnessREd, "postBokehBrightness", TinaController.SLIDER_SCALE_CENTRE);
    owner.getFrameControlsUtil().updateControl(settings, data.postBokehSizeSlider, data.postBokehSizeREd, "postBokehSize", TinaController.SLIDER_SCALE_CENTRE);
    owner.getFrameControlsUtil().updateControl(settings, data.postBokehActivationSlider, data.postBokehActivationREd, "postBokehActivation", TinaController.SLIDER_SCALE_CENTRE);
    data.postBokehFilterKernelCmb.setSelectedItem(settings.getPostBokehFilterKernel());
  }

  private void refreshSolidRenderingMaterialControls() {
    MaterialSettings material = getSolidRenderingSelectedMaterial();
    if (material != null) {
      owner.getFrameControlsUtil().updateControl(material, data.tinaSolidRenderingMaterialDiffuseSlider, data.tinaSolidRenderingMaterialDiffuseREd, "diffuse", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(material, data.tinaSolidRenderingMaterialAmbientSlider, data.tinaSolidRenderingMaterialAmbientREd, "ambient", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(material, data.tinaSolidRenderingMaterialSpecularSlider, data.tinaSolidRenderingMaterialSpecularREd, "phong", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(material, data.tinaSolidRenderingMaterialSpecularSharpnessSlider, data.tinaSolidRenderingMaterialSpecularSharpnessREd, "phongSize", TinaController.SLIDER_SCALE_CENTRE);
      data.tinaSolidRenderingMaterialDiffuseResponseCmb.setSelectedItem(material.getLightDiffFunc());
      owner.getFrameControlsUtil().updateControl(material, data.tinaSolidRenderingMaterialReflectionMapIntensitySlider, data.tinaSolidRenderingMaterialReflectionMapIntensityREd, "reflMapIntensity", TinaController.SLIDER_SCALE_CENTRE);
      data.tinaSolidRenderingMaterialReflectionMappingCmb.setSelectedItem(material.getReflectionMapping());
    }
    refreshSolidRenderMaterialSpecularColorIndicator();
  }

  private void refreshLightPosControls(DistantLight light) {
    owner.getFrameControlsUtil().updateControl(light, data.tinaSolidRenderingLightAltitudeSlider, data.tinaSolidRenderingLightAltitudeREd, "altitude", TinaController.SLIDER_SCALE_CENTRE);
    owner.getFrameControlsUtil().updateControl(light, data.tinaSolidRenderingLightAzimuthSlider, data.tinaSolidRenderingLightAzimuthREd, "azimuth", TinaController.SLIDER_SCALE_CENTRE);
  }

  private void refreshSolidRenderingLightControls() {
    DistantLight light = getSolidRenderingSelectedLight();
    if (light != null) {
      refreshLightPosControls(light);
      data.tinaSolidRenderingLightCastShadowsCBx.setSelected(light.isCastShadows());
      owner.getFrameControlsUtil().updateControl(light, data.tinaSolidRenderingLightIntensitySlider, data.tinaSolidRenderingLightIntensityREd, "intensity", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(light, data.tinaSolidRenderingShadowIntensitySlider, data.tinaSolidRenderingShadowIntensityREd, "shadowIntensity", TinaController.SLIDER_SCALE_CENTRE);
    }
    refreshSolidRenderLightColorIndicator();
  }

  private void refreshBokehParams() {
    owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dofDOFScaleSlider, data.dofDOFScaleREd, "camDOFScale", TinaController.SLIDER_SCALE_ZOOM);
    owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dofDOFAngleSlider, data.dofDOFAngleREd, "camDOFAngle", TinaController.SLIDER_SCALE_ZOOM);
    owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dofDOFFadeSlider, data.dofDOFFadeREd, "camDOFFade", TinaController.SLIDER_SCALE_ZOOM);
    owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dofDOFParam1Slider, data.dofDOFParam1REd, "camDOFParam1", TinaController.SLIDER_SCALE_ZOOM);
    owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dofDOFParam2Slider, data.dofDOFParam2REd, "camDOFParam2", TinaController.SLIDER_SCALE_ZOOM);
    owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dofDOFParam3Slider, data.dofDOFParam3REd, "camDOFParam3", TinaController.SLIDER_SCALE_ZOOM);
    owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dofDOFParam4Slider, data.dofDOFParam4REd, "camDOFParam4", TinaController.SLIDER_SCALE_ZOOM);
    owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dofDOFParam5Slider, data.dofDOFParam5REd, "camDOFParam5", TinaController.SLIDER_SCALE_ZOOM);
    owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dofDOFParam6Slider, data.dofDOFParam6REd, "camDOFParam6", TinaController.SLIDER_SCALE_ZOOM);
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
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraRollSlider, data.cameraRollREd, "camRoll", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraPitchSlider, data.cameraPitchREd, "camPitch", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraYawSlider, data.cameraYawREd, "camYaw", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraBankSlider, data.cameraBankREd, "camBank", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraCentreXSlider, data.cameraCentreXREd, "centreX", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.cameraCentreYSlider, data.cameraCentreYREd, "centreY", TinaController.SLIDER_SCALE_CENTRE);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.pixelsPerUnitSlider, data.pixelsPerUnitREd, "pixelsPerUnit", 1.0);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.focusXSlider, data.focusXREd, "focusX", TinaController.SLIDER_SCALE_ZPOS);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.focusYSlider, data.focusYREd, "focusY", TinaController.SLIDER_SCALE_ZPOS);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.focusZSlider, data.focusZREd, "focusZ", TinaController.SLIDER_SCALE_ZPOS);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dimishZSlider, data.dimishZREd, "dimishZ", TinaController.SLIDER_SCALE_ZPOS);
      owner.getFrameControlsUtil().updateControl(getCurrFlame(), data.dimZDistanceSlider, data.dimZDistanceREd, "dimZDistance", TinaController.SLIDER_SCALE_ZPOS);
      refreshDimishZColorIndicator();
    }
    finally {
      setNoRefresh(oldNoRefrsh);
    }
  }

  private void refreshDimishZColorIndicator() {
    Color color = getCurrFlame() != null ? new Color(getCurrFlame().getDimishZRed(),
        getCurrFlame().getDimishZGreen(), getCurrFlame().getDimishZBlue()) : Color.BLACK;
    data.dimishZColorButton.setBackground(color);
  }

  public void dimishZColorBtn_clicked() {
    if (getCurrFlame() != null) {
      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");

      Color selectedColor = JColorChooser.showDialog(rootPanel, title, new Color(getCurrFlame().getDimishZRed(),
          getCurrFlame().getDimishZGreen(), getCurrFlame().getDimishZBlue()));
      if (selectedColor != null) {
        getCurrFlame().setDimishZRed(selectedColor.getRed());
        getCurrFlame().setDimishZGreen(selectedColor.getGreen());
        getCurrFlame().setDimishZBlue(selectedColor.getBlue());
        refreshDimishZColorIndicator();
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void motionBlurLengthREd_changed() {
    flameTextFieldChanged(data.motionBlurLengthSlider, data.motionBlurLengthField, "motionBlurLength", 1.0, false);
  }

  public void flameFPSField_changed() {
    flameTextFieldChanged(null, data.flameFPSField, "fps", 1.0, false);
  }

  public void motionBlurLengthSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.motionBlurLengthSlider, data.motionBlurLengthField, "motionBlurLength", 1.0, false);
  }

  public void motionBlurTimeStepREd_changed() {
    flameTextFieldChanged(data.motionBlurTimeStepSlider, data.motionBlurTimeStepField, "motionBlurTimeStep", TinaController.SLIDER_SCALE_COLOR, false);
  }

  public void motionBlurTimeStepSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.motionBlurTimeStepSlider, data.motionBlurTimeStepField, "motionBlurTimeStep", TinaController.SLIDER_SCALE_COLOR, false);
  }

  public void motionBlurDecayREd_changed() {
    flameTextFieldChanged(data.motionBlurDecaySlider, data.motionBlurDecayField, "motionBlurDecay", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void motionBlurDecaySlider_changed(ChangeEvent e) {
    flameSliderChanged(data.motionBlurDecaySlider, data.motionBlurDecayField, "motionBlurDecay", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void postSymmetryCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setPostSymmetryType((PostSymmetryType) data.postSymmetryTypeCmb.getSelectedItem());
        enablePostSymmetryUI();
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void postSymmetryDistanceREd_changed() {
    flameTextFieldChanged(data.postSymmetryDistanceSlider, data.postSymmetryDistanceREd, "postSymmetryDistance", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void postSymmetryDistanceSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryDistanceSlider, data.postSymmetryDistanceREd, "postSymmetryDistance", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void postSymmetryRotationREd_changed() {
    flameTextFieldChanged(data.postSymmetryRotationSlider, data.postSymmetryRotationREd, "postSymmetryRotation", 1.0, false);
  }

  public void postSymmetryRotationSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryRotationSlider, data.postSymmetryRotationREd, "postSymmetryRotation", 1.0, false);
  }

  public void postSymmetryCentreXREd_changed() {
    flameTextFieldChanged(data.postSymmetryCentreXSlider, data.postSymmetryCentreXREd, "postSymmetryCentreX", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void postSymmetryCentreXSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryCentreXSlider, data.postSymmetryCentreXREd, "postSymmetryCentreX", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void postSymmetryCentreYREd_changed() {
    flameTextFieldChanged(data.postSymmetryCentreYSlider, data.postSymmetryCentreYREd, "postSymmetryCentreY", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void postSymmetryCentreYSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryCentreYSlider, data.postSymmetryCentreYREd, "postSymmetryCentreY", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void postSymmetryOrderREd_changed() {
    flameTextFieldChanged(data.postSymmetryOrderSlider, data.postSymmetryOrderREd, "postSymmetryOrder", 1.0, false);
  }

  public void postSymmetryOrderSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.postSymmetryOrderSlider, data.postSymmetryOrderREd, "postSymmetryOrder", 1.0, false);
  }

  public void stereo3dModeCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setStereo3dMode((Stereo3dMode) data.stereo3dModeCmb.getSelectedItem());
        enableStereo3dUI();
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void stereo3dLeftEyeColorCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setStereo3dLeftEyeColor((Stereo3dColor) data.stereo3dLeftEyeColorCmb.getSelectedItem());
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void stereo3dRightEyeColorCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setStereo3dRightEyeColor((Stereo3dColor) data.stereo3dRightEyeColorCmb.getSelectedItem());
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void stereo3dAngleREd_changed() {
    flameTextFieldChanged(data.stereo3dAngleSlider, data.stereo3dAngleREd, "stereo3dAngle", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, false);
  }

  public void stereo3dAngleSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.stereo3dAngleSlider, data.stereo3dAngleREd, "stereo3dAngle", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, false);
  }

  public void stereo3dEyeDistREd_changed() {
    flameTextFieldChanged(data.stereo3dEyeDistSlider, data.stereo3dEyeDistREd, "stereo3dEyeDist", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, false);
  }

  public void stereo3dEyeDistSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.stereo3dEyeDistSlider, data.stereo3dEyeDistREd, "stereo3dEyeDist", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, false);
  }

  public void stereo3dFocalOffsetREd_changed() {
    flameTextFieldChanged(data.stereo3dFocalOffsetSlider, data.stereo3dFocalOffsetREd, "stereo3dFocalOffset", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, false);
  }

  public void stereo3dFocalOffsetSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.stereo3dFocalOffsetSlider, data.stereo3dFocalOffsetREd, "stereo3dFocalOffset", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, false);
  }

  public void stereo3dInterpolatedImageCountREd_changed() {
    flameTextFieldChanged(data.stereo3dInterpolatedImageCountSlider, data.stereo3dInterpolatedImageCountREd, "stereo3dInterpolatedImageCount", 1.0, false);
  }

  public void stereo3dInterpolatedImageCountSlider_changed(ChangeEvent e) {
    flameSliderChanged(data.stereo3dInterpolatedImageCountSlider, data.stereo3dInterpolatedImageCountREd, "stereo3dInterpolatedImageCount", 1.0, false);
  }

  public void stereo3dPreviewCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setStereo3dPreview((Stereo3dPreview) data.stereo3dPreviewCmb.getSelectedItem());
        enableStereo3dUI();
        owner.refreshFlameImage(true, false, 1, true, false);
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
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void camPosXSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.camPosXSlider, data.camPosXREd, "camPosX", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void camPosYSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.camPosYSlider, data.camPosYREd, "camPosY", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void camPosZSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.camPosZSlider, data.camPosZREd, "camPosZ", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void camPosXREd_changed() {
    flameTextFieldChanged(data.camPosXSlider, data.camPosXREd, "camPosX", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void camPosYREd_changed() {
    flameTextFieldChanged(data.camPosYSlider, data.camPosYREd, "camPosY", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void camPosZREd_changed() {
    flameTextFieldChanged(data.camPosZSlider, data.camPosZREd, "camPosZ", TinaController.SLIDER_SCALE_CENTRE, false);
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
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void dofDOFScaleSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFScaleSlider, data.dofDOFScaleREd, "camDOFScale", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFScaleREd_changed() {
    flameTextFieldChanged(data.dofDOFScaleSlider, data.dofDOFScaleREd, "camDOFScale", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFAngleSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFAngleSlider, data.dofDOFAngleREd, "camDOFAngle", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFAngleREd_changed() {
    flameTextFieldChanged(data.dofDOFAngleSlider, data.dofDOFAngleREd, "camDOFAngle", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFFadeSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFFadeSlider, data.dofDOFFadeREd, "camDOFFade", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFFadeREd_changed() {
    flameTextFieldChanged(data.dofDOFFadeSlider, data.dofDOFFadeREd, "camDOFFade", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam1Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam1Slider, data.dofDOFParam1REd, "camDOFParam1", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam1REd_changed() {
    flameTextFieldChanged(data.dofDOFParam1Slider, data.dofDOFParam1REd, "camDOFParam1", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam2Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam2Slider, data.dofDOFParam2REd, "camDOFParam2", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam2REd_changed() {
    flameTextFieldChanged(data.dofDOFParam2Slider, data.dofDOFParam2REd, "camDOFParam2", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam3Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam3Slider, data.dofDOFParam3REd, "camDOFParam3", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam3REd_changed() {
    flameTextFieldChanged(data.dofDOFParam3Slider, data.dofDOFParam3REd, "camDOFParam3", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam4Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam4Slider, data.dofDOFParam4REd, "camDOFParam4", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam4REd_changed() {
    flameTextFieldChanged(data.dofDOFParam4Slider, data.dofDOFParam4REd, "camDOFParam4", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam5Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam5Slider, data.dofDOFParam5REd, "camDOFParam5", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam5REd_changed() {
    flameTextFieldChanged(data.dofDOFParam5Slider, data.dofDOFParam5REd, "camDOFParam5", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam6Slider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.dofDOFParam6Slider, data.dofDOFParam6REd, "camDOFParam6", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam6REd_changed() {
    flameTextFieldChanged(data.dofDOFParam6Slider, data.dofDOFParam6REd, "camDOFParam6", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void spatialOversamplingSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaSpatialOversamplingSlider, data.tinaSpatialOversamplingREd, "spatialOversampling", 1.0, false);
    owner.refreshFilterKernelPreviewImg();
  }

  public void spatialOversamplingREd_changed() {
    flameTextFieldChanged(data.tinaSpatialOversamplingSlider, data.tinaSpatialOversamplingREd, "spatialOversampling", 1.0, false);
    owner.refreshFilterKernelPreviewImg();
  }

  public void flameTransparencyCbx_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setBGTransparency(data.bgTransparencyCBx.isSelected());
        enableControls();
      }
    }
  }

  public void postNoiseFilterCheckBox_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setPostNoiseFilter(data.tinaPostNoiseFilterCheckBox.isSelected());
        enableControls();
      }
    }
  }

  public void tinaOptiXDenoiserCheckBox_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setPostOptiXDenoiser(data.tinaOptiXDenoiserCheckBox.isSelected());
        enableControls();
        // owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void filterIndicatorCheckBox_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setSpatialFilterIndicator(data.tinaFilterIndicatorCBx.isSelected());
        owner.refreshFlameImage(true, false, 1, true, true);
      }
    }
  }

  public void postNoiseFilterThresholdSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaPostNoiseThresholdSlider, data.tinaPostNoiseThresholdField, "postNoiseFilterThreshold", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD, false);
  }

  public void postNoiseFilterThresholdREd_changed() {
    flameTextFieldChanged(data.tinaPostNoiseThresholdSlider, data.tinaPostNoiseThresholdField, "postNoiseFilterThreshold", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD, false);
  }

  public void tinaOptiXDenoiserBlendSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaOptiXDenoiserBlendSlider, data.tinaOptiXDenoiserBlendField, "postOptiXDenoiserBlend", TinaController.SLIDER_SCALE_POST_OPTIX_DENOISER_BLEND, true);
  }

  public void tinaOptiXDenoiserBlendField_changed() {
    flameTextFieldChanged(data.tinaOptiXDenoiserBlendSlider, data.tinaOptiXDenoiserBlendField, "postOptiXDenoiserBlend", TinaController.SLIDER_SCALE_POST_OPTIX_DENOISER_BLEND, true);
  }

  public void foregroundOpacitySlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.foregroundOpacitySlider, data.foregroundOpacityField, "foregroundOpacity", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD, true);
  }

  public void foregroundOpacityREd_changed() {
    flameTextFieldChanged(data.foregroundOpacitySlider, data.foregroundOpacityField, "foregroundOpacity", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD, true);
  }

  public void postBlurRadiusREd_changed() {
    flameTextFieldChanged(data.postBlurRadiusSlider, data.postBlurRadiusREd, "postBlurRadius", 1.0, false);
  }

  public void postBlurRadiusSlider_changed() {
    flameSliderChanged(data.postBlurRadiusSlider, data.postBlurRadiusREd, "postBlurRadius", 1.0, false);
  }

  public void zBufferScaleREd_changed() {
    flameTextFieldChanged(data.tinaZBufferScaleSlider, data.tinaZBufferScaleREd, "zBufferScale", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void zBufferScaleSlider_changed() {
    flameSliderChanged(data.tinaZBufferScaleSlider, data.tinaZBufferScaleREd, "zBufferScale", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void zBufferBiasREd_changed() {
    flameTextFieldChanged(data.tinaZBufferBiasSlider, data.tinaZBufferBiasREd, "zBufferBias", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void zBufferBiasSlider_changed() {
    flameSliderChanged(data.tinaZBufferBiasSlider, data.tinaZBufferBiasREd, "zBufferBias", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public ZBufferFilename getZBufferFilenameSetting() {
    if (data.tinaZBufferFilename1.isSelected())
      return ZBufferFilename.PRE_ZBUF;
    else if (data.tinaZBufferFilename2.isSelected())
      return ZBufferFilename.POST_DEPTH;
    else
      return ZBufferFilename.PRE_ZBUF;
  }

  public void zBufferFilename_changed() {
    getCurrFlame().setZBufferFilename(getZBufferFilenameSetting());
  }

  public void postBlurFadeREd_changed() {
    flameTextFieldChanged(data.postBlurFadeSlider, data.postBlurFadeREd, "postBlurFade", TinaController.SLIDER_SCALE_AMBIENT, false);
  }

  public void postBlurFadeSlider_changed() {
    flameSliderChanged(data.postBlurFadeSlider, data.postBlurFadeREd, "postBlurFade", TinaController.SLIDER_SCALE_AMBIENT, false);
  }

  public void postBlurFallOffSlider_changed() {
    flameSliderChanged(data.postBlurFallOffSlider, data.postBlurFallOffREd, "postBlurFallOff", TinaController.SLIDER_SCALE_AMBIENT, false);
  }

  public void postBlurFallOffREd_changed() {
    flameTextFieldChanged(data.postBlurFallOffSlider, data.postBlurFallOffREd, "postBlurFallOff", TinaController.SLIDER_SCALE_AMBIENT, false);
  }

  public void solidRenderingCBx_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.getSolidRenderSettings().setSolidRenderingEnabled(data.solidRenderingToggleBtn.isSelected());
        if (data.solidRenderingToggleBtn.isSelected()) {
          flame.setDefaultSolidRenderingSettings();
          refreshFlameValues();
        }
        enableControls();
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void solidRenderingEnableAOCBx_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.getSolidRenderSettings().setAoEnabled(data.tinaSolidRenderingEnableAOCBx.isSelected());
        enableControls();
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void randomizeLightPosition() {
    if (!isNoRefresh()) {
      DistantLight light = getSolidRenderingSelectedLight();
      if (light != null) {
        owner.saveUndoPoint();
        light.setCastShadows(data.tinaSolidRenderingLightCastShadowsCBx.isSelected());
        light.setAltitude((0.5 - Math.random()) * 60.0);
        light.setAzimuth((0.5 - Math.random()) * 40.0);
        refreshLightPosControls(light);
        owner.refreshFlameImage(true, false, 1, true, !areShadowsEnabled());
      }
    }
  }

  public void solidRenderingLightCastShadowsCBx_changed() {
    if (!isNoRefresh()) {
      DistantLight light = getSolidRenderingSelectedLight();
      if (light != null) {
        owner.saveUndoPoint();
        light.setCastShadows(data.tinaSolidRenderingLightCastShadowsCBx.isSelected());
        enableControls();
        owner.refreshFlameImage(true, false, 1, true, true);
      }
    }
  }

  public void solidRenderingMaterialSpecularColorBtn_clicked() {
    MaterialSettings material = getSolidRenderingSelectedMaterial();
    if (material != null) {
      owner.undoManager.saveUndoPoint(getCurrFlame());

      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");

      Color selectedColor = JColorChooser.showDialog(rootPanel, title, new Color(Tools.roundColor(material.getPhongRed() * GammaCorrectionFilter.COLORSCL), Tools.roundColor(material.getPhongGreen() * GammaCorrectionFilter.COLORSCL), Tools.roundColor(material.getPhongBlue() * GammaCorrectionFilter.COLORSCL)));
      if (selectedColor != null) {
        material.setPhongRed((double) selectedColor.getRed() / GammaCorrectionFilter.COLORSCL);
        material.setPhongGreen((double) selectedColor.getGreen() / GammaCorrectionFilter.COLORSCL);
        material.setPhongBlue((double) selectedColor.getBlue() / GammaCorrectionFilter.COLORSCL);
        owner.refreshFlameImage(true, false, 1, true, true);
        refreshSolidRenderMaterialSpecularColorIndicator();
      }
    }
  }

  public void solidRenderingMaterialSpecularColorBtn_reset() {
    MaterialSettings material = getSolidRenderingSelectedMaterial();
    if (material != null) {
      owner.undoManager.saveUndoPoint(getCurrFlame());
      MaterialSettings newMaterial = new Flame().getSolidRenderSettings().getMaterials().get(0);
      material.setPhongRed(newMaterial.getPhongRed());
      material.setPhongGreen(newMaterial.getPhongGreen());
      material.setPhongBlue(newMaterial.getPhongBlue());
      owner.refreshFlameImage(true, false, 1, true, true);
      refreshSolidRenderMaterialSpecularColorIndicator();
    }
  }

  public void randomizeLightColor() {
    DistantLight light = getSolidRenderingSelectedLight();
    if (light != null) {
      owner.undoManager.saveUndoPoint(getCurrFlame());
      List<RGBColor> rndColors = new AllRandomGradientGenerator().generateKeyFrames(7);
      RGBColor rndColor = rndColors.get((int) (Math.random() * rndColors.size()));
      Color selectedColor = new Color(rndColor.getRed(), rndColor.getGreen(), rndColor.getBlue());
      setLightColor(light, selectedColor);
    }
  }

  public void solidRenderingLightColorBtn_clicked() {
    DistantLight light = getSolidRenderingSelectedLight();
    if (light != null) {
      owner.undoManager.saveUndoPoint(getCurrFlame());

      ResourceManager rm = ResourceManager.all(FilePropertyEditor.class);
      String title = rm.getString("ColorPropertyEditor.title");

      Color selectedColor = JColorChooser.showDialog(rootPanel, title, new Color(Tools.roundColor(light.getRed() * GammaCorrectionFilter.COLORSCL), Tools.roundColor(light.getGreen() * GammaCorrectionFilter.COLORSCL), Tools.roundColor(light.getBlue() * GammaCorrectionFilter.COLORSCL)));
      setLightColor(light, selectedColor);
    }
  }

  private void setLightColor(DistantLight light, Color selectedColor) {
    if (selectedColor != null) {
      light.setRed((double) selectedColor.getRed() / GammaCorrectionFilter.COLORSCL);
      light.setGreen((double) selectedColor.getGreen() / GammaCorrectionFilter.COLORSCL);
      light.setBlue((double) selectedColor.getBlue() / GammaCorrectionFilter.COLORSCL);
      owner.refreshFlameImage(true, false, 1, true, true);
      refreshSolidRenderLightColorIndicator();
    }
  }

  public void solidRenderingSelectedMaterialCmb_changed() {
    refreshSolidRenderingMaterialControls();
  }

  public void solidRenderingSelectedLightCmb_changed() {
    refreshSolidRenderingLightControls();
  }

  public void solidRenderingAddLightBtn_clicked() {
    getCurrFlame().getSolidRenderSettings().addLight();
    setNoRefresh(true);
    try {
      refreshSolidRenderingSelectedLightCmb();
      data.tinaSolidRenderingSelectedLightCmb.setSelectedIndex(getCurrFlame().getSolidRenderSettings().getLights().size() - 1);
    }
    finally {
      setNoRefresh(false);
    }
    refreshSolidRenderingLightControls();
    enableSolidRenderUI();
    owner.refreshFlameImage(true, false, 1, true, true);
  }

  public void solidRenderingDeleteLightBtn_clicked() {
    getCurrFlame().getSolidRenderSettings().getLights().remove(data.tinaSolidRenderingSelectedLightCmb.getSelectedIndex());
    setNoRefresh(true);
    try {
      refreshSolidRenderingSelectedLightCmb();
      if (getCurrFlame().getSolidRenderSettings().getLights().size() > 0) {
        data.tinaSolidRenderingSelectedLightCmb.setSelectedIndex(0);
      }
    }
    finally {
      setNoRefresh(false);
    }
    refreshSolidRenderingLightControls();
    enableSolidRenderUI();
    owner.refreshFlameImage(true, false, 1, true, true);
  }

  public void solidRenderingResetLightsBtn_clicked() {
    setNoRefresh(true);
    try {
      getCurrFlame().getSolidRenderSettings().setupDefaultLights();
      refreshSolidRenderingSelectedLightCmb();
      data.tinaSolidRenderingSelectedLightCmb.setSelectedIndex(0);
    }
    finally {
      setNoRefresh(false);
    }
    refreshSolidRenderingLightControls();
    enableSolidRenderUI();
    owner.refreshFlameImage(true, false, 1, true, true);
  }

  public void solidRenderingResetHardShadowsBtn_clicked() {
    setNoRefresh(true);
    try {
      getCurrFlame().getSolidRenderSettings().setupDefaultHardShadowOptions();
    }
    finally {
      setNoRefresh(false);
    }
    refreshSolidRenderingGlobals();
    enableSolidRenderUI();
    owner.refreshFlameImage(true, false, 1, true, true);
  }

  public void solidRenderingResetAmbientShadowsBtn_clicked() {
    setNoRefresh(true);
    try {
      getCurrFlame().getSolidRenderSettings().setupDefaultAmbientShadowOptions();
    }
    finally {
      setNoRefresh(false);
    }
    refreshSolidRenderingGlobals();
    enableSolidRenderUI();
    owner.refreshFlameImage(true, false, 1, true, true);
  }

  public void solidRenderingAddMaterialBtn_clicked() {
    getCurrFlame().getSolidRenderSettings().addMaterial();
    setNoRefresh(true);
    try {
      refreshSolidRenderingSelectedMaterialCmb();
      data.tinaSolidRenderingSelectedMaterialCmb.setSelectedIndex(getCurrFlame().getSolidRenderSettings().getMaterials().size() - 1);
    }
    finally {
      setNoRefresh(false);
    }
    refreshSolidRenderingMaterialControls();
    enableSolidRenderUI();
    owner.refreshFlameImage(true, false, 1, true, true);
  }

  public void solidRenderingDeleteMaterialBtn_clicked() {
    getCurrFlame().getSolidRenderSettings().getMaterials().remove(data.tinaSolidRenderingSelectedMaterialCmb.getSelectedIndex());
    setNoRefresh(true);
    try {
      refreshSolidRenderingSelectedMaterialCmb();
      if (getCurrFlame().getSolidRenderSettings().getMaterials().size() > 0) {
        data.tinaSolidRenderingSelectedMaterialCmb.setSelectedIndex(0);
      }
    }
    finally {
      setNoRefresh(false);
    }
    refreshSolidRenderingMaterialControls();
    enableSolidRenderUI();
    owner.refreshFlameImage(true, false, 1, true, true);
  }

  public void solidRenderingResetMaterialsBtn_clicked() {
    setNoRefresh(true);
    try {
      getCurrFlame().getSolidRenderSettings().setupDefaultMaterials();
      refreshSolidRenderingSelectedMaterialCmb();
      data.tinaSolidRenderingSelectedMaterialCmb.setSelectedIndex(0);
    }
    finally {
      setNoRefresh(false);
    }
    refreshSolidRenderingMaterialControls();
    enableSolidRenderUI();
    owner.refreshFlameImage(true, false, 1, true, true);
  }

  public void solidRenderingMaterialDiffuseResponseCmb_changed() {
    if (!isNoRefresh()) {
      MaterialSettings material = getSolidRenderingSelectedMaterial();
      if (material != null) {
        owner.saveUndoPoint();
        material.setLightDiffFunc((LightDiffFunc) data.tinaSolidRenderingMaterialDiffuseResponseCmb.getSelectedItem());
        owner.refreshFlameImage(true, false, 1, true, true);
      }
    }
  }

  public void solidRenderingMaterialDiffuseResponseCmb_reset() {
    if (!isNoRefresh()) {
      data.tinaSolidRenderingMaterialDiffuseResponseCmb.setSelectedItem(new Flame().getSolidRenderSettings().getMaterials().get(0).getLightDiffFunc());
      solidRenderingMaterialDiffuseResponseCmb_changed();
    }
  }

  public void solidRenderingMaterialReflectionMappingCmb_changed() {
    if (!isNoRefresh()) {
      MaterialSettings material = getSolidRenderingSelectedMaterial();
      if (material != null) {
        owner.saveUndoPoint();
        material.setReflectionMapping((ReflectionMapping) data.tinaSolidRenderingMaterialReflectionMappingCmb.getSelectedItem());
        owner.refreshFlameImage(true, false, 1, true, true);
      }
    }
  }

  public void solidRenderingMaterialReflectionMappingCmb_reset() {
    if (!isNoRefresh()) {
      data.tinaSolidRenderingMaterialReflectionMappingCmb.setSelectedItem(new Flame().getSolidRenderSettings().getMaterials().get(0).getReflectionMapping());
      solidRenderingMaterialReflectionMappingCmb_changed();
    }
  }

  private void solidRenderingTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    setNoRefresh(true);
    try {
      owner.getFrameControlsUtil().valueChangedByTextField(getCurrFlame().getSolidRenderSettings(), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private void solidRenderingTextFieldReset(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    setNoRefresh(true);
    try {
      pTextField.setValue(owner.getFrameControlsUtil().getRawPropertyValue(new Flame().getSolidRenderSettings(), pProperty));
      owner.getFrameControlsUtil().valueChangedByTextField(getCurrFlame().getSolidRenderSettings(), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private void solidRenderingSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    setNoRefresh(true);
    try {
      owner.getFrameControlsUtil().valueChangedBySlider(getCurrFlame().getSolidRenderSettings(), pSlider, pTextField, pProperty, pSliderScale);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private void solidRenderingMaterialTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    int idx = getRenderSettingsMaterialIdx();
    if (idx < 0 || idx >= getCurrFlame().getSolidRenderSettings().getMaterials().size())
      return;
    setNoRefresh(true);
    try {
      owner.getFrameControlsUtil().valueChangedByTextField(getCurrFlame().getSolidRenderSettings().getMaterials().get(idx), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private void solidRenderingMaterialTextFieldReset(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    int idx = getRenderSettingsMaterialIdx();
    if (idx < 0 || idx >= getCurrFlame().getSolidRenderSettings().getMaterials().size())
      return;
    setNoRefresh(true);
    try {
      pTextField.setValue(owner.getFrameControlsUtil().getRawPropertyValue(new Flame().getSolidRenderSettings().getMaterials().get(0), pProperty));
      owner.getFrameControlsUtil().valueChangedByTextField(getCurrFlame().getSolidRenderSettings().getMaterials().get(idx), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private void solidRenderingMaterialSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    int idx = getRenderSettingsMaterialIdx();
    if (idx < 0 || idx >= getCurrFlame().getSolidRenderSettings().getMaterials().size())
      return;
    setNoRefresh(true);
    try {
      owner.getFrameControlsUtil().valueChangedBySlider(getCurrFlame().getSolidRenderSettings().getMaterials().get(idx), pSlider, pTextField, pProperty, pSliderScale);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private void solidRenderingLightTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    int idx = getRenderSettingsLightIdx();
    if (idx < 0 || idx >= getCurrFlame().getSolidRenderSettings().getLights().size())
      return;
    setNoRefresh(true);
    try {
      owner.getFrameControlsUtil().valueChangedByTextField(getCurrFlame().getSolidRenderSettings().getLights().get(idx), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private void solidRenderingLightTextFieldReset(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    int idx = getRenderSettingsLightIdx();
    if (idx < 0 || idx >= getCurrFlame().getSolidRenderSettings().getLights().size())
      return;
    setNoRefresh(true);
    try {
      pTextField.setValue(owner.getFrameControlsUtil().getRawPropertyValue(new Flame().getSolidRenderSettings().getLights().get(0), pProperty));
      owner.getFrameControlsUtil().valueChangedByTextField(getCurrFlame().getSolidRenderSettings().getLights().get(idx), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private int getRenderSettingsLightIdx() {
    return data.tinaSolidRenderingSelectedLightCmb.getSelectedIndex();
  }

  private void solidRenderingLightSliderChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    int idx = getRenderSettingsLightIdx();
    if (idx < 0 || idx >= getCurrFlame().getSolidRenderSettings().getLights().size())
      return;
    setNoRefresh(true);
    try {
      owner.getFrameControlsUtil().valueChangedBySlider(getCurrFlame().getSolidRenderSettings().getLights().get(idx), pSlider, pTextField, pProperty, pSliderScale);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private int getRenderSettingsMaterialIdx() {
    return data.tinaSolidRenderingSelectedMaterialCmb.getSelectedIndex();
  }

  public void solidRenderingMaterialDiffuseREd_changed() {
    solidRenderingMaterialTextFieldChanged(data.tinaSolidRenderingMaterialDiffuseSlider, data.tinaSolidRenderingMaterialDiffuseREd, "diffuse", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingAOIntensityREd_changed() {
    solidRenderingTextFieldChanged(data.tinaSolidRenderingAOIntensitySlider, data.tinaSolidRenderingAOIntensityREd, "aoIntensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingAOSearchRadiusREd_changed() {
    solidRenderingTextFieldChanged(data.tinaSolidRenderingAOSearchRadiusSlider, data.tinaSolidRenderingAOSearchRadiusREd, "aoSearchRadius", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAOBlurRadiusREd_changed() {
    solidRenderingTextFieldChanged(data.tinaSolidRenderingAOBlurRadiusSlider, data.tinaSolidRenderingAOBlurRadiusREd, "aoBlurRadius", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAOFalloffREd_changed() {
    solidRenderingTextFieldChanged(data.tinaSolidRenderingAOFalloffSlider, data.tinaSolidRenderingAOFalloffREd, "aoFalloff", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAORadiusSamplesREd_changed() {
    solidRenderingTextFieldChanged(data.tinaSolidRenderingAORadiusSamplesSlider, data.tinaSolidRenderingAORadiusSamplesREd, "aoRadiusSamples", 1.0, false);
  }

  public void solidRenderingAOAzimuthSamplesREd_changed() {
    solidRenderingTextFieldChanged(data.tinaSolidRenderingAOAzimuthSamplesSlider, data.tinaSolidRenderingAOAzimuthSamplesREd, "aoAzimuthSamples", 1.0, false);
  }

  public void solidRenderingAOAffectDiffuseREd_changed() {
    solidRenderingTextFieldChanged(data.tinaSolidRenderingAOAffectDiffuseSlider, data.tinaSolidRenderingAOAffectDiffuseREd, "aoAffectDiffuse", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingMaterialAmbientREd_changed() {
    solidRenderingMaterialTextFieldChanged(data.tinaSolidRenderingMaterialAmbientSlider, data.tinaSolidRenderingMaterialAmbientREd, "ambient", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialSpecularREd_changed() {
    solidRenderingMaterialTextFieldChanged(data.tinaSolidRenderingMaterialSpecularSlider, data.tinaSolidRenderingMaterialSpecularREd, "phong", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialSpecularSharpnessREd_changed() {
    solidRenderingMaterialTextFieldChanged(data.tinaSolidRenderingMaterialSpecularSharpnessSlider, data.tinaSolidRenderingMaterialSpecularSharpnessREd, "phongSize", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingLightAltitudeREd_changed() {
    solidRenderingLightTextFieldChanged(data.tinaSolidRenderingLightAltitudeSlider, data.tinaSolidRenderingLightAltitudeREd, "altitude", TinaController.SLIDER_SCALE_CENTRE, !areShadowsEnabled());
  }

  public void solidRenderingLightAzimuthREd_changed() {
    solidRenderingLightTextFieldChanged(data.tinaSolidRenderingLightAzimuthSlider, data.tinaSolidRenderingLightAzimuthREd, "azimuth", TinaController.SLIDER_SCALE_CENTRE, !areShadowsEnabled());
  }

  public void solidRenderingLightIntensityREd_changed() {
    solidRenderingLightTextFieldChanged(data.tinaSolidRenderingLightIntensitySlider, data.tinaSolidRenderingLightIntensityREd, "intensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingShadowIntensityREd_changed() {
    solidRenderingLightTextFieldChanged(data.tinaSolidRenderingShadowIntensitySlider, data.tinaSolidRenderingShadowIntensityREd, "shadowIntensity", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAOIntensitySlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.tinaSolidRenderingAOIntensitySlider, data.tinaSolidRenderingAOIntensityREd, "aoIntensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingAOAffectDiffuseSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.tinaSolidRenderingAOAffectDiffuseSlider, data.tinaSolidRenderingAOAffectDiffuseREd, "aoAffectDiffuse", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAOSearchRadiusSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.tinaSolidRenderingAOSearchRadiusSlider, data.tinaSolidRenderingAOSearchRadiusREd, "aoSearchRadius", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAOBlurRadiusSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.tinaSolidRenderingAOBlurRadiusSlider, data.tinaSolidRenderingAOBlurRadiusREd, "aoBlurRadius", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAOFalloffSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.tinaSolidRenderingAOFalloffSlider, data.tinaSolidRenderingAOFalloffREd, "aoFalloff", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAORadiusSamplesSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.tinaSolidRenderingAORadiusSamplesSlider, data.tinaSolidRenderingAORadiusSamplesREd, "aoRadiusSamples", 1.0, false);
  }

  public void solidRenderingAOAzimuthSamplesSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.tinaSolidRenderingAOAzimuthSamplesSlider, data.tinaSolidRenderingAOAzimuthSamplesREd, "aoAzimuthSamples", 1.0, false);
  }

  public void solidRenderingMaterialDiffuseSlider_stateChanged(ChangeEvent e) {
    solidRenderingMaterialSliderChanged(data.tinaSolidRenderingMaterialDiffuseSlider, data.tinaSolidRenderingMaterialDiffuseREd, "diffuse", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialAmbientSlider_stateChanged(ChangeEvent e) {
    solidRenderingMaterialSliderChanged(data.tinaSolidRenderingMaterialAmbientSlider, data.tinaSolidRenderingMaterialAmbientREd, "ambient", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialSpecularSlider_stateChanged(ChangeEvent e) {
    solidRenderingMaterialSliderChanged(data.tinaSolidRenderingMaterialSpecularSlider, data.tinaSolidRenderingMaterialSpecularREd, "phong", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialSpecularSharpnessSlider_stateChanged(ChangeEvent e) {
    solidRenderingMaterialSliderChanged(data.tinaSolidRenderingMaterialSpecularSharpnessSlider, data.tinaSolidRenderingMaterialSpecularSharpnessREd, "phongSize", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingLightAltitudeSlider_stateChanged(ChangeEvent e) {
    solidRenderingLightSliderChanged(data.tinaSolidRenderingLightAltitudeSlider, data.tinaSolidRenderingLightAltitudeREd, "altitude", TinaController.SLIDER_SCALE_CENTRE, !areShadowsEnabled());
  }

  private boolean areShadowsEnabled() {
    Flame flame = getCurrFlame();
    return flame != null && flame.getSolidRenderSettings().isSolidRenderingEnabled() && !ShadowType.OFF.equals(flame.getSolidRenderSettings().getShadowType());
  }

  public void solidRenderingLightAzimuthSlider_stateChanged(ChangeEvent e) {
    solidRenderingLightSliderChanged(data.tinaSolidRenderingLightAzimuthSlider, data.tinaSolidRenderingLightAzimuthREd, "azimuth", TinaController.SLIDER_SCALE_CENTRE, !areShadowsEnabled());
  }

  public void solidRenderingLightIntensitySlider_stateChanged(ChangeEvent e) {
    solidRenderingLightSliderChanged(data.tinaSolidRenderingLightIntensitySlider, data.tinaSolidRenderingLightIntensityREd, "intensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingShadowIntensitySlider_stateChanged(ChangeEvent e) {
    solidRenderingLightSliderChanged(data.tinaSolidRenderingShadowIntensitySlider, data.tinaSolidRenderingShadowIntensityREd, "shadowIntensity", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingMaterialRemoveReflMapBtn_clicked() {
    MaterialSettings material = getSolidRenderingSelectedMaterial();
    if (material != null) {
      owner.saveUndoPoint();
      material.setReflMapFilename(null);
      refreshReflMapColorIndicator();
      owner.refreshFlameImage(true, false, 1, true, true);
    }
  }

  public void solidRenderingMaterialSelectReflMapBtn_clicked() {
    MaterialSettings material = getSolidRenderingSelectedMaterial();
    if (material != null) {
      JFileChooser chooser = new ImageFileChooser(Tools.FILEEXT_PNG);
      if (Prefs.getPrefs().getInputImagePath() != null) {
        try {
          if (getCurrFlame().getBGImageFilename().length() > 0) {
            chooser.setSelectedFile(new File(getCurrFlame().getBGImageFilename()));
          }
          else {
            chooser.setCurrentDirectory(new File(Prefs.getPrefs().getInputImagePath()));
          }
        }
        catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      if (chooser.showOpenDialog(owner.getFlamePanel()) == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();
        try {
          String filename = file.getAbsolutePath();
          WFImage img = RessourceManager.getImage(filename);
          if (img.getImageWidth() < 2 || img.getImageHeight() < 2 || !(img instanceof SimpleImage)) {
            throw new Exception("Invalid image");
          }
          Prefs.getPrefs().setLastInputImageFile(file);

          owner.saveUndoPoint();
          material.setReflMapFilename(filename);

          refreshReflMapColorIndicator();
          owner.refreshFlameImage(true, false, 1, true, false);
        }
        catch (Throwable ex) {
          owner.errorHandler.handleError(ex);
        }
      }
    }
  }

  private void refreshReflMapColorIndicator() {
    MaterialSettings material = getSolidRenderingSelectedMaterial();
    Color color = Color.BLACK;
    if (material != null && material.getReflMapFilename() != null && !material.getReflMapFilename().isEmpty()) {
      try {
        WFImage img = RessourceManager.getImage(material.getReflMapFilename());
        if (img instanceof SimpleImage) {
          SimpleImage sImg = (SimpleImage) img;
          int samples = 6;
          int dx = sImg.getImageWidth() / samples;
          int dy = sImg.getImageHeight() / samples;
          double r = 0.0, g = 0.0, b = 0.0;
          for (int x = 0; x < sImg.getImageWidth(); x += dx) {
            for (int y = 0; y < sImg.getImageHeight(); y += dy) {
              r += sImg.getRValue(x, y);
              g += sImg.getGValue(x, y);
              b += sImg.getBValue(x, y);
            }
          }
          color = new Color(Tools.roundColor(r / (samples * samples)), Tools.roundColor(g / (samples * samples)), Tools.roundColor(b / (samples * samples)));
        }
        else {
          color = Color.RED;
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
    data.tinaSolidRenderingMaterialReflMapBtn.setBackground(color);
  }

  public void solidRenderingMaterialReflectionMapIntensityREd_changed() {
    solidRenderingMaterialTextFieldChanged(data.tinaSolidRenderingMaterialReflectionMapIntensitySlider, data.tinaSolidRenderingMaterialReflectionMapIntensityREd, "reflMapIntensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialReflectionMapIntensitySlider_stateChanged(ChangeEvent e) {
    solidRenderingMaterialSliderChanged(data.tinaSolidRenderingMaterialReflectionMapIntensitySlider, data.tinaSolidRenderingMaterialReflectionMapIntensityREd, "reflMapIntensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingShadowTypeCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        SolidRenderSettings settings = flame.getSolidRenderSettings();
        owner.saveUndoPoint();
        settings.setShadowType((ShadowType) data.tinaSolidRenderingShadowTypeCmb.getSelectedItem());
        enableControls();
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void solidRenderingShadowTypeCmb_reset() {
    if (!isNoRefresh()) {
      data.tinaSolidRenderingShadowTypeCmb.setSelectedItem(new Flame().getSolidRenderSettings().getShadowType());
      solidRenderingShadowTypeCmb_changed();
    }
  }

  public void solidRenderingShadowmapSizeCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        SolidRenderSettings settings = flame.getSolidRenderSettings();
        owner.saveUndoPoint();
        settings.setShadowmapSize(Integer.parseInt((String) data.tinaSolidRenderingShadowmapSizeCmb.getSelectedItem()));
        owner.refreshFlameImage(true, false, 1, true, false);
      }
    }
  }

  public void solidRenderingShadowmapSizeCmb_reset() {
    if (!isNoRefresh()) {
      data.tinaSolidRenderingShadowmapSizeCmb.setSelectedItem(String.valueOf(new Flame().getSolidRenderSettings().getShadowmapSize()));
      solidRenderingShadowmapSizeCmb_changed();
    }
  }

  public void solidRenderingShadowSmoothRadiusREd_changed() {
    solidRenderingTextFieldChanged(data.tinaSolidRenderingShadowSmoothRadiusSlider, data.tinaSolidRenderingShadowSmoothRadiusREd, "shadowSmoothRadius", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingShadowmapBiasREd_changed() {
    solidRenderingTextFieldChanged(data.tinaSolidRenderingShadowmapBiasSlider, data.tinaSolidRenderingShadowmapBiasREd, "shadowmapBias", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingShadowmapBiasSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.tinaSolidRenderingShadowmapBiasSlider, data.tinaSolidRenderingShadowmapBiasREd, "shadowmapBias", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingShadowSmoothRadiusSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.tinaSolidRenderingShadowSmoothRadiusSlider, data.tinaSolidRenderingShadowSmoothRadiusREd, "shadowSmoothRadius", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingPostBokehActivationSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.postBokehActivationSlider, data.postBokehActivationREd, "postBokehActivation", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehSizeREd_changed() {
    solidRenderingTextFieldChanged(data.postBokehSizeSlider, data.postBokehSizeREd, "postBokehSize", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehBrightnessSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.postBokehBrightnessSlider, data.postBokehBrightnessREd, "postBokehBrightness", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehIntensityREd_changed() {
    solidRenderingTextFieldChanged(data.postBokehIntensitySlider, data.postBokehIntensityREd, "postBokehIntensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehSizeSlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.postBokehSizeSlider, data.postBokehSizeREd, "postBokehSize", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehIntensitySlider_stateChanged(ChangeEvent e) {
    solidRenderingSliderChanged(data.postBokehIntensitySlider, data.postBokehIntensityREd, "postBokehIntensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehFilterKernelCmb_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        SolidRenderSettings settings = flame.getSolidRenderSettings();
        owner.saveUndoPoint();
        settings.setPostBokehFilterKernel((FilterKernelType) data.postBokehFilterKernelCmb.getSelectedItem());
      }
    }
  }

  public void solidRenderingPostBokehBrightnessREd_changed() {
    solidRenderingTextFieldChanged(data.postBokehBrightnessSlider, data.postBokehBrightnessREd, "postBokehBrightness", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehActivationREd_changed() {
    solidRenderingTextFieldChanged(data.postBokehActivationSlider, data.postBokehActivationREd, "postBokehActivation", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void fillFilterKernelCmb(FilteringType filteringType) {
    data.filterKernelCmb.removeAllItems();
    switch (filteringType) {
      case GLOBAL_SHARPENING:
        for (FilterKernelType kernel : FilterKernelType.getSharpeningFilters()) {
          data.filterKernelCmb.addItem(kernel);
        }
        break;
      case GLOBAL_SMOOTHING:
        for (FilterKernelType kernel : FilterKernelType.getSmoothingFilters()) {
          data.filterKernelCmb.addItem(kernel);
        }
        break;
      case ADAPTIVE:
        for (FilterKernelType kernel : FilterKernelType.getAdapativeFilters()) {
          data.filterKernelCmb.addItem(kernel);
        }
        break;
    }
  }

  private void flameTextFieldReset(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || pTextField == null || !pTextField.isEnabled() || getCurrFlame() == null) {
      return;
    }
    setNoRefresh(true);
    try {
      pTextField.setValue(owner.getFrameControlsUtil().getRawPropertyValue(new Flame(), pProperty));
      owner.getFrameControlsUtil().valueChangedByTextField(getCurrFlame(), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  private void camDOFParamFieldReset(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || pTextField == null || !pTextField.isEnabled() || getCurrFlame() == null) {
      return;
    }
    setNoRefresh(true);
    try {
      Flame newFlame = new Flame();
      newFlame.setCamDOFShape((DOFBlurShapeType) data.dofDOFShapeCmb.getSelectedItem());
      newFlame.getCamDOFShape().getDOFBlurShape().setDefaultParams(newFlame);
      pTextField.setValue(owner.getFrameControlsUtil().getRawPropertyValue(newFlame, pProperty));
      owner.getFrameControlsUtil().valueChangedByTextField(getCurrFlame(), pSlider, pTextField, pProperty, pSliderScale, 0.0);
      owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
    }
    finally {
      setNoRefresh(false);
    }
  }

  public void cameraRollREd_reset() {
    flameTextFieldReset(data.cameraRollSlider, data.cameraRollREd, "camRoll", 1.0, false);
  }

  public void cameraPitchREd_reset() {
    flameTextFieldReset(data.cameraPitchSlider, data.cameraPitchREd, "camPitch", 1.0, false);
  }

  public void cameraYawREd_reset() {
    flameTextFieldReset(data.cameraYawSlider, data.cameraYawREd, "camYaw", 1.0, false);
  }

  public void cameraBankREd_reset() {
    flameTextFieldReset(data.cameraBankSlider, data.cameraBankREd, "camBank", 1.0, false);
  }

  public void cameraPerspectiveREd_reset() {
    flameTextFieldReset(data.cameraPerspectiveSlider, data.cameraPerspectiveREd, "camPerspective", TinaController.SLIDER_SCALE_PERSPECTIVE, false);
  }

  public void cameraCentreXREd_reset() {
    flameTextFieldReset(data.cameraCentreXSlider, data.cameraCentreXREd, "centreX", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void cameraCentreYREd_reset() {
    flameTextFieldReset(data.cameraCentreYSlider, data.cameraCentreYREd, "centreY", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void cameraZoomREd_reset() {
    flameTextFieldReset(data.cameraZoomSlider, data.cameraZoomREd, "camZoom", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void pixelsPerUnitREd_reset() {
    flameTextFieldReset(data.pixelsPerUnitSlider, data.pixelsPerUnitREd, "pixelsPerUnit", 1.0, false);
  }

  public void camPosXREd_reset() {
    flameTextFieldReset(data.camPosXSlider, data.camPosXREd, "camPosX", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void camPosYREd_reset() {
    flameTextFieldReset(data.camPosYSlider, data.camPosYREd, "camPosY", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void camPosZREd_reset() {
    flameTextFieldReset(data.camPosZSlider, data.camPosZREd, "camPosZ", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void cameraDOFREd_reset() {
    flameTextFieldReset(data.cameraDOFSlider, data.cameraDOFREd, "camDOF", TinaController.SLIDER_SCALE_DOF, false);
  }

  public void cameraDOFAreaREd_reset() {
    flameTextFieldReset(data.cameraDOFAreaSlider, data.cameraDOFAreaREd, "camDOFArea", TinaController.SLIDER_SCALE_DOF_AREA, false);
  }

  public void cameraDOFExponentREd_reset() {
    flameTextFieldReset(data.cameraDOFExponentSlider, data.cameraDOFExponentREd, "camDOFExponent", TinaController.SLIDER_SCALE_DOF_EXPONENT, false);
  }

  public void camZREd_reset() {
    flameTextFieldReset(data.camZSlider, data.camZREd, "camZ", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void focusXREd_reset() {
    flameTextFieldReset(data.focusXSlider, data.focusXREd, "focusX", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void focusYREd_reset() {
    flameTextFieldReset(data.focusYSlider, data.focusYREd, "focusY", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void focusZREd_reset() {
    flameTextFieldReset(data.focusZSlider, data.focusZREd, "focusZ", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void diminishZREd_reset() {
    flameTextFieldReset(data.dimishZSlider, data.dimishZREd, "dimishZ", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void dimishZColorBtn_reset() {
    Flame newFlame = new Flame();
    getCurrFlame().setDimishZRed(newFlame.getDimishZRed());
    getCurrFlame().setDimishZGreen(newFlame.getDimishZGreen());
    getCurrFlame().setDimishZBlue(newFlame.getDimishZBlue());
    refreshDimishZColorIndicator();
    owner.refreshFlameImage(true, false, 1, true, false);
  }

  public void dimZDistanceREd_reset() {
    flameTextFieldReset(data.dimZDistanceSlider, data.dimZDistanceREd, "dimZDistance", TinaController.SLIDER_SCALE_ZPOS, false);
  }

  public void brightnessREd_reset() {
    flameTextFieldReset(data.brightnessSlider, data.brightnessREd, "brightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void lowDensityBrightnessREd_reset() {
    flameTextFieldReset(data.lowDensityBrightnessSlider, data.lowDensityBrightnessREd, "lowDensityBrightness", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void gammaREd_reset() {
    flameTextFieldReset(data.gammaSlider, data.gammaREd, "gamma", TinaController.SLIDER_SCALE_GAMMA, true);
  }

  public void gammaThresholdREd_reset() {
    flameTextFieldReset(data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, true);
  }

  public void contrastREd_reset() {
    flameTextFieldReset(data.contrastSlider, data.contrastREd, "contrast", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void balanceRedREd_reset() {
    flameTextFieldReset(data.balanceRedSlider, data.balanceRedREd, "balanceRed", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void balanceGreenREd_reset() {
    flameTextFieldReset(data.balanceGreenSlider, data.balanceGreenREd, "balanceGreen", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void balanceBlueREd_reset() {
    flameTextFieldReset(data.balanceBlueSlider, data.balanceBlueREd, "balanceBlue", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void whiteLevelREd_reset() {
    flameTextFieldReset(data.whiteLevelSlider, data.whiteLevelREd, "whiteLevel", 1.0, true);
  }

  public void vibrancyREd_reset() {
    flameTextFieldReset(data.vibrancySlider, data.vibrancyREd, "vibrancy", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void saturationREd_reset() {
    flameTextFieldReset(data.saturationSlider, data.saturationREd, "saturation", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, true);
  }

  public void foregroundOpacityREd_reset() {
    flameTextFieldReset(data.foregroundOpacitySlider, data.foregroundOpacityField, "foregroundOpacity", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD, true);
  }

  public void spatialOversamplingREd_reset() {
    flameTextFieldReset(data.tinaSpatialOversamplingSlider, data.tinaSpatialOversamplingREd, "spatialOversampling", 1.0, false);
    owner.refreshFilterKernelPreviewImg();
  }

  public void filterRadiusREd_reset() {
    flameTextFieldReset(data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
    owner.refreshFilterKernelPreviewImg();
  }

  public void filterSharpnessREd_reset() {
    flameTextFieldReset(data.tinaFilterSharpnessSlider, data.tinaFilterSharpnessREd, "spatialFilterSharpness", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
  }

  public void filterLowDensityREd_reset() {
    flameTextFieldReset(data.tinaFilterLowDensitySlider, data.tinaFilterLowDensityREd, "spatialFilterLowDensity", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
  }

  public void xFormAntialiasAmountREd_reset() {
    flameTextFieldReset(data.xFormAntialiasAmountSlider, data.xFormAntialiasAmountREd, "antialiasAmount", TinaController.SLIDER_SCALE_COLOR, false);
  }

  public void xFormAntialiasRadiusREd_reset() {
    flameTextFieldReset(data.xFormAntialiasRadiusSlider, data.xFormAntialiasRadiusREd, "antialiasRadius", TinaController.SLIDER_SCALE_COLOR, false);
  }

  public void tinaOptiXDenoiserBlendField_reset() {
    flameTextFieldReset(data.tinaOptiXDenoiserBlendSlider, data.tinaOptiXDenoiserBlendField, "postOptiXDenoiserBlend", TinaController.SLIDER_SCALE_POST_OPTIX_DENOISER_BLEND, true);
  }

  public void postNoiseFilterThresholdREd_reset() {
    flameTextFieldReset(data.tinaPostNoiseThresholdSlider, data.tinaPostNoiseThresholdField, "postNoiseFilterThreshold", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD, false);
  }

  public void solidRenderingAOIntensityREd_reset() {
    solidRenderingTextFieldReset(data.tinaSolidRenderingAOIntensitySlider, data.tinaSolidRenderingAOIntensityREd, "aoIntensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingAOSearchRadiusREd_reset() {
    solidRenderingTextFieldReset(data.tinaSolidRenderingAOSearchRadiusSlider, data.tinaSolidRenderingAOSearchRadiusREd, "aoSearchRadius", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAOBlurRadiusREd_reset() {
    solidRenderingTextFieldReset(data.tinaSolidRenderingAOBlurRadiusSlider, data.tinaSolidRenderingAOBlurRadiusREd, "aoBlurRadius", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAOFalloffREd_reset() {
    solidRenderingTextFieldReset(data.tinaSolidRenderingAOFalloffSlider, data.tinaSolidRenderingAOFalloffREd, "aoFalloff", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingAORadiusSamplesREd_reset() {
    solidRenderingTextFieldReset(data.tinaSolidRenderingAORadiusSamplesSlider, data.tinaSolidRenderingAORadiusSamplesREd, "aoRadiusSamples", 1.0, false);
  }

  public void solidRenderingAOAzimuthSamplesREd_reset() {
    solidRenderingTextFieldReset(data.tinaSolidRenderingAOAzimuthSamplesSlider, data.tinaSolidRenderingAOAzimuthSamplesREd, "aoAzimuthSamples", 1.0, false);
  }

  public void solidRenderingAOAffectDiffuseREd_reset() {
    solidRenderingTextFieldReset(data.tinaSolidRenderingAOAffectDiffuseSlider, data.tinaSolidRenderingAOAffectDiffuseREd, "aoAffectDiffuse", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingShadowSmoothRadiusREd_reset() {
    solidRenderingTextFieldReset(data.tinaSolidRenderingShadowSmoothRadiusSlider, data.tinaSolidRenderingShadowSmoothRadiusREd, "shadowSmoothRadius", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingShadowmapBiasREd_reset() {
    solidRenderingTextFieldReset(data.tinaSolidRenderingShadowmapBiasSlider, data.tinaSolidRenderingShadowmapBiasREd, "shadowmapBias", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void solidRenderingMaterialDiffuseREd_reset() {
    solidRenderingMaterialTextFieldReset(data.tinaSolidRenderingMaterialDiffuseSlider, data.tinaSolidRenderingMaterialDiffuseREd, "diffuse", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialAmbientREd_reset() {
    solidRenderingMaterialTextFieldReset(data.tinaSolidRenderingMaterialAmbientSlider, data.tinaSolidRenderingMaterialAmbientREd, "ambient", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialSpecularREd_reset() {
    solidRenderingMaterialTextFieldReset(data.tinaSolidRenderingMaterialSpecularSlider, data.tinaSolidRenderingMaterialSpecularREd, "phong", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialSpecularSharpnessREd_reset() {
    solidRenderingMaterialTextFieldReset(data.tinaSolidRenderingMaterialSpecularSharpnessSlider, data.tinaSolidRenderingMaterialSpecularSharpnessREd, "phongSize", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingMaterialRemoveReflMapBtn_reset() {
    solidRenderingMaterialRemoveReflMapBtn_clicked();
  }

  public void solidRenderingMaterialReflectionMapIntensityREd_reset() {
    solidRenderingMaterialTextFieldReset(data.tinaSolidRenderingMaterialReflectionMapIntensitySlider, data.tinaSolidRenderingMaterialReflectionMapIntensityREd, "reflMapIntensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingLightAltitudeREd_reset() {
    solidRenderingLightTextFieldReset(data.tinaSolidRenderingLightAltitudeSlider, data.tinaSolidRenderingLightAltitudeREd, "altitude", TinaController.SLIDER_SCALE_CENTRE, !areShadowsEnabled());
  }

  public void solidRenderingLightAzimuthREd_reset() {
    solidRenderingLightTextFieldReset(data.tinaSolidRenderingLightAzimuthSlider, data.tinaSolidRenderingLightAzimuthREd, "azimuth", TinaController.SLIDER_SCALE_CENTRE, !areShadowsEnabled());
  }

  public void solidRenderingLightColorBtn_reset() {
    DistantLight light = getSolidRenderingSelectedLight();
    if (light != null) {
      owner.undoManager.saveUndoPoint(getCurrFlame());
      DistantLight newLight = new Flame().getSolidRenderSettings().getLights().get(0);

      Color selectedColor = new Color(Tools.roundColor(newLight.getRed() * GammaCorrectionFilter.COLORSCL), Tools.roundColor(newLight.getGreen() * GammaCorrectionFilter.COLORSCL), Tools.roundColor(newLight.getBlue() * GammaCorrectionFilter.COLORSCL));
      setLightColor(light, selectedColor);
    }
  }

  public void solidRenderingLightIntensityREd_reset() {
    solidRenderingLightTextFieldReset(data.tinaSolidRenderingLightIntensitySlider, data.tinaSolidRenderingLightIntensityREd, "intensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingShadowIntensityREd_reset() {
    solidRenderingLightTextFieldReset(data.tinaSolidRenderingShadowIntensitySlider, data.tinaSolidRenderingShadowIntensityREd, "shadowIntensity", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void postSymmetryCmb_reset() {
    data.postSymmetryTypeCmb.setSelectedItem(new Flame().getPostSymmetryType());
    postSymmetryCmb_changed();
  }

  public void postSymmetryDistanceREd_reset() {
    flameTextFieldReset(data.postSymmetryDistanceSlider, data.postSymmetryDistanceREd, "postSymmetryDistance", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void postSymmetryRotationREd_reset() {
    flameTextFieldReset(data.postSymmetryRotationSlider, data.postSymmetryRotationREd, "postSymmetryRotation", 1.0, false);
  }

  public void postSymmetryOrderREd_reset() {
    flameTextFieldReset(data.postSymmetryOrderSlider, data.postSymmetryOrderREd, "postSymmetryOrder", 1.0, false);
  }

  public void postSymmetryCentreXREd_reset() {
    flameTextFieldReset(data.postSymmetryCentreXSlider, data.postSymmetryCentreXREd, "postSymmetryCentreX", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void postSymmetryCentreYREd_reset() {
    flameTextFieldReset(data.postSymmetryCentreYSlider, data.postSymmetryCentreYREd, "postSymmetryCentreY", TinaController.SLIDER_SCALE_CENTRE, false);
  }

  public void motionBlurLengthREd_reset() {
    flameTextFieldReset(data.motionBlurLengthSlider, data.motionBlurLengthField, "motionBlurLength", 1.0, false);
  }

  public void motionBlurTimeStepREd_reset() {
    flameTextFieldReset(data.motionBlurTimeStepSlider, data.motionBlurTimeStepField, "motionBlurTimeStep", TinaController.SLIDER_SCALE_COLOR, false);
  }

  public void motionBlurDecayREd_reset() {
    flameTextFieldReset(data.motionBlurDecaySlider, data.motionBlurDecayField, "motionBlurDecay", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void flameFPSField_reset() {
    flameTextFieldReset(null, data.flameFPSField, "fps", 1.0, false);
  }

  public void dofDOFShapeCmb_reset() {
    data.dofDOFShapeCmb.setSelectedItem(new Flame().getCamDOFShape());
    dofDOFShapeCmb_changed();
  }

  public void dofDOFScaleREd_reset() {
    camDOFParamFieldReset(data.dofDOFScaleSlider, data.dofDOFScaleREd, "camDOFScale", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFAngleREd_reset() {
    camDOFParamFieldReset(data.dofDOFAngleSlider, data.dofDOFAngleREd, "camDOFAngle", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFFadeREd_reset() {
    camDOFParamFieldReset(data.dofDOFFadeSlider, data.dofDOFFadeREd, "camDOFFade", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam1REd_reset() {
    camDOFParamFieldReset(data.dofDOFParam1Slider, data.dofDOFParam1REd, "camDOFParam1", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam2REd_reset() {
    camDOFParamFieldReset(data.dofDOFParam2Slider, data.dofDOFParam2REd, "camDOFParam2", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam3REd_reset() {
    camDOFParamFieldReset(data.dofDOFParam3Slider, data.dofDOFParam3REd, "camDOFParam3", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam4REd_reset() {
    camDOFParamFieldReset(data.dofDOFParam4Slider, data.dofDOFParam4REd, "camDOFParam4", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam5REd_reset() {
    camDOFParamFieldReset(data.dofDOFParam5Slider, data.dofDOFParam5REd, "camDOFParam5", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void dofDOFParam6REd_reset() {
    camDOFParamFieldReset(data.dofDOFParam6Slider, data.dofDOFParam6REd, "camDOFParam6", TinaController.SLIDER_SCALE_ZOOM, false);
  }

  public void solidRenderingPostBokehIntensityREd_reset() {
    solidRenderingTextFieldReset(data.postBokehIntensitySlider, data.postBokehIntensityREd, "postBokehIntensity", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehSizeREd_reset() {
    solidRenderingTextFieldReset(data.postBokehSizeSlider, data.postBokehSizeREd, "postBokehSize", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehFilterKernelCmb_reset() {
    data.postBokehFilterKernelCmb.setSelectedItem(new Flame().getSolidRenderSettings().getPostBokehFilterKernel());
    solidRenderingPostBokehFilterKernelCmb_changed();
  }

  public void solidRenderingPostBokehBrightnessREd_reset() {
    solidRenderingTextFieldReset(data.postBokehBrightnessSlider, data.postBokehBrightnessREd, "postBokehBrightness", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void solidRenderingPostBokehActivationREd_reset() {
    solidRenderingTextFieldReset(data.postBokehActivationSlider, data.postBokehActivationREd, "postBokehActivation", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void postBlurRadiusREd_reset() {
    flameTextFieldReset(data.postBlurRadiusSlider, data.postBlurRadiusREd, "postBlurRadius", 1.0, false);
  }

  public void postBlurFadeREd_reset() {
    flameTextFieldReset(data.postBlurFadeSlider, data.postBlurFadeREd, "postBlurFade", TinaController.SLIDER_SCALE_AMBIENT, false);
  }

  public void postBlurFallOffREd_reset() {
    flameTextFieldReset(data.postBlurFallOffSlider, data.postBlurFallOffREd, "postBlurFallOff", TinaController.SLIDER_SCALE_AMBIENT, false);
  }

  public void zBufferScaleREd_reset() {
    flameTextFieldReset(data.tinaZBufferScaleSlider, data.tinaZBufferScaleREd, "zBufferScale", TinaController.SLIDER_SCALE_CENTRE, true);
  }

  public void zBufferBiasREd_reset() {
    flameTextFieldReset(data.tinaZBufferBiasSlider, data.tinaZBufferBiasREd, "zBufferBias", TinaController.SLIDER_SCALE_CENTRE, true);
  }

}
