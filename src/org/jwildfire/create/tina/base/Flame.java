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
package org.jwildfire.create.tina.base;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.filter.FilterKernelType;

public class Flame implements Assignable<Flame>, Serializable {
  private static final long serialVersionUID = 1L;
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
  @AnimAware
  private double camDOF;
  private final MotionCurve camDOFCurve = new MotionCurve();
  @AnimAware
  private double camDOFExponent;
  private final MotionCurve camDOFExponentCurve = new MotionCurve();
  @AnimAware
  private double camDOFArea;
  private final MotionCurve camDOFAreaCurve = new MotionCurve();
  private boolean newCamDOF;
  private double spatialFilterRadius;
  private FilterKernelType spatialFilterKernel;
  private double sampleDensity;
  private boolean bgTransparency;
  @AnimAware
  private int bgColorRed;
  @AnimAware
  private int bgColorGreen;
  @AnimAware
  private int bgColorBlue;
  private double gamma;
  private final MotionCurve gammaCurve = new MotionCurve();
  private double gammaThreshold;
  private final MotionCurve gammaThresholdCurve = new MotionCurve();
  private double pixelsPerUnit;
  private final MotionCurve pixelsPerUnitCurve = new MotionCurve();
  private int whiteLevel;
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
  private boolean preserveZ;
  private String resolutionProfile;
  private String qualityProfile;
  private String name = "";
  @AnimAware
  private final List<Layer> layers = new ArrayList<Layer>();

  @AnimAware
  private ShadingInfo shadingInfo = new ShadingInfo();
  private String lastFilename = null;
  private double antialiasAmount = 0.75;
  private double antialiasRadius = 0.36;

  private int motionBlurLength = 0;
  private double motionBlurTimeStep = 0.15;
  private double motionBlurDecay = 0.03;

  private int frame = 1;
  private int frameCount = 300;

  private PostSymmetryType postSymmetryType = PostSymmetryType.NONE;
  private int postSymmetryOrder = 3;
  private double postSymmetryCentreX = 0.0;
  private double postSymmetryCentreY = 0.0;
  private double postSymmetryDistance = 1.25;
  private double postSymmetryRotation = 6.0;

  private Stereo3dMode stereo3dMode = Stereo3dMode.NONE;
  private double stereo3dAngle = 1.6;
  private double stereo3dEyeDist = 0.032;
  private double stereo3dFocalOffset = 0.5;
  private Stereo3dColor stereo3dLeftEyeColor = Stereo3dColor.RED;
  private Stereo3dColor stereo3dRightEyeColor = Stereo3dColor.CYAN;
  private Stereo3dPreview stereo3dPreview = Stereo3dPreview.ANAGLYPH;
  private int stereo3dInterpolatedImageCount = 3;
  private boolean stereo3dSwapSides = false;

