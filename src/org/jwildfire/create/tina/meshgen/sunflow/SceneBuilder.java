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
package org.jwildfire.create.tina.meshgen.sunflow;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.tina.meshgen.sunflow.base.PartBuilder;

public class SceneBuilder implements PartBuilder {
  private final ImageBuilder imageBuilder = new ImageBuilder(this);
  private final CameraBuilder cameraBuilder = new CameraBuilder(this);
  private final List<LightBuilder> lightBuilders = new ArrayList<LightBuilder>();
  private final List<SunSkyLightBuilder> sunSkyLightBuilders = new ArrayList<SunSkyLightBuilder>();
  private final List<ShaderBuilder> shaderBuilders = new ArrayList<ShaderBuilder>();
  private final List<MeshBuilder> meshBuilders = new ArrayList<MeshBuilder>();

  public String getProduct() {
    StringBuilder sb = new StringBuilder();
    buildPart(sb);
    return sb.toString();
  }

  public ImageBuilder openImage() {
    return imageBuilder;
  }

  public CameraBuilder openCamera() {
    return cameraBuilder;
  }

  public LightBuilder addLight() {
    LightBuilder lightBuilder = new LightBuilder(this);
    lightBuilders.add(lightBuilder);
    return lightBuilder;
  }

  public SunSkyLightBuilder addSunSkyLight() {
    SunSkyLightBuilder sunSkyLightBuilder = new SunSkyLightBuilder(this);
    sunSkyLightBuilders.add(sunSkyLightBuilder);
    return sunSkyLightBuilder;
  }

  public ShaderBuilder addShader() {
    ShaderBuilder shaderBuilder = new ShaderBuilder(this);
    shaderBuilders.add(shaderBuilder);
    return shaderBuilder;
  }

  public MeshBuilder addMesh() {
    MeshBuilder meshBuilder = new MeshBuilder(this);
    meshBuilders.add(meshBuilder);
    return meshBuilder;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    imageBuilder.buildPart(pTarget);
    cameraBuilder.buildPart(pTarget);
    for (LightBuilder lightBuilder : lightBuilders) {
      lightBuilder.buildPart(pTarget);
    }
    for (SunSkyLightBuilder sunSkyLightBuilder : sunSkyLightBuilders) {
      sunSkyLightBuilder.buildPart(pTarget);
    }
    for (ShaderBuilder shaderBuilder : shaderBuilders) {
      shaderBuilder.buildPart(pTarget);
    }
    for (MeshBuilder meshBuilder : meshBuilders) {
      meshBuilder.buildPart(pTarget);
    }
  }
}
