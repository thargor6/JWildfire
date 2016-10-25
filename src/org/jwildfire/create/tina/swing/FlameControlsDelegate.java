/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
import java.lang.reflect.Field;
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
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.PostSymmetryType;
import org.jwildfire.create.tina.base.Stereo3dColor;
import org.jwildfire.create.tina.base.Stereo3dMode;
import org.jwildfire.create.tina.base.Stereo3dPreview;
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
    flameSliderChanged(data.vibrancySlider, data.vibrancyREd, "vibrancy", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, false);
  }

  public void saturationSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.saturationSlider, data.saturationREd, "saturation", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, false);
  }

  public void filterRadiusSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.filterRadiusSlider, data.filterRadiusREd, "spatialFilterRadius", TinaController.SLIDER_SCALE_FILTER_RADIUS, false);
    owner.refreshFilterKernelPreviewImg();
  }

  public void gammaThresholdSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.gammaThresholdSlider, data.gammaThresholdREd, "gammaThreshold", TinaController.SLIDER_SCALE_GAMMA_THRESHOLD, true);
  }

  public void vibrancyREd_changed() {
    flameTextFieldChanged(data.vibrancySlider, data.vibrancyREd, "vibrancy", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, false);
  }

  public void saturationREd_changed() {
    flameTextFieldChanged(data.saturationSlider, data.saturationREd, "saturation", TinaController.SLIDER_SCALE_BRIGHTNESS_CONTRAST_VIBRANCY, false);
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

  public void gammaREd_changed() {
    flameTextFieldChanged(data.gammaSlider, data.gammaREd, "gamma", TinaController.SLIDER_SCALE_GAMMA, false);
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
    flameSliderChanged(data.gammaSlider, data.gammaREd, "gamma", TinaController.SLIDER_SCALE_GAMMA, false);
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

  public void refreshFlameValues() {
    boolean oldNoRefrsh = isNoRefresh();
    setNoRefresh(true);
    try {
      refreshSolidRenderingSelectedLightCmb();
      refreshSolidRenderingSelectedMaterialCmb();

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

      data.xFormAntialiasAmountREd.setText(Tools.doubleToString(getCurrFlame().getAntialiasAmount()));
      data.xFormAntialiasAmountSlider.setValue(Tools.FTOI(getCurrFlame().getAntialiasAmount() * TinaController.SLIDER_SCALE_COLOR));
      data.xFormAntialiasRadiusREd.setText(Tools.doubleToString(getCurrFlame().getAntialiasRadius()));
      data.xFormAntialiasRadiusSlider.setValue(Tools.FTOI(getCurrFlame().getAntialiasRadius() * TinaController.SLIDER_SCALE_COLOR));

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

      data.postBlurRadiusSlider.setValue(Tools.FTOI(getCurrFlame().getPostBlurRadius()));
      data.postBlurRadiusREd.setText(Tools.doubleToString(getCurrFlame().getPostBlurRadius()));
      data.postBlurFadeSlider.setValue(Tools.FTOI(getCurrFlame().getPostBlurFade() * TinaController.SLIDER_SCALE_AMBIENT));
      data.postBlurFadeREd.setText(Tools.doubleToString(getCurrFlame().getPostBlurFade()));
      data.postBlurFallOffSlider.setValue(Tools.FTOI(getCurrFlame().getPostBlurFallOff() * TinaController.SLIDER_SCALE_AMBIENT));
      data.postBlurFallOffREd.setText(Tools.doubleToString(getCurrFlame().getPostBlurFallOff()));

      data.tinaZBufferScaleREd.setText(Tools.doubleToString(getCurrFlame().getZBufferScale()));
      data.tinaZBufferScaleSlider.setValue(Tools.FTOI(getCurrFlame().getZBufferScale() * TinaController.SLIDER_SCALE_CENTRE));

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
      ((JTabbedPane) data.bokehSettingsPnl.getParent()).setEnabledAt(1, !settings.isSolidRenderingEnabled());
      ((JTabbedPane) data.bokehSettingsPnl.getParent()).setEnabledAt(2, settings.isSolidRenderingEnabled());
    }
  }

  private void refreshSolidRenderingGlobals() {
    enableBokehPanels();
    SolidRenderSettings settings = getCurrFlame().getSolidRenderSettings();
    data.solidRenderingToggleBtn.setSelected(settings.isSolidRenderingEnabled());
    data.tinaSolidRenderingEnableAOCBx.setSelected(settings.isAoEnabled());
    data.tinaSolidRenderingAOIntensityREd.setText(Tools.doubleToString(settings.getAoIntensity()));
    data.tinaSolidRenderingAOIntensitySlider.setValue(Tools.FTOI(settings.getAoIntensity() * TinaController.SLIDER_SCALE_CENTRE));
    data.tinaSolidRenderingAOSearchRadiusREd.setText(Tools.doubleToString(settings.getAoSearchRadius()));
    data.tinaSolidRenderingAOSearchRadiusSlider.setValue(Tools.FTOI(settings.getAoSearchRadius() * TinaController.SLIDER_SCALE_CENTRE));
    data.tinaSolidRenderingAOBlurRadiusREd.setText(Tools.doubleToString(settings.getAoBlurRadius()));
    data.tinaSolidRenderingAOBlurRadiusSlider.setValue(Tools.FTOI(settings.getAoBlurRadius() * TinaController.SLIDER_SCALE_CENTRE));
    data.tinaSolidRenderingAOFalloffREd.setText(Tools.doubleToString(settings.getAoFalloff()));
    data.tinaSolidRenderingAOFalloffSlider.setValue(Tools.FTOI(settings.getAoFalloff() * TinaController.SLIDER_SCALE_CENTRE));
    data.tinaSolidRenderingAORadiusSamplesREd.setText(Tools.doubleToString(settings.getAoRadiusSamples()));
    data.tinaSolidRenderingAORadiusSamplesSlider.setValue(Tools.FTOI(settings.getAoRadiusSamples()));
    data.tinaSolidRenderingAOAzimuthSamplesREd.setText(Tools.doubleToString(settings.getAoAzimuthSamples()));
    data.tinaSolidRenderingAOAzimuthSamplesSlider.setValue(Tools.FTOI(settings.getAoAzimuthSamples()));
    data.tinaSolidRenderingAOAffectDiffuseREd.setText(Tools.doubleToString(settings.getAoAffectDiffuse()));
    data.tinaSolidRenderingAOAffectDiffuseSlider.setValue(Tools.FTOI(settings.getAoAffectDiffuse() * TinaController.SLIDER_SCALE_CENTRE));
    data.tinaSolidRenderingShadowTypeCmb.setSelectedItem(settings.getShadowType());
    data.tinaSolidRenderingShadowmapSizeCmb.setSelectedItem(String.valueOf(settings.getShadowmapSize()));
    if (data.tinaSolidRenderingShadowmapSizeCmb.getSelectedIndex() < 0) {
      data.tinaSolidRenderingShadowmapSizeCmb.setSelectedIndex(0);
    }
    data.tinaSolidRenderingShadowSmoothRadiusREd.setText(Tools.doubleToString(settings.getShadowSmoothRadius()));
    data.tinaSolidRenderingShadowSmoothRadiusSlider.setValue(Tools.FTOI(settings.getShadowSmoothRadius() * TinaController.SLIDER_SCALE_CENTRE));
    data.tinaSolidRenderingShadowmapBiasREd.setText(Tools.doubleToString(settings.getShadowmapBias()));
    data.tinaSolidRenderingShadowmapBiasSlider.setValue(Tools.FTOI(settings.getShadowmapBias() * TinaController.SLIDER_SCALE_CENTRE));

    data.postBokehIntensityREd.setText(Tools.doubleToString(settings.getPostBokehIntensity()));
    data.postBokehIntensitySlider.setValue(Tools.FTOI(settings.getPostBokehIntensity() * TinaController.SLIDER_SCALE_CENTRE));
    data.postBokehBrightnessREd.setText(Tools.doubleToString(settings.getPostBokehBrightness()));
    data.postBokehBrightnessSlider.setValue(Tools.FTOI(settings.getPostBokehBrightness() * TinaController.SLIDER_SCALE_CENTRE));
    data.postBokehSizeREd.setText(Tools.doubleToString(settings.getPostBokehSize()));
    data.postBokehSizeSlider.setValue(Tools.FTOI(settings.getPostBokehSize() * TinaController.SLIDER_SCALE_CENTRE));
    data.postBokehActivationREd.setText(Tools.doubleToString(settings.getPostBokehActivation()));
    data.postBokehActivationSlider.setValue(Tools.FTOI(settings.getPostBokehActivation() * TinaController.SLIDER_SCALE_CENTRE));
    data.postBokehFilterKernelCmb.setSelectedItem(settings.getPostBokehFilterKernel());
  }

  private void refreshSolidRenderingMaterialControls() {
    MaterialSettings material = getSolidRenderingSelectedMaterial();
    if (material != null) {
      data.tinaSolidRenderingMaterialDiffuseREd.setText(Tools.doubleToString(material.getDiffuse()));
      data.tinaSolidRenderingMaterialDiffuseSlider.setValue(Tools.FTOI(material.getDiffuse() * TinaController.SLIDER_SCALE_CENTRE));
      data.tinaSolidRenderingMaterialAmbientREd.setText(Tools.doubleToString(material.getAmbient()));
      data.tinaSolidRenderingMaterialAmbientSlider.setValue(Tools.FTOI(material.getAmbient() * TinaController.SLIDER_SCALE_CENTRE));
      data.tinaSolidRenderingMaterialSpecularREd.setText(Tools.doubleToString(material.getPhong()));
      data.tinaSolidRenderingMaterialSpecularSlider.setValue(Tools.FTOI(material.getPhong() * TinaController.SLIDER_SCALE_CENTRE));
      data.tinaSolidRenderingMaterialSpecularSharpnessREd.setText(Tools.doubleToString(material.getPhongSize()));
      data.tinaSolidRenderingMaterialSpecularSharpnessSlider.setValue(Tools.FTOI(material.getPhongSize() * TinaController.SLIDER_SCALE_CENTRE));
      data.tinaSolidRenderingMaterialDiffuseResponseCmb.setSelectedItem(material.getLightDiffFunc());
      data.tinaSolidRenderingMaterialReflectionMapIntensityREd.setText(Tools.doubleToString(material.getReflMapIntensity()));
      data.tinaSolidRenderingMaterialReflectionMapIntensitySlider.setValue(Tools.FTOI(material.getReflMapIntensity() * TinaController.SLIDER_SCALE_CENTRE));
      data.tinaSolidRenderingMaterialReflectionMappingCmb.setSelectedItem(material.getReflectionMapping());
    }
    refreshSolidRenderMaterialSpecularColorIndicator();
  }

  private void refreshLightPosControls(DistantLight light) {
    data.tinaSolidRenderingLightAltitudeREd.setText(Tools.doubleToString(light.getAltitude()));
    data.tinaSolidRenderingLightAltitudeSlider.setValue(Tools.FTOI(light.getAltitude() * TinaController.SLIDER_SCALE_CENTRE));
    data.tinaSolidRenderingLightAzimuthREd.setText(Tools.doubleToString(light.getAzimuth()));
    data.tinaSolidRenderingLightAzimuthSlider.setValue(Tools.FTOI(light.getAzimuth() * TinaController.SLIDER_SCALE_CENTRE));
  }

  private void refreshSolidRenderingLightControls() {
    DistantLight light = getSolidRenderingSelectedLight();
    if (light != null) {
      refreshLightPosControls(light);
      data.tinaSolidRenderingLightCastShadowsCBx.setSelected(light.isCastShadows());
      data.tinaSolidRenderingLightIntensityREd.setText(Tools.doubleToString(light.getIntensity()));
      data.tinaSolidRenderingLightIntensitySlider.setValue(Tools.FTOI(light.getIntensity() * TinaController.SLIDER_SCALE_CENTRE));
      data.tinaSolidRenderingShadowIntensityREd.setText(Tools.doubleToString(light.getShadowIntensity()));
      data.tinaSolidRenderingShadowIntensitySlider.setValue(Tools.FTOI(light.getShadowIntensity() * TinaController.SLIDER_SCALE_CENTRE));
    }
    refreshSolidRenderLightColorIndicator();
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

  public void motionBlurLengthREd_changed() {
    flameTextFieldChanged(data.motionBlurLengthSlider, data.motionBlurLengthField, "motionBlurLength", 1.0, false);
  }

  public void flameFPSField_changed() {
    if (isNoRefresh() || getCurrFlame() == null) {
      return;
    }
    getCurrFlame().setFps(data.flameFPSField.getIntValue());
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

  public void colorOversamplingSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaColorOversamplingSlider, data.tinaColorOversamplingREd, "colorOversampling", 1.0, false);
  }

  public void colorOversamplingREd_changed() {
    flameTextFieldChanged(data.tinaColorOversamplingSlider, data.tinaColorOversamplingREd, "colorOversampling", 1.0, false);
  }

  public void sampleJitteringCbx_changed() {
    if (!isNoRefresh()) {
      Flame flame = getCurrFlame();
      if (flame != null) {
        owner.saveUndoPoint();
        flame.setSampleJittering(data.tinaSampleJitteringCheckBox.isSelected());
        enableControls();
      }
    }
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

  public void postNoiseFilterThresholdSlider_stateChanged(ChangeEvent e) {
    flameSliderChanged(data.tinaPostNoiseThresholdSlider, data.tinaPostNoiseThresholdField, "postNoiseFilterThreshold", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD, false);
  }

  public void postNoiseFilterThresholdREd_changed() {
    flameTextFieldChanged(data.tinaPostNoiseThresholdSlider, data.tinaPostNoiseThresholdField, "postNoiseFilterThreshold", TinaController.SLIDER_SCALE_POST_NOISE_FILTER_THRESHOLD, false);
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

  public void postBlurFadeREd_changed() {
    flameTextFieldChanged(data.postBlurFadeSlider, data.postBlurFadeREd, "postBlurFadeRadius", TinaController.SLIDER_SCALE_AMBIENT, false);
  }

  public void postBlurFadeSlider_changed() {
    flameSliderChanged(data.postBlurFadeSlider, data.postBlurFadeREd, "postBlurFadeRadius", TinaController.SLIDER_SCALE_AMBIENT, false);
  }

  public void postBlurFallOffSlider_changed() {
    flameTextFieldChanged(data.postBlurFallOffSlider, data.postBlurFallOffREd, "postBlurFallOffRadius", TinaController.SLIDER_SCALE_AMBIENT, false);
  }

  public void postBlurFallOffREd_changed() {
    flameSliderChanged(data.postBlurFallOffSlider, data.postBlurFallOffREd, "postBlurFallOffRadius", TinaController.SLIDER_SCALE_AMBIENT, false);
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
        owner.refreshFlameImage(true, false, 1, true, true /*!areShadowsEnabled()*/);
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

  private void textFieldChanged(String property, Object value, double propValue, boolean pAllowUseCache) {
    Class<?> cls = value.getClass();
    try {
      Field field = cls.getDeclaredField(property);
      field.setAccessible(true);
      Class<?> fieldCls = field.getType();
      if (fieldCls == double.class || fieldCls == Double.class) {
        field.setDouble(value, propValue);
      }
      else if (fieldCls == int.class || fieldCls == Integer.class) {
        field.setInt(value, Tools.FTOI(propValue));
      }
      else {
        throw new IllegalStateException();
      }
    }
    catch (Throwable ex) {
      ex.printStackTrace();
    }
    owner.refreshFlameImage(true, false, 1, true, pAllowUseCache);
  }

  private void solidRenderingTextFieldChanged(JSlider pSlider, JWFNumberField pTextField, String pProperty, double pSliderScale, boolean pAllowUseCache) {
    if (isNoRefresh() || getCurrFlame() == null)
      return;
    setNoRefresh(true);
    try {
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));
      Object value = getCurrFlame().getSolidRenderSettings();
      textFieldChanged(pProperty, value, propValue, pAllowUseCache);
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
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Object value = getCurrFlame().getSolidRenderSettings();
      textFieldChanged(pProperty, value, propValue, pAllowUseCache);
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
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));
      Object value = getCurrFlame().getSolidRenderSettings().getMaterials().get(idx);
      textFieldChanged(pProperty, value, propValue, pAllowUseCache);
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
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Object value = getCurrFlame().getSolidRenderSettings().getMaterials().get(idx);
      textFieldChanged(pProperty, value, propValue, pAllowUseCache);
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
      double propValue = Tools.stringToDouble(pTextField.getText());
      pSlider.setValue(Tools.FTOI(propValue * pSliderScale));
      Object value = getCurrFlame().getSolidRenderSettings().getLights().get(idx);
      textFieldChanged(pProperty, value, propValue, pAllowUseCache);
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
      double propValue = pSlider.getValue() / pSliderScale;
      pTextField.setText(Tools.doubleToString(propValue));
      Object value = getCurrFlame().getSolidRenderSettings().getLights().get(idx);
      textFieldChanged(pProperty, value, propValue, pAllowUseCache);
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
    solidRenderingLightTextFieldChanged(data.tinaSolidRenderingLightAltitudeSlider, data.tinaSolidRenderingLightAltitudeREd, "altitude", TinaController.SLIDER_SCALE_CENTRE, true/*!areShadowsEnabled()*/);
  }

  private boolean areShadowsEnabled() {
    return ShadowType.areShadowsEnabled((ShadowType) data.tinaSolidRenderingShadowTypeCmb.getSelectedItem());
  }

  public void solidRenderingLightAzimuthREd_changed() {
    solidRenderingLightTextFieldChanged(data.tinaSolidRenderingLightAzimuthSlider, data.tinaSolidRenderingLightAzimuthREd, "azimuth", TinaController.SLIDER_SCALE_CENTRE, true /*!areShadowsEnabled()*/);
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
    solidRenderingLightSliderChanged(data.tinaSolidRenderingLightAltitudeSlider, data.tinaSolidRenderingLightAltitudeREd, "altitude", TinaController.SLIDER_SCALE_CENTRE, true /*!areShadowsEnabled()*/);
  }

  public void solidRenderingLightAzimuthSlider_stateChanged(ChangeEvent e) {
    solidRenderingLightSliderChanged(data.tinaSolidRenderingLightAzimuthSlider, data.tinaSolidRenderingLightAzimuthREd, "azimuth", TinaController.SLIDER_SCALE_CENTRE, true /*!areShadowsEnabled()*/);
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
          owner.refreshFlameImage(true, false, 1, true, false);
        }
        catch (Throwable ex) {
          owner.errorHandler.handleError(ex);
        }
      }
    }
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
}
