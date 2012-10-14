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
#ifndef __JWF_FLAME_RENDERER_H__
#define __JWF_FLAME_RENDERER_H__

#include <Windows.h>

DWORD WINAPI iterateThreadFunction(LPVOID lpParam) {
	FlameRenderThread *thread = (FlameRenderThread*) lpParam;
	thread->iterate();
	return 0;
}

struct FlameRenderer {
	// init in initRaster
	int imageWidth;
	int imageHeight;
	int rasterWidth;
	int rasterHeight;
	int rasterSize;
	int borderWidth;
	int maxBorderWidth;
	int threadCount;
	LogDensityFilter *logDensityFilter;
	GammaCorrectionFilter *gammaCorrectionFilter;
	RasterPoint **raster;
	// init in createColorMap
	RenderColor *colorMap;
	JWF_FLOAT paletteIdxScl;
	FlameTransformationContext *flameTransformationContext;
	RenderInfo *renderInfo;
	FlameView *flameView;
	Flame *flame;

	void create(Flame *pFlame, int pThreadCount) {
		imageWidth = 0;
		imageHeight = 0;
		rasterWidth = 0;
		rasterHeight = 0;
		rasterSize = 0;
		borderWidth = 0;
		maxBorderWidth = 0;
		logDensityFilter = NULL;
		gammaCorrectionFilter = NULL;
		raster = NULL;
		flameView = NULL;
		// init in createColorMap
		colorMap = NULL;
		paletteIdxScl = 0.0;
		flameTransformationContext = NULL;
		renderInfo = NULL;
		flame = pFlame;
		flameView = NULL;
		flameTransformationContext = new FlameTransformationContext(flame->preserveZ);

		flameView = new FlameView(pFlame);
		threadCount = pThreadCount;
		if (threadCount < 1) {
			threadCount = 1;
		}
	}

	void free() {
		delete flameView;
		if (logDensityFilter != NULL) {
			logDensityFilter->free();
			hostFree(logDensityFilter);
			logDensityFilter = NULL;
		}
		if (gammaCorrectionFilter != NULL) {
			gammaCorrectionFilter->free();
			hostFree(gammaCorrectionFilter);
			gammaCorrectionFilter = NULL;
		}
		if (raster != NULL) {
			for (int i = 0; i < rasterHeight; i++) {
				hostFree(raster[i]);
			}
			hostFree(raster);
			raster = NULL;
		}
		if (flameTransformationContext != NULL) {
			delete flameTransformationContext;
			flameTransformationContext = NULL;
		}
		if (flameView != NULL) {
			hostFree(flameView);
			flameView = NULL;
		}
		if (colorMap != NULL) {
			hostFree(colorMap);
			colorMap = NULL;
		}
	}

#define MAX_FILTER_WIDTH 25

	void initRaster(int pImageWidth, int pImageHeight) {
		imageWidth = pImageWidth;
		imageHeight = pImageHeight;
		hostMalloc((void**) &logDensityFilter, sizeof(LogDensityFilter));
		logDensityFilter->create(flame);
		hostMalloc((void**) &gammaCorrectionFilter, sizeof(GammaCorrectionFilter));
		gammaCorrectionFilter->create(flame);
		maxBorderWidth = (MAX_FILTER_WIDTH - 1) / 2;
		borderWidth = (logDensityFilter->noiseFilterSize - 1) / 2;
		rasterWidth = imageWidth + 2 * maxBorderWidth;
		rasterHeight = imageHeight + 2 * maxBorderWidth;
		rasterSize = rasterWidth * rasterHeight;
		hostMalloc((void**) &raster, rasterHeight * sizeof(RasterPoint*));
		for (int i = 0; i < rasterHeight; i++) {
			hostMalloc((void**) &raster[i], rasterWidth * sizeof(RasterPoint));
			memset(raster[i], 0, rasterWidth * sizeof(RasterPoint));
		}
		long memSize = (long) rasterWidth * (long) sizeof(RasterPoint) * (long) rasterHeight;
		printf("RASTER SIZE %ld MB\n", memSize / (long) (1024 * 1024));
	}

