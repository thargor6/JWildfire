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
package org.jwildfire.create.tina.render;

import static org.jwildfire.base.mathlib.MathLib.EPSILON;
import static org.jwildfire.base.mathlib.MathLib.M_PI;
import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.exp;
import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.sin;

import org.jwildfire.base.mathlib.GfxMathLib;
import org.jwildfire.create.tina.base.Flame;
import org.jwildfire.create.tina.base.Stereo3dEye;
import org.jwildfire.create.tina.base.XYZPoint;
import org.jwildfire.create.tina.base.XYZProjectedPoint;
import org.jwildfire.create.tina.base.solidrender.ShadowType;
import org.jwildfire.create.tina.random.AbstractRandomGenerator;
import org.jwildfire.create.tina.render.dof.DOFBlurShape;
import org.jwildfire.create.tina.variation.FlameTransformationContext;

public class FlameRendererView {
  protected final Flame flame;
  protected double cameraMatrix[][] = new double[3][3];
  protected double camDOF_10;
  protected boolean useDOF;
  protected boolean solidFlame;
  protected boolean solidHardShadows;
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
  private final int imageWidth;
  private final int imageHeight;
  private final int rasterWidth;
  private final int rasterHeight;
  private final FlameTransformationContext flameTransformationContext;
  private final DOFBlurShape dofBlurShape;
  protected final Stereo3dEye eye;
  private double area, fade, areaMinusFade;
  private LightViewCalculator lightViewCalculator;
  private double segmentRenderingCamXModifier = 0.0;
  private double segmentRenderingCamYModifier = 0.0;

  public FlameRendererView(Stereo3dEye pEye, Flame pFlame, AbstractRandomGenerator pRandGen, int pBorderWidth, int pImageWidth, int pImageHeight, int pRasterWidth, int pRasterHeight, FlameTransformationContext pFlameTransformationContext, RenderInfo renderInfo) {
    flame = pFlame;
    randGen = pRandGen;
    borderWidth = pBorderWidth;
    imageWidth = pImageWidth;
    imageHeight = pImageHeight;
    rasterWidth = pRasterWidth;
    rasterHeight = pRasterHeight;
    eye = pEye;
    flameTransformationContext = pFlameTransformationContext;
    if(renderInfo!=null) {
      segmentRenderingCamXModifier = renderInfo.getSegmentRenderingCamXModifier();
      segmentRenderingCamYModifier = renderInfo.getSegmentRenderingCamYModifier();
    }
    init3D();
    initView();
    dofBlurShape = pFlame.getCamDOFShape().getDOFBlurShape();
    dofBlurShape.assignParams(pFlame);
    dofBlurShape.prepare(flameTransformationContext, randGen, camDOF_10);
  }

  protected void init3D() {
    {
      double yaw = -flame.getCamYaw() * M_PI / 180.0;
      double pitch = flame.getCamPitch() * M_PI / 180.0;
      double roll = flame.getCamRoll() * M_PI / 180.0;
      double bank = flame.getCamBank() * M_PI / 180.0;
      createProjectionMatrix(cameraMatrix, yaw, pitch, bank, roll);
    }
    useDOF = flame.isDOFActive();

    solidFlame = flame.getSolidRenderSettings().isSolidRenderingEnabled();
    if (solidFlame) {
      lightViewCalculator = new LightViewCalculator(flame);
      solidHardShadows = !ShadowType.OFF.equals(flame.getSolidRenderSettings().getShadowType());
    }
    else {
      lightViewCalculator = null;
      solidHardShadows = false;
    }

    doProject3D = flame.is3dProjectionRequired();
    legacyDOF = !flame.isNewCamDOF();
    camDOF_10 = 0.1 * flame.getCamDOF();
    area = flame.getCamDOFArea();
    fade = flame.getCamDOFArea() / 2.25;
    areaMinusFade = area - fade;
  }

  private void createProjectionMatrix(double m[][], double yaw, double pitch, double bank, double roll) {
    m[0][0] = -cos(pitch)*sin(roll)*sin(yaw) - (sin(pitch)*sin(bank)*sin(roll) - cos(bank)*cos(roll))*cos(yaw);
    m[1][0] = -cos(pitch)*cos(yaw)*sin(roll) + (sin(pitch)*sin(bank)*sin(roll) - cos(bank)*cos(roll))*sin(yaw);
    m[2][0] = cos(bank)*sin(pitch)*sin(roll) + cos(roll)*sin(bank);
    m[0][1] = cos(pitch)*cos(roll)*sin(yaw) + (cos(roll)*sin(pitch)*sin(bank) + cos(bank)*sin(roll))*cos(yaw);
    m[1][1] = cos(pitch)*cos(roll)*cos(yaw) - (cos(roll)*sin(pitch)*sin(bank) + cos(bank)*sin(roll))*sin(yaw);
    m[2][1] = -cos(bank)*cos(roll)*sin(pitch) + sin(bank)*sin(roll);
    m[0][2] = -cos(pitch)*cos(yaw)*sin(bank) + sin(pitch)*sin(yaw);
    m[1][2] = cos(pitch)*sin(bank)*sin(yaw) + cos(yaw)*sin(pitch);
    m[2][2] = cos(pitch)*cos(bank);
  }

