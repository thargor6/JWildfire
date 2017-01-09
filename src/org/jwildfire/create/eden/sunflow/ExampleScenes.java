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

import org.jwildfire.create.eden.sunflow.base.CameraType;
import org.jwildfire.create.eden.sunflow.base.ImageFilter;
import org.jwildfire.create.eden.sunflow.base.ShaderType;
import org.jwildfire.create.tina.meshgen.marchingcubes.Face;
import org.jwildfire.create.tina.meshgen.marchingcubes.Mesh;
import org.jwildfire.create.tina.meshgen.marchingcubes.Point3f;

public class ExampleScenes {

  public static final String SHADER_SHINY = "shader_shiny";

  public static SunflowSceneBuilder getExampleScene1() {
    List<Point3f> points = new ArrayList<Point3f>();
    points.add(new Point3f(7.1, 7.1, -1.0));
    points.add(new Point3f(7.1, -7.1, -1.0));
    points.add(new Point3f(-7.1, -7.1, -1.0));
    points.add(new Point3f(-7.1, 7.1, -1.0));
    points.add(new Point3f(-7.1, 7.1, -1.61));
    points.add(new Point3f(-7.1, -7.1, -1.61));
    points.add(new Point3f(7.1, -7.1, -1.61));
    points.add(new Point3f(7.1, 7.1, -1.61));

    List<Face> faces = new ArrayList<Face>();
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

    SunflowSceneBuilder scene = new SunflowSceneBuilder();
    scene
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
        .withFov(60)
        .withAspect(1)
        .close()

        .addSkyLight()
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
        .withName(SHADER_SHINY)
        .withType(ShaderType.SHINY)
        .withDiff(0.490196, 0.909804, 0)
        .withRefl(0.1)
        .close()

        .addLegacyMesh()
        .withName("Plane")
        .withShader("ground")
        .withMesh(mesh)
        // .close()
        ;
    return scene;
  }

  public static void main(String args[]) throws Exception {
    //    long t0 = System.currentTimeMillis();
    SunflowSceneBuilder scene = getExampleScene1();
    System.out.println(scene.getProduct());
    //    long t1 = System.currentTimeMillis();
    //    System.out.println("DONE (" + (t1 - t0) / 1000.0 + "s)");
  }

}
