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
#ifndef JWFVAR_ELLIPTIC_H_
#define JWFVAR_ELLIPTIC_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class EllipticFunc: public Variation {
public:
	EllipticFunc() {
	}

	const char* getName() const {
		return "elliptic";
	}

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP,
			XYZPoint *pVarTP, float pAmount) {
		float tmp = pAffineTP->getPrecalcSumsq() + 1.0f;
		float x2 = 2.0f * pAffineTP->x;
		float xmax = 0.5f * (sqrtf(tmp + x2) + sqrtf(tmp - x2));
		float a = pAffineTP->x / xmax;
		float b = 1.0f - a * a;
		float ssx = xmax - 1.0f;
		float w = pAmount / (M_PI_2);
		if (b < 0.0f) {
			b = 0.0f;
		}
		else {
			b = sqrtf(b);
		}

		if (ssx < 0.0f) {
			ssx = 0.0f;
		}
		else {
			ssx = sqrtf(ssx);
		}
		pVarTP->x += w * atan2f(a, b);
		if (pContext->randGen->random() < 0.5f) {
			pVarTP->y += w * logf(xmax + ssx);
		}
		else {
			pVarTP->y -= w * logf(xmax + ssx);
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EllipticFunc* makeCopy() {
		return new EllipticFunc(*this);
	}

};

#endif // JWFVAR_ELLIPTIC_H_