  public void initView() {
    double pixelsPerUnit = flame.getPixelsPerUnit() * flame.getCamZoom();
    double corner_x = flame.getCentreX() - (double) imageWidth / pixelsPerUnit / 2.0;
    double corner_y = flame.getCentreY() - (double) imageHeight / pixelsPerUnit / 2.0;
    int oversample = flame.getSpatialOversampling();
    double border_size = (double)borderWidth / (double)(pixelsPerUnit);

    camX0 = corner_x - border_size;
    camY0 = corner_y - border_size;
    camX1 = corner_x + (double) imageWidth / pixelsPerUnit + border_size;
    camY1 = corner_y + (double) imageHeight / pixelsPerUnit + border_size;

    camW = camX1 - camX0;
    camH = camY1 - camY0;

    bws = rasterWidth/camW;
    bhs = rasterHeight/camH;

    if (!doProject3D) {
      cosa = cos(-M_PI * (flame.getCamRoll()) / 180.0);
      sina = sin(-M_PI * (flame.getCamRoll()) / 180.0);
      rcX = flame.getCentreX() * (1 - cosa) - flame.getCentreY() * sina - camX0;
      rcY = flame.getCentreY() * (1 - cosa) + flame.getCentreX() * sina - camY0;
    }
    else {
      cosa = 1.0;
      sina = 0.0;
      rcX = -camX0;
      rcY = -camY0;
    }

    rcX += segmentRenderingCamXModifier;
    rcY += segmentRenderingCamYModifier;
  }

  public boolean project(XYZPoint pPoint, XYZProjectedPoint pProjectedPoint) {
    if (doProject3D) {
      if (solidHardShadows) {
        lightViewCalculator.project(pPoint, pProjectedPoint);
      }

      applyCameraMatrix(pPoint);
      camPoint.x += flame.getCamPosX();
      camPoint.y += flame.getCamPosY();
      camPoint.z += flame.getCamPosZ();

      double zr = 1.0 - flame.getCamPerspective() * camPoint.z + flame.getCamPosZ();
      if (zr < EPSILON) {
        return false;
      }
      pProjectedPoint.z = camPoint.z;

      if (flame.getDimishZ() > EPSILON) {
        double zdist = (flame.getDimZDistance() - camPoint.z);
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
            pProjectedPoint.dofDist = zdist;
            if (solidFlame) {
              dofBlurShape.applyOnlyCamera(camPoint, pPoint, zdist, zr);
            }
            else {
              dofBlurShape.applyDOFAndCamera(camPoint, pPoint, zdist, zr);
            }
          }
          else {
            pProjectedPoint.dofDist = 0.0;
            dofBlurShape.applyOnlyCamera(camPoint, pPoint, zdist, zr);
          }
        }
        else {
          double xdist = (camPoint.x - flame.getFocusX());
          double ydist = (camPoint.y - flame.getFocusY());
          double zdist = (camPoint.z - flame.getFocusZ());

          double dist = Math.pow(xdist * xdist + ydist * ydist + zdist * zdist, 1.0 / flame.getCamDOFExponent());
          if (dist > area) {
            pProjectedPoint.dofDist = dist;
            if (solidFlame) {
              dofBlurShape.applyOnlyCamera(camPoint, pPoint, dist, zr);
            }
            else {
              dofBlurShape.applyDOFAndCamera(camPoint, pPoint, dist, zr);
            }
          }
          else if (dist > areaMinusFade) {
            /*
                        double scl = (dist - areaMinusFade) / fade;
                        double sclDist = dist * scl * scl;*/

            double scl = GfxMathLib.smootherstep(0.0, 1.0, (dist - areaMinusFade) / fade);
            double sclDist = scl * dist;

            pProjectedPoint.dofDist = sclDist;
            if (solidFlame) {
              dofBlurShape.applyOnlyCamera(camPoint, pPoint, sclDist, zr);
            }
            else {
              dofBlurShape.applyDOFAndCamera(camPoint, pPoint, sclDist, zr);
            }
          }
          else {
            pProjectedPoint.dofDist = 0.0;
            dofBlurShape.applyOnlyCamera(camPoint, pPoint, zdist, zr);
          }
        }
      }
      else {
        pPoint.x = camPoint.x / zr;
        pPoint.y = camPoint.y / zr;
        pProjectedPoint.dofDist = 0.0;
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
    camPoint.x = cameraMatrix[0][0] * pPoint.x + cameraMatrix[1][0] * pPoint.y + cameraMatrix[2][0] * pPoint.z;
    camPoint.y = cameraMatrix[0][1] * pPoint.x + cameraMatrix[1][1] * pPoint.y + cameraMatrix[2][1] * pPoint.z;
    camPoint.z = cameraMatrix[0][2] * pPoint.x + cameraMatrix[1][2] * pPoint.y + cameraMatrix[2][2] * pPoint.z;
  }

  public double getRcX() {
    return rcX;
  }

  public double getRcY() {
    return rcY;
  }

  public double getBws() {
    return bws;
  }

  public double getBhs() {
    return bhs;
  }

  public LightViewCalculator getLightViewCalculator() {
    return lightViewCalculator;
  }

}
