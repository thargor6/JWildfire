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
package org.jwildfire.create.tina.base;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.QualityProfile;
import org.jwildfire.base.ResolutionProfile;
import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.render.filter.FilterKernelType;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;

public class Flame implements Assignable<Flame>, Serializable {
  private static final long serialVersionUID = 1L;
  @AnimAware
  private double centreX;
  @AnimAware
  private double centreY;
  private int width;
  private int height;
  @AnimAware
  private double camPitch;
  @AnimAware
  private double camYaw;
  @AnimAware
  private double camPerspective;
  @AnimAware
  private double camRoll;
  @AnimAware
  private double camZoom;
  @AnimAware
  private double camZ;
  @AnimAware
  private double focusX;
  @AnimAware
  private double focusY;
  @AnimAware
  private double focusZ;
  @AnimAware
  private double dimishZ;
  @AnimAware
  private double camDOF;
  @AnimAware
  private double camDOFExponent;
  @AnimAware
  private double camDOFArea;
  private boolean newCamDOF;
  private double spatialFilterRadius;
  private FilterKernelType spatialFilterKernel;
  private boolean deFilterEnabled;
  private double deFilterMaxRadius;
  private double deFilterMinRadius;
  private double deFilterCurve;
  private FilterKernelType deFilterKernel;
  private double sampleDensity;
  private boolean bgTransparency;
  @AnimAware
  private int bgColorRed;
  @AnimAware
  private int bgColorGreen;
  @AnimAware
  private int bgColorBlue;
  private double gamma;
  private double gammaThreshold;
  private double pixelsPerUnit;
  private int whiteLevel;
  @AnimAware
  private double brightness;
  private double contrast;
  @AnimAware
  private double vibrancy;
  private boolean preserveZ;
  private String resolutionProfile;
  private String qualityProfile;
  private String name = "";
  @AnimAware
  private RGBPalette palette = new RGBPalette();
  @AnimAware
  private final List<XForm> xForms = new ArrayList<XForm>();
  @AnimAware
  private final List<XForm> finalXForms = new ArrayList<XForm>();
  private ShadingInfo shadingInfo = new ShadingInfo();
  private String lastFilename = null;

