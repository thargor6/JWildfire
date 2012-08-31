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
#ifndef __JWF_FLAME_H__
#define __JWF_FLAME_H__

struct Flame {
  float centreX;
  float centreY;
  int width;
  int height;
  float camPitch;
  float camYaw;
  float camPerspective;
  float camRoll;
  float camZoom;
  float camZ;
  float camDOF;
  int spatialOversample;
  int colorOversample;
  float spatialFilterRadius;
  float sampleDensity;
  int bgColorRed;
  int bgColorGreen;
  int bgColorBlue;
  float gamma;
  float gammaThreshold;
  float pixelsPerUnit;
  int whiteLevel;
  float brightness;
  float contrast;
  float vibrancy;
  bool  preserveZ;

  RGBPalette *palette;
  XForm **xForms;
  int xFormCount;
  XForm *finalXForm;

  void dump() {
    printf("Flame {\n");
    printf("  centreX=%.2f centreY=%.2f width=%d height=%d\n",centreX,centreY,width,height);
  	printf("  camRoll=%.2f camPitch=%.2f camYaw=%.2f\n",camRoll,camPitch,camYaw);
	  printf("  camZoom=%.2f camPerspective=%.2f camDOF=%.2f camZ=%.2f\n",camZoom,camPerspective,camDOF,camZ);
	  printf("  sampleDensity=%.3f spatialFilterRadius=%.2f colorOversample=%d\n",sampleDensity,spatialFilterRadius,colorOversample);
	  printf("  gamma=%.2f gammaThreshold=%.2f brightness=%.2f contrast=%.2f\n",gamma,gammaThreshold,brightness,contrast);
	  printf("  pixelsPerUnit=%.2f vibrancy=%.2f whiteLevel=%d preserveZ=%d\n",pixelsPerUnit,vibrancy,whiteLevel,preserveZ);
	  printf("  bgColorRed=%d bgColorGreen=%d bgColorBlue=%d\n",bgColorRed,bgColorGreen,bgColorBlue);
	  printf("  spatialOversample=%d\n",spatialOversample);
	  for(int i=0;i<xFormCount;i++) {
      xForms[i]->dump("XForm");
	  }
	  if(finalXForm!=NULL) {
      finalXForm->dump("FinalXForm");
	  }
	  if(palette!=NULL) {
      palette->dump();
	  }
    printf("}\n");
  }

  void create() {
	  palette=NULL;
    xForms=NULL;
    xFormCount=0;
    finalXForm=NULL;
    //
	  spatialFilterRadius = 0.0f;
    sampleDensity = 100.0f;
    bgColorRed = bgColorGreen = bgColorBlue = 0;
    brightness = 4.0f;
    contrast = 1.0f;
    vibrancy = 1.0f;
    gamma = 4.0f;
    centreX = 0.0f;
    centreY = 0.0f;
    camRoll = 0.0f;
    camPitch = 0.0f;
    camYaw = 0.0f;
    camPerspective = 0.0f;
    camZoom = 1.0f;
    camZ = 0.0f;
    camDOF = 0.0f;
    spatialOversample = 1;
    colorOversample = 1;
    gammaThreshold = 0.04f;
    pixelsPerUnit = 50;
    whiteLevel = 200;
	  preserveZ=FALSE;
    hostMalloc((void**)&palette,sizeof(RGBPalette));
    palette->create();
  }

  void free() {
    if(palette!=NULL) {
      hostFree(palette);
      palette=NULL;
    }
  }

  void prepareFlame(FlameTransformationContext *pFlameTransformationContext,int pThreadCount) {
    float tp[MAX_MOD_WEIGHT_COUNT];
    int n = xFormCount;
    // sort and init variations
    for (int i=0;i<xFormCount;i++)  {
      xForms[i]->prepareXForm(pThreadCount);
      for (int j=0;j<xForms[i]->variationCount;j++) {
        //Variation *var=xForms[i]->__sortedVariations[j];
        //var->init(pFlameTransformationContext, var->amount);
        for(int k=0;k<pThreadCount;k++) {
          pFlameTransformationContext->threadIdx=k;
          Variation *var=xForms[i]->__preparedVariations[k][j];
          var->init(pFlameTransformationContext, var->amount);
        }
      }
    }
    // sort and init variations of final transform
    if (finalXForm != NULL) {
      finalXForm->prepareXForm(pThreadCount);
      for (int i=0;i<finalXForm->variationCount;i++) {
        //Variation *var=finalXForm->__sortedVariations[i];
        //var->init(pFlameTransformationContext, var->amount);
        for(int k=0;k<pThreadCount;k++) {
          pFlameTransformationContext->threadIdx=k;
          Variation *var=finalXForm->__preparedVariations[k][i];
          var->init(pFlameTransformationContext, var->amount);          
        }
      }
    }
    // calculate modified weights tables
    for (int k = 0; k < n; k++) {
      float totValue = 0.0f;
      XForm *xForm = xForms[k];
      memset(xForm->nextAppliedXFormIdxTable, -1, NEXT_APPLIED_XFORM_TABLE_SIZE*sizeof(int));
      for (int i = 0; i < n; i++) {
        tp[i] = xForms[i]->weight * xForms[k]->modifiedWeights[i];
        totValue = totValue + tp[i];
      }

      if (totValue > 0.0f) {
        float loopValue = 0.0f;
        for (int i = 0; i < NEXT_APPLIED_XFORM_TABLE_SIZE; i++) {
          float totalProb = 0;
          int j = -1;
          do {
            j++;
            totalProb = totalProb + tp[j];
          }
          while (!((totalProb > loopValue) || (j == n - 1)));
          xForm->nextAppliedXFormIdxTable[i] = j;
          loopValue = loopValue + totValue / (float) NEXT_APPLIED_XFORM_TABLE_SIZE;
        }
      }
      else {
        for (int i = 0; i < NEXT_APPLIED_XFORM_TABLE_SIZE - 1; i++) {
          xForm->nextAppliedXFormIdxTable[i] = -1;
        }
      }
    }
    pFlameTransformationContext->threadIdx=0;
  }

  void addXForm(XForm *pXForm) {
  	if(xFormCount==0) {
      hostMalloc((void**)&xForms, sizeof(XForm*));
      xForms[0]=pXForm;
      xFormCount=1;
  	}
  	else {
      XForm **newXForms;
      hostMalloc((void**)&newXForms, (xFormCount+1)*sizeof(XForm*));
      memcpy(newXForms, xForms, xFormCount*sizeof(XForm*));
      newXForms[xFormCount]=pXForm;
      hostFree(xForms);
      xForms=newXForms;
      xFormCount++;
  	}


  }

};

#endif // __JWF_FLAME_H__
