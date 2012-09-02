/*
  JWildfireC - an external C-based fractal-flame-renderer for JWildfire 
  Copyright (C) 2012 Andreas Maschke

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
#ifndef __JWF_FLAME_VIEW_H__
#define __JWF_FLAME_VIEW_H__

class FlameView {
public:

  FlameView(Flame *pFlame) {
    flame=pFlame;
    cosa=0.0f;
    sina=0.0f;
    camW=0.0f;
    camH=0.0f;
    rcX=0.0f;
    rcY=0.0f;
    bws=0.0f;
    bhs=0.0f;
    doProject3D=FALSE;
    for(int i=0;i<3;i++) {
      for(int j=0;j<3;j++) {
        cameraMatrix[i][j]=0.0f;
      }
    }

    useDOF=fabs(flame->camDOF) > EPSILON;
    camDOF_10 = 0.1f*flame->camDOF;
  }

  void initView(int pImageWidth,int pImageHeight,int pBorderWidth,int pMaxBorderWidth, int pRasterWidth,int pRasterHeight) {
    float pixelsPerUnit = flame->pixelsPerUnit * flame->camZoom;
    float corner_x = flame->centreX - (float) pImageWidth / pixelsPerUnit / 2.0f;
    float corner_y = flame->centreY - (float) pImageHeight / pixelsPerUnit / 2.0f;
    float t0 = pBorderWidth / pixelsPerUnit;
    float t1 = pBorderWidth / pixelsPerUnit;
    float t2 = (2 * pMaxBorderWidth - pBorderWidth) / pixelsPerUnit;
    float t3 = (2 * pMaxBorderWidth - pBorderWidth) / pixelsPerUnit;

    float camX0 = corner_x - t0;
    float camY0 = corner_y - t1;
    float camX1 = corner_x + (float) pImageWidth / pixelsPerUnit + t2;
    float camY1 = corner_y + (float) pImageHeight / pixelsPerUnit + t3;

    camW = camX1 - camX0;
    float Xsize, Ysize;
    if (fabs(camW) > 0.01f)
      Xsize = 1.0f / camW;
    else
      Xsize = 1.0f;
    camH = camY1 - camY0;
    if (fabs(camH) > 0.01f)
      Ysize = 1.0f / camH;
    else
      Ysize = 1;
    bws = (pRasterWidth - 0.5f) * Xsize;
    bhs = (pRasterHeight - 0.5f) * Ysize;

    cosa = cos(-M_PI * (flame->camRoll) / 180.0f);
    sina = sin(-M_PI * (flame->camRoll) / 180.0f);
    rcX = flame->centreX * (1.0f - cosa) - flame->centreY * sina - camX0;
    rcY = flame->centreY * (1.0f - cosa) + flame->centreX * sina - camY0;
  }

  void init3D() {
    float yaw = -flame->camYaw * M_PI / 180.0f;
    float pitch = flame->camPitch * M_PI / 180.0f;
    cameraMatrix[0][0] = cosf(yaw);
    cameraMatrix[1][0] = -sinf(yaw);
    cameraMatrix[2][0] = 0.0f;
    cameraMatrix[0][1] = cosf(pitch) * sinf(yaw);
    cameraMatrix[1][1] = cosf(pitch) * cosf(yaw);
    cameraMatrix[2][1] = -sin(pitch);
    cameraMatrix[0][2] = sinf(pitch) * sinf(yaw);
    cameraMatrix[1][2] = sinf(pitch) * cosf(yaw);
    cameraMatrix[2][2] = cosf(pitch);
    doProject3D = fabs(flame->camYaw) > EPSILON || fabs(flame->camPitch) > EPSILON || fabs(flame->camPerspective) > EPSILON || fabs(flame->camDOF) > EPSILON;
  }

  void project(XYZPoint *pPoint,FlameTransformationContext *pFlameTransformationContext) {
    if (!doProject3D) {
      return;
    }
    float z = pPoint->z;
    float px = cameraMatrix[0][0] * pPoint->x + cameraMatrix[1][0] * pPoint->y;
    float py = cameraMatrix[0][1] * pPoint->x + cameraMatrix[1][1] * pPoint->y + cameraMatrix[2][1] * z;
    float pz = cameraMatrix[0][2] * pPoint->x + cameraMatrix[1][2] * pPoint->y + cameraMatrix[2][2] * z;
    float zr = 1.0f - flame->camPerspective * pz;
    if (useDOF) {
      float a = 2.0f * M_PI * pFlameTransformationContext->randGen->random();
      float dsina = sinf(a);
      float dcosa = cosf(a);
      float zdist = (flame->camZ - pz);
      float dr;
      if (zdist > 0.0f) {
        dr = pFlameTransformationContext->randGen->random() * camDOF_10 * zdist;
        pPoint->x = (px + dr * dcosa) / zr;
        pPoint->y = (py + dr * dsina) / zr;
      }
      else {
        pPoint->x = px / zr;
        pPoint->y = py / zr;
      }
    }
    else {
      pPoint->x = px / zr;
      pPoint->y = py / zr;
    }
  }

  float cosa;
  float sina;
  float camW;
  float camH;
  float rcX;
  float rcY;
  float bws;
  float bhs;

private:
  Flame *flame;
  bool doProject3D;
  float cameraMatrix[3][3];

  float camDOF_10;
  bool useDOF;
};


#endif // __JWF_FLAME_VIEW_H__
