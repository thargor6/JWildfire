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
package org.jwildfire.create.tina.render.filter;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.create.eden.base.Face3i;
import org.jwildfire.create.eden.base.Point3f;
import org.jwildfire.create.eden.export.SunflowExporter;
import org.jwildfire.create.eden.scene.Scene;
import org.jwildfire.create.eden.scene.camera.Camera;
import org.jwildfire.create.eden.scene.light.SkyLight;
import org.jwildfire.create.eden.scene.primitive.Mesh;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.render.FilterHolder;
import org.jwildfire.image.SimpleImage;
import org.sunflow.SunflowAPI;

public class FilterKernelVisualisation3dRenderer extends FilterHolder implements FilterKernelVisualisationRenderer {
  private static final Map<String, SimpleImage> cache = new HashMap<String, SimpleImage>();

  public FilterKernelVisualisation3dRenderer(Flame pFlame) {
    super(pFlame);
  }

  private static final Color emptyFilterColor = new Color(160, 160, 160);

  public SimpleImage createKernelVisualisation(int pWidth, int pHeight) {
    String key = makeKey(pWidth, pHeight);
    SimpleImage img = cache.get(key);
    if (img == null) {
      img = new SimpleImage(pWidth, pHeight);
      if (noiseFilterSize > 0) {
        Scene scene = createScene(pWidth, pHeight);
        double sunflowScale = 10.0;
        double sunflowZScale = 0.6;
        int rectCount = noiseFilterSize;
        double dx = (double) pWidth / (double) rectCount;
        double dy = (double) pHeight / (double) rectCount;
        double w2 = (double) pWidth / 2.0;
        double h2 = (double) pHeight / 2.0;
        double xOff = 0.0, yOff = 0.0;
        for (int i = 0; i < noiseFilterSize; i++) {
          xOff = 0;
          for (int j = 0; j < noiseFilterSize; j++) {
            double fValue = filter[i][j];
            Color rectColor;
            if (fValue >= 0) {
              int fValueClr = Tools.FTOI(255.0 * fValue);
              if (fValueClr > 255) {
                fValueClr = 255;
              }
              rectColor = new Color(fValueClr, fValueClr, fValueClr);
            }
            else {
              int fValueClr = Tools.FTOI(-255.0 * (fValue - 0.5));
              if (fValueClr > 255) {
                fValueClr = 255;
              }
              rectColor = new Color(fValueClr, 0, 0);
            }
            addCube(scene, sunflowScale * (xOff - w2) / (double) pWidth, sunflowScale * (yOff - h2) / (double) pHeight, sunflowScale * dx / (double) pWidth, sunflowScale * dy / (double) pHeight, sunflowScale * fValue * sunflowZScale, rectColor);
            xOff += dx;
          }
          yOff += dy;
        }

        try {
          SunflowAPI api = createSunflowRenderer(scene);
          SunFlowImagePanel imagePanel = new SunFlowImagePanel();
          imagePanel.setBounds(0, 0, pWidth, pHeight);
          api.render(SunflowAPI.DEFAULT_OPTIONS, imagePanel);
          img.setBufferedImage(imagePanel.getImage(), imagePanel.getWidth(), imagePanel.getHeight());
        }
        catch (Exception e) {
          e.printStackTrace();
          img.fillBackground(emptyFilterColor.getRed(), emptyFilterColor.getGreen(), emptyFilterColor.getBlue());
        }
      }
      else {
        img.fillBackground(emptyFilterColor.getRed(), emptyFilterColor.getGreen(), emptyFilterColor.getBlue());
      }
      cache.put(key, img);
    }
    return img;
  }

  private String makeKey(int pWidth, int pHeight) {
    return filterKernel.getClass().getName() + "#" + noiseFilterSize + "#" + pWidth + "#" + pHeight;
  }

