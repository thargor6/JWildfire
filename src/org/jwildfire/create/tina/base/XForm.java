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

import java.awt.Color;
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
import org.jwildfire.create.tina.base.weightingfield.*;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.palette.RGBColor;
import org.jwildfire.create.tina.palette.RGBPalette;
import org.jwildfire.create.tina.palette.RenderColor;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;

public final class XForm implements Assignable<XForm>, Serializable {
  private static final long serialVersionUID = 1L;
  private Layer owner;
  @AnimAware
  private double weight;
  private final MotionCurve weightCurve = new MotionCurve();
  @AnimAware
  private double color;
  private final MotionCurve colorCurve = new MotionCurve();
  @AnimAware
  private double colorSymmetry;
  private final MotionCurve colorSymmetryCurve = new MotionCurve();
  private RGBColor targetColor = new RGBColor(0, 0, 0);
  @AnimAware
  private double material;
  private final MotionCurve materialCurve = new MotionCurve();
  @AnimAware
  private double materialSpeed;
  private final MotionCurve materialSpeedCurve = new MotionCurve();
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
  private double modHue;
  @AnimAware
  private double modHueSpeed;

  @AnimAware
  double xyCoeff00;
  private final MotionCurve xyCoeff00Curve = new MotionCurve();
  @AnimAware
  double xyCoeff01;
  private final MotionCurve xyCoeff01Curve = new MotionCurve();
  @AnimAware
  double xyCoeff10;
  private final MotionCurve xyCoeff10Curve = new MotionCurve();
  @AnimAware
  double xyCoeff11;
  private final MotionCurve xyCoeff11Curve = new MotionCurve();
  @AnimAware
  double xyCoeff20;
  private final MotionCurve xyCoeff20Curve = new MotionCurve();
  @AnimAware
  double xyCoeff21;
  private final MotionCurve xyCoeff21Curve = new MotionCurve();
  @AnimAware
  double xyPostCoeff00;
  private final MotionCurve xyPostCoeff00Curve = new MotionCurve();
  @AnimAware
  double xyPostCoeff01;
  private final MotionCurve xyPostCoeff01Curve = new MotionCurve();
  @AnimAware
  double xyPostCoeff10;
  private final MotionCurve xyPostCoeff10Curve = new MotionCurve();
  @AnimAware
  double xyPostCoeff11;
  private final MotionCurve xyPostCoeff11Curve = new MotionCurve();
  @AnimAware
  double xyPostCoeff20;
  private final MotionCurve xyPostCoeff20Curve = new MotionCurve();
  @AnimAware
  double xyPostCoeff21;
  private final MotionCurve xyPostCoeff21Curve = new MotionCurve();
  boolean hasXYPostCoeffs;
  boolean hasXYCoeffs;

  @AnimAware
  double yzCoeff00;
  private final MotionCurve yzCoeff00Curve = new MotionCurve();
  @AnimAware
  double yzCoeff01;
  private final MotionCurve yzCoeff01Curve = new MotionCurve();
  @AnimAware
  double yzCoeff10;
  private final MotionCurve yzCoeff10Curve = new MotionCurve();
  @AnimAware
  double yzCoeff11;
  private final MotionCurve yzCoeff11Curve = new MotionCurve();
  @AnimAware
  double yzCoeff20;
  private final MotionCurve yzCoeff20Curve = new MotionCurve();
  @AnimAware
  double yzCoeff21;
  private final MotionCurve yzCoeff21Curve = new MotionCurve();
  @AnimAware
  double yzPostCoeff00;
  private final MotionCurve yzPostCoeff00Curve = new MotionCurve();
  @AnimAware
  double yzPostCoeff01;
  private final MotionCurve yzPostCoeff01Curve = new MotionCurve();
  @AnimAware
  double yzPostCoeff10;
  private final MotionCurve yzPostCoeff10Curve = new MotionCurve();
  @AnimAware
  double yzPostCoeff11;
  private final MotionCurve yzPostCoeff11Curve = new MotionCurve();
  @AnimAware
  double yzPostCoeff20;
  private final MotionCurve yzPostCoeff20Curve = new MotionCurve();
  @AnimAware
  double yzPostCoeff21;
  private final MotionCurve yzPostCoeff21Curve = new MotionCurve();
  boolean hasYZPostCoeffs;
  boolean hasYZCoeffs;

  @AnimAware
  double zxCoeff00;
  private final MotionCurve zxCoeff00Curve = new MotionCurve();
  @AnimAware
  double zxCoeff01;
  private final MotionCurve zxCoeff01Curve = new MotionCurve();
  @AnimAware
  double zxCoeff10;
  private final MotionCurve zxCoeff10Curve = new MotionCurve();
  @AnimAware
  double zxCoeff11;
  private final MotionCurve zxCoeff11Curve = new MotionCurve();
  @AnimAware
  double zxCoeff20;
  private final MotionCurve zxCoeff20Curve = new MotionCurve();
  @AnimAware
  double zxCoeff21;
  private final MotionCurve zxCoeff21Curve = new MotionCurve();
  @AnimAware
  double zxPostCoeff00;
  private final MotionCurve zxPostCoeff00Curve = new MotionCurve();
  @AnimAware
  double zxPostCoeff01;
  private final MotionCurve zxPostCoeff01Curve = new MotionCurve();
  @AnimAware
  double zxPostCoeff10;
  private final MotionCurve zxPostCoeff10Curve = new MotionCurve();
  @AnimAware
  double zxPostCoeff11;
  private final MotionCurve zxPostCoeff11Curve = new MotionCurve();
  @AnimAware
  double zxPostCoeff20;
  private final MotionCurve zxPostCoeff20Curve = new MotionCurve();
  @AnimAware
  double zxPostCoeff21;
  private final MotionCurve zxPostCoeff21Curve = new MotionCurve();
  boolean hasZXPostCoeffs;
  boolean hasZXCoeffs;

  private WeightingFieldType weightingFieldType = WeightingFieldType.NONE;
  private WeightingFieldInputType weightingFieldInput = WeightingFieldInputType.AFFINE;
  @AnimAware
  private double weightingFieldColorIntensity;
  private final MotionCurve weightingFieldColorIntensityCurve = new MotionCurve();
  @AnimAware
  private double weightingFieldVarAmountIntensity;
  private final MotionCurve weightingFieldVarAmountIntensityCurve = new MotionCurve();

  @AnimAware
  private double weightingFieldVarParam1Intensity;
  private String weightingFieldVarParam1VarName = "";
  private String weightingFieldVarParam1ParamName = "";
  private final MotionCurve weightingFieldVarParam1IntensityCurve = new MotionCurve();
  @AnimAware
  private double weightingFieldVarParam2Intensity;
  private String weightingFieldVarParam2VarName = "";
  private String weightingFieldVarParam2ParamName = "";
  private final MotionCurve weightingFieldVarParam2IntensityCurve = new MotionCurve();
  @AnimAware
  private double weightingFieldVarParam3Intensity;
  private String weightingFieldVarParam3VarName = "";
  private String weightingFieldVarParam3ParamName = "";
  private final MotionCurve weightingFieldVarParam3IntensityCurve = new MotionCurve();
  @AnimAware
  private double weightingFieldJitterIntensity;
  private final MotionCurve weightingFieldJitterIntensityCurve = new MotionCurve();

  private String weightingFieldColorMapFilename = "";
  private double weightingFieldColorMapXSize = 4.0;
  private final MotionCurve weightingFieldColorMapXSizeCurve = new MotionCurve();
  private double weightingFieldColorMapYSize = 4.0;
  private final MotionCurve weightingFieldColorMapYSizeCurve = new MotionCurve();
  private double weightingFieldColorMapXCentre = 0.0;
  private final MotionCurve weightingFieldColorMapXCentreCurve = new MotionCurve();
  private double weightingFieldColorMapYCentre = 0.0;
  private final MotionCurve weightingFieldColorMapYCentreCurve = new MotionCurve();

  private int weightingFieldNoiseSeed = 1337;
  private final MotionCurve weightingFieldNoiseSeedCurve = new MotionCurve();
  private double weightingFieldNoiseFrequency = 1.0;
  private final MotionCurve weightingFieldNoiseFrequencyCurve = new MotionCurve();

  private FractalType weightingFieldFractalType = FractalType.FBM;
  private int weightingFieldFractalNoiseOctaves = 3;
  private final MotionCurve weightingFieldFractalNoiseOctavesCurve = new MotionCurve();
  private double weightingFieldFractalNoiseGain = 0.5;
  private final MotionCurve weightingFieldFractalNoiseGainCurve = new MotionCurve();
  private double weightingFieldFractalNoiseLacunarity = 2.0;
  private final MotionCurve weightingFieldFractalNoiseLacunarityCurve = new MotionCurve();
  private CellularNoiseReturnType weightingFieldCellularNoiseReturnType = CellularNoiseReturnType.DISTANCE2;
  private CellularNoiseDistanceFunction weightingFieldCellularNoiseDistanceFunction = CellularNoiseDistanceFunction.EUCLIDIAN;

  private int index = -1;

  @AnimAware
  private final List<Variation> variations = new ArrayList<Variation>();
  private final double modifiedWeights[] = new double[Constants.MAX_MOD_WEIGHT_COUNT]; // the same like "xaos" in Apophysis
  @AnimAware
  private double opacity = 0.0;
  private final MotionCurve opacityCurve = new MotionCurve();
  private final XForm[] nextAppliedXFormTable = new XForm[Constants.NEXT_APPLIED_XFORM_TABLE_SIZE];
  private DrawMode drawMode = DrawMode.NORMAL;
  private ColorType colorType = ColorType.UNSET;
  private String name = "";

  private final MotionCurve xyRotateCurve = new MotionCurve(RotateMotionValueChangeHandler.INSTANCE);
  private final MotionCurve xyScaleCurve = new MotionCurve(ScaleMotionValueChangeHandler.INSTANCE);
  private final MotionCurve xyPostRotateCurve = new MotionCurve(PostRotateMotionValueChangeHandler.INSTANCE);
  private final MotionCurve xyPostScaleCurve = new MotionCurve(PostScaleMotionValueChangeHandler.INSTANCE);

