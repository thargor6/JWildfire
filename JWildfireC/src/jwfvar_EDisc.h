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


#include "jwf_Variation.h"

class EDiscFunc: public Variation {
public:
	EDiscFunc() {
	}

	const char* getName() const {
		return "edisc";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT tmp = pAffineTP->getPrecalcSumsq() + 1.0;
		JWF_FLOAT tmp2 = 2.0 * pAffineTP->x;
		JWF_FLOAT r1 = JWF_SQRT(tmp + tmp2);
		JWF_FLOAT r2 = JWF_SQRT(tmp - tmp2);
		JWF_FLOAT xmax = (r1 + r2) * 0.5;
		JWF_FLOAT a1 = JWF_LOG(xmax + JWF_SQRT(xmax - 1.0));
		JWF_FLOAT a2 = -JWF_ACOS(pAffineTP->x / xmax);
		JWF_FLOAT w = pAmount / 11.57034632;

		JWF_FLOAT snv = JWF_SIN(a1);
		JWF_FLOAT csv = JWF_COS(a1);
		JWF_FLOAT snhu = JWF_SINH(a2);
		JWF_FLOAT cshu = JWF_COSH(a2);

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

