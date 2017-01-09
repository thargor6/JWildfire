/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2016 Andreas Maschke

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
import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.animate.FlameMorphService;
import org.jwildfire.create.tina.edit.Assignable;
import org.jwildfire.create.tina.render.filter.FilterKernelType;

@SuppressWarnings("serial")
public class SolidRenderSettings implements Assignable<SolidRenderSettings>, Serializable {
  private boolean solidRenderingEnabled = false;

  private boolean aoEnabled;
  private double aoIntensity;
  private double aoSearchRadius;
  private double aoBlurRadius;
  private int aoRadiusSamples;
  private int aoAzimuthSamples;
  private double aoFalloff;
  private double aoAffectDiffuse;

  private ShadowType shadowType;
  private double shadowmapBias;
  private int shadowmapSize;
  private double shadowSmoothRadius;

  private FilterKernelType postBokehFilterKernel;
  private double postBokehIntensity;
  private double postBokehBrightness;
  private double postBokehSize;
  private double postBokehActivation;

  private final List<MaterialSettings> materials = new ArrayList<>();
  private final List<DistantLight> lights = new ArrayList<>();

  public SolidRenderSettings() {
    setupDefaultPostBokehOptions();
    setupDefaultAmbientShadowOptions();
    setupDefaultHardShadowOptions();
  }

  public void setupDefaults() {
    setupDefaultPostBokehOptions();
    setupDefaultAmbientShadowOptions();
    setupDefaultHardShadowOptions();
    setupDefaultMaterials();
    setupDefaultLights();
  }

  public void setupDefaultLights() {
    lights.clear();
    {
      DistantLight light = new DistantLight();
      lights.add(light);
      light.setAltitude(60);
      light.setAzimuth(-30.0);
      light.setIntensity(0.8);
      light.setShadowIntensity(0.8);
      light.setRed(1.0);
      light.setGreen(1.0);
      light.setBlue(1.0);
      light.setCastShadows(true);
    }

    {
      DistantLight light = new DistantLight();
      lights.add(light);
      light.setAltitude(55);
      light.setAzimuth(-15.0);
      light.setIntensity(0.5);
      light.setShadowIntensity(0.7);
      light.setRed(1.0);
      light.setGreen(1.0);
      light.setBlue(1.0);
      light.setCastShadows(false);
    }

  }

  public void setupDefaultMaterials() {
    materials.clear();
    {
      MaterialSettings material = new MaterialSettings();
      material.setAmbient(0.6);
      material.setDiffuse(0.4);
      material.setPhong(0.8);
      material.setPhongSize(12.0);
      material.setPhongRed(1.0);
      material.setPhongGreen(1.0);
      material.setPhongBlue(1.0);
      material.setReflMapIntensity(0.5);
      material.setReflMapFilename(null);
      materials.add(material);
    }

    {
      MaterialSettings material = new MaterialSettings();
      material.setAmbient(0.8);
      material.setDiffuse(0.1);
      material.setPhong(0.6);
      material.setPhongSize(15.0);
      material.setPhongRed(1.0);
      material.setPhongGreen(1.0);
      material.setPhongBlue(1.0);
      material.setReflMapIntensity(0.5);
      material.setReflMapFilename(null);
      materials.add(material);
    }
  }

  private boolean isValidMaterialIdx(int idx) {
    return idx >= 0 && idx < materials.size();
  }

