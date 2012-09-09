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
#ifndef __JWF_XFORM_H__
#define __JWF_XFORM_H__

int variationCompare (const void *a, const void *b) {
  int pa=(*(Variation**)a)->getPriority();
  int pb=(*(Variation**)b)->getPriority();
  if(pa<pb) {
    return -1;
  }
  else if(pa>pb) {
    return 1;
  }
  else {
    return 0;
  }
}

struct XForm {
  float weight;
  float color;
  float colorSymmetry;
  float coeff00;
  float coeff01;
  float coeff10;
  float coeff11;
  float coeff20;
  float coeff21;
  float postCoeff00;
  float postCoeff01;
  float postCoeff10;
  float postCoeff11;
  float postCoeff20;
  float postCoeff21;
  float opacity;
  int drawMode;
  // derived properties
  bool __hasPostCoeffs;
  bool __hasCoeffs;
  float __c1;
  float __c2;

  float modifiedWeights[MAX_MOD_WEIGHT_COUNT]; // the same like "xaos" in Apophysis
  int nextAppliedXFormIdxTable[NEXT_APPLIED_XFORM_TABLE_SIZE];
	
  int variationCount;
  Variation **variations;
  Variation **__sortedVariations;
  Variation ***__preparedVariations;
  int __preparedVariationDim;

  void init() {    
	  variationCount=0;
    variations=NULL;
    __sortedVariations=NULL;
    __preparedVariations=NULL;
    __preparedVariationDim=0;
	  //  
    coeff00 = 1.0f;
	  coeff10 = 0.0f;
	  coeff20 = 0.0f;
    coeff01 = 0.0f;
    coeff11 = 1.0f;
	  coeff21 = 0.0f;
	  postCoeff00 = 1.0f;
	  postCoeff10 = 0.0f;
	  postCoeff20 = 0.0f;
	  postCoeff01 = 0.0f;
    postCoeff11 = 1.0f;
	  postCoeff21 = 0.0f;
    weight = 0.0;
    color = 0.0;
    colorSymmetry = 0.0;
    opacity = 0.0;
    drawMode = DRAWMODE_NORMAL;
    __hasPostCoeffs = FALSE;
    __hasCoeffs = FALSE;
    __c1 = __c2 = 0.0f;
    for (int i = 0; i < MAX_MOD_WEIGHT_COUNT; i++) {
      modifiedWeights[i] = 1.0f;
    }
  }


  void free() {
    freeSortedVariations();
    freePreparedVariations();
    freeVariations();
  }

  void freeVariations() {
    if(variations!=NULL) {
      for(int j=0;j<variationCount;j++) {
        Variation *var=variations[j];
        hostFree(var);
      }
      hostFree(variations);
      variations=NULL;
      variationCount=0;
    }
  }

  void freeSortedVariations() {
    if(__sortedVariations!=NULL) {
      hostFree(__sortedVariations);
      __sortedVariations=NULL;
    }
  }
  
  void freePreparedVariations() {
  	if(__preparedVariations!=NULL) {
      for(int i=0;i<__preparedVariationDim;i++) {
        Variation **vars=__preparedVariations[i];
        for(int j=0;j<variationCount;j++) {
          Variation *var=vars[j];
          hostFree(var);
        }
        hostFree(vars);
      }
      __preparedVariations=NULL;
    }
  }

  Variation* addVariation(Variation *newVariation,float pAmount) {
    Variation **newVariations;
  	int newVariationCount=variationCount+1;
	  hostMalloc((void**)&newVariations,newVariationCount*sizeof(Variation*));
	  if(variationCount>0) {
	    memcpy(newVariations, variations, variationCount * sizeof(Variation*));
	  }
	  newVariation->amount=pAmount;
  	newVariations[variationCount]=newVariation;

    variationCount=newVariationCount;
    variations=newVariations;
    freeSortedVariations();
    freePreparedVariations();
	  return newVariation;
  }

