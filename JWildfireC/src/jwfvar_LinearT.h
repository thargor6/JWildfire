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

class LinearTFunc: public Variation {
public:
	LinearTFunc() {
		powX = 1.2;
		powY = 1.2;
		initParameterNames(2, "powX", "powY");
	}

	const char* getName() const {
		return "linearT";
	}

	void setParameter(char *pName, JWF_FLOAT pValue) {
		if (strcmp(pName, "powX") == 0) {
			powX = pValue;
		}
		else if (strcmp(pName, "powY") == 0) {
			powY = pValue;
		}
	}

	void transform(FlameTransformationContext *pContext, XForm *pXForm, XYZPoint *pAffineTP, XYZPoint *pVarTP, JWF_FLOAT pAmount) {
		pVarTP->x += sgn(pAffineTP->x) * JWF_POW(JWF_FABS(pAffineTP->x), powX) * pAmount;
		pVarTP->y += sgn(pAffineTP->y) * JWF_POW(JWF_FABS(pAffineTP->y), powY) * pAmount;
		if (pContext->isPreserveZCoordinate) {
			pVarTP->z += pAmount * pAffineTP->z;
		}
	}

	LinearTFunc* makeCopy() {
		return new LinearTFunc(*this);
	}

private:
	JWF_FLOAT sgn(JWF_FLOAT arg) {
		if (arg > 0)
			return 1.0;
		else
			return -1.0;
	}

	JWF_FLOAT powX;
	JWF_FLOAT powY;
};

