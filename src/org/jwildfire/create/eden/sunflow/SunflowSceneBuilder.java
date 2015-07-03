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
package org.jwildfire.create.eden.sunflow;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.create.eden.sunflow.base.PartBuilder;

public class SunflowSceneBuilder implements PartBuilder {
  private final ImageBuilder imageBuilder = new ImageBuilder(this);
  private final CameraBuilder cameraBuilder = new CameraBuilder(this);
  private final TraceDepthsBuilder traceDepthsBuilder = new TraceDepthsBuilder(this);
  private final List<LightBuilder> lightBuilders = new ArrayList<LightBuilder>();
  private final List<SunSkyLightBuilder> sunSkyLightBuilders = new ArrayList<SunSkyLightBuilder>();
  private final List<ShaderBuilder> shaderBuilders = new ArrayList<ShaderBuilder>();
  private final List<LegacyMeshBuilder> legacyMeshBuilders = new ArrayList<LegacyMeshBuilder>();
  private final List<MeshBuilder> meshBuilders = new ArrayList<MeshBuilder>();
  private final List<SphereBuilder> sphereBuilders = new ArrayList<SphereBuilder>();
  private final List<BoxBuilder> boxBuilders = new ArrayList<BoxBuilder>();
  private final List<CylinderBuilder> cylinderBuilders = new ArrayList<CylinderBuilder>();
  private final List<BanchoffSurfaceBuilder> banchoffSurfaceBuilders = new ArrayList<BanchoffSurfaceBuilder>();
  private final List<SphereflakeBuilder> sphereflakeBuilders = new ArrayList<SphereflakeBuilder>();
  private final List<TorusBuilder> torusBuilders = new ArrayList<TorusBuilder>();
  private final List<PlaneBuilder> planeBuilders = new ArrayList<PlaneBuilder>();
  private final List<JuliaBuilder> juliaBuilders = new ArrayList<JuliaBuilder>();

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

  public TraceDepthsBuilder openTraceDepths() {
    return traceDepthsBuilder;
  }

  public LightBuilder addLight() {
    LightBuilder lightBuilder = new LightBuilder(this);
    lightBuilders.add(lightBuilder);
    return lightBuilder;
  }

  public SunSkyLightBuilder addSkyLight() {
    SunSkyLightBuilder sunSkyLightBuilder = new SunSkyLightBuilder(this);
    sunSkyLightBuilders.add(sunSkyLightBuilder);
    return sunSkyLightBuilder;
  }

  public ShaderBuilder addShader() {
    ShaderBuilder shaderBuilder = new ShaderBuilder(this);
    shaderBuilders.add(shaderBuilder);
    return shaderBuilder;
  }

  @Deprecated
  public LegacyMeshBuilder addLegacyMesh() {
    LegacyMeshBuilder meshBuilder = new LegacyMeshBuilder(this);
    legacyMeshBuilders.add(meshBuilder);
    return meshBuilder;
  }

  public MeshBuilder addMesh() {
    MeshBuilder meshBuilder = new MeshBuilder(this);
    meshBuilders.add(meshBuilder);
    return meshBuilder;
  }

  public SphereBuilder addSphere() {
    SphereBuilder sphereBuilder = new SphereBuilder(this);
    sphereBuilders.add(sphereBuilder);
    return sphereBuilder;
  }

  public BoxBuilder addBox() {
    BoxBuilder boxBuilder = new BoxBuilder(this);
    boxBuilders.add(boxBuilder);
    return boxBuilder;
  }

  public CylinderBuilder addCylinder() {
    CylinderBuilder cylinderBuilder = new CylinderBuilder(this);
    cylinderBuilders.add(cylinderBuilder);
    return cylinderBuilder;
  }

  public BanchoffSurfaceBuilder addBanchoffSurface() {
    BanchoffSurfaceBuilder banchoffSurfaceBuilder = new BanchoffSurfaceBuilder(this);
    banchoffSurfaceBuilders.add(banchoffSurfaceBuilder);
    return banchoffSurfaceBuilder;
  }

  public SphereflakeBuilder addSphereflake() {
    SphereflakeBuilder sphereflakeBuilder = new SphereflakeBuilder(this);
    sphereflakeBuilders.add(sphereflakeBuilder);
    return sphereflakeBuilder;
  }

  public JuliaBuilder addJulia() {
    JuliaBuilder juliaBuilder = new JuliaBuilder(this);
    juliaBuilders.add(juliaBuilder);
    return juliaBuilder;
  }

  public PlaneBuilder addPlane() {
    PlaneBuilder planeBuilder = new PlaneBuilder(this);
    planeBuilders.add(planeBuilder);
    return planeBuilder;
  }

  public TorusBuilder addTorus() {
    TorusBuilder torusBuilder = new TorusBuilder(this);
    torusBuilders.add(torusBuilder);
    return torusBuilder;
  }

  @Override
  public void buildPart(StringBuilder pTarget) {
    imageBuilder.buildPart(pTarget);
    cameraBuilder.buildPart(pTarget);
    traceDepthsBuilder.buildPart(pTarget);
    for (LightBuilder lightBuilder : lightBuilders) {
      lightBuilder.buildPart(pTarget);
    }
    for (SunSkyLightBuilder sunSkyLightBuilder : sunSkyLightBuilders) {
      sunSkyLightBuilder.buildPart(pTarget);
    }
    for (ShaderBuilder shaderBuilder : shaderBuilders) {
      shaderBuilder.buildPart(pTarget);
    }
    for (LegacyMeshBuilder legacyMeshBuilder : legacyMeshBuilders) {
      legacyMeshBuilder.buildPart(pTarget);
    }
    for (MeshBuilder meshBuilder : meshBuilders) {
      meshBuilder.buildPart(pTarget);
    }
    for (SphereBuilder sphereBuilder : sphereBuilders) {
      sphereBuilder.buildPart(pTarget);
    }
    for (BoxBuilder boxBuilder : boxBuilders) {
      boxBuilder.buildPart(pTarget);
    }
    for (CylinderBuilder cylinderBuilder : cylinderBuilders) {
      cylinderBuilder.buildPart(pTarget);
    }
    for (BanchoffSurfaceBuilder banchoffSurfaceBuilder : banchoffSurfaceBuilders) {
      banchoffSurfaceBuilder.buildPart(pTarget);
    }
    for (SphereflakeBuilder sphereflakeBuilder : sphereflakeBuilders) {
      sphereflakeBuilder.buildPart(pTarget);
    }
    for (TorusBuilder torusBuilder : torusBuilders) {
      torusBuilder.buildPart(pTarget);
    }
    for (PlaneBuilder planeBuilder : planeBuilders) {
      planeBuilder.buildPart(pTarget);
    }
    for (JuliaBuilder juliaBuilder : juliaBuilders) {
      juliaBuilder.buildPart(pTarget);
    }
  }
}
