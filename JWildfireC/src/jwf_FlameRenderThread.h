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

struct FlameRenderThread {
  Flame *flame;
  FlameTransformationContext *ctx;
  long samples;
  FlameView *flameView;
  RasterPoint **raster;
  int rasterWidth;
  int rasterHeight;
  RenderColor *colorMap;
  float paletteIdxScl;
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
  XYZPoint *qArray;
  float *pxArray, *pyArray;
  bool *pValidArray;
  int *bundle;

  XForm *xf;
  long startIter;

  void init(Flame *pFlame, FlameTransformationContext *pCtx, long pSamples,FlameView *pFlameView,RasterPoint **pRaster,int pRasterWidth,int pRasterHeight,RenderColor *pColorMap,float pPaletteIdxScl,int pPoolSize, int pBundleSize) {    
    xf=NULL;
    flame=pFlame;
    ctx=pCtx;
    samples=pSamples;
    flameView=pFlameView;
    raster=pRaster;
    rasterWidth=pRasterWidth;
    rasterHeight=pRasterHeight;
    colorMap=pColorMap;
    paletteIdxScl=pPaletteIdxScl;
    poolSize=pPoolSize;
    if(poolSize<1)
      poolSize=1;
    bundleSize=pBundleSize;
    if(bundleSize<1)
      bundleSize=1;
    else if(bundleSize>poolSize)
      bundleSize=poolSize;
    affineTArray=varTArray=pArray=qArray=NULL;
    pxArray=pyArray=NULL;
    pValidArray=NULL;
  }

  void initState() {
    if(poolSize<=1)
      initState1();
    else
      initStateN();
  }

  void initStateN() {
    XForm **xForms=flame->xForms;
    startIter = 0;
    // TODO: free
    affineTArray = new XYZPoint[poolSize];
    varTArray = new XYZPoint[poolSize];
    pArray = new XYZPoint[poolSize];
    qArray = new XYZPoint[poolSize];

    hostMalloc((void**)&pxArray, poolSize*sizeof(float));
    hostMalloc((void**)&pyArray, poolSize*sizeof(float));
    hostMalloc((void**)&pValidArray, poolSize*sizeof(bool));
    hostMalloc((void**)&bundle, bundleSize*sizeof(int));

    xf = flame->xForms[0];
    
    // TODO: free
    for(int i=0;i<poolSize;i++) {
      pArray[i].x = 2.0f * ctx->randGen->random() - 1.0f;
      pArray[i].y = 2.0f * ctx->randGen->random() - 1.0f;
      pArray[i].z = 0.0f;
      pArray[i].color = ctx->randGen->random();
      xf->transformPoint(ctx, &affineTArray[i], &varTArray[i], &pArray[i], &pArray[i]);
    }

    
    for (int iter = 0; iter <= INITIAL_ITERATIONS; iter++) {
      int xfIdx=xf->nextAppliedXFormIdxTable[ctx->randGen->random(NEXT_APPLIED_XFORM_TABLE_SIZE)];
      xf = xfIdx>=0 ? xForms[xfIdx] : NULL;
      if (xf == NULL) {
        return;
      }
      for(int i=0;i<poolSize;i++) {
        xf->transformPoint(ctx, &affineTArray[i], &varTArray[i], &pArray[i], &pArray[i]);
      }
    }
  }

  void initState1() {
    XForm **xForms=flame->xForms;
    startIter = 0;
    p.x = 2.0f * ctx->randGen->random() - 1.0f;
    p.y = 2.0f * ctx->randGen->random() - 1.0f;
    p.z = 0.0f;
    p.color = ctx->randGen->random();

    xf = flame->xForms[0];
    xf->transformPoint(ctx, &affineT, &varT, &p, &p);
    for (int i = 0; i <= INITIAL_ITERATIONS; i++) {
      int xfIdx=xf->nextAppliedXFormIdxTable[ctx->randGen->random(NEXT_APPLIED_XFORM_TABLE_SIZE)];
      xf = xfIdx>=0 ? xForms[xfIdx] : NULL;
      if (xf == NULL) {
        return;
      }
      xf->transformPoint(ctx, &affineT, &varT, &p, &p);
    }
  }

  void iterate() {
    if(poolSize<=1)
      iterate1();
    else
      iterateN();
  }

