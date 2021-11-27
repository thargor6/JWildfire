/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

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

package org.jwildfire.cli;

public class RenderOptions {
  private int renderWidth = 800;
  private int renderHeight = 600;
  private double renderQuality = 50;
  // "GLOBAL_SMOOTHING" | "GLOBAL_SHARPENING" | "ADAPTIVE"
  private String spatialFilteringType = "ADAPTIVE";
  private int spatialOversampling = 1;
  private double spatialFilterRadius = 0.75;
  // "OPTIX" | "OIDN" | "NONE"
  private String aiPostDenoiserType = "NONE";
  // overwrite the "postDenoiserOnlyForCpuRender" on flames
  private boolean forceAiPostDenoiser = false;

  private boolean useGPU = false;

  public int getRenderWidth() {
    return renderWidth;
  }

  public void setRenderWidth(int renderWidth) {
    this.renderWidth = renderWidth;
  }

  public int getRenderHeight() {
    return renderHeight;
  }

  public void setRenderHeight(int renderHeight) {
    this.renderHeight = renderHeight;
  }

  public double getRenderQuality() {
    return renderQuality;
  }

  public void setRenderQuality(double renderQuality) {
    this.renderQuality = renderQuality;
  }

  public boolean isUseGPU() {
    return useGPU;
  }

  public void setUseGPU(boolean useGPU) {
    this.useGPU = useGPU;
  }

  public int getSpatialOversampling() {
    return spatialOversampling;
  }

  public void setSpatialOversampling(int spatialOversampling) {
    this.spatialOversampling = spatialOversampling;
  }

  public double getSpatialFilterRadius() {
    return spatialFilterRadius;
  }

  public void setSpatialFilterRadius(double spatialFilterRadius) {
    this.spatialFilterRadius = spatialFilterRadius;
  }

  public String getSpatialFilteringType() {
    return spatialFilteringType;
  }

  public void setSpatialFilteringType(String spatialFilteringType) {
    this.spatialFilteringType = spatialFilteringType;
  }

  public String getAiPostDenoiserType() {
    return aiPostDenoiserType;
  }

  public void setAiPostDenoiserType(String aiPostDenoiserType) {
    this.aiPostDenoiserType = aiPostDenoiserType;
  }

  public boolean isForceAiPostDenoiser() {
    return forceAiPostDenoiser;
  }

  public void setForceAiPostDenoiser(boolean forceAiPostDenoiser) {
    this.forceAiPostDenoiser = forceAiPostDenoiser;
  }

  public void validate() {
    //
  }
}
