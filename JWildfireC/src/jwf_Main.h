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
#ifndef __JWF_MAIN_H__
#define __JWF_MAIN_H__

// system stuff
#include <stdio.h>
#include <string.h>
#include <FLOAT.h>
#include <math.h>
#include <stdlib.h>
#include <time.h>
#include <stddef.h>

// JWF stuff
#include "jwf_Constants.h"
#include "jwf_Math.h"
#include "jwf_AppSettings.h"
#include "jwf_Types.h"
#include "jwf_Util.h"
#include "jwf_XYZPoint.h"
#include "jwf_RandGen.h"
#include "jwf_FlameTransformationContext.h"
#include "jwf_Variation.h"
#include "jwf_XForm.h"
#include "jwf_RGBColor.h"
#include "jwf_RenderColor.h"
#include "jwf_RGBPalette.h"
#include "jwf_RenderInfo.h"
#include "jwf_Flame.h"
#include "jwf_GammaCorrectionFilter.h"
#include "jwf_LogDensityFilter.h"
#include "jwf_Pixel.h"
#include "jwf_SimpleImage.h"
#include "jwf_SimpleHDRImage.h"
#include "jwf_RenderedFlame.h"
#include "jwf_FlameView.h"
#include "jwf_FlameRenderThread.h"
#include "jwf_FlameUtil.h"
#include "jwf_FlameRenderer.h"
#include "jwf_PPMWriter.h"
#include "jwf_HDRWriter.h"
#include "jwf_Flam3Reader.h"

int renderFlame(AppSettings *pAppSettings) {
	Flam3Reader *reader = new Flam3Reader();
	Flame **flames = NULL;
	int flameCount = 0;

	reader->readFlames(pAppSettings->getFlameFilename(), &flames, &flameCount);
	if (flameCount < 1) {
		printf("No flame to render");
		return -1;
	}

	Flame *hostFlame = flames[0];

	FlameRenderer renderer;
	renderer.create(hostFlame, 8);
	RenderInfo info;

	info.imageWidth = pAppSettings->getOutputWidth();
	info.imageHeight = pAppSettings->getOutputHeight();

	if (info.imageWidth != hostFlame->width || info.imageHeight != hostFlame->height) {
		FLOAT wScl = (FLOAT) info.imageWidth / (FLOAT) hostFlame->width;
		FLOAT hScl = (FLOAT) info.imageHeight / (FLOAT) hostFlame->height;
		hostFlame->pixelsPerUnit = (wScl + hScl) * 0.5 * hostFlame->pixelsPerUnit;
	}

	hostFlame->sampleDensity = pAppSettings->getSampleDensity();
	// TODO remove option
	//hostFlame->spatialFilterRadius = pAppSettings->getFilterRadius();

	info.renderHDR =  (pAppSettings->getOutputHDRFilename()!=NULL && strlen(pAppSettings->getOutputHDRFilename())>0) ? TRUE :  FALSE;
	info.renderHDRIntensityMap = FALSE;

	boolean usePool = hostFlame->sampleDensity > 150.0;
	for (int i = 0; i < hostFlame->xFormCount; i++) {
		XForm *xForm = hostFlame->xForms[i];
		for (int j = 0; j < hostFlame->xFormCount; j++) {
			if (fabs(xForm->modifiedWeights[j] - 1.0f) > EPSILON) {
				usePool = false;
				break;
			}
		}
	}
	hostFlame->dump();

	usePool=false;

	if (usePool == true) {
		info.poolSize = 1024;
		info.bundleSize = 256;
	}
	else {
		info.poolSize = 1;
		info.bundleSize = 1;
	}
	printf("Using pool %d/%d...\n", info.poolSize, info.bundleSize);

	clock_t begin = clock();
	RenderedFlame *res = renderer.renderFlame(&info);
	clock_t end = clock();
	FLOAT elapsed_secs = (FLOAT) (end - begin) / CLOCKS_PER_SEC;

	printf("Elapsed render time %3.1f s\n", elapsed_secs);
	if(res->image!=NULL && pAppSettings->getOutputFilename()!=NULL && strlen(pAppSettings->getOutputFilename())>0) {
	  PPMWriter *writer=new PPMWriter();
	  writer->writeImage(pAppSettings->getOutputFilename(), res->image);
	}
	if(res->hdrImage!=NULL && pAppSettings->getOutputHDRFilename()!=NULL && strlen(pAppSettings->getOutputHDRFilename())>0) {
	  HDRWriter *writer=new HDRWriter();
	  writer->writeImage(pAppSettings->getOutputHDRFilename(), res->hdrImage);
	}


	freeHostFlame(hostFlame);
	return 0;
}

int jwf_main(int argc, char *argv[]) {
	AppSettings *appSettings = new AppSettings();
	printf("Welcome to %s %s\n", APP_TITLE, APP_VERSION);
	printf("-------------------------------------------------------\n");
	if (appSettings->parseCmlLineArgs(argc, argv) != 0) {
		appSettings->showUsage(argv[0]);
		return -1;
	}
	appSettings->dump();
	return renderFlame(appSettings);
}

#endif // __JWF_MAIN_H__