  public Flame() {
    sampleDensity = 100.0;
    bgTransparency = true;
    bgColorRed = bgColorGreen = bgColorBlue = 0;
    brightness = 4;
    contrast = 1;
    vibrancy = 1;
    gamma = 4;
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
    camZ = 0.0;
    newCamDOF = false;
    camDOFArea = 0.5;
    camDOFExponent = 2.0;
    gammaThreshold = 0.01;
    pixelsPerUnit = 50;
    whiteLevel = 200;
    name = "";
    spatialFilterRadius = 0.0;
    spatialFilterKernel = FilterKernelType.GAUSSIAN;
    deFilterEnabled = false;
    deFilterMaxRadius = 0.7;
    deFilterMinRadius = 0.0;
    deFilterCurve = 0.36;
    deFilterKernel = FilterKernelType.GAUSSIAN;
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

  public List<XForm> getXForms() {
    return xForms;
  }

  public List<XForm> getFinalXForms() {
    return finalXForms;
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

  public RGBPalette getPalette() {
    return palette;
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

  public void setPalette(RGBPalette pPalette) {
    if (pPalette == null || pPalette.getSize() != RGBPalette.PALETTE_SIZE)
      throw new IllegalArgumentException(pPalette.toString());
    palette = pPalette;
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

  public void distributeColors() {
    int cnt = getXForms().size();
    if (cnt > 1) {
      for (int i = 0; i < getXForms().size(); i++) {
        XForm xForm = getXForms().get(i);
        xForm.setColor((double) i / (double) (cnt - 1));
      }
    }
  }

  public void randomizeColors() {
    for (int i = 0; i < getXForms().size(); i++) {
      XForm xForm = getXForms().get(i);
      xForm.setColor(Math.random());
    }
  }

  public void refreshModWeightTables(FlameTransformationContext pFlameTransformationContext) {
    double tp[] = new double[Constants.MAX_MOD_WEIGHT_COUNT];
    int n = getXForms().size();

    for (XForm xForm : this.getXForms()) {
      xForm.initTransform();
      for (Variation var : xForm.getSortedVariations()) {
        var.getFunc().init(pFlameTransformationContext, xForm, var.getAmount());
      }
    }
    for (XForm xForm : this.getFinalXForms()) {
      xForm.initTransform();
      for (Variation var : xForm.getSortedVariations()) {
        var.getFunc().init(pFlameTransformationContext, xForm, var.getAmount());
      }
    }
    for (int k = 0; k < n; k++) {
      XForm xform = getXForms().get(k);
      double totValue = 0;
      for (int i = 0; i < n; i++) {
        tp[i] = getXForms().get(i).getWeight() * getXForms().get(k).getModifiedWeights()[i];
        totValue = totValue + tp[i];
      }
      if (totValue > 0) {
        double loopValue = 0;
        for (int i = 0; i < xform.getNextAppliedXFormTable().length; i++) {
          double totalProb = 0;
          int j = -1;
          do {
            j++;
            totalProb = totalProb + tp[j];
          }
          while (!((totalProb > loopValue) || (j == n - 1)));
          xform.getNextAppliedXFormTable()[i] = getXForms().get(j);
          loopValue = loopValue + totValue / (double) xform.getNextAppliedXFormTable().length;
        }
      }
      else {
        for (int i = 0; i < xform.getNextAppliedXFormTable().length; i++) {
          xform.getNextAppliedXFormTable()[i] = null;
        }
      }
    }
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
    centreY = pFlame.centreY;
    width = pFlame.width;
    height = pFlame.height;
    camPitch = pFlame.camPitch;
    camYaw = pFlame.camYaw;
    camPerspective = pFlame.camPerspective;
    camRoll = pFlame.camRoll;
    camZoom = pFlame.camZoom;
    focusX = pFlame.focusX;
    focusY = pFlame.focusY;
    focusZ = pFlame.focusZ;
    dimishZ = pFlame.dimishZ;
    camZ = pFlame.camZ;
    camDOF = pFlame.camDOF;
    newCamDOF = pFlame.newCamDOF;
    camDOFArea = pFlame.camDOFArea;
    camDOFExponent = pFlame.camDOFExponent;
    spatialFilterRadius = pFlame.spatialFilterRadius;
    spatialFilterKernel = pFlame.spatialFilterKernel;
    deFilterEnabled = pFlame.deFilterEnabled;
    deFilterMaxRadius = pFlame.deFilterMaxRadius;
    deFilterMinRadius = pFlame.deFilterMinRadius;
    deFilterCurve = pFlame.deFilterCurve;
    deFilterKernel = pFlame.deFilterKernel;
    sampleDensity = pFlame.sampleDensity;
    bgTransparency = pFlame.bgTransparency;
    bgColorRed = pFlame.bgColorRed;
    bgColorGreen = pFlame.bgColorGreen;
    bgColorBlue = pFlame.bgColorBlue;
    gamma = pFlame.gamma;
    gammaThreshold = pFlame.gammaThreshold;
    pixelsPerUnit = pFlame.pixelsPerUnit;
    whiteLevel = pFlame.whiteLevel;
    brightness = pFlame.brightness;
    contrast = pFlame.contrast;
    vibrancy = pFlame.vibrancy;
    preserveZ = pFlame.preserveZ;
    resolutionProfile = pFlame.resolutionProfile;
    qualityProfile = pFlame.qualityProfile;
    shadingInfo.assign(pFlame.shadingInfo);
    palette = pFlame.palette.makeCopy();
    name = pFlame.name;
    xForms.clear();
    for (XForm xForm : pFlame.getXForms()) {
      xForms.add(xForm.makeCopy());
    }
    finalXForms.clear();
    for (XForm xForm : pFlame.getFinalXForms()) {
      finalXForms.add(xForm.makeCopy());
    }
    lastFilename = pFlame.lastFilename;
  }

  @Override
  public boolean isEqual(Flame pFlame) {
    if ((fabs(centreX - pFlame.centreX) > EPSILON) || (fabs(centreY - pFlame.centreY) > EPSILON) ||
        (width != pFlame.width) || (height != pFlame.height) ||
        (fabs(camPitch - pFlame.camPitch) > EPSILON) || (fabs(camYaw - pFlame.camYaw) > EPSILON) ||
        (fabs(camPerspective - pFlame.camPerspective) > EPSILON) || (fabs(camRoll - pFlame.camRoll) > EPSILON) ||
        (fabs(camZoom - pFlame.camZoom) > EPSILON) || (fabs(focusX - pFlame.focusX) > EPSILON) ||
        (fabs(focusY - pFlame.focusY) > EPSILON) || (fabs(focusZ - pFlame.focusZ) > EPSILON) || (fabs(dimishZ - pFlame.dimishZ) > EPSILON) ||
        (fabs(camDOF - pFlame.camDOF) > EPSILON) || (fabs(camDOFArea - pFlame.camDOFArea) > EPSILON) ||
        (fabs(camDOFExponent - pFlame.camDOFExponent) > EPSILON) || (fabs(camZ - pFlame.camZ) > EPSILON) ||
        (newCamDOF != pFlame.newCamDOF) || (fabs(spatialFilterRadius - pFlame.spatialFilterRadius) > EPSILON) ||
        !spatialFilterKernel.equals(pFlame.spatialFilterKernel) || !deFilterKernel.equals(pFlame.deFilterKernel) ||
        (deFilterEnabled != pFlame.deFilterEnabled) || (fabs(deFilterMaxRadius - pFlame.deFilterMaxRadius) > EPSILON) ||
        (fabs(deFilterMinRadius - pFlame.deFilterMinRadius) > EPSILON) || (fabs(deFilterCurve - pFlame.deFilterCurve) > EPSILON) ||
        (fabs(sampleDensity - pFlame.sampleDensity) > EPSILON) || (bgTransparency != pFlame.bgTransparency) || (bgColorRed != pFlame.bgColorRed) ||
        (bgColorGreen != pFlame.bgColorGreen) || (bgColorBlue != pFlame.bgColorBlue) ||
        (fabs(gamma - pFlame.gamma) > EPSILON) || (fabs(gammaThreshold - pFlame.gammaThreshold) > EPSILON) ||
        (fabs(pixelsPerUnit - pFlame.pixelsPerUnit) > EPSILON) || (whiteLevel != pFlame.whiteLevel) ||
        (fabs(brightness - pFlame.brightness) > EPSILON) || (fabs(contrast - pFlame.contrast) > EPSILON) ||
        (fabs(vibrancy - pFlame.vibrancy) > EPSILON) || (preserveZ != pFlame.preserveZ) ||
        ((resolutionProfile != null && pFlame.resolutionProfile == null) || (resolutionProfile == null && pFlame.resolutionProfile != null) ||
        (resolutionProfile != null && pFlame.resolutionProfile != null && !resolutionProfile.equals(pFlame.resolutionProfile))) ||
        ((qualityProfile != null && pFlame.qualityProfile == null) || (qualityProfile == null && pFlame.qualityProfile != null) ||
        (qualityProfile != null && pFlame.qualityProfile != null && !qualityProfile.equals(pFlame.qualityProfile))) ||
        !palette.isEqual(pFlame.palette) || !name.equals(pFlame.name) ||
        !shadingInfo.isEqual(pFlame.shadingInfo) ||
        (xForms.size() != pFlame.xForms.size()) || (finalXForms.size() != pFlame.finalXForms.size())) {
      return false;
    }
    for (int i = 0; i < xForms.size(); i++) {
      if (!xForms.get(i).isEqual(pFlame.xForms.get(i))) {
        return false;
      }
    }
    for (int i = 0; i < finalXForms.size(); i++) {
      if (!finalXForms.get(i).isEqual(pFlame.finalXForms.get(i))) {
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

  public boolean isDeFilterEnabled() {
    return deFilterEnabled;
  }

  public void setDeFilterEnabled(boolean deFilterEnabled) {
    this.deFilterEnabled = deFilterEnabled;
  }

  public double getDeFilterMaxRadius() {
    return deFilterMaxRadius;
  }

  public void setDeFilterMaxRadius(double deFilterMaxRadius) {
    this.deFilterMaxRadius = deFilterMaxRadius;
  }

  public double getDeFilterMinRadius() {
    return deFilterMinRadius;
  }

  public void setDeFilterMinRadius(double deFilterMinRadius) {
    this.deFilterMinRadius = deFilterMinRadius;
  }

  public double getDeFilterCurve() {
    return deFilterCurve;
  }

  public void setDeFilterCurve(double deFilterCurve) {
    this.deFilterCurve = deFilterCurve;
  }

  public FilterKernelType getSpatialFilterKernel() {
    return spatialFilterKernel;
  }

  public void setSpatialFilterKernel(FilterKernelType spatialFilterKernel) {
    this.spatialFilterKernel = spatialFilterKernel;
  }

  public FilterKernelType getDeFilterKernel() {
    return deFilterKernel;
  }

  public void setDeFilterKernel(FilterKernelType deFilterKernel) {
    this.deFilterKernel = deFilterKernel;
  }

  public double getDimishZ() {
    return dimishZ;
  }

  public void setDimishZ(double dimishZ) {
    this.dimishZ = dimishZ;
  }
}