  public MaterialSettings getInterpolatedMaterial(double materialIdx) {
    if (materialIdx < 0 || materials.isEmpty()) {
      return null;
    }

    int fromIdx = (int) materialIdx;
    if (fromIdx >= materials.size()) {
      return materials.get(materials.size() - 1);
    }

    int toIdx = fromIdx + 1;
    if (toIdx >= materials.size()) {
      toIdx = 0;
    }

    double scl = MathLib.frac(materialIdx);
    double EPS = 0.01;

    if (scl < EPS) {
      return isValidMaterialIdx(fromIdx) ? materials.get(fromIdx) : null;
    }
    else if (scl > 1.0 - EPS) {
      return isValidMaterialIdx(toIdx) ? materials.get(toIdx) : null;
    }
    else {
      return isValidMaterialIdx(fromIdx) && isValidMaterialIdx(toIdx) ? morphMaterial(materials.get(fromIdx), materials.get(toIdx), scl) : null;
      //return scl <= 0.5 ? isValidMaterialIdx(fromIdx) ? materials.get(fromIdx) : null : isValidMaterialIdx(toIdx) ? materials.get(toIdx) : null;
    }
  }

  private MaterialSettings morphMaterial(final MaterialSettings from, final MaterialSettings to, final double scl) {
    MaterialSettings morph = new MaterialSettings();
    morph.setDiffuse(FlameMorphService.morphValue(from.getDiffuse(), to.getDiffuse(), scl));
    morph.setAmbient(FlameMorphService.morphValue(from.getAmbient(), to.getAmbient(), scl));
    morph.setPhong(FlameMorphService.morphValue(from.getPhong(), to.getPhong(), scl));
    morph.setPhongSize(FlameMorphService.morphValue(from.getPhongSize(), to.getPhongSize(), scl));
    morph.setPhongRed(FlameMorphService.morphValue(from.getPhongRed(), to.getPhongRed(), scl));
    morph.setPhongGreen(FlameMorphService.morphValue(from.getPhongGreen(), to.getPhongGreen(), scl));
    morph.setPhongBlue(FlameMorphService.morphValue(from.getPhongBlue(), to.getPhongBlue(), scl));
    morph.setDiffuse(FlameMorphService.morphValue(from.getReflMapIntensity(), to.getReflMapIntensity(), scl));
    morph.setReflMapIntensity(FlameMorphService.morphValue(from.getReflMapIntensity(), to.getReflMapIntensity(), scl));
    morph.setLightDiffFunc(new LightDiffFunc() {

      @Override
      public double evaluate(double pCosa) {
        double d1 = from.getLightDiffFunc().evaluate(pCosa);
        double d2 = to.getLightDiffFunc().evaluate(pCosa);
        return FlameMorphService.morphValue(d1, d2, scl);
      }

    });
    return morph;
  }

  public List<DistantLight> getLights() {
    return lights;
  }

  public boolean isAoEnabled() {
    return aoEnabled;
  }

  public void setAoEnabled(boolean aoEnabled) {
    this.aoEnabled = aoEnabled;
  }

  public double getAoIntensity() {
    return aoIntensity;
  }

  public void setAoIntensity(double aoIntensity) {
    this.aoIntensity = aoIntensity;
  }

  public boolean isSolidRenderingEnabled() {
    return solidRenderingEnabled;
  }

  public void setSolidRenderingEnabled(boolean solidRenderingEnabled) {
    this.solidRenderingEnabled = solidRenderingEnabled;
  }

  @Override
  public void assign(SolidRenderSettings pSrc) {
    solidRenderingEnabled = pSrc.solidRenderingEnabled;
    aoEnabled = pSrc.aoEnabled;
    aoIntensity = pSrc.aoIntensity;
    aoSearchRadius = pSrc.aoSearchRadius;
    aoBlurRadius = pSrc.aoBlurRadius;
    aoRadiusSamples = pSrc.aoRadiusSamples;
    aoAzimuthSamples = pSrc.aoAzimuthSamples;
    aoFalloff = pSrc.aoFalloff;
    aoAffectDiffuse = pSrc.aoAffectDiffuse;

    shadowType = pSrc.shadowType;
    shadowmapBias = pSrc.shadowmapBias;
    shadowmapSize = pSrc.shadowmapSize;
    shadowSmoothRadius = pSrc.shadowSmoothRadius;

    postBokehFilterKernel = pSrc.postBokehFilterKernel;
    postBokehIntensity = pSrc.postBokehIntensity;
    postBokehBrightness = pSrc.postBokehBrightness;
    postBokehSize = pSrc.postBokehSize;
    postBokehActivation = pSrc.postBokehActivation;

    materials.clear();
    for (MaterialSettings src : pSrc.getMaterials()) {
      MaterialSettings dst = new MaterialSettings();
      dst.assign(src);
      materials.add(dst);
    }
    lights.clear();
    for (DistantLight src : pSrc.getLights()) {
      DistantLight dst = new DistantLight();
      dst.assign(src);
      lights.add(dst);
    }
  }

