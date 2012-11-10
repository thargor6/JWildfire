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
#ifndef __JWF_FLAME_RENDER_THREAD_H__
#define __JWF_FLAME_RENDER_THREAD_H__

#include "jwf_Math.h"

struct FlameRenderThread {
	Flame *flame;
	FlameTransformationContext *ctx;
	double totalSamples;
	FlameView *flameView;
	RasterPoint **raster;
	int rasterWidth;
	int rasterHeight;
	RenderColor *colorMap;JWF_FLOAT paletteIdxScl;
	int poolSize;
	int bundleSize;
	//
	XYZPoint affineT;
	XYZPoint varT;
	XYZPoint p;
	XYZPoint q;

	XYZPoint *affineTArray;
	XYZPoint *varTArray;
	XYZPoint *pArray;
	XYZPoint *qArray;JWF_FLOAT *pxArray, *pyArray;
	bool *pValidArray;
	int *bundle;

	XForm *xf;
	double startIter;

	int threadCount;
	double *threadStatus;
	bool reportStatus;
	char *statusFilename;
	int lastReportedStatus;

	void init(Flame *pFlame, FlameTransformationContext *pCtx, double pTotalSamples, FlameView *pFlameView, RasterPoint **pRaster, int pRasterWidth, int pRasterHeight,
			RenderColor *pColorMap, JWF_FLOAT pPaletteIdxScl, int pPoolSize, int pBundleSize, int pThreadCount, double *pThreadStatus,
			char *pStatusFilename) {
		xf = NULL;
		flame = pFlame;
		ctx = pCtx;
		totalSamples = pTotalSamples;
		flameView = pFlameView;
		raster = pRaster;
		rasterWidth = pRasterWidth;
		rasterHeight = pRasterHeight;
		colorMap = pColorMap;
		paletteIdxScl = pPaletteIdxScl;
		poolSize = pPoolSize;
		if (poolSize < 1)
			poolSize = 1;
		bundleSize = pBundleSize;
		if (bundleSize < 1)
			bundleSize = 1;
		else if (bundleSize > poolSize)
			bundleSize = poolSize;
		affineTArray = varTArray = pArray = qArray = NULL;
		pxArray = pyArray = NULL;
		pValidArray = NULL;
		threadCount = pThreadCount;
		threadStatus = pThreadStatus;
		statusFilename = pStatusFilename;
		reportStatus = statusFilename!=NULL && strlen(statusFilename)>0;
		lastReportedStatus=0;
	}

	void initState() {
		if (poolSize <= 1)
			initState1();
		else
			initStateN();
	}

	void initStateN() {
		XForm **xForms = flame->xForms;
		// TODO: free
		affineTArray = new XYZPoint[poolSize];
		varTArray = new XYZPoint[poolSize];
		pArray = new XYZPoint[poolSize];
		qArray = new XYZPoint[poolSize];

		hostMalloc((void**) &pxArray, poolSize * sizeof(JWF_FLOAT));
		hostMalloc((void**) &pyArray, poolSize * sizeof(JWF_FLOAT));
		hostMalloc((void**) &pValidArray, poolSize * sizeof(bool));
		hostMalloc((void**) &bundle, bundleSize * sizeof(int));

		xf = flame->xForms[0];

		// TODO: free
		for (int i = 0; i < poolSize; i++) {
			pArray[i].x = 2.0 * ctx->randGen->random() - 1.0;
			pArray[i].y = 2.0 * ctx->randGen->random() - 1.0;
			pArray[i].z = 0.0;
			pArray[i].color = ctx->randGen->random();
			xf->transformPoint(ctx, &affineTArray[i], &varTArray[i], &pArray[i], &pArray[i]);
		}

		for (int iter = 0; iter <= INITIAL_ITERATIONS; iter++) {
			int xfIdx = xf->nextAppliedXFormIdxTable[ctx->randGen->random(NEXT_APPLIED_XFORM_TABLE_SIZE)];
			xf = xfIdx >= 0 ? xForms[xfIdx] : NULL;
			if (xf == NULL) {
				return;
			}
			for (int i = 0; i < poolSize; i++) {
				xf->transformPoint(ctx, &affineTArray[i], &varTArray[i], &pArray[i], &pArray[i]);
			}
		}
	}

