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
#ifndef JWFVAR_PRE_CIRCLECROP_H_
#define JWFVAR_PRE_CIRCLECROP_H_

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class PreCircleCropFunc: public Variation {
public:
	PreCircleCropFunc() {
		radius = 1.0f;
		x = 0.0f;
		y = 0.0f;
		scatter_area = 0.0f;
		zero = true;
		initParameterNames(5, "radius", "x", "y", "scatter_area", "zero");
	}

	const char* getName() const {
		return "pre_circlecrop";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "radius") == 0) {
			radius = pValue;
		}
		else if (strcmp(pName, "x") == 0) {
			x = pValue;
		}
		else if (strcmp(pName, "y") == 0) {
			y = pValue;
		}
		else if (strcmp(pName, "scatter_area") == 0) {
			scatter_area = pValue;
		}
		else if (strcmp(pName, "zero") == 0) {
			zero = FTOI(pValue) == 1;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float x0 = x;
		float y0 = y;
		float cr = radius;
		float ca = _cA;
		float vv = pAmount;

		pAffineTP->x -= x0;
		pAffineTP->y -= y0;

		pAffineTP->z += vv * pAffineTP->z;

		float rad = JWF_SQRT(pAffineTP->x * pAffineTP->x + pAffineTP->y * pAffineTP->y);
		float ang = atan2f(pAffineTP->y, pAffineTP->x);
		float rdc = cr + (pContext->randGen->random() * 0.5f * ca);

		boolean esc = rad > cr;
		boolean cr0 = zero;

		float s = JWF_SIN(ang);
		float c = JWF_COS(ang);

		if (cr0 && esc) {
			pAffineTP->x = pAffineTP->y = 0.0f;
		}
		else if (cr0 && !esc) {
			pAffineTP->x += vv * pAffineTP->x + x0;
			pAffineTP->y += vv * pAffineTP->y + y0;
		}
		else if (!cr0 && esc) {
			pAffineTP->x += vv * rdc * c + x0;
			pAffineTP->y += vv * rdc * s + y0;
		}
		else if (!cr0 && !esc) {
			pAffineTP->x += vv * pAffineTP->x + x0;
			pAffineTP->y += vv * pAffineTP->y + y0;
		}
	}

	void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
#undef min
#undef max
#define min(a,b) (((a)<(b))?(a):(b))
#define max(a,b) (((a)>(b))?(a):(b))
		_cA = max(-1.0f, min(scatter_area, 1.0f));
#undef max
#undef min
	}

	int const getPriority() {
		return -1;
	}

	PreCircleCropFunc* makeCopy() {
		return new PreCircleCropFunc(*this);
	}

private:
	float radius;
	float x;
	float y;
	float scatter_area;
	bool zero;
	float _cA;
};

#endif // JWFVAR_PRE_CIRCLECROP_H_