  @Override
  public SolidRenderSettings makeCopy() {
    SolidRenderSettings res = new SolidRenderSettings();
    res.assign(this);
    return res;
  }

  @Override
  public boolean isEqual(SolidRenderSettings pSrc) {

    // do not care when solid rendering is disabled
    if (!solidRenderingEnabled && !pSrc.solidRenderingEnabled) {
      return true;
    }

    if (solidRenderingEnabled != pSrc.solidRenderingEnabled ||
        aoEnabled != pSrc.aoEnabled || !shadowType.equals(pSrc.shadowType) ||
        fabs(shadowSmoothRadius - pSrc.shadowSmoothRadius) > EPSILON || shadowmapSize != pSrc.shadowmapSize ||
        fabs(shadowmapBias - pSrc.shadowmapBias) > EPSILON ||
        fabs(aoIntensity - pSrc.aoIntensity) > EPSILON || fabs(aoSearchRadius - pSrc.aoSearchRadius) > EPSILON ||
        fabs(aoBlurRadius - pSrc.aoBlurRadius) > EPSILON || aoRadiusSamples != pSrc.aoRadiusSamples ||
        aoAzimuthSamples != pSrc.aoAzimuthSamples || fabs(aoFalloff - pSrc.aoFalloff) > EPSILON ||
        !postBokehFilterKernel.equals(pSrc.postBokehFilterKernel) || fabs(postBokehIntensity - pSrc.postBokehIntensity) > EPSILON ||
        fabs(postBokehBrightness - pSrc.postBokehBrightness) > EPSILON || fabs(postBokehSize - pSrc.postBokehSize) > EPSILON ||
        fabs(postBokehActivation - pSrc.postBokehActivation) > EPSILON || fabs(aoAffectDiffuse - pSrc.aoAffectDiffuse) > EPSILON) {
      return false;
    }

    if (materials.size() != pSrc.materials.size()) {
      return false;
    }
    if (lights.size() != pSrc.lights.size()) {
      return false;
    }

    for (int i = 0; i < materials.size(); i++) {
      if (!materials.get(i).isEqual(pSrc.getMaterials().get(i))) {
        return false;
      }
    }

    for (int i = 0; i < lights.size(); i++) {
      if (!lights.get(i).isEqual(pSrc.getLights().get(i))) {
        return false;
      }
    }
    return true;
  }

  public List<MaterialSettings> getMaterials() {
    return materials;
  }

  public DistantLight addLight() {
    DistantLight light = new DistantLight();
    lights.add(light);
    light.setAltitude(60.0);
    light.setAzimuth(-30.0);
    light.setIntensity(0.7);
    light.setShadowIntensity(0.6);
    light.setRed(1.0);
    light.setGreen(1.0);
    light.setBlue(1.0);
    light.setCastShadows(false);
    return light;
  }

  public MaterialSettings addMaterial() {
    MaterialSettings material = new MaterialSettings();
    material.setAmbient(0.6);
    material.setDiffuse(0.5);
    material.setPhong(0.7);
    material.setPhongSize(36.0);
    material.setPhongRed(1.0);
    material.setPhongGreen(0.9);
    material.setPhongBlue(0.3);
    material.setReflMapIntensity(0.5);
    materials.add(material);
    return material;
  }

  public double getAoSearchRadius() {
    return aoSearchRadius;
  }