  void iterate1() {
    XForm **xForms=flame->xForms;
    XForm *finalXForm = flame->finalXForm;
    RandGen *rnd=ctx->randGen;
    for (long iter = startIter; iter < samples; iter++) {
      int xfIdx=xf->nextAppliedXFormIdxTable[ctx->randGen->random(NEXT_APPLIED_XFORM_TABLE_SIZE)];
      xf = xForms[xfIdx];
      //xf->transformPoint(ctx, &affineT, &varT, &p, &p);
      xf->transformPoint(ctx, &affineT, &varT, &p);

      if(xf->drawMode!=DRAWMODE_NORMAL) {
				if (xf->drawMode == DRAWMODE_HIDDEN)
					continue;
				else if ((xf->drawMode == DRAWMODE_OPAQUE) && (ctx->randGen->random() > xf->opacity))
					continue;
      }

      float px, py;
      if (finalXForm != NULL) {
        finalXForm->transformPoint(ctx, &affineT, &varT, &p, &q);
        flameView->project(&q,ctx);
        px = q.x * flameView->cosa + q.y * flameView->sina + flameView->rcX;
        if ((px < 0) || (px > flameView->camW))
          continue;
        py = q.y * flameView->cosa - q.x * flameView->sina + flameView->rcY;
        if ((py < 0) || (py > flameView->camH))
          continue;
      }
      else {
        q.assign(&p);
        flameView->project(&q,ctx);
        px = q.x * flameView->cosa + q.y * flameView->sina + flameView->rcX;
        if ((px < 0) || (px > flameView->camW))
          continue;
        py = q.y * flameView->cosa - q.x * flameView->sina + flameView->rcY;
        if ((py < 0) || (py > flameView->camH))
          continue;
      }
      int xIdx, yIdx;


 //#ifdef ANTIAALIAS
      if(rnd->random()<0.5) {
        float dr = expf(0.5f * sqrtf(-logf(rnd->random())))-1.0f;
        float dtheta = rnd->random() * 2.0f * M_PI;
        xIdx = (int) (flameView->bws * px + dr*cosf(dtheta) + 0.5f);
        yIdx = (int) (flameView->bhs * py + dr*sinf(dtheta) + 0.5f);
      }
      else {
        xIdx = (int) (flameView->bws * px + 0.5f);
        yIdx = (int) (flameView->bhs * py + 0.5f);
      }
    if(xIdx<0 || xIdx>=rasterWidth || yIdx<0 || yIdx>=rasterHeight) {

//#endif
//       xIdx = (int) (flameView->bws * px + 0.5f);
//       yIdx = (int) (flameView->bhs * py + 0.5f);
//      if(xIdx>=rasterWidth || yIdx>=rasterHeight) {
        continue;
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
    }
  }

  void iterateN() {
    XForm **xForms=flame->xForms;
    RandGen *rnd=ctx->randGen;
    register int idx;
    for (long iter = startIter; iter < samples; iter++) {
      int xfIdx=xf->nextAppliedXFormIdxTable[rnd->random(NEXT_APPLIED_XFORM_TABLE_SIZE)];
      xf = xForms[xfIdx];
      for(int i=0;i<bundleSize;i++) {
        bundle[i]=rnd->random(poolSize);
      }

      for(int i=0;i<bundleSize;i++) {
        idx=bundle[i];
        xf->transformPoint(ctx, &affineTArray[idx], &varTArray[idx], &pArray[idx]);
      }

      if(xf->drawMode != DRAWMODE_NORMAL) {
				if (xf->drawMode == DRAWMODE_HIDDEN)
					continue;
				else if ((xf->drawMode == DRAWMODE_OPAQUE) && (ctx->randGen->random() > xf->opacity))
					continue;
      }

      XForm *finalXForm = flame->finalXForm;
      if (finalXForm != NULL) {
        for(int i=0;i<bundleSize;i++) {
          idx=bundle[i];
          finalXForm->transformPoint(ctx, &affineTArray[idx], &varTArray[idx], &pArray[idx], &qArray[idx]);
          flameView->project(&qArray[idx],ctx);
          pxArray[idx] = qArray[idx].x * flameView->cosa + qArray[idx].y * flameView->sina + flameView->rcX;
          if ((pxArray[idx] < 0) || (pxArray[idx] > flameView->camW)) {
            pValidArray[idx]=FALSE;
            continue;
          }
          pyArray[idx] = qArray[idx].y * flameView->cosa - qArray[idx].x * flameView->sina + flameView->rcY;
          if ((pyArray[idx] < 0) || (pyArray[idx] > flameView->camH)) {
            pValidArray[idx]=FALSE;
            continue;
          }
          pValidArray[idx]=TRUE;
        }
      }
      else {
        for(int i=0;i<bundleSize;i++) {
          idx=bundle[i];
          qArray[idx].assign(&pArray[idx]);
          flameView->project(&qArray[idx],ctx);
          pxArray[idx] = qArray[idx].x * flameView->cosa + qArray[idx].y * flameView->sina + flameView->rcX;
          if ((pxArray[idx] < 0) || (pxArray[idx] > flameView->camW)) {
            pValidArray[idx]=FALSE;            
            continue;
          }
          pyArray[idx] = qArray[idx].y * flameView->cosa - qArray[idx].x * flameView->sina + flameView->rcY;
          if ((pyArray[idx] < 0) || (pyArray[idx] > flameView->camH)) {
            pValidArray[idx]=FALSE;            
            continue;
          }
          pValidArray[idx]=TRUE;
        }
      }

      for(int i=0;i<bundleSize;i++) {     
        idx=bundle[i];
        if(pValidArray[idx]==TRUE) {
          int xIdx, yIdx;

//#ifdef ALIAS
            if(rnd->random()<0.5) {
              float dr = expf(0.5f * sqrtf(-logf(rnd->random())))-1.0f;
              float dtheta = rnd->random() * 2.0f * M_PI;
              xIdx = (int) (flameView->bws * pxArray[idx] + dr*cosf(dtheta) + 0.5f);
              yIdx = (int) (flameView->bhs * pyArray[idx] + dr*sinf(dtheta) + 0.5f);
            }
            else {
              xIdx = (int) (flameView->bws * pxArray[idx] + 0.5f);
              yIdx = (int) (flameView->bhs * pyArray[idx] + 0.5f);
            }
          if(xIdx>=0 && xIdx<rasterWidth && yIdx>=0 && yIdx<rasterHeight) {
//#endif
//            xIdx = (int) (flameView->bws * pxArray[idx] + 0.5f);
//            yIdx = (int) (flameView->bhs * pyArray[idx] + 0.5f);
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
    }
  }

};

#endif // __JWF_FLAME_RENDER_THREAD_H__
