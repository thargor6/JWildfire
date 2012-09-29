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
#ifndef JWFVAR_HYPERTILE1_H_
#define JWFVAR_HYPERTILE1_H_

#include "limits.h"
#include "jwf_Constants.h"
#include "jwf_Variation.h"

class Hypertile1Func: public Variation {
public:
	Hypertile1Func() {
		p = 3;
		q = 7;
		initParameterNames(2, "p", "q");
	}

	const char* getName() const {
		return "hypertile1";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "p") == 0) {
			p = FTOI(pValue);
		}
		else if (strcmp(pName, "q") == 0) {
			q = FTOI(pValue);
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		float rpa = pContext->randGen->random(INT_MAX) * _pa;

		float sina = JWF_SIN(rpa);
		float cosa = JWF_COS(rpa);

		float re = _r * cosa;
		float im = _r * sina;

		float a = pAffineTP->x + re, b = pAffineTP->y - im;

		float c = re * pAffineTP->x - im * pAffineTP->y + 1;
		float d = re * pAffineTP->y + im * pAffineTP->x;

		float vr = pAmount / (c * c + d * d);

		pVarTP->x += vr * (a * c + b * d);
		pVarTP->y += vr * (b * c - a * d);

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	Hypertile1Func* makeCopy() {
		return new Hypertile1Func(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
		_pa = 2.0f * M_PI / p;

		float r2 = 1.0f - (cosf(2.0f * M_PI / p) - 1.0f) / (cosf(2.0f * M_PI / p) + JWF_COS(2.0f * M_PI / q));
		if (r2 > 0)
			_r = 1.0f / JWF_SQRT(r2);
		else
			_r = 1.0f;
	}

private:
	int p;
	int q;
	float _pa, _r;
};

#endif // JWFVAR_HYPERTILE1_H_
