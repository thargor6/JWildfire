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

#ifndef JWFVAR_TAN_H_
#define JWFVAR_TAN_H_

#include "jwf_Variation.h"

class TanFunc: public Variation {
public:
	TanFunc() {
	}

	const char* getName() const {
		return "tan";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float tansin = JWF_SIN(2.0f * pAffineTP->x);
		float tancos = JWF_COS(2.0f * pAffineTP->x);
		float tansinh = JWF_SINH(2.0f * pAffineTP->y);
		float tancosh = JWF_COSH(2.0f * pAffineTP->y);
		float d = (tancos + tancosh);
		if (d == 0) {
			return;
		}
		float tanden = 1.0f / d;
		pVarTP->x += pAmount * tanden * tansin;
		pVarTP->y += pAmount * tanden * tansinh;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	TanFunc* makeCopy() {
		return new TanFunc(*this);
	}

};

#endif // JWFVAR_TAN_H_
