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
#ifndef JWFVAR_CURL_H_
#define JWFVAR_CURL_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class CurlFunc: public Variation {
public:
	CurlFunc() {
		c1 = 0.0f;
		c2 = 0.0f;
		initParameterNames(2, "c1", "c2");
	}

	const char* getName() const {
		return "curl";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "c1") == 0) {
			c1 = pValue;
		}
		else if (strcmp(pName, "c2") == 0) {
			c2 = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float re = 1.0f + c1 * pAffineTP->x + c2 * (pAffineTP->x*pAffineTP->x - pAffineTP->y*pAffineTP->y);
    float im = c1 * pAffineTP->y + c2 * 2 * pAffineTP->x * pAffineTP->y;

    double r = pAmount / (re*re + im*im);

    pVarTP->x += (pAffineTP->x * re + pAffineTP->y * im) * r;
    pVarTP->y += (pAffineTP->y * re - pAffineTP->x * im) * r;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CurlFunc* makeCopy() {
		return new CurlFunc(*this);
	}

private:
	float c1;
	float c2;
};

#endif // JWFVAR_CURL_H_
