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

#ifndef JWFVAR_SINH_H_
#define JWFVAR_SINH_H_

#include "jwf_Variation.h"

class SinhFunc: public Variation {
public:
	SinhFunc() {
	}

	const char* getName() const {
		return "sinh";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float sinhsin = sinf(pAffineTP->y);
    float sinhcos = cosf(pAffineTP->y);
    float sinhsinh = sinhf(pAffineTP->x);
    float sinhcosh = coshf(pAffineTP->x);
    pVarTP->x += pAmount * sinhsinh * sinhcos;
    pVarTP->y += pAmount * sinhcosh * sinhsin;
		if (pContext->isPreserveZCoordinate) {
      pVarTP->z += pAmount * pAffineTP->z;
    }
	}

	SinhFunc* makeCopy() {
		return new SinhFunc(*this);
	}

};

#endif // JWFVAR_SINH_H_
