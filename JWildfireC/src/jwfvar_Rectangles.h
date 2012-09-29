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
#ifndef JWFVAR_RECTANGLES_H_
#define JWFVAR_RECTANGLES_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class RectanglesFunc: public Variation {
public:
	RectanglesFunc() {
		x = 0.0f;
		y = 0.0f;
		initParameterNames(2, "x", "y");
	}

	const char* getName() const {
		return "rectangles";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		if (fabsf(x) < EPSILON) {
			pVarTP->x += pAmount * pAffineTP->x;
		}
		else {
			pVarTP->x += pAmount * ((2.0f * floorf(pAffineTP->x / x) + 1.0f) * x - pAffineTP->x);
		}
		if (fabsf(y) < EPSILON) {
			pVarTP->y += pAmount * pAffineTP->y;
		}
		else {
			pVarTP->y += pAmount * ((2.0f * floorf(pAffineTP->y / y) + 1.0f) * y - pAffineTP->y);
		}
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	RectanglesFunc* makeCopy() {
		return new RectanglesFunc(*this);
	}

private:
	float x;
	float y;
};

#endif // JWFVAR_RECTANGLES_H_
