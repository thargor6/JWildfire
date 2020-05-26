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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.render.GammaCorrectionFilter;
import org.jwildfire.envelope.Envelope;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.create.GradientCreator;
import org.jwildfire.create.tina.variation.RessourceManager;

import java.io.Serializable;
import java.util.List;

import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;

public class Layer implements Assignable<Layer>, Serializable {
  private Flame owner;

  private static final long serialVersionUID = 1L;

  private boolean visible = true;

  @AnimAware
  private double weight = 1.0;
  private final MotionCurve weightCurve = new MotionCurve();
  @AnimAware
  private double density = 1.0;
  private final MotionCurve densityCurve = new MotionCurve();
  @AnimAware
  private RGBPalette palette = new RGBPalette();
  @AnimAware
  private final List<XForm> xForms = new XFormList(this);
  @AnimAware
  private final List<XForm> finalXForms = new XFormList(this);

  private String name = "";

  private String gradientMapFilename = "";
  private double gradientMapHorizOffset = 0.0;
  private double gradientMapHorizScale = 1.0;
  private double gradientMapVertOffset = 0.0;
  private double gradientMapVertScale = 1.0;
  private double gradientMapLocalColorAdd = 0.8;
  private double gradientMapLocalColorScale = 0.2;
  private boolean smoothGradient = false;
  private RenderColor[] colorMap = null;
  private SimpleImage gradientMap = null;

  private final MotionCurve gradientEditorHueCurve = createGradientCurve();
  private final MotionCurve gradientEditorSaturationCurve = createGradientCurve();
  private final MotionCurve gradientEditorLuminosityCurve = createGradientCurve();

  public List<XForm> getXForms() {
    return xForms;
  }

  public List<XForm> getFinalXForms() {
    return finalXForms;
  }

  public RGBPalette getPalette() {
    return palette;
  }

  private final static int DFLT_CURVE_POINTS = 12;

  private MotionCurve createGradientCurve() {
    MotionCurve curve = new MotionCurve();
    double dx = (RGBPalette.PALETTE_SIZE - 1) / (double)(DFLT_CURVE_POINTS - 1);
    int x[] = new int[DFLT_CURVE_POINTS];
    double y[] = new double[DFLT_CURVE_POINTS];
    x[0] = 0;
    for(int i=1;i<DFLT_CURVE_POINTS-1;i++) {
      x[i] = Tools.FTOI(i * dx);
    }
    x[DFLT_CURVE_POINTS-1] = (RGBPalette.PALETTE_SIZE - 1);

    curve.setPoints(x, y);
    {
      int border = 5;
      curve.setViewXMin(-border);
      curve.setViewXMax((RGBPalette.PALETTE_SIZE - 1) + border);
      curve.setViewYMin(-border);
      curve.setViewYMax((RGBPalette.PALETTE_SIZE - 1) + border);
    }
    return curve;
  }
  public void setPalette(RGBPalette pPalette) {
    if (pPalette == null || pPalette.getSize() != RGBPalette.PALETTE_SIZE)
      throw new IllegalArgumentException(pPalette != null ? pPalette.toString() + " " + pPalette.getSize() : "NULL");
    palette = pPalette;
    colorMap = null;
    if(pPalette.getGradHueX()!=null && pPalette.getGradHue() != null && pPalette.getGradSaturationX()!=null && pPalette.getGradSaturation()!=null && pPalette.getGradLuminosityX()!=null && pPalette.getGradLuminosity()!=null) {
      gradientEditorHueCurve.setPoints(pPalette.getGradHueX(), pPalette.getGradHue());
      gradientEditorSaturationCurve.setPoints(pPalette.getGradSaturationX(), pPalette.getGradSaturation());
      gradientEditorLuminosityCurve.setPoints(pPalette.getGradLuminosityX(), pPalette.getGradLuminosity());
    }
    else {
      recalcHSLCurves();
    }
  }

