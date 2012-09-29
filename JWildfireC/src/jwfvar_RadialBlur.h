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
#ifndef JWFVAR_RADIALBLUR_H_
#define JWFVAR_RADIALBLUR_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class RadialBlurFunc: public Variation {
public:
	RadialBlurFunc() {
		angle = 0.5f;
		initParameterNames(1, "angle");
	}

	const char* getName() const {
		return "radial_blur";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "angle") == 0) {
			angle = pValue;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_gauss_rnd[0] = pContext->randGen->random();
		_gauss_rnd[1] = pContext->randGen->random();
		_gauss_rnd[2] = pContext->randGen->random();
		_gauss_rnd[3] = pContext->randGen->random();
		_gauss_N = 0;
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float rndG = (_gauss_rnd[0] + _gauss_rnd[1] + _gauss_rnd[2] + _gauss_rnd[3] - 2.0f);
		_gauss_rnd[_gauss_N] = pContext->randGen->random();
		_gauss_N = (_gauss_N + 1) & 3;

		float spin = pAmount * JWF_SIN(angle * M_PI * 0.5f);
		float zoom = pAmount * JWF_COS(angle * M_PI * 0.5f);

		float ra = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
		float alpha = atan2f(pAffineTP->y, pAffineTP->x) + spin * rndG;
		float sina = JWF_SIN(alpha);
		float cosa = JWF_COS(alpha);
		float rz = zoom * rndG - 1;
		pVarTP->x += ra * cosa + rz * pAffineTP->x;
		pVarTP->y += ra * sina + rz * pAffineTP->y;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	RadialBlurFunc* makeCopy() {
		return new RadialBlurFunc(*this);
	}

private:
	float angle;
	float _gauss_rnd[4];
	int _gauss_N;
};

#endif // JWFVAR_RADIALBLUR_H_
