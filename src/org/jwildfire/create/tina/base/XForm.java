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

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.base.motion.MotionCurve;
import org.jwildfire.create.tina.base.motion.PostRotateMotionValueChangeHandler;
import org.jwildfire.create.tina.base.motion.PostScaleMotionValueChangeHandler;
import org.jwildfire.create.tina.base.motion.RotateMotionValueChangeHandler;
import org.jwildfire.create.tina.base.motion.ScaleMotionValueChangeHandler;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;

public final class XForm implements Assignable<XForm>, Serializable {
  private static final long serialVersionUID = 1L;
  @AnimAware
  private double weight;
  @AnimAware
  private double color;
  @AnimAware
  private double colorSymmetry;
  @AnimAware
  private double modGamma;
  @AnimAware
  private double modGammaSpeed;
  @AnimAware
  private double modContrast;
  @AnimAware
  private double modContrastSpeed;
  @AnimAware
  private double modSaturation;
  @AnimAware
  private double modSaturationSpeed;

  @AnimAware
  private double coeff00;
  private final MotionCurve coeff00Curve = new MotionCurve();
  @AnimAware
  private double coeff01;
  private final MotionCurve coeff01Curve = new MotionCurve();
  @AnimAware
  private double coeff10;
  private final MotionCurve coeff10Curve = new MotionCurve();
  @AnimAware
  private double coeff11;
  private final MotionCurve coeff11Curve = new MotionCurve();
  @AnimAware
  private double coeff20;
  private final MotionCurve coeff20Curve = new MotionCurve();
  @AnimAware
  private double coeff21;
  private final MotionCurve coeff21Curve = new MotionCurve();
  @AnimAware
  private double postCoeff00;
  private final MotionCurve postCoeff00Curve = new MotionCurve();
  @AnimAware
  private double postCoeff01;
  private final MotionCurve postCoeff01Curve = new MotionCurve();
  @AnimAware
  private double postCoeff10;
  private final MotionCurve postCoeff10Curve = new MotionCurve();
  @AnimAware
  private double postCoeff11;
  private final MotionCurve postCoeff11Curve = new MotionCurve();
  @AnimAware
  private double postCoeff20;
  private final MotionCurve postCoeff20Curve = new MotionCurve();
  @AnimAware
  private double postCoeff21;
  private final MotionCurve postCoeff21Curve = new MotionCurve();
  private boolean hasPostCoeffs;
  private boolean hasCoeffs;
  @AnimAware
  private final List<Variation> variations = new ArrayList<Variation>();
  private final double modifiedWeights[] = new double[Constants.MAX_MOD_WEIGHT_COUNT]; // the same like "xaos" in Apophysis
  @AnimAware
  private double opacity = 0.0;
  private final XForm[] nextAppliedXFormTable = new XForm[Constants.NEXT_APPLIED_XFORM_TABLE_SIZE];
  private DrawMode drawMode = DrawMode.NORMAL;
  private String name = "";

  private final MotionCurve rotateCurve = new MotionCurve(RotateMotionValueChangeHandler.INSTANCE);
  private final MotionCurve scaleCurve = new MotionCurve(ScaleMotionValueChangeHandler.INSTANCE);
  private final MotionCurve postRotateCurve = new MotionCurve(PostRotateMotionValueChangeHandler.INSTANCE);
  private final MotionCurve postScaleCurve = new MotionCurve(PostScaleMotionValueChangeHandler.INSTANCE);

