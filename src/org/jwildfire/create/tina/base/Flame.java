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
package org.jwildfire.create.tina.base;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;
import java.util.List;

import org.jwildfire.base.Prefs;
import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.base.solidrender.DistantLight;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.base.solidrender.SolidRenderSettings;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.ChannelMixerMode;
import org.jwildfire.create.tina.render.dof.DOFBlurShapeType;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.render.filter.FilteringType;
import org.jwildfire.create.tina.render.optix.OptixCmdLineDenoiser;
import org.jwildfire.create.tina.swing.ChannelMixerCurves;

public class Flame implements Assignable<Flame>, Serializable {
  private static final long serialVersionUID = 1L;
  private static final int DFLT_SOLID_SPATIAL_OVERSAMPLING = 2;
  @AnimAware
  private double centreX;
  private final MotionCurve centreXCurve = new MotionCurve();
  @AnimAware
  private double centreY;
  private final MotionCurve centreYCurve = new MotionCurve();

  private int width;
  private int height;
  @AnimAware
  private double camPitch;
  private final MotionCurve camPitchCurve = new MotionCurve();
  @AnimAware
  private double camYaw;
  private final MotionCurve camYawCurve = new MotionCurve();
  @AnimAware
  private double camBank;
  private final MotionCurve camBankCurve = new MotionCurve();
  @AnimAware
  private double camPerspective;
  private final MotionCurve camPerspectiveCurve = new MotionCurve();
  @AnimAware
  private double camRoll;
  private final MotionCurve camRollCurve = new MotionCurve();
  @AnimAware
  private double camZoom;
  private final MotionCurve camZoomCurve = new MotionCurve();
  @AnimAware
  private double camPosX;
  private final MotionCurve camPosXCurve = new MotionCurve();
  @AnimAware
  private double camPosY;
  private final MotionCurve camPosYCurve = new MotionCurve();
  @AnimAware
  private double camPosZ;
  private final MotionCurve camPosZCurve = new MotionCurve();
  @AnimAware
  private double camZ;
  private final MotionCurve camZCurve = new MotionCurve();
  @AnimAware
  private double focusX;
  private final MotionCurve focusXCurve = new MotionCurve();
  @AnimAware
  private double focusY;
  private final MotionCurve focusYCurve = new MotionCurve();
  @AnimAware
  private double focusZ;
  private final MotionCurve focusZCurve = new MotionCurve();
  @AnimAware
  private double dimishZ;
  private final MotionCurve dimishZCurve = new MotionCurve();
  private int dimishZRed;
  private int dimishZGreen;
  private int dimishZBlue;
  @AnimAware
  private double dimZDistance;
  private final MotionCurve dimZDistanceCurve = new MotionCurve();
  @AnimAware
  private double camDOF;
  private final MotionCurve camDOFCurve = new MotionCurve();
  private DOFBlurShapeType camDOFShape;
  @AnimAware
  private double camDOFScale;
  private final MotionCurve camDOFScaleCurve = new MotionCurve();
  @AnimAware
  private double camDOFAngle;
  private final MotionCurve camDOFAngleCurve = new MotionCurve();
  @AnimAware
  private double camDOFFade;
  private final MotionCurve camDOFFadeCurve = new MotionCurve();
  private double camDOFParam1;
  private final MotionCurve camDOFParam1Curve = new MotionCurve();
  private double camDOFParam2;
  private final MotionCurve camDOFParam2Curve = new MotionCurve();
  private double camDOFParam3;
  private final MotionCurve camDOFParam3Curve = new MotionCurve();
  private double camDOFParam4;
  private final MotionCurve camDOFParam4Curve = new MotionCurve();
  private double camDOFParam5;
  private final MotionCurve camDOFParam5Curve = new MotionCurve();
  private double camDOFParam6;
  private final MotionCurve camDOFParam6Curve = new MotionCurve();

  @AnimAware
  private double camDOFExponent;
  private final MotionCurve camDOFExponentCurve = new MotionCurve();
  @AnimAware
  private double camDOFArea;
  private final MotionCurve camDOFAreaCurve = new MotionCurve();
  private boolean newCamDOF;
  private int spatialOversampling;
  private double spatialFilterRadius;
  private FilterKernelType spatialFilterKernel;
  private FilteringType spatialFilteringType;
  private boolean spatialFilterIndicator;
  private double spatialFilterSharpness;
  private double spatialFilterLowDensity;
  private double sampleDensity;
  private boolean bgTransparency;
  private boolean postNoiseFilter;
  private double postNoiseFilterThreshold;
  private boolean postOptiXDenoiser;
  private double postOptiXDenoiserBlend;

  private double foregroundOpacity;
  private final MotionCurve foregroundOpacityCurve = new MotionCurve();

  private BGColorType bgColorType = BGColorType.GRADIENT_2X2_C;
  @AnimAware
  private int bgColorRed;
  @AnimAware
  private int bgColorGreen;
  @AnimAware
  private int bgColorBlue;

  private int bgColorULRed;
  private int bgColorULGreen;
  private int bgColorULBlue;
  private int bgColorURRed;
  private int bgColorURGreen;
  private int bgColorURBlue;
  private int bgColorLLRed;
  private int bgColorLLGreen;
  private int bgColorLLBlue;
  private int bgColorLRRed;
  private int bgColorLRGreen;
  private int bgColorLRBlue;
  private int bgColorCCRed;
  private int bgColorCCGreen;
  private int bgColorCCBlue;

  private double gamma;
  private final MotionCurve gammaCurve = new MotionCurve();
  private double gammaThreshold;
  private final MotionCurve gammaThresholdCurve = new MotionCurve();
  private double pixelsPerUnit;
  private final MotionCurve pixelsPerUnitCurve = new MotionCurve();
  private double whiteLevel;
  private final MotionCurve whiteLevelCurve = new MotionCurve();
  @AnimAware
  private double brightness;
  private final MotionCurve brightnessCurve = new MotionCurve();
  private double contrast;
  private final MotionCurve contrastCurve = new MotionCurve();
  @AnimAware
  private double saturation;
  private final MotionCurve saturationCurve = new MotionCurve();
  @AnimAware
  private double vibrancy;
  private final MotionCurve vibrancyCurve = new MotionCurve();
  @AnimAware
  private double lowDensityBrightness;
  private final MotionCurve lowDensityBrightnessCurve = new MotionCurve();
  @AnimAware
  private double balanceRed;
  private final MotionCurve balanceRedCurve = new MotionCurve();
  @AnimAware
  private double balanceGreen;
  private final MotionCurve balanceGreenCurve = new MotionCurve();
  @AnimAware
  private double balanceBlue;
  private final MotionCurve balanceBlueCurve = new MotionCurve();

  private boolean preserveZ;
  private String resolutionProfile;
  private String qualityProfile;
  private String name = "";
  private String bgImageFilename = "";
  @AnimAware
  private final List<Layer> layers = new LayerList(this);

  @AnimAware
  private String lastFilename = null;
  private double antialiasAmount;
  private double antialiasRadius;

  private int motionBlurLength;
  private double motionBlurTimeStep;
  private double motionBlurDecay;

  private int frame = 1;
  private int frameCount = Prefs.getPrefs().getTinaDefaultFrameCount();
  private int fps = 25;
  private double zBufferScale = 1.0;
  private double zBufferBias = 0.0;
  private ZBufferFilename zBufferFilename = ZBufferFilename.PRE_ZBUF;

  private int postBlurRadius;
  private double postBlurFade;
  private double postBlurFallOff;

  private PostSymmetryType postSymmetryType;
  private int postSymmetryOrder;
  private double postSymmetryCentreX;
  private double postSymmetryCentreY;
  private double postSymmetryDistance;
  private double postSymmetryRotation;

  private Stereo3dMode stereo3dMode;
  private double stereo3dAngle;
  private double stereo3dEyeDist;
  private double stereo3dFocalOffset;
  private Stereo3dColor stereo3dLeftEyeColor;
  private Stereo3dColor stereo3dRightEyeColor;
  private Stereo3dPreview stereo3dPreview;
  private int stereo3dInterpolatedImageCount;
  private boolean stereo3dSwapSides;

  private ChannelMixerMode channelMixerMode;
  private final MotionCurve mixerRRCurve = new MotionCurve();
  private final MotionCurve mixerRGCurve = new MotionCurve();
  private final MotionCurve mixerRBCurve = new MotionCurve();
  private final MotionCurve mixerGRCurve = new MotionCurve();
  private final MotionCurve mixerGGCurve = new MotionCurve();
  private final MotionCurve mixerGBCurve = new MotionCurve();
  private final MotionCurve mixerBRCurve = new MotionCurve();
  private final MotionCurve mixerBGCurve = new MotionCurve();
  private final MotionCurve mixerBBCurve = new MotionCurve();
  private SolidRenderSettings solidRenderSettings = new SolidRenderSettings();

  private EditPlane editPlane;

