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

#ifndef JWFVAR_BLADE_H_
#define JWFVAR_BLADE_H_

#include "jwf_Variation.h"

class BladeFunc: public Variation {
public:
	BladeFunc() {
	}

	const char* getName() const {
		return "blade";
	}

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float r= pContext->randGen->random() * pAmount * sqrt(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    float sinr = sinf(r);
    float cosr = cosf(r);
    pVarTP->x += pAmount * pAffineTP->x * (cosr + sinr);
    pVarTP->y += pAmount * pAffineTP->x * (cosr - sinr);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	BladeFunc* makeCopy() {
		return new BladeFunc(*this);
	}

};

#endif // JWFVAR_BLADE_H_