  void dump(char *pDumpName) {
    printf("  %s {\n",pDumpName);
  	printf("    weight=%.2f color=%.2f colorSymmetry=%.2f\n",weight,color,colorSymmetry);
	  printf("    coeff00=%.2f coeff10=%.2f coeff20=%.2f\n",coeff00,coeff10,coeff20);
	  printf("    coeff01=%.2f coeff11=%.2f coeff21=%.2f\n",coeff01,coeff11,coeff21);
	  printf("    postCoeff00=%.2f postCoeff10=%.2f postCoeff20=%.2f\n",postCoeff00,postCoeff10,postCoeff20);
	  printf("    postCoeff01=%.2f postCoeff11=%.2f postCoeff21=%.2f\n",postCoeff01,postCoeff11,postCoeff21);
	  printf("    drawMode=%d\n",drawMode);
	  printf("    modifiedWeights=%.2f",modifiedWeights[0]);
	  for(int i=1;i<10 /*MAX_MOD_WEIGHT_COUNT*/;i++) {
      printf(" %.2f",modifiedWeights[i]);
	  }
	  printf("\n");
    printf("    nextAppliedXFormTable=%d",nextAppliedXFormIdxTable[0]);
 	  for(int i=1;i<10 /*NEXT_APPLIED_XFORM_TABLE_SIZE*/;i++) {
      printf(" %d",nextAppliedXFormIdxTable[i]);
 	  }
	  printf("\n");

	  for(int i=0;i<variationCount;i++) {
      variations[i]->dump();
	  }
	  printf("  }\n");
  }
  
  void prepareXForm(int pThreadCount) {
    // precalculate those variables to simplify the expression: 
    //   pDstPoint.color = (pSrcPoint.color + color) * 0.5 * (1 - colorSymmetry) + colorSymmetry * pSrcPoint.color;
    // to get:
    //   pDstPoint.color = pSrcPoint.color * c1 + c2;
    __c1 = (1 + colorSymmetry) * 0.5f;
    __c2 = color * (1 - colorSymmetry) * 0.5f;
    updateHasCoeffs();
    updateHasPostCoeffs();
    sortVariations();
    createPreparedVariations(pThreadCount);
  }

  void createPreparedVariations(int pThreadCount) {
    freePreparedVariations();
    hostMalloc((void**)&__preparedVariations,sizeof(Variation**)*pThreadCount);
    for(int i=0;i<pThreadCount;i++) {
      hostMalloc((void**)&__preparedVariations[i],variationCount*sizeof(Variation*));
      Variation** variations=__preparedVariations[i];
      for(int j=0;j<variationCount;j++) {
        Variation *srcVar=__sortedVariations[j];
        variations[j]=srcVar->makeCopy();
      }
    }
    __preparedVariationDim=pThreadCount;
  }

  void updateHasPostCoeffs() {
    __hasPostCoeffs = fabs(postCoeff00 - 1.0f) > EPSILON || fabs(postCoeff01) > EPSILON || fabs(postCoeff10) > EPSILON
        || fabs(postCoeff11 - 1.0f) > EPSILON || fabs(postCoeff20) > EPSILON || fabs(postCoeff21) > EPSILON;
  }

  void updateHasCoeffs() {
    __hasCoeffs = fabs(coeff00 - 1.0f) > EPSILON || fabs(coeff01) > EPSILON || fabs(coeff10) > EPSILON
        || fabs(coeff11 - 1.0f) > EPSILON || fabs(coeff20) > EPSILON || fabs(coeff21) > EPSILON;
  }
  

  void sortVariations() {
    freeSortedVariations();
    freePreparedVariations();
    hostMalloc((void**)&__sortedVariations, variationCount * sizeof(Variation*));
    memcpy(__sortedVariations, variations, variationCount * sizeof(Variation*));
    /*
    if(variationCount>1) {
      printf("UNSORTED:");
      for(int i=0;i<variationCount;i++) {
        printf(" %s",__sortedVariations[i]->getName());
      }
      printf("\n");
    }
    */
    if(variationCount>1) {
      qsort(__sortedVariations,variationCount, sizeof(Variation*), variationCompare);
    }
    /*
    if(variationCount>1) {
      printf("SORTED:");
      for(int i=0;i<variationCount;i++) {
        printf(" %s",__sortedVariations[i]->getName());
      }
      printf("\n");
    }
    */
  }

