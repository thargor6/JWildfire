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

class HypertileFunc: public Variation {
public:
	HypertileFunc() {
		p = 3;
		q = 7;
		n = 1;
		initParameterNames(3, "p", "q", "n");
	}

	const char* getName() const {
		return "hypertile";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "p") == 0) {
			p = FTOI(pValue);
		}
		else if (strcmp(pName, "q") == 0) {
			q = FTOI(pValue);
		}
		else if (strcmp(pName, "n") == 0) {
			n = FTOI(pValue);
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
    JWF_FLOAT a = pAffineTP->x + _re;
    JWF_FLOAT b = pAffineTP->y - _im;

    JWF_FLOAT c = _re * pAffineTP->x - _im * pAffineTP->y + 1.0;
    JWF_FLOAT d = _re * pAffineTP->y + _im * pAffineTP->x;

    JWF_FLOAT vr = pAmount / (JWF_SQR(c) + JWF_SQR(d));

    pVarTP->x += vr * (a * c + b * d);
    pVarTP->y += vr * (b * c - a * d);

		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	HypertileFunc* makeCopy() {
		return new HypertileFunc(*this);
	}

	virtual void init(FlameTransformationContext *pContext, XForm *pXForm, JWF_FLOAT pAmount) {
    JWF_FLOAT pa = 2.0 * M_PI / (JWF_FLOAT) p;
    JWF_FLOAT qa = 2.0 * M_PI / (JWF_FLOAT) q;

    JWF_FLOAT r = (1.0 - JWF_COS(pa)) / (JWF_COS(pa) + JWF_COS(qa)) + 1.0;

    if (r > 0)
      r = 1.0 / JWF_SQRT(r);
    else
      r = 1.0;

    JWF_FLOAT a = n * pa;

    _re = r * JWF_COS(a);
    _im = r * JWF_SIN(a);
	}

private:
	int p;
	int q;
	int n;

	JWF_FLOAT _re, _im;
};
