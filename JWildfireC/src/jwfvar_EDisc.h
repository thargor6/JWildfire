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

#ifndef JWFVAR_EDISC_H_
#define JWFVAR_EDISC_H_

#include "jwf_Variation.h"

class EDiscFunc: public Variation {
public:
	EDiscFunc() {
	}

	const char* getName() const {
		return "edisc";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float tmp = pAffineTP->getPrecalcSumsq() + 1.0f;
		float tmp2 = 2.0f * pAffineTP->x;
		float r1 = JWF_SQRT(tmp + tmp2);
		float r2 = JWF_SQRT(tmp - tmp2);
		float xmax = (r1 + r2) * 0.5f;
		float a1 = JWF_LOG(xmax + JWF_SQRT(xmax - 1.0));
		float a2 = -acosf(pAffineTP->x / xmax);
		float w = pAmount / 11.57034632f;

		float snv = JWF_SIN(a1);
		float csv = JWF_COS(a1);
		float snhu = JWF_SINH(a2);
		float cshu = JWF_COSH(a2);

		if (pAffineTP->y > 0.0) {
			snv = -snv;
		}

		pVarTP->x += w * cshu * csv;
		pVarTP->y += w * snhu * snv;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EDiscFunc* makeCopy() {
		return new EDiscFunc(*this);
	}

};

#endif // JWFVAR_EDISC_H_
