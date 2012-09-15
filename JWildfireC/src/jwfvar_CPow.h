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
#ifndef JWFVAR_CPOW_H_
#define JWFVAR_CPOW_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class CPowFunc: public Variation {
public:
	CPowFunc() {
		r = 1.0f;
		i = 0.1f;
		power = 1.5f;
		initParameterNames(3, "r", "i", "power");
	}

	const char* getName() const {
		return "cpow";
	}

	void setParameter(char *pName, float pValue) {
		if (strcmp(pName, "r") == 0) {
			r = pValue;
		}
		else if (strcmp(pName, "i") == 0) {
			i = pValue;
		}
		else if (strcmp(pName, "power") == 0) {
			power = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, float pAmount) {
		float a = pAffineTP->getPrecalcAtanYX();
    float lnr = 0.5f * logf(pAffineTP->getPrecalcSumsq());
    float va = 2.0f * M_PI / power;
    float vc = r / power;
    float vd = i / power;
    float ang = vc * a + vd * lnr + va * floor(power * pContext->randGen->random());

    float m = pAmount * expf(vc * lnr - vd * a);
    float sa = sinf(ang);
    float ca = cosf(ang);

    pVarTP->x += m * ca;
    pVarTP->y += m * sa;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CPowFunc* makeCopy() {
		return new CPowFunc(*this);
	}

private:
	float r;
	float i;
	float power;
};

#endif // JWFVAR_CPOW_H_
