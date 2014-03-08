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
package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sin;

import org.jwildfire.base.mathlib.MathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Stereo3dEye;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;

public class FlameRendererView {
  protected final Flame flame;
  protected double cameraMatrix[][] = new double[3][3];
  protected double camDOF_10;
  protected boolean useDOF;
  protected boolean legacyDOF;
  protected double cosa;
  protected double sina;
  double camX0, camX1, camY0, camY1;
  double camW, camH;
  protected double rcX;
  protected double rcY;
  double bws;
  double bhs;
  protected XYZPoint camPoint = new XYZPoint();
  // 3D stuff
  protected boolean doProject3D = false;
  private final AbstractRandomGenerator randGen;
  private final int borderWidth;
  private final int maxBorderWidth;
  private final int imageWidth;
  private final int imageHeight;
  private final int rasterWidth;
  private final int rasterHeight;
  protected final Stereo3dEye eye;

  public FlameRendererView(Stereo3dEye pEye, Flame pFlame, AbstractRandomGenerator pRandGen, int pBorderWidth, int pMaxBorderWidth, int pImageWidth, int pImageHeight, int pRasterWidth, int pRasterHeight) {
    flame = pFlame;
    randGen = pRandGen;
    borderWidth = pBorderWidth;
    maxBorderWidth = pMaxBorderWidth;
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    eye = pEye;
    init3D();
    initView();
  }

  protected void init3D() {
    double yaw = -flame.getCamYaw() * M_PI / 180.0;
    double pitch = flame.getCamPitch() * M_PI / 180.0;
    cameraMatrix[0][0] = cos(yaw);
    cameraMatrix[1][0] = -sin(yaw);
    cameraMatrix[2][0] = 0;
    cameraMatrix[0][1] = cos(pitch) * sin(yaw);
    cameraMatrix[1][1] = cos(pitch) * cos(yaw);
    cameraMatrix[2][1] = -sin(pitch);
    cameraMatrix[0][2] = sin(pitch) * sin(yaw);
    cameraMatrix[1][2] = sin(pitch) * cos(yaw);
    cameraMatrix[2][2] = cos(pitch);
    useDOF = fabs(flame.getCamDOF()) > MathLib.EPSILON;
    doProject3D = fabs(flame.getCamYaw()) > EPSILON || fabs(flame.getCamPitch()) > EPSILON || fabs(flame.getCamPerspective()) > EPSILON || useDOF || fabs(flame.getDimishZ()) > EPSILON;
    legacyDOF = !flame.isNewCamDOF();
    camDOF_10 = 0.1 * flame.getCamDOF();
  }

  public void initView() {
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double corner_x = flame.getCentreX() - (double) imageWidth / pixelsPerUnit / 2.0;
    double corner_y = flame.getCentreY() - (double) imageHeight / pixelsPerUnit / 2.0;
    double t0 = borderWidth / pixelsPerUnit;
    double t1 = borderWidth / pixelsPerUnit;
    double t2 = (2 * maxBorderWidth - borderWidth) / pixelsPerUnit;
    double t3 = (2 * maxBorderWidth - borderWidth) / pixelsPerUnit;

    camX0 = corner_x - t0;
    camY0 = corner_y - t1;
    camX1 = corner_x + (double) imageWidth / pixelsPerUnit + t2;
    camY1 = corner_y + (double) imageHeight / pixelsPerUnit + t3;

    camW = camX1 - camX0;
    double Xsize, Ysize;
    if (fabs(camW) > 0.01)
      Xsize = 1.0 / camW;
    else
      Xsize = 1.0;
    camH = camY1 - camY0;
    if (fabs(camH) > 0.01)
      Ysize = 1.0 / camH;
    else
      Ysize = 1;
    bws = (rasterWidth - 0.5) * Xsize;
    bhs = (rasterHeight - 0.5) * Ysize;

    cosa = cos(-M_PI * (flame.getCamRoll()) / 180.0);
    sina = sin(-M_PI * (flame.getCamRoll()) / 180.0);
    rcX = flame.getCentreX() * (1 - cosa) - flame.getCentreY() * sina - camX0;
    rcY = flame.getCentreY() * (1 - cosa) + flame.getCentreX() * sina - camY0;
  }

  public boolean project(XYZPoint pPoint, XYZProjectedPoint pProjectedPoint) {
    if (doProject3D) {
      applyCameraMatrix(pPoint);
      double zr = 1.0 - flame.getCamPerspective() * camPoint.z;
      if (flame.getDimishZ() > EPSILON) {
        double zdist = (flame.getCamZ() - camPoint.z);
        if (zdist > 0.0) {
          pProjectedPoint.intensity = exp(-zdist * zdist * flame.getDimishZ());
        }
        else {
          pProjectedPoint.intensity = 1.0;
        }
      }
      else {
        pProjectedPoint.intensity = 1.0;
      }

      if (useDOF) {
        if (legacyDOF) {
          double zdist = (flame.getCamZ() - camPoint.z);
          if (zdist > 0.0) {
            double dr = randGen.random() * camDOF_10 * zdist;
            double a = 2.0 * M_PI * randGen.random();
            double dsina = sin(a);
            double dcosa = cos(a);
            pPoint.x = (camPoint.x + dr * dcosa) / zr;
            pPoint.y = (camPoint.y + dr * dsina) / zr;
          }
          else {
            pPoint.x = camPoint.x / zr;
            pPoint.y = camPoint.y / zr;
          }
        }
        else {
          double xdist = (camPoint.x - flame.getFocusX());
          double ydist = (camPoint.y - flame.getFocusY());
          double zdist = (camPoint.z - flame.getFocusZ());
          double dist = Math.pow(xdist * xdist + ydist * ydist + zdist * zdist, 1 / flame.getCamDOFExponent()) - flame.getCamDOFArea();
          if (dist > 0.00001) {
            double dr = randGen.random() * camDOF_10 * dist;
            double a = 2.0 * M_PI * randGen.random();
            double dsina = sin(a);
            double dcosa = cos(a);
            pPoint.x = (camPoint.x + dr * dcosa) / zr;
            pPoint.y = (camPoint.y + dr * dsina) / zr;
          }
          else {
            pPoint.x = camPoint.x / zr;
            pPoint.y = camPoint.y / zr;
          }
        }
      }
      else {
        pPoint.x = camPoint.x / zr;
        pPoint.y = camPoint.y / zr;
      }
    }
    else {
      pProjectedPoint.intensity = 1.0;
    }
    pProjectedPoint.x = pPoint.x * cosa + pPoint.y * sina + rcX;
    if ((pProjectedPoint.x < 0) || (pProjectedPoint.x > camW))
      return false;
    pProjectedPoint.y = pPoint.y * cosa - pPoint.x * sina + rcY;
    if ((pProjectedPoint.y < 0) || (pProjectedPoint.y > camH))
      return false;
    return true;
  }

  protected void applyCameraMatrix(XYZPoint pPoint) {
    camPoint.x = cameraMatrix[0][0] * pPoint.x + cameraMatrix[1][0] * pPoint.y /*+ cameraMatrix[2][0] * pPoint.z*/;
    camPoint.y = cameraMatrix[0][1] * pPoint.x + cameraMatrix[1][1] * pPoint.y + cameraMatrix[2][1] * pPoint.z;
    camPoint.z = cameraMatrix[0][2] * pPoint.x + cameraMatrix[1][2] * pPoint.y + cameraMatrix[2][2] * pPoint.z;
  }

}
