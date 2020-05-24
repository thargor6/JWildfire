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
package org.jwildfire.create.tina.base.solidrender;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.fabs;

import java.io.Serializable;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.edit.Assignable;

@SuppressWarnings("serial")
public class MaterialSettings implements Assignable<MaterialSettings>, Serializable {
  private double diffuse = 0.5;
  private double ambient = 1.0;
  private double phong = 1.0;
  private double phongSize = 24.0;
  private double phongRed, phongGreen, phongBlue;
  private String reflMapFilename = null;
  private double reflMapIntensity = 0.5;
  private ReflectionMapping reflectionMapping = ReflectionMapping.BLINN_NEWELL;
  private LightDiffFunc lightDiffFunc = LightDiffFuncPreset.COSA;

  public double getDiffuse() {
    return diffuse;
  }

  public void setDiffuse(double diffuse) {
    this.diffuse = diffuse;
  }

  public double getAmbient() {
    return ambient;
  }

  public void setAmbient(double ambient) {
    this.ambient = ambient;
  }

  public double getPhong() {
    return phong;
  }

  public void setPhong(double phong) {
    this.phong = phong;
  }

  public double getPhongSize() {
    return phongSize;
  }

  public void setPhongSize(double phongSize) {
    this.phongSize = phongSize;
  }

  public double getPhongRed() {
    return phongRed;
  }

  public void setPhongRed(double phongRed) {
    this.phongRed = phongRed;
  }

  public double getPhongGreen() {
    return phongGreen;
  }

  public void setPhongGreen(double phongGreen) {
    this.phongGreen = phongGreen;
  }

  public double getPhongBlue() {
    return phongBlue;
  }

  public void setPhongBlue(double phongBlue) {
    this.phongBlue = phongBlue;
  }

  public LightDiffFunc getLightDiffFunc() {
    return lightDiffFunc;
  }

  public void setLightDiffFunc(LightDiffFunc lightDiffFunc) {
    this.lightDiffFunc = lightDiffFunc;
  }

  @Override
  public void assign(MaterialSettings pSrc) {
    diffuse = pSrc.diffuse;
    ambient = pSrc.ambient;
    phong = pSrc.phong;
    phongSize = pSrc.phongSize;
    phongRed = pSrc.phongRed;
    phongGreen = pSrc.phongGreen;
    phongBlue = pSrc.phongBlue;
    lightDiffFunc = pSrc.lightDiffFunc;
    reflMapIntensity = pSrc.reflMapIntensity;
    reflMapFilename = pSrc.reflMapFilename;
    reflectionMapping = pSrc.reflectionMapping;
  }

  @Override
  public MaterialSettings makeCopy() {
    MaterialSettings res = new MaterialSettings();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(MaterialSettings pSrc) {
    if (fabs(diffuse - pSrc.diffuse) > EPSILON || fabs(ambient - pSrc.ambient) > EPSILON ||
        fabs(phong - pSrc.phong) > EPSILON || fabs(phongSize - pSrc.phongSize) > EPSILON ||
        fabs(phongRed - pSrc.phongRed) > EPSILON || fabs(phongGreen - pSrc.phongGreen) > EPSILON ||
        fabs(phongBlue - pSrc.phongBlue) > EPSILON || !lightDiffFunc.equals(pSrc.lightDiffFunc) ||
        fabs(reflMapIntensity - pSrc.reflMapIntensity) > EPSILON || reflectionMapping != pSrc.reflectionMapping ||
        !Tools.stringEquals(reflMapFilename, pSrc.reflMapFilename)) {
      return false;
    }
    return true;
  }

  public String getReflMapFilename() {
    return reflMapFilename;
  }

  public void setReflMapFilename(String reflMapFilename) {
    this.reflMapFilename = reflMapFilename;
  }

  public double getReflMapIntensity() {
    return reflMapIntensity;
  }

  public void setReflMapIntensity(double reflMapIntensity) {
    this.reflMapIntensity = reflMapIntensity;
  }

  public ReflectionMapping getReflectionMapping() {
    return reflectionMapping;
  }

  public void setReflectionMapping(ReflectionMapping reflectionMapping) {
    this.reflectionMapping = reflectionMapping;
  }

}
