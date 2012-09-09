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
#ifndef JWFVAR_BLOB_H_
#define JWFVAR_BLOB_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class BlobFunc: public Variation {
public:
	BlobFunc() {
    low = 0.3f;
    high = 1.2f;
    waves = 6.0f;
		initParameterNames(3, "low", "high", "waves");
	}

	const char* getName() const {
		return "blob";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "low") == 0) {
			low = pValue;
		}
		else if (strcmp(pName, "high") == 0) {
			high = pValue;
		}
		else if (strcmp(pName, "waves") == 0) {
			waves = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float a = atan2f(pAffineTP->x, pAffineTP->y);
    float r = sqrtf(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    r = r * (low + (high - low) * (0.5f + 0.5f * sinf(waves * a)));
    float nx = sinf(a) * r;
    float ny = cosf(a) * r;
    pVarTP->x += pAmount * nx;
    pVarTP->y += pAmount * ny;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	BlobFunc* makeCopy() {
		return new BlobFunc(*this);
	}

private:
	float low;
	float high;
	float waves;
};

#endif // JWFVAR_BLOB_H_