  public void recalcHSLCurves() {
    GammaCorrectionFilter.HSLRGBConverter converter = new GammaCorrectionFilter.HSLRGBConverter();
    gradientEditorHueCurve.assign(createGradientCurve());
    gradientEditorSaturationCurve.assign(createGradientCurve());
    gradientEditorLuminosityCurve.assign(createGradientCurve());
    {
      RGBColor color = palette.getRawColor(0);
      converter.fromRgb(color.getRed() / 255.0, color.getGreen()/ 255.0, color.getBlue()/ 255.0);
      gradientEditorHueCurve.getY()[0] = converter.getHue() * 255.0;
      gradientEditorSaturationCurve.getY()[0] = converter.getSaturation() * 255.0;
      gradientEditorLuminosityCurve.getY()[0] = converter.getLuminosity() * 255.0;
    }

    {
      double dx = (RGBPalette.PALETTE_SIZE - 1) / (double) (DFLT_CURVE_POINTS - 1);
      int avgCount = (RGBPalette.PALETTE_SIZE - 1) / (DFLT_CURVE_POINTS - 1) / 2;
      for (int i = 1; i < DFLT_CURVE_POINTS - 1; i++) {
        double hue = 0.0, saturation = 0.0, luminosity = 0.0;
        int count = 0;
        for(int j = avgCount; j <= avgCount; j++) {
          int x = Tools.FTOI( i * dx + j );
          RGBColor color = palette.getRawColor(x);
          converter.fromRgb(color.getRed() / 255.0, color.getGreen()/ 255.0, color.getBlue()/ 255.0);
          hue += converter.getHue();
          saturation += converter.getSaturation();
          luminosity += converter.getLuminosity();
          count++;
        }
        gradientEditorHueCurve.getY()[i] = (hue * 255.0) / (double)count;
        gradientEditorSaturationCurve.getY()[i] = (saturation * 255.0) / (double)count;
        gradientEditorLuminosityCurve.getY()[i] = (luminosity * 255.0) / (double)count;
      }
    }

    {
      RGBColor color = palette.getRawColor(RGBPalette.PALETTE_SIZE - 1);
      converter.fromRgb(color.getRed()/ 255.0, color.getGreen()/ 255.0, color.getBlue()/ 255.0);
      gradientEditorHueCurve.getY()[DFLT_CURVE_POINTS - 1] = converter.getHue() * 255.0;
      gradientEditorSaturationCurve.getY()[DFLT_CURVE_POINTS - 1] = converter.getSaturation() * 255.0;
      gradientEditorLuminosityCurve.getY()[DFLT_CURVE_POINTS - 1] = converter.getLuminosity() * 255.0;
    }
  }