  public Flame() {
    layers.clear();
    layers.add(new Layer());
    sampleDensity = 100.0;
    bgTransparency = true;
    pixelsPerUnit = 50;
    name = "";
    bgImageFilename = "";
    editPlane = EditPlane.XY;

    resetColoringSettings();
    resetAntialiasingSettings();
    resetCameraSettings();
    resetDOFSettings();
    resetBokehSettings();
    resetPostBlurSettings();
    resetStereo3DSettings();
    resetPostSymmetrySettings();
    resetMotionBlurSettings();
    resetSolidRenderSettings();
    channelMixerMode = ChannelMixerMode.OFF;
    resetMixerCurves();
  }

  private void resetSolidRenderSettings() {
    solidRenderSettings.setupDefaults();
  }

  public void resetMotionBlurSettings() {
    motionBlurLength = 0;
    motionBlurTimeStep = 0.05;
    motionBlurDecay = 0.03;
    fps = Prefs.getPrefs().getTinaDefaultFPS();
  }

  public void resetPostSymmetrySettings() {
    postSymmetryType = PostSymmetryType.NONE;
    postSymmetryOrder = 3;
    postSymmetryCentreX = 0.0;
    postSymmetryCentreY = 0.0;
    postSymmetryDistance = 1.25;
    postSymmetryRotation = 6.0;
  }

  public void resetStereo3DSettings() {
    stereo3dMode = Stereo3dMode.NONE;
    stereo3dAngle = 1.6;
    stereo3dEyeDist = 0.032;
    stereo3dFocalOffset = 0.5;
    stereo3dLeftEyeColor = Stereo3dColor.RED;
    stereo3dRightEyeColor = Stereo3dColor.CYAN;
    stereo3dPreview = Stereo3dPreview.ANAGLYPH;
    stereo3dInterpolatedImageCount = 3;
    stereo3dSwapSides = false;
  }

  public void resetPostBlurSettings() {
    postBlurRadius = 0;
    postBlurFade = 0.95;
    postBlurFallOff = 2.0;
  }

  public void resetAntialiasingSettings() {
    spatialFilterRadius = Prefs.getPrefs().getTinaDefaultSpatialFilterRadius();
    spatialFilterKernel = Prefs.getPrefs().getTinaDefaultSpatialFilterKernel();
    spatialFilterSharpness = 4.0;
    spatialFilterLowDensity = 0.025;
    if (FilterKernelType.getSharpeningFilters().contains(spatialFilterKernel)) {
      spatialFilteringType = FilteringType.GLOBAL_SHARPENING;
    }
    else if (FilterKernelType.getSmoothingFilters().contains(spatialFilterKernel)) {
      spatialFilteringType = FilteringType.GLOBAL_SMOOTHING;
    }
    else if (FilterKernelType.getAdapativeFilters().contains(spatialFilterKernel)) {
      spatialFilteringType = FilteringType.ADAPTIVE;
    }
    else {
      spatialFilteringType = FilteringType.GLOBAL_SMOOTHING;
      spatialFilterKernel = FilterKernelType.SINEPOW10;
    }
    spatialOversampling = Prefs.getPrefs().getTinaDefaultSpatialOversampling();
    spatialFilterIndicator = false;
    postNoiseFilter = Prefs.getPrefs().isTinaDefaultPostNoiseFilter();
    postNoiseFilterThreshold = Prefs.getPrefs().getTinaDefaultPostNoiseFilterThreshold();
    antialiasAmount = Prefs.getPrefs().getTinaDefaultAntialiasingAmount();
    antialiasRadius = Prefs.getPrefs().getTinaDefaultAntialiasingRadius();
    postOptiXDenoiser = Prefs.getPrefs().isTinaDefaultPostOptiXDenoiser() && OptixCmdLineDenoiser.isAvailable();
    postOptiXDenoiserBlend = Prefs.getPrefs().getTinaDefaultPostOptiXDenoiserBlend();
  }

  public void resetColoringSettings() {
    brightness = 4.0;
    contrast = 1;
    gamma = 4.0;
    gammaThreshold = 0.01;
    vibrancy = 1;
    bgColorType = BGColorType.GRADIENT_2X2_C;
    bgColorRed = bgColorGreen = bgColorBlue = 0;
    bgColorULRed = bgColorULGreen = bgColorULBlue = bgColorURRed = bgColorURGreen = bgColorURBlue = bgColorLLRed = bgColorLLGreen = bgColorLLBlue = bgColorLRRed = bgColorLRGreen = bgColorLRBlue = 0;
    bgColorCCRed = bgColorCCGreen = bgColorCCBlue = 0;
    whiteLevel = Prefs.getPrefs().getTinaDefaultFadeToWhiteLevel();
    saturation = 1.0;
    foregroundOpacity = Prefs.getPrefs().getTinaDefaultForegroundOpacity();
    lowDensityBrightness = 0.24;
    balanceRed = 1.0;
    balanceGreen = 1.0;
    balanceBlue = 1.0;
  }

  public void resetBokehSettings() {
    camDOFShape = DOFBlurShapeType.BUBBLE;
    camDOFScale = 0.0;
    camDOFAngle = 0.0;
    camDOFFade = 0.0;
    camDOFParam1 = 0.0;
    camDOFParam2 = 0.0;
    camDOFParam3 = 0.0;
    camDOFParam4 = 0.0;
    camDOFParam5 = 0.0;
    camDOFParam6 = 0.0;
    camDOFShape.getDOFBlurShape().setDefaultParams(this);
  }

  public void resetDOFSettings() {
    newCamDOF = false;
    camDOFArea = 0.5;
    camDOFExponent = 2.0;
    camDOF = 0.0;
    dimishZ = 0.0;
    dimishZRed = 0;
    dimishZGreen = 0;
    dimishZBlue = 0;
    dimZDistance = 0.0;
    camZ = 0.0;
    focusX = 0.0;
    focusY = 0.0;
    focusZ = 0.0;
  }

  public void resetCameraSettings() {
    camRoll = 0.0;
    camPitch = 0.0;
    camYaw = 0.0;
    camBank = 0.0;
    camPerspective = 0.0;
    centreX = 0.0;
    centreY = 0.0;
    camZoom = 1.0;
    camPosX = 0.0;
    camPosY = 0.0;
    camPosZ = 0.0;
  }

  public void resetMixerCurves() {
    ChannelMixerCurves.DEFAULT.makeCurve(mixerRRCurve);
    ChannelMixerCurves.ZERO.makeCurve(mixerRGCurve);
    ChannelMixerCurves.ZERO.makeCurve(mixerRBCurve);
    ChannelMixerCurves.ZERO.makeCurve(mixerGRCurve);
    ChannelMixerCurves.DEFAULT.makeCurve(mixerGGCurve);
    ChannelMixerCurves.ZERO.makeCurve(mixerGBCurve);
    ChannelMixerCurves.ZERO.makeCurve(mixerBRCurve);
    ChannelMixerCurves.ZERO.makeCurve(mixerBGCurve);
    ChannelMixerCurves.DEFAULT.makeCurve(mixerBBCurve);
  }

  public double getCentreX() {
    return centreX;
  }

  public void setCentreX(double centreX) {
    this.centreX = centreX;
  }

  public double getCentreY() {
    return centreY;
  }

  public void setCentreY(double centreY) {
    this.centreY = centreY;
  }

  public double getCamRoll() {
    return camRoll;
  }

  public void setCamRoll(double pCamRoll) {
    this.camRoll = pCamRoll;
  }

  public double getBrightness() {
    return brightness;
  }

  public void setBrightness(double brightness) {
    this.brightness = brightness;
  }

  public double getGamma() {
    return gamma;
  }

  public void setGamma(double gamma) {
    this.gamma = gamma;
  }

  public double getGammaThreshold() {
    return gammaThreshold;
  }

  public void setGammaThreshold(double gammaThreshold) {
    this.gammaThreshold = gammaThreshold;
  }

  public double getSpatialFilterRadius() {
    return spatialFilterRadius;
  }

  public void setSpatialFilterRadius(double spatialFilterRadius) {
    this.spatialFilterRadius = spatialFilterRadius;
  }

  public double getSampleDensity() {
    return sampleDensity;
  }

  public void setSampleDensity(double sampleDensity) {
    this.sampleDensity = sampleDensity;
  }

  public double getPixelsPerUnit() {
    return pixelsPerUnit;
  }

  public void setPixelsPerUnit(double pixelsPerUnit) {
    this.pixelsPerUnit = pixelsPerUnit;
  }

  public double getWhiteLevel() {
    return whiteLevel;
  }

  public void setWhiteLevel(double pWhiteLevel) {
    whiteLevel = pWhiteLevel;
  }

  public double getContrast() {
    return contrast;
  }

  public void setContrast(double contrast) {
    this.contrast = contrast;
  }

  public double getVibrancy() {
    return vibrancy;
  }

