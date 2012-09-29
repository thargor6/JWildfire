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

#include "jwf_Math.h"

class FlameView {
public:

	FlameView(Flame *pFlame) {
		flame = pFlame;
		cosa = 0.0;
		sina = 0.0;
		camW = 0.0;
		camH = 0.0;
		rcX = 0.0;
		rcY = 0.0;
		bws = 0.0;
		bhs = 0.0;
		doProject3D = FALSE;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				cameraMatrix[i][j] = 0.0;
			}
		}

		useDOF = JWF_FABS(flame->camDOF) > EPSILON;
		camDOF_10 = 0.1 * flame->camDOF;
	}

	void initView(int pImageWidth, int pImageHeight, int pBorderWidth, int pMaxBorderWidth, int pRasterWidth, int pRasterHeight) {
		FLOAT pixelsPerUnit = flame->pixelsPerUnit * flame->camZoom;
		FLOAT corner_x = flame->centreX - (FLOAT) pImageWidth / pixelsPerUnit / 2.0;
		FLOAT corner_y = flame->centreY - (FLOAT) pImageHeight / pixelsPerUnit / 2.0;
		FLOAT t0 = pBorderWidth / pixelsPerUnit;
		FLOAT t1 = pBorderWidth / pixelsPerUnit;
		FLOAT t2 = (2 * pMaxBorderWidth - pBorderWidth) / pixelsPerUnit;
		FLOAT t3 = (2 * pMaxBorderWidth - pBorderWidth) / pixelsPerUnit;

		FLOAT camX0 = corner_x - t0;
		FLOAT camY0 = corner_y - t1;
		FLOAT camX1 = corner_x + (FLOAT) pImageWidth / pixelsPerUnit + t2;
		FLOAT camY1 = corner_y + (FLOAT) pImageHeight / pixelsPerUnit + t3;

		camW = camX1 - camX0;
		FLOAT Xsize, Ysize;
		if (JWF_FABS(camW) > 0.01)
			Xsize = 1.0 / camW;
		else
			Xsize = 1.0;
		camH = camY1 - camY0;
		if (JWF_FABS(camH) > 0.01f)
			Ysize = 1.0 / camH;
		else
			Ysize = 1;
		bws = (pRasterWidth - 0.5) * Xsize;
		bhs = (pRasterHeight - 0.5) * Ysize;

		cosa = JWF_COS(-M_PI * (flame->camRoll) / 180.0);
		sina = JWF_SIN(-M_PI * (flame->camRoll) / 180.0);
		rcX = flame->centreX * (1.0 - cosa) - flame->centreY * sina - camX0;
		rcY = flame->centreY * (1.0 - cosa) + flame->centreX * sina - camY0;
	}

	void init3D() {
		FLOAT yaw = -flame->camYaw * M_PI / 180.0;
		FLOAT pitch = flame->camPitch * M_PI / 180.0;
		cameraMatrix[0][0] = JWF_COS(yaw);
		cameraMatrix[1][0] = -JWF_SIN(yaw);
		cameraMatrix[2][0] = 0.0;
		cameraMatrix[0][1] = JWF_COS(pitch) * JWF_SIN(yaw);
		cameraMatrix[1][1] = JWF_COS(pitch) * JWF_COS(yaw);
		cameraMatrix[2][1] = -JWF_SIN(pitch);
		cameraMatrix[0][2] = JWF_SIN(pitch) * JWF_SIN(yaw);
		cameraMatrix[1][2] = JWF_SIN(pitch) * JWF_COS(yaw);
		cameraMatrix[2][2] = JWF_COS(pitch);
		doProject3D = JWF_FABS(flame->camYaw) > EPSILON || JWF_FABS(flame->camPitch) > EPSILON || JWF_FABS(flame->camPerspective) > EPSILON || JWF_FABS(flame->camDOF) > EPSILON;
	}

	void project(XYZPoint *pPoint, FlameTransformationContext *pFlameTransformationContext) {
		if (!doProject3D) {
			return;
		}
		FLOAT z = pPoint->z;
		FLOAT px = cameraMatrix[0][0] * pPoint->x + cameraMatrix[1][0] * pPoint->y;
		FLOAT py = cameraMatrix[0][1] * pPoint->x + cameraMatrix[1][1] * pPoint->y + cameraMatrix[2][1] * z;
		FLOAT pz = cameraMatrix[0][2] * pPoint->x + cameraMatrix[1][2] * pPoint->y + cameraMatrix[2][2] * z;
		FLOAT zr = 1.0 - flame->camPerspective * pz;
		if (useDOF) {
			FLOAT a = 2.0 * M_PI * pFlameTransformationContext->randGen->random();
			FLOAT dsina = JWF_SIN(a);
			FLOAT dcosa = JWF_COS(a);
			FLOAT zdist = (flame->camZ - pz);
			FLOAT dr;
			if (zdist > 0.0) {
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

	FLOAT cosa;
	FLOAT sina;
	FLOAT camW;
	FLOAT camH;
	FLOAT rcX;
	FLOAT rcY;
	FLOAT bws;
	FLOAT bhs;

private:
	Flame *flame;
	bool doProject3D;
	FLOAT cameraMatrix[3][3];

	FLOAT camDOF_10;
	bool useDOF;
};

#endif // __JWF_FLAME_VIEW_H__
