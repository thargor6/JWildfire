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

import static org.jwildfire.base.MathLib.EPSILON;
import static org.jwildfire.base.MathLib.fabs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jwildfire.base.MathLib;
import org.jwildfire.create.tina.animate.AnimAware;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.random.AbstractRandomGenerator.RandGenStatus;
import org.jwildfire.create.tina.variation.FlameTransformationContext;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;
import org.jwildfire.create.tina.variation.VariationPriorityComparator;

public final class XForm implements Assignable<XForm>, Serializable {
  private static final long serialVersionUID = 1L;
  @AnimAware
  private double weight;
  @AnimAware
  private double color;
  @AnimAware
  private double colorSymmetry;
  @AnimAware
  private double coeff00;
  @AnimAware
  private double coeff01;
  @AnimAware
  private double coeff10;
  @AnimAware
  private double coeff11;
  @AnimAware
  private double coeff20;
  @AnimAware
  private double coeff21;
  @AnimAware
  private double postCoeff00;
  @AnimAware
  private double postCoeff01;
  @AnimAware
  private double postCoeff10;
  @AnimAware
  private double postCoeff11;
  @AnimAware
  private double postCoeff20;
  @AnimAware
  private double postCoeff21;
  private boolean hasPostCoeffs;
  private boolean hasCoeffs;
  @AnimAware
  private final List<Variation> variations = new ArrayList<Variation>();
  private final List<Variation> sortedVariations = new ArrayList<Variation>();
  private Variation[] sortedVariationsArray = null;
  private final double modifiedWeights[] = new double[Constants.MAX_MOD_WEIGHT_COUNT]; // the same like "xaos" in Apophysis
  @AnimAware
  private double opacity = 0.0;
  private final XForm[] nextAppliedXFormTable = new XForm[Constants.NEXT_APPLIED_XFORM_TABLE_SIZE];
  private DrawMode drawMode = DrawMode.NORMAL;
  private double antialiasAmount = 0.0;
  private double antialiasRadius = 0.5;

