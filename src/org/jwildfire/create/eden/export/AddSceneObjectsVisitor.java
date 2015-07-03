/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2015 Andreas Maschke

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
package org.jwildfire.create.eden.export;

import java.util.ArrayList;
import java.util.List;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.eden.scene.MaterialGroup;
import org.jwildfire.create.eden.scene.PositionableSceneElement;
import org.jwildfire.create.eden.scene.Scene;
import org.jwildfire.create.eden.scene.SceneElement;
import org.jwildfire.create.eden.scene.SceneElementVisitor;
import org.jwildfire.create.eden.scene.camera.Camera;
import org.jwildfire.create.eden.scene.light.PointLight;
import org.jwildfire.create.eden.scene.light.SkyLight;
import org.jwildfire.create.eden.scene.material.Glass;
import org.jwildfire.create.eden.scene.material.Material;
import org.jwildfire.create.eden.scene.material.Shiny;
import org.jwildfire.create.eden.scene.primitive.Box;
import org.jwildfire.create.eden.scene.primitive.Cylinder;
import org.jwildfire.create.eden.scene.primitive.Mesh;
import org.jwildfire.create.eden.scene.primitive.Sphere;
import org.jwildfire.create.eden.sunflow.SunflowSceneBuilder;
import org.jwildfire.create.eden.sunflow.base.CameraType;
import org.jwildfire.create.eden.sunflow.base.LightType;
import org.jwildfire.create.eden.sunflow.base.ShaderType;
import org.sunflow.math.Matrix4;

public class AddSceneObjectsVisitor implements SceneElementVisitor {
  private final SunflowSceneBuilder sceneBuilder;
  private boolean useMatrix = true;

  public AddSceneObjectsVisitor(SunflowSceneBuilder pSceneBuilder) {
    sceneBuilder = pSceneBuilder;
  }

  @Override
  public void visitBefore(SceneElement pSceneElement) {
    if (pSceneElement instanceof PositionableSceneElement) {
      addPositionableSceneElement((PositionableSceneElement) pSceneElement);
    }
    else if (pSceneElement instanceof Material) {
      addMaterial(pSceneElement);
    }
    else {
      System.out.println("Unknown element " + pSceneElement);
    }
  }

  private void addMaterial(SceneElement pSceneElement) {
    if (pSceneElement instanceof Glass) {
      Glass glass = (Glass) pSceneElement;
      sceneBuilder.addShader()
          .withName(glass.getName())
          .withType(ShaderType.GLASS)
          .withEta(glass.getEta())
          .withColor(glass.getColor().getR(), glass.getColor().getG(), glass.getColor().getB())
          .withAbsorbtionColor(glass.getAbsorptionColor().getR(), glass.getAbsorptionColor().getG(), glass.getAbsorptionColor().getB());
    }
    else if (pSceneElement instanceof Shiny) {
      Shiny shiny = (Shiny) pSceneElement;
      sceneBuilder.addShader()
          .withName(shiny.getName())
          .withType(ShaderType.SHINY)
          .withDiff(shiny.getColor().getR(), shiny.getColor().getG(), shiny.getColor().getB())
          .withRefl(shiny.getRefl());
    }
    else {
      System.out.println("Unknown material " + pSceneElement);
    }
  }

