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

#ifndef JWFVAR_TWINTRIAN_H_
#define JWFVAR_TWINTRIAN_H_

#include "jwf_Variation.h"

class TwintrianFunc: public Variation {
public:
	TwintrianFunc() {
	}

	const char* getName() const {
		return "twintrian";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float r = pContext->randGen->random() * pAmount * pAffineTP->getPrecalcSqrt();

		float sinr = JWF_SIN(r);
		float cosr = JWF_COS(r);
		float diff = JWF_LOG10(sinr * sinr) + cosr;

		if (fabsf(diff) < EPSILON) {
			diff = -30.0f;
		}

		pVarTP->x += pAmount * pAffineTP->x * diff;
		pVarTP->y += pAmount * pAffineTP->x * (diff - sinr * M_PI);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	TwintrianFunc* makeCopy() {
		return new TwintrianFunc(*this);
	}

};

#endif // JWFVAR_TWINTRIAN_H_
