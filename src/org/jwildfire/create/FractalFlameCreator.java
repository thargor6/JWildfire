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
package org.jwildfire.create;

import java.awt.Color;
import java.io.File;
import java.util.List;

import org.jwildfire.base.Property;
import org.jwildfire.base.PropertyMax;
import org.jwildfire.base.PropertyMin;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.io.Flam3Reader;
import org.jwildfire.create.tina.render.FlameRenderer;
import org.jwildfire.create.tina.transform.XFormTransformService;
import org.jwildfire.image.SimpleImage;


public class FractalFlameCreator extends ImageCreator {
  @Property(description = "Filter radius (higher value results into less noise but more unsharp image)")
  @PropertyMin(0)
  private double spatialFilterRadius = 1.25;

  @Property(description = "Sample density in percent. But there is no upper limit of 100%. Higher values result in more details but needs more computation time")
  @PropertyMin(1)
  private int sampleDensity = 50;

  @Property(description = "Background color")
  private Color backgroundColor = new Color(0, 0, 0);

  @Property(description = "Brightness")
  @PropertyMin(0.0)
  private double brightness = 2.0;

  @Property(description = "Contrast")
  @PropertyMin(0.0)
  private double contrast = 1.0;

  @Property(description = "Vibrancy")
  @PropertyMin(0.0)
  @PropertyMax(1.0)
  private double vibrancy = 1;

  @Property(description = "Gamma value")
  @PropertyMin(0.0)
  private double gamma = 2.0;

  @Property(description = "X-coordinate of the centre (overrides value from flam3-file if not zero)")
  @PropertyMin(0.0)
  private double centreX = 0.0;

  @Property(description = "Y-coordinate of the centre (overrides value from flam3-file if not zero)")
  @PropertyMin(0.0)
  private double centreY = 0.0;

  @Property(description = "Pitch rotation angle in degrees (overrides value from flam3-file if not zero)")
  private double camRoll = 0.0;

  @Property(description = "Yaw rotation angle in degrees (overrides value from flam3-file if not zero)")
  private double camYaw = 0.0;

  @Property(description = "Pitch rotation angle in degrees (overrides value from flam3-file if not zero)")
  private double camPitch = 0.0;

  @Property(description = "Amount of perspective distortion (overrides value from flam3-file if not zero)")
  @PropertyMin(0.0)
  private double camPerspective = 0.0;

  @Property(description = "Spatial oversample")
  @PropertyMin(0)
  private int spatialOversample = 1;

  @Property(description = "RGB white level")
  @PropertyMin(0)
  @PropertyMax(255)
  private int whiteLevel = 200;

  @Property(description = "Gamma threshold")
  @PropertyMin(0)
  private double gammaThreshold = 0.04;

  @Property(description = "Zoom factor (overrides value from flam3-file if not zero)")
  @PropertyMin(0)
  private int pixelsPerUnit = 0;

  @Property(description = "Rotation of XForm 0 (overrides value from flam3-file if not zero)")
  private double xForm0Rotate = 0.0;

  @Property(description = "Scale of XForm 0 (overrides value from flam3-file if not zero)")
  private double xForm0Scale = 0.0;

  @Property(description = "X-translation of XForm 0 (overrides value from flam3-file if not zero)")
  private double xForm0TransX = 0.0;

  @Property(description = "Y-translation of XForm 0 (overrides value from flam3-file if not zero)")
  private double xForm0TransY = 0.0;

  @Property(description = "Rotation of XForm 1 (overrides value from flam3-file if not zero)")
  private double xForm1Rotate = 0.0;

  @Property(description = "Scale of XForm 1 (overrides value from flam3-file if not zero)")
  private double xForm1Scale = 0.0;

  @Property(description = "X-translation of XForm 1 (overrides value from flam3-file if not zero)")
  private double xForm1TransX = 0.0;

  @Property(description = "Y-translation of XForm 1 (overrides value from flam3-file if not zero)")
  private double xForm1TransY = 0.0;

  @Property(description = "Rotation of XForm 2 (overrides value from flam3-file if not zero)")
  private double xForm2Rotate = 0.0;

  @Property(description = "Scale of XForm 2 (overrides value from flam3-file if not zero)")
  private double xForm2Scale = 0.0;

  @Property(description = "X-translation of XForm 2 (overrides value from flam3-file if not zero)")
  private double xForm2TransX = 0.0;

  @Property(description = "Y-translation of XForm 2 (overrides value from flam3-file if not zero)")
  private double xForm2TransY = 0.0;