	void renderImage(SimpleImage *pImage, SimpleHDRImage *pHDRImage, SimpleHDRImage *pHDRIntensityMap) {
		LogDensityPoint *logDensityPnt;
		hostMalloc((void**) &logDensityPnt, sizeof(LogDensityPoint));

		bool newShit = false;
		JWF_FLOAT minDensity, maxDensity, avgDensity;

		if (pImage != NULL) {
			logDensityFilter->setRaster(raster, rasterWidth, rasterHeight, pImage->imageWidth, pImage->imageHeight);
		}
		else if (pHDRImage != NULL) {
			logDensityFilter->setRaster(raster, rasterWidth, rasterHeight, pHDRImage->imageWidth, pHDRImage->imageHeight);
		}
		else if (pHDRIntensityMap != NULL) {
			logDensityFilter->setRaster(raster, rasterWidth, rasterHeight, pHDRIntensityMap->imageWidth, pHDRIntensityMap->imageHeight);
		}
		if (newShit) {
			logDensityFilter->calcDensity(&minDensity, &maxDensity, &avgDensity);
			printf("AVG DENSITY: %f (%f...%f)\n", avgDensity, minDensity, maxDensity);
		}

		if (pImage != NULL) {
			GammaCorrectedRGBPoint *rbgPoint;
			hostMalloc((void**) &rbgPoint, sizeof(GammaCorrectedRGBPoint));
			for (int i = 0; i < pImage->imageHeight; i++) {
				for (int j = 0; j < pImage->imageWidth; j++) {
					if (newShit) {
						logDensityFilter->transformPointNewShit(logDensityPnt, j, i, minDensity, maxDensity, avgDensity);
					}
					else {
						logDensityFilter->transformPoint(logDensityPnt, j, i);
					}
					gammaCorrectionFilter->transformPoint(logDensityPnt, rbgPoint);
					pImage->setRGB(j, i, rbgPoint->red, rbgPoint->green, rbgPoint->blue);
				}
			}
			hostFree(rbgPoint);
		}

		if (pHDRImage != NULL) {
			GammaCorrectedHDRPoint *rbgPoint;
			hostMalloc((void**) &rbgPoint, sizeof(GammaCorrectedHDRPoint));

			BOOLEAN hasBGColor = flame->bgColorRed > 0 || flame->bgColorGreen > 0 || flame->bgColorBlue > 0;
			BOOLEAN setBG = FALSE;
			if (hasBGColor) {
				BOOLEAN *bgMap;
				hostMalloc((void**) &bgMap, pHDRImage->imageWidth * pHDRImage->imageHeight * sizeof(BOOLEAN));
				memset(bgMap, 0, pHDRImage->imageWidth * pHDRImage->imageHeight * sizeof(BOOLEAN));
				JWF_FLOAT minLum = FLT_MAX, maxLum = 0.0;
				for (int i = 0; i < pHDRImage->imageHeight; i++) {
					for (int j = 0; j < pHDRImage->imageWidth; j++) {
						if (newShit) {
						  logDensityFilter->transformPointHDRNewShit(logDensityPnt, j, i, maxDensity);
						}
						else {
							logDensityFilter->transformPointHDR(logDensityPnt, j, i);
						}
						gammaCorrectionFilter->transformPointHDR(logDensityPnt, rbgPoint);
						if (rbgPoint->red < 0.0f) {
							bgMap[i * pHDRImage->imageWidth + j] = TRUE;
							setBG = TRUE;
						}
						else {
							pHDRImage->setRGB(j, i, rbgPoint->red, rbgPoint->green, rbgPoint->blue);
							JWF_FLOAT lum = (rbgPoint->red * 0.299 + rbgPoint->green * 0.588 + rbgPoint->blue * 0.113);
							if (lum > maxLum) {
								maxLum = lum;
							}
							else if (lum < minLum) {
								minLum = lum;
							}
						}
					}
				}
				if (setBG) {
					JWF_FLOAT weight = (JWF_FLOAT) (0.25 * (maxLum + 3.0 * minLum));
					JWF_FLOAT bgRed = (JWF_FLOAT) (gammaCorrectionFilter->bgRedDouble * weight);
					JWF_FLOAT bgGreen = (JWF_FLOAT) (gammaCorrectionFilter->bgGreenDouble * weight);
					JWF_FLOAT bgBlue = (JWF_FLOAT) (gammaCorrectionFilter->bgBlueDouble * weight);
					for (int i = 0; i < pHDRImage->imageHeight; i++) {
						for (int j = 0; j < pHDRImage->imageWidth; j++) {
							if (bgMap[i * pHDRImage->imageWidth + j]) {
								pHDRImage->setRGB(j, i, bgRed, bgGreen, bgBlue);
							}
						}
					}
				}
				hostFree(bgMap);
			}
			else {
				for (int i = 0; i < pHDRImage->imageHeight; i++) {
					for (int j = 0; j < pHDRImage->imageWidth; j++) {
						if(newShit) {
						  logDensityFilter->transformPointHDRNewShit(logDensityPnt, j, i, maxDensity);
						}
						else {
							logDensityFilter->transformPointHDR(logDensityPnt, j, i);
						}
						gammaCorrectionFilter->transformPointHDR(logDensityPnt, rbgPoint);
						pHDRImage->setRGB(j, i, rbgPoint->red, rbgPoint->green, rbgPoint->blue);
					}
				}
			}

			hostFree(rbgPoint);
		}

		if (pHDRIntensityMap != NULL) {
			for (int i = 0; i < pHDRIntensityMap->imageHeight; i++) {
				for (int j = 0; j < pHDRIntensityMap->imageWidth; j++) {
					logDensityFilter->transformPointHDR(logDensityPnt, j, i);
					pHDRIntensityMap->setRGB(j, i, (JWF_FLOAT) logDensityPnt->intensity, (JWF_FLOAT) logDensityPnt->intensity, (JWF_FLOAT) logDensityPnt->intensity);
				}
			}
		}

		hostFree(logDensityPnt);
		logDensityPnt = NULL;
	}

