/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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
package org.jwildfire.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jwildfire.base.Tools;
import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.transform.Mesh3D;
import org.jwildfire.transform.Mesh3DTransformer.Rotate;


public class SunFlowWriter {

  private void addImage(StringBuilder pSB, Mesh3D pMesh3D) {
    pSB.append("  image {\n");
    pSB.append("    resolution " + pMesh3D.getImageWidth() + " " + pMesh3D.getImageHeight() + "\n");
    //    pSB.append("    aa 1 2\n");
    //    pSB.append("    samples 4\n");
    pSB.append("    aa 0 0\n");
    pSB.append("    samples 1\n");
    pSB.append("    filter gaussian\n");
    pSB.append("  }\n");
    pSB.append("\n");
  }

  private void addCamera(StringBuilder pSB, Mesh3D pMesh3D) {
    pSB.append("  camera {\n");
    pSB.append("    type pinhole\n");
    pSB.append("    eye    " + pMesh3D.getLastTransformation().getCamX() + " " + pMesh3D.getLastTransformation().getCamZ() + " " + pMesh3D.getLastTransformation().getCamY() + "\n");
    pSB.append("    target 0 0 0\n");
    pSB.append("    up 0 0 1\n");
    //    pSB.append("    fov 20\n");
    //    pSB.append("   aspect " + ((double) pMesh3D.getImageWidth() / (double) pMesh3D.getImageHeight()) + "\n");
    pSB.append("    fov 10.5\n");
    pSB.append("   aspect 1.25\n");
    pSB.append("   }\n");
    pSB.append("\n");
  }

  private void addLight(StringBuilder pSB, double pX, double pY, double pZ, int pRed, int pGreen, int pBlue) {
    if (pRed > 0 || pGreen > 0 || pBlue > 0) {
      pSB.append("  light {\n");
      pSB.append("    type point\n");
      pSB.append("    color { \"sRGB nonlinear\" " + ((double) pRed / 255.0) + " " + ((double) pGreen / 255.0) + " " + ((double) pBlue / 255.0) + " }\n");
      pSB.append("    power 5000000\n");
      pSB.append("   p " + pX + " " + pZ + " " + pY + "\n");
      pSB.append("  }\n");
      pSB.append("\n");
    }
  }

  private void addLights(StringBuilder pSB, Mesh3D pMesh3D) {
    pSB.append("  gi { type path samples 16 }\n");
    pSB.append("\n");
    addLight(pSB, pMesh3D.getLastTransformation().getLight1X(), pMesh3D.getLastTransformation().getLight1Y(), pMesh3D.getLastTransformation().getLight1Z(), pMesh3D.getLastTransformation().getLight1Color().getRed(), pMesh3D.getLastTransformation().getLight1Color().getGreen(), pMesh3D.getLastTransformation().getLight1Color().getBlue());
    addLight(pSB, pMesh3D.getLastTransformation().getLight2X(), pMesh3D.getLastTransformation().getLight2Y(), pMesh3D.getLastTransformation().getLight2Z(), pMesh3D.getLastTransformation().getLight2Color().getRed(), pMesh3D.getLastTransformation().getLight2Color().getGreen(), pMesh3D.getLastTransformation().getLight2Color().getBlue());
    addLight(pSB, pMesh3D.getLastTransformation().getLight3X(), pMesh3D.getLastTransformation().getLight3Y(), pMesh3D.getLastTransformation().getLight3Z(), pMesh3D.getLastTransformation().getLight3Color().getRed(), pMesh3D.getLastTransformation().getLight3Color().getGreen(), pMesh3D.getLastTransformation().getLight3Color().getBlue());
    addLight(pSB, pMesh3D.getLastTransformation().getLight4X(), pMesh3D.getLastTransformation().getLight4Y(), pMesh3D.getLastTransformation().getLight4Z(), pMesh3D.getLastTransformation().getLight4Color().getRed(), pMesh3D.getLastTransformation().getLight4Color().getGreen(), pMesh3D.getLastTransformation().getLight4Color().getBlue());
  }

  private void addShaders(StringBuilder pSB, Mesh3D pMesh3D, String pTexturename) {
    pSB.append("  shader {\n");
    pSB.append("    name \"shader01\"\n");
    pSB.append("    type phong\n");
    pSB.append("    texture " + pTexturename + "\n");
    pSB.append("    spec { \"sRGB nonlinear\" 1 1 1 } 100\n");
    pSB.append("      samples 4\n");
    pSB.append("  }\n");
    pSB.append("\n");
    pSB.append("  shader {\n");
    pSB.append("    name \"shader02\"\n");
    pSB.append("    type shiny\n");
    pSB.append("    texture " + pTexturename + "\n");
    pSB.append("    refl 0.0\n");
    pSB.append("  }\n");
    pSB.append("\n");
  }

