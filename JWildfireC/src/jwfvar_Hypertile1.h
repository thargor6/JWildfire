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
		JWF_FLOAT rpa = pContext->randGen->random(INT_MAX) * _pa;

		JWF_FLOAT sina, cosa;
		JWF_SINCOS(rpa, &sina, &cosa);

		JWF_FLOAT re = _r * cosa;
		JWF_FLOAT im = _r * sina;

		JWF_FLOAT a = pAffineTP->x + re, b = pAffineTP->y - im;

		JWF_FLOAT c = re * pAffineTP->x - im * pAffineTP->y + 1;
		JWF_FLOAT d = re * pAffineTP->y + im * pAffineTP->x;

		JWF_FLOAT vr = pAmount / (c * c + d * d);

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
		_pa = 2.0 * M_PI / p;

		JWF_FLOAT r2 = 1.0 - (JWF_COS(2.0 * M_PI / p) - 1.0) / (JWF_COS(2.0 * M_PI / p) + JWF_COS(2.0 * M_PI / q));
		if (r2 > 0)
			_r = 1.0 / JWF_SQRT(r2);
		else
			_r = 1.0;
	}

private:
	int p;
	int q;
	JWF_FLOAT _pa, _r;
};

