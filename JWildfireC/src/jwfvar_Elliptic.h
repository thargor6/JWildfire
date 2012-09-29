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

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float tmp = pAffineTP->y * pAffineTP->y + pAffineTP->x * pAffineTP->x + 1.0;
		float x2 = 2.0f * pAffineTP->x;
		float xmax = 0.5f * (sqrtf(tmp + x2) + JWF_SQRT(tmp - x2));

		float a = pAffineTP->x / xmax;
		float b = sqrt_safe(1.0f - a * a);

		pVarTP->x += pAmount * atan2f(a, b);

		if (pAffineTP->y > 0)
			pVarTP->y += pAmount * JWF_LOG(xmax + sqrt_safe(xmax - 1.0));
		else
			pVarTP->y -= pAmount * JWF_LOG(xmax + sqrt_safe(xmax - 1.0));

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EllipticFunc* makeCopy() {
		return new EllipticFunc(*this);
	}

private:
	float sqrt_safe(float x) {
		return (x < EPSILON) ? 0.0 : JWF_SQRT(x);
	}

};

#endif // JWFVAR_ELLIPTIC_H_
