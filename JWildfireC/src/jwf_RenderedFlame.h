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
#ifndef __JWF_RENDERED_FLAME_H__
#define __JWF_RENDERED_FLAME_H__

struct RenderedFlame {
	SimpleImage *image;
	SimpleHDRImage *hdrImage;
	SimpleHDRImage *hdrHeightMap;

	void create(RenderInfo *pRenderInfo) {
		image = NULL;
		hdrImage = NULL;
		hdrHeightMap = NULL;

		hostMalloc((void**) &image, sizeof(SimpleImage));
		image->create();
		image->init(pRenderInfo->imageWidth, pRenderInfo->imageHeight);
		if (pRenderInfo->renderHDR) {
			hostMalloc((void**) &hdrImage, sizeof(SimpleHDRImage));
			hdrImage->create();
			hdrImage->init(pRenderInfo->imageWidth, pRenderInfo->imageHeight);
		}
		if (pRenderInfo->renderHDRIntensityMap) {
			hostMalloc((void**) &hdrHeightMap, sizeof(SimpleHDRImage));
			hdrHeightMap->create();
			hdrHeightMap->init(pRenderInfo->imageWidth, pRenderInfo->imageHeight);
		}
	}

	void free() {
		if (image != NULL) {
			image->free();
			hostFree(image);
			image = NULL;
		}
	}

};

#endif // __JWF_RENDERED_FLAME_H__