  //////////////////////////////////////////////////
  @Property(description = "flame drawer")
  @PropertyMin(0)
  private String flameDrawer = "C:\\TMP\\wf\\";

  @Property(description = "flame file (flam3 compatible format)")
  @PropertyMin(0)
  private String flameFilename = "BALL3D.flame"; // 

  //////////////////////////////////////////////////

  @Override
  protected void fillImage(SimpleImage res) {
    String fn = new File(flameDrawer, flameFilename).getAbsolutePath();

    List<Flame> flames = new Flam3Reader().readFlames(fn);
    Flame flame = flames.get(0);

    double wScl = (double) res.getImageWidth() / (double) flame.getWidth();
    double hScl = (double) res.getImageHeight() / (double) flame.getHeight();
    flame.setPixelsPerUnit((wScl + hScl) * 0.5 * flame.getPixelsPerUnit());

    flame.setSpatialFilterRadius(spatialFilterRadius);
    flame.setSampleDensity(sampleDensity);
    flame.setBGColorRed(backgroundColor.getRed());
    flame.setBGColorGreen(backgroundColor.getGreen());
    flame.setBGColorBlue(backgroundColor.getBlue());
    flame.setBrightness(brightness);
    flame.setContrast(contrast);
    flame.setVibrancy(vibrancy);
    flame.setGamma(gamma);
    flame.setSpatialOversample(spatialOversample);
    flame.setWhiteLevel(whiteLevel);
    flame.setGammaThreshold(gammaThreshold);
    if (Math.abs(centreX) > 0.0)
      flame.setCentreX(centreX);
    if (Math.abs(centreY) > 0.0)
      flame.setCentreY(centreY);
    if (Math.abs(camPerspective) > 0.0)
      flame.setCamPerspective(camPerspective);
    if (Math.abs(camYaw) > 0.0)
      flame.setCamYaw(camYaw);
    if (Math.abs(camPitch) > 0.0)
      flame.setCamPitch(camPitch);
    if (Math.abs(camRoll) > 0.0)
      flame.setCamRoll(camRoll);
    if (pixelsPerUnit > 0)
      flame.setPixelsPerUnit(pixelsPerUnit);
    if (Math.abs(xForm0Rotate) > 0.0 && flame.getXForms().size() > 0) {
      XFormTransformService.rotate(flame.getXForms().get(0), xForm0Rotate);
    }
    if (Math.abs(xForm0Scale) > 0.0 && flame.getXForms().size() > 0) {
      XFormTransformService.scale(flame.getXForms().get(0), xForm0Scale);
    }
    if ((Math.abs(xForm0TransX) > 0.0 || Math.abs(xForm0TransY) > 0.0) && flame.getXForms().size() > 0) {
      XFormTransformService.translate(flame.getXForms().get(0), xForm0TransX, xForm0TransY);
    }

    if (Math.abs(xForm1Rotate) > 0.0 && flame.getXForms().size() > 1) {
      XFormTransformService.rotate(flame.getXForms().get(1), xForm1Rotate);
    }
    if (Math.abs(xForm1Scale) > 0.0 && flame.getXForms().size() > 1) {
      XFormTransformService.scale(flame.getXForms().get(1), xForm1Scale);
    }
    if ((Math.abs(xForm1TransX) > 0.0 || Math.abs(xForm1TransY) > 0.0) && flame.getXForms().size() > 1) {
      XFormTransformService.translate(flame.getXForms().get(1), xForm1TransX, xForm1TransY);
    }

    if (Math.abs(xForm2Rotate) > 0.0 && flame.getXForms().size() > 2) {
      XFormTransformService.rotate(flame.getXForms().get(2), xForm2Rotate);
    }
    if (Math.abs(xForm2Scale) > 0.0 && flame.getXForms().size() > 2) {
      XFormTransformService.scale(flame.getXForms().get(2), xForm2Scale);
    }
    if ((Math.abs(xForm2TransX) > 0.0 || Math.abs(xForm2TransY) > 0.0) && flame.getXForms().size() > 2) {
      XFormTransformService.translate(flame.getXForms().get(2), xForm2TransX, xForm2TransY);
    }

    //    flame.setFinalXForm(null);
    //    flame.getXForms().clear();
    //    for (int i = 0; i < 3; i++) {
    //      XForm xForm = new XForm();
    //      flame.getXForms().add(xForm);
    //      flame.setPixelsPerUnit(500);
    //      XFormTransformService.rotate(xForm, 360 * Math.random());
    //      XFormTransformService.translate(xForm, Math.random(), Math.random());
    //      XFormTransformService.scale(xForm, 0.5 + Math.random() / 2);
    //
    //      xForm.setColor(Math.random());
    //      xForm.addVariation(Math.random(), new Linear3DFunc());
    //      xForm.setWeight(Math.random());
    //    }

    FlameRenderer renderer = new FlameRenderer();
    renderer.renderFlame(flame, res);
  }

