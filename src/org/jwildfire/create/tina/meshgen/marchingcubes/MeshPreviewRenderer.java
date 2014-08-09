/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2013 Andreas Maschke

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
package org.jwildfire.create.tina.meshgen.marchingcubes;

import java.awt.Color;

import org.jwildfire.image.Pixel;
import org.jwildfire.image.SimpleImage;
import org.jwildfire.transform.Mesh3D;
import org.jwildfire.transform.Mesh3DTransformer.Light;
import org.jwildfire.transform.PerspectiveTransformer;

public class MeshPreviewRenderer {

  public static SimpleImage renderMesh(Mesh pMesh, int pWidth, int pHeight, double pPositionX, double pPositionY,
      double pSize, double pScaleZ, double pRotateAlpha, double pRotateBeta) {
    if (pMesh.getFaces().size() == 0)
      return new SimpleImage(pWidth, pHeight);

    Mesh3D mesh3d = new Mesh3D();
    mesh3d.setImageWidth(pWidth);
    mesh3d.setImageHeight(pHeight);

    int pCount = pMesh.getVertices().size();
    mesh3d.setPCount(pCount);
    double[] x = new double[pCount];
    double[] y = new double[pCount];
    double[] z = new double[pCount];
    double cx = pWidth / 2;
    double cy = pHeight / 2;
    int idx = 0;
    double xmin = pMesh.getPMin().x;
    double ymin = pMesh.getPMin().y;
    double zmin = pMesh.getPMin().z;
    double xmax = pMesh.getPMax().x;
    double ymax = pMesh.getPMax().y;
    double zmax = pMesh.getPMax().z;
    double size = (xmax - xmin + ymax - ymin + zmax - zmin) / 3.0;

    for (Point point : pMesh.getVertices()) {
      double currX = (point.x - xmin) / size * pWidth - cx;
      double currY = (point.y - ymin) / size * pHeight - cy;
      double currZ = pScaleZ * (point.z - zmin) / size * pHeight;
      x[idx] = currX;
      y[idx] = currY;
      z[idx] = currZ;
      idx++;
    }
    mesh3d.setX(x);
    mesh3d.setY(y);
    mesh3d.setZ(z);

    int fCount = pMesh.getFaces().size();
    mesh3d.setFCount(fCount);
    mesh3d.setU(null);
    mesh3d.setV(null);
    mesh3d.setTexture(null);

    int[] pp1 = new int[fCount];
    int[] pp2 = new int[fCount];
    int[] pp3 = new int[fCount];
    idx = 0;
    for (Face face : pMesh.getFaces()) {
      pp1[idx] = face.a;
      pp2[idx] = face.b;
      pp3[idx] = face.c;
      idx++;
    }
    mesh3d.setPP1(pp1);
    mesh3d.setPP2(pp2);
    mesh3d.setPP3(pp3);

    int color[] = new int[fCount];
    Pixel toolPixel = new Pixel();
    toolPixel.setRGB(225, 185, 160);
    int argb = toolPixel.getARGBValue();
    for (int c = 0; c < fCount; c++) {
      color[c] = argb;
    }
    mesh3d.setColor(color);

    PerspectiveTransformer trans = new PerspectiveTransformer();
    trans.setInputMesh3D(mesh3d);
    trans.setDoCam(true);
    trans.setCentreX(trans.getCentreX() * (1.0 + pPositionX));
    trans.setCentreY(trans.getCentreY() * (1.0 - pPositionY));
    trans.setZoom(pSize * 0.5);
    trans.setLight(Light.NORMAL);
    trans.setSmoothing(0);
    trans.setAmbient(0.4);
    trans.setDoCam(false);
    trans.setAlpha(pRotateAlpha);
    trans.setBeta(pRotateBeta);
    trans.setLight1Color(new Color(255, 245, 180));
    SimpleImage out = new SimpleImage(pWidth, pHeight);
    trans.transformImage(out);
    return out;
  }
}