  private SunflowAPI createSunflowRenderer(Scene scene) throws IOException, Exception {
    String sceneTxt = new SunflowExporter().exportScene(scene);
    String filename = createTmpFilename();
    Tools.writeUTF8Textfile(filename, sceneTxt);
    String template = "import org.sunflow.core.*;\nimport org.sunflow.core.accel.*;\nimport org.sunflow.core.camera.*;\nimport org.sunflow.core.primitive.*;\nimport org.sunflow.core.shader.*;\nimport org.sunflow.image.Color;\nimport org.sunflow.math.*;\n\npublic void build() {\n  include(\"" + filename.replace("\\", "\\\\") + "\");\n}\n";
    SunflowAPI api = SunflowAPI.compile(template);

    api.build();

    api.parameter("sampler", "ipr");
    api.parameter("accel", "kdtree");
    api.options(SunflowAPI.DEFAULT_OPTIONS);
    return api;
  }

  private static File tmpFile = null;

  private static String createTmpFilename() throws IOException {
    if (tmpFile == null) {
      tmpFile = File.createTempFile("jwf", ".sc");
      tmpFile.deleteOnExit();
    }
    return tmpFile.getAbsolutePath();
  }

  private void addCube(Scene pScene, double pX, double pY, double pWidth, double pHeight, double pZ, Color pColor) {
    Mesh mesh = pScene.addMesh();
    List<Point3f> pPoints = mesh.getPoints();
    List<Face3i> pFaces = mesh.getFaces();
    double z0 = 0;
    int idx0 = pPoints.size();
    // top points
    pPoints.add(new Point3f(pX, pY, pZ));
    pPoints.add(new Point3f(pX, pY + pHeight, pZ));
    pPoints.add(new Point3f(pX + pWidth, pY + pHeight, pZ));
    pPoints.add(new Point3f(pX + pWidth, pY, pZ));
    // top 0 1 3/3 1 2
    pFaces.add(new Face3i(idx0, idx0 + 1, idx0 + 3));
    pFaces.add(new Face3i(idx0 + 3, idx0 + 1, idx0 + 2));
    // bottom points
    pPoints.add(new Point3f(pX, pY, z0));
    pPoints.add(new Point3f(pX, pY + pHeight, z0));
    pPoints.add(new Point3f(pX + pWidth, pY + pHeight, z0));
    pPoints.add(new Point3f(pX + pWidth, pY, z0));
    // front 1 5 2/2 5 6 
    pFaces.add(new Face3i(idx0 + 1, idx0 + 5, idx0 + 2));
    pFaces.add(new Face3i(idx0 + 2, idx0 + 5, idx0 + 6));
    // right 2 6 3/3 6 7 
    pFaces.add(new Face3i(idx0 + 2, idx0 + 6, idx0 + 3));
    pFaces.add(new Face3i(idx0 + 3, idx0 + 6, idx0 + 7));
    // back 3 7 0/0 7 4 
    pFaces.add(new Face3i(idx0 + 3, idx0 + 7, idx0));
    pFaces.add(new Face3i(idx0, idx0 + 7, idx0 + 4));
    // left 0 4 1/1 4 5 
    pFaces.add(new Face3i(idx0, idx0 + 4, idx0 + 1));
    pFaces.add(new Face3i(idx0 + 1, idx0 + 4, idx0 + 5));
  }

  private Scene createScene(int pWidth, int pHeight) {
    Scene scene = new Scene();
    SkyLight light = scene.addSkyLight();
    light.setUp(0.0, 0.0, 1.0);
    light.setEast(0.0, 1.0, 0.0);
    light.setSundir(1.0, -1.0, 0.31);
    light.setTurbidity(1.8);
    light.setSamples(16);

    Camera camera = scene.getCamera();
    camera.setEye(12.0, 11.0, 15.0);
    camera.setTarget(0.0, 0.0, 2.0);
    camera.setUp(0.0, 0.0, 1.0);
    camera.setFov(32.0);
    camera.setAspect(1.0);

    scene.setImageWidth(pWidth);
    scene.setImageHeight(pHeight);
    scene.getCamera().setAspect((double) pWidth / (double) pHeight);

    return scene;
  }

}