  public void setAoSearchRadius(double aoSearchRadius) {
    this.aoSearchRadius = aoSearchRadius;
  }

  public double getAoBlurRadius() {
    return aoBlurRadius;
  }

  public void setAoBlurRadius(double aoBlurRadius) {
    this.aoBlurRadius = aoBlurRadius;
  }

  public int getAoRadiusSamples() {
    return aoRadiusSamples;
  }

  public void setAoRadiusSamples(int aoRadiusSamples) {
    this.aoRadiusSamples = aoRadiusSamples;
  }

  public int getAoAzimuthSamples() {
    return aoAzimuthSamples;
  }

  public void setAoAzimuthSamples(int aoAzimuthSamples) {
    this.aoAzimuthSamples = aoAzimuthSamples;
  }

  public double getAoFalloff() {
    return aoFalloff;
  }

  public void setAoFalloff(double aoFalloff) {
    this.aoFalloff = aoFalloff;
  }

  public double getAoAffectDiffuse() {
    return aoAffectDiffuse;
  }

  public void setAoAffectDiffuse(double aoAffectDiffuse) {
    this.aoAffectDiffuse = aoAffectDiffuse;
  }

  public void setupDefaultPostBokehOptions() {
    postBokehFilterKernel = FilterKernelType.SINEPOW15;
    postBokehIntensity = 0.005;
    postBokehBrightness = 1.0;
    postBokehSize = 2.0;
    postBokehActivation = 0.2;
  }

  public void setupDefaultAmbientShadowOptions() {
    aoEnabled = true;
    aoIntensity = 0.6;
    aoSearchRadius = 4.0;
    aoBlurRadius = 1.5;
    aoRadiusSamples = 6;
    aoAzimuthSamples = 7;
    aoFalloff = 0.5;
    aoAffectDiffuse = 0.1;
  }

  public void setupDefaultHardShadowOptions() {
    shadowType = ShadowType.OFF;
    shadowmapBias = 0.01;
    shadowmapSize = 2048;
    shadowSmoothRadius = 1.0;
  }

  public ShadowType getShadowType() {
    return shadowType;
  }

  public void setShadowType(ShadowType shadowType) {
    this.shadowType = shadowType;
  }

  public double getShadowmapBias() {
    return shadowmapBias;
  }

  public void setShadowmapBias(double shadowmapBias) {
    this.shadowmapBias = shadowmapBias;
  }

  public int getShadowmapSize() {
    return shadowmapSize;
  }

  public void setShadowmapSize(int shadowmapSize) {
    this.shadowmapSize = shadowmapSize;
  }

  public double getShadowSmoothRadius() {
    return shadowSmoothRadius;
  }

  public void setShadowSmoothRadius(double shadowSmoothRadius) {
    this.shadowSmoothRadius = shadowSmoothRadius;
  }

  public FilterKernelType getPostBokehFilterKernel() {
    return postBokehFilterKernel;
  }

  public void setPostBokehFilterKernel(FilterKernelType postBokehFilterKernel) {
    this.postBokehFilterKernel = postBokehFilterKernel;
  }

  public double getPostBokehIntensity() {
    return postBokehIntensity;
  }

  public void setPostBokehIntensity(double postBokehIntensity) {
    this.postBokehIntensity = postBokehIntensity;
  }

  public double getPostBokehBrightness() {
    return postBokehBrightness;
  }

  public void setPostBokehBrightness(double postBokehBrightness) {
    this.postBokehBrightness = postBokehBrightness;
  }

  public double getPostBokehSize() {
    return postBokehSize;
  }

  public void setPostBokehSize(double postBokehSize) {
    this.postBokehSize = postBokehSize;
  }

  public double getPostBokehActivation() {
    return postBokehActivation;
  }

  public void setPostBokehActivation(double postBokehActivation) {
    this.postBokehActivation = postBokehActivation;
  }

}
