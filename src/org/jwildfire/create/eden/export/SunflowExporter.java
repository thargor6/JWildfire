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

import org.jwildfire.create.eden.scene.Scene;
import org.jwildfire.create.eden.sunflow.SunflowSceneBuilder;
import org.jwildfire.create.eden.sunflow.base.ImageFilter;

public class SunflowExporter implements SceneExporter<String> {

  @Override
  public String exportScene(Scene pScene) {
    SunflowSceneBuilder sceneBuilder = new SunflowSceneBuilder();
    sceneBuilder
        .openImage()
        .withAa(1, 1)
        .withFilter(ImageFilter.MITCHELL)
        .withSamples(8)
        .withResolution(pScene.getImageWidth(), pScene.getImageHeight())
        // .close()
        ;
    pScene.accept(new AddSceneObjectsVisitor(sceneBuilder));
    return sceneBuilder.getProduct();
  }

  //        .addShader()
  //        .withName("ground")
  //        .withType(ShaderType.DIFFUSE)
  //        //.withTexture("...")
  //        .withDiff(0.7, 0.5, 0.5)
  //        .close()

  //        .addMesh()
  //        .withName("Plane")
  //        .withShader("ground")
  //        .withMesh(makeSurface(surfWidth, surfHeight, surfDepth))
  //        .close();

  //  private Mesh makeSurface(double pWidth, double pHeight, double pDepth) {
  //    double w2 = pWidth / 2.0;
  //    double h2 = pHeight / 2.0;
  //    double z0 = pDepth;
  //    double z1 = 2.0 * pDepth;
  //    List<Point3f> points = new ArrayList<Point3f>();
  //    points.add(new Point3f(w2, h2, z0));
  //    points.add(new Point3f(w2, -h2, z0));
  //    points.add(new Point3f(-w2, -h2, z0));
  //    points.add(new Point3f(-w2, h2, z0));
  //    points.add(new Point3f(-w2, h2, z1));
  //    points.add(new Point3f(-w2, -h2, z1));
  //    points.add(new Point3f(w2, -h2, z1));
  //    points.add(new Point3f(w2, h2, z1));
  //
  //    List<Point2f> uv = new ArrayList<Point2f>();
  //    uv.add(new Point2f(1, 1));
  //    uv.add(new Point2f(1, 0));
  //    uv.add(new Point2f(0, 0));
  //    uv.add(new Point2f(0, 1));
  //    uv.add(new Point2f(0, 1));
  //    uv.add(new Point2f(0, 0));
  //    uv.add(new Point2f(1, 0));
  //    uv.add(new Point2f(1, 1));
  //
  //    List<Face> faces = new ArrayList<Face>();
  //    faces.add(new Face(0, 3, 2));
  //    faces.add(new Face(0, 2, 1));
  //    faces.add(new Face(2, 3, 4));
  //    faces.add(new Face(2, 4, 5));
  //    faces.add(new Face(3, 0, 7));
  //    faces.add(new Face(3, 7, 4));
  //    faces.add(new Face(0, 1, 6));
  //    faces.add(new Face(0, 6, 7));
  //    faces.add(new Face(1, 2, 5));
  //    faces.add(new Face(1, 5, 6));
  //    faces.add(new Face(5, 4, 7));
  //    faces.add(new Face(5, 7, 6));
  //
  //    Mesh mesh = new Mesh(points, faces);
  //    mesh.setTextureCoords(uv);
  //    return mesh;
  //  }
}
