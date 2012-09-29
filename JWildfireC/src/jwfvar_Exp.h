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

#ifndef JWFVAR_EXP_H_
#define JWFVAR_EXP_H_

#include "jwf_Variation.h"

class ExpFunc: public Variation {
public:
	ExpFunc() {
	}

	const char* getName() const {
		return "exp";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float expe = JWF_EXP(pAffineTP->x);
		float expsin = JWF_SIN(pAffineTP->y);
		float expcos = JWF_COS(pAffineTP->y);
		pVarTP->x += pAmount * expe * expcos;
		pVarTP->y += pAmount * expe * expsin;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	ExpFunc* makeCopy() {
		return new ExpFunc(*this);
	}

};

#endif // JWFVAR_EXP_H_