  public XForm() {
    coeff00 = 1;
    coeff11 = 1;
    postCoeff00 = 1;
    postCoeff11 = 1;
    for (int i = 0; i < modifiedWeights.length; i++) {
      modifiedWeights[i] = 1.0;
    }
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

  private void updateSortedVariations() {
    sortedVariations.clear();
    sortedVariationsArray = new Variation[variations.size()];
    if (variations.size() > 0) {
      sortedVariations.addAll(variations);
      if (variations.size() > 1) {
        Collections.sort(sortedVariations, new VariationPriorityComparator());
      }
      // for faster access
      int i = 0;
      for (Variation var : sortedVariations) {
        sortedVariationsArray[i++] = var;
      }
    }
  }

  public List<Variation> getSortedVariations() {
    return sortedVariations;
  }

  public Variation addVariation(double pAmount, VariationFunc pVariationFunc) {
    Variation variation = new Variation();
    variation.setAmount(pAmount);
    variation.setFunc(pVariationFunc);
    variations.add(variation);
    updateSortedVariations();
    return variation;
  }

  public void addVariation(Variation pVariation) {
    variations.add(pVariation);
    updateSortedVariations();
  }

  public void removeVariation(Variation pVariation) {
    variations.remove(pVariation);
    updateSortedVariations();
  }

  public void clearVariations() {
    variations.clear();
    updateSortedVariations();
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

  public void initTransform() {
    // precalculate those variables to simplify the expression: 
    //   pDstPoint.color = (pSrcPoint.color + color) * 0.5 * (1 - colorSymmetry) + colorSymmetry * pSrcPoint.color;
    // to get:
    //   pDstPoint.color = pSrcPoint.color * c1 + c2;
    c1 = (1 + colorSymmetry) * 0.5;
    c2 = color * (1 - colorSymmetry) * 0.5;
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
    //    pAffineT.color = pSrcPoint.color * c1 + c2;

    //pAffineT.color = (pSrcPoint.color + color) * 0.5 * (1 - colorSymmetry) + colorSymmetry * pSrcPoint.color;
    //    pAffineT.color = 1.0 - Math.exp(-pAffineT.color);
    //    pAffineT.color = fabs(Math.log(pAffineT.color));
    //    pAffineT.color = fabs(Math.sin(pAffineT.color));
    //    pAffineT.color = fabs(Math.cos(pAffineT.color));

    //    pAffineT.color = pAffineT.color * pAffineT.color;
    //    pAffineT.color = Math.sqrt(pAffineT.color);
    //    if (pAffineT.color < 0)
    //      pAffineT.color = 0;
    //    else if (pAffineT.color > 1)
    //      pAffineT.color = 1;
    pAffineT.color = pSrcPoint.color * c1 + c2;

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
    pVarT.x = pVarT.y = pVarT.z = pVarT.color = 0.0;

    pVarT.color = pAffineT.color;
    pVarT.rgbColor = pAffineT.rgbColor;
    pVarT.redColor = pAffineT.redColor;
    pVarT.greenColor = pAffineT.greenColor;
    pVarT.blueColor = pAffineT.blueColor;

    for (int i = 0; i < sortedVariationsArray.length; i++) {
      Variation variation = sortedVariationsArray[i];
      variation.transform(pContext, this, pAffineT, pVarT);
      if (variation.getFunc().getPriority() < 0) {
        pAffineT.invalidate();
      }
    }
    pDstPoint.color = pVarT.color;
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
    try {
      for (int i = 0; i < 3; i++) {
        switch (i) {
          case 0:
            pContext.setRandGenStatus(RandGenStatus.RECORDING);
            break;
          default:
            pContext.setRandGenStatus(RandGenStatus.REPLAY);
            break;
        }
        pAffineT[i].clear();
        pAffineT[i].color = pSrcPoint[i].color * c1 + c2;
        pAffineT[i].x = coeff00 * pSrcPoint[i].x + coeff10 * pSrcPoint[i].y + coeff20;
        pAffineT[i].y = coeff01 * pSrcPoint[i].x + coeff11 * pSrcPoint[i].y + coeff21;
        pAffineT[i].z = pSrcPoint[i].z;
        pVarT[i].clear();
        pVarT[i].color = pAffineT[i].color;
        for (int j = 0; j < sortedVariationsArray.length; j++) {
          Variation variation = sortedVariationsArray[j];
          variation.transform(pContext, this, pAffineT[i], pVarT[i]);
          if (variation.getFunc().getPriority() < 0) {
            pAffineT[i].invalidate();
          }
        }
        pDstPoint[i].color = pVarT[i].color;
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
    finally {
      pContext.setRandGenStatus(RandGenStatus.DEFAULT);
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
    coeff00 = pXForm.coeff00;
    coeff01 = pXForm.coeff01;
    coeff10 = pXForm.coeff10;
    coeff11 = pXForm.coeff11;
    coeff20 = pXForm.coeff20;
    coeff21 = pXForm.coeff21;
    postCoeff00 = pXForm.postCoeff00;
    postCoeff01 = pXForm.postCoeff01;
    postCoeff10 = pXForm.postCoeff10;
    postCoeff11 = pXForm.postCoeff11;
    postCoeff20 = pXForm.postCoeff20;
    postCoeff21 = pXForm.postCoeff21;
    hasPostCoeffs = pXForm.hasPostCoeffs;
    hasCoeffs = pXForm.hasCoeffs;
    variations.clear();
    for (Variation var : pXForm.variations) {
      Variation newVar = new Variation();
      newVar.assign(var);
      variations.add(newVar);
    }
    updateSortedVariations();
    System.arraycopy(pXForm.modifiedWeights, 0, modifiedWeights, 0, pXForm.modifiedWeights.length);
    opacity = pXForm.opacity;
    drawMode = pXForm.drawMode;
    antialiasAmount = pXForm.antialiasAmount;
    antialiasRadius = pXForm.antialiasRadius;
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
    if ((fabs(weight - pSrc.weight) > EPSILON) || (fabs(color - pSrc.color) > EPSILON) ||
        (fabs(colorSymmetry - pSrc.colorSymmetry) > EPSILON) || (fabs(coeff00 - pSrc.coeff00) > EPSILON) ||
        (fabs(coeff01 - pSrc.coeff01) > EPSILON) || (fabs(coeff10 - pSrc.coeff10) > EPSILON) ||
        (fabs(coeff11 - pSrc.coeff11) > EPSILON) || (fabs(coeff20 - pSrc.coeff20) > EPSILON) ||
        (fabs(coeff21 - pSrc.coeff21) > EPSILON) || (fabs(postCoeff00 - pSrc.postCoeff00) > EPSILON) ||
        (fabs(postCoeff01 - pSrc.postCoeff01) > EPSILON) || (fabs(postCoeff10 - pSrc.postCoeff10) > EPSILON) ||
        (fabs(postCoeff11 - pSrc.postCoeff11) > EPSILON) || (fabs(postCoeff20 - pSrc.postCoeff20) > EPSILON) ||
        (fabs(postCoeff21 - pSrc.postCoeff21) > EPSILON) || (fabs(opacity - pSrc.opacity) > EPSILON) ||
        ((drawMode != null && pSrc.drawMode == null) || (drawMode == null && pSrc.drawMode != null) ||
        (drawMode != null && pSrc.drawMode != null && !drawMode.equals(pSrc.drawMode))) ||
        (modifiedWeights.length != pSrc.modifiedWeights.length) || (variations.size() != pSrc.variations.size()) ||
        (fabs(antialiasAmount - pSrc.antialiasAmount) > EPSILON) || (fabs(antialiasRadius - pSrc.antialiasRadius) > EPSILON)) {
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
}