  private final MotionCurve yzRotateCurve = new MotionCurve(RotateMotionValueChangeHandler.INSTANCE);
  private final MotionCurve yzScaleCurve = new MotionCurve(ScaleMotionValueChangeHandler.INSTANCE);
  private final MotionCurve yzPostRotateCurve = new MotionCurve(PostRotateMotionValueChangeHandler.INSTANCE);
  private final MotionCurve yzPostScaleCurve = new MotionCurve(PostScaleMotionValueChangeHandler.INSTANCE);

  private final MotionCurve zxRotateCurve = new MotionCurve(RotateMotionValueChangeHandler.INSTANCE);
  private final MotionCurve zxScaleCurve = new MotionCurve(ScaleMotionValueChangeHandler.INSTANCE);
  private final MotionCurve zxPostRotateCurve = new MotionCurve(PostRotateMotionValueChangeHandler.INSTANCE);
  private final MotionCurve zxPostScaleCurve = new MotionCurve(PostScaleMotionValueChangeHandler.INSTANCE);

  public XForm() {
    xyCoeff00 = 1.0;
    xyCoeff11 = 1.0;
    xyPostCoeff00 = 1.0;
    xyPostCoeff11 = 1.0;

    yzCoeff00 = 1.0;
    yzCoeff11 = 1.0;
    yzPostCoeff00 = 1.0;
    yzPostCoeff11 = 1.0;

    zxCoeff00 = 1.0;
    zxCoeff11 = 1.0;
    zxPostCoeff00 = 1.0;
    zxPostCoeff11 = 1.0;

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
    variation.setPriority(pVariationFunc.getPriority());
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

  double c1;
  double c2;
  double material1;
  double material2;
  double modGamma1;
  double modGamma2;
  double modContrast1;
  double modContrast2;
  double modSaturation1;
  double modSaturation2;
  double modHue1;
  double modHue2;
  RenderColor targetRenderColor;
  double distMult;
  double oldx, oldy, oldz;

  public void initTransform() {
    // precalculate those variables to simplify the expression: 
    //   pDstPoint.color = (pSrcPoint.color + color) * 0.5 * (1 - colorSymmetry) + colorSymmetry * pSrcPoint.color;
    // to get:
    //   pDstPoint.color = pSrcPoint.color * c1 + c2;
    c1 = (1 + colorSymmetry) * 0.5;
    c2 = color * (1 - colorSymmetry) * 0.5;
    if (colorType != ColorType.DIFFUSION && colorType != ColorType.TARGETG) {
      c1 = 1;
      c2 = 0;
    }
    if (colorType == ColorType.DISTANCE) {
      distMult = colorSymmetry + 1;
    }
    if (colorType == ColorType.TARGET) {
      targetRenderColor = new RenderColor(owner.getOwner().getWhiteLevel(), targetColor);
    }
    if (colorType == ColorType.TARGETG) {
      RenderColor[] colorMap = owner.getColorMap();
      double paletteIdxScl = colorMap.length - 2;
      int colorIdx = (int) (color * paletteIdxScl + 0.5);
      if (colorIdx < 0)
        colorIdx = 0;
      else if (colorIdx >= RGBPalette.PALETTE_SIZE)
        colorIdx = RGBPalette.PALETTE_SIZE - 1;
      
      targetRenderColor = colorMap[colorIdx];
    }
    material1 = (1 + materialSpeed) * 0.5;
    material2 = material * (1 - materialSpeed) * 0.5;

    modGamma1 = (1 + modGammaSpeed) * 0.5;
    modGamma2 = modGamma * (1 - modGammaSpeed) * 0.5;

    modContrast1 = (1 + modContrastSpeed) * 0.5;
    modContrast2 = modContrast * (1 - modContrastSpeed) * 0.5;

    modSaturation1 = (1 + modSaturationSpeed) * 0.5;
    modSaturation2 = modSaturation * (1 - modSaturationSpeed) * 0.5;

    modHue1 = (1 + modHueSpeed) * 0.5;
    modHue2 = modHue * (1 - modHueSpeed) * 0.5;

    updateHasXYCoeffs();
    updateHasXYPostCoeffs();
    updateHasYZCoeffs();
    updateHasYZPostCoeffs();
    updateHasZXCoeffs();
    updateHasZXPostCoeffs();

    createTransformations();
  }

  public void notifyCoeffChange() {
    updateHasXYCoeffs();
    updateHasXYPostCoeffs();
    updateHasYZCoeffs();
    updateHasYZPostCoeffs();
    updateHasZXCoeffs();
    updateHasZXPostCoeffs();
  }

  private void updateHasXYPostCoeffs() {
    hasXYPostCoeffs = fabs(xyPostCoeff00 - 1.0) > MathLib.EPSILON || fabs(xyPostCoeff01) > MathLib.EPSILON || fabs(xyPostCoeff10) > MathLib.EPSILON
        || fabs(xyPostCoeff11 - 1.0) > MathLib.EPSILON || fabs(xyPostCoeff20) > MathLib.EPSILON || fabs(xyPostCoeff21) > MathLib.EPSILON;
  }

  private void updateHasXYCoeffs() {
    hasXYCoeffs = fabs(xyCoeff00 - 1.0) > MathLib.EPSILON || fabs(xyCoeff01) > MathLib.EPSILON || fabs(xyCoeff10) > MathLib.EPSILON
        || fabs(xyCoeff11 - 1.0) > MathLib.EPSILON || fabs(xyCoeff20) > MathLib.EPSILON || fabs(xyCoeff21) > MathLib.EPSILON;
  }

  public boolean isHasXYPostCoeffs() {
    return hasXYPostCoeffs;
  }

  public boolean isHasXYCoeffs() {
    return hasXYCoeffs;
  }

  private void updateHasYZPostCoeffs() {
    hasYZPostCoeffs = fabs(yzPostCoeff00 - 1.0) > MathLib.EPSILON || fabs(yzPostCoeff01) > MathLib.EPSILON || fabs(yzPostCoeff10) > MathLib.EPSILON
        || fabs(yzPostCoeff11 - 1.0) > MathLib.EPSILON || fabs(yzPostCoeff20) > MathLib.EPSILON || fabs(yzPostCoeff21) > MathLib.EPSILON;
  }

  private void updateHasYZCoeffs() {
    hasYZCoeffs = fabs(yzCoeff00 - 1.0) > MathLib.EPSILON || fabs(yzCoeff01) > MathLib.EPSILON || fabs(yzCoeff10) > MathLib.EPSILON
        || fabs(yzCoeff11 - 1.0) > MathLib.EPSILON || fabs(yzCoeff20) > MathLib.EPSILON || fabs(yzCoeff21) > MathLib.EPSILON;
  }

  public boolean isHasYZPostCoeffs() {
    return hasYZPostCoeffs;
  }

  public boolean isHasYZCoeffs() {
    return hasYZCoeffs;
  }

  private void updateHasZXPostCoeffs() {
    hasZXPostCoeffs = fabs(zxPostCoeff00 - 1.0) > MathLib.EPSILON || fabs(zxPostCoeff01) > MathLib.EPSILON || fabs(zxPostCoeff10) > MathLib.EPSILON
        || fabs(zxPostCoeff11 - 1.0) > MathLib.EPSILON || fabs(zxPostCoeff20) > MathLib.EPSILON || fabs(zxPostCoeff21) > MathLib.EPSILON;
  }

  private void updateHasZXCoeffs() {
    hasZXCoeffs = fabs(zxCoeff00 - 1.0) > MathLib.EPSILON || fabs(zxCoeff01) > MathLib.EPSILON || fabs(zxCoeff10) > MathLib.EPSILON
        || fabs(zxCoeff11 - 1.0) > MathLib.EPSILON || fabs(zxCoeff20) > MathLib.EPSILON || fabs(zxCoeff21) > MathLib.EPSILON;
  }

  public boolean isHasZXPostCoeffs() {
    return hasZXPostCoeffs;
  }

  public boolean isHasZXCoeffs() {
    return hasZXCoeffs;
  }

  private List<TransformationStep> t = new ArrayList<>();

  private void createTransformations() {
    t = new ArrayList<TransformationStep>();
    t.add(new TransformationInitStep(this));
    if (!isHasXYCoeffs() && !isHasYZCoeffs() && !isHasZXCoeffs()) {
      t.add(new TransformationAffineNoneStep(this));
    }
    else if (isHasXYCoeffs() && !isHasYZCoeffs() && !isHasZXCoeffs()) {
      t.add(new TransformationAffineFlatStep(this));
    }
    else {
      t.add(new TransformationAffineFullStep(this));
    }
    if(isWeightingFieldActive()) {
      t.add(new TransformationInitWeightingFieldStep(this));
    }


    t.add(new TransformationPreparePreVariationsStep(this));

    for (Variation variation : variations) {
      variation.clearWeightingFieldParams();
      if(fabs(weightingFieldVarParam1Intensity)>EPSILON && variation.getFunc().getName().equals(weightingFieldVarParam1VarName)) {
        variation.addWeightingFieldParam(weightingFieldVarParam1ParamName, weightingFieldVarParam1Intensity);
      }
      if(fabs(weightingFieldVarParam2Intensity)>EPSILON && variation.getFunc().getName().equals(weightingFieldVarParam2VarName)) {
        variation.addWeightingFieldParam(weightingFieldVarParam2ParamName, weightingFieldVarParam2Intensity);
      }
      if(fabs(weightingFieldVarParam3Intensity)>EPSILON && variation.getFunc().getName().equals(weightingFieldVarParam3VarName)) {
        variation.addWeightingFieldParam(weightingFieldVarParam3ParamName, weightingFieldVarParam3Intensity);
      }
      if (variation.getPriority() < 0 || variation.getPriority() == 2) {
        if (variation.getFunc().getPriority() == 2) {
        	t.add(new PreInvVariationTransformationStep(this, variation));
        }
        else if (variation.getFunc().getPriority() < 0) {
          t.add(new PreVariationTransformationStep(this, variation));
        }
        else {
          t.add(new EnforcedPreVariationTransformationStep(this, variation));
        }
      }
    }

    t.add(new TransformationPrepareVariationsStep(this));

    for (Variation variation : variations) {
      if (variation.getPriority() == 0) {
        if (variation.getFunc().getPriority() == 0) {
          t.add(new VariationTransformationStep(this, variation));
        }
        else {
          t.add(new EnforcedVariationTransformationStep(this, variation));
        }
      }
    }

    for (Variation variation : variations) {
      if (variation.getPriority() > 0 || variation.getPriority() == -2) {
        if (variation.getFunc().getPriority() == -2) {
          t.add(new PostInvVariationTransformationStep(this, variation));
        }
        else if (variation.getFunc().getPriority() > 0) {
          t.add(new PostVariationTransformationStep(this, variation));
        }
        else {
          t.add(new EnforcedPostVariationTransformationStep(this, variation));
        }
      }
    }

    t.add(new TransformationPreparePostAffineStep(this));
    if (!isHasXYPostCoeffs() && !isHasYZPostCoeffs() && !isHasZXPostCoeffs()) {
      t.add(new TransformationPostAffineNoneStep(this));
    }
    else if (isHasXYPostCoeffs() && !isHasYZPostCoeffs() && !isHasZXPostCoeffs()) {
      t.add(new TransformationPostAffineFlatStep(this));
    }
    else {
      t.add(new TransformationPostAffineStep(this));
    }
    
    if (isWeightingFieldActive() && fabs(weightingFieldJitterIntensity)>EPSILON) {
      t.add(new TransformationApplyWeightMapJitter(this));
    }

    if(isWeightingFieldActive() && fabs(weightingFieldColorIntensity)>EPSILON) {
      t.add(new TransformationApplyWeightMapToColorStep(this));
    }

    if (colorType == ColorType.DIFFUSION || colorType == ColorType.CYCLIC) {
      if (owner.isGradientMap()) {
        t.add(new TransformationGradientMapColorStep(this));
      }
      else if (owner.isSmoothGradient()) {
        t.add(new TransformationSmoothGradientColorStep(this));
      }
      else {
        t.add(new TransformationGradientColorStep(this));
      }
    }
    else if (colorType == ColorType.TARGET || colorType == ColorType.TARGETG) {
      t.add(new TransformationTargetColorStep(this));
    }
    else if (colorType == ColorType.DISTANCE) {
      t.add(new TransformationDistanceColorStep(this));
    }

  }

  public void transformPoint(FlameTransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint) {
    for (TransformationStep transformation : t) {
      transformation.transform(pContext, pAffineT, pVarT, pSrcPoint, pDstPoint);
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
  
  public ColorType getColorType() {
    return colorType;
  }

  public void setColorType(ColorType colorType) {
    this.colorType = colorType;
  }
  
  public RGBColor getTargetColor() {
    return targetColor;
  }
  
  public void setTargetColor(RGBColor c) {
    targetColor = c;
  }
  
  public void setTargetColor(int r, int g, int b) {
    targetColor.setRed(r);
    targetColor.setGreen(g);
    targetColor.setBlue(b);
  }
  
  public void setTargetColor(Color c) {
    targetColor.setColor(c);
  }

 @Override
  public void assign(XForm pXForm) {
    weight = pXForm.weight;
    weightCurve.assign(pXForm.weightCurve);
    color = pXForm.color;
    colorCurve.assign(pXForm.colorCurve);
    targetColor.assign(pXForm.targetColor);
    colorSymmetry = pXForm.colorSymmetry;
    colorSymmetryCurve.assign(pXForm.colorSymmetryCurve);
    material = pXForm.material;
    materialCurve.assign(pXForm.materialCurve);
    materialSpeed = pXForm.materialSpeed;
    materialSpeedCurve.assign(pXForm.materialSpeedCurve);
    modGamma = pXForm.modGamma;
    modGammaSpeed = pXForm.modGammaSpeed;
    modContrast = pXForm.modContrast;
    modContrastSpeed = pXForm.modContrastSpeed;
    modSaturation = pXForm.modSaturation;
    modSaturationSpeed = pXForm.modSaturationSpeed;
    modHue = pXForm.modHue;
    modHueSpeed = pXForm.modHueSpeed;

    xyCoeff00 = pXForm.xyCoeff00;
    xyCoeff00Curve.assign(pXForm.xyCoeff00Curve);
    xyCoeff01 = pXForm.xyCoeff01;
    xyCoeff01Curve.assign(pXForm.xyCoeff01Curve);
    xyCoeff10 = pXForm.xyCoeff10;
    xyCoeff10Curve.assign(pXForm.xyCoeff10Curve);
    xyCoeff11 = pXForm.xyCoeff11;
    xyCoeff11Curve.assign(pXForm.xyCoeff11Curve);
    xyCoeff20 = pXForm.xyCoeff20;
    xyCoeff20Curve.assign(pXForm.xyCoeff20Curve);
    xyCoeff21 = pXForm.xyCoeff21;
    xyCoeff21Curve.assign(pXForm.xyCoeff21Curve);
    xyPostCoeff00 = pXForm.xyPostCoeff00;
    xyPostCoeff00Curve.assign(pXForm.xyPostCoeff00Curve);
    xyPostCoeff01 = pXForm.xyPostCoeff01;
    xyPostCoeff01Curve.assign(pXForm.xyPostCoeff01Curve);
    xyPostCoeff10 = pXForm.xyPostCoeff10;
    xyPostCoeff10Curve.assign(pXForm.xyPostCoeff10Curve);
    xyPostCoeff11 = pXForm.xyPostCoeff11;
    xyPostCoeff11Curve.assign(pXForm.xyPostCoeff11Curve);
    xyPostCoeff20 = pXForm.xyPostCoeff20;
    xyPostCoeff20Curve.assign(pXForm.xyPostCoeff20Curve);
    xyPostCoeff21 = pXForm.xyPostCoeff21;
    xyPostCoeff21Curve.assign(pXForm.xyPostCoeff21Curve);

    yzCoeff00 = pXForm.yzCoeff00;
    yzCoeff00Curve.assign(pXForm.yzCoeff00Curve);
    yzCoeff01 = pXForm.yzCoeff01;
    yzCoeff01Curve.assign(pXForm.yzCoeff01Curve);
    yzCoeff10 = pXForm.yzCoeff10;
    yzCoeff10Curve.assign(pXForm.yzCoeff10Curve);
    yzCoeff11 = pXForm.yzCoeff11;
    yzCoeff11Curve.assign(pXForm.yzCoeff11Curve);
    yzCoeff20 = pXForm.yzCoeff20;
    yzCoeff20Curve.assign(pXForm.yzCoeff20Curve);
    yzCoeff21 = pXForm.yzCoeff21;
    yzCoeff21Curve.assign(pXForm.yzCoeff21Curve);
    yzPostCoeff00 = pXForm.yzPostCoeff00;
    yzPostCoeff00Curve.assign(pXForm.yzPostCoeff00Curve);
    yzPostCoeff01 = pXForm.yzPostCoeff01;
    yzPostCoeff01Curve.assign(pXForm.yzPostCoeff01Curve);
    yzPostCoeff10 = pXForm.yzPostCoeff10;
    yzPostCoeff10Curve.assign(pXForm.yzPostCoeff10Curve);
    yzPostCoeff11 = pXForm.yzPostCoeff11;
    yzPostCoeff11Curve.assign(pXForm.yzPostCoeff11Curve);
    yzPostCoeff20 = pXForm.yzPostCoeff20;
    yzPostCoeff20Curve.assign(pXForm.yzPostCoeff20Curve);
    yzPostCoeff21 = pXForm.yzPostCoeff21;
    yzPostCoeff21Curve.assign(pXForm.yzPostCoeff21Curve);

    zxCoeff00 = pXForm.zxCoeff00;
    zxCoeff00Curve.assign(pXForm.zxCoeff00Curve);
    zxCoeff01 = pXForm.zxCoeff01;
    zxCoeff01Curve.assign(pXForm.zxCoeff01Curve);
    zxCoeff10 = pXForm.zxCoeff10;
    zxCoeff10Curve.assign(pXForm.zxCoeff10Curve);
    zxCoeff11 = pXForm.zxCoeff11;
    zxCoeff11Curve.assign(pXForm.zxCoeff11Curve);
    zxCoeff20 = pXForm.zxCoeff20;
    zxCoeff20Curve.assign(pXForm.zxCoeff20Curve);
    zxCoeff21 = pXForm.zxCoeff21;
    zxCoeff21Curve.assign(pXForm.zxCoeff21Curve);
    zxPostCoeff00 = pXForm.zxPostCoeff00;
    zxPostCoeff00Curve.assign(pXForm.zxPostCoeff00Curve);
    zxPostCoeff01 = pXForm.zxPostCoeff01;
    zxPostCoeff01Curve.assign(pXForm.zxPostCoeff01Curve);
    zxPostCoeff10 = pXForm.zxPostCoeff10;
    zxPostCoeff10Curve.assign(pXForm.zxPostCoeff10Curve);
    zxPostCoeff11 = pXForm.zxPostCoeff11;
    zxPostCoeff11Curve.assign(pXForm.zxPostCoeff11Curve);
    zxPostCoeff20 = pXForm.zxPostCoeff20;
    zxPostCoeff20Curve.assign(pXForm.zxPostCoeff20Curve);
    zxPostCoeff21 = pXForm.zxPostCoeff21;
    zxPostCoeff21Curve.assign(pXForm.zxPostCoeff21Curve);

    xyRotateCurve.assign(pXForm.xyRotateCurve);
    xyScaleCurve.assign(pXForm.xyScaleCurve);
    xyPostRotateCurve.assign(pXForm.xyPostRotateCurve);
    xyPostScaleCurve.assign(pXForm.xyPostScaleCurve);

    yzRotateCurve.assign(pXForm.yzRotateCurve);
    yzScaleCurve.assign(pXForm.yzScaleCurve);
    yzPostRotateCurve.assign(pXForm.yzPostRotateCurve);
    yzPostScaleCurve.assign(pXForm.yzPostScaleCurve);

    zxRotateCurve.assign(pXForm.zxRotateCurve);
    zxScaleCurve.assign(pXForm.zxScaleCurve);
    zxPostRotateCurve.assign(pXForm.zxPostRotateCurve);
    zxPostScaleCurve.assign(pXForm.zxPostScaleCurve);

    hasXYPostCoeffs = pXForm.hasXYPostCoeffs;
    hasXYCoeffs = pXForm.hasXYCoeffs;
    hasYZPostCoeffs = pXForm.hasYZPostCoeffs;
    hasYZCoeffs = pXForm.hasYZCoeffs;
    hasZXPostCoeffs = pXForm.hasZXPostCoeffs;
    hasZXCoeffs = pXForm.hasZXCoeffs;
    variations.clear();
    for (Variation var : pXForm.variations) {
      Variation newVar = new Variation();
      newVar.assign(var);
      variations.add(newVar);
    }
    System.arraycopy(pXForm.modifiedWeights, 0, modifiedWeights, 0, pXForm.modifiedWeights.length);
    opacity = pXForm.opacity;
    opacityCurve.assign(pXForm.opacityCurve);
    drawMode = pXForm.drawMode;
    colorType = pXForm.colorType;
    name = pXForm.name;

    weightingFieldType = pXForm.weightingFieldType;
    weightingFieldInput = pXForm.weightingFieldInput;
    weightingFieldColorIntensity = pXForm.weightingFieldColorIntensity;
    weightingFieldColorIntensityCurve.assign(pXForm.weightingFieldColorIntensityCurve);
    weightingFieldVarAmountIntensity = pXForm.weightingFieldVarAmountIntensity;
    weightingFieldVarAmountIntensityCurve.assign(pXForm.weightingFieldVarAmountIntensityCurve);

    weightingFieldVarParam1Intensity = pXForm.weightingFieldVarParam1Intensity;
    weightingFieldVarParam1VarName = pXForm.weightingFieldVarParam1VarName;
    weightingFieldVarParam1ParamName = pXForm.weightingFieldVarParam1ParamName;
    weightingFieldVarParam1IntensityCurve.assign(pXForm.weightingFieldVarParam1IntensityCurve);

    weightingFieldVarParam2Intensity = pXForm.weightingFieldVarParam2Intensity;
    weightingFieldVarParam2VarName = pXForm.weightingFieldVarParam2VarName;
    weightingFieldVarParam2ParamName = pXForm.weightingFieldVarParam2ParamName;
    weightingFieldVarParam2IntensityCurve.assign(pXForm.weightingFieldVarParam2IntensityCurve);

    weightingFieldVarParam3Intensity = pXForm.weightingFieldVarParam3Intensity;
    weightingFieldVarParam3VarName = pXForm.weightingFieldVarParam3VarName;
    weightingFieldVarParam3ParamName = pXForm.weightingFieldVarParam3ParamName;
    weightingFieldVarParam3IntensityCurve.assign(pXForm.weightingFieldVarParam3IntensityCurve);
    
    weightingFieldJitterIntensity = pXForm.weightingFieldJitterIntensity;
    weightingFieldJitterIntensityCurve.assign(pXForm.weightingFieldJitterIntensityCurve);    

    weightingFieldColorMapFilename = pXForm.weightingFieldColorMapFilename;
    weightingFieldColorMapXSize = pXForm.weightingFieldColorMapXSize;
    weightingFieldColorMapXSizeCurve.assign(pXForm.weightingFieldColorMapXSizeCurve);
    weightingFieldColorMapYSize = pXForm.weightingFieldColorMapYSize;
    weightingFieldColorMapYSizeCurve.assign(pXForm.weightingFieldColorMapYSizeCurve);
    weightingFieldColorMapXCentre = pXForm.weightingFieldColorMapXCentre;
    weightingFieldColorMapXCentreCurve.assign(pXForm.weightingFieldColorMapXCentreCurve);
    weightingFieldColorMapYCentre = pXForm.weightingFieldColorMapYCentre;
    weightingFieldColorMapYCentreCurve.assign(pXForm.weightingFieldColorMapYCentreCurve);
    weightingFieldNoiseSeed = pXForm.weightingFieldNoiseSeed;
    weightingFieldNoiseSeedCurve.assign(pXForm.weightingFieldNoiseSeedCurve);
    weightingFieldNoiseFrequency = pXForm.weightingFieldNoiseFrequency;
    weightingFieldNoiseFrequencyCurve.assign(pXForm.weightingFieldNoiseFrequencyCurve);
    weightingFieldFractalType = pXForm.weightingFieldFractalType;
    weightingFieldFractalNoiseOctaves = pXForm.weightingFieldFractalNoiseOctaves;
    weightingFieldFractalNoiseOctavesCurve.assign(pXForm.weightingFieldFractalNoiseOctavesCurve);
    weightingFieldFractalNoiseGain = pXForm.weightingFieldFractalNoiseGain;
    weightingFieldFractalNoiseGainCurve.assign(pXForm.weightingFieldFractalNoiseGainCurve);
    weightingFieldFractalNoiseLacunarity = pXForm.weightingFieldFractalNoiseLacunarity;
    weightingFieldFractalNoiseLacunarityCurve.assign(pXForm.weightingFieldFractalNoiseLacunarityCurve);
    weightingFieldCellularNoiseReturnType = pXForm.weightingFieldCellularNoiseReturnType;
    weightingFieldCellularNoiseDistanceFunction = pXForm.weightingFieldCellularNoiseDistanceFunction;
  }

  @Override
  public XForm makeCopy() {
    XForm res = new XForm();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(XForm pSrc) {
    if ((fabs(weight - pSrc.weight) > EPSILON) || !weightCurve.isEqual(pSrc.weightCurve) ||
        (fabs(color - pSrc.color) > EPSILON) || !colorCurve.isEqual(pSrc.colorCurve) ||
        (!targetColor.isEqual(pSrc.targetColor)) ||
        (fabs(colorSymmetry - pSrc.colorSymmetry) > EPSILON) || !colorSymmetryCurve.isEqual(pSrc.colorSymmetryCurve) ||
        (fabs(material - pSrc.material) > EPSILON) || !materialCurve.isEqual(pSrc.materialCurve) ||
        (fabs(materialSpeed - pSrc.materialSpeed) > EPSILON) || !materialSpeedCurve.isEqual(pSrc.materialSpeedCurve) ||
        (fabs(modGamma - pSrc.modGamma) > EPSILON) || (fabs(modGammaSpeed - pSrc.modGammaSpeed) > EPSILON) ||
        (fabs(modContrast - pSrc.modContrast) > EPSILON) || (fabs(modContrastSpeed - pSrc.modContrastSpeed) > EPSILON) ||
        (fabs(modSaturation - pSrc.modSaturation) > EPSILON) || (fabs(modSaturationSpeed - pSrc.modSaturationSpeed) > EPSILON) ||
        (fabs(modHue - pSrc.modHue) > EPSILON) || (fabs(modHueSpeed - pSrc.modHueSpeed) > EPSILON) ||

        (fabs(xyCoeff00 - pSrc.xyCoeff00) > EPSILON) || !xyCoeff00Curve.isEqual(pSrc.xyCoeff00Curve) ||
        (fabs(xyCoeff01 - pSrc.xyCoeff01) > EPSILON) || !xyCoeff01Curve.isEqual(pSrc.xyCoeff01Curve) ||
        (fabs(xyCoeff10 - pSrc.xyCoeff10) > EPSILON) || !xyCoeff10Curve.isEqual(pSrc.xyCoeff10Curve) ||
        (fabs(xyCoeff11 - pSrc.xyCoeff11) > EPSILON) || !xyCoeff11Curve.isEqual(pSrc.xyCoeff11Curve) ||
        (fabs(xyCoeff20 - pSrc.xyCoeff20) > EPSILON) || !xyCoeff20Curve.isEqual(pSrc.xyCoeff20Curve) ||
        (fabs(xyCoeff21 - pSrc.xyCoeff21) > EPSILON) || !xyCoeff21Curve.isEqual(pSrc.xyCoeff21Curve) ||
        (fabs(xyPostCoeff00 - pSrc.xyPostCoeff00) > EPSILON) || !xyPostCoeff00Curve.isEqual(pSrc.xyPostCoeff00Curve) ||
        (fabs(xyPostCoeff01 - pSrc.xyPostCoeff01) > EPSILON) || !xyPostCoeff01Curve.isEqual(pSrc.xyPostCoeff01Curve) ||
        (fabs(xyPostCoeff10 - pSrc.xyPostCoeff10) > EPSILON) || !xyPostCoeff10Curve.isEqual(pSrc.xyPostCoeff10Curve) ||
        (fabs(xyPostCoeff11 - pSrc.xyPostCoeff11) > EPSILON) || !xyPostCoeff11Curve.isEqual(pSrc.xyPostCoeff11Curve) ||
        (fabs(xyPostCoeff20 - pSrc.xyPostCoeff20) > EPSILON) || !xyPostCoeff20Curve.isEqual(pSrc.xyPostCoeff20Curve) ||
        (fabs(xyPostCoeff21 - pSrc.xyPostCoeff21) > EPSILON) || !xyPostCoeff21Curve.isEqual(pSrc.xyPostCoeff21Curve) ||

        (fabs(yzCoeff00 - pSrc.yzCoeff00) > EPSILON) || !yzCoeff00Curve.isEqual(pSrc.yzCoeff00Curve) ||
        (fabs(yzCoeff01 - pSrc.yzCoeff01) > EPSILON) || !yzCoeff01Curve.isEqual(pSrc.yzCoeff01Curve) ||
        (fabs(yzCoeff10 - pSrc.yzCoeff10) > EPSILON) || !yzCoeff10Curve.isEqual(pSrc.yzCoeff10Curve) ||
        (fabs(yzCoeff11 - pSrc.yzCoeff11) > EPSILON) || !yzCoeff11Curve.isEqual(pSrc.yzCoeff11Curve) ||
        (fabs(yzCoeff20 - pSrc.yzCoeff20) > EPSILON) || !yzCoeff20Curve.isEqual(pSrc.yzCoeff20Curve) ||
        (fabs(yzCoeff21 - pSrc.yzCoeff21) > EPSILON) || !yzCoeff21Curve.isEqual(pSrc.yzCoeff21Curve) ||
        (fabs(yzPostCoeff00 - pSrc.yzPostCoeff00) > EPSILON) || !yzPostCoeff00Curve.isEqual(pSrc.yzPostCoeff00Curve) ||
        (fabs(yzPostCoeff01 - pSrc.yzPostCoeff01) > EPSILON) || !yzPostCoeff01Curve.isEqual(pSrc.yzPostCoeff01Curve) ||
        (fabs(yzPostCoeff10 - pSrc.yzPostCoeff10) > EPSILON) || !yzPostCoeff10Curve.isEqual(pSrc.yzPostCoeff10Curve) ||
        (fabs(yzPostCoeff11 - pSrc.yzPostCoeff11) > EPSILON) || !yzPostCoeff11Curve.isEqual(pSrc.yzPostCoeff11Curve) ||
        (fabs(yzPostCoeff20 - pSrc.yzPostCoeff20) > EPSILON) || !yzPostCoeff20Curve.isEqual(pSrc.yzPostCoeff20Curve) ||
        (fabs(yzPostCoeff21 - pSrc.yzPostCoeff21) > EPSILON) || !yzPostCoeff21Curve.isEqual(pSrc.yzPostCoeff21Curve) ||

        (fabs(zxCoeff00 - pSrc.zxCoeff00) > EPSILON) || !zxCoeff00Curve.isEqual(pSrc.zxCoeff00Curve) ||
        (fabs(zxCoeff01 - pSrc.zxCoeff01) > EPSILON) || !zxCoeff01Curve.isEqual(pSrc.zxCoeff01Curve) ||
        (fabs(zxCoeff10 - pSrc.zxCoeff10) > EPSILON) || !zxCoeff10Curve.isEqual(pSrc.zxCoeff10Curve) ||
        (fabs(zxCoeff11 - pSrc.zxCoeff11) > EPSILON) || !zxCoeff11Curve.isEqual(pSrc.zxCoeff11Curve) ||
        (fabs(zxCoeff20 - pSrc.zxCoeff20) > EPSILON) || !zxCoeff20Curve.isEqual(pSrc.zxCoeff20Curve) ||
        (fabs(zxCoeff21 - pSrc.zxCoeff21) > EPSILON) || !zxCoeff21Curve.isEqual(pSrc.zxCoeff21Curve) ||
        (fabs(zxPostCoeff00 - pSrc.zxPostCoeff00) > EPSILON) || !zxPostCoeff00Curve.isEqual(pSrc.zxPostCoeff00Curve) ||
        (fabs(zxPostCoeff01 - pSrc.zxPostCoeff01) > EPSILON) || !zxPostCoeff01Curve.isEqual(pSrc.zxPostCoeff01Curve) ||
        (fabs(zxPostCoeff10 - pSrc.zxPostCoeff10) > EPSILON) || !zxPostCoeff10Curve.isEqual(pSrc.zxPostCoeff10Curve) ||
        (fabs(zxPostCoeff11 - pSrc.zxPostCoeff11) > EPSILON) || !zxPostCoeff11Curve.isEqual(pSrc.zxPostCoeff11Curve) ||
        (fabs(zxPostCoeff20 - pSrc.zxPostCoeff20) > EPSILON) || !zxPostCoeff20Curve.isEqual(pSrc.zxPostCoeff20Curve) ||
        (fabs(zxPostCoeff21 - pSrc.zxPostCoeff21) > EPSILON) || !zxPostCoeff21Curve.isEqual(pSrc.zxPostCoeff21Curve) ||

        !xyRotateCurve.isEqual(pSrc.xyRotateCurve) || !xyScaleCurve.isEqual(pSrc.xyScaleCurve) ||
        !xyPostRotateCurve.isEqual(pSrc.xyPostRotateCurve) || !xyPostScaleCurve.isEqual(pSrc.xyPostScaleCurve) ||
        !yzRotateCurve.isEqual(pSrc.yzRotateCurve) || !yzScaleCurve.isEqual(pSrc.yzScaleCurve) ||
        !yzPostRotateCurve.isEqual(pSrc.yzPostRotateCurve) || !yzPostScaleCurve.isEqual(pSrc.yzPostScaleCurve) ||
        !zxRotateCurve.isEqual(pSrc.zxRotateCurve) || !zxScaleCurve.isEqual(pSrc.zxScaleCurve) ||
        !zxPostRotateCurve.isEqual(pSrc.zxPostRotateCurve) || !zxPostScaleCurve.isEqual(pSrc.zxPostScaleCurve) ||

        (fabs(opacity - pSrc.opacity) > EPSILON) || !opacityCurve.isEqual(pSrc.opacityCurve) ||
        ((drawMode != null && pSrc.drawMode == null) || (drawMode == null && pSrc.drawMode != null) ||
        (drawMode != null && pSrc.drawMode != null && !drawMode.equals(pSrc.drawMode))) ||
        ((colorType != null && pSrc.colorType == null) || (colorType == null && pSrc.colorType != null) ||
        (colorType != null && pSrc.colorType != null && !colorType.equals(pSrc.colorType))) ||
        !name.equals(pSrc.name) ||
        (modifiedWeights.length != pSrc.modifiedWeights.length) || (variations.size() != pSrc.variations.size()) ||

         weightingFieldType != pSrc.weightingFieldType || weightingFieldInput != pSrc.weightingFieldInput ||
         (fabs(weightingFieldColorIntensity - pSrc.weightingFieldColorIntensity) > EPSILON) || !weightingFieldColorIntensityCurve.isEqual(pSrc.weightingFieldColorIntensityCurve) ||
         (fabs(weightingFieldVarAmountIntensity - pSrc.weightingFieldVarAmountIntensity) > EPSILON) || !weightingFieldVarAmountIntensityCurve.isEqual(pSrc.weightingFieldVarAmountIntensityCurve) ||

         (fabs(weightingFieldVarParam1Intensity - pSrc.weightingFieldVarParam1Intensity) > EPSILON) || !weightingFieldVarParam1VarName.equals(pSrc.weightingFieldVarParam1VarName) ||
         !weightingFieldVarParam1ParamName.equals(pSrc.weightingFieldVarParam1ParamName) || !weightingFieldVarParam1IntensityCurve.isEqual(pSrc.weightingFieldVarParam1IntensityCurve) ||
         (fabs(weightingFieldVarParam2Intensity - pSrc.weightingFieldVarParam2Intensity) > EPSILON) || !weightingFieldVarParam2VarName.equals(pSrc.weightingFieldVarParam2VarName) ||
         !weightingFieldVarParam2ParamName.equals(pSrc.weightingFieldVarParam2ParamName) || !weightingFieldVarParam2IntensityCurve.isEqual(pSrc.weightingFieldVarParam2IntensityCurve) ||
         (fabs(weightingFieldVarParam3Intensity - pSrc.weightingFieldVarParam3Intensity) > EPSILON) || !weightingFieldVarParam3VarName.equals(pSrc.weightingFieldVarParam3VarName) ||
         !weightingFieldVarParam3ParamName.equals(pSrc.weightingFieldVarParam3ParamName) || !weightingFieldVarParam3IntensityCurve.isEqual(pSrc.weightingFieldVarParam3IntensityCurve) ||

         (fabs(weightingFieldJitterIntensity - pSrc.weightingFieldJitterIntensity) > EPSILON) || !weightingFieldJitterIntensityCurve.isEqual(pSrc.weightingFieldJitterIntensityCurve) ||

         !weightingFieldColorMapFilename.equals(pSrc.weightingFieldColorMapFilename) ||
         (fabs(weightingFieldColorMapXSize - pSrc.weightingFieldColorMapXSize) > EPSILON) || !weightingFieldColorMapXSizeCurve.isEqual(pSrc.weightingFieldColorMapXSizeCurve) ||
         (fabs(weightingFieldColorMapYSize - pSrc.weightingFieldColorMapYSize) > EPSILON) || !weightingFieldColorMapYSizeCurve.isEqual(pSrc.weightingFieldColorMapYSizeCurve) ||
         (fabs(weightingFieldColorMapXCentre - pSrc.weightingFieldColorMapXCentre) > EPSILON) || !weightingFieldColorMapXCentreCurve.isEqual(pSrc.weightingFieldColorMapXCentreCurve) ||
         (fabs(weightingFieldColorMapYCentre - pSrc.weightingFieldColorMapYCentre) > EPSILON) || !weightingFieldColorMapYCentreCurve.isEqual(pSrc.weightingFieldColorMapYCentreCurve) ||

         (weightingFieldNoiseSeed != pSrc.weightingFieldNoiseSeed) || !weightingFieldNoiseSeedCurve.isEqual(pSrc.weightingFieldNoiseSeedCurve) ||
         (fabs(weightingFieldNoiseFrequency - pSrc.weightingFieldNoiseFrequency) > EPSILON) || !weightingFieldNoiseFrequencyCurve.isEqual(pSrc.weightingFieldNoiseFrequencyCurve) ||
         (weightingFieldFractalType != pSrc.weightingFieldFractalType) ||
         (weightingFieldFractalNoiseOctaves != pSrc.weightingFieldFractalNoiseOctaves) || !weightingFieldFractalNoiseOctavesCurve.isEqual(pSrc.weightingFieldFractalNoiseOctavesCurve) ||
         (fabs(weightingFieldFractalNoiseGain - pSrc.weightingFieldFractalNoiseGain) > EPSILON) || !weightingFieldFractalNoiseGainCurve.isEqual(pSrc.weightingFieldFractalNoiseGainCurve) ||
         (fabs(weightingFieldFractalNoiseLacunarity - pSrc.weightingFieldFractalNoiseLacunarity) > EPSILON) || !weightingFieldFractalNoiseLacunarityCurve.isEqual(pSrc.weightingFieldFractalNoiseLacunarityCurve) ||
         (weightingFieldCellularNoiseReturnType != pSrc.weightingFieldCellularNoiseReturnType) ||
         (weightingFieldCellularNoiseDistanceFunction != pSrc.weightingFieldCellularNoiseDistanceFunction)
    ) {
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

  public MotionCurve getXYRotateCurve() {
    return xyRotateCurve;
  }

  public MotionCurve getXYScaleCurve() {
    return xyScaleCurve;
  }

  public MotionCurve getXYPostRotateCurve() {
    return xyPostRotateCurve;
  }

  public MotionCurve getXYPostScaleCurve() {
    return xyPostScaleCurve;
  }


  public MotionCurve getYZRotateCurve() {
    return yzRotateCurve;
  }

  public MotionCurve getYZScaleCurve() {
    return yzScaleCurve;
  }

  public MotionCurve getYZPostRotateCurve() {
    return yzPostRotateCurve;
  }

  public MotionCurve getYZPostScaleCurve() {
    return yzPostScaleCurve;
  }

  public MotionCurve getZXRotateCurve() {
    return zxRotateCurve;
  }

  public MotionCurve getZXScaleCurve() {
    return zxScaleCurve;
  }

  public MotionCurve getZXPostRotateCurve() {
    return zxPostRotateCurve;
  }

  public MotionCurve getZXPostScaleCurve() {
    return zxPostScaleCurve;
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
    randomizeModGamma();
    randomizeModContrast();
    randomizeModSaturation();
    randomizeModHue();
  }

  public void randomizeModGamma() {
    modGamma = 1.0 - 2.0 * Math.random();
    modGammaSpeed = Math.random() < 0.33 ? 1.0 - 2.0 * Math.random() : 0;
  }

  public void randomizeModContrast() {
    modContrast = 1.0 - 2.0 * Math.random();
    modContrastSpeed = Math.random() < 0.33 ? 1.0 - 2.0 * Math.random() : 0;
  }

  public void randomizeModSaturation() {
    modSaturation = 1.0 - 2.0 * Math.random();
    modSaturationSpeed = Math.random() < 0.33 ? 1.0 - 2.0 * Math.random() : 0;
  }

  public void randomizeModHue() {
    modHue = 1.0 - 2.0 * Math.random();
    modHueSpeed = Math.random() < 0.33 ? 1.0 - 2.0 * Math.random() : 0;
  }

  public void resetModColorEffects() {
    modGamma = 0.0;
    modGammaSpeed = 0.0;
    modContrast = 0.0;
    modContrastSpeed = 0.0;
    modSaturation = 0.0;
    modSaturationSpeed = 0.0;
    modHue = 0.0;
    modHueSpeed = 0.0;
  }

  public double getXYCoeff00() {
    return xyCoeff00;
  }

  public void setXYCoeff00(double xyCoeff00) {
    this.xyCoeff00 = xyCoeff00;
    updateHasXYCoeffs();
  }

  public double getXYCoeff01() {
    return xyCoeff01;
  }

  public void setXYCoeff01(double xyCoeff01) {
    this.xyCoeff01 = xyCoeff01;
    updateHasXYCoeffs();
  }

  public double getXYCoeff10() {
    return xyCoeff10;
  }

  public void setXYCoeff10(double xyCoeff10) {
    this.xyCoeff10 = xyCoeff10;
    updateHasXYCoeffs();
  }

  public double getXYCoeff11() {
    return xyCoeff11;
  }

  public void setXYCoeff11(double xyCoeff11) {
    this.xyCoeff11 = xyCoeff11;
    updateHasXYCoeffs();
  }

  public double getXYCoeff20() {
    return xyCoeff20;
  }

  public void setXYCoeff20(double val) {
    this.xyCoeff20 = val;
    updateHasXYCoeffs();
  }

  public double getXYCoeff21() {
    return xyCoeff21;
  }

  public void setXYCoeff21(double val) {
    this.xyCoeff21 = val;
    updateHasXYCoeffs();
  }

  public double getXYPostCoeff00() {
    return xyPostCoeff00;
  }

  public void setXYPostCoeff00(double xyPostCoeff00) {
    this.xyPostCoeff00 = xyPostCoeff00;
    updateHasXYPostCoeffs();
  }

  public double getXYPostCoeff01() {
    return xyPostCoeff01;
  }

  public void setXYPostCoeff01(double xyPostCoeff01) {
    this.xyPostCoeff01 = xyPostCoeff01;
    updateHasXYPostCoeffs();
  }

  public double getXYPostCoeff10() {
    return xyPostCoeff10;
  }

  public void setXYPostCoeff10(double xyPostCoeff10) {
    this.xyPostCoeff10 = xyPostCoeff10;
    updateHasXYPostCoeffs();
  }

  public double getXYPostCoeff11() {
    return xyPostCoeff11;
  }

  public void setXYPostCoeff11(double xyPostCoeff11) {
    this.xyPostCoeff11 = xyPostCoeff11;
    updateHasXYPostCoeffs();
  }

  public double getXYPostCoeff20() {
    return xyPostCoeff20;
  }

  public void setXYPostCoeff20(double val) {
    this.xyPostCoeff20 = val;
    updateHasXYPostCoeffs();
  }

  public double getXYPostCoeff21() {
    return xyPostCoeff21;
  }

  public void setXYPostCoeff21(double val) {
    this.xyPostCoeff21 = val;
    updateHasXYPostCoeffs();
  }

  public double getYZCoeff00() {
    return yzCoeff00;
  }

  public void setYZCoeff00(double yzCoeff00) {
    this.yzCoeff00 = yzCoeff00;
    updateHasYZCoeffs();
  }

  public double getYZCoeff01() {
    return yzCoeff01;
  }

  public void setYZCoeff01(double yzCoeff01) {
    this.yzCoeff01 = yzCoeff01;
    updateHasYZCoeffs();
  }

  public double getYZCoeff10() {
    return yzCoeff10;
  }

  public void setYZCoeff10(double yzCoeff10) {
    this.yzCoeff10 = yzCoeff10;
    updateHasYZCoeffs();
  }

  public double getYZCoeff11() {
    return yzCoeff11;
  }

  public void setYZCoeff11(double yzCoeff11) {
    this.yzCoeff11 = yzCoeff11;
    updateHasYZCoeffs();
  }

  public double getYZCoeff20() {
    return yzCoeff20;
  }

  public void setYZCoeff20(double val) {
    this.yzCoeff20 = val;
    updateHasYZCoeffs();
  }

  public double getYZCoeff21() {
    return yzCoeff21;
  }

  public void setYZCoeff21(double val) {
    this.yzCoeff21 = val;
    updateHasYZCoeffs();
  }

  public double getYZPostCoeff00() {
    return yzPostCoeff00;
  }

  public void setYZPostCoeff00(double yzPostCoeff00) {
    this.yzPostCoeff00 = yzPostCoeff00;
    updateHasYZPostCoeffs();
  }

  public double getYZPostCoeff01() {
    return yzPostCoeff01;
  }

  public void setYZPostCoeff01(double yzPostCoeff01) {
    this.yzPostCoeff01 = yzPostCoeff01;
    updateHasYZPostCoeffs();
  }

  public double getYZPostCoeff10() {
    return yzPostCoeff10;
  }

  public void setYZPostCoeff10(double yzPostCoeff10) {
    this.yzPostCoeff10 = yzPostCoeff10;
    updateHasYZPostCoeffs();
  }

  public double getYZPostCoeff11() {
    return yzPostCoeff11;
  }

  public void setYZPostCoeff11(double yzPostCoeff11) {
    this.yzPostCoeff11 = yzPostCoeff11;
    updateHasYZPostCoeffs();
  }

  public double getYZPostCoeff20() {
    return yzPostCoeff20;
  }

  public void setYZPostCoeff20(double val) {
    this.yzPostCoeff20 = val;
    updateHasYZPostCoeffs();
  }

  public double getYZPostCoeff21() {
    return yzPostCoeff21;
  }

  public void setYZPostCoeff21(double val) {
    this.yzPostCoeff21 = val;
    updateHasYZPostCoeffs();
  }

  public double getZXCoeff00() {
    return zxCoeff00;
  }

  public void setZXCoeff00(double zxCoeff00) {
    this.zxCoeff00 = zxCoeff00;
    updateHasZXCoeffs();
  }

  public double getZXCoeff01() {
    return zxCoeff01;
  }

  public void setZXCoeff01(double zxCoeff01) {
    this.zxCoeff01 = zxCoeff01;
    updateHasZXCoeffs();
  }

  public double getZXCoeff10() {
    return zxCoeff10;
  }

  public void setZXCoeff10(double zxCoeff10) {
    this.zxCoeff10 = zxCoeff10;
    updateHasZXCoeffs();
  }

  public double getZXCoeff11() {
    return zxCoeff11;
  }

  public void setZXCoeff11(double zxCoeff11) {
    this.zxCoeff11 = zxCoeff11;
    updateHasZXCoeffs();
  }

  public double getZXCoeff20() {
    return zxCoeff20;
  }

  public void setZXCoeff20(double val) {
    this.zxCoeff20 = val;
    updateHasZXCoeffs();
  }

  public double getZXCoeff21() {
    return zxCoeff21;
  }

  public void setZXCoeff21(double val) {
    this.zxCoeff21 = val;
    updateHasZXCoeffs();
  }

  public double getZXPostCoeff00() {
    return zxPostCoeff00;
  }

  public void setZXPostCoeff00(double zxPostCoeff00) {
    this.zxPostCoeff00 = zxPostCoeff00;
    updateHasZXPostCoeffs();
  }

  public double getZXPostCoeff01() {
    return zxPostCoeff01;
  }

  public void setZXPostCoeff01(double zxPostCoeff01) {
    this.zxPostCoeff01 = zxPostCoeff01;
    updateHasZXPostCoeffs();
  }

  public double getZXPostCoeff10() {
    return zxPostCoeff10;
  }

  public void setZXPostCoeff10(double zxPostCoeff10) {
    this.zxPostCoeff10 = zxPostCoeff10;
    updateHasZXPostCoeffs();
  }

  public double getZXPostCoeff11() {
    return zxPostCoeff11;
  }

  public void setZXPostCoeff11(double zxPostCoeff11) {
    this.zxPostCoeff11 = zxPostCoeff11;
    updateHasZXPostCoeffs();
  }

  public double getZXPostCoeff20() {
    return zxPostCoeff20;
  }

  public void setZXPostCoeff20(double val) {
    this.zxPostCoeff20 = val;
    updateHasZXPostCoeffs();
  }

  public double getZXPostCoeff21() {
    return zxPostCoeff21;
  }

  public void setZXPostCoeff21(double val) {
    this.zxPostCoeff21 = val;
    updateHasZXPostCoeffs();
  }

  public EditPlane getEditPlane() {
    if (owner == null || owner.getOwner() == null) {
      System.out.println("EditPlane NULL");
      return EditPlane.XY;
    }
    else {
      return owner.getOwner().getEditPlane();
    }
  }

  public double getCoeff00() {
    switch (getEditPlane()) {
      case XY:
        return xyCoeff00;
      case YZ:
        return yzCoeff00;
      default:
        return zxCoeff00;
    }
  }

  public double getCoeff01() {
    switch (getEditPlane()) {
      case XY:
        return xyCoeff01;
      case YZ:
        return yzCoeff01;
      default:
        return zxCoeff01;
    }
  }

  public double getCoeff10() {
    switch (getEditPlane()) {
      case XY:
        return xyCoeff10;
      case YZ:
        return yzCoeff10;
      default:
        return zxCoeff10;
    }
  }

  public double getCoeff11() {
    switch (getEditPlane()) {
      case XY:
        return xyCoeff11;
      case YZ:
        return yzCoeff11;
      default:
        return zxCoeff11;
    }
  }

  public double getCoeff20() {
    switch (getEditPlane()) {
      case XY:
        return xyCoeff20;
      case YZ:
        return yzCoeff20;
      default:
        return zxCoeff20;
    }
  }

  public double getCoeff21() {
    switch (getEditPlane()) {
      case XY:
        return xyCoeff21;
      case YZ:
        return yzCoeff21;
      default:
        return zxCoeff21;
    }
  }

  public double getPostCoeff00() {
    switch (getEditPlane()) {
      case XY:
        return xyPostCoeff00;
      case YZ:
        return yzPostCoeff00;
      default:
        return zxPostCoeff00;
    }
  }

  public double getPostCoeff01() {
    switch (getEditPlane()) {
      case XY:
        return xyPostCoeff01;
      case YZ:
        return yzPostCoeff01;
      default:
        return zxPostCoeff01;
    }
  }

  public double getPostCoeff10() {
    switch (getEditPlane()) {
      case XY:
        return xyPostCoeff10;
      case YZ:
        return yzPostCoeff10;
      default:
        return zxPostCoeff10;
    }
  }

  public double getPostCoeff11() {
    switch (getEditPlane()) {
      case XY:
        return xyPostCoeff11;
      case YZ:
        return yzPostCoeff11;
      default:
        return zxPostCoeff11;
    }
  }

  public double getPostCoeff20() {
    switch (getEditPlane()) {
      case XY:
        return xyPostCoeff20;
      case YZ:
        return yzPostCoeff20;
      default:
        return zxPostCoeff20;
    }
  }

  public double getPostCoeff21() {
    switch (getEditPlane()) {
      case XY:
        return xyPostCoeff21;
      case YZ:
        return yzPostCoeff21;
      default:
        return zxPostCoeff21;
    }
  }

  public void setCoeff00(double coeff00) {
    switch (getEditPlane()) {
      case XY:
        setXYCoeff00(coeff00);
        break;
      case YZ:
        setYZCoeff00(coeff00);
        break;
      default:
        setZXCoeff00(coeff00);
        break;
    }
  }

  public void setCoeff01(double coeff01) {
    switch (getEditPlane()) {
      case XY:
        setXYCoeff01(coeff01);
        break;
      case YZ:
        setYZCoeff01(coeff01);
        break;
      default:
        setZXCoeff01(coeff01);
        break;
    }
  }

  public void setCoeff10(double coeff10) {
    switch (getEditPlane()) {
      case XY:
        setXYCoeff10(coeff10);
        break;
      case YZ:
        setYZCoeff10(coeff10);
        break;
      default:
        setZXCoeff10(coeff10);
        break;
    }
  }

  public void setCoeff11(double coeff11) {
    switch (getEditPlane()) {
      case XY:
        setXYCoeff11(coeff11);
        break;
      case YZ:
        setYZCoeff11(coeff11);
        break;
      default:
        setZXCoeff11(coeff11);
        break;
    }
  }

  public void setCoeff20(double coeff20) {
    switch (getEditPlane()) {
      case XY:
        setXYCoeff20(coeff20);
        break;
      case YZ:
        setYZCoeff20(coeff20);
        break;
      default:
        setZXCoeff20(coeff20);
        break;
    }
  }

  public void setCoeff21(double coeff21) {
    switch (getEditPlane()) {
      case XY:
        setXYCoeff21(coeff21);
        break;
      case YZ:
        setYZCoeff21(coeff21);
        break;
      default:
        setZXCoeff21(coeff21);
        break;
    }
  }

  public void setPostCoeff00(double postCoeff00) {
    switch (getEditPlane()) {
      case XY:
        setXYPostCoeff00(postCoeff00);
        break;
      case YZ:
        setYZPostCoeff00(postCoeff00);
        break;
      default:
        setZXPostCoeff00(postCoeff00);
        break;
    }
  }

  public void setPostCoeff01(double postCoeff01) {
    switch (getEditPlane()) {
      case XY:
        setXYPostCoeff01(postCoeff01);
        break;
      case YZ:
        setYZPostCoeff01(postCoeff01);
        break;
      default:
        setZXPostCoeff01(postCoeff01);
        break;
    }
  }

  public void setPostCoeff10(double postCoeff10) {
    switch (getEditPlane()) {
      case XY:
        setXYPostCoeff10(postCoeff10);
        break;
      case YZ:
        setYZPostCoeff10(postCoeff10);
        break;
      default:
        setZXPostCoeff10(postCoeff10);
        break;
    }
  }

  public void setPostCoeff11(double postCoeff11) {
    switch (getEditPlane()) {
      case XY:
        setXYPostCoeff11(postCoeff11);
        break;
      case YZ:
        setYZPostCoeff11(postCoeff11);
        break;
      default:
        setZXPostCoeff11(postCoeff11);
        break;
    }
  }

  public void setPostCoeff20(double postCoeff20) {
    switch (getEditPlane()) {
      case XY:
        setXYPostCoeff20(postCoeff20);
        break;
      case YZ:
        setYZPostCoeff20(postCoeff20);
        break;
      default:
        setZXPostCoeff20(postCoeff20);
        break;
    }
  }

  public void setPostCoeff21(double postCoeff21) {
    switch (getEditPlane()) {
      case XY:
        setXYPostCoeff21(postCoeff21);
        break;
      case YZ:
        setYZPostCoeff21(postCoeff21);
        break;
      default:
        setZXPostCoeff21(postCoeff21);
        break;
    }
  }
  
  public Layer getOwner() {
    return owner;
  }

  public void setOwner(Layer owner) {
    this.owner = owner;
  }

  public MotionCurve getXYCoeff00Curve() {
    return xyCoeff00Curve;
  }

  public MotionCurve getYZCoeff00Curve() {
    return yzCoeff00Curve;
  }

  public MotionCurve getZXCoeff00Curve() {
    return zxCoeff00Curve;
  }

  public MotionCurve getXYCoeff01Curve() {
    return xyCoeff01Curve;
  }

  public MotionCurve getYZCoeff01Curve() {
    return yzCoeff01Curve;
  }

  public MotionCurve getZXCoeff01Curve() {
    return zxCoeff01Curve;
  }

  public MotionCurve getXYCoeff10Curve() {
    return xyCoeff10Curve;
  }

  public MotionCurve getYZCoeff10Curve() {
    return yzCoeff10Curve;
  }

  public MotionCurve getZXCoeff10Curve() {
    return zxCoeff10Curve;
  }

  public MotionCurve getXYCoeff11Curve() {
    return xyCoeff11Curve;
  }

  public MotionCurve getYZCoeff11Curve() {
    return yzCoeff11Curve;
  }

  public MotionCurve getZXCoeff11Curve() {
    return zxCoeff11Curve;
  }

  public MotionCurve getXYCoeff20Curve() {
    return xyCoeff20Curve;
  }

  public MotionCurve getYZCoeff20Curve() {
    return yzCoeff20Curve;
  }

  public MotionCurve getZXCoeff20Curve() {
    return zxCoeff20Curve;
  }


  public MotionCurve getXYCoeff21Curve() {
    return xyCoeff21Curve;
  }

  public MotionCurve getYZCoeff21Curve() {
    return yzCoeff21Curve;
  }

  public MotionCurve getZXCoeff21Curve() {
    return zxCoeff21Curve;
  }

  public MotionCurve getXYPostCoeff00Curve() {
    return xyPostCoeff00Curve;
  }

  public MotionCurve getYZPostCoeff00Curve() {
    return yzPostCoeff00Curve;
  }

  public MotionCurve getZXPostCoeff00Curve() {
    return zxPostCoeff00Curve;
  }

  public MotionCurve getXYPostCoeff01Curve() {
    return xyPostCoeff01Curve;
  }

  public MotionCurve getYZPostCoeff01Curve() {
    return yzPostCoeff01Curve;
  }

  public MotionCurve getZXPostCoeff01Curve() {
    return zxPostCoeff01Curve;
  }

  public MotionCurve getXYPostCoeff10Curve() {
    return xyPostCoeff10Curve;
  }

  public MotionCurve getYZPostCoeff10Curve() {
    return yzPostCoeff10Curve;
  }

  public MotionCurve getZXPostCoeff10Curve() {
    return zxPostCoeff10Curve;
  }

  public MotionCurve getXYPostCoeff11Curve() {
    return xyPostCoeff11Curve;
  }

  public MotionCurve getYZPostCoeff11Curve() {
    return yzPostCoeff11Curve;
  }

  public MotionCurve getZXPostCoeff11Curve() {
    return zxPostCoeff11Curve;
  }

  public MotionCurve getXYPostCoeff20Curve() {
    return xyPostCoeff20Curve;
  }

  public MotionCurve getYZPostCoeff20Curve() {
    return yzPostCoeff20Curve;
  }

  public MotionCurve getZXPostCoeff20Curve() {
    return zxPostCoeff20Curve;
  }

  public MotionCurve getXYPostCoeff21Curve() {
    return xyPostCoeff21Curve;
  }

  public MotionCurve getYZPostCoeff21Curve() {
    return yzPostCoeff21Curve;
  }

  public MotionCurve getZXPostCoeff21Curve() {
    return zxPostCoeff21Curve;
  }

  public MotionCurve getWeightCurve() {
    return weightCurve;
  }

  public MotionCurve getColorCurve() {
    return colorCurve;
  }

  public double getModHue() {
    return modHue;
  }

  public void setModHue(double modHue) {
    this.modHue = modHue;
  }

  public double getModHueSpeed() {
    return modHueSpeed;
  }

  public void setModHueSpeed(double modHueSpeed) {
    this.modHueSpeed = modHueSpeed;
  }

  public double getMaterial() {
    return material;
  }

  public void setMaterial(double material) {
    this.material = material;
  }

  public double getMaterialSpeed() {
    return materialSpeed;
  }

  public void setMaterialSpeed(double materialSpeed) {
    this.materialSpeed = materialSpeed;
  }

  public WeightingFieldType getWeightingFieldType() {
    return weightingFieldType;
  }

  public void setWeightingFieldType(WeightingFieldType weightingFieldType) {
    this.weightingFieldType = weightingFieldType;
  }

  public WeightingFieldInputType getWeightingFieldInput() {
    return weightingFieldInput;
  }

  public void setWeightingFieldInput(WeightingFieldInputType weightingFieldInput) {
    this.weightingFieldInput = weightingFieldInput;
  }

  public double getWeightingFieldColorIntensity() {
    return weightingFieldColorIntensity;
  }

  public void setWeightingFieldColorIntensity(double weightingFieldColorIntensity) {
    this.weightingFieldColorIntensity = weightingFieldColorIntensity;
  }

  public MotionCurve getWeightingFieldColorIntensityCurve() {
    return weightingFieldColorIntensityCurve;
  }

  public double getWeightingFieldVarAmountIntensity() {
    return weightingFieldVarAmountIntensity;
  }

  public void setWeightingFieldVarAmountIntensity(double weightingFieldVarAmountIntensity) {
    this.weightingFieldVarAmountIntensity = weightingFieldVarAmountIntensity;
  }

  public MotionCurve getWeightingFieldVarAmountIntensityCurve() {
    return weightingFieldVarAmountIntensityCurve;
  }

  public double getWeightingFieldJitterIntensity() {
    return weightingFieldJitterIntensity;
  }

  public void setWeightingFieldJitterIntensity(double weightingFieldJitterIntensity) {
    this.weightingFieldJitterIntensity = weightingFieldJitterIntensity;
  }

  public MotionCurve getWeightingFieldJitterIntensityCurve() {
    return weightingFieldJitterIntensityCurve;
  }

  public String getWeightingFieldColorMapFilename() {
    return weightingFieldColorMapFilename;
  }

  public void setWeightingFieldColorMapFilename(String weightingFieldColorMapFilename) {
    this.weightingFieldColorMapFilename = weightingFieldColorMapFilename;
  }

  public double getWeightingFieldColorMapXSize() {
    return weightingFieldColorMapXSize;
  }

  public void setWeightingFieldColorMapXSize(double weightingFieldColorMapXSize) {
    this.weightingFieldColorMapXSize = weightingFieldColorMapXSize;
  }

  public double getWeightingFieldColorMapYSize() {
    return weightingFieldColorMapYSize;
  }

  public void setWeightingFieldColorMapYSize(double weightingFieldColorMapYSize) {
    this.weightingFieldColorMapYSize = weightingFieldColorMapYSize;
  }

  public double getWeightingFieldColorMapXCentre() {
    return weightingFieldColorMapXCentre;
  }

  public void setWeightingFieldColorMapXCentre(double weightingFieldColorMapXCentre) {
    this.weightingFieldColorMapXCentre = weightingFieldColorMapXCentre;
  }

  public double getWeightingFieldColorMapYCentre() {
    return weightingFieldColorMapYCentre;
  }

  public void setWeightingFieldColorMapYCentre(double weightingFieldColorMapYCentre) {
    this.weightingFieldColorMapYCentre = weightingFieldColorMapYCentre;
  }

  public int getWeightingFieldNoiseSeed() {
    return weightingFieldNoiseSeed;
  }

  public void setWeightingFieldNoiseSeed(int weightingFieldNoiseSeed) {
    this.weightingFieldNoiseSeed = weightingFieldNoiseSeed;
  }

  public double getWeightingFieldNoiseFrequency() {
    return weightingFieldNoiseFrequency;
  }

  public void setWeightingFieldNoiseFrequency(double weightingFieldNoiseFrequency) {
    this.weightingFieldNoiseFrequency = weightingFieldNoiseFrequency;
  }

  public int getWeightingFieldFractalNoiseOctaves() {
    return weightingFieldFractalNoiseOctaves;
  }

  public void setWeightingFieldFractalNoiseOctaves(int weightingFieldFractalNoiseOctaves) {
    this.weightingFieldFractalNoiseOctaves = weightingFieldFractalNoiseOctaves;
  }

  public double getWeightingFieldFractalNoiseGain() {
    return weightingFieldFractalNoiseGain;
  }

  public void setWeightingFieldFractalNoiseGain(double weightingFieldFractalNoiseGain) {
    this.weightingFieldFractalNoiseGain = weightingFieldFractalNoiseGain;
  }

  public double getWeightingFieldFractalNoiseLacunarity() {
    return weightingFieldFractalNoiseLacunarity;
  }

  public void setWeightingFieldFractalNoiseLacunarity(double weightingFieldFractalNoiseLacunarity) {
    this.weightingFieldFractalNoiseLacunarity = weightingFieldFractalNoiseLacunarity;
  }

  public CellularNoiseReturnType getWeightingFieldCellularNoiseReturnType() {
    return weightingFieldCellularNoiseReturnType;
  }

  public void setWeightingFieldCellularNoiseReturnType(CellularNoiseReturnType weightingFieldCellularNoiseReturnType) {
    this.weightingFieldCellularNoiseReturnType = weightingFieldCellularNoiseReturnType;
  }

  public CellularNoiseDistanceFunction getWeightingFieldCellularNoiseDistanceFunction() {
    return weightingFieldCellularNoiseDistanceFunction;
  }

  public void setWeightingFieldCellularNoiseDistanceFunction(CellularNoiseDistanceFunction weightingFieldCellularNoiseDistanceFunction) {
    this.weightingFieldCellularNoiseDistanceFunction = weightingFieldCellularNoiseDistanceFunction;
  }

  public FractalType getWeightingFieldFractalType() {
    return weightingFieldFractalType;
  }

  public void setWeightingFieldFractalType(FractalType weightingFieldFractalType) {
    this.weightingFieldFractalType = weightingFieldFractalType;
  }

  public double getWeightingFieldVarParam1Intensity() {
    return weightingFieldVarParam1Intensity;
  }

  public void setWeightingFieldVarParam1Intensity(double weightingFieldVarParam1Intensity) {
    this.weightingFieldVarParam1Intensity = weightingFieldVarParam1Intensity;
  }

  public String getWeightingFieldVarParam1VarName() {
    return weightingFieldVarParam1VarName;
  }

  public void setWeightingFieldVarParam1VarName(String weightingFieldVarParam1VarName) {
    this.weightingFieldVarParam1VarName = weightingFieldVarParam1VarName;
  }

  public String getWeightingFieldVarParam1ParamName() {
    return weightingFieldVarParam1ParamName;
  }

  public void setWeightingFieldVarParam1ParamName(String weightingFieldVarParam1ParamName) {
    this.weightingFieldVarParam1ParamName = weightingFieldVarParam1ParamName;
  }

  public MotionCurve getWeightingFieldVarParam1IntensityCurve() {
    return weightingFieldVarParam1IntensityCurve;
  }

  public double getWeightingFieldVarParam2Intensity() {
    return weightingFieldVarParam2Intensity;
  }

  public void setWeightingFieldVarParam2Intensity(double weightingFieldVarParam2Intensity) {
    this.weightingFieldVarParam2Intensity = weightingFieldVarParam2Intensity;
  }

  public String getWeightingFieldVarParam2VarName() {
    return weightingFieldVarParam2VarName;
  }

  public void setWeightingFieldVarParam2VarName(String weightingFieldVarParam2VarName) {
    this.weightingFieldVarParam2VarName = weightingFieldVarParam2VarName;
  }

  public String getWeightingFieldVarParam2ParamName() {
    return weightingFieldVarParam2ParamName;
  }

  public void setWeightingFieldVarParam2ParamName(String weightingFieldVarParam2ParamName) {
    this.weightingFieldVarParam2ParamName = weightingFieldVarParam2ParamName;
  }

  public MotionCurve getWeightingFieldVarParam2IntensityCurve() {
    return weightingFieldVarParam2IntensityCurve;
  }

  public double getWeightingFieldVarParam3Intensity() {
    return weightingFieldVarParam3Intensity;
  }

  public void setWeightingFieldVarParam3Intensity(double weightingFieldVarParam3Intensity) {
    this.weightingFieldVarParam3Intensity = weightingFieldVarParam3Intensity;
  }

  public String getWeightingFieldVarParam3VarName() {
    return weightingFieldVarParam3VarName;
  }

  public void setWeightingFieldVarParam3VarName(String weightingFieldVarParam3VarName) {
    this.weightingFieldVarParam3VarName = weightingFieldVarParam3VarName;
  }

  public String getWeightingFieldVarParam3ParamName() {
    return weightingFieldVarParam3ParamName;
  }

  public void setWeightingFieldVarParam3ParamName(String weightingFieldVarParam3ParamName) {
    this.weightingFieldVarParam3ParamName = weightingFieldVarParam3ParamName;
  }

  public MotionCurve getWeightingFieldVarParam3IntensityCurve() {
    return weightingFieldVarParam3IntensityCurve;
  }

  public boolean isWeightingFieldActive() {
    return (fabs(weightingFieldColorIntensity)>EPSILON || fabs(weightingFieldVarAmountIntensity)>EPSILON ||
            (fabs(weightingFieldVarParam1Intensity)>EPSILON && weightingFieldVarParam1VarName.length()>0) ||
            (fabs(weightingFieldVarParam2Intensity)>EPSILON && weightingFieldVarParam2VarName.length()>0) ||
            (fabs(weightingFieldVarParam3Intensity)>EPSILON && weightingFieldVarParam3VarName.length()>0) ||
            fabs(weightingFieldJitterIntensity)>EPSILON
            ) && (
            (WeightingFieldType.IMAGE_MAP.equals(weightingFieldType) && weightingFieldColorMapFilename !=null && !weightingFieldColorMapFilename.equals("")) ||
            (weightingFieldType !=null && !WeightingFieldType.NONE.equals(weightingFieldType))
    );
  }
}