  public void saveMesh(Mesh3D pMesh3D, String pAbsolutePath) throws Exception {
    // create texture
    String textureFilename;
    {
      File outputFile = new File(pAbsolutePath);
      String filename = outputFile.getName();
      int p = filename.lastIndexOf(".");
      if (p > 0 && p < filename.length() - 1) {
        textureFilename = filename.substring(0, p) + ".jpg";
      }
      else {
        textureFilename = filename + ".jpg";
      }
      new ImageWriter().saveAsJPEG(pMesh3D.getTexture(), new File(outputFile.getParent(), textureFilename).getAbsolutePath());
    }
    //
    StringBuilder sb = new StringBuilder();

    addImage(sb, pMesh3D);
    addCamera(sb, pMesh3D);
    addLights(sb, pMesh3D);
    addShaders(sb, pMesh3D, textureFilename);
    sb.append(" object {\n");
    sb.append("   shader \"shader01\"\n");
    sb.append("   transform {\n");
    sb.append("     scaleu " + pMesh3D.getLastTransformation().getZoom() + "\n");
    if (pMesh3D.getLastTransformation().getDoRotate() == Rotate.XY) {
      sb.append("     rotatex " + (90 - pMesh3D.getLastTransformation().getBeta()) + "\n");
      sb.append("     rotatey 0\n");
      sb.append("     rotatez " + (-pMesh3D.getLastTransformation().getAlpha()) + "\n");
    }
    else {
      throw new Exception("Only XY rotation is currently supported");
    }

    sb.append("     translate 0 0 0\n");
    sb.append("   }\n");
    sb.append("   type generic-mesh\n");
    sb.append("   name \"JWildfire.mesh.001\"\n");
    // points
    sb.append("   points ");
    sb.append(pMesh3D.getPCount());
    sb.append("\n");
    double x[] = pMesh3D.getX(), y[] = pMesh3D.getY(), z[] = pMesh3D.getZ();
    double xMin = 0.0, xMax = 0.0, yMin = 0.0, yMax = 0.0, xSize, ySize;
    double scl = 1;
    for (int i = 0; i < pMesh3D.getPCount(); i++) {
      if (x[i] < xMin) {
        xMin = x[i];
      }
      else if (x[i] > xMax) {
        xMax = x[i];
      }
      if (y[i] < yMin) {
        yMin = y[i];
      }
      else if (y[i] > yMax) {
        yMax = y[i];
      }
      sb.append("  " + Tools.doubleToString(x[i] * scl) + " " + Tools.doubleToString(y[i] * scl) + " " + Tools.doubleToString(-z[i] * scl) + "\n");
    }
    xSize = xMax - xMin;
    if (xSize < MathLib.EPSILON) {
      xSize = MathLib.EPSILON;
    }
    ySize = yMax - yMin;
    if (ySize < MathLib.EPSILON) {
      ySize = MathLib.EPSILON;
    }

    // triangles
    sb.append("  triangles ");
    sb.append(pMesh3D.getFCount());
    sb.append("\n");
    int pp1[] = pMesh3D.getPP1(), pp2[] = pMesh3D.getPP2(), pp3[] = pMesh3D.getPP3();
    for (int i = 0; i < pMesh3D.getFCount(); i++) {
      sb.append("  " + pp1[i] + " " + pp2[i] + " " + pp3[i] + "\n");
    }
    // normals
    //    sb.append("   normals none\n");
    sb.append("  normals vertex\n");
    Map<Integer, AvgNormal> normals = new HashMap<Integer, AvgNormal>();
    for (int i = 0; i < pMesh3D.getFCount(); i++) {
      int p1 = pp1[i], p2 = pp2[i], p3 = pp3[i];
      double x1 = x[p1];
      double y1 = y[p1];
      double z1 = z[p1];
      double x2 = x[p2];
      double y2 = y[p2];
      double z2 = z[p2];
      double x3 = x[p3];
      double y3 = y[p3];
      double z3 = z[p3];
      double vax = x2 - x1;
      double vay = y2 - y1;
      double vaz = z2 - z1;
      double vbx = x3 - x1;
      double vby = y3 - y1;
      double vbz = z3 - z1;
      double nfx = vay * vbz - vaz * vby;
      double nfy = vaz * vbx - vax * vbz;
      double nfz = vax * vby - vay * vbx;
      double rr = Math.sqrt(nfx * nfx + nfy * nfy + nfz * nfz);
      if (Math.abs(rr) <= MathLib.EPSILON) {
        nfx = nfy = 0.0;
        nfz = -1.0;
      }
      else {
        nfx = 0.0 - nfx / rr;
        nfy = 0.0 - nfy / rr;
        nfz = 0.0 - nfz / rr;
      }
      AvgNormal n;

      n = normals.get(p1);
      if (n == null) {
        n = new AvgNormal();
        normals.put(p1, n);
      }
      n.sumNX += nfx;
      n.sumNY += nfy;
      n.sumNZ += nfz;
      n.count++;

      n = normals.get(p2);
      if (n == null) {
        n = new AvgNormal();
        normals.put(p2, n);
      }
      n.sumNX += nfx;
      n.sumNY += nfy;
      n.sumNZ += nfz;
      n.count++;

      n = normals.get(p3);
      if (n == null) {
        n = new AvgNormal();
        normals.put(p3, n);
      }
      n.sumNX += nfx;
      n.sumNY += nfy;
      n.sumNZ += nfz;
      n.count++;

    }
    for (int i = 0; i < pMesh3D.getPCount(); i++) {
      AvgNormal n = normals.get(i);
      if (n == null) {
        n = new AvgNormal();
        n.count++;
      }
      sb.append("    " + Tools.doubleToString(n.sumNX / (double) n.count) + " " + Tools.doubleToString(n.sumNY / (double) n.count) + " " + Tools.doubleToString(n.sumNZ / (double) n.count) + "\n");
    }

    sb.append("   uvs vertex\n");
    for (int i = 0; i < pMesh3D.getPCount(); i++) {
      //      double x1 = x[i];
      //      double y1 = y[i];
      //      double xx = (x1 - xMin) / xSize;
      //      double yy = (y1 - yMin) / ySize;
      //      sb.append("  " + xx + " " + yy + "\n");
      double u = pMesh3D.getU()[i];
      double v = pMesh3D.getV()[i];
      sb.append("  " + u + " " + v + "\n");
    }
    sb.append("   }\n");
    Tools.writeUTF8Textfile(pAbsolutePath, sb.toString());
  }

  private static class AvgNormal {
    public double sumNX, sumNY, sumNZ;
    public int count;
  }
}