	void initState1() {
		XForm **xForms = flame->xForms;
		p.x = 4.0 * ctx->randGen->random() - 2.0;
		p.y = 4.0 * ctx->randGen->random() - 2.0;
		p.z = 0.0;
		p.color = ctx->randGen->random();

		xf = flame->xForms[0];
		xf->transformPoint(ctx, &affineT, &varT, &p, &p);
		for (int i = 0; i <= INITIAL_ITERATIONS; i++) {
			int xfIdx = xf->nextAppliedXFormIdxTable[ctx->randGen->random(NEXT_APPLIED_XFORM_TABLE_SIZE)];
			xf = xfIdx >= 0 ? xForms[xfIdx] : NULL;
			if (xf == NULL) {
				return;
			}
			xf->transformPoint(ctx, &affineT, &varT, &p, &p);
		}
	}

	void iterate() {
		if (poolSize <= 1)
			iterate1();
		else
			iterateN();
	}

	void iterate1() {
		XForm **xForms = flame->xForms;
		RandGen *rnd = ctx->randGen;
		bool ready = false;
		for (double iter = 1; !ready; iter = iter + 1.0) {
			int xfIdx = xf->nextAppliedXFormIdxTable[ctx->randGen->random(NEXT_APPLIED_XFORM_TABLE_SIZE)];
			xf = xForms[xfIdx];
			//xf->transformPoint(ctx, &affineT, &varT, &p, &p);
			xf->transformPoint(ctx, &affineT, &varT, &p);

			if (xf->drawMode != DRAWMODE_NORMAL) {
				if (xf->drawMode == DRAWMODE_HIDDEN)
					continue;
				else if ((xf->drawMode == DRAWMODE_OPAQUE) && (ctx->randGen->random() > xf->opacity))
					continue;
			}

			JWF_FLOAT px, py;
			int xIdx, yIdx;
			if (flame->finalXFormCount>0) {
				flame->finalXForms[0]->transformPoint(ctx, &affineT, &varT, &p, &q);
				for(int i=1;i<flame->finalXFormCount;i++) {
					flame->finalXForms[i]->transformPoint(ctx, &affineT, &varT, &q, &q);
				}
				flameView->project(&q, ctx);
				px = q.x * flameView->cosa + q.y * flameView->sina + flameView->rcX;
				if ((px < 0) || (px > flameView->camW))
					continue;
				py = q.y * flameView->cosa - q.x * flameView->sina + flameView->rcY;
				if ((py < 0) || (py > flameView->camH))
					continue;
        XForm *finalXForm = flame->finalXForms[flame->finalXFormCount-1];
				if ((finalXForm->antialiasAmount > EPSILON) && (finalXForm->antialiasRadius > EPSILON) && (rnd->random() > 1.0 - finalXForm->antialiasAmount)) {
					JWF_FLOAT dr = JWF_EXP(finalXForm->antialiasRadius * JWF_SQRT(-JWF_LOG((JWF_FLOAT) rnd->random()))) - 1.0;
					JWF_FLOAT da = rnd->random() * 2.0 * M_PI;
					JWF_FLOAT sinda, cosda;
					JWF_SINCOS(da, &sinda, &cosda);
					xIdx = (int) (flameView->bws * px + dr * cosda + 0.5);
					if (xIdx < 0 || xIdx >= rasterWidth)
						continue;
					yIdx = (int) (flameView->bhs * py + dr * sinda + 0.5);
					if (yIdx < 0 || yIdx >= rasterHeight)
						continue;
				}
				else {
					xIdx = (int) (flameView->bws * px + 0.5);
					if (xIdx < 0 || xIdx >= rasterWidth)
						continue;
					yIdx = (int) (flameView->bhs * py + 0.5);
					if (yIdx < 0 || yIdx >= rasterHeight)
						continue;
				}

			}
			else {
				//q.assign(&p);
				q.x = p.x;
				q.y = p.y;
				q.z = p.z;
				flameView->project(&q, ctx);
				px = q.x * flameView->cosa + q.y * flameView->sina + flameView->rcX;
				if ((px < 0) || (px > flameView->camW))
					continue;
				py = q.y * flameView->cosa - q.x * flameView->sina + flameView->rcY;
				if ((py < 0) || (py > flameView->camH))
					continue;

				if ((xf->antialiasAmount > EPSILON) && (xf->antialiasRadius > EPSILON) && (rnd->random() > 1.0 - xf->antialiasAmount)) {
					JWF_FLOAT dr = JWF_EXP(xf->antialiasRadius * JWF_SQRT(-JWF_LOG((JWF_FLOAT) rnd->random()))) - 1.0;
					JWF_FLOAT da = rnd->random() * 2.0 * M_PI;
					JWF_FLOAT sinda, cosda;
					JWF_SINCOS(da, &sinda, &cosda);

					xIdx = (int) (flameView->bws * px + dr * cosda + 0.5);
					if (xIdx < 0 || xIdx >= rasterWidth)
						continue;
					yIdx = (int) (flameView->bhs * py + dr * sinda + 0.5);
					if (yIdx < 0 || yIdx >= rasterHeight)
						continue;
				}
				else {
					xIdx = (int) (flameView->bws * px + 0.5);
					if (xIdx < 0 || xIdx >= rasterWidth)
						continue;
					yIdx = (int) (flameView->bhs * py + 0.5);
					if (yIdx < 0 || yIdx >= rasterHeight)
						continue;
				}

			}

			RasterPoint *rp = &raster[yIdx][xIdx];

			if (p.rgbColor) {
				rp->red += p.redColor;
				rp->green += p.greenColor;
				rp->blue += p.blueColor;
			}
			else {
				int colorIdx = (int) (p.color * paletteIdxScl + 0.5);
				RenderColor color = colorMap[colorIdx];
				rp->red += color.red;
				rp->green += color.green;
				rp->blue += color.blue;
			}
			rp->count++;

			ready = checkStatus(iter);
		}
	}

