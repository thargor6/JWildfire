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
#ifndef JWFVAR_BSWIRL_H_
#define JWFVAR_BSWIRL_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class BSwirlFunc: public Variation {
public:
	BSwirlFunc() {
		in = 0.0f;
		out = 0.0f;
		initParameterNames(2, "in", "out");
	}

	const char* getName() const {
		return "bSwirl";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "in") == 0) {
			in = pValue;
		}
		else if (strcmp(pName, "out") == 0) {
			out = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float tau, sigma;
		float temp;
		float cosht, sinht;
		float sins, coss;

		tau = 0.5f
				* (logf((pAffineTP->x + 1.0f) * (pAffineTP->x + 1.0f) + pAffineTP->y * pAffineTP->y) - JWF_LOG((pAffineTP->x - 1.0f) * (pAffineTP->x - 1.0f) + pAffineTP->y * pAffineTP->y));
		sigma = M_PI - atan2f(pAffineTP->y, pAffineTP->x + 1.0f) - atan2(pAffineTP->y, 1.0f - pAffineTP->x);

		sigma = sigma + tau * out + in / tau;

		sinht = JWF_SINH(tau);
		cosht = JWF_COSH(tau);
		sins = JWF_SIN(sigma);
		coss = JWF_COS(sigma);
		temp = cosht - coss;
		if (temp == 0) {
			return;
		}
		pVarTP->x += pAmount * sinht / temp;
		pVarTP->y += pAmount * sins / temp;

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	BSwirlFunc* makeCopy() {
		return new BSwirlFunc(*this);
	}

private:
	float sqrtf_safe(float x) {
		if (x <= 0.0f)
			return 0.0f;
		return JWF_SQRT(x);
	}

	float in;
	float out;
};

#endif // JWFVAR_BSWIRL_H_
