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

#ifndef JWFVAR_DIAMOND_H_
#define JWFVAR_DIAMOND_H_

#include "jwf_Variation.h"

class DiamondFunc: public Variation {
public:
	DiamondFunc() {
	}

	const char* getName() const {
		return "diamond";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float length = pAffineTP->getPrecalcSqrt();
		float sinA = pAffineTP->getPrecalcSinA();
		float cosA = pAffineTP->getPrecalcCosA();
		float sinr = JWF_SIN(length);
		float cosr = JWF_COS(length);
		pVarTP->x += pAmount * sinA * cosr;
		pVarTP->y += pAmount * cosA * sinr;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	DiamondFunc* makeCopy() {
		return new DiamondFunc(*this);
	}

};

#endif // JWFVAR_DIAMOND_H_
