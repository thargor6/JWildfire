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
#include <float.h>
#include <math.h>
#include <stdlib.h>
#include <time.h>
#include <stddef.h>

// JWF stuff
#include "jwf_Constants.h"
#include "jwf_FastMath.h"
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

#include "jwfvar_Bipolar.h"
#include "jwfvar_Bubble.h"
#include "jwfvar_Cross.h"
#include "jwfvar_Cylinder.h"
#include "jwfvar_Elliptic.h"
#include "jwfvar_Epispiral.h"
#include "jwfvar_EpispiralWF.h"
#include "jwfvar_Eyefish.h"
#include "jwfvar_Flower.h"
#include "jwfvar_Flux.h"
#include "jwfvar_GaussianBlur.h"
#include "jwfvar_Julia3D.h"
#include "jwfvar_JuliaN.h"
#include "jwfvar_Linear3D.h"
#include "jwfvar_Lissajous.h"
#include "jwfvar_Log.h"
#include "jwfvar_Pie.h"
#include "jwfvar_Pie3D.h"
#include "jwfvar_PreBlur.h"
#include "jwfvar_PreCircleCrop.h"
#include "jwfvar_RadialBlur.h"
#include "jwfvar_Spherical.h"
#include "jwfvar_Spherical3D.h"
#include "jwfvar_Splits.h"
#include "jwfvar_Unpolar.h"
#include "jwfvar_Waves2.h"
#include "jwfvar_Waves4WF.h"
#include "jwfvar_ZCone.h"
#include "jwfvar_ZScale.h"
#include "jwfvar_ZTranslate.h"

#include "jwf_Flam3Reader.h"

#include "jwf_ExampleFlames.h"




void testHost1000Flame() {
  Flame *hostFlame=createExample1Flame();

  FlameRenderer renderer;
  renderer.create(hostFlame,8);
  RenderInfo info;
  
  info.imageWidth=hostFlame->width;
  info.imageHeight=hostFlame->height;
  
  info.imageWidth=1920;
  info.imageHeight=1080;

//   times: 800x600, 1000.0, 1, 27.5s

  if(info.imageWidth!=hostFlame->width || info.imageHeight!=hostFlame->height) {
    double wScl = (float) info.imageWidth / (float) hostFlame->width;
    double hScl = (float) info.imageHeight / (float) hostFlame->height;
    hostFlame->pixelsPerUnit=(wScl + hScl) * 0.5 * hostFlame->pixelsPerUnit;
  }

  hostFlame->sampleDensity=200.0;
  hostFlame->spatialFilterRadius=0.5;
  //hostFlame->spatialFilterRadius=0.0;

  info.renderHDR=FALSE;
  info.renderHDRIntensityMap=FALSE;

  info.poolSize=1000;
  info.bundleSize=200;

  //info.poolSize=1;
//    info.bundleSize=1;


  clock_t begin = clock();
  RenderedFlame *res=renderer.renderFlame(&info);
  clock_t end = clock();
  double elapsed_secs = double(end - begin) / CLOCKS_PER_SEC;
  hostFlame->dump();
  printf("Elapsed render time %3.1f s\n",elapsed_secs);

  PPMWriter writer;
  writer.writeImage("C:\\TMP\\CUDA.ppm",res->image);
  freeHostFlame(hostFlame);
}

int renderFlame(AppSettings *pAppSettings) {
	Flam3Reader *reader=new Flam3Reader();
	Flame **flames;
	int flameCount;
	reader->readFlames(pAppSettings->getFlameFilename(), &flames, &flameCount);
	if(flameCount<1) {
		printf("No flame to render");
		return -1;
	}
  Flame *hostFlame=flames[0];

  FlameRenderer renderer;
  renderer.create(hostFlame,8);
  RenderInfo info;



  info.imageWidth=pAppSettings->getOutputWidth();
  info.imageHeight=pAppSettings->getOutputHeight();

  if(info.imageWidth!=hostFlame->width || info.imageHeight!=hostFlame->height) {
    double wScl = (float) info.imageWidth / (float) hostFlame->width;
    double hScl = (float) info.imageHeight / (float) hostFlame->height;
    hostFlame->pixelsPerUnit=(wScl + hScl) * 0.5 * hostFlame->pixelsPerUnit;
  }

  hostFlame->sampleDensity=pAppSettings->getSampleDensity();
  hostFlame->spatialFilterRadius=pAppSettings->getFilterRadius();

  info.renderHDR=FALSE;
  info.renderHDRIntensityMap=FALSE;

  boolean usePool=true;
  for(int i=0;i<hostFlame->xFormCount;i++) {
    XForm *xForm=hostFlame->xForms[i];
    for(int j=0;j<hostFlame->xFormCount;j++) {
    	if(fabs(xForm->modifiedWeights[j]-1.0f)>EPSILON) {
    		usePool=false;
    		break;
    	}
    }
  }
  hostFlame->dump();

  if(usePool==true) {
    info.poolSize=1024;
    info.bundleSize=256;
  }
  else {
    info.poolSize=1;
    info.bundleSize=1;
  }
	printf("Using pool %d/%d...\n",info.poolSize, info.bundleSize);

  clock_t begin = clock();
  RenderedFlame *res=renderer.renderFlame(&info);
  clock_t end = clock();
  double elapsed_secs = double(end - begin) / CLOCKS_PER_SEC;
  // TODO: option

  printf("Elapsed render time %3.1f s\n",elapsed_secs);

  PPMWriter writer;
  // TODO option:
  writer.writeImage("C:\\TMP\\CUDA.ppm",res->image);
  freeHostFlame(hostFlame);
	return 0;
}


int jwf_main(int argc, char *argv[]) {
	AppSettings *appSettings=new AppSettings();
  printf("Welcome to %s %s\n", APP_TITLE, APP_VERSION);
  printf("-------------------------------------------------------\n");
  if(appSettings->parseCmlLineArgs(argc, argv)!=0) {
  	appSettings->showUsage(argv[0]);
  	return -1;
  }
  appSettings->dump();
  //testHost1000Flame();
  return renderFlame(appSettings);
}


#endif // __JWF_MAIN_H__
