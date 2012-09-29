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

#ifndef JWFVAR_PRE_BLUR_H_
#define JWFVAR_PRE_BLUR_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class PreBlurFunc: public Variation {
public:
	PreBlurFunc() {
	}

	const char* getName() const {
		return "pre_blur";
	}

	int const getPriority() {
		return -1;
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_gauss_rnd[0] = pContext->randGen->random();
		_gauss_rnd[1] = pContext->randGen->random();
		_gauss_rnd[2] = pContext->randGen->random();
		_gauss_rnd[3] = pContext->randGen->random();
		_gauss_N = 0;
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float r = pContext->randGen->random() * 2 * M_PI;
		float sina = JWF_SIN(r);
		float cosa = JWF_COS(r);
		r = pAmount * (_gauss_rnd[0] + _gauss_rnd[1] + _gauss_rnd[2] + _gauss_rnd[3] - 2.0f);
		_gauss_rnd[_gauss_N] = pContext->randGen->random();
		_gauss_N = (_gauss_N + 1) & 3;
		pAffineTP->x += r * cosa;
		pAffineTP->y += r * sina;
	}

	PreBlurFunc* makeCopy() {
		return new PreBlurFunc(*this);
	}

private:
	float _gauss_rnd[4];
	int _gauss_N;
};

#endif // JWFVAR_PRE_BLUR_H_