  private void addPositionableSceneElement(PositionableSceneElement pSceneElement) {
    Matrix4 matrix = calcTransform(pSceneElement);
    if (pSceneElement instanceof Sphere) {
      Sphere sphere = (Sphere) pSceneElement;
      if (useMatrix) {
        sceneBuilder.addSphere()
            .withShader(sphere.getMaterial())
            .withTransform()
            .withMatrix(matrix);
      }
      else {
        sceneBuilder.addSphere()
            .withShader(sphere.getMaterial())
            .withTransform()
            .withScaleX(sphere.getSize().getX())
            .withScaleY(sphere.getSize().getY())
            .withScaleZ(sphere.getSize().getZ())
            .withTranslateX(sphere.getPosition().getX())
            .withTranslateY(sphere.getPosition().getY())
            .withTranslateZ(sphere.getPosition().getZ());
      }
    }
    else if (pSceneElement instanceof Cylinder) {
      Cylinder cylinder = (Cylinder) pSceneElement;
      if (useMatrix) {
        sceneBuilder.addCylinder()
            .withShader(cylinder.getMaterial())
            .withTransform()
            .withMatrix(matrix);
      }
      else {
        sceneBuilder.addCylinder()
            .withShader(cylinder.getMaterial())
            .withTransform()
            .withScaleX(cylinder.getSize().getX())
            .withScaleY(cylinder.getSize().getY())
            .withScaleZ(cylinder.getSize().getZ())
            .withTranslateX(cylinder.getPosition().getX())
            .withTranslateY(cylinder.getPosition().getY())
            .withTranslateZ(cylinder.getPosition().getZ())
            .withRotateX(cylinder.getOrientation().getAlpha())
            .withRotateY(cylinder.getOrientation().getBeta())
            .withRotateZ(cylinder.getOrientation().getGamma());
      }
    }
    else if (pSceneElement instanceof Box) {
      Box box = (Box) pSceneElement;
      if (useMatrix) {
        sceneBuilder.addBox()
            .withShader(box.getMaterial())
            .withTransform()
            .withMatrix(matrix);
      }
      else {
        sceneBuilder.addBox()
            .withShader(box.getMaterial())
            .withTransform()
            .withScaleX(box.getSize().getX())
            .withScaleY(box.getSize().getY())
            .withScaleZ(box.getSize().getZ())
            .withTranslateX(box.getPosition().getX())
            .withTranslateY(box.getPosition().getY())
            .withTranslateZ(box.getPosition().getZ())
            .withRotateX(box.getOrientation().getAlpha())
            .withRotateY(box.getOrientation().getBeta())
            .withRotateZ(box.getOrientation().getGamma());
      }
    }
    else if (pSceneElement instanceof Mesh) {
      Mesh mesh = (Mesh) pSceneElement;
      if (useMatrix) {
        sceneBuilder.addMesh()
            .withShader(mesh.getMaterial())
            .withMesh(mesh)
            .withTransform()
            .withMatrix(matrix);
      }
      else {
        sceneBuilder.addMesh()
            .withShader(mesh.getMaterial())
            .withMesh(mesh)
            .withTransform()
            .withScaleX(mesh.getSize().getX())
            .withScaleY(mesh.getSize().getY())
            .withScaleZ(mesh.getSize().getZ())
            .withTranslateX(mesh.getPosition().getX())
            .withTranslateY(mesh.getPosition().getY())
            .withTranslateZ(mesh.getPosition().getZ());
      }
    }
    else if (pSceneElement instanceof PointLight) {
      PointLight light = (PointLight) pSceneElement;
      sceneBuilder.addLight()
          .withType(LightType.POINT)
          .withColor(light.getColor().getR(), light.getColor().getG(), light.getColor().getB())
          .withP(light.getPosition().getX(), light.getPosition().getY(), light.getPosition().getZ())
          .withPower(light.getIntensity() * 2000000.0);
    }
    else if (pSceneElement instanceof SkyLight) {
      SkyLight light = (SkyLight) pSceneElement;
      sceneBuilder.addSkyLight()
          .withUp(light.getUp().getX(), light.getUp().getY(), light.getUp().getZ())
          .withEast(light.getEast().getX(), light.getEast().getY(), light.getEast().getZ())
          .withSundir(light.getSundir().getX(), light.getSundir().getY(), light.getSundir().getZ())
          .withTurbidity(light.getTurbidity())
          .withSamples(light.getSamples());
    }
    else if (pSceneElement instanceof Camera) {
      Camera camera = (Camera) pSceneElement;
      sceneBuilder
          .openCamera()
          .withType(CameraType.PINHOLE)
          .withEye(camera.getEye().getX(), camera.getEye().getY(), camera.getEye().getZ())
          .withTarget(camera.getTarget().getX(), camera.getTarget().getY(), camera.getTarget().getZ())
          .withUp(camera.getUp().getX(), camera.getUp().getY(), camera.getUp().getZ())
          .withFov(camera.getFov())
          .withAspect(camera.getAspect());
    }
    else if (pSceneElement instanceof Scene || pSceneElement instanceof MaterialGroup) {

    }
    else {
      System.out.println("Unknown positionable element " + pSceneElement);
    }
  }

  private Matrix4 calcTransform(PositionableSceneElement pElement) {
    Matrix4 transform = Matrix4.IDENTITY;
    List<Matrix4> transforms = new ArrayList<Matrix4>();

    PositionableSceneElement element = pElement;
    while (element != null) {
      transforms.add(Matrix4.scale(element.getSize().getX(), element.getSize().getY(), element.getSize().getZ()));
      if (MathLib.fabs(element.getOrientation().getAlpha()) > MathLib.EPSILON) {
        transforms.add(Matrix4.rotateX((float) Math.toRadians(element.getOrientation().getAlpha())));
      }
      if (MathLib.fabs(element.getOrientation().getBeta()) > MathLib.EPSILON) {
        transforms.add(Matrix4.rotateY((float) Math.toRadians(element.getOrientation().getBeta())));
      }
      if (MathLib.fabs(element.getOrientation().getGamma()) > MathLib.EPSILON) {
        transforms.add(Matrix4.rotateZ((float) Math.toRadians(element.getOrientation().getGamma())));
      }
      transforms.add(Matrix4.translation(element.getPosition().getX(), element.getPosition().getY(), element.getPosition().getZ()));
      element = element.getParent();
    }
    for (int i = transforms.size() - 1; i >= 0; i--) {
      transform = transform.multiply(transforms.get(i));
    }
    return transform;
  }

  @Override
  public void visitAfter(SceneElement pSceneElement) {
    // EMPTY
  }

}