  public Flame() {
    layers.clear();
    layers.add(new Layer());
    sampleDensity = 100.0;
    bgTransparency = true;
    bgColorRed = bgColorGreen = bgColorBlue = 0;
    brightness = 4.0;
    contrast = 1;
    vibrancy = 1;
    gamma = 4.0;
    centreX = 0.0;
    centreY = 0.0;
    camRoll = 0.0;
    camPitch = 0.0;
    camYaw = 0.0;
    camPerspective = 0.0;
    camZoom = 1.0;
    focusX = 0.0;
    focusY = 0.0;
    focusZ = 0.0;
    dimishZ = 0.0;
    camDOF = 0.0;
    camPosX = 0.0;
    camPosY = 0.0;
    camPosZ = 0.0;
    camZ = 0.0;
    newCamDOF = false;
    camDOFArea = 0.5;
    camDOFExponent = 2.0;
    gammaThreshold = 0.01;
    pixelsPerUnit = 50;
    whiteLevel = 240;
    saturation = 1.0;
    name = "";
    spatialFilterRadius = 0.0;
    spatialFilterKernel = FilterKernelType.GAUSSIAN;
    shadingInfo.init();
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

  public int getWhiteLevel() {
    return whiteLevel;
  }

  public void setWhiteLevel(int whiteLevel) {
    this.whiteLevel = whiteLevel;
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

  public int getBGColorRed() {
    return bgColorRed;
  }

  public void setBGColorRed(int bgColorRed) {
    this.bgColorRed = bgColorRed;
  }

  public int getBGColorGreen() {
    return bgColorGreen;
  }

  public void setBGColorGreen(int bgColorGreen) {
    this.bgColorGreen = bgColorGreen;
  }

  public int getBGColorBlue() {
    return bgColorBlue;
  }

  public void setBGColorBlue(int bgColorBlue) {
    this.bgColorBlue = bgColorBlue;
  }

  @Override
  public Flame makeCopy() {
    Flame res = new Flame();
    res.assign(this);
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

  public ShadingInfo getShadingInfo() {
    return shadingInfo;
  }

  public void setShadingInfo(ShadingInfo shadingInfo) {
    this.shadingInfo = shadingInfo;
  }

  public boolean isPreserveZ() {
    return preserveZ;
  }

  public void setPreserveZ(boolean preserveZ) {
    this.preserveZ = preserveZ;
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
    camPosX = pFlame.camPosX;
    camPosXCurve.assign(pFlame.camPosXCurve);
    camPosY = pFlame.camPosY;
    camPosYCurve.assign(pFlame.camPosYCurve);
    camPosZ = pFlame.camPosZ;
    camPosZCurve.assign(pFlame.camPosZCurve);
    camZ = pFlame.camZ;
    camZCurve.assign(pFlame.camZCurve);
    camDOF = pFlame.camDOF;
    camDOFCurve.assign(pFlame.camDOFCurve);
    newCamDOF = pFlame.newCamDOF;
    camDOFArea = pFlame.camDOFArea;
    camDOFAreaCurve.assign(pFlame.camDOFAreaCurve);
    camDOFExponent = pFlame.camDOFExponent;
    camDOFExponentCurve.assign(pFlame.camDOFExponentCurve);
    spatialFilterRadius = pFlame.spatialFilterRadius;
    spatialFilterKernel = pFlame.spatialFilterKernel;
    sampleDensity = pFlame.sampleDensity;
    bgTransparency = pFlame.bgTransparency;
    bgColorRed = pFlame.bgColorRed;
    bgColorGreen = pFlame.bgColorGreen;
    bgColorBlue = pFlame.bgColorBlue;
    gamma = pFlame.gamma;
    gammaCurve.assign(pFlame.gammaCurve);
    gammaThreshold = pFlame.gammaThreshold;
    gammaThresholdCurve.assign(pFlame.gammaThresholdCurve);
    pixelsPerUnit = pFlame.pixelsPerUnit;
    pixelsPerUnitCurve.assign(pFlame.pixelsPerUnitCurve);
    whiteLevel = pFlame.whiteLevel;
    brightness = pFlame.brightness;
    brightnessCurve.assign(pFlame.brightnessCurve);
    saturation = pFlame.saturation;
    saturationCurve.assign(pFlame.saturationCurve);
    contrast = pFlame.contrast;
    contrastCurve.assign(pFlame.contrastCurve);
    vibrancy = pFlame.vibrancy;
    vibrancyCurve.assign(pFlame.vibrancyCurve);
    preserveZ = pFlame.preserveZ;
    resolutionProfile = pFlame.resolutionProfile;
    qualityProfile = pFlame.qualityProfile;
    shadingInfo.assign(pFlame.shadingInfo);
    name = pFlame.name;
    lastFilename = pFlame.lastFilename;
    antialiasAmount = pFlame.antialiasAmount;
    antialiasRadius = pFlame.antialiasRadius;

    motionBlurLength = pFlame.motionBlurLength;
    motionBlurTimeStep = pFlame.motionBlurTimeStep;
    motionBlurDecay = pFlame.motionBlurDecay;

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

    layers.clear();
    for (Layer layer : pFlame.getLayers()) {
      layers.add(layer.makeCopy());
    }

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
        (fabs(camZoom - pFlame.camZoom) > EPSILON) || !camZoomCurve.isEqual(pFlame.camZoomCurve) ||
        (fabs(focusX - pFlame.focusX) > EPSILON) || !focusXCurve.isEqual(pFlame.focusXCurve) ||
        (fabs(focusY - pFlame.focusY) > EPSILON) || !focusYCurve.isEqual(pFlame.focusYCurve) ||
        (fabs(focusZ - pFlame.focusZ) > EPSILON) || !focusZCurve.isEqual(pFlame.focusZCurve) ||
        (fabs(dimishZ - pFlame.dimishZ) > EPSILON) || !dimishZCurve.isEqual(pFlame.dimishZCurve) ||
        (fabs(camDOF - pFlame.camDOF) > EPSILON) || !camDOFCurve.isEqual(pFlame.camDOFCurve) ||
        (fabs(camDOFArea - pFlame.camDOFArea) > EPSILON) || !camDOFAreaCurve.isEqual(pFlame.camDOFAreaCurve) ||
        (fabs(camDOFExponent - pFlame.camDOFExponent) > EPSILON) || !camDOFExponentCurve.isEqual(pFlame.camDOFExponentCurve) ||
        (fabs(camPosX - pFlame.camPosX) > EPSILON) || !camPosXCurve.isEqual(pFlame.camPosXCurve) ||
        (fabs(camPosY - pFlame.camPosY) > EPSILON) || !camPosYCurve.isEqual(pFlame.camPosYCurve) ||
        (fabs(camPosZ - pFlame.camPosZ) > EPSILON) || !camPosZCurve.isEqual(pFlame.camPosZCurve) ||
        (fabs(camZ - pFlame.camZ) > EPSILON) || !camZCurve.isEqual(pFlame.camZCurve) ||
        (newCamDOF != pFlame.newCamDOF) || (fabs(spatialFilterRadius - pFlame.spatialFilterRadius) > EPSILON) ||
        !spatialFilterKernel.equals(pFlame.spatialFilterKernel) ||
        (fabs(sampleDensity - pFlame.sampleDensity) > EPSILON) || (bgTransparency != pFlame.bgTransparency) || (bgColorRed != pFlame.bgColorRed) ||
        (bgColorGreen != pFlame.bgColorGreen) || (bgColorBlue != pFlame.bgColorBlue) ||
        (fabs(gamma - pFlame.gamma) > EPSILON) || !gammaCurve.isEqual(pFlame.gammaCurve) ||
        (fabs(gammaThreshold - pFlame.gammaThreshold) > EPSILON) || !gammaThresholdCurve.isEqual(pFlame.gammaThresholdCurve) ||
        (fabs(pixelsPerUnit - pFlame.pixelsPerUnit) > EPSILON) || !pixelsPerUnitCurve.isEqual(pFlame.pixelsPerUnitCurve) ||
        (whiteLevel != pFlame.whiteLevel) ||
        (fabs(brightness - pFlame.brightness) > EPSILON) || !brightnessCurve.isEqual(pFlame.brightnessCurve) ||
        (fabs(saturation - pFlame.saturation) > EPSILON) || !saturationCurve.isEqual(pFlame.saturationCurve) ||
        (fabs(contrast - pFlame.contrast) > EPSILON) || !contrastCurve.isEqual(pFlame.contrastCurve) ||
        (fabs(vibrancy - pFlame.vibrancy) > EPSILON) || !vibrancyCurve.isEqual(pFlame.vibrancyCurve) ||
        (preserveZ != pFlame.preserveZ) ||
        ((resolutionProfile != null && pFlame.resolutionProfile == null) || (resolutionProfile == null && pFlame.resolutionProfile != null) ||
        (resolutionProfile != null && pFlame.resolutionProfile != null && !resolutionProfile.equals(pFlame.resolutionProfile))) ||
        ((qualityProfile != null && pFlame.qualityProfile == null) || (qualityProfile == null && pFlame.qualityProfile != null) ||
        (qualityProfile != null && pFlame.qualityProfile != null && !qualityProfile.equals(pFlame.qualityProfile))) ||
        !shadingInfo.isEqual(pFlame.shadingInfo) || !name.equals(pFlame.name) ||
        (fabs(antialiasAmount - pFlame.antialiasAmount) > EPSILON) || (fabs(antialiasRadius - pFlame.antialiasRadius) > EPSILON) ||
        (layers.size() != pFlame.layers.size()) || (motionBlurLength != pFlame.motionBlurLength) ||
        (fabs(motionBlurTimeStep - pFlame.motionBlurTimeStep) > EPSILON) || (fabs(motionBlurDecay - pFlame.motionBlurDecay) > EPSILON) ||
        (frame != pFlame.frame) || (frameCount != pFlame.frameCount) ||
        (postSymmetryType != pFlame.postSymmetryType) || (postSymmetryOrder != pFlame.postSymmetryOrder) ||
        (fabs(postSymmetryCentreX - pFlame.postSymmetryCentreX) > EPSILON) || (fabs(postSymmetryCentreY - pFlame.postSymmetryCentreY) > EPSILON) ||
        (fabs(postSymmetryDistance - pFlame.postSymmetryDistance) > EPSILON) || (fabs(postSymmetryDistance - pFlame.postSymmetryDistance) > EPSILON) ||
        (stereo3dMode != pFlame.stereo3dMode) || (fabs(stereo3dAngle - pFlame.stereo3dAngle) > EPSILON) ||
        (fabs(stereo3dEyeDist - pFlame.stereo3dEyeDist) > EPSILON) || (stereo3dLeftEyeColor != pFlame.stereo3dLeftEyeColor) ||
        (stereo3dRightEyeColor != pFlame.stereo3dRightEyeColor) || (stereo3dInterpolatedImageCount != pFlame.stereo3dInterpolatedImageCount) ||
        (stereo3dPreview != pFlame.stereo3dPreview) || (fabs(stereo3dFocalOffset - pFlame.stereo3dFocalOffset) > EPSILON) ||
        (stereo3dSwapSides != pFlame.stereo3dSwapSides)) {
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

  public void setAntialiasAmount(double antialiasAmount) {
    this.antialiasAmount = antialiasAmount;
  }

  public double getAntialiasRadius() {
    return antialiasRadius;
  }

  public void setAntialiasRadius(double antialiasRadius) {
    this.antialiasRadius = antialiasRadius;
  }

  public int getMotionBlurLength() {
    return motionBlurLength;
  }

  public void setMotionBlurLength(int motionBlurLength) {
    this.motionBlurLength = motionBlurLength;
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
    return brightnessCurve.isEnabled() || contrastCurve.isEnabled() || gammaCurve.isEnabled() || gammaThresholdCurve.isEnabled() || vibrancyCurve.isEnabled() || saturationCurve.isEnabled();
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

  public void setAnaglyph3dMode(Stereo3dMode anaglyph3dMode) {
    this.stereo3dMode = anaglyph3dMode;
  }

  public double getAnaglyph3dAngle() {
    return stereo3dAngle;
  }

  public void setAnaglyph3dAngle(double anaglyph3dAngle) {
    this.stereo3dAngle = anaglyph3dAngle;
  }

  public double getAnaglyph3dEyeDist() {
    return stereo3dEyeDist;
  }

  public void setAnaglyph3dEyeDist(double anaglyph3dEyeDist) {
    this.stereo3dEyeDist = anaglyph3dEyeDist;
  }

  public Stereo3dColor getAnaglyph3dLeftEyeColor() {
    return stereo3dLeftEyeColor;
  }

  public void setAnaglyph3dLeftEyeColor(Stereo3dColor anaglyph3dLeftEyeColor) {
    this.stereo3dLeftEyeColor = anaglyph3dLeftEyeColor;
  }

  public Stereo3dColor getAnaglyph3dRightEyeColor() {
    return stereo3dRightEyeColor;
  }

  public void setAnaglyph3dRightEyeColor(Stereo3dColor anaglyph3dRightEyeColor) {
    this.stereo3dRightEyeColor = anaglyph3dRightEyeColor;
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

  public void setStereo3dAngle(double stereo3dAngle) {
    this.stereo3dAngle = stereo3dAngle;
  }

  public double getStereo3dEyeDist() {
    return stereo3dEyeDist;
  }

  public void setStereo3dEyeDist(double stereo3dEyeDist) {
    this.stereo3dEyeDist = stereo3dEyeDist;
  }

  public Stereo3dColor getStereo3dLeftEyeColor() {
    return stereo3dLeftEyeColor;
  }

  public void setStereo3dLeftEyeColor(Stereo3dColor stereo3dLeftEyeColor) {
    this.stereo3dLeftEyeColor = stereo3dLeftEyeColor;
  }

  public Stereo3dColor getStereo3dRightEyeColor() {
    return stereo3dRightEyeColor;
  }

  public void setStereo3dRightEyeColor(Stereo3dColor stereo3dRightEyeColor) {
    this.stereo3dRightEyeColor = stereo3dRightEyeColor;
  }

  public int getStereo3dInterpolatedImageCount() {
    return stereo3dInterpolatedImageCount;
  }

  public void setStereo3dInterpolatedImageCount(int stereo3dInterpolatedImageCount) {
    this.stereo3dInterpolatedImageCount = stereo3dInterpolatedImageCount;
  }

  public Stereo3dPreview getStereo3dPreview() {
    return stereo3dPreview;
  }

  public void setStereo3dPreview(Stereo3dPreview stereo3dPreview) {
    this.stereo3dPreview = stereo3dPreview;
  }

  public double getStereo3dFocalOffset() {
    return stereo3dFocalOffset;
  }

  public void setStereo3dFocalOffset(double stereo3dFocalOffset) {
    this.stereo3dFocalOffset = stereo3dFocalOffset;
  }

  public boolean isDOFActive() {
    return fabs(getCamDOF()) > MathLib.EPSILON;
  }

  public boolean is3dProjectionRequired() {
    return fabs(getCamYaw()) > EPSILON || fabs(getCamPitch()) > EPSILON || fabs(getCamPerspective()) > EPSILON || isDOFActive() ||
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

}
