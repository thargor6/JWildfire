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

class PDJFunc: public Variation {
public:
	PDJFunc() {
		a = 1.0;
		b = 2.0;
		c = 3.0;
		d = 4.0;
		initParameterNames(4, "a", "b", "c", "d");
	}

	const char* getName() const {
		return "pdj";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "a") == 0) {
			a = pValue;
		}
		else if (strcmp(pName, "b") == 0) {
			b = pValue;
		}
		else if (strcmp(pName, "c") == 0) {
			c = pValue;
		}
		else if (strcmp(pName, "d") == 0) {
			d = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		pVarTP->x += pAmount * (JWF_SIN(a * pAffineTP->y) - JWF_COS(b * pAffineTP->x));
		pVarTP->y += pAmount * (JWF_SIN(c * pAffineTP->x) - JWF_COS(d * pAffineTP->y));
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	PDJFunc* makeCopy() {
		return new PDJFunc(*this);
	}

private:
	JWF_FLOAT a, b, c, d;
};

