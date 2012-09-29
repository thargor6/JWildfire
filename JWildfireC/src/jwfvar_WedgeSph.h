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
#ifndef JWFVAR_WEDGESPH_H_
#define JWFVAR_WEDGESPH_H_

#include "jwf_Variation.h"

class WedgeSphFunc: public Variation {
public:
	WedgeSphFunc() {
		angle = 0.20f;
		hole = 0.20f;
		count = 2.0;
		swirl = 0.30;
		initParameterNames(4, "angle", "hole", "count", "swirl");
	}

	const char* getName() const {
		return "wedge_sph";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "angle") == 0) {
			angle = pValue;
		}
		else if (strcmp(pName, "hole") == 0) {
			hole = pValue;
		}
		else if (strcmp(pName, "count") == 0) {
			count = pValue;
		}
		else if (strcmp(pName, "swirl") == 0) {
			swirl = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float r = 1.0f / (pAffineTP->getPrecalcSqrt() + EPSILON);
		float a = pAffineTP->getPrecalcAtanYX() + swirl * r;
		float c = floorf((count * a + M_PI) * M_1_PI * 0.5f);

		float comp_fac = 1.0f - angle * count * M_1_PI * 0.5f;

		a = a * comp_fac + c * angle;

		float sa = JWF_SIN(a);
		float ca = JWF_COS(a);
		r = pAmount * (r + hole);

		pVarTP->x += r * ca;
		pVarTP->y += r * sa;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	WedgeSphFunc* makeCopy() {
		return new WedgeSphFunc(*this);
	}

private:
	float angle;
	float hole;
	float count;
	float swirl;
};

#endif // JWFVAR_WEDGESPH_H_
