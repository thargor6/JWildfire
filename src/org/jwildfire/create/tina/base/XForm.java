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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.render.AffineZStyle;
import org.jwildfire.create.tina.variation.TransformationContext;
import org.jwildfire.create.tina.variation.Variation;
import org.jwildfire.create.tina.variation.VariationFunc;

public class XForm {
  private double weight;
  private double color;
  private double colorSymmetry;
  private double coeff00;
  private double coeff01;
  private double coeff10;
  private double coeff11;
  private double coeff20;
  private double coeff21;
  private double postCoeff00;
  private double postCoeff01;
  private double postCoeff10;
  private double postCoeff11;
  private double postCoeff20;
  private double postCoeff21;
  private final List<Variation> variations = new ArrayList<Variation>();
  private final double modifiedWeights[] = new double[Constants.MAX_MOD_WEIGHT_COUNT]; // the same like "xaos" in Apophysis
  private double opacity = 0.0;
  private final XForm[] nextAppliedXFormTable = new XForm[Constants.NEXT_APPLIED_XFORM_TABLE_SIZE];
  private DrawMode drawMode = DrawMode.NORMAL;

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
    this.color = color;
  }

  public double getCoeff00() {
    return coeff00;
  }

  public void setCoeff00(double coeff00) {
    this.coeff00 = coeff00;
  }

  public double getCoeff01() {
    return coeff01;
  }

  public void setCoeff01(double coeff01) {
    this.coeff01 = coeff01;
  }

  public double getCoeff10() {
    return coeff10;
  }

  public void setCoeff10(double coeff10) {
    this.coeff10 = coeff10;
  }

  public double getCoeff11() {
    return coeff11;
  }

  public void setCoeff11(double coeff11) {
    this.coeff11 = coeff11;
  }

  public double getCoeff20() {
    return coeff20;
  }

  public void setCoeff20(double coeff20) {
    this.coeff20 = coeff20;
  }

  public double getCoeff21() {
    return coeff21;
  }

  public void setCoeff21(double coeff21) {
    this.coeff21 = coeff21;
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

  public double getColorSymmetry() {
    return colorSymmetry;
  }

  public void setColorSymmetry(double colorSymmetry) {
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
  }

  public boolean hasPostCoeffs() {
    return (Math.abs(postCoeff00 - 1.0) > Tools.EPSILON || Math.abs(postCoeff01) > Tools.EPSILON || Math.abs(postCoeff10) > Tools.EPSILON
        || Math.abs(postCoeff11 - 1.0) > Tools.EPSILON || Math.abs(postCoeff20) > Tools.EPSILON || Math.abs(postCoeff21) > Tools.EPSILON);
  }

  public void transformPoint(TransformationContext pContext, XYZPoint pAffineT, XYZPoint pVarT, XYZPoint pSrcPoint, XYZPoint pDstPoint, AffineZStyle pZStyle) {
    pDstPoint.color = pSrcPoint.color * c1 + c2;

    pAffineT.clear();
    pAffineT.x = coeff00 * pSrcPoint.x + coeff10 * pSrcPoint.y + coeff20;
    pAffineT.y = coeff01 * pSrcPoint.x + coeff11 * pSrcPoint.y + coeff21;
    switch (pZStyle) {
      case FLAT:
        pAffineT.z = pSrcPoint.z;
        break;
      case Z1:
        pAffineT.z = coeff11 * pSrcPoint.y + coeff00 * pSrcPoint.z + coeff20;
        break;
      case Z2:
        pAffineT.z = coeff00 * pSrcPoint.x + coeff11 * pSrcPoint.y + (coeff10 + coeff01) * pSrcPoint.z + (coeff20 + coeff21) * 0.5;
        break;
      case Z3:
        pAffineT.z = coeff01 * pSrcPoint.x + coeff10 * pSrcPoint.y + (coeff00 + coeff11) * pSrcPoint.z + (coeff20 + coeff21) * 0.5;
        break;
      case Z4:
        pAffineT.z = coeff01 * pSrcPoint.y + coeff00 * pSrcPoint.z + (coeff20 + coeff21) * 0.5;
        break;
      case Z5:
        pAffineT.z = (coeff00 + coeff01) * pSrcPoint.x * 0.5 + (coeff10 + coeff11) * pSrcPoint.y * 0.5 + (coeff10 + coeff01) * pSrcPoint.z * 0.5 + (coeff20 + coeff21) * 0.5;
      case Z6:
        pAffineT.z = (coeff00 + coeff01) * pSrcPoint.x * 0.5 + (coeff10 + coeff11) * pSrcPoint.y * 0.5 + (coeff00 + coeff10 + coeff01 + coeff11) * pSrcPoint.z * 0.25 + (coeff20 + coeff21) * 0.5;
        break;
      default:
        throw new IllegalStateException(pZStyle.toString());
    }
    pVarT.clear();
    for (Variation variation : variations) {
      variation.transform(pContext, this, pAffineT, pVarT);
      if (variation.getFunc().getPriority() < 0) {
        pAffineT.invalidate();
      }
    }
    if (hasPostCoeffs()) {
      double px = postCoeff00 * pVarT.x + postCoeff10 * pVarT.y + postCoeff20;
      double py = postCoeff01 * pVarT.x + postCoeff11 * pVarT.y + postCoeff21;
      double pz;
      switch (pZStyle) {
        case FLAT:
          pz = pVarT.z;
          break;
        case Z1:
          pz = postCoeff11 * pVarT.y + postCoeff00 * pVarT.z + postCoeff20;
          break;
        case Z2:
          pz = postCoeff00 * pVarT.x + postCoeff11 * pVarT.y + (postCoeff10 + postCoeff01) * pVarT.z + (postCoeff20 + postCoeff21) * 0.5;
          break;
        case Z3:
          pz = postCoeff01 * pVarT.x + postCoeff10 * pVarT.y + (postCoeff00 + postCoeff11) * pVarT.z + (postCoeff20 + postCoeff21) * 0.5;
          break;
        case Z4:
          pz = postCoeff01 * pVarT.y + postCoeff00 * pVarT.z + (postCoeff20 + postCoeff21) * 0.5;
          break;
        case Z5:
          pz = (postCoeff00 + postCoeff01) * pVarT.x * 0.5 + (postCoeff10 + postCoeff11) * pVarT.y * 0.5 + (postCoeff10 + postCoeff01) * pVarT.z * 0.5 + (postCoeff20 + postCoeff21) * 0.5;
        case Z6:
          pz = (postCoeff00 + postCoeff01) * pVarT.x * 0.5 + (postCoeff10 + postCoeff11) * pVarT.y * 0.5 + (postCoeff00 + postCoeff10 + postCoeff01 + postCoeff11) * pVarT.z * 0.25 + (postCoeff20 + postCoeff21) * 0.5;
          break;
        default:
          throw new IllegalStateException(pZStyle.toString());
      }
      pVarT.x = px;
      pVarT.y = py;
      pVarT.z = pz;
    }

    pDstPoint.x = pVarT.x;
    pDstPoint.y = pVarT.y;
    pDstPoint.z = pVarT.z;
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
    variations.clear();
    for (Variation var : pXForm.variations) {
      Variation newVar = new Variation();
      newVar.assign(var);
      variations.add(newVar);
    }
    opacity = pXForm.opacity;
    drawMode = pXForm.drawMode;
  }

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
  }

  public double getPostCoeff01() {
    return postCoeff01;
  }

  public void setPostCoeff01(double postCoeff01) {
    this.postCoeff01 = postCoeff01;
  }

  public double getPostCoeff10() {
    return postCoeff10;
  }

  public void setPostCoeff10(double postCoeff10) {
    this.postCoeff10 = postCoeff10;
  }

  public double getPostCoeff11() {
    return postCoeff11;
  }

  public void setPostCoeff11(double postCoeff11) {
    this.postCoeff11 = postCoeff11;
  }

  public double getPostCoeff20() {
    return postCoeff20;
  }

  public void setPostCoeff20(double postCoeff20) {
    this.postCoeff20 = postCoeff20;
  }

  public double getPostCoeff21() {
    return postCoeff21;
  }

  public void setPostCoeff21(double postCoeff21) {
    this.postCoeff21 = postCoeff21;
  }

}
