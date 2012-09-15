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
#ifndef JWFVAR_CONIC_H_
#define JWFVAR_CONIC_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class ConicFunc: public Variation {
public:
	ConicFunc() {
		eccentricity = 1.0f;
		holes = 0.0f;
		initParameterNames(2, "eccentricity", "holes");
	}

	const char* getName() const {
		return "conic";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "eccentricity") == 0) {
			eccentricity = pValue;
		}
		else if (strcmp(pName, "holes") == 0) {
			holes = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    double ct = pAffineTP->x / pAffineTP->getPrecalcSqrt();
    double r = pAmount * (pContext->randGen->random() - holes) * eccentricity / (1.0f + eccentricity * ct) / pAffineTP->getPrecalcSqrt();
    pVarTP->x += r * pAffineTP->x;
    pVarTP->y += r * pAffineTP->y;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	ConicFunc* makeCopy() {
		return new ConicFunc(*this);
	}

private:
	float eccentricity;
	float holes;
};

#endif // JWFVAR_CONIC_H_
