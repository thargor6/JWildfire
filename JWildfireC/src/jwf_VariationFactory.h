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

#ifndef __JWF_VARIATION_FACTORY_H__
#define __JWF_VARIATION_FACTORY_H__

#include "stdio.h"
#include "string.h"
#include "jwf_XYZPoint.h"
#include "jwf_FlameTransformationContext.h"

#include "jwfvar_Bent.h"
#include "jwfvar_Bipolar.h"
#include "jwfvar_Blur.h"
#include "jwfvar_Bubble.h"
#include "jwfvar_Butterfly.h"
#include "jwfvar_Butterfly3D.h"
#include "jwfvar_Cell.h"
#include "jwfvar_Circlize.h"
#include "jwfvar_Cos.h"
#include "jwfvar_Cross.h"
#include "jwfvar_Curl.h"
#include "jwfvar_Curl3D.h"
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
#include "jwfvar_Linear.h"
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
#include "jwfvar_Square.h"
#include "jwfvar_Square3D.h"
#include "jwfvar_Unpolar.h"
#include "jwfvar_Waves2.h"
#include "jwfvar_Waves4WF.h"
#include "jwfvar_XHeart.h"
#include "jwfvar_ZCone.h"
#include "jwfvar_ZScale.h"
#include "jwfvar_ZTranslate.h"

#define POOL_SIZE 1000

class VariationFactory {
public:
	VariationFactory() {
		variationCount = 0;
		variations = (Variation**) malloc(POOL_SIZE * sizeof(Variation*));
		initVariations();
	}

	~VariationFactory() {
		if (variations != NULL) {
			for (int i = 0; i < variationCount; i++) {
				delete (variations[i]);
			}
			free(variations);
			variations = NULL;
			variationCount = 0;
		}
	}

	int getVariationCount() {
		return variationCount;
	}

	Variation **getVariations() {
		return variations;
	}

	Variation* newInstance(char *varName) {
		Variation* srcVar=findVariation(varName);
		if(srcVar==NULL) {
		  printf("Variation %s unknown\n", varName);
		  exit(-1);
		}
		return srcVar->makeCopy();
	}

	Variation* findVariation(char *varName) {
		for(int i=0;i<variationCount;i++) {
			if(strcmp(varName, variations[i]->getName())==0) {
        return variations[i];
			}
		}
		return NULL;
	}

private:
	int variationCount;
	Variation **variations;

	void initVariations() {
		addVariation(new BentFunc());
		addVariation(new BipolarFunc());
		addVariation(new BlurFunc());
		addVariation(new BubbleFunc());
		addVariation(new ButterflyFunc());
		addVariation(new Butterfly3DFunc());
		addVariation(new CellFunc());
		addVariation(new CirclizeFunc());
		addVariation(new CosFunc());
		addVariation(new CrossFunc());
		addVariation(new CurlFunc());
		addVariation(new Curl3DFunc());
		addVariation(new CylinderFunc());
		addVariation(new EllipticFunc());
		addVariation(new EpispiralFunc());
		addVariation(new EpispiralWFFunc());
		addVariation(new EyefishFunc());
		addVariation(new FlowerFunc());
		addVariation(new FluxFunc());
		addVariation(new GaussianBlurFunc());
		addVariation(new Julia3DFunc());
		addVariation(new JuliaNFunc());
		addVariation(new LinearFunc());
		addVariation(new Linear3DFunc());
		addVariation(new LissajousFunc());
		addVariation(new LogFunc());
		addVariation(new PieFunc());
		addVariation(new Pie3DFunc());
		addVariation(new PreBlurFunc());
		addVariation(new PreCircleCropFunc());
		addVariation(new RadialBlurFunc());
		addVariation(new SphericalFunc());
		addVariation(new Spherical3DFunc());
		addVariation(new SplitsFunc());
		addVariation(new SquareFunc());
		addVariation(new Square3DFunc());
		addVariation(new UnpolarFunc());
		addVariation(new Waves2Func());
		addVariation(new Waves4WFFunc());
		addVariation(new XHeartFunc());
		addVariation(new ZConeFunc());
		addVariation(new ZScaleFunc());
		addVariation(new ZTranslateFunc());
	}

	void addVariation(Variation *var) {
		if(variationCount>=POOL_SIZE) {
			printf("Variation pool size %d exceeded\n",POOL_SIZE);
			exit(-1);
		}
		variations[variationCount++]=var;
	}

};

#undef POOL_SIZE

#endif // __JWF_VARIATION_FACTORY_H__