  public void setVibrancy(double vibrancy) {
    this.vibrancy = vibrancy;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public double getCamPitch() {
    return camPitch;
  }

  public void setCamPitch(double camPitch) {
    this.camPitch = camPitch;
  }

  public double getCamYaw() {
    return camYaw;
  }

  public void setCamYaw(double camYaw) {
    this.camYaw = camYaw;
  }

  public double getCamBank() {
    return camBank;
  }

  public void setCamBank(double pCamBank) {
    this.camBank = pCamBank;
  }

  public double getCamPerspective() {
    return camPerspective;
  }

  public void setCamPerspective(double camPerspective) {
    this.camPerspective = camPerspective;
  }

  public double getCamZoom() {
    return camZoom;
  }

  public void setCamZoom(double camZoom) {
    this.camZoom = camZoom;
  }

  public int getBgColorRed() {
    return bgColorRed;
  }

  public void setBgColorRed(int bgColorRed) {
    this.bgColorRed = bgColorRed;
  }

  public int getBgColorGreen() {
    return bgColorGreen;
  }

  public void setBgColorGreen(int bgColorGreen) {
    this.bgColorGreen = bgColorGreen;
  }

  public int getBgColorBlue() {
    return bgColorBlue;
  }

  public void setBgColorBlue(int bgColorBlue) {
    this.bgColorBlue = bgColorBlue;
  }

  @Override
  public Flame makeCopy() {
    Flame res = new Flame();
    res.assign(this);
    return res;
  }
  
  public Flame makeCopy(Layer pLayer) {
    Flame res = new Flame();
    res.assign(this, pLayer);
    return res;
  }

  public double getFocusX() {
    return focusX;
  }

  public void setFocusX(double focusX) {
    this.focusX = focusX;
  }

  public double getFocusY() {
    return focusY;
  }

  public void setFocusY(double focusY) {
    this.focusY = focusY;
  }

  public double getFocusZ() {
    return focusZ;
  }

  public void setFocusZ(double focusZ) {
    this.focusZ = focusZ;
  }

  public double getCamZ() {
    return camZ;
  }

  public void setCamZ(double camZ) {
    this.camZ = camZ;
  }

  public double getCamDOF() {
    return camDOF;
  }

  public void setCamDOF(double camDOF) {
    this.camDOF = camDOF;
  }

  public double getCamDOFArea() {
    return camDOFArea;
  }

  public void setCamDOFArea(double camDOFArea) {
    this.camDOFArea = camDOFArea;
  }

  public double getCamDOFExponent() {
    return camDOFExponent;
  }

  public void setCamDOFExponent(double camDOFExponent) {
    this.camDOFExponent = camDOFExponent;
  }

  public String getResolutionProfile() {
    return resolutionProfile;
  }

  public void setResolutionProfile(ResolutionProfile pResolutionProfile) {
    resolutionProfile = pResolutionProfile.toString();
  }

  public void setResolutionProfile(String pResolutionProfile) {
    resolutionProfile = pResolutionProfile;
  }

  public String getQualityProfile() {
    return qualityProfile;
  }

  public void setQualityProfile(QualityProfile pQualityProfile) {
    qualityProfile = pQualityProfile.toString();
  }

  public void setQualityProfile(String pQualityProfile) {
    qualityProfile = pQualityProfile;
  }

  @Override
  public void assign(Flame pFlame) {
    assign(pFlame, null);
  }
  
  public void assign(Flame pFlame, Layer pLayer) {
    centreX = pFlame.centreX;
    centreXCurve.assign(pFlame.centreXCurve);
    centreY = pFlame.centreY;
    centreYCurve.assign(pFlame.centreYCurve);
    width = pFlame.width;
    height = pFlame.height;
    camPitch = pFlame.camPitch;
    camPitchCurve.assign(pFlame.camPitchCurve);
    camYaw = pFlame.camYaw;
    camYawCurve.assign(pFlame.camYawCurve);
    camPerspective = pFlame.camPerspective;
    camPerspectiveCurve.assign(pFlame.camPerspectiveCurve);
    camRoll = pFlame.camRoll;
    camRollCurve.assign(pFlame.camRollCurve);
    camBank = pFlame.camBank;
    camBankCurve.assign(pFlame.camBankCurve);
    camZoom = pFlame.camZoom;
    camZoomCurve.assign(pFlame.camZoomCurve);
    focusX = pFlame.focusX;
    focusXCurve.assign(pFlame.focusXCurve);
    focusY = pFlame.focusY;
    focusYCurve.assign(pFlame.focusYCurve);
    focusZ = pFlame.focusZ;
    focusZCurve.assign(pFlame.focusZCurve);
    dimishZ = pFlame.dimishZ;
    dimishZCurve.assign(pFlame.dimishZCurve);
    dimishZRed = pFlame.dimishZRed;
    dimishZGreen = pFlame.dimishZGreen;
    dimishZBlue = pFlame.dimishZBlue;
    dimZDistance = pFlame.dimZDistance;
    dimZDistanceCurve.assign(pFlame.dimZDistanceCurve);
    camPosX = pFlame.camPosX;
    camPosXCurve.assign(pFlame.camPosXCurve);
    camPosY = pFlame.camPosY;
    camPosYCurve.assign(pFlame.camPosYCurve);
    camPosZ = pFlame.camPosZ;
    camPosZCurve.assign(pFlame.camPosZCurve);
    camZ = pFlame.camZ;
    camZCurve.assign(pFlame.camZCurve);
    camDOF = pFlame.camDOF;
    camDOFShape = pFlame.camDOFShape;
    camDOFCurve.assign(pFlame.camDOFCurve);
    camDOFScale = pFlame.camDOFScale;
    camDOFScaleCurve.assign(pFlame.camDOFScaleCurve);
    camDOFAngle = pFlame.camDOFAngle;
    camDOFAngleCurve.assign(pFlame.camDOFAngleCurve);
    camDOFFade = pFlame.camDOFFade;
    camDOFFadeCurve.assign(pFlame.camDOFFadeCurve);
    camDOFParam1 = pFlame.camDOFParam1;
    camDOFParam1Curve.assign(pFlame.camDOFParam1Curve);
    camDOFParam2 = pFlame.camDOFParam2;
    camDOFParam2Curve.assign(pFlame.camDOFParam2Curve);
    camDOFParam3 = pFlame.camDOFParam3;
    camDOFParam3Curve.assign(pFlame.camDOFParam3Curve);
    camDOFParam4 = pFlame.camDOFParam4;
    camDOFParam4Curve.assign(pFlame.camDOFParam4Curve);
    camDOFParam5 = pFlame.camDOFParam5;
    camDOFParam5Curve.assign(pFlame.camDOFParam5Curve);
    camDOFParam6 = pFlame.camDOFParam6;
    camDOFParam6Curve.assign(pFlame.camDOFParam6Curve);
    newCamDOF = pFlame.newCamDOF;
    camDOFArea = pFlame.camDOFArea;
    camDOFAreaCurve.assign(pFlame.camDOFAreaCurve);
    camDOFExponent = pFlame.camDOFExponent;
    camDOFExponentCurve.assign(pFlame.camDOFExponentCurve);
    spatialFilterRadius = pFlame.spatialFilterRadius;
    spatialFilterKernel = pFlame.spatialFilterKernel;
    spatialFilteringType = pFlame.spatialFilteringType;
    spatialFilterIndicator = pFlame.spatialFilterIndicator;
    spatialFilterSharpness = pFlame.spatialFilterSharpness;
    spatialFilterLowDensity = pFlame.spatialFilterLowDensity;
    sampleDensity = pFlame.sampleDensity;
    bgTransparency = pFlame.bgTransparency;
    bgColorRed = pFlame.bgColorRed;
    bgColorGreen = pFlame.bgColorGreen;
    bgColorBlue = pFlame.bgColorBlue;
    bgColorType = pFlame.bgColorType;
    bgColorULRed = pFlame.bgColorULRed;
    bgColorULGreen = pFlame.bgColorULGreen;
    bgColorULBlue = pFlame.bgColorULBlue;
    bgColorURRed = pFlame.bgColorURRed;
    bgColorURGreen = pFlame.bgColorURGreen;
    bgColorURBlue = pFlame.bgColorURBlue;
    bgColorLLRed = pFlame.bgColorLLRed;
    bgColorLLGreen = pFlame.bgColorLLGreen;
    bgColorLLBlue = pFlame.bgColorLLBlue;
    bgColorLRRed = pFlame.bgColorLRRed;
    bgColorLRGreen = pFlame.bgColorLRGreen;
    bgColorLRBlue = pFlame.bgColorLRBlue;
    bgColorCCRed = pFlame.bgColorCCRed;
    bgColorCCGreen = pFlame.bgColorCCGreen;
    bgColorCCBlue = pFlame.bgColorCCBlue;
    gamma = pFlame.gamma;
    gammaCurve.assign(pFlame.gammaCurve);
    gammaThreshold = pFlame.gammaThreshold;
    gammaThresholdCurve.assign(pFlame.gammaThresholdCurve);
    pixelsPerUnit = pFlame.pixelsPerUnit;
    pixelsPerUnitCurve.assign(pFlame.pixelsPerUnitCurve);
    whiteLevel = pFlame.whiteLevel;
    whiteLevelCurve.assign(pFlame.whiteLevelCurve);
    brightness = pFlame.brightness;
    brightnessCurve.assign(pFlame.brightnessCurve);
    saturation = pFlame.saturation;
    saturationCurve.assign(pFlame.saturationCurve);
    contrast = pFlame.contrast;
    contrastCurve.assign(pFlame.contrastCurve);
    vibrancy = pFlame.vibrancy;
    vibrancyCurve.assign(pFlame.vibrancyCurve);
    lowDensityBrightness = pFlame.lowDensityBrightness;
    lowDensityBrightnessCurve.assign(pFlame.lowDensityBrightnessCurve);
    balanceRed = pFlame.balanceRed;
    balanceRedCurve.assign(pFlame.balanceRedCurve);
    balanceGreen = pFlame.balanceGreen;
    balanceGreenCurve.assign(pFlame.balanceGreenCurve);
    balanceBlue = pFlame.balanceBlue;
    balanceBlueCurve.assign(pFlame.balanceBlueCurve);
    preserveZ = pFlame.preserveZ;
    resolutionProfile = pFlame.resolutionProfile;
    qualityProfile = pFlame.qualityProfile;
    name = pFlame.name;
    bgImageFilename = pFlame.bgImageFilename;
    lastFilename = pFlame.lastFilename;
    antialiasAmount = pFlame.antialiasAmount;
    antialiasRadius = pFlame.antialiasRadius;
    spatialOversampling = pFlame.spatialOversampling;
    postNoiseFilter = pFlame.postNoiseFilter;
    postNoiseFilterThreshold = pFlame.postNoiseFilterThreshold;
    postOptiXDenoiser = pFlame.postOptiXDenoiser;
    postOptiXDenoiserBlend = pFlame.postOptiXDenoiserBlend;
    foregroundOpacity = pFlame.foregroundOpacity;
    foregroundOpacityCurve.assign(pFlame.foregroundOpacityCurve);
    postBlurRadius = pFlame.postBlurRadius;
    postBlurFade = pFlame.postBlurFade;
    postBlurFallOff = pFlame.postBlurFallOff;
    zBufferScale = pFlame.zBufferScale;
    zBufferBias = pFlame.zBufferBias;
    zBufferFilename = pFlame.zBufferFilename; 

    motionBlurLength = pFlame.motionBlurLength;
    motionBlurTimeStep = pFlame.motionBlurTimeStep;
    motionBlurDecay = pFlame.motionBlurDecay;
    fps = pFlame.fps;

    frame = pFlame.frame;
    frameCount = pFlame.frameCount;

    postSymmetryType = pFlame.postSymmetryType;
    postSymmetryOrder = pFlame.postSymmetryOrder;
    postSymmetryCentreX = pFlame.postSymmetryCentreX;
    postSymmetryCentreY = pFlame.postSymmetryCentreY;
    postSymmetryDistance = pFlame.postSymmetryDistance;
    postSymmetryRotation = pFlame.postSymmetryRotation;

    stereo3dMode = pFlame.stereo3dMode;
    stereo3dAngle = pFlame.stereo3dAngle;
    stereo3dEyeDist = pFlame.stereo3dEyeDist;
    stereo3dLeftEyeColor = pFlame.stereo3dLeftEyeColor;
    stereo3dRightEyeColor = pFlame.stereo3dRightEyeColor;
    stereo3dInterpolatedImageCount = pFlame.stereo3dInterpolatedImageCount;
    stereo3dPreview = pFlame.stereo3dPreview;
    stereo3dFocalOffset = pFlame.stereo3dFocalOffset;
    stereo3dSwapSides = pFlame.stereo3dSwapSides;

    channelMixerMode = pFlame.channelMixerMode;
    mixerRRCurve.assign(pFlame.mixerRRCurve);
    mixerRGCurve.assign(pFlame.mixerRGCurve);
    mixerRBCurve.assign(pFlame.mixerRBCurve);
    mixerGRCurve.assign(pFlame.mixerGRCurve);
    mixerGGCurve.assign(pFlame.mixerGGCurve);
    mixerGBCurve.assign(pFlame.mixerGBCurve);
    mixerBRCurve.assign(pFlame.mixerBRCurve);
    mixerBGCurve.assign(pFlame.mixerBGCurve);
    mixerBBCurve.assign(pFlame.mixerBBCurve);

    editPlane = pFlame.editPlane;
    layers.clear();
    if (pLayer == null) {
      for (Layer layer : pFlame.getLayers()) {
        layers.add(layer.makeCopy());
      }
    }
    else {
      layers.add(pLayer);
    }

    solidRenderSettings.assign(pFlame.solidRenderSettings);
  }

  public List<Layer> getLayers() {
    return layers;
  }

  @Override
  public boolean isEqual(Flame pFlame) {
    if ((fabs(centreX - pFlame.centreX) > EPSILON) || !centreXCurve.isEqual(pFlame.centreXCurve) ||
        (fabs(centreY - pFlame.centreY) > EPSILON) || !centreYCurve.isEqual(pFlame.centreYCurve) ||
        (width != pFlame.width) || (height != pFlame.height) ||
        (fabs(camPitch - pFlame.camPitch) > EPSILON) || !camPitchCurve.isEqual(pFlame.camPitchCurve) ||
        (fabs(camYaw - pFlame.camYaw) > EPSILON) || !camYawCurve.isEqual(pFlame.camYawCurve) ||
        (fabs(camPerspective - pFlame.camPerspective) > EPSILON) || !camPerspectiveCurve.isEqual(pFlame.camPerspectiveCurve) ||
        (fabs(camRoll - pFlame.camRoll) > EPSILON) || !camRollCurve.isEqual(pFlame.camRollCurve) ||
        (fabs(camBank - pFlame.camBank) > EPSILON) || !camBankCurve.isEqual(pFlame.camBankCurve) ||
        (fabs(camZoom - pFlame.camZoom) > EPSILON) || !camZoomCurve.isEqual(pFlame.camZoomCurve) ||
        (fabs(focusX - pFlame.focusX) > EPSILON) || !focusXCurve.isEqual(pFlame.focusXCurve) ||
        (fabs(focusY - pFlame.focusY) > EPSILON) || !focusYCurve.isEqual(pFlame.focusYCurve) ||
        (fabs(focusZ - pFlame.focusZ) > EPSILON) || !focusZCurve.isEqual(pFlame.focusZCurve) ||
        (fabs(dimishZ - pFlame.dimishZ) > EPSILON) || !dimishZCurve.isEqual(pFlame.dimishZCurve) ||
        (fabs(dimishZRed - pFlame.dimishZRed) > EPSILON) || (fabs(dimishZGreen - pFlame.dimishZGreen) > EPSILON) || (fabs(dimishZBlue - pFlame.dimishZBlue) > EPSILON) ||
        (fabs(dimZDistance - pFlame.dimZDistance) > EPSILON) || !dimZDistanceCurve.isEqual(pFlame.dimZDistanceCurve) ||
        (fabs(camDOF - pFlame.camDOF) > EPSILON) || !camDOFCurve.isEqual(pFlame.camDOFCurve) ||
        (camDOFShape != pFlame.camDOFShape) || (spatialOversampling != pFlame.spatialOversampling) ||
        (postNoiseFilter != pFlame.postNoiseFilter) || (fabs(postNoiseFilterThreshold - pFlame.postNoiseFilterThreshold) > EPSILON) ||
        (postOptiXDenoiser != pFlame.postOptiXDenoiser) || (fabs(postOptiXDenoiserBlend - pFlame.postOptiXDenoiserBlend) > EPSILON) ||
        (fabs(foregroundOpacity - pFlame.foregroundOpacity) > EPSILON) || !foregroundOpacityCurve.isEqual(pFlame.foregroundOpacityCurve) ||
        (fabs(camDOFScale - pFlame.camDOFScale) > EPSILON) || !camDOFScaleCurve.isEqual(pFlame.camDOFScaleCurve) ||
        (fabs(camDOFAngle - pFlame.camDOFAngle) > EPSILON) || !camDOFAngleCurve.isEqual(pFlame.camDOFAngleCurve) ||
        (fabs(camDOFFade - pFlame.camDOFFade) > EPSILON) || !camDOFFadeCurve.isEqual(pFlame.camDOFFadeCurve) ||
        (fabs(camDOFParam1 - pFlame.camDOFParam1) > EPSILON) || !camDOFParam1Curve.isEqual(pFlame.camDOFParam1Curve) ||
        (fabs(camDOFParam2 - pFlame.camDOFParam2) > EPSILON) || !camDOFParam2Curve.isEqual(pFlame.camDOFParam2Curve) ||
        (fabs(camDOFParam3 - pFlame.camDOFParam3) > EPSILON) || !camDOFParam3Curve.isEqual(pFlame.camDOFParam3Curve) ||
        (fabs(camDOFParam4 - pFlame.camDOFParam4) > EPSILON) || !camDOFParam4Curve.isEqual(pFlame.camDOFParam4Curve) ||
        (fabs(camDOFParam5 - pFlame.camDOFParam5) > EPSILON) || !camDOFParam5Curve.isEqual(pFlame.camDOFParam5Curve) ||
        (fabs(camDOFParam6 - pFlame.camDOFParam6) > EPSILON) || !camDOFParam6Curve.isEqual(pFlame.camDOFParam6Curve) ||
        (fabs(camDOFArea - pFlame.camDOFArea) > EPSILON) || !camDOFAreaCurve.isEqual(pFlame.camDOFAreaCurve) ||
        (fabs(camDOFExponent - pFlame.camDOFExponent) > EPSILON) || !camDOFExponentCurve.isEqual(pFlame.camDOFExponentCurve) ||
        (fabs(camPosX - pFlame.camPosX) > EPSILON) || !camPosXCurve.isEqual(pFlame.camPosXCurve) ||
        (fabs(camPosY - pFlame.camPosY) > EPSILON) || !camPosYCurve.isEqual(pFlame.camPosYCurve) ||
        (fabs(camPosZ - pFlame.camPosZ) > EPSILON) || !camPosZCurve.isEqual(pFlame.camPosZCurve) ||
        (fabs(camZ - pFlame.camZ) > EPSILON) || !camZCurve.isEqual(pFlame.camZCurve) ||
        (newCamDOF != pFlame.newCamDOF) || (fabs(spatialFilterRadius - pFlame.spatialFilterRadius) > EPSILON) ||
        (fabs(spatialFilterSharpness - pFlame.spatialFilterSharpness) > EPSILON) || (fabs(spatialFilterLowDensity - pFlame.spatialFilterLowDensity) > EPSILON) ||
        !spatialFilteringType.equals(pFlame.spatialFilteringType) || !spatialFilterKernel.equals(pFlame.spatialFilterKernel) ||
        spatialFilterIndicator != pFlame.spatialFilterIndicator ||
        (fabs(sampleDensity - pFlame.sampleDensity) > EPSILON) || (bgTransparency != pFlame.bgTransparency) ||
        !bgColorType.equals(pFlame.bgColorType) ||
        (bgColorType.equals(BGColorType.SINGLE_COLOR) && ((bgColorRed != pFlame.bgColorRed) ||
            (bgColorGreen != pFlame.bgColorGreen) || (bgColorBlue != pFlame.bgColorBlue)))
        ||
        (bgColorType.equals(BGColorType.GRADIENT_2X2) && ((bgColorLLRed != pFlame.bgColorLLRed) || (bgColorLLGreen != pFlame.bgColorLLGreen) || (bgColorLLBlue != pFlame.bgColorLLBlue) ||
            (bgColorLRRed != pFlame.bgColorLRRed) || (bgColorLRGreen != pFlame.bgColorLRGreen) || (bgColorLRBlue != pFlame.bgColorLRBlue) ||
            (bgColorULRed != pFlame.bgColorULRed) || (bgColorULGreen != pFlame.bgColorULGreen) || (bgColorULBlue != pFlame.bgColorULBlue) ||
            (bgColorURRed != pFlame.bgColorURRed) || (bgColorURGreen != pFlame.bgColorURGreen) || (bgColorURBlue != pFlame.bgColorURBlue)))
        ||
        (bgColorType.equals(BGColorType.GRADIENT_2X2_C) && ((bgColorLLRed != pFlame.bgColorLLRed) || (bgColorLLGreen != pFlame.bgColorLLGreen) || (bgColorLLBlue != pFlame.bgColorLLBlue) ||
            (bgColorLRRed != pFlame.bgColorLRRed) || (bgColorLRGreen != pFlame.bgColorLRGreen) || (bgColorLRBlue != pFlame.bgColorLRBlue) ||
            (bgColorULRed != pFlame.bgColorULRed) || (bgColorULGreen != pFlame.bgColorULGreen) || (bgColorULBlue != pFlame.bgColorULBlue) ||
            (bgColorURRed != pFlame.bgColorURRed) || (bgColorURGreen != pFlame.bgColorURGreen) || (bgColorURBlue != pFlame.bgColorURBlue) ||
            (bgColorCCRed != pFlame.bgColorCCRed) || (bgColorCCGreen != pFlame.bgColorCCGreen) || (bgColorCCBlue != pFlame.bgColorCCBlue)))
        ||

        (fabs(gamma - pFlame.gamma) > EPSILON) || !gammaCurve.isEqual(pFlame.gammaCurve) ||
        (fabs(gammaThreshold - pFlame.gammaThreshold) > EPSILON) || !gammaThresholdCurve.isEqual(pFlame.gammaThresholdCurve) ||
        (fabs(pixelsPerUnit - pFlame.pixelsPerUnit) > EPSILON) || !pixelsPerUnitCurve.isEqual(pFlame.pixelsPerUnitCurve) ||
        (whiteLevel != pFlame.whiteLevel) || !whiteLevelCurve.isEqual(pFlame.whiteLevelCurve) ||
        (fabs(brightness - pFlame.brightness) > EPSILON) || !brightnessCurve.isEqual(pFlame.brightnessCurve) ||
        (fabs(saturation - pFlame.saturation) > EPSILON) || !saturationCurve.isEqual(pFlame.saturationCurve) ||
        (fabs(contrast - pFlame.contrast) > EPSILON) || !contrastCurve.isEqual(pFlame.contrastCurve) ||
        (fabs(vibrancy - pFlame.vibrancy) > EPSILON) || !vibrancyCurve.isEqual(pFlame.vibrancyCurve) ||
        (fabs(lowDensityBrightness - pFlame.lowDensityBrightness) > EPSILON) || !lowDensityBrightnessCurve.isEqual(pFlame.lowDensityBrightnessCurve) ||
        (fabs(balanceRed - pFlame.balanceRed) > EPSILON) || !balanceRedCurve.isEqual(pFlame.balanceRedCurve) ||
        (fabs(balanceGreen - pFlame.balanceGreen) > EPSILON) || !balanceGreenCurve.isEqual(pFlame.balanceGreenCurve) ||
        (fabs(balanceBlue - pFlame.balanceBlue) > EPSILON) || !balanceBlueCurve.isEqual(pFlame.balanceBlueCurve) ||
        (preserveZ != pFlame.preserveZ) ||
        ((resolutionProfile != null && pFlame.resolutionProfile == null) || (resolutionProfile == null && pFlame.resolutionProfile != null) ||
            (resolutionProfile != null && pFlame.resolutionProfile != null && !resolutionProfile.equals(pFlame.resolutionProfile)))
        ||
        ((qualityProfile != null && pFlame.qualityProfile == null) || (qualityProfile == null && pFlame.qualityProfile != null) ||
            (qualityProfile != null && pFlame.qualityProfile != null && !qualityProfile.equals(pFlame.qualityProfile)))
        ||
        !name.equals(pFlame.name) ||
        !bgImageFilename.equals(pFlame.bgImageFilename) ||
        (fabs(antialiasAmount - pFlame.antialiasAmount) > EPSILON) || (fabs(antialiasRadius - pFlame.antialiasRadius) > EPSILON) ||
        (layers.size() != pFlame.layers.size()) || (motionBlurLength != pFlame.motionBlurLength) || (fps != pFlame.fps) ||
        (fabs(motionBlurTimeStep - pFlame.motionBlurTimeStep) > EPSILON) || (fabs(motionBlurDecay - pFlame.motionBlurDecay) > EPSILON) ||
        (frame != pFlame.frame) || (frameCount != pFlame.frameCount) ||
        (postSymmetryType != pFlame.postSymmetryType) || (postSymmetryOrder != pFlame.postSymmetryOrder) ||
        (fabs(postSymmetryCentreX - pFlame.postSymmetryCentreX) > EPSILON) || (fabs(postSymmetryCentreY - pFlame.postSymmetryCentreY) > EPSILON) ||
        (fabs(postSymmetryDistance - pFlame.postSymmetryDistance) > EPSILON) ||
        (stereo3dMode != pFlame.stereo3dMode) || (fabs(stereo3dAngle - pFlame.stereo3dAngle) > EPSILON) ||
        (fabs(stereo3dEyeDist - pFlame.stereo3dEyeDist) > EPSILON) || (stereo3dLeftEyeColor != pFlame.stereo3dLeftEyeColor) ||
        (stereo3dRightEyeColor != pFlame.stereo3dRightEyeColor) || (stereo3dInterpolatedImageCount != pFlame.stereo3dInterpolatedImageCount) ||
        (stereo3dPreview != pFlame.stereo3dPreview) || (fabs(stereo3dFocalOffset - pFlame.stereo3dFocalOffset) > EPSILON) ||
        (stereo3dSwapSides != pFlame.stereo3dSwapSides) || (editPlane != pFlame.editPlane) ||
        (channelMixerMode != pFlame.channelMixerMode) || !mixerRRCurve.isEqual(pFlame.mixerRRCurve) || !mixerRGCurve.isEqual(pFlame.mixerRGCurve) ||
        !mixerRBCurve.isEqual(pFlame.mixerRBCurve) || !mixerGRCurve.isEqual(pFlame.mixerGRCurve) || !mixerGGCurve.isEqual(pFlame.mixerGGCurve) ||
        !mixerGBCurve.isEqual(pFlame.mixerGBCurve) || !mixerBRCurve.isEqual(pFlame.mixerBRCurve) || !mixerBGCurve.isEqual(pFlame.mixerBGCurve) ||
        !mixerBBCurve.isEqual(pFlame.mixerBBCurve) || !solidRenderSettings.isEqual(pFlame.solidRenderSettings) ||
        postBlurRadius != pFlame.postBlurRadius || (fabs(postBlurFade - pFlame.postBlurFade) > EPSILON) ||
        (fabs(postBlurFallOff - pFlame.postBlurFallOff) > EPSILON) || !zBufferFilename.equals(pFlame.zBufferFilename) ||
        (fabs(zBufferScale - pFlame.zBufferScale) > EPSILON) || (fabs(zBufferBias - pFlame.zBufferBias) > EPSILON)) {
      return false;
    }
    for (int i = 0; i < layers.size(); i++) {
      if (!layers.get(i).isEqual(pFlame.layers.get(i))) {
        return false;
      }
    }
    return true;
  }

  public boolean isBGTransparency() {
    return bgTransparency;
  }

  public void setBGTransparency(boolean bgTransparency) {
    this.bgTransparency = bgTransparency;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name != null ? name : "";
  }

  public void setLastFilename(String pLastFilename) {
    lastFilename = pLastFilename;
  }

  public String getLastFilename() {
    return lastFilename;
  }

  public boolean isNewCamDOF() {
    return newCamDOF;
  }

  public void setNewCamDOF(boolean newCamDOF) {
    this.newCamDOF = newCamDOF;
  }

  public FilterKernelType getSpatialFilterKernel() {
    return spatialFilterKernel;
  }

  public void setSpatialFilterKernel(FilterKernelType spatialFilterKernel) {
    this.spatialFilterKernel = spatialFilterKernel;
  }

  public double getDimishZ() {
    return dimishZ;
  }

  public void setDimishZ(double dimishZ) {
    this.dimishZ = dimishZ;
  }

  public int getDimishZRed() {
    return dimishZRed;
  }

  public void setDimishZRed(int dimishZRed) {
    this.dimishZRed = dimishZRed;
  }

  public int getDimishZGreen() {
    return dimishZGreen;
  }

  public void setDimishZGreen(int dimishZGreen) {
    this.dimishZGreen = dimishZGreen;
  }

  public int getDimishZBlue() {
    return dimishZBlue;
  }

  public void setDimishZBlue(int dimishZBlue) {
    this.dimishZBlue = dimishZBlue;
  }

  public double getDimZDistance() {
    return dimZDistance;
  }

  public void setDimZDistance(double dimZDistance) {
    this.dimZDistance = dimZDistance;
  }

  public Layer getFirstLayer() {
    return layers.get(0);
  }

  // only because of script-compatiblity
  @Deprecated
  public List<XForm> getFinalXForms() {
    return layers.get(0).getFinalXForms();
  }

  // only because of script-compatiblity
  @Deprecated
  public List<XForm> getXForms() {
    return layers.get(0).getXForms();
  }

  // only because of script-compatiblity
  @Deprecated
  public RGBPalette getPalette() {
    return layers.get(0).getPalette();
  }

  // only because of script-compatiblity
  @Deprecated
  public void setPalette(RGBPalette pPalette) {
    layers.get(0).setPalette(pPalette);
  }

  // only because of script-compatiblity
  @Deprecated
  public void randomizeColors() {
    layers.get(0).randomizeColors();
  }

  // only because of script-compatiblity
  @Deprecated
  public void distributeColors() {
    layers.get(0).distributeColors();
  }

  public boolean isRenderable() {
    for (Layer layer : getLayers()) {
      if (layer.isRenderable()) {
        return true;
      }
    }
    return false;
  }

  public double getAntialiasAmount() {
    return antialiasAmount;
  }

  public void setAntialiasAmount(double pAntialiasAmount) {
    antialiasAmount = pAntialiasAmount;
  }

  public double getAntialiasRadius() {
    return antialiasRadius;
  }

  public void setAntialiasRadius(double pAntialiasRadius) {
    antialiasRadius = pAntialiasRadius;
  }

  public int getMotionBlurLength() {
    return motionBlurLength;
  }

  public void setMotionBlurLength(int pMotionBlurLength) {
    motionBlurLength = pMotionBlurLength;
  }

  public double getMotionBlurTimeStep() {
    return motionBlurTimeStep;
  }

  public void setMotionBlurTimeStep(double motionBlurTimeStep) {
    this.motionBlurTimeStep = motionBlurTimeStep;
  }

  public MotionCurve getPixelsPerUnitCurve() {
    return pixelsPerUnitCurve;
  }

  public double getMotionBlurDecay() {
    return motionBlurDecay;
  }

  public void setMotionBlurDecay(double motionBlurDecay) {
    this.motionBlurDecay = motionBlurDecay;
  }

  public int getFrame() {
    return frame;
  }

  public void setFrame(int frame) {
    this.frame = frame;
  }

  public int getFrameCount() {
    return frameCount;
  }

  public void setFrameCount(int frameCount) {
    this.frameCount = frameCount;
  }

  public boolean hasPreRenderMotionProperty() {
    return brightnessCurve.isEnabled() || contrastCurve.isEnabled() || gammaCurve.isEnabled() || gammaThresholdCurve.isEnabled() || 
        vibrancyCurve.isEnabled() || saturationCurve.isEnabled() || whiteLevelCurve.isEnabled() || lowDensityBrightnessCurve.isEnabled() ||
            balanceRedCurve.isEnabled() || balanceGreenCurve.isEnabled() || balanceBlueCurve.isEnabled();
  }

  public PostSymmetryType getPostSymmetryType() {
    return postSymmetryType;
  }

  public void setPostSymmetryType(PostSymmetryType postSymmetryType) {
    this.postSymmetryType = postSymmetryType;
  }

  public int getPostSymmetryOrder() {
    return postSymmetryOrder;
  }

  public void setPostSymmetryOrder(int postSymmetryOrder) {
    this.postSymmetryOrder = postSymmetryOrder;
  }

  public double getPostSymmetryCentreX() {
    return postSymmetryCentreX;
  }

  public void setPostSymmetryCentreX(double postSymmetryCentreX) {
    this.postSymmetryCentreX = postSymmetryCentreX;
  }

  public double getPostSymmetryCentreY() {
    return postSymmetryCentreY;
  }

  public void setPostSymmetryCentreY(double postSymmetryCentreY) {
    this.postSymmetryCentreY = postSymmetryCentreY;
  }

  public double getPostSymmetryDistance() {
    return postSymmetryDistance;
  }

  public void setPostSymmetryDistance(double postSymmetryDistance) {
    this.postSymmetryDistance = postSymmetryDistance;
  }

  public double getPostSymmetryRotation() {
    return postSymmetryRotation;
  }

  public void setPostSymmetryRotation(double postSymmetryRotation) {
    this.postSymmetryRotation = postSymmetryRotation;
  }

  public Stereo3dMode getAnaglyph3dMode() {
    return stereo3dMode;
  }

  public void setAnaglyph3dMode(Stereo3dMode pAnaglyph3dMode) {
    stereo3dMode = pAnaglyph3dMode;
  }

  public double getAnaglyph3dAngle() {
    return stereo3dAngle;
  }

  public void setAnaglyph3dAngle(double pAnaglyph3dAngle) {
    stereo3dAngle = pAnaglyph3dAngle;
  }

  public double getAnaglyph3dEyeDist() {
    return stereo3dEyeDist;
  }

  public void setAnaglyph3dEyeDist(double pAnaglyph3dEyeDist) {
    stereo3dEyeDist = pAnaglyph3dEyeDist;
  }

  public Stereo3dColor getAnaglyph3dLeftEyeColor() {
    return stereo3dLeftEyeColor;
  }

  public void setAnaglyph3dLeftEyeColor(Stereo3dColor pAnaglyph3dLeftEyeColor) {
    stereo3dLeftEyeColor = pAnaglyph3dLeftEyeColor;
  }

  public Stereo3dColor getAnaglyph3dRightEyeColor() {
    return stereo3dRightEyeColor;
  }

  public void setAnaglyph3dRightEyeColor(Stereo3dColor pAnaglyph3dRightEyeColor) {
    stereo3dRightEyeColor = pAnaglyph3dRightEyeColor;
  }

  public int calcStereo3dSampleMultiplier() {
    switch (getStereo3dMode()) {
      case ANAGLYPH:
      case SIDE_BY_SIDE:
        return 2;
      case INTERPOLATED_IMAGES:
        return getStereo3dInterpolatedImageCount();
      default:
        return 1;
    }
  }

  public int calcPostSymmetrySampleMultiplier() {
    switch (getPostSymmetryType()) {
      case POINT:
        return getPostSymmetryOrder();
      case X_AXIS:
      case Y_AXIS:
        return 2;
      default:
        return 1;
    }
  }

  public Stereo3dMode getStereo3dMode() {
    return stereo3dMode;
  }

  public void setStereo3dMode(Stereo3dMode stereo3dMode) {
    this.stereo3dMode = stereo3dMode;
  }

  public double getStereo3dAngle() {
    return stereo3dAngle;
  }

  public void setStereo3dAngle(double pStereo3dAngle) {
    stereo3dAngle = pStereo3dAngle;
  }

  public double getStereo3dEyeDist() {
    return stereo3dEyeDist;
  }

  public void setStereo3dEyeDist(double pStereo3dEyeDist) {
    stereo3dEyeDist = pStereo3dEyeDist;
  }

  public Stereo3dColor getStereo3dLeftEyeColor() {
    return stereo3dLeftEyeColor;
  }

  public void setStereo3dLeftEyeColor(Stereo3dColor pStereo3dLeftEyeColor) {
    stereo3dLeftEyeColor = pStereo3dLeftEyeColor;
  }

  public Stereo3dColor getStereo3dRightEyeColor() {
    return stereo3dRightEyeColor;
  }

  public void setStereo3dRightEyeColor(Stereo3dColor pStereo3dRightEyeColor) {
    stereo3dRightEyeColor = pStereo3dRightEyeColor;
  }

  public int getStereo3dInterpolatedImageCount() {
    return stereo3dInterpolatedImageCount;
  }

  public void setStereo3dInterpolatedImageCount(int pStereo3dInterpolatedImageCount) {
    stereo3dInterpolatedImageCount = pStereo3dInterpolatedImageCount;
  }

  public Stereo3dPreview getStereo3dPreview() {
    return stereo3dPreview;
  }

  public void setStereo3dPreview(Stereo3dPreview pStereo3dPreview) {
    stereo3dPreview = pStereo3dPreview;
  }

  public double getStereo3dFocalOffset() {
    return stereo3dFocalOffset;
  }

  public void setStereo3dFocalOffset(double pStereo3dFocalOffset) {
    stereo3dFocalOffset = pStereo3dFocalOffset;
  }

  public boolean isDOFActive() {
    return fabs(getCamDOF()) > MathLib.EPSILON;
  }

  public boolean is3dProjectionRequired() {
    return getSolidRenderSettings().isSolidRenderingEnabled() ||
        fabs(getCamYaw()) > EPSILON || fabs(getCamPitch()) > EPSILON || fabs(getCamBank()) > EPSILON || fabs(getCamPerspective()) > EPSILON || isDOFActive() ||
        fabs(getDimishZ()) > EPSILON || fabs(getCamPosX()) > EPSILON || fabs(getCamPosY()) > EPSILON || fabs(getCamPosZ()) > EPSILON;
  }

  public MotionCurve getCamPitchCurve() {
    return camPitchCurve;
  }

  public MotionCurve getCamRollCurve() {
    return camRollCurve;
  }

  public MotionCurve getCamYawCurve() {
    return camYawCurve;
  }

  public MotionCurve getCamBankCurve() {
    return camBankCurve;
  }

  public boolean isStereo3dSwapSides() {
    return stereo3dSwapSides;
  }

  public void setStereo3dSwapSides(boolean pStereo3dSwapSides) {
    stereo3dSwapSides = pStereo3dSwapSides;
  }

  public MotionCurve getCentreXCurve() {
    return centreXCurve;
  }

  public MotionCurve getCentreYCurve() {
    return centreYCurve;
  }

  public double getCamPosX() {
    return camPosX;
  }

  public void setCamPosX(double camPosX) {
    this.camPosX = camPosX;
  }

  public double getCamPosY() {
    return camPosY;
  }

  public void setCamPosY(double camPosY) {
    this.camPosY = camPosY;
  }

  public double getCamPosZ() {
    return camPosZ;
  }

  public void setCamPosZ(double camPosZ) {
    this.camPosZ = camPosZ;
  }

  public MotionCurve getCamPosXCurve() {
    return camPosXCurve;
  }

  public MotionCurve getCamPosYCurve() {
    return camPosYCurve;
  }

  public MotionCurve getCamPosZCurve() {
    return camPosZCurve;
  }

  public double getSaturation() {
    return saturation;
  }

  public void setSaturation(double pSaturation) {
    saturation = pSaturation;
  }

  public ChannelMixerMode getChannelMixerMode() {
    return channelMixerMode;
  }

  public void setChannelMixerMode(ChannelMixerMode pChannelMixerMode) {
    channelMixerMode = pChannelMixerMode;
  }

  public MotionCurve getMixerRRCurve() {
    return mixerRRCurve;
  }

  public MotionCurve getMixerRGCurve() {
    return mixerRGCurve;
  }

  public MotionCurve getMixerRBCurve() {
    return mixerRBCurve;
  }

  public MotionCurve getMixerGRCurve() {
    return mixerGRCurve;
  }

  public MotionCurve getMixerGGCurve() {
    return mixerGGCurve;
  }

  public MotionCurve getMixerGBCurve() {
    return mixerGBCurve;
  }

  public MotionCurve getMixerBRCurve() {
    return mixerBRCurve;
  }

  public MotionCurve getMixerBGCurve() {
    return mixerBGCurve;
  }

  public MotionCurve getMixerBBCurve() {
    return mixerBBCurve;
  }

  public DOFBlurShapeType getCamDOFShape() {
    return camDOFShape;
  }

  public void setCamDOFShape(DOFBlurShapeType pCamDOFShape) {
    camDOFShape = pCamDOFShape;
  }

  public double getCamDOFScale() {
    return camDOFScale;
  }

  public void setCamDOFScale(double pCamDOFScale) {
    camDOFScale = pCamDOFScale;
  }

  public double getCamDOFAngle() {
    return camDOFAngle;
  }

  public void setCamDOFAngle(double pCamDOFAngle) {
    camDOFAngle = pCamDOFAngle;
  }

  public double getCamDOFParam1() {
    return camDOFParam1;
  }

  public void setCamDOFParam1(double pCamDOFParam1) {
    camDOFParam1 = pCamDOFParam1;
  }

  public double getCamDOFParam2() {
    return camDOFParam2;
  }

  public void setCamDOFParam2(double pCamDOFParam2) {
    camDOFParam2 = pCamDOFParam2;
  }

  public double getCamDOFParam3() {
    return camDOFParam3;
  }

  public void setCamDOFParam3(double pCamDOFParam3) {
    camDOFParam3 = pCamDOFParam3;
  }

  public double getCamDOFParam4() {
    return camDOFParam4;
  }

  public void setCamDOFParam4(double pCamDOFParam4) {
    camDOFParam4 = pCamDOFParam4;
  }

  public double getCamDOFParam5() {
    return camDOFParam5;
  }

  public void setCamDOFParam5(double pCamDOFParam5) {
    camDOFParam5 = pCamDOFParam5;
  }

  public double getCamDOFParam6() {
    return camDOFParam6;
  }

  public void setCamDOFParam6(double pCamDOFParam6) {
    camDOFParam6 = pCamDOFParam6;
  }

  public double getCamDOFFade() {
    return camDOFFade;
  }

  public void setCamDOFFade(double pCamDOFFade) {
    camDOFFade = pCamDOFFade;
  }

  public EditPlane getEditPlane() {
    return editPlane;
  }

  public void setEditPlane(EditPlane editPlane) {
    this.editPlane = editPlane;
  }

  public String getBGImageFilename() {
    return bgImageFilename;
  }

  public void setBGImageFilename(String pBGImageFilename) {
    bgImageFilename = pBGImageFilename != null ? pBGImageFilename : "";
  }

  public int getFps() {
    return fps;
  }

  public void setFps(int pFps) {
    fps = pFps;
  }

  public boolean isPreserveZ() {
    return preserveZ;
  }

  public void setPreserveZ(boolean preserveZ) {
    this.preserveZ = preserveZ;
  }

  public MotionCurve getCamZoomCurve() {
    return camZoomCurve;
  }

  public int getSpatialOversampling() {
    return spatialOversampling;
  }

  public void setSpatialOversampling(int pSpatialOversampling) {
    spatialOversampling = pSpatialOversampling;
    if (spatialOversampling < 1) {
      spatialOversampling = 1;
    }
    else if (spatialOversampling > Tools.MAX_SPATIAL_OVERSAMPLING) {
      spatialOversampling = Tools.MAX_SPATIAL_OVERSAMPLING;
    }
  }

  public void applyFastOversamplingSettings() {
    setSpatialFilterRadius(0.0);
    setSpatialOversampling(1);
    setPostNoiseFilter(false);
    setPostOptiXDenoiser(false);
  }

  public void applyDefaultOversamplingSettings() {
    Prefs prefs = Prefs.getPrefs();
    setSpatialFilterRadius(prefs.getTinaDefaultSpatialFilterRadius());
    setSpatialOversampling(getSolidRenderSettings().isSolidRenderingEnabled() ? DFLT_SOLID_SPATIAL_OVERSAMPLING : prefs.getTinaDefaultSpatialOversampling());
    setPostNoiseFilter(prefs.isTinaDefaultPostNoiseFilter());
    setPostOptiXDenoiser(prefs.isTinaDefaultPostOptiXDenoiser() && OptixCmdLineDenoiser.isAvailable());
    setPostOptiXDenoiserBlend(prefs.getTinaDefaultPostOptiXDenoiserBlend());
  }

  public boolean isPostNoiseFilter() {
    return postNoiseFilter;
  }

  public void setPostNoiseFilter(boolean pPostNoiseFilter) {
    postNoiseFilter = pPostNoiseFilter;
  }

  public double getPostNoiseFilterThreshold() {
    return postNoiseFilterThreshold;
  }

  public void setPostNoiseFilterThreshold(double pPostNoiseFilterThreshold) {
    postNoiseFilterThreshold = pPostNoiseFilterThreshold;
  }

  public boolean isPostOptiXDenoiser() {
    return postOptiXDenoiser;
  }

  public void setPostOptiXDenoiser(boolean postOptiXDenoiser) {
    this.postOptiXDenoiser = postOptiXDenoiser;
  }

  public double getPostOptiXDenoiserBlend() {
    return postOptiXDenoiserBlend;
  }

  public void setPostOptiXDenoiserBlend(double postOptiXDenoiserBlend) {
    this.postOptiXDenoiserBlend = postOptiXDenoiserBlend;
  }

  public double getForegroundOpacity() {
    return foregroundOpacity;
  }

  public void setForegroundOpacity(double pForegroundOpacity) {
    foregroundOpacity = pForegroundOpacity;
  }

  public SolidRenderSettings getSolidRenderSettings() {
    return solidRenderSettings;
  }

  public int getPostBlurRadius() {
    return postBlurRadius;
  }

  public void setPostBlurRadius(int postBlurRadius) {
    this.postBlurRadius = postBlurRadius;
  }

  public double getPostBlurFade() {
    return postBlurFade;
  }

  public void setPostBlurFade(double postBlurFade) {
    this.postBlurFade = postBlurFade;
  }

  public double getPostBlurFallOff() {
    return postBlurFallOff;
  }

  public void setPostBlurFallOff(double postBlurFallOff) {
    this.postBlurFallOff = postBlurFallOff;
  }

  public boolean isWithShadows() {
    boolean res = getSolidRenderSettings().isSolidRenderingEnabled() && ShadowType.areShadowsEnabled(getSolidRenderSettings().getShadowType());
    if (res) {
      res = false;
      for (DistantLight light : getSolidRenderSettings().getLights()) {
        if (light.isCastShadows()) {
          res = true;
          break;
        }
      }
    }
    return res;
  }

  public int getActiveLightCount() {
    return isWithShadows() ? getSolidRenderSettings().getLights().size() : 0;
  }

  public void setDefaultSolidRenderingSettings() {
    setSpatialOversampling(DFLT_SOLID_SPATIAL_OVERSAMPLING);
    setSpatialFilterRadius(0.5);
    setSpatialFilteringType(FilteringType.GLOBAL_SMOOTHING);
    setSpatialFilterKernel(FilterKernelType.GAUSSIAN);
    setAntialiasAmount(0.0);
    setAntialiasRadius(0.0);
  }

  public double getZBufferScale() {
    return zBufferScale;
  }

  public void setZBufferScale(double zBufferScale) {
    this.zBufferScale = zBufferScale;
  }

  public double getZBufferBias() {
    return zBufferBias;
  }

  public void setZBufferBias(double zBufferBias) {
    this.zBufferBias = zBufferBias;
  }
  
  public ZBufferFilename getZBufferFilename() {
    return zBufferFilename;
  }
  
  public void setZBufferFilename(ZBufferFilename zBufferFilename) {
    this.zBufferFilename = zBufferFilename;
  }

  public double getLowDensityBrightness() {
    return lowDensityBrightness;
  }

  public void setLowDensityBrightness(double lowDensityBrightness) {
    this.lowDensityBrightness = lowDensityBrightness;
  }

  public double getBalanceRed() {
    return balanceRed;
  }

  public void setBalanceRed(double balanceRed) {
    this.balanceRed = balanceRed;
  }

  public double getBalanceGreen() {
    return balanceGreen;
  }

  public void setBalanceGreen(double balanceGreen) {
    this.balanceGreen = balanceGreen;
  }

  public double getBalanceBlue() {
    return balanceBlue;
  }

  public void setBalanceBlue(double balanceBlue) {
    this.balanceBlue = balanceBlue;
  }

  public BGColorType getBgColorType() {
    return bgColorType;
  }

  public void setBgColorType(BGColorType bgColorType) {
    this.bgColorType = bgColorType;
  }

  public int getBgColorULRed() {
    return bgColorULRed;
  }

  public void setBgColorULRed(int bgColorULRed) {
    this.bgColorULRed = bgColorULRed;
  }

  public int getBgColorULGreen() {
    return bgColorULGreen;
  }

  public void setBgColorULGreen(int bgColorULGreen) {
    this.bgColorULGreen = bgColorULGreen;
  }

  public int getBgColorULBlue() {
    return bgColorULBlue;
  }

  public void setBgColorULBlue(int bgColorULBlue) {
    this.bgColorULBlue = bgColorULBlue;
  }

  public int getBgColorURRed() {
    return bgColorURRed;
  }

  public void setBgColorURRed(int bgColorURRed) {
    this.bgColorURRed = bgColorURRed;
  }

  public int getBgColorURGreen() {
    return bgColorURGreen;
  }

  public void setBgColorURGreen(int bgColorURGreen) {
    this.bgColorURGreen = bgColorURGreen;
  }

  public int getBgColorURBlue() {
    return bgColorURBlue;
  }

  public void setBgColorURBlue(int bgColorURBlue) {
    this.bgColorURBlue = bgColorURBlue;
  }

  public int getBgColorLLRed() {
    return bgColorLLRed;
  }

  public void setBgColorLLRed(int bgColorLLRed) {
    this.bgColorLLRed = bgColorLLRed;
  }

  public int getBgColorLLGreen() {
    return bgColorLLGreen;
  }

  public void setBgColorLLGreen(int bgColorLLGreen) {
    this.bgColorLLGreen = bgColorLLGreen;
  }

  public int getBgColorLLBlue() {
    return bgColorLLBlue;
  }

  public void setBgColorLLBlue(int bgColorLLBlue) {
    this.bgColorLLBlue = bgColorLLBlue;
  }

  public int getBgColorLRRed() {
    return bgColorLRRed;
  }

  public void setBgColorLRRed(int bgColorLRRed) {
    this.bgColorLRRed = bgColorLRRed;
  }

  public int getBgColorLRGreen() {
    return bgColorLRGreen;
  }

  public void setBgColorLRGreen(int bgColorLRGreen) {
    this.bgColorLRGreen = bgColorLRGreen;
  }

  public int getBgColorLRBlue() {
    return bgColorLRBlue;
  }

  public void setBgColorLRBlue(int bgColorLRBlue) {
    this.bgColorLRBlue = bgColorLRBlue;
  }

  public int getBgColorCCRed() {
    return bgColorCCRed;
  }

  public void setBgColorCCRed(int bgColorCCRed) {
    this.bgColorCCRed = bgColorCCRed;
  }

  public int getBgColorCCGreen() {
    return bgColorCCGreen;
  }

  public void setBgColorCCGreen(int bgColorCCGreen) {
    this.bgColorCCGreen = bgColorCCGreen;
  }

  public int getBgColorCCBlue() {
    return bgColorCCBlue;
  }

  public void setBgColorCCBlue(int bgColorCCBlue) {
    this.bgColorCCBlue = bgColorCCBlue;
  }

  public FilteringType getSpatialFilteringType() {
    return spatialFilteringType;
  }

  public void setSpatialFilteringType(FilteringType spatialFilteringType) {
    this.spatialFilteringType = spatialFilteringType;
  }

  public boolean isSpatialFilterIndicator() {
    return spatialFilterIndicator;
  }

  public void setSpatialFilterIndicator(boolean spatialFilterIndicator) {
    this.spatialFilterIndicator = spatialFilterIndicator;
  }

  public double getSpatialFilterSharpness() {
    return spatialFilterSharpness;
  }

  public void setSpatialFilterSharpness(double spatialFilterSharpness) {
    this.spatialFilterSharpness = spatialFilterSharpness;
  }

  public double getSpatialFilterLowDensity() {
    return spatialFilterLowDensity;
  }

  public void setSpatialFilterLowDensity(double spatialFilterLowDensity) {
    this.spatialFilterLowDensity = spatialFilterLowDensity;
  }

}