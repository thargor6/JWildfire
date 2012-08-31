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
#ifndef JWFVAR_EPISPIRAL_WF_H_
#define JWFVAR_EPISPIRAL_WF_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class EpispiralWFFunc: public Variation {
public:
	EpispiralWFFunc() {
		waves = 4.0f;
		initParameterNames(1, "waves");
	}

	const char* getName() const {
		return "epispiral_wf";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "waves") == 0) {
			waves = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float a = atan2f(pAffineTP->x, pAffineTP->y);
    float r = sqrtf(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
    r = 0.5 / cosf(waves * a);
    float nx = sinf(a) * r;
    float ny = cosf(a) * r;
    pVarTP->x += pAmount * nx;
    pVarTP->y += pAmount * ny;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	EpispiralWFFunc* makeCopy() {
		return new EpispiralWFFunc(*this);
	}

private:
	float waves;
};

#endif // JWFVAR_EPISPIRAL_WF_H_
