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

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.palette.RGBPalette;

public class Flame {
  private double centreX;
  private double centreY;
  private int width;
  private int height;
  private double camPitch;
  private double camYaw;
  private double camPerspective;
  private double camRoll;
  private double camZoom;
  private double camZ;
  private double camDOF;
  private int spatialOversample;
  private int colorOversample;
  private double spatialFilterRadius;
  private int sampleDensity;
  private int bgColorRed;
  private int bgColorGreen;
  private int bgColorBlue;
  private double gamma;
  private double gammaThreshold;
  private double pixelsPerUnit;
  private int whiteLevel;
  private double brightness;
  private double contrast;
  private double vibrancy;

  private RGBPalette palette = new RGBPalette();
  private final List<XForm> xForms = new ArrayList<XForm>();
  private XForm finalXForm = null;

  public Flame() {
    init();
  }

  private void init() {
    spatialFilterRadius = 1.2;
    sampleDensity = 100;
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
    camZ = 0.0;
    camDOF = 0.0;
    spatialOversample = 1;
    colorOversample = 1;
    gammaThreshold = 0.04;
    pixelsPerUnit = 50;
    whiteLevel = 200;
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

  public int getSpatialOversample() {
    return spatialOversample;
  }

  public void setSpatialOversample(int spatialOversample) {
    this.spatialOversample = spatialOversample;
  }

  public double getSpatialFilterRadius() {
    return spatialFilterRadius;
  }

  public void setSpatialFilterRadius(double spatialFilterRadius) {
    this.spatialFilterRadius = spatialFilterRadius;
  }

  public int getSampleDensity() {
    return sampleDensity;
  }

  public void setSampleDensity(int sampleDensity) {
    this.sampleDensity = sampleDensity;
  }

  public List<XForm> getXForms() {
    return xForms;
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

  public XForm getFinalXForm() {
    return finalXForm;
  }

  public void setFinalXForm(XForm pFinalXForm) {
    finalXForm = pFinalXForm;
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

  public Flame makeCopy() {
    Flame res = new Flame();
    res.assign(this);
    return res;
  }

  private void assign(Flame pFlame) {
    centreX = pFlame.centreX;
    centreY = pFlame.centreY;
    width = pFlame.width;
    height = pFlame.height;
    camPitch = pFlame.camPitch;
    camYaw = pFlame.camYaw;
    camPerspective = pFlame.camPerspective;
    camRoll = pFlame.camRoll;
    camZoom = pFlame.camZoom;
    spatialOversample = pFlame.spatialOversample;
    colorOversample = pFlame.colorOversample;
    spatialFilterRadius = pFlame.spatialFilterRadius;
    sampleDensity = pFlame.sampleDensity;
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

    palette = pFlame.palette.makeCopy();
    xForms.clear();
    for (XForm xForm : pFlame.getXForms()) {
      xForms.add(xForm.makeCopy());
    }
    if (pFlame.finalXForm != null) {
      finalXForm = pFlame.finalXForm.makeCopy();
    }

  }

  public int getColorOversample() {
    return colorOversample;
  }

  public void setColorOversample(int colorOversample) {
    this.colorOversample = colorOversample;
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

}
