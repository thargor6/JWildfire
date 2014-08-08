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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.meshgen.SimpleWavefrontObjLoader;
import org.jwildfire.create.tina.meshgen.marchingcubes.Face;
import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point;
import org.jwildfire.create.tina.meshgen.sunflow.base.CameraType;
import org.jwildfire.create.tina.meshgen.sunflow.base.ImageFilter;
import org.jwildfire.create.tina.meshgen.sunflow.base.PartBuilder;
import org.jwildfire.create.tina.meshgen.sunflow.base.ShaderType;

public class SceneBuilder implements PartBuilder {
  private final ImageBuilder imageBuilder = new ImageBuilder(this);
  private final CameraBuilder cameraBuilder = new CameraBuilder(this);
  private final List<LightBuilder> lightBuilders = new ArrayList<LightBuilder>();
  private final List<SunSkyLightBuilder> sunSkyLightBuilders = new ArrayList<SunSkyLightBuilder>();
  private final List<ShaderBuilder> shaderBuilders = new ArrayList<ShaderBuilder>();
  private final List<MeshBuilder> meshBuilders = new ArrayList<MeshBuilder>();

  public String getResult() {
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

  public static String buildTestScene1() {
    List<Point> points = new ArrayList<Point>();
    points.add(new Point(3.1, 3.1, 0));
    points.add(new Point(3.1, -3.1, 0));
    points.add(new Point(-3.1, -3.1, 0));
    points.add(new Point(-3.1, 3.1, 0));
    points.add(new Point(-3.1, 3.1, -0.61));
    points.add(new Point(-3.1, -3.1, -0.61));
    points.add(new Point(3.1, -3.1, -0.61));
    points.add(new Point(3.1, 3.1, -0.61));

    Set<Face> faces = new HashSet<Face>();
    faces.add(new Face(0, 3, 2));
    faces.add(new Face(0, 2, 1));
    faces.add(new Face(2, 3, 4));
    faces.add(new Face(2, 4, 5));
    faces.add(new Face(3, 0, 7));
    faces.add(new Face(3, 7, 4));
    faces.add(new Face(0, 1, 6));
    faces.add(new Face(0, 6, 7));
    faces.add(new Face(1, 2, 5));
    faces.add(new Face(1, 5, 6));
    faces.add(new Face(5, 4, 7));
    faces.add(new Face(5, 7, 6));

    Mesh mesh = new Mesh(points, faces);

    SceneBuilder scene = new SceneBuilder();
    return scene
        .openImage()
        .withAa(1, 1)
        .withFilter(ImageFilter.GAUSSIAN)
        .withSamples(4)
        .withResolution(640, 480)
        .close()

        .openCamera()
        .withType(CameraType.PINHOLE)
        .withEye(3.27743673325, -9.07978439331, 9.93055152893)
        .withTarget(0, 0, 0)
        .withUp(0, 0, 1)
        .withFov(40)
        .withAspect(1)
        .close()

        .addSunSkyLight()
        .withUp(0, 0, 1)
        .withEast(0, 1, 0)
        .withSundir(1, -1, 0.31)
        .withTurbidity(2)
        .withSamples(16)
        .close()

        .addShader()
        .withName("ground")
        .withType(ShaderType.DIFFUSE)
        .withDiff(0.7, 0.5, 0.5)
        .close()

        .addShader()
        .withName("shader01")
        .withType(ShaderType.SHINY)
        .withDiff(0.490196, 0.909804, 0)
        .withRefl(0.1)
        .close()

        .addMesh()
        .withName("Plane")
        .withShader("ground")
        .withMesh(mesh)
        .close()

        .getResult();
  }

  public static String buildTestScene2() {
    List<Point> points = new ArrayList<Point>();
    points.add(new Point(3.1, 3.1, 0));
    points.add(new Point(3.1, -3.1, 0));
    points.add(new Point(-3.1, -3.1, 0));
    points.add(new Point(-3.1, 3.1, 0));
    points.add(new Point(-3.1, 3.1, -0.61));
    points.add(new Point(-3.1, -3.1, -0.61));
    points.add(new Point(3.1, -3.1, -0.61));
    points.add(new Point(3.1, 3.1, -0.61));

    Set<Face> faces = new HashSet<Face>();
    faces.add(new Face(0, 3, 2));
    faces.add(new Face(0, 2, 1));
    faces.add(new Face(2, 3, 4));
    faces.add(new Face(2, 4, 5));
    faces.add(new Face(3, 0, 7));
    faces.add(new Face(3, 7, 4));
    faces.add(new Face(0, 1, 6));
    faces.add(new Face(0, 6, 7));
    faces.add(new Face(1, 2, 5));
    faces.add(new Face(1, 5, 6));
    faces.add(new Face(5, 4, 7));
    faces.add(new Face(5, 7, 6));

    Mesh ground = new Mesh(points, faces);

    //    Mesh object = SimpleWavefrontObjLoader.readMesh("C:\\TMP\\wf\\_test.obj");
    Mesh object = SimpleWavefrontObjLoader.readMesh("C:\\TMP\\wf\\meshes\\flowerfield3.obj");

    SceneBuilder scene = new SceneBuilder();
    return scene
        .openImage()
        .withAa(1, 1)
        .withFilter(ImageFilter.GAUSSIAN)
        .withSamples(4)
        .withResolution(640, 480)
        .close()

        .openCamera()
        .withType(CameraType.PINHOLE)
        .withEye(3.27743673325, -9.07978439331, 9.93055152893)
        .withTarget(0, 0, 0)
        .withUp(0, 0, 1)
        .withFov(40)
        .withAspect(1)
        .close()

        .addSunSkyLight()
        .withUp(0, 0, 1)
        .withEast(0, 1, 0)
        .withSundir(1, -1, 0.31)
        .withTurbidity(2)
        .withSamples(16)
        .close()

        .addShader()
        .withName("ground")
        .withType(ShaderType.DIFFUSE)
        .withDiff(0.7, 0.5, 0.5)
        .close()

        .addShader()
        .withName("shader01")
        .withType(ShaderType.SHINY)
        .withDiff(0.490196, 0.909804, 0)
        .withRefl(0.1)
        .close()

        .addMesh()
        .withName("example_mesh")
        .withShader("shader01")
        .withMesh(object)
        .close()

        .addMesh()
        .withName("ground_mesh")
        .withShader("ground")
        .withMesh(ground)
        .close()

        .getResult();
  }

  public static void main(String args[]) throws Exception {
    long t0 = System.currentTimeMillis();
    String scene = buildTestScene2();
    //    System.out.println(scene);
    Tools.writeUTF8Textfile("C:\\TMP\\wf\\_test.sc", scene);
    long t1 = System.currentTimeMillis();
    System.out.println("DONE (" + (t1 - t0) / 1000.0 + "s)");
  }
}