	bool checkStatus(double iter) {
		if (fmod(iter, 10.0*threadCount) < EPSILON) {
			threadStatus[ctx->threadIdx]=iter;
			double samples=0;
			for (int s = 0; s < threadCount; s++) {
				samples += threadStatus[s];
			}
			if(reportStatus && samples>0) {
        int p=((samples/totalSamples)*100+0.5);
        if(p>0 && p<100 && p%5==0 && p>lastReportedStatus) {
        	FILE *fp= fopen(statusFilename, "w");
        	fprintf(fp,"%03d\n",p);
  				fclose(fp);
  				/*
        	printf("%03d\n",p);
        	fflush(stdout);*/
        	lastReportedStatus=p;
        }
			}
			return samples > totalSamples;
		}
    return false;
	}


	void iterateN() {
		XForm **xForms = flame->xForms;
		RandGen *rnd = ctx->randGen;
		int idx;
		bool ready = false;
		for (double iter = 0; !ready; iter = iter + 1.0) {
			int xfIdx = xf->nextAppliedXFormIdxTable[rnd->random(NEXT_APPLIED_XFORM_TABLE_SIZE)];
			xf = xForms[xfIdx];
			for (int i = 0; i < bundleSize; i++) {
				bundle[i] = rnd->random(poolSize);
			}

			for (int i = 0; i < bundleSize; i++) {
				idx = bundle[i];
				xf->transformPoint(ctx, &affineTArray[idx], &varTArray[idx], &pArray[idx]);
			}

			if (xf->drawMode != DRAWMODE_NORMAL) {
				if (xf->drawMode == DRAWMODE_HIDDEN)
					continue;
				else if ((xf->drawMode == DRAWMODE_OPAQUE) && (ctx->randGen->random() > xf->opacity))
					continue;
			}

			if (flame->finalXFormCount>0) {
				for (int i = 0; i < bundleSize; i++) {
					idx = bundle[i];
					flame->finalXForms[0]->transformPoint(ctx, &affineTArray[idx], &varTArray[idx], &pArray[idx], &qArray[idx]);
					for(int i=1;i<flame->finalXFormCount;i++) {
						flame->finalXForms[i]->transformPoint(ctx, &affineTArray[idx], &varTArray[idx], &qArray[idx], &qArray[idx]);
					}
					flameView->project(&qArray[idx], ctx);
					pxArray[idx] = qArray[idx].x * flameView->cosa + qArray[idx].y * flameView->sina + flameView->rcX;
					if ((pxArray[idx] < 0) || (pxArray[idx] > flameView->camW)) {
						pValidArray[idx] = FALSE;
						continue;
					}
					pyArray[idx] = qArray[idx].y * flameView->cosa - qArray[idx].x * flameView->sina + flameView->rcY;
					if ((pyArray[idx] < 0) || (pyArray[idx] > flameView->camH)) {
						pValidArray[idx] = FALSE;
						continue;
					}
					pValidArray[idx] = TRUE;
				}
			}
			else {
				for (int i = 0; i < bundleSize; i++) {
					idx = bundle[i];
					//qArray[idx].assign(&pArray[idx]);
					qArray[idx].x = pArray[idx].x;
					qArray[idx].y = pArray[idx].y;
					qArray[idx].z = pArray[idx].z;

					flameView->project(&qArray[idx], ctx);
					pxArray[idx] = qArray[idx].x * flameView->cosa + qArray[idx].y * flameView->sina + flameView->rcX;
					if ((pxArray[idx] < 0) || (pxArray[idx] > flameView->camW)) {
						pValidArray[idx] = FALSE;
						continue;
					}
					pyArray[idx] = qArray[idx].y * flameView->cosa - qArray[idx].x * flameView->sina + flameView->rcY;
					if ((pyArray[idx] < 0) || (pyArray[idx] > flameView->camH)) {
						pValidArray[idx] = FALSE;
						continue;
					}
					pValidArray[idx] = TRUE;
				}
			}

			for (int i = 0; i < bundleSize; i++) {
				idx = bundle[i];
				if (pValidArray[idx] == TRUE) {
					int xIdx, yIdx;

					XForm *currXForm =  flame->finalXFormCount>0  ? flame->finalXForms[flame->finalXFormCount-1] : xf;

					if ((currXForm->antialiasAmount > EPSILON) && (currXForm->antialiasRadius > EPSILON) && (rnd->random() > 1.0 - currXForm->antialiasAmount)) {
						JWF_FLOAT dr = JWF_EXP(currXForm->antialiasRadius * JWF_SQRT(-JWF_LOG((JWF_FLOAT) rnd->random()))) - 1.0;
						JWF_FLOAT da = rnd->random() * 2.0 * M_PI;
						JWF_FLOAT sinda, cosda;
						JWF_SINCOS(da, &sinda, &cosda);
						xIdx = (int) (flameView->bws * pxArray[idx] + dr * cosda + 0.5);
						yIdx = (int) (flameView->bhs * pyArray[idx] + dr * sinda + 0.5);
					}
					else {
						xIdx = (int) (flameView->bws * pxArray[idx] + 0.5);
						yIdx = (int) (flameView->bhs * pyArray[idx] + 0.5);
					}

					if (xIdx >= 0 && xIdx < rasterWidth && yIdx >= 0 && yIdx < rasterHeight) {
//            xIdx = (int) (flameView->bws * pxArray[idx] + 0.5);
//            yIdx = (int) (flameView->bhs * pyArray[idx] + 0.5);
						//        if(xIdx<rasterWidth && yIdx<rasterHeight) {
						RasterPoint *rp = &raster[yIdx][xIdx];
						if (pArray[idx].rgbColor) {
							rp->red += pArray[idx].redColor;
							rp->green += pArray[idx].greenColor;
							rp->blue += pArray[idx].blueColor;
						}
						else {
							int colorIdx = (int) (pArray[idx].color * paletteIdxScl + 0.5);
							RenderColor color = colorMap[colorIdx];
							rp->red += color.red;
							rp->green += color.green;
							rp->blue += color.blue;
						}
						rp->count++;
					}
				}
			}

			ready = checkStatus(iter);
		}
	}

};

#endif // __JWF_FLAME_RENDER_THREAD_H__