  public String getFlameFilename() {
    return flameFilename;
  }

  public void setFlameFilename(String flameFilename) {
    this.flameFilename = flameFilename;
  }

  public String getFlameDrawer() {
    return flameDrawer;
  }

  public void setFlameDrawer(String flameDrawer) {
    this.flameDrawer = flameDrawer;
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

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public double getBrightness() {
    return brightness;
  }

  public void setBrightness(double brightness) {
    this.brightness = brightness;
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

  public double getGamma() {
    return gamma;
  }

  public void setGamma(double gamma) {
    this.gamma = gamma;
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

  public void setCamRoll(double camRoll) {
    this.camRoll = camRoll;
  }

  public double getCamYaw() {
    return camYaw;
  }

  public void setCamYaw(double camYaw) {
    this.camYaw = camYaw;
  }

  public double getCamPitch() {
    return camPitch;
  }

  public void setCamPitch(double camPitch) {
    this.camPitch = camPitch;
  }

  public double getCamPerspective() {
    return camPerspective;
  }

  public void setCamPerspective(double camPerspective) {
    this.camPerspective = camPerspective;
  }

  public int getSpatialOversample() {
    return spatialOversample;
  }

  public void setSpatialOversample(int spatialOversample) {
    this.spatialOversample = spatialOversample;
  }

  public int getWhiteLevel() {
    return whiteLevel;
  }

  public void setWhiteLevel(int whiteLevel) {
    this.whiteLevel = whiteLevel;
  }

  public double getGammaThreshold() {
    return gammaThreshold;
  }

  public void setGammaThreshold(double gammaThreshold) {
    this.gammaThreshold = gammaThreshold;
  }

  public int getPixelsPerUnit() {
    return pixelsPerUnit;
  }

  public void setPixelsPerUnit(int pixelsPerUnit) {
    this.pixelsPerUnit = pixelsPerUnit;
  }

  public double getXForm0Rotate() {
    return xForm0Rotate;
  }

  public void setXForm0Rotate(double xForm0Rotate) {
    this.xForm0Rotate = xForm0Rotate;
  }

  public double getXForm0Scale() {
    return xForm0Scale;
  }

  public void setXForm0Scale(double xForm0Scale) {
    this.xForm0Scale = xForm0Scale;
  }

  public double getXForm0TransX() {
    return xForm0TransX;
  }

  public void setXForm0TransX(double xForm0TransX) {
    this.xForm0TransX = xForm0TransX;
  }

  public double getXForm0TransY() {
    return xForm0TransY;
  }

  public void setXForm0TransY(double xForm0TransY) {
    this.xForm0TransY = xForm0TransY;
  }

  public double getXForm1Rotate() {
    return xForm1Rotate;
  }

  public void setXForm1Rotate(double xForm1Rotate) {
    this.xForm1Rotate = xForm1Rotate;
  }

  public double getXForm1Scale() {
    return xForm1Scale;
  }

  public void setXForm1Scale(double xForm1Scale) {
    this.xForm1Scale = xForm1Scale;
  }

  public double getXForm1TransX() {
    return xForm1TransX;
  }

  public void setXForm1TransX(double xForm1TransX) {
    this.xForm1TransX = xForm1TransX;
  }

  public double getXForm1TransY() {
    return xForm1TransY;
  }

  public void setXForm1TransY(double xForm1TransY) {
    this.xForm1TransY = xForm1TransY;
  }

  public double getXForm2Rotate() {
    return xForm2Rotate;
  }

  public void setXForm2Rotate(double xForm2Rotate) {
    this.xForm2Rotate = xForm2Rotate;
  }

  public double getXForm2Scale() {
    return xForm2Scale;
  }

  public void setXForm2Scale(double xForm2Scale) {
    this.xForm2Scale = xForm2Scale;
  }

  public double getXForm2TransX() {
    return xForm2TransX;
  }

  public void setXForm2TransX(double xForm2TransX) {
    this.xForm2TransX = xForm2TransX;
  }

  public double getXForm2TransY() {
    return xForm2TransY;
  }

  public void setXForm2TransY(double xForm2TransY) {
    this.xForm2TransY = xForm2TransY;
  }

}