  public RenderColor[] getColorMap() {
    if (colorMap == null) {
      colorMap = palette.createRenderPalette(owner.getWhiteLevel());
    }
    return colorMap;
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

  public void resetColors() {
    for (int i = 0; i < getXForms().size(); i++) {
      XForm xForm = getXForms().get(i);
      xForm.setColor(0.0);
      xForm.setColorSymmetry(0.0);
    }
  }

  public void randomizeColorSpeed() {
    for (int i = 0; i < getXForms().size(); i++) {
      XForm xForm = getXForms().get(i);
      xForm.setColorSymmetry(Math.random());
    }
  }

  public void refreshModWeightTables(FlameTransformationContext pFlameTransformationContext) {
    double tp[] = new double[Constants.MAX_MOD_WEIGHT_COUNT];
    int n = getXForms().size();
    {
      for (XForm xForm : this.getXForms()) {
        if (xForm.getColorType() == ColorType.UNSET) xForm.setColorType(ColorType.DIFFUSION);
        xForm.initTransform();
        if(pFlameTransformationContext.getThreadId()==0) {
          for (Variation var : xForm.getVariations()) {
            var.getFunc().initOnce(pFlameTransformationContext, this, xForm, var.getAmount());
          }
        }
        for (Variation var : xForm.getVariations()) {
          var.getFunc().init(pFlameTransformationContext, this, xForm, var.getAmount());
        }
      }
    }
    {
      for (XForm xForm : this.getFinalXForms()) {
        if (xForm.getColorType() == ColorType.UNSET) xForm.setColorType(ColorType.NONE);
        xForm.initTransform();
        if(pFlameTransformationContext.getThreadId()==0) {
          for (Variation var : xForm.getVariations()) {
            var.getFunc().initOnce(pFlameTransformationContext, this, xForm, var.getAmount());
          }
        }
        for (Variation var : xForm.getVariations()) {
          var.getFunc().init(pFlameTransformationContext, this, xForm, var.getAmount());
        }
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

  @Override
  public void assign(Layer pSrc) {
    weight = pSrc.weight;
    weightCurve.assign(pSrc.weightCurve);
    density = pSrc.density;
    densityCurve.assign(pSrc.densityCurve);
    visible = pSrc.visible;
    name = pSrc.name;
    gradientMapFilename = pSrc.gradientMapFilename;
    gradientMapHorizOffset = pSrc.gradientMapHorizOffset;
    gradientMapHorizScale = pSrc.gradientMapHorizScale;
    gradientMapVertOffset = pSrc.gradientMapVertOffset;
    gradientMapVertScale = pSrc.gradientMapVertScale;
    gradientMapLocalColorAdd = pSrc.gradientMapLocalColorAdd;
    gradientMapLocalColorScale = pSrc.gradientMapLocalColorScale;
    smoothGradient = pSrc.smoothGradient;
    gradientEditorHueCurve.assign(pSrc.gradientEditorHueCurve);
    gradientEditorSaturationCurve.assign(pSrc.gradientEditorSaturationCurve);
    gradientEditorLuminosityCurve.assign(pSrc.gradientEditorLuminosityCurve);
    palette = pSrc.palette.makeCopy();
    xForms.clear();
    for (XForm xForm : pSrc.getXForms()) {
      xForms.add(xForm.makeCopy());
    }
    finalXForms.clear();
    for (XForm xForm : pSrc.getFinalXForms()) {
      finalXForms.add(xForm.makeCopy());
    }
  }

  @Override
  public Layer makeCopy() {
    Layer res = new Layer();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(Layer pSrc) {
    if ((fabs(weight - pSrc.weight) > EPSILON) || !weightCurve.isEqual(pSrc.weightCurve) ||
        (fabs(density - pSrc.density) > EPSILON) || !densityCurve.isEqual(pSrc.densityCurve) ||
        (fabs(gradientMapHorizOffset - pSrc.gradientMapHorizOffset) > EPSILON) || (fabs(gradientMapHorizScale - pSrc.gradientMapHorizScale) > EPSILON) ||
        (fabs(gradientMapVertOffset - pSrc.gradientMapVertOffset) > EPSILON) || (fabs(gradientMapVertScale - pSrc.gradientMapVertScale) > EPSILON) ||
        (fabs(gradientMapLocalColorAdd - pSrc.gradientMapLocalColorAdd) > EPSILON) || (fabs(gradientMapLocalColorScale - pSrc.gradientMapLocalColorScale) > EPSILON) ||
        !gradientEditorHueCurve.isEqual(pSrc.gradientEditorHueCurve) ||
        !gradientEditorSaturationCurve.isEqual(pSrc.gradientEditorSaturationCurve) || !gradientEditorLuminosityCurve.isEqual(pSrc.gradientEditorLuminosityCurve) ||
        !name.equals(pSrc.name) ||
        !gradientMapFilename.equals(pSrc.gradientMapFilename) ||
        smoothGradient != pSrc.smoothGradient ||
        !palette.isEqual(pSrc.palette) || (visible != pSrc.visible) ||
        (xForms.size() != pSrc.xForms.size()) || (finalXForms.size() != pSrc.finalXForms.size())) {
      return false;
    }
    for (int i = 0; i < xForms.size(); i++) {
      if (!xForms.get(i).isEqual(pSrc.xForms.get(i))) {
        return false;
      }
    }
    for (int i = 0; i < finalXForms.size(); i++) {
      if (!finalXForms.get(i).isEqual(pSrc.finalXForms.get(i))) {
        return false;
      }
    }
    return true;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double pWeight) {
    weight = pWeight;
  }
  
  public double getDensity() {
    return density;
  }
  
  public void setDensity(double pDensity) {
    density = pDensity;
  }
  
  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean pVisible) {
    visible = pVisible;
  }

  public boolean isRenderable() {
    return isVisible() && getWeight() > -EPSILON && getDensity() > -EPSILON && getXForms().size() > 0;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name != null ? name : "";
  }

  public String getGradientMapFilename() {
    return gradientMapFilename;
  }

  public void setGradientMapFilename(String gradientMapFilename) {
    this.gradientMapFilename = gradientMapFilename != null ? gradientMapFilename : "";
    this.gradientMap = null;
  }

  public void setOwner(Flame pOwner) {
    owner = pOwner;
  }

  public Flame getOwner() {
    return owner;
  }

  public boolean isSmoothGradient() {
    return smoothGradient;
  }

  public void setSmoothGradient(boolean pSmoothGradient) {
    smoothGradient = pSmoothGradient;
  }

  public double getGradientMapHorizOffset() {
    return gradientMapHorizOffset;
  }

  public void setGradientMapHorizOffset(double pGradientMapHorizOffset) {
    gradientMapHorizOffset = pGradientMapHorizOffset;
  }

  public double getGradientMapHorizScale() {
    return gradientMapHorizScale;
  }

  public void setGradientMapHorizScale(double pGradientMapHorizScale) {
    gradientMapHorizScale = pGradientMapHorizScale;
  }

  public double getGradientMapVertOffset() {
    return gradientMapVertOffset;
  }

  public void setGradientMapVertOffset(double pGradientMapVertOffset) {
    gradientMapVertOffset = pGradientMapVertOffset;
  }

  public double getGradientMapVertScale() {
    return gradientMapVertScale;
  }

  public void setGradientMapVertScale(double pGradientMapVertScale) {
    gradientMapVertScale = pGradientMapVertScale;
  }

  public double getGradientMapLocalColorAdd() {
    return gradientMapLocalColorAdd;
  }

  public void setGradientMapLocalColorAdd(double pGradientMapLocalColorAdd) {
    gradientMapLocalColorAdd = pGradientMapLocalColorAdd;
  }

  public double getGradientMapLocalColorScale() {
    return gradientMapLocalColorScale;
  }

  public void setGradientMapLocalColorScale(double pGradientMapLocalColorScale) {
    gradientMapLocalColorScale = pGradientMapLocalColorScale;
  }

  public MotionCurve getGradientEditorHueCurve() {
    return gradientEditorHueCurve;
  }

  public MotionCurve getGradientEditorSaturationCurve() {
    return gradientEditorSaturationCurve;
  }

  public MotionCurve getGradientEditorLuminosityCurve() {
    return gradientEditorLuminosityCurve;
  }

  public boolean isGradientMap() {
    return this.gradientMapFilename != null && this.gradientMapFilename.length() > 0;
  }
  
  private SimpleImage createDfltImage() {
    GradientCreator creator = new GradientCreator();
    return creator.createImage(256, 256);
  }
  public SimpleImage getGradientMap() {
    if (gradientMap == null && isGradientMap()) {
      try {
        gradientMap = (SimpleImage) RessourceManager.getImage(this.gradientMapFilename);
      }
      catch (Exception e) {
        e.printStackTrace();
        gradientMap = createDfltImage();
      }
    }
    return gradientMap;
  }

  public void refreshGradientFromCurves() {
    Envelope hueCurve = gradientEditorHueCurve.toEnvelope();
    Envelope saturationCurve = gradientEditorSaturationCurve.toEnvelope();
    Envelope luminosityCurve = gradientEditorLuminosityCurve.toEnvelope();
    GammaCorrectionFilter.HSLRGBConverter converter = new GammaCorrectionFilter.HSLRGBConverter();
    for(int i=0;i<RGBPalette.PALETTE_SIZE;i++) {
      double currHue = hueCurve.evaluate(i) / 255.0;
      double currSaturation = saturationCurve.evaluate(i) / 255.0;
      double currLuminosity = luminosityCurve.evaluate(i) / 255.0;
      converter.fromHsl(currHue, currSaturation, currLuminosity);
      int red =  Math.min( Math.max( Tools.FTOI( converter.getRed() * 255.0), 0), 255);
      int green = Math.min( Math.max( Tools.FTOI( converter.getGreen() * 255.0), 0), 255);
      int blue = Math.min( Math.max( Tools.FTOI( converter.getBlue() * 255.0), 0), 255);
      palette.setColor(i, red, green, blue);
    }
  }
}