  void transformPoint(FlameTransformationContext *pContext, XYZPoint *pAffineT, XYZPoint *pVarT, XYZPoint *pSrcPoint, XYZPoint *pDstPoint) {
    //pAffineT->clear();


  	pAffineT->rgbColor = FALSE;
  	pAffineT->redColor = pAffineT->greenColor = pAffineT->blueColor = 0.0f;
  //	pAffineT->x = pAffineT->y = pAffineT->z = pAffineT->color = 0.0f;
  //	pAffineT->sumsq = pAffineT->sqrt = pAffineT->atan = pAffineT->atanYX = pAffineT->sinA = pAffineT->cosA = 0.0f;
  //	pAffineT->validSumsq = pAffineT->validSqrt = pAffineT->validAtan = pAffineT->validAtanYX = pAffineT->validSinA = pAffineT->validCosA = FALSE;


    pAffineT->color = pSrcPoint->color * __c1 + __c2;
    if (__hasCoeffs) {
      pAffineT->x = coeff00 * pSrcPoint->x + coeff10 * pSrcPoint->y + coeff20;
      pAffineT->y = coeff01 * pSrcPoint->x + coeff11 * pSrcPoint->y + coeff21;
    }
    else {
      pAffineT->x = pSrcPoint->x;
      pAffineT->y = pSrcPoint->y;
    }
    pAffineT->z = pSrcPoint->z;

    //pVarT->invalidate();
    pVarT->x = pVarT->y = pVarT->z = pVarT->color = 0.0f;


    pVarT->color = pAffineT->color;
    pVarT->rgbColor = pAffineT->rgbColor;
    pVarT->redColor = pAffineT->redColor;
    pVarT->greenColor = pAffineT->greenColor;
    pVarT->blueColor = pAffineT->blueColor;

    Variation** vars=__preparedVariations[pContext->threadIdx];
    for (int i = 0; i < variationCount; i++) {
      Variation *variation = vars[i];
      vars[i]->transform(pContext, pAffineT, pVarT,variation->amount);
    //  if (variation->getPriority() < 0) {
     //   pAffineT->invalidate();
    // }
    }
    pDstPoint->color = pVarT->color;
    pDstPoint->rgbColor = pVarT->rgbColor;
    pDstPoint->redColor = pVarT->redColor;
    pDstPoint->greenColor = pVarT->greenColor;
    pDstPoint->blueColor = pVarT->blueColor;
    if (__hasPostCoeffs) {
      float px = postCoeff00 * pVarT->x + postCoeff10 * pVarT->y + postCoeff20;
      float py = postCoeff01 * pVarT->x + postCoeff11 * pVarT->y + postCoeff21;
      float pz = pVarT->z;
      pDstPoint->x = px;
      pDstPoint->y = py;
      pDstPoint->z = pz;
    }
    else {
			pDstPoint->x = pVarT->x;
			pDstPoint->y = pVarT->y;
			pDstPoint->z = pVarT->z;
    }
  }

  void transformPoint(FlameTransformationContext *pContext, XYZPoint *pAffineT, XYZPoint *pVarT, XYZPoint *pPoint) {
    //pAffineT->clear();


  	pAffineT->rgbColor = FALSE;
  	pAffineT->redColor = pAffineT->greenColor = pAffineT->blueColor = 0.0f;
  	pAffineT->x = pAffineT->y = pAffineT->z = pAffineT->color = 0.0f;
  	pAffineT->sumsq = pAffineT->sqrt = pAffineT->atan = pAffineT->atanYX = pAffineT->sinA = pAffineT->cosA = 0.0f;
  	pAffineT->validSumsq = pAffineT->validSqrt = pAffineT->validAtan = pAffineT->validAtanYX = pAffineT->validSinA = pAffineT->validCosA = FALSE;


    pAffineT->color = pPoint->color * __c1 + __c2;
    if (__hasCoeffs) {
      pAffineT->x = coeff00 * pPoint->x + coeff10 * pPoint->y + coeff20;
      pAffineT->y = coeff01 * pPoint->x + coeff11 * pPoint->y + coeff21;
    }
    else {
      pAffineT->x = pPoint->x;
      pAffineT->y = pPoint->y;
    }
    pAffineT->z = pPoint->z;

    //pVarT->invalidate();
    pVarT->x = pVarT->y = pVarT->z = pVarT->color = 0.0f;


    pVarT->color = pAffineT->color;
    pVarT->rgbColor = pAffineT->rgbColor;
    pVarT->redColor = pAffineT->redColor;
    pVarT->greenColor = pAffineT->greenColor;
    pVarT->blueColor = pAffineT->blueColor;

    Variation** vars=__preparedVariations[pContext->threadIdx];
    for (int i = 0; i < variationCount; i++) {
      Variation *variation = vars[i];
      vars[i]->transform(pContext, pAffineT, pVarT,variation->amount);
      if (variation->getPriority() < 0) {
        pAffineT->invalidate();
      }
    }
    pPoint->color = pVarT->color;
    pPoint->rgbColor = pVarT->rgbColor;
    pPoint->redColor = pVarT->redColor;
    pPoint->greenColor = pVarT->greenColor;
    pPoint->blueColor = pVarT->blueColor;
    if (__hasPostCoeffs) {
      float px = postCoeff00 * pVarT->x + postCoeff10 * pVarT->y + postCoeff20;
      float py = postCoeff01 * pVarT->x + postCoeff11 * pVarT->y + postCoeff21;
      float pz = pVarT->z;
      pPoint->x = px;
      pPoint->y = py;
      pPoint->z = pz;
    }
    else {
			pPoint->x = pVarT->x;
			pPoint->y = pVarT->y;
			pPoint->z = pVarT->z;
    }
  }
};
#endif // __JWF_XFORM_H__