	void renderImageSimple(SimpleImage *pImage) {
		LogDensityPoint *logDensityPnt;
		hostMalloc((void**) &logDensityPnt, sizeof(LogDensityPoint));

		GammaCorrectedRGBPoint *rbgPoint;
		hostMalloc((void**) &rbgPoint, sizeof(GammaCorrectedRGBPoint));

		logDensityFilter->setRaster(raster, rasterWidth, rasterHeight, pImage->imageWidth, pImage->imageHeight);
		for (int i = 0; i < pImage->imageHeight; i++) {
			for (int j = 0; j < pImage->imageWidth; j++) {
				logDensityFilter->transformPointSimple(logDensityPnt, j, i);
				gammaCorrectionFilter->transformPointSimple(logDensityPnt, rbgPoint);
				pImage->setRGB(j, i, rbgPoint->red, rbgPoint->green, rbgPoint->blue);
			}
		}

		hostFree(rbgPoint);
		hostFree(logDensityPnt);
	}

	RenderedFlame *renderFlame(RenderInfo *pRenderInfo) {
		RenderedFlame *res;
		hostMalloc((void**) &res, sizeof(RenderedFlame));
		res->create(pRenderInfo);

		BOOLEAN renderNormal = TRUE;
		BOOLEAN renderHDR = pRenderInfo->renderHDR;

		if (flame->xFormCount == 0) {
			if (renderNormal) {
				res->image->fillBackground(flame->bgColorRed, flame->bgColorGreen, flame->bgColorBlue);
			}
			if (renderHDR) {
				res->hdrImage->fillBackground(flame->bgColorRed, flame->bgColorGreen, flame->bgColorBlue);
			}
			return res;
		}
		initRaster(pRenderInfo->imageWidth, pRenderInfo->imageHeight);
		flameView->init3D();
		createColorMap();
		flameView->initView(imageWidth, imageHeight, borderWidth, maxBorderWidth, rasterWidth, rasterHeight);
		flame->prepareFlame(flameTransformationContext, threadCount);
		double nSamples = ((double) flame->sampleDensity * (double) rasterSize / ((double) threadCount * (double) pRenderInfo->bundleSize) + 0.5);
		printf("SAMPLES: %fd (%d threads)\n", nSamples * (double) threadCount, threadCount);

		if (threadCount == 1) {
			FlameRenderThread thread;
			flameTransformationContext->threadIdx = 0;
			thread.init(flame, flameTransformationContext, nSamples, flameView, raster, rasterWidth, rasterHeight, colorMap, paletteIdxScl, pRenderInfo->poolSize,
					pRenderInfo->bundleSize);
			thread.initState();
			thread.iterate();
		}
		else {
			FlameRenderThread *threads;
			hostMalloc((void**) &threads, sizeof(FlameRenderThread) * threadCount);
			FlameTransformationContext **ctxArray;
			hostMalloc((void**) &ctxArray, sizeof(FlameTransformationContext*) * threadCount);
			HANDLE *hThreadArray;
			hostMalloc((void**) &hThreadArray, sizeof(HANDLE) * threadCount);
			DWORD *dwThreadIdArray;
			hostMalloc((void**) &dwThreadIdArray, sizeof(DWORD) * threadCount);
			for (int i = 0; i < threadCount; i++) {
				ctxArray[i] = new FlameTransformationContext(flame->preserveZ);
				ctxArray[i]->threadIdx = i;
				threads[i].init(flame, ctxArray[i], nSamples, flameView, raster, rasterWidth, rasterHeight, colorMap, paletteIdxScl, pRenderInfo->poolSize, pRenderInfo->bundleSize);
				threads[i].initState();
				hThreadArray[i] = CreateThread(NULL, // default security attributes
						0, // use default stack size
						iterateThreadFunction, // thread function name
						&threads[i], // argument to thread function
						0, // use default creation flags
						&dwThreadIdArray[i]); // returns the thread identifier
			}
			WaitForMultipleObjects(threadCount, hThreadArray, TRUE, INFINITE);
			for (int i = 0; i < threadCount; i++) {
				CloseHandle(hThreadArray[i]);
			}
			hostFree(dwThreadIdArray);
			hostFree(hThreadArray);
			for (int i = 0; i < threadCount; i++) {
				delete ctxArray[i];
			}
			hostFree(ctxArray);
			hostFree(threads);
		}
		if (flame->sampleDensity <= 10.0) {
			renderImageSimple(res->image);
		}
		else {
			renderImage(res->image, res->hdrImage, res->hdrHeightMap);
		}
		return res;
	}

	void createColorMap() {
		colorMap = flame->palette->createRenderPalette(flame->whiteLevel);
		paletteIdxScl = (JWF_FLOAT) flame->palette->getSize() - 1.0;
	}

};

#endif // __JWF_FLAME_RENDERER_H__