  public XForm() {
    coeff00 = 1;
    coeff11 = 1;
    postCoeff00 = 1;
    postCoeff11 = 1;
    for (int i = 0; i < modifiedWeights.length; i++) {
      modifiedWeights[i] = 1.0;
    }
    resetModColorEffects();
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public double getColor() {
    return color;
  }

  public void setColor(double color) {
    if (color < -1.0) {
      color = -1.0;
    }
    else if (color > 1.0) {
      color = 1.0;
    }
    this.color = color;
  }

  public double getCoeff00() {
    return coeff00;
  }

  public void setCoeff00(double coeff00) {
    this.coeff00 = coeff00;
    updateHasCoeffs();
  }

  public double getCoeff01() {
    return coeff01;
  }

  public void setCoeff01(double coeff01) {
    this.coeff01 = coeff01;
    updateHasCoeffs();
  }

  public double getCoeff10() {
    return coeff10;
  }

  public void setCoeff10(double coeff10) {
    this.coeff10 = coeff10;
    updateHasCoeffs();
  }

  public double getCoeff11() {
    return coeff11;
  }

  public void setCoeff11(double coeff11) {
    this.coeff11 = coeff11;
    updateHasCoeffs();
  }

  public double getCoeff20() {
    return coeff20;
  }

  public void setCoeff20(double coeff20) {
    this.coeff20 = coeff20;
    updateHasCoeffs();
  }

  public double getCoeff21() {
    return coeff21;
  }

  public void setCoeff21(double coeff21) {
    this.coeff21 = coeff21;
    updateHasCoeffs();
  }

  public int getVariationCount() {
    return variations.size();
  }

  public Variation getVariation(int pIdx) {
    return variations.get(pIdx);
  }

  public List<Variation> getVariations() {
    return variations;
  }

  public Variation addVariation(double pAmount, VariationFunc pVariationFunc) {
    Variation variation = new Variation();
    variation.setAmount(pAmount);
    variation.setFunc(pVariationFunc);
    variations.add(variation);
    return variation;
  }

  public void addVariation(Variation pVariation) {
    variations.add(pVariation);
  }

  public void removeVariation(Variation pVariation) {
    variations.remove(pVariation);
  }

  public void clearVariations() {
    variations.clear();
  }

  public double getColorSymmetry() {
    return colorSymmetry;
  }

  public void setColorSymmetry(double colorSymmetry) {
    if (colorSymmetry < -1.0) {
      colorSymmetry = -1.0;
    }
    else if (colorSymmetry > 1.0) {
      colorSymmetry = 1.0;
    }
    this.colorSymmetry = colorSymmetry;
  }

  private double c1, c2;
  private double modGamma1, modGamma2;
  private double modContrast1, modContrast2;
  private double modSaturation1, modSaturation2;

  public void initTransform() {
    // precalculate those variables to simplify the expression: 
    //   pDstPoint.color = (pSrcPoint.color + color) * 0.5 * (1 - colorSymmetry) + colorSymmetry * pSrcPoint.color;
    // to get:
    //   pDstPoint.color = pSrcPoint.color * c1 + c2;
    c1 = (1 + colorSymmetry) * 0.5;
    c2 = color * (1 - colorSymmetry) * 0.5;

    modGamma1 = (1 + modGammaSpeed) * 0.5;
    modGamma2 = modGamma * (1 - modGammaSpeed) * 0.5;

    modContrast1 = (1 + modContrastSpeed) * 0.5;
    modContrast2 = modContrast * (1 - modContrastSpeed) * 0.5;

    modSaturation1 = (1 + modSaturationSpeed) * 0.5;
    modSaturation2 = modSaturation * (1 - modSaturationSpeed) * 0.5;

    updateHasCoeffs();
    updateHasPostCoeffs();
  }

  private void updateHasPostCoeffs() {
    hasPostCoeffs = fabs(postCoeff00 - 1.0) > MathLib.EPSILON || fabs(postCoeff01) > MathLib.EPSILON || fabs(postCoeff10) > MathLib.EPSILON
        || fabs(postCoeff11 - 1.0) > MathLib.EPSILON || fabs(postCoeff20) > MathLib.EPSILON || fabs(postCoeff21) > MathLib.EPSILON;
  }

  private void updateHasCoeffs() {
    hasCoeffs = fabs(coeff00 - 1.0) > MathLib.EPSILON || fabs(coeff01) > MathLib.EPSILON || fabs(coeff10) > MathLib.EPSILON
        || fabs(coeff11 - 1.0) > MathLib.EPSILON || fabs(coeff20) > MathLib.EPSILON || fabs(coeff21) > MathLib.EPSILON;
  }

  public boolean isHasPostCoeffs() {
    return hasPostCoeffs;
  }

  public boolean isHasCoeffs() {
    return hasCoeffs;
  }

  public void transformPoint(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    pAffineT.clear();
    pAffineT.color = pSrcPoint.color * c1 + c2;
    pAffineT.modGamma = pSrcPoint.modGamma * modGamma1 + modGamma2;
    pAffineT.modContrast = pSrcPoint.modContrast * modContrast1 + modContrast2;
    pAffineT.modSaturation = pSrcPoint.modSaturation * modSaturation1 + modSaturation2;

    if (isHasCoeffs()) {
      pAffineT.x = coeff00 * pSrcPoint.x + coeff10 * pSrcPoint.y + coeff20;
      pAffineT.y = coeff01 * pSrcPoint.x + coeff11 * pSrcPoint.y + coeff21;
    }
    else {
      pAffineT.x = pSrcPoint.x;
      pAffineT.y = pSrcPoint.y;
    }
    pAffineT.z = pSrcPoint.z;
    //pVarT.clear();

    pVarT.invalidate();
    pVarT.x = pVarT.y = pVarT.z = pVarT.color = pVarT.modGamma = pVarT.modContrast = pVarT.modSaturation = 0.0;

    pVarT.color = pAffineT.color;
    pVarT.modGamma = pAffineT.modGamma;
    pVarT.modContrast = pAffineT.modContrast;
    pVarT.modSaturation = pAffineT.modSaturation;
    pVarT.dontPlot = pAffineT.dontPlot;
    pVarT.rgbColor = pAffineT.rgbColor;
    pVarT.redColor = pAffineT.redColor;
    pVarT.greenColor = pAffineT.greenColor;
    pVarT.blueColor = pAffineT.blueColor;

    for (Variation variation : variations) {
      if (variation.getFunc().getPriority() < 0) {
        variation.transform(pContext, this, pAffineT, pVarT);
        pAffineT.invalidate();
      }
    }
    for (Variation variation : variations) {
      if (variation.getFunc().getPriority() == 0) {
        variation.transform(pContext, this, pAffineT, pVarT);
      }
    }
    for (Variation variation : variations) {
      if (variation.getFunc().getPriority() > 0) {
        variation.transform(pContext, this, pAffineT, pVarT);
      }
    }

    pDstPoint.color = pVarT.color;
    pDstPoint.modGamma = pVarT.modGamma;
    pDstPoint.modContrast = pVarT.modContrast;
    pDstPoint.modSaturation = pVarT.modSaturation;
    pDstPoint.dontPlot = pVarT.dontPlot;
    pDstPoint.rgbColor = pVarT.rgbColor;
    pDstPoint.redColor = pVarT.redColor;
    pDstPoint.greenColor = pVarT.greenColor;
    pDstPoint.blueColor = pVarT.blueColor;
    if (isHasPostCoeffs()) {
      double px = postCoeff00 * pVarT.x + postCoeff10 * pVarT.y + postCoeff20;
      double py = postCoeff01 * pVarT.x + postCoeff11 * pVarT.y + postCoeff21;
      double pz = pVarT.z;
      pVarT.x = px;
      pVarT.y = py;
      pVarT.z = pz;
    }
    pDstPoint.x = pVarT.x;
    pDstPoint.y = pVarT.y;
    pDstPoint.z = pVarT.z;
  }

  public void transformPoints(FlameTransformationContext pContext, XYZPoint[] pAffineT, XYZPoint[] pVarT, XYZPoint[] pSrcPoint, XYZPoint[] pDstPoint) {
    for (int i = 0; i < 3; i++) {
      pAffineT[i].clear();
      pAffineT[i].color = pSrcPoint[i].color * c1 + c2;
      pAffineT[i].modGamma = pSrcPoint[i].modGamma * modGamma1 + modGamma2;
      pAffineT[i].modContrast = pSrcPoint[i].modContrast * modContrast1 + modContrast2;
      pAffineT[i].modSaturation = pSrcPoint[i].modSaturation * modSaturation1 + modSaturation2;
      pAffineT[i].dontPlot = pSrcPoint[i].dontPlot;
      pAffineT[i].x = coeff00 * pSrcPoint[i].x + coeff10 * pSrcPoint[i].y + coeff20;
      pAffineT[i].y = coeff01 * pSrcPoint[i].x + coeff11 * pSrcPoint[i].y + coeff21;
      pAffineT[i].z = pSrcPoint[i].z;
      pVarT[i].clear();
      pVarT[i].color = pAffineT[i].color;
      pVarT[i].modGamma = pAffineT[i].modGamma;
      pVarT[i].modContrast = pAffineT[i].modContrast;
      pVarT[i].modSaturation = pAffineT[i].modSaturation;
      pVarT[i].dontPlot = pAffineT[i].dontPlot;
      for (Variation variation : variations) {
        if (variation.getFunc().getPriority() < 0) {
          variation.transform(pContext, this, pAffineT[i], pVarT[i]);
          pAffineT[i].invalidate();
        }
      }
      for (Variation variation : variations) {
        if (variation.getFunc().getPriority() == 0) {
          variation.transform(pContext, this, pAffineT[i], pVarT[i]);
        }
      }
      for (Variation variation : variations) {
        if (variation.getFunc().getPriority() > 0) {
          variation.transform(pContext, this, pAffineT[i], pVarT[i]);
        }
      }
      pDstPoint[i].color = pVarT[i].color;
      pDstPoint[i].modGamma = pVarT[i].modGamma;
      pDstPoint[i].modContrast = pVarT[i].modContrast;
      pDstPoint[i].modSaturation = pVarT[i].modSaturation;
      if (isHasPostCoeffs()) {
        double px = postCoeff00 * pVarT[i].x + postCoeff10 * pVarT[i].y + postCoeff20;
        double py = postCoeff01 * pVarT[i].x + postCoeff11 * pVarT[i].y + postCoeff21;
        double pz = pVarT[i].z;
        pVarT[i].x = px;
        pVarT[i].y = py;
        pVarT[i].z = pz;
      }

      pDstPoint[i].x = pVarT[i].x;
      pDstPoint[i].y = pVarT[i].y;
      pDstPoint[i].z = pVarT[i].z;
    }
  }

  public double getOpacity() {
    return opacity;
  }

  public void setOpacity(double opacity) {
    this.opacity = opacity;
  }

  public double[] getModifiedWeights() {
    return modifiedWeights;
  }

  public XForm[] getNextAppliedXFormTable() {
    return nextAppliedXFormTable;
  }

  public DrawMode getDrawMode() {
    return drawMode;
  }

  public void setDrawMode(DrawMode drawMode) {
    this.drawMode = drawMode;
  }

  @Override
  public void assign(XForm pXForm) {
    weight = pXForm.weight;
    color = pXForm.color;
    colorSymmetry = pXForm.colorSymmetry;
    modGamma = pXForm.modGamma;
    modGammaSpeed = pXForm.modGammaSpeed;
    modContrast = pXForm.modContrast;
    modContrastSpeed = pXForm.modContrastSpeed;
    modSaturation = pXForm.modSaturation;
    modSaturationSpeed = pXForm.modSaturationSpeed;
    coeff00 = pXForm.coeff00;
    coeff00Curve.assign(pXForm.coeff00Curve);
    coeff01 = pXForm.coeff01;
    coeff01Curve.assign(pXForm.coeff01Curve);
    coeff10 = pXForm.coeff10;
    coeff10Curve.assign(pXForm.coeff10Curve);
    coeff11 = pXForm.coeff11;
    coeff11Curve.assign(pXForm.coeff11Curve);
    coeff20 = pXForm.coeff20;
    coeff20Curve.assign(pXForm.coeff20Curve);
    coeff21 = pXForm.coeff21;
    coeff21Curve.assign(pXForm.coeff21Curve);
    postCoeff00 = pXForm.postCoeff00;
    postCoeff00Curve.assign(pXForm.postCoeff00Curve);
    postCoeff01 = pXForm.postCoeff01;
    postCoeff01Curve.assign(pXForm.postCoeff01Curve);
    postCoeff10 = pXForm.postCoeff10;
    postCoeff10Curve.assign(pXForm.postCoeff10Curve);
    postCoeff11 = pXForm.postCoeff11;
    postCoeff11Curve.assign(pXForm.postCoeff11Curve);
    postCoeff20 = pXForm.postCoeff20;
    postCoeff20Curve.assign(pXForm.postCoeff20Curve);
    postCoeff21 = pXForm.postCoeff21;
    postCoeff21Curve.assign(pXForm.postCoeff21Curve);
    rotateCurve.assign(pXForm.rotateCurve);
    scaleCurve.assign(pXForm.scaleCurve);
    postRotateCurve.assign(pXForm.postRotateCurve);
    postScaleCurve.assign(pXForm.postScaleCurve);
    hasPostCoeffs = pXForm.hasPostCoeffs;
    hasCoeffs = pXForm.hasCoeffs;
    variations.clear();
    for (Variation var : pXForm.variations) {
      Variation newVar = new Variation();
      newVar.assign(var);
      variations.add(newVar);
    }
    System.arraycopy(pXForm.modifiedWeights, 0, modifiedWeights, 0, pXForm.modifiedWeights.length);
    opacity = pXForm.opacity;
    drawMode = pXForm.drawMode;
    name = pXForm.name;
  }

  @Override
  public XForm makeCopy() {
    XForm res = new XForm();
    res.assign(this);
    return res;
  }

  public double getPostCoeff00() {
    return postCoeff00;
  }

  public void setPostCoeff00(double postCoeff00) {
    this.postCoeff00 = postCoeff00;
    updateHasPostCoeffs();
  }

  public double getPostCoeff01() {
    return postCoeff01;
  }

  public void setPostCoeff01(double postCoeff01) {
    this.postCoeff01 = postCoeff01;
    updateHasPostCoeffs();
  }

  public double getPostCoeff10() {
    return postCoeff10;
  }

  public void setPostCoeff10(double postCoeff10) {
    this.postCoeff10 = postCoeff10;
    updateHasPostCoeffs();
  }

  public double getPostCoeff11() {
    return postCoeff11;
  }

  public void setPostCoeff11(double postCoeff11) {
    this.postCoeff11 = postCoeff11;
    updateHasPostCoeffs();
  }

  public double getPostCoeff20() {
    return postCoeff20;
  }

  public void setPostCoeff20(double postCoeff20) {
    this.postCoeff20 = postCoeff20;
    updateHasPostCoeffs();
  }

  public double getPostCoeff21() {
    return postCoeff21;
  }

  public void setPostCoeff21(double postCoeff21) {
    this.postCoeff21 = postCoeff21;
    updateHasPostCoeffs();
  }

  @Override
  public boolean isEqual(XForm pSrc) {
    if ((fabs(weight - pSrc.weight) > EPSILON) ||
        (fabs(color - pSrc.color) > EPSILON) || (fabs(colorSymmetry - pSrc.colorSymmetry) > EPSILON) ||
        (fabs(modGamma - pSrc.modGamma) > EPSILON) || (fabs(modGammaSpeed - pSrc.modGammaSpeed) > EPSILON) ||
        (fabs(modContrast - pSrc.modContrast) > EPSILON) || (fabs(modContrastSpeed - pSrc.modContrastSpeed) > EPSILON) ||
        (fabs(modSaturation - pSrc.modSaturation) > EPSILON) || (fabs(modSaturationSpeed - pSrc.modSaturationSpeed) > EPSILON) ||
        (fabs(coeff00 - pSrc.coeff00) > EPSILON) || !coeff00Curve.isEqual(pSrc.coeff00Curve) ||
        (fabs(coeff01 - pSrc.coeff01) > EPSILON) || !coeff01Curve.isEqual(pSrc.coeff01Curve) ||
        (fabs(coeff10 - pSrc.coeff10) > EPSILON) || !coeff10Curve.isEqual(pSrc.coeff10Curve) ||
        (fabs(coeff11 - pSrc.coeff11) > EPSILON) || !coeff11Curve.isEqual(pSrc.coeff11Curve) ||
        (fabs(coeff20 - pSrc.coeff20) > EPSILON) || !coeff20Curve.isEqual(pSrc.coeff20Curve) ||
        (fabs(coeff21 - pSrc.coeff21) > EPSILON) || !coeff21Curve.isEqual(pSrc.coeff21Curve) ||
        (fabs(postCoeff00 - pSrc.postCoeff00) > EPSILON) || !postCoeff00Curve.isEqual(pSrc.postCoeff00Curve) ||
        (fabs(postCoeff01 - pSrc.postCoeff01) > EPSILON) || !postCoeff01Curve.isEqual(pSrc.postCoeff01Curve) ||
        (fabs(postCoeff10 - pSrc.postCoeff10) > EPSILON) || !postCoeff10Curve.isEqual(pSrc.postCoeff10Curve) ||
        (fabs(postCoeff11 - pSrc.postCoeff11) > EPSILON) || !postCoeff11Curve.isEqual(pSrc.postCoeff11Curve) ||
        (fabs(postCoeff20 - pSrc.postCoeff20) > EPSILON) || !postCoeff20Curve.isEqual(pSrc.postCoeff20Curve) ||
        (fabs(postCoeff21 - pSrc.postCoeff21) > EPSILON) || !postCoeff21Curve.isEqual(pSrc.postCoeff21Curve) ||
        !rotateCurve.isEqual(pSrc.rotateCurve) || !scaleCurve.isEqual(pSrc.scaleCurve) ||
        !postRotateCurve.isEqual(pSrc.postRotateCurve) || !postScaleCurve.isEqual(pSrc.postScaleCurve) ||
        (fabs(opacity - pSrc.opacity) > EPSILON) ||
        ((drawMode != null && pSrc.drawMode == null) || (drawMode == null && pSrc.drawMode != null) ||
        (drawMode != null && pSrc.drawMode != null && !drawMode.equals(pSrc.drawMode))) ||
        !name.equals(pSrc.name) ||
        (modifiedWeights.length != pSrc.modifiedWeights.length) || (variations.size() != pSrc.variations.size())) {
      return false;
    }
    for (int i = 0; i < modifiedWeights.length; i++) {
      if (fabs(modifiedWeights[i] - pSrc.modifiedWeights[i]) > EPSILON) {
        return false;
      }
    }
    for (int i = 0; i < variations.size(); i++) {
      if (!variations.get(i).isEqual(pSrc.variations.get(i))) {
        return false;
      }
    }
    return true;
  }

  // only because of script-compatiblity
  @Deprecated
  public void setAntialiasAmount(double antialiasAmount) {
    // this.antialiasAmount = antialiasAmount;
  }

  // only because of script-compatiblity
  @Deprecated
  public void setAntialiasRadius(double antialiasRadius) {
    // this.antialiasRadius = antialiasRadius;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name != null ? name : "";
  }

  public boolean hasVariation(String pName) {
    for (Variation var : variations) {
      if (var.getFunc().getName().equalsIgnoreCase(pName)) {
        return true;
      }
    }
    return false;
  }

  public MotionCurve getRotateCurve() {
    return rotateCurve;
  }

  public MotionCurve getScaleCurve() {
    return scaleCurve;
  }

  public MotionCurve getPostRotateCurve() {
    return postRotateCurve;
  }

  public MotionCurve getPostScaleCurve() {
    return postScaleCurve;
  }

  public double getModGamma() {
    return modGamma;
  }

  public void setModGamma(double modGamma) {
    this.modGamma = modGamma;
  }

  public double getModGammaSpeed() {
    return modGammaSpeed;
  }

  public void setModGammaSpeed(double modGammaSpeed) {
    this.modGammaSpeed = modGammaSpeed;
  }

  public double getModContrast() {
    return modContrast;
  }

  public void setModContrast(double modContrast) {
    this.modContrast = modContrast;
  }

  public double getModContrastSpeed() {
    return modContrastSpeed;
  }

  public void setModContrastSpeed(double modContrastSpeed) {
    this.modContrastSpeed = modContrastSpeed;
  }

  public double getModSaturation() {
    return modSaturation;
  }

  public void setModSaturation(double modSaturation) {
    this.modSaturation = modSaturation;
  }

  public double getModSaturationSpeed() {
    return modSaturationSpeed;
  }

  public void setModSaturationSpeed(double modSaturationSpeed) {
    this.modSaturationSpeed = modSaturationSpeed;
  }

  public void randomizeModColorEffects() {
    modGamma = 1.0 - 2.0 * Math.random();
    modGammaSpeed = Math.random() < 0.33 ? 1.0 - 2.0 * Math.random() : 0;
    modContrast = 1.0 - 2.0 * Math.random();
    modContrastSpeed = Math.random() < 0.33 ? 1.0 - 2.0 * Math.random() : 0;
    modSaturation = 1.0 - 2.0 * Math.random();
    modSaturationSpeed = Math.random() < 0.33 ? 1.0 - 2.0 * Math.random() : 0;
  }

  public void resetModColorEffects() {
    modGamma = 0.0;
    modGammaSpeed = 0.0;
    modContrast = 0.0;
    modContrastSpeed = 0.0;
    modSaturation = 0.0;
    modSaturationSpeed = 0.0;
  }
}
