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
import java.util.List;

import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;

public class Layer implements Assignable<Layer>, Serializable {
  private Flame owner;

  private static final long serialVersionUID = 1L;

  private boolean visible = true;

  @AnimAware
  private double weight = 1.0;
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

  public List<XForm> getXForms() {
    return xForms;
  }

  public List<XForm> getFinalXForms() {
    return finalXForms;
  }

  public RGBPalette getPalette() {
    return palette;
  }

  public void setPalette(RGBPalette pPalette) {
    if (pPalette == null || pPalette.getSize() != RGBPalette.PALETTE_SIZE)
      throw new IllegalArgumentException(pPalette != null ? pPalette.toString() + " " + pPalette.getSize() : "NULL");
    palette = pPalette;
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
      int idx = 0;
      for (XForm xForm : this.getXForms()) {
        xForm.initTransform();
        xForm.setIndex(idx++);
        for (Variation var : xForm.getVariations()) {
          var.getFunc().init(pFlameTransformationContext, this, xForm, var.getAmount());
        }
      }
    }
    {
      int idx = 0;
      for (XForm xForm : this.getFinalXForms()) {
        xForm.initTransform();
        xForm.setIndex(idx++);
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
    if ((fabs(weight - pSrc.weight) > EPSILON) ||
        (fabs(gradientMapHorizOffset - pSrc.gradientMapHorizOffset) > EPSILON) || (fabs(gradientMapHorizScale - pSrc.gradientMapHorizScale) > EPSILON) ||
        (fabs(gradientMapVertOffset - pSrc.gradientMapVertOffset) > EPSILON) || (fabs(gradientMapVertScale - pSrc.gradientMapVertScale) > EPSILON) ||
        (fabs(gradientMapLocalColorAdd - pSrc.gradientMapLocalColorAdd) > EPSILON) || (fabs(gradientMapLocalColorScale - pSrc.gradientMapLocalColorScale) > EPSILON) ||
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

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean pVisible) {
    visible = pVisible;
  }

  public boolean isRenderable() {
    return isVisible() && getWeight() > -EPSILON && getXForms().size() > 0;
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

}
