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
#ifndef JWFVAR_BENT2_H_
#define JWFVAR_BENT2_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Bent2Func: public Variation {
public:
	Bent2Func() {
		x = 1.0f;
		y = 1.0f;
		initParameterNames(2, "x", "y");
	}

	const char* getName() const {
		return "bent2";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float nx = pAffineTP->x;
    float ny = pAffineTP->y;
    if (nx < 0.0)
      nx = nx * x;
    if (ny < 0.0)
      ny = ny * y;
    pVarTP->x += pAmount * nx;
    pVarTP->y += pAmount * ny;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	Bent2Func* makeCopy() {
		return new Bent2Func(*this);
	}

private:
	float x;
	float y;
};

#endif // JWFVAR_BENT2_H_
