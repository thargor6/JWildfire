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
#ifndef JWFVAR_CANNABISCURVE_WF_H_
#define JWFVAR_CANNABISCURVE_WF_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class CannabisCurveWFFunc: public Variation {
public:
	CannabisCurveWFFunc() {
		filled = 1;
		initParameterNames(1, "filled");
	}

	const char* getName() const {
		return "cannabiscurve_wf";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "filled") == 0) {
			filled = FTOI(pValue);
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
    float a= pAffineTP->getPrecalcAtan();
    float r = pAffineTP->getPrecalcSqrt();

    // cannabis curve (http://mathworld.wolfram.com/CannabisCurve.html)
    r = (1.0f + 9.0f / 10.0f * cosf(8.0f * a)) * (1.0f + 1.0f / 10.0f * cosf(24.0f * a)) * (9.0f / 10.0f + 1.0f / 10.0f * cosf(200.0f * a)) * (1.0f + sinf(a));
    a += M_PI / 2.0f;

    if (filled == 1) {
      r *= pContext->randGen->random();
    }

    float nx = sinf(a) * r;
    float ny = cosf(a) * r;

    pVarTP->x += pAmount * nx;
    pVarTP->y += pAmount * ny;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CannabisCurveWFFunc* makeCopy() {
		return new CannabisCurveWFFunc(*this);
	}

private:
	int filled;
};

#endif // JWFVAR_CANNABISCURVE_WF_H_
