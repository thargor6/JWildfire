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
#ifndef JWFVAR_ZBLUR_H_
#define JWFVAR_ZBLUR_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class ZBlurFunc: public Variation {
public:
	ZBlurFunc() {
	}

	const char* getName() const {
		return "zblur";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    pVarTP->z += pAmount * (_gauss_rnd[0] + _gauss_rnd[1] + _gauss_rnd[2] + _gauss_rnd[3] - 2.0f);
    _gauss_rnd[_gauss_N] = _randGen->random();
    _gauss_N = (_gauss_N + 1) & 3;
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, float pAmount) {
		_randGen=pContext->randGen;
    _gauss_rnd[0] = _randGen->random();
    _gauss_rnd[1] = _randGen->random();
    _gauss_rnd[2] = _randGen->random();
    _gauss_rnd[3] = _randGen->random();
    _gauss_N = 0;
	}

	ZBlurFunc* makeCopy() {
		return new ZBlurFunc(*this);
	}

private:
  float _gauss_rnd[4];
  RandGen *_randGen;
  int _gauss_N;
};

#endif // JWFVAR_ZBLUR_H_
