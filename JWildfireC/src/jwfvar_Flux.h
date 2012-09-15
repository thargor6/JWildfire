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

#ifndef JWFVAR_FLUX_H_
#define JWFVAR_FLUX_H_

#include "jwf_Variation.h"

class FluxFunc: public Variation {
public:
	FluxFunc() {
		spread = 0.0f;
		initParameterNames(1, "spread");
	}

	const char* getName() const {
		return "flux";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "spread") == 0) {
			spread = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
		float xpw = pAffineTP->x + pAmount;
		float xmw = pAffineTP->x - pAmount;
		float avgr = pAmount * (2.0f + spread) * sqrtf(sqrtf(pAffineTP->y * pAffineTP->y + xpw * xpw) / sqrtf(pAffineTP->y * pAffineTP->y + xmw * xmw));
		float avga = (atan2f(pAffineTP->y, xmw) - atan2f(pAffineTP->y, xpw)) * 0.5f;

		pVarTP->x += avgr * cosf(avga);
		pVarTP->y += avgr * sinf(avga);
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	FluxFunc* makeCopy() {
		return new FluxFunc(*this);
	}


private:
	float spread;
};

#endif // JWFVAR_FLUX_H_
