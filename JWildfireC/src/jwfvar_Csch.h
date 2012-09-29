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

#ifndef JWFVAR_CSCH_H_
#define JWFVAR_CSCH_H_

#include "jwf_Variation.h"

class CschFunc: public Variation {
public:
	CschFunc() {
	}

	const char* getName() const {
		return "csch";
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float cschsin = JWF_SIN(pAffineTP->y);
		float cschcos = JWF_COS(pAffineTP->y);
		float cschsinh = JWF_SINH(pAffineTP->x);
		float cschcosh = JWF_COSH(pAffineTP->x);
		float d = (coshf(2.0f * pAffineTP->x) - JWF_COS(2.0f * pAffineTP->y));
		if (d == 0) {
			return;
		}
		float cschden = 2.0f / d;
		pVarTP->x += pAmount * cschden * cschsinh * cschcos;
		pVarTP->y -= pAmount * cschden * cschcosh * cschsin;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CschFunc* makeCopy() {
		return new CschFunc(*this);
	}

};

#endif // JWFVAR_CSCH_H_
