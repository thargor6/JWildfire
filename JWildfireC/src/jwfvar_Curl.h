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

#include "jwf_Constants.h"
#include "jwf_Variation.h"

class CurlFunc: public Variation {
public:
	CurlFunc() {
		c1 = 0.0;
		c2 = 0.0;
		initParameterNames(2, "c1", "c2");
	}

	const char* getName() const {
		return "curl";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "c1") == 0) {
			c1 = pValue;
		}
		else if (strcmp(pName, "c2") == 0) {
			c2 = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		JWF_FLOAT re = 1.0 + c1 * pAffineTP->x + c2 * (pAffineTP->x * pAffineTP->x - pAffineTP->y * pAffineTP->y);
		JWF_FLOAT im = c1 * pAffineTP->y + c2 * 2 * pAffineTP->x * pAffineTP->y;

		double r = pAmount / (re * re + im * im);

		pVarTP->x += (pAffineTP->x * re + pAffineTP->y * im) * r;
		pVarTP->y += (pAffineTP->y * re - pAffineTP->x * im) * r;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	CurlFunc* makeCopy() {
		return new CurlFunc(*this);
	}

private:
	JWF_FLOAT c1;
	JWF_FLOAT c2;
};

